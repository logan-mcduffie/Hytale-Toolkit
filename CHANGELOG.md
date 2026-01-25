# Changelog

All notable changes to Hytale Toolkit will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.13] - 2025-01-25

### Changed
- Updated Kotlin version from 2.1.0 to 2.3.0 for generated mod projects

## [1.0.12] - 2025-01-25

### Fixed
- CLI tools installation now automatically adds Python Scripts directory to user PATH on Windows
- Fixes "hytale-mod is not recognized" error in new terminal windows after installation

## [1.0.11] - 2025-01-25

### Fixed
- Maven wrapper scripts (`mvnw`, `mvnw.cmd`) now embed the toolkit's JDK path directly
- Fixes "java not found" errors even when running Maven wrapper from command line
- Added `<hytale.jdk.path>` property to generated `pom.xml`

## [1.0.10] - 2025-01-25

### Fixed
- VS Code Gemini and Codex extensions now properly configure MCP (share VS Code settings.json)
- JetBrains Codex extension now properly configures MCP (shares JetBrains config)
- Removed false "Not yet supported" messages for supported providers
- Maven `run-server` profile now copies JAR to `Server/mods` (was incorrectly using `Server/plugins`)

## [1.0.9] - 2025-01-25

### Added
- Mod IDs now support underscores (e.g., `my_mod`) in addition to hyphens

### Fixed
- CLI now finds decompiled source from standard toolkit location (`%LOCALAPPDATA%\Hytale-Toolkit`)
- Fixes "Vineflower not found" error when CLI is installed separately from toolkit

## [1.0.8] - 2025-01-25

### Fixed
- Maven projects now use toolkit's JDK path instead of system PATH for `run-server`
- `hytale-mod init` embeds detected JDK path in generated `pom.xml`
- Fixes "java not found" errors when system PATH has broken/missing Java

## [1.0.7] - 2025-01-25

### Added
- Kotlin language support in `hytale-mod init` CLI
- Interactive language selection prompt (Java or Kotlin)
- `--language` / `-l` CLI flag for non-interactive mode
- Kotlin project templates for both Maven and Gradle build systems
- Idiomatic Kotlin main class template with proper syntax

### Fixed
- JDK detection now searches `~/.jdks/` folder where IntelliJ installs JDKs
- Changed "semver" to "SemVer" in header so it doesn't look like a typo for "server"
- CLI tool installation now validates hytale-mod-cli directory exists before installing
- Shows clear error message if CLI directory is missing (suggests git pull or re-download)
- Improved UI responsiveness when clicking action buttons (decompile, javadocs, integration)
- Buttons and views now update immediately before heavy operations begin

## [1.0.6] - 2025-01-25

### Fixed
- Database download now uses inline CDN logic instead of importing from toolkit path
- Fixes "Could not find lancedb in latest release" error when toolkit has old setup.py

## [1.0.5] - 2025-01-25

### Added
- Database page now shows Hytale version info (fetched from CDN manifest)
- Shows installed database version when existing database is found
- Displays update warning when newer database version is available

## [1.0.4] - 2025-01-24

### Fixed
- MCP integration now works in standalone executables (extracted to bundled mcp_config module)
- Back button now works on Integration results page when there are errors

### Changed
- Extracted MCP configuration functions to shared `mcp_config.py` module for better code reuse

## [1.0.3] - 2025-01-24

### Fixed
- Database download not working in standalone executables (was using wrong Python path)
- CLI tools installation check failing in standalone executables (was using exe path instead of Python)
- CLI tools pip install failing in standalone executables (same issue)

### Changed
- Renamed internal `SCRIPT_DIR` to `BUNDLE_DIR` for bundled assets to avoid confusion with import path

## [1.0.2] - 2025-01-24

### Fixed
- Background image and icons not bundled in standalone executables

## [1.0.1] - 2025-01-24

### Fixed
- Version detection in standalone executables (was showing 0.0.0)
- App icon not appearing in standalone executables
- Update dialog styling (removed gray backgrounds, made changelog scrollable)
- Redundant platform naming in release filenames

### Changed
- Simplified release assets to only include setup wizard
- Cleaned up macOS build artifacts from releases

## [1.0.0] - 2025-01-24

### Added
- PyQt6-based setup wizard with modern dark UI
- Decompilation of Hytale client and server JARs using Vineflower
- Javadocs generation from decompiled source
- Vector database indexing for AI-powered code search
- MCP server integration for Claude, Cursor, and GitHub Copilot
- `hytale-mod init` CLI tool for scaffolding new mod projects
- Support for Voyage AI and Ollama embedding providers

### Notes
- Initial public release
