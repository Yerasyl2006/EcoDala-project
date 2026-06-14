package com.ecodala.feature.achievements.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.repository.DemoPointsEconomyRepository
import com.ecodala.core.domain.model.Achievement
import com.ecodala.core.domain.usecase.GetAchievementProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AchievementsViewModel : ViewModel() {
    private val getAchievementProgressUseCase = GetAchievementProgressUseCase(DemoPointsEconomyRepository())

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            getAchievementProgressUseCase("user-1")
                .onSuccess { _achievements.value = it }
        }
    }
}
