package com.localattendance.client.ui.screens.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.DailyNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DailyNotesUiState(
    val isLoading: Boolean = true,
    val notes: List<DailyNote> = emptyList(),
    val dateInput: String = LocalDate.now().toString(),
    val noteInput: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class DailyNotesViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(DailyNotesUiState())
        private set

    fun loadDailyNotes(classId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, successMessage = null)
            try {
                val response = api.getDailyNotes(classId)
                if (response.isSuccessful) {
                    val notes = (response.body() ?: emptyMap())
                        .map { DailyNote(date = it.key, note = it.value) }
                        .sortedByDescending { it.date }
                    uiState = uiState.copy(isLoading = false, notes = notes)
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Failed to load daily notes")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onDateChange(value: String) {
        uiState = uiState.copy(dateInput = value)
    }

    fun onNoteChange(value: String) {
        uiState = uiState.copy(noteInput = value)
    }

    fun saveDailyNote(classId: String) {
        val date = uiState.dateInput.trim()
        val note = uiState.noteInput.trim()
        if (date.isEmpty() || note.isEmpty()) {
            uiState = uiState.copy(error = "Date and note are required")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.saveDailyNote(
                    classId,
                    mapOf("date" to date, "note" to note)
                )
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        isSaving = false,
                        noteInput = "",
                        successMessage = "Daily note saved"
                    )
                    loadDailyNotes(classId)
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to save daily note")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }
}
