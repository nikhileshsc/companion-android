package com.companion.astrodating.ui.profileDetails.data.mapper

import com.companion.astrodating.ui.profileDetails.data.dto.GetUserDetailsDto
import com.companion.astrodating.ui.profileDetails.data.dto.GetUserDetailsGalleryDto
import com.companion.astrodating.ui.profileDetails.domain.model.GetUserDetailsDomain
import com.companion.astrodating.ui.profileDetails.domain.model.GetUserDetailsGalleryDomain
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetUserDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: GetUserDetailsDto): GetUserDetailsDomain {
        return GetUserDetailsDomain(
            id = dto.data?.user?._id ?: APP_EMPTY_STRING,
            aboutYourself = dto.data?.user?.aboutYourself ?: APP_EMPTY_STRING,
            age = dto.data?.user?.age ?: 0,
            approvedPhotosCount = dto.data?.user?.approvedPhotosCount ?: 0,
            birthDate = dto.data?.user?.birthDate ?: APP_EMPTY_STRING,
            cityOfBirth = dto.data?.user?.cityOfBirth ?: APP_EMPTY_STRING,
            community = dto.data?.user?.community ?: APP_EMPTY_STRING,
            currentCity = dto.data?.user?.currentCity ?: APP_EMPTY_STRING,
            education = dto.data?.user?.education ?: APP_EMPTY_STRING,
            expectations = dto.data?.user?.expectations ?: APP_EMPTY_STRING,
            fullName = dto.data?.user?.fullName ?: APP_EMPTY_STRING,
            gallery = toDomainList(dto.data?.user?.gallery ?: emptyList()),
            gender = dto.data?.user?.gender ?: APP_EMPTY_STRING,
            height = dto.data?.user?.height ?: 0.0,
            interest = dto.data?.user?.interest ?: APP_EMPTY_STRING,
            isVerifiedAccount = dto.data?.user?.isVerifiedAccount ?: false,
            latitudeOfCityOfBirth = dto.data?.user?.latitudeOfCityOfBirth ?: APP_EMPTY_STRING,
            latitudeOfCurrentCity = dto.data?.user?.latitudeOfCurrentCity ?: APP_EMPTY_STRING,
            longitudeOfCityOfBirth = dto.data?.user?.longitudeOfCityOfBirth ?: APP_EMPTY_STRING,
            longitudeOfCurrentCity = dto.data?.user?.longitudeOfCurrentCity ?: APP_EMPTY_STRING,
            profession = dto.data?.user?.profession ?: APP_EMPTY_STRING,
            profileUrl = dto.data?.user?.profileUrl ?: APP_EMPTY_STRING,
            religion = dto.data?.user?.religion ?: APP_EMPTY_STRING,
            status = dto.data?.user?.status ?: APP_EMPTY_STRING,
            userId = dto.data?.user?.userId ?: APP_EMPTY_STRING,
            timeOfBirth = dto.data?.user?.timeOfBirth ?: APP_EMPTY_STRING,
            zodiacPngUrl = dto.data?.user?.zodiacPngUrl ?: APP_EMPTY_STRING,
            zodiacSignInEng = dto.data?.user?.zodiacSignInEng ?: APP_EMPTY_STRING,
            zodiacSignInMarathi = dto.data?.user?.zodiacSignInMarathi ?: APP_EMPTY_STRING,
            zodiacSvgUrl = dto.data?.user?.zodiacSvgUrl ?: APP_EMPTY_STRING,
            userStatus = dto.data?.user?.userStatus ?: APP_EMPTY_STRING

        )
    }

    private fun toDomainList(list: List<GetUserDetailsGalleryDto>): List<GetUserDetailsGalleryDomain> {
        return list.map {
            GetUserDetailsGalleryDomain(
                id = it._id ?: APP_EMPTY_STRING,
                createdAt = it.createdAt ?: APP_EMPTY_STRING,
                galleryUrl = it.galleryUrl ?: APP_EMPTY_STRING,
                isActionTaken = it.isActionTaken ?: false,
                status = it.status ?: APP_EMPTY_STRING,
                updatedAt = it.updatedAt ?: APP_EMPTY_STRING
            )
        }
    }


}