package com.companion.astrodating.ui.managePhotos.data.dto

data class GetMyPhotosDto(
    val `data`: GetMyPhotosDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class GetMyPhotosDtoData(
    val user: GetMyPhotosUserDtoEntity? = null
)

data class GetMyPhotosUserDtoEntity(
    val _id: String? = null,
    val gallery: List<GetMyPhotoGalleryDtoEntity>? = null,
    val maxGalleryCount: Int? = null
)

data class GetMyPhotoGalleryDtoEntity(
    val _id: String? = null,
    val createdAt: String? = null,
    val galleryUrl: String? = null,
    val isActionTaken: Boolean? = null,
    val status: String? = null,
    val updatedAt: String? = null
)
