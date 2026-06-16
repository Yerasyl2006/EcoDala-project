package com.ecodala.feature.challenges.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.Challenge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChallengesViewModel(
    private val repository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
    private val _challenges = MutableStateFlow(DummyEcoData.challenges)
    val challenges: StateFlow<List<Challenge>> = _challenges

    init {
        viewModelScope.launch {
            repository.challenges()
                .onSuccess { if (it.isNotEmpty()) _challenges.value = it }
        }
    }
}
