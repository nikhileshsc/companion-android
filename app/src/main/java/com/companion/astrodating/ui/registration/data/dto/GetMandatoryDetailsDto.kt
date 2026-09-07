package com.companion.astrodating.ui.registration.data.dto

data class GetMandatoryDetailsDto(
    val `data`: GetMandatoryDetailsDtoData,
    val message: String,
    val statusCode: Int
)

data class GetMandatoryDetailsDtoData(
    val community: List<String>? = null,
    val subscription: SubscriptionDtoEntity? = null,
    val user: UserMandatoryDetailsDto? = null
){
    data class SubscriptionDtoEntity(
        val _id: String? = null,
        val benefits: BenefitsDtoEntity? = null,
        val duration: String? = null,
        val planExpiredOn: String? = null,
        val planName: String? = null,
        val planStartsOn: String? = null,
        val subscriptionStatus: String? = null
    ) {
        data class BenefitsDtoEntity(
            val _id: String? = null,
            val chatProfiles: Int? = null,
            val matchMakingReport: Int? = null
        )
    }
}

data class UserMandatoryDetailsDto(
    val _id: String? = null,
    val age: Int? = null,
    val birthDate: String? = null,
    val cityOfBirth: String? = null,
    val community: String? = null,
    val currentCity: String? = null,
    val fullName: String? = null,
    val gender: String? = null,
    val isRegistrationCompleted: Boolean? = null,
    val timeOfBirth: String? = null,
    val latitudeOfCityOfBirth: String? = null,
    val latitudeOfCurrentCity: String? = null,
    val longitudeOfCityOfBirth: String? = null,
    val longitudeOfCurrentCity: String? = null,
    val profileUrl: String? = null,
    val subscriptionId: String? = null
    )