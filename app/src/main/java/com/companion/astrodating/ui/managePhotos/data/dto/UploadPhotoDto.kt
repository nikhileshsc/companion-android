package com.companion.astrodating.ui.managePhotos.data.dto

data class UploadPhotoDto(
    val `data`: UploadPhotoDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class UploadPhotoDtoData(
    val url: String? = null
)
