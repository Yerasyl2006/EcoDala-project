package com.ecodala.feature.submit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.ScannerResult
import com.ecodala.core.domain.model.WasteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SubmitWasteUiState(
    val wasteType: WasteType = WasteType.Plastic,
    val quantity: Int = 5,
    val unit: String = "kg",
    val comment: String = "",
    val rewardPoints: Int = 50,
    val isSubmitting: Boolean = false,
    val submitMessage: String? = null,
    val aiScan: AiScanUiState = AiScanUiState()
)

data class AiScanUiState(
    val isScanning: Boolean = false,
    val result: ScannerResult? = null,
    val nearestPoint: RecyclingPoint? = null,
    val errorMessage: String? = null
)

class SubmitWasteViewModel(
    private val repository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(SubmitWasteUiState())
    val uiState: StateFlow<SubmitWasteUiState> = _uiState

    fun onQuantityChange(value: Int) {
        _uiState.update { it.copy(quantity = value.coerceAtLeast(1)) }
    }

    fun onWasteTypeChange(value: WasteType) {
        _uiState.update { it.copy(wasteType = value) }
    }

    fun onUnitChange(value: String) {
        _uiState.update { it.copy(unit = value) }
    }

    fun onCommentChange(value: String) {
        _uiState.update { it.copy(comment = value) }
    }

    fun increaseQuantity() {
        _uiState.update { it.copy(quantity = it.quantity + 1) }
    }

    fun decreaseQuantity() {
        _uiState.update { it.copy(quantity = (it.quantity - 1).coerceAtLeast(1)) }
    }

    fun scanWastePhoto() {
        scanCapturedPhoto("demo-gallery-photo")
    }

    fun scanCapturedPhoto(photoSource: String = "camera-capture") {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    aiScan = it.aiScan.copy(
                        isScanning = true,
                        errorMessage = null
                    )
                )
            }

            val result = repository.scanWaste(photoSource).getOrElse { error ->
                _uiState.update {
                    it.copy(
                        aiScan = AiScanUiState(
                            isScanning = false,
                            errorMessage = error.message ?: "Scanner is unavailable"
                        )
                    )
                }
                return@launch
            }
            val nearestPoint = DummyEcoData.recyclingPoints
                .filter { result.wasteType in it.acceptedWasteTypes }
                .minByOrNull { it.distanceMeters }

            _uiState.update {
                it.copy(
                    wasteType = result.wasteType,
                    rewardPoints = calculateRewardPoints(result.wasteType, it.quantity),
                    aiScan = AiScanUiState(
                        isScanning = false,
                        result = result,
                        nearestPoint = nearestPoint
                    )
                )
            }
        }
    }

    fun submitWaste(onSuccess: () -> Unit = {}) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitMessage = null) }
            repository.submitWaste(
                type = state.wasteType,
                quantity = state.quantity.toDouble(),
                unit = state.unit,
                comment = state.comment
            )
                .onSuccess { submission ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            rewardPoints = submission.earnedPoints,
                            submitMessage = "Submitted +${submission.earnedPoints} pts"
                        )
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submitMessage = error.message ?: "Submit failed"
                        )
                    }
                }
        }
    }

    fun clearScanResult() {
        _uiState.update { it.copy(aiScan = AiScanUiState()) }
    }

    private fun calculateRewardPoints(type: WasteType, quantity: Int): Int {
        val multiplier = when (type) {
            WasteType.Plastic -> 10
            WasteType.Paper -> 8
            WasteType.Glass -> 6
            WasteType.Batteries -> 12
            WasteType.Electronics -> 18
            WasteType.Organic -> 5
            WasteType.Metal -> 14
        }
        return (quantity * multiplier).coerceAtLeast(10)
    }
}
