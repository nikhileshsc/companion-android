package com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.dto.GetAboutDetailsDto
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.mapper.GetAboutDetailsMapper
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.requestData.UpdateAboutDetailsRequestData
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.domain.model.GetAboutDetailsDomain
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.domain.repository.IGetAboutDetailsRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetAboutDetailsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetAboutDetailsMapper
) : IGetAboutDetailsRepository {


    override suspend fun getAboutDetails(token: String): ApiResult<GetAboutDetailsDomain> {
        val result = api.getAboutDetails(token)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetAboutDetailsDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }

    override suspend fun updateAboutDetails(
        token: String,
        requestData: UpdateAboutDetailsRequestData
    ): ApiResult<GetAboutDetailsDomain> {
        val result = api.updateAboutDetails(token, requestData)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetAboutDetailsDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}