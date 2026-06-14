package com.ecodala.core.domain.usecase

import com.ecodala.core.domain.model.MonthlyImpact
import com.ecodala.core.domain.model.PointsSource
import com.ecodala.core.domain.model.PointsWallet
import com.ecodala.core.domain.repository.PointsEconomyRepository

class GetPointsWalletUseCase(
    private val repository: PointsEconomyRepository
) {
    suspend operator fun invoke(userId: String): Result<PointsWallet> {
        return repository.getPointEvents(userId).map { events ->
            val total = events.sumOf { it.points }
            val thisMonth = events.filter { it.monthKey == CURRENT_MONTH_KEY }.sumOf { it.points }
            PointsWallet(
                totalPoints = total,
                thisMonthPoints = thisMonth,
                wastePoints = events.filter { it.source == PointsSource.WasteSubmission }.sumOf { it.points },
                challengePoints = events.filter { it.source == PointsSource.ChallengeReward }.sumOf { it.points },
                achievementBonusPoints = events.filter { it.source == PointsSource.AchievementBonus }.sumOf { it.points },
                level = (total / POINTS_PER_LEVEL).coerceAtLeast(1),
                progressToNextLevelPercent = ((total % POINTS_PER_LEVEL) * 100 / POINTS_PER_LEVEL)
            )
        }
    }
}

class GetMonthlyImpactUseCase(
    private val repository: PointsEconomyRepository
) {
    suspend operator fun invoke(userId: String): Result<MonthlyImpact> {
        val eventsResult = repository.getPointEvents(userId)
        val submissionsResult = repository.getWasteSubmissions(userId)

        return eventsResult.mapCatching { events ->
            val submissions = submissionsResult.getOrThrow()
                .filter {
                    it.createdAt.contains("Today") ||
                        it.createdAt.contains("Yesterday") ||
                        it.createdAt.contains("Jun")
                }
            MonthlyImpact(
                recycledKg = submissions.filter { it.unit == "kg" }.sumOf { it.quantity },
                submissions = submissions.size,
                pointsEarned = events.filter { it.monthKey == CURRENT_MONTH_KEY }.sumOf { it.points },
                challengeRewards = events.filter {
                    it.monthKey == CURRENT_MONTH_KEY && it.source == PointsSource.ChallengeReward
                }.sumOf { it.points },
                achievementBonuses = events.filter {
                    it.monthKey == CURRENT_MONTH_KEY && it.source == PointsSource.AchievementBonus
                }.sumOf { it.points }
            )
        }
    }
}

private const val CURRENT_MONTH_KEY = "2026-06"
private const val POINTS_PER_LEVEL = 200
