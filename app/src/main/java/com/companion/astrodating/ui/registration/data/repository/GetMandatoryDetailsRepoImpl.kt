package com.companion.astrodating.ui.registration.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.registration.data.dto.GetMandatoryDetailsDto
import com.companion.astrodating.ui.registration.data.mapper.GetMandatoryDetailsMapper
import com.companion.astrodating.ui.registration.domain.model.GetMandatoryDetailsDomain
import com.companion.astrodating.ui.registration.domain.repository.IGetMandatoryDetailsRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetMandatoryDetailsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetMandatoryDetailsMapper
) : IGetMandatoryDetailsRepository {

    override suspend fun getMandatoryDetails(token: String): ApiResult<GetMandatoryDetailsDomain> {
        val result = api.getMandatoryDetails(token)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetMandatoryDetailsDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}