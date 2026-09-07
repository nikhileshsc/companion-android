package com.companion.astrodating.ui.message.data.mapper

import com.companion.astrodating.ui.message.data.dto.GetAgoraTokenDto
import com.companion.astrodating.ui.message.data.dto.GetAgoraTokenUserDtoEntity
import com.companion.astrodating.ui.message.domain.model.GetAgoraTokenDomainDetails
import com.companion.astrodating.ui.message.domain.model.GetAgoraTokenUserDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetAgoraTokenDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: GetAgoraTokenDto): GetAgoraTokenDomainDetails {
        return GetAgoraTokenDomainDetails(
            user = mapToAgoraChatTokenDomainModel(dto.data?.user!!)

        )
    }

    fun mapToAgoraChatTokenDomainModel(dto: GetAgoraTokenUserDtoEntity): GetAgoraTokenUserDomainEntity {
        return GetAgoraTokenUserDomainEntity(
            id = dto._id ?: APP_EMPTY_STRING,
            chatToken = dto.agoraChatLoginDetails?.chatToken ?: APP_EMPTY_STRING,
            userName = dto.agoraChatLoginDetails?.userName ?: APP_EMPTY_STRING,
            updatedAt = dto.updatedAt ?: APP_EMPTY_STRING,


        )
    }

}