package com.companion.astrodating.ui.purchasePlans.domain.model

data class GetPlanDomainDetails(
    val currentSubcription: CurrentSubscriptionDomainEntity,
    val plans: List<PlanDomainEntity>
)

data class CurrentSubscriptionDomainEntity(
    val id: String,
    val androidInAppPurchaseRes: AndroidInAppPurchaseResDomainEntity,
    val planName: String,
    val subscribedPlan: String,
    val subscriptionStatus: String
)

data class AndroidInAppPurchaseResDomainEntity(
    val acknowledged: Boolean,
    val autoRenewing: Boolean,
    val orderId: String,
    val packageName: String,
    val productId: String,
    val purchaseState: Int,
    val purchaseTime: Long,
    val purchaseToken: String,
    val quantity: Int
)

data class PlanDomainEntity(
    val id: String,
    val androidGroupId: String,
    val androidSubscriptionId: String,
    val benefits: List<BenefitDomainEntity>,
    val benefitsDescriptions: List<String>,
    val createdAt: String,
    val duration: String,
    val iOsSubscriptionId: String,
    val isActive: Boolean,
    val isMostPopular: Boolean,
    val order: Int,
    val planDescription: String,
    val planName: String,
    val price: Int,
    val status: String,
    val subscriptionStatus: String,
    val type: String,
    val updatedAt: String
)

data class BenefitDomainEntity(
    val id: String,
    val count: Int,
    val title: String,
    val updateKey: String
)
