package com.companion.astrodating.base

import android.content.Context
import android.view.SurfaceView
import android.widget.FrameLayout
import dagger.hilt.android.qualifiers.ApplicationContext
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class RtcClient @Inject constructor(
    @ApplicationContext private val context: Context,
//    @Named("AGORA_APP_ID") private val appId: String
) {
    private var engine: RtcEngine? = null
    private var handler: IRtcEngineEventHandler? = null

    fun init(eventHandler: IRtcEngineEventHandler) {
        if (engine != null) return
        handler = eventHandler
        val cfg = RtcEngineConfig().apply {
            mContext = context
            mAppId = appId
            mEventHandler = eventHandler
        }
        engine = RtcEngine.create(cfg)
    }

    fun enableVideo(enable: Boolean) {
        if (enable) engine?.enableVideo() else engine?.disableVideo()
    }

    fun startPreview() = engine?.startPreview()
    fun stopPreview() = engine?.stopPreview()

    fun join(token: String?, channel: String, uid: Int, isVideo: Boolean) {
        val opts = ChannelMediaOptions().apply {
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            publishMicrophoneTrack = true
            publishCameraTrack = isVideo
        }
        engine?.joinChannel(token, channel, uid, opts)
    }

    fun leave() { engine?.leaveChannel() }

    fun destroy() {
        RtcEngine.destroy()
        engine = null
        handler = null
    }

    fun setupLocal(container: FrameLayout): SurfaceView {
        val view = SurfaceView(container.context)
        container.removeAllViews()
        container.addView(view)
        engine?.setupLocalVideo(VideoCanvas(view, VideoCanvas.RENDER_MODE_FIT, 0))
        return view
    }

    fun setupRemote(container: FrameLayout, uid: Int) {
        val view = SurfaceView(container.context).apply { setZOrderMediaOverlay(true) }
        container.removeAllViews()
        container.addView(view)
        engine?.setupRemoteVideo(VideoCanvas(view, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    fun switchCamera() = engine?.switchCamera()
    fun muteLocalAudio(mute: Boolean) = engine?.muteLocalAudioStream(mute)
    fun muteLocalVideo(mute: Boolean) = engine?.muteLocalVideoStream(mute)
    fun setSpeaker(on: Boolean) = engine?.setEnableSpeakerphone(on)
    fun renewToken(newToken: String) = engine?.renewToken(newToken)
}