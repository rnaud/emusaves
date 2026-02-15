package com.emusaves.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emusaves.data.local.dao.SyncedFileDao
import com.emusaves.data.local.dao.SynologyConfigDao
import com.emusaves.data.local.dao.SyncFolderDao
import com.emusaves.data.local.entity.SyncedFileEntity
import com.emusaves.data.local.entity.SynologyConfigEntity
import com.emusaves.data.local.entity.SyncFolderEntity

@Database(
    entities = [
        SyncedFileEntity::class,
        SyncFolderEntity::class,
        SynologyConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EmusavesDatabase : RoomDatabase() {
    abstract fun syncedFileDao(): SyncedFileDao
    abstract fun syncFolderDao(): SyncFolderDao
    abstract fun synologyConfigDao(): SynologyConfigDao

    companion object {
        @Volatile
        private var INSTANCE: EmusavesDatabase? = null

        fun getDatabase(context: Context): EmusavesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EmusavesDatabase::class.java,
                    "emusaves_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
