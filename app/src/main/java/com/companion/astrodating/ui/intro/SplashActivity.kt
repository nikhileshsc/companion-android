package com.companion.astrodating.ui.intro

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.ui.home.ui.HomePageActivity
import com.companion.astrodating.ui.managePhotos.ui.ManagePhotosActivity
import com.companion.astrodating.ui.notification.ui.PushNotificationActivity
import com.companion.astrodating.ui.registration.ui.RegistrationActivity
import com.companion.astrodating.ui.updateProfile.ui.UpdateProfileActivity
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.REGISTRATION
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.setFullscreenWithNavigation
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private lateinit var language: String
    private var skipOnboarding: Boolean = false
    private var authToken: String = APP_EMPTY_STRING

    private var forceUpdateRequired = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setFullscreenWithNavigation(this,window)
        setContentView(R.layout.activity_splash)
//        Log.e(TAG, "onCreate: BASE URL:  ${NetworkModule.BASE_URL}")
        checkForAppUpdate(this)
    }

    override fun initObservers() {
        splashViewModel.isTimeout.observe(this) { isTimeout ->
            if (isTimeout) {
                handleNavigation()
            }
        }
    }

    private fun handleNavigation() {

        skipOnboarding = StorePreferences.getOnboardingStatus()
        StorePreferences.getAuthToken()?.let {
            authToken = it
        }
        val isRegistrationCompleted = StorePreferences.isRegistrationCompleted()
        /*        if (language.isEmpty()) {
                    navigateToLanguageSelectionScreen()
                } else*/ if (!skipOnboarding && !isRegistrationCompleted) {
            navigateToOnboardingScreen()
        } else {
            navigateToLogin()
        }
    }

    private fun navigateToLanguageSelectionScreen() {
        /*launchScreenAndFinish<LanguageSelectionActivity>()*/
    }

    private fun navigateToOnboardingScreen() {
        launchScreenAndFinish<OnBoardingActivity>()
    }

    fun getAppVersionName(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "0.1.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }


    fun showForceUpdateDialog(context: Activity, storeUrl: String) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Update Required")
            .setMessage("A new version of this app is required. Please update to continue.")
            .setCancelable(false)
            .setPositiveButton("Update") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl))
                context.startActivity(intent)
                context.finish()
            }
            .setNegativeButton("Exit") { _, _ ->
                context.finish()
            }
            .show()

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(context.getColor(R.color.colorPrimary)) // Replace with your color

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(context.getColor(R.color.colorPrimary))  // Replace with your color

    }



    fun checkForAppUpdate(context: Activity) {
        val currentVersion = getAppVersionName(context)
        val platform = "android"
        val url =
//            "http://192.168.0.106:3000/api/app/v1/user/checkAppVersion?platform=$platform&version=$currentVersion"
//            "http://apicompaniontest.ap-south-1.elasticbeanstalk.com/api/app/v1/user/checkAppVersion?platform=$platform&version=$currentVersion"
            "http://apicompaniondev.ap-south-1.elasticbeanstalk.com/api/app/v1/user/checkAppVersion?platform=$platform&version=$currentVersion"
        Log.d(TAG, "App Version: "+currentVersion)
        Thread {
            try {
                // Add timeouts to prevent indefinite hangs
                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build()
                val request = okhttp3.Request.Builder()
                    .url(url)
                    .build()
                val response = client.newCall(request).execute()
                val body = response.body?.string()

                if (response.isSuccessful && body != null) {
                    val json = org.json.JSONObject(body)
                    val forceUpdate = json.getBoolean("force_update")
                    val storeUrl = json.getString("store_url")

                    // CRITICAL FIX: Check if activity is still alive before updating UI
                    if (!context.isDestroyed && !context.isFinishing) {
                        context.runOnUiThread {
                            if (forceUpdate) {
                                forceUpdateRequired = true
                                showForceUpdateDialog(context, storeUrl)
                            } else {
                                // proceed to main screen
                            }
                        }
                    } else {
                        Log.d(TAG, "Activity destroyed before response, skipping dialog")
                    }
                }
                else{
                    Log.d(TAG,"Error in app version: "+response.body)
                }
                Log.d(TAG, "completed")

            } catch (e: Exception) {
                Log.d(TAG,"Error in app version: "+ e.message)
                e.printStackTrace()
                // If API fails, proceed as normal
            }
        }.start()
    }


    private fun navigateToLogin() {
        if(forceUpdateRequired){
            return
        }
        val profileUrl = StorePreferences.getProfileUrl()
        Log.e(
            TAG,
            "StorePreferences.isRegistrationCompleted()-${StorePreferences.isRegistrationCompleted()}, profile -$profileUrl"
        )

        if (authToken != null && authToken.isNotEmpty()) {
            if (StorePreferences.isRegistrationCompleted()) {
                if (StorePreferences.isUpdateProfileCompleted()) {
                    if (profileUrl != null && profileUrl.isNotEmpty()) {
                        if (NotificationManagerCompat.from(this)
                                .areNotificationsEnabled()
                        ) {
                            launchScreenAndFinish<HomePageActivity>() {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        } else {
                            launchScreenAndFinish<PushNotificationActivity>()
                        }
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
                launchScreenAndFinish<RegistrationActivity>()
            }
        }else {
            launchScreenAndFinish<OnBoardingActivity>()
        }
    }
}