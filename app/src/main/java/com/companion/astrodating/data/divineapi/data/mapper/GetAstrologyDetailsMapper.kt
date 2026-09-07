package com.companion.astrodating.data.divineapi.data.mapper

import com.companion.astrodating.data.divineapi.data.dto.AstrologyDetailsDto
import com.companion.astrodating.data.divineapi.data.dto.P1DtoEntity
import com.companion.astrodating.data.divineapi.data.dto.P2DtoEntity
import com.companion.astrodating.data.divineapi.domain.model.AstrologyDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.P1DomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetAstrologyDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: AstrologyDetailsDto): AstrologyDomainDetails {
        return AstrologyDomainDetails(
            p1 = mapToP1DomainModel(dto.data!!.p1!!),
            p2 = mapToP2DomainModel(dto.data.p2!!),
            success = dto.success!!,
            msg = mapToMsgDomainModel(dto.msg),
        )
    }
    fun mapToP1DomainModel(data: P1DtoEntity): P1DomainEntity {
        return P1DomainEntity(
            full_name = data.full_name ?: APP_EMPTY_STRING,
            varna = data.varna ?: APP_EMPTY_STRING,
            vashya = data.vashya ?: APP_EMPTY_STRING,
            yoni = data.yoni ?: APP_EMPTY_STRING,
            gana = data.gana ?: APP_EMPTY_STRING,
            nadi = data.nadi ?: APP_EMPTY_STRING,
            signLord = APP_EMPTY_STRING,
            nakshatra = data.nakshatra ?: APP_EMPTY_STRING,
            nakshatraLord =  APP_EMPTY_STRING,
            prahar = data.prahar ?: 0,
            tatva = data.tatva ?: APP_EMPTY_STRING,
            nameAlphabet = data.rashi_akshar ?: APP_EMPTY_STRING,
            paya = data.paya?.result ?: APP_EMPTY_STRING
        )
    }

    fun mapToP2DomainModel(data: P2DtoEntity): P1DomainEntity {
        return P1DomainEntity(
            full_name = data.full_name ?: APP_EMPTY_STRING,
            varna = data.varna ?: APP_EMPTY_STRING,
            vashya = data.vashya ?: APP_EMPTY_STRING,
            yoni = data.yoni ?: APP_EMPTY_STRING,
            gana = data.gana ?: APP_EMPTY_STRING,
            nadi = data.nadi ?: APP_EMPTY_STRING,
            signLord = APP_EMPTY_STRING,
            nakshatra = data.nakshatra ?: APP_EMPTY_STRING,
            nakshatraLord =  APP_EMPTY_STRING,
            prahar = data.prahar ?: 0,
            tatva = data.tatva ?: APP_EMPTY_STRING,
            nameAlphabet = data.rashi_akshar ?: APP_EMPTY_STRING,
            paya = data.paya?.result ?: APP_EMPTY_STRING
        )
    }
    fun mapToMsgDomainModel(dto: AstrologyDetailsDto.MsgDtoEntity?): String {
        return DivineApiMsgFormatter.format(dto?.p1, dto?.p2)
    }


}
