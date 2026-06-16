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
        id = id.toString(),
        fullName = fullName?.takeIf { it.isNotBlank() }
            ?: listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { username },
        email = email,
        avatarUrl = avatar,
        ecoPoints = ecoPoints,
        globalRank = globalRank,
        level = level,
        joinedAt = "Backend"
    )
}

fun RecyclingPointDto.toDomain(): RecyclingPoint {
    return RecyclingPoint(
        id = id.toString(),
        name = name,
        address = address,
        phone = phone.orEmpty(),
        openingHours = openingHours,
        latitude = latitude,
        longitude = longitude,
        rating = rating,
        distanceMeters = 0,
        acceptedWasteTypes = acceptedWasteTypes.mapNotNull { it.toWasteTypeOrNull() },
        rewardPoints = rewardPoints
    )
}

fun BiotoiletDto.toDomain(): Biotoilet {
    return Biotoilet(
        id = id.toString(),
        name = name,
        photoLabel = photo ?: "Biotoilet location",
        address = address,
        latitude = latitude,
        longitude = longitude,
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
        id = id.toString(),
        name = name,
        photoLabel = photo ?: "Water station",
        address = address,
        latitude = latitude,
        longitude = longitude,
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
        id = id.toString(),
        title = title,
        photoLabel = photo ?: "Eco report photo",
        address = address,
        latitude = latitude,
        longitude = longitude,
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
        id = id.toString(),
        userId = userId,
        wasteType = wasteType.toWasteTypeOrNull() ?: WasteType.Plastic,
        quantity = quantity,
        unit = unit,
        photoUrl = null,
        comment = comment,
        earnedPoints = earnedPoints,
        createdAt = createdAt
    )
}

fun ChallengeDto.toDomain(): Challenge {
    return Challenge(
        id = id.toString(),
        title = title,
        description = description,
        progress = 0,
        target = target,
        rewardPoints = rewardPoints,
        status = ChallengeStatus.Active,
        type = when (type) {
            "weekly" -> ChallengeType.Weekly
            "special" -> ChallengeType.Special
            else -> ChallengeType.Daily
        }
    )
}

fun AchievementDto.toDomain(): Achievement {
    return Achievement(
        id = id.toString(),
        title = title,
        description = description,
        unlockedAt = null,
        iconName = iconName,
        isUnlocked = false,
        progressPercent = 0,
        bonusPoints = bonusPoints
    )
}

fun LeaderboardUserDto.toDomain(index: Int, currentUserId: String?): LeaderboardEntry {
    return LeaderboardEntry(
        rank = index + 1,
        userId = id.toString(),
        name = fullName,
        points = ecoPoints,
        isCurrentUser = currentUserId == id.toString()
    )
}

fun ScannerResultDto.toDomain(): ScannerResult {
    return ScannerResult(
        wasteType = wasteType.toWasteTypeOrNull() ?: WasteType.Plastic,
        confidence = confidence,
        disposalHint = disposalHint
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
