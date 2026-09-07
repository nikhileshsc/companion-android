package com.companion.astrodating.ui.onlineusers.data.mapper

import com.companion.astrodating.ui.onlineusers.data.dto.GetOnlineUsersDto
import com.companion.astrodating.ui.onlineusers.data.dto.GetOnlineUsersDtoEntity
import com.companion.astrodating.ui.onlineusers.domain.model.GetOnlineUsersDomain
import com.companion.astrodating.ui.onlineusers.domain.model.GetOnlineUsersDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetOnlineUsersMapper @Inject constructor() {
    fun mapToDomainModel(dto: GetOnlineUsersDto): GetOnlineUsersDomain {
        return GetOnlineUsersDomain(
            users = toDomainList(dto.data?.users ?: emptyList())
        )
    }

    private fun toDomainList(list: List<GetOnlineUsersDtoEntity>): List<GetOnlineUsersDomainEntity> {
        return list.map {
            GetOnlineUsersDomainEntity(
                _id = it._id ?: APP_EMPTY_STRING,
                fullName = it.fullName ?: APP_EMPTY_STRING,
                profileUrl = it.profileUrl ?: APP_EMPTY_STRING,
                age = it.age ?: 0,
                status = it.status ?: false,
                distance = it.distance ?: APP_EMPTY_STRING
            )
        }
    }
}