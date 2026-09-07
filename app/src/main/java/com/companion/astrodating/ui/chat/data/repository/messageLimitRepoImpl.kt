package com.companion.astrodating.ui.chat.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.chat.data.dto.messageLimitDto
import com.companion.astrodating.ui.chat.data.mapper.freeMessageMapper
import com.companion.astrodating.ui.chat.data.requestData.messageLimitRequestData
import com.companion.astrodating.ui.chat.domain.model.freeMessageDomain
import com.companion.astrodating.ui.chat.domain.repository.freeMessageLimitRepository
import com.google.gson.Gson
import javax.inject.Inject

class messageLimitRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: freeMessageMapper
    ) : freeMessageLimitRepository {
    override suspend fun checkfreemessagelimit(
        requestData: messageLimitRequestData
    ): ApiResult<freeMessageDomain> {
        var result = api.checkFreeMessages(requestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse = Gson().fromJson(
                result.errorBody()!!.charStream(),
                messageLimitDto::class.java
            )
            ApiResult.Error(result.code(), errorResponse.message!!)
        }

    }
}