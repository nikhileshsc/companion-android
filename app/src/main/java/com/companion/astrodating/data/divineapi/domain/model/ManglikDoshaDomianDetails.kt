package com.companion.astrodating.data.divineapi.domain.model

data class ManglikDoshaDomainDetails(
    val `data`: ManglikDoshaDomainEntity,
    val success: Int,
    val msg: String
)

data class ManglikDoshaDomainEntity(
    val content: String,
    val p1: P1ManglikDoshaDomainEntity,
    val p2: P1ManglikDoshaDomainEntity
)

data class P1ManglikDoshaDomainEntity(
    val manglik_dosha: Boolean,
    val percentage: Double,
    val remedies: List<String>,
    val strength: String
)
