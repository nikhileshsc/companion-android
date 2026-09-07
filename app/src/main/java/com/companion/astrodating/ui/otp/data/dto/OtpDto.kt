package com.companion.astrodating.ui.otp.data.dto

data class OtpDto(
    val data: OtpDtoData,
    val message: String,
    val statusCode: Int
)

data class OtpDtoData(
    val type: String? = null,
    val user: UserDto? = null
)

data class UserDto(
    val countryCode: String? = null,
    val email: String? = null,
    val mobile: String? = null
)