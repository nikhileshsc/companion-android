package com.companion.astrodating.ui.registration.data.mapper

import com.companion.astrodating.ui.registration.data.dto.GetMandatoryDetailsDto
import com.companion.astrodating.ui.registration.data.dto.GetMandatoryDetailsDtoData
import com.companion.astrodating.ui.registration.domain.model.GetMandatoryDetailsDomain
import com.companion.astrodating.ui.registration.domain.model.GetMandatoryUserDetailsDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetMandatoryDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: GetMandatoryDetailsDto): GetMandatoryDetailsDomain {
        return GetMandatoryDetailsDomain(
            community = dto.data.community ?: emptyList(),
            user = GetMandatoryUserDetailsDomainEntity(
                id = dto.data.user?._id ?: APP_EMPTY_STRING,
                age = dto.data.user?.age ?: 0,
                birthDate = dto.data.user?.birthDate ?: APP_EMPTY_STRING,
                cityOfBirth = dto.data.user?.cityOfBirth ?: APP_EMPTY_STRING,
                community = dto.data.user?.community ?: APP_EMPTY_STRING,
                currentCity = dto.data.user?.currentCity ?: APP_EMPTY_STRING,
                fullName = dto.data.user?.fullName ?: APP_EMPTY_STRING,
                gender = dto.data.user?.gender ?: APP_EMPTY_STRING,
                isRegistrationCompleted = dto.data.user?.isRegistrationCompleted ?: false,
                timeOfBirth = dto.data.user?.timeOfBirth ?: APP_EMPTY_STRING,
                latitudeOfCityOfBirth = dto.data.user?.latitudeOfCityOfBirth ?: APP_EMPTY_STRING,
                longitudeOfCityOfBirth = dto.data.user?.longitudeOfCityOfBirth ?: APP_EMPTY_STRING,
                latitudeOfCurrentCity = dto.data.user?.latitudeOfCurrentCity ?: APP_EMPTY_STRING,
                longitudeOfCurrentCity = dto.data.user?.longitudeOfCurrentCity ?: APP_EMPTY_STRING,
                profileUrl = dto.data.user?.profileUrl ?: APP_EMPTY_STRING,
                subscriptionId = dto.data.user?.subscriptionId ?: APP_EMPTY_STRING,
            ),
            subscription = mapToSubscriptionDomainModel(dto.data.subscription ?: GetMandatoryDetailsDtoData.SubscriptionDtoEntity())
        )
    }

    fun mapToSubscriptionDomainModel(dto: GetMandatoryDetailsDtoData.SubscriptionDtoEntity): GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity {
        return GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity(
            id = dto._id ?: APP_EMPTY_STRING,
            benefits = mapToBenefitsDomainModel(dto.benefits ?: GetMandatoryDetailsDtoData.SubscriptionDtoEntity.BenefitsDtoEntity() ),
            duration = dto.duration ?: APP_EMPTY_STRING,
            planExpiredOn = dto.planExpiredOn ?: APP_EMPTY_STRING,
            planName = dto.planName ?: APP_EMPTY_STRING,
            planStartsOn = dto.planStartsOn ?: APP_EMPTY_STRING,
            subscriptionStatus = dto.subscriptionStatus ?: APP_EMPTY_STRING
        )
    }

    fun mapToBenefitsDomainModel(dto: GetMandatoryDetailsDtoData.SubscriptionDtoEntity.BenefitsDtoEntity): GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity.BenefitsDomainEntity {
        return GetMandatoryUserDetailsDomainEntity.SubscriptionDomainEntity.BenefitsDomainEntity(
            id = dto._id ?: APP_EMPTY_STRING,
            chatProfiles = dto.chatProfiles ?: 0,
            matchMakingReport = dto.matchMakingReport ?: 0,
            )
    }

}