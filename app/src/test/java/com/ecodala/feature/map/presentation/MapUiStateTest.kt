package com.ecodala.feature.map.presentation

import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.BiotoiletStatus
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.domain.model.WaterStationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MapUiStateTest {
    @Test
    fun filteredPoints_filtersByWasteTypeAndSearchQuery() {
        val state = MapUiState(
            searchQuery = "green",
            selectedWasteType = WasteType.Plastic,
            allPoints = DummyEcoData.recyclingPoints
        )

        assertTrue(state.filteredPoints.isNotEmpty())
        assertTrue(state.filteredPoints.all { point ->
            WasteType.Plastic in point.acceptedWasteTypes &&
                point.name.lowercase().contains("green")
        })
    }

    @Test
    fun filteredBiotoilets_openNowOnlyReturnsOpenFacilities() {
        val state = MapUiState(
            allBiotoilets = DummyEcoData.biotoilets,
            biotoiletFilter = BiotoiletFilter(openNowOnly = true)
        )

        assertTrue(state.filteredBiotoilets.all { it.status == BiotoiletStatus.Open })
    }

    @Test
    fun filteredWaterStations_openNowOnlyReturnsAvailableStations() {
        val state = MapUiState(
            allWaterStations = DummyEcoData.waterStations,
            waterStationFilter = WaterStationFilter(openNowOnly = true)
        )

        assertTrue(state.filteredWaterStations.all { it.status == WaterStationStatus.Available })
    }

    @Test
    fun nearestWaterStations_areSortedByDistance() {
        val state = MapUiState(
            allWaterStations = DummyEcoData.waterStations,
            waterStationFilter = WaterStationFilter(nearestOnly = true)
        )

        assertEquals(
            state.filteredWaterStations.map { it.distanceMeters }.sorted(),
            state.filteredWaterStations.map { it.distanceMeters }
        )
    }
}
