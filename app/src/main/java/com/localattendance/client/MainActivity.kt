package com.localattendance.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.localattendance.client.data.api.AuthEvents
import com.localattendance.client.ui.navigation.AppNavigation
import com.localattendance.client.ui.navigation.Screen
import com.localattendance.client.ui.theme.LocalAttendanceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authEvents: AuthEvents

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocalAttendanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val viewModel: MainViewModel = hiltViewModel()
    val startDestination = viewModel.startDestination

    if (startDestination != null) {
        AppNavigation(startDestination = startDestination, authEvents = authEvents)
    }
}