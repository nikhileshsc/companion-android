package com.companion.astrodating.ui.otp.domain.model



data class VerifyOtpDomain(
    val token: String,
    val agoraChatUserName:String,
    val agoraChatToken:String,
    val blockedUsers: List<String>,
    val declinedUserInterests: List<String>,
    val unlockChatUsers: List<String>,
    val isRegistrationCompleted: Boolean,
    val profileUrl: String,
    val receivedInterestUsers: List<String>,
    val sentInterestUsers: List<String>,
    val shortListedUsers: List<String>,
    val subscription: SubscriptionDomain? = null,
    val needsUpdateProfile: Boolean
)

data class SubscriptionDomain(
    val id: String,
    val benefits: BenefitsDomain,
    val subscriptionStatus: String
)

data class BenefitsDomain(
    val id: String,
    val chatProfiles: Int,
    val matchMakingReport: Int
)
