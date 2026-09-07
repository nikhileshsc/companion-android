package com.companion.astrodating.ui.uploadKyc.data.mapper

import com.companion.astrodating.ui.managePhotos.data.dto.UploadPhotoDto
import com.companion.astrodating.ui.managePhotos.domain.model.UploadPhotoDomain
import com.companion.astrodating.ui.uploadKyc.data.dto.GetKycDto
import com.companion.astrodating.ui.uploadKyc.domain.model.DocumentDomainEntity
import com.companion.astrodating.ui.uploadKyc.domain.model.GetKycDomain
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class UploadKycMapper @Inject constructor() {

    fun mapToGetKycDomainModel(dto: GetKycDto): GetKycDomain {
        return GetKycDomain(
            id = dto.data?.user?._id ?: APP_EMPTY_STRING, aadharCardFront = DocumentDomainEntity(
                id = dto.data?.user?.aadharCardDetails?.front?._id ?: APP_EMPTY_STRING,
                createdAt = dto.data?.user?.aadharCardDetails?.front?.createdAt ?: APP_EMPTY_STRING,
                documentKey = dto.data?.user?.aadharCardDetails?.front?.documentKey
                    ?: APP_EMPTY_STRING,
                documentUrl = dto.data?.user?.aadharCardDetails?.front?.documentUrl
                    ?: APP_EMPTY_STRING,
                status = dto.data?.user?.aadharCardDetails?.front?.status ?: APP_EMPTY_STRING,
                updatedAt = dto.data?.user?.aadharCardDetails?.front?.updatedAt ?: APP_EMPTY_STRING,
            ), aadharCardBack = DocumentDomainEntity(
                id = dto.data?.user?.aadharCardDetails?.back?._id ?: APP_EMPTY_STRING,
                createdAt = dto.data?.user?.aadharCardDetails?.back?.createdAt ?: APP_EMPTY_STRING,
                documentKey = dto.data?.user?.aadharCardDetails?.back?.documentKey
                    ?: APP_EMPTY_STRING,
                documentUrl = dto.data?.user?.aadharCardDetails?.back?.documentUrl
                    ?: APP_EMPTY_STRING,
                status = dto.data?.user?.aadharCardDetails?.back?.status ?: APP_EMPTY_STRING,
                updatedAt = dto.data?.user?.aadharCardDetails?.back?.updatedAt ?: APP_EMPTY_STRING,
            ), drivingLicenseFront = DocumentDomainEntity(
                id = dto.data?.user?.drivingLicense?.front?._id ?: APP_EMPTY_STRING,
                createdAt = dto.data?.user?.drivingLicense?.front?.createdAt ?: APP_EMPTY_STRING,
                documentKey = dto.data?.user?.drivingLicense?.front?.documentKey
                    ?: APP_EMPTY_STRING,
                documentUrl = dto.data?.user?.drivingLicense?.front?.documentUrl
                    ?: APP_EMPTY_STRING,
                status = dto.data?.user?.drivingLicense?.front?.status ?: APP_EMPTY_STRING,
                updatedAt = dto.data?.user?.drivingLicense?.front?.updatedAt ?: APP_EMPTY_STRING,
            ), panCardDetailsFront = DocumentDomainEntity(
                id = dto.data?.user?.panCardDetails?.front?._id ?: APP_EMPTY_STRING,
                createdAt = dto.data?.user?.panCardDetails?.front?.createdAt ?: APP_EMPTY_STRING,
                documentKey = dto.data?.user?.panCardDetails?.front?.documentKey
                    ?: APP_EMPTY_STRING,
                documentUrl = dto.data?.user?.panCardDetails?.front?.documentUrl
                    ?: APP_EMPTY_STRING,
                status = dto.data?.user?.panCardDetails?.front?.status ?: APP_EMPTY_STRING,
                updatedAt = dto.data?.user?.panCardDetails?.front?.updatedAt ?: APP_EMPTY_STRING,
            )
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