package com.companion.astrodating.ui.call.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityCallVideoBinding
import com.companion.astrodating.databinding.ActivityCallVoiceBinding
import com.companion.astrodating.ui.call.ui.VoiceCallActivity.Companion
import com.companion.astrodating.ui.call.ui.VoiceCallActivity.Companion.EXTRA_CALLEE_PROFILE
import com.companion.astrodating.ui.call.viewmodel.CallViewModel
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoCallActivity : BaseActivity() {
    val MyUIdString = StorePreferences.getPrimaryUserAgoraUserName()
    val MyFullDetails = StorePreferences.getUserDetails()
    val fullName = MyFullDetails.fullName
    val profilePhoto = MyFullDetails.profileUrl

    val MyUIdInt = MyUIdString?.toIntOrNull()
    private val binding by lazy{
        ActivityCallVideoBinding.inflate(layoutInflater)
    }

    private var callTimeoutHandler: Handler? = null
    private var callTimeoutRunnable: Runnable? = null
    private val CALL_TIMEOUT_MS = 30_000L

    private var joinTimeoutHandler: Handler? = null
    private var joinTimeoutRunnable: Runnable? = null
    private val JOIN_TIMEOUT_MS = 30_000L  // adjust as you like

    private val RING_TIMEOUT_MS = 30_000L  // rename existing CALL_TIMEOUT_MS


    private var callStartTime: Long = 0L
    private var timerHandler: Handler? = null
    private var timerRunnable: Runnable? = null


    private var mRtcEngine : RtcEngine? = null
    private val mRtcEventHandler = object: IRtcEngineEventHandler() {
            override fun onError(err: Int) {
                Log.e(TAG, "Agora error: $err")
            }

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                Log.i(TAG, "Join channel success: $channel, uid=$uid")
//                startCallTimeoutTimer()
                runOnUiThread{
                    cancelJoinTimeout()
                    startRingTimeout()
                    startCallTimeoutTimer()

                }
            }

        override fun onConnectionStateChanged(state: Int, reason: Int) {
            super.onConnectionStateChanged(state, reason)
            Log.d("CALLFLOW","Connection state changed: $state, $reason")
        }

            override fun onUserJoined(uid: Int, elapsed: Int) {
                Log.i(TAG, "Remote user joined: $uid")
//                cancelCallTimeoutTimer()
//                callStartTime = System.currentTimeMillis()
//                startCallTimer()
//
//                binding.root.post{
//                    binding.callState.text = "00:00"
//                }
//
//                runOnUiThread { setupRemoteVideo(uid) }
                runOnUiThread {
                    cancelRingTimeout()
                    callStartTime = System.currentTimeMillis()
                    startCallTimer()
                    binding.callState.text = "00:00"
                    setupRemoteVideo(uid)
                }
            }

        private fun startRingTimeout() { // replacement for startCallTimeoutTimer
            cancelRingTimeout()
            callTimeoutHandler = Handler(Looper.getMainLooper())
            callTimeoutRunnable = Runnable {
                binding.callState.text = "No response"
                Log.d("CALLFLOW", "Ring timeout: no remote answer.")
                Toast.makeText(applicationContext, "No answer. Call ended.", Toast.LENGTH_SHORT).show()

                lifecycleScope.launch {
                    try {
                        val flow = intent.getStringExtra(EXTRA_FLOW) ?: "out"   // <-- use local constants
                        val calleeId = intent.getStringExtra(EXTRA_CALLEE_ID)
                        if (flow == "out" && calleeId != null) {
                            vm.endCall(calleeId, status = "missed", fullName )
                        }
                    } catch (_: Throwable) { }
                    finally { finish() }
                }
            }
            callTimeoutHandler?.postDelayed(callTimeoutRunnable!!, RING_TIMEOUT_MS)
        }

        private fun cancelRingTimeout() {
            val runnable = callTimeoutRunnable ?: return
            callTimeoutHandler?.removeCallbacks(runnable)
            callTimeoutHandler = null
            callTimeoutRunnable = null
        }



        override fun onUserOffline(uid: Int, reason: Int) {
                cancelCallTimer()
                binding.root.post{
                    binding.callState.text = "Disconnected"
                }
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    finish()
                }, 3000L)

                runOnUiThread { onRemoteUserLeft() }
            }


            override fun onUserMuteVideo(uid: Int, muted: Boolean) {
                Log.d("CALLFLOW", "Remote mute event triggered!")
                runOnUiThread { onRemoteUserVideoMuted(uid, muted) }
            }

    }

    private fun initAgoraEngine(){
        try{
            mRtcEngine = RtcEngine.create(
                baseContext,
                getString(R.string.agora_app_id),
                mRtcEventHandler
            )
        } catch (e: Exception){
            Log.d(TAG, Log.getStackTraceString(e))
            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }
    }

    private fun setupVideoProfile(){
        mRtcEngine!!.enableVideo()
        mRtcEngine!!.setVideoEncoderConfiguration(VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
            VideoEncoderConfiguration.STANDARD_BITRATE,
            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT))
    }

    private fun setupLocalVideo(){
        val container = findViewById<FrameLayout>(R.id.localContainer)
        container.removeAllViews()

        val surfaceView = SurfaceView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        container.addView(surfaceView)

        mRtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))

    }

    private fun joinChannel(token: String?, channel: String?, userId: Int){
//        val MyUIdString = StorePreferences.getPrimaryUserAgoraUserName()
//        var MyUIdInt = MyUIdString?.toIntOrNull()
//            ?: (x.hashCode() and 0x7fffffff)

        Log.d(TAG,"Primary agora username: ${userId}")
        Log.d("RTCToken","JoinToken: ${token}")
        Log.d("RTCToken","JoinChannel: ${channel}")
//        if(userId!=null){
//            val finalUid = userId

        mRtcEngine?.setDefaultAudioRoutetoSpeakerphone(false)
        Log.d("RTCToken","JoinUid: ${userId}")
        mRtcEngine!!.joinChannel(token, channel, "test", userId)
//        }else{
//            mRtcEngine!!.joinChannel(token, channel, "test", userId!!)
//        }
//        mRtcEngine!!.joinChannel("007eJxTYHAVjAqbt8/yZ9usY6FSPxlenkkWWrRkpf5L7gi3zs0rXj5WYDAySDM1SDQ0TTO2SDFJMjBItEyyTEwzTrFMS7QwMDVL6p2wLWPuvG0ZO0sMGBkZGBlYgBgEmMAkM5hkAZNKDMkZ8WYWJqnmKcamZompJknmRoZpJilmJkamBvEmluYGRqZcDIbmFsZmlmaWFuYA6zMq2w==", "demo100", "test", 99)

    }

    private fun setupRemoteVideo(uid: Int){
        val container = findViewById<FrameLayout>(R.id.remoteContainer)
        container.removeAllViews()

        if(container.childCount >= 1){
            return
        }


        val surfaceView = SurfaceView(baseContext).apply {
//            layoutparams =
                FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
            )
            setZOrderMediaOverlay(false)
            tag = uid
        }

        container.addView(surfaceView)

        mRtcEngine!!.setupRemoteVideo(VideoCanvas(
            surfaceView,
            VideoCanvas.RENDER_MODE_HIDDEN,
//            VideoCanvas.RENDER_MODE_FIT,
            uid))
    }

    private fun leaveChannel(){
        mRtcEngine!!.leaveChannel()
    }

    private fun onRemoteUserLeft(){
        val container = findViewById(R.id.remoteContainer) as FrameLayout
        container.removeAllViews()
    }

    private fun onRemoteUserVideoMuted(uid: Int, muted: Boolean){
        Log.d("CALLFLOW", "Attempting remote view blackout!")
        val container = findViewById(R.id.remoteContainer) as FrameLayout
        val surfaceView = container.getChildAt(0) as SurfaceView
        val tag = surfaceView.tag
        if(tag!= null && tag as Int == uid){
            surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
        }
        Log.d("CALLFLOW", "Attempted remote view blackout!")
    }



    companion object {
        private const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
        private const val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

        const val EXTRA_FLOW = "flow"          // "out" | "in"
        const val EXTRA_CALLEE_ID = "calleeId" // for outgoing push (optional)
        const val EXTRA_PRIMARY_ID = "primaryId"
        const val EXTRA_CALLEE_NAME = "calleeName"
        const val EXTRA_CHANNEL = "channel"    // incoming or preferred
        const val EXTRA_TOKEN = "rtcToken"     // optional on incoming
    }

    private val vm: CallViewModel by viewModels()

    private lateinit var remoteContainer: FrameLayout
    private lateinit var localContainer: FrameLayout
    private lateinit var btnMic: ImageButton
    private lateinit var btnCam: ImageButton
    private lateinit var btnFlip: ImageButton
    private lateinit var btnSpeaker: ImageButton
    private lateinit var btnEnd: ImageButton
    private lateinit var stateText: TextView

    private var speakerOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        val localImageView = binding.bgImageLocal
//        Glide.with(binding.bgImageLocal.context).load("w")
//            .error(R.drawable.ic_default_profile).into(binding.bgImageLocal)
        switchImage(profilePhoto)

//        startJoinTimeout()

        binding.controlsVideo.btnMic.setColorFilter(resources.getColor(R.color.white))
        binding.peerName.text = intent.getStringExtra(EXTRA_CALLEE_NAME)

        val uri = intent.getStringExtra(EXTRA_CALLEE_PROFILE)

//        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
//            initAgoraEngineAndJoinChannel()
//        }

        ensurePermissionsThen ( videoPerms) {initAgoraEngineAndJoinChannel()}




    }

    override fun initObservers() {
        //
    }

    private fun switchImage(uri: String){
        Glide.with(binding.bgImageLocal.context).load(uri)
            .error(R.drawable.ic_default_profile)
            .circleCrop()
            .into(binding.bgImageLocal)
    }

    private fun initAgoraEngineAndJoinChannel() {
        initAgoraEngine()
        setupVideoProfile()

        mRtcEngine!!.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER)

        setupLocalVideo()
        mRtcEngine!!.startPreview()
        mRtcEngine!!.muteLocalVideoStream(false)   // <--- important
        mRtcEngine!!.muteLocalAudioStream(false)


//        val calleeId = intent.getStringExtra(EXTRA_CALLEE_ID)
//        val primaryId = intent.getStringExtra(EXTRA_PRIMARY_ID)
//        Log.d("!X!X!X!!X!X!", calleeId + primaryId)
//        vm.startVideoCall(calleeId)

        val flow = intent.getStringExtra("flow") ?: "out"

        val ctoken: String?
        val channel: String?
//        val userId: Int?


        if (flow == "in") {
            // Receiver — get extras from intent
            ctoken = intent.getStringExtra("token") ?: error("Missing token for incoming call")
            channel = intent.getStringExtra("channel") ?: error("Missing channel for incoming call")
//            val x = intent.getStringExtra("userId") ?: error("Missing userId for incoming call")
            Log.d("!X!X!X!!X!X!", "caller token: "+ctoken +" " + channel)
            lifecycleScope.launch {

                val result = vm.startCallInternal(true, null, MyUIdString!!, channel, fullName, "")
                if(result!=null){
                    val (token, channel, userId) = result
                    Log.d("!!!!!!!!!!",token + " "+ channel)
                    joinChannel(token, channel, userId)


//                    Glide.with(binding.peerAvatar.context).load(uri)
//                        .error(R.drawable.ic_default_profile).circleCrop().into(binding.peerAvatar)
                } else {
                    showLongToast("Failed to accept video call")
                    finish()
                }
            }
//            userId = x.toInt()
//            joinChannel(token, channel, MyUIdInt!!)
        } else {
//            val x = StorePreferences.getPrimaryUserAgoraUserName()
//            var finalUid = x?.toIntOrNull()
            // Caller — use VM values
            lifecycleScope.launch {
                val calleeId = intent.getStringExtra(EXTRA_CALLEE_ID)
                val result = vm.startCallInternal(isVideo = true, calleeId, MyUIdString!!, null, fullName, profilePhoto)
                if (result != null) {
                    val (token, channel, userId) = result
                    Log.d("!!!!!!!!!!",token + " "+ channel)
                    joinChannel(token, channel, userId)
                } else {
                    showLongToast("Failed to start video call")
                    finish()
                }
            }

        }
//        joinChannel(vm.currentToken, vm.currentChannel)
    }

//    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
//        Log.d(TAG, "checkSelfPermission $permission $requestCode")
//        if (ContextCompat.checkSelfPermission(this,
//                permission) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                arrayOf(permission),
//                requestCode)
//            return false
//        }
//        return true
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        Log.d(TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode)
//
//        when (requestCode) {
//            PERMISSION_REQ_ID_RECORD_AUDIO -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)
//                } else {
//                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO)
//                    finish()
//                }
//            }
//            PERMISSION_REQ_ID_CAMERA -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    initAgoraEngineAndJoinChannel()
//                } else {
//                    showLongToast("No permission for " + Manifest.permission.CAMERA)
//                    finish()
//                }
//            }
//        }
//    }
//
//    private fun showLongToast(msg: String) {
//        this.runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
//    }

    override fun onDestroy() {
        super.onDestroy()
//        cancelCallTimeoutTimer()
//        cancelCallTimer()
//        leaveChannel()
//        /*
//          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.
//
//          This method is useful for apps that occasionally make voice or video calls,
//          to free up resources for other operations when not making calls.
//         */
//        RtcEngine.destroy()
//        mRtcEngine = null
        Log.d("!X!X!X!!X!X!", EXTRA_CALLEE_ID + EXTRA_PRIMARY_ID)

        cancelJoinTimeout()
//        cancelRingTimeout()
        cancelCallTimer()
        try { mRtcEngine?.leaveChannel() } catch (_: Throwable) {}
        RtcEngine.destroy()
        mRtcEngine = null
    }

    fun onLocalVideoMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        // Stops/Resumes sending the local video stream.
        mRtcEngine!!.muteLocalVideoStream(iv.isSelected)

        val container = findViewById(R.id.localContainer) as FrameLayout
        val surfaceView = container.getChildAt(0) as SurfaceView
        surfaceView.setZOrderMediaOverlay(!iv.isSelected)
        surfaceView.visibility = if (iv.isSelected) View.GONE else View.VISIBLE
    }

    fun onLocalAudioMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine!!.muteLocalAudioStream(iv.isSelected)
    }

    fun onSwitchCameraClicked(view: View) {
        // Switches between front and rear cameras.
        mRtcEngine!!.switchCamera()
    }

    fun onEncCallClicked(view: View) {
        view.isEnabled = false

        val flow = intent.getStringExtra(EXTRA_FLOW) ?: "out"
        val calleeId = intent.getStringExtra(EXTRA_CALLEE_ID)
        lifecycleScope.launch {
            try {
                if(flow == "out" && calleeId != null) {
                    vm.endCall(calleeId, status = "canceled", fullName)   // <-- suspend
                }
            } catch (t: Throwable) {
                Log.w("CALLFLOW", "endCall failed: ${t.message}")
            } finally {
                finish() // close after the network call completes
            }
        }
        finish()
    }


private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
    if (ContextCompat.checkSelfPermission(this, permission)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        return false
    }
    return true
}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            VideoCallActivity.PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel()
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


    private fun startJoinTimeout() {
        cancelJoinTimeout()
        joinTimeoutHandler = Handler(Looper.getMainLooper())
        joinTimeoutRunnable = Runnable {
            Log.d("CALLFLOW", "Join timeout — could not connect to channel.")
            Toast.makeText(this, "Connection failed. Please try again.", Toast.LENGTH_SHORT).show()
//            endCallAndFinishIfOutgoing(status = "canceled")
        }
        joinTimeoutHandler?.postDelayed(joinTimeoutRunnable!!, JOIN_TIMEOUT_MS)
    }

    private fun cancelJoinTimeout() {
        joinTimeoutHandler?.removeCallbacks(joinTimeoutRunnable ?: return)
        joinTimeoutHandler = null
        joinTimeoutRunnable = null
    }






    private fun startCallTimeoutTimer() {
        cancelCallTimeoutTimer()
        callTimeoutHandler = Handler(Looper.getMainLooper())
        callTimeoutRunnable = Runnable {
            binding.callState.text = "No response"
            Log.d("CALLFLOW", "Call timeout reached. No remote user joined.")
            Toast.makeText(applicationContext, "No answer. Call ended.", Toast.LENGTH_SHORT).show()

            // Tell backend call was missed/ended
            lifecycleScope.launch {
                try {
                    val flow = intent.getStringExtra(VoiceCallActivity.EXTRA_FLOW) ?: "out"
                    val calleeId = intent.getStringExtra(VoiceCallActivity.EXTRA_CALLEE_ID)
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

                binding.callState.text = String.format("%02d:%02d", minutes, secs)

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        registerReceiver(callStateReceiver, android.content.IntentFilter("com.companion.astrodating.ACTION_CALL_STATE"), RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        try { unregisterReceiver(callStateReceiver) } catch (_: Throwable) {}
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
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
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
                    this,
                    p
                ) == PackageManager.PERMISSION_GRANTED
            }
            if (allGranted) {
                pendingAction?.invoke()
            } else {
                Toast.makeText(this, "Permissions denied. Cannot continue with the call.", Toast.LENGTH_SHORT).show()
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
