package com.localattendance.client.ui.screens.reports

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.AttendanceRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AttendanceStats(
    val present: Int = 0,
    val absent: Int = 0,
    val late: Int = 0,
    val sick: Int = 0
)

data class ReportsUiState(
    val isLoading: Boolean = true,
    val records: List<AttendanceRecord> = emptyList(),
    val stats: AttendanceStats = AttendanceStats(),
    val totalRecords: Int = 0,
    val error: String? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {

    var uiState by mutableStateOf(ReportsUiState())
        private set

    fun loadReport(classId: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = api.getAttendanceRecords(classId)
                if (response.isSuccessful) {
                    val allRecords = response.body() ?: emptyList()
                    val monthRecords = allRecords.filter { 
                        it.date >= startDate && it.date <= endDate 
                    }

                    val stats = AttendanceStats(
                        present = monthRecords.count { it.status == "Present" },
                        absent = monthRecords.count { it.status == "Absent" },
                        late = monthRecords.count { it.status == "Late" },
                        sick = monthRecords.count { it.status == "Sick" }
                    )

                    uiState = uiState.copy(
                        isLoading = false,
                        records = monthRecords,
                        stats = stats,
                        totalRecords = monthRecords.size
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Failed to load records")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }
}