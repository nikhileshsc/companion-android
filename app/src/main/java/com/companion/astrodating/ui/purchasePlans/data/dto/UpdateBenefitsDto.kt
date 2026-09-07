package com.companion.astrodating.ui.purchasePlans.data.dto

data class UpdateBenefitsDto(
    val `data`: Data? = null,
    val message: String? = null,
    val statusCode: Int? = null
) {

    data class Data(
        val subscription: Subscription? = null,
        val unlockChatUsers: List<String>? = null
    ) {
        data class Subscription(
            val _id: String? = null,
            val benefits: Benefits? = null,
            val subscriptionStatus: String? = null,
            val updatedAt: String? = null
        )

        data class Benefits(
            val _id: String? = null,
            val chatProfiles: Int? = null,
            val matchMakingReport: Int? = null
        )
    }
}