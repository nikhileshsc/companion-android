package com.companion.astrodating.ui.managePhotos.data.dto

data class SetProfilePhotoDto(
    val `data`: SetProfilePhotoDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class SetProfilePhotoDtoData(
    val user: SetProfilePhotoUserDtoEntity? = null
)

data class SetProfilePhotoUserDtoEntity(
    val _id: String? = null,
    val agoraChatUid: String? = null,
    val fullName: String? = null,
    val gallery: List<GetMyPhotoGalleryDtoEntity>? = null,
    val profileUrl: String? = null,
    val updatedAt: String? = null
)
