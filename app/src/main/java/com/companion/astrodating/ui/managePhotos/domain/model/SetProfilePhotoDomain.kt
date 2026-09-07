package com.companion.astrodating.ui.managePhotos.domain.model

data class SetProfilePhotoDomain(
    val id: String,
    val agoraChatUid: String,
    val fullName: String,
    val gallery: List<GetMyPhotoGalleryDomainEntity>,
    val profileUrl: String,
    val updatedAt: String
)
