package com.companion.astrodating.ui.otp.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.otp.data.requestData.RequestOtpRequestData
import com.companion.astrodating.ui.otp.data.requestData.VerifyOtpRequestData
import com.companion.astrodating.ui.otp.domain.model.OtpDomain
import com.companion.astrodating.ui.otp.domain.model.VerifyOtpDomain

interface IOtpRepository {

    suspend fun requestOtp(reqData: RequestOtpRequestData, commonAuth: String): ApiResult<OtpDomain>

    suspend fun verifyOtp(reqData: VerifyOtpRequestData, commonAuth: String): ApiResult<VerifyOtpDomain>
}