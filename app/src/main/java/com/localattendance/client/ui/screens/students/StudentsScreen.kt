package com.localattendance.client.ui.screens.students

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Switch
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
import com.localattendance.client.data.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    classId: String,
    viewModel: StudentsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }

    var newStudentName by remember { mutableStateOf("") }
    var newStudentRoll by remember { mutableStateOf("") }

    LaunchedEffect(classId) {
        viewModel.loadStudents(classId)
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
                title = { Text("Student Roster") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Student")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Student")
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

            uiState.students.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No students yet")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { showAddDialog = true }) {
                            Text("Add First Student")
                        }
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
                    items(uiState.students) { student ->
                        StudentCard(
                            student = student,
                            onEdit = { editingStudent = student },
                            onDelete = { viewModel.deleteStudent(classId, student.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isSaving) showAddDialog = false },
            title = { Text("Add Student") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newStudentName,
                        onValueChange = { newStudentName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newStudentRoll,
                        onValueChange = { newStudentRoll = it },
                        label = { Text("Roll Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !uiState.isSaving && newStudentName.isNotBlank() && newStudentRoll.isNotBlank(),
                    onClick = {
                        viewModel.addStudent(classId, newStudentName.trim(), newStudentRoll.trim()) {
                            newStudentName = ""
                            newStudentRoll = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text(if (uiState.isSaving) "Saving..." else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }, enabled = !uiState.isSaving) {
                    Text("Cancel")
                }
            }
        )
    }

    editingStudent?.let { student ->
        StudentEditorDialog(
            student = student,
            isSaving = uiState.isSaving,
            onDismiss = { editingStudent = null },
            onSave = { name, roll, parentName, parentPhone, isFlagged ->
                viewModel.updateStudent(
                    classId = classId,
                    studentId = student.id,
                    name = name,
                    rollNumber = roll,
                    parentName = parentName,
                    parentPhone = parentPhone,
                    isFlagged = isFlagged
                ) {
                    editingStudent = null
                }
            }
        )
    }
}

@Composable
private fun StudentCard(
    student: Student,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (student.isFlagged) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Flagged",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    "Roll: ${student.rollNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Student") },
            text = { Text("Are you sure you want to remove ${student.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StudentEditorDialog(
    student: Student,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, rollNumber: String, parentName: String, parentPhone: String, isFlagged: Boolean) -> Unit
) {
    var name by remember(student.id) { mutableStateOf(student.name) }
    var rollNumber by remember(student.id) { mutableStateOf(student.rollNumber) }
    var parentName by remember(student.id) { mutableStateOf(student.parentName ?: "") }
    var parentPhone by remember(student.id) { mutableStateOf(student.parentPhone ?: "") }
    var isFlagged by remember(student.id) { mutableStateOf(student.isFlagged) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Edit Student") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = { rollNumber = it },
                    label = { Text("Roll Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = parentPhone,
                    onValueChange = { parentPhone = it },
                    label = { Text("Parent Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Flag student")
                    Switch(checked = isFlagged, onCheckedChange = { isFlagged = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving && name.isNotBlank() && rollNumber.isNotBlank(),
                onClick = {
                    onSave(
                        name.trim(),
                        rollNumber.trim(),
                        parentName.trim(),
                        parentPhone.trim(),
                        isFlagged
                    )
                }
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
