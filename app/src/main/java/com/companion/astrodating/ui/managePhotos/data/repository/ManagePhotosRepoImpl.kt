package com.companion.astrodating.ui.managePhotos.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.api.CompanionApi
import com.companion.astrodating.ui.managePhotos.data.dto.DeletePhotoDto
import com.companion.astrodating.ui.managePhotos.data.dto.GetMyPhotosDto
import com.companion.astrodating.ui.managePhotos.data.dto.SetProfilePhotoDto
import com.companion.astrodating.ui.managePhotos.data.dto.UploadPhotoDto
import com.companion.astrodating.ui.managePhotos.data.mapper.ManagePhotosMapper
import com.companion.astrodating.ui.managePhotos.data.requestData.DeletePhotoRequestData
import com.companion.astrodating.ui.managePhotos.data.requestData.SetProfilePhotoRequestData
import com.companion.astrodating.ui.managePhotos.domain.model.DeletePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.GetMyPhotosUserDomain
import com.companion.astrodating.ui.managePhotos.domain.model.SetProfilePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.UploadPhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.repository.IManagePhotosRepository
import com.google.gson.Gson
import okhttp3.MultipartBody
import javax.inject.Inject

class ManagePhotosRepoImpl @Inject constructor(
    private val api: CompanionApi,
    private val mapper: ManagePhotosMapper
) : IManagePhotosRepository {
    override suspend fun getMyPhotos(token: String): ApiResult<GetMyPhotosUserDomain> {
        val result = api.getMyPhotos(token)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), GetMyPhotosDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }

    override suspend fun setProfilePhoto(
        token: String,
        requestData: SetProfilePhotoRequestData
    ): ApiResult<SetProfilePhotoDomain> {
        val result = api.setProfilePhoto(token,requestData)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToSetProfilePhotoUserDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), SetProfilePhotoDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }

    override suspend fun deletePhoto(
        token: String,
        requestData: DeletePhotoRequestData
    ): ApiResult<DeletePhotoDomain> {
        val result = api.deletePhoto(token,requestData)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToDeletePhotoUserDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), DeletePhotoDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }

    override suspend fun uploadPhoto(
        token: String,
        fileType: String,
        image: MultipartBody.Part
    ): ApiResult<UploadPhotoDomain> {
        val result = api.uploadPhoto(token,fileType, image)
        return if (result.isSuccessful){
            ApiResult.Success(mapper.mapToUploadPhotoUserDomainModel(result.body()!!))
        }else{
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), UploadPhotoDto::class.java)
            ApiResult.Error(result.code(),errorResponse.message)
        }
    }
}