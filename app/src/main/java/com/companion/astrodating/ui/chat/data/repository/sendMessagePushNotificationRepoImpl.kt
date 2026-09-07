package com.companion.astrodating.ui.chat.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.chat.data.mapper.sendMessagePushNotificationMapper
import com.companion.astrodating.ui.chat.data.requestData.sendMessagePushNotificationData
import com.companion.astrodating.ui.chat.domain.model.sendMessagePushNotificationDomain
import com.companion.astrodating.ui.chat.domain.repository.sendMesssagePushNotificationRepository
import com.companion.astrodating.ui.purchasePlans.data.dto.CreateInAppSubscriptionDto
import com.google.gson.Gson
import javax.inject.Inject

class sendMessagePushNotificationRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: sendMessagePushNotificationMapper
): sendMesssagePushNotificationRepository {
    override suspend fun sendMessagePushNotification(
        token: String,
        requestData: sendMessagePushNotificationData
    ): ApiResult<sendMessagePushNotificationDomain> {
        val result = api.sendMessagePushNotification(
            token,
            requestData
        )
        return if(result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse = Gson().fromJson(
                result.errorBody()!!.charStream(),
                CreateInAppSubscriptionDto::class.java
            )
            ApiResult.Error(result.code(), errorResponse.message!!)
        }
    }

}