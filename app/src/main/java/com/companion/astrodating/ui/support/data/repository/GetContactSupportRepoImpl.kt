package com.companion.astrodating.ui.support.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.support.data.dto.ContactSupportDto
import com.companion.astrodating.ui.support.data.mapper.GetContactSupportMapper
import com.companion.astrodating.ui.support.domain.model.ContactSupportDomainDetails
import com.companion.astrodating.ui.support.domain.repository.IGetContactSupportRepository
import com.companion.astrodating.ui.support.requestData.ContactSupportRequestData
import com.google.gson.Gson
import javax.inject.Inject

class GetContactSupportRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetContactSupportMapper
) : IGetContactSupportRepository {
    override suspend fun createNewTicketDetails(
        token: String,
        requestData: ContactSupportRequestData
    ): ApiResult<ContactSupportDomainDetails> {
        val result = api.createNewTicket(token,requestData)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), ContactSupportDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}