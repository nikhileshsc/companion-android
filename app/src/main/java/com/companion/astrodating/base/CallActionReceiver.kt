package com.companion.astrodating.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.companion.astrodating.ui.call.ui.IncomingCallActivity
import com.companion.astrodating.ui.call.ui.VideoCallActivity
import com.companion.astrodating.ui.call.ui.VoiceCallActivity

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val callId      = intent.getStringExtra("callId").orEmpty()
        val callType    = intent.getStringExtra("callType").orEmpty()
        val channelName = intent.getStringExtra("channelName").orEmpty()
        val token       = intent.getStringExtra("token").orEmpty()
        val callerId    = intent.getStringExtra("callerId").orEmpty()
        val callerPhoto = intent.getStringExtra("callerPhoto").orEmpty()

        Log.d("CALLFLOW", "Receiver action=$action callId=$callId")

        when (action) {
            // ✅ match what the Service sends
            "com.companion.astrodating.ACTION_ACCEPT" -> {
                // Stop ringing service & clear notif
                context.stopService(Intent(context, CallRingingService::class.java))

                // Launch your incoming call screen with ALL needed extras
//                val i = Intent(context, IncomingCallActivity::class.java).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    putExtra("callId", callId)
//                    putExtra("channel", channelName)   // your activity expects "channel"
//                    putExtra("callType", callType)
//                    putExtra("token", token)
//                    putExtra("callerId", callerId)
//                    putExtra("callerPhoto", callerPhoto)
//                }
                if(callType=="Voice"){
                    val i = Intent(context, VoiceCallActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra("callId", callId)
                        putExtra("channel", channelName)
                        putExtra("token", token)
                        putExtra("flow", "in")
                        putExtra("callerPhoto", callerPhoto)
                        putExtra("calleeName", callerId)
                    }
                    startActivity(context, i, null)
                    //                context.startActivity(i)
                }else{
                    val i = Intent(context, VideoCallActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra("callId", callId)
                        putExtra("channel", channelName)
                        putExtra("token", token)
                        putExtra("flow", "in")
//                        putExtra("callerPhoto", callerPhoto)
                        putExtra("calleeName", callerId)
                    }
                    startActivity(context, i, null)
                }

            }

            "com.companion.astrodating.ACTION_DECLINE" -> {
                // Stop ringing + clear notif; optionally call backend to end the call
                context.stopService(Intent(context, CallRingingService::class.java))
                // (Optional) enqueue a WorkManager task to notify server call declined
            }
        }
    }

}
