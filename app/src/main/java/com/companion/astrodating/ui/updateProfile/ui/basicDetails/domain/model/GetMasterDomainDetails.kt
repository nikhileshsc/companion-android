package com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model

data class GetMasterDomainDetails(
    val masterData: List<MasterDataDomainEntity>
)
data class MasterDataDomainEntity(
    val id: String,
    val isActive: Boolean,
    val max: Int,
    val min: Int,
    val type: String,
    val values: List<String>,
    val whichKindOfData: String
)
