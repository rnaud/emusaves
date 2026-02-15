package com.emusaves.domain.model

import android.net.Uri

data class SynologyConfig(
    val host: String,
    val username: String,
    val password: String,
    val remotePath: String
)

data class SyncFolder(
    val uri: Uri,
    val name: String,
    val lastSyncTimestamp: Long = 0
)

data class SyncedFile(
    val id: Long = 0,
    val localUri: String,
    val relativePath: String,
    val size: Long,
    val lastModified: Long,
    val hash: String? = null,
    val lastSynced: Long = 0
)

data class SyncStatus(
    val isSyncing: Boolean = false,
    val lastSyncTime: Long = 0,
    val filesBackedUp: Int = 0,
    val totalFiles: Int = 0,
    val lastError: String? = null
)
