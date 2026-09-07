package com.companion.astrodating.ui.profile.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.profile.data.dto.LogoutDto
import com.companion.astrodating.ui.profile.data.mapper.ProfileDtoMapper
import com.companion.astrodating.ui.profile.domain.model.LogoutDomain
import com.companion.astrodating.ui.profile.domain.repository.IProfileRepository
import com.google.gson.Gson
import javax.inject.Inject

class ProfileRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: ProfileDtoMapper
) : IProfileRepository {

    override suspend fun logoutUser(token: String): ApiResult<LogoutDomain> {
        val result = api.logoutUser(token)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToLogoutDomain(result.body()!!))
        } else {
            val errorResponse = Gson().fromJson(result.errorBody()!!.charStream(), LogoutDto::class.java)
            ApiResult.Error(result.code(), errorResponse.message)
        }
    }
}