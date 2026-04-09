package com.localattendance.client.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ServerSettingsScreen(
    viewModel: ServerSettingsViewModel = hiltViewModel(),
    onServerConfigured: () -> Unit
) {
    var serverUrl by remember { mutableStateOf("") }
    val isValidUrl = serverUrl.startsWith("http://") || serverUrl.startsWith("https://")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.CloudQueue,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Connect to Server",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter the IP address of your Local Attendance server to connect",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = serverUrl,
            onValueChange = { serverUrl = it },
            label = { Text("Server URL") },
            placeholder = { Text("http://192.168.1.5:3000") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = serverUrl.isNotBlank() && !isValidUrl
        )

        if (serverUrl.isNotBlank() && !isValidUrl) {
            Text(
                text = "URL must start with http:// or https://",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.saveServerUrl(serverUrl)
                onServerConfigured()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = serverUrl.isNotBlank() && isValidUrl
        ) {
            Text("Connect")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Setup Instructions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "1. Start your Local Attendance server\n2. Find your computer's IP address\n3. Enter http://YOUR_IP:3000 above",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}