package com.ecodala.core.data.repository

import com.ecodala.core.data.remote.EcoDalaApi
import com.ecodala.core.data.remote.NetworkModule
import com.ecodala.core.data.remote.dto.WasteSubmissionRequestDto
import com.ecodala.core.data.remote.dto.toApiValue
import com.ecodala.core.data.remote.dto.toDomain
import com.ecodala.core.domain.model.Achievement
import com.ecodala.core.domain.model.Biotoilet
import com.ecodala.core.domain.model.Challenge
import com.ecodala.core.domain.model.EcoReport
import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.LeaderboardEntry
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.ScannerResult
import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.domain.model.WaterStation
import com.ecodala.core.session.SessionManager

class ApiEcoRepository(
    private val api: EcoDalaApi = NetworkModule.api
) {
    suspend fun currentUser(): Result<EcoUser> = runCatching {
        api.getMe().toDomain()
    }

    suspend fun recyclingPoints(wasteType: WasteType? = null): Result<List<RecyclingPoint>> = runCatching {
        api.getRecyclingPoints(wasteType?.toApiValue()).results.map { it.toDomain() }
    }

    suspend fun recyclingPoint(id: String): Result<RecyclingPoint> = runCatching {
        api.getRecyclingPoint(id).toDomain()
    }

    suspend fun biotoilets(): Result<List<Biotoilet>> = runCatching {
        api.getBiotoilets().results.map { it.toDomain() }
    }

    suspend fun biotoilet(id: String): Result<Biotoilet> = runCatching {
        api.getBiotoilet(id).toDomain()
    }

    suspend fun waterStations(): Result<List<WaterStation>> = runCatching {
        api.getWaterStations().results.map { it.toDomain() }
    }

    suspend fun waterStation(id: String): Result<WaterStation> = runCatching {
        api.getWaterStation(id).toDomain()
    }

    suspend fun ecoReports(): Result<List<EcoReport>> = runCatching {
        api.getEcoReports().results.map { it.toDomain() }
    }

    suspend fun ecoReport(id: String): Result<EcoReport> = runCatching {
        api.getEcoReport(id).toDomain()
    }

    suspend fun submitWaste(type: WasteType, quantity: Double, unit: String, comment: String?): Result<WasteSubmission> = runCatching {
        val category = api.getWasteCategories().results
            .firstOrNull { it.slug.equals(type.toApiValue(), ignoreCase = true) }
            ?: error("Waste category '${type.toApiValue()}' was not found on backend.")
        val point = api.getRecyclingPoints(type.toApiValue()).results
            .firstOrNull { point -> point.acceptedCategories.any { it.id == category.id || it.slug == category.slug } }
            ?: api.getRecyclingPoints().results.firstOrNull()
            ?: error("No recycling point was found on backend.")

        api.submitWaste(
            WasteSubmissionRequestDto(
                category = category.id,
                recyclingPoint = point.id,
                weightKg = when (unit.lowercase()) {
                    "g", "gram", "grams" -> (quantity / 1000.0).toString()
                    else -> quantity.toString()
                },
                comment = comment?.takeIf { it.isNotBlank() }
            )
        ).toDomain(SessionManager.session.value.userId ?: "me")
    }

    suspend fun submissions(): Result<List<WasteSubmission>> = runCatching {
        api.getWasteSubmissions().results.map { it.toDomain(SessionManager.session.value.userId ?: "me") }
    }

    suspend fun challenges(): Result<List<Challenge>> = runCatching {
        api.getChallenges().results.map { it.toDomain() }
    }

    suspend fun achievements(): Result<List<Achievement>> = runCatching {
        api.getAchievements().results.map { it.toDomain() }
    }

    suspend fun leaderboard(): Result<List<LeaderboardEntry>> = runCatching {
        val currentUserId = SessionManager.session.value.userId
        api.getLeaderboard().mapIndexed { index, dto -> dto.toDomain(index, currentUserId) }
    }

    suspend fun scanWaste(hint: String): Result<ScannerResult> = runCatching {
        api.scanWaste(provider = hint.ifBlank { "demo" }).toDomain()
    }
}
