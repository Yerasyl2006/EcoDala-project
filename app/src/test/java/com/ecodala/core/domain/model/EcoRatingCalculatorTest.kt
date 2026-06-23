package com.ecodala.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class EcoRatingCalculatorTest {
    @Test
    fun calculate_clampsNegativePointsToLevelZero() {
        val rating = EcoRatingCalculator.calculate(-40)

        assertEquals(0, rating.totalPoints)
        assertEquals(0, rating.level)
        assertEquals(0, rating.progressPercent)
    }

    @Test
    fun calculate_returnsMaxLevelAtThousandPoints() {
        val rating = EcoRatingCalculator.calculate(1_250)

        assertEquals(EcoRatingCalculator.MaxLevel, rating.level)
        assertEquals(100, rating.progressPercent)
        assertEquals(0, rating.pointsToNextLevel)
    }

    @Test
    fun pointsForWaste_convertsUnitsBeforeScoring() {
        assertEquals(35, EcoRatingCalculator.pointsForWaste(WasteType.Plastic, 1.0, "kg"))
        assertEquals(5, EcoRatingCalculator.pointsForWaste(WasteType.Plastic, 100.0, "g"))
        assertEquals(88, EcoRatingCalculator.pointsForWaste(WasteType.Paper, 2.0, "bag"))
    }
}
