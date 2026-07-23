# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0] - 2026-07-23

### Changed
- **Standards Update:** Introduced Gradle Version Catalog (`libs.versions.toml`) and updated Gradle wrapper to 8.13.

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
