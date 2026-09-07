package com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.requestData.UpdateBasicDetailsRequestData
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.GetBasicDetailsDomain

interface IGetBasicDetailsRepository {

    suspend fun getBasicDetails(token: String) : ApiResult<GetBasicDetailsDomain>
    suspend fun updateBasicDetails(token: String, requestData: UpdateBasicDetailsRequestData) : ApiResult<GetBasicDetailsDomain>
}