package com.emusaves.data.local.dao

import androidx.room.*
import com.emusaves.data.local.entity.SyncedFileEntity
import com.emusaves.data.local.entity.SyncFolderEntity
import com.emusaves.data.local.entity.SynologyConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncedFileDao {
    @Query("SELECT * FROM synced_files WHERE localUri = :uri LIMIT 1")
    suspend fun getByUri(uri: String): SyncedFileEntity?

    @Query("SELECT * FROM synced_files")
    fun getAll(): Flow<List<SyncedFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: SyncedFileEntity)

    @Update
    suspend fun update(file: SyncedFileEntity)

    @Delete
    suspend fun delete(file: SyncedFileEntity)

    @Query("DELETE FROM synced_files WHERE localUri = :uri")
    suspend fun deleteByUri(uri: String)
}

@Dao
interface SyncFolderDao {
    @Query("SELECT * FROM sync_folders")
    fun getAll(): Flow<List<SyncFolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: SyncFolderEntity)

    @Delete
    suspend fun delete(folder: SyncFolderEntity)

    @Query("DELETE FROM sync_folders WHERE uri = :uri")
    suspend fun deleteByUri(uri: String)
}

@Dao
interface SynologyConfigDao {
    @Query("SELECT * FROM synology_config WHERE id = 1")
    fun getConfig(): Flow<SynologyConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: SynologyConfigEntity)

    @Query("DELETE FROM synology_config")
    suspend fun delete()
}
