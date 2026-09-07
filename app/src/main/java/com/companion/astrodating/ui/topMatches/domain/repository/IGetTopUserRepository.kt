package com.companion.astrodating.ui.topMatches.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomain

interface IGetTopUserRepository {

    suspend fun getTopUserDetails(
        token: String,
        pageNumber: Int,
        perPage: Int
    ): ApiResult<GetHomeUserDomain>
}