package com.companion.astrodating.ui.interests.data.dto

data class UpdateInterestDto(
    val `data`: UpdateInterestDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class UpdateInterestDtoData(
    val user: UpdateInterestUserDtoEntity? = null
)

data class UpdateInterestUserDtoEntity(
    val __v: Int? = null,
    val _id: String? = null,
    val blockedUsers: List<String>? = null,
    val declinedUserInterests: List<String>? = null,
    val receivedInterestUsers: List<String>? = null,
    val sentInterestUsers: List<String>? = null,
    val shortListedUsers: List<String?>? = null,
    val updatedAt: String? = null
)
