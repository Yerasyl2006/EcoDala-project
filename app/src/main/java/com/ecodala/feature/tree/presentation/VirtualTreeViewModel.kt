package com.ecodala.feature.tree.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.EcoRatingCalculator
import com.ecodala.core.domain.model.TreeGrowthEvent
import com.ecodala.core.domain.model.VirtualTree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VirtualTreeViewModel(
    private val repository: ApiEcoRepository = ApiEcoRepository()
) : ViewModel() {
    private val _tree = MutableStateFlow(buildTreeFromPoints(DummyEcoData.currentUser.ecoPoints))
    val tree: StateFlow<VirtualTree> = _tree

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.currentUser()
                .onSuccess { user ->
                    _tree.value = buildTreeFromPoints(user.ecoPoints)
                }
        }
    }

    fun previewLevel(level: Int) {
        _tree.value = buildTreeFromPoints(level.coerceIn(0, EcoRatingCalculator.MaxLevel) * EcoRatingCalculator.PointsPerLevel)
    }

    private fun buildTreeFromPoints(points: Int): VirtualTree {
        val rating = EcoRatingCalculator.calculate(points)

        return VirtualTree(
            level = rating.level,
            progressPercent = rating.progressPercent,
            currentXp = rating.pointsInCurrentLevel,
            nextLevelXp = rating.pointsForNextLevel,
            growthHistory = buildGrowthHistory(rating.level, rating.totalPoints)
        )
    }

    private fun buildGrowthHistory(level: Int, points: Int): List<TreeGrowthEvent> {
        val stageName = when (level) {
            0 -> "Twig"
            1, 2 -> "Seedling"
            3, 4 -> "Young tree"
            5, 6 -> "Growing tree"
            7, 8, 9 -> "Dense tree"
            else -> "Eco forest"
        }

        return listOf(
            TreeGrowthEvent("Now", "Level $level", "$stageName unlocked with $points EcoPoints"),
            TreeGrowthEvent("EcoPoints", "$points pts", "Every action grows your tree"),
            TreeGrowthEvent("Goal", "Level 10", "Reach ${EcoRatingCalculator.PointsPerLevel * EcoRatingCalculator.MaxLevel} EcoPoints")
        )
    }
}
