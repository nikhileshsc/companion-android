package com.companion.astrodating.ui.profile.data.mapper

import com.companion.astrodating.ui.profile.data.dto.LogoutDto
import com.companion.astrodating.ui.profile.domain.model.LogoutDomain
import javax.inject.Inject

class ProfileDtoMapper @Inject constructor() {

    fun mapToLogoutDomain(dto: LogoutDto): LogoutDomain {
        return LogoutDomain(
            message = dto.message,
            statusCode = dto.statusCode
        )
    }

}