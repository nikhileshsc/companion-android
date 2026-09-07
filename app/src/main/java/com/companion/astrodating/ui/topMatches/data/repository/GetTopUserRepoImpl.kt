package com.companion.astrodating.ui.topMatches.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.home.data.dto.GetHomeUsersDto
import com.companion.astrodating.ui.home.data.mapper.GetHomeUsersMapper
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomain
import com.companion.astrodating.ui.topMatches.domain.repository.IGetTopUserRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetTopUserRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetHomeUsersMapper
) : IGetTopUserRepository {

    override suspend fun getTopUserDetails(
        token: String,
        pageNumber: Int,
        perPage: Int
    ): ApiResult<GetHomeUserDomain> {
        val result = api.getTopUsers(token,pageNumber, perPage)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetHomeUsersDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}