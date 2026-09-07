package com.companion.astrodating.ui.profile.ui

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.databinding.FragmentProfileBinding
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.managePhotos.ui.ManagePhotosActivity
import com.companion.astrodating.ui.policy.PrivacyPolicyActivity
import com.companion.astrodating.ui.profile.viewmodel.ProfileViewModel
import com.companion.astrodating.ui.purchasePlans.ui.PurchasePlansActivity
import com.companion.astrodating.ui.registration.viewmodel.GetMandatoryDetailsViewModel
import com.companion.astrodating.ui.requestToDeleteData.ui.RequestToDeleteDataActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.support.ui.ContactSupportActivity
import com.companion.astrodating.ui.termsofuse.TermsOfUseActivity
import com.companion.astrodating.ui.updateProfile.ui.UpdateProfileActivity
import com.companion.astrodating.ui.uploadKyc.ui.UploadKYCActivity
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.SubscriptionStatusConstants
import com.companion.astrodating.util.URL_PRIVACY_POLICY
import com.companion.astrodating.util.URL_TERMS_AND_CONDITION
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.formatDate
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreen
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showCommonDialogWithButtons
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showVisibility
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentProfileBinding
    private var profileUrl: String = APP_EMPTY_STRING
    private lateinit var loadingDialog: LoadingDialog
    private val getMandatoryDetailsViewModel: GetMandatoryDetailsViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var authToken: String = APP_EMPTY_STRING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        getIntentData()
        initObservers()
        getVersionDetails()
        handleClickEvents()
    }

    override fun onResume() {
        super.onResume()
        initView()
//        getIntentData()
    }

    private fun initObservers() {
        profileViewModel.logoutUser.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        requireContext().showLoggedOutDialog() {
                            requireContext().clearCache()
                            requireActivity().launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        requireContext().showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    requireContext().clearCache()
                    StorePreferences.clearSharedPreferences()
                    requireActivity().launchScreenAndFinish<SplashActivity>()
                }
            }
        }
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
                    loadingDialog.hideDialog()
                    StorePreferences.saveCommunityList(it.data.community)
                    StorePreferences.saveUserDetails(it.data.user)
                    if (it.data.subscription != null) {
                        if (it.data.subscription.id!=null && it.data.subscription.id.isNotEmpty()) {
                            binding.clPlanDetails.showVisibility()
                            StorePreferences.saveSubscriptionData(it.data.subscription)
                            binding.tvPlanType.text = "${
                                ContextCompat.getString(
                                    requireContext(),
                                    R.string.text_profile_plan_type
                                )
                            } ${it.data.subscription.planName}"
                            binding.tvPlanStatus.text =
                                "( ${it.data.subscription.subscriptionStatus} )"
                            if (it.data.subscription.subscriptionStatus == SubscriptionStatusConstants.active) {
                                binding.tvPlanStatus.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorMarks
                                    )
                                )
                                binding.tvPlanValidity.text = "${
                                    ContextCompat.getString(
                                        requireContext(),
                                        R.string.text_profile_plan_expires
                                    )
                                } ${formatDate(it.data.subscription.planExpiredOn)}"
                            } else {
                                binding.tvPlanStatus.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorPurchasePlanNeedHelp
                                    )
                                )
                                binding.tvPlanValidity.text = ContextCompat.getString(
                                    requireContext(),
                                    R.string.text_profile_plan_purchase
                                )
                            }
                        }else{
                            binding.clPlanDetails.hideVisibility()
                        }
                    }else{
                        binding.clPlanDetails.hideVisibility()
                    }
                    profileUrl = it.data.user.profileUrl
                    StorePreferences.saveProfileUrl(profileUrl)
                    Glide.with(binding.ivProfile.context).load(profileUrl)
                        .error(R.drawable.ic_default_profile).into(binding.ivProfile)
//
                }
            }
        }


    }

    private fun handleClickEvents() {
        binding.clEditMyProfile.setOnClickListener(this)
        binding.ivEditProfile.setOnClickListener(this)
        binding.clManagePhotos.setOnClickListener(this)
        binding.clPurchasePlans.setOnClickListener(this)
        binding.clContactSupport.setOnClickListener(this)
        binding.clLogout.setOnClickListener(this)
        binding.clCompleteKYCDetails.setOnClickListener(this)
        binding.clDeleteData.setOnClickListener(this)
        binding.clPrivacyPolicy.setOnClickListener(this)
        binding.clTermsOfUse.setOnClickListener(this)
    }

  /*  private fun getIntentData() {
//        if (arguments!=null){
//            val type = arguments?.getString(NotificationTypeConstants.type)
//            if (type == NotificationTypeConstants.photoApproved || type == NotificationTypeConstants.photoRejected){
//                requireActivity().launchScreen<ManagePhotosActivity>()
//            }
//        }

        StorePreferences.getProfileUrl()?.let {
            profileUrl = it
            Glide.with(binding.ivProfile.context).load(profileUrl)
                .error(R.drawable.ic_default_profile).into(binding.ivProfile)
        }
    }
*/
    private fun initView() {
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        if (requireActivity().isInternetConnection()) {
            getMandatoryDetailsViewModel.getMandatoryDetails(authToken)
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }

    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.clEditMyProfile, binding.ivEditProfile -> {
                requireActivity().launchScreen<UpdateProfileActivity>()
            }

            binding.clContactSupport -> {
                requireActivity().launchScreen<ContactSupportActivity>()
            }
            binding.clManagePhotos, binding.ivProfile -> {
                requireActivity().launchScreen<ManagePhotosActivity>()
            }
            binding.clPurchasePlans -> {
                requireActivity().launchScreen<PurchasePlansActivity>()
            }
            binding.clCompleteKYCDetails -> {
                requireActivity().launchScreen<UploadKYCActivity>()
            }
            binding.clDeleteData -> {
                requireActivity().launchScreen<RequestToDeleteDataActivity>()
            }
            binding.clPrivacyPolicy -> {
                requireActivity().launchScreen<PrivacyPolicyActivity>() {
                    putExtra(
                        PrivacyPolicyActivity.PRIVACY_POLICY_URL,
                        URL_PRIVACY_POLICY
                    )
                }
            }
            binding.clTermsOfUse -> {
                requireActivity().launchScreen<TermsOfUseActivity>() {
                    putExtra(
                        PrivacyPolicyActivity.KEY_URL_TERMS_OF_USE,
                        URL_TERMS_AND_CONDITION
                    )
                }
            }

            binding.clLogout -> {
                requireActivity().showCommonDialogWithButtons(
                    title = requireActivity().getString(R.string.dialog_log_out_title),
                    description = requireActivity().getString(R.string.dialog_log_out_desc),
                    btnPositiveText = requireActivity().getString(R.string.text_yes),
                    btnNegativeText = requireActivity().getString(R.string.text_no),
                    actionPositive = {
                        if (requireContext().isInternetConnection()) {
                            profileViewModel.logoutUser()
                        } else {
                            binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
                        }

                    }
                )


            }
        }
    }
    private fun getVersionDetails(){
        try {
            val packageInfo: PackageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            val versionCode = packageInfo.versionCode
            val versionName = packageInfo.versionName

            // Display version code and version name
            binding.tvVersionCode.text = "App Version Code : $versionName($versionCode)"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

}