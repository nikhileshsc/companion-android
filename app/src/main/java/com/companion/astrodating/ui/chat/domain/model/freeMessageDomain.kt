package com.companion.astrodating.ui.chat.domain.model

import com.companion.astrodating.ui.purchasePlans.domain.model.UpdateBenefitsDomainDetails.Data
import com.companion.astrodating.ui.purchasePlans.domain.model.UpdateBenefitsDomainDetails.Data.Subscription

data class freeMessageDomain (
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val allowed: Boolean,
        val message: String
    )
}