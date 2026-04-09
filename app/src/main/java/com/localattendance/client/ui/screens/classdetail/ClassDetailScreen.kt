package com.localattendance.client.ui.screens.classdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailScreen(
    classId: String,
    viewModel: ClassDetailViewModel = hiltViewModel(),
    onNavigateToAttendance: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToReports: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState

    LaunchedEffect(classId) {
        viewModel.loadClassDetails(classId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.className ?: "Class Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ClassActionCard(
                    icon = Icons.Default.Assignment,
                    title = "Take Attendance",
                    description = "Record daily attendance for today or past dates",
                    onClick = onNavigateToAttendance
                )
            }

            item {
                ClassActionCard(
                    icon = Icons.Default.People,
                    title = "Students",
                    description = "Manage class roster, add or remove students",
                    onClick = onNavigateToStudents
                )
            }

            item {
                ClassActionCard(
                    icon = Icons.Default.TableChart,
                    title = "Timetable",
                    description = "Weekly schedule and lesson planning",
                    onClick = onNavigateToTimetable
                )
            }

            item {
                ClassActionCard(
                    icon = Icons.Default.Event,
                    title = "Events",
                    description = "Classwork, tests, exams and calendar",
                    onClick = onNavigateToEvents
                )
            }

            item {
                ClassActionCard(
                    icon = Icons.Default.Assessment,
                    title = "Reports",
                    description = "Monthly attendance summaries and exports",
                    onClick = onNavigateToReports
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Class Info",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow("Role", uiState.role?.replaceFirstChar { it.uppercase() } ?: "Teacher")
                        InfoRow("Total Students", "${uiState.studentCount}")
                        uiState.ownerName?.let { InfoRow("Owner", it) }
                    }
                }
            }
        }
    }
}

@Composable
fun ClassActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}