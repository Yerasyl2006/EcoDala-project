package com.ecodala.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.DemoAuthRepository
import com.ecodala.core.data.repository.DemoPointsEconomyRepository
import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.usecase.GetMonthlyImpactUseCase
import com.ecodala.core.domain.usecase.GetPointsWalletUseCase
import com.ecodala.core.domain.usecase.GetStreakSummaryUseCase
import com.ecodala.core.domain.usecase.LogoutUseCase
import com.ecodala.core.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val thisMonthKg: Double = 0.0,
    val totalPoints: Int = DummyEcoData.currentUser.ecoPoints,
    val thisMonthPoints: Int = 0,
    val trees: Int = 2,
    val streakDays: Int = 0,
    val nextStreakRewardPoints: Int = 0
)

class ProfileViewModel : ViewModel() {
    private val logoutUseCase = LogoutUseCase(DemoAuthRepository())
    private val pointsRepository = DemoPointsEconomyRepository()
    private val getPointsWalletUseCase = GetPointsWalletUseCase(pointsRepository)
    private val getMonthlyImpactUseCase = GetMonthlyImpactUseCase(pointsRepository)
    private val getStreakSummaryUseCase = GetStreakSummaryUseCase(pointsRepository)

    private val _user = MutableStateFlow(sessionUserOrFallback())
    val user: StateFlow<EcoUser> = _user

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadEconomy()
    }

    fun retry() {
        _user.value = sessionUserOrFallback()
        loadEconomy()
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLoggedOut()
        }
    }

    private fun loadEconomy() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val userId = DummyEcoData.currentUser.id
            val wallet = getPointsWalletUseCase(userId).getOrNull()
            val impact = getMonthlyImpactUseCase(userId).getOrNull()
            val streak = getStreakSummaryUseCase(userId).getOrNull()
            val total = wallet?.totalPoints ?: DummyEcoData.currentUser.ecoPoints

            _uiState.value = ProfileUiState(
                thisMonthKg = impact?.recycledKg ?: 0.0,
                totalPoints = total,
                thisMonthPoints = wallet?.thisMonthPoints ?: 0,
                trees = (total / 250).coerceAtLeast(1),
                streakDays = streak?.currentDays ?: 0,
                nextStreakRewardPoints = streak?.nextRewardPoints ?: 0
            )
        }
    }

    private fun sessionUserOrFallback(): EcoUser {
        val session = SessionManager.session.value
        return DummyEcoData.currentUser.copy(
            id = session.userId ?: DummyEcoData.currentUser.id,
            fullName = session.fullName ?: DummyEcoData.currentUser.fullName,
            email = session.email ?: DummyEcoData.currentUser.email
        )
    }
}
