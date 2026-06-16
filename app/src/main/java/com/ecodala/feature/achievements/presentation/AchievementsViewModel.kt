package com.ecodala.feature.achievements.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.data.repository.DemoPointsEconomyRepository
import com.ecodala.core.domain.model.Achievement
import com.ecodala.core.domain.usecase.GetAchievementProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AchievementsViewModel(
    private val repository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
    private val getAchievementProgressUseCase = GetAchievementProgressUseCase(DemoPointsEconomyRepository())

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            repository.achievements()
                .onSuccess { if (it.isNotEmpty()) _achievements.value = it }
                .onFailure {
                    getAchievementProgressUseCase("user-1")
                        .onSuccess { achievements -> _achievements.value = achievements }
                        .onFailure { _achievements.value = DummyEcoData.achievements }
                }
        }
    }
}
