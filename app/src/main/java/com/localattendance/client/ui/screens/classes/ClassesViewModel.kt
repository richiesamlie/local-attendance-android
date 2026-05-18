package com.localattendance.client.ui.screens.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.ClassRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ClassesUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val classes: List<ClassRoom> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ClassesViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(ClassesUiState())
        private set

    init {
        loadClasses()
    }

    fun clearMessages() {
        uiState = uiState.copy(error = null, successMessage = null)
    }

    fun loadClasses() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = api.getClasses()
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        isLoading = false,
                        classes = response.body() ?: emptyList()
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Failed to load classes")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun createClass(name: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val classId = "class_${UUID.randomUUID().toString().replace("-", "").slice(0..15)}"
                val response = api.createClass(mapOf("id" to classId, "name" to name))
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Class created")
                    loadClasses()
                    onDone()
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to create class")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun updateClass(classId: String, name: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.updateClass(classId, mapOf("name" to name))
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Class updated")
                    loadClasses()
                    onDone()
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to update class")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun deleteClass(classId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.deleteClass(classId)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Class deleted")
                    loadClasses()
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to delete class")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }
}
