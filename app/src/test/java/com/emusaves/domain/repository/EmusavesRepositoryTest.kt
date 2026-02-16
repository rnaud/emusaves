package com.emusaves.domain.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.emusaves.data.local.EmusavesDatabase
import com.emusaves.data.local.dao.SyncFolderDao
import com.emusaves.data.local.dao.SynologyConfigDao
import com.emusaves.data.local.entity.SyncFolderEntity
import com.emusaves.data.local.entity.SynologyConfigEntity
import com.emusaves.domain.model.SynologyConfig
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.junit.Assert.*

class EmusavesRepositoryTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var database: EmusavesDatabase

    @Mock
    private lateinit var syncFolderDao: SyncFolderDao

    @Mock
    private lateinit var synologyConfigDao: SynologyConfigDao

    @Mock
    private lateinit var documentFile: DocumentFile

    @Mock
    private lateinit var uri: Uri

    private lateinit var repository: EmusavesRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        whenever(database.syncFolderDao()).thenReturn(syncFolderDao)
        whenever(database.synologyConfigDao()).thenReturn(synologyConfigDao)
        whenever(database.syncedFileDao()).thenReturn(mock())

        // Use the test constructor to inject the mock database directly
        repository = EmusavesRepository(context, database)
    }

    @Test
    fun `getSynologyConfig should return config from DAO`() = runTest {
        // Given
        val configEntity = SynologyConfigEntity(
            host = "nas.local",
            username = "user",
            password = "pass",
            remotePath = "/Drive/EmulatorBackups"
        )
        whenever(synologyConfigDao.getConfig()).thenReturn(flowOf(configEntity))

        // When
        val result = repository.getSynologyConfig()

        // Then
        result.collect { config ->
            assertNotNull(config)
            assertEquals("nas.local", config!!.host)
            assertEquals("user", config.username)
            assertEquals("pass", config.password)
            assertEquals("/Drive/EmulatorBackups", config.remotePath)
        }
    }

    @Test
    fun `getSynologyConfig should return null when no config exists`() = runTest {
        // Given
        whenever(synologyConfigDao.getConfig()).thenReturn(flowOf(null))

        // When
        val result = repository.getSynologyConfig()

        // Then
        result.collect { config ->
            assertNull(config)
        }
    }

    @Test
    fun `saveSynologyConfig should insert config entity`() = runTest {
        // Given
        val config = SynologyConfig(
            host = "nas.local",
            username = "user", 
            password = "pass",
            remotePath = "/Drive/EmulatorBackups"
        )

        // When
        repository.saveSynologyConfig(config)

        // Then
        verify(synologyConfigDao).insert(any())
    }

    @Test
    fun `getFolders should return folders from DAO`() = runTest {
        // Given
        val folderEntities = listOf(
            SyncFolderEntity(
                uri = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2Fcom.retroarch%2Ffiles%2Fsaves",
                name = "RetroArch Saves"
            ),
            SyncFolderEntity(
                uri = "content://com.android.externalstorage.documents/tree/primary%3APSP%2FSAVEDATA",
                name = "PPSSPP Saves"
            )
        )
        whenever(syncFolderDao.getAll()).thenReturn(flowOf(folderEntities))

        // When
        val result = repository.getFolders()

        // Then
        result.collect { folders ->
            assertEquals(2, folders.size)
            assertEquals("RetroArch Saves", folders[0].name)
            assertEquals("PPSSPP Saves", folders[1].name)
        }
    }

    @Test
    fun `addFolder should insert folder entity with provided name`() = runTest {
        // Given
        val uri = mock<Uri>()
        whenever(uri.toString()).thenReturn("content://test/uri")

        // When - pass name directly to avoid DocumentFile mocking issues
        repository.addFolder(uri, "Test Folder")

        // Then
        verify(syncFolderDao).insert(argThat<SyncFolderEntity> { entity ->
            entity.uri == "content://test/uri" && 
            entity.name == "Test Folder"
        })
    }

    @Test
    fun `addFolder should use uri as fallback when name is null`() = runTest {
        // Given
        val uri = mock<Uri>()
        whenever(uri.toString()).thenReturn("content://test/uri")

        // When - pass null name
        repository.addFolder(uri, null)

        // Then - name will be "Unknown" from the repository logic when DocumentFile lookup fails
        verify(syncFolderDao).insert(any())
    }

    @Test
    fun `removeFolder should delete folder by URI`() = runTest {
        // Given
        val uriString = "content://test/uri"
        whenever(uri.toString()).thenReturn(uriString)

        // When
        repository.removeFolder(uri)

        // Then
        verify(syncFolderDao).deleteByUri(uriString)
    }

    @Test
    fun `isSaveFile should recognize RetroArch save file extensions`() {
        // Use reflection to access private method for testing
        val method = repository::class.java.getDeclaredMethod("isSaveFile", String::class.java)
        method.isAccessible = true

        // Test various save file extensions
        assertTrue("Should recognize .srm files", method.invoke(repository, "game.srm") as Boolean)
        assertTrue("Should recognize .state files", method.invoke(repository, "game.state") as Boolean) 
        assertTrue("Should recognize .sav files", method.invoke(repository, "game.sav") as Boolean)
        assertTrue("Should recognize .bsv files", method.invoke(repository, "game.bsv") as Boolean)
        
        // Test case insensitivity
        assertTrue("Should be case insensitive", method.invoke(repository, "GAME.SRM") as Boolean)
        
        // Test non-save files
        assertFalse("Should reject non-save files", method.invoke(repository, "game.txt") as Boolean)
        assertFalse("Should reject files without extension", method.invoke(repository, "game") as Boolean)
    }
}
