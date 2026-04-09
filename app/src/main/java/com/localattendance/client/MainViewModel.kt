package com.localattendance.client

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.repository.SettingsRepository
import com.localattendance.client.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var startDestination by mutableStateOf<String?>(null)
        private set

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {
        viewModelScope.launch {
            val url = settingsRepository.serverUrl.first()
            startDestination = if (url.isNullOrBlank()) {
                Screen.ServerSetup.route
            } else {
                Screen.Login.route
            }
        }
    }
}