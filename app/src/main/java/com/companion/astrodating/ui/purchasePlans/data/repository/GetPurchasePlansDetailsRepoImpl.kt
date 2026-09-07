package com.companion.astrodating.ui.purchasePlans.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.purchasePlans.data.dto.CreateInAppSubscriptionDto
import com.companion.astrodating.ui.purchasePlans.data.dto.GetPlanDtoDetails
import com.companion.astrodating.ui.purchasePlans.data.dto.VerifyInAppSubscriptionDto
import com.companion.astrodating.ui.purchasePlans.data.mapper.GetPurchasePlanDetailsMapper
import com.companion.astrodating.ui.purchasePlans.data.requestData.CreateInAppSubscriptionRequestData
import com.companion.astrodating.ui.purchasePlans.data.requestData.VerifyInAppSubscriptionRequestData
import com.companion.astrodating.ui.purchasePlans.domain.model.CreateInAppSubscriptionDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.model.GetPlanDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.model.VerifyInAppSubscriptionDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.repository.IGetPurchasePlansRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetPurchasePlansDetailsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetPurchasePlanDetailsMapper
) : IGetPurchasePlansRepository {
    override suspend fun getPlanDetails(token: String): ApiResult<GetPlanDomainDetails> {
        val result = api.getPurchasePlanDetails(token)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetPlanDtoDetails::class.java)
            ApiResult.Error(result.code(), errorResponse.message)
        }
    }

    override suspend fun createInAppSubscriptionDetails(
        token: String,
        requestData: CreateInAppSubscriptionRequestData
    ): ApiResult<CreateInAppSubscriptionDomainDetails> {
        val result = api.createInAppSubscription(token,requestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToCreateInAppSubscriptionDomainModel(result.body()!!))
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), CreateInAppSubscriptionDto::class.java)
            ApiResult.Error(result.code(), errorResponse.message!!)
        }
    }

    override suspend fun verifyInAppSubscriptionDetails(
        token: String,
        requestData: VerifyInAppSubscriptionRequestData
    ): ApiResult<VerifyInAppSubscriptionDomainDetails> {
        val result = api.verifyInAppSubscription(token,requestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToVerifyInAppSubscriptionDomainModel(result.body()!!))
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), VerifyInAppSubscriptionDto::class.java)
            ApiResult.Error(result.code(), "")
        }
    }
}