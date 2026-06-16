package com.ecodala.core.domain.model

data class EcoUser(
    val id: String,
    val fullName: String,
    val email: String,
    val avatarUrl: String? = null,
    val ecoPoints: Int,
    val globalRank: Int,
    val level: Int,
    val joinedAt: String
)

data class RecyclingPoint(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val openingHours: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val distanceMeters: Int,
    val acceptedWasteTypes: List<WasteType>,
    val rewardPoints: Int
)

data class Biotoilet(
    val id: String,
    val name: String,
    val photoLabel: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Int,
    val openingHours: String,
    val status: BiotoiletStatus,
    val type: BiotoiletType,
    val isAccessible: Boolean,
    val isFamilyFriendly: Boolean,
    val cleanlinessRating: Double,
    val reviews: List<BiotoiletReview>
)

enum class BiotoiletStatus {
    Open,
    Unknown,
    Closed,
    Maintenance
}

enum class BiotoiletType {
    Free,
    Paid
}

data class BiotoiletReview(
    val userName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String
)

enum class BiotoiletIssueType {
    ClosedToilet,
    DirtyToilet,
    DamagedToilet,
    NoWaterAvailable
}

data class WaterStation(
    val id: String,
    val name: String,
    val photoLabel: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Int,
    val workingHours: String,
    val waterType: WaterStationType,
    val status: WaterStationStatus,
    val rating: Double,
    val reviews: List<WaterStationReview>
)

enum class WaterStationType {
    FreeDrinkingWater,
    RefillStation,
    FilteredWater,
    WaterDispenser,
    BottledWaterVendingMachine
}

enum class WaterStationStatus {
    Available,
    Unknown,
    TemporarilyUnavailable,
    Maintenance
}

data class WaterStationReview(
    val userName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String
)

enum class WaterStationIssueType {
    NotWorking,
    PoorWaterQuality,
    NoWater,
    WrongHours
}

data class EcoReport(
    val id: String,
    val title: String,
    val photoLabel: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Int,
    val wasteDescription: String,
    val status: EcoReportStatus,
    val severity: EcoReportSeverity,
    val reportedBy: String,
    val reportedAt: String,
    val verificationCount: Int,
    val comments: List<EcoReportComment>
)

enum class EcoReportStatus {
    Submitted,
    Verified,
    InProgress,
    Resolved,
    Rejected
}

enum class EcoReportSeverity {
    Low,
    Medium,
    High
}

data class EcoReportComment(
    val userName: String,
    val comment: String,
    val createdAt: String
)

enum class EcoReportIssueType {
    IllegalDump,
    ConstructionWaste,
    HazardousWaste,
    OverflowingBins
}

enum class WasteType(val title: String) {
    Plastic("Plastic"),
    Paper("Paper"),
    Glass("Glass"),
    Batteries("Batteries"),
    Electronics("Electronics"),
    Organic("Organic"),
    Metal("Metal")
}

data class WasteSubmission(
    val id: String,
    val userId: String,
    val wasteType: WasteType,
    val quantity: Double,
    val unit: String,
    val photoUrl: String?,
    val comment: String?,
    val earnedPoints: Int,
    val createdAt: String
)

data class VirtualTree(
    val level: Int,
    val progressPercent: Int,
    val currentXp: Int,
    val nextLevelXp: Int,
    val growthHistory: List<TreeGrowthEvent>
)

data class TreeGrowthEvent(
    val date: String,
    val title: String,
    val description: String
)

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int,
    val target: Int,
    val rewardPoints: Int,
    val status: ChallengeStatus,
    val type: ChallengeType
)

enum class ChallengeStatus {
    Active,
    Completed,
    Locked
}

enum class ChallengeType {
    Daily,
    Weekly,
    Special
}

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val name: String,
    val points: Int,
    val avatarUrl: String? = null,
    val isCurrentUser: Boolean = false
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val unlockedAt: String?,
    val iconName: String,
    val isUnlocked: Boolean,
    val progressPercent: Int = if (isUnlocked) 100 else 0,
    val bonusPoints: Int = 0
)

data class ScannerResult(
    val wasteType: WasteType,
    val confidence: Float,
    val disposalHint: String
)
