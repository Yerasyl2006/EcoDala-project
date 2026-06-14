package com.ecodala.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.data.dummy.DummyEcoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    val userName: String = "Erasyl",
    val ecoPoints: Int = DummyEcoData.currentUser.ecoPoints,
    val globalRank: Int = DummyEcoData.currentUser.globalRank,
    val level: Int = DummyEcoData.currentUser.level,
    val treeProgressPercent: Int = DummyEcoData.tree.progressPercent,
    val achievements: List<HomeAchievementUi> = DummyEcoData.achievements.map {
        HomeAchievementUi(
            title = it.title,
            subtitle = it.unlockedAt.orEmpty(),
            iconName = it.iconName
        )
    }
)

data class HomeAchievementUi(
    val title: String,
    val subtitle: String,
    val iconName: String
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
}
