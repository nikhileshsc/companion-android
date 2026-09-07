package com.companion.astrodating.ui.uploadKyc.domain.model

data class GetKycDomain(
    val id: String,
    val aadharCardFront: DocumentDomainEntity,
    val aadharCardBack: DocumentDomainEntity,
    val drivingLicenseFront: DocumentDomainEntity,
    val panCardDetailsFront: DocumentDomainEntity
)


data class DocumentDomainEntity(
    val id: String,
    val createdAt: String,
    val documentKey: String,
    val documentUrl: String,
    val status: String,
    val updatedAt: String
)