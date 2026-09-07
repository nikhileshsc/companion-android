package com.companion.astrodating.ui.registration.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.registration.domain.model.GetMandatoryDetailsDomain

interface IGetMandatoryDetailsRepository {

    suspend fun getMandatoryDetails(token: String) : ApiResult<GetMandatoryDetailsDomain>
}