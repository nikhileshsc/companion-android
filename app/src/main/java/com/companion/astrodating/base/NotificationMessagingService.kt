package com.companion.astrodating.base

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.companion.astrodating.R
import com.companion.astrodating.ui.call.ui.IncomingCallActivity
import com.companion.astrodating.util.NotificationTypeConstants
import com.companion.astrodating.util.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import io.agora.chat.ChatClient


class NotificationMessagingService : FirebaseMessagingService(){

    private lateinit var notificationManager: NotificationManager
    private var notificationBuilder: NotificationBuilder?=null
    private var agoraMessageNotificationBuilder: AgoraMessageNotificationBuilder?=null
    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val type = data["type"] ?: return

        Log.d("CALLFLOW","CALL TYPE: $type")

        if(type == "incoming_call"){

            Log.d(TAG,"Type is ${type}!")

            val callId = data["callId"].orEmpty()
            val channelName = data["channelName"].orEmpty()
            val token = data["rtcToken"].orEmpty()
            val callerId = data["callerId"].orEmpty()
            val callType = data["callType"].orEmpty()
            val ttl = data["ttlSeconds"]?.toLongOrNull() ?: 120L
            val callerPhoto = data["callerPhoto"].orEmpty()
//            val userId = data["userId"]?.toIntOrNull()

                val i = Intent(this, CallRingingService::class.java).apply {
                    putExtra("callId", callId)
                    putExtra("channel", channelName)
                    putExtra("token", token)
                    putExtra("callerId", callerId)
                    putExtra("callType", callType)
                    putExtra("title", data["title"] ?: "Incoming Call")
                    putExtra("body", data["body"] ?: "Call from $callerId")
                    putExtra("callerPhoto", callerPhoto)
                }
                startForegroundService(i)
            return
        }

        if (type == "call_state") {
            val status = data["status"].orEmpty() // "accepted" | "declined" | "missed" | "canceled" | "ended"
            val callId = data["callId"].orEmpty()

            // Forward to app UI
            val i = Intent("com.companion.astrodating.ACTION_CALL_STATE")
                .putExtra("status", status)
                .putExtra("callId", callId)
            sendBroadcast(i)

            if (status == "canceled" || status == "missed" ){//|| status == "ended") {
                stopService(Intent(applicationContext, CallRingingService::class.java))
                val stopIntent = Intent("ACTION_STOP_INCOMING_CALL_ACTIVITY").apply {
                }
                sendBroadcast(stopIntent)
            }
            return
        }

        val message = remoteMessage.data["alert"]
        Log.d(TAG, "onMessageReceivedy: $message")
        Log.d("onMessageReceivedy", "onMessageReceivedy: $message")

        Log.e(TAG, "CompanionNotification Service -> onMessageReceived: ${remoteMessage.data}")
        notificationData = Gson().fromJson(Gson().toJson(remoteMessage.data), NotificationData::class.java)

        notificationBuilder = NotificationBuilder(applicationContext,notificationData)
//        if (notificationData.type == "received-interest") {
            sentNotification()
//        }

        // In addition to the system-tray notification above, also surface an
        // in-app pop-up + bottom-nav badge for interest events when the app is
        // in the foreground.
        if (notificationData.type == NotificationTypeConstants.receivedInterest ||
            notificationData.type == NotificationTypeConstants.declineInterest
        ) {
            if (notificationData.type == NotificationTypeConstants.receivedInterest) {
                InAppEventBus.incrementInterestBadge()
            }
            InAppEventBus.postAlert(
                InAppAlertEvent(
                    type = notificationData.type,
                    title = notificationData.title,
                    body = notificationData.body
                )
            )
        }
//        if (remoteMessage.data.size > 0) {
//            val f = remoteMessage.data["f"]
//            val t = remoteMessage.data["t"]
//            val m = remoteMessage.data["m"]
//            val g = remoteMessage.data["g"]
//            val e: Any? = remoteMessage.data["e"]
//
//            Log.e(TAG, "from - $f,to-$t, msg-$m, group-$g, e-$e")
//            ChatClient.getInstance().pushManager()
//                .setPushTemplate("default", object : CallBack {
//                    override fun onSuccess() {}
//                    override fun onError(code: Int, error: String) {}
//                })
//            agoraMessageNotificationBuilder = AgoraMessageNotificationBuilder(applicationContext,remoteMessage)
//            sendMessageNotification()
//        }
    }

    private fun sendMessageNotification() {
        val NOTIFICATION_ID =112
        notificationManager.notify(
            NOTIFICATION_ID,
            agoraMessageNotificationBuilder!!.build()
        )
    }

    override fun handleIntent(@NonNull intent: Intent) {
        super.handleIntent(intent)
        val bundle = intent.extras
        if (bundle != null) {
            val map: MutableMap<String, Any?> = HashMap()
            for (key in bundle.keySet()) {
                if (!TextUtils.isEmpty(key)) {
                    val content = bundle[key]
                    map[key] = content
                }
            }
            Log.i(TAG, "handleIntent: $map")
        }
    }

    private fun sentNotification() {
        val NOTIFICATION_ID =111
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder!!.build()
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if(ChatClient.getInstance().isSdkInited) {
            ChatClient.getInstance().sendFCMTokenToServer(token)
        }

    }

    override fun onDestroy() {
        // unregisterReceiver(hangUpReceiver)
        super.onDestroy()
    }

    companion object{
        lateinit var notificationData: NotificationData
    }

}
