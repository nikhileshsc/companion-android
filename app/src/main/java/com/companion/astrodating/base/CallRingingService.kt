package com.companion.astrodating.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.base.NotificationMessagingService.Companion.notificationData
import com.companion.astrodating.ui.call.ui.IncomingCallActivity
import com.companion.astrodating.util.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CallRingingService : Service() {
    private var timeoutJob: Job? = null

    // Ring/Vibrate state
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    // top of CallRingingService
    private var mediaPlayer: android.media.MediaPlayer? = null
    private var audioManager: android.media.AudioManager? = null
    private var audioFocusRequested = false

    private var notificationBuilder: NotificationBuilder?=null
    private lateinit var notificationManager: NotificationManager

    var callerId = ""


    override fun onCreate() {
        super.onCreate()
        Log.d("CALLFLOW", "RingingService.onCreate()")
        CallNotifications.createChannels(this)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        audioManager = getSystemService(AUDIO_SERVICE) as android.media.AudioManager

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.keySet()?.forEach { key ->
            Log.d("RingingService", "extra[$key] = ${intent.extras?.get(key)}")
        }
        Log.d("CALLFLOW", "RingingService.onStartCommand() extras=${intent?.extras}")

        // ---- Read extras ----
        val callId = intent?.getStringExtra("callId").orEmpty()
        val channelName = intent?.getStringExtra("channel").orEmpty()
        val token = intent?.getStringExtra("token").orEmpty()
        callerId = intent?.getStringExtra("callerId").orEmpty() // class-level var in your service
        val title = intent?.getStringExtra("title") ?: "Incoming Call"
        val callType = intent?.getStringExtra("callType") ?: ""
        val body = intent?.getStringExtra("body") ?: "Call from $callerId"
        val callerImageUrl = intent?.getStringExtra("callerPhoto").orEmpty()

        val safeName = callerId.takeIf { it.isNotBlank() } ?: "Unknown caller"
        Log.d(TAG, "CALLER ID: $callerId (safeName=$safeName)")


        // ---- PendingIntents ----
        val fullScreenPI = PendingIntent.getActivity(
            this, 0,
            Intent(this, IncomingCallActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("callId", callId)
                putExtra("channel", channelName)
                putExtra("callType", callType)
                putExtra("token", token)
                putExtra("callerId", callerId)
                putExtra("callerPhoto", callerImageUrl)

            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val acceptPI = PendingIntent.getBroadcast(
            this, 1,
            Intent(this, CallActionReceiver::class.java)
                .setAction("com.companion.astrodating.ACTION_ACCEPT")
                .putExtra("callId", callId)
                .putExtra("callType", callType)
                .putExtra("channelName", channelName)
                .putExtra("token", token)
                .putExtra("callerId", callerId)
                .putExtra("callerPhoto", callerImageUrl),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val declinePI = PendingIntent.getBroadcast(
            this, 2,
            Intent(this, CallActionReceiver::class.java)
                .setAction("com.companion.astrodating.ACTION_DECLINE")
                .putExtra("callId", callId)
                .putExtra("callerId", callerId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Ensure channel exists
        CallNotifications.createChannels(this)

        // ---- Build initial person with placeholder icon ----
        val placeholderBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_avatar_placeholder)
        // replace both lines that create the placeholder image:
        val placeholderIcon = IconCompat.createWithResource(this, R.drawable.ic_avatar_placeholder)

        val person = Person.Builder()
            .setName(safeName)
            .setKey(safeName)
            .setIcon(placeholderIcon)   // <- no NPE anymore
            .build()


        // ---- Foreground ringing notification (full-screen intent) ----
        val notifBuilder = NotificationCompat.Builder(this, CallNotifications.CHANNEL_CALLS)
            .setSmallIcon(R.drawable.ic_call)
            .setContentTitle(title)
            .setContentText(body)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPI, true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Native call style shows the Person avatar
            notifBuilder.setStyle(
                NotificationCompat.CallStyle.forIncomingCall(person, declinePI, acceptPI)
            )
        } else {
            // Legacy: show a large icon + explicit actions
            notifBuilder
                .setLargeIcon(placeholderBitmap)
                .addAction(R.drawable.ic_decline, "Decline", declinePI)
                .addAction(R.drawable.ic_call, "Accept", acceptPI)
        }

        val notifId = CallNotifications.NOTIF_ID_RINGING
        Log.d("CALLFLOW", "Posting foreground notif with fullScreenIntent")
        startForeground(notifId, notifBuilder.build())

        // ---- Start ringtone + vibration immediately ----
        startRinging()

        // ---- Timeout (auto-stop/missed) ----
        timeoutJob?.cancel()
        timeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30_000) // use 30s (or your preferred window)
            // onMissedCall(callerId, callId) // if you want to post a missed notification here
            stopSelf()
        }

        // ---- Load caller image on background thread and update notification ----
        val nm = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        CoroutineScope(Dispatchers.IO).launch {
            val bmp = try {
                if (callerImageUrl.isBlank()) null else
                    Glide.with(applicationContext)
                        .asBitmap()
                        .load(callerImageUrl)
                        .circleCrop()
                        .submit(128, 128)
                        .get()
            } catch (t: Throwable) {
                Log.d("CALLFLOW", "Failed to load caller image (IO): ${t.message}")
                null
            }

            if (bmp != null) {
                val personWithPhoto = Person.Builder()
                    .setName(safeName)
                    .setKey(safeName)
                    .setIcon(IconCompat.createWithBitmap(bmp))
                    .build()

                val updated = NotificationCompat.Builder(applicationContext, CallNotifications.CHANNEL_CALLS)
                    .setSmallIcon(R.drawable.ic_call)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setFullScreenIntent(fullScreenPI, true)
                    .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                    .apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            setStyle(NotificationCompat.CallStyle.forIncomingCall(personWithPhoto, declinePI, acceptPI))
                        } else {
                            setLargeIcon(bmp)
                            addAction(R.drawable.ic_decline, "Decline", declinePI)
                            addAction(R.drawable.ic_call, "Accept", acceptPI)
                        }
                    }
                    .build()

                // Update the existing notification with the avatar
                nm.notify(notifId, updated)
            }
        }

        return START_NOT_STICKY
    }


//    @RequiresPermission(Manifest.permission.VIBRATE)
//    private fun startRinging() {
//        try {
//            if (ringtone?.isPlaying == true) return
//            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
//            ringtone = RingtoneManager.getRingtone(applicationContext, uri)
//            ringtone?.play()
//        } catch (t: Throwable) {
//            Log.w("CALLFLOW", "Failed to play ringtone: ${t.message}")
//        }
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val effect = VibrationEffect.createWaveform(longArrayOf(0, 600, 800), 0)
//                vibrator?.vibrate(effect)
//            } else {
//                @Suppress("DEPRECATION")
//                vibrator?.vibrate(longArrayOf(0, 600, 800), 0)
//            }
//        } catch (t: Throwable) {
//            Log.w("CALLFLOW", "Failed to vibrate: ${t.message}")
//        }
//    }
//
//    @RequiresPermission(Manifest.permission.VIBRATE)
//    private fun stopRinging() {
//        try {
//            ringtone?.let { r -> if (r.isPlaying) r.stop() }
//            ringtone = null
//        } catch (_: Throwable) {}
//        try {
//            vibrator?.cancel()
//        } catch (_: Throwable) {}
//    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun startRinging() {
        // 1) Request audio focus for ringtone
        val focusReq = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val attrs = android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val afReq = android.media.AudioFocusRequest.Builder(android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(attrs)
                .setOnAudioFocusChangeListener { /* no-op */ }
                .build()
            audioFocusRequested = audioManager?.requestAudioFocus(afReq) == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            true
        } else {
            @Suppress("DEPRECATION")
            run {
                audioFocusRequested = (audioManager?.requestAudioFocus(
                    null,
                    android.media.AudioManager.STREAM_RING,
                    android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                ) == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            }
            false
        }

        // 2) Build a looping MediaPlayer for system ringtone URI
        val uri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_RINGTONE)
        stopToneInternal() // in case it was already running

        mediaPlayer = android.media.MediaPlayer().apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            } else {
                @Suppress("DEPRECATION")
                setAudioStreamType(android.media.AudioManager.STREAM_RING)
            }
            setDataSource(applicationContext, uri)
            isLooping = true
            setOnPreparedListener { start() }
            setOnErrorListener { _, _, _ -> stopToneInternal(); true }
            prepareAsync()
        }

        // 3) Vibrate in a repeating pattern (you already do this)
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val effect = android.os.VibrationEffect.createWaveform(longArrayOf(0, 600, 800), 0)
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 600, 800), 0)
            }
        } catch (_: Throwable) { /* ignore */ }
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun stopRinging() {
        stopToneInternal()
        try { vibrator?.cancel() } catch (_: Throwable) {}
    }

    private fun stopToneInternal() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.reset()
                mp.release()
            }
        } catch (_: Throwable) { /* ignore */ }
        mediaPlayer = null

        if (audioFocusRequested) {
            try { audioManager?.abandonAudioFocus(null) } catch (_: Throwable) {}
            audioFocusRequested = false
        }
    }


    private fun onMissedCall(callerId: String, callId: String?) {
        // Broadcast so UI can show a Missed Call notification/log entry
        try {
//            val missedIntent = Intent("com.companion.astrodating.ACTION_MISSED_CALL")
//                .putExtra("callerId", callerId)
//                .putExtra("callId", callId)
//            sendBroadcast(missedIntent)

            //
            notificationData = NotificationData(
                "You missed a call from ${callerId}",
                "Missed Call",
                "Companion")
            notificationBuilder = NotificationBuilder(applicationContext, notificationData)
            sentNotification()

//            val openAppPI = PendingIntent.getActivity(
//                this, 0,
//                Intent(this, IncomingCallActivity::class.java).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                },
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//            val notif = NotificationCompat.Builder(this, CallNotifications.CHANNEL_CALLS)
//                .setSmallIcon(R.drawable.ic_call)
//                .setContentTitle("Missed call")
//                .setContentText("From $callerId")
////                .setAutoCancel(true)
//                .setContentIntent(openAppPI)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .build()
//
//            notificationManager.notify(1001, notif) // stable id for missed calls

            // (Optional) also tell backend this side timed out -> caller gets push
            // callApi.missed(callId)

        } catch (t: Throwable) {
            Log.w("CALLFLOW", "Failed to broadcast missed call: ${t.message}")
        }
    }

    private fun sentNotification() {
        Log.e(TAG, "Notification: NotificationService")
        val NOTIFICATION_ID =111
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder!!.build()
        )
    }

    override fun onBind(intent: Intent?) = null

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun onDestroy() {
        timeoutJob?.cancel()
        stopRinging()
        onMissedCall(callerId, null)

        super.onDestroy()
    }
}
