package com.companion.astrodating.ui.purchasePlans.data.dto

data class CreateInAppSubscriptionDto(
    val `data`: CreateInAppSubscriptionDtoData? = null,
    val statusCode: Int? = null,
    val message: String? = null
)
data class CreateInAppSubscriptionDtoData(
    val subscription: SubscriptionDtoEntity? = null
)
data class SubscriptionDtoEntity(
    val _id: String? = null,
    val subscribedPlan: String? = null
)
