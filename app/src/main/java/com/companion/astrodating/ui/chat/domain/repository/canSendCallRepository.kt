package com.companion.astrodating.ui.chat.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.chat.domain.model.canSendCallDomain

interface canSendCallRepository {
    suspend fun canSendCall(
        token: String
    ): ApiResult<canSendCallDomain>
}