package com.companion.astrodating.ui.call.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityCallVoiceBinding
import com.companion.astrodating.ui.call.viewmodel.CallViewModel
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoiceCallActivity : BaseActivity() {

    private val vm: CallViewModel by viewModels()
    private val MyFullDetails = StorePreferences.getUserDetails()
    val fullName = MyFullDetails.fullName
    private val profilePhoto = MyFullDetails.profileUrl
    private val binding by lazy {
        ActivityCallVoiceBinding.inflate(layoutInflater)
    }

    private var rtcEngine: RtcEngine? = null

    private val myUidString = StorePreferences.getPrimaryUserAgoraUserName()
    private val myUidInt = myUidString?.toIntOrNull() ?: 0

    private var callTimeoutHandler: Handler? = null
    private var callTimeoutRunnable: Runnable? = null
    private val CALL_TIMEOUT_MS = 30_000L

    private var callStartTime: Long = 0L
    private var timerHandler: Handler? = null
    private var timerRunnable: Runnable? = null


    companion object {
        private const val PERMISSION_REQ_ID_RECORD_AUDIO = 301

        const val EXTRA_FLOW = "flow"          // "out" | "in"
        const val EXTRA_CALLEE_ID = "calleeId" // for outgoing push (optional)
        const val EXTRA_PRIMARY_ID = "primaryId"
        const val EXTRA_CHANNEL = "channel"    // incoming or preferred
        const val EXTRA_TOKEN = "rtcToken"     // optional on incoming
        const val EXTRA_CALLEE_NAME = "calleeName"
        const val EXTRA_CALLEE_PROFILE = "calleeProfile"
    }

    private val rtcHandler = object : IRtcEngineEventHandler() {
        override fun onError(err: Int) {
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            startCallTimeoutTimer()
        }

        override fun onConnectionStateChanged(state: Int, reason: Int) {
            super.onConnectionStateChanged(state, reason)
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            cancelCallTimeoutTimer()
            callStartTime = System.currentTimeMillis()
            startCallTimer()

            binding.root.post {
                binding.callTimerOrState.text = "00:00"
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            cancelCallTimer()
            cancelCallTimeoutTimer() // Also cancel timeout timer here
            binding.root.post {
                binding.callTimerOrState.text = "Disconnected"
            }
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                finish()
            }, 3000L)

            // Optional: finish() or update UI as needed
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.controlsVoice.btnMic.setColorFilter(resources.getColor(R.color.white))
        binding.peerName.text = intent.getStringExtra(EXTRA_CALLEE_NAME)

        val uri = intent.getStringExtra(EXTRA_CALLEE_PROFILE)
        Glide.with(binding.peerAvatar.context).load(uri).error(R.drawable.ic_default_profile)
            .circleCrop().into(binding.peerAvatar)

//        if (checkSelfPermission(
//                Manifest.permission.RECORD_AUDIO,
//                PERMISSION_REQ_ID_RECORD_AUDIO
//            )
//        ) {
//            initEngineAndJoin()
//        }
        ensurePermissionsThen(voicePerms) { initEngineAndJoin() }
    }

    override fun initObservers() {
        //
    }

    private fun initEngineAndJoin() {
        val audioManager = getSystemService(AUDIO_SERVICE) as android.media.AudioManager
        audioManager.mode = android.media.AudioManager.MODE_IN_COMMUNICATION

        initAgoraEngine()

        // Audio-only: ensure video is disabled
        rtcEngine?.enableAudio()
        rtcEngine?.disableVideo()

        // Be a broadcaster to publish mic
        rtcEngine?.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER)

        // Ensure we’re transmitting mic audio
        rtcEngine?.muteLocalAudioStream(false)
        // Optional: default to speakerphone for a call-style experience
//        rtcEngine?.setEnableSpeakerphone(true)

        val flow = intent.getStringExtra(EXTRA_FLOW) ?: "out"

        if (flow == "in") {
            // Receiver: channel/token usually supplied via push extras
            val channelFromPush = intent.getStringExtra(EXTRA_CHANNEL)

            lifecycleScope.launch {
                // Let backend/VM confirm/refresh token for our own uid on this channel
                val result = vm.startCallInternal(
                    isVideo = false,
                    calleeId = null,
                    userId = myUidString ?: "$myUidInt",
                    preferredChannel = channelFromPush,
                    fullName,
                    callerPhoto = ""
                )
                if (result != null) {
                    val (token, channel, userId) = result
                    joinChannel(token, channel, userId)

                    val uri = intent.getStringExtra("callerPhoto")

                    Glide.with(binding.peerAvatar.context).load(uri)
                        .error(R.drawable.ic_default_profile).circleCrop().into(binding.peerAvatar)
                } else {
                    showLongToast("Failed to accept voice call")
                    finish()
                }
            }
        } else {
            // Caller: we provide calleeId, VM creates channel & token
            lifecycleScope.launch {
                val calleeId = intent.getStringExtra(EXTRA_CALLEE_ID)
                val result = vm.startCallInternal(
                    isVideo = false,
                    calleeId = calleeId,
                    userId = myUidString ?: "$myUidInt",
                    preferredChannel = null,
                    fullName,
                    profilePhoto
                )
                if (result != null) {
                    val (token, channel, userId) = result
                    joinChannel(token, channel, userId)
                } else {
                    showLongToast("Failed to start voice call")
                    finish()
                }
            }
        }
    }

    private fun initAgoraEngine() {
        // If you want to START on earpiece:
        rtcEngine?.setDefaultAudioRoutetoSpeakerphone(false)
// If you prefer STARTING on speaker, set true instead.

// (Optional but recommended)
        rtcEngine?.setAudioProfile(
            Constants.AUDIO_PROFILE_DEFAULT, Constants.AUDIO_SCENARIO_DEFAULT
        )

        try {
            rtcEngine = RtcEngine.create(
                baseContext, getString(R.string.agora_app_id), rtcHandler
            )
//            rtcEngine?.setDefaultAudioRoutetoSpeakerphone(false)
        } catch (e: Exception) {
            Log.e(TAG, "Rtc init error: ${Log.getStackTraceString(e)}")
            throw RuntimeException("Rtc SDK init fatal error\n${Log.getStackTraceString(e)}")
        }
    }

    private fun joinChannel(token: String?, channel: String?, userId: Int) {
        Log.d("RTCToken", "Voice token: $token")
        Log.d("RTCToken", "Voice channel: $channel")
        Log.d("RTCToken", "Voice uid: $userId")

//        rtcEngine?.setDefaultAudioRoutetoSpeakerphone(false)

        rtcEngine?.joinChannel(token, channel, "voice", userId)

    }

    private fun leaveChannel() {
        rtcEngine?.leaveChannel()
    }

    // --- Controls (wire these to your buttons if needed) ---

    fun onLocalAudioMuteClicked(view: View) {
        val iv = view as ImageView
        val newState = !iv.isSelected
        iv.isSelected = newState
        if (newState) iv.setColorFilter(resources.getColor(R.color.colorPrimary)) else iv.setColorFilter(
            resources.getColor(R.color.white)
        )
        rtcEngine?.muteLocalAudioStream(newState)
    }

//    fun onSpeakerToggleClicked(view: View) {
//        val iv = view as ImageView
//        val usingSpeaker = rtcEngine?.isSpeakerphoneEnabled ?: false
//        val newState = !usingSpeaker
////        val iv = view as ImageView
////        val newState = !iv.isSelected
////        iv.isSelected = newState
////        if (newState) iv.setColorFilter(resources.getColor(R.color.colorPrimary)) else iv.clearColorFilter()
////        rtcEngine?.setEnableSpeakerphone(newState)
//
//        if (newState) {
//            // Switch to loudspeaker
//            iv.setColorFilter(resources.getColor(R.color.colorPrimary))
//            rtcEngine?.setEnableSpeakerphone(true)
//        } else {
//            // Switch to earpiece
//            iv.clearColorFilter()
//            rtcEngine?.setEnableSpeakerphone(false)
////            rtcEngine?.setDefaultAudioRoutetoSpeakerphone(false) // 👈 force earpiece
//        }
//    }

    @Suppress("DEPRECATION")
    private fun routeToSpeaker(enable: Boolean) {
        val audioManager = getSystemService(AUDIO_SERVICE) as android.media.AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val devices = audioManager.availableCommunicationDevices
            val device = devices.firstOrNull {
                if (enable) it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
                else it.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
            }
            device?.let { audioManager.setCommunicationDevice(it) }
        } else {
            audioManager.mode = android.media.AudioManager.MODE_IN_COMMUNICATION
            audioManager.isSpeakerphoneOn = enable
        }
    }


    fun onSpeakerToggleClicked(view: View) {
        val iv = view as ImageView

        // Let Agora own routing; don’t force OS-level device here.
        val usingSpeaker = rtcEngine?.isSpeakerphoneEnabled ?: false
        val newState = !usingSpeaker

        rtcEngine?.setEnableSpeakerphone(newState)   // switch current route

        if (newState) {
            iv.setColorFilter(resources.getColor(R.color.colorPrimary))
        } else {
            iv.clearColorFilter()
        }
    }


    fun onEndCallClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        Log.d("CALLFLOW", "onEndCallClicked called")
        view.isEnabled = false
        // Cancel timers immediately to prevent them from running after finish() is called
        cancelCallTimeoutTimer()
        cancelCallTimer()

        val flow = intent.getStringExtra(EXTRA_FLOW) ?: "out"
        val calleeId = intent.getStringExtra(EXTRA_CALLEE_ID)
//        if(flow == "out" && calleeId != null) {
        lifecycleScope.launch {
            try {
                if (flow == "out" && calleeId != null) {
                    vm.endCall(calleeId, status = "canceled", fullName)   // <-- suspend
                }
            } catch (t: Throwable) {
                Log.w("CALLFLOW", "endCall failed: ${t.message}")
            } finally {
                finish() // close after the network call completes
            }
        }
        finish()
//        }
    }

    // --- Permissions helpers ---

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(
                this, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initEngineAndJoin()
                } else {
                    showLongToast("Microphone permission is required for voice calls")
                    finish()
                }
            }
        }
    }

    private fun showLongToast(msg: String) {
        runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
    }

    private fun startCallTimeoutTimer() {
        cancelCallTimeoutTimer()
        callTimeoutHandler = Handler(Looper.getMainLooper())
        callTimeoutRunnable = Runnable {
            binding.callTimerOrState.text = "No response"
            Log.d("CALLFLOW", "Call timeout reached. No remote user joined.")
            Toast.makeText(applicationContext, "No answer. Call ended.", Toast.LENGTH_SHORT).show()

            // Tell backend call was missed/ended

            lifecycleScope.launch {
                try {
                    val flow = intent.getStringExtra(EXTRA_FLOW) ?: "out"
                    val calleeId = intent.getStringExtra(EXTRA_CALLEE_ID)
                    if (flow == "out" && calleeId != null) {
                        vm.endCall(calleeId, status = "missed", fullName)
                    }
                } catch (t: Throwable) {
                    Log.w("CALLFLOW", "endCall on timeout failed: ${t.message}")
                } finally {
                    finish()
                }
            }
        }
        callTimeoutHandler?.postDelayed(callTimeoutRunnable!!, CALL_TIMEOUT_MS)
    }

    private fun cancelCallTimeoutTimer() {
        callTimeoutHandler?.removeCallbacks(callTimeoutRunnable!!)
        callTimeoutHandler = null
        callTimeoutRunnable = null
    }

    private fun startCallTimer() {
        cancelCallTimer()
        timerHandler = Handler(Looper.getMainLooper())
        timerRunnable = object : Runnable {
            override fun run() {
                val elapsedMillis = System.currentTimeMillis() - callStartTime
                val seconds = (elapsedMillis / 1000).toInt()
                val minutes = seconds / 60
                val secs = seconds % 60

                binding.callTimerOrState.text = String.format("%02d:%02d", minutes, secs)

                timerHandler?.postDelayed(this, 1000L)
            }
        }
        timerHandler?.post(timerRunnable!!)
    }

    private fun cancelCallTimer() {
        timerHandler?.removeCallbacks(timerRunnable!!)
        timerHandler = null
        timerRunnable = null
    }


    override fun onDestroy() {
        super.onDestroy()
        cancelCallTimeoutTimer()
        cancelCallTimer()
        leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        registerReceiver(
            callStateReceiver,
            android.content.IntentFilter("com.companion.astrodating.ACTION_CALL_STATE"),
            RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(callStateReceiver)
        } catch (_: Throwable) {
        }
    }


    private val callStateReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
//            if (intent.action == "com.companion.astrodating.ACTION_CALL_STATE") {
//                val status = intent.getStringExtra("status")
//                when (status) {
//                    "declined", "missed", "canceled", "ended" -> {
//                        // Leave RTC + close screen
//                        finish()
//                    }
//                }
//            }
        }
    }

    private val videoPerms = arrayOf(
        Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA
    )
    private val voicePerms = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    private var pendingAction: (() -> Unit)? = null
    private var lastRequestedPerms: Array<String> = emptyArray()

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val allGranted = lastRequestedPerms.all { p ->
                result[p] == true || ContextCompat.checkSelfPermission(
                    this, p
                ) == PackageManager.PERMISSION_GRANTED
            }
            if (allGranted) {
                pendingAction?.invoke()
            } else {
                Toast.makeText(
                    this, "Permissions denied. Cannot continue with the call.", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            pendingAction = null
            lastRequestedPerms = emptyArray()
        }

    private fun ensurePermissionsThen(required: Array<String>, action: () -> Unit) {
        val missing = required.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) {
            action()
        } else {
            pendingAction = action
            lastRequestedPerms = missing.toTypedArray()
            permissionsLauncher.launch(lastRequestedPerms)
        }
    }
}

//
//import android.Manifest
//import android.os.Bundle
//import android.os.SystemClock
//import android.widget.ImageButton
//import android.widget.TextView
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.Observer
//import com.companion.astrodating.R
//import com.companion.astrodating.ui.call.viewmodel.CallViewModel
//import com.companion.astrodating.ui.states.UiState
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.MainScope
//import kotlinx.coroutines.cancel
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//
//@AndroidEntryPoint
//class VoiceCallActivity : AppCompatActivity() {
//
//    companion object {
//        const val EXTRA_FLOW = "flow"       // "out" | "in"
//        const val EXTRA_CALLEE_ID = "calleeId"
//        const val EXTRA_PRIMARY_ID = "primaryId"
//        const val EXTRA_CHANNEL = "channel"
//        const val EXTRA_TOKEN = "rtcToken"
//    }
//
//    private val vm: CallViewModel by viewModels()
//
//    private lateinit var btnMic: ImageButton
//    private lateinit var btnSpeaker: ImageButton
//    private lateinit var btnEnd: ImageButton
//    private lateinit var timerOrState: TextView
//
//    private var speakerOn = true
//    private val uiScope = MainScope()
//    private var timerJob: Job? = null
//    private var startAt: Long = 0L
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_call_voice)
//
//        btnMic = findViewById(R.id.btnMic)
//        btnSpeaker = findViewById(R.id.btnSpeaker)
//        btnEnd = findViewById(R.id.btnEnd)
//        timerOrState = findViewById(R.id.callTimerOrState)
//
////        requestPermissions {
////            vm.initEngine()
////            vm.setSpeaker(true) // set false if you prefer earpiece by default
////
////            val flow = intent.getStringExtra(EXTRA_FLOW) ?: "out"
//////            if (flow == "out") {
//////                vm.startVoiceCall(
//////                    calleeId = intent.getStringExtra(EXTRA_CALLEE_ID),
//////                    preferredChannel = intent.getStringExtra(EXTRA_CHANNEL)
//////                )
//////            } else {
//////                vm.acceptIncomingVoice(
//////                    channel = intent.getStringExtra(EXTRA_CHANNEL) ?: error("Missing channel"),
//////                    tokenFromPush = intent.getStringExtra(EXTRA_TOKEN)
//////                )
//////            }
////
////            wireControls()
////            observeVm()
////        }
//    }
//
////    private fun wireControls() {
////        btnMic.setOnClickListener { vm.toggleMic() }
////        btnSpeaker.setOnClickListener { speakerOn = !speakerOn; vm.setSpeaker(speakerOn) }
////        btnEnd.setOnClickListener { vm.endCall(); finish() }
////
////        vm.micMuted.observe(this) { muted ->
////            btnMic.setImageResource(if (muted) R.drawable.ic_mic_off else R.drawable.ic_mic_on)
////        }
////    }
//
//    private fun observeVm() {
//        vm.callSetupState.observe(this, Observer { state ->
//            when (state) {
//                is UiState.Loading -> { stopTimer(); timerOrState.text = "Preparing…" }
//                is UiState.Success -> { timerOrState.text = "Ringing…"; startTimer() }
//                is UiState.Error   -> { stopTimer(); timerOrState.text = "Error: ${state.error}" }
//            }
//        })
//
//        vm.remoteUid.observe(this) { uid ->
//            if (uid == null) { stopTimer(); timerOrState.text = "Ringing…" }
//        }
//    }
//
//    private fun startTimer() {
//        stopTimer()
//        startAt = SystemClock.elapsedRealtime()
//        timerJob = uiScope.launch {
//            while (isActive) {
//                val secs = (SystemClock.elapsedRealtime() - startAt) / 1000
//                timerOrState.text = String.format("Connected %02d:%02d", secs / 60, secs % 60)
//                delay(1000)
//            }
//        }
//    }
//
//    private fun stopTimer() { timerJob?.cancel(); timerJob = null }
////    override fun onDestroy() { super.onDestroy(); uiScope.cancel(); stopTimer(); vm.endCall() }
//
//    private fun requestPermissions(onGranted: () -> Unit) {
//        val launcher = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { if (it.values.all { g -> g }) onGranted() else finish() }
//        launcher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
//    }
//}
