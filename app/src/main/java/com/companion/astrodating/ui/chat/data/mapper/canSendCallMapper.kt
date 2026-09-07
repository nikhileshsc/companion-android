package com.companion.astrodating.ui.chat.data.mapper

import com.companion.astrodating.ui.chat.data.dto.canSendCallDto
import com.companion.astrodating.ui.chat.domain.model.canSendCallDomain
import javax.inject.Inject

class canSendCallMapper @Inject constructor() {
    fun mapToDomainModel(dto: canSendCallDto): canSendCallDomain {
        return canSendCallDomain(
            allowed = dto.data?.allowed ?: false,
            message = dto.data?.message ?: ""
        )
    }
}