package com.companion.astrodating.ui.registration.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.registration.data.requestData.AddUpdateMandatoryDetailsRequestData
import com.companion.astrodating.ui.registration.domain.model.AddUpdateMandatoryDetailsDomain

interface IAddUpdateMandatoryDetailsRepository {

    suspend fun addUpdateMandatoryDetails(token: String, requestData: AddUpdateMandatoryDetailsRequestData) : ApiResult<AddUpdateMandatoryDetailsDomain>
}