package com.companion.astrodating.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build

object CallNotifications {
    const val CHANNEL_CALLS = "calls_v4"  // NEW ID
    const val NOTIF_ID_RINGING = 1001

    fun createChannels(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = ctx.getSystemService(NotificationManager::class.java)
            val existing = nm.getNotificationChannel(CHANNEL_CALLS)
            if (existing == null) {
                val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                val attrs = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val ch = NotificationChannel(
                    CHANNEL_CALLS,
                    "Calls",
                    NotificationManager.IMPORTANCE_HIGH // critical for heads-up/FSI
                ).apply {
                    description = "Incoming & ongoing calls"
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    setSound(null, null)         // make it ring
                    enableVibration(false)
                    vibrationPattern = longArrayOf(0, 400, 250, 400)
                    // Helps on some devices (user can still override in Settings):
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setBypassDnd(true)
                    }
                }
                nm.createNotificationChannel(ch)
            }
        }
    }
}