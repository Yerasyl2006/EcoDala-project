package com.ecodala.feature.scanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.ScannerResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AiWasteScannerUiState(
    val isScanning: Boolean = false,
    val scannerResult: ScannerResult? = null,
    val nearestPoint: RecyclingPoint? = null
)

class AiWasteScannerViewModel(
    private val repository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AiWasteScannerUiState())
    val uiState: StateFlow<AiWasteScannerUiState> = _uiState

    fun scanWaste(photoPath: String? = null) {
        viewModelScope.launch {
            _uiState.value = AiWasteScannerUiState(isScanning = true)
            val result = repository.scanWaste(
                hint = if (photoPath.isNullOrBlank()) "scanner-screen-capture" else "android-camera",
                imagePath = photoPath
            ).getOrElse {
                DummyEcoData.scannerResult
            }
            val nearestPoint = DummyEcoData.recyclingPoints
                .filter { result.wasteType in it.acceptedWasteTypes }
                .minByOrNull { it.distanceMeters }
            _uiState.value = AiWasteScannerUiState(
                scannerResult = result,
                nearestPoint = nearestPoint
            )
        }
    }
}
