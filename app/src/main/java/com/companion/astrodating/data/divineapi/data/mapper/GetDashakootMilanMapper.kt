package com.companion.astrodating.data.divineapi.data.mapper

import com.companion.astrodating.data.divineapi.data.dto.DashakootMilanDto
import com.companion.astrodating.data.divineapi.domain.model.DashakootMilanDomainDetails
import com.companion.astrodating.util.APP_EMPTY_STRING
import javax.inject.Inject

class GetDashakootMilanMapper @Inject constructor() {

    fun mapToDomainModel(dto: DashakootMilanDto): DashakootMilanDomainDetails {
        return DashakootMilanDomainDetails(
            data = mapToDashakootMilanDomainModel(dto.data!!),
            msg = mapToMsgDomainModel(dto.msg),
            success = dto.success?:0

        )
    }
    fun mapToDashakootMilanDomainModel(data: DashakootMilanDto.DashakootMilanDataDto): DashakootMilanDomainDetails.DashakootMilanDataDomain {
        return DashakootMilanDomainDetails.DashakootMilanDataDomain(
            dashakoot_milan = mapToDashakootMilanDomainModel(data.dashakoot_milan!!),
            dashakoot_milan_result = mapToDashakootMilanResultDomainModel(data.dashakoot_milan_result!!),
            manglik_dosha = mapToManglikDoshaDomainModel(data.manglik_dosha!!),
            rajju_dosha = data.rajju_dosha ?: APP_EMPTY_STRING,
        )
    }

    fun mapToDashakootMilanDomainModel(dto: DashakootMilanDto.DashakootMilanDtoEntity): DashakootMilanDomainDetails.DashakootMilanDomainEntity {
        return DashakootMilanDomainDetails.DashakootMilanDomainEntity(
            dina = mapToDinaDomainModel(dto.dina!!),
            gana = mapToGanaDomainModel(dto.gana!!),
            mahendra = mapToMahendraDomainModel(dto.mahendra!!),
            rajju = mapToRajjuDomainModel(dto.rajju!!),
            rashi = mapToRashiDomainModel(dto.rashi!!),
            rasyadhipati = mapToRasyadhipatiDomainModel(dto.rasyadhipati!!),
            streedargha = mapToStreedarghaDomainModel(dto.streedargha!!),
            vashya = mapToVashyaDomainModel(dto.vashya!!),
            vedha = mapToVedhaDomainModel(dto.vedha!!),
            yoni = mapToYoniDomainModel(dto.yoni!!),



        )
    }

    fun mapToDinaDomainModel(dto: DashakootMilanDto.DinaDtoEntity): DashakootMilanDomainDetails.DinaDomainEntity {
        return DashakootMilanDomainDetails.DinaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING
        )
    }

    fun mapToStreedarghaDomainModel(dto: DashakootMilanDto.StreedarghaDtoEntity): DashakootMilanDomainDetails.StreedarghaDomainEntity {
        return DashakootMilanDomainDetails.StreedarghaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING
        )
    }

    fun mapToVashyaDomainModel(dto: DashakootMilanDto.VashyaDtoEntity): DashakootMilanDomainDetails.VashyaDomainEntity {
        return DashakootMilanDomainDetails.VashyaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING
        )
    }

    fun mapToMahendraDomainModel(dto: DashakootMilanDto.MahendraDtoEntity): DashakootMilanDomainDetails.MahendraDomainEntity {
        return DashakootMilanDomainDetails.MahendraDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING
        )
    }

    fun mapToYoniDomainModel(dto: DashakootMilanDto.YoniDtoEntity): DashakootMilanDomainDetails.YoniDomainEntity {
        return DashakootMilanDomainDetails.YoniDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING
        )
    }

    fun mapToRajjuDomainModel(dto: DashakootMilanDto.RajjuDtoEntity): DashakootMilanDomainDetails.RajjuDomainEntity {
        return DashakootMilanDomainDetails.RajjuDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING
        )
    }

    fun mapToGanaDomainModel(dto: DashakootMilanDto.GanaDtoEntity): DashakootMilanDomainDetails.GanaDomainEntity {
        return DashakootMilanDomainDetails.GanaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING
        )
    }

    fun mapToRashiDomainModel(dto: DashakootMilanDto.RashiDtoEntity): DashakootMilanDomainDetails.RashiDomainEntity {
        return DashakootMilanDomainDetails.RashiDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING,
        )
    }
    fun mapToVedhaDomainModel(dto: DashakootMilanDto.VedhaDtoEntity): DashakootMilanDomainDetails.VedhaDomainEntity {
        return DashakootMilanDomainDetails.VedhaDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING,
        )
    }

    fun mapToRasyadhipatiDomainModel(dto: DashakootMilanDto.RasyadhipatiDtoEntity): DashakootMilanDomainDetails.RasyadhipatiDomainEntity {
        return DashakootMilanDomainDetails.RasyadhipatiDomainEntity(
            area_of_life = dto.area_of_life ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
            points_obtained = dto.points_obtained ?: 0.0,
            result = dto.result ?: APP_EMPTY_STRING,
        )
    }

    fun mapToDashakootMilanResultDomainModel(dto: DashakootMilanDto.DashakootMilanResultDtoEntity): DashakootMilanDomainDetails.DashakootMilanResultDomainEntity {
        return DashakootMilanDomainDetails.DashakootMilanResultDomainEntity(
            content = dto.content ?: APP_EMPTY_STRING,
            is_compatible = dto.is_compatible ?: APP_EMPTY_STRING,
            max_ponits = dto.max_ponits ?: 0,
            points_obtained = dto.points_obtained ?: 0.0,
        )
    }

    fun mapToManglikDoshaDomainModel(dto: DashakootMilanDto.ManglikDoshaDtoEntity): DashakootMilanDomainDetails.ManglikDoshaDomainEntity {
        return DashakootMilanDomainDetails.ManglikDoshaDomainEntity(
            p1 = dto.p1 ?: APP_EMPTY_STRING,
            p2 = dto.p2 ?: APP_EMPTY_STRING,
        )
    }

    fun mapToMsgDomainModel(dto: DashakootMilanDto.MsgDtoEntity?): String {
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
