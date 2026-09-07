package com.companion.astrodating.ui.home.data.repository

import android.util.Log
import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.home.data.dto.GetHomeUsersDto
import com.companion.astrodating.ui.home.data.dto.UpdateLocationDto
import com.companion.astrodating.ui.home.data.dto.UpdateOnlineStatusDto
import com.companion.astrodating.ui.home.data.mapper.GetHomeUsersMapper
import com.companion.astrodating.ui.home.data.mapper.UpdateLocationMapper
import com.companion.astrodating.ui.home.data.mapper.UpdateOnlineStatusMapper
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomain
import com.companion.astrodating.ui.home.domain.model.UpdateOnlineStatusDomain
import com.companion.astrodating.ui.home.domain.repository.IGetHomeUserRepository
import com.companion.astrodating.ui.home.data.requestData.updateLocationRequestData
import com.companion.astrodating.ui.home.data.requestData.updateOnlineStatusRequestData
import com.companion.astrodating.util.TAG
import com.google.gson.Gson
import javax.inject.Inject

class GetHomeUserRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetHomeUsersMapper,
    private val mapper2: UpdateOnlineStatusMapper,
) : IGetHomeUserRepository {
    override suspend fun getHomeUserDetails(
        token: String,
        searchText: String,
        minHeight: Double,
        maxHeight: Double,
        minAge: Int,
        maxAge: Int,
        education: ArrayList<String>,
        profession: ArrayList<String>,
        religion: ArrayList<String>,
        country: ArrayList<String>,
        city: ArrayList<String>,
        status: ArrayList<String>,
        community: ArrayList<String>,
        pageNumber: Int,
        perPage: Int
    ): ApiResult<GetHomeUserDomain> {
        val result = api.getHomeUsers(token,searchText,minHeight, maxHeight, minAge, maxAge, education, profession, religion, status, country,city,community,pageNumber, perPage)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetHomeUsersDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }

    override suspend fun getOnlineStatusDetails(
        token: String,
        requestData: updateOnlineStatusRequestData
    ): ApiResult<UpdateOnlineStatusDomain> {
        val result = api.updateOnlineStatus(token, requestData)
        return if (result.isSuccessful){
            ApiResult.Success(mapper2.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), UpdateOnlineStatusDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message ?: "Something went wrong")
        }
    }

//    override suspend fun getUpdateLocationDetails(
//        token: String,
//        requestData: updateLocationRequestData
//    ): ApiResult<UpdateLocationDomain> {
//        val result = api.updateLocation(token, requestData)
//        return if (result.isSuccessful){
//            ApiResult.Success(mapper3.mapToDomainModel(result.body()!!))
//        }else{
//            val errorResponse =
//                Gson().fromJson(result.errorBody()!!.charStream(), UpdateLocationDto::class.java)
//            ApiResult.Error(result.code(),errorResponse.message ?: "Something went wrong")
//        }
//    }
}