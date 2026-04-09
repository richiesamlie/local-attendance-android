package com.localattendance.client.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.localattendance.client.ui.screens.auth.LoginScreen
import com.localattendance.client.ui.screens.settings.ServerSettingsScreen
import com.localattendance.client.ui.screens.dashboard.DashboardScreen
import com.localattendance.client.ui.screens.classes.ClassesScreen
import com.localattendance.client.ui.screens.attendance.TakeAttendanceScreen
import com.localattendance.client.ui.screens.classdetail.ClassDetailScreen
import com.localattendance.client.ui.screens.students.StudentsScreen
import com.localattendance.client.ui.screens.timetable.TimetableScreen
import com.localattendance.client.ui.screens.events.EventsScreen
import com.localattendance.client.ui.screens.reports.ReportsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.ServerSetup.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Dashboard.route,
        Screen.Classes.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
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
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ServerSetup.route) {
                ServerSettingsScreen(
                    onServerConfigured = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ServerSetup.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onClassClick = { classId ->
                        navController.navigate(Screen.ClassDetail.createRoute(classId))
                    }
                )
            }

            composable(Screen.Classes.route) {
                ClassesScreen(
                    onClassClick = { classId ->
                        navController.navigate(Screen.ClassDetail.createRoute(classId))
                    },
                    onCreateClass = { classId ->
                        navController.navigate(Screen.ClassDetail.createRoute(classId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                com.localattendance.client.ui.screens.settings.SettingsScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.ClassDetail.route,
                arguments = listOf(navArgument("classId") { type = NavType.StringType })
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getString("classId") ?: ""
                ClassDetailScreen(
                    classId = classId,
                    onNavigateToAttendance = {
                        navController.navigate(Screen.TakeAttendance.createRoute(classId))
                    },
                    onNavigateToStudents = {
                        navController.navigate(Screen.Students.createRoute(classId))
                    },
                    onNavigateToTimetable = {
                        navController.navigate(Screen.Timetable.createRoute(classId))
                    },
                    onNavigateToEvents = {
                        navController.navigate(Screen.Events.createRoute(classId))
                    },
                    onNavigateToReports = {
                        navController.navigate(Screen.Reports.createRoute(classId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.TakeAttendance.route,
                arguments = listOf(navArgument("classId") { type = NavType.StringType })
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getString("classId") ?: ""
                TakeAttendanceScreen(
                    classId = classId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Students.route,
                arguments = listOf(navArgument("classId") { type = NavType.StringType })
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getString("classId") ?: ""
                StudentsScreen(
                    classId = classId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Timetable.route,
                arguments = listOf(navArgument("classId") { type = NavType.StringType })
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getString("classId") ?: ""
                TimetableScreen(
                    classId = classId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Events.route,
                arguments = listOf(navArgument("classId") { type = NavType.StringType })
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getString("classId") ?: ""
                EventsScreen(
                    classId = classId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Reports.route,
                arguments = listOf(navArgument("classId") { type = NavType.StringType })
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getString("classId") ?: ""
                ReportsScreen(
                    classId = classId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}