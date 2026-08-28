# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.3.0] - 2026-08-28

### Added
- **Log Actions:** Added copy-to-clipboard and per-entry delete actions with an overflow menu next to every log entry.
- **Settings Dialog:** Redesigned the settings pane with a modern Material 3 layout, categorized sections, interactive preset chips with active endpoint indicators, real-time URL validation, and an app information card.
- **Localization:** Added localized strings for log actions and the redesigned settings pane across all supported locales (de, es, fr, ja, ko, ru, zh).
- **Per-App Language Preferences:** Configured Android system locale config (`locales_config.xml`) for per-app language settings in Android 13+.

### Changed
- **UI Architecture:** Extracted settings interface into a dedicated, modular `SettingsDialog` composable.
- **Dependencies Updated:** Upgraded Android Gradle Plugin (AGP) to 8.13.0 and AndroidX Core KTX to 1.18.0.

## [1.2.0] - 2026-08-04

### Added
- **Release Automation:** Support for release keystore signing via Gradle properties/env vars and GitHub Actions workflow to publish signed APK and AAB artifacts.
- **Build Performance:** Enabled Gradle configuration caching.

### Changed
- **Dependency Management:** Migrated build dependencies to standard Gradle Version Catalog (`libs.versions.toml`).
- **Dependencies Updated:** Upgraded Gradle wrapper to 8.13, Kotlin to 2.1.20, Compose BOM to 2026.06.01, and Activity Compose to 1.13.0.

## [1.1.0] - 2026-02-14

### Added
- **Theming:** Added support for Light and Dark themes with a persistent toggle switch.
- **IP Sources:** Added ability to configure a custom service URL with several popular presets.
- **UI:** Improved layout to prevent overlap with the system status bar and modernized the Settings dialog.
- **Reliability:** Added error handling for invalid URLs to prevent crashes.

## [1.0.0] - 2026-01-31

### Added
- **IP Info Fetching:** Retrieve IP details from `ipinfo.io`.
- **Persistent Logging:** Automatically logs all requests with timestamps to a local file.
- **Log Management:** View and clear logs directly within the app.
- **UI:** Clean, Material Design 3 interface built with Jetpack Compose.
