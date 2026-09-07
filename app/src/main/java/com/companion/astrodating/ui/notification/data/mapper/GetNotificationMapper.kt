package com.companion.astrodating.ui.notification.data.mapper

import com.companion.astrodating.ui.notification.data.dto.NotificationDto
import com.companion.astrodating.ui.notification.data.dto.NotificationDtoEntity
import com.companion.astrodating.ui.notification.domain.model.NotificationDomainDetails
import com.companion.astrodating.ui.notification.domain.model.NotificationDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetNotificationMapper @Inject constructor() {

    fun mapToDomainModel(dto: NotificationDto): NotificationDomainDetails {
        return NotificationDomainDetails(
            notifications = toDomainList(dto.data?.notifications ?: emptyList()),
            pageNumber = dto.data?.pageNumber ?: 0,
            perPage = dto.data?.perPage ?: 0,
            totalCount = dto.data?.totalCount ?: 0
        )
    }

    private fun toDomainList(list: List<NotificationDtoEntity>): List<NotificationDomainEntity> {
        return list.map {
            NotificationDomainEntity(
                id = it._id ?: APP_EMPTY_STRING,
                body = it.body ?: APP_EMPTY_STRING,
                imageDrawable = 0,
                notificationNo = it.notificationNo ?: APP_EMPTY_STRING,
                notificationType = it.notificationType ?: APP_EMPTY_STRING,
                title = it.title ?: APP_EMPTY_STRING,
                user = it.user ?: APP_EMPTY_STRING,
                userType = it.userType ?: APP_EMPTY_STRING
            )
        }
    }


}