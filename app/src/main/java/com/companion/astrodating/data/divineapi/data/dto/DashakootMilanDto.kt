package com.companion.astrodating.data.divineapi.data.dto

data class DashakootMilanDto(
    val `data`: DashakootMilanDataDto? = null,
    val success: Int? = null,
    val msg: MsgDtoEntity? = null,
) {
    data class MsgDtoEntity(
        val p1: List<String>? = null,
        val p2: List<String>? = null
    )

    data class DashakootMilanDataDto(
        val dashakoot_milan: DashakootMilanDtoEntity? = null,
        val dashakoot_milan_result: DashakootMilanResultDtoEntity? = null,
        val manglik_dosha: ManglikDoshaDtoEntity? = null,
        val rajju_dosha: String? = null
    )

    data class DashakootMilanDtoEntity(
        val dina: DinaDtoEntity? = null,
        val gana: GanaDtoEntity? = null,
        val mahendra: MahendraDtoEntity? = null,
        val rajju: RajjuDtoEntity? = null,
        val rashi: RashiDtoEntity? = null,
        val rasyadhipati: RasyadhipatiDtoEntity? = null,
        val streedargha: StreedarghaDtoEntity? = null,
        val vashya: VashyaDtoEntity? = null,
        val vedha: VedhaDtoEntity? = null,
        val yoni: YoniDtoEntity? = null
    )

    data class DinaDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )

    data class GanaDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )

    data class MahendraDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )

    data class RajjuDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )

    data class RashiDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )

    data class RasyadhipatiDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )

    data class StreedarghaDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )

    data class VashyaDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )

    data class VedhaDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )

    data class YoniDtoEntity(
        val area_of_life: String? = null,
        val max_ponits: Int? = null,
        val p1: String? = null,
        val p2: String? = null,
        val points_obtained: Double? = null,
        val result: String? = null
    )


    data class DashakootMilanResultDtoEntity(
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
