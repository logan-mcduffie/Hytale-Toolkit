#!/usr/bin/env python3
"""
Build and release script for Hytale Toolkit.

Creates standalone executables and publishes GitHub releases.

Usage:
    python build-tools.py              # Build for current platform
    python build-tools.py --clean      # Clean build directories first
    python build-tools.py --release    # Build and create GitHub release
"""

import argparse
import os
import platform
import re
import shutil
import subprocess
import sys
from datetime import datetime
from pathlib import Path

SCRIPT_DIR = Path(__file__).parent.resolve()
BUILD_DIR = SCRIPT_DIR / "build"
DIST_DIR = SCRIPT_DIR / "dist"
VERSION_FILE = SCRIPT_DIR / "VERSION"
CHANGELOG_FILE = SCRIPT_DIR / "CHANGELOG.md"
ICON_FILE = SCRIPT_DIR / ".github" / "logo-transparent.png"

# Tools to build
TOOLS = [
    {
        "name": "hytale-setup",
        "script": SCRIPT_DIR / "hytale-rag" / "setup_gui_pyqt.py",
        "icon": ICON_FILE,
        "windowed": True,  # No console window (GUI app)
        "hidden_imports": [
            "PyQt6",
            "PyQt6.QtWidgets",
            "PyQt6.QtCore",
            "PyQt6.QtGui",
        ],
        "datas": [
            (str(SCRIPT_DIR / ".github" / "logo-transparent.png"), ".github"),
        ],
    },
]


def get_version() -> str:
    """Read version from VERSION file."""
    if VERSION_FILE.exists():
        return VERSION_FILE.read_text().strip()
    return "0.0.0"


def set_version(version: str):
    """Write version to VERSION file."""
    VERSION_FILE.write_text(f"{version}\n")


def get_latest_changelog() -> str:
    """Extract the latest version's changelog entry."""
    if not CHANGELOG_FILE.exists():
        return "No changelog available."

    content = CHANGELOG_FILE.read_text()

    # Find the first version section (after the header)
    # Pattern matches ## [x.x.x] - date and captures until next ## [ or end
    pattern = r'## \[[\d.]+\][^\n]*\n(.*?)(?=\n## \[|\Z)'
    match = re.search(pattern, content, re.DOTALL)

    if match:
        return match.group(0).strip()
    return "No changelog entry found."


def get_platform_suffix() -> str:
    """Get platform-specific suffix for executables."""
    system = platform.system().lower()
    machine = platform.machine().lower()

    # Use friendly OS names
    if system == "darwin":
        system = "macos"

    if machine in ("x86_64", "amd64"):
        arch = "x64"
    elif machine in ("aarch64", "arm64"):
        arch = "arm64"
    else:
        arch = machine

    return f"{system}-{arch}"


def clean_build():
    """Clean build directories."""
    print("Cleaning build directories...")
    for dir_path in [BUILD_DIR, DIST_DIR]:
        if dir_path.exists():
            shutil.rmtree(dir_path)
            print(f"  Removed {dir_path}")
    print()


def build_tool(tool: dict, version: str) -> Path | None:
    """Build a single tool with PyInstaller. Returns output path or None."""
    name = tool["name"]
    script = tool["script"]

    print(f"Building {name} v{version}...")
    print(f"  Script: {script}")

    if not script.exists():
        print(f"  ERROR: Script not found: {script}")
        return None

    # Build PyInstaller command
    cmd = [
        sys.executable, "-m", "PyInstaller",
        "--onefile",
        "--name", name,
        "--distpath", str(DIST_DIR),
        "--workpath", str(BUILD_DIR / name),
        "--specpath", str(BUILD_DIR),
        "--noconfirm",
    ]

    # Add windowed flag for GUI apps (no console window)
    if tool.get("windowed"):
        cmd.append("--windowed")

    # Add icon if specified and exists
    icon = tool.get("icon")
    if icon and Path(icon).exists():
        # Convert PNG to ICO for Windows if needed
        if platform.system() == "Windows" and str(icon).endswith(".png"):
            ico_path = convert_png_to_ico(icon)
            if ico_path:
                cmd.extend(["--icon", str(ico_path)])
        else:
            cmd.extend(["--icon", str(icon)])

    # Add hidden imports
    for hidden in tool.get("hidden_imports", []):
        cmd.extend(["--hidden-import", hidden])

    # Add data files
    for src, dst in tool.get("datas", []):
        if Path(src).exists():
            cmd.extend(["--add-data", f"{src}{os.pathsep}{dst}"])

    # Add VERSION file
    cmd.extend(["--add-data", f"{VERSION_FILE}{os.pathsep}."])

    # Add the script
    cmd.append(str(script))

    print(f"  Running PyInstaller...")

    try:
        result = subprocess.run(cmd, capture_output=True, text=True)
        if result.returncode != 0:
            print(f"  ERROR: Build failed")
            print(result.stderr[-2000:] if len(result.stderr) > 2000 else result.stderr)
            return None

        # Get output path
        suffix = ".exe" if platform.system() == "Windows" else ""
        output = DIST_DIR / f"{name}{suffix}"

        if output.exists():
            size_mb = output.stat().st_size / (1024 * 1024)
            print(f"  Success: {output.name} ({size_mb:.1f} MB)")
            return output
        else:
            print(f"  ERROR: Output not found: {output}")
            return None

    except Exception as e:
        print(f"  ERROR: {e}")
        return None


def convert_png_to_ico(png_path: Path) -> Path | None:
    """Convert PNG to ICO for Windows. Returns ICO path or None."""
    try:
        from PIL import Image
        ico_path = BUILD_DIR / "icon.ico"
        BUILD_DIR.mkdir(parents=True, exist_ok=True)

        img = Image.open(png_path).convert("RGBA")

        # Create resized versions for ICO (must resize, not just specify sizes)
        sizes = [256, 128, 64, 48, 32, 16]
        icons = []
        for size in sizes:
            resized = img.resize((size, size), Image.Resampling.LANCZOS)
            icons.append(resized)

        # Save the largest as base, with all sizes
        icons[0].save(ico_path, format='ICO', append_images=icons[1:])
        return ico_path
    except Exception as e:
        print(f"  Warning: Could not convert icon: {e}")
        return None


def rename_for_release(outputs: list[Path], version: str) -> list[Path]:
    """Rename outputs with version and platform suffix for release."""
    platform_suffix = get_platform_suffix()
    renamed = []

    for output in outputs:
        suffix = output.suffix  # .exe or empty
        new_name = f"{output.stem}-{version}-{platform_suffix}{suffix}"
        new_path = output.parent / new_name

        if new_path.exists():
            new_path.unlink()
        output.rename(new_path)
        renamed.append(new_path)
        print(f"  {output.name} -> {new_name}")

    return renamed


def create_github_release(version: str, assets: list[Path], changelog: str) -> bool:
    """Create a GitHub release using gh CLI."""
    print(f"\nCreating GitHub release v{version}...")

    # On Windows, use shell=True to find gh in PATH
    use_shell = platform.system() == "Windows"

    # Check if gh is available
    try:
        subprocess.run(["gh", "--version"], capture_output=True, check=True, shell=use_shell)
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("  ERROR: GitHub CLI (gh) not found or not authenticated")
        print("  Install: https://cli.github.com/")
        print("  Then run: gh auth login")
        return False

    # Check if tag already exists
    result = subprocess.run(
        ["gh", "release", "view", f"v{version}"],
        capture_output=True,
        cwd=SCRIPT_DIR,
        shell=use_shell
    )
    if result.returncode == 0:
        print(f"  ERROR: Release v{version} already exists")
        return False

    # Create release
    cmd = [
        "gh", "release", "create", f"v{version}",
        "--title", f"Hytale Toolkit v{version}",
        "--notes", changelog,
    ]

    # Add assets
    for asset in assets:
        if asset.exists():
            cmd.append(str(asset))

    try:
        result = subprocess.run(cmd, capture_output=True, text=True, cwd=SCRIPT_DIR, shell=use_shell)
        if result.returncode == 0:
            print(f"  Release created: https://github.com/logan-mcduffie/Hytale-Toolkit/releases/tag/v{version}")
            return True
        else:
            print(f"  ERROR: {result.stderr}")
            return False
    except Exception as e:
        print(f"  ERROR: {e}")
        return False


def prompt_version_bump(current: str) -> str:
    """Prompt user for new version."""
    print(f"\nCurrent version: {current}")
    parts = current.split(".")

    if len(parts) == 3:
        major, minor, patch = int(parts[0]), int(parts[1]), int(parts[2])
        suggestions = [
            f"{major}.{minor}.{patch + 1}",  # Patch
            f"{major}.{minor + 1}.0",         # Minor
            f"{major + 1}.0.0",               # Major
        ]
        print(f"  Suggestions: {suggestions[0]} (patch), {suggestions[1]} (minor), {suggestions[2]} (major)")

    new_version = input(f"Enter new version [{current}]: ").strip()
    return new_version if new_version else current


def prompt_changelog() -> str:
    """Prompt user for changelog entries."""
    print("\nEnter changelog items (empty line to finish):")
    print("  Prefix with 'add:', 'fix:', 'change:', or 'remove:' for categorization")
    print("  Example: add: Dark mode support")
    print()

    added = []
    fixed = []
    changed = []
    removed = []
    other = []

    while True:
        line = input("  > ").strip()
        if not line:
            break

        lower = line.lower()
        if lower.startswith("add:"):
            added.append(line[4:].strip())
        elif lower.startswith("fix:"):
            fixed.append(line[4:].strip())
        elif lower.startswith("change:"):
            changed.append(line[7:].strip())
        elif lower.startswith("remove:"):
            removed.append(line[7:].strip())
        else:
            other.append(line)

    # Build markdown
    sections = []
    if added:
        sections.append("### Added\n" + "\n".join(f"- {item}" for item in added))
    if fixed:
        sections.append("### Fixed\n" + "\n".join(f"- {item}" for item in fixed))
    if changed:
        sections.append("### Changed\n" + "\n".join(f"- {item}" for item in changed))
    if removed:
        sections.append("### Removed\n" + "\n".join(f"- {item}" for item in removed))
    if other:
        sections.append("\n".join(f"- {item}" for item in other))

    return "\n\n".join(sections) if sections else "- Bug fixes and improvements"


def update_changelog(version: str, entries: str):
    """Prepend new version entry to CHANGELOG.md."""
    today = datetime.now().strftime("%Y-%m-%d")

    new_entry = f"## [{version}] - {today}\n\n{entries}\n\n"

    if CHANGELOG_FILE.exists():
        content = CHANGELOG_FILE.read_text()
        # Find where to insert (after the header section)
        header_end = content.find("\n## [")
        if header_end == -1:
            # No existing versions, append after header
            content = content.rstrip() + "\n\n" + new_entry
        else:
            # Insert before first version
            content = content[:header_end + 1] + new_entry + content[header_end + 1:]
    else:
        content = f"# Changelog\n\n{new_entry}"

    CHANGELOG_FILE.write_text(content)
    print(f"  Updated {CHANGELOG_FILE.name}")


def main():
    parser = argparse.ArgumentParser(description="Build Hytale Toolkit executables")
    parser.add_argument("--clean", action="store_true", help="Clean build directories first")
    parser.add_argument("--release", action="store_true", help="Build and create GitHub release")
    parser.add_argument("--tool", type=str, help="Build specific tool only")
    parser.add_argument("--skip-build", action="store_true", help="Skip build, just release existing files")
    args = parser.parse_args()

    print()
    print("=" * 60)
    print("  Hytale Toolkit - Build & Release")
    print("=" * 60)
    print()
    print(f"Platform: {get_platform_suffix()}")
    print(f"Python: {sys.version.split()[0]}")

    version = get_version()
    print(f"Version: {version}")
    print()

    # Release mode: prompt for version and changelog
    if args.release:
        new_version = prompt_version_bump(version)
        if new_version != version:
            set_version(new_version)
            version = new_version
            print(f"\n  Version updated to {version}")

        changelog_entries = prompt_changelog()
        update_changelog(version, changelog_entries)

        # Get the full changelog entry for release notes
        changelog = get_latest_changelog()
        print(f"\nRelease notes:\n{'-' * 40}\n{changelog}\n{'-' * 40}")

    if not args.skip_build:
        # Check for PyInstaller
        try:
            import PyInstaller
            print(f"PyInstaller: {PyInstaller.__version__}")
        except ImportError:
            print("ERROR: PyInstaller not installed")
            print("Run: pip install pyinstaller")
            return 1

        print()

        if args.clean:
            clean_build()

        # Build tools
        tools_to_build = TOOLS
        if args.tool:
            tools_to_build = [t for t in TOOLS if t["name"] == args.tool]
            if not tools_to_build:
                print(f"ERROR: Unknown tool: {args.tool}")
                print(f"Available: {', '.join(t['name'] for t in TOOLS)}")
                return 1

        outputs = []
        for tool in tools_to_build:
            output = build_tool(tool, version)
            if output:
                outputs.append(output)
            print()

        print("=" * 60)
        print(f"  Build Complete: {len(outputs)}/{len(tools_to_build)} tools")
        print("=" * 60)
        print()

        if len(outputs) != len(tools_to_build):
            print("ERROR: Some builds failed")
            return 1

        # Rename for release
        print("Renaming for release...")
        outputs = rename_for_release(outputs, version)
        print()
    else:
        # Find existing release files
        outputs = list(DIST_DIR.glob(f"*-{version}-*"))
        if not outputs:
            print(f"ERROR: No release files found for v{version}")
            return 1

    print(f"Release files in: {DIST_DIR}")
    for output in outputs:
        print(f"  {output.name}")

    # Create GitHub release if requested
    if args.release:
        print()
        confirm = input("Create GitHub release? [y/N]: ").strip().lower()
        if confirm == "y":
            changelog = get_latest_changelog()
            if create_github_release(version, outputs, changelog):
                print("\nRelease complete!")
            else:
                print("\nRelease failed")
                return 1

    return 0


if __name__ == "__main__":
    sys.exit(main())
