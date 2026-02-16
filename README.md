# EmuSaves 🎮

[![Build APK](https://github.com/rnaud/emusaves/actions/workflows/build-apk.yml/badge.svg)](https://github.com/rnaud/emusaves/actions/workflows/build-apk.yml)

**EmuSaves** is an Android application that automatically backs up your emulator save files to a Synology NAS. Never lose your game progress again!

## ✨ Features

### 📱 Core Functionality
- **Automatic Save Detection**: Scans selected folders for emulator save files (RetroArch, Dolphin, PPSSPP, etc.)
- **Synology NAS Integration**: Secure backup to your personal NAS via Synology FileStation API
- **Folder Management**: Select multiple emulator directories to monitor
- **Real-time Sync Status**: Track sync progress and view last backup timestamps
- **Scheduled Backups**: Configurable background sync (every 6 hours on Wi-Fi + charging)

### 🛠 Technical Features
- **Modern Android**: Built with Kotlin and Jetpack Compose
- **Material Design 3**: Clean, intuitive UI following Google's design guidelines
- **Room Database**: Local storage for sync history and configuration
- **WorkManager**: Reliable background processing for scheduled syncs
- **Document Provider Integration**: Seamless folder selection with persistent permissions

### 🎯 Supported Emulators
Currently supports save files from:
- **RetroArch** (`.srm`, `.state`, `.sav`, `.bsv`)
- More emulators coming soon!

## 🚀 Quick Start

1. **Download**: Get the latest APK from [GitHub Releases](../../releases) or [Actions](../../actions)
2. **Install**: Enable "Unknown Sources" and install the APK
3. **Configure Synology**: Enter your NAS address, username, and app password
4. **Select Folders**: Choose your emulator save directories
5. **Sync**: Tap "Sync Now" or enable scheduled backups

## ⚙️ Setup Guide

### Synology NAS Configuration
1. **Enable FileStation**: Go to Package Center → Install File Station
2. **Create App Password**: 
   - Control Panel → User & Group → [Your User] → Edit → Applications
   - Create new application password for "EmuSaves"
3. **Test Connection**: Use your NAS local IP (e.g., `192.168.1.100` or `nas.local`)

### Android Setup
1. **Folder Selection**: Use the document picker to grant access to your emulator directories
2. **Background Sync**: Enable for automatic backups when charging + on Wi-Fi
3. **Manual Sync**: Tap "Sync Now" to backup immediately

## 🔧 Building the APK

### Automatic Builds (GitHub Actions)

APK files are automatically built by GitHub Actions on every push to `main` and on pull requests.

**To download the APK:**
1. Go to the [Actions tab](../../actions) in the repository
2. Click on the latest "Build APK" workflow run
3. Scroll down to the "Artifacts" section
4. Download either:
   - `app-debug` - Debug version of the APK
   - `app-release` - Release version of the APK (unsigned)

The artifacts are retained for 30 days.

### Manual Build (Local)

**Requirements:**
- JDK 17 or higher
- Android SDK (automatically downloaded by Gradle)

**Build commands:**
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

**Output locations:**
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

## 🏗 Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Database**: Room (SQLite)
- **Background Processing**: WorkManager
- **Networking**: OkHttp3 + Retrofit-style API client
- **Storage**: Android Storage Access Framework (SAF)

### Project Structure
```
app/src/main/java/com/emusaves/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   └── remote/         # Synology API client, network models
├── domain/
│   ├── model/          # Domain models
│   └── repository/     # Repository pattern implementation
└── ui/
    └── screens/        # Compose UI screens and components
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

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Synology** for their comprehensive FileStation API
- **Android Open Source Project** for the excellent development platform
- **Jetpack Compose** team for the modern UI toolkit

---

**Note**: EmuSaves is not affiliated with any emulator projects or Synology Inc. This is an independent project created for the retro gaming community.
