package com.emusaves.ui.screens

import android.content.Context
import android.net.Uri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.emusaves.domain.model.SynologyConfig
import com.emusaves.domain.model.SyncFolder
import com.emusaves.domain.model.SyncStatus
import com.emusaves.domain.repository.EmusavesRepository
import com.emusaves.sync.SyncWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val folders: List<SyncFolder> = emptyList(),
    val config: SynologyConfig? = null,
    val syncStatus: SyncStatus = SyncStatus(),
    val scheduledSyncEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val context: Context,
    private val repository: EmusavesRepository = EmusavesRepository(context)
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val workManager = WorkManager.getInstance(context)
    private val syncWorkObserver = Observer<List<WorkInfo>> { infos ->
        val active = infos?.any { info ->
            info.state == WorkInfo.State.ENQUEUED || info.state == WorkInfo.State.RUNNING
        } ?: false
        _uiState.update { it.copy(scheduledSyncEnabled = active) }
    }
    private val syncWorkLiveData = workManager.getWorkInfosForUniqueWorkLiveData(SyncWorker.WORK_NAME)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getFolders(),
                repository.getSynologyConfig()
            ) { folders, config ->
                _uiState.update {
                    it.copy(
                        folders = folders,
                        config = config
                    )
                }
            }.collect()
        }

        // Observe WorkManager periodic sync state so the toggle reflects reality
        syncWorkLiveData.observeForever(syncWorkObserver)
    }

    override fun onCleared() {
        super.onCleared()
        syncWorkLiveData.removeObserver(syncWorkObserver)
    }

    fun addFolder(uri: Uri, name: String? = null) {
        viewModelScope.launch {
            try {
                repository.addFolder(uri, name)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun removeFolder(uri: Uri) {
        viewModelScope.launch {
            try {
                repository.removeFolder(uri)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun saveConfig(config: SynologyConfig) {
        viewModelScope.launch {
            try {
                repository.saveSynologyConfig(config)
                _uiState.update { it.copy(config = config) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncStatus = it.syncStatus.copy(isSyncing = true, lastError = null)) }
            try {
                // Trigger sync worker
                SyncWorker.enqueueOneTime(context)
                _uiState.update { 
                    it.copy(
                        syncStatus = it.syncStatus.copy(
                            isSyncing = false,
                            lastSyncTime = System.currentTimeMillis()
                        )
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        syncStatus = it.syncStatus.copy(
                            isSyncing = false,
                            lastError = e.message
                        )
                    ) 
                }
            }
        }
    }

    fun enableScheduledSync(enabled: Boolean) {
        if (enabled) {
            SyncWorker.enqueuePeriodic(context)
        } else {
            SyncWorker.cancel(context)
        }
        // Optimistically update UI; WorkManager observer will confirm
        _uiState.update { it.copy(scheduledSyncEnabled = enabled) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    class Factory(
        private val context: Context,
        private val repository: EmusavesRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(context, repository ?: EmusavesRepository(context)) as T
        }
    }
}
