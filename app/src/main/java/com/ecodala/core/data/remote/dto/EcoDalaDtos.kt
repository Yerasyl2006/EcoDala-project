package com.ecodala.core.data.remote.dto

import com.squareup.moshi.Json

data class PaginatedResponseDto<T>(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T> = emptyList()
)

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val email: String,
    val password: String,
    @Json(name = "full_name") val fullName: String,
    val city: String = "Almaty",
    val university: String? = null,
    val faculty: String? = null
)

data class JwtTokenDto(
    val access: String,
    val refresh: String,
    val user: UserDto? = null
)

data class UserDto(
    val id: String,
    val email: String,
    @Json(name = "full_name") val fullName: String? = null,
    val city: String? = null,
    val university: String? = null,
    val faculty: String? = null,
    val avatar: String? = null,
    val role: String? = null,
    @Json(name = "eco_points") val ecoPoints: Int = 0,
    @Json(name = "total_recycled_kg") val totalRecycledKg: String? = null,
    val level: Int = 1,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

data class WasteCategoryDto(
    val id: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    @Json(name = "points_per_kg") val pointsPerKg: Double = 10.0,
    val icon: String? = null,
    @Json(name = "color_hex") val colorHex: String? = null
)

data class RecyclingPointDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: String,
    val longitude: String,
    val description: String? = null,
    @Json(name = "working_hours") val workingHours: String? = null,
    val phone: String? = null,
    @Json(name = "accepted_categories") val acceptedCategories: List<WasteCategoryDto> = emptyList(),
    @Json(name = "is_active") val isActive: Boolean = true
)

data class BiotoiletDto(
    val id: String,
    val name: String,
    val photo: String? = null,
    val address: String,
    val latitude: String,
    val longitude: String,
    @Json(name = "opening_hours") val openingHours: String = "08:00 - 20:00",
    val status: String = "unknown",
    val type: String = "free",
    @Json(name = "is_accessible") val isAccessible: Boolean = false,
    @Json(name = "is_family_friendly") val isFamilyFriendly: Boolean = false,
    @Json(name = "cleanliness_rating") val cleanlinessRating: Double = 0.0,
    @Json(name = "review_count") val reviewCount: Int = 0
)

data class WaterStationDto(
    val id: String,
    val name: String,
    val photo: String? = null,
    val address: String,
    val latitude: String,
    val longitude: String,
    @Json(name = "working_hours") val workingHours: String = "08:00 - 20:00",
    @Json(name = "water_type") val waterType: String = "free_drinking_water",
    val status: String = "unknown",
    val rating: Double = 0.0,
    @Json(name = "review_count") val reviewCount: Int = 0
)

data class EcoReportDto(
    val id: String,
    val title: String,
    val photo: String? = null,
    val address: String,
    val latitude: String,
    val longitude: String,
    @Json(name = "waste_description") val wasteDescription: String,
    val status: String = "submitted",
    val severity: String = "medium",
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
    val category: String,
    @Json(name = "recycling_point") val recyclingPoint: String,
    @Json(name = "weight_kg") val weightKg: String,
    val comment: String? = null
)

data class WasteSubmissionDto(
    val id: String,
    val category: String? = null,
    @Json(name = "category_detail") val categoryDetail: WasteCategoryDto? = null,
    @Json(name = "recycling_point") val recyclingPoint: String? = null,
    @Json(name = "weight_kg") val weightKg: String = "0.0",
    val photo: String? = null,
    val comment: String? = null,
    val status: String? = null,
    @Json(name = "points_awarded") val pointsAwarded: Int = 0,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String? = null
)

data class ChallengeDto(
    val id: String,
    val title: String,
    val description: String,
    @Json(name = "target_kg") val targetKg: String = "1.0",
    @Json(name = "reward_points") val rewardPoints: Int,
    @Json(name = "starts_at") val startsAt: String? = null,
    @Json(name = "ends_at") val endsAt: String? = null,
    val status: String? = null,
    val progress: String = "0.0",
    @Json(name = "is_completed") val isCompleted: Boolean = false,
    @Json(name = "created_at") val createdAt: String? = null
)

data class AchievementDto(
    val id: String,
    val code: String? = null,
    val title: String,
    val description: String,
    val icon: String? = null,
    val rule: AchievementRuleDto? = null,
    val unlocked: Boolean = false,
    @Json(name = "created_at") val createdAt: String? = null
)

data class AchievementRuleDto(
    val metric: String? = null,
    val threshold: Double? = null
)

data class LeaderboardUserDto(
    val rank: Int,
    val id: String,
    @Json(name = "full_name") val fullName: String,
    val city: String? = null,
    val university: String? = null,
    val faculty: String? = null,
    @Json(name = "eco_points") val ecoPoints: Int,
    val level: Int
)

data class ScannerResultDto(
    val id: String? = null,
    val image: String? = null,
    val provider: String? = null,
    @Json(name = "predicted_category") val predictedCategory: WasteCategoryDto? = null,
    val confidence: Float = 0f,
    val advice: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)
