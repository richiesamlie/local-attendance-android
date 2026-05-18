package com.localattendance.client.ui.screens.students

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.Student
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class StudentsUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val students: List<Student> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(StudentsUiState())
        private set

    fun clearMessages() {
        uiState = uiState.copy(error = null, successMessage = null)
    }

    fun loadStudents(classId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = api.getStudents(classId)
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        isLoading = false,
                        students = response.body() ?: emptyList()
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Failed to load students")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun addStudent(classId: String, name: String, rollNumber: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val studentId = "student_${UUID.randomUUID().toString().replace("-", "").slice(0..15)}"
                val student = Student(studentId, name, rollNumber)
                val response = api.addStudent(classId, student)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Student added")
                    loadStudents(classId)
                    onDone()
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to add student")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun updateStudent(
        classId: String,
        studentId: String,
        name: String,
        rollNumber: String,
        parentName: String = "",
        parentPhone: String = "",
        isFlagged: Boolean = false,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val request: Map<String, Any> = mapOf(
                    "name" to name,
                    "rollNumber" to rollNumber,
                    "parentName" to parentName,
                    "parentPhone" to parentPhone,
                    "isFlagged" to isFlagged
                )
                val response = api.updateStudent(studentId, request)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Student updated")
                    loadStudents(classId)
                    onDone()
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to update student")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun deleteStudent(classId: String, studentId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null, successMessage = null)
            try {
                val response = api.deleteStudent(studentId)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, successMessage = "Student removed")
                    loadStudents(classId)
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to remove student")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }
}
