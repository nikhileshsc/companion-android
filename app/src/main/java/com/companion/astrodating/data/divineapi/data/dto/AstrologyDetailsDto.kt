package com.companion.astrodating.data.divineapi.data.dto

data class AstrologyDetailsDto(
    val `data`: AstrologyDetailsDataDto? = null,
    val success: Int? = null,
    val msg: MsgDtoEntity? = null,
) {
    data class MsgDtoEntity(
        val p1: List<String>? = null,
        val p2: List<String>? = null
    )
}

data class AstrologyDetailsDataDto(
    val p1: P1DtoEntity? = null,
    val p2: P2DtoEntity? = null
)

data class P1DtoEntity(
    val ayanamsha: String? = null,
    val chandramasa: String? = null,
    val day: Int? = null,
    val full_name: String? = null,
    val gana: String? = null,
    val gender: String? = null,
    val hour: Int? = null,
    val karana: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val minute: Int? = null,
    val month: Int? = null,
    val moonsign: String? = null,
    val nadi: String? = null,
    val nakshatra: String? = null,
    val paksha: String? = null,
    val paya: Paya? = null,
    val place: String? = null,
    val prahar: Int? = null,
    val rashi_akshar: String? = null,
    val sunrise: String? = null,
    val sunset: String? = null,
    val sunsign: String? = null,
    val tatva: String? = null,
    val timezone: Double? = null,
    val tithi: String? = null,
    val vaar: String? = null,
    val varna: String? = null,
    val vashya: String? = null,
    val year: Int? = null,
    val yoga: String? = null,
    val yoni: String? = null,
    val yunja: String? = null

)

data class P2DtoEntity(
    val ayanamsha: String? = null,
    val chandramasa: String? = null,
    val day: Int? = null,
    val full_name: String? = null,
    val gana: String? = null,
    val gender: String? = null,
    val hour: Int? = null,
    val karana: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val minute: Int? = null,
    val month: Int? = null,
    val moonsign: String? = null,
    val nadi: String? = null,
    val nakshatra: String? = null,
    val paksha: String? = null,
    val paya: Paya? = null,
    val place: String? = null,
    val prahar: Int? = null,
    val rashi_akshar: String? = null,
    val sunrise: String? = null,
    val sunset: String? = null,
    val sunsign: String? = null,
    val tatva: String? = null,
    val timezone: Double? = null,
    val tithi: String? = null,
    val vaar: String? = null,
    val varna: String? = null,
    val vashya: String? = null,
    val year: Int? = null,
    val yoga: String? = null,
    val yoni: String? = null,
    val yunja: String? = null
)
data class Paya(
    val result: String? = null,
    val type: String? = null
)
