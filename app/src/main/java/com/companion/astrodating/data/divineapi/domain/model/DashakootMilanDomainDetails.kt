package com.companion.astrodating.data.divineapi.domain.model

data class DashakootMilanDomainDetails(
    val `data`: DashakootMilanDataDomain,
    val success: Int,
    val msg: String
) {

    data class DashakootMilanDataDomain(
        val dashakoot_milan: DashakootMilanDomainEntity,
        val dashakoot_milan_result: DashakootMilanResultDomainEntity,
        val manglik_dosha: ManglikDoshaDomainEntity,
        val rajju_dosha: String
    )

    data class DashakootMilanDomainEntity(
        val dina: DinaDomainEntity,
        val gana: GanaDomainEntity,
        val mahendra: MahendraDomainEntity,
        val rajju: RajjuDomainEntity,
        val rashi: RashiDomainEntity,
        val rasyadhipati: RasyadhipatiDomainEntity,
        val streedargha: StreedarghaDomainEntity,
        val vashya: VashyaDomainEntity,
        val vedha: VedhaDomainEntity,
        val yoni: YoniDomainEntity
    )

    data class DinaDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )

    data class GanaDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )

    data class MahendraDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )

    data class RajjuDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )

    data class RashiDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )

    data class RasyadhipatiDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )

    data class StreedarghaDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )

    data class VashyaDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )

    data class VedhaDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )

    data class YoniDomainEntity(
        val area_of_life: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double,
        val result: String
    )


    data class DashakootMilanResultDomainEntity(
        val content: String,
        val is_compatible: String,
        val max_ponits: Int,
        val points_obtained: Double
    )

    data class ManglikDoshaDomainEntity(
        val p1: String,
        val p2: String
    )
}
