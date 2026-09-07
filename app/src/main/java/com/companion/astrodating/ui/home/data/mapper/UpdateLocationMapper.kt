package com.companion.astrodating.ui.home.data.mapper

import com.companion.astrodating.ui.home.data.dto.UpdateLocationDto
import com.companion.astrodating.ui.home.domain.model.UpdateLocationDomain
import javax.inject.Inject

class UpdateLocationMapper @Inject constructor() {
    fun mapToDomainModel(dto: UpdateLocationDto): UpdateLocationDomain {
        return UpdateLocationDomain(
            latitude = dto.data?.latitude ?: 0.0,
            longitude = dto.data?.longitude ?: 0.0
        )
    }
}