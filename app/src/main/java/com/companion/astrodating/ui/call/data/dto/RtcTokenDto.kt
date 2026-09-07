package com.companion.astrodating.ui.call.data.dto

data class RtcTokenDto(
    val statusCode: Int,
    val message: String,
    val error: String?,
    val data: RtcTokenDataDto?
)

data class RtcTokenDataDto(
    val token: String,
    val channelName: String,
    val userId: Int
)

data class RtcTokenRequestDto(
    val uid: String? = null,        // backend reads from auth usually; keep null
    val channel: String? = null,    // let server auto-generate if null
    val ttlSeconds: Int? = 3600
)
