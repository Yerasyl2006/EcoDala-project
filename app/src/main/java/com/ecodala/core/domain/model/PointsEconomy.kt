package com.ecodala.core.domain.model

data class PointsEvent(
    val id: String,
    val userId: String,
    val source: PointsSource,
    val title: String,
    val points: Int,
    val monthKey: String,
    val createdAt: String
)

enum class PointsSource {
    WasteSubmission,
    ChallengeReward,
    AchievementBonus
}

data class PointsWallet(
    val totalPoints: Int,
    val thisMonthPoints: Int,
    val wastePoints: Int,
    val challengePoints: Int,
    val achievementBonusPoints: Int,
    val level: Int,
    val progressToNextLevelPercent: Int
)

data class MonthlyImpact(
    val recycledKg: Double,
    val submissions: Int,
    val pointsEarned: Int,
    val challengeRewards: Int,
    val achievementBonuses: Int
)
