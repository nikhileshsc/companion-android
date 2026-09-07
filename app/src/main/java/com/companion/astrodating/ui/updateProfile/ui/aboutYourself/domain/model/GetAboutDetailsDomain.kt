package com.companion.astrodating.ui.updateProfile.ui.aboutYourself.domain.model

data class GetAboutDetailsDomain(
    val id: String,
    val aboutYourself: String,
    val expectations: String,
    val interest: String,
    val updatedAt: String
)
