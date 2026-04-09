package com.localattendance.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.localattendance.client.ui.screens.settings.ServerSettingsScreen
import com.localattendance.client.ui.screens.auth.LoginScreen
import com.localattendance.client.ui.theme.LocalAttendanceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocalAttendanceTheme {
                val navController = rememberNavController()

                // State to track if we've determined the start destination
                var startDestination by remember { mutableStateOf<String?>(null) }

                // We use the SettingsViewModel to access the repository
                val settingsViewModel: com.localattendance.client.ui.screens.settings.SettingsViewModel = hiltViewModel()

                LaunchedEffect(Unit) {
                    val url = settingsViewModel.serverUrl.first()
                    startDestination = if (url != null) "login" else "settings"
                }

                if (startDestination != null) {
                    NavHost(navController = navController, startDestination = startDestination!!) {
                        composable("settings") {
                            ServerSettingsScreen(
                                viewModel = hiltViewModel(),
                                onUrlSaved = {
                                    navController.navigate("login") {
                                        popUpTo("settings") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                viewModel = hiltViewModel(),
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            com.localattendance.client.ui.screens.home.HomeScreen(
                                viewModel = hiltViewModel(),
                                onClassSelected = { classId ->
                                    navController.navigate("class/$classId")
                                }
                            )
                        }
                        composable("class/{classId}") { backStackEntry ->
                            val classId = backStackEntry.arguments?.getString("classId") ?: ""
                            com.localattendance.client.ui.screens.home.ClassDetailScreen(
                                classId = classId,
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
