package com.ecodala.core.domain.model

import kotlin.math.roundToInt

data class EcoRating(
    val totalPoints: Int,
    val level: Int,
    val progressPercent: Int,
    val pointsInCurrentLevel: Int,
    val pointsForNextLevel: Int,
    val pointsToNextLevel: Int,
    val title: String,
    val badgeCode: String
)

object EcoRatingCalculator {
    const val PointsPerLevel = 100
    const val MaxLevel = 10

    fun calculate(totalPoints: Int): EcoRating {
        val safeTotal = totalPoints.coerceAtLeast(0)
        val level = (safeTotal / PointsPerLevel).coerceIn(0, MaxLevel)
        val isMaxLevel = level == MaxLevel
        val pointsInLevel = if (isMaxLevel) PointsPerLevel else safeTotal % PointsPerLevel
        val pointsToNext = if (isMaxLevel) 0 else PointsPerLevel - pointsInLevel

        return EcoRating(
            totalPoints = safeTotal,
            level = level,
            progressPercent = if (isMaxLevel) 100 else pointsInLevel * 100 / PointsPerLevel,
            pointsInCurrentLevel = pointsInLevel,
            pointsForNextLevel = PointsPerLevel,
            pointsToNextLevel = pointsToNext,
            title = titleForLevel(level),
            badgeCode = badgeForLevel(level)
        )
    }

    fun pointsForWaste(type: WasteType, quantity: Double, unit: String): Int {
        val normalizedKg = when (unit.trim().lowercase()) {
            "g", "gram", "grams" -> quantity / 1000.0
            "pcs", "piece", "pieces", "item", "items" -> quantity * 0.05
            "bag", "bags" -> quantity * 2.0
            else -> quantity
        }.coerceAtLeast(0.0)

        val pointsPerKg = when (type) {
            WasteType.Plastic -> 35
            WasteType.Paper -> 22
            WasteType.Glass -> 26
            WasteType.Batteries -> 90
            WasteType.Electronics -> 75
            WasteType.Organic -> 14
            WasteType.Metal -> 48
        }

        return (normalizedKg * pointsPerKg).roundToInt().coerceAtLeast(5)
    }

    private fun titleForLevel(level: Int): String {
        return when (level.coerceIn(0, MaxLevel)) {
            0 -> "Eco Starter"
            1, 2 -> "Seedling Helper"
            3, 4 -> "Recycling Builder"
            5, 6 -> "Green Guardian"
            7, 8 -> "Eco Champion"
            9 -> "Forest Keeper"
            else -> "EcoDala Legend"
        }
    }

    private fun badgeForLevel(level: Int): String {
        return when (level.coerceIn(0, MaxLevel)) {
            0 -> "starter"
            in 1..2 -> "seedling"
            in 3..4 -> "builder"
            in 5..6 -> "guardian"
            in 7..8 -> "champion"
            9 -> "keeper"
            else -> "legend"
        }
    }
}
