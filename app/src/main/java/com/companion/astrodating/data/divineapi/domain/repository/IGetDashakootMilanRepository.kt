package com.companion.astrodating.data.divineapi.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.DashakootMilanDomainDetails

interface IGetDashakootMilanRepository {

    suspend fun getDashakootMilanDetails(
        token: String,
        ashtakootMilanRequestData: AshtakootMilanRequestData
    ): ApiResult<DashakootMilanDomainDetails>
}