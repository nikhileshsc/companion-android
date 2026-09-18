package com.companion.astrodating.ui.home.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.companion.astrodating.R
import com.companion.astrodating.base.CallNotifHealth
import com.companion.astrodating.base.CallNotifications
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.databinding.FragmentHomeBinding
import com.companion.astrodating.ui.chat.ChatActivity
import com.companion.astrodating.ui.filter.ui.FilterActivity
import com.companion.astrodating.ui.home.adapter.HomeUsersAdapter
import com.companion.astrodating.ui.home.data.requestData.updateLocationRequestData
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomainEntity
import com.companion.astrodating.ui.home.viewmodel.HomeUserViewModel
import com.companion.astrodating.ui.interests.data.requestData.InterestRequestData
import com.companion.astrodating.ui.interests.viewmodel.InterestsViewModel
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.notification.ui.NotificationActivity
import com.companion.astrodating.ui.onlineusers.ui.OnlineUsersActivity
import com.companion.astrodating.ui.profileDetails.ui.ProfileDetailsActivity
import com.companion.astrodating.util.AUTO_UNLOCK_COMPATIBILITY
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.purchasePlans.ui.PurchasePlansActivity
import com.companion.astrodating.ui.purchasePlans.viewmodel.UpdateBenefitsDetailsViewModel
import com.companion.astrodating.ui.registration.domain.model.GetMandatoryUserDetailsDomainEntity
import com.companion.astrodating.ui.registration.viewmodel.GetMandatoryDetailsViewModel
import com.companion.astrodating.ui.states.HomeMessageUiState
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.viewmodel.GetMasterDataDetailsViewModel
import com.companion.astrodating.util.AGORA_TAG
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_400
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.FILTER_REQUEST_CODE
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.FacebookAppEvent
import com.companion.astrodating.util.FilterConstants
import com.companion.astrodating.util.InterestTypeConstant
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.SEC_USER_FULL_NAME
import com.companion.astrodating.util.SEC_USER_ID
import com.companion.astrodating.util.SEC_USER_PROFILE
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.USER_ID
import com.companion.astrodating.util.UpdateBenefitsConstant
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.facebookLogEvent
import com.companion.astrodating.util.fromChat
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreen
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationToast
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showShortDurationToast
import com.companion.astrodating.util.showVisibility
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import io.agora.CallBack
import io.agora.chat.ChatClient
import io.agora.chat.ChatOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var companionApi: CompanionApi

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var binding: FragmentHomeBinding
    private val homeUserViewModel: HomeUserViewModel by viewModels()
    private val interestViewModel: InterestsViewModel by viewModels()
    private val getMandatoryDetailsViewModel: GetMandatoryDetailsViewModel by viewModels()
    private val updateBenefitsDetailsViewModel: UpdateBenefitsDetailsViewModel by viewModels()
    private val getMasterDataDetailsViewModel: GetMasterDataDetailsViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    private val homeUsersAdapter by lazy {
        HomeUsersAdapter()
    }
    private var homeUserList: ArrayList<GetHomeUserDomainEntity>? = null
    var firstVisibleItem = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    private var loading = true
    private var previousTotal = 0
    private var visibleThreshold = 5
    var page = 0
    var layoutManager: LinearLayoutManager? = null
    private var authToken: String = APP_EMPTY_STRING
    private var interestType: String = APP_EMPTY_STRING
    private var agoraChatAppKey: String = "611228573#1419625"
    private var agoraChatToken: String = APP_EMPTY_STRING
    private var agoraUserId: String = APP_EMPTY_STRING
    private var searchString: String = APP_EMPTY_STRING
    private var isInterestApiCalled = false
    lateinit var agoraChatClient: ChatClient
    private var subscriptionDomain: GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity? = null

    private var secUserId = APP_EMPTY_STRING
    private var secUserFullName = APP_EMPTY_STRING
    private var secUserProfile = APP_EMPTY_STRING
    var community: ArrayList<String>? = null
    var education: ArrayList<String>? = null
    var status: ArrayList<String>? = null
    var profession: ArrayList<String>? = null
    var religion: ArrayList<String>? = null
    var country: ArrayList<String>? = null
    var city: ArrayList<String>? = null
    var minHeight: Double = 4.0
    var maxHeight: Double = 8.0
    var minAge: Int = 18
    var maxAge: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (authToken.isNotEmpty()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            updateLocationAndPresence(requireActivity(), 1001)
        }
        CallNotifications.createChannels(requireContext())
        CallNotifHealth.ensureAndPromptAppInfoIfNeeded(requireContext(), CallNotifications.CHANNEL_CALLS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }

        initData()
        initObserver()
        handleClickEvents()
        setupKeyboardListener()
        setupBackButtonHandler()
    }

    private var isKeyboardVisible = false

    private fun setupKeyboardListener() {
        val rootView = binding.root
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                isKeyboardVisible = keypadHeight > 150
                Log.d(TAG, "Keyboard visible: $isKeyboardVisible")
            }
        })
    }

    private fun setupBackButtonHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "Back pressed. Keyboard visible: $isKeyboardVisible, Search focused: ${binding.searchView.hasFocus()}")

                try {
                    if (isKeyboardVisible && binding.searchView.hasFocus()) {
                        Log.d(TAG, "Closing search due to back press")

                        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                        searchEditText?.setText("")
                        searchString = ""
                        hideKeyboard(requireContext())
                        searchListWithInputValues(searchString)
                        binding.searchView.onActionViewCollapsed()
                        return
                    }

                    if (binding.searchView.hasFocus() && !isKeyboardVisible) {
                        Log.d(TAG, "Collapsing search (keyboard not visible)")

                        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                        searchEditText?.setText("")
                        searchString = ""
                        searchListWithInputValues(searchString)
                        binding.searchView.onActionViewCollapsed()
                        return
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling back press: ${e.message}")
                }

                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initChatSDK(agoraChatAppKey: String) {
        val options = ChatOptions()
        if (agoraChatAppKey.isEmpty()) {
            requireActivity().showShortDurationToast("You need to set your AppKey")
            return
        }
        options.appKey = agoraChatAppKey
        agoraChatClient = ChatClient.getInstance()
        agoraChatClient.init(requireContext(), options)
        agoraChatClient.setDebugMode(true)
        loginForChatToUpdateUserInfo()
    }

    private fun initAgoraPushNotification() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.e(TAG, "In Application generateDeviceToken function in success of token -> ${it.result}")
                agoraChatClient.sendFCMTokenToServer(it.result)

                // Also keep our own backend's loginToken.deviceToken in sync
                // as a safety net for already-installed users, whose token
                // won't necessarily rotate right away after updating to a
                // build with the onNewToken() fix - see
                // NotificationMessagingService.onNewToken() for the primary
                // fix. Cheap and idempotent, so firing it on every chat
                // login is fine.
                val fcmToken = it.result
                if (authToken.isNotEmpty() && fcmToken != null) {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            companionApi.updateDeviceToken(
                                authToken,
                                mapOf("deviceToken" to fcmToken, "deviceType" to "android")
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to sync device token to backend: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateLocationAndPresence(requireActivity(), 1001)
        } else {
            requireContext().showShortDurationToast("Location is needed for some features to work!")
        }
    }

    private val locationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            updateLocationAndPresence(requireActivity(), 1001)
        }

    private fun updateLocationAndPresence(activity: Activity, REQUEST_CHECK_SETTINGS: Int) {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
                .setMinUpdateIntervalMillis(5000L)
                .build()

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
            client.checkLocationSettings(builder.build())
                .addOnSuccessListener { locationSettingsResponse ->
                    // Location settings are satisfied
                }
                .addOnFailureListener { e ->
                    if (e is ResolvableApiException) {
                        try {
                            e.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                        } catch (sendEx: SendIntentException) {
                            sendEx.printStackTrace()
                        }
                    }
                }
            return
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(0)
            .build()

        fusedLocationClient.getCurrentLocation(request, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d(TAG, "Fetched current location: ${location.latitude}, ${location.longitude}")
                    val reqData = updateLocationRequestData(location.latitude, location.longitude)
                    homeUserViewModel.updateLocation(reqData)
                } else {
                    Log.e(TAG, "getCurrentLocation returned null")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to get current location: ${it.message}")
            }
    }

    private fun loginForChatToUpdateUserInfo() {
        agoraChatClient.loginWithAgoraToken(
            agoraUserId,
            agoraChatToken,
            object : CallBack {
                override fun onSuccess() {
                    Log.e(AGORA_TAG, "onChatLoginSuccess")
                    initAgoraPushNotification()
                    // Unread-count polling and the Message badge are now
                    // owned by HomePageActivity (see setupUnreadMessagePolling())
                    // so they keep working regardless of which bottom-nav tab
                    // is currently showing, not just while Home is visible.
                }

                override fun onError(code: Int, error: String) {
                    Log.e(AGORA_TAG, "onError $code, $error")
                }
            })
    }

    private fun logoutFromChat() {
        if (agoraChatClient.isLoggedInBefore) {
            agoraChatClient.logout(false, object : CallBack {
                override fun onSuccess() {
                    Log.e(AGORA_TAG, "Chat -> onLogoutSuccess ")
                }

                override fun onError(code: Int, error: String) {
                    Log.e(AGORA_TAG, "Chat -> onLogoutError: $error")
                }
            })
        } else {
            Log.e(AGORA_TAG, "You were not logged in")
        }
    }

    private fun handleClickEvents() {
        homeUsersAdapter.onSentInterestClicked = {
            isInterestApiCalled = true
            if (requireActivity().isInternetConnection()) {
                interestType = InterestTypeConstant.sendInterest
                interestViewModel.updateInterestDetails(
                    InterestRequestData(
                        InterestTypeConstant.sendInterest,
                        it.id
                    )
                )
            } else {
                binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
            }
        }
        homeUsersAdapter.onShortlistButtonClicked = {
            isInterestApiCalled = true
            if (requireActivity().isInternetConnection()) {
                interestType = InterestTypeConstant.shortlist
                interestViewModel.updateInterestDetails(
                    InterestRequestData(
                        InterestTypeConstant.shortlist,
                        it.id
                    )
                )
            } else {
                binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
            }
        }
        homeUsersAdapter.onChatButtonClicked = {
            secUserId = it.id
            secUserFullName = it.fullName
            secUserProfile = it.profileUrl
            if (requireActivity().isInternetConnection()) {
                homeUserViewModel.updateBenefitByType(
                    authToken,
                    UpdateBenefitRequestData(
                        UpdateBenefitsConstant.chatProfiles,
                        it.id
                    )
                )
            } else {
                binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
            }
        }
        homeUsersAdapter.onItemClicked = {
            requireActivity().launchScreen<ProfileDetailsActivity> {
                putExtra(USER_ID, it.id)
            }
        }
         homeUsersAdapter.onCompatibilityReportClicked = {
            requireActivity().launchScreen<ProfileDetailsActivity> {
                putExtra(USER_ID, it.id)
                putExtra(AUTO_UNLOCK_COMPATIBILITY, true)
            }
        }
        binding.cardOnlineUsers.setOnClickListener {
            requireActivity().launchScreen<OnlineUsersActivity>()
        }
        binding.ivNotification.setOnClickListener {
            requireActivity().launchScreen<NotificationActivity>()
        }
        binding.ivFilter.setOnClickListener {
            val intent = Intent(requireActivity(), FilterActivity::class.java)
            intent.putStringArrayListExtra(FilterConstants.community, community)
            intent.putStringArrayListExtra(FilterConstants.education, education)
            intent.putExtra(FilterConstants.minHeight, minHeight)
            intent.putExtra(FilterConstants.maxHeight, maxHeight)
            intent.putExtra(FilterConstants.minAge, minAge)
            intent.putExtra(FilterConstants.maxAge, maxAge)
            intent.putStringArrayListExtra(FilterConstants.status, status)
            intent.putStringArrayListExtra(FilterConstants.profession, profession)
            intent.putStringArrayListExtra(FilterConstants.religion, religion)
            intent.putStringArrayListExtra(FilterConstants.country, country)
            intent.putStringArrayListExtra(FilterConstants.city, city)
            FILTER_REQUEST_CODE_ResultLauncher.launch(intent)
        }

        binding.btnGlobe.setOnClickListener {
            requireActivity().launchScreen<OnlineUsersActivity>()
        }
    }

    private var FILTER_REQUEST_CODE_ResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == FILTER_REQUEST_CODE) {
                val data = result.data
                if (data != null) {
                    if (data.hasExtra(FROM)) {
                        val from = data.getStringExtra(FROM)
                        if (from == "filter") {
                            binding.ivFilter.icon =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_home_filter_selected
                                )
                            binding.ivFilter.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                            )

                        } else {
                            binding.ivFilter.icon =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_home_filter
                                )
                            binding.ivFilter.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), com.google.android.libraries.places.R.color.quantum_grey300)
                            )
//                            binding.ivFilter.iconTint = ColorStateList.valueOf(Color.WHITE)
                        }
                    }

                    if (data.hasExtra(FilterConstants.community)) {
                        community = data.getStringArrayListExtra(FilterConstants.community)!!
                    }
                    if (data.hasExtra(FilterConstants.education)) {
                        education = data.getStringArrayListExtra(FilterConstants.education)!!
                    }
                    if (data.hasExtra(FilterConstants.status)) {
                        status = data.getStringArrayListExtra(FilterConstants.status)!!
                    }
                    if (data.hasExtra(FilterConstants.profession)) {
                        profession = data.getStringArrayListExtra(FilterConstants.profession)!!
                    }
                    if (data.hasExtra(FilterConstants.religion)) {
                        religion = data.getStringArrayListExtra(FilterConstants.religion)!!
                    }
                    if (data.hasExtra(FilterConstants.country)) {
                        country = data.getStringArrayListExtra(FilterConstants.country)!!
                    }
                    if (data.hasExtra(FilterConstants.city)) {
                        city = data.getStringArrayListExtra(FilterConstants.city)!!
                    }
                    if (data.hasExtra(FilterConstants.minAge)) {
                        val minAgeStr = data.getStringExtra(FilterConstants.minAge)!!
                        minAge = if (minAgeStr.isNotEmpty()) {
                            minAgeStr.trim().toInt()
                        } else {
                            StorePreferences.getFilterMinAge()
                        }
                    }
                    if (data.hasExtra(FilterConstants.maxAge)) {
                        val maxAgeStr = data.getStringExtra(FilterConstants.maxAge)!!
                        maxAge = if (maxAgeStr.isNotEmpty()) {
                            maxAgeStr.trim().toInt()
                        } else {
                            StorePreferences.getFilterMaxAge()
                        }
                    }
                    if (data.hasExtra(FilterConstants.minHeight)) {
                        val minHeightStr = data.getStringExtra(FilterConstants.minHeight)!!
                        minHeight = if (minHeightStr.isNotEmpty()) {
                            minHeightStr.trim().toDouble()
                        } else {
                            StorePreferences.getFilterMinHeight().toDouble()
                        }
                    }
                    if (data.hasExtra(FilterConstants.maxHeight)) {
                        val maxHeightStr = data.getStringExtra(FilterConstants.maxHeight)!!
                        maxHeight = if (maxHeightStr.isNotEmpty()) {
                            maxHeightStr.trim().toDouble()
                        } else {
                            StorePreferences.getFilterMaxHeight().toDouble()
                        }
                    }
                    if (requireActivity().isInternetConnection()) {
                        resetValue()
                        homeUserViewModel.getHomeUserDetails(
                            searchText = searchString,
                            minHeight = minHeight.toDouble(),
                            maxHeight = maxHeight.toDouble(),
                            minAge = minAge.toInt(),
                            maxAge = maxAge.toInt(),
                            education = education!!,
                            profession = profession!!,
                            religion = religion!!,
                            country = country!!,
                            city = city!!,
                            status = status!!,
                            community = community!!,
                            pageNumber = page
                        )
                    } else {
                        binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                    }
                }
            }
        }

    private fun initObserver() {
        getMandatoryDetailsViewModel.state.observe(requireActivity()) { it ->
            when (it) {
                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireActivity().showLoggedOutDialog() {
                            requireActivity().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        requireActivity().showErrorDialog(it.error)
                    }
                }

                is UiState.Success -> {
                    isInterestApiCalled = false
                    StorePreferences.saveCommunityList(it.data.community)
                    StorePreferences.saveUserDetails(it.data.user)
                    if (it.data.subscription != null) {
                        StorePreferences.saveSubscriptionData(it.data.subscription)
                    }
                }
            }
        }

        homeUserViewModel.homeUserList.observe(requireActivity()) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireActivity().showLoggedOutDialog() {
                            requireActivity().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        requireActivity().showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    homeUserList!!.clear()
                    homeUserList!!.addAll(it.data.users)
                    isInterestApiCalled = false
                    if (homeUserList!!.isEmpty()) {
                        binding.rvHomeUsers.hideVisibility()
                        binding.tvNoData.showVisibility()
                    } else {
                        binding.rvHomeUsers.showVisibility()
                        binding.tvNoData.hideVisibility()
                        homeUsersAdapter.submitData(homeUserList!!)
                    }
                }
            }
        }

        homeUserViewModel.homeMoreUserList.observe(requireActivity()) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireActivity().showLoggedOutDialog() {
                            requireActivity().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        requireActivity().showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    homeUserList!!.addAll(it.data.users)
                    if (homeUserList!!.isEmpty()) {
                        // No action
                    } else {
                        binding.rvHomeUsers.showVisibility()
                        binding.tvNoData.hideVisibility()
                        homeUsersAdapter.submitData(homeUserList!!)
                    }
                }
            }
        }

        interestViewModel.updateInterestState.observe(requireActivity()) { it ->
            when (it) {
                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireActivity().showLoggedOutDialog() {
                            requireActivity().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        requireActivity().showErrorDialog(it.error)
                    }
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    if (isInterestApiCalled) {
                        if (interestType == InterestTypeConstant.sendInterest) {
                            requireContext().showLongDurationToast(getString(R.string.text_sent_interest_success_msg))
                        } else {
                            requireContext().showLongDurationToast(getString(R.string.text_shortlist_profile_success_msg))
                        }
                    }
                }
            }
        }

        homeUserViewModel.state.observe(requireActivity()) { it ->
            when (it) {
                HomeMessageUiState.Idle -> {
                    // No action
                }
                HomeMessageUiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is HomeMessageUiState.Success -> {
                    loadingDialog.hideDialog()
                    facebookLogEvent(requireActivity(), FacebookAppEvent.unlock_chat)
                    requireActivity().launchScreen<ChatActivity> {
                        putExtra(SEC_USER_ID, secUserId)
                        putExtra(SEC_USER_FULL_NAME, secUserFullName)
                        putExtra(SEC_USER_PROFILE, secUserProfile)
                    }
                    homeUserViewModel.resetState()
                }

                is HomeMessageUiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireActivity().showLoggedOutDialog() {
                            requireActivity().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        if (it.errorCode == 403) {
                            loadingDialog.hideDialog()
                            facebookLogEvent(requireActivity(), FacebookAppEvent.unlock_chat)
                            requireActivity().launchScreen<ChatActivity> {
                                putExtra(SEC_USER_ID, secUserId)
                                putExtra(SEC_USER_FULL_NAME, secUserFullName)
                                putExtra(SEC_USER_PROFILE, secUserProfile)
                                putExtra("isFreeUser", true)
                            }
                            homeUserViewModel.resetState()
                        } else {
                            if (it.errorCode == ERROR_CODE_400) {
                                requireActivity().launchScreen<PurchasePlansActivity>() {
                                    putExtra(FROM, fromChat)
                                    putExtra(SEC_USER_ID, secUserId)
                                    putExtra(SEC_USER_FULL_NAME, secUserFullName)
                                    putExtra(SEC_USER_PROFILE, secUserProfile)
                                }
                            } else {
                                requireActivity().showErrorDialog(it.error + "")
                            }
                            homeUserViewModel.resetState()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initData() {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        StorePreferences.getPrimaryUserAgoraUserName()?.let {
            agoraUserId = it
        }
        StorePreferences.getPrimaryUserAgoraChatToken()?.let {
            agoraChatToken = it
        }
        StorePreferences.getSubscriptionData().let {
            subscriptionDomain = it
        }
        initChatSDK(agoraChatAppKey)
        homeUserList = ArrayList()
        community = ArrayList()
        education = ArrayList()
        profession = ArrayList()
        religion = ArrayList()
        country = ArrayList()
        city = ArrayList()
        status = ArrayList()

        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvHomeUsers.layoutManager = layoutManager
        PagerSnapHelper().attachToRecyclerView(binding.rvHomeUsers)
        binding.rvHomeUsers.adapter = homeUsersAdapter
        if (requireActivity().isInternetConnection()) {
            getMandatoryDetailsViewModel.getMandatoryDetails(authToken)
            resetValue()
            homeUserViewModel.getHomeUserDetails(
                searchText = searchString,
                minHeight = minHeight.toDouble() ?: 0.0,
                maxHeight = maxHeight.toDouble(),
                minAge = minAge.toInt(),
                maxAge = maxAge.toInt(),
                education = education!!,
                profession = profession!!,
                religion = religion!!,
                country = country!!,
                city = city!!,
                status = status!!,
                community = community!!,
                pageNumber = page
            )
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }

        binding.rvHomeUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (binding.searchView.hasFocus()) {
                    binding.searchView.clearFocus()
                    hideKeyboard(requireContext())
                }

                visibleItemCount = binding.rvHomeUsers.childCount
                totalItemCount = layoutManager!!.itemCount
                firstVisibleItem = layoutManager!!.findFirstVisibleItemPosition()
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                    requireActivity().runOnUiThread(Runnable {
                        if (requireActivity().isInternetConnection()) {
                            homeUserViewModel.getMoreHomeUserDetails(
                                searchText = searchString,
                                minHeight = minHeight.toDouble(),
                                maxHeight = maxHeight.toDouble(),
                                minAge = minAge.toInt(),
                                maxAge = maxAge.toInt(),
                                education = education!!,
                                profession = profession!!,
                                religion = religion!!,
                                country = country!!,
                                city = city!!,
                                status = status!!,
                                community = community!!,
                                pageNumber = ++page
                            )
                        }
                    })
                    loading = true
                }
            }
        })

        binding.root.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val searchViewBounds = Rect()
                    binding.searchView.getHitRect(searchViewBounds)

                    if (!searchViewBounds.contains(event.x.toInt(), event.y.toInt())) {
                        if (binding.searchView.hasFocus()) {
                            binding.searchView.clearFocus()
                            hideKeyboard(requireContext())
                        }
                    }
                    view.performClick()
                    true
                }
                else -> false
            }
        }

        binding.root.isClickable = true
        binding.root.isFocusable = true

        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        val searchPlate = binding.searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchPlate?.setBackgroundColor(Color.TRANSPARENT)

        binding.searchView.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && binding.searchView.isIconified()) {
                // Focus is truly gone
            }
        })

        binding.searchView.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                animateButtonsOut()

                val params = binding.cardSearchView.layoutParams as ConstraintLayout.LayoutParams
                params.width = 0
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                params.endToStart = ConstraintLayout.LayoutParams.UNSET
                params.marginEnd = resources.getDimensionPixelSize(R.dimen._12sdp)

                binding.cardSearchView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
                binding.cardSearchView.layoutParams = params
                binding.searchView.queryHint = getString(R.string.text_home_search)

            } else {
                Log.d("SEARCHDEBUG", "FOCUS LOST!")
                animateButtonsIn()

                val params = binding.cardSearchView.layoutParams as ConstraintLayout.LayoutParams
                params.width = resources.getDimensionPixelSize(R.dimen._30sdp)
                params.height = resources.getDimensionPixelSize(R.dimen._30sdp)
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET
                params.endToStart = binding.btnGlobe.id
                params.marginEnd = resources.getDimensionPixelSize(R.dimen._8sdp)

                binding.cardSearchView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
                binding.cardSearchView.layoutParams = params
                binding.searchView.queryHint = ""
                binding.searchView.setQuery("", false)
                hideKeyboard(requireContext())

                searchEditText.setText("")
                searchString = ""
                searchListWithInputValues(searchString)
                binding.searchView.onActionViewCollapsed()
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchText: String): Boolean {
                searchString = searchText
                searchListWithInputValues(searchString)
                return true
            }

            override fun onQueryTextChange(searchText: String): Boolean {
                return false
            }
        })

        val searchCloseButton = binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchCloseButton?.setOnClickListener {
            Log.d("SEARCHDEBUG", "Close button clicked")
            searchEditText.setText("")
            searchString = ""
            hideKeyboard(requireContext())
            searchListWithInputValues(searchString)
            binding.searchView.onActionViewCollapsed()
        }
    }

    private fun hideKeyboard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
    }

    private fun animateButtonsOut() {
        binding.btnGlobe.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.btnGlobe.visibility = View.GONE
            }
            .start()

        binding.ivFilter.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.ivFilter.visibility = View.GONE
            }
            .start()

        binding.ivNotification.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.ivNotification.visibility = View.GONE
            }
            .start()

        binding.tvTitle.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.tvTitle.visibility = View.GONE
            }
            .start()

        binding.ivTitleLogo.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.ivTitleLogo.visibility = View.GONE
            }
            .start()
    }

    private fun animateButtonsIn() {
        binding.btnGlobe.alpha = 0f
        binding.ivFilter.alpha = 0f
        binding.ivNotification.alpha = 0f
        binding.tvTitle.alpha = 0f
        binding.ivTitleLogo.alpha = 0f

        binding.btnGlobe.visibility = View.VISIBLE
        binding.ivFilter.visibility = View.VISIBLE
        binding.ivNotification.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.VISIBLE
        binding.ivTitleLogo.visibility = View.VISIBLE

        binding.btnGlobe.animate()
            .alpha(1f)
            .setDuration(200)
            .start()

        binding.ivFilter.animate()
            .alpha(1f)
            .setDuration(200)
            .start()

        binding.ivNotification.animate()
            .alpha(1f)
            .setDuration(200)
            .start()

        binding.tvTitle.animate()
            .alpha(1f)
            .setDuration(200)
            .start()

        binding.ivTitleLogo.animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    }

    private fun searchListWithInputValues(searchQuery: String) {
        resetValue()
        if (requireActivity().isInternetConnection()) {
            homeUserViewModel.getHomeUserDetails(
                searchText = searchQuery,
                minHeight = minHeight.toDouble(),
                maxHeight = maxHeight.toDouble(),
                minAge = minAge.toInt(),
                maxAge = maxAge.toInt(),
                education = education!!,
                profession = profession!!,
                religion = religion!!,
                country = country!!,
                city = city!!,
                status = status!!,
                community = community!!,
                pageNumber = page
            )
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }
    }

    fun resetValue() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        loading = true
        previousTotal = 0
        visibleThreshold = 5
        page = 1
        homeUserList!!.clear()
    }
}
