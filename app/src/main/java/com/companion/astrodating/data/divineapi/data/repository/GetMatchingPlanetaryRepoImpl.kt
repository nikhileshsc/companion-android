package com.companion.astrodating.data.divineapi.data.repository

import com.companion.astrodating.data.api.ApiResult
import com.companion.astrodating.data.divineapi.api.DivineApi
import com.companion.astrodating.data.divineapi.data.dto.MatchingPlanetaryPositionDto
import com.companion.astrodating.data.divineapi.data.mapper.GetMatchingPlanetaryMapper
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import com.companion.astrodating.data.divineapi.domain.model.MatchingPlanetaryPositionDomainDetails
import com.companion.astrodating.data.divineapi.domain.repository.IGetMatchingPlanetaryRepository
import com.google.gson.Gson
import javax.inject.Inject

class GetMatchingPlanetaryRepoImpl @Inject constructor(
    private val api: DivineApi,
    private val mapper: GetMatchingPlanetaryMapper
) : IGetMatchingPlanetaryRepository {
    override suspend fun getMatchingPlanetaryDetails(
        token: String,
        ashtakootMilanRequestData: AshtakootMilanRequestData
    ): ApiResult<MatchingPlanetaryPositionDomainDetails> {
        val result = api.getMatchingPlanetaryPositionsApi(token, ashtakootMilanRequestData)
        return if (result.isSuccessful) {
            ApiResult.Success(mapper.mapToDomainModel(result.body()!!))
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()!!.charStream(), MatchingPlanetaryPositionDto::class.java)
            if (errorResponse.success!! == 3){
                ApiResult.Error(errorResponse.success,"Invalid divine authorization token!")
            }else{
                ApiResult.Error(errorResponse.success,errorResponse!!.msg!!)
            }

        }
    }
}