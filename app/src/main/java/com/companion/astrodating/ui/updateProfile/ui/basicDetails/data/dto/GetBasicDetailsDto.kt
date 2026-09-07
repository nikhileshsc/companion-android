package com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.dto

data class GetBasicDetailsDto(
    val `data`: GetBasicDetailsDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class GetBasicDetailsDtoData(
    val user: GetBasicUserDetailsEntity? = null
)

data class GetBasicUserDetailsEntity(
    val _id: String? = null,
    val community: String? = null,
    val currentCity: String? = null,
    val education: String? = null,
    val height: String? = null,
    val lookingFor: String? = null,
    val profession: String? = null,
    val religion: String? = null,
    val status: String? = null
)
