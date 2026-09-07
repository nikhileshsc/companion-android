package com.companion.astrodating.ui.purchasePlans.data.requestData

data class CreateInAppSubscriptionRequestData(
    val planPurchasedFrom: String = "android",
    val promoCode: String,
    val promocodeDiscount: Int,
    val subscribedPlan: String,
    val subscriptionId: String,
    val type: String
)