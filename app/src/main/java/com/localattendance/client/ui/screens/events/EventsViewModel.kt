package com.localattendance.client.ui.screens.events

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventsUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val events: List<Event> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(EventsUiState())
        private set

    fun clearMessages() {
        uiState = uiState.copy(error = null, successMessage = null)
    }

    fun loadEvents(classId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = api.getEvents(classId)
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        isLoading = false,
                        events = response.body() ?: emptyList()
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Failed to load events")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun addEvent(classId: String, event: Event, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.addEvent(classId, event)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Event added")
                    loadEvents(classId)
                    onDone()
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to add event")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun updateEvent(classId: String, id: String, request: Map<String, Any>, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.updateEvent(id, request)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Event updated")
                    loadEvents(classId)
                    onDone()
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to update event")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun deleteEvent(classId: String, id: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.deleteEvent(id)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Event deleted")
                    loadEvents(classId)
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to delete event")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }
}