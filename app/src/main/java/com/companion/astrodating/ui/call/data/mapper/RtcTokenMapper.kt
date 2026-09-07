package com.companion.astrodating.ui.call.data.mapper

import com.companion.astrodating.ui.call.data.dto.RtcTokenDto
import com.companion.astrodating.ui.call.domain.model.RtcTokenDomain
import javax.inject.Inject

class RtcTokenMapper @Inject constructor() {
    fun mapToDomainModel(dto: RtcTokenDto): RtcTokenDomain{
        return RtcTokenDomain(
            token = dto.data?.token ?: "",
            channel = dto.data?.channelName ?: "",
            userId = dto.data?.userId ?: 1
        )
    }
}