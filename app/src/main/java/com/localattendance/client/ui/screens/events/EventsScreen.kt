package com.localattendance.client.ui.screens.events

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
import androidx.compose.material3.Surface
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
import com.localattendance.client.data.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    classId: String,
    viewModel: EventsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(classId) {
        viewModel.loadEvents(classId)
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
                title = { Text("Events & Calendar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
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

            uiState.events.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No events yet")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Add classwork, tests, or exams", style = MaterialTheme.typography.bodySmall)
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
                    items(uiState.events.sortedBy { it.date }) { event ->
                        EventCard(
                            event = event,
                            onEdit = { editingEvent = it },
                            onDelete = { viewModel.deleteEvent(classId, it.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        EventEditorDialog(
            title = "Add Event",
            isSaving = uiState.isSaving,
            initialEvent = null,
            onDismiss = { showAddDialog = false },
            onSave = { date, title, type, description ->
                val event = Event(
                    id = "",
                    date = date,
                    title = title,
                    type = type,
                    description = description.ifBlank { null }
                )
                viewModel.addEvent(classId, event) {
                    showAddDialog = false
                }
            }
        )
    }

    editingEvent?.let { event ->
        EventEditorDialog(
            title = "Edit Event",
            isSaving = uiState.isSaving,
            initialEvent = event,
            onDismiss = { editingEvent = null },
            onSave = { date, eventTitle, type, description ->
                val request = mapOf(
                    "date" to date,
                    "title" to eventTitle,
                    "type" to type,
                    "description" to description
                )
                viewModel.updateEvent(classId, event.id, request) {
                    editingEvent = null
                }
            }
        )
    }
}

@Composable
private fun EventCard(
    event: Event,
    onEdit: (Event) -> Unit,
    onDelete: (Event) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    EventTypeChip(type = event.type)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    event.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                event.description?.let { desc ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(desc, style = MaterialTheme.typography.bodySmall)
                }
            }

            Row {
                IconButton(onClick = { onEdit(event) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Event")
                }
                IconButton(onClick = { onDelete(event) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Event")
                }
            }
        }
    }
}

@Composable
private fun EventEditorDialog(
    title: String,
    isSaving: Boolean,
    initialEvent: Event?,
    onDismiss: () -> Unit,
    onSave: (date: String, title: String, type: String, description: String) -> Unit
) {
    var date by remember(initialEvent) { mutableStateOf(initialEvent?.date ?: "") }
    var eventTitle by remember(initialEvent) { mutableStateOf(initialEvent?.title ?: "") }
    var type by remember(initialEvent) { mutableStateOf(initialEvent?.type ?: "classwork") }
    var description by remember(initialEvent) { mutableStateOf(initialEvent?.description ?: "") }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = eventTitle,
                    onValueChange = { eventTitle = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type (classwork/test/exam/homework)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(date.trim(), eventTitle.trim(), type.trim(), description.trim()) },
                enabled = !isSaving && date.isNotBlank() && eventTitle.isNotBlank() && type.isNotBlank()
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

@Composable
private fun EventTypeChip(type: String) {
    val color = when (type.lowercase()) {
        "test" -> MaterialTheme.colorScheme.error
        "exam" -> MaterialTheme.colorScheme.errorContainer
        "classwork" -> MaterialTheme.colorScheme.primaryContainer
        "homework" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            type.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
