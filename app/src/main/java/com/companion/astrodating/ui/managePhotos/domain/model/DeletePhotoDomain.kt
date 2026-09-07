package com.companion.astrodating.ui.managePhotos.domain.model

data class DeletePhotoDomain(
    val id: String,
    val gallery: List<GetMyPhotoGalleryDomainEntity>,
    val profileUrl: String,
    val updatedAt: String
)
