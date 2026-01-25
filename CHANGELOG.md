# Changelog

All notable changes to Hytale Toolkit will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.7] - 2025-01-25

### Fixed
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
