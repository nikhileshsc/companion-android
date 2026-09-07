package com.companion.astrodating.data.divineapi.domain.model

data class AshtakootMilanDomainDetails(
    val `data`: AshtakootMilanDomain,
    val msg :String,
    val success: Int

) {

    data class MsgDomainEntity(
        val p1: List<String>,
        val p2: List<String>
    )

    data class AshtakootMilanDomain(
        val ashtakoot_milan: AshtakootMilanDomainEntity,
        val ashtakoot_milan_result: AshtakootMilanResultDomainEntity,
        val bhakoot_dosha: String,
        val manglik_dosha: ManglikDoshaDomainEntity,
        val nadi_dosha: String
    )

    data class AshtakootMilanDomainEntity(
        val varna: AshtagunaDomainEntity,
        val vashya: AshtagunaDomainEntity,
        val tara: AshtagunaDomainEntity,
        val yoni: AshtagunaDomainEntity,
        val graha_maitri: AshtagunaDomainEntity,
        val gana: AshtagunaDomainEntity,
        val bhakoota: AshtagunaDomainEntity,
        val nadi: AshtagunaDomainEntity
    )


    data class AshtagunaDomainEntity(
        val area_of_life: String,
        val description: String,
        val max_ponits: Int,
        val p1: String,
        val p2: String,
        val points_obtained: Double
    )


    data class AshtakootMilanResultDomainEntity(
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