package com.companion.astrodating.ui.notification.domain.model

data class NotificationDomainDetails(
    val notifications: List<NotificationDomainEntity>,
    val pageNumber: Int,
    val perPage: Int,
    val totalCount: Int
)

data class NotificationDomainEntity(
    val id: String,
    val body: String,
    val imageDrawable: Int,
    val notificationNo: String,
    val notificationType: String,
    val title: String,
    val user: String,
    val userType: String
)