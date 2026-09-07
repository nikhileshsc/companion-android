package com.companion.astrodating.ui.notification.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityPushNotificationBinding
import com.companion.astrodating.ui.home.ui.HomePageActivity
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.registration.viewmodel.GetMandatoryDetailsViewModel
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PushNotificationActivity : BaseActivity() {

    private val binding by lazy {
        ActivityPushNotificationBinding.inflate(layoutInflater)
    }
    private val getMandatoryDetailsViewModel: GetMandatoryDetailsViewModel by viewModels()
    private var authToken: String = APP_EMPTY_STRING
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        loadingDialog = LoadingDialog(this)
        handleClickEvents()
    }

    override fun initObservers() {
        getMandatoryDetailsViewModel.state.observe(this) { state ->
            when (state) {
                UiState.Loading -> {
                    loadingDialog.showDialog()
                }
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (state.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(state.error)
                    }
                }
                is UiState.Success -> {
                    loadingDialog.hideDialog()

                }
            }
        }

    }

    private fun handleClickEvents(){
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        if (isInternetConnection()) {
            getMandatoryDetailsViewModel.getMandatoryDetails(authToken)
        }else {
            binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
        }
        binding.btnProceed.setOnClickListener{
            askForNotificationPermission()
        }
    }
    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }else{
                    launchScreenAndFinish<HomePageActivity>(){
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    showPermissionNeededDialog()
                }
            }
        }
    }

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(
                    this,
                    getString(R.string.text_permission_granted),
                    Toast.LENGTH_SHORT
                ).show()
                launchScreenAndFinish<HomePageActivity>(){
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }

            } else {
                showPermissionNeededDialog()
            }
        }

    private fun showPermissionNeededDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.text_permission_required_title))
            .setMessage(getString(R.string.text_permission_required_message))
            .setCancelable(false)
            .setPositiveButton(R.string.text_settings_title) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
//                finish()
            }.show()
    }


}