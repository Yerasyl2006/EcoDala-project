package com.ecodala.feature.achievements.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.Achievement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AchievementsViewModel : ViewModel() {
    private val _achievements = MutableStateFlow(DummyEcoData.achievements)
    val achievements: StateFlow<List<Achievement>> = _achievements
}
