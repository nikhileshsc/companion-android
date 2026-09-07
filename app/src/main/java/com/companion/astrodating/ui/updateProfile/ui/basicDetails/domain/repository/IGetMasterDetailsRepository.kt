package com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.GetMasterDomainDetails

interface IGetMasterDetailsRepository {

    suspend fun getMasterDataDetails(token: String) : ApiResult<GetMasterDomainDetails>
}