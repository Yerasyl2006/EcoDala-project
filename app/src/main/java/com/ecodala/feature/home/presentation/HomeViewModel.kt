package com.ecodala.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.DemoPointsEconomyRepository
import com.ecodala.core.domain.usecase.GetAchievementProgressUseCase
import com.ecodala.core.domain.usecase.GetMonthlyImpactUseCase
import com.ecodala.core.domain.usecase.GetPointsWalletUseCase
import com.ecodala.core.domain.usecase.GetStreakSummaryUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val userName: String = "Erasyl",
    val ecoPoints: Int = DummyEcoData.currentUser.ecoPoints,
    val globalRank: Int = DummyEcoData.currentUser.globalRank,
    val level: Int = DummyEcoData.currentUser.level,
    val treeProgressPercent: Int = DummyEcoData.tree.progressPercent,
    val thisMonthPoints: Int = 0,
    val thisMonthKg: Double = 0.0,
    val thisMonthSubmissions: Int = 0,
    val challengePoints: Int = 0,
    val achievementBonusPoints: Int = 0,
    val streakDays: Int = 0,
    val nextStreakRewardPoints: Int = 0,
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
    private val pointsRepository = DemoPointsEconomyRepository()
    private val getPointsWalletUseCase = GetPointsWalletUseCase(pointsRepository)
    private val getMonthlyImpactUseCase = GetMonthlyImpactUseCase(pointsRepository)
    private val getStreakSummaryUseCase = GetStreakSummaryUseCase(pointsRepository)
    private val getAchievementProgressUseCase = GetAchievementProgressUseCase(pointsRepository)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadPointsEconomy()
    }

    fun loadPointsEconomy() {
        viewModelScope.launch {
            val userId = DummyEcoData.currentUser.id
            val wallet = getPointsWalletUseCase(userId).getOrNull()
            val impact = getMonthlyImpactUseCase(userId).getOrNull()
            val streak = getStreakSummaryUseCase(userId).getOrNull()
            val achievements = getAchievementProgressUseCase(userId).getOrNull()

            _uiState.update { state ->
                state.copy(
                    ecoPoints = wallet?.totalPoints ?: state.ecoPoints,
                    level = wallet?.level ?: state.level,
                    treeProgressPercent = wallet?.progressToNextLevelPercent ?: state.treeProgressPercent,
                    thisMonthPoints = wallet?.thisMonthPoints ?: state.thisMonthPoints,
                    challengePoints = wallet?.challengePoints ?: state.challengePoints,
                    achievementBonusPoints = wallet?.achievementBonusPoints ?: state.achievementBonusPoints,
                    thisMonthKg = impact?.recycledKg ?: state.thisMonthKg,
                    thisMonthSubmissions = impact?.submissions ?: state.thisMonthSubmissions,
                    streakDays = streak?.currentDays ?: state.streakDays,
                    nextStreakRewardPoints = streak?.nextRewardPoints ?: state.nextStreakRewardPoints,
                    achievements = achievements
                        ?.filter { it.isUnlocked }
                        ?.take(3)
                        ?.map {
                            HomeAchievementUi(
                                title = it.title,
                                subtitle = "+${it.bonusPoints} pts - ${it.unlockedAt.orEmpty()}",
                                iconName = it.iconName
                            )
                        }
                        ?: state.achievements
                )
            }
        }
    }
}
