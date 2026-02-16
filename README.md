# EmuSaves

An Android application for managing emulator save states.

## Building the APK

### Automatic Builds (GitHub Actions)

APK files are automatically built by GitHub Actions on every push to the `main` or `master` branch, and on pull requests. You can also trigger a manual build.

**To download the APK:**
1. Go to the [Actions tab](../../actions) in the repository
2. Click on the latest "Build APK" workflow run
3. Scroll down to the "Artifacts" section
4. Download either:
   - `app-debug` - Debug version of the APK
   - `app-release` - Release version of the APK (unsigned)

The artifacts are retained for 30 days.

### Manual Build (Local)

To build the APK locally:

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

The built APKs will be available in:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Requirements

- JDK 17 or higher
- Android SDK (automatically downloaded by Gradle)

## Features

- Save state management for emulators
- Built with Kotlin and Jetpack Compose
- Material Design 3 UI
- Room database for local storage
- Background sync with WorkManager

## License

See the LICENSE file for details.
