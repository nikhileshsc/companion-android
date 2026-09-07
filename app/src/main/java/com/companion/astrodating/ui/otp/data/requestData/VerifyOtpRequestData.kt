package com.companion.astrodating.ui.otp.data.requestData

data class VerifyOtpRequestData(
    val countryCode: String,
    val deviceToken: String,
    val deviceType: String = "android",
    val email: String,
    val mobile: String,
    val otp: String,
    val type: String
)