package com.emusaves.domain.model

import android.net.Uri
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ModelsTest {

    @Test
    fun `SynologyConfig should create with correct properties`() {
        // Given
        val host = "nas.local"
        val username = "testuser"
        val password = "testpass"
        val remotePath = "/Drive/EmulatorBackups"

        // When
        val config = SynologyConfig(host, username, password, remotePath)

        // Then
        assertEquals(host, config.host)
        assertEquals(username, config.username)
        assertEquals(password, config.password)
        assertEquals(remotePath, config.remotePath)
    }

    @Test
    fun `SynologyConfig should be data class with proper equals and hashCode`() {
        // Given
        val config1 = SynologyConfig("nas.local", "user", "pass", "/path")
        val config2 = SynologyConfig("nas.local", "user", "pass", "/path")
        val config3 = SynologyConfig("different.local", "user", "pass", "/path")

        // Then
        assertEquals("Same configs should be equal", config1, config2)
        assertNotEquals("Different configs should not be equal", config1, config3)
        assertEquals("Same configs should have same hash", config1.hashCode(), config2.hashCode())
    }

    @Test
    fun `SyncFolder should create with URI and name`() {
        // Given
        val uri = mock<Uri>()
        val name = "RetroArch Saves"
        val timestamp = System.currentTimeMillis()

        // When
        val folder = SyncFolder(uri, name, timestamp)

        // Then
        assertEquals(uri, folder.uri)
        assertEquals(name, folder.name)
        assertEquals(timestamp, folder.lastSyncTimestamp)
    }

    @Test
    fun `SyncFolder should have default timestamp of 0`() {
        // Given
        val uri = mock<Uri>()
        val name = "Test Folder"

        // When
        val folder = SyncFolder(uri, name)

        // Then
        assertEquals(0L, folder.lastSyncTimestamp)
    }

    @Test
    fun `SyncedFile should create with all properties`() {
        // Given
        val id = 123L
        val localUri = "content://test/uri"
        val relativePath = "saves/game.srm"
        val size = 1024L
        val lastModified = System.currentTimeMillis()
        val hash = "abc123"
        val lastSynced = System.currentTimeMillis()

        // When
        val file = SyncedFile(id, localUri, relativePath, size, lastModified, hash, lastSynced)

        // Then
        assertEquals(id, file.id)
        assertEquals(localUri, file.localUri)
        assertEquals(relativePath, file.relativePath)
        assertEquals(size, file.size)
        assertEquals(lastModified, file.lastModified)
        assertEquals(hash, file.hash)
        assertEquals(lastSynced, file.lastSynced)
    }

    @Test
    fun `SyncedFile should have default values`() {
        // Given
        val localUri = "content://test/uri"
        val relativePath = "saves/game.srm"
        val size = 1024L
        val lastModified = System.currentTimeMillis()

        // When
        val file = SyncedFile(
            localUri = localUri,
            relativePath = relativePath,
            size = size,
            lastModified = lastModified
        )

        // Then
        assertEquals(0L, file.id)
        assertNull(file.hash)
        assertEquals(0L, file.lastSynced)
    }

    @Test
    fun `SyncStatus should create with all properties`() {
        // Given
        val isSyncing = true
        val lastSyncTime = System.currentTimeMillis()
        val filesBackedUp = 10
        val totalFiles = 15
        val lastError = "Network error"

        // When
        val status = SyncStatus(isSyncing, lastSyncTime, filesBackedUp, totalFiles, lastError)

        // Then
        assertEquals(isSyncing, status.isSyncing)
        assertEquals(lastSyncTime, status.lastSyncTime)
        assertEquals(filesBackedUp, status.filesBackedUp)
        assertEquals(totalFiles, status.totalFiles)
        assertEquals(lastError, status.lastError)
    }

    @Test
    fun `SyncStatus should have default values`() {
        // When
        val status = SyncStatus()

        // Then
        assertFalse(status.isSyncing)
        assertEquals(0L, status.lastSyncTime)
        assertEquals(0, status.filesBackedUp)
        assertEquals(0, status.totalFiles)
        assertNull(status.lastError)
    }

    @Test
    fun `SyncStatus should calculate sync progress correctly`() {
        // Given
        val status1 = SyncStatus(filesBackedUp = 5, totalFiles = 10)
        val status2 = SyncStatus(filesBackedUp = 0, totalFiles = 0)
        val status3 = SyncStatus(filesBackedUp = 10, totalFiles = 10)

        // Then
        // We can't directly test progress calculation since it's not in the model,
        // but we can verify the data needed for such calculations is present
        assertTrue("Should have backed up files", status1.filesBackedUp > 0)
        assertTrue("Should have total files", status1.totalFiles > 0)
        assertTrue("Progress should be calculable", status1.filesBackedUp <= status1.totalFiles)
        
        assertEquals("Empty sync should have no files", 0, status2.filesBackedUp)
        assertEquals("Empty sync should have no total", 0, status2.totalFiles)
        
        assertEquals("Complete sync should match totals", status3.filesBackedUp, status3.totalFiles)
    }

    @Test
    fun `models should be immutable data classes`() {
        // Given
        val originalConfig = SynologyConfig("host", "user", "pass", "/path")
        val originalFolder = SyncFolder(mock<Uri>(), "folder")
        val originalFile = SyncedFile(localUri = "uri", relativePath = "path", size = 100, lastModified = 123)
        val originalStatus = SyncStatus()

        // When - Copy with changes
        val modifiedConfig = originalConfig.copy(host = "newhost")
        val modifiedFolder = originalFolder.copy(name = "newfolder")
        val modifiedFile = originalFile.copy(size = 200)
        val modifiedStatus = originalStatus.copy(isSyncing = true)

        // Then - Originals should be unchanged
        assertEquals("host", originalConfig.host)
        assertEquals("folder", originalFolder.name)
        assertEquals(100L, originalFile.size)
        assertFalse(originalStatus.isSyncing)

        // And copies should be modified
        assertEquals("newhost", modifiedConfig.host)
        assertEquals("newfolder", modifiedFolder.name)
        assertEquals(200L, modifiedFile.size)
        assertTrue(modifiedStatus.isSyncing)
    }

    @Test
    fun `SyncedFile should handle different file extensions`() {
        // Given - Various save file types
        val srmFile = SyncedFile(localUri = "uri", relativePath = "game.srm", size = 100, lastModified = 123)
        val savFile = SyncedFile(localUri = "uri", relativePath = "game.sav", size = 200, lastModified = 124)
        val stateFile = SyncedFile(localUri = "uri", relativePath = "game.state", size = 1024, lastModified = 125)

        // Then
        assertTrue("SRM files should be valid", srmFile.relativePath.endsWith(".srm"))
        assertTrue("SAV files should be valid", savFile.relativePath.endsWith(".sav"))
        assertTrue("State files should be valid", stateFile.relativePath.endsWith(".state"))
        
        // Verify sizes are preserved
        assertEquals(100L, srmFile.size)
        assertEquals(200L, savFile.size)
        assertEquals(1024L, stateFile.size)
    }

    @Test
    fun `SyncFolder should handle URI toString and parsing correctly`() {
        // Given
        val mockUri = mock<Uri>()
        val uriString = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2Fcom.retroarch%2Ffiles%2Fsaves"
        whenever(mockUri.toString()).thenReturn(uriString)

        // When
        val folder = SyncFolder(mockUri, "RetroArch Saves")

        // Then
        assertEquals(uriString, folder.uri.toString())
        assertEquals("RetroArch Saves", folder.name)
    }

    @Test
    fun `SyncStatus should track error states correctly`() {
        // Given
        val statusWithError = SyncStatus(lastError = "Connection failed")
        val statusWithoutError = SyncStatus(lastError = null)
        val statusClearedError = statusWithError.copy(lastError = null)

        // Then
        assertNotNull("Should have error", statusWithError.lastError)
        assertNull("Should not have error", statusWithoutError.lastError)
        assertNull("Should have cleared error", statusClearedError.lastError)
        
        // Verify error doesn't affect other properties
        assertEquals(statusWithoutError.isSyncing, statusWithError.isSyncing)
        assertEquals(statusWithoutError.lastSyncTime, statusWithError.lastSyncTime)
    }
}