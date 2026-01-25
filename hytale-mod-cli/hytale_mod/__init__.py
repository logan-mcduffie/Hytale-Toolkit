"""Hytale Mod CLI - Create new Hytale mod projects."""

from pathlib import Path as _Path

# Read version from central VERSION file
_version_file = _Path(__file__).parent.parent.parent / "VERSION"
__version__ = _version_file.read_text().strip() if _version_file.exists() else "0.0.0"
