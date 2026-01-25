# Changelog

All notable changes to Hytale Toolkit will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.3] - 2025-01-24

### Fixed
- Database download not working in standalone executables (was using wrong Python path)
- CLI tools installation check failing in standalone executables (was using exe path instead of Python)
- CLI tools pip install failing in standalone executables (same issue)
- Setup module import now explicitly skipped when running as bundled exe (with documentation)

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
