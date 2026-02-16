package com.emusaves.ui.screens

import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.emusaves.domain.model.SynologyConfig
import com.emusaves.domain.model.SyncFolder
import com.emusaves.domain.repository.EmusavesRepository
import com.emusaves.sync.SyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.mockito.kotlin.mockStatic
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var repository: EmusavesRepository

    @Mock
    private lateinit var uri: Uri

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Mock repository methods
        whenever(repository.getFolders()).thenReturn(flowOf(emptyList()))
        whenever(repository.getSynologyConfig()).thenReturn(flowOf(null))
        
        viewModel = HomeViewModel(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() {
        // Then
        val state = viewModel.uiState.value
        assertEquals(emptyList<SyncFolder>(), state.folders)
        assertNull(state.config)
        assertFalse(state.syncStatus.isSyncing)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `should load folders and config on init`() = runTest {
        // Given
        val folders = listOf(
            SyncFolder(uri, "RetroArch Saves"),
            SyncFolder(uri, "PPSSPP Saves")
        )
        val config = SynologyConfig("nas.local", "user", "pass", "/Drive/EmulatorBackups")
        
        whenever(repository.getFolders()).thenReturn(flowOf(folders))
        whenever(repository.getSynologyConfig()).thenReturn(flowOf(config))
        
        // When
        val newViewModel = HomeViewModel(context)
        advanceUntilIdle()
        
        // Then
        val state = newViewModel.uiState.value
        assertEquals(folders, state.folders)
        assertEquals(config, state.config)
    }

    @Test
    fun `addFolder should call repository addFolder`() = runTest {
        // Given
        whenever(repository.addFolder(uri, null)).thenReturn(Unit)
        
        // When
        viewModel.addFolder(uri, null)
        advanceUntilIdle()
        
        // Then
        verify(repository).addFolder(uri, null)
    }

    @Test
    fun `addFolder should set error on exception`() = runTest {
        // Given
        val errorMessage = "Failed to add folder"
        whenever(repository.addFolder(uri, null)).thenThrow(RuntimeException(errorMessage))
        
        // When
        viewModel.addFolder(uri, null)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `removeFolder should call repository removeFolder`() = runTest {
        // Given
        whenever(repository.removeFolder(uri)).thenReturn(Unit)
        
        // When
        viewModel.removeFolder(uri)
        advanceUntilIdle()
        
        // Then
        verify(repository).removeFolder(uri)
    }

    @Test
    fun `removeFolder should set error on exception`() = runTest {
        // Given
        val errorMessage = "Failed to remove folder"
        whenever(repository.removeFolder(uri)).thenThrow(RuntimeException(errorMessage))
        
        // When
        viewModel.removeFolder(uri)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `saveConfig should call repository and update state`() = runTest {
        // Given
        val config = SynologyConfig("nas.local", "user", "pass", "/Drive/EmulatorBackups")
        whenever(repository.saveSynologyConfig(config)).thenReturn(Unit)
        
        // When
        viewModel.saveConfig(config)
        advanceUntilIdle()
        
        // Then
        verify(repository).saveSynologyConfig(config)
        val state = viewModel.uiState.value
        assertEquals(config, state.config)
    }

    @Test
    fun `saveConfig should set error on exception`() = runTest {
        // Given
        val config = SynologyConfig("nas.local", "user", "pass", "/Drive/EmulatorBackups")
        val errorMessage = "Failed to save config"
        whenever(repository.saveSynologyConfig(config)).thenThrow(RuntimeException(errorMessage))
        
        // When
        viewModel.saveConfig(config)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `syncNow should start sync and update sync status`() = runTest {
        // Given
        mockStatic<SyncWorker>().use { mockedStatic ->
            // When
            viewModel.syncNow()
            advanceUntilIdle()
            
            // Then
            mockedStatic.verify { SyncWorker.enqueueOneTime(context) }
            val state = viewModel.uiState.value
            assertFalse(state.syncStatus.isSyncing)
            assertTrue(state.syncStatus.lastSyncTime > 0)
            assertNull(state.syncStatus.lastError)
        }
    }

    @Test
    fun `syncNow should handle sync worker exception`() = runTest {
        // Given
        val errorMessage = "Sync failed"
        mockStatic<SyncWorker>().use { mockedStatic ->
            mockedStatic.`when`<Unit> { SyncWorker.enqueueOneTime(context) }
                .thenThrow(RuntimeException(errorMessage))
            
            // When
            viewModel.syncNow()
            advanceUntilIdle()
            
            // Then
            val state = viewModel.uiState.value
            assertFalse(state.syncStatus.isSyncing)
            assertEquals(errorMessage, state.syncStatus.lastError)
        }
    }

    @Test
    fun `enableScheduledSync true should enqueue periodic sync`() {
        // Given
        mockStatic<SyncWorker>().use { mockedStatic ->
            // When
            viewModel.enableScheduledSync(true)
            
            // Then
            mockedStatic.verify { SyncWorker.enqueuePeriodic(context) }
        }
    }

    @Test
    fun `enableScheduledSync false should cancel sync`() {
        // Given
        mockStatic<SyncWorker>().use { mockedStatic ->
            // When
            viewModel.enableScheduledSync(false)
            
            // Then
            mockedStatic.verify { SyncWorker.cancel(context) }
        }
    }

    @Test
    fun `clearError should clear error from state`() = runTest {
        // Given - Set an error first
        val config = SynologyConfig("nas.local", "user", "pass", "/Drive/EmulatorBackups")
        whenever(repository.saveSynologyConfig(config)).thenThrow(RuntimeException("Error"))
        viewModel.saveConfig(config)
        advanceUntilIdle()
        
        // Verify error is set
        assertNotNull(viewModel.uiState.value.error)
        
        // When
        viewModel.clearError()
        
        // Then
        val state = viewModel.uiState.value
        assertNull(state.error)
    }

    @Test
    fun `HomeUiState should have correct default values`() {
        // When
        val uiState = HomeUiState()
        
        // Then
        assertEquals(emptyList<SyncFolder>(), uiState.folders)
        assertNull(uiState.config)
        assertFalse(uiState.syncStatus.isSyncing)
        assertEquals(0, uiState.syncStatus.lastSyncTime)
        assertEquals(0, uiState.syncStatus.filesBackedUp)
        assertEquals(0, uiState.syncStatus.totalFiles)
        assertNull(uiState.syncStatus.lastError)
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `Factory should create HomeViewModel correctly`() {
        // Given
        val factory = HomeViewModel.Factory(context)
        
        // When
        val createdViewModel = factory.create(HomeViewModel::class.java)
        
        // Then
        assertNotNull(createdViewModel)
        assertTrue(createdViewModel is HomeViewModel)
    }
}