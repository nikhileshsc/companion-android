package com.companion.astrodating.ui.interests.data.mapper

import com.companion.astrodating.ui.interests.data.dto.GetInterestDto
import com.companion.astrodating.ui.interests.data.dto.GetInterestUserDtoEntity
import com.companion.astrodating.ui.interests.data.dto.UpdateInterestDto
import com.companion.astrodating.ui.interests.domain.model.GetInterestUserDomainEntity
import com.companion.astrodating.ui.interests.domain.model.GetInterestsDomain
import com.companion.astrodating.ui.interests.domain.model.UpdateInterestDomain
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class InterestsMapper @Inject constructor() {

    fun mapToUpdateInterestsDomainModel(dto: UpdateInterestDto): UpdateInterestDomain {
        return UpdateInterestDomain(
            v = dto.data?.user?.__v ?: 0,
            id = dto.data?.user?._id ?: APP_EMPTY_STRING,
            blockedUsers = dto.data?.user?.blockedUsers ?: emptyList(),
            declinedUserInterests = dto.data?.user?.declinedUserInterests ?: emptyList(),
            receivedInterestUsers = dto.data?.user?.receivedInterestUsers ?: emptyList(),
            sentInterestUsers = dto.data?.user?.sentInterestUsers ?: emptyList(),
            shortListedUsers = dto.data?.user?.shortListedUsers ?: emptyList(),
            updatedAt = dto.data?.user?.updatedAt ?: APP_EMPTY_STRING,
        )
    }

    fun mapToGetInterestsDomainModel(dto: GetInterestDto): GetInterestsDomain {
        return GetInterestsDomain(
           users = toDomainList(dto.data?.users ?: emptyList())
        )
    }

    private fun toDomainList(list: List<GetInterestUserDtoEntity>): List<GetInterestUserDomainEntity> {
        return list.map {
            GetInterestUserDomainEntity(
                id = it._id ?: APP_EMPTY_STRING,
                age = it.age ?: 0,
                community = it.community ?: APP_EMPTY_STRING,
                currentCity = it.currentCity ?: APP_EMPTY_STRING,
                education = it.education ?: APP_EMPTY_STRING,
                fullName = it.fullName ?: APP_EMPTY_STRING,
                height = it.height ?: APP_EMPTY_STRING,
                isVerifiedAccount = it.isVerifiedAccount ?: false,
                profession = it.profession ?: APP_EMPTY_STRING,
                profileUrl = it.profileUrl ?: APP_EMPTY_STRING,
                religion = it.religion ?: APP_EMPTY_STRING,
                status = it.status ?: APP_EMPTY_STRING,
                zodiacSignInEng = it.zodiacSignInEng ?: APP_EMPTY_STRING,
                zodiacSignInMarathi = it.zodiacSignInMarathi ?: APP_EMPTY_STRING,
                zodiacPngUrl = it.zodiacPngUrl ?: APP_EMPTY_STRING,
                zodiacSvgUrl = it.zodiacSvgUrl ?: APP_EMPTY_STRING,
            )
        }
    }

}