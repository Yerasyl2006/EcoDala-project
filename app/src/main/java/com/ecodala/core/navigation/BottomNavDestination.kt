package com.ecodala.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavDestinations = listOf(
    BottomNavDestination(EcoDalaRoute.Home.route, "Home", Icons.Filled.Home),
    BottomNavDestination(EcoDalaRoute.Map.route, "Map", Icons.Filled.Map),
    BottomNavDestination(EcoDalaRoute.SubmitWaste.route, "Submit", Icons.Filled.AddCircle),
    BottomNavDestination(EcoDalaRoute.Leaderboard.route, "Leads", Icons.Filled.Leaderboard),
    BottomNavDestination(EcoDalaRoute.Profile.route, "Profile", Icons.Filled.Person)
)
