package com.ecodala.core.data.remote

import com.ecodala.core.data.remote.dto.AchievementDto
import com.ecodala.core.data.remote.dto.BiotoiletDto
import com.ecodala.core.data.remote.dto.ChallengeDto
import com.ecodala.core.data.remote.dto.EcoReportDto
import com.ecodala.core.data.remote.dto.JwtTokenDto
import com.ecodala.core.data.remote.dto.LeaderboardUserDto
import com.ecodala.core.data.remote.dto.LoginRequestDto
import com.ecodala.core.data.remote.dto.PaginatedResponseDto
import com.ecodala.core.data.remote.dto.RecyclingPointDto
import com.ecodala.core.data.remote.dto.RegisterRequestDto
import com.ecodala.core.data.remote.dto.ScannerResultDto
import com.ecodala.core.data.remote.dto.UserDto
import com.ecodala.core.data.remote.dto.WasteScanRequestDto
import com.ecodala.core.data.remote.dto.WasteSubmissionDto
import com.ecodala.core.data.remote.dto.WasteSubmissionRequestDto
import com.ecodala.core.data.remote.dto.WaterStationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EcoDalaApi {
    @POST("auth/token/")
    suspend fun login(@Body request: LoginRequestDto): JwtTokenDto

    @POST("auth/register/")
    suspend fun register(@Body request: RegisterRequestDto): UserDto

    @GET("auth/me/")
    suspend fun getMe(): UserDto

    @GET("places/recycling-points/")
    suspend fun getRecyclingPoints(@Query("waste_type") wasteType: String? = null): PaginatedResponseDto<RecyclingPointDto>

    @GET("places/recycling-points/{id}/")
    suspend fun getRecyclingPoint(@Path("id") id: String): RecyclingPointDto

    @GET("places/biotoilets/")
    suspend fun getBiotoilets(
        @Query("free") free: Boolean? = null,
        @Query("accessible") accessible: Boolean? = null,
        @Query("open_now") openNow: Boolean? = null
    ): PaginatedResponseDto<BiotoiletDto>

    @GET("places/biotoilets/{id}/")
    suspend fun getBiotoilet(@Path("id") id: String): BiotoiletDto

    @GET("places/water-stations/")
    suspend fun getWaterStations(
        @Query("free") free: Boolean? = null,
        @Query("open_now") openNow: Boolean? = null,
        @Query("refill") refill: Boolean? = null
    ): PaginatedResponseDto<WaterStationDto>

    @GET("places/water-stations/{id}/")
    suspend fun getWaterStation(@Path("id") id: String): WaterStationDto

    @GET("reports/eco-reports/")
    suspend fun getEcoReports(
        @Query("status") status: String? = null,
        @Query("severity") severity: String? = null
    ): PaginatedResponseDto<EcoReportDto>

    @GET("reports/eco-reports/{id}/")
    suspend fun getEcoReport(@Path("id") id: String): EcoReportDto

    @POST("reports/eco-reports/{id}/verify/")
    suspend fun verifyEcoReport(@Path("id") id: String): EcoReportDto

    @GET("reports/waste-submissions/")
    suspend fun getWasteSubmissions(): PaginatedResponseDto<WasteSubmissionDto>

    @POST("reports/waste-submissions/")
    suspend fun submitWaste(@Body request: WasteSubmissionRequestDto): WasteSubmissionDto

    @GET("gamification/challenges/")
    suspend fun getChallenges(@Query("type") type: String? = null): PaginatedResponseDto<ChallengeDto>

    @GET("gamification/achievements/")
    suspend fun getAchievements(): PaginatedResponseDto<AchievementDto>

    @GET("gamification/leaderboard/")
    suspend fun getLeaderboard(): PaginatedResponseDto<LeaderboardUserDto>

    @POST("scanner/scan/")
    suspend fun scanWaste(@Body request: WasteScanRequestDto): ScannerResultDto
}
