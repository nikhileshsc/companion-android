package com.companion.astrodating.data.divineapi.data.mapper

import com.companion.astrodating.data.divineapi.data.dto.ManglikDoshaDto
import com.companion.astrodating.data.divineapi.data.dto.ManglikDoshaDtoData
import com.companion.astrodating.data.divineapi.data.dto.P1
import com.companion.astrodating.data.divineapi.domain.model.ManglikDoshaDomainDetails
import com.companion.astrodating.data.divineapi.domain.model.ManglikDoshaDomainEntity
import com.companion.astrodating.data.divineapi.domain.model.P1ManglikDoshaDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetManglikDoshaMapper @Inject constructor() {

    fun mapToDomainModel(dto: ManglikDoshaDto): ManglikDoshaDomainDetails {
        return ManglikDoshaDomainDetails(
            data = mapToManglikDoshaDomainModel(dto.data!!),
            success = dto.success?:0,
            msg = mapToMsgDomainModel(dto.msg),

        )
    }
    fun mapToManglikDoshaDomainModel(data: ManglikDoshaDtoData): ManglikDoshaDomainEntity {
        return ManglikDoshaDomainEntity(
            content = data.content ?: APP_EMPTY_STRING,
            p1 = mapToP1ManglikDoshaDomainModel(data.p1!!),
            p2 = mapToP1ManglikDoshaDomainModel(data.p2!!),

        )
    }

    fun mapToP1ManglikDoshaDomainModel(dto: P1): P1ManglikDoshaDomainEntity {
        return P1ManglikDoshaDomainEntity(
            manglik_dosha = dto.manglik_dosha ?: false,
            percentage = dto.percentage ?: 0.0,
            remedies = dto.remedies ?: emptyList(),
            strength = dto.strength ?: APP_EMPTY_STRING,
        )
    }
    fun mapToMsgDomainModel(dto: ManglikDoshaDto.MsgDtoEntity?): String {
        return DivineApiMsgFormatter.format(dto?.p1, dto?.p2)
    }

}
