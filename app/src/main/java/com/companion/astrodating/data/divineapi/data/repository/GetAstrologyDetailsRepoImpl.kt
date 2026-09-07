package com.companion.astrodating.data.divineapi.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.api.DivineApi
import com.companion.astrodating.data.divineapi.data.dto.AstrologyDetailsDto
import com.companion.astrodating.data.divineapi.data.mapper.DivineApiGsonProvider
import com.companion.astrodating.data.divineapi.data.mapper.GetAstrologyDetailsMapper
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.AstrologyDomainDetails
import com.companion.astrodating.data.divineapi.domain.repository.IGetAstrologyDetailsRepository
import javax.inject.Inject

class GetAstrologyDetailsRepoImpl @Inject constructor(
    private val api: DivineApi,
    private val mapper: GetAstrologyDetailsMapper
) : IGetAstrologyDetailsRepository {

    override suspend fun getAstrologyDetails(
        token: String,
        ashtakootMilanRequestData: AshtakootMilanRequestData
    ): ApiResult<AstrologyDomainDetails> {
        val result = api.getAstrologyDetailsApi(token, ashtakootMilanRequestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse = DivineApiGsonProvider.create()
                .fromJson(result.errorBody()!!.charStream(), AstrologyDetailsDto::class.java)
            ApiResult.Error(errorResponse.success!!, mapper.mapToMsgDomainModel(errorResponse.msg))
        }
    }
}
