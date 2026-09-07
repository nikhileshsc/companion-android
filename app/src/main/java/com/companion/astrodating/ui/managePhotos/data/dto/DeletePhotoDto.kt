package com.companion.astrodating.ui.managePhotos.data.dto


data class DeletePhotoDto(
    val `data`: DeletePhotoDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class DeletePhotoDtoData(
    val user: DeletePhotoUserDtoEntity? = null
)

data class DeletePhotoUserDtoEntity(
    val __v: Int? = null,
    val _id: String? = null,
    val gallery: List<GetMyPhotoGalleryDtoEntity>? = null,
    val profileUrl: String? = null,
    val updatedAt: String? = null
)