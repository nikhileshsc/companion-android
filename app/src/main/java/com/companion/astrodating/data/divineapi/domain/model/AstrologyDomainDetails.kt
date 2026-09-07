package com.companion.astrodating.data.divineapi.domain.model

data class AstrologyDomainDetails(
    val p1: P1DomainEntity,
    val p2: P1DomainEntity,
    val success:Int,
    val msg: String
)
data class P1DomainEntity(
    val full_name: String,
    val varna: String,
    val vashya: String,
    val yoni: String,
    val gana: String,
    val nadi: String,
    val signLord: String,
    val nakshatra: String,
    val nakshatraLord: String,
    val prahar: Int,
    val tatva: String,
    val nameAlphabet: String,
    val paya: String
/*

    val ayanamsha: String,
    val chandramasa: String,
    val day: Int,

    val gender: String,
    val hour: Int,
    val karana: String,
    val latitude: String,
    val longitude: String,
    val minute: Int,
    val month: Int,
    val moonsign: String,


    val paksha: String,
    val paya: Paya,
    val place: String,

    val rashi_akshar: String,
    val sunrise: String,
    val sunset: String,
    val sunsign: String,

    val timezone: Double,
    val tithi: String,
    val vaar: String,

    val year: Int,
    val yoga: String,

    val yunja: String
*/

)