package com.localattendance.client.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Login : Screen("login", "Login")
    object ServerSetup : Screen("server", "Server Setup")
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Classes : Screen("classes", "Classes", Icons.Default.DateRange)
    object TakeAttendance : Screen("attendance/{classId}", "Take Attendance", Icons.Default.List) {
        fun createRoute(classId: String) = "attendance/$classId"
    }
    object ClassDetail : Screen("class/{classId}", "Class Detail", Icons.Default.School) {
        fun createRoute(classId: String) = "class/$classId"
    }
    object Students : Screen("students/{classId}", "Students", Icons.Default.School) {
        fun createRoute(classId: String) = "students/$classId"
    }
    object Timetable : Screen("timetable/{classId}", "Timetable", Icons.Default.DateRange) {
        fun createRoute(classId: String) = "timetable/$classId"
    }
    object Events : Screen("events/{classId}", "Events", Icons.Default.CalendarMonth) {
        fun createRoute(classId: String) = "events/$classId"
    }
    object Reports : Screen("reports/{classId}", "Reports", Icons.Default.Analytics) {
        fun createRoute(classId: String) = "reports/$classId"
    }
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem("dashboard", "Dashboard", Icons.Default.Home)
    object Classes : BottomNavItem("classes", "Classes", Icons.Default.DateRange)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.Classes,
    BottomNavItem.Settings
)