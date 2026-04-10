package com.localattendance.client

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.api.AuthEvents
import com.localattendance.client.data.repository.SettingsRepository
import com.localattendance.client.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val api: AttendanceApi,
    val authEvents: AuthEvents
) : ViewModel() {

    var startDestination by mutableStateOf<String?>(null)
        private set

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {
        viewModelScope.launch {
            val url = settingsRepository.serverUrl.first()
            if (url.isNullOrBlank()) {
                startDestination = Screen.ServerSetup.route
            } else {
                try {
                    val verifyResponse = api.verifySession()
                    if (verifyResponse.isSuccessful && verifyResponse.body()?.get("authenticated") == true) {
                        startDestination = Screen.Dashboard.route
                    } else {
                        startDestination = Screen.Login.route
                    }
                } catch (e: Exception) {
                    startDestination = Screen.Login.route
                }
            }
        }
    }
}