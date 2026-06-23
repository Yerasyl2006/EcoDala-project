package com.ecodala.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.data.repository.DemoPointsEconomyRepository
import com.ecodala.core.domain.model.EcoRatingCalculator
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
    val level: Int = EcoRatingCalculator.calculate(DummyEcoData.currentUser.ecoPoints).level,
    val ratingTitle: String = EcoRatingCalculator.calculate(DummyEcoData.currentUser.ecoPoints).title,
    val pointsToNextLevel: Int = EcoRatingCalculator.calculate(DummyEcoData.currentUser.ecoPoints).pointsToNextLevel,
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

class HomeViewModel(
    private val backendRepository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
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
            backendRepository.currentUser()
                .onSuccess { user ->
                    val rating = EcoRatingCalculator.calculate(user.ecoPoints)
                    _uiState.update { state ->
                        state.copy(
                            userName = user.fullName,
                            ecoPoints = user.ecoPoints,
                            level = rating.level,
                            ratingTitle = rating.title,
                            pointsToNextLevel = rating.pointsToNextLevel,
                            treeProgressPercent = rating.progressPercent,
                            globalRank = user.globalRank
                        )
                    }
                }
            val userId = DummyEcoData.currentUser.id
            val wallet = getPointsWalletUseCase(userId).getOrNull()
            val impact = getMonthlyImpactUseCase(userId).getOrNull()
            val streak = getStreakSummaryUseCase(userId).getOrNull()
            val achievements = getAchievementProgressUseCase(userId).getOrNull()

            _uiState.update { state ->
                val fallbackRating = EcoRatingCalculator.calculate(state.ecoPoints)
                state.copy(
                    ecoPoints = wallet?.totalPoints ?: state.ecoPoints,
                    level = wallet?.level ?: fallbackRating.level,
                    ratingTitle = wallet?.ratingTitle ?: fallbackRating.title,
                    pointsToNextLevel = wallet?.pointsToNextLevel ?: fallbackRating.pointsToNextLevel,
                    treeProgressPercent = wallet?.progressToNextLevelPercent ?: fallbackRating.progressPercent,
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
