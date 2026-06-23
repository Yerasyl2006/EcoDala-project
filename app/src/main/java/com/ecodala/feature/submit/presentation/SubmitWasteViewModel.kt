package com.ecodala.feature.submit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.EcoRatingCalculator
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
    val rewardPoints: Int = EcoRatingCalculator.pointsForWaste(WasteType.Plastic, 5.0, "kg"),
    val photoPath: String? = null,
    val isSubmitting: Boolean = false,
    val submitMessage: SubmitWasteMessage? = null,
    val submitMessagePoints: Int = 0,
    val aiScan: AiScanUiState = AiScanUiState()
)

data class AiScanUiState(
    val isScanning: Boolean = false,
    val result: ScannerResult? = null,
    val nearestPoint: RecyclingPoint? = null,
    val errorMessage: SubmitWasteMessage? = null
)

enum class SubmitWasteMessage {
    AiScannerUnavailable,
    CameraCaptureFailed,
    SubmissionFailed,
    Approved,
    SubmittedForReview
}

class SubmitWasteViewModel(
    private val repository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(SubmitWasteUiState())
    val uiState: StateFlow<SubmitWasteUiState> = _uiState

    fun onQuantityChange(value: Int) {
        _uiState.update {
            val quantity = value.coerceAtLeast(1)
            it.copy(
                quantity = quantity,
                rewardPoints = calculateRewardPoints(it.wasteType, quantity, it.unit)
            )
        }
    }

    fun onWasteTypeChange(value: WasteType) {
        _uiState.update {
            it.copy(
                wasteType = value,
                rewardPoints = calculateRewardPoints(value, it.quantity, it.unit)
            )
        }
    }

    fun onUnitChange(value: String) {
        _uiState.update {
            it.copy(
                unit = value,
                rewardPoints = calculateRewardPoints(it.wasteType, it.quantity, value)
            )
        }
    }

    fun onCommentChange(value: String) {
        _uiState.update { it.copy(comment = value) }
    }

    fun increaseQuantity() {
        _uiState.update {
            val quantity = it.quantity + 1
            it.copy(
                quantity = quantity,
                rewardPoints = calculateRewardPoints(it.wasteType, quantity, it.unit)
            )
        }
    }

    fun decreaseQuantity() {
        _uiState.update {
            val quantity = (it.quantity - 1).coerceAtLeast(1)
            it.copy(
                quantity = quantity,
                rewardPoints = calculateRewardPoints(it.wasteType, quantity, it.unit)
            )
        }
    }

    fun scanWastePhoto() {
        scanCapturedPhoto("demo-gallery-photo")
    }

    fun scanCapturedPhoto(photoSource: String = "camera-capture") {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    photoPath = photoSource.takeIf { source -> source.endsWith(".jpg", ignoreCase = true) },
                    aiScan = it.aiScan.copy(
                        isScanning = true,
                        errorMessage = null
                    )
                )
            }

            val result = repository.scanWaste(
                hint = if (photoSource.endsWith(".jpg", ignoreCase = true)) "android-camera" else photoSource,
                imagePath = photoSource.takeIf { it.endsWith(".jpg", ignoreCase = true) }
            ).getOrElse { error ->
                _uiState.update {
                    it.copy(
                        aiScan = AiScanUiState(
                            isScanning = false,
                            errorMessage = SubmitWasteMessage.AiScannerUnavailable
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
                    rewardPoints = calculateRewardPoints(result.wasteType, it.quantity, it.unit),
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
                comment = state.comment,
                photoPath = state.photoPath
            )
                .onSuccess { submission ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            rewardPoints = submission.earnedPoints.takeIf { points -> points > 0 }
                                ?: calculateRewardPoints(state.wasteType, state.quantity, state.unit),
                            submitMessage = buildSubmitMessage(submission.earnedPoints),
                            submitMessagePoints = submission.earnedPoints
                        )
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submitMessage = SubmitWasteMessage.SubmissionFailed,
                            submitMessagePoints = 0
                        )
                    }
                }
        }
    }

    fun onCameraCaptureFailed() {
        _uiState.update {
            it.copy(
                aiScan = it.aiScan.copy(
                    isScanning = false,
                    errorMessage = SubmitWasteMessage.CameraCaptureFailed
                )
            )
        }
    }

    fun clearScanResult() {
        _uiState.update { it.copy(photoPath = null, aiScan = AiScanUiState()) }
    }

    private fun calculateRewardPoints(type: WasteType, quantity: Int, unit: String): Int {
        return EcoRatingCalculator.pointsForWaste(type, quantity.toDouble(), unit)
    }

    private fun buildSubmitMessage(earnedPoints: Int): SubmitWasteMessage {
        return if (earnedPoints > 0) {
            SubmitWasteMessage.Approved
        } else {
            SubmitWasteMessage.SubmittedForReview
        }
    }
}
