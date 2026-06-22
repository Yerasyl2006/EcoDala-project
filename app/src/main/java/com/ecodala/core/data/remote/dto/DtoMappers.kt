package com.ecodala.core.data.remote.dto

import com.ecodala.core.domain.model.Achievement
import com.ecodala.core.domain.model.Biotoilet
import com.ecodala.core.domain.model.BiotoiletReview
import com.ecodala.core.domain.model.BiotoiletStatus
import com.ecodala.core.domain.model.BiotoiletType
import com.ecodala.core.domain.model.Challenge
import com.ecodala.core.domain.model.ChallengeStatus
import com.ecodala.core.domain.model.ChallengeType
import com.ecodala.core.domain.model.EcoReport
import com.ecodala.core.domain.model.EcoReportComment
import com.ecodala.core.domain.model.EcoReportSeverity
import com.ecodala.core.domain.model.EcoReportStatus
import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.LeaderboardEntry
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.ScannerResult
import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.domain.model.WaterStation
import com.ecodala.core.domain.model.WaterStationReview
import com.ecodala.core.domain.model.WaterStationStatus
import com.ecodala.core.domain.model.WaterStationType

fun UserDto.toDomain(): EcoUser {
    return EcoUser(
        id = id,
        fullName = fullName?.takeIf { it.isNotBlank() } ?: email.substringBefore("@"),
        email = email,
        avatarUrl = avatar,
        ecoPoints = ecoPoints,
        globalRank = 0,
        level = level,
        joinedAt = createdAt ?: "Backend"
    )
}

fun RecyclingPointDto.toDomain(): RecyclingPoint {
    return RecyclingPoint(
        id = id,
        name = name,
        address = address,
        phone = phone.orEmpty(),
        openingHours = workingHours ?: "08:00 - 20:00",
        latitude = latitude.toDoubleOrNull() ?: 0.0,
        longitude = longitude.toDoubleOrNull() ?: 0.0,
        rating = if (isActive) 4.8 else 3.8,
        distanceMeters = 0,
        acceptedWasteTypes = acceptedCategories.mapNotNull { it.slug.toWasteTypeOrNull() },
        rewardPoints = acceptedCategories.firstOrNull()?.pointsPerKg?.toInt() ?: 10
    )
}

fun BiotoiletDto.toDomain(): Biotoilet {
    return Biotoilet(
        id = id,
        name = name,
        photoLabel = photo ?: "Biotoilet location",
        address = address,
        latitude = latitude.toDoubleOrNull() ?: 0.0,
        longitude = longitude.toDoubleOrNull() ?: 0.0,
        distanceMeters = 0,
        openingHours = openingHours,
        status = status.toBiotoiletStatus(),
        type = type.toBiotoiletType(),
        isAccessible = isAccessible,
        isFamilyFriendly = isFamilyFriendly,
        cleanlinessRating = cleanlinessRating,
        reviews = listOf(
            BiotoiletReview("EcoDala", cleanlinessRating.toInt().coerceIn(1, 5), "$reviewCount community reviews", "Backend")
        )
    )
}

fun WaterStationDto.toDomain(): WaterStation {
    return WaterStation(
        id = id,
        name = name,
        photoLabel = photo ?: "Water station",
        address = address,
        latitude = latitude.toDoubleOrNull() ?: 0.0,
        longitude = longitude.toDoubleOrNull() ?: 0.0,
        distanceMeters = 0,
        workingHours = workingHours,
        waterType = waterType.toWaterStationType(),
        status = status.toWaterStationStatus(),
        rating = rating,
        reviews = listOf(
            WaterStationReview("EcoDala", rating.toInt().coerceIn(1, 5), "$reviewCount community reviews", "Backend")
        )
    )
}

fun EcoReportDto.toDomain(): EcoReport {
    return EcoReport(
        id = id,
        title = title,
        photoLabel = photo ?: "Eco report photo",
        address = address,
        latitude = latitude.toDoubleOrNull() ?: 0.0,
        longitude = longitude.toDoubleOrNull() ?: 0.0,
        distanceMeters = 0,
        wasteDescription = wasteDescription,
        status = status.toEcoReportStatus(),
        severity = severity.toEcoReportSeverity(),
        reportedBy = reportedByName ?: "Community",
        reportedAt = createdAt ?: "Backend",
        verificationCount = verificationCount,
        comments = comments.map { EcoReportComment(it.userName ?: "User", it.comment, it.createdAt) }
    )
}

fun WasteSubmissionDto.toDomain(userId: String): WasteSubmission {
    return WasteSubmission(
        id = id,
        userId = userId,
        wasteType = categoryDetail?.slug?.toWasteTypeOrNull() ?: WasteType.Plastic,
        quantity = weightKg.toDoubleOrNull() ?: 0.0,
        unit = "kg",
        photoUrl = null,
        comment = comment,
        earnedPoints = pointsAwarded,
        createdAt = createdAt
    )
}

fun ChallengeDto.toDomain(): Challenge {
    return Challenge(
        id = id,
        title = title,
        description = description,
        progress = progress.toDoubleOrNull()?.toInt() ?: 0,
        target = (targetKg.toDoubleOrNull()?.toInt() ?: 1).coerceAtLeast(1),
        rewardPoints = rewardPoints,
        status = if (isCompleted) ChallengeStatus.Completed else ChallengeStatus.Active,
        type = when (status?.lowercase()) {
            "weekly" -> ChallengeType.Weekly
            "special" -> ChallengeType.Special
            else -> ChallengeType.Daily
        }
    )
}

fun AchievementDto.toDomain(): Achievement {
    return Achievement(
        id = id,
        title = title,
        description = description,
        unlockedAt = if (unlocked) createdAt else null,
        iconName = icon ?: code ?: "eco",
        isUnlocked = unlocked,
        progressPercent = if (unlocked) 100 else 0,
        bonusPoints = rule?.threshold?.toInt() ?: 0
    )
}

fun LeaderboardUserDto.toDomain(index: Int, currentUserId: String?): LeaderboardEntry {
    return LeaderboardEntry(
        rank = rank.takeIf { it > 0 } ?: index + 1,
        userId = id,
        name = fullName,
        points = ecoPoints,
        isCurrentUser = currentUserId == id
    )
}

fun ScannerResultDto.toDomain(): ScannerResult {
    return ScannerResult(
        wasteType = predictedCategory?.slug?.toWasteTypeOrNull() ?: WasteType.Plastic,
        confidence = confidence,
        disposalHint = advice ?: "Recycle it at the nearest EcoDala point."
    )
}

fun WasteType.toApiValue(): String {
    return when (this) {
        WasteType.Plastic -> "plastic"
        WasteType.Paper -> "paper"
        WasteType.Glass -> "glass"
        WasteType.Batteries -> "batteries"
        WasteType.Electronics -> "electronics"
        WasteType.Organic -> "organic"
        WasteType.Metal -> "metal"
    }
}

private fun String.toWasteTypeOrNull(): WasteType? {
    return when (lowercase()) {
        "plastic" -> WasteType.Plastic
        "paper" -> WasteType.Paper
        "glass" -> WasteType.Glass
        "batteries", "battery" -> WasteType.Batteries
        "electronics" -> WasteType.Electronics
        "organic" -> WasteType.Organic
        "metal" -> WasteType.Metal
        else -> null
    }
}

private fun String.toBiotoiletStatus(): BiotoiletStatus {
    return when (lowercase()) {
        "open" -> BiotoiletStatus.Open
        "closed" -> BiotoiletStatus.Closed
        "maintenance" -> BiotoiletStatus.Maintenance
        else -> BiotoiletStatus.Unknown
    }
}

private fun String.toBiotoiletType(): BiotoiletType {
    return if (lowercase() == "paid") BiotoiletType.Paid else BiotoiletType.Free
}

private fun String.toWaterStationStatus(): WaterStationStatus {
    return when (lowercase()) {
        "available" -> WaterStationStatus.Available
        "unavailable", "temporarily_unavailable" -> WaterStationStatus.TemporarilyUnavailable
        "maintenance" -> WaterStationStatus.Maintenance
        else -> WaterStationStatus.Unknown
    }
}

private fun String.toWaterStationType(): WaterStationType {
    return when (lowercase()) {
        "refill_station" -> WaterStationType.RefillStation
        "filtered_water" -> WaterStationType.FilteredWater
        "water_dispenser" -> WaterStationType.WaterDispenser
        "vending_machine", "bottled_water_vending_machine" -> WaterStationType.BottledWaterVendingMachine
        else -> WaterStationType.FreeDrinkingWater
    }
}

private fun String.toEcoReportStatus(): EcoReportStatus {
    return when (lowercase()) {
        "verified" -> EcoReportStatus.Verified
        "in_progress" -> EcoReportStatus.InProgress
        "resolved" -> EcoReportStatus.Resolved
        "rejected" -> EcoReportStatus.Rejected
        else -> EcoReportStatus.Submitted
    }
}

private fun String.toEcoReportSeverity(): EcoReportSeverity {
    return when (lowercase()) {
        "high" -> EcoReportSeverity.High
        "low" -> EcoReportSeverity.Low
        else -> EcoReportSeverity.Medium
    }
}
