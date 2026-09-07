package com.companion.astrodating.ui.purchasePlans.data.requestData

data class VerifyInAppSubscriptionRequestData(
    val androidInAppPurchaseRes: AndroidInAppPurchaseRes? = null,
    val iosInAppPurchaseRes: String = "",
    val iosInAppVerifyReceiptRes: String = "",
    val subscribedPlan: String? = null,
    val subscriptionId: String? = null,
    val type: String = "restore",
    val purchaseToken: String? = null,
    val productId: String? = null
)

data class AndroidInAppPurchaseRes(
    val purchaseObject: String = ""
)
