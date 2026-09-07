package com.companion.astrodating.base

//import android.app.AlertDialog
//import android.app.NotificationManager
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.provider.Settings
//import androidx.core.app.NotificationManagerCompat
//
//object NotificationPermissionHelper {
//
//    fun checkAndPrompt(context: Context) {
//        val nm = NotificationManagerCompat.from(context)
//
//        // 1. If completely disabled
//        if (nm.areNotificationsEnabled()) {
//            showDialog(context, isChannel = false)
//            return
//        }
//
//        // 2. If channel importance is too low
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val sysNM = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            val ch = sysNM.getNotificationChannel(CallNotifications.CHANNEL_CALLS)
//            if (ch == null || ch.importance < NotificationManager.IMPORTANCE_HIGH) {
//                showDialog(context, isChannel = true)
//            }
//        }
//    }
//
//    private fun showDialog(context: Context, isChannel: Boolean) {
//        AlertDialog.Builder(context)
//            .setTitle("Enable Call Notifications")
//            .setMessage("To receive incoming calls, please enable 'Show on lock screen' and 'Pop-ups' for notifications in settings.")
//            .setPositiveButton("Go to Settings") { _, _ ->
//                openSettings(context, isChannel)
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    private fun openSettings(context: Context, isChannel: Boolean) {
//        try {
//            val intent = if (Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)) {
//                val intent = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
//                    setClassName("com.miui.securitycenter",
//                        "com.miui.permcenter.permissions.PermissionsEditorActivity")
//                    putExtra("extra_pkgname", context.packageName)
//                }
//                runCatching { context.startActivity(intent) }
//                return
//            }
//            else {
//                if (isChannel && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
//                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//                    putExtra(Settings.EXTRA_CHANNEL_ID, CallNotifications.CHANNEL_CALLS)
//                }
//            } else {
//                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
//                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//                }
//            }
//            }
//            context.startActivity(intent)
//        } catch (e: Exception) {
//            // fallback: open app details
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                data = Uri.parse("package:${context.packageName}")
//            }
//            context.startActivity(intent)
//        }
//    }
//}

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat

object CallNotifHealth {

    data class Status(
        val notificationsEnabled: Boolean,
        val channelExists: Boolean,
        val channelImportanceHigh: Boolean,
        val channelVisibleOnLockscreen: Boolean
    )

    fun status(context: Context, channelId: String): Status {
        val nmCompat = NotificationManagerCompat.from(context)
        var exists = false
        var high = false
        var visible = true // assume OK until we can read it

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = nm.getNotificationChannel(channelId)
            if (ch != null) {
                exists = true
                high = ch.importance >= NotificationManager.IMPORTANCE_HIGH
                // VISIBILITY_SECRET means “don’t show on lockscreen”
                visible = ch.lockscreenVisibility != Notification.VISIBILITY_SECRET
            }
        } else {
            // Pre-O, importance is per-notification; we can only check app-level enablement
            exists = true
            high = true
            visible = true
        }

        return Status(
            notificationsEnabled = nmCompat.areNotificationsEnabled(),
            channelExists = exists,
            channelImportanceHigh = high,
            channelVisibleOnLockscreen = visible
        )
    }

    fun ensureAndPromptAppInfoIfNeeded(context: Context, channelId: String) {
        val s = status(context, channelId)
        val problem = when {
            !s.notificationsEnabled -> "Notifications are disabled for the app."
            !s.channelExists        -> "The Calls notification channel isn’t created yet."
            !s.channelImportanceHigh-> "The Calls channel importance is not High."
            !s.channelVisibleOnLockscreen -> "The Calls channel is hidden on the lock screen."
            else -> null
        }
        if (problem == null) return

        AlertDialog.Builder(context)
            .setTitle("Enable Call Notifications")
            .setMessage(
                "$problem\n\nPlease enable “Show on lock screen” and “Pop-ups” for calls " +
                        "in the app’s system settings so incoming calls can appear."
            )
            .setPositiveButton("Open App Settings") { _, _ ->
                openAppInfo(context)
            }
            .setNegativeButton("Not now", null)
            .show()
    }

    private fun openAppInfo(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

