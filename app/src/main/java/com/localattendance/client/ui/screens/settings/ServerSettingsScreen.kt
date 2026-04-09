package com.localattendance.client.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.localattendance.client.data.repository.SettingsRepository

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val serverUrl: StateFlow<String?> = settingsRepository.serverUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveUrl(url: String) {
        viewModelScope.launch {
            settingsRepository.saveServerUrl(url)
        }
    }
}

@Composable
fun ServerSettingsScreen(
    viewModel: SettingsViewModel,
    onUrlSaved: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val currentUrl by viewModel.serverUrl.collectAsState()

    LaunchedEffect(currentUrl) {
        text = currentUrl ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Server Configuration",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Enter the local IP address of your server (e.g., http://192.168.1.5:3000)",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Server URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.saveUrl(text)
                onUrlSaved()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save and Continue")
        }
    }
}
