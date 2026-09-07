package com.companion.astrodating.data.divineapi.data.mapper

import com.companion.astrodating.data.divineapi.data.dto.MatchingPlanetaryPositionDto
import com.companion.astrodating.data.divineapi.data.dto.P1MatchingPlanetaryDtoEntity
import com.companion.astrodating.data.divineapi.data.dto.PlanetDtoEntity
import com.companion.astrodating.data.divineapi.domain.model.MatchingPlanetaryPositionDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.P1MatchingPlanetaryDomainEntity
import com.companion.astrodating.data.divineapi.domain.model.PlanetDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetMatchingPlanetaryMapper @Inject constructor() {

    fun mapToDomainModel(dto: MatchingPlanetaryPositionDto): MatchingPlanetaryPositionDomainDetails {
        return MatchingPlanetaryPositionDomainDetails(
            success = dto.success ?: 0,
            p1 = mapToP1MatchingPlanetaryDomainModel(dto.data?.p1!!),
            p2 = mapToP1MatchingPlanetaryDomainModel(dto.data.p2!!)

        )
    }
    fun mapToP1MatchingPlanetaryDomainModel(data: P1MatchingPlanetaryDtoEntity): P1MatchingPlanetaryDomainEntity {
        return P1MatchingPlanetaryDomainEntity(
            date = data.date ?: APP_EMPTY_STRING,
            latitude = data.latitude ?: APP_EMPTY_STRING,
            longitude = data.longitude ?: APP_EMPTY_STRING,
            planets = toDomainList(data.planets!!),
            time = data.time ?: APP_EMPTY_STRING,
            timezone = data.timezone ?: APP_EMPTY_STRING,


        )
    }

    private fun toDomainList(list: List<PlanetDtoEntity>): List<PlanetDomainEntity> {
        return list.map {
            PlanetDomainEntity(
                awastha = it.awastha ?: APP_EMPTY_STRING,
                full_degree = it.full_degree ?: APP_EMPTY_STRING,
                house = it.house ?: 0,
                image = it.image ?: APP_EMPTY_STRING,
                is_combusted = it.is_combusted ?: APP_EMPTY_STRING,
                is_retro = it.is_retro ?: APP_EMPTY_STRING,
                karakamsha = it.karakamsha ?: APP_EMPTY_STRING,
                longitude = it.longitude ?: APP_EMPTY_STRING,
                lord_of = it.lord_of ?: APP_EMPTY_STRING,
                nakshatra = it.nakshatra ?: APP_EMPTY_STRING,
                nakshatra_lord = it.nakshatra_lord ?: APP_EMPTY_STRING,
                nakshatra_no = it.nakshatra_no ?: 0,
                nakshatra_pada = it.nakshatra_pada ?: 0,
                name = it.name ?: APP_EMPTY_STRING,
                name_lan = it.name_lan ?: APP_EMPTY_STRING,
                rashi_lord = it.rashi_lord ?: APP_EMPTY_STRING,
                sign = it.sign ?: APP_EMPTY_STRING,
                sign_no = it.sign_no ?: 0,
                speed = it.speed ?: APP_EMPTY_STRING,
                sub_lord = it.sub_lord ?: APP_EMPTY_STRING,
                type = it.type ?: APP_EMPTY_STRING
            )
        }
    }
}