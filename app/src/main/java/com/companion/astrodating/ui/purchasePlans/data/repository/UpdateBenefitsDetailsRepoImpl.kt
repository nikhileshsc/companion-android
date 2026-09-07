package com.companion.astrodating.ui.purchasePlans.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.purchasePlans.data.dto.CreateInAppSubscriptionDto
import com.companion.astrodating.ui.purchasePlans.data.mapper.UpdateBenefitsDetailsMapper
import com.companion.astrodating.ui.purchasePlans.data.requestData.UpdateBenefitRequestData
import com.companion.astrodating.ui.purchasePlans.domain.model.UpdateBenefitsDomainDetails
import com.companion.astrodating.ui.purchasePlans.domain.repository.IUpdateBenefitsRepository
import com.google.gson.Gson
import javax.inject.Inject

class UpdateBenefitsDetailsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: UpdateBenefitsDetailsMapper
) : IUpdateBenefitsRepository {
    override suspend fun updateBenefitsDetails(
        token: String,
        requestData: UpdateBenefitRequestData
    ): ApiResult<UpdateBenefitsDomainDetails> {
        val result = api.updateBenefitByType(token,requestData)
        val body = result.body()
        return if (result.isSuccessful && body?.statusCode == 200) {
            try {
                val domainModel = mapper.mapToDomainModel(body)
                ApiResult.Success(domainModel)
            }catch (e: Exception) {
                ApiResult.Success(UpdateBenefitsDomainDetails(null, body?.message ?: "", 200))
            }

        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), CreateInAppSubscriptionDto::class.java)
            ApiResult.Error(result.code(), errorResponse.message!!)
        }
    }
}