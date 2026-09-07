package com.companion.astrodating.ui.registration.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.registration.data.dto.AddUpdateMandatoryDetailsDto
import com.companion.astrodating.ui.registration.data.mapper.AddUpdateMandatoryDetailsMapper
import com.companion.astrodating.ui.registration.data.requestData.AddUpdateMandatoryDetailsRequestData
import com.companion.astrodating.ui.registration.domain.model.AddUpdateMandatoryDetailsDomain
import com.companion.astrodating.ui.registration.domain.repository.IAddUpdateMandatoryDetailsRepository
import com.google.gson.Gson
import javax.inject.Inject

class AddUpdateMandatoryDetailsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: AddUpdateMandatoryDetailsMapper
) : IAddUpdateMandatoryDetailsRepository {

    override suspend fun addUpdateMandatoryDetails(
        token: String,
        requestData: AddUpdateMandatoryDetailsRequestData
    ): ApiResult<AddUpdateMandatoryDetailsDomain> {
        val result = api.addUpdateMandatoryDetails(token, requestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse =
                Gson().fromJson(
                    result.errorBody()!!.charStream(),
                    AddUpdateMandatoryDetailsDto::class.java
                )
            ApiResult.Error(result.code(), errorResponse.message)
        }
    }
}