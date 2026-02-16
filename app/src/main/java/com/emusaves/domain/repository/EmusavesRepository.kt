package com.emusaves.domain.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.emusaves.data.local.EmusavesDatabase
import com.emusaves.data.local.entity.SyncedFileEntity
import com.emusaves.data.local.entity.SyncFolderEntity
import com.emusaves.data.local.entity.SynologyConfigEntity
import com.emusaves.data.remote.api.SynologyApiClient
import com.emusaves.domain.model.SyncedFile
import com.emusaves.domain.model.SynologyConfig
import com.emusaves.domain.model.SyncFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class EmusavesRepository(private val context: Context) {
    private val db = EmusavesDatabase.getDatabase(context)
    private val syncedFileDao = db.syncedFileDao()
    private val syncFolderDao = db.syncFolderDao()
    private val synologyConfigDao = db.synologyConfigDao()
    
    private var apiClient: SynologyApiClient? = null

    // Config
    fun getSynologyConfig(): Flow<SynologyConfig?> = 
        synologyConfigDao.getConfig().map { it?.toDomain() }

    suspend fun saveSynologyConfig(config: SynologyConfig) {
        synologyConfigDao.insert(config.toEntity())
        apiClient = SynologyApiClient(
            host = config.host,
            username = config.username,
            password = config.password
        )
    }

    // Folders
    fun getFolders(): Flow<List<SyncFolder>> = 
        syncFolderDao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun addFolder(uri: Uri, name: String? = null) {
        val documentFile = DocumentFile.fromTreeUri(context, uri)
        syncFolderDao.insert(SyncFolderEntity(
            uri = uri.toString(),
            name = name ?: documentFile?.name ?: "Unknown"
        ))
    }

    suspend fun removeFolder(uri: Uri) {
        syncFolderDao.deleteByUri(uri.toString())
    }

    // Synced Files
    fun getSyncedFiles(): Flow<List<SyncedFile>> = 
        syncedFileDao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun getLocalFiles(folderUri: Uri): List<SyncedFile> = withContext(Dispatchers.IO) {
        val files = mutableListOf<SyncedFile>()
        val documentFile = DocumentFile.fromTreeUri(context, folderUri) ?: return@withContext files
        
        scanDirectory(documentFile, files, "")
        files
    }

    private fun scanDirectory(dir: DocumentFile, files: MutableList<SyncedFile>, relativePath: String) {
        dir.listFiles().forEach { file ->
            if (file.isDirectory) {
                val newPath = if (relativePath.isEmpty()) file.name ?: "" 
                else "$relativePath/${file.name}"
                scanDirectory(file, files, newPath)
            } else if (file.name?.let { isSaveFile(it) } == true) {
                val path = if (relativePath.isEmpty()) file.name ?: "" 
                else "$relativePath/${file.name}"
                files.add(SyncedFile(
                    localUri = file.uri.toString(),
                    relativePath = path,
                    size = file.length(),
                    lastModified = file.lastModified()
                ))
            }
        }
    }

    private fun isSaveFile(name: String): Boolean {
        val extensions = listOf(
            // RetroArch
            "srm", "state", "sav", "bsv",
            // Dolphin
            // PPSSPP
            // Cemu
            // Yuzu/Ryujinx
            // DOSBox
        )
        return extensions.any { name.lowercase().endsWith(".$it") }
    }

    suspend fun syncFolder(folder: SyncFolder): Result<Int> = withContext(Dispatchers.IO) {
        val client = apiClient ?: return@withContext Result.failure(Exception("Not configured"))
        
        // Login first
        val loginResult = client.login()
        if (loginResult.isFailure) {
            return@withContext Result.failure(loginResult.exceptionOrNull() ?: Exception("Login failed"))
        }

        try {
            var uploadedCount = 0
            
            // Get local files
            val localFiles = getLocalFiles(folder.uri)
            
            // Ensure remote folder exists
            client.createFolder("/Drive", "EmulatorBackups")
            
            // Upload each file
            for (file in localFiles) {
                val result = uploadFile(client, folder, file)
                if (result.isSuccess) {
                    syncedFileDao.insert(file.copy(lastSynced = System.currentTimeMillis()).toEntity())
                    uploadedCount++
                }
            }
            
            Result.success(uploadedCount)
        } finally {
            client.logout()
        }
    }

    private suspend fun uploadFile(client: SynologyApiClient, folder: SyncFolder, file: SyncedFile): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val content = context.contentResolver.openInputStream(Uri.parse(file.localUri))?.use { input ->
                    val output = ByteArrayOutputStream()
                    input.copyTo(output)
                    output.toByteArray()
                } ?: return@withContext Result.failure(Exception("Cannot read file"))

                // Create versioned backup path
                val versionedPath = "${folder.name}/${file.relativePath}"
                client.uploadFile(content, "/Drive/EmulatorBackups", versionedPath)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Mappers
    private fun SynologyConfigEntity.toDomain() = SynologyConfig(
        host = host,
        username = username,
        password = password,
        remotePath = remotePath
    )

    private fun SynologyConfig.toEntity() = SynologyConfigEntity(
        host = host,
        username = username,
        password = password,
        remotePath = remotePath
    )

    private fun SyncFolderEntity.toDomain() = SyncFolder(
        uri = Uri.parse(uri),
        name = name,
        lastSyncTimestamp = lastSyncTimestamp
    )

    private fun SyncedFileEntity.toDomain() = SyncedFile(
        id = id,
        localUri = localUri,
        relativePath = relativePath,
        size = size,
        lastModified = lastModified,
        hash = hash,
        lastSynced = lastSynced
    )

    private fun SyncedFile.toEntity() = SyncedFileEntity(
        id = id,
        localUri = localUri,
        relativePath = relativePath,
        size = size,
        lastModified = lastModified,
        hash = hash,
        lastSynced = lastSynced
    )
}
