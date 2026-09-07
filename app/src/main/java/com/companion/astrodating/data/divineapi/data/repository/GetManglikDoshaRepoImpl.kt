package com.companion.astrodating.data.divineapi.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.api.DivineApi
import com.companion.astrodating.data.divineapi.data.dto.ManglikDoshaDto
import com.companion.astrodating.data.divineapi.data.mapper.DivineApiGsonProvider
import com.companion.astrodating.data.divineapi.data.mapper.GetManglikDoshaMapper
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.ManglikDoshaDomainDetails
import com.companion.astrodating.data.divineapi.domain.repository.IGetManglikDoshaRepository
import javax.inject.Inject

class GetManglikDoshaRepoImpl @Inject constructor(
    private val api: DivineApi,
    private val mapper: GetManglikDoshaMapper
) : IGetManglikDoshaRepository {
    override suspend fun getManglikDoshaDetails(
        token: String,
        ashtakootMilanRequestData: AshtakootMilanRequestData
    ): ApiResult<ManglikDoshaDomainDetails> {
        val result = api.getManglikDoshaApi(token, ashtakootMilanRequestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse = DivineApiGsonProvider.create()
                .fromJson(result.errorBody()!!.charStream(), ManglikDoshaDto::class.java)
            if (errorResponse.success!! == 3){
                ApiResult.Error(errorResponse.success,"Invalid divine authorization token!")
            }else{
                ApiResult.Error(errorResponse.success, mapper.mapToMsgDomainModel(errorResponse.msg))
            }

        }
    }
}
