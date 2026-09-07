package com.companion.astrodating.ui.registration.data.dto

data class AddUpdateMandatoryDetailsDto(
    val `data`: AddUpdateMandatoryDetailsDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class AddUpdateMandatoryDetailsDtoData(
    val user: AddUpdateMandatoryUserDto? = null
)

data class AddUpdateMandatoryUserDto(
    val _id: String? = null,
    val age: Int? = null,
    val agoraChatUid: String? = null,
    val birthDate: String? = null,
    val cityOfBirth: String? = null,
    val community: String? = null,
    val currentCity: String? = null,
    val fullName: String? = null,
    val gender: String? = null,
    val isRegistrationCompleted: Boolean? = null,
    val profileUrl: String? = null,
    val timeOfBirth: String? = null,
    val updatedAt: String? = null,
    val zodiacSignInEng: String? = null,
    val zodiacSignInMarathi: String? = null
)
