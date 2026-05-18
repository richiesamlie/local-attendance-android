package com.localattendance.client.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.LoginRequest
import com.localattendance.client.data.model.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class SessionRestoreState {
    object Idle : SessionRestoreState()
    object Checking : SessionRestoreState()
    object Restored : SessionRestoreState()
    object NotRestored : SessionRestoreState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var loginState by mutableStateOf<LoginResult>(LoginResult.Idle)
        private set

    var sessionRestoreState by mutableStateOf<SessionRestoreState>(SessionRestoreState.Idle)
        private set

    fun restoreSessionIfAvailable() {
        if (sessionRestoreState is SessionRestoreState.Checking || sessionRestoreState is SessionRestoreState.Restored) {
            return
        }

        viewModelScope.launch {
            sessionRestoreState = SessionRestoreState.Checking
            try {
                val response = api.verifySession()
                val authenticated = response.isSuccessful && response.body()?.get("authenticated") == true
                sessionRestoreState = if (authenticated) {
                    SessionRestoreState.Restored
                } else {
                    SessionRestoreState.NotRestored
                }
            } catch (e: Exception) {
                sessionRestoreState = SessionRestoreState.NotRestored
            }
        }
    }

    fun login(username: String, password: String) {
        if (username.isBlank()) {
            loginState = LoginResult.Error("Username is required")
            return
        }
        if (username.length < 3) {
            loginState = LoginResult.Error("Username must be at least 3 characters")
            return
        }
        if (password.isBlank()) {
            loginState = LoginResult.Error("Password is required")
            return
        }
        if (password.length < 4) {
            loginState = LoginResult.Error("Password must be at least 4 characters")
            return
        }
        viewModelScope.launch {
            loginState = LoginResult.Loading
            try {
                val response = api.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    sessionRestoreState = SessionRestoreState.Restored
                    loginState = LoginResult.Success
                } else {
                    val errorBody = response.body()?.error
                    loginState = LoginResult.Error(errorBody ?: "Login failed")
                }
            } catch (e: Exception) {
                loginState = LoginResult.Error(e.message ?: "Connection error")
            }
        }
    }

    fun resetState() {
        loginState = LoginResult.Idle
    }
}