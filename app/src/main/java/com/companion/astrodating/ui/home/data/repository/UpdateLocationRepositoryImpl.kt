package com.companion.astrodating.ui.home.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.home.data.dto.UpdateLocationDto
import com.companion.astrodating.ui.home.data.mapper.UpdateLocationMapper
import com.companion.astrodating.ui.home.domain.model.UpdateLocationDomain
import com.companion.astrodating.ui.home.domain.repository.IUpdateLocationRepository
import com.companion.astrodating.ui.home.data.requestData.updateLocationRequestData
import com.google.gson.Gson
import javax.inject.Inject

class UpdateLocationRepositoryImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: UpdateLocationMapper
) : IUpdateLocationRepository {
    override suspend fun getUpdateLocationDetails(
        token: String,
        requestData: updateLocationRequestData
    ): ApiResult<UpdateLocationDomain> {
        val result = api.updateLocation(token, requestData)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), UpdateLocationDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message ?: "Something went wrong")
        }
    }
}