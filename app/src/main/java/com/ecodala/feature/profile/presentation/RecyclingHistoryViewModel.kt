package com.ecodala.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.model.WasteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class RecyclingHistoryViewModel(
    private val repository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecyclingHistoryUiState())
    val uiState: StateFlow<RecyclingHistoryUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.submissions()
                .onSuccess { if (it.isNotEmpty()) _uiState.update { state -> state.copy(submissions = it) } }
        }
    }

    fun onTypeSelected(type: WasteType?) {
        _uiState.update { it.copy(selectedType = type) }
    }
}
