package com.companion.astrodating.ui.home.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.home.domain.model.GetHomeUserDomain
import com.companion.astrodating.ui.home.domain.model.UpdateOnlineStatusDomain
import com.companion.astrodating.ui.home.data.requestData.updateLocationRequestData
import com.companion.astrodating.ui.home.data.requestData.updateOnlineStatusRequestData

interface IGetHomeUserRepository {

    suspend fun getHomeUserDetails(
        token: String,
        searchText: String,
        minHeight: Double,
        maxHeight: Double,
        minAge: Int,
        maxAge: Int,
        education: ArrayList<String>,
        profession: ArrayList<String>,
        religion: ArrayList<String>,
        country: ArrayList<String>,
        city: ArrayList<String>,
        status: ArrayList<String>,
        community: ArrayList<String>,
        pageNumber: Int,
        perPage: Int
    ): ApiResult<GetHomeUserDomain>

    suspend fun getOnlineStatusDetails(
        token: String,
        requestData: updateOnlineStatusRequestData
    ): ApiResult<UpdateOnlineStatusDomain>

//    suspend fun getUpdateLocationDetails(
//        token: String,
//        requestData: updateLocationRequestData
//    ): ApiResult<UpdateLocationDomain>
}