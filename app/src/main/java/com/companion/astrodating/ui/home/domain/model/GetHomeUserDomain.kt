package com.companion.astrodating.ui.home.domain.model


data class GetHomeUserDomain(
    val users: List<GetHomeUserDomainEntity>
)

data class GetHomeUserDomainEntity(
    val id: String,
    val age: Int,
    val birthDate: String,
    val cityOfBirth: String,
    val community: String,
    val currentCity: String,
    val education: String,
    val fullName: String,
    val height: String,
    val isVerifiedAccount: Boolean,
    val latitudeOfCityOfBirth: String,
    val latitudeOfCurrentCity: String,
    val longitudeOfCityOfBirth: String,
    val longitudeOfCurrentCity: String,
    val profession: String,
    val profileUrl: String,
    val religion: String,
    val status: String,
    val timeOfBirth: String,
    val userId: String,
    val zodiacSignInEng: String,
    val zodiacSignInMarathi: String,
    val zodiacPngUrl: String,
    val zodiacSvgUrl: String,
    val isOnline: Boolean
)
