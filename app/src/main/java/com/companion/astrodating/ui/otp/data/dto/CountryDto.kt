package com.companion.astrodating.ui.otp.data.dto

import com.google.gson.annotations.SerializedName

data class CountryDto(
    @SerializedName("data")
    val data: CountryDtoData,
    val message: String,
    val statusCode: Int
)

data class CountryDtoData(
    @SerializedName("countryCodes")
    val countryCodes: List<CountryDtoEntity>
)

data class CountryDtoEntity(
    val _id: String,
    val country: String,
    val iconUrl: String,
    val iso: String,
    val phone: String

)

