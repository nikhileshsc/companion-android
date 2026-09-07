package com.companion.astrodating.ui.profile.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.profile.domain.model.LogoutDomain

interface IProfileRepository {

    suspend fun logoutUser(token:String): ApiResult<LogoutDomain>

}