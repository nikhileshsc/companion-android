package com.companion.astrodating.ui.onlineusers.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.onlineusers.data.dto.GetOnlineUsersDto
import com.companion.astrodating.ui.onlineusers.data.mapper.GetOnlineUsersMapper
import com.companion.astrodating.ui.onlineusers.domain.model.GetOnlineUsersDomain
import com.companion.astrodating.ui.onlineusers.domain.repository.IGetOnlineUsersRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetOnlineUsersRepoImpl @Inject constructor(
private val api: CompanionApi,
private val mapper: GetOnlineUsersMapper
) : IGetOnlineUsersRepository {
    override suspend fun GetOnlineUsersDetails(
        token: String,
        pageNumber: Int,
        perPage: Int,
        latitude: Double,
        longitude: Double
    ): ApiResult<GetOnlineUsersDomain> {
        val result = api.getOnlineUsers(token, latitude, longitude)
        return if(result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse = Gson().fromJson(result.errorBody()!!.charStream(), GetOnlineUsersDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}