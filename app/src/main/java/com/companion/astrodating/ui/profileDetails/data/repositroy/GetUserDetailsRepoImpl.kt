package com.companion.astrodating.ui.profileDetails.data.repositroy

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.profileDetails.data.dto.GetUserDetailsDto
import com.companion.astrodating.ui.profileDetails.data.mapper.GetUserDetailsMapper
import com.companion.astrodating.ui.profileDetails.domain.model.GetUserDetailsDomain
import com.companion.astrodating.ui.profileDetails.domain.repository.IGetUserDetailsRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetUserDetailsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetUserDetailsMapper
) : IGetUserDetailsRepository {

    override suspend fun getHomeUserDetails(
        token: String,
        userId: String
    ): ApiResult<GetUserDetailsDomain> {
        val result = api.getUsersDetailsById(token, userId)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetUserDetailsDto::class.java)
            ApiResult.Error(result.code(), errorResponse.message)
        }
    }
}