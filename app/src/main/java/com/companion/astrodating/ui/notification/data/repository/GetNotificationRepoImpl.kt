package com.companion.astrodating.ui.notification.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.notification.data.dto.NotificationDto
import com.companion.astrodating.ui.notification.data.mapper.GetNotificationMapper
import com.companion.astrodating.ui.notification.domain.model.NotificationDomainDetails
import com.companion.astrodating.ui.notification.domain.repository.IGetNotificationRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetNotificationRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetNotificationMapper
) : IGetNotificationRepository {

    override suspend fun getNotificationDetails(
        token: String,
        pageNumber: Int,
        perPage: Int
    ): ApiResult<NotificationDomainDetails> {
        val result = api.getNotification(token,pageNumber, perPage)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), NotificationDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}