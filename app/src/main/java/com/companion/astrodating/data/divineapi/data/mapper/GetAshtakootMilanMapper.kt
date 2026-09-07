package com.companion.astrodating.data.divineapi.data.mapper

import com.companion.astrodating.data.divineapi.data.dto.AshtakootMilanDto
import com.companion.astrodating.data.divineapi.domain.model.AshtakootMilanDomainDetails
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetAshtakootMilanMapper @Inject constructor() {

    fun mapToDomainModel(dto: AshtakootMilanDto): AshtakootMilanDomainDetails {
        return AshtakootMilanDomainDetails(
            data = mapToAshtakootMilanDomainModel(dto.data ?: AshtakootMilanDto.AshtakootMilanDataDto() ),
            msg = mapToMsgDomainModel(dto.msg),
            success = dto.success?:0

        )
    }

    fun mapToAshtakootMilanDomainModel(data: AshtakootMilanDto.AshtakootMilanDataDto): AshtakootMilanDomainDetails.AshtakootMilanDomain {
        return AshtakootMilanDomainDetails.AshtakootMilanDomain(
            ashtakoot_milan = mapToAshtakootMilanDomainModel(data.ashtakoot_milan ?: AshtakootMilanDto.AshtakootMilanDtoEntity()),
            ashtakoot_milan_result = mapToAshtakootMilanResultDomainModel(data.ashtakoot_milan_result ?:AshtakootMilanDto.AshtakootMilanResultDtoEntity() ),
            bhakoot_dosha = data.bhakoot_dosha ?: APP_EMPTY_STRING,
            manglik_dosha = mapToManglikDoshaDomainModel(data.manglik_dosha ?: AshtakootMilanDto.ManglikDoshaDtoEntity()),
            nadi_dosha = data.nadi_dosha ?: APP_EMPTY_STRING
        )
    }

    fun mapToAshtakootMilanDomainModel(dto: AshtakootMilanDto.AshtakootMilanDtoEntity): AshtakootMilanDomainDetails.AshtakootMilanDomainEntity {
        return AshtakootMilanDomainDetails.AshtakootMilanDomainEntity(
            varna = mapToVarnaDomainModel(dto.varna ?:AshtakootMilanDto.AshtagunaDtoEntity()),
            vashya = mapToVashyaDomainModel(dto.vashya ?:AshtakootMilanDto.AshtagunaDtoEntity()),
            tara = mapToTaraDomainModel(dto.tara ?:AshtakootMilanDto.AshtagunaDtoEntity()),
            yoni = mapToYoniDomainModel(dto.yoni ?:AshtakootMilanDto.AshtagunaDtoEntity()),
            graha_maitri = mapToGrahaMaitriDomainModel(dto.graha_maitri ?:AshtakootMilanDto.AshtagunaDtoEntity()),
            gana = mapToGanaDomainModel(dto.gana ?:AshtakootMilanDto.AshtagunaDtoEntity()),
            bhakoota = mapToBhakootaDomainModel(dto.bhakoota ?:AshtakootMilanDto.AshtagunaDtoEntity()),
            nadi = mapToNadiDomainModel(dto.nadi ?:AshtakootMilanDto.AshtagunaDtoEntity()),
        )
    }

    fun mapToVarnaDomainModel(dto: AshtakootMilanDto.AshtagunaDtoEntity): AshtakootMilanDomainDetails.AshtagunaDomainEntity {
        return AshtakootMilanDomainDetails.AshtagunaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            description = dto.description ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToVashyaDomainModel(dto: AshtakootMilanDto.AshtagunaDtoEntity): AshtakootMilanDomainDetails.AshtagunaDomainEntity {
        return AshtakootMilanDomainDetails.AshtagunaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            description = dto.description ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToTaraDomainModel(dto: AshtakootMilanDto.AshtagunaDtoEntity): AshtakootMilanDomainDetails.AshtagunaDomainEntity {
        return AshtakootMilanDomainDetails.AshtagunaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            description = dto.description ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToYoniDomainModel(dto: AshtakootMilanDto.AshtagunaDtoEntity): AshtakootMilanDomainDetails.AshtagunaDomainEntity {
        return AshtakootMilanDomainDetails.AshtagunaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            description = dto.description ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToGrahaMaitriDomainModel(dto: AshtakootMilanDto.AshtagunaDtoEntity): AshtakootMilanDomainDetails.AshtagunaDomainEntity {
        return AshtakootMilanDomainDetails.AshtagunaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            description = dto.description ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToGanaDomainModel(dto: AshtakootMilanDto.AshtagunaDtoEntity): AshtakootMilanDomainDetails.AshtagunaDomainEntity {
        return AshtakootMilanDomainDetails.AshtagunaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            description = dto.description ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToBhakootaDomainModel(dto: AshtakootMilanDto.AshtagunaDtoEntity): AshtakootMilanDomainDetails.AshtagunaDomainEntity {
        return AshtakootMilanDomainDetails.AshtagunaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            description = dto.description ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToNadiDomainModel(dto: AshtakootMilanDto.AshtagunaDtoEntity): AshtakootMilanDomainDetails.AshtagunaDomainEntity {
        return AshtakootMilanDomainDetails.AshtagunaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            description = dto.description ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToAshtakootMilanResultDomainModel(dto: AshtakootMilanDto.AshtakootMilanResultDtoEntity): AshtakootMilanDomainDetails.AshtakootMilanResultDomainEntity {
        return AshtakootMilanDomainDetails.AshtakootMilanResultDomainEntity(
            content = dto.content ?: APP_EMPTY_STRING,
            is_compatible = dto.is_compatible ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToManglikDoshaDomainModel(dto: AshtakootMilanDto.ManglikDoshaDtoEntity): AshtakootMilanDomainDetails.ManglikDoshaDomainEntity {
        return AshtakootMilanDomainDetails.ManglikDoshaDomainEntity(
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
        )
    }

    fun mapToMsgDomainModel(dto: AshtakootMilanDto.MsgDtoEntity?): String {
        return DivineApiMsgFormatter.format(dto?.p1, dto?.p2)
    }


    /*  private fun toDomainList(list: List<GetUserDetailsGalleryDto>): List<GetUserDetailsGalleryDomain> {
          return list.map {
              GetUserDetailsGalleryDomain(
                  id = it._id ?: APP_EMPTY_STRING,
                  createdAt = it.createdAt ?: APP_EMPTY_STRING,
                  galleryUrl = it.galleryUrl ?: APP_EMPTY_STRING,
                  isActionTaken = it.isActionTaken ?: false,
                  status = it.status ?: APP_EMPTY_STRING,
                  updatedAt = it.updatedAt ?: APP_EMPTY_STRING
              )
          }
      }*/


}
