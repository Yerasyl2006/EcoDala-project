package com.ecodala.core.domain.usecase

import com.ecodala.core.domain.model.MonthlyImpact
import com.ecodala.core.domain.model.EcoRatingCalculator
import com.ecodala.core.domain.model.PointsSource
import com.ecodala.core.domain.model.PointsWallet
import com.ecodala.core.domain.repository.PointsEconomyRepository
import java.time.YearMonth

class GetPointsWalletUseCase(
    private val repository: PointsEconomyRepository
) {
    suspend operator fun invoke(userId: String): Result<PointsWallet> {
        return repository.getPointEvents(userId).map { events ->
            val total = events.sumOf { it.points }
            val monthKey = currentMonthKey()
            val thisMonth = events.filter { it.monthKey == monthKey }.sumOf { it.points }
            val rating = EcoRatingCalculator.calculate(total)
            PointsWallet(
                totalPoints = total,
                thisMonthPoints = thisMonth,
                wastePoints = events.filter { it.source == PointsSource.WasteSubmission }.sumOf { it.points },
                challengePoints = events.filter { it.source == PointsSource.ChallengeReward }.sumOf { it.points },
                achievementBonusPoints = events.filter { it.source == PointsSource.AchievementBonus }.sumOf { it.points },
                level = rating.level,
                progressToNextLevelPercent = rating.progressPercent,
                pointsInCurrentLevel = rating.pointsInCurrentLevel,
                pointsToNextLevel = rating.pointsToNextLevel,
                ratingTitle = rating.title
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
        val monthKey = currentMonthKey()

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
                pointsEarned = events.filter { it.monthKey == monthKey }.sumOf { it.points },
                challengeRewards = events.filter {
                    it.monthKey == monthKey && it.source == PointsSource.ChallengeReward
                }.sumOf { it.points },
                achievementBonuses = events.filter {
                    it.monthKey == monthKey && it.source == PointsSource.AchievementBonus
                }.sumOf { it.points }
            )
        }
    }
}

private fun currentMonthKey(): String = YearMonth.now().toString()
