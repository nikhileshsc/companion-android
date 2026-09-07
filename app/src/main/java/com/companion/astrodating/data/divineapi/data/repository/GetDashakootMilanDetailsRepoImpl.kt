package com.companion.astrodating.data.divineapi.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.api.DivineApi
import com.companion.astrodating.data.divineapi.data.dto.DashakootMilanDto
import com.companion.astrodating.data.divineapi.data.mapper.DivineApiGsonProvider
import com.companion.astrodating.data.divineapi.data.mapper.GetDashakootMilanMapper
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.DashakootMilanDomainDetails
import com.companion.astrodating.data.divineapi.domain.repository.IGetDashakootMilanRepository
import javax.inject.Inject

class GetDashakootMilanDetailsRepoImpl @Inject constructor(
    private val api: DivineApi,
    private val mapper: GetDashakootMilanMapper
) : IGetDashakootMilanRepository {

    override suspend fun getDashakootMilanDetails(
        token: String,
        ashtakootMilanRequestData: AshtakootMilanRequestData
    ): ApiResult<DashakootMilanDomainDetails> {
        val result = api.getDashakootMilanApi(token, ashtakootMilanRequestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse = DivineApiGsonProvider.create()
                .fromJson(result.errorBody()!!.charStream(), DashakootMilanDto::class.java)
            ApiResult.Error(errorResponse.success!!, mapper.mapToMsgDomainModel(errorResponse.msg))
        }
    }
}
