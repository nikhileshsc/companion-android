package com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.mapper

import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.dto.GetBasicDetailsDto
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.GetBasicDetailsDomain
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetBasicDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: GetBasicDetailsDto): GetBasicDetailsDomain {
        return GetBasicDetailsDomain(
            id = dto.data?.user?._id ?: APP_EMPTY_STRING,
            community = dto.data?.user?.community ?: APP_EMPTY_STRING,
            currentCity = dto.data?.user?.currentCity ?: APP_EMPTY_STRING,
            education = dto.data?.user?.education ?: APP_EMPTY_STRING,
            height = dto.data?.user?.height ?: APP_EMPTY_STRING,
            lookingFor = dto.data?.user?.lookingFor ?: APP_EMPTY_STRING,
            profession = dto.data?.user?.profession ?: APP_EMPTY_STRING,
            religion = dto.data?.user?.religion ?: APP_EMPTY_STRING,
            status = dto.data?.user?.status ?: APP_EMPTY_STRING
        )
    }

}