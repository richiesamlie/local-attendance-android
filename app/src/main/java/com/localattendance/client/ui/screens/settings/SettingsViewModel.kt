package com.localattendance.client.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val serverUrl: String? = null,
    val teacherName: String? = null,
    val teacherUsername: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(SettingsUiState())
        private set

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            val url = settingsRepository.serverUrl.first()
            uiState = uiState.copy(serverUrl = url)

            try {
                val response = api.getMe()
                if (response.isSuccessful) {
                    response.body()?.let { teacher ->
                        uiState = uiState.copy(
                            teacherName = teacher.name,
                            teacherUsername = teacher.username
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    fun saveServerUrl(url: String) {
        viewModelScope.launch {
            settingsRepository.saveServerUrl(url)
            uiState = uiState.copy(serverUrl = url)
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                api.logout()
                settingsRepository.clearServerUrl()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}