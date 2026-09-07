package com.companion.astrodating.ui.otp.domain.model


data class CountryDomain(
    val list: List<CountryDomainEntity>
)

data class CountryDomainEntity(
    val id: String,
    val country: String,
    val iconUrl: String,
    val iso: String,
    val phone: String
)