package com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.dto

data class GetMasterDto(
    val `data`: GetMasterDtoData? = null,
    val message: String? = null,
    val statusCode: Int? = null
)

data class GetMasterDtoData(
    val masterData: List<MasterDataDtoEntity>? = null
)

data class MasterDataDtoEntity(
    val _id: String? = null,
    val isActive: Boolean? = null,
    val max: Int? = null,
    val min: Int? = null,
    val type: String? = null,
    val values: List<String>? = null,
    val whichKindOfData: String? = null
)
