package com.companion.astrodating.ui.chat.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.chat.domain.model.sendMessagePushNotificationDomain
import com.companion.astrodating.ui.chat.data.requestData.sendMessagePushNotificationData

interface sendMesssagePushNotificationRepository {
    suspend fun sendMessagePushNotification(
        token: String,
        requestData: sendMessagePushNotificationData
    ): ApiResult<sendMessagePushNotificationDomain>
}