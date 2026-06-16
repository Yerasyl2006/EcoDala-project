package com.ecodala.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecodala.core.session.SessionManager
import com.ecodala.core.ui.components.EcoDalaBottomBar
import com.ecodala.feature.achievements.presentation.AchievementsRoute
import com.ecodala.feature.auth.presentation.ForgotPasswordRoute
import com.ecodala.feature.auth.presentation.EmailVerificationRoute
import com.ecodala.feature.auth.presentation.LoginRoute
import com.ecodala.feature.auth.presentation.RegisterRoute
import com.ecodala.feature.challenges.presentation.ChallengesRoute
import com.ecodala.feature.home.presentation.HomeRoute
import com.ecodala.feature.leaderboard.presentation.LeaderboardRoute
import com.ecodala.feature.map.presentation.BiotoiletDetailsRoute
import com.ecodala.feature.map.presentation.EcoReportDetailsRoute
import com.ecodala.feature.map.presentation.MapRoute
import com.ecodala.feature.map.presentation.RecyclingPointDetailsRoute
import com.ecodala.feature.map.presentation.WaterStationDetailsRoute
import com.ecodala.feature.profile.presentation.NotificationsRoute
import com.ecodala.feature.profile.presentation.ProfileRoute
import com.ecodala.feature.profile.presentation.RecyclingHistoryRoute
import com.ecodala.feature.profile.presentation.SettingsRoute
import com.ecodala.feature.profile.presentation.SupportRoute
import com.ecodala.feature.scanner.presentation.AiWasteScannerRoute
import com.ecodala.feature.splash.presentation.SplashScreen
import com.ecodala.feature.submit.presentation.SubmitWasteRoute
import com.ecodala.feature.tree.presentation.VirtualTreeRoute

@Composable
fun EcoDalaApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = currentDestination.shouldShowBottomBar()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (showBottomBar) {
                EcoDalaBottomBar(
                    currentRoute = currentDestination?.route,
                    onDestinationClick = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = EcoDalaRoute.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(EcoDalaRoute.Splash.route) {
                val session by SessionManager.session.collectAsState()
                SplashScreen(
                    onFinished = {
                        val nextRoute = if (session.isLoggedIn) {
                            EcoDalaRoute.Home.route
                        } else {
                            EcoDalaRoute.Login.route
                        }
                        navController.navigate(nextRoute) {
                            popUpTo(EcoDalaRoute.Splash.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(EcoDalaRoute.Login.route) {
                LoginRoute(
                    onLoginClick = { navController.navigate(EcoDalaRoute.Home.route) },
                    onRegisterClick = { navController.navigate(EcoDalaRoute.Register.route) },
                    onForgotPasswordClick = { navController.navigate(EcoDalaRoute.ForgotPassword.route) }
                )
            }
            composable(EcoDalaRoute.ForgotPassword.route) {
                ForgotPasswordRoute(
                    onBackClick = { navController.popBackStack() },
                    onLoginClick = { navController.popBackStack(EcoDalaRoute.Login.route, inclusive = false) }
                )
            }
            composable(EcoDalaRoute.Register.route) {
                RegisterRoute(
                    onBackClick = { navController.popBackStack() },
                    onCreateAccountClick = {
                        navController.navigate(EcoDalaRoute.EmailVerification.route)
                    },
                    onLoginClick = {
                        navController.navigate(EcoDalaRoute.Login.route) {
                            popUpTo(EcoDalaRoute.Register.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(EcoDalaRoute.EmailVerification.route) {
                EmailVerificationRoute(
                    onContinueClick = {
                        navController.navigate(EcoDalaRoute.Home.route) {
                            popUpTo(EcoDalaRoute.Login.route) {
                                inclusive = true
                            }
                        }
                    },
                    onBackToLoginClick = {
                        navController.navigate(EcoDalaRoute.Login.route) {
                            popUpTo(EcoDalaRoute.Register.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(EcoDalaRoute.Home.route) {
                HomeRoute(
                    onMapClick = { navController.navigate(EcoDalaRoute.Map.route) },
                    onSubmitClick = { navController.navigate(EcoDalaRoute.SubmitWaste.route) },
                    onChallengesClick = { navController.navigate(EcoDalaRoute.Challenges.route) },
                    onLeaderboardClick = { navController.navigate(EcoDalaRoute.Leaderboard.route) },
                    onNotificationsClick = { navController.navigate(EcoDalaRoute.Notifications.route) },
                    onTreeClick = { navController.navigate(EcoDalaRoute.VirtualTree.route) },
                    onAchievementsClick = { navController.navigate(EcoDalaRoute.Achievements.route) }
                )
            }
            composable(EcoDalaRoute.Map.route) {
                MapRoute(
                    onPointDetailsClick = { pointId ->
                        navController.navigate(EcoDalaRoute.RecyclingPointDetails.createRoute(pointId))
                    },
                    onBiotoiletDetailsClick = { toiletId ->
                        navController.navigate(EcoDalaRoute.BiotoiletDetails.createRoute(toiletId))
                    },
                    onWaterStationDetailsClick = { stationId ->
                        navController.navigate(EcoDalaRoute.WaterStationDetails.createRoute(stationId))
                    },
                    onEcoReportDetailsClick = { reportId ->
                        navController.navigate(EcoDalaRoute.EcoReportDetails.createRoute(reportId))
                    }
                )
            }
            composable(EcoDalaRoute.RecyclingPointDetails.route) {
                RecyclingPointDetailsRoute(
                    onBackClick = { navController.popBackStack() },
                    onBuildRouteClick = { navController.navigate(EcoDalaRoute.Map.route) },
                    onCallClick = {},
                    onShareClick = {}
                )
            }
            composable(EcoDalaRoute.BiotoiletDetails.route) { backStackEntry ->
                BiotoiletDetailsRoute(
                    toiletId = backStackEntry.arguments?.getString("toiletId"),
                    onBackClick = { navController.popBackStack() },
                    onBuildRouteClick = { navController.navigate(EcoDalaRoute.Map.route) }
                )
            }
            composable(EcoDalaRoute.WaterStationDetails.route) { backStackEntry ->
                WaterStationDetailsRoute(
                    stationId = backStackEntry.arguments?.getString("stationId"),
                    onBackClick = { navController.popBackStack() },
                    onBuildRouteClick = { navController.navigate(EcoDalaRoute.Map.route) }
                )
            }
            composable(EcoDalaRoute.EcoReportDetails.route) { backStackEntry ->
                EcoReportDetailsRoute(
                    reportId = backStackEntry.arguments?.getString("reportId"),
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(EcoDalaRoute.SubmitWaste.route) {
                SubmitWasteRoute(
                    onBackClick = { navController.popBackStack() },
                    onSubmitClick = { navController.navigate(EcoDalaRoute.VirtualTree.route) }
                )
            }
            composable(EcoDalaRoute.VirtualTree.route) {
                VirtualTreeRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(EcoDalaRoute.Challenges.route) {
                ChallengesRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(EcoDalaRoute.Leaderboard.route) {
                LeaderboardRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(EcoDalaRoute.Profile.route) {
                ProfileRoute(
                    onBackClick = { navController.popBackStack() },
                    onAchievementsClick = { navController.navigate(EcoDalaRoute.Achievements.route) },
                    onRecyclingHistoryClick = { navController.navigate(EcoDalaRoute.RecyclingHistory.route) },
                    onNotificationsClick = { navController.navigate(EcoDalaRoute.Notifications.route) },
                    onSettingsClick = { navController.navigate(EcoDalaRoute.Settings.route) },
                    onSupportClick = { navController.navigate(EcoDalaRoute.Support.route) },
                    onLogoutClick = {
                        navController.navigate(EcoDalaRoute.Login.route) {
                            popUpTo(EcoDalaRoute.Home.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(EcoDalaRoute.Achievements.route) {
                AchievementsRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(EcoDalaRoute.RecyclingHistory.route) {
                RecyclingHistoryRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(EcoDalaRoute.Notifications.route) {
                NotificationsRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(EcoDalaRoute.Settings.route) {
                SettingsRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(EcoDalaRoute.Support.route) {
                SupportRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(EcoDalaRoute.AiWasteScanner.route) {
                AiWasteScannerRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

private fun NavDestination?.shouldShowBottomBar(): Boolean {
    val currentRoute = this?.route ?: return false
    return bottomNavDestinations.any { it.route == currentRoute }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box {
        Text(text = name)
    }
}
