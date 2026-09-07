package com.companion.astrodating.ui.interests.domain.model

data class GetInterestsDomain(
    val users: List<GetInterestUserDomainEntity>
)


data class GetInterestUserDomainEntity(
    val id: String,
    val age: Int,
    val community: String,
    val currentCity: String,
    val education: String,
    val fullName: String,
    val height: String,
    val isVerifiedAccount: Boolean,
    val profession: String,
    val profileUrl: String,
    val religion: String,
    val status: String,
    val zodiacSignInEng: String,
    val zodiacSignInMarathi: String,
    val zodiacPngUrl: String,
    val zodiacSvgUrl: String
)
