package com.ecodala.feature.map.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.WasteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class MapUiState(
    val searchQuery: String = "",
    val allPoints: List<RecyclingPoint> = DummyEcoData.recyclingPoints,
    val selectedWasteType: WasteType? = null,
    val selectedPoint: RecyclingPoint? = null,
    val routeDestination: RecyclingPoint? = null
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
}

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    fun onSearchQueryChange(value: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = value,
                selectedPoint = null,
                routeDestination = null
            )
        }
    }

    fun onWasteTypeSelected(type: WasteType?) {
        _uiState.update { state ->
            state.copy(
                selectedWasteType = type,
                selectedPoint = null,
                routeDestination = null
            )
        }
    }

    fun onPointSelected(point: RecyclingPoint) {
        _uiState.update { it.copy(selectedPoint = point) }
    }

    fun onRouteRequested(point: RecyclingPoint) {
        _uiState.update { it.copy(selectedPoint = point, routeDestination = point) }
    }
}

private val RecyclingPoint.primaryWasteType: WasteType?
    get() = acceptedWasteTypes.firstOrNull()
