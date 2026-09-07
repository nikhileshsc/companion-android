package com.companion.astrodating.ui.chat.data.mapper

import com.companion.astrodating.ui.chat.data.dto.SendMessagePushNotificationDto
import com.companion.astrodating.ui.chat.domain.model.sendMessagePushNotificationDomain
import javax.inject.Inject

class sendMessagePushNotificationMapper @Inject constructor() {
    fun mapToDomainModel(dto: SendMessagePushNotificationDto): sendMessagePushNotificationDomain{
        return sendMessagePushNotificationDomain(
            message = dto.message ?: "",
            statusCode = dto.statusCode ?: 0
        )
    }
}