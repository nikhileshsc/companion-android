package com.companion.astrodating.data.divineapi.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.AstrologyDomainDetails

interface IGetAstrologyDetailsRepository {
    suspend fun getAstrologyDetails(
        token: String,
        ashtakootMilanRequestData: AshtakootMilanRequestData
    ): ApiResult<AstrologyDomainDetails>
}