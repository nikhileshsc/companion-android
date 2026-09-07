package com.companion.astrodating.ui.home.data.dto

data class GetHomeUsersDto(
    val `data`: GetHomeUsersDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class GetHomeUsersDtoData(
    val pageNumber: Int? = null,
    val perPage: Int? = null,
    val totalCount: Int? = null,
    val users: List<GetHomeUserDtoEntity>
)

data class GetHomeUserDtoEntity(
    val _id: String?=null,
    val age: Int? = null,
    val birthDate: String? = null,
    val cityOfBirth: String? = null,
    val community: String? = null,
    val currentCity: String? = null,
    val education: String? = null,
    val fullName: String? = null,
    val height: String? = null,
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
    val zodiacSignInEng: String? = null,
    val zodiacSignInMarathi: String? = null,
    val zodiacPngUrl: String? = null,
    val zodiacSvgUrl: String? = null,
    val isOnline: Boolean? = null
)
