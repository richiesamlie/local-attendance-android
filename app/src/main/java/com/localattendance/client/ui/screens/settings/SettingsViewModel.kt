package com.localattendance.client.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.api.SessionCookieStore
import com.localattendance.client.data.repository.SettingsRepository
import com.localattendance.client.data.repository.normalizeServerUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val serverUrl: String? = null,
    val teacherName: String? = null,
    val teacherUsername: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val api: AttendanceApi,
    private val cookieStore: SessionCookieStore
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

    fun clearMessages() {
        uiState = uiState.copy(error = null, successMessage = null)
    }

    fun saveServerUrl(url: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val normalizedUrl = normalizeServerUrl(url)
            if (normalizedUrl == null) {
                uiState = uiState.copy(error = "Enter a valid http:// or https:// server URL")
                return@launch
            }

            val previousUrl = settingsRepository.serverUrl.first()
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                settingsRepository.saveServerUrl(normalizedUrl)
                val healthResponse = api.healthCheck()
                val status = healthResponse.body()?.get("status") as? String
                if (healthResponse.isSuccessful && (status == "ok" || status == "healthy")) {
                    if (previousUrl != normalizedUrl) {
                        cookieStore.clearAll()
                    }
                    uiState = uiState.copy(
                        serverUrl = normalizedUrl,
                        isSaving = false,
                        successMessage = "Server URL updated"
                    )
                    onDone()
                } else {
                    restorePreviousUrl(previousUrl)
                    uiState = uiState.copy(isSaving = false, error = "Server is not responding correctly")
                }
            } catch (e: Exception) {
                restorePreviousUrl(previousUrl)
                uiState = uiState.copy(isSaving = false, error = "Cannot connect to server: ${e.message}")
            }
        }
    }

    fun logout(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                api.revokeSessions(mapOf("sessionId" to "all"))
                api.logout()
            } catch (e: Exception) {
                // Handle error
            } finally {
                cookieStore.clearAll()
                onDone()
            }
        }
    }

    private suspend fun restorePreviousUrl(previousUrl: String?) {
        if (previousUrl.isNullOrBlank()) {
            settingsRepository.clearServerUrl()
        } else {
            settingsRepository.saveServerUrl(previousUrl)
        }
    }
}
