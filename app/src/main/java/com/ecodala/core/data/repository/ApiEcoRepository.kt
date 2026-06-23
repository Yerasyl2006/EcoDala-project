package com.ecodala.core.data.repository

import com.ecodala.core.data.remote.EcoDalaApi
import com.ecodala.core.data.local.EcoLocalCache
import com.ecodala.core.data.remote.MultipartRequestFactory
import com.ecodala.core.data.remote.NetworkModule
import com.ecodala.core.data.remote.dto.WasteSubmissionRequestDto
import com.ecodala.core.data.remote.dto.toApiValue
import com.ecodala.core.data.remote.dto.toDomain
import com.ecodala.core.domain.model.Achievement
import com.ecodala.core.domain.model.Biotoilet
import com.ecodala.core.domain.model.Challenge
import com.ecodala.core.domain.model.EcoReport
import com.ecodala.core.domain.model.EcoReportSeverity
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
        runCatching { api.getMe().toDomain() }
            .onSuccess { EcoLocalCache.saveProfile(it) }
            .getOrElse { error -> EcoLocalCache.getProfile() ?: throw error }
    }

    suspend fun recyclingPoints(wasteType: WasteType? = null): Result<List<RecyclingPoint>> = runCatching {
        runCatching { api.getRecyclingPoints(wasteType?.toApiValue()).results.map { it.toDomain() } }
            .onSuccess { points ->
                if (wasteType == null) {
                    val cached = EcoLocalCache.getMapData()
                    EcoLocalCache.saveMapData(points, cached?.biotoilets.orEmpty(), cached?.waterStations.orEmpty(), cached?.ecoReports.orEmpty())
                }
            }
            .getOrElse { error ->
                EcoLocalCache.getMapData()?.points?.takeIf { it.isNotEmpty() } ?: throw error
            }
    }

    suspend fun recyclingPoint(id: String): Result<RecyclingPoint> = runCatching {
        api.getRecyclingPoint(id).toDomain()
    }

    suspend fun biotoilets(): Result<List<Biotoilet>> = runCatching {
        runCatching { api.getBiotoilets().results.map { it.toDomain() } }
            .onSuccess { toilets ->
                val cached = EcoLocalCache.getMapData()
                EcoLocalCache.saveMapData(cached?.points.orEmpty(), toilets, cached?.waterStations.orEmpty(), cached?.ecoReports.orEmpty())
            }
            .getOrElse { error ->
                EcoLocalCache.getMapData()?.biotoilets?.takeIf { it.isNotEmpty() } ?: throw error
            }
    }

    suspend fun biotoilet(id: String): Result<Biotoilet> = runCatching {
        api.getBiotoilet(id).toDomain()
    }

    suspend fun waterStations(): Result<List<WaterStation>> = runCatching {
        runCatching { api.getWaterStations().results.map { it.toDomain() } }
            .onSuccess { stations ->
                val cached = EcoLocalCache.getMapData()
                EcoLocalCache.saveMapData(cached?.points.orEmpty(), cached?.biotoilets.orEmpty(), stations, cached?.ecoReports.orEmpty())
            }
            .getOrElse { error ->
                EcoLocalCache.getMapData()?.waterStations?.takeIf { it.isNotEmpty() } ?: throw error
            }
    }

    suspend fun waterStation(id: String): Result<WaterStation> = runCatching {
        api.getWaterStation(id).toDomain()
    }

    suspend fun ecoReports(): Result<List<EcoReport>> = runCatching {
        runCatching { api.getEcoReports().results.map { it.toDomain() } }
            .onSuccess { reports ->
                val cached = EcoLocalCache.getMapData()
                EcoLocalCache.saveMapData(cached?.points.orEmpty(), cached?.biotoilets.orEmpty(), cached?.waterStations.orEmpty(), reports)
            }
            .getOrElse { error ->
                EcoLocalCache.getMapData()?.ecoReports?.takeIf { it.isNotEmpty() } ?: throw error
            }
    }

    suspend fun ecoReport(id: String): Result<EcoReport> = runCatching {
        api.getEcoReport(id).toDomain()
    }

    suspend fun submitWaste(
        type: WasteType,
        quantity: Double,
        unit: String,
        comment: String?,
        photoPath: String? = null
    ): Result<WasteSubmission> = runCatching {
        val category = api.getWasteCategories().results
            .firstOrNull { it.slug.equals(type.toApiValue(), ignoreCase = true) }
            ?: error("Waste category '${type.toApiValue()}' was not found on backend.")
        val point = api.getRecyclingPoints(type.toApiValue()).results
            .firstOrNull { point -> point.acceptedCategories.any { it.id == category.id || it.slug == category.slug } }
            ?: api.getRecyclingPoints().results.firstOrNull()
            ?: error("No recycling point was found on backend.")

        val weightKg = when (unit.lowercase()) {
            "g", "gram", "grams" -> (quantity / 1000.0).toString()
            else -> quantity.toString()
        }
        val photo = MultipartRequestFactory.imagePart("photo", photoPath)

        if (photo != null) {
            api.submitWasteMultipart(
                category = MultipartRequestFactory.text(category.id),
                recyclingPoint = MultipartRequestFactory.text(point.id),
                weightKg = MultipartRequestFactory.text(weightKg),
                comment = MultipartRequestFactory.optionalText(comment),
                photo = photo
            )
        } else {
            api.submitWaste(
                WasteSubmissionRequestDto(
                    category = category.id,
                    recyclingPoint = point.id,
                    weightKg = weightKg,
                    comment = comment?.takeIf { it.isNotBlank() }
                )
            )
        }.toDomain(SessionManager.session.value.userId ?: "me")
    }

    suspend fun createEcoReport(
        title: String,
        address: String,
        latitude: Double,
        longitude: Double,
        wasteDescription: String,
        severity: EcoReportSeverity,
        photoPath: String?
    ): Result<EcoReport> = runCatching {
        api.createEcoReport(
            title = MultipartRequestFactory.text(title),
            address = MultipartRequestFactory.text(address),
            latitude = MultipartRequestFactory.text(latitude.toString()),
            longitude = MultipartRequestFactory.text(longitude.toString()),
            wasteDescription = MultipartRequestFactory.text(wasteDescription),
            severity = MultipartRequestFactory.text(severity.name.lowercase()),
            photo = MultipartRequestFactory.imagePart("photo", photoPath)
        ).toDomain()
    }

    suspend fun uploadEcoReportPhoto(
        reportId: String,
        photoPath: String,
        comment: String? = null
    ): Result<EcoReport> = runCatching {
        val photo = MultipartRequestFactory.imagePart("photo", photoPath)
            ?: error("Photo file was not found.")
        api.uploadEcoReportPhoto(
            id = reportId,
            photo = photo,
            comment = MultipartRequestFactory.optionalText(comment)
        ).toDomain()
    }

    suspend fun submissions(): Result<List<WasteSubmission>> = runCatching {
        runCatching {
            api.getWasteSubmissions().results.map { it.toDomain(SessionManager.session.value.userId ?: "me") }
        }
            .onSuccess { EcoLocalCache.saveHistory(it) }
            .getOrElse { error -> EcoLocalCache.getHistory()?.takeIf { it.isNotEmpty() } ?: throw error }
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

    suspend fun scanWaste(hint: String, imagePath: String? = null): Result<ScannerResult> = runCatching {
        val image = MultipartRequestFactory.imagePart("image", imagePath)
        if (image != null) {
            api.scanWasteImage(
                image = image,
                provider = MultipartRequestFactory.text(hint.ifBlank { "android" })
            )
        } else {
            api.scanWaste(provider = hint.ifBlank { "demo" })
        }.toDomain()
    }
}
