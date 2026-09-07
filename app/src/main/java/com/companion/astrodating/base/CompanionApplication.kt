package com.companion.astrodating.base

import android.app.Application
import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import com.companion.astrodating.ui.home.viewmodel.HomeUserViewModel
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.isInternetConnection
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okio.IOException
import javax.inject.Inject

@HiltAndroidApp
class CompanionApplication : Application() {

    init {
        System.loadLibrary("keys")
    }

    private external fun getPlacesKey(): String
    private  val PlacesApiKey = String(Base64.decode(getPlacesKey(), Base64.DEFAULT))

    private external fun getDivineAPIKey(): String
    private  val getDivineApiKey = String(Base64.decode(getDivineAPIKey(), Base64.DEFAULT))

    private external fun getDivineAPIAccessToken(): String
    private  val getDivineApiAccessToken = String(Base64.decode(getDivineAPIAccessToken(), Base64.DEFAULT))
    private external fun getAgoraChatAppKey(): String
    private  val getAgoraChatAppKey = String(Base64.decode(getAgoraChatAppKey(), Base64.DEFAULT))


    @Inject
    lateinit var lifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)
//        CallNotifications.createChannels(this)
        initStorePref()
        generateDeviceToken()
        initPlaces()

        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)

    }

    private fun initStorePref() {
        StorePreferences.initPreferences(this.applicationContext)
        StorePreferences.saveDivineApiKey(getDivineApiKey)
        StorePreferences.saveDivineApiAccessToken(getDivineApiAccessToken)
        StorePreferences.saveAgoraChatAppKey(getAgoraChatAppKey)
    }

    private fun generateDeviceToken() {
        Log.e(TAG, "Companion App -> generateDeviceToken: ")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // This is the line that can fail
                val token = FirebaseMessaging.getInstance().token.await()

                StorePreferences.saveDeviceToken(token)

                Log.d("CompanionApp", "FCM Token generated successfully: $token")
                // TODO: Here you would save the token to your server or preferences

            } catch (e: Exception) {
                // Catch any exception during token retrieval

                // This block now executes instead of crashing the app.
                Log.e("CompanionApp", "Failed to get FCM token", e)

                // Specifically check for the IOException that causes the crash
                if (e is IOException) {
                    Log.w("CompanionApp", "Could not get FCM token, most likely due to network issues or unavailable Google Play Services. Will retry later.")
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext, // Use applicationContext for safety
                            "Please check internet connection and restart the app",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // You can decide if you want to schedule a retry logic here using WorkManager
            }
        }
//        if(isInternetConnection()) {
//            FirebaseMessaging.getInstance().token.addOnCompleteListener {
//                Log.e(
//                    TAG,
//                    "Application -> generateDeviceToken: isSuccessful -> ${it.result.toString()}"
//                )
//                if (it.isSuccessful) {
//                    Log.e(
//                        TAG,
//                        "In Application generateDeviceToken function in success of token -> ${it.result}"
//                    )
//                    StorePreferences.saveDeviceToken(it.result)
//                }
//            }
//        }
    }

    private fun initPlaces() {
        Places.initialize(this, PlacesApiKey)
        Places.createClient(this)
    }

    companion object {
        lateinit var appContext: Context
        lateinit var firebaseAnalytics: FirebaseAnalytics
    }
}