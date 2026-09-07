package com.companion.astrodating.base
//
//import android.app.Application
//import android.content.Context
//import android.os.Looper
//import android.os.Handler
//import com.companion.astrodating.ui.home.viewmodel.HomeUserViewModel
//import com.companion.astrodating.util.StorePreferences
//import java.util.Date
//
//object OnlinePinger {
//    private var handler: Handler? = null
//    private var runnable: Runnable? = null
//    private const val intervalMillis = 70 * 1000L // 70 seconds
//
//    fun startPinging(viewModel: HomeUserViewModel) {
//        stopPinging() // Avoid duplicate calls
//
//        handler = Handler(Looper.getMainLooper())
//        runnable = object : Runnable {
//            override fun run() {
//                val token = getAuthToken()
//                if (token != null) {
//                    callOnlineAPI(viewModel)
//                    handler?.postDelayed(this, intervalMillis)
//                } else {
//                    println("⛔ Token not available. Skipping API call.")
//                }
//            }
//        }
//        handler?.post(runnable!!)
//    }
//
//    fun stopPinging() {
//        handler?.removeCallbacks(runnable!!)
//        handler = null
//        runnable = null
//    }
//
//    private fun getAuthToken(): String? {
//        return StorePreferences.getAuthToken()
//    }
//
//    private fun callOnlineAPI(viewModel: HomeUserViewModel) {
//        viewModel.updateOnlineStatus(true)
//        // Replace with your actual API call (Retrofit, Ktor, etc.)
//        println("✅ Calling API with token at ${Date()}")
//    }
//}
