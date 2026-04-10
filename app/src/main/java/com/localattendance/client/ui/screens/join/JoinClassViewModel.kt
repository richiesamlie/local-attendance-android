package com.localattendance.client.ui.screens.join

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JoinClassUiState(
    val isLoading: Boolean = false,
    val isJoined: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class JoinClassViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(JoinClassUiState())
        private set

    fun joinClass(inviteCode: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = api.redeemInvite(mapOf("code" to inviteCode))
                if (response.isSuccessful) {
                    uiState = uiState.copy(isLoading = false, isJoined = true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    uiState = uiState.copy(
                        isLoading = false,
                        error = errorBody ?: "Failed to join class"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Cannot connect to server: ${e.message}"
                )
            }
        }
    }
}