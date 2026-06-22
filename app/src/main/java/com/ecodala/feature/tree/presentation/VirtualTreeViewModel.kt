package com.ecodala.feature.tree.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
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
        _tree.value = buildTreeFromPoints(level.coerceIn(0, MAX_TREE_LEVEL) * POINTS_PER_LEVEL)
    }

    private fun buildTreeFromPoints(points: Int): VirtualTree {
        val safePoints = points.coerceAtLeast(0)
        val level = (safePoints / POINTS_PER_LEVEL).coerceIn(0, MAX_TREE_LEVEL)
        val isMaxLevel = level == MAX_TREE_LEVEL
        val currentXp = if (isMaxLevel) POINTS_PER_LEVEL else safePoints % POINTS_PER_LEVEL
        val progressPercent = if (isMaxLevel) 100 else currentXp * 100 / POINTS_PER_LEVEL

        return VirtualTree(
            level = level,
            progressPercent = progressPercent,
            currentXp = currentXp,
            nextLevelXp = POINTS_PER_LEVEL,
            growthHistory = buildGrowthHistory(level, safePoints)
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
            TreeGrowthEvent("Goal", "Level 10", "Reach ${POINTS_PER_LEVEL * MAX_TREE_LEVEL} EcoPoints")
        )
    }

    private companion object {
        const val POINTS_PER_LEVEL = 100
        const val MAX_TREE_LEVEL = 10
    }
}
