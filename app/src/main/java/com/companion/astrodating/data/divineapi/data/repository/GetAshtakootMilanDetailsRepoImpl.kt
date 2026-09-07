package com.companion.astrodating.data.divineapi.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.api.DivineApi
import com.companion.astrodating.data.divineapi.data.dto.AshtakootMilanDto
import com.companion.astrodating.data.divineapi.data.mapper.DivineApiGsonProvider
import com.companion.astrodating.data.divineapi.data.mapper.GetAshtakootMilanMapper
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.AshtakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.repository.IGetAshtakootMilanRepository
import javax.inject.Inject

class GetAshtakootMilanDetailsRepoImpl @Inject constructor(
    private val api: DivineApi,
    private val mapper: GetAshtakootMilanMapper
) : IGetAshtakootMilanRepository {

    override suspend fun getAshtakootMilanDetails(
        token: String,
        ashtakootMilanRequestData: AshtakootMilanRequestData
    ): ApiResult<AshtakootMilanDomainDetails> {
        val result = api.getAshtakootMilanApi(token, ashtakootMilanRequestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse = DivineApiGsonProvider.create()
                .fromJson(result.errorBody()!!.charStream(), AshtakootMilanDto::class.java)
            if (errorResponse.success!! == 3){
                ApiResult.Error(errorResponse.success,"Invalid divine authorization token!")
            }else{
                ApiResult.Error(errorResponse.success!!, mapper.mapToMsgDomainModel(errorResponse.msg))
            }

        }
    }
}
