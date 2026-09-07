package com.companion.astrodating.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.companion.astrodating.R
import com.companion.astrodating.util.TAG
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.firebase.messaging.RemoteMessage
import io.agora.chat.ChatClient
import io.agora.chat.ChatOptions
import io.agora.push.PushConfig
import io.agora.push.PushHelper
import io.agora.push.PushListener
import io.agora.push.PushType

class AgoraMessageNotificationBuilder(
    private val context: Context,
    private val remoteMessage: RemoteMessage
) {

    private val vibrationPattern = longArrayOf(1000, 1000)
    private lateinit var notification: Notification

    init {
        Log.e(TAG, "Agora init block - NotificationBuilder: $remoteMessage")
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANEL_ID,
                NOTIFICATION_CHANNEL_NAME_REMEDY,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun build(): Notification {
        /* val intent : Intent

         val pendingIntent: PendingIntent? =
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                 Log.e(TAG, "build fun = buildVersion > S")
                 PendingIntent.getActivity(
                     context,
                     NOTIFICATION_ID,
                     intent,
                     PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                 )
             } else {
                 PendingIntent.getActivity(
                     context,
                     NOTIFICATION_ID,
                     intent,
                     PendingIntent.FLAG_UPDATE_CURRENT
                 )
             }*/
        val options = ChatOptions()
        val builder = PushConfig.Builder(context)
        // Replace with your FCM Sender ID.
        builder.enableFCM("475586254726");
        // Configure push settings in the ChatOptions class.
        options.pushConfig = builder.build()
        // Initialize the IM SDK.
        ChatClient.getInstance().init(context, options)
        // Set up push monitoring.
        PushHelper.getInstance().setPushListener(object : PushListener() {
            override fun isSupportPush(pushType: PushType?, pushConfig: PushConfig?): Boolean {
                if (pushType == PushType.FCM) {
                    return GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(
                        CompanionApplication.appContext
                    ) == ConnectionResult.SUCCESS
                }
                return super.isSupportPush(pushType, pushConfig)
            }

            override fun onBindTokenSuccess(p0: PushType?, p1: String?) {
                Log.e(TAG, "onBindTokenSuccess: $p0 - $p1")

            }

            override fun onError(p0: PushType?, p1: Long) {
                Log.e(TAG, "Push client occur a error: $p0 - $p1")
            }
        });
        notification = NotificationCompat.Builder(context, NOTIFICATION_CHANEL_ID)
            .setSmallIcon(R.drawable.companion_logo)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle(remoteMessage.from)
            .setContentText(remoteMessage.data["m"])
//            .setContentIntent(pendingIntent)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .build()
        notification.flags = notification.flags
        return notification


    }

    companion object {
        private const val INCOMING_CALL_HANG_UP_REQUEST_ID = 1100
        private const val NOTIFICATION_CHANEL_ID = "GENERAL_NOTIFICATION"
        private const val NOTIFICATION_ID = 111
        private const val NOTIFICATION_CHANNEL_NAME_REMEDY = "Notification"
        const val NOTIFICATION_DATA = "notificationData"
    }

}