package com.companion.astrodating.ui.uploadKyc.domain.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.ui.managePhotos.domain.model.UploadPhotoDomain
import com.companion.astrodating.ui.uploadKyc.domain.model.GetKycDomain
import okhttp3.MultipartBody

interface IUploadKycRepository {

    suspend fun getKyc(token: String): ApiResult<GetKycDomain>
    suspend fun uploadKycDocument(token: String, fileType: String, image: MultipartBody.Part): ApiResult<UploadPhotoDomain>
}