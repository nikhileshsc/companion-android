package com.companion.astrodating.ui.chat.data.mapper

import com.companion.astrodating.ui.chat.data.dto.messageLimitDto
import com.companion.astrodating.ui.chat.domain.model.freeMessageDomain
import com.companion.astrodating.ui.purchasePlans.data.dto.UpdateBenefitsDto
import com.companion.astrodating.ui.purchasePlans.domain.model.UpdateBenefitsDomainDetails
import javax.inject.Inject

class freeMessageMapper @Inject constructor() {
    fun mapToDomainModel(dto: messageLimitDto): freeMessageDomain {
        return freeMessageDomain(
            data = mapToBenefitsDataDomainModel(dto.data!!),
            message = dto.message?: "",
            statusCode = dto.statusCode ?: 0
        )
    }

    fun mapToBenefitsDataDomainModel(dto: messageLimitDto.Data): freeMessageDomain.Data {
        return freeMessageDomain.Data(
            allowed = dto.allowed!!,
            message = dto.message?: "something went wrong"
        )
    }
}