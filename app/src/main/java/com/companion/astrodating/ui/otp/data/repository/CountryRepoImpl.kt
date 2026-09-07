package com.companion.astrodating.ui.otp.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.otp.data.dto.CountryDto
import com.companion.astrodating.ui.otp.data.mapper.CountryDtoMapper
import com.companion.astrodating.ui.otp.domain.model.CountryDomain
import com.companion.astrodating.ui.otp.domain.repository.ICountryRepository
import com.google.gson.Gson
import javax.inject.Inject

class CountryRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: CountryDtoMapper
) : ICountryRepository {

    override suspend fun getAllCountryList(commonAuth: String): ApiResult<CountryDomain> {
        val result = api.getCountryList(commonAuth)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(),CountryDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}