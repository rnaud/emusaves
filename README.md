# EmuSaves 🎮

[![Build APK](https://github.com/rnaud/emusaves/actions/workflows/build-apk.yml/badge.svg)](https://github.com/rnaud/emusaves/actions/workflows/build-apk.yml)
[![Run Tests](https://github.com/rnaud/emusaves/actions/workflows/test.yml/badge.svg)](https://github.com/rnaud/emusaves/actions/workflows/test.yml)
[![codecov](https://codecov.io/gh/rnaud/emusaves/branch/main/graph/badge.svg)](https://codecov.io/gh/rnaud/emusaves)

**EmuSaves** is an Android application that automatically backs up your emulator save files to a Synology NAS. Never lose your game progress again!

## 📱 Screenshots

### Main Interface
<div align="center">
<table>
<tr>
<td align="center" width="33%">

**Home Screen**  
<img src="docs/screenshots/screenshot-home.png" alt="Home Screen" width="250"/>

*Main dashboard with ⭐ Quick Add button, sync status, and configured folders*

</td>
<td align="center" width="33%">

**Quick Add Dialog**  
<img src="docs/screenshots/screenshot-quick-add.png" alt="Quick Add Dialog" width="250"/>

*Pre-configured emulator locations with category filtering*

</td>
<td align="center" width="33%">

**Sync Progress**  
<img src="docs/screenshots/screenshot-sync-progress.png" alt="Sync Progress" width="250"/>

*Real-time upload progress with detailed file statistics*

</td>
</tr>
</table>
</div>

### Configuration Screens
<div align="center">
<table>
<tr>
<td align="center" width="50%">

**Synology Setup**  
<img src="docs/screenshots/screenshot-synology-config.png" alt="Synology Configuration" width="250"/>

*Simple NAS configuration with connection testing*

</td>
</tr>
</table>
</div>

## ✨ Features

### 📱 Core Functionality
- **Automatic Save Detection**: Scans selected folders for emulator save files (RetroArch, Dolphin, PPSSPP, etc.)
- **Synology NAS Integration**: Secure backup to your personal NAS via Synology FileStation API
- **Folder Management**: Select multiple emulator directories to monitor
- **Real-time Sync Status**: Track sync progress and view last backup timestamps
- **Scheduled Backups**: Configurable background sync (every 6 hours on Wi-Fi + charging)

### ⭐ Quick Add Feature
- **15+ Pre-configured Emulator Locations**: Instantly add popular emulator save folders
- **Category Filtering**: Multi-System 🎮, Console 🕹️, Handheld 📱, Arcade 🕹️
- **One-tap Selection**: No more manual folder browsing for common emulators
- **Material Design 3 UI**: Clean category chips with intuitive selection

### 🛠 Technical Features
- **Modern Android**: Built with Kotlin and Jetpack Compose
- **Material Design 3**: Clean, intuitive UI following Google's design guidelines
- **Room Database**: Local storage for sync history and configuration
- **WorkManager**: Reliable background processing for scheduled syncs
- **Document Provider Integration**: Seamless folder selection with persistent permissions

### 🎯 Supported Emulators
**File Format Support**: `.srm`, `.sav`, `.state`, `.bsv`, `.dat`, `.mcr`, `.gci`, and more

**Tested Emulators**:
- 🎮 **RetroArch** - All cores and save types
- 🎮 **PPSSPP** - PSP save data and states  
- 🎮 **Dolphin** - GameCube/Wii saves and memory cards
- 🎮 **AetherSX2** - PS2 save data and states
- 🎮 **My Boy!** - Game Boy Advance saves
- 🎮 **DraStic** - Nintendo DS saves
- 🎮 **Pizza Boy** - Game Boy/Color saves
- 🎮 **Lemuroid** - Multi-system saves

## 🚀 Getting Started

### Prerequisites
- Android 8.0+ (API level 26)
- Synology NAS with FileStation enabled
- Wi-Fi connection to your local network

### Installation
1. Download the latest APK from [Releases](../../releases)
2. Install the APK (enable "Unknown Sources" if needed)
3. Configure your Synology NAS connection
4. Add your emulator folders using Quick Add or Browse
5. Enable background sync for automatic backups

### Quick Setup
1. **Connect to NAS**: Enter your Synology NAS address, username, and app password
2. **Add Folders**: Use Quick Add for popular emulators or Browse for custom locations
3. **Start Backup**: Tap "Sync Now" or enable scheduled sync for automatic backups

## 🔧 Configuration

### Synology NAS Setup
1. Enable FileStation on your Synology NAS
2. Create a dedicated user account for EmuSaves
3. Generate an app-specific password for the account
4. Create a backup directory (e.g., `/Drive/EmulatorBackups`)

### Folder Selection
- **Quick Add**: Choose from 15+ pre-configured emulator locations
- **Browse**: Manually select any folder containing save files
- **Multiple Folders**: Add as many emulator directories as needed

### Sync Options
- **Manual Sync**: Tap "Sync Now" for immediate backup
- **Scheduled Sync**: Automatic backup every 6 hours (Wi-Fi + charging)
- **Progress Tracking**: Real-time sync status and file counts

## 📁 Project Structure

```
app/
├── src/main/java/com/emusaves/
│   ├── data/
│   │   ├── local/          # Room database, DAOs, entities
│   │   └── remote/         # Synology API client, network models
│   ├── domain/
│   │   ├── model/          # Domain models
│   │   └── repository/     # Repository pattern implementation
│   └── ui/
│       └── screens/        # Compose UI screens and components
```

## 🛡 Privacy & Security

- **Local Storage**: All data stays on your device and your personal NAS
- **No Third-Party Services**: Direct connection to your Synology NAS only
- **Encrypted Transport**: HTTPS communication with your NAS
- **Minimal Permissions**: Only requests access to folders you explicitly select

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues, feature requests, or pull requests.

### Development Setup
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run on device or emulator

### 🧪 Testing & Quality Assurance

#### **Automated Testing**
- **62 comprehensive unit tests** covering all core functionality
- **90%+ code coverage** with JaCoCo reporting
- **GitHub Actions CI** runs tests on every push and PR
- **Quality gates** ensure tests pass before code merge

#### **Running Tests Locally**
```bash
# Run all unit tests
./run-tests.sh

# Run with coverage report
./run-tests.sh --coverage --open

# Watch mode for development  
./run-tests.sh --watch --coverage
```

#### **Continuous Integration**
- ✅ **Automated testing** on every commit
- 📊 **Coverage reporting** with trend tracking
- 🔍 **Code quality checks** (Kotlin lint + Android lint)
- 🛡️ **PR protection** requiring tests to pass
- 📋 **Visual test reports** in GitHub Actions

See [TESTING.md](TESTING.md) for detailed testing documentation.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Synology** for their comprehensive FileStation API
- **Android Open Source Project** for the excellent development platform
- **Jetpack Compose** team for the modern UI toolkit

---

**Note**: EmuSaves is not affiliated with any emulator projects or Synology Inc. This is an independent project created for the retro gaming community.