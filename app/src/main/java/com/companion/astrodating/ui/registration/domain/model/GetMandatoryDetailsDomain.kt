package com.companion.astrodating.ui.registration.domain.model

data class GetMandatoryDetailsDomain(
    val community: List<String>,
    val user: GetMandatoryUserDetailsDomainEntity,
    val subscription: GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity? = null,
)
data class GetMandatoryUserDetailsDomainEntity(
    val id: String,
    val age: Int,
    val birthDate: String,
    val cityOfBirth: String,
    val community: String,
    val currentCity: String,
    val fullName: String,
    val gender: String,
    val isRegistrationCompleted: Boolean,
    val timeOfBirth: String,
    val latitudeOfCityOfBirth: String,
    val latitudeOfCurrentCity: String,
    val longitudeOfCityOfBirth: String,
    val longitudeOfCurrentCity: String,
    val profileUrl: String,
    val subscriptionId: String
){
    data class SubscriptionDomainEntity(
        val id: String,
        val benefits: BenefitsDomainEntity,
        val duration: String,
        val planExpiredOn: String,
        val planName: String,
        val planStartsOn: String,
        val subscriptionStatus: String
    ) {
        data class BenefitsDomainEntity(
            val id: String,
            val chatProfiles: Int,
            val matchMakingReport: Int
        )
    }
}
