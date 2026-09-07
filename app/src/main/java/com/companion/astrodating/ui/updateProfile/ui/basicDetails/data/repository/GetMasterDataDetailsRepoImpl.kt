package com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.dto.GetMasterDto
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.mapper.GetMasterDataDetailsMapper
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.GetMasterDomainDetails
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.repository.IGetMasterDetailsRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetMasterDataDetailsRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: GetMasterDataDetailsMapper
) : IGetMasterDetailsRepository {

    override suspend fun getMasterDataDetails(token: String): ApiResult<GetMasterDomainDetails> {
        val result = api.getMasterDataDetails(token)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetMasterDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message!!)
        }
    }
}