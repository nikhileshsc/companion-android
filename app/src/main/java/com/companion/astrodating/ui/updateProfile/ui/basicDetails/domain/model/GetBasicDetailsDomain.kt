package com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model

data class GetBasicDetailsDomain(
    val id: String,
    val community: String,
    val currentCity: String,
    val education: String,
    val height: String,
    val lookingFor: String,
    val profession: String,
    val religion: String,
    val status: String
)
