package com.companion.astrodating.ui.otp.ui.request

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityRequestOtpBinding
import com.companion.astrodating.ui.filter.model.FilterDomainEntity
import com.companion.astrodating.ui.otp.data.requestData.RequestOtpRequestData
import com.companion.astrodating.ui.otp.domain.model.CountryDomainEntity
import com.companion.astrodating.ui.otp.domain.model.OtpDomain
import com.companion.astrodating.ui.otp.ui.adapter.CountryAdapter
import com.companion.astrodating.ui.otp.ui.verify.VerifyOtpActivity
import com.companion.astrodating.ui.otp.viewmodel.CountryViewModel
import com.companion.astrodating.ui.otp.viewmodel.RequestOtpViewModel
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.COMMON_AUTH
import com.companion.astrodating.util.KEY_OTP_DOMAIN_DATA
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.isValidEmail
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestOtpActivity : BaseActivity() {

    private lateinit var binding: ActivityRequestOtpBinding
    private val reqOtpViewModel: RequestOtpViewModel by viewModels()
    private val countryViewModel: CountryViewModel by viewModels()
    private lateinit var countryAdapter: CountryAdapter
    private lateinit var dialog: LoadingDialog
    private lateinit var countryDomainList: ArrayList<CountryDomainEntity>
    private lateinit var countryList: ArrayList<FilterDomainEntity>
    private var isoCode = ""
    private var country = ""
    private var countryCode = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestOtpBinding.inflate(layoutInflater)
        dialog = LoadingDialog(this)
        setContentView(binding.root)
        enableEdgeToEdge()
        initViews()
        setCountryCode()
        handleClickEvents()
    }

    private fun setCountryCode() {
        binding.dropdownCountry.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (countryDomainList!=null && countryDomainList.size >0) {
                    isoCode = countryDomainList[position].iso
                    countryCode = countryDomainList[position].phone
                    country = countryDomainList[position].country
                    if (countryDomainList[position].iso == "in") {
                        binding.tvPhoneNumberTitle.text =
                            getString(R.string.text_login_enter_phone_number)
                        binding.tvPhoneNumberDesc.text =
                            getString(R.string.text_login_please_enter_phone_number)
                        binding.llEmail.hideVisibility()
                        binding.llMobile.showVisibility()
                    } else {
                        binding.tvPhoneNumberTitle.text =
                            getString(R.string.text_login_enter_email_address)
                        binding.tvPhoneNumberDesc.text =
                            getString(R.string.text_login_please_enter_email_address)
                        binding.llEmail.showVisibility()
                        binding.llMobile.hideVisibility()
                    }
                }else{
                    binding.root.showShortDurationSnackBar(getString(R.string.text_something_went_wrong))
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Nothing to do
            }
        }
    }

    private fun initViews() {
        countryDomainList = ArrayList()
        countryList = ArrayList()
        countryAdapter = CountryAdapter(this)
        binding.dropdownCountry.adapter = countryAdapter
        binding.btnRequestOtp.isEnabled = false
        dialog = LoadingDialog(this)
        if (isInternetConnection()) {
            Log.d(TAG, "OTP country-codes request started")
            countryViewModel.getCountryList(COMMON_AUTH)
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }
    }

    override fun initObservers() {
        reqOtpViewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> dialog.showDialog()
                is UiState.Success -> {
                    dialog.hideDialog()
                    Log.d(TAG, "OTP request succeeded: channel=${state.data.type}")
//                    FirebaseAnalyticsConstants.setFirebaseLogEvent(FirebaseAnalytics.Event.LOGIN,FirebaseAnalyticsConstants.REQUEST_OTP_BUTTON)
                    moveToOtpVerification(state.data)
                }

                is UiState.Error -> {
                    dialog.hideDialog()
                    Log.w(TAG, "OTP request failed: code=${state.errorCode}, error=${state.error}")
                    binding.root.showLongDurationSnackBar(state.error)
                }
            }
        }

        countryViewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> dialog.showDialog()
                is UiState.Success -> {
                    dialog.hideDialog()
                    Log.d(TAG, "OTP country-codes received: count=${state.data.list.size}")
                    countryDomainList.addAll(state.data.list)
                    countryList.clear()
                    for (item in countryDomainList){
                        countryList.add(FilterDomainEntity(item.country,false))
                    }
                    StorePreferences.saveFilterCountryList(countryList)
                    countryAdapter.submitData(countryDomainList)
                    binding.btnRequestOtp.isEnabled = countryDomainList.isNotEmpty()

                }

                is UiState.Error -> {
                    dialog.hideDialog()
                    Log.w(TAG, "OTP country-codes failed: code=${state.errorCode}, error=${state.error}")
                    binding.root.showLongDurationSnackBar(getString(R.string.text_something_went_wrong))
                }
            }
        }

    }

    private fun moveToOtpVerification(otpDomain: OtpDomain) {
        launchScreenAndFinish<VerifyOtpActivity>() {
            putExtra(KEY_OTP_DOMAIN_DATA, Gson().toJson(otpDomain))
        }
    }


    private fun handleClickEvents() {
        binding.btnRequestOtp.setOnClickListener {
            if (isoCode != null && isoCode.isNotEmpty()) {
                if (isoCode == "in") {
                    if (isInternetConnection()) {
                        val mobile = binding.etMobileNumber.text.toString()
                        if (country.isNullOrBlank() && country.isEmpty()){
                            binding.root.showLongDurationSnackBar(getString(R.string.error_message_select_country))
                        }
                        else if (mobile.isNullOrBlank() && mobile.isEmpty()) {
                            binding.root.showLongDurationSnackBar(getString(R.string.error_message_select_mobile))
                        }
                        else if (mobile.length < 10) {
                            binding.root.showLongDurationSnackBar(getString(R.string.error_message_invalid_mobile_number))
                        } else {
                            val requestData = RequestOtpRequestData(
                                mobile = buildString {
                                    append(binding.tvCountryCode.text.toString())
                                    append(binding.etMobileNumber.text.toString())
                                },
                                type = "mobile",
                                countryCode = countryCode,
                                email = "",
                                country = country
                            )
                            StorePreferences.saveRequestOtpRequestData(requestData)
                            Log.d(TAG, "OTP request started: channel=mobile, countryIso=$isoCode")
                            reqOtpViewModel.requestOtpFor(
                                requestData,
                                commonAuth = COMMON_AUTH
                            )

                        }
                    } else {
                        showNoConnectionSnackBar()
                    }
                } else {
                    if (isInternetConnection()) {
                        val email = binding.etEmail.text.toString()
                        if (country.isNullOrBlank() && country.isEmpty()){
                            binding.root.showLongDurationSnackBar(getString(R.string.error_message_select_country))
                        }
                        if (email.isValidEmail()) {
                            val requestData = RequestOtpRequestData(
                                type = "email",
                                mobile = APP_EMPTY_STRING,
                                countryCode = countryCode,
                                email = email ?: APP_EMPTY_STRING,
                                country = country
                            )

                            StorePreferences.saveRequestOtpRequestData(requestData)
                            Log.d(TAG, "OTP request started: channel=email, countryIso=$isoCode")
                            reqOtpViewModel.requestOtpFor(
                                requestData,
                                commonAuth = COMMON_AUTH
                            )

                        } else {
                            binding.root.showLongDurationSnackBar(getString(R.string.error_message_invalid_email_address))
                        }
                    } else {
                        showNoConnectionSnackBar()
                    }
                }
            }else{
                binding.root.showLongDurationSnackBar(resources.getString(R.string.text_something_went_wrong))
            }
        }
    }

    private fun showNoConnectionSnackBar() {
        binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
    }

    override fun onPause() {
        super.onPause()
        dialog.hideDialog()
    }
}
