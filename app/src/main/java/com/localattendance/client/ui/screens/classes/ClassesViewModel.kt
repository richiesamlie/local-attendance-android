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
    val classes: List<ClassRoom> = emptyList(),
    val error: String? = null
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

    fun createClass(name: String) {
        viewModelScope.launch {
            try {
                val classId = "class_${UUID.randomUUID().toString().replace("-", "").slice(0..15)}"
                val response = api.createClass(mapOf("id" to classId, "name" to name))
                if (response.isSuccessful) {
                    loadClasses()
                }
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            }
        }
    }

    fun deleteClass(classId: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteClass(classId)
                if (response.isSuccessful) {
                    loadClasses()
                }
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            }
        }
    }
}