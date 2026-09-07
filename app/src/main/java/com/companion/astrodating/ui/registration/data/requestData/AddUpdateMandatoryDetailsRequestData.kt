package com.companion.astrodating.ui.registration.data.requestData

data class AddUpdateMandatoryDetailsRequestData(
    val age: Int,
    val birthDate: String,
    val cityOfBirth: String,
    val community: String,
    val currentCity: String,
    val fullName: String,
    val gender: String,
    val latitudeOfCityOfBirth: String,
    val latitudeOfCurrentCity: String,
    val longitudeOfCityOfBirth: String,
    val longitudeOfCurrentCity: String,
    val timeOfBirth: String
)