package com.companion.astrodating.ui.home.data.dto

data class UpdateLocationDto(
    val `data`: UpdateLocationDtoData? = null,
    val message: String? = null,
    val statusCode: Int? = null
)

data class UpdateLocationDtoData(
    val latitude: Double? = null,
    val longitude: Double? = null
)