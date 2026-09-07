package com.companion.astrodating.ui.home.data.mapper

import com.companion.astrodating.ui.home.data.dto.UpdateOnlineStatusDto
import com.companion.astrodating.ui.home.domain.model.UpdateOnlineStatusDomain
import javax.inject.Inject

class UpdateOnlineStatusMapper @Inject constructor() {
    fun mapToDomainModel(dto: UpdateOnlineStatusDto): UpdateOnlineStatusDomain {
        return UpdateOnlineStatusDomain(
            isOnline = dto.data?.isOnline ?: false
        )
    }
}