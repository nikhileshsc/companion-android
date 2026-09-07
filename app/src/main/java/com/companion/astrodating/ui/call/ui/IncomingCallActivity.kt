package com.companion.astrodating.ui.call.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.base.CallRingingService
import com.companion.astrodating.base.FullScreenNotifier
import com.companion.astrodating.databinding.ActivityIncomingCallBinding
import com.companion.astrodating.ui.call.ui.VideoCallActivity.Companion.EXTRA_FLOW
//import com.companion.astrodating.util.ACTION_STOP_INCOMING_CALL_ACTIVITY
//import com.example.calls.databinding.ActivityIncomingCallBinding
//import com.example.calls.util.FullScreenNotifier
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomingCallActivity : BaseActivity() {

    private lateinit var vb: ActivityIncomingCallBinding
    private lateinit var callId: String
    private lateinit var channelName: String
    private lateinit var token: String
    private lateinit var callerId: String
    private lateinit var callType: String

    //    private lateinit var uid: String
    private var ttl: Long = 120
    private lateinit var callerPhoto: String

    private val stopActivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ACTION_STOP_INCOMING_CALL_ACTIVITY") {
                finish()
            }
        }
    }


    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        vb = ActivityIncomingCallBinding.inflate(layoutInflater)
//        setContentView(vb.root)
//
//        // Keep screen on & show over lock screen (set in Manifest too)
//        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
//
//        callId = intent.getStringExtra("callId") ?: ""
//        channelName = intent.getStringExtra("channelName") ?: ""
//        callerId = intent.getStringExtra("callerId") ?: ""
//        ttl = intent.getLongExtra("ttl", 120)
//
//        vb.callerName.text = "Call from $callerId"
//
//        vb.btnAccept.setOnClickListener {
//            FullScreenNotifier.cancelIncoming(this)
//            // Navigate to VideoCallActivity; token fetch happens there or via ViewModel
//            startActivity(Intent(this, VideoCallActivity::class.java).apply {
//                putExtra("callId", callId)
//                putExtra("channelName", channelName)
//            })
//            finish()
//        }
//
//        vb.btnDecline.setOnClickListener {
//            FullScreenNotifier.cancelIncoming(this)
//            // Hit /calls/:id/end in background if needed
//            finish()
//        }
//    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the broadcast receiver
        val filter = IntentFilter("ACTION_STOP_INCOMING_CALL_ACTIVITY")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stopActivityReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(stopActivityReceiver, filter)
        }

        // For Android 8.1+ (API 27+) nice way:
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
//        val pm = getSystemService(PowerManager::class.java)
//        @Suppress("DEPRECATION")
//        val wl = pm?.newWakeLock(
//            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
//            "yourapp:incoming_call_wakeup"
//        )
//        wl?.acquire(2500)
//        wl?.release()

            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val km = getSystemService(android.app.KeyguardManager::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) km?.requestDismissKeyguard(
                this,
                null
            ) // optional: dismiss keyguard UI
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }


        vb = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(vb.root)
//    super.onCreate(savedInstanceState)
//    vb.btnDecline.setTextColor(Color.WHITE)
//    vb.btnAccept.setTextColor(Color.WHITE)
        vb.callerName.text = ""

        // --- Critical: allow activity over lockscreen ---
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//        setShowWhenLocked(true)
//        setTurnScreenOn(true)
//    } else {
//        @Suppress("DEPRECATION")
//        window.addFlags(
//            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
//                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
//                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//        )
//    }

        callId = intent.getStringExtra("callId") ?: ""
        channelName = intent.getStringExtra("channel") ?: ""
        token = intent.getStringExtra("token") ?: ""
        callerId = intent.getStringExtra("callerId") ?: ""
        callType = intent.getStringExtra("callType") ?: ""
//    uid = intent.getStringExtra("userId") ?: ""
        ttl = intent.getLongExtra("ttl", 120)
        callerPhoto = intent.getStringExtra("callerPhoto").orEmpty()

        vb.callerName.text = "Call from $callerId"
        vb.callType.text = "${callType} Call"


        Glide.with(vb.callerImage.context).load(callerPhoto)
            .error(R.drawable.ic_default_profile).circleCrop().into(vb.callerImage)

        vb.btnAccept.setOnClickListener {
            FullScreenNotifier.cancelIncoming(this)
            this.stopService(Intent(this, CallRingingService::class.java))
            val km = getSystemService(KeyguardManager::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                km?.requestDismissKeyguard(this, null)
            }
            if (callType == "Video") {
                //launch video call
                launchVideoCallAndFinish()
//            startActivity(Intent(this, VideoCallActivity::class.java).apply {
//                putExtra("callId", callId)
//                putExtra("channel", channelName)
//                putExtra("token", token)
////            putExtra("userId", uid)
//                putExtra("flow", "in")
//                putExtra("calleeName", callerId)
//            })
//            finish()
            } else {
                //launch voice call
                launchVoiceCallAndFinish()
//                startActivity(Intent(this, VoiceCallActivity::class.java).apply {
//                    putExtra("callId", callId)
//                    putExtra("channel", channelName)
//                    putExtra("token", token)
//                    putExtra("flow", "in")
//                    putExtra("callerPhoto", callerPhoto)
//                    putExtra("calleeName", callerId)
//                })
//                finish()
            }
        }

        vb.btnDecline.setOnClickListener {
            FullScreenNotifier.cancelIncoming(this)
            this.stopService(Intent(this, CallRingingService::class.java))
            finish()
        }
    }

    override fun initObservers() {
        //
    }

    override fun onStop() {
        super.onStop()
        // prevent Activity lingering on back stack
        if (isFinishing.not()) finish()
    }

    private fun launchVideoCallAndFinish() {
        startActivity(Intent(this, VideoCallActivity::class.java).apply {
            putExtra("callId", callId)
            putExtra("channel", channelName)
            putExtra("token", token)
//            putExtra("userId", uid)
            putExtra("flow", "in")
            putExtra("calleeName", callerId)
        })
        finish()
    }

    private fun launchVoiceCallAndFinish() {
        startActivity(Intent(this, VoiceCallActivity::class.java).apply {
            putExtra("callId", callId)
            putExtra("channel", channelName)
            putExtra("token", token)
            putExtra("flow", "in")
            putExtra("callerPhoto", callerPhoto)
            putExtra("calleeName", callerId)
        })
        finish()
    }
}
