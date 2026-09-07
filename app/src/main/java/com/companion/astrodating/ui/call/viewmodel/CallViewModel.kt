package com.companion.astrodating.ui.call.viewmodel

import android.os.SystemClock
import android.util.Log
import android.widget.FrameLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.companion.astrodating.base.RtcClient
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.call.domain.model.RtcTokenDomain
import com.companion.astrodating.ui.call.domain.repository.IRtcTokenRepository
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.StorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtc2.IRtcEngineEventHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class CallViewModel @Inject constructor(
    private val repository: IRtcTokenRepository,
    private val rtc: RtcClient
) : ViewModel() {

    // ───────────────── Auth (same pattern as OnlineUsersViewModel) ─────────────────
    private lateinit var authToken: String
    init { StorePreferences.getAuthToken()?.let { authToken = it } }

    // ───────────────── LiveData state ─────────────────
    // 1) Setup / join status (we reuse UiState like the example VM)
    private val _callSetupState = MutableLiveData<UiState<RtcTokenDomain>>()
    val callSetupState: LiveData<UiState<RtcTokenDomain>> = _callSetupState

    // 2) Remote user presence
    private val _remoteUid = MutableLiveData<Int?>()
    val remoteUid: LiveData<Int?> = _remoteUid

    // 3) Local toggles
    private val _micMuted = MutableLiveData(false)
    val micMuted: LiveData<Boolean> = _micMuted

    private val _camMuted = MutableLiveData(false)
    val camMuted: LiveData<Boolean> = _camMuted

    // ───────────────── Session ─────────────────
    var currentChannel: String? = null
    var currentToken: String? = null
    var uid: String? = null

    // a stable UID for RTC (Int) and API (String). Use your app userId if you prefer.
    private fun myUidString(): String {
        val base = StorePreferences.getPrimaryUserAgoraUserName() ?: authToken.ifBlank { SystemClock.uptimeMillis().toString() }
        return ((base.hashCode() and 0x7fffffff)).toString()
    }
    private fun myUidInt(): Int = myUidString().hashCode() and 0x7fffffff

    // ───────────────── RTC events ─────────────────
//    private val rtcEvents = object : IRtcEngineEventHandler() {
//        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
//            // Already emitted Success when we joined; this confirms it’s connected.
//        }
//        override fun onUserJoined(uid: Int, elapsed: Int) { _remoteUid.postValue(uid) }
//        override fun onUserOffline(uid: Int, reason: Int) {
//            if (_remoteUid.value == uid) _remoteUid.postValue(null)
//        }
//        override fun onTokenPrivilegeWillExpire(token: String?) {
//            val ch = currentChannel ?: return
//            viewModelScope.launch {
//                when (val res = repository.getRtcToken(authToken, myUidString(), ch)) {
//                    is ApiResult.Success -> rtc.renewToken(res.data.token)
//                    is ApiResult.Error -> { /* optionally log */ }
//                }
//            }
//        }
//    }

    // ───────────────── Lifecycle ─────────────────
//    fun initEngine() { rtc.init(rtcEvents) }
//    override fun onCleared() { rtc.destroy(); super.onCleared() }

    // ───────────────── Helpers Activities call ─────────────────
//    fun attachLocal(container: FrameLayout) = rtc.setupLocal(container)
//    fun attachRemote(container: FrameLayout, uid: Int) = rtc.setupRemote(container, uid)

//    fun toggleMic() {
//        val newMuted = !(_micMuted.value ?: false)   // Boolean (non-null)
//        _micMuted.value = newMuted
//        rtc.muteLocalAudio(newMuted)                 // true = mute, false = unmute
//    }
//
//    fun toggleCam() {
//        val newMuted = !(_camMuted.value ?: false)
//        _camMuted.value = newMuted
//        rtc.muteLocalVideo(newMuted)                 // true = stop video, false = resume
//    }


//    fun switchCamera() = rtc.switchCamera()
//    fun setSpeaker(on: Boolean) = rtc.setSpeaker(on)
//    fun endCall() { rtc.leave(); _remoteUid.value = null; currentChannel = null }

    // ───────────────── OUTGOING (sender) ─────────────────
//    suspend fun startVideoCall(calleeId: String?, preferredChannel: String? = null) =
//        startCallInternal(isVideo = true, calleeId = calleeId, preferredChannel = preferredChannel)
//
//    suspend fun startVoiceCall(calleeId: String?, preferredChannel: String? = null) =
//        startCallInternal(isVideo = false, calleeId = calleeId, preferredChannel = preferredChannel)

    suspend fun startCallInternal(
        isVideo: Boolean,
        calleeId: String?,
        userId: String,
        preferredChannel: String?,
        myName: String,
        callerPhoto: String
    ): Triple<String, String, Int>? {
        Log.d("CallFlow", "CalleeID: $calleeId")

        val channel =
            preferredChannel ?:
            buildChannelName(calleeId)
        _callSetupState.value = UiState.Loading

        // 1) get RTC token directly (since we are already inside a suspend func)
        return when (val res = repository.getRtcToken(authToken,
            userId,
//            myUidString(),
            channel)) {
            is ApiResult.Error -> {
                _callSetupState.value = UiState.Error(res.errorCode, res.errorMessage)
                null
            }

            is ApiResult.Success -> {
                val tk = res.data
                currentChannel = tk.channel
                currentToken = tk.token
//                uid = tk.userId

                // 2) send call push
                Log.d("CALLERDEBUG", "calleeId: $calleeId")
                if (!calleeId.isNullOrBlank()) {
                    Log.d("CALLFLOW", "Preparing push for channel=${tk.channel}")
                    Log.d("CALLFLOW", "Preparing push for channel=${myName}")
                    val payload = mapOf(
                        "type" to "CALL",
                        "callType" to if (isVideo) "VIDEO" else "VOICE",
                        "channel" to channel,
                        "rtcToken" to tk.token,
                        "uid" to myUidString(),
                        "callerId" to myName,
                        "callerPhoto" to callerPhoto
                    )
                    when (
                        repository.sendCallPush(
                            authToken,
                            calleeId,
                            if (isVideo) "Incoming Video Call" else "Incoming Voice Call",
                            "Tap to join",
                            payload
                        )
                    ) {
                        is ApiResult.Success -> Log.d("CALLFLOW", "Push sent ok")
                        is ApiResult.Error -> Log.w("CALLFLOW", "Push failed (non-blocking)")
                    }
                }

                // 3) update state
                _callSetupState.value = UiState.Success(tk)

                // 4) return token + channel
                Triple(tk.token, tk.channel, tk.userId)
            }
        }
    }

    suspend fun endCall(
        calleeId: String,
        status: String,
        myName: String
    ){
            val payload = mapOf(
                "type" to "call_state",
                "status" to status,
                "callerId" to myName,
            )
            when(repository.sendCallPush(
                authToken,
                calleeId,
                "Companion",
                "Companion",
                payload
            )){
                is ApiResult.Success -> Log.d("CALLFLOW","Success in end call push")
                is ApiResult.Error -> Log.e("CALLFLOW","Failure in end call push")
            }
    }


    // ───────────────── INCOMING (receiver) ─────────────────
//    fun acceptIncomingVideo(channel: String, tokenFromPush: String?) =
//        acceptIncomingInternal(isVideo = true, channel = channel, tokenFromPush = tokenFromPush)
//
//    fun acceptIncomingVoice(channel: String, tokenFromPush: String?) =
//        acceptIncomingInternal(isVideo = false, channel = channel, tokenFromPush = tokenFromPush)
//
//    private fun acceptIncomingInternal(isVideo: Boolean, channel: String, tokenFromPush: String?) {
//        if (!this::authToken.isInitialized || authToken.isBlank()) {
//            _callSetupState.value = UiState.Error(401, "Missing auth token"); return
//        }
//
//        _callSetupState.value = UiState.Loading
//        currentChannel = channel
//
//        viewModelScope.launch {
//            val token = if (!tokenFromPush.isNullOrBlank()) tokenFromPush
//            else when (val res = repository.getRtcToken(authToken, myUidString(), channel)) {
//                is ApiResult.Success -> res.data.token
//                is ApiResult.Error -> return@launch run {
//                    _callSetupState.value = UiState.Error(res.errorCode, res.errorMessage)
//                }
//            }
//
//            val tk = RtcTokenDomain(token, channel)
//            joinRtc(tk, isVideo)
//            _callSetupState.value = UiState.Success(tk)
//        }
//    }

    // ───────────────── Internals ─────────────────
    private fun joinRtc(tk: RtcTokenDomain, isVideo: Boolean) {
        rtc.enableVideo(isVideo)
        if (isVideo) rtc.startPreview()
        rtc.join(tk.token, tk.channel, myUidInt(), isVideo)
    }

    private fun buildChannelName(calleeId: String?) =
        "ch_${calleeId ?: "peer"}_${abs(SystemClock.uptimeMillis()).toString().takeLast(6)}"
}
