package com.ecodala.feature.map.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.RecyclingPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecyclingPointDetailsUiState(
    val point: RecyclingPoint? = null,
    val sustainableFact: String = "Recycling one ton of paper saves about 17 trees and thousands of liters of water."
)

class RecyclingPointDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
    private val pointId: String = checkNotNull(savedStateHandle["pointId"])

    private val _uiState = MutableStateFlow(
        RecyclingPointDetailsUiState(
            point = DummyEcoData.recyclingPoints.firstOrNull { it.id == pointId }
                ?: DummyEcoData.recyclingPoints.firstOrNull()
        )
    )
    val uiState: StateFlow<RecyclingPointDetailsUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.recyclingPoint(pointId)
                .onSuccess { point -> _uiState.update { it.copy(point = point) } }
        }
    }
}
