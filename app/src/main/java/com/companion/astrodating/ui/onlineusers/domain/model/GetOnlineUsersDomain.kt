package com.companion.astrodating.ui.onlineusers.domain.model

data class GetOnlineUsersDomain(
    val users: List<GetOnlineUsersDomainEntity>
)
data class GetOnlineUsersDomainEntity(
    val _id: String,
    val fullName: String,
    val profileUrl: String,
    val age: Int,
    val status: Boolean,
    val distance: String
)