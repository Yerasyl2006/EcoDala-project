package com.ecodala.feature.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.Biotoilet
import com.ecodala.core.domain.model.BiotoiletStatus
import com.ecodala.core.domain.model.BiotoiletType
import com.ecodala.core.domain.model.EcoReport
import com.ecodala.core.domain.model.EcoReportSeverity
import com.ecodala.core.domain.model.EcoReportStatus
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.domain.model.WaterStation
import com.ecodala.core.domain.model.WaterStationStatus
import com.ecodala.core.domain.model.WaterStationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val allPoints: List<RecyclingPoint> = DummyEcoData.recyclingPoints,
    val allBiotoilets: List<Biotoilet> = DummyEcoData.biotoilets,
    val allWaterStations: List<WaterStation> = DummyEcoData.waterStations,
    val allEcoReports: List<EcoReport> = DummyEcoData.ecoReports,
    val mapLayer: MapLayer = MapLayer.Recycling,
    val selectedWasteType: WasteType? = null,
    val selectedPoint: RecyclingPoint? = null,
    val selectedBiotoilet: Biotoilet? = null,
    val selectedWaterStation: WaterStation? = null,
    val selectedEcoReport: EcoReport? = null,
    val routeDestination: RecyclingPoint? = null,
    val biotoiletFilter: BiotoiletFilter = BiotoiletFilter(),
    val waterStationFilter: WaterStationFilter = WaterStationFilter(),
    val ecoReportFilter: EcoReportFilter = EcoReportFilter()
) {
    val filteredPoints: List<RecyclingPoint>
        get() {
            val normalizedQuery = searchQuery.trim().lowercase()

            return allPoints.filter { point ->
                val matchesWasteType = selectedWasteType == null ||
                    point.primaryWasteType == selectedWasteType
                val matchesQuery = normalizedQuery.isBlank() ||
                    point.name.lowercase().contains(normalizedQuery) ||
                    point.address.lowercase().contains(normalizedQuery) ||
                    point.acceptedWasteTypes.any { it.title.lowercase().contains(normalizedQuery) }

                matchesWasteType && matchesQuery
            }
        }

    val filteredBiotoilets: List<Biotoilet>
        get() {
            val normalizedQuery = searchQuery.trim().lowercase()

            return allBiotoilets.filter { toilet ->
                val matchesQuery = normalizedQuery.isBlank() ||
                    toilet.name.lowercase().contains(normalizedQuery) ||
                    toilet.address.lowercase().contains(normalizedQuery)
                val matchesFree = !biotoiletFilter.freeOnly || toilet.type == BiotoiletType.Free
                val matchesPaid = !biotoiletFilter.paidOnly || toilet.type == BiotoiletType.Paid
                val matchesAccessible = !biotoiletFilter.accessibleOnly || toilet.isAccessible
                val matchesHighlyRated = !biotoiletFilter.highlyRatedOnly || toilet.cleanlinessRating >= 4.3
                val matchesOpenNow = !biotoiletFilter.openNowOnly || toilet.status == BiotoiletStatus.Open

                matchesQuery && matchesFree && matchesPaid && matchesAccessible && matchesHighlyRated && matchesOpenNow
            }
        }

    val filteredWaterStations: List<WaterStation>
        get() {
            val normalizedQuery = searchQuery.trim().lowercase()
            val filtered = allWaterStations.filter { station ->
                val matchesQuery = normalizedQuery.isBlank() ||
                    station.name.lowercase().contains(normalizedQuery) ||
                    station.address.lowercase().contains(normalizedQuery)
                val matchesFree = !waterStationFilter.freeOnly ||
                    station.waterType == WaterStationType.FreeDrinkingWater
                val matchesOpen = !waterStationFilter.openNowOnly ||
                    station.status == WaterStationStatus.Available
                val matchesRated = !waterStationFilter.highlyRatedOnly ||
                    station.rating >= 4.5
                val matchesRefill = !waterStationFilter.refillOnly ||
                    station.waterType == WaterStationType.RefillStation

                matchesQuery && matchesFree && matchesOpen && matchesRated && matchesRefill
            }

            return if (waterStationFilter.nearestOnly) {
                filtered.sortedBy { it.distanceMeters }
            } else {
                filtered
            }
        }

    val filteredEcoReports: List<EcoReport>
        get() {
            val normalizedQuery = searchQuery.trim().lowercase()
            val filtered = allEcoReports.filter { report ->
                val matchesQuery = normalizedQuery.isBlank() ||
                    report.title.lowercase().contains(normalizedQuery) ||
                    report.address.lowercase().contains(normalizedQuery) ||
                    report.wasteDescription.lowercase().contains(normalizedQuery)
                val matchesOpen = !ecoReportFilter.activeOnly ||
                    report.status in listOf(EcoReportStatus.Submitted, EcoReportStatus.Verified, EcoReportStatus.InProgress)
                val matchesHigh = !ecoReportFilter.highSeverityOnly || report.severity == EcoReportSeverity.High
                val matchesVerified = !ecoReportFilter.verifiedOnly || report.status == EcoReportStatus.Verified
                val matchesResolved = !ecoReportFilter.resolvedOnly || report.status == EcoReportStatus.Resolved

                matchesQuery && matchesOpen && matchesHigh && matchesVerified && matchesResolved
            }

            return if (ecoReportFilter.nearestOnly) filtered.sortedBy { it.distanceMeters } else filtered
        }
}

enum class MapLayer {
    Recycling,
    Biotoilets,
    WaterStations,
    EcoReports
}

data class BiotoiletFilter(
    val freeOnly: Boolean = false,
    val paidOnly: Boolean = false,
    val accessibleOnly: Boolean = false,
    val highlyRatedOnly: Boolean = false,
    val openNowOnly: Boolean = false
)

data class WaterStationFilter(
    val freeOnly: Boolean = false,
    val openNowOnly: Boolean = false,
    val highlyRatedOnly: Boolean = false,
    val nearestOnly: Boolean = false,
    val refillOnly: Boolean = false
)

data class EcoReportFilter(
    val activeOnly: Boolean = false,
    val highSeverityOnly: Boolean = false,
    val verifiedOnly: Boolean = false,
    val resolvedOnly: Boolean = false,
    val nearestOnly: Boolean = false
)

class MapViewModel(
    private val repository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        refreshMapData()
    }

    fun refreshMapData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val points = repository.recyclingPoints()
            val toilets = repository.biotoilets()
            val water = repository.waterStations()
            val reports = repository.ecoReports()

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMessage = listOf(points, toilets, water, reports)
                        .firstOrNull { it.isFailure }
                        ?.exceptionOrNull()
                        ?.message,
                    allPoints = points.getOrElse { state.allPoints },
                    allBiotoilets = toilets.getOrElse { state.allBiotoilets },
                    allWaterStations = water.getOrElse { state.allWaterStations },
                    allEcoReports = reports.getOrElse { state.allEcoReports }
                )
            }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = value,
                selectedPoint = null,
                selectedBiotoilet = null,
                selectedWaterStation = null,
                selectedEcoReport = null,
                routeDestination = null
            )
        }
    }

    fun onMapLayerSelected(layer: MapLayer) {
        _uiState.update { state ->
            state.copy(
                mapLayer = layer,
                selectedPoint = null,
                selectedBiotoilet = null,
                selectedWaterStation = null,
                selectedEcoReport = null,
                routeDestination = null
            )
        }
    }

    fun onWasteTypeSelected(type: WasteType?) {
        _uiState.update { state ->
            state.copy(
                selectedWasteType = type,
                selectedPoint = null,
                selectedBiotoilet = null,
                selectedWaterStation = null,
                selectedEcoReport = null,
                routeDestination = null
            )
        }
    }

    fun onPointSelected(point: RecyclingPoint) {
        _uiState.update { it.copy(selectedPoint = point, selectedBiotoilet = null, selectedWaterStation = null, selectedEcoReport = null) }
    }

    fun onBiotoiletSelected(toilet: Biotoilet) {
        _uiState.update { it.copy(selectedBiotoilet = toilet, selectedPoint = null, selectedWaterStation = null, selectedEcoReport = null) }
    }

    fun onWaterStationSelected(station: WaterStation) {
        _uiState.update { it.copy(selectedWaterStation = station, selectedPoint = null, selectedBiotoilet = null, selectedEcoReport = null) }
    }

    fun onEcoReportSelected(report: EcoReport) {
        _uiState.update { it.copy(selectedEcoReport = report, selectedPoint = null, selectedBiotoilet = null, selectedWaterStation = null) }
    }

    fun toggleBiotoiletFilter(option: BiotoiletFilterOption) {
        _uiState.update { state ->
            val filter = state.biotoiletFilter
            state.copy(
                selectedBiotoilet = null,
                biotoiletFilter = when (option) {
                    BiotoiletFilterOption.Free -> filter.copy(freeOnly = !filter.freeOnly, paidOnly = false)
                    BiotoiletFilterOption.Paid -> filter.copy(paidOnly = !filter.paidOnly, freeOnly = false)
                    BiotoiletFilterOption.Accessible -> filter.copy(accessibleOnly = !filter.accessibleOnly)
                    BiotoiletFilterOption.HighlyRated -> filter.copy(highlyRatedOnly = !filter.highlyRatedOnly)
                    BiotoiletFilterOption.OpenNow -> filter.copy(openNowOnly = !filter.openNowOnly)
                }
            )
        }
    }

    fun toggleWaterStationFilter(option: WaterStationFilterOption) {
        _uiState.update { state ->
            val filter = state.waterStationFilter
            state.copy(
                selectedWaterStation = null,
                waterStationFilter = when (option) {
                    WaterStationFilterOption.Free -> filter.copy(freeOnly = !filter.freeOnly)
                    WaterStationFilterOption.OpenNow -> filter.copy(openNowOnly = !filter.openNowOnly)
                    WaterStationFilterOption.HighlyRated -> filter.copy(highlyRatedOnly = !filter.highlyRatedOnly)
                    WaterStationFilterOption.Nearest -> filter.copy(nearestOnly = !filter.nearestOnly)
                    WaterStationFilterOption.Refill -> filter.copy(refillOnly = !filter.refillOnly)
                }
            )
        }
    }

    fun toggleEcoReportFilter(option: EcoReportFilterOption) {
        _uiState.update { state ->
            val filter = state.ecoReportFilter
            state.copy(
                selectedEcoReport = null,
                ecoReportFilter = when (option) {
                    EcoReportFilterOption.Active -> filter.copy(activeOnly = !filter.activeOnly, resolvedOnly = false)
                    EcoReportFilterOption.HighSeverity -> filter.copy(highSeverityOnly = !filter.highSeverityOnly)
                    EcoReportFilterOption.Verified -> filter.copy(verifiedOnly = !filter.verifiedOnly)
                    EcoReportFilterOption.Resolved -> filter.copy(resolvedOnly = !filter.resolvedOnly, activeOnly = false)
                    EcoReportFilterOption.Nearest -> filter.copy(nearestOnly = !filter.nearestOnly)
                }
            )
        }
    }

    fun onRouteRequested(point: RecyclingPoint) {
        _uiState.update { it.copy(selectedPoint = point, routeDestination = point) }
    }
}

enum class BiotoiletFilterOption {
    Free,
    Paid,
    Accessible,
    HighlyRated,
    OpenNow
}

enum class WaterStationFilterOption {
    Free,
    OpenNow,
    HighlyRated,
    Nearest,
    Refill
}

enum class EcoReportFilterOption {
    Active,
    HighSeverity,
    Verified,
    Resolved,
    Nearest
}

private val RecyclingPoint.primaryWasteType: WasteType?
    get() = acceptedWasteTypes.firstOrNull()
