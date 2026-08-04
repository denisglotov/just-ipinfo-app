# Just IP Info

A minimalistic, Clean Code Android application that fetches IP information from various providers
and maintains a timestamped log file.

## Features
- **Configurable IP Info Source**: Fetches data from `ipinfo.io` (default) or any user-defined URL.
- **Service Presets**: Quickly switch between popular IP services like `ifconfig.co`, `ipify.org`, and `icanhazip.com`.
- **Light & Dark Themes**: Full support for both themes with a persistent toggle.
- **Persistent Logging**: Saves every request result (or error) with a timestamp to an internal log file.
- **Log Management**: Immediate visibility of logs in the UI with a "Clear" function.
- **Clean Architecture**: Follows MVVM principles with manual dependency injection for minimal boilerplate and state hoisting for predictable UI behavior.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Networking**: OkHttp
- **Build System**: Gradle (Kotlin DSL, Version Catalog `libs.versions.toml`)

## Prerequisites
- **JDK 17** or higher.
- **Android SDK** installed on your system.

## Build Instructions (Terminal)

### 1. Configure `local.properties`
Make sure you have Android SDK installed and `ANDROID_HOME` exported, or specify its path in `local.properties`:

```properties
sdk.dir=/opt/homebrew/share/android-commandlinetools
```

### 2. Build the Project
Use the Gradle wrapper to compile the application and generate a Debug APK:

```bash
./gradlew assembleDebug
```
The resulting APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### 3. Run on Device/Emulator
To install and launch the app on a connected device or running emulator:

```bash
./gradlew installDebug
```

### 4. Build & Sign Release APK / Bundle

To build a signed release APK or bundle, add your keystore path and key alias to `local.properties`:

```properties
sdk.dir=/opt/homebrew/share/android-commandlinetools
RELEASE_STORE_FILE=/path/to/your/release.jks
RELEASE_KEY_ALIAS=your_key_alias
```

Then run the build command passing your keystore password via environment variable or `-P` flag:

```bash
# Build Signed Release APK
KEYSTORE_PASSWORD="your_keystore_password" ./gradlew assembleRelease

# Build Signed App Bundle (.aab) for Google Play
KEYSTORE_PASSWORD="your_keystore_password" ./gradlew bundleRelease
```

**Output locations:**
- **Signed APK:** `app/build/outputs/apk/release/app-release.apk`
- **Signed App Bundle:** `app/build/outputs/bundle/release/app-release.aab`

### 5. Other Useful Commands
- **Clean the build**: `./gradlew clean`
- **Check Linting**: `./gradlew lint`

## Project Structure
- `data/`: Contains the `IpService` (network), `Logger` (storage), and `AppRepository`.
- `ui/`: Contains the `MainActivity` (Compose UI) and `MainViewModel`.
