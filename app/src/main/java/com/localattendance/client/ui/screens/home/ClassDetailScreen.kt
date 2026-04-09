package com.localattendance.client.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.AttendanceRecord
import com.localattendance.client.data.model.Student

@HiltViewModel
class ClassDetailViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {
    var students by mutableStateOf<List<Student>>(emptyList())
        private set

    private val _attendanceMap = mutableStateMapOf<String, String>()
    val attendanceMap: Map<String, String> get() = _attendanceMap

    fun fetchStudents(classId: String) {
        viewModelScope.launch {
            try {
                students = api.getStudents(classId)
            } catch (e: Exception) { }
        }
    }

    fun updateAttendance(studentId: String, status: String) {
        _attendanceMap[studentId] = status
    }

    fun saveAttendance(classId: String, date: String) {
        viewModelScope.launch {
            val records = _attendanceMap.map { (studentId, status) ->
                AttendanceRecord(studentId, classId, date, status)
            }
            try {
                api.saveAttendance(records)
            } catch (e: Exception) { }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailScreen(
    classId: String,
    viewModel: ClassDetailViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(classId) {
        viewModel.fetchStudents(classId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mark Attendance") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Use current date for simplicity
                viewModel.saveAttendance(classId, "2026-04-09")
            }) {
                Text("Save")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.students) { student ->
                AttendanceItem(
                    student = student,
                    status = viewModel.attendanceMap[student.id] ?: "Present",
                    onStatusChange = { viewModel.updateAttendance(student.id, it) }
                )
            }
        }
    }
}

@Composable
fun AttendanceItem(student: Student, status: String, onStatusChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = student.name, modifier = Modifier.weight(1f))

        Row {
            listOf("Present", "Absent", "Late", "Sick").forEach { s ->
                FilterChip(
                    selected = status == s,
                    onClick = { onStatusChange(s) },
                    label = { Text(s) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
