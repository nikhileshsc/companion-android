package com.companion.astrodating.ui.message.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.message.data.dto.GetAgoraTokenDto
import com.companion.astrodating.ui.message.data.mapper.GetAgoraTokenDetailsMapper
import com.companion.astrodating.ui.message.data.repository.IGetAgoraTokenDetailsRepository
import com.companion.astrodating.ui.message.domain.model.GetAgoraTokenDomainDetails
import com.google.gson.Gson
import javax.inject.Inject

class GetAgoraTokenDetailsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetAgoraTokenDetailsMapper
) : IGetAgoraTokenDetailsRepository {

    override suspend fun getAgoraTokenDetails(token: String): ApiResult<GetAgoraTokenDomainDetails> {
        val result = api.getAgoraTokenDetails(token)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetAgoraTokenDto::class.java)
            ApiResult.Error(result.code(), errorResponse.message!!)
        }
    }
}