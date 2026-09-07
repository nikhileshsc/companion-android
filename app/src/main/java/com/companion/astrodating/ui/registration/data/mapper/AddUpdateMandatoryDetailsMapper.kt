package com.companion.astrodating.ui.registration.data.mapper

import com.companion.astrodating.ui.registration.data.dto.AddUpdateMandatoryDetailsDto
import com.companion.astrodating.ui.registration.domain.model.AddUpdateMandatoryDetailsDomain
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class AddUpdateMandatoryDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: AddUpdateMandatoryDetailsDto): AddUpdateMandatoryDetailsDomain {
        return AddUpdateMandatoryDetailsDomain(
            id = dto.data?.user?._id ?: APP_EMPTY_STRING,
            age = dto.data?.user?.age ?: 0,
            agoraChatUid = dto.data?.user?.agoraChatUid ?: APP_EMPTY_STRING,
            birthDate = dto.data?.user?.birthDate ?: APP_EMPTY_STRING,
            cityOfBirth = dto.data?.user?.cityOfBirth ?: APP_EMPTY_STRING,
            community = dto.data?.user?.community ?: APP_EMPTY_STRING,
            currentCity = dto.data?.user?.currentCity ?: APP_EMPTY_STRING,
            fullName = dto.data?.user?.fullName ?: APP_EMPTY_STRING,
            gender = dto.data?.user?.gender ?: APP_EMPTY_STRING,
            isRegistrationCompleted  = dto.data?.user?.isRegistrationCompleted ?: false,
            profileUrl = dto.data?.user?.profileUrl ?: APP_EMPTY_STRING,
            timeOfBirth = dto.data?.user?.timeOfBirth ?: APP_EMPTY_STRING,
            updatedAt = dto.data?.user?.updatedAt ?: APP_EMPTY_STRING,
            zodiacSignInEng = dto.data?.user?.zodiacSignInEng ?: APP_EMPTY_STRING,
            zodiacSignInMarathi = dto.data?.user?.zodiacSignInMarathi ?: APP_EMPTY_STRING
        )
    }

}