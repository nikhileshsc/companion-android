package com.companion.astrodating.ui.otp.data.mapper

import com.companion.astrodating.ui.otp.data.dto.CountryDto
import com.companion.astrodating.ui.otp.data.dto.CountryDtoEntity
import com.companion.astrodating.ui.otp.domain.model.CountryDomain
import com.companion.astrodating.ui.otp.domain.model.CountryDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class CountryDtoMapper @Inject constructor() {

    fun mapToDomainModel(dto: CountryDto): CountryDomain {
        return CountryDomain(
            list = toDomainList(dto.data.countryCodes) ?: emptyList()
        )
    }

    private fun toDomainList(list: List<CountryDtoEntity>?): List<CountryDomainEntity>? {
        return list?.map {
            CountryDomainEntity(
                id = it._id ?: APP_EMPTY_STRING,
                country = it.country ?: APP_EMPTY_STRING,
                iconUrl = it.iconUrl ?: APP_EMPTY_STRING,
                iso = it.iso ?: APP_EMPTY_STRING,
                phone = it.phone ?: APP_EMPTY_STRING,

            )
        }
    }
}