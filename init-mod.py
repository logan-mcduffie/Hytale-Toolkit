#!/usr/bin/env python3
"""
Hytale Mod Initialization Wizard

A comprehensive setup script that guides you through creating a new Hytale mod project:
1. Selecting where to create the mod
2. Configuring mod metadata (name, author, version, etc.)
3. Setting up Gradle with HytaleGradle plugin
4. Configuring IDE integration (IntelliJ / VS Code)
5. Optionally initializing git repository
"""

import json
import os
import platform
import re
import shutil
import subprocess
import sys
import urllib.request
from pathlib import Path

# Add tools directory to path for shared utilities
sys.path.insert(0, str(Path(__file__).parent / "tools"))
from logger import setup_logging, log_command, log_exception, log_section

# Global logger (initialized in main)
log = None
log_file = None

# ============================================================================
#  Configuration
# ============================================================================

SCRIPT_DIR = Path(__file__).parent.resolve()
DECOMPILED_DIR = SCRIPT_DIR / "decompiled"
VINEFLOWER_JAR = SCRIPT_DIR / "tools" / "vineflower.jar"

# Required contents of a valid Hytale installation
REQUIRED_CONTENTS = ["Client", "Server", "Assets.zip"]

# Gradle wrapper version
GRADLE_VERSION = "8.14"
GRADLE_WRAPPER_URL = f"https://services.gradle.org/distributions/gradle-{GRADLE_VERSION}-bin.zip"

# License templates
LICENSES = {
    "MIT": """MIT License

Copyright (c) {year} {author}

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
""",
    "Apache-2.0": """Apache License
Version 2.0, January 2004
http://www.apache.org/licenses/

Copyright {year} {author}

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
""",
    "GPL-3.0": """GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (c) {year} {author}

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
""",
    "None": None,
}


# ============================================================================
#  Utilities
# ============================================================================

def clear_screen():
    """Clear the terminal screen."""
    os.system('cls' if platform.system() == 'Windows' else 'clear')


def print_header(title: str):
    """Print a formatted section header."""
    print()
    print("=" * 60)
    print(f"  {title}")
    print("=" * 60)
    print()


def print_step(step: int, total: int, title: str):
    """Print a step header."""
    print()
    print("-" * 60)
    print(f"  Step {step}/{total}: {title}")
    print("-" * 60)
    print()


def prompt_yes_no(question: str, default: bool = True) -> bool:
    """Prompt for yes/no input with a default."""
    hint = "[Y/n]" if default else "[y/N]"
    while True:
        response = input(f"  {question} {hint}: ").strip().lower()
        if response == "":
            return default
        if response in ("y", "yes"):
            return True
        if response in ("n", "no"):
            return False
        print("  Please enter 'y' or 'n'.")


def prompt_choice(options: list[tuple[str, str]], prompt_text: str = "Select an option") -> int:
    """Prompt user to select from numbered options. Returns 0-based index."""
    for i, (label, desc) in enumerate(options, 1):
        print(f"  [{i}] {label}")
        if desc:
            for line in desc.split("\n"):
                print(f"      {line}")
        print()

    while True:
        try:
            choice = input(f"  {prompt_text} [1-{len(options)}]: ").strip()
            idx = int(choice) - 1
            if 0 <= idx < len(options):
                return idx
        except ValueError:
            pass
        print(f"  Please enter a number between 1 and {len(options)}.")


def prompt_multi_choice(options: list[tuple[str, str]], prompt_text: str = "Select options") -> list[int]:
    """Prompt user to select multiple options. Returns list of 0-based indices."""
    for i, (label, desc) in enumerate(options, 1):
        print(f"  [{i}] {label}")
        if desc:
            for line in desc.split("\n"):
                print(f"      {line}")
        print()

    print(f"  [A] All of the above")
    print(f"  [0] None")
    print()

    while True:
        response = input(f"  {prompt_text} (e.g., 1,2 or A for all): ").strip().lower()

        if response == "0" or response == "":
            return []

        if response == "a":
            return list(range(len(options)))

        try:
            indices = []
            for part in response.replace(" ", "").split(","):
                idx = int(part) - 1
                if 0 <= idx < len(options):
                    indices.append(idx)
                else:
                    raise ValueError()
            if indices:
                return list(set(indices))
        except ValueError:
            pass

        print(f"  Please enter numbers 1-{len(options)} separated by commas, 'A' for all, or '0' for none.")


def prompt_string(prompt_text: str, default: str = "", validator: callable = None, required: bool = True) -> str:
    """Prompt for string input with optional validation."""
    hint = f" [{default}]" if default else ""
    while True:
        response = input(f"  {prompt_text}{hint}: ").strip()
        if response == "" and default:
            response = default
        if response == "" and required:
            print("  This field is required.")
            continue
        if response == "" and not required:
            return ""
        if validator:
            error = validator(response)
            if error:
                print(f"  {error}")
                continue
        return response


def command_exists(cmd: str) -> bool:
    """Check if a command exists in PATH."""
    if platform.system() == "Windows":
        result = subprocess.run(["where", cmd], capture_output=True, shell=True)
    else:
        result = subprocess.run(["which", cmd], capture_output=True)
    return result.returncode == 0


def run_command(cmd: list[str], cwd: Path = None, env: dict = None, shell: bool = None) -> tuple[int, str]:
    """Run a command and return exit code and output."""
    try:
        use_shell = shell if shell is not None else (platform.system() == "Windows")
        cmd_for_log = cmd
        if use_shell and isinstance(cmd, list):
            cmd = " ".join(cmd)

        merged_env = os.environ.copy()
        if env:
            merged_env.update(env)

        result = subprocess.run(
            cmd, cwd=cwd, capture_output=True, text=True, shell=use_shell, env=merged_env
        )
        output = result.stdout + result.stderr

        # Log the command execution
        if log:
            log_command(log, cmd_for_log, result.returncode, output, cwd)

        return result.returncode, output
    except Exception as e:
        if log:
            log.error(f"Command failed with exception: {e}")
            log_exception(log, "run_command")
        return 1, str(e)


# ============================================================================
#  Validators
# ============================================================================

def validate_mod_id(value: str) -> str | None:
    """Validate mod ID (lowercase, alphanumeric, hyphens only)."""
    if not re.match(r'^[a-z][a-z0-9-]*$', value):
        return "Mod ID must start with a letter and contain only lowercase letters, numbers, and hyphens."
    if len(value) < 2:
        return "Mod ID must be at least 2 characters."
    if len(value) > 64:
        return "Mod ID must be 64 characters or less."
    return None


def validate_group(value: str) -> str | None:
    """Validate Java package group (e.g., com.author)."""
    if not re.match(r'^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)*$', value):
        return "Group must be a valid Java package (e.g., com.yourname). Lowercase letters and numbers only."
    return None


def validate_version(value: str) -> str | None:
    """Validate semantic version."""
    if not re.match(r'^\d+\.\d+\.\d+(-[a-zA-Z0-9]+)?(\+[a-zA-Z0-9]+)?$', value):
        return "Version must be semantic versioning format (e.g., 1.0.0, 1.0.0-beta, 1.0.0+build)."
    return None


def validate_class_name(value: str) -> str | None:
    """Validate Java class name."""
    if not re.match(r'^[A-Z][a-zA-Z0-9]*$', value):
        return "Class name must start with uppercase letter and contain only letters and numbers."
    return None


def validate_email(value: str) -> str | None:
    """Validate email format (loose validation)."""
    if not re.match(r'^[^@]+@[^@]+\.[^@]+$', value):
        return "Please enter a valid email address."
    return None


def validate_url(value: str) -> str | None:
    """Validate URL format."""
    if not re.match(r'^https?://.+', value):
        return "URL must start with http:// or https://"
    return None


# ============================================================================
#  Hytale Detection
# ============================================================================

def detect_hytale_installation() -> str | None:
    """Try to auto-detect the Hytale installation path."""
    system = platform.system()

    # Common installation paths
    if system == "Windows":
        appdata = os.environ.get("APPDATA", "")
        if appdata:
            # Check for Hytale launcher installation
            possible_paths = [
                Path(appdata) / "Hytale Launcher" / "install" / "release" / "package" / "game" / "latest",
                Path(appdata) / "Hytale" / "install" / "release" / "package" / "game" / "latest",
            ]
            for path in possible_paths:
                if path.exists():
                    is_valid, _ = validate_hytale_installation(str(path))
                    if is_valid:
                        return str(path)
    elif system == "Darwin":  # macOS
        home = Path.home()
        possible_paths = [
            home / "Library" / "Application Support" / "Hytale Launcher" / "install" / "release" / "package" / "game" / "latest",
        ]
        for path in possible_paths:
            if path.exists():
                is_valid, _ = validate_hytale_installation(str(path))
                if is_valid:
                    return str(path)
    else:  # Linux
        home = Path.home()
        possible_paths = [
            home / ".local" / "share" / "Hytale Launcher" / "install" / "release" / "package" / "game" / "latest",
        ]
        for path in possible_paths:
            if path.exists():
                is_valid, _ = validate_hytale_installation(str(path))
                if is_valid:
                    return str(path)

    return None


def validate_hytale_installation(folder: str) -> tuple[bool, list[str]]:
    """Validate that a folder is a valid Hytale installation."""
    folder_path = Path(folder)

    if not folder_path.exists():
        return False, ["Folder does not exist"]

    missing = []
    for item in REQUIRED_CONTENTS:
        if not (folder_path / item).exists():
            missing.append(item)

    return len(missing) == 0, missing


def open_folder_picker(title: str = "Select Folder", initial_dir: str = None) -> str | None:
    """Open a native folder picker dialog."""
    try:
        import tkinter as tk
        from tkinter import filedialog

        root = tk.Tk()
        root.withdraw()
        root.attributes('-topmost', True)

        folder = filedialog.askdirectory(title=title, initialdir=initial_dir)
        root.destroy()

        return folder if folder else None
    except ImportError:
        return None


def get_hytale_install_path() -> str | None:
    """Get Hytale install path, auto-detecting or prompting user."""

    # Try auto-detection first
    detected = detect_hytale_installation()
    if detected:
        print(f"  Auto-detected Hytale installation:")
        print(f"    {detected}")
        print()
        if prompt_yes_no("Use this path?", default=True):
            return detected

    # Manual selection
    print()
    print("  Please select your Hytale installation folder.")
    print()
    print("  The folder should contain:")
    print("    - Client/       (client files)")
    print("    - Server/       (HytaleServer.jar)")
    print("    - Assets.zip    (game assets)")
    print()

    max_attempts = 3
    for attempt in range(max_attempts):
        if attempt > 0:
            print(f"\n  Attempt {attempt + 1}/{max_attempts}")

        print("  A folder picker will open. If it doesn't appear, you can type the path.")
        input("  Press Enter to open folder picker...")

        # Start at detected location or home
        initial = detected if detected else str(Path.home())
        folder = open_folder_picker("Select Hytale Installation Folder", initial_dir=initial)

        if not folder:
            print()
            folder = input("  Enter path manually: ").strip().strip('"').strip("'")

        if not folder:
            print("  No path provided.")
            continue

        is_valid, missing = validate_hytale_installation(folder)

        if is_valid:
            print(f"\n  Valid installation found!")
            return folder
        else:
            print(f"\n  ERROR: Invalid Hytale installation folder.")
            print(f"  Missing: {', '.join(missing)}")

            if attempt < max_attempts - 1:
                if not prompt_yes_no("Try again?", default=True):
                    return None

    print("\n  Maximum attempts reached.")
    return None


# ============================================================================
#  Project Generation
# ============================================================================

def to_class_name(mod_id: str) -> str:
    """Convert mod-id to ClassName format."""
    # Split by hyphens, capitalize each part
    parts = mod_id.split("-")
    return "".join(part.capitalize() for part in parts)


def generate_main_class(mod_config: dict) -> str:
    """Generate the main plugin Java class."""
    package = f"{mod_config['group']}.{mod_config['name'].replace('-', '')}"
    class_name = mod_config['main_class']

    return f'''package {package};

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import java.util.logging.Level;
import javax.annotation.Nonnull;

/**
 * Main entry point for the {mod_config['display_name']} plugin.
 */
public class {class_name} extends JavaPlugin {{

    public {class_name}(@Nonnull JavaPluginInit init) {{
        super(init);
    }}

    @Override
    protected void setup() {{
        // Called during plugin setup phase
    }}

    @Override
    protected void start() {{
        // Called when the plugin is enabled
        getLogger().at(Level.INFO).log("{mod_config['display_name']} has been enabled!");
    }}

    @Override
    protected void shutdown() {{
        // Called when the plugin is disabled
        getLogger().at(Level.INFO).log("{mod_config['display_name']} has been disabled!");
    }}
}}
'''


def generate_manifest(mod_config: dict) -> dict:
    """Generate the manifest.json content."""
    manifest = {
        "Group": mod_config['group'],
        "Name": mod_config['name'],
        "Version": mod_config['version'],
        "Main": f"{mod_config['group']}.{mod_config['name'].replace('-', '')}.{mod_config['main_class']}",
        "ServerVersion": mod_config.get('server_version', '*'),
        "Authors": [{"Name": mod_config['author_name']}],
    }

    if mod_config.get('description'):
        manifest['Description'] = mod_config['description']

    if mod_config.get('author_email'):
        manifest['Authors'][0]['Email'] = mod_config['author_email']

    if mod_config.get('author_url'):
        manifest['Authors'][0]['Url'] = mod_config['author_url']

    return manifest


def generate_build_gradle(mod_config: dict) -> str:
    """Generate build.gradle.kts content."""
    return f'''plugins {{
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("app.ultradev.hytalegradle") version "1.6.7"
}}

group = "{mod_config['group']}"
version = "{mod_config['version']}"

java {{
    toolchain {{
        languageVersion.set(JavaLanguageVersion.of(24))
    }}
}}

repositories {{
    mavenCentral()
}}

dependencies {{
    // HytaleServer.jar - provided at runtime by the server
    compileOnly(files("${{property("hytaleInstallPath")}}/Server/HytaleServer.jar"))

    // Add your dependencies here
    // implementation("com.example:library:1.0.0")
}}

hytale {{
    // Enable operator privileges for testing
    allowOp.set(true)

    // Use release patchline (options: "release", "pre-release")
    patchline.set("release")
}}

tasks.shadowJar {{
    archiveClassifier.set("")
}}
'''


def generate_settings_gradle(mod_config: dict) -> str:
    """Generate settings.gradle.kts content."""
    return f'''pluginManagement {{
    repositories {{
        gradlePluginPortal()
        maven("https://mvn.ultradev.app/snapshots")
    }}
}}

rootProject.name = "{mod_config['name']}"
'''


def generate_gradle_properties(mod_config: dict, hytale_path: str, toolkit_path: str, jdk24_path: str = None) -> str:
    """Generate gradle.properties content."""
    # Normalize paths for properties file (use forward slashes)
    hytale_path_normalized = hytale_path.replace("\\", "/")
    toolkit_path_normalized = toolkit_path.replace("\\", "/")

    content = f'''# Project properties
org.gradle.jvmargs=-Xmx2g

# Hytale paths
hytaleInstallPath={hytale_path_normalized}
hytaleToolkitPath={toolkit_path_normalized}
'''

    # Add JDK 24 path for running Gradle (if available)
    if jdk24_path:
        jdk24_normalized = jdk24_path.replace("\\", "/")
        content += f'''
# JDK for running Gradle and compilation
org.gradle.java.home={jdk24_normalized}
'''

    return content


def generate_gitignore(ignore_ide_configs: bool = True) -> str:
    """Generate .gitignore content."""
    content = '''# Gradle
.gradle/
build/
out/

'''
    if ignore_ide_configs:
        content += '''# IDE
.idea/
*.iml
.vscode/
*.code-workspace

'''
    content += '''# OS
.DS_Store
Thumbs.db

# Java
*.class
*.jar
*.war
*.ear

# Logs
*.log
logs/

# Runtime
run/
'''
    return content


def generate_readme(mod_config: dict) -> str:
    """Generate README.md content."""
    return f'''# {mod_config['display_name']}

{mod_config.get('description', 'A Hytale server plugin.')}

## Building

```bash
./gradlew build
```

## Development

### Run Server with Plugin

```bash
./gradlew runServer
```

### Install Plugin (Hot Reload)

```bash
./gradlew installPlugin
```

### Generate Decompiled Sources

```bash
./gradlew generateSources
```

## Requirements

- **JDK 24** - Required for Gradle and compilation
- Gradle {GRADLE_VERSION} or newer (included via wrapper)
- Hytale Server installation

The project is configured to automatically use JDK 24 via `gradle.properties`.

## License

{mod_config.get('license', 'MIT')}

## Author

{mod_config['author_name']}
'''


def generate_intellij_config(mod_config: dict, project_path: Path, toolkit_path: str) -> None:
    """Generate IntelliJ IDEA configuration."""
    idea_dir = project_path / ".idea"
    idea_dir.mkdir(exist_ok=True)

    # Create misc.xml for JDK configuration
    misc_xml = f'''<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectRootManager" version="2" languageLevel="JDK_25" default="true" project-jdk-name="25" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/build/classes" />
  </component>
</project>
'''
    (idea_dir / "misc.xml").write_text(misc_xml)

    # Create a library entry for decompiled sources
    libraries_dir = idea_dir / "libraries"
    libraries_dir.mkdir(exist_ok=True)

    toolkit_normalized = toolkit_path.replace("\\", "/")
    decompiled_lib = f'''<component name="libraryTable">
  <library name="Hytale Decompiled Sources">
    <CLASSES />
    <JAVADOC />
    <SOURCES>
      <root url="file://{toolkit_normalized}/decompiled" />
    </SOURCES>
  </library>
</component>
'''
    (libraries_dir / "Hytale_Decompiled_Sources.xml").write_text(decompiled_lib)

    print("    Created .idea/ configuration")


def detect_jdk_path(version: int) -> str | None:
    """Try to detect JDK installation path for a specific version."""
    system = platform.system()
    version_str = str(version)

    if system == "Windows":
        possible_paths = [
            Path(f"C:/Program Files/Java/jdk-{version}"),
            Path(f"C:/Program Files/Eclipse Adoptium/jdk-{version}"),
        ]
        # Check JAVA_HOME if it contains the version
        java_home = os.environ.get("JAVA_HOME", "")
        if version_str in java_home:
            possible_paths.insert(0, Path(java_home))

        # Also check for versioned directories like jdk-25.0.1
        java_dir = Path("C:/Program Files/Java")
        if java_dir.exists():
            for item in java_dir.iterdir():
                if item.is_dir() and item.name.startswith(f"jdk-{version}"):
                    possible_paths.insert(0, item)
        adoptium_dir = Path("C:/Program Files/Eclipse Adoptium")
        if adoptium_dir.exists():
            for item in adoptium_dir.iterdir():
                if item.is_dir() and f"-{version}" in item.name:
                    possible_paths.insert(0, item)

        # Check local .jdks folder (where we download to)
        local_jdks = SCRIPT_DIR / ".jdks"
        if local_jdks.exists():
            for item in local_jdks.iterdir():
                if item.is_dir() and f"jdk-{version}" in item.name:
                    possible_paths.insert(0, item)

    elif system == "Darwin":  # macOS
        possible_paths = [
            Path(f"/Library/Java/JavaVirtualMachines/jdk-{version}.jdk/Contents/Home"),
            Path(f"/Library/Java/JavaVirtualMachines/temurin-{version}.jdk/Contents/Home"),
        ]
        java_home = os.environ.get("JAVA_HOME", "")
        if version_str in java_home:
            possible_paths.insert(0, Path(java_home))

        # Check local .jdks folder
        local_jdks = SCRIPT_DIR / ".jdks"
        if local_jdks.exists():
            for item in local_jdks.iterdir():
                if item.is_dir() and f"jdk-{version}" in item.name:
                    # macOS extracted JDKs have Contents/Home structure
                    home_path = item / "Contents" / "Home"
                    if home_path.exists():
                        possible_paths.insert(0, home_path)
                    else:
                        possible_paths.insert(0, item)

    else:  # Linux
        possible_paths = [
            Path(f"/usr/lib/jvm/java-{version}-openjdk"),
            Path(f"/usr/lib/jvm/jdk-{version}"),
            Path(f"/usr/lib/jvm/temurin-{version}"),
        ]
        java_home = os.environ.get("JAVA_HOME", "")
        if version_str in java_home:
            possible_paths.insert(0, Path(java_home))

        # Check local .jdks folder
        local_jdks = SCRIPT_DIR / ".jdks"
        if local_jdks.exists():
            for item in local_jdks.iterdir():
                if item.is_dir() and f"jdk-{version}" in item.name:
                    possible_paths.insert(0, item)

    java_exe = "java.exe" if system == "Windows" else "java"
    for path in possible_paths:
        if path and path.exists() and (path / "bin" / java_exe).exists():
            return str(path)

    return None


def detect_jdk25_path() -> str | None:
    """Try to detect JDK 25 installation path."""
    return detect_jdk_path(25)


def detect_jdk24_path() -> str | None:
    """Try to detect JDK 24 installation path."""
    return detect_jdk_path(24)


def get_adoptium_download_url(version: int) -> tuple[str, str] | None:
    """Get the Adoptium download URL for a specific JDK version.

    Returns (url, filename) or None if not available.
    """
    system = platform.system()
    machine = platform.machine().lower()

    # Map platform to Adoptium API values
    if system == "Windows":
        os_name = "windows"
        archive_type = "zip"
    elif system == "Darwin":
        os_name = "mac"
        archive_type = "tar.gz"
    else:
        os_name = "linux"
        archive_type = "tar.gz"

    # Map architecture
    if machine in ("x86_64", "amd64"):
        arch = "x64"
    elif machine in ("aarch64", "arm64"):
        arch = "aarch64"
    else:
        arch = "x64"  # Default fallback

    # JDK 25 might be early access (ea), JDK 24 should be GA
    release_type = "ea" if version >= 25 else "ga"

    # Try to get the latest release info from Adoptium API
    api_url = f"https://api.adoptium.net/v3/assets/latest/{version}/{release_type}"
    params = f"?architecture={arch}&image_type=jdk&os={os_name}&vendor=eclipse"

    try:
        req = urllib.request.Request(api_url + params)
        req.add_header("Accept", "application/json")

        with urllib.request.urlopen(req, timeout=30) as response:
            data = json.loads(response.read().decode())

            if data and len(data) > 0:
                binary = data[0].get("binary", {})
                package = binary.get("package", {})
                download_url = package.get("link")
                filename = package.get("name")

                if download_url and filename:
                    return (download_url, filename)
    except Exception as e:
        print(f"    Warning: Could not query Adoptium API: {e}")

    return None


def download_and_extract_jdk(version: int, target_dir: Path) -> str | None:
    """Download and extract a JDK from Adoptium.

    Returns the path to the extracted JDK, or None on failure.
    """
    print(f"  Fetching JDK {version} download information...")

    url_info = get_adoptium_download_url(version)
    if not url_info:
        print(f"    ERROR: Could not find JDK {version} download")
        return None

    download_url, filename = url_info
    print(f"    Found: {filename}")

    # Create target directory
    target_dir.mkdir(parents=True, exist_ok=True)
    archive_path = target_dir / filename

    # Download the archive
    print(f"  Downloading JDK {version}...")
    print(f"    This may take a few minutes...")

    try:
        # Download with progress
        def report_progress(block_num, block_size, total_size):
            if total_size > 0:
                downloaded = block_num * block_size
                percent = min(100, downloaded * 100 // total_size)
                mb_downloaded = downloaded / (1024 * 1024)
                mb_total = total_size / (1024 * 1024)
                print(f"\r    Progress: {percent}% ({mb_downloaded:.1f}/{mb_total:.1f} MB)", end="", flush=True)

        urllib.request.urlretrieve(download_url, archive_path, reporthook=report_progress)
        print()  # New line after progress

    except Exception as e:
        print(f"\n    ERROR: Download failed: {e}")
        return None

    # Extract the archive
    print(f"  Extracting JDK {version}...")

    try:
        if filename.endswith(".zip"):
            import zipfile
            with zipfile.ZipFile(archive_path, 'r') as zip_ref:
                zip_ref.extractall(target_dir)
        elif filename.endswith(".tar.gz") or filename.endswith(".tgz"):
            import tarfile
            with tarfile.open(archive_path, 'r:gz') as tar_ref:
                tar_ref.extractall(target_dir)
        else:
            print(f"    ERROR: Unknown archive format: {filename}")
            return None

        # Remove the archive after extraction
        archive_path.unlink()

        # Find the extracted JDK directory
        for item in target_dir.iterdir():
            if item.is_dir() and f"jdk-{version}" in item.name:
                # On macOS, the JDK might be in Contents/Home
                if platform.system() == "Darwin":
                    home_path = item / "Contents" / "Home"
                    if home_path.exists():
                        print(f"    Extracted to: {home_path}")
                        return str(home_path)
                print(f"    Extracted to: {item}")
                return str(item)

        print("    ERROR: Could not find extracted JDK directory")
        return None

    except Exception as e:
        print(f"    ERROR: Extraction failed: {e}")
        return None


def ensure_jdk_installed() -> str | None:
    """Ensure JDK 24 is available.

    Returns jdk24_path or None.
    """
    print("  Checking for JDK 24 installation...")
    print()

    jdk24_path = detect_jdk24_path()

    # Report what we found
    if jdk24_path:
        print(f"    JDK 24: Found at {jdk24_path}")
        print()
        print("  JDK 24 is installed!")
        return jdk24_path

    print("    JDK 24: Not found")
    print()

    options = [
        ("Download JDK 24 automatically", "Downloads from Eclipse Adoptium (Temurin) to a local .jdks folder"),
        ("Install manually", "You'll need to install JDK 24 yourself"),
        ("Skip JDK setup", "Continue without JDK validation (may cause build issues)"),
    ]

    choice = prompt_choice(options, "How would you like to proceed?")

    if choice == 0:
        # Download JDK 24
        jdks_dir = SCRIPT_DIR / ".jdks"
        print()
        jdk24_path = download_and_extract_jdk(24, jdks_dir)
        if jdk24_path:
            print(f"    JDK 24 installed successfully!")
        else:
            print("    WARNING: Failed to install JDK 24")

    elif choice == 1:
        # Manual installation guidance
        print()
        print("  Please install JDK 24 manually:")
        print()
        print("    https://adoptium.net/temurin/releases/?version=24")
        print()
        print("  After installation, run this script again.")
        print()
        if not prompt_yes_no("Continue anyway?", default=False):
            return None

    # choice == 2: Skip - just continue with what we have

    return jdk24_path


def generate_vscode_config(mod_config: dict, project_path: Path, toolkit_path: str, hytale_path: str, jdk24_path: str = None) -> None:
    """Generate VS Code configuration."""
    vscode_dir = project_path / ".vscode"
    vscode_dir.mkdir(exist_ok=True)

    toolkit_normalized = toolkit_path.replace("\\", "/")
    hytale_normalized = hytale_path.replace("\\", "/")

    # Use provided JDK 24 path or try to detect it
    if not jdk24_path:
        jdk24_path = detect_jdk24_path()
    jdk24_normalized = jdk24_path.replace("\\", "/") if jdk24_path else ""

    # settings.json
    settings = {
        "java.configuration.updateBuildConfiguration": "automatic",
        "java.import.gradle.enabled": True,
        "java.import.gradle.wrapper.enabled": True,
        "java.project.referencedLibraries": [
            f"{hytale_normalized}/Server/HytaleServer.jar"
        ],
        "editor.formatOnSave": True,
        "java.compile.nullAnalysis.mode": "disabled",
    }

    # Add JDK 24 configuration if found
    if jdk24_normalized:
        settings["java.jdt.ls.java.home"] = jdk24_normalized
        settings["java.configuration.runtimes"] = [
            {
                "name": "JavaSE-24",
                "path": jdk24_normalized,
                "default": True
            }
        ]
        print(f"    Detected JDK 24 at: {jdk24_path}")
    else:
        # Add placeholder for user to fill in
        settings["java.jdt.ls.java.home"] = ""
        settings["java.configuration.runtimes"] = [
            {
                "name": "JavaSE-24",
                "path": "PATH_TO_JDK_24",
                "default": True
            }
        ]
        print("    WARNING: JDK 24 not detected. Update java.jdt.ls.java.home in settings.json")

    (vscode_dir / "settings.json").write_text(json.dumps(settings, indent=2))

    # launch.json for debugging (placeholder)
    launch = {
        "version": "0.2.0",
        "configurations": [
            {
                "type": "java",
                "name": "Debug Plugin",
                "request": "attach",
                "hostName": "localhost",
                "port": 5005,
                "projectName": mod_config['name'],
            }
        ]
    }
    (vscode_dir / "launch.json").write_text(json.dumps(launch, indent=2))

    # extensions.json for recommended extensions
    extensions = {
        "recommendations": [
            "vscjava.vscode-java-pack",
            "redhat.java",
            "vscjava.vscode-gradle",
        ]
    }
    (vscode_dir / "extensions.json").write_text(json.dumps(extensions, indent=2))

    print("    Created .vscode/ configuration")


def download_gradle_wrapper(project_path: Path) -> bool:
    """Download and set up Gradle wrapper from Gradle's GitHub repository."""
    print("  Setting up Gradle wrapper...")

    wrapper_dir = project_path / "gradle" / "wrapper"
    wrapper_dir.mkdir(parents=True, exist_ok=True)

    # Base URL for Gradle wrapper files on GitHub
    # Note: Gradle tags use full version like v8.14.0, not v8.14
    gradle_tag = f"v{GRADLE_VERSION}.0" if GRADLE_VERSION.count('.') == 1 else f"v{GRADLE_VERSION}"
    github_base = f"https://raw.githubusercontent.com/gradle/gradle/{gradle_tag}"

    # Files to download
    files_to_download = [
        (f"{github_base}/gradle/wrapper/gradle-wrapper.jar", wrapper_dir / "gradle-wrapper.jar"),
        (f"{github_base}/gradlew", project_path / "gradlew"),
        (f"{github_base}/gradlew.bat", project_path / "gradlew.bat"),
    ]

    # Download each file
    for url, target_path in files_to_download:
        filename = target_path.name
        print(f"    Downloading {filename}...")
        try:
            urllib.request.urlretrieve(url, target_path)
        except Exception as e:
            print(f"    ERROR: Failed to download {filename}: {e}")
            print(f"    URL: {url}")
            return False

    # Set executable permission on Unix
    if platform.system() != "Windows":
        gradlew_path = project_path / "gradlew"
        gradlew_path.chmod(0o755)

    # Create gradle-wrapper.properties (this configures the version)
    properties_content = f'''distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-{GRADLE_VERSION}-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
'''
    (wrapper_dir / "gradle-wrapper.properties").write_text(properties_content)

    print("    Gradle wrapper installed successfully")
    return True


def create_project_structure(mod_config: dict, project_path: Path, hytale_path: str, toolkit_path: str, ides: list[str], gitignore_ide: bool = True, jdk24_path: str = None) -> bool:
    """Create the complete mod project structure."""
    if log:
        log_section(log, "Project Creation")
        log.info(f"Creating project at: {project_path}")
        log.info(f"Mod config: {json.dumps(mod_config, indent=2)}")
        log.info(f"Hytale path: {hytale_path}")
        log.info(f"Toolkit path: {toolkit_path}")
        log.info(f"IDEs: {ides}")
        log.info(f"JDK 24 path: {jdk24_path}")

    print(f"\n  Creating project at: {project_path}")
    print()

    try:
        # Create directory structure
        project_path.mkdir(parents=True, exist_ok=True)

        # Source directories
        package_path = mod_config['group'].replace('.', '/') + '/' + mod_config['name'].replace('-', '')
        src_main_java = project_path / "src" / "main" / "java" / package_path
        src_main_resources = project_path / "src" / "main" / "resources"

        src_main_java.mkdir(parents=True, exist_ok=True)
        src_main_resources.mkdir(parents=True, exist_ok=True)

        print("    Created directory structure")

        # Main class
        main_class_file = src_main_java / f"{mod_config['main_class']}.java"
        main_class_file.write_text(generate_main_class(mod_config))
        print(f"    Created {mod_config['main_class']}.java")

        # manifest.json
        manifest = generate_manifest(mod_config)
        manifest_path = src_main_resources / "manifest.json"
        manifest_path.write_text(json.dumps(manifest, indent=2))
        print("    Created manifest.json")

        # build.gradle.kts
        (project_path / "build.gradle.kts").write_text(generate_build_gradle(mod_config))
        print("    Created build.gradle.kts")

        # settings.gradle.kts
        (project_path / "settings.gradle.kts").write_text(generate_settings_gradle(mod_config))
        print("    Created settings.gradle.kts")

        # gradle.properties
        (project_path / "gradle.properties").write_text(
            generate_gradle_properties(mod_config, hytale_path, toolkit_path, jdk24_path)
        )
        print("    Created gradle.properties")

        # .gitignore
        (project_path / ".gitignore").write_text(generate_gitignore(ignore_ide_configs=gitignore_ide))
        print("    Created .gitignore")

        # README.md
        (project_path / "README.md").write_text(generate_readme(mod_config))
        print("    Created README.md")

        # License file
        if mod_config.get('license') and mod_config['license'] != 'None':
            import datetime
            license_text = LICENSES.get(mod_config['license'])
            if license_text:
                license_text = license_text.format(
                    year=datetime.datetime.now().year,
                    author=mod_config['author_name']
                )
                (project_path / "LICENSE").write_text(license_text)
                print("    Created LICENSE")

        # Gradle wrapper
        download_gradle_wrapper(project_path)

        # IDE configurations
        if "intellij" in ides:
            generate_intellij_config(mod_config, project_path, toolkit_path)

        if "vscode" in ides:
            generate_vscode_config(mod_config, project_path, toolkit_path, hytale_path, jdk24_path)

        if log:
            log.info("Project structure created successfully")
        return True

    except Exception as e:
        if log:
            log.error(f"Failed to create project: {e}")
            log_exception(log, "create_project_structure")
        print(f"\n  ERROR: Failed to create project: {e}")
        return False


# ============================================================================
#  Decompilation (reused from setup.py)
# ============================================================================

def prompt_ram_allocation(default: int = 8) -> int:
    """Prompt user for RAM allocation in GB."""
    print(f"  How much RAM (in GB) should be allocated for decompilation?")
    print(f"  Default is {default}GB. Increase if you encounter OutOfMemory errors.")
    print()

    while True:
        response = input(f"  RAM allocation in GB [{default}]: ").strip()
        if response == "":
            return default
        try:
            value = int(response)
            if 2 <= value <= 64:
                return value
            print("  Please enter a value between 2 and 64 GB.")
        except ValueError:
            print("  Please enter a valid number.")


def decompile_server(install_path: str, ram_gb: int = 8) -> bool:
    """Decompile HytaleServer.jar using Vineflower."""
    server_jar = Path(install_path) / "Server" / "HytaleServer.jar"

    if not server_jar.exists():
        print(f"  ERROR: HytaleServer.jar not found at {server_jar}")
        return False

    if not VINEFLOWER_JAR.exists():
        print(f"  ERROR: Vineflower not found at {VINEFLOWER_JAR}")
        print(f"  Please ensure the Hytale-Toolkit is set up correctly.")
        return False

    DECOMPILED_DIR.mkdir(parents=True, exist_ok=True)

    print()
    print(f"  Source:  {server_jar}")
    print(f"  Output:  {DECOMPILED_DIR}")
    print(f"  RAM:     {ram_gb}GB")
    print()
    print("  Decompiling... (this may take several minutes)")
    print()

    cmd = [
        "java",
        "-Xms2G",
        f"-Xmx{ram_gb}G",
        "-jar", str(VINEFLOWER_JAR),
        "-dgs=1",
        "-asc=1",
        "-rsy=1",
        str(server_jar),
        str(DECOMPILED_DIR)
    ]

    try:
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            bufsize=1
        )

        class_count = 0
        for line in process.stdout:
            line = line.strip()
            if line:
                if "Loading Class:" in line or "Decompiling class" in line:
                    class_count += 1
                    if "Loading Class:" in line:
                        class_name = line.split("Loading Class:")[-1].split()[0]
                    else:
                        class_name = line.split()[-1] if line.split() else ""
                    short_name = class_name.split("/")[-1] if "/" in class_name else class_name
                    print(f"\r  [{class_count:,} classes] {short_name[:45]:<45}", end="", flush=True)

        process.wait()
        print()

        if process.returncode == 0:
            print("  Decompilation complete!")
            return True
        else:
            print(f"  Decompilation failed with exit code {process.returncode}")
            return False

    except FileNotFoundError:
        print("\n  ERROR: Java not found. Please install JDK 24 and try again.")
        return False
    except Exception as e:
        print(f"\n  ERROR: {e}")
        return False


# ============================================================================
#  Main Wizard
# ============================================================================

def main():
    global log, log_file

    # Initialize logging
    log, log_file = setup_logging("init-mod", SCRIPT_DIR)
    log_section(log, "Initialization")
    log.info(f"SCRIPT_DIR: {SCRIPT_DIR}")
    log.info(f"DECOMPILED_DIR: {DECOMPILED_DIR}")

    clear_screen()

    print()
    print("  +----------------------------------------------------------+")
    print("  |                                                          |")
    print("  |          Hytale Mod Initialization Wizard                |")
    print("  |                                                          |")
    print("  +----------------------------------------------------------+")
    print()
    print("  This wizard will help you create a new Hytale mod project with:")
    print("    - Gradle build configuration (HytaleGradle plugin)")
    print("    - Plugin manifest and main class")
    print("    - IDE integration (IntelliJ / VS Code)")
    print("    - Reference to decompiled server source")
    print()

    input("  Press Enter to begin...")

    total_steps = 8

    # =========================================================================
    # Step 1: Hytale Installation
    # =========================================================================
    print_step(1, total_steps, "Hytale Installation")

    hytale_path = get_hytale_install_path()
    if not hytale_path:
        print("\n  Setup cancelled: Hytale installation is required.")
        sys.exit(1)

    # =========================================================================
    # Step 2: JDK Setup
    # =========================================================================
    print_step(2, total_steps, "Java Development Kit (JDK) Setup")

    print("  Hytale modding requires JDK 24.")
    print("  This is used for both running Gradle and compiling your mod.")
    print()

    jdk24_path = ensure_jdk_installed()

    if jdk24_path is None:
        print("\n  WARNING: JDK 24 not configured. Build may fail.")
        if not prompt_yes_no("Continue anyway?", default=False):
            print("\n  Setup cancelled.")
            sys.exit(1)

    # =========================================================================
    # Step 3: Project Location
    # =========================================================================
    print_step(3, total_steps, "Project Location")

    print("  Select where to create your mod project.")
    print("  A new folder will be created with your mod's name.")
    print()

    input("  Press Enter to open folder picker...")

    # Start at parent of Hytale installation
    initial_dir = str(Path(hytale_path).parent.parent)
    parent_folder = open_folder_picker("Select Parent Folder for Mod Project", initial_dir=initial_dir)

    if not parent_folder:
        parent_folder = input("  Enter path manually: ").strip().strip('"').strip("'")

    if not parent_folder or not Path(parent_folder).exists():
        print("  ERROR: Invalid folder selected.")
        sys.exit(1)

    print(f"\n  Project will be created in: {parent_folder}")

    # =========================================================================
    # Step 4: Mod Metadata
    # =========================================================================
    print_step(4, total_steps, "Mod Configuration")

    print("  Let's configure your mod. Required fields are marked with *")
    print()

    mod_config = {}

    # Group (required)
    print("  * Group is your Java package prefix (e.g., com.yourname, io.github.username)")
    mod_config['group'] = prompt_string("Group", validator=validate_group)
    print()

    # Name/ID (required)
    print("  * Name is your mod's unique identifier (lowercase, hyphens allowed)")
    print("    This will also be the name of the project folder.")
    mod_config['name'] = prompt_string("Mod ID", validator=validate_mod_id)
    print()

    # Display name (defaults to formatted ID)
    default_display = to_class_name(mod_config['name']).replace("-", " ")
    print("  Display name is what users see (defaults to formatted ID)")
    mod_config['display_name'] = prompt_string("Display Name", default=default_display, required=False) or default_display
    print()

    # Version
    mod_config['version'] = prompt_string("Version", default="1.0.0", validator=validate_version)
    print()

    # Main class name
    default_class = to_class_name(mod_config['name'])
    mod_config['main_class'] = prompt_string("Main Class Name", default=default_class, validator=validate_class_name)
    print()

    # Description (optional)
    print("  Description is optional but recommended")
    mod_config['description'] = prompt_string("Description", required=False)
    print()

    # Author info
    print("  * Author information")
    mod_config['author_name'] = prompt_string("Author Name")
    mod_config['author_email'] = prompt_string("Author Email (optional)", required=False, validator=lambda x: validate_email(x) if x else None)
    mod_config['author_url'] = prompt_string("Author Website (optional)", required=False, validator=lambda x: validate_url(x) if x else None)
    print()

    # Server version compatibility
    print("  Server version specifies Hytale version compatibility")
    print("  Use '*' for any version, or a semver range like '>=1.0.0'")
    mod_config['server_version'] = prompt_string("Server Version", default="*")
    print()

    # License
    print("  Select a license for your mod:")
    license_options = [
        ("MIT", "Permissive, allows commercial use"),
        ("Apache-2.0", "Permissive with patent protection"),
        ("GPL-3.0", "Copyleft, derivatives must be open source"),
        ("None", "No license file"),
    ]
    license_idx = prompt_choice(license_options, "Select license")
    mod_config['license'] = list(LICENSES.keys())[license_idx]

    # =========================================================================
    # Step 5: IDE Selection
    # =========================================================================
    print_step(5, total_steps, "IDE Configuration")

    print("  Which IDE(s) do you use? We'll generate appropriate config files.")
    print()

    ide_options = [
        ("IntelliJ IDEA", "JetBrains IDE with excellent Java support"),
        ("VS Code", "Microsoft's lightweight editor with Java extensions"),
    ]
    ide_indices = prompt_multi_choice(ide_options, "Select IDE(s)")

    ides = []
    if 0 in ide_indices:
        ides.append("intellij")
    if 1 in ide_indices:
        ides.append("vscode")

    # Ask about gitignoring IDE configs if they selected any IDE
    gitignore_ide = True  # Default to ignoring
    if ides:
        print()
        print("  Should IDE configuration files be excluded from git?")
        print()
        print("    Yes (default): IDE configs (.idea/, .vscode/) will be gitignored.")
        print("                   Each developer configures their own IDE settings.")
        print("                   Best for: Teams with different IDE preferences,")
        print("                   or when configs contain machine-specific paths.")
        print()
        print("    No:            IDE configs will be committed to the repository.")
        print("                   All collaborators share the same IDE settings.")
        print("                   Best for: Solo projects or teams using the same IDE")
        print("                   who want consistent project configuration.")
        print()
        gitignore_ide = prompt_yes_no("Exclude IDE configs from git?", default=True)

    # =========================================================================
    # Step 6: Git Repository
    # =========================================================================
    print_step(6, total_steps, "Version Control")

    init_git = prompt_yes_no("Initialize a git repository?", default=True)

    # =========================================================================
    # Step 7: Decompiled Source
    # =========================================================================
    print_step(7, total_steps, "Decompiled Source Code")

    print("  Decompiled source code enables IDE code navigation and autocomplete")
    print("  for the Hytale server classes.")
    print()
    print(f"  Looking for decompiled source at: {DECOMPILED_DIR}")
    print()

    has_decompiled = DECOMPILED_DIR.exists() and any(DECOMPILED_DIR.iterdir())

    if has_decompiled:
        print("  Decompiled source found!")
        print("  Your mod will reference this shared source directory.")
    else:
        print("  Decompiled source not found.")
        if prompt_yes_no("Decompile server now? (recommended for IDE support)", default=True):
            print()
            ram_gb = prompt_ram_allocation(default=8)
            if DECOMPILED_DIR.exists():
                shutil.rmtree(DECOMPILED_DIR)
            if not decompile_server(hytale_path, ram_gb):
                print("\n  WARNING: Decompilation failed. Continuing without decompiled source.")
                print("  You can run setup.py later to decompile.")
        else:
            print("\n  Skipping decompilation. IDE code navigation will be limited.")
            print("  Run 'python hytale-rag/setup.py' later to decompile.")

    # =========================================================================
    # Step 8: Create Project
    # =========================================================================
    print_step(8, total_steps, "Creating Project")

    project_path = Path(parent_folder) / mod_config['name']

    if project_path.exists():
        print(f"  WARNING: Folder already exists: {project_path}")
        if not prompt_yes_no("Overwrite?", default=False):
            print("  Cancelled.")
            sys.exit(1)
        shutil.rmtree(project_path)

    success = create_project_structure(
        mod_config=mod_config,
        project_path=project_path,
        hytale_path=hytale_path,
        toolkit_path=str(SCRIPT_DIR),
        ides=ides,
        gitignore_ide=gitignore_ide,
        jdk24_path=jdk24_path
    )

    if not success:
        sys.exit(1)

    # Initialize git
    if init_git:
        print()
        print("  Initializing git repository...")
        exit_code, output = run_command(["git", "init"], cwd=project_path)
        if exit_code == 0:
            print("    Git repository initialized")
            # Initial commit
            run_command(["git", "add", "."], cwd=project_path)
            run_command(
                ["git", "commit", "-m", "Initial commit: Project scaffolding"],
                cwd=project_path
            )
            print("    Created initial commit")
        else:
            print(f"    Warning: Could not initialize git: {output}")

    # Verify build
    print()
    print("  Verifying project setup...")

    # Use JDK 24 for Gradle if available
    gradle_env = None
    if jdk24_path:
        gradle_env = {"JAVA_HOME": jdk24_path}

    gradlew_script = "gradlew.bat" if platform.system() == "Windows" else "./gradlew"

    if (project_path / gradlew_script.replace("./", "")).exists():
        exit_code, output = run_command(
            [gradlew_script, "build", "-x", "test"],
            cwd=project_path,
            env=gradle_env
        )
        if exit_code == 0:
            print("    Build successful!")
        else:
            print("    Warning: Initial build had issues. This may be normal if")
            print("    the HytaleGradle plugin needs additional setup.")
            print(f"    Run 'gradlew build' in {project_path} for details.")
    elif command_exists("gradle"):
        exit_code, output = run_command(["gradle", "build", "-x", "test"], cwd=project_path, env=gradle_env)
        if exit_code == 0:
            print("    Build successful!")
        else:
            print("    Warning: Initial build had issues.")
            print(f"    Run 'gradle build' in {project_path} for details.")
    else:
        print("    Gradle not found. Please install Gradle and run 'gradle build'")
        print("    to verify the project setup.")

    # =========================================================================
    # Done!
    # =========================================================================
    print()
    print("  +----------------------------------------------------------+")
    print("  |                   Setup Complete!                        |")
    print("  +----------------------------------------------------------+")
    print()
    print(f"  Your mod project has been created at:")
    print(f"    {project_path}")
    print()

    # Show JDK info
    if jdk24_path:
        jdk24_display = jdk24_path.replace("/", "\\") if platform.system() == "Windows" else jdk24_path
        print(f"  JDK 24 configured at: {jdk24_display}")
        print("  (Gradle will use this automatically via gradle.properties)")
    else:
        print("  WARNING: JDK 24 not configured.")
        print("  You may need to install JDK 24 and update gradle.properties:")
        print("    org.gradle.java.home=/path/to/jdk-24")
    print()

    print("  Next steps:")
    print(f"    1. Open the project in your IDE")
    print(f"    2. Edit src/main/java/.../{ mod_config['main_class']}.java")
    print(f"    3. Run 'gradlew runServer' to test your mod")
    print()
    print("  Useful commands:")
    print("    gradlew build         - Build the mod")
    print("    gradlew runServer     - Run server with mod installed")
    print("    gradlew installPlugin - Install mod without restart")
    print("    gradlew generateSources - Generate decompiled sources")
    print()
    print("  Happy modding!")
    print()

    # Show log file location
    if log_file:
        print(f"  Log file: {log_file}")
        print("  (Share this file if you need help troubleshooting)")
        print()
        log.info("=== Mod initialization completed successfully ===")
        log.info(f"Project created at: {project_path}")


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        if log:
            log.warning("Setup cancelled by user (Ctrl+C)")
        print("\n\n  Setup cancelled by user.")
        if log_file:
            print(f"  Log file: {log_file}")
        sys.exit(1)
    except Exception as e:
        if log:
            log.error(f"Unhandled exception: {e}")
            log_exception(log, "main")
        print(f"\n\n  ERROR: An unexpected error occurred: {e}")
        if log_file:
            print(f"  Log file: {log_file}")
            print("  Please share this log file when reporting the issue.")
        sys.exit(1)
