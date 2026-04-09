package com.localattendance.client.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var isSaved by mutableStateOf(false)
        private set

    fun saveServerUrl(url: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            settingsRepository.saveServerUrl(url)
            isSaved = true
            onComplete()
        }
    }
}