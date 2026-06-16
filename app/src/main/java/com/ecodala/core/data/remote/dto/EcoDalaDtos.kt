package com.ecodala.core.data.remote.dto

import com.squareup.moshi.Json

data class PaginatedResponseDto<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String
)

data class JwtTokenDto(
    val access: String,
    val refresh: String
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "eco_points") val ecoPoints: Int = 0,
    val level: Int = 1,
    @Json(name = "global_rank") val globalRank: Int = 0,
    val avatar: String? = null
)

data class RecyclingPointDto(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String? = null,
    @Json(name = "opening_hours") val openingHours: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    @Json(name = "accepted_waste_types") val acceptedWasteTypes: List<String> = emptyList(),
    @Json(name = "reward_points") val rewardPoints: Int = 0
)

data class BiotoiletDto(
    val id: Int,
    val name: String,
    val photo: String? = null,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    @Json(name = "opening_hours") val openingHours: String,
    val status: String,
    val type: String,
    @Json(name = "is_accessible") val isAccessible: Boolean,
    @Json(name = "is_family_friendly") val isFamilyFriendly: Boolean,
    @Json(name = "cleanliness_rating") val cleanlinessRating: Double,
    @Json(name = "review_count") val reviewCount: Int = 0
)

data class WaterStationDto(
    val id: Int,
    val name: String,
    val photo: String? = null,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    @Json(name = "working_hours") val workingHours: String,
    @Json(name = "water_type") val waterType: String,
    val status: String,
    val rating: Double,
    @Json(name = "review_count") val reviewCount: Int = 0
)

data class EcoReportDto(
    val id: Int,
    val title: String,
    val photo: String? = null,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    @Json(name = "waste_description") val wasteDescription: String,
    val status: String,
    val severity: String,
    @Json(name = "reported_by_name") val reportedByName: String? = null,
    @Json(name = "verification_count") val verificationCount: Int = 0,
    @Json(name = "created_at") val createdAt: String? = null,
    val comments: List<EcoReportCommentDto> = emptyList()
)

data class EcoReportCommentDto(
    @Json(name = "user_name") val userName: String? = null,
    val comment: String,
    @Json(name = "created_at") val createdAt: String
)

data class WasteSubmissionRequestDto(
    @Json(name = "waste_type") val wasteType: String,
    val quantity: Double,
    val unit: String,
    val comment: String? = null
)

data class WasteSubmissionDto(
    val id: Int,
    @Json(name = "waste_type") val wasteType: String,
    val quantity: Double,
    val unit: String,
    val comment: String? = null,
    @Json(name = "earned_points") val earnedPoints: Int,
    @Json(name = "created_at") val createdAt: String
)

data class ChallengeDto(
    val id: Int,
    val title: String,
    val description: String,
    val type: String,
    val target: Int,
    @Json(name = "reward_points") val rewardPoints: Int
)

data class AchievementDto(
    val id: Int,
    val title: String,
    val description: String,
    @Json(name = "icon_name") val iconName: String,
    @Json(name = "bonus_points") val bonusPoints: Int
)

data class LeaderboardUserDto(
    val id: Int,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "eco_points") val ecoPoints: Int,
    val level: Int,
    @Json(name = "global_rank") val globalRank: Int
)

data class WasteScanRequestDto(
    val hint: String = ""
)

data class ScannerResultDto(
    @Json(name = "waste_type") val wasteType: String,
    val confidence: Float,
    val recyclable: Boolean,
    @Json(name = "disposal_hint") val disposalHint: String,
    @Json(name = "nearest_point_query") val nearestPointQuery: String? = null
)
