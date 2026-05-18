package com.localattendance.client.ui.screens.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localattendance.client.data.model.TimetableSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    classId: String,
    viewModel: TimetableViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingSlot by remember { mutableStateOf<TimetableSlot?>(null) }

    LaunchedEffect(classId) {
        viewModel.loadTimetable(classId)
    }

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timetable") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Slot")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.slots.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No timetable yet")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Add your weekly schedule", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                    days.forEach { day ->
                        val daySlots = uiState.slots
                            .filter { getDayName(it.dayOfWeek) == day }
                            .sortedBy { it.startTime }

                        if (daySlots.isNotEmpty()) {
                            item {
                                Text(
                                    day,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(daySlots) { slot ->
                                TimetableSlotCard(
                                    slot = slot,
                                    onEdit = { editingSlot = it },
                                    onDelete = { viewModel.deleteTimetableSlot(classId, it.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TimetableEditorDialog(
            title = "Add Timetable Slot",
            isSaving = uiState.isSaving,
            initialSlot = null,
            onDismiss = { showAddDialog = false },
            onSave = { dayOfWeek, startTime, endTime, subject, lesson ->
                val slot = TimetableSlot(
                    id = "",
                    dayOfWeek = dayOfWeek,
                    startTime = startTime,
                    endTime = endTime,
                    subject = subject,
                    lesson = lesson
                )
                viewModel.addTimetableSlot(classId, slot) {
                    showAddDialog = false
                }
            }
        )
    }

    editingSlot?.let { slot ->
        TimetableEditorDialog(
            title = "Edit Timetable Slot",
            isSaving = uiState.isSaving,
            initialSlot = slot,
            onDismiss = { editingSlot = null },
            onSave = { dayOfWeek, startTime, endTime, subject, lesson ->
                val request: Map<String, Any> = mapOf(
                    "dayOfWeek" to dayOfWeek,
                    "startTime" to startTime,
                    "endTime" to endTime,
                    "subject" to subject,
                    "lesson" to lesson
                )
                viewModel.updateTimetableSlot(classId, slot.id, request) {
                    editingSlot = null
                }
            }
        )
    }
}

@Composable
private fun TimetableSlotCard(
    slot: TimetableSlot,
    onEdit: (TimetableSlot) -> Unit,
    onDelete: (TimetableSlot) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    slot.subject,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Lesson ${slot.lesson}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${slot.startTime} - ${slot.endTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row {
                IconButton(onClick = { onEdit(slot) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Slot")
                }
                IconButton(onClick = { onDelete(slot) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Slot")
                }
            }
        }
    }
}

@Composable
private fun TimetableEditorDialog(
    title: String,
    isSaving: Boolean,
    initialSlot: TimetableSlot?,
    onDismiss: () -> Unit,
    onSave: (dayOfWeek: Int, startTime: String, endTime: String, subject: String, lesson: String) -> Unit
) {
    var dayOfWeekInput by remember(initialSlot) { mutableStateOf((initialSlot?.dayOfWeek ?: 1).toString()) }
    var startTime by remember(initialSlot) { mutableStateOf(initialSlot?.startTime ?: "") }
    var endTime by remember(initialSlot) { mutableStateOf(initialSlot?.endTime ?: "") }
    var subject by remember(initialSlot) { mutableStateOf(initialSlot?.subject ?: "") }
    var lesson by remember(initialSlot) { mutableStateOf(initialSlot?.lesson ?: "") }

    val parsedDay = dayOfWeekInput.toIntOrNull()
    val dayValid = parsedDay != null && parsedDay in 0..7

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dayOfWeekInput,
                    onValueChange = { dayOfWeekInput = it },
                    label = { Text("Day Of Week (0..7)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = lesson,
                    onValueChange = { lesson = it },
                    label = { Text("Lesson") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        parsedDay ?: 1,
                        startTime.trim(),
                        endTime.trim(),
                        subject.trim(),
                        lesson.trim()
                    )
                },
                enabled = !isSaving && dayValid && startTime.isNotBlank() && endTime.isNotBlank() && subject.isNotBlank() && lesson.isNotBlank()
            ) {
                Text(if (isSaving) "Saving..." else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancel")
            }
        }
    )
}

fun getDayName(dayOfWeek: Int): String = when (dayOfWeek) {
    0, 1 -> "Monday"
    2 -> "Tuesday"
    3 -> "Wednesday"
    4 -> "Thursday"
    5 -> "Friday"
    6 -> "Saturday"
    7 -> "Sunday"
    else -> "Unknown"
}
