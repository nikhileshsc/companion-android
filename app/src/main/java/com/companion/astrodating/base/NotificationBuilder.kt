package com.companion.astrodating.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.companion.astrodating.R
import com.companion.astrodating.ui.home.ui.HomePageActivity
import com.companion.astrodating.ui.interests.ui.InterestsFragment
import com.companion.astrodating.ui.profile.ui.ProfileFragment
import com.companion.astrodating.util.NotificationTypeConstants
import com.companion.astrodating.util.TAG

class NotificationBuilder(
    private val context: Context,
    private val notificationData: NotificationData
) {

    private val vibrationPattern = longArrayOf(1000, 1000)
    private lateinit var notification: Notification

    init {
        Log.e(TAG, "init block - NotificationBuilder: $notificationData")
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
        Log.e(TAG, "build Notification - NotificationBuilder: $notificationData")
        val intent : Intent
        when (notificationData.type) {
            NotificationTypeConstants.receivedInterest -> {
                val targetFragment = InterestsFragment::class.java.name
                intent = Intent(context, HomePageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("targetFragment", targetFragment)
                    putExtra(NotificationTypeConstants.type, NotificationTypeConstants.receivedInterest)
                }
            }

            NotificationTypeConstants.declineInterest -> {
                val targetFragment = InterestsFragment::class.java.name
                intent = Intent(context, HomePageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("targetFragment", targetFragment)
                    putExtra(NotificationTypeConstants.type, NotificationTypeConstants.declineInterest)
                }

            }

            NotificationTypeConstants.photoApproved,
            NotificationTypeConstants.photoRejected -> {
                val targetFragment = ProfileFragment::class.java.name
                intent = Intent(context, HomePageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("targetFragment", targetFragment)
                    putExtra(NotificationTypeConstants.type, notificationData.type)
                }
            }
            NotificationTypeConstants.deleteMyAccount -> {
                val targetFragment = NotificationTypeConstants.deleteMyAccount
                intent = Intent(context, HomePageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("targetFragment", targetFragment)
                    putExtra("body", notificationData.body)
                    putExtra(NotificationTypeConstants.type, notificationData.type)
                }
            }

            else -> {
                intent = Intent(context, HomePageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(NotificationTypeConstants.type, notificationData.type)
                }
            }
        }
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
            }
        notification = NotificationCompat.Builder(context, NOTIFICATION_CHANEL_ID)
            .setSmallIcon(R.drawable.companion_logo)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.body)
            .setContentIntent(pendingIntent)
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