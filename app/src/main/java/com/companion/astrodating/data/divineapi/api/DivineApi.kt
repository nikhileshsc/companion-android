package com.companion.astrodating.data.divineapi.api
import com.companion.astrodating.data.divineapi.data.dto.AshtakootMilanDto
import com.companion.astrodating.data.divineapi.data.dto.AstrologyDetailsDto
import com.companion.astrodating.data.divineapi.data.dto.DashakootMilanDto
import com.companion.astrodating.data.divineapi.data.dto.ManglikDoshaDto
import com.companion.astrodating.data.divineapi.data.dto.MatchingPlanetaryPositionDto
import com.companion.astrodating.data.divineapi.data.requestData.AshtakootMilanRequestData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DivineApi {

    @POST("v2/ashtakoot-milan")
    suspend fun getAshtakootMilanApi(
        @Header("Authorization") token: String,
        @Body body: AshtakootMilanRequestData
    ): Response<AshtakootMilanDto>

    @POST("v1/dashakoot-milan")
    suspend fun getDashakootMilanApi(
        @Header("Authorization") token: String,
        @Body body: AshtakootMilanRequestData
    ): Response<DashakootMilanDto>

    @POST("v2/matching/basic-astro-details")
    suspend fun getAstrologyDetailsApi(
        @Header("Authorization") token: String,
        @Body body: AshtakootMilanRequestData
    ): Response<AstrologyDetailsDto>
    @POST("v1/matching/manglik-dosha")
    suspend fun getManglikDoshaApi(
        @Header("Authorization") token: String,
        @Body body: AshtakootMilanRequestData
    ): Response<ManglikDoshaDto>

    @POST("v1/matching/planetary-positions")
    suspend fun getMatchingPlanetaryPositionsApi(
        @Header("Authorization") token: String,
        @Body body: AshtakootMilanRequestData
    ): Response<MatchingPlanetaryPositionDto>
}