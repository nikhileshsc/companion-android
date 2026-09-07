package com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.dto.GetBasicDetailsDto
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.mapper.GetBasicDetailsMapper
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.requestData.UpdateBasicDetailsRequestData
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.GetBasicDetailsDomain
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.repository.IGetBasicDetailsRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetBasicDetailsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetBasicDetailsMapper
) : IGetBasicDetailsRepository {

    override suspend fun getBasicDetails(token: String): ApiResult<GetBasicDetailsDomain> {
        val result = api.getBasicDetails(token)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetBasicDetailsDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }

    override suspend fun updateBasicDetails(
        token: String,
        requestData: UpdateBasicDetailsRequestData
    ): ApiResult<GetBasicDetailsDomain> {
        val result = api.updateBasicDetails(token, requestData)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetBasicDetailsDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}