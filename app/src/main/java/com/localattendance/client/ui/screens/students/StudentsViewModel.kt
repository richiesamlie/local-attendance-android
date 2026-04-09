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
    val students: List<Student> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(StudentsUiState())
        private set

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

    fun addStudent(classId: String, name: String, rollNumber: String) {
        viewModelScope.launch {
            try {
                val studentId = "student_${UUID.randomUUID().toString().replace("-", "").slice(0..15)}"
                val student = Student(studentId, name, rollNumber)
                val response = api.addStudent(classId, student)
                if (response.isSuccessful) {
                    loadStudents(classId)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            }
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteStudent(studentId)
                if (response.isSuccessful) {
                    uiState = uiState.copy(students = uiState.students.filter { it.id != studentId })
                }
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            }
        }
    }
}