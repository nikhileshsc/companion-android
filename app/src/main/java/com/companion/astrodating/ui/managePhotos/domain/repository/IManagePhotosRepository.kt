package com.companion.astrodating.ui.managePhotos.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.managePhotos.data.requestData.DeletePhotoRequestData
import com.companion.astrodating.ui.managePhotos.data.requestData.SetProfilePhotoRequestData
import com.companion.astrodating.ui.managePhotos.domain.model.DeletePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.GetMyPhotosUserDomain
import com.companion.astrodating.ui.managePhotos.domain.model.SetProfilePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.UploadPhotoDomain
import okhttp3.MultipartBody

interface IManagePhotosRepository {

    suspend fun getMyPhotos(token: String): ApiResult<GetMyPhotosUserDomain>
    suspend fun setProfilePhoto(token: String,requestData: SetProfilePhotoRequestData): ApiResult<SetProfilePhotoDomain>
    suspend fun deletePhoto(token: String,requestData: DeletePhotoRequestData): ApiResult<DeletePhotoDomain>

    suspend fun uploadPhoto(token: String, fileType: String, image: MultipartBody.Part): ApiResult<UploadPhotoDomain>
}