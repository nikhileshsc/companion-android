package com.companion.astrodating.ui.updateProfile.ui.aboutYourself.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.requestData.UpdateAboutDetailsRequestData
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.domain.model.GetAboutDetailsDomain

interface IGetAboutDetailsRepository {

    suspend fun getAboutDetails(token: String) : ApiResult<GetAboutDetailsDomain>
    suspend fun updateAboutDetails(token: String, requestData: UpdateAboutDetailsRequestData) : ApiResult<GetAboutDetailsDomain>
}