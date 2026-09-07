package com.companion.astrodating.ui.interests.domain.model


data class UpdateInterestDomain(
    val v: Int,
    val id: String,
    val blockedUsers: List<String>,
    val declinedUserInterests: List<String>,
    val receivedInterestUsers: List<String>,
    val sentInterestUsers: List<String>,
    val shortListedUsers: List<String?>,
    val updatedAt: String
)