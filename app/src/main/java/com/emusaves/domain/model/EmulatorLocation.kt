package com.emusaves.domain.model

data class EmulatorLocation(
    val name: String,
    val emulator: String,
    val path: String,
    val description: String,
    val icon: String = "🎮",
    val category: EmulatorCategory = EmulatorCategory.MULTI_SYSTEM
)

enum class EmulatorCategory(val displayName: String, val icon: String) {
    MULTI_SYSTEM("Multi-System", "🎮"),
    CONSOLE("Console", "🕹️"),
    HANDHELD("Handheld", "📱"),
    ARCADE("Arcade", "🎪")
}

object EmulatorLocations {
    val DEFAULT_LOCATIONS = listOf(
        // Multi-System Emulators
        EmulatorLocation(
            name = "RetroArch Saves",
            emulator = "RetroArch",
            path = "/storage/emulated/0/Android/data/com.retroarch/files/saves/",
            description = "Battery saves and SRAM files",
            icon = "🎮",
            category = EmulatorCategory.MULTI_SYSTEM
        ),
        EmulatorLocation(
            name = "RetroArch States", 
            emulator = "RetroArch",
            path = "/storage/emulated/0/Android/data/com.retroarch/files/states/",
            description = "Save states for all cores",
            icon = "💾",
            category = EmulatorCategory.MULTI_SYSTEM
        ),
        
        // Console Emulators
        EmulatorLocation(
            name = "PPSSPP Saves",
            emulator = "PPSSPP",
            path = "/storage/emulated/0/PSP/SAVEDATA/",
            description = "PSP save data and memory sticks",
            icon = "🎮",
            category = EmulatorCategory.CONSOLE
        ),
        EmulatorLocation(
            name = "Dolphin Saves",
            emulator = "Dolphin",
            path = "/storage/emulated/0/dolphin-emu/Saves/",
            description = "GameCube and Wii save files",
            icon = "🐬",
            category = EmulatorCategory.CONSOLE
        ),
        EmulatorLocation(
            name = "AetherSX2 Saves",
            emulator = "AetherSX2", 
            path = "/storage/emulated/0/Android/data/xyz.aethersx2.android/files/saves/",
            description = "PlayStation 2 save data",
            icon = "🎮",
            category = EmulatorCategory.CONSOLE
        ),
        EmulatorLocation(
            name = "ePSXe States",
            emulator = "ePSXe",
            path = "/storage/emulated/0/epsxe/sstates/",
            description = "PlayStation 1 save states", 
            icon = "💿",
            category = EmulatorCategory.CONSOLE
        ),
        EmulatorLocation(
            name = "ePSXe Memory Cards",
            emulator = "ePSXe",
            path = "/storage/emulated/0/epsxe/memcards/",
            description = "PlayStation 1 memory card files",
            icon = "💾",
            category = EmulatorCategory.CONSOLE
        ),
        
        // Handheld Emulators
        EmulatorLocation(
            name = "DraStic Saves",
            emulator = "DraStic",
            path = "/storage/emulated/0/DraStic/backup/",
            description = "Nintendo DS save backups",
            icon = "📱",
            category = EmulatorCategory.HANDHELD
        ),
        EmulatorLocation(
            name = "My Boy! Saves",
            emulator = "My Boy!",
            path = "/storage/emulated/0/MyBoy/saves/",
            description = "Game Boy Advance save files",
            icon = "🟢",
            category = EmulatorCategory.HANDHELD
        ),
        EmulatorLocation(
            name = "Pizza Boy GBA",
            emulator = "Pizza Boy GBA",
            path = "/storage/emulated/0/Android/data/it.dbtecno.pizzaboygba/files/",
            description = "Game Boy Advance saves and states",
            icon = "🍕",
            category = EmulatorCategory.HANDHELD
        ),
        EmulatorLocation(
            name = "Pizza Boy GBC",
            emulator = "Pizza Boy GBC", 
            path = "/storage/emulated/0/Android/data/it.dbtecno.pizzaboygbc/files/",
            description = "Game Boy Color saves and states",
            icon = "🍕",
            category = EmulatorCategory.HANDHELD
        ),
        EmulatorLocation(
            name = "Snes9x EX+ Saves",
            emulator = "Snes9x EX+",
            path = "/storage/emulated/0/Android/data/com.explusalpha.Snes9xPlus/files/saves/",
            description = "Super Nintendo save files",
            icon = "🎮",
            category = EmulatorCategory.HANDHELD
        ),
        EmulatorLocation(
            name = "MD.emu Saves", 
            emulator = "MD.emu",
            path = "/storage/emulated/0/Android/data/com.explusalpha.MdEmu/files/saves/",
            description = "Genesis/Mega Drive save files",
            icon = "🎮", 
            category = EmulatorCategory.HANDHELD
        ),
        
        // Arcade Emulators
        EmulatorLocation(
            name = "MAME4droid Saves",
            emulator = "MAME4droid",
            path = "/storage/emulated/0/MAME4droid/saves/",
            description = "Arcade machine save files",
            icon = "🕹️",
            category = EmulatorCategory.ARCADE
        ),
        EmulatorLocation(
            name = "FBNeo Saves",
            emulator = "FBNeo",
            path = "/storage/emulated/0/Android/data/com.explusalpha.NeoEmu/files/saves/",
            description = "Neo Geo and arcade saves",
            icon = "🎪",
            category = EmulatorCategory.ARCADE
        )
    )
    
    fun getByCategory(category: EmulatorCategory): List<EmulatorLocation> {
        return DEFAULT_LOCATIONS.filter { it.category == category }
    }
    
    fun searchByName(query: String): List<EmulatorLocation> {
        return DEFAULT_LOCATIONS.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.emulator.contains(query, ignoreCase = true)
        }
    }
}