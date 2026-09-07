package com.companion.astrodating.ui.purchasePlans.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.purchasePlans.data.requestData.CreateInAppSubscriptionRequestData
import com.companion.astrodating.ui.purchasePlans.data.requestData.VerifyInAppSubscriptionRequestData
import com.companion.astrodating.ui.purchasePlans.domain.model.CreateInAppSubscriptionDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.model.GetPlanDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.model.VerifyInAppSubscriptionDomainDetails

interface IGetPurchasePlansRepository {

    suspend fun getPlanDetails(token: String): ApiResult<GetPlanDomainDetails>
    suspend fun createInAppSubscriptionDetails(token: String,requestData: CreateInAppSubscriptionRequestData): ApiResult<CreateInAppSubscriptionDomainDetails>
    suspend fun verifyInAppSubscriptionDetails(token: String,requestData: VerifyInAppSubscriptionRequestData): ApiResult<VerifyInAppSubscriptionDomainDetails>
}