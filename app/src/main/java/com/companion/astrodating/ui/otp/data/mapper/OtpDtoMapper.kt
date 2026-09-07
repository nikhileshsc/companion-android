package com.companion.astrodating.ui.otp.data.mapper

import com.companion.astrodating.ui.otp.data.dto.BenefitsDto
import com.companion.astrodating.ui.otp.data.dto.OtpDto
import com.companion.astrodating.ui.otp.data.dto.SubscriptionDto
import com.companion.astrodating.ui.otp.data.dto.VerifyOtpDto
import com.companion.astrodating.ui.otp.domain.model.BenefitsDomain
import com.companion.astrodating.ui.otp.domain.model.OtpDomain
import com.companion.astrodating.ui.otp.domain.model.SubscriptionDomain
import com.companion.astrodating.ui.otp.domain.model.VerifyOtpDomain
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class OtpDtoMapper @Inject constructor() {

    fun mapToDomainModel(dto: OtpDto): OtpDomain {
        return OtpDomain(
            countryCode = dto.data.user?.countryCode ?: APP_EMPTY_STRING,
            email = dto.data.user?.email ?: APP_EMPTY_STRING,
            mobile = dto.data.user?.mobile ?: APP_EMPTY_STRING,
            type = dto.data.type ?: APP_EMPTY_STRING,
        )
    }

    fun mapToVerifyOtpDomainModel(dto: VerifyOtpDto): VerifyOtpDomain {
        return VerifyOtpDomain(
            token = dto.data?.token ?: APP_EMPTY_STRING,
            agoraChatUserName = dto.data?.user?.agoraChatLoginDetails?.userName ?: APP_EMPTY_STRING,
            agoraChatToken = dto.data?.user?.agoraChatLoginDetails?.chatToken ?: APP_EMPTY_STRING,
            blockedUsers = dto.data?.user?.blockedUsers ?: emptyList(),
            declinedUserInterests = dto.data?.user?.declinedUserInterests ?: emptyList(),
            unlockChatUsers = dto.data?.user?.unlockChatUsers ?: emptyList(),
            isRegistrationCompleted = dto.data?.user?.isRegistrationCompleted ?: false,
            profileUrl = dto.data?.user?.profileUrl ?: APP_EMPTY_STRING,
            receivedInterestUsers = dto.data?.user?.receivedInterestUsers ?: emptyList(),
            sentInterestUsers = dto.data?.user?.sentInterestUsers ?: emptyList(),
            shortListedUsers = dto.data?.user?.shortListedUsers ?: emptyList(),
            subscription = mapToVerifyOtpSubscriptionDomainModel(dto.data?.subscription ?: SubscriptionDto()),
            needsUpdateProfile = dto.data?.user?.needsUpdateProfile ?: false

        )
    }

    fun mapToVerifyOtpSubscriptionDomainModel(dto: SubscriptionDto): SubscriptionDomain {
        return SubscriptionDomain(
            id = dto._id ?: APP_EMPTY_STRING,
            benefits = mapToVerifyOtpSubscriptionBenefitsDomainModel(dto.benefits ?: BenefitsDto()) ,
            subscriptionStatus = dto.subscriptionStatus ?: APP_EMPTY_STRING,
            )
    }

    fun mapToVerifyOtpSubscriptionBenefitsDomainModel(dto: BenefitsDto): BenefitsDomain {
        return BenefitsDomain(
            id = dto._id ?: APP_EMPTY_STRING,
            chatProfiles = dto.chatProfiles ?: 0 ,
            matchMakingReport = dto.matchMakingReport ?: 0,

            )
    }


}