package com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.mapper

import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.dto.GetMasterDto
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.data.dto.MasterDataDtoEntity
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.GetMasterDomainDetails
import com.companion.astrodating.ui.updateProfile.ui.basicDetails.domain.model.MasterDataDomainEntity
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetMasterDataDetailsMapper @Inject constructor() {

    fun mapToDomainModel(dto: GetMasterDto): GetMasterDomainDetails {
        return GetMasterDomainDetails(
            masterData =toDomainList( dto.data?.masterData ?: emptyList())

        )
    }

    private fun toDomainList(list: List<MasterDataDtoEntity>): List<MasterDataDomainEntity> {
        return list.map {
            MasterDataDomainEntity(
                id = it._id ?: APP_EMPTY_STRING,
                isActive = it.isActive ?: false,
                max = it.max ?: 0,
                min = it.min ?: 0,
                type = it.type ?: APP_EMPTY_STRING,
                values = it.values ?: emptyList(),
                whichKindOfData = it.whichKindOfData ?: APP_EMPTY_STRING,

            )
        }
    }


}