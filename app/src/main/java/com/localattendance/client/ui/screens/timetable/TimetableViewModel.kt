package com.localattendance.client.ui.screens.timetable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.TimetableSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimetableUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val slots: List<TimetableSlot> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(TimetableUiState())
        private set

    fun clearMessages() {
        uiState = uiState.copy(error = null, successMessage = null)
    }

    fun loadTimetable(classId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = api.getTimetable(classId)
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        isLoading = false,
                        slots = response.body() ?: emptyList()
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Failed to load timetable")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun addTimetableSlot(classId: String, slot: TimetableSlot, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.addTimetableSlot(classId, slot)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Timetable slot added")
                    loadTimetable(classId)
                    onDone()
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to add timetable slot")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun updateTimetableSlot(classId: String, id: String, request: Map<String, Any>, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.updateTimetableSlot(id, request)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Timetable slot updated")
                    loadTimetable(classId)
                    onDone()
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to update timetable slot")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun deleteTimetableSlot(classId: String, id: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.deleteTimetableSlot(id)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Timetable slot deleted")
                    loadTimetable(classId)
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to delete timetable slot")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }
}