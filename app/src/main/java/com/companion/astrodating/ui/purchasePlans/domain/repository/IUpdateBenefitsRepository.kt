package com.companion.astrodating.ui.purchasePlans.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.purchasePlans.domain.model.UpdateBenefitsDomainDetails

interface IUpdateBenefitsRepository {

    suspend fun updateBenefitsDetails(token: String,requestData: UpdateBenefitRequestData): ApiResult<UpdateBenefitsDomainDetails>

}