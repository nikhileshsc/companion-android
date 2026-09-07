package com.companion.astrodating.data.divineapi.domain.model

data class MatchingPlanetaryPositionDomainDetails(
    val success: Int,
    val p1: P1MatchingPlanetaryDomainEntity,
    val p2: P1MatchingPlanetaryDomainEntity
)
data class P1MatchingPlanetaryDomainEntity(
    val date: String,
    val latitude: String,
    val longitude: String,
    val planets: List<PlanetDomainEntity>,
    val time: String,
    val timezone: String
)

data class PlanetDomainEntity(
    val awastha: String,
    val full_degree: String,
    val house: Int,
    val image: String,
    val is_combusted: String,
    val is_retro: String,
    val karakamsha: String,
    val longitude: String,
    val lord_of: String,
    val nakshatra: String,
    val nakshatra_lord: String,
    val nakshatra_no: Int,
    val nakshatra_pada: Int,
    val name: String,
    val name_lan: String,
    val rashi_lord: String,
    val sign: String,
    val sign_no: Int,
    val speed: String,
    val sub_lord: String,
    val type: String
)
