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
- **UI Framework**: Jetpack Compose (Material 3, Edge-to-Edge)
- **Networking**: OkHttp
- **Build System**: Gradle (Kotlin DSL, Version Catalog `libs.versions.toml`)
- **Testing**: JUnit 4, Kotlinx Coroutines Test

## Prerequisites
- **JDK 17** or higher.
- **Android SDK** installed on your system.

## Build Instructions (Terminal)

### 1. Configure the SDK Path
Make sure you have Android SDK inatalled and `ANDROID_HOME` exported. 
Alternatively explicitly specify the path to it. 

```bash
echo "sdk.dir=/opt/homebrew/share/android-commandlinetools" > local.properties
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

### 4. Other Useful Commands
- **Clean the build**: `./gradlew clean`
- **Run Unit Tests**: `./gradlew test`
- **Check Linting**: `./gradlew lint`

## Project Structure
- `data/`: Contains the `IpService` (network), `Logger` (storage), and `AppRepository`.
- `ui/`: Contains the `MainActivity` (Compose UI) and `MainViewModel`.
