package com.companion.astrodating.ui.profileDetails.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.profileDetails.domain.model.GetUserDetailsDomain

interface IGetUserDetailsRepository {

    suspend fun getHomeUserDetails(
        token: String,
        userId: String
    ): ApiResult<GetUserDetailsDomain>
}