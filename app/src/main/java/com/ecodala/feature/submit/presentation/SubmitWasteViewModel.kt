package com.ecodala.feature.submit.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.domain.model.WasteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class SubmitWasteUiState(
    val wasteType: WasteType = WasteType.Plastic,
    val quantity: Int = 5,
    val unit: String = "kg",
    val comment: String = "",
    val rewardPoints: Int = 50
)

class SubmitWasteViewModel : ViewModel() {
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
}
