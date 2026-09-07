package com.companion.astrodating.ui.purchasePlans.domain.model

data class CreateInAppSubscriptionDomainDetails(
    val subscription: SubscriptionDomainEntity
)
data class SubscriptionDomainEntity(
    val id: String,
    val subscribedPlan: String
)
