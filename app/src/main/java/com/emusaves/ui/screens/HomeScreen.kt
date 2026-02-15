package com.emusaves.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = androidx.lifecycle.viewModel.compose.collectAsState().value
) {
    val context = LocalContext.current
    var selectedFolderUri by remember { mutableStateOf<Uri?>(null) }
    var synologyHost by remember { mutableStateOf("") }
    var synologyUser by remember { mutableStateOf("") }
    var synologyPassword by remember { mutableStateOf("") }
    var remotePath by remember { mutableStateOf("/Drive/EmulatorBackups") }

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            // Take persistable permission
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or 
                          Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            selectedFolderUri = uri
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EmuSaves") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Sync Status",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedFolderUri != null) "Folder selected" else "No folder selected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedFolderUri != null) 
                            MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.error
                    )
                }
            }

            // Folder Selection
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Save Files Location",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select the folder containing your emulator saves",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { folderPicker.launch(null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedFolderUri != null) "Change Folder" else "Select Folder")
                    }
                    if (selectedFolderUri != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = DocumentFile.fromTreeUri(context, selectedFolderUri!!)?.name ?: "Unknown",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Synology Settings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Synology NAS",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = synologyHost,
                        onValueChange = { synologyHost = it },
                        label = { Text("NAS Address (e.g., nas.local)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = synologyUser,
                        onValueChange = { synologyUser = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = synologyPassword,
                        onValueChange = { synologyPassword = it },
                        label = { Text("App Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = remotePath,
                        onValueChange = { remotePath = it },
                        label = { Text("Remote Path") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Sync Now Button
            Button(
                onClick = { /* Trigger sync */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedFolderUri != null && synologyHost.isNotBlank() 
                        && synologyUser.isNotBlank() && synologyPassword.isNotBlank()
            ) {
                Text("Sync Now")
            }
        }
    }
}
