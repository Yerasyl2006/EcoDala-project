package com.ecodala.feature.leaderboard.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.LeaderboardEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FacultyLeaderboardEntry(
    val rank: Int,
    val name: String,
    val shortName: String,
    val points: Int,
    val members: Int,
    val topContributor: String,
    val weeklyGrowthPercent: Int,
    val isCurrentFaculty: Boolean = false
)

class LeaderboardViewModel : ViewModel() {
    private val currentUserPoints = DummyEcoData.pointsLedger
        .filter { it.userId == DummyEcoData.currentUser.id }
        .sumOf { it.points }

    private val _entries = MutableStateFlow(
        DummyEcoData.leaderboard.map { entry ->
            if (entry.isCurrentUser) {
                entry.copy(points = currentUserPoints)
            } else {
                entry
            }
        }
    )
    val entries: StateFlow<List<LeaderboardEntry>> = _entries

    private val _faculties = MutableStateFlow(
        listOf(
            FacultyLeaderboardEntry(
                rank = 1,
                name = "Computer Science",
                shortName = "CS",
                points = 8420,
                members = 128,
                topContributor = "Aibek",
                weeklyGrowthPercent = 18
            ),
            FacultyLeaderboardEntry(
                rank = 2,
                name = "Environmental Studies",
                shortName = "ENV",
                points = 7890,
                members = 96,
                topContributor = "Alina",
                weeklyGrowthPercent = 14,
                isCurrentFaculty = true
            ),
            FacultyLeaderboardEntry(
                rank = 3,
                name = "Business School",
                shortName = "BUS",
                points = 7310,
                members = 112,
                topContributor = "Yerzhan",
                weeklyGrowthPercent = 11
            ),
            FacultyLeaderboardEntry(
                rank = 4,
                name = "Engineering",
                shortName = "ENG",
                points = 6950,
                members = 104,
                topContributor = "Samat Bolat",
                weeklyGrowthPercent = 9
            ),
            FacultyLeaderboardEntry(
                rank = 5,
                name = "Medicine",
                shortName = "MED",
                points = 6410,
                members = 88,
                topContributor = "Aizhan K.",
                weeklyGrowthPercent = 7
            )
        )
    )
    val faculties: StateFlow<List<FacultyLeaderboardEntry>> = _faculties
}
