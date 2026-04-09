package com.localattendance.client.ui.screens.classdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.ClassRoom
import com.localattendance.client.data.model.Teacher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClassDetailUiState(
    val isLoading: Boolean = true,
    val className: String? = null,
    val role: String? = null,
    val ownerName: String? = null,
    val studentCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class ClassDetailViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(ClassDetailUiState())
        private set

    fun loadClassDetails(classId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val classesResponse = api.getClasses()
                val studentsResponse = api.getStudents(classId)
                val teachersResponse = api.getClassTeachers(classId)

                if (classesResponse.isSuccessful) {
                    val classes = classesResponse.body() ?: emptyList()
                    val currentClass = classes.find { it.id == classId }

                    val studentCount = if (studentsResponse.isSuccessful) {
                        (studentsResponse.body() ?: emptyList()).size
                    } else 0

                    uiState = uiState.copy(
                        isLoading = false,
                        className = currentClass?.name,
                        role = currentClass?.role,
                        ownerName = currentClass?.ownerName,
                        studentCount = studentCount
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Failed to load class")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }
}