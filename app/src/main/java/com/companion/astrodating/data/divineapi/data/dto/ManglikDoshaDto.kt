package com.companion.astrodating.data.divineapi.data.dto

data class ManglikDoshaDto(
    val `data`: ManglikDoshaDtoData? = null,
    val success: Int? = null,
    val msg: MsgDtoEntity? = null,
) {
    data class MsgDtoEntity(
        val p1: List<String>? = null,
        val p2: List<String>? = null
    )
}

data class ManglikDoshaDtoData(
    val content: String? = null,
    val p1: P1? = null,
    val p2: P1? = null
)

data class P1(
    val manglik_dosha: Boolean? = null,
    val percentage: Double? = null,
    val remedies: List<String>? = null,
    val strength: String? = null
)
