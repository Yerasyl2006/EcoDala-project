package com.ecodala.feature.profile.presentation

import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.model.WasteType
import org.junit.Assert.assertEquals
import org.junit.Test

class RecyclingHistoryUiStateTest {
    @Test
    fun totals_sumOnlyKilogramsForKgMetricAndAllPoints() {
        val state = RecyclingHistoryUiState(submissions = submissions)

        assertEquals(7, state.totalQuantityKg)
        assertEquals(170, state.totalPoints)
    }

    @Test
    fun filteredSubmissions_returnsOnlySelectedWasteType() {
        val state = RecyclingHistoryUiState(
            submissions = submissions,
            selectedType = WasteType.Plastic
        )

        assertEquals(1, state.filteredSubmissions.size)
        assertEquals(WasteType.Plastic, state.filteredSubmissions.first().wasteType)
    }
}

private val submissions = listOf(
    WasteSubmission(
        id = "1",
        userId = "user",
        wasteType = WasteType.Plastic,
        quantity = 2.0,
        unit = "kg",
        photoUrl = null,
        comment = null,
        earnedPoints = 70,
        createdAt = "2026-06-23"
    ),
    WasteSubmission(
        id = "2",
        userId = "user",
        wasteType = WasteType.Paper,
        quantity = 5.0,
        unit = "kg",
        photoUrl = null,
        comment = null,
        earnedPoints = 80,
        createdAt = "2026-06-23"
    ),
    WasteSubmission(
        id = "3",
        userId = "user",
        wasteType = WasteType.Glass,
        quantity = 4.0,
        unit = "pcs",
        photoUrl = null,
        comment = null,
        earnedPoints = 20,
        createdAt = "2026-06-23"
    )
)
