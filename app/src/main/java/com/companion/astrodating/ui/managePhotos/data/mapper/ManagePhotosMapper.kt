package com.companion.astrodating.ui.managePhotos.data.mapper

import com.companion.astrodating.ui.managePhotos.data.dto.DeletePhotoDto
import com.companion.astrodating.ui.managePhotos.data.dto.GetMyPhotoGalleryDtoEntity
import com.companion.astrodating.ui.managePhotos.data.dto.GetMyPhotosDto
import com.companion.astrodating.ui.managePhotos.data.dto.SetProfilePhotoDto
import com.companion.astrodating.ui.managePhotos.data.dto.UploadPhotoDto
import com.companion.astrodating.ui.managePhotos.domain.model.DeletePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.GetMyPhotoGalleryDomainEntity
import com.companion.astrodating.ui.managePhotos.domain.model.GetMyPhotosUserDomain
import com.companion.astrodating.ui.managePhotos.domain.model.SetProfilePhotoDomain
import com.companion.astrodating.ui.managePhotos.domain.model.UploadPhotoDomain
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class ManagePhotosMapper @Inject constructor() {

    fun mapToDomainModel(dto: GetMyPhotosDto): GetMyPhotosUserDomain {
        return GetMyPhotosUserDomain(
            id = dto.data?.user?._id ?: APP_EMPTY_STRING,
            gallery = toDomainList(dto.data?.user?.gallery ?: emptyList()),
            maxGalleryCount = dto.data?.user?.maxGalleryCount ?: 0,
        )
    }

    private fun toDomainList(list: List<GetMyPhotoGalleryDtoEntity>): List<GetMyPhotoGalleryDomainEntity> {
        return list.map {
            GetMyPhotoGalleryDomainEntity(
                id = it._id ?: APP_EMPTY_STRING,
                createdAt = it.createdAt ?: APP_EMPTY_STRING,
                galleryUrl = it.galleryUrl ?: APP_EMPTY_STRING,
                isActionTaken = it.isActionTaken ?: false,
                status = it.status ?: APP_EMPTY_STRING,
                updatedAt = it.updatedAt ?: APP_EMPTY_STRING,
            )
        }
    }

    fun mapToSetProfilePhotoUserDomainModel(dto: SetProfilePhotoDto): SetProfilePhotoDomain {
        return SetProfilePhotoDomain(
            id = dto.data?.user?._id ?: APP_EMPTY_STRING,
            agoraChatUid = dto.data?.user?.agoraChatUid ?: APP_EMPTY_STRING,
            fullName = dto.data?.user?.fullName ?: APP_EMPTY_STRING,
            gallery = toDomainList(dto.data?.user?.gallery ?: emptyList()),
            profileUrl = dto.data?.user?.profileUrl ?: APP_EMPTY_STRING,
            updatedAt = dto.data?.user?.updatedAt ?: APP_EMPTY_STRING,
        )
    }

    fun mapToDeletePhotoUserDomainModel(dto: DeletePhotoDto): DeletePhotoDomain {
        return DeletePhotoDomain(
            id = dto.data?.user?._id ?: APP_EMPTY_STRING,
            gallery = toDomainList(dto.data?.user?.gallery ?: emptyList()),
            profileUrl = dto.data?.user?.profileUrl ?: APP_EMPTY_STRING,
            updatedAt = dto.data?.user?.updatedAt ?: APP_EMPTY_STRING,
        )
    }

    fun mapToUploadPhotoUserDomainModel(dto: UploadPhotoDto): UploadPhotoDomain {
        return UploadPhotoDomain(
            url = dto.data?.url ?: APP_EMPTY_STRING,
            message= dto.message ?: APP_EMPTY_STRING,
            statusCode = dto.statusCode ?: 0,
        )
    }



}