package com.companion.astrodating.data.divineapi.data.dto

data class MatchingPlanetaryPositionDto(
    val `data`: MatchingPlanetaryPositionDtoData? = null,
    val success: Int? = null,
    val msg: String? = null
)

data class MatchingPlanetaryPositionDtoData(
    val p1: P1MatchingPlanetaryDtoEntity? = null,
    val p2: P1MatchingPlanetaryDtoEntity? = null
)

data class P1MatchingPlanetaryDtoEntity(
    val date: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val planets: List<PlanetDtoEntity>? = null,
    val time: String? = null,
    val timezone: String? = null
)

data class PlanetDtoEntity(
    val awastha: String? = null,
    val full_degree: String? = null,
    val house: Int? = null,
    val image: String? = null,
    val is_combusted: String? = null,
    val is_retro: String? = null,
    val karakamsha: String? = null,
    val longitude: String? = null,
    val lord_of: String? = null,
    val nakshatra: String? = null,
    val nakshatra_lord: String? = null,
    val nakshatra_no: Int? = null,
    val nakshatra_pada: Int? = null,
    val name: String? = null,
    val name_lan: String? = null,
    val rashi_lord: String? = null,
    val sign: String? = null,
    val sign_no: Int? = null,
    val speed: String? = null,
    val sub_lord: String? = null,
    val type: String? = null
)
