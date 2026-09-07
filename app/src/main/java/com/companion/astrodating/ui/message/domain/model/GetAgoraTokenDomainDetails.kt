package com.companion.astrodating.ui.message.domain.model

data class GetAgoraTokenDomainDetails(
    val user: GetAgoraTokenUserDomainEntity
)
data class GetAgoraTokenUserDomainEntity(
    val id: String,
    val chatToken: String,
    val userName: String,
    val updatedAt: String
)

data class AgoraChatLoginDtoEntity(
    val chatToken: String,
    val userName: String
)
