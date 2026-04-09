package com.localattendance.client.ui.screens.reports

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
import com.localattendance.client.data.model.AttendanceRecord
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    classId: String,
    viewModel: ReportsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(classId, selectedMonth) {
        val startDate = "${selectedMonth.atDay(1)}"
        val endDate = "${selectedMonth.atEndOfMonth()}"
        viewModel.loadReport(classId, startDate, endDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Reports") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export */ }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Month selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
                    }
                    Text(
                        selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { selectedMonth = selectedMonth.plusMonths(1) },
                        enabled = selectedMonth < YearMonth.now()
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Summary stats
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "Attendance Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "Present",
                                value = "${uiState.stats.present}",
                                color = MaterialTheme.colorScheme.primaryContainer
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "Absent",
                                value = "${uiState.stats.absent}",
                                color = MaterialTheme.colorScheme.errorContainer
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "Late",
                                value = "${uiState.stats.late}",
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        val rate = if (uiState.totalRecords > 0) {
                            (uiState.stats.present * 100) / uiState.totalRecords
                        } else 0
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Attendance Rate",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "$rate%",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}