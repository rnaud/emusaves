package com.emusaves.ui.screens

import com.emusaves.domain.model.SynologyConfig
import com.emusaves.domain.model.EmulatorLocation
import com.emusaves.domain.model.EmulatorLocations
import com.emusaves.domain.model.EmulatorCategory
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(context))
    val uiState by viewModel.uiState.collectAsState()

    var selectedFolderUri by remember { mutableStateOf<Uri?>(null) }
    var synologyHost by remember { mutableStateOf("") }
    var synologyUser by remember { mutableStateOf("") }
    var synologyPassword by remember { mutableStateOf("") }
    var remotePath by remember { mutableStateOf("/Drive/EmulatorBackups") }
    var showSynologyDialog by remember { mutableStateOf(false) }
    var showQuickAddDialog by remember { mutableStateOf(false) }
    var scheduledSyncEnabled by remember { mutableStateOf(false) }

    // Load config into fields when available
    LaunchedEffect(uiState.config) {
        uiState.config?.let { config ->
            synologyHost = config.host
            synologyUser = config.username
            synologyPassword = config.password
            remotePath = config.remotePath
        }
    }

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            selectedFolderUri = uri
            viewModel.addFolder(uri)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sync Status Card
            item {
                StatusCard(
                    syncStatus = uiState.syncStatus,
                    onSyncNow = { viewModel.syncNow() }
                )
            }

            // Folders Card
            item {
                FoldersCard(
                    folders = uiState.folders,
                    onSelectFolder = { folderPicker.launch(null) },
                    onQuickAdd = { showQuickAddDialog = true },
                    onRemoveFolder = { viewModel.removeFolder(it) }
                )
            }

            // Synology Card
            item {
                SynologyCard(
                    config = uiState.config,
                    onConfigure = { showSynologyDialog = true }
                )
            }

            // Scheduled Sync Card
            item {
                ScheduledSyncCard(
                    enabled = scheduledSyncEnabled,
                    onToggle = { enabled ->
                        scheduledSyncEnabled = enabled
                        viewModel.enableScheduledSync(enabled)
                    }
                )
            }
        }
    }

    // Synology Config Dialog
    if (showSynologyDialog) {
        AlertDialog(
            onDismissRequest = { showSynologyDialog = false },
            title = { Text("Synology NAS Configuration") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = synologyHost,
                        onValueChange = { synologyHost = it },
                        label = { Text("NAS Address (e.g., nas.local or QuickConnect username)") },
                        placeholder = { Text("nas.local or username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = synologyUser,
                        onValueChange = { synologyUser = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = synologyPassword,
                        onValueChange = { synologyPassword = it },
                        label = { Text("App Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = remotePath,
                        onValueChange = { remotePath = it },
                        label = { Text("Remote Path") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.saveConfig(
                            SynologyConfig(
                                host = synologyHost,
                                username = synologyUser,
                                password = synologyPassword,
                                remotePath = remotePath
                            )
                        )
                        showSynologyDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSynologyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Quick Add Dialog
    if (showQuickAddDialog) {
        QuickAddDialog(
            onDismiss = { showQuickAddDialog = false },
            onLocationSelected = { location ->
                // Try to parse the URI and add the folder
                try {
                    val uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3A${location.path.removePrefix("/storage/emulated/0/").replace("/", "%2F")}")
                    viewModel.addFolder(uri, location.name)
                    showQuickAddDialog = false
                } catch (e: Exception) {
                    // Fallback: launch folder picker at the location
                    showQuickAddDialog = false
                    folderPicker.launch(null)
                }
            }
        )
    }
}

@Composable
private fun StatusCard(
    syncStatus: com.emusaves.domain.model.SyncStatus,
    onSyncNow: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sync Status", style = MaterialTheme.typography.titleMedium)
                if (syncStatus.isSyncing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (syncStatus.lastSyncTime > 0) {
                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                Text(
                    "Last sync: ${dateFormat.format(Date(syncStatus.lastSyncTime))}",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    "Never synced",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            syncStatus.lastError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Error: $error",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSyncNow,
                modifier = Modifier.fillMaxWidth(),
                enabled = !syncStatus.isSyncing
            ) {
                if (syncStatus.isSyncing) {
                    Text("Syncing...")
                } else {
                    Text("Sync Now")
                }
            }
        }
    }
}

@Composable
private fun FoldersCard(
    folders: List<com.emusaves.domain.model.SyncFolder>,
    onSelectFolder: () -> Unit,
    onQuickAdd: () -> Unit,
    onRemoveFolder: (Uri) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Backup Folders", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            if (folders.isEmpty()) {
                Text(
                    "No folders selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                folders.forEach { folder ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(folder.name, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { onRemoveFolder(folder.uri) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onQuickAdd,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quick Add")
                }
                OutlinedButton(
                    onClick = onSelectFolder,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Browse")
                }
            }
        }
    }
}

@Composable
private fun SynologyCard(
    config: com.emusaves.domain.model.SynologyConfig?,
    onConfigure: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Synology NAS", style = MaterialTheme.typography.titleMedium)
                if (config != null) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Configured",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (config != null) {
                Text(
                    "${config.host} → ${config.remotePath}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "Not configured",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onConfigure,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (config != null) "Update Configuration" else "Configure")
            }
        }
    }
}

@Composable
private fun ScheduledSyncCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Scheduled Sync", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Every 6 hours on Wi-Fi + charging",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = enabled, onCheckedChange = onToggle)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickAddDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (EmulatorLocation) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(EmulatorCategory.MULTI_SYSTEM) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Quick Add Emulator Folders",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Select common emulator save locations:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Category tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(EmulatorCategory.values()) { category ->
                        FilterChip(
                            onClick = { selectedCategory = category },
                            label = { 
                                Text(
                                    "${category.icon} ${category.displayName}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            selected = selectedCategory == category
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Emulator locations for selected category
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(EmulatorLocations.getByCategory(selectedCategory)) { location ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLocationSelected(location) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = location.icon,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.width(32.dp)
                                )
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = location.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = location.emulator,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = location.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
