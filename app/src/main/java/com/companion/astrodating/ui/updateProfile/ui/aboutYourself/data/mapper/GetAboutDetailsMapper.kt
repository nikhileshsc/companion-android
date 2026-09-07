package com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.mapper

import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.data.dto.GetAboutDetailsDto
import com.companion.astrodating.ui.updateProfile.ui.aboutYourself.domain.model.GetAboutDetailsDomain
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetAboutDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: GetAboutDetailsDto): GetAboutDetailsDomain {
        return GetAboutDetailsDomain(
            id = dto.data?.user?._id ?: APP_EMPTY_STRING,
            aboutYourself = dto.data?.user?.aboutYourself ?: APP_EMPTY_STRING,
            expectations = dto.data?.user?.expectations ?: APP_EMPTY_STRING,
            interest = dto.data?.user?.interest ?: APP_EMPTY_STRING,
            updatedAt = dto.data?.user?.updatedAt ?: APP_EMPTY_STRING,
        )
    }

}