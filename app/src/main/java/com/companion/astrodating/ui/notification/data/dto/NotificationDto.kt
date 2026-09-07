package com.companion.astrodating.ui.notification.data.dto

data class NotificationDto(
    val `data`: NotificationDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class NotificationDtoData(
    val notifications: List<NotificationDtoEntity>? = null,
    val pageNumber: Int? = null,
    val perPage: Int? = null,
    val totalCount: Int? = null
)
data class NotificationDtoEntity(
    val __v: Int? = null,
    val _id: String? = null,
    val body: String? = null,
    val createdAt: String? = null,
    val imageUrl: String? = null,
    val isActive: Boolean? = null,
    val isGlobal: Boolean? = null,
    val modelPath: String? = null,
    val notificationNo: String? = null,
    val notificationType: String? = null,
    val title: String? = null,
    val updatedAt: String? = null,
    val user: String? = null,
    val userType: String? = null
)