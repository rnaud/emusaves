package com.emusaves

import com.emusaves.data.remote.api.SynologyApiClientTest
import com.emusaves.domain.model.EmulatorLocationTest
import com.emusaves.domain.model.ModelsTest
import com.emusaves.domain.repository.EmusavesRepositoryTest
import com.emusaves.ui.screens.HomeViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test Suite for EmuSaves application
 * 
 * This suite runs all unit tests for the application to ensure comprehensive coverage
 * of core functionality including:
 * 
 * - Domain Models (EmulatorLocation, SyncFolder, etc.)
 * - Repository Layer (EmusavesRepository) 
 * - API Client (SynologyApiClient)
 * - View Models (HomeViewModel)
 * 
 * Run with: ./gradlew test
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    // Domain Model Tests
    EmulatorLocationTest::class,
    ModelsTest::class,
    
    // Repository Tests  
    EmusavesRepositoryTest::class,
    
    // API Client Tests
    SynologyApiClientTest::class,
    
    // ViewModel Tests
    HomeViewModelTest::class
)
class EmuSavesTestSuite