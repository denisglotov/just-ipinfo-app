# JustIpInfo

A minimalistic, Clean Code Android application that fetches IP information from `ipinfo.io` and
maintains a timestamped log file.

## Features
- **Request IP Info**: Fetches JSON data from `ipinfo.io` via GET request.
- **Persistent Logging**: Saves every request result (or error) with a
  timestamp to an internal log file.
- **Log Management**: Immediate visibility of logs in the UI with a "Clear" function.
- **Clean Architecture**: Follows MVVM principles with manual dependency injection for minimal
  boilerplate.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Networking**: OkHttp
- **Build System**: Gradle (Kotlin DSL)

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
