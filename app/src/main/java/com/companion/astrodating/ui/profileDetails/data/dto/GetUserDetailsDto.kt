package com.companion.astrodating.ui.profileDetails.data.dto

data class GetUserDetailsDto(
    val `data`: GetUserDetailsDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class GetUserDetailsDtoData(
    val user: GetUserDto? = null
)

data class GetUserDto(
    val _id: String? = null,
    val aboutYourself: String? = null,
    val age: Int? = null,
    val approvedPhotosCount: Int? = null,
    val birthDate: String? = null,
    val cityOfBirth: String? = null,
    val community: String? = null,
    val currentCity: String? = null,
    val education: String? = null,
    val expectations: String? = null,
    val fullName: String? = null,
    val gallery: List<GetUserDetailsGalleryDto>? = null,
    val gender: String? = null,
    val height: Double? = null,
    val interest: String? = null,
    val isVerifiedAccount: Boolean? = null,
    val latitudeOfCityOfBirth: String? = null,
    val latitudeOfCurrentCity: String? = null,
    val longitudeOfCityOfBirth: String? = null,
    val longitudeOfCurrentCity: String? = null,
    val profession: String? = null,
    val profileUrl: String? = null,
    val religion: String? = null,
    val status: String? = null,
    val timeOfBirth: String? = null,
    val userId: String? = null,
    val zodiacPngUrl: String? = null,
    val zodiacSignInEng: String? = null,
    val zodiacSignInMarathi: String? = null,
    val zodiacSvgUrl: String? = null,
    val userStatus: String? = null
)

data class GetUserDetailsGalleryDto(
    val _id: String? = null,
    val createdAt: String? = null,
    val galleryUrl: String? = null,
    val isActionTaken: Boolean? = null,
    val status: String? = null,
    val updatedAt: String? = null
)