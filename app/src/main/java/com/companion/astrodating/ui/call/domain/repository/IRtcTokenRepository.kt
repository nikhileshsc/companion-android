package com.companion.astrodating.ui.call.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.call.domain.model.RtcTokenDomain

interface IRtcTokenRepository {
    suspend fun getRtcToken(
        token: String,
        uid: String,
        channel: String
    ): ApiResult<RtcTokenDomain>

    suspend fun sendCallPush(
        token: String,
        receiverId: String,
        title: String,
        message: String,
        extra: Map<String, String>        // payload for the callee
    ): ApiResult<Unit>
}