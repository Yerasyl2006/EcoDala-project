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
import com.ecodala.core.data.remote.dto.RefreshTokenRequestDto
import com.ecodala.core.data.remote.dto.RegisterRequestDto
import com.ecodala.core.data.remote.dto.ScannerResultDto
import com.ecodala.core.data.remote.dto.UserDto
import com.ecodala.core.data.remote.dto.WasteCategoryDto
import com.ecodala.core.data.remote.dto.WasteSubmissionDto
import com.ecodala.core.data.remote.dto.WasteSubmissionRequestDto
import com.ecodala.core.data.remote.dto.WaterStationDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface EcoDalaApi {
    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequestDto): JwtTokenDto

    @POST("auth/register/")
    suspend fun register(@Body request: RegisterRequestDto): UserDto

    @POST("auth/token/refresh/")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): JwtTokenDto

    @GET("auth/me/")
    suspend fun getMe(): UserDto

    @GET("recycling-points/")
    suspend fun getRecyclingPoints(@Query("search") search: String? = null): PaginatedResponseDto<RecyclingPointDto>

    @GET("recycling-points/{id}/")
    suspend fun getRecyclingPoint(@Path("id") id: String): RecyclingPointDto

    @GET("waste-categories/")
    suspend fun getWasteCategories(): PaginatedResponseDto<WasteCategoryDto>

    @GET("biotoilets/")
    suspend fun getBiotoilets(
        @Query("free") free: Boolean? = null,
        @Query("accessible") accessible: Boolean? = null,
        @Query("open_now") openNow: Boolean? = null
    ): PaginatedResponseDto<BiotoiletDto>

    @GET("biotoilets/{id}/")
    suspend fun getBiotoilet(@Path("id") id: String): BiotoiletDto

    @GET("water-stations/")
    suspend fun getWaterStations(
        @Query("free") free: Boolean? = null,
        @Query("open_now") openNow: Boolean? = null,
        @Query("refill") refill: Boolean? = null
    ): PaginatedResponseDto<WaterStationDto>

    @GET("water-stations/{id}/")
    suspend fun getWaterStation(@Path("id") id: String): WaterStationDto

    @GET("eco-reports/")
    suspend fun getEcoReports(
        @Query("status") status: String? = null,
        @Query("severity") severity: String? = null
    ): PaginatedResponseDto<EcoReportDto>

    @GET("eco-reports/{id}/")
    suspend fun getEcoReport(@Path("id") id: String): EcoReportDto

    @POST("eco-reports/{id}/verify/")
    suspend fun verifyEcoReport(@Path("id") id: String): EcoReportDto

    @Multipart
    @POST("eco-reports/")
    suspend fun createEcoReport(
        @Part("title") title: RequestBody,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("waste_description") wasteDescription: RequestBody,
        @Part("severity") severity: RequestBody,
        @Part photo: MultipartBody.Part? = null
    ): EcoReportDto

    @Multipart
    @POST("eco-reports/{id}/photo/")
    suspend fun uploadEcoReportPhoto(
        @Path("id") id: String,
        @Part photo: MultipartBody.Part,
        @Part("comment") comment: RequestBody? = null
    ): EcoReportDto

    @GET("waste-submissions/")
    suspend fun getWasteSubmissions(): PaginatedResponseDto<WasteSubmissionDto>

    @POST("submit-waste/")
    suspend fun submitWaste(@Body request: WasteSubmissionRequestDto): WasteSubmissionDto

    @Multipart
    @POST("submit-waste/")
    suspend fun submitWasteMultipart(
        @Part("category") category: RequestBody,
        @Part("recycling_point") recyclingPoint: RequestBody,
        @Part("weight_kg") weightKg: RequestBody,
        @Part("comment") comment: RequestBody? = null,
        @Part photo: MultipartBody.Part? = null
    ): WasteSubmissionDto

    @GET("challenges/")
    suspend fun getChallenges(@Query("type") type: String? = null): PaginatedResponseDto<ChallengeDto>

    @GET("achievements/")
    suspend fun getAchievements(): PaginatedResponseDto<AchievementDto>

    @GET("leaderboard/")
    suspend fun getLeaderboard(): List<LeaderboardUserDto>

    @FormUrlEncoded
    @POST("ai-waste-scanner/analyze/")
    suspend fun scanWaste(@Field("provider") provider: String = "demo"): ScannerResultDto

    @Multipart
    @POST("ai-waste-scanner/analyze/")
    suspend fun scanWasteImage(
        @Part image: MultipartBody.Part,
        @Part("provider") provider: RequestBody
    ): ScannerResultDto
}
