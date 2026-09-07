package com.companion.astrodating.ui.notification.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.notification.domain.model.NotificationDomainDetails

interface IGetNotificationRepository {

    suspend fun getNotificationDetails(
        token: String,
        pageNumber: Int,
        perPage: Int
    ): ApiResult<NotificationDomainDetails>
}