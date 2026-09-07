package com.companion.astrodating.ui.profileDetails.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.AshtakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.DashakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.ManglikDoshaDomainEntity
import com.companion.astrodating.data.divineapi.domain.model.P1DomainEntity
import com.companion.astrodating.data.divineapi.viewmodel.GetAshtakootMilanDetailsViewModel
import com.companion.astrodating.data.divineapi.viewmodel.GetAstrologyDetailsViewModel
import com.companion.astrodating.data.divineapi.viewmodel.GetDashakootMilanDetailsViewModel
import com.companion.astrodating.data.divineapi.viewmodel.GetManglikDoshaDetailsViewModel
import com.companion.astrodating.databinding.ActivityProfileDetailsBinding
import com.companion.astrodating.ui.chat.ChatActivity
import com.companion.astrodating.ui.completeanalysis.ui.CompleteAnalysisActivity
import com.companion.astrodating.ui.interests.data.requestData.InterestRequestData
import com.companion.astrodating.ui.interests.viewmodel.InterestsViewModel
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.profileDetails.domain.model.GetUserDetailsDomain
import com.companion.astrodating.ui.profileDetails.domain.model.GetUserDetailsGalleryDomain
import com.companion.astrodating.ui.profileDetails.ui.adapter.ProfileImageAdapter
import com.companion.astrodating.ui.profileDetails.viewmodel.GetUserDetailsViewModel
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.purchasePlans.ui.PurchasePlansActivity
import com.companion.astrodating.ui.purchasePlans.viewmodel.UpdateBenefitsDetailsViewModel
import com.companion.astrodating.ui.registration.domain.model.GetMandatoryUserDetailsDomainEntity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ASHTAGUNA_MILAN_DATA
import com.companion.astrodating.util.ASTROLOGY_DETAILS_P1_DATA
import com.companion.astrodating.util.ASTROLOGY_DETAILS_P2_DATA
import com.companion.astrodating.util.DASHAGUNA_MILAN_DATA
import com.companion.astrodating.util.ERROR_CODE_400
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.FacebookAppEvent
import com.companion.astrodating.util.InterestTypeConstant
import com.companion.astrodating.util.LANG_ENGLISH
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.MANGLIK_DOSHA_DATA
import com.companion.astrodating.util.SEC_USER_FULL_NAME
import com.companion.astrodating.util.SEC_USER_ID
import com.companion.astrodating.util.SEC_USER_PROFILE
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.TIME_ZONE
import com.companion.astrodating.util.USER_1
import com.companion.astrodating.util.USER_2
import com.companion.astrodating.util.USER_ID
import com.companion.astrodating.util.UpdateBenefitsConstant
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.facebookLogEvent
import com.companion.astrodating.util.formatDateForBirthDate
import com.companion.astrodating.util.formatNumber
import com.companion.astrodating.util.fromChat
import com.companion.astrodating.util.fromUnlockCompleteAnalysis
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreen
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showCommonDialogWithButtons
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showLongDurationToast
import com.companion.astrodating.util.showShortDurationSnackBar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class ProfileDetailsActivity : BaseActivity(), View.OnClickListener {
    private val binding by lazy {
        ActivityProfileDetailsBinding.inflate(layoutInflater)
    }
    private val interestViewModel: InterestsViewModel by viewModels()
    private val getUserDetailsViewModel: GetUserDetailsViewModel by viewModels()
    private val getAshtakootMilanDetailsViewModel: GetAshtakootMilanDetailsViewModel by viewModels()
    private val getDashakootMilanDetailsViewModel: GetDashakootMilanDetailsViewModel by viewModels()
    private val getAstrologyDetailsViewModel: GetAstrologyDetailsViewModel by viewModels()
    private val getManglikDoshaDetailsViewModel: GetManglikDoshaDetailsViewModel by viewModels()
    private val updateBenefitsDetailsViewModel: UpdateBenefitsDetailsViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    private var authToken: String = APP_EMPTY_STRING
    private var divineApiAuthToken: String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2FzdHJvYXBpLTEuZGl2aW5lYXBpLmNvbS9hcGkvYXV0aC1hcGktdXNlciIsImlhdCI6MTcyNTMzODE0NCwibmJmIjoxNzI1MzM4MTQ0LCJqdGkiOiJ4bDYxV044YWJNdW8wd0k1Iiwic3ViIjoiMjQxNSIsInBydiI6ImU2ZTY0YmIwYjYxMjZkNzNjNmI5N2FmYzNiNDY0ZDk4NWY0NmM5ZDcifQ.MJKjWRpsGcodk7snvwzirdF5FwmQAlHdcRQF6uWBIAI"
    private var divineApiKey: String = "e3ca0449fa2ea7701a7ac53fb719c51a"
    private var userId: String = APP_EMPTY_STRING
    private var user2: String = APP_EMPTY_STRING
    private var profileUrl: String = APP_EMPTY_STRING
    private var interestType: String = APP_EMPTY_STRING
    private var primaryUserDetails: GetMandatoryUserDetailsDomainEntity? = null
    private var secondaryUserDetails: GetUserDetailsDomain? = null
    private var ashtakootMillanDomainData: AshtakootMilanDomainDetails.AshtakootMilanDomain? = null
    private var dashakootMilanDomainData: DashakootMilanDomainDetails.DashakootMilanDataDomain? =
        null
    private var p1DomainEntity: P1DomainEntity? = null
    private var p2DomainEntity: P1DomainEntity? = null
    private var manglikDoshaDomainEntity: ManglikDoshaDomainEntity? = null
    private var subscriptionDomain: GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity? =
        null
    private var benefitType = APP_EMPTY_STRING
    private var userStatus = APP_EMPTY_STRING
    private val profileImageAdapter by lazy {
        ProfileImageAdapter()
    }
    private var currentPageCount = 0
    private var arrayList = arrayListOf<GetUserDetailsGalleryDomain>()

    var MIN_POSTION = 0
    var MAX_POSTION = 0
    private var popup : PopupMenu?=null
    var ashtakootMilanRequestData : AshtakootMilanRequestData?=null
    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            MIN_POSTION = position
            currentPageCount = position
            if (arrayList.isNotEmpty()) {
//                dotAdapter.changeDotPosition(arrayList[position].position)
            }
            countDownTimer.cancel()
            countDownTimer.start()
            if (position == arrayList.size - 2)
                binding.viewPager.post(runnable)
//            if (position == IntroList.introList.size - 1) {
////                binding.btnGetStarted.text = getString(R.string.text_get_started)
////                binding.ivAction.setImageResource(R.drawable.ic_done_white)
//            } else {
////                binding.btnGetStarted.text = getString(R.string.text_next_title)
////                binding.ivAction.setImageResource(R.drawable.ic_arrow_forward_white)
//            }
        }
    }

    val countDownTimer = object : CountDownTimer(3000, 1000) {
        override fun onTick(p0: Long) {}

        override fun onFinish() {
            if (MIN_POSTION < MAX_POSTION) {
                MIN_POSTION += 1
                //Log.e("onFinish: ", "" + MIN_POSTION)
                binding.viewPager.currentItem = MIN_POSTION
            } else {
                MIN_POSTION = 0
                binding.viewPager.setCurrentItem(MIN_POSTION, true)
            }
        }
    }

    var count = 0
    val runnable = Runnable {
        if (count < 3) {
//            Log.e("Runable: ", "$count")
            count++
            arrayList.addAll(arrayList)
            MAX_POSTION = arrayList.size - 1
            profileImageAdapter.notifyDataSetChanged()
        } else {
            count = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        loadingDialog = LoadingDialog(this)
        getIntentData()
        initViewData()
        handleClickEvents()
    }

    private fun showPopupMenu(view: View) {
        popup = PopupMenu(this, view)
        popup!!.menuInflater.inflate(R.menu.more_menu_options, popup!!.menu)
        val blockMenu = popup!!.menu.findItem(R.id.action_block)
        val unblockMenu = popup!!.menu.findItem(R.id.action_un_block)
        if (userStatus.isNotEmpty() && userStatus == "Blocked"){
            blockMenu.setVisible(false)
            unblockMenu.setVisible(true)
        }else{
            blockMenu.setVisible(true)
            unblockMenu.setVisible(false)
        }
        popup!!.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_block -> {
                    showCommonDialogWithButtons(
                        title = getString(R.string.dialog_block_user_title),
                        description = getString(R.string.dialog_block_user_desc),
                        btnPositiveText = getString(R.string.text_yes),
                        btnNegativeText = getString(R.string.text_no),
                        actionPositive = {
                            if (isInternetConnection()) {
                                interestType = InterestTypeConstant.block
                                interestViewModel.updateInterestDetails(
                                    InterestRequestData(
                                        InterestTypeConstant.block,
                                        secondaryUserDetails!!.id
                                    )
                                )
                            } else {
                                binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                            }


                        }
                    )

                    true
                } // Handle edit action
                R.id.action_un_block -> {
                    showCommonDialogWithButtons(
                        title = getString(R.string.dialog_unblock_user_title),
                        description = getString(R.string.dialog_unblock_user_desc),
                        btnPositiveText = getString(R.string.text_yes),
                        btnNegativeText = getString(R.string.text_no),
                        actionPositive = {
                            if (isInternetConnection()) {
                                interestType = InterestTypeConstant.unblock
                                interestViewModel.updateInterestDetails(
                                    InterestRequestData(
                                        InterestTypeConstant.unblock,
                                        secondaryUserDetails!!.id
                                    )
                                )
                            } else {
                                binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                            }


                        }
                    )

                    true
                }
                R.id.action_decline_user -> {
                    showCommonDialogWithButtons(
                        title = getString(R.string.dialog_decline_interest_title),
                        description = getString(R.string.dialog_decline_interest_desc),
                        btnPositiveText = getString(R.string.text_yes),
                        btnNegativeText = getString(R.string.text_no),
                        actionPositive = {
                            if (isInternetConnection()) {
                                interestType = InterestTypeConstant.declineInterest
                                interestViewModel.updateInterestDetails(
                                    InterestRequestData(
                                        InterestTypeConstant.declineInterest,
                                        secondaryUserDetails!!.id
                                    )
                                )
                            } else {
                                binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                            }


                        }
                    )

                    true
                }


                else -> false
            }
        }
        popup!!.show() // Show the popup menu
    }
    private fun setupViews() {
        profileImageAdapter.submitData(arrayList)
        MAX_POSTION = arrayList.size - 1
        binding.viewPager.adapter = profileImageAdapter
        binding.viewPager.clipChildren = false
        binding.viewPager.clipToPadding = false
        binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

//        binding.dotsIndicator.attachTo(binding.viewPager)
        binding.viewPager.registerOnPageChangeCallback(pageCallback)
    }


    private fun getIntentData() {
        if (intent != null) {
            userId = intent.getStringExtra(USER_ID) ?: APP_EMPTY_STRING
//            secondaryUserDetailsFromTopUser = Gson().fromJson(
//                intent.getStringExtra(SEC_USER_DATA) ?: APP_EMPTY_STRING,
//                GetHomeUserDomainEntity::class.java
//            )
        }
    }

    private fun handleClickEvents() {

        binding.clUnlockCompleteAnalysis.setOnClickListener(this)
        binding.ivSendInterest.setOnClickListener(this)
        binding.tvSendInterest.setOnClickListener(this)
        binding.tvShortlist.setOnClickListener(this)
        binding.ivShortlist.setOnClickListener(this)
        binding.ivChat.setOnClickListener(this)
        binding.tvChat.setOnClickListener(this)
        binding.ivMoreSettings.setOnClickListener(this)
    }

    private fun initViewData() {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
//        StorePreferences.getDivineApiKey()?.let {
//            divineApiKey = it
//        }
//        StorePreferences.getDivineApiAccessToken()?.let {
//            divineApiAuthToken = it
//        }
        StorePreferences.getUserDetails().let {
            primaryUserDetails = it
        }
        StorePreferences.getProfileUrl()?.let {
            profileUrl = it
            Glide.with(binding.ivUser1.context).load(profileUrl)
                .error(R.drawable.ic_default_profile).into(binding.ivUser1)
        }
        StorePreferences.getSubscriptionData().let {
            subscriptionDomain = it
        }
        arrayList = ArrayList()
        if (isInternetConnection()) {
            if (userId.isNotEmpty()) {
                getUserDetailsViewModel.getUserDetails(authToken, userId)
            }
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }
    }

    override fun initObservers() {
        getUserDetailsViewModel.state.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
//                    loadingDialog.hideDialog()

                    secondaryUserDetails = it.data
                    arrayList.addAll(secondaryUserDetails!!.gallery)
                    setupViews()
                    if (secondaryUserDetails != null) {
                        getAshtakootMilanDetailsApi(secondaryUserDetails!!)
                        user2 = secondaryUserDetails?.fullName!!
                        "${user2}, ${secondaryUserDetails?.age}".also {
                            binding.tvUserName.text = it
                        }
                        Glide.with(binding.ivUser2.context).load(secondaryUserDetails?.profileUrl)
                            .error(R.drawable.ic_default_profile).into(binding.ivUser2)
                        binding.tvAboutDesc.text = secondaryUserDetails?.aboutYourself
                        binding.tvMaritalStatus.text = secondaryUserDetails?.status
                        binding.tvHeight.text = formatNumber( secondaryUserDetails?.height!!)
                        binding.tvCommunity.text = secondaryUserDetails?.community
                        binding.tvReligion.text = secondaryUserDetails?.religion
                        binding.tvLocation.text = secondaryUserDetails?.currentCity
                        binding.tvProfession.text = secondaryUserDetails?.profession
                        binding.tvUserId.text = secondaryUserDetails?.userId
                        binding.tvEducation.text = secondaryUserDetails?.education
                        binding.tvInterestDesc.text = secondaryUserDetails?.interest
                        binding.tvExpectationDesc.text = secondaryUserDetails?.expectations
                        userStatus = secondaryUserDetails?.userStatus!!
                        binding.tvGalleryCount.text =
                            secondaryUserDetails?.approvedPhotosCount.toString()
                    }

                }
            }
        }

        getAshtakootMilanDetailsViewModel.state.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
//                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
//                    loadingDialog.hideDialog()
                    if (it.data.success == 1) {
                        getDashakootMilanDetailsViewModel.getDashakootMilanDetails(
                            divineApiAuthToken, ashtakootMilanRequestData!!
                        )
                        ashtakootMillanDomainData = it.data.data
                        binding.tvUserGunaMatch.text =
                            "${it.data.data.ashtakoot_milan_result.points_obtained} / ${it.data.data.ashtakoot_milan_result.max_ponits}"

                    } else if (it.data.success == 2) {
                        loadingDialog.hideDialog()
                        // CRITICAL FIX: Safely handle msg which can be String or Object
                        showErrorDialog(getErrorMessage(it.data.msg))

                    } else if (it.data.success == 3) {
                        loadingDialog.hideDialog()
                        showErrorDialog(getErrorMessage(it.data.msg))
                    }

                }
            }
        }

        getDashakootMilanDetailsViewModel.state.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
//                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
//                    loadingDialog.hideDialog()
                    if (it.data.success == 1) {
                        dashakootMilanDomainData = it.data.data
                        getAstrologyDetailsViewModel.getAstrologyDetails(
                            divineApiAuthToken, ashtakootMilanRequestData!!
                        )

                    } else if (it.data.success == 2) {
                        loadingDialog.hideDialog()
                        showErrorDialog(getErrorMessage(it.data.msg))

                    } else if (it.data.success == 3) {
                        loadingDialog.hideDialog()
                        showErrorDialog(getErrorMessage(it.data.msg))
                    }

                }
            }
        }

        getAstrologyDetailsViewModel.state.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
//                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
//                    loadingDialog.hideDialog()
                    if (it.data.success == 1) {
                        p1DomainEntity = it.data.p1
                        p2DomainEntity = it.data.p2
                        getManglikDoshaDetailsViewModel.getManglikDoshaDetails(
                            divineApiAuthToken, ashtakootMilanRequestData!!
                        )
                    } else if (it.data.success == 2) {
                        loadingDialog.hideDialog()
                        showErrorDialog(getErrorMessage(it.data.msg))

                    } else if (it.data.success == 3) {
                        loadingDialog.hideDialog()
                        showErrorDialog(getErrorMessage(it.data.msg))
                    }

                }
            }
        }

        getManglikDoshaDetailsViewModel.state.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
//                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    if (it.data.success == 1) {
                        manglikDoshaDomainEntity = it.data.data

                    } else if (it.data.success == 2) {
                        loadingDialog.hideDialog()
                        showErrorDialog(getErrorMessage(it.data.msg))

                    } else if (it.data.success == 3) {
                        loadingDialog.hideDialog()
                        showErrorDialog(getErrorMessage(it.data.msg))
                    }

                }
            }
        }

        interestViewModel.updateInterestState.observe(this) { it ->
            when (it) {
                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    if (interestType == InterestTypeConstant.sendInterest) {
                        showLongDurationToast(getString(R.string.text_sent_interest_success_msg))
                    } else if (interestType == InterestTypeConstant.shortlist) {
                        showLongDurationToast(getString(R.string.text_shortlist_profile_success_msg))
                    } else if (interestType == InterestTypeConstant.block) {
                        showLongDurationToast(getString(R.string.text_block_profile_success_msg))
                    } else if (interestType == InterestTypeConstant.unblock) {
                        showLongDurationToast(getString(R.string.text_unblock_profile_success_msg))
                    }else if (interestType == InterestTypeConstant.declineInterest) {
                        showLongDurationToast(getString(R.string.text_decline_interest_success_msg))
                    }
                }
            }
        }

        updateBenefitsDetailsViewModel.state.observe(this) { it ->
            when (it) {
                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        if(it.errorCode == 403){
                            launchScreen<ChatActivity> {
                                putExtra(SEC_USER_ID, secondaryUserDetails!!.id)
                                putExtra(SEC_USER_FULL_NAME, secondaryUserDetails!!.fullName)
                                putExtra(SEC_USER_PROFILE, secondaryUserDetails!!.profileUrl)
                                putExtra("isFreeUser",true)
                            }
                        }else {
                            if (it.errorCode == ERROR_CODE_400) {
                                launchScreen<PurchasePlansActivity>() {
                                    if (benefitType == UpdateBenefitsConstant.matchMakingReport) {
                                        putExtra(FROM, fromUnlockCompleteAnalysis)
                                    } else {
                                        putExtra(FROM, fromChat)
                                    }
                                    putExtra(SEC_USER_ID, secondaryUserDetails!!.id)
                                    putExtra(SEC_USER_FULL_NAME, secondaryUserDetails!!.fullName)
                                    putExtra(SEC_USER_PROFILE, secondaryUserDetails!!.profileUrl)
                                    putExtra(
                                        ASHTAGUNA_MILAN_DATA,
                                        Gson().toJson(ashtakootMillanDomainData)
                                    )
                                    putExtra(
                                        DASHAGUNA_MILAN_DATA,
                                        Gson().toJson(dashakootMilanDomainData)
                                    )
                                    putExtra(
                                        ASTROLOGY_DETAILS_P1_DATA,
                                        Gson().toJson(p1DomainEntity)
                                    )
                                    putExtra(
                                        ASTROLOGY_DETAILS_P2_DATA,
                                        Gson().toJson(p2DomainEntity)
                                    )
                                    putExtra(
                                        MANGLIK_DOSHA_DATA,
                                        Gson().toJson(manglikDoshaDomainEntity)
                                    )
                                    putExtra(USER_1, primaryUserDetails!!.fullName)
                                    putExtra(USER_2, user2)
                                }
                            } else {
                                showErrorDialog(it.error)
                            }
                        }
                    }
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    if (benefitType == UpdateBenefitsConstant.matchMakingReport) {
                        if (ashtakootMillanDomainData != null) {
                            facebookLogEvent(this, FacebookAppEvent.unlock_analysis)
                            launchScreen<CompleteAnalysisActivity> {
                                putExtra(
                                    ASHTAGUNA_MILAN_DATA,
                                    Gson().toJson(ashtakootMillanDomainData)
                                )
                                putExtra(
                                    DASHAGUNA_MILAN_DATA,
                                    Gson().toJson(dashakootMilanDomainData)
                                )
                                putExtra(ASTROLOGY_DETAILS_P1_DATA, Gson().toJson(p1DomainEntity))
                                putExtra(ASTROLOGY_DETAILS_P2_DATA, Gson().toJson(p2DomainEntity))
                                putExtra(
                                    MANGLIK_DOSHA_DATA,
                                    Gson().toJson(manglikDoshaDomainEntity)
                                )
                                putExtra(USER_1, primaryUserDetails!!.fullName)
                                putExtra(USER_2, user2)
                            }
                        }
                    } else {
                        facebookLogEvent(this, FacebookAppEvent.unlock_chat)
                        launchScreen<ChatActivity> {
                            putExtra(SEC_USER_ID, secondaryUserDetails!!.id)
                            putExtra(SEC_USER_FULL_NAME, secondaryUserDetails!!.fullName)
                            putExtra(SEC_USER_PROFILE, secondaryUserDetails!!.profileUrl)
                        }
                    }
                }
            }
        }


    }

    /**
     * Helper function to safely extract error message from API response
     * Handles cases where msg can be either String or Object
     */
    private fun getErrorMessage(msg: Any?): String {
        return when (msg) {
            is String -> msg
            null -> "An error occurred"
            else -> msg.toString()
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.clUnlockCompleteAnalysis -> {
                benefitType = UpdateBenefitsConstant.matchMakingReport
                if (isInternetConnection()) {
                    updateBenefitsDetailsViewModel.updateBenefitByType(
                        authToken,
                        UpdateBenefitRequestData(
                            benefitType,
                            secondaryUserDetails!!.id
                        )
                    )
                } else {
                    binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                }
            }

            binding.ivSendInterest, binding.tvSendInterest -> {
                if (isInternetConnection()) {
                    interestType = InterestTypeConstant.sendInterest
                    interestViewModel.updateInterestDetails(
                        InterestRequestData(
                            InterestTypeConstant.sendInterest,
                            secondaryUserDetails!!.id
                        )
                    )
                } else {
                    binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                }

            }

            binding.ivShortlist, binding.tvShortlist -> {
                if (isInternetConnection()) {
                    interestType = InterestTypeConstant.shortlist
                    interestViewModel.updateInterestDetails(
                        InterestRequestData(
                            InterestTypeConstant.shortlist,
                            secondaryUserDetails!!.id
                        )
                    )
                } else {
                    binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                }

            }

            binding.ivMoreSettings -> {
                showPopupMenu(binding.ivMoreSettings)
            }

            binding.ivChat, binding.tvChat -> {
                benefitType = UpdateBenefitsConstant.chatProfiles
                if (isInternetConnection()) {
                    updateBenefitsDetailsViewModel.updateBenefitByType(
                        authToken,
                        UpdateBenefitRequestData(
                            benefitType,
                            secondaryUserDetails!!.id
                        )
                    )
                } else {
                    binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                }
            }
        }

    }

    fun getAshtakootMilanDetailsApi(secondaryUserDetails: GetUserDetailsDomain) {
        lifecycleScope.launch {
            try {
                if (primaryUserDetails != null) {
//                    StorePreferences.getDivineApiKey()?.let {
//                        divineApiKey = it
//                    }
                    if (divineApiKey.isNotEmpty()) {
                        val p1_full_name = primaryUserDetails!!.fullName
                        val p1_birthDate = formatDateForBirthDate(primaryUserDetails!!.birthDate)
                        val datePartsForP1 = p1_birthDate.split("-")

                        // Extract day, month, and year
                        val p1_year = datePartsForP1[0].toInt()
                        val p1_month = datePartsForP1[1].toInt()
                        val p1_day = datePartsForP1[2].toInt()

                        val p1_time_of_birth = primaryUserDetails!!.timeOfBirth
                        val timePartsForP1 = p1_time_of_birth.split(":")

                        // Extract day, month, and year
                        val p1_hour = timePartsForP1[0].toInt()
                        val p1_min = timePartsForP1[1].toInt()
                        val p1_sec = 0

                        val p1_gender = primaryUserDetails!!.gender.lowercase(Locale.getDefault())
                        val p1_place = primaryUserDetails!!.cityOfBirth
                        val p1_lat = primaryUserDetails?.latitudeOfCityOfBirth?.toDouble() ?: 0.0
                        val p1_lon = primaryUserDetails?.longitudeOfCityOfBirth?.toDouble() ?: 0.0


                        if (secondaryUserDetails != null) {
                            val p2_full_name = secondaryUserDetails.fullName
                            val p2_birthDate =
                                formatDateForBirthDate(secondaryUserDetails.birthDate)
                            val datePartsForP2 = p2_birthDate.split("-")

                            // Extract day, month, and year
                            val p2_year = datePartsForP2[0].toInt()
                            val p2_month = datePartsForP2[1].toInt()
                            val p2_day = datePartsForP2[2].toInt()

                            val p2_time_of_birth = secondaryUserDetails.timeOfBirth
                            val timePartsForP2 = p2_time_of_birth.split(":")

                            // Extract day, month, and year
                            val p2_hour = timePartsForP2[0].toInt()
                            val p2_min = timePartsForP2[1].toInt()
                            val p2_sec = 0

                            val p2_gender =
                                secondaryUserDetails.gender.lowercase(Locale.getDefault())
                            val p2_place = secondaryUserDetails.cityOfBirth
                            val p2_lat = secondaryUserDetails.latitudeOfCityOfBirth!!.toDouble()
                            val p2_lon =
                                secondaryUserDetails.longitudeOfCityOfBirth!!.toDouble()

                            binding.tvUser1.text = primaryUserDetails!!.fullName
                            binding.tvUser2.text = secondaryUserDetails.fullName
                            ashtakootMilanRequestData = AshtakootMilanRequestData(
                                api_key = divineApiKey,
                                p1_day = p1_day,
                                p1_full_name = p1_full_name,
                                p1_gender = p1_gender,
                                p1_hour = p1_hour,
                                p1_lat = p1_lat,
                                p1_lon = p1_lon,
                                p1_min = p1_min,
                                p1_month = p1_month,
                                p1_place = p1_place,
                                p1_sec = p1_sec,
                                p1_tzone = TIME_ZONE,
                                p1_year = p1_year,
                                p2_day = p2_day,
                                p2_full_name = p2_full_name,
                                p2_gender = p2_gender,
                                p2_hour = p2_hour,
                                p2_lat = p2_lat,
                                p2_lon = p2_lon,
                                p2_min = p2_min,
                                p2_month = p2_month,
                                p2_place = p2_place,
                                p2_sec = p2_sec,
                                p2_tzone = TIME_ZONE,
                                p2_year = p2_year,
                                lan = LANG_ENGLISH
                            )

                            if (isInternetConnection()) {
                                StorePreferences.saveAshtakootRequestData(ashtakootMilanRequestData!!)
                                getAshtakootMilanDetailsViewModel.getAshtakootMilanDetails(
                                    divineApiAuthToken, ashtakootMilanRequestData!!
                                )

                            } else {
                                binding.root.showLongDurationSnackBar(
                                    resources.getString(
                                        R.string.text_no_internet_connection
                                    )
                                )

                            }
                        }
                    } else {
                        binding.root.showLongDurationSnackBar(
                            "Divine API Key is Empty"
                        )

                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error in getAshtakootMilanDetailsApi: ${e.message}", e)
                e.printStackTrace()
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(pageCallback)
    }

}