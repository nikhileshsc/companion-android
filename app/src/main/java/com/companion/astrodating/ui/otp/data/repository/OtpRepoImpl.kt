package com.companion.astrodating.ui.otp.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.otp.data.dto.OtpDto
import com.companion.astrodating.ui.otp.data.dto.VerifyOtpDto
import com.companion.astrodating.ui.otp.data.mapper.OtpDtoMapper
import com.companion.astrodating.ui.otp.data.requestData.RequestOtpRequestData
import com.companion.astrodating.ui.otp.data.requestData.VerifyOtpRequestData
import com.companion.astrodating.ui.otp.domain.model.OtpDomain
import com.companion.astrodating.ui.otp.domain.model.VerifyOtpDomain
import com.companion.astrodating.ui.otp.domain.repository.IOtpRepository
import com.google.gson.Gson
import javax.inject.Inject

class OtpRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: OtpDtoMapper
) : IOtpRepository {


    override suspend fun requestOtp(reqData: RequestOtpRequestData, commonAuth: String): ApiResult<OtpDomain> {
        val result = api.requestOtp(commonAuth,reqData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), OtpDto::class.java)
            ApiResult.Error(result.code(), errorResponse.message)
        }
    }

    override suspend fun verifyOtp(
        reqData: VerifyOtpRequestData,
        commonAuth: String
    ): ApiResult<VerifyOtpDomain> {
        val result = api.verifyOtp(commonAuth,reqData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToVerifyOtpDomainModel(result.body()!!))
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), VerifyOtpDto::class.java)
            ApiResult.Error(result.code(), errorResponse.message)
        }
    }
}