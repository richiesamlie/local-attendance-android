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

data class ServerSettingsUiState(
    val isValidating: Boolean = false,
    val validationError: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class ServerSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val api: AttendanceApi,
    private val cookieStore: SessionCookieStore
) : ViewModel() {

    var uiState by mutableStateOf(ServerSettingsUiState())
        private set

    fun saveServerUrl(url: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isValidating = true, validationError = null)
            val normalizedUrl = normalizeServerUrl(url)
            if (normalizedUrl == null) {
                uiState = uiState.copy(isValidating = false, validationError = "Enter a valid http:// or https:// server URL")
                return@launch
            }

            val previousUrl = settingsRepository.serverUrl.first()
            try {
                settingsRepository.saveServerUrl(normalizedUrl)
                val healthResponse = api.healthCheck()
                val status = healthResponse.body()?.get("status") as? String
                if (healthResponse.isSuccessful && (status == "ok" || status == "healthy")) {
                    if (previousUrl != normalizedUrl) {
                        cookieStore.clearAll()
                    }
                    uiState = uiState.copy(isValidating = false, isSaved = true)
                    onComplete()
                } else {
                    restorePreviousUrl(previousUrl)
                    uiState = uiState.copy(isValidating = false, validationError = "Server is not responding correctly")
                }
            } catch (e: Exception) {
                restorePreviousUrl(previousUrl)
                uiState = uiState.copy(isValidating = false, validationError = "Cannot connect to server: ${e.message}")
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
