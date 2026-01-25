#!/usr/bin/env python3
"""
Hytale Mod Initialization Wizard

A comprehensive setup script that guides you through creating a new Hytale mod project:
1. Selecting where to create the mod
2. Configuring mod metadata (name, author, version, etc.)
3. Setting up Gradle with HytaleGradle plugin
4. Configuring IDE integration (IntelliJ / VS Code)
5. Optionally initializing git repository

Can be run interactively or with CLI arguments for automation/LLM usage.
"""

from pathlib import Path as _Path

# Read version from central VERSION file
_version_file = _Path(__file__).parent.parent.parent / "VERSION"
__version__ = _version_file.read_text().strip() if _version_file.exists() else "0.0.0"

import argparse
import json
import os
import platform
import re
import shutil
import subprocess
import sys
import urllib.request
from pathlib import Path


def get_base_path() -> Path:
    """Get the base path for the toolkit, handling both normal and frozen (PyInstaller) execution."""
    if getattr(sys, 'frozen', False):
        # Running as compiled executable
        return Path(sys.executable).parent

    # Check standard toolkit location first (where setup GUI installs)
    if sys.platform == "win32":
        standard_path = Path(os.environ.get("LOCALAPPDATA", "")) / "Hytale-Toolkit"
    else:
        standard_path = Path.home() / ".hytale-toolkit"

    # Use standard path if it has decompiled source or tools
    if (standard_path / "decompiled").exists() or (standard_path / "tools").exists():
        return standard_path

    # Fall back to relative path from CLI installation
    # cli.py is at: {toolkit}/hytale-mod-cli/hytale_mod/cli.py
    # So toolkit root is 3 levels up
    return Path(__file__).parent.parent.parent


# Simple logging setup for the CLI package
def setup_logging(name, path):
    """Set up basic logging."""
    import logging
    logging.basicConfig(
        level=logging.INFO,
        format='%(message)s'
    )
    return logging.getLogger(name), None

def log_command(*args, **kwargs): pass
def log_exception(*args, **kwargs): pass
def log_section(*args, **kwargs): pass

# Global logger (initialized in main)
log = None
log_file = None

# ============================================================================
#  Configuration
# ============================================================================

SCRIPT_DIR = get_base_path()
DECOMPILED_DIR = SCRIPT_DIR / "decompiled"
VINEFLOWER_JAR = SCRIPT_DIR / "tools" / "vineflower.jar"

# Required contents of a valid Hytale installation
REQUIRED_CONTENTS = ["Client", "Server", "Assets.zip"]

# Gradle wrapper version
GRADLE_VERSION = "8.14"
GRADLE_WRAPPER_URL = f"https://services.gradle.org/distributions/gradle-{GRADLE_VERSION}-bin.zip"

# Maven wrapper version
MAVEN_VERSION = "3.9.9"
MAVEN_WRAPPER_VERSION = "3.3.2"

# Kotlin version (for Kotlin projects)
KOTLIN_VERSION = "2.3.0"

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
            if log:
                log.info(f"User prompt: '{question}' -> {'Yes' if default else 'No'} (default)")
            return default
        if response in ("y", "yes"):
            if log:
                log.info(f"User prompt: '{question}' -> Yes")
            return True
        if response in ("n", "no"):
            if log:
                log.info(f"User prompt: '{question}' -> No")
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
                if log:
                    log.info(f"User choice: '{prompt_text}' -> [{idx + 1}] {options[idx][0]}")
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
            if log:
                log.info(f"User multi-choice: '{prompt_text}' -> None (skipped)")
            return []

        if response == "a":
            if log:
                selected = [options[i][0] for i in range(len(options))]
                log.info(f"User multi-choice: '{prompt_text}' -> All: {selected}")
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
                result = list(set(indices))
                if log:
                    selected = [options[i][0] for i in result]
                    log.info(f"User multi-choice: '{prompt_text}' -> {selected}")
                return result
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
            if log:
                log.info(f"User input: '{prompt_text}' -> '{response}' (default)")
        if response == "" and required:
            print("  This field is required.")
            continue
        if response == "" and not required:
            if log:
                log.info(f"User input: '{prompt_text}' -> (empty)")
            return ""
        if validator:
            error = validator(response)
            if error:
                print(f"  {error}")
                continue
        if log and response != default:
            log.info(f"User input: '{prompt_text}' -> '{response}'")
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
            # Use list2cmdline for proper Windows quoting (handles spaces in arguments)
            cmd = subprocess.list2cmdline(cmd)

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
    """Validate mod ID (lowercase, alphanumeric, hyphens, underscores)."""
    if not re.match(r'^[a-z][a-z0-9_-]*$', value):
        return "Mod ID must start with a letter and contain only lowercase letters, numbers, hyphens, and underscores."
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
            if log:
                log.info(f"User selected Hytale installation path (auto-detected): {detected}")
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
            if log:
                log.info(f"User selected Hytale installation path: {folder}")
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
    """Convert mod-id or mod_id to ClassName format."""
    # Split by hyphens and underscores, capitalize each part
    parts = re.split(r'[-_]', mod_id)
    return "".join(part.capitalize() for part in parts)


def generate_main_class(mod_config: dict) -> str:
    """Generate the main plugin Java class."""
    package = f"{mod_config['group']}.{mod_config['name'].replace('-', '').replace('_', '')}"
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


def generate_main_class_kotlin(mod_config: dict) -> str:
    """Generate the main plugin Kotlin class."""
    package = f"{mod_config['group']}.{mod_config['name'].replace('-', '').replace('_', '')}"
    class_name = mod_config['main_class']

    return f'''package {package}

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import java.util.logging.Level

/**
 * Main entry point for the {mod_config['display_name']} plugin.
 */
class {class_name}(init: JavaPluginInit) : JavaPlugin(init) {{

    override fun setup() {{
        // Called during plugin setup phase
    }}

    override fun start() {{
        // Called when the plugin is enabled
        logger.at(Level.INFO).log("{mod_config['display_name']} has been enabled!")
    }}

    override fun shutdown() {{
        // Called when the plugin is disabled
        logger.at(Level.INFO).log("{mod_config['display_name']} has been disabled!")
    }}
}}
'''


def generate_manifest(mod_config: dict) -> dict:
    """Generate the manifest.json content."""
    manifest = {
        "Group": mod_config['group'],
        "Name": mod_config['name'],
        "Version": mod_config['version'],
        "Main": f"{mod_config['group']}.{mod_config['name'].replace('-', '').replace('_', '')}.{mod_config['main_class']}",
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


def generate_build_gradle(mod_config: dict, language: str = "java") -> str:
    """Generate build.gradle.kts content."""
    if language == "kotlin":
        return f'''plugins {{
    kotlin("jvm") version "{KOTLIN_VERSION}"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("app.ultradev.hytalegradle") version "1.6.7"
}}

group = "{mod_config['group']}"
version = "{mod_config['version']}"

kotlin {{
    jvmToolchain(24)
}}

repositories {{
    mavenCentral()
}}

dependencies {{
    // HytaleServer.jar - provided at runtime by the server
    compileOnly(files("${{property("hytaleInstallPath")}}/Server/HytaleServer.jar"))

    // Kotlin standard library
    implementation(kotlin("stdlib"))

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
    else:
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


def generate_pom_xml(mod_config: dict, hytale_path: str, toolkit_path: str, language: str = "java", jdk_path: str = None) -> str:
    """Generate pom.xml content for Maven projects."""
    hytale_path_normalized = hytale_path.replace("\\", "/")
    toolkit_path_normalized = toolkit_path.replace("\\", "/")
    jdk_path_normalized = jdk_path.replace("\\", "/") if jdk_path else ""

    # Properties section differs for Kotlin
    if language == "kotlin":
        properties = f'''    <properties>
        <kotlin.version>{KOTLIN_VERSION}</kotlin.version>
        <maven.compiler.source>25</maven.compiler.source>
        <maven.compiler.target>25</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <hytale.install.path>{hytale_path_normalized}</hytale.install.path>
        <hytale.toolkit.path>{toolkit_path_normalized}</hytale.toolkit.path>
        <hytale.jdk.path>{jdk_path_normalized}</hytale.jdk.path>
    </properties>'''
        dependencies = f'''    <dependencies>
        <dependency>
            <groupId>com.hypixel.hytale</groupId>
            <artifactId>HytaleServer</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${{hytale.install.path}}/Server/HytaleServer.jar</systemPath>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>${{kotlin.version}}</version>
        </dependency>
    </dependencies>'''
        build_plugins = f'''    <build>
        <sourceDirectory>${{project.basedir}}/src/main/kotlin</sourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${{kotlin.version}}</version>
                <executions>
                    <execution>
                        <id>compile</id>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <jvmTarget>25</jvmTarget>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.4.2</version>
                <configuration>
                    <archive>
                        <manifestEntries>
                            <Plugin-Group>${{project.groupId}}</Plugin-Group>
                            <Plugin-Name>${{project.artifactId}}</Plugin-Name>
                            <Plugin-Version>${{project.version}}</Plugin-Version>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.6.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>'''
    else:
        properties = f'''    <properties>
        <maven.compiler.source>25</maven.compiler.source>
        <maven.compiler.target>25</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <hytale.install.path>{hytale_path_normalized}</hytale.install.path>
        <hytale.toolkit.path>{toolkit_path_normalized}</hytale.toolkit.path>
        <hytale.jdk.path>{jdk_path_normalized}</hytale.jdk.path>
    </properties>'''
        dependencies = f'''    <dependencies>
        <dependency>
            <groupId>com.hypixel.hytale</groupId>
            <artifactId>HytaleServer</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${{hytale.install.path}}/Server/HytaleServer.jar</systemPath>
        </dependency>
    </dependencies>'''
        build_plugins = f'''    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <release>25</release>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.4.2</version>
                <configuration>
                    <archive>
                        <manifestEntries>
                            <Plugin-Group>${{project.groupId}}</Plugin-Group>
                            <Plugin-Name>${{project.artifactId}}</Plugin-Name>
                            <Plugin-Version>${{project.version}}</Plugin-Version>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.6.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>'''

    return f'''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>{mod_config['group']}</groupId>
    <artifactId>{mod_config['name']}</artifactId>
    <version>{mod_config['version']}</version>
    <packaging>jar</packaging>

    <name>{mod_config['display_name']}</name>
    <description>{mod_config.get('description', '')}</description>

{properties}

{dependencies}

{build_plugins}

    <profiles>
        <!-- Run the Hytale server with the plugin installed -->
        <profile>
            <id>run-server</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-resources-plugin</artifactId>
                        <version>3.3.1</version>
                        <executions>
                            <execution>
                                <id>copy-plugin-to-server</id>
                                <phase>package</phase>
                                <goals>
                                    <goal>copy-resources</goal>
                                </goals>
                                <configuration>
                                    <outputDirectory>${{hytale.install.path}}/Server/mods</outputDirectory>
                                    <resources>
                                        <resource>
                                            <directory>${{project.build.directory}}</directory>
                                            <includes>
                                                <include>${{project.build.finalName}}.jar</include>
                                            </includes>
                                        </resource>
                                    </resources>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <version>3.4.1</version>
                        <executions>
                            <execution>
                                <id>run-hytale-server</id>
                                <phase>package</phase>
                                <goals>
                                    <goal>exec</goal>
                                </goals>
                                <configuration>
                                    <executable>${{hytale.jdk.path}}/bin/java</executable>
                                    <workingDirectory>${{hytale.install.path}}/Server</workingDirectory>
                                    <arguments>
                                        <argument>--enable-native-access=ALL-UNNAMED</argument>
                                        <argument>-jar</argument>
                                        <argument>HytaleServer.jar</argument>
                                        <argument>--assets</argument>
                                        <argument>${{hytale.install.path}}/Assets.zip</argument>
                                        <argument>--assets</argument>
                                        <argument>${{project.basedir}}/src/main/resources</argument>
                                        <argument>--allow-op</argument>
                                    </arguments>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>

        <!-- Install plugin to server without running -->
        <profile>
            <id>install-plugin</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-resources-plugin</artifactId>
                        <version>3.3.1</version>
                        <executions>
                            <execution>
                                <id>copy-plugin-to-server</id>
                                <phase>package</phase>
                                <goals>
                                    <goal>copy-resources</goal>
                                </goals>
                                <configuration>
                                    <outputDirectory>${{hytale.install.path}}/Server/mods</outputDirectory>
                                    <resources>
                                        <resource>
                                            <directory>${{project.build.directory}}</directory>
                                            <includes>
                                                <include>${{project.build.finalName}}.jar</include>
                                            </includes>
                                        </resource>
                                    </resources>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
</project>
'''


def download_maven_wrapper(project_path: Path, jdk_path: str = None) -> bool:
    """Download and set up Maven wrapper."""
    print("  Setting up Maven wrapper...")

    mvn_wrapper_dir = project_path / ".mvn" / "wrapper"
    mvn_wrapper_dir.mkdir(parents=True, exist_ok=True)

    # Base URL for Maven wrapper files
    wrapper_base = f"https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/{MAVEN_WRAPPER_VERSION}"

    # Download maven-wrapper.jar
    jar_url = f"{wrapper_base}/maven-wrapper-{MAVEN_WRAPPER_VERSION}.jar"
    jar_path = mvn_wrapper_dir / "maven-wrapper.jar"

    print(f"    Downloading maven-wrapper.jar...")
    try:
        urllib.request.urlretrieve(jar_url, jar_path)
    except Exception as e:
        print(f"    ERROR: Failed to download maven-wrapper.jar: {e}")
        return False

    # Create maven-wrapper.properties
    properties_content = f'''distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/{MAVEN_VERSION}/apache-maven-{MAVEN_VERSION}-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/{MAVEN_WRAPPER_VERSION}/maven-wrapper-{MAVEN_WRAPPER_VERSION}.jar
'''
    (mvn_wrapper_dir / "maven-wrapper.properties").write_text(properties_content)

    # Create mvnw scripts (embedded to avoid network dependency issues)
    # These scripts use the maven-wrapper.jar with the MavenWrapperMain class
    # For Unix, use the bundled JDK path directly if available
    jdk_path_unix = jdk_path.replace("\\", "/") if jdk_path else None
    if jdk_path_unix:
        mvnw_script = f'''#!/bin/sh
# Maven Wrapper script
# Uses maven-wrapper.jar to download and run Maven

BASEDIR=$(cd "$(dirname "$0")" && pwd)
WRAPPER_JAR="$BASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Error: maven-wrapper.jar not found at $WRAPPER_JAR"
    echo "Please ensure the .mvn/wrapper directory exists with maven-wrapper.jar"
    exit 1
fi

# Use bundled JDK from Hytale Toolkit
JAVACMD="{jdk_path_unix}/bin/java"

# Set MAVEN_PROJECTBASEDIR for multi-module support
export MAVEN_PROJECTBASEDIR="$BASEDIR"

exec "$JAVACMD" $MAVEN_OPTS \\
    -Dmaven.multiModuleProjectDirectory="$BASEDIR" \\
    -classpath "$WRAPPER_JAR" \\
    org.apache.maven.wrapper.MavenWrapperMain "$@"
'''
    else:
        mvnw_script = '''#!/bin/sh
# Maven Wrapper script
# Uses maven-wrapper.jar to download and run Maven

BASEDIR=$(cd "$(dirname "$0")" && pwd)
WRAPPER_JAR="$BASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Error: maven-wrapper.jar not found at $WRAPPER_JAR"
    echo "Please ensure the .mvn/wrapper directory exists with maven-wrapper.jar"
    exit 1
fi

# Use JAVA_HOME if set, otherwise fall back to java in PATH
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Set MAVEN_PROJECTBASEDIR for multi-module support
export MAVEN_PROJECTBASEDIR="$BASEDIR"

exec "$JAVACMD" $MAVEN_OPTS \\
    -Dmaven.multiModuleProjectDirectory="$BASEDIR" \\
    -classpath "$WRAPPER_JAR" \\
    org.apache.maven.wrapper.MavenWrapperMain "$@"
'''

    # For Windows, use the bundled JDK path directly if available
    jdk_path_win = jdk_path.replace("/", "\\") if jdk_path else None
    if jdk_path_win:
        mvnw_cmd_script = f'''@echo off
@rem Maven Wrapper script for Windows
@rem Uses maven-wrapper.jar to download and run Maven

setlocal

set BASEDIR=%~dp0
@rem Remove trailing backslash if present
if "%BASEDIR:~-1%"=="\\" set BASEDIR=%BASEDIR:~0,-1%

set WRAPPER_JAR=%BASEDIR%\\.mvn\\wrapper\\maven-wrapper.jar

if not exist "%WRAPPER_JAR%" (
    echo Error: maven-wrapper.jar not found at %WRAPPER_JAR%
    echo Please ensure the .mvn\\wrapper directory exists with maven-wrapper.jar
    exit /b 1
)

@rem Use bundled JDK from Hytale Toolkit
set JAVACMD={jdk_path_win}\\bin\\java.exe

set MAVEN_PROJECTBASEDIR=%BASEDIR%

"%JAVACMD%" %MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory="%BASEDIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
'''
    else:
        mvnw_cmd_script = '''@echo off
@rem Maven Wrapper script for Windows
@rem Uses maven-wrapper.jar to download and run Maven

setlocal

set BASEDIR=%~dp0
@rem Remove trailing backslash if present
if "%BASEDIR:~-1%"=="\\" set BASEDIR=%BASEDIR:~0,-1%

set WRAPPER_JAR=%BASEDIR%\\.mvn\\wrapper\\maven-wrapper.jar

if not exist "%WRAPPER_JAR%" (
    echo Error: maven-wrapper.jar not found at %WRAPPER_JAR%
    echo Please ensure the .mvn\\wrapper directory exists with maven-wrapper.jar
    exit /b 1
)

@rem Use JAVA_HOME if set, otherwise fall back to java in PATH
if defined JAVA_HOME (
    set JAVACMD=%JAVA_HOME%\\bin\\java.exe
) else (
    set JAVACMD=java
)

set MAVEN_PROJECTBASEDIR=%BASEDIR%

"%JAVACMD%" %MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory="%BASEDIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
'''

    print(f"    Creating mvnw...")
    (project_path / "mvnw").write_text(mvnw_script, newline='\n')  # Unix line endings
    print(f"    Creating mvnw.cmd...")
    (project_path / "mvnw.cmd").write_text(mvnw_cmd_script)

    # Set executable permission on Unix
    if platform.system() != "Windows":
        mvnw_path = project_path / "mvnw"
        mvnw_path.chmod(0o755)

    print("    Maven wrapper installed successfully")
    return True


def generate_gitignore(ignore_ide_configs: bool = True, build_system: str = "maven") -> str:
    """Generate .gitignore content."""
    if build_system == "maven":
        content = '''# Maven
target/
dependency-reduced-pom.xml
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties

'''
    else:
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


def generate_readme(mod_config: dict, build_system: str = "maven") -> str:
    """Generate README.md content."""
    if build_system == "maven":
        return f'''# {mod_config['display_name']}

{mod_config.get('description', 'A Hytale server plugin.')}

## Building

```bash
./mvnw clean package
```

Or on Windows:

```cmd
mvnw.cmd clean package
```

## Development

### Run Server with Plugin

```bash
./mvnw clean package -Prun-server
```

This will build your plugin, copy it to the server's mods folder, and start the Hytale server.

### Install Plugin Only (Hot Reload)

```bash
./mvnw clean package -Pinstall-plugin
```

This builds and copies the plugin to the server without starting it.

## Requirements

- **JDK 25** - Required for Maven and compilation
- Maven {MAVEN_VERSION} or newer (included via wrapper)
- Hytale Server installation

The Hytale installation path is configured in `pom.xml` properties.

## License

{mod_config.get('license', 'MIT')}

## Author

{mod_config['author_name']}
'''
    else:
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


def generate_intellij_config(mod_config: dict, project_path: Path, toolkit_path: str, build_system: str = "maven") -> None:
    """Generate IntelliJ IDEA configuration."""
    idea_dir = project_path / ".idea"
    idea_dir.mkdir(exist_ok=True)

    # Determine JDK version and output dir based on build system
    jdk_version = "25" if build_system == "maven" else "24"
    output_dir = "target/classes" if build_system == "maven" else "build/classes"

    # Create misc.xml for JDK configuration
    misc_xml = f'''<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectRootManager" version="2" languageLevel="JDK_{jdk_version}" default="true" project-jdk-name="{jdk_version}" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/{output_dir}" />
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
    java_exe = "java.exe" if system == "Windows" else "java"

    # First, check for toolkit's JDK (installed by GUI setup wizard)
    # This is the preferred location for JDK 25
    toolkit_jdk = SCRIPT_DIR / "jdk"
    if toolkit_jdk.exists():
        for item in toolkit_jdk.iterdir():
            if item.is_dir() and f"jdk-{version}" in item.name:
                if (item / "bin" / java_exe).exists():
                    return str(item)

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

        # Check user home .jdks folder (where IntelliJ installs JDKs)
        home_jdks = Path.home() / ".jdks"
        if home_jdks.exists():
            for item in home_jdks.iterdir():
                if item.is_dir() and (f"jdk-{version}" in item.name or f"temurin-{version}" in item.name):
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

        # Check user home .jdks folder (where IntelliJ installs JDKs)
        home_jdks = Path.home() / ".jdks"
        if home_jdks.exists():
            for item in home_jdks.iterdir():
                if item.is_dir() and (f"jdk-{version}" in item.name or f"temurin-{version}" in item.name):
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

        # Check user home .jdks folder (where IntelliJ installs JDKs)
        home_jdks = Path.home() / ".jdks"
        if home_jdks.exists():
            for item in home_jdks.iterdir():
                if item.is_dir() and (f"jdk-{version}" in item.name or f"temurin-{version}" in item.name):
                    possible_paths.insert(0, item)

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

    # Query Adoptium API for the JDK
    # Format: /v3/assets/latest/{feature_version}/hotspot?os={os}&architecture={arch}&image_type=jdk
    api_url = f"https://api.adoptium.net/v3/assets/latest/{version}/hotspot"
    params = f"?architecture={arch}&image_type=jdk&os={os_name}"

    try:
        req = urllib.request.Request(api_url + params)
        req.add_header("Accept", "application/json")
        req.add_header("User-Agent", "Hytale-Toolkit")

        with urllib.request.urlopen(req, timeout=30) as response:
            data = json.loads(response.read().decode())

            if data and len(data) > 0:
                # Find the ZIP/tar.gz package (not MSI/PKG installer)
                for asset in data:
                    binary = asset.get("binary", {})
                    package = binary.get("package", {})
                    download_url = package.get("link", "")
                    filename = package.get("name", "")

                    # Prefer archive formats over installers
                    if archive_type == "zip" and filename.endswith(".zip"):
                        return (download_url, filename)
                    elif archive_type == "tar.gz" and filename.endswith(".tar.gz"):
                        return (download_url, filename)

                # Fallback: return first result
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


def ensure_jdk_installed(version: int = 24) -> str | None:
    """Ensure the specified JDK version is available.

    Args:
        version: JDK version to check/install (default: 24)

    Returns:
        Path to JDK installation or None.
    """
    print(f"  Checking for JDK {version} installation...")
    print()

    jdk_path = detect_jdk_path(version)

    # Report what we found
    if jdk_path:
        print(f"    JDK {version}: Found at {jdk_path}")
        print()
        print(f"  JDK {version} is installed!")
        return jdk_path

    print(f"    JDK {version}: Not found")
    print()

    options = [
        (f"Download JDK {version} automatically", "Downloads from Eclipse Adoptium (Temurin) to a local .jdks folder"),
        ("Install manually", f"You'll need to install JDK {version} yourself"),
        ("Skip JDK setup", "Continue without JDK validation (may cause build issues)"),
    ]

    choice = prompt_choice(options, "How would you like to proceed?")

    if choice == 0:
        # Download JDK
        jdks_dir = SCRIPT_DIR / ".jdks"
        print()
        jdk_path = download_and_extract_jdk(version, jdks_dir)
        if jdk_path:
            print(f"    JDK {version} installed successfully!")
        else:
            print(f"    WARNING: Failed to install JDK {version}")

    elif choice == 1:
        # Manual installation guidance
        print()
        print(f"  Please install JDK {version} manually:")
        print()
        print(f"    https://adoptium.net/temurin/releases/?version={version}")
        print()
        print("  After installation, run this script again.")
        print()
        if not prompt_yes_no("Continue anyway?", default=False):
            return None

    # choice == 2: Skip - just continue with what we have

    return jdk_path


def generate_vscode_config(mod_config: dict, project_path: Path, toolkit_path: str, hytale_path: str, jdk_path: str = None, build_system: str = "maven") -> None:
    """Generate VS Code configuration."""
    vscode_dir = project_path / ".vscode"
    vscode_dir.mkdir(exist_ok=True)

    toolkit_normalized = toolkit_path.replace("\\", "/")
    hytale_normalized = hytale_path.replace("\\", "/")

    # Determine JDK version based on build system
    jdk_version = 25 if build_system == "maven" else 24

    # Use provided JDK path or try to detect it
    if not jdk_path:
        jdk_path = detect_jdk_path(jdk_version)
    jdk_normalized = jdk_path.replace("\\", "/") if jdk_path else ""

    # settings.json
    settings = {
        "java.configuration.updateBuildConfiguration": "automatic",
        "java.project.referencedLibraries": [
            f"{hytale_normalized}/Server/HytaleServer.jar"
        ],
        "editor.formatOnSave": True,
        "java.compile.nullAnalysis.mode": "disabled",
    }

    # Add build system specific settings
    if build_system == "maven":
        settings["java.import.maven.enabled"] = True
        settings["java.configuration.maven.userSettings"] = ""
    else:
        settings["java.import.gradle.enabled"] = True
        settings["java.import.gradle.wrapper.enabled"] = True

    # Add JDK configuration if found
    if jdk_normalized:
        settings["java.jdt.ls.java.home"] = jdk_normalized
        settings["java.configuration.runtimes"] = [
            {
                "name": f"JavaSE-{jdk_version}",
                "path": jdk_normalized,
                "default": True
            }
        ]
        print(f"    Detected JDK {jdk_version} at: {jdk_path}")
    else:
        # Add placeholder for user to fill in
        settings["java.jdt.ls.java.home"] = ""
        settings["java.configuration.runtimes"] = [
            {
                "name": f"JavaSE-{jdk_version}",
                "path": f"PATH_TO_JDK_{jdk_version}",
                "default": True
            }
        ]
        print(f"    WARNING: JDK {jdk_version} not detected. Update java.jdt.ls.java.home in settings.json")

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

    # tasks.json for build tasks
    if build_system == "maven":
        tasks = {
            "version": "2.0.0",
            "tasks": [
                {
                    "label": "Maven: Clean",
                    "type": "shell",
                    "command": "./mvnw",
                    "args": ["clean"],
                    "windows": {"command": ".\\mvnw.cmd", "args": ["clean"]},
                    "group": "build",
                    "problemMatcher": "$maven"
                },
                {
                    "label": "Maven: Package",
                    "type": "shell",
                    "command": "./mvnw",
                    "args": ["package"],
                    "windows": {"command": ".\\mvnw.cmd", "args": ["package"]},
                    "group": {"kind": "build", "isDefault": True},
                    "problemMatcher": "$maven"
                },
                {
                    "label": "Maven: Clean Package",
                    "type": "shell",
                    "command": "./mvnw",
                    "args": ["clean", "package"],
                    "windows": {"command": ".\\mvnw.cmd", "args": ["clean", "package"]},
                    "group": "build",
                    "problemMatcher": "$maven"
                },
                {
                    "label": "Maven: Install Plugin",
                    "type": "shell",
                    "command": "./mvnw",
                    "args": ["package", "-Pinstall-plugin"],
                    "windows": {"command": ".\\mvnw.cmd", "args": ["package", "-Pinstall-plugin"]},
                    "group": "build",
                    "problemMatcher": "$maven",
                    "detail": "Builds and copies plugin to Hytale Server/mods folder"
                },
                {
                    "label": "Maven: Run Server",
                    "type": "shell",
                    "command": "./mvnw",
                    "args": ["package", "-Prun-server"],
                    "windows": {"command": ".\\mvnw.cmd", "args": ["package", "-Prun-server"]},
                    "group": "build",
                    "problemMatcher": "$maven",
                    "detail": "Builds, installs plugin, and starts Hytale server"
                }
            ]
        }
    else:
        # Gradle tasks
        tasks = {
            "version": "2.0.0",
            "tasks": [
                {
                    "label": "Gradle: Clean",
                    "type": "shell",
                    "command": "./gradlew",
                    "args": ["clean"],
                    "windows": {"command": ".\\gradlew.bat", "args": ["clean"]},
                    "group": "build",
                    "problemMatcher": "$gradle"
                },
                {
                    "label": "Gradle: Build",
                    "type": "shell",
                    "command": "./gradlew",
                    "args": ["build"],
                    "windows": {"command": ".\\gradlew.bat", "args": ["build"]},
                    "group": {"kind": "build", "isDefault": True},
                    "problemMatcher": "$gradle"
                },
                {
                    "label": "Gradle: Clean Build",
                    "type": "shell",
                    "command": "./gradlew",
                    "args": ["clean", "build"],
                    "windows": {"command": ".\\gradlew.bat", "args": ["clean", "build"]},
                    "group": "build",
                    "problemMatcher": "$gradle"
                },
                {
                    "label": "Gradle: Install Plugin",
                    "type": "shell",
                    "command": "./gradlew",
                    "args": ["installPlugin"],
                    "windows": {"command": ".\\gradlew.bat", "args": ["installPlugin"]},
                    "group": "build",
                    "problemMatcher": "$gradle",
                    "detail": "Builds and copies plugin to Hytale Server/mods folder"
                },
                {
                    "label": "Gradle: Run Server",
                    "type": "shell",
                    "command": "./gradlew",
                    "args": ["runServer"],
                    "windows": {"command": ".\\gradlew.bat", "args": ["runServer"]},
                    "group": "build",
                    "problemMatcher": "$gradle",
                    "detail": "Builds, installs plugin, and starts Hytale server"
                }
            ]
        }
    (vscode_dir / "tasks.json").write_text(json.dumps(tasks, indent=2))

    # package.json for NPM Scripts panel (provides play buttons in VS Code sidebar)
    # Use platform-appropriate wrapper scripts
    if platform.system() == "Windows":
        mvn_wrapper = "mvnw.cmd"
        gradle_wrapper = "gradlew.bat"
    else:
        mvn_wrapper = "./mvnw"
        gradle_wrapper = "./gradlew"

    if build_system == "maven":
        package_json = {
            "name": f"{mod_config['name']}-hytale-mod",
            "private": True,
            "scripts": {
                "build": f"{mvn_wrapper} package",
                "clean": f"{mvn_wrapper} clean",
                "install-plugin": f"{mvn_wrapper} package -Pinstall-plugin",
                "run-server": f"{mvn_wrapper} package -Prun-server"
            }
        }
    else:
        package_json = {
            "name": f"{mod_config['name']}-hytale-mod",
            "private": True,
            "scripts": {
                "build": f"{gradle_wrapper} build",
                "clean": f"{gradle_wrapper} clean",
                "install-plugin": f"{gradle_wrapper} installPlugin",
                "run-server": f"{gradle_wrapper} runServer"
            }
        }
    (project_path / "package.json").write_text(json.dumps(package_json, indent=2))

    # extensions.json for recommended extensions
    extensions = {
        "recommendations": [
            "vscjava.vscode-java-pack",
            "redhat.java",
        ]
    }
    # Add build system specific extension
    if build_system == "maven":
        extensions["recommendations"].append("vscjava.vscode-maven")
    else:
        extensions["recommendations"].append("vscjava.vscode-gradle")

    (vscode_dir / "extensions.json").write_text(json.dumps(extensions, indent=2))

    print("    Created .vscode/ configuration")
    print("    Created package.json (NPM Scripts panel for easy run buttons)")


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


def create_project_structure(mod_config: dict, project_path: Path, hytale_path: str, toolkit_path: str, ides: list[str], gitignore_ide: bool = True, jdk_path: str = None, build_system: str = "maven", language: str = "java") -> bool:
    """Create the complete mod project structure."""
    if log:
        log_section(log, "Project Creation")
        log.info(f"Creating project at: {project_path}")
        log.info(f"Mod config: {json.dumps(mod_config, indent=2)}")
        log.info(f"Hytale path: {hytale_path}")
        log.info(f"Toolkit path: {toolkit_path}")
        log.info(f"IDEs: {ides}")
        log.info(f"Build system: {build_system}")
        log.info(f"Language: {language}")
        log.info(f"JDK path: {jdk_path}")

    print(f"\n  Creating project at: {project_path}")
    print(f"  Build system: {build_system.capitalize()}")
    print(f"  Language: {language.capitalize()}")
    print()

    try:
        # Create directory structure
        project_path.mkdir(parents=True, exist_ok=True)

        # Source directories - use kotlin or java based on language selection
        package_path = mod_config['group'].replace('.', '/') + '/' + mod_config['name'].replace('-', '').replace('_', '')
        source_lang = "kotlin" if language == "kotlin" else "java"
        src_main_code = project_path / "src" / "main" / source_lang / package_path
        src_main_resources = project_path / "src" / "main" / "resources"

        src_main_code.mkdir(parents=True, exist_ok=True)
        src_main_resources.mkdir(parents=True, exist_ok=True)

        print("    Created directory structure")

        # Main class - use appropriate language template and extension
        if language == "kotlin":
            main_class_file = src_main_code / f"{mod_config['main_class']}.kt"
            main_class_file.write_text(generate_main_class_kotlin(mod_config))
            print(f"    Created {mod_config['main_class']}.kt")
        else:
            main_class_file = src_main_code / f"{mod_config['main_class']}.java"
            main_class_file.write_text(generate_main_class(mod_config))
            print(f"    Created {mod_config['main_class']}.java")

        # manifest.json
        manifest = generate_manifest(mod_config)
        manifest_path = src_main_resources / "manifest.json"
        manifest_path.write_text(json.dumps(manifest, indent=2))
        print("    Created manifest.json")

        # Build system specific files
        if build_system == "maven":
            # pom.xml
            (project_path / "pom.xml").write_text(generate_pom_xml(mod_config, hytale_path, toolkit_path, language=language, jdk_path=jdk_path))
            print("    Created pom.xml")

            # Maven wrapper
            download_maven_wrapper(project_path, jdk_path=jdk_path)
        else:
            # build.gradle.kts
            (project_path / "build.gradle.kts").write_text(generate_build_gradle(mod_config, language=language))
            print("    Created build.gradle.kts")

            # settings.gradle.kts
            (project_path / "settings.gradle.kts").write_text(generate_settings_gradle(mod_config))
            print("    Created settings.gradle.kts")

            # gradle.properties
            (project_path / "gradle.properties").write_text(
                generate_gradle_properties(mod_config, hytale_path, toolkit_path, jdk_path)
            )
            print("    Created gradle.properties")

            # Gradle wrapper
            download_gradle_wrapper(project_path)

        # .gitignore
        (project_path / ".gitignore").write_text(generate_gitignore(ignore_ide_configs=gitignore_ide, build_system=build_system))
        print("    Created .gitignore")

        # README.md
        (project_path / "README.md").write_text(generate_readme(mod_config, build_system=build_system))
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

        # IDE configurations
        if "intellij" in ides:
            generate_intellij_config(mod_config, project_path, toolkit_path, build_system=build_system)

        if "vscode" in ides:
            generate_vscode_config(mod_config, project_path, toolkit_path, hytale_path, jdk_path, build_system=build_system)

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
            if log:
                log.info(f"User input: RAM allocation -> {default}GB (default)")
            return default
        try:
            value = int(response)
            if 2 <= value <= 64:
                if log:
                    log.info(f"User input: RAM allocation -> {value}GB")
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
#  CLI / Non-Interactive Mode
# ============================================================================

def create_mod_from_config(config: dict, quiet: bool = False) -> dict:
    """
    Create a mod project from a configuration dictionary.

    This function enables non-interactive mod creation for automation and LLM usage.

    Args:
        config: Dictionary with mod configuration:
            - name (required): Mod ID (lowercase, hyphens/underscores allowed)
            - group (required): Java package group (e.g., com.author)
            - parent_dir (required): Parent directory for the project
            - hytale_path (required): Path to Hytale installation
            - build_system (optional): Build system to use ("maven" or "gradle"), defaults to "maven"
            - language (optional): Programming language ("java" or "kotlin"), defaults to "java"
            - display_name (optional): Human-readable name
            - version (optional): Semantic version, defaults to "1.0.0"
            - main_class (optional): Main class name, defaults to formatted name
            - description (optional): Mod description
            - author_name (optional): Author name, defaults to "Unknown"
            - author_email (optional): Author email
            - author_url (optional): Author website
            - server_version (optional): Server version compatibility, defaults to "*"
            - license (optional): License type (MIT, Apache-2.0, GPL-3.0, None)
            - ides (optional): List of IDEs to configure ["intellij", "vscode"]
            - init_git (optional): Initialize git repo, defaults to True
            - skip_decompile (optional): Skip decompilation check, defaults to True
        quiet: Suppress output if True

    Returns:
        Dictionary with:
            - success: True if mod was created successfully
            - project_path: Path to created project (if successful)
            - error: Error message (if failed)
    """
    global log, log_file

    # Initialize logging
    log, log_file = setup_logging("init-mod-cli", SCRIPT_DIR)

    def output(msg):
        if not quiet:
            print(msg)

    # Validate required fields
    required = ["name", "group", "parent_dir", "hytale_path"]
    missing = [f for f in required if not config.get(f)]
    if missing:
        return {"success": False, "error": f"Missing required fields: {', '.join(missing)}"}

    # Validate mod ID
    error = validate_mod_id(config["name"])
    if error:
        return {"success": False, "error": f"Invalid mod name: {error}"}

    # Validate group
    error = validate_group(config["group"])
    if error:
        return {"success": False, "error": f"Invalid group: {error}"}

    # Validate Hytale installation
    is_valid, missing_items = validate_hytale_installation(config["hytale_path"])
    if not is_valid:
        return {"success": False, "error": f"Invalid Hytale installation. Missing: {', '.join(missing_items)}"}

    # Build system (default to maven)
    build_system = config.get("build_system", "maven")
    if build_system not in ("maven", "gradle"):
        return {"success": False, "error": f"Invalid build_system: {build_system}. Must be 'maven' or 'gradle'."}

    # Language (default to java)
    language = config.get("language", "java")
    if language not in ("java", "kotlin"):
        return {"success": False, "error": f"Invalid language: {language}. Must be 'java' or 'kotlin'."}

    # Build mod config with defaults
    mod_config = {
        "name": config["name"],
        "group": config["group"],
        "display_name": config.get("display_name", to_class_name(config["name"])),
        "version": config.get("version", "1.0.0"),
        "main_class": config.get("main_class", to_class_name(config["name"])),
        "description": config.get("description", ""),
        "author_name": config.get("author_name", "Unknown"),
        "author_email": config.get("author_email", ""),
        "author_url": config.get("author_url", ""),
        "server_version": config.get("server_version", "*"),
        "license": config.get("license", "MIT"),
    }

    # Validate version if provided
    if config.get("version"):
        error = validate_version(config["version"])
        if error:
            return {"success": False, "error": f"Invalid version: {error}"}

    parent_dir = Path(config["parent_dir"])
    if not parent_dir.exists():
        return {"success": False, "error": f"Parent directory does not exist: {parent_dir}"}

    project_path = parent_dir / config["name"]

    # Check if project already exists
    if project_path.exists():
        return {"success": False, "error": f"Project directory already exists: {project_path}"}

    # Detect JDK based on build system
    jdk_version = 25 if build_system == "maven" else 24
    jdk_path = detect_jdk_path(jdk_version)

    # IDEs to configure
    ides = config.get("ides", ["vscode"])

    output(f"  Creating mod: {mod_config['display_name']}")
    output(f"  Location: {project_path}")
    output(f"  Build system: {build_system.capitalize()}")
    output(f"  Language: {language.capitalize()}")

    # Create project structure
    success = create_project_structure(
        mod_config=mod_config,
        project_path=project_path,
        hytale_path=config["hytale_path"],
        toolkit_path=str(SCRIPT_DIR),
        ides=ides,
        gitignore_ide=True,
        jdk_path=jdk_path,
        build_system=build_system,
        language=language
    )

    if not success:
        return {"success": False, "error": "Failed to create project structure"}

    # Initialize git if requested
    if config.get("init_git", True):
        output("  Initializing git repository...")
        exit_code, _ = run_command(["git", "init"], cwd=project_path)
        if exit_code == 0:
            run_command(["git", "add", "."], cwd=project_path)
            run_command(["git", "commit", "-m", "Initial commit: Project scaffolding"], cwd=project_path)
            output("  Git repository initialized with initial commit")

    output(f"  Mod created successfully at: {project_path}")

    return {
        "success": True,
        "project_path": str(project_path),
        "mod_config": mod_config
    }


def parse_cli_args():
    """Parse command line arguments for non-interactive mode."""
    parser = argparse.ArgumentParser(
        description="Hytale Mod CLI Tool",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )

    parser.add_argument("--version", action="version", version=f"%(prog)s {__version__}")

    # Subcommands
    subparsers = parser.add_subparsers(dest="command", help="Available commands")

    # init subcommand
    init_parser = subparsers.add_parser(
        "init",
        help="Initialize a new Hytale mod project",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  Interactive mode (default):
    hytale-mod init

  Use current directory as project location:
    hytale-mod init --here

  Create mod with CLI arguments:
    hytale-mod init --name my-mod --group com.example --parent-dir ./projects --hytale-path "C:/path/to/hytale"

  Create mod from JSON config:
    hytale-mod init --config mod-config.json

  Output as JSON (for LLM/automation):
    hytale-mod init --name my-mod --group com.example --parent-dir . --hytale-path /path --json
"""
    )

    # Config file option
    init_parser.add_argument("--config", "-c", type=str, help="Path to JSON config file")

    # Directory options
    init_parser.add_argument("--here", action="store_true",
                             help="Use current directory as project location (skips folder picker)")

    # Direct arguments
    init_parser.add_argument("--name", "-n", type=str, help="Mod ID (lowercase, hyphens/underscores allowed)")
    init_parser.add_argument("--group", "-g", type=str, help="Java package group (e.g., com.author)")
    init_parser.add_argument("--parent-dir", "-d", type=str, help="Parent directory for the project")
    init_parser.add_argument("--hytale-path", "-H", type=str, help="Path to Hytale installation")

    # Optional arguments
    init_parser.add_argument("--build-system", "-b", type=str, choices=["maven", "gradle"], default="maven",
                        help="Build system to use (default: maven)")
    init_parser.add_argument("--language", "-l", type=str, choices=["java", "kotlin"], default="java",
                        help="Programming language to use (default: java)")
    init_parser.add_argument("--display-name", type=str, help="Human-readable mod name")
    init_parser.add_argument("--mod-version", type=str, default="1.0.0", help="Mod version (default: 1.0.0)")
    init_parser.add_argument("--main-class", type=str, help="Main class name")
    init_parser.add_argument("--description", type=str, help="Mod description")
    init_parser.add_argument("--author", type=str, help="Author name")
    init_parser.add_argument("--author-email", type=str, help="Author email")
    init_parser.add_argument("--author-url", type=str, help="Author website URL")
    init_parser.add_argument("--license", type=str, choices=["MIT", "Apache-2.0", "GPL-3.0", "None"], default="MIT")
    init_parser.add_argument("--ide", type=str, action="append", choices=["intellij", "vscode"], help="IDE to configure (can specify multiple)")
    init_parser.add_argument("--no-git", action="store_true", help="Don't initialize git repository")

    # Output options
    init_parser.add_argument("--json", "-j", action="store_true", help="Output result as JSON (implies --quiet)")
    init_parser.add_argument("--quiet", "-q", action="store_true", help="Suppress non-essential output")

    # Optional positional argument for project name shorthand
    init_parser.add_argument("project_name", nargs="?", type=str, help="Optional project name (shorthand for --name)")

    return parser.parse_args()


# ============================================================================
#  Main Wizard
# ============================================================================

def main():
    global log, log_file

    # Parse CLI arguments
    args = parse_cli_args()

    # Check if a command was provided
    if args.command is None:
        print("Usage: hytale-mod <command> [options]")
        print()
        print("Commands:")
        print("  init    Initialize a new Hytale mod project")
        print()
        print("Run 'hytale-mod <command> --help' for more information on a command.")
        return 0

    # Handle init command
    if args.command == "init":
        return run_init_command(args)

    return 0


def run_init_command(args):
    """Run the init subcommand."""
    global log, log_file

    # Handle positional project_name as shorthand for --name
    if args.project_name and not args.name:
        args.name = args.project_name

    # Check if we're in non-interactive mode
    if args.config or args.name:
        # Non-interactive mode
        if args.config:
            # Load config from file
            try:
                with open(args.config, 'r') as f:
                    config = json.load(f)
            except Exception as e:
                result = {"success": False, "error": f"Failed to load config file: {e}"}
                if args.json:
                    print(json.dumps(result))
                else:
                    print(f"ERROR: {result['error']}")
                return 1
        else:
            # Build config from arguments
            config = {
                "name": args.name,
                "group": args.group,
                "parent_dir": args.parent_dir,
                "hytale_path": args.hytale_path,
                "build_system": args.build_system,
                "language": args.language,
            }

            if args.display_name:
                config["display_name"] = args.display_name
            if args.mod_version:
                config["version"] = args.mod_version
            if args.main_class:
                config["main_class"] = args.main_class
            if args.description:
                config["description"] = args.description
            if args.author:
                config["author_name"] = args.author
            if args.author_email:
                config["author_email"] = args.author_email
            if args.author_url:
                config["author_url"] = args.author_url
            if args.license:
                config["license"] = args.license
            if args.ide:
                config["ides"] = args.ide
            config["init_git"] = not args.no_git

        # Create the mod
        quiet = args.quiet or args.json
        result = create_mod_from_config(config, quiet=quiet)

        if args.json:
            print(json.dumps(result, indent=2))
        elif not result["success"]:
            print(f"ERROR: {result['error']}")

        return 0 if result["success"] else 1

    # Interactive mode
    use_here = getattr(args, 'here', False)

    # Initialize logging for interactive mode
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
    print("    - Maven or Gradle build configuration")
    print("    - Plugin manifest and main class")
    print("    - IDE integration (IntelliJ / VS Code)")
    print("    - Reference to decompiled server source")
    print()

    input("  Press Enter to begin...")

    total_steps = 9

    # =========================================================================
    # Step 1: Hytale Installation
    # =========================================================================
    print_step(1, total_steps, "Hytale Installation")

    hytale_path = get_hytale_install_path()
    if not hytale_path:
        print("\n  Setup cancelled: Hytale installation is required.")
        sys.exit(1)

    # =========================================================================
    # Step 2: Build System & Language Selection
    # =========================================================================
    print_step(2, total_steps, "Build System & Language Selection")

    print("  Choose your preferred build system for the mod project.")
    print()

    build_system_options = [
        ("Maven (Recommended)", "Supports JDK 25, simpler configuration, standard Java build tool"),
        ("Gradle", "Uses HytaleGradle plugin with integrated runServer task (requires JDK 24)"),
    ]
    build_system_idx = prompt_choice(build_system_options, "Select build system")
    build_system = "maven" if build_system_idx == 0 else "gradle"

    if log:
        log.info(f"User selected build system: {build_system}")

    print()
    print("  Choose your preferred programming language.")
    print()

    language_options = [
        ("Java", "Standard choice for Hytale modding, familiar to most developers"),
        ("Kotlin", "Modern JVM language with concise syntax, null safety, and full Java interop"),
    ]
    language_idx = prompt_choice(language_options, "Select language")
    language = "java" if language_idx == 0 else "kotlin"

    if log:
        log.info(f"User selected language: {language}")

    # =========================================================================
    # Step 3: JDK Setup
    # =========================================================================
    print_step(3, total_steps, "Java Development Kit (JDK) Setup")

    # JDK version depends on build system
    jdk_version = 25 if build_system == "maven" else 24
    print(f"  {build_system.capitalize()} requires JDK {jdk_version}.")
    print(f"  This is used for building and compiling your mod.")
    print()

    jdk_path = ensure_jdk_installed(jdk_version)

    if jdk_path is None:
        print(f"\n  WARNING: JDK {jdk_version} not configured. Build may fail.")
        if not prompt_yes_no("Continue anyway?", default=False):
            print("\n  Setup cancelled.")
            sys.exit(1)

    # =========================================================================
    # Step 4: Project Location
    # =========================================================================
    print_step(4, total_steps, "Project Location")

    if use_here:
        # Use current working directory
        parent_folder = str(Path.cwd())
        print(f"  Using current directory: {parent_folder}")
        if log:
            log.info(f"Using --here flag, project location: {parent_folder}")
    else:
        print("  Select where to create your mod project.")
        print("  A new folder will be created with your mod's name.")
        print()

        input("  Press Enter to open folder picker...")

        # Start at current working directory
        initial_dir = str(Path.cwd())
        parent_folder = open_folder_picker("Select Parent Folder for Mod Project", initial_dir=initial_dir)

        if not parent_folder:
            parent_folder = input("  Enter path manually: ").strip().strip('"').strip("'")

        if not parent_folder or not Path(parent_folder).exists():
            print("  ERROR: Invalid folder selected.")
            sys.exit(1)

        if log:
            log.info(f"User selected project location: {parent_folder}")
        print(f"\n  Project will be created in: {parent_folder}")

    # =========================================================================
    # Step 5: Mod Metadata
    # =========================================================================
    print_step(5, total_steps, "Mod Configuration")

    print("  Let's configure your mod. Required fields are marked with *")
    print()

    mod_config = {}

    # Group (required)
    print("  * Group is your Java package prefix (e.g., com.yourname, io.github.username)")
    mod_config['group'] = prompt_string("Group", validator=validate_group)
    print()

    # Name/ID (required)
    print("  * Name is your mod's unique identifier (lowercase, hyphens/underscores allowed)")
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
    print("  Use '*' for any version, or a SemVer range like '>=1.0.0'")
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
    # Step 6: IDE Selection
    # =========================================================================
    print_step(6, total_steps, "IDE Configuration")

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
    # Step 7: Git Repository
    # =========================================================================
    print_step(7, total_steps, "Version Control")

    init_git = prompt_yes_no("Initialize a git repository?", default=True)

    # =========================================================================
    # Step 8: Decompiled Source
    # =========================================================================
    print_step(8, total_steps, "Decompiled Source Code")

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
    # Step 9: Create Project
    # =========================================================================
    print_step(9, total_steps, "Creating Project")

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
        jdk_path=jdk_path,
        build_system=build_system,
        language=language
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

    # Set JAVA_HOME for the build
    build_env = None
    if jdk_path:
        build_env = {"JAVA_HOME": jdk_path}

    if build_system == "maven":
        mvnw_script = "mvnw.cmd" if platform.system() == "Windows" else "./mvnw"

        if (project_path / mvnw_script.replace("./", "")).exists():
            exit_code, output = run_command(
                [mvnw_script, "clean", "package", "-DskipTests"],
                cwd=project_path,
                env=build_env
            )
            if exit_code == 0:
                print("    Build successful!")
            else:
                print("    Warning: Initial build had issues.")
                print(f"    Run 'mvnw package' in {project_path} for details.")
        elif command_exists("mvn"):
            exit_code, output = run_command(["mvn", "clean", "package", "-DskipTests"], cwd=project_path, env=build_env)
            if exit_code == 0:
                print("    Build successful!")
            else:
                print("    Warning: Initial build had issues.")
                print(f"    Run 'mvn package' in {project_path} for details.")
        else:
            print("    Maven not found. Please install Maven and run 'mvn package'")
            print("    to verify the project setup.")
    else:
        gradlew_script = "gradlew.bat" if platform.system() == "Windows" else "./gradlew"

        if (project_path / gradlew_script.replace("./", "")).exists():
            exit_code, output = run_command(
                [gradlew_script, "build", "-x", "test"],
                cwd=project_path,
                env=build_env
            )
            if exit_code == 0:
                print("    Build successful!")
            else:
                print("    Warning: Initial build had issues. This may be normal if")
                print("    the HytaleGradle plugin needs additional setup.")
                print(f"    Run 'gradlew build' in {project_path} for details.")
        elif command_exists("gradle"):
            exit_code, output = run_command(["gradle", "build", "-x", "test"], cwd=project_path, env=build_env)
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
    print(f"  Build system: {build_system.capitalize()}")
    print()

    # Show JDK info
    jdk_version = 25 if build_system == "maven" else 24
    if jdk_path:
        jdk_display = jdk_path.replace("/", "\\") if platform.system() == "Windows" else jdk_path
        print(f"  JDK {jdk_version} configured at: {jdk_display}")
        if build_system == "maven":
            print("  (Set JAVA_HOME to this path when building)")
        else:
            print("  (Gradle will use this automatically via gradle.properties)")
    else:
        print(f"  WARNING: JDK {jdk_version} not configured.")
        if build_system == "maven":
            print(f"  You may need to install JDK {jdk_version} and set JAVA_HOME")
        else:
            print(f"  You may need to install JDK {jdk_version} and update gradle.properties:")
            print(f"    org.gradle.java.home=/path/to/jdk-{jdk_version}")
    print()

    print("  Next steps:")
    print(f"    1. Open the project in your IDE")
    source_lang = "kotlin" if language == "kotlin" else "java"
    file_ext = "kt" if language == "kotlin" else "java"
    print(f"    2. Edit src/main/{source_lang}/.../{ mod_config['main_class']}.{file_ext}")
    if build_system == "maven":
        print(f"    3. Run 'mvnw package -Prun-server' to test your mod")
    else:
        print(f"    3. Run 'gradlew runServer' to test your mod")
    print()
    print("  Useful commands:")
    if build_system == "maven":
        print("    mvnw package              - Build the mod")
        print("    mvnw package -Prun-server - Run server with mod installed")
        print("    mvnw package -Pinstall-plugin - Install mod without starting server")
    else:
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
        exit_code = main()
        if exit_code is not None:
            sys.exit(exit_code)
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
