package com.localattendance.client.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.localattendance.client.data.api.AttendanceApi
import com.localattendance.client.data.model.ClassRoom

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: AttendanceApi
) : ViewModel() {
    var classes by mutableStateOf<List<ClassRoom>>(emptyList())
        private set

    fun fetchClasses() {
        viewModelScope.launch {
            try {
                classes = api.getClasses()
            } catch (e: Exception) {
                // Handle error (e.g., show toast or snackbar)
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onClassSelected: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.fetchClasses()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("My Classes") })
        }
    ) { padding ->
        if (viewModel.classes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No classes found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.classes) { classRoom ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClassSelected(classRoom.id) }
                    ) {
                        Text(
                            text = classRoom.name,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}
