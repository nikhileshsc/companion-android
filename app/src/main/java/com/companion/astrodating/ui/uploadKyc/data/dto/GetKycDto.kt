package com.companion.astrodating.ui.uploadKyc.data.dto

data class GetKycDto(
    val `data`: GetKycDtoData? = null,
    val message: String,
    val statusCode: Int
)

data class GetKycDtoData(
    val user: GetKycUserDtoEntity? = null
)

data class GetKycUserDtoEntity(
    val _id: String? = null,
    val aadharCardDetails: AadharCardDetails? = null,
    val drivingLicense: DrivingLicense? = null,
    val panCardDetails: PanCardDetails? = null
)

data class AadharCardDetails(
    val back: DocumentDtoEntity? = null,
    val front: DocumentDtoEntity? = null
)

data class DrivingLicense(
    val front: DocumentDtoEntity? = null
)

data class PanCardDetails(
    val front: DocumentDtoEntity? = null
)

data class DocumentDtoEntity(
    val _id: String? = null,
    val createdAt: String? = null,
    val documentKey: String? = null,
    val documentUrl: String? = null,
    val status: String? = null,
    val updatedAt: String? = null
)