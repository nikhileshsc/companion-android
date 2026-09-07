package com.companion.astrodating.ui.purchasePlans.data.dto

data class GetPlanDtoDetails(
    val `data`: GetPlanDtoData? = null,
    val statusCode: Int? = null,
    val message: String
)

data class GetPlanDtoData(
    val currentSubcription: CurrentSubscriptionDtoEntity? = null,
    val plans: List<PlanDtoEntity>? = null
)

data class CurrentSubscriptionDtoEntity(
    val _id: String? = null,
    val androidInAppPurchaseRes: AndroidInAppPurchaseResDtoEntity? = null,
    val planName: String? = null,
    val subscribedPlan: String? = null,
    val subscriptionStatus: String? = null
)

data class AndroidInAppPurchaseResDtoEntity(
    val acknowledged: Boolean? = null,
    val autoRenewing: Boolean? = null,
    val orderId: String? = null,
    val packageName: String? = null,
    val productId: String? = null,
    val purchaseState: Int? = null,
    val purchaseTime: Long? = null,
    val purchaseToken: String? = null,
    val quantity: Int? = null
)

data class PlanDtoEntity(
    val _id: String? = null,
    val androidGroupId: String? = null,
    val androidSubscriptionId: String? = null,
    val benefits: List<BenefitDtoEntity>? = null,
    val benefitsDescriptions: List<String>? = null,
    val createdAt: String? = null,
    val duration: String? = null,
    val iOsSubscriptionId: String? = null,
    val isActive: Boolean? = null,
    val isMostPopular: Boolean? = null,
    val order: Int? = null,
    val planDescription: String? = null,
    val planName: String? = null,
    val price: Int? = null,
    val status: String? = null,
    val subscriptionStatus: String? = null,
    val type: String? = null,
    val updatedAt: String? = null
)

data class BenefitDtoEntity(
    val _id: String? = null,
    val count: Int? = null,
    val title: String? = null,
    val updateKey: String? = null
)


data class aa(
    val `data`: Data? = null,
    val statusCode: Int? = null
) {
    data class Data(
        val currentSubcription: CurrentSubcription? = null,
        val plans: List<Plan?>? = null
    ) {
        data class CurrentSubcription(
            val _id: String? = null,
            val androidInAppPurchaseRes: AndroidInAppPurchaseRes? = null,
            val planName: String? = null,
            val subscribedPlan: String? = null,
            val subscriptionStatus: String? = null
        ) {
            data class AndroidInAppPurchaseRes(
                val acknowledged: Boolean? = null,
                val autoRenewing: Boolean? = null,
                val orderId: String? = null,
                val packageName: String? = null,
                val productId: String? = null,
                val purchaseState: Int? = null,
                val purchaseTime: Long? = null,
                val purchaseToken: String? = null,
                val quantity: Int? = null
            )
        }

        data class Plan(
            val _id: String? = null,
            val androidGroupId: String? = null,
            val androidSubscriptionId: String? = null,
            val benefits: List<Benefit?>? = null,
            val benefitsDescriptions: List<String?>? = null,
            val createdAt: String? = null,
            val duration: String? = null,
            val iOsSubscriptionId: String? = null,
            val isActive: Boolean? = null,
            val isMostPopular: Boolean? = null,
            val order: Int? = null,
            val planDescription: String? = null,
            val planName: String? = null,
            val price: Int? = null,
            val status: String? = null,
            val subscriptionStatus: String? = null,
            val type: String? = null,
            val updatedAt: String? = null
        ) {
            data class Benefit(
                val _id: String? = null,
                val count: Int? = null,
                val title: String? = null,
                val updateKey: String? = null
            )
        }
    }
}