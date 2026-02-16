package com.emusaves.domain.model

import org.junit.Assert.*
import org.junit.Test

class EmulatorLocationTest {

    @Test
    fun `EmulatorLocation should create with correct properties`() {
        // Given
        val name = "RetroArch Saves"
        val emulator = "RetroArch"
        val path = "/storage/emulated/0/Android/data/com.retroarch/files/saves/"
        val description = "Battery saves and SRAM files"
        val icon = "🎮"
        val category = EmulatorCategory.MULTI_SYSTEM

        // When
        val location = EmulatorLocation(
            name = name,
            emulator = emulator,
            path = path,
            description = description,
            icon = icon,
            category = category
        )

        // Then
        assertEquals(name, location.name)
        assertEquals(emulator, location.emulator)
        assertEquals(path, location.path)
        assertEquals(description, location.description)
        assertEquals(icon, location.icon)
        assertEquals(category, location.category)
    }

    @Test
    fun `EmulatorLocation should have default values`() {
        // Given/When
        val location = EmulatorLocation(
            name = "Test Emulator",
            emulator = "TestEmu",
            path = "/test/path",
            description = "Test description"
        )

        // Then
        assertEquals("🎮", location.icon) // Default icon
        assertEquals(EmulatorCategory.MULTI_SYSTEM, location.category) // Default category
    }
}

class EmulatorCategoryTest {

    @Test
    fun `EmulatorCategory should have correct display names`() {
        assertEquals("Multi-System", EmulatorCategory.MULTI_SYSTEM.displayName)
        assertEquals("Console", EmulatorCategory.CONSOLE.displayName)
        assertEquals("Handheld", EmulatorCategory.HANDHELD.displayName)
        assertEquals("Arcade", EmulatorCategory.ARCADE.displayName)
    }

    @Test
    fun `EmulatorCategory should have correct icons`() {
        assertEquals("🎮", EmulatorCategory.MULTI_SYSTEM.icon)
        assertEquals("🕹️", EmulatorCategory.CONSOLE.icon)
        assertEquals("📱", EmulatorCategory.HANDHELD.icon)
        assertEquals("🎪", EmulatorCategory.ARCADE.icon)
    }
}

class EmulatorLocationsTest {

    @Test
    fun `DEFAULT_LOCATIONS should contain expected emulators`() {
        // Given
        val locations = EmulatorLocations.DEFAULT_LOCATIONS

        // Then
        assertTrue("Should contain RetroArch Saves", locations.any { it.name == "RetroArch Saves" })
        assertTrue("Should contain RetroArch States", locations.any { it.name == "RetroArch States" })
        assertTrue("Should contain PPSSPP Saves", locations.any { it.name == "PPSSPP Saves" })
        assertTrue("Should contain Dolphin Saves", locations.any { it.name == "Dolphin Saves" })
        assertTrue("Should contain My Boy! Saves", locations.any { it.name == "My Boy! Saves" })
        
        // Verify minimum count of locations
        assertTrue("Should have at least 15 emulator locations", locations.size >= 15)
    }

    @Test
    fun `getByCategory should filter locations correctly`() {
        // When
        val multiSystemLocations = EmulatorLocations.getByCategory(EmulatorCategory.MULTI_SYSTEM)
        val consoleLocations = EmulatorLocations.getByCategory(EmulatorCategory.CONSOLE)
        val handheldLocations = EmulatorLocations.getByCategory(EmulatorCategory.HANDHELD)
        val arcadeLocations = EmulatorLocations.getByCategory(EmulatorCategory.ARCADE)

        // Then
        assertTrue("Should have multi-system emulators", multiSystemLocations.isNotEmpty())
        assertTrue("Should have console emulators", consoleLocations.isNotEmpty())
        assertTrue("Should have handheld emulators", handheldLocations.isNotEmpty())
        assertTrue("Should have arcade emulators", arcadeLocations.isNotEmpty())

        // Verify all returned items have correct category
        multiSystemLocations.forEach { location ->
            assertEquals(EmulatorCategory.MULTI_SYSTEM, location.category)
        }
        consoleLocations.forEach { location ->
            assertEquals(EmulatorCategory.CONSOLE, location.category)
        }
    }

    @Test
    fun `getByCategory should return specific emulators in correct categories`() {
        // When
        val multiSystemLocations = EmulatorLocations.getByCategory(EmulatorCategory.MULTI_SYSTEM)
        val consoleLocations = EmulatorLocations.getByCategory(EmulatorCategory.CONSOLE)
        val handheldLocations = EmulatorLocations.getByCategory(EmulatorCategory.HANDHELD)

        // Then - Multi-System should contain RetroArch
        assertTrue("Multi-System should contain RetroArch Saves", 
            multiSystemLocations.any { it.name == "RetroArch Saves" })
        assertTrue("Multi-System should contain RetroArch States",
            multiSystemLocations.any { it.name == "RetroArch States" })

        // Console should contain specific console emulators
        assertTrue("Console should contain PPSSPP Saves",
            consoleLocations.any { it.name == "PPSSPP Saves" })
        assertTrue("Console should contain Dolphin Saves",
            consoleLocations.any { it.name == "Dolphin Saves" })

        // Handheld should contain handheld emulators
        assertTrue("Handheld should contain My Boy! Saves",
            handheldLocations.any { it.name == "My Boy! Saves" })
    }

    @Test
    fun `searchByName should find emulators by name`() {
        // When
        val retroArchResults = EmulatorLocations.searchByName("RetroArch")
        val ppssppResults = EmulatorLocations.searchByName("PPSSPP")
        val boyResults = EmulatorLocations.searchByName("Boy")

        // Then
        assertTrue("Should find RetroArch emulators", retroArchResults.isNotEmpty())
        assertTrue("Should find PPSSPP emulators", ppssppResults.isNotEmpty())
        assertTrue("Should find Boy emulators (My Boy, Pizza Boy)", boyResults.isNotEmpty())

        // Verify search results contain the search term
        retroArchResults.forEach { location ->
            assertTrue("Result should contain 'RetroArch' in name or emulator",
                location.name.contains("RetroArch", ignoreCase = true) ||
                location.emulator.contains("RetroArch", ignoreCase = true))
        }
    }

    @Test
    fun `searchByName should be case insensitive`() {
        // When
        val lowerCaseResults = EmulatorLocations.searchByName("retroarch")
        val upperCaseResults = EmulatorLocations.searchByName("RETROARCH")
        val mixedCaseResults = EmulatorLocations.searchByName("RetroArch")

        // Then
        assertEquals("Case insensitive search should return same results", 
            lowerCaseResults.size, upperCaseResults.size)
        assertEquals("Case insensitive search should return same results",
            lowerCaseResults.size, mixedCaseResults.size)
    }

    @Test
    fun `searchByName should return empty list for non-existent emulator`() {
        // When
        val results = EmulatorLocations.searchByName("NonExistentEmulator")

        // Then
        assertTrue("Should return empty list for non-existent emulator", results.isEmpty())
    }

    @Test
    fun `DEFAULT_LOCATIONS should have valid paths`() {
        // Given
        val locations = EmulatorLocations.DEFAULT_LOCATIONS

        // Then
        locations.forEach { location ->
            assertTrue("Path should not be empty: ${location.name}", location.path.isNotBlank())
            assertTrue("Path should be absolute: ${location.name}", location.path.startsWith("/"))
            assertTrue("Path should end with slash: ${location.name}", location.path.endsWith("/"))
        }
    }

    @Test
    fun `DEFAULT_LOCATIONS should have unique names`() {
        // Given
        val locations = EmulatorLocations.DEFAULT_LOCATIONS
        val names = locations.map { it.name }

        // Then
        assertEquals("All location names should be unique", names.size, names.toSet().size)
    }

    @Test
    fun `DEFAULT_LOCATIONS should have consistent emulator groupings`() {
        // Given
        val locations = EmulatorLocations.DEFAULT_LOCATIONS
        
        // When - Get RetroArch locations
        val retroArchLocations = locations.filter { it.emulator == "RetroArch" }
        
        // Then
        assertTrue("Should have multiple RetroArch entries", retroArchLocations.size >= 2)
        assertTrue("Should have RetroArch Saves", 
            retroArchLocations.any { it.name == "RetroArch Saves" })
        assertTrue("Should have RetroArch States",
            retroArchLocations.any { it.name == "RetroArch States" })
    }

    @Test
    fun `DEFAULT_LOCATIONS should cover all categories`() {
        // Given
        val locations = EmulatorLocations.DEFAULT_LOCATIONS
        val categoriesUsed = locations.map { it.category }.toSet()

        // Then
        assertEquals("Should use all emulator categories", 
            EmulatorCategory.values().toSet(), categoriesUsed)
    }

    @Test
    fun `Pizza Boy emulators should be in handheld category`() {
        // When
        val pizzaBoyGBA = EmulatorLocations.DEFAULT_LOCATIONS
            .find { it.emulator == "Pizza Boy GBA" }
        val pizzaBoyGBC = EmulatorLocations.DEFAULT_LOCATIONS
            .find { it.emulator == "Pizza Boy GBC" }

        // Then
        assertNotNull("Should have Pizza Boy GBA", pizzaBoyGBA)
        assertNotNull("Should have Pizza Boy GBC", pizzaBoyGBC)
        assertEquals("Pizza Boy GBA should be handheld", 
            EmulatorCategory.HANDHELD, pizzaBoyGBA?.category)
        assertEquals("Pizza Boy GBC should be handheld",
            EmulatorCategory.HANDHELD, pizzaBoyGBC?.category)
    }
}