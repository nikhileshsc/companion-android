package com.companion.astrodating.ui.chat.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.chat.data.requestData.messageLimitRequestData
import com.companion.astrodating.ui.chat.data.requestData.sendMessagePushNotificationData
import com.companion.astrodating.ui.chat.domain.model.freeMessageDomain
import com.companion.astrodating.ui.chat.domain.model.sendMessagePushNotificationDomain

interface freeMessageLimitRepository {
        suspend fun checkfreemessagelimit(
            token: String,
            requestData: messageLimitRequestData
        ): ApiResult<freeMessageDomain>

}
