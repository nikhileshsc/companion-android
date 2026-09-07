package com.companion.astrodating.ui.home.data.dto

data class UpdateOnlineStatusDto(
    val `data`: UpdateOnlineStatusDtoData? = null,
    val message: String? = null,
    val statusCode: Int? = null
)

data class UpdateOnlineStatusDtoData(
    val isOnline: Boolean? = null
)