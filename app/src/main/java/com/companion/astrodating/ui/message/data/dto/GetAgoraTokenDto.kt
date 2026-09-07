package com.companion.astrodating.ui.message.data.dto

data class GetAgoraTokenDto(
    val `data`: GetAgoraTokenDtoData? = null,
    val message: String? = null,
    val statusCode: Int? = null
)

data class GetAgoraTokenDtoData(
    val user: GetAgoraTokenUserDtoEntity? = null
)

data class GetAgoraTokenUserDtoEntity(
    val _id: String? = null,
    val agoraChatLoginDetails: AgoraChatLoginDtoEntity? = null,
    val updatedAt: String? = null
)

data class AgoraChatLoginDtoEntity(
    val chatToken: String? = null,
    val userName: String? = null
)
