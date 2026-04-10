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

data class ServerSettingsUiState(
    val isValidating: Boolean = false,
    val validationError: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class ServerSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(ServerSettingsUiState())
        private set

    fun saveServerUrl(url: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isValidating = true, validationError = null)
            try {
                settingsRepository.saveServerUrl(url)
                val healthResponse = api.healthCheck()
                if (healthResponse.isSuccessful && healthResponse.body()?.get("status") == "ok") {
                    uiState = uiState.copy(isValidating = false, isSaved = true)
                    onComplete()
                } else {
                    uiState = uiState.copy(isValidating = false, validationError = "Server is not responding correctly")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isValidating = false, validationError = "Cannot connect to server: ${e.message}")
            }
        }
    }
}