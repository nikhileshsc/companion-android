package com.companion.astrodating.ui.purchasePlans.data.mapper

import com.companion.astrodating.ui.purchasePlans.data.dto.UpdateBenefitsDto
import com.companion.astrodating.ui.purchasePlans.domain.model.UpdateBenefitsDomainDetails
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class UpdateBenefitsDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: UpdateBenefitsDto): UpdateBenefitsDomainDetails {
        return UpdateBenefitsDomainDetails(
            data = dto.data?.let {mapToBenefitsDataDomainModel(dto.data)},
            message = dto.message ?: APP_EMPTY_STRING,
            statusCode = dto.statusCode ?: 0

        )
    }

    fun mapToBenefitsDataDomainModel(dto: UpdateBenefitsDto.Data): UpdateBenefitsDomainDetails.Data {
        return UpdateBenefitsDomainDetails.Data(
            subscription = mapToSubscriptionDomainModel(dto.subscription!!),
            unlockChatUsers = dto.unlockChatUsers?: emptyList()
        )
    }

    fun mapToSubscriptionDomainModel(dto: UpdateBenefitsDto.Data.Subscription): UpdateBenefitsDomainDetails.Data.Subscription {
        return UpdateBenefitsDomainDetails.Data.Subscription(
            id = dto._id?: APP_EMPTY_STRING,
            benefits = mapToBenefitsDomainModel(dto.benefits!!),
            subscriptionStatus = dto.subscriptionStatus?: APP_EMPTY_STRING,
            updatedAt = dto.updatedAt?: APP_EMPTY_STRING
        )
    }

    fun mapToBenefitsDomainModel(dto: UpdateBenefitsDto.Data.Benefits): UpdateBenefitsDomainDetails.Data.Benefits {
        return UpdateBenefitsDomainDetails.Data.Benefits(
            id = dto._id?: APP_EMPTY_STRING,
            chatProfiles = dto.chatProfiles?: 0,
            matchMakingReport = dto.matchMakingReport?: 0,
        )
    }



}