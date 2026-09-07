package com.companion.astrodating.data.divineapi.data.mapper

import com.companion.astrodating.data.divineapi.data.dto.AshtakootMilanDto
import com.companion.astrodating.data.divineapi.data.dto.AstrologyDetailsDto
import com.companion.astrodating.data.divineapi.data.dto.DashakootMilanDto
import com.companion.astrodating.data.divineapi.data.dto.ManglikDoshaDto
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object DivineApiGsonProvider {
    fun create(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                AshtakootMilanDto.MsgDtoEntity::class.java,
                DivineApiMsgDeserializer { p1, p2 -> AshtakootMilanDto.MsgDtoEntity(p1, p2) }
            )
            .registerTypeAdapter(
                DashakootMilanDto.MsgDtoEntity::class.java,
                DivineApiMsgDeserializer { p1, p2 -> DashakootMilanDto.MsgDtoEntity(p1, p2) }
            )
            .registerTypeAdapter(
                AstrologyDetailsDto.MsgDtoEntity::class.java,
                DivineApiMsgDeserializer { p1, p2 -> AstrologyDetailsDto.MsgDtoEntity(p1, p2) }
            )
            .registerTypeAdapter(
                ManglikDoshaDto.MsgDtoEntity::class.java,
                DivineApiMsgDeserializer { p1, p2 -> ManglikDoshaDto.MsgDtoEntity(p1, p2) }
            )
            .create()
    }
}
