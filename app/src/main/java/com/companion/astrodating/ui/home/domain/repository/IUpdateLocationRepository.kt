package com.companion.astrodating.ui.home.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.home.domain.model.UpdateLocationDomain
import com.companion.astrodating.ui.home.data.requestData.updateLocationRequestData

interface IUpdateLocationRepository {
    suspend fun getUpdateLocationDetails(
        token: String,
        requestData: updateLocationRequestData
    ): ApiResult<UpdateLocationDomain>
}