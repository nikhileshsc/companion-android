package com.companion.astrodating.ui.home.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityHomePageBinding
import com.companion.astrodating.ui.home.domain.model.InterestDomain
import com.companion.astrodating.ui.interests.ui.InterestsFragment
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.managePhotos.ui.ManagePhotosActivity
import com.companion.astrodating.ui.profile.ui.ProfileFragment
import com.companion.astrodating.ui.profile.viewmodel.ProfileViewModel
import com.companion.astrodating.ui.purchasePlans.GooglePlayPurchaseReconciler
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.InterestTypeConstant
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.NotificationTypeConstants
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreen
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.setFullscreenWithNavigation
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomePageActivity : BaseActivity() {
    @Inject lateinit var googlePlayPurchaseReconciler: GooglePlayPurchaseReconciler
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navHostController: NavController
    private lateinit var appUpdateManager: AppUpdateManager
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    var notificationBody = APP_EMPTY_STRING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        setFullscreenWithNavigation(this, window)
        checkForAppUpdates()
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        enableEdgeToEdge()
        loadingDialog = LoadingDialog(this)
        setupNavigationComponents()
        getIntentData()
        googlePlayPurchaseReconciler.reconcile()
    }

    private fun getIntentData() {

    }

    override fun initObservers() {
        profileViewModel.logoutUser.observe(this) {
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
                    loadingDialog.hideDialog()
                    showLoggedOutDialog(title =  "Delete Data Received.",
                        description = notificationBody) {
                        clearCache()
                        launchScreenAndFinish<SplashActivity>()
                    }

                }
            }
        }
    }

    private fun setupNavigationComponents() {
        bottomNavView = binding.bottomNavigationView
        bottomNavView.itemIconTintList = null
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragNavHost) as NavHostFragment
        navHostController = navHostFragment.navController

        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(navView, navHostController)

        if (intent != null) {
            intent?.getStringExtra("targetFragment")?.let { targetFragment ->
                val type = intent?.getStringExtra(NotificationTypeConstants.type)
                val bundle = Bundle()
                bundle.putString(NotificationTypeConstants.type, type)
                Log.e(TAG, "NotificationTypeConstants type-$type")
                when (targetFragment) {
                    InterestsFragment::class.java.name -> {
                        if (type == NotificationTypeConstants.receivedInterest) {
                            val interestDomain =
                                InterestDomain(
                                    image = R.drawable.ic_interest_receivedinterest,
                                    title = R.string.text_interest_receivedinterest,
                                    interestType = InterestTypeConstant.receivedInterest
                                )
                            bundle.putString(
                                InterestTypeConstant.interestData,
                                Gson().toJson(interestDomain)
                            )
                            binding.bottomNavigationView.selectedItemId =
                                R.id.interestsFragment
                            navHostController.navigate(R.id.interestUserFragment, bundle)
                        } else if (type == NotificationTypeConstants.declineInterest) {
                            val interestDomain =
                                InterestDomain(
                                    image = R.drawable.ic_interest_declined,
                                    title = R.string.text_interest_declinedlist,
                                    interestType = InterestTypeConstant.declineInterest
                                )
                            bundle.putString(
                                InterestTypeConstant.interestData,
                                Gson().toJson(interestDomain)
                            )
                            binding.bottomNavigationView.selectedItemId =
                                R.id.interestsFragment
                            navHostController.navigate(R.id.interestUserFragment, bundle)
                        }
                    }

                    ProfileFragment::class.java.name -> launchScreen<ManagePhotosActivity>()
                    NotificationTypeConstants.deleteMyAccount ->{
                        notificationBody = intent?.getStringExtra("body")!!
                        if (isInternetConnection()) {
                            profileViewModel.logoutUser()
                        } else {
                            binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
                        }
                    }
                    // Handle other fragments as needed
                }
            }
        }
    }

    //    override fun onBackPressed() {
//        // Handle back press to ensure the bottom navigation reflects the current fragment
//        if (navHostController.currentDestination?.id == R.id.homeFragment) {
//            super.onBackPressed() // Exit the app if on home
//        } else {
//            navHostController.popBackStack() // Navigate back in the NavController
//        }
//    }
    override fun onResume() {
        super.onResume()
        googlePlayPurchaseReconciler.reconcile()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            }
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = info.isImmediateUpdateAllowed
            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info, activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.e(TAG, "Update flow failed! Result code: " + result.resultCode);
                // If the update is canceled or fails,
                // you can request to start the update again.
            }

        }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            // Check if the current fragment is HomeFragment
            val currentFragment = fragmentManager.findFragmentById(R.id.fragNavHost)
            if (currentFragment is HomeFragment) {
                binding.bottomNavigationView.selectedItemId =
                    R.id.homeFragment // Set home as selected
            }
        } else {
            super.onBackPressed() // Exit the app if no fragments are in the back stack
        }
    }
}
