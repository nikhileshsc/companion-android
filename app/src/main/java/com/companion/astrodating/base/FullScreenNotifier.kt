package com.companion.astrodating.base


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.companion.astrodating.R

object FullScreenNotifier {

    fun showIncomingCall(
        context: Context,
        title: String,
        text: String,
        fullScreenPI: PendingIntent,
        acceptPI: PendingIntent,
        declinePI: PendingIntent
    ) {
        val notif = NotificationCompat.Builder(context, CallNotifications.CHANNEL_CALLS)
            .setSmallIcon(R.drawable.ic_call)//ic_stat_name)
            .setContentTitle(title)
            .setContentText(text)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPI, true)
            .addAction(R.drawable.ic_call, "Accept", acceptPI)
            .addAction(R.drawable.ic_decline, "Decline", declinePI)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(CallNotifications.NOTIF_ID_RINGING, notif)
    }

    fun cancelIncoming(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(CallNotifications.NOTIF_ID_RINGING)
    }
}
