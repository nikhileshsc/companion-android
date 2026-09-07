package com.companion.astrodating.ui.chat.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.chat.data.dto.canSendCallDto
import com.companion.astrodating.ui.chat.data.mapper.canSendCallMapper
import com.companion.astrodating.ui.chat.domain.model.canSendCallDomain
import com.companion.astrodating.ui.chat.domain.repository.canSendCallRepository
import com.google.gson.Gson
import javax.inject.Inject

class canSendCallRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: canSendCallMapper
) : canSendCallRepository{
    override suspend fun canSendCall(token: String): ApiResult<canSendCallDomain> {
        val result = api.canSendCall(token)
        return if(result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse = Gson().fromJson(
                result.errorBody()!!.charStream(),
                canSendCallDto::class.java
            )
            ApiResult.Error(result.code(), errorResponse.message!!)
        }
    }
}