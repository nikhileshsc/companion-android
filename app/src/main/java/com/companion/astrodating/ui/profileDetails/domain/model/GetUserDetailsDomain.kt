package com.companion.astrodating.ui.profileDetails.domain.model


data class GetUserDetailsDomain(
    val id: String,
    val aboutYourself: String,
    val age: Int,
    val approvedPhotosCount: Int,
    val birthDate: String,
    val cityOfBirth: String,
    val community: String,
    val currentCity: String,
    val education: String,
    val expectations: String,
    val fullName: String,
    val gallery: List<GetUserDetailsGalleryDomain>,
    val gender: String,
    val height: Double,
    val interest: String,
    val isVerifiedAccount: Boolean,
    val latitudeOfCityOfBirth: String,
    val latitudeOfCurrentCity: String,
    val longitudeOfCityOfBirth: String,
    val longitudeOfCurrentCity: String,
    val profession: String,
    val profileUrl: String,
    val religion: String,
    val status: String,
    val userId: String,
    val timeOfBirth: String,
    val zodiacPngUrl: String,
    val zodiacSignInEng: String,
    val zodiacSignInMarathi: String,
    val zodiacSvgUrl: String,
    val userStatus: String
)

data class GetUserDetailsGalleryDomain(
    val id: String,
    val createdAt: String,
    val galleryUrl: String,
    val isActionTaken: Boolean,
    val status: String,
    val updatedAt: String
)