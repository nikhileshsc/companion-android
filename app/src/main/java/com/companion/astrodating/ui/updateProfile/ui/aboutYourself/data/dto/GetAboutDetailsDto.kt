package com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.dto

data class GetAboutDetailsDto(
    val `data`: GetAboutDetailsDataDto? = null,
    val message: String,
    val statusCode: Int
)

data class GetAboutDetailsDataDto(
    val user: GetAboutDetailsUserDto? = null
)

data class GetAboutDetailsUserDto(
    val _id: String? = null,
    val aboutYourself: String? = null,
    val expectations: String? = null,
    val interest: String? = null,
    val updatedAt: String? = null
)
