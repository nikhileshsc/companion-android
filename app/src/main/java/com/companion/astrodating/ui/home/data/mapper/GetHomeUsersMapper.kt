package com.companion.astrodating.ui.home.data.mapper

import com.companion.astrodating.ui.home.data.dto.GetHomeUserDtoEntity
import com.companion.astrodating.ui.home.data.dto.GetHomeUsersDto
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomain
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetHomeUsersMapper @Inject constructor() {

    fun mapToDomainModel(dto: GetHomeUsersDto): GetHomeUserDomain {
        return GetHomeUserDomain(
            users = toDomainList(dto.data?.users ?: emptyList())
        )
    }

    private fun toDomainList(list: List<GetHomeUserDtoEntity>): List<GetHomeUserDomainEntity> {
        return list.map {
            GetHomeUserDomainEntity(
                id = it._id ?: APP_EMPTY_STRING,
                age = it.age ?: 0,
                birthDate = it.birthDate ?: APP_EMPTY_STRING,
                cityOfBirth = it.cityOfBirth ?: APP_EMPTY_STRING,
                community = it.community ?: APP_EMPTY_STRING,
                currentCity = it.currentCity ?: APP_EMPTY_STRING,
                education = it.education ?: APP_EMPTY_STRING,
                fullName = it.fullName ?: APP_EMPTY_STRING,
                height = it.height ?: APP_EMPTY_STRING,
                isVerifiedAccount = it.isVerifiedAccount ?: false,
                latitudeOfCityOfBirth = it.latitudeOfCityOfBirth ?: APP_EMPTY_STRING,
                latitudeOfCurrentCity = it.latitudeOfCurrentCity ?: APP_EMPTY_STRING,
                longitudeOfCityOfBirth = it.longitudeOfCityOfBirth ?: APP_EMPTY_STRING,
                longitudeOfCurrentCity = it.longitudeOfCurrentCity ?: APP_EMPTY_STRING,
                profession = it.profession ?: APP_EMPTY_STRING,
                profileUrl = it.profileUrl ?: APP_EMPTY_STRING,
                religion = it.religion ?: APP_EMPTY_STRING,
                status = it.status ?: APP_EMPTY_STRING,
                timeOfBirth = it.timeOfBirth ?: APP_EMPTY_STRING,
                userId = it.userId ?: APP_EMPTY_STRING,
                zodiacSignInEng = it.zodiacSignInEng ?: APP_EMPTY_STRING,
                zodiacSignInMarathi = it.zodiacSignInMarathi ?: APP_EMPTY_STRING,
                zodiacPngUrl = it.zodiacPngUrl ?: APP_EMPTY_STRING,
                zodiacSvgUrl = it.zodiacSvgUrl ?: APP_EMPTY_STRING,
                isOnline = it.isOnline ?: false
            )
        }
    }


}