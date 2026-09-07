package com.companion.astrodating.ui.interests.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.interests.data.requestData.InterestRequestData
import com.companion.astrodating.ui.interests.domain.model.GetInterestsDomain
import com.companion.astrodating.ui.interests.domain.model.UpdateInterestDomain

interface IInterestsRepository {

    suspend fun updateInterest(token:String,requestData: InterestRequestData): ApiResult<UpdateInterestDomain>

    suspend fun getInterestByType(token:String,interestType:String): ApiResult<GetInterestsDomain>

}