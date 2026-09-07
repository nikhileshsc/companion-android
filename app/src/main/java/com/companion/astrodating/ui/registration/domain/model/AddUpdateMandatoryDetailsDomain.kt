package com.companion.astrodating.ui.registration.domain.model

data class AddUpdateMandatoryDetailsDomain(
    val id: String,
    val age: Int,
    val agoraChatUid: String,
    val birthDate: String,
    val cityOfBirth: String,
    val community: String,
    val currentCity: String,
    val fullName: String,
    val gender: String,
    val isRegistrationCompleted: Boolean,
    val profileUrl: String,
    val timeOfBirth: String,
    val updatedAt: String,
    val zodiacSignInEng: String,
    val zodiacSignInMarathi: String

)
