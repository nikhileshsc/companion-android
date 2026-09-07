package com.companion.astrodating.ui.interests.data.dto

data class GetInterestDto(
    val `data`: GetInterestDataDto? = null,
    val message: String,
    val statusCode: Int
)

data class GetInterestDataDto(
    val users: List<GetInterestUserDtoEntity>? = null
)

data class GetInterestUserDtoEntity(
    val _id: String? = null,
    val age: Int? = null,
    val community: String? = null,
    val currentCity: String? = null,
    val education: String? = null,
    val fullName: String? = null,
    val height: String? = null,
    val isVerifiedAccount: Boolean? = null,
    val profession: String? = null,
    val profileUrl: String? = null,
    val religion: String? = null,
    val status: String? = null,
    val zodiacSignInEng: String? = null,
    val zodiacSignInMarathi: String? = null,
    val zodiacPngUrl: String? = null,
    val zodiacSvgUrl: String? = null
)