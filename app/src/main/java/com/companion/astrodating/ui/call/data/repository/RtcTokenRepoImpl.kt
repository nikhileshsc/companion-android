package com.companion.astrodating.ui.call.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.call.data.dto.RtcTokenDto
import com.companion.astrodating.ui.call.data.dto.RtcTokenRequestDto
import com.companion.astrodating.ui.call.data.mapper.RtcTokenMapper
import com.companion.astrodating.ui.call.domain.model.RtcTokenDomain
import com.companion.astrodating.ui.call.domain.repository.IRtcTokenRepository
import com.companion.astrodating.ui.chat.data.requestData.sendMessagePushNotificationData
import com.google.gson.Gson
import javax.inject.Inject

class RtcTokenRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: RtcTokenMapper
): IRtcTokenRepository {
    override suspend fun getRtcToken(
        token: String,
        uid: String,
        channel: String
    ): ApiResult<RtcTokenDomain> {
        val result = api.getRtcToken(token, RtcTokenRequestDto(uid, channel))
        return if(result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse = Gson().fromJson(result.errorBody()!!.charStream(), RtcTokenDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }

    override suspend fun sendCallPush(
        token: String,
        receiverId: String,
        title: String,
        message: String,
        extra: Map<String, String>
    ): ApiResult<Unit> {
        val type = extra["type"]
        val payloadJson = org.json.JSONObject().apply {
            put("type", type)
            put("title", title)                              // e.g., "Incoming Video Call"
            put("hint", message)                             // "Tap to join"
            val meta = org.json.JSONObject()
            extra.forEach { (k, v) -> meta.put(k, v) }       // channel, rtcToken, callType, callerId...
            put("meta", meta)
        }.toString()

        val req = sendMessagePushNotificationData(
            receiverId = receiverId,
            message = payloadJson
        )
        val result = api.sendMessagePushNotification(token, req)
        return if(result.isSuccessful){
            ApiResult.Success(Unit)
        }else{
            val errorResponse = Gson().fromJson(result.errorBody()!!.charStream(), RtcTokenDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}