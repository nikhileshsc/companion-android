package com.companion.astrodating.ui.uploadKyc.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.managePhotos.data.dto.UploadPhotoDto
import com.companion.astrodating.ui.managePhotos.domain.model.UploadPhotoDomain
import com.companion.astrodating.ui.uploadKyc.data.dto.GetKycDto
import com.companion.astrodating.ui.uploadKyc.data.mapper.UploadKycMapper
import com.companion.astrodating.ui.uploadKyc.domain.model.GetKycDomain
import com.companion.astrodating.ui.uploadKyc.domain.repository.IUploadKycRepository
import com.google.gson.Gson
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadKycRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: UploadKycMapper
) : IUploadKycRepository {
    override suspend fun getKyc(token: String): ApiResult<GetKycDomain> {
        val result = api.getKyc(token)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToGetKycDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetKycDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }

    override suspend fun uploadKycDocument(
        token: String,
        fileType: String,
        image: MultipartBody.Part
    ): ApiResult<UploadPhotoDomain> {
        val result = api.uploadKycDocument(token,fileType, image)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToUploadPhotoUserDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), UploadPhotoDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}