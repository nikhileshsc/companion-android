package com.companion.astrodating.ui.otp.ui.verify

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityVerifyOtpBinding
import com.companion.astrodating.ui.home.ui.HomePageActivity
import com.companion.astrodating.ui.managePhotos.ui.ManagePhotosActivity
import com.companion.astrodating.ui.notification.ui.PushNotificationActivity
import com.companion.astrodating.ui.otp.data.requestData.RequestOtpRequestData
import com.companion.astrodating.ui.otp.domain.model.OtpDomain
import com.companion.astrodating.ui.otp.ui.request.RequestOtpActivity
import com.companion.astrodating.ui.otp.viewmodel.RequestOtpViewModel
import com.companion.astrodating.ui.otp.viewmodel.VerifyOtpViewModel
import com.companion.astrodating.ui.registration.ui.RegistrationActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.updateProfile.ui.UpdateProfileActivity
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.APP_EMPTY_STRING_WITH_SPACE
import com.companion.astrodating.util.COMMON_AUTH
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.FacebookAppEvent
import com.companion.astrodating.util.KEY_OTP_DOMAIN_DATA
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.MOBILE
import com.companion.astrodating.util.REGISTRATION
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.facebookLogEvent
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showVisibility
import com.goodiebag.pinview.Pinview
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VerifyOtpActivity : BaseActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding
    private val verifyOtpViewModel: VerifyOtpViewModel by viewModels()
    private val requestOtpViewModel: RequestOtpViewModel by viewModels()
    private lateinit var dialog: LoadingDialog
    private lateinit var otpDomain: OtpDomain
    private var enteredOtp:String = ""
    private var deviceToken:String = ""
    private var requestData : RequestOtpRequestData?=null
    private val otpTimer = object : CountDownTimer(45000, 1000) {
        override fun onTick(miliSeconds: Long) {
            binding.layoutOtp.showVisibility()
            binding.layoutResendOtp.hideVisibility()
            val seconds = miliSeconds / 1000
            binding.tvSeconds.text = buildString {
                append(seconds.toString())
                append(APP_EMPTY_STRING_WITH_SPACE)
                append(getString(R.string.text_seconds))
            }
        }

        override fun onFinish() {
            binding.layoutOtp.hideVisibility()
            binding.layoutResendOtp.showVisibility()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        enableEdgeToEdge()
        dialog = LoadingDialog(this)
        getIntentData()
        handleClickEvents()
        startOtpTimer()
    }

    fun populateOtpMessage(otp :String){
        binding.otpView.value = otp
    }
    private fun handleClickEvents() {

        binding.otpView.setPinViewEventListener(object : Pinview.PinViewEventListener {
            override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {
                pinview?.value?.let {
                    enteredOtp = it
                }
            }
        })

        binding.btnLogin.setOnClickListener {
            if (isInternetConnection()) {
                validateOtp()
            } else {
                binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
            }
        }

        binding.ivEditMobileNumber.setOnClickListener {
            stopOtpTimer()
            moveToRequestOtpScreen()
        }

        binding.layoutResendOtp.setOnClickListener {
            startOtpTimer()
        }
        binding.layoutResendOtp.setOnClickListener{
            if (isInternetConnection()) {
                Log.d(TAG, "OTP resend started: channel=${requestData?.type}")
                requestOtpViewModel.requestOtpFor(requestData!!,commonAuth = COMMON_AUTH)
            } else {
                binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
            }
        }
    }

    override fun initObservers() {
        verifyOtpViewModel.verifyOtp.observe(this) { state ->
            when (state) {
                is UiState.Loading -> dialog.showDialog()
                is UiState.Success -> {
                    Log.d(TAG, "OTP verification succeeded")
                    dialog.hideDialog()
                    StorePreferences.saveAuthToken(state.data.token)

                    StorePreferences.saveUpdateProfileStatus(!state.data.needsUpdateProfile)
                    //state.data.needsUpdateProfile
                    StorePreferences.saveRegistrationStatus(state.data.isRegistrationCompleted)
                    StorePreferences.savePrimaryUserAgoraUserName(state.data.agoraChatUserName)
                    StorePreferences.savePrimaryUserAgoraChatToken(state.data.agoraChatToken)
                    stopOtpTimer()
                    val profileUrl = state.data.profileUrl
                    if (profileUrl!=null && profileUrl.isNotEmpty()){
                        StorePreferences.saveProfileUrl(profileUrl)
//                        StorePreferences.saveUpdateProfileStatus(true)
                    }
                    if (state.data.isRegistrationCompleted) {
//                        FirebaseAnalyticsConstants.setFirebaseLogEvent(FirebaseAnalytics.Event.LOGIN,FirebaseAnalyticsConstants.VERIFY_OTP_BUTTON)
                        if (StorePreferences.isUpdateProfileCompleted()) {
                            if (profileUrl != null && profileUrl.isNotEmpty()) {
                                moveToHomeScreen()
                            } else {
                                launchScreenAndFinish<ManagePhotosActivity> {
                                    putExtra(FROM, REGISTRATION)
                                }
                            }
                        }else {
                            launchScreenAndFinish<UpdateProfileActivity>(){
                                putExtra(FROM, REGISTRATION)
                            }
                        }
                    } else {
                        moveToRegistrationScreen()
                    }
                }
                is UiState.Error -> {
                    dialog.hideDialog()
                    Log.w(TAG, "OTP verification failed: code=${state.errorCode}")
                    showErrorDialog(state.error)
                }
            }
        }
        requestOtpViewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> dialog.showDialog()
                is UiState.Success -> {
                    dialog.hideDialog()
                    Log.d(TAG, "OTP resend succeeded: channel=${state.data.type}")
//                    FirebaseAnalyticsConstants.setFirebaseLogEvent(FirebaseAnalytics.Event.LOGIN,FirebaseAnalyticsConstants.REQUEST_OTP_BUTTON)
                    otpDomain = state.data
                    startOtpTimer()
                }

                is UiState.Error -> {
                    dialog.hideDialog()
                    Log.w(TAG, "OTP resend failed: code=${state.errorCode}")
                }
            }
        }
    }

    private fun getIntentData() {
        if (intent != null) {
            val data =  intent.getStringExtra(KEY_OTP_DOMAIN_DATA) ?: APP_EMPTY_STRING
            otpDomain = Gson().fromJson(data,OtpDomain::class.java)
            requestData = StorePreferences.getRequestOtpRequestData()
            if (otpDomain!=null && requestData != null){
                if (requestData!!.type == MOBILE) {
                    binding.tvOtpHint.text = getString(R.string.text_code_mobile_verification)
                    binding.tvMobileNumber.text = requestData!!.mobile

                }else{
                    binding.tvOtpHint.text = getString(R.string.text_code_email_verification)
                    binding.tvMobileNumber.text = requestData!!.email
                }
            }
        }
    }

    private fun validateOtp() {
        if (enteredOtp.length < 4) {
            showInvalidOtpSnackBar()
        } else {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (it.isSuccessful) {
                    deviceToken = it.result
                    StorePreferences.saveDeviceToken(deviceToken)
                    val originalRequest = requestData
                    if (originalRequest == null) {
                        Log.w(TAG, "OTP verification blocked: original request data unavailable")
                        showErrorDialog(getString(R.string.text_something_went_wrong))
                        return@addOnCompleteListener
                    }
                    Log.d(TAG, "OTP verification started: channel=${originalRequest.type}")
                    verifyOtpViewModel.verifyOtp(
                        enteredOtp,
                        originalRequest,
                        deviceToken,
                        COMMON_AUTH
                    )
                }
            }
        }
    }

    private fun showInvalidOtpSnackBar() {
        binding.root.showShortDurationSnackBar(resources.getString(R.string.text_no_invalid_otp))
    }

    private fun moveToHomeScreen() {
        if (NotificationManagerCompat.from(this)
                .areNotificationsEnabled()
        ) {
            facebookLogEvent(this,FacebookAppEvent.login)
            launchScreenAndFinish<HomePageActivity>(){
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            launchScreenAndFinish<PushNotificationActivity>()
        }
    }

    private fun moveToRegistrationScreen() {
        launchScreenAndFinish<RegistrationActivity>()
    }

    private fun moveToRequestOtpScreen() {
        launchScreenAndFinish<RequestOtpActivity>()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOtpTimer()
    }

    private fun startOtpTimer() {
        otpTimer.start()
    }

    private fun stopOtpTimer() {
        otpTimer.cancel()
    }
}
