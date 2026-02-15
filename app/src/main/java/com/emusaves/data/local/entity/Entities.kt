package com.emusaves.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synced_files")
data class SyncedFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val localUri: String,
    val relativePath: String,
    val size: Long,
    val lastModified: Long,
    val hash: String?,
    val lastSynced: Long
)

@Entity(tableName = "sync_folders")
data class SyncFolderEntity(
    @PrimaryKey
    val uri: String,
    val name: String,
    val lastSyncTimestamp: Long = 0
)

@Entity(tableName = "synology_config")
data class SynologyConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val host: String,
    val username: String,
    val password: String,
    val remotePath: String
)
