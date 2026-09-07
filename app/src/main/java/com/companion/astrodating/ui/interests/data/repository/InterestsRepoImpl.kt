package com.companion.astrodating.ui.interests.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.interests.data.dto.GetInterestDto
import com.companion.astrodating.ui.interests.data.dto.UpdateInterestDto
import com.companion.astrodating.ui.interests.data.mapper.InterestsMapper
import com.companion.astrodating.ui.interests.data.requestData.InterestRequestData
import com.companion.astrodating.ui.interests.domain.model.GetInterestsDomain
import com.companion.astrodating.ui.interests.domain.model.UpdateInterestDomain
import com.companion.astrodating.ui.interests.domain.repository.IInterestsRepository
import com.google.gson.Gson
import javax.inject.Inject

class InterestsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: InterestsMapper
) : IInterestsRepository {

    override suspend fun updateInterest(
        token: String,
        requestData: InterestRequestData
    ): ApiResult<UpdateInterestDomain> {
        val result = api.updateInterest(token,requestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToUpdateInterestsDomainModel(result.body()!!))
        } else {
            val errorResponse = Gson().fromJson(result.errorBody()!!.charStream(), UpdateInterestDto::class.java)
            ApiResult.Error(result.code(), errorResponse.message)
        }
    }

    override suspend fun getInterestByType(
        token: String,
        interestType: String
    ): ApiResult<GetInterestsDomain> {
        val result = api.getInterestByType(token,interestType)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToGetInterestsDomainModel(result.body()!!))
        } else {
            val errorResponse = Gson().fromJson(result.errorBody()!!.charStream(), GetInterestDto::class.java)
            ApiResult.Error(result.code(), errorResponse.message)
        }
    }
}