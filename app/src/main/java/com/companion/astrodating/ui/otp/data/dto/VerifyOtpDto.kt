package com.companion.astrodating.ui.otp.data.dto

data class VerifyOtpDto(
    val `data`: VerifyOtpDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class VerifyOtpDtoData(
    val token: String? = null,
    val user: UserVerifyOtpDto? = null,
    val subscription: SubscriptionDto? = null,
)

data class UserVerifyOtpDto(
    val agoraChatLoginDetails: AgoraChatLoginDetailsDto? = null,
    val blockedUsers: List<String>? = null,
    val declinedUserInterests: List<String>? = null,
    val unlockChatUsers: List<String>? = null,
    val isRegistrationCompleted: Boolean? = null,
    val profileUrl: String? = null,
    val receivedInterestUsers: List<String>? = null,
    val sentInterestUsers: List<String>? = null,
    val shortListedUsers: List<String>? = null,
    val needsUpdateProfile: Boolean? = null
)

data class AgoraChatLoginDetailsDto(
    val chatToken: String? = null,
    val userName: String? = null
)

data class SubscriptionDto(
    val _id: String? = null,
    val benefits: BenefitsDto? = null,
    val subscriptionStatus: String? = null
)

data class BenefitsDto(
    val _id: String? = null,
    val chatProfiles: Int? = null,
    val matchMakingReport: Int? = null
)
