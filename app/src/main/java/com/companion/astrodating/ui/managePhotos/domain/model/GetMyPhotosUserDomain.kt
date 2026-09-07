package com.companion.astrodating.ui.managePhotos.domain.model



data class GetMyPhotosUserDomain(
    val id: String,
    val gallery: List<GetMyPhotoGalleryDomainEntity>,
    val maxGalleryCount: Int
)

data class GetMyPhotoGalleryDomainEntity(
    val id: String,
    val createdAt: String,
    val galleryUrl: String,
    val isActionTaken: Boolean,
    val status: String,
    val updatedAt: String
)