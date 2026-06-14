package com.ecodala.core.domain.model

data class StreakSummary(
    val currentDays: Int,
    val longestDays: Int,
    val nextRewardAtDays: Int,
    val nextRewardPoints: Int
)
