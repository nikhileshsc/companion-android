package com.companion.astrodating.ui.otp.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.otp.domain.model.CountryDomain

interface ICountryRepository {

    suspend fun getAllCountryList(commonAuth: String) : ApiResult<CountryDomain>
}