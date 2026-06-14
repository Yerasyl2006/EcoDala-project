package com.ecodala.feature.profile.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.model.WasteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class RecyclingHistoryUiState(
    val submissions: List<WasteSubmission> = DummyEcoData.wasteSubmissions,
    val selectedType: WasteType? = null
) {
    val filteredSubmissions: List<WasteSubmission>
        get() = selectedType?.let { type -> submissions.filter { it.wasteType == type } } ?: submissions

    val totalQuantityKg: Int
        get() = submissions
            .filter { it.unit == "kg" }
            .sumOf { it.quantity.toInt() }

    val totalPoints: Int
        get() = submissions.sumOf { it.earnedPoints }
}

class RecyclingHistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecyclingHistoryUiState())
    val uiState: StateFlow<RecyclingHistoryUiState> = _uiState

    fun onTypeSelected(type: WasteType?) {
        _uiState.update { it.copy(selectedType = type) }
    }
}
