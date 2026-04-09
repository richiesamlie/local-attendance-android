package com.localattendance.client.ui.screens.dashboard

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

data class DashboardUiState(
    val isLoading: Boolean = true,
    val teacher: Teacher? = null,
    val classes: List<ClassRoom> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val teacherResponse = api.getMe()
                val classesResponse = api.getClasses()

                if (teacherResponse.isSuccessful && classesResponse.isSuccessful) {
                    uiState = uiState.copy(
                        isLoading = false,
                        teacher = teacherResponse.body(),
                        classes = classesResponse.body() ?: emptyList()
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = teacherResponse.errorBody()?.string() ?: classesResponse.errorBody()?.string()
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }
}