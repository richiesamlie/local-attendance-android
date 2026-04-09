package com.localattendance.client.ui.screens.attendance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.AttendanceRecord
import com.localattendance.client.data.model.Student
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TakeAttendanceUiState(
    val isLoading: Boolean = true,
    val students: List<Student> = emptyList(),
    val attendanceMap: Map<String, String> = emptyMap(),
    val hasChanges: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

@HiltViewModel
class TakeAttendanceViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(TakeAttendanceUiState())
        private set

    private val currentAttendance = mutableStateMapOf<String, String>()

    fun loadAttendance(classId: String, date: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val studentsResponse = api.getStudents(classId)
                val recordsResponse = api.getAttendanceRecords(classId)

                if (studentsResponse.isSuccessful) {
                    val students = studentsResponse.body() ?: emptyList()
                    val records = recordsResponse.body() ?: emptyList()

                    val dateRecords = records.filter { it.date == date }
                    val initialMap = dateRecords.associate { it.studentId to it.status }

                    currentAttendance.clear()
                    currentAttendance.putAll(initialMap)

                    uiState = uiState.copy(
                        isLoading = false,
                        students = students,
                        attendanceMap = initialMap,
                        hasChanges = false
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Failed to load students")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun updateAttendance(studentId: String, status: String) {
        currentAttendance[studentId] = status
        uiState = uiState.copy(
            attendanceMap = currentAttendance.toMap(),
            hasChanges = true
        )
    }

    fun saveAttendance(classId: String, date: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null)
            try {
                val records = currentAttendance.map { (studentId, status) ->
                    AttendanceRecord(studentId, classId, date, status)
                }
                val response = api.saveAttendance(records)
                if (response.isSuccessful) {
                    uiState = uiState.copy(isSaving = false, hasChanges = false, saved = true)
                } else {
                    uiState = uiState.copy(isSaving = false, error = "Failed to save attendance")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, error = e.message)
            }
        }
    }
}