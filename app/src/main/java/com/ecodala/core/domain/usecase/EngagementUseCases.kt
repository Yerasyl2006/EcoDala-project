package com.ecodala.core.domain.usecase

import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.Achievement
import com.ecodala.core.domain.model.ChallengeStatus
import com.ecodala.core.domain.model.StreakSummary
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.domain.repository.PointsEconomyRepository

class GetAchievementProgressUseCase(
    private val repository: PointsEconomyRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Achievement>> {
        val eventsResult = repository.getPointEvents(userId)
        val submissionsResult = repository.getWasteSubmissions(userId)

        return eventsResult.mapCatching { events ->
            val submissions = submissionsResult.getOrThrow()
            val totalPoints = events.sumOf { it.points }
            val submissionCount = submissions.size
            val plasticKg = submissions
                .filter { it.wasteType == WasteType.Plastic && it.unit == "kg" }
                .sumOf { it.quantity }
            val completedChallenges = DummyEcoData.challenges.count { it.status == ChallengeStatus.Completed }
            val currentStreak = calculateDemoStreakDays(submissions.map { it.createdAt })

            listOf(
                buildAchievement(
                    id = "achievement-1",
                    title = "First Recycling",
                    description = "Submit your first recyclable waste",
                    iconName = "recycling",
                    current = submissionCount,
                    target = 1,
                    bonusPoints = 100,
                    unlockedAt = "Yesterday"
                ),
                buildAchievement(
                    id = "achievement-2",
                    title = "100 Points",
                    description = "Earn your first 100 EcoDala points",
                    iconName = "star",
                    current = totalPoints,
                    target = 100,
                    bonusPoints = 50,
                    unlockedAt = "2 days ago"
                ),
                buildAchievement(
                    id = "achievement-3",
                    title = "Challenge Joined",
                    description = "Complete your first daily challenge",
                    iconName = "group",
                    current = completedChallenges,
                    target = 1,
                    bonusPoints = 25,
                    unlockedAt = "Last week"
                ),
                buildAchievement(
                    id = "achievement-4",
                    title = "Plastic Saver",
                    description = "Recycle 10 kg of plastic",
                    iconName = "plastic",
                    current = plasticKg.toInt(),
                    target = 10,
                    bonusPoints = 120
                ),
                buildAchievement(
                    id = "achievement-5",
                    title = "7 Day Streak",
                    description = "Keep recycling activity for 7 active days",
                    iconName = "streak",
                    current = currentStreak,
                    target = 7,
                    bonusPoints = 150
                ),
                buildAchievement(
                    id = "achievement-6",
                    title = "Tree Keeper",
                    description = "Reach virtual tree level 5",
                    iconName = "tree",
                    current = totalPoints,
                    target = 1000,
                    bonusPoints = 200
                )
            )
        }
    }

    private fun buildAchievement(
        id: String,
        title: String,
        description: String,
        iconName: String,
        current: Int,
        target: Int,
        bonusPoints: Int,
        unlockedAt: String? = null
    ): Achievement {
        val progress = ((current.coerceAtMost(target) * 100f) / target).toInt()
        val unlocked = current >= target
        return Achievement(
            id = id,
            title = title,
            description = if (unlocked) description else "$description ($current/$target)",
            unlockedAt = if (unlocked) unlockedAt ?: "Unlocked" else null,
            iconName = iconName,
            isUnlocked = unlocked,
            progressPercent = progress,
            bonusPoints = bonusPoints
        )
    }
}

class GetStreakSummaryUseCase(
    private val repository: PointsEconomyRepository
) {
    suspend operator fun invoke(userId: String): Result<StreakSummary> {
        return repository.getWasteSubmissions(userId).map { submissions ->
            val current = calculateDemoStreakDays(submissions.map { it.createdAt })
            StreakSummary(
                currentDays = current,
                longestDays = maxOf(current, 5),
                nextRewardAtDays = 7,
                nextRewardPoints = 150
            )
        }
    }
}

private fun calculateDemoStreakDays(createdAtValues: List<String>): Int {
    val hasToday = createdAtValues.any { it.contains("Today", ignoreCase = true) }
    val hasYesterday = createdAtValues.any { it.contains("Yesterday", ignoreCase = true) }
    return when {
        hasToday && hasYesterday -> 2
        hasToday || hasYesterday -> 1
        else -> 0
    }
}
