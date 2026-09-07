package com.companion.astrodating.ui.message.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.message.domain.model.GetAgoraTokenDomainDetails

interface IGetAgoraTokenDetailsRepository {

    suspend fun getAgoraTokenDetails(token: String): ApiResult<GetAgoraTokenDomainDetails>
}