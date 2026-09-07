package com.companion.astrodating.ui.onlineusers.data.dto

data class GetOnlineUsersDto(
    val `data`: GetOnlineUsersDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class GetOnlineUsersDtoData(
    val pageNumber: Int? = null,
    val perPage: Int? = null,
    val totalCount: Int? = null,
    val users: List<GetOnlineUsersDtoEntity>
)

data class GetOnlineUsersDtoEntity(
    val _id: String?,
    val fullName: String?,
    val profileUrl: String?,
    val age: Int?,
    val status: Boolean?,
    val distance: String?
)