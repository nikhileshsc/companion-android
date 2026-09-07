package com.companion.astrodating.ui.onlineusers.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.onlineusers.domain.model.GetOnlineUsersDomain

interface IGetOnlineUsersRepository {

    suspend fun GetOnlineUsersDetails(
        token: String,
        pageNumber: Int,
        perPage: Int,
        latitude: Double,
        longitude: Double
    ): ApiResult<GetOnlineUsersDomain>
}