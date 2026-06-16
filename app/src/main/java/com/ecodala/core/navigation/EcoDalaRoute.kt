package com.ecodala.core.navigation

sealed class EcoDalaRoute(val route: String) {
    data object Splash : EcoDalaRoute("splash")
    data object Login : EcoDalaRoute("login")
    data object Register : EcoDalaRoute("register")
    data object ForgotPassword : EcoDalaRoute("forgot-password")
    data object EmailVerification : EcoDalaRoute("email-verification")

    data object Home : EcoDalaRoute("home")
    data object Map : EcoDalaRoute("map")
    data object RecyclingPointDetails : EcoDalaRoute("recycling-point/{pointId}") {
        fun createRoute(pointId: String) = "recycling-point/$pointId"
    }
    data object BiotoiletDetails : EcoDalaRoute("biotoilet/{toiletId}") {
        fun createRoute(toiletId: String) = "biotoilet/$toiletId"
    }
    data object WaterStationDetails : EcoDalaRoute("water-station/{stationId}") {
        fun createRoute(stationId: String) = "water-station/$stationId"
    }
    data object EcoReportDetails : EcoDalaRoute("eco-report/{reportId}") {
        fun createRoute(reportId: String) = "eco-report/$reportId"
    }
    data object SubmitWaste : EcoDalaRoute("submit-waste")
    data object VirtualTree : EcoDalaRoute("virtual-tree")
    data object Challenges : EcoDalaRoute("challenges")
    data object Leaderboard : EcoDalaRoute("leaderboard")
    data object Profile : EcoDalaRoute("profile")
    data object Achievements : EcoDalaRoute("achievements")
    data object RecyclingHistory : EcoDalaRoute("recycling-history")
    data object Notifications : EcoDalaRoute("notifications")
    data object Settings : EcoDalaRoute("settings")
    data object Support : EcoDalaRoute("support")
    data object AiWasteScanner : EcoDalaRoute("ai-waste-scanner")
}
