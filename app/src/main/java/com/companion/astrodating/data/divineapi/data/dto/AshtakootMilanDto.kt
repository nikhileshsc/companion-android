package com.companion.astrodating.data.divineapi.data.dto

data class AshtakootMilanDto(
    val `data`: AshtakootMilanDataDto? = null,
    val success: Int? = null,
    val msg: MsgDtoEntity? = null,
) {
    data class MsgDtoEntity(
        val p1: List<String>? = null,
        val p2: List<String>? = null
    )

    data class AshtakootMilanDataDto(
        val ashtakoot_milan: AshtakootMilanDtoEntity? = null,
        val ashtakoot_milan_result: AshtakootMilanResultDtoEntity? = null,
        val bhakoot_dosha: String? = null,
        val manglik_dosha: ManglikDoshaDtoEntity? = null,
        val nadi_dosha: String? = null
    )

    data class AshtakootMilanDtoEntity(
        val varna: AshtagunaDtoEntity? = null,
        val vashya: AshtagunaDtoEntity? = null,
        val tara: AshtagunaDtoEntity? = null,
        val yoni: AshtagunaDtoEntity? = null,
        val graha_maitri: AshtagunaDtoEntity? = null,
        val gana: AshtagunaDtoEntity? = null,
        val bhakoota: AshtagunaDtoEntity? = null,
        val nadi: AshtagunaDtoEntity? = null
    )

    data class AshtagunaDtoEntity(
        val area_of_life: String? = null,
        val description: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null
    )

    data class AshtakootMilanResultDtoEntity(
        val content: String? = null,
        val is_compatible: String? = null,
        val max_ponits: Int? = null,
        val points_obtained: Double? = null
    )

    data class ManglikDoshaDtoEntity(
        val p1: String? = null,
        val p2: String? = null
    )

}
