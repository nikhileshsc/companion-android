package com.companion.astrodating.ui.purchasePlans.domain.model

data class UpdateBenefitsDomainDetails(
    val `data`: Data?,
    val message: String,
    val statusCode: Int
) {

    data class Data(
        val subscription: Subscription,
        val unlockChatUsers: List<String>
    ) {
        data class Subscription(
            val id: String,
            val benefits: Benefits,
            val subscriptionStatus: String,
            val updatedAt: String
        )

        data class Benefits(
            val id: String,
            val chatProfiles: Int,
            val matchMakingReport: Int
        )
    }
}
