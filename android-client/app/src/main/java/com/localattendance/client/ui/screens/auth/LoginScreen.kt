package com.localattendance.client.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.LoginRequest

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {
    var loginState by mutableStateOf<LoginResult>(LoginResult.Idle)
        private set

    fun login(username: String, password: String) {
        viewModelScope.launch {
            loginState = LoginResult.Loading
            try {
                val response = api.login(LoginRequest(username, password))
                loginState = LoginResult.Success(response)
            } catch (e: Exception) {
                loginState = LoginResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class LoginResult {
        object Idle : LoginResult()
        object Loading : LoginResult()
        data class Success(val teacher: com.localattendance.client.data.model.LoginResponse) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state = viewModel.loginState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Teacher Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (state is AuthViewModel.LoginResult.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.login(username, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }

        if (state is AuthViewModel.LoginResult.Error) {
            Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        LaunchedEffect(state) {
            if (state is AuthViewModel.LoginResult.Success) {
                onLoginSuccess()
            }
        }
    }
}
