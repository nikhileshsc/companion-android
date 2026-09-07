package com.companion.astrodating.data.divineapi.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.ManglikDoshaDomainDetails

interface IGetManglikDoshaRepository {

    suspend fun getManglikDoshaDetails(
        token: String,
        ashtakootMilanRequestData: AshtakootMilanRequestData
    ): ApiResult<ManglikDoshaDomainDetails>
}