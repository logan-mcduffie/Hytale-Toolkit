#!/usr/bin/env python3
"""
Hytale Toolkit - Installation Wizard

A comprehensive setup script that guides you through:
1. Selecting your Hytale installation
2. Decompiling server source code
3. Generating Javadocs (optional)
4. Setting up the RAG database
5. Configuring Claude Code integration
"""

import json
import os
import platform
import re
import shutil
import subprocess
import sys
import tarfile
import time
import urllib.request
from pathlib import Path

# Add parent directory to path for shared utilities
sys.path.insert(0, str(Path(__file__).parent.parent / "tools"))
from logger import setup_logging, log_command, log_exception, log_section

# Global logger (initialized in main)
log = None
log_file = None

# ============================================================================
#  Configuration
# ============================================================================

GITHUB_REPO = "logan-mcduffie/Hytale-Toolkit"
OLLAMA_MODEL = "nomic-embed-text"
JDK_VERSION = 21  # LTS version for javadoc generation

SCRIPT_DIR = Path(__file__).parent.resolve()
REPO_ROOT = SCRIPT_DIR.parent
VINEFLOWER_JAR = REPO_ROOT / "tools" / "vineflower.jar"
ENV_FILE = SCRIPT_DIR / ".env"
DECOMPILED_DIR = REPO_ROOT / "decompiled"  # Always in repo root
JAVADOCS_DIR = REPO_ROOT / "javadocs"      # Also in repo root

# Required contents of a valid Hytale installation
REQUIRED_CONTENTS = ["Client", "Server", "Assets.zip"]

# Data tables
DATA_TABLES = ["hytale_methods.lance", "hytale_client_ui.lance", "hytale_gamedata.lance"]


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


def prompt_ram_allocation(default: int = 8) -> int:
    """Prompt user for RAM allocation in GB. Returns the value in GB."""
    print(f"  How much RAM (in GB) should be allocated for decompilation/docs?")
    print(f"  Default is {default}GB which is usually enough.")
    print(f"  If you encounter OutOfMemory errors, try increasing this value.")
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


def is_admin() -> bool:
    """Check if running with admin/root privileges."""
    if platform.system() == "Windows":
        try:
            import ctypes
            return ctypes.windll.shell32.IsUserAnAdmin() != 0
        except:
            return False
    return os.geteuid() == 0


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
            cmd, cwd=cwd, capture_output=True, encoding='utf-8', errors='replace', shell=use_shell, env=merged_env
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
#  .env File Management
# ============================================================================

def load_env() -> dict[str, str]:
    """Load existing .env file."""
    env = {}
    if ENV_FILE.exists():
        with open(ENV_FILE, "r") as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#") and "=" in line:
                    key, value = line.split("=", 1)
                    env[key.strip()] = value.strip()
    return env


def save_env(env: dict[str, str]):
    """Save .env file."""
    with open(ENV_FILE, "w") as f:
        for key, value in env.items():
            f.write(f"{key}={value}\n")


# ============================================================================
#  Folder Picker & Validation
# ============================================================================

def open_folder_picker(title: str = "Select Folder") -> str | None:
    """Open a native folder picker dialog."""
    try:
        import tkinter as tk
        from tkinter import filedialog

        root = tk.Tk()
        root.withdraw()
        root.attributes('-topmost', True)

        folder = filedialog.askdirectory(title=title)
        root.destroy()

        return folder if folder else None
    except ImportError:
        return None


def validate_hytale_installation(folder: str) -> tuple[bool, list[str]]:
    """
    Validate that a folder is a valid Hytale installation.
    Returns (is_valid, missing_items).
    """
    folder_path = Path(folder)

    if log:
        log.debug(f"Validating Hytale installation at: {folder}")

    if not folder_path.exists():
        if log:
            log.warning(f"Folder does not exist: {folder}")
        return False, ["Folder does not exist"]

    missing = []
    for item in REQUIRED_CONTENTS:
        item_path = folder_path / item
        exists = item_path.exists()
        if log:
            log.debug(f"  Checking {item}: {'found' if exists else 'MISSING'}")
        if not exists:
            missing.append(item)

    is_valid = len(missing) == 0
    if log:
        if is_valid:
            log.info(f"Valid Hytale installation found at: {folder}")
        else:
            log.warning(f"Invalid installation - missing: {missing}")

    return is_valid, missing


def get_hytale_install_path(env: dict[str, str]) -> str | None:
    """Get Hytale install path from env or prompt user."""

    # Check for existing saved path
    existing = env.get("HYTALE_INSTALL_PATH", "")
    if existing:
        is_valid, missing = validate_hytale_installation(existing)
        if is_valid:
            print(f"  Found saved Hytale installation: {existing}")
            if prompt_yes_no("Use this path?", default=True):
                return existing
        else:
            print(f"  Saved path is no longer valid (missing: {', '.join(missing)})")

    # Show instructions
    print()
    print("  Please select your Hytale installation folder.")
    print()
    print("  The path should look like:")
    print("    <drive>:\\...\\Roaming\\install\\release\\package\\game\\latest")
    print()
    print("  The folder MUST contain:")
    print("    - Client\\       (client data files)")
    print("    - Server\\       (HytaleServer.jar)")
    print("    - Assets.zip    (game assets)")
    print()

    max_attempts = 3
    for attempt in range(max_attempts):
        if attempt > 0:
            print(f"\n  Attempt {attempt + 1}/{max_attempts}")

        print("  A folder picker will open. If it doesn't appear, you can type the path.")
        input("  Press Enter to open folder picker...")

        folder = open_folder_picker("Select Hytale Installation Folder (the 'latest' folder)")

        if not folder:
            print()
            folder = input("  Enter path manually: ").strip().strip('"').strip("'")

        if not folder:
            print("  No path provided.")
            continue

        # Validate
        is_valid, missing = validate_hytale_installation(folder)

        if is_valid:
            print(f"\n  Valid installation found!")
            print(f"    Client/     : Found")
            print(f"    Server/     : Found")
            print(f"    Assets.zip  : Found")
            if log:
                log.info(f"User selected Hytale installation path: {folder}")
            return folder
        else:
            print(f"\n  ERROR: Invalid Hytale installation folder.")
            print(f"  Missing: {', '.join(missing)}")
            print()
            print("  Make sure you selected the 'latest' folder that contains")
            print("  Client/, Server/, and Assets.zip")

            if attempt < max_attempts - 1:
                if not prompt_yes_no("Try again?", default=True):
                    return None

    print("\n  Maximum attempts reached.")
    return None


# ============================================================================
#  Decompilation
# ============================================================================

def decompile_server(install_path: str, env: dict[str, str], ram_gb: int = 8) -> bool:
    """Decompile HytaleServer.jar using Vineflower."""
    if log:
        log_section(log, "Decompilation")
        log.info(f"Starting decompilation with {ram_gb}GB RAM")

    server_jar = Path(install_path) / "Server" / "HytaleServer.jar"

    if not server_jar.exists():
        if log:
            log.error(f"HytaleServer.jar not found at {server_jar}")
        print(f"  ERROR: HytaleServer.jar not found at {server_jar}")
        return False

    if not VINEFLOWER_JAR.exists():
        if log:
            log.error(f"Vineflower not found at {VINEFLOWER_JAR}")
        print(f"  ERROR: Vineflower not found at {VINEFLOWER_JAR}")
        return False

    # Check for existing decompiled code
    if DECOMPILED_DIR.exists() and any(DECOMPILED_DIR.iterdir()):
        print(f"  Decompiled code already exists at: {DECOMPILED_DIR}")
        if not prompt_yes_no("Re-decompile? (This will delete existing files)", default=False):
            return True
        shutil.rmtree(DECOMPILED_DIR)

    DECOMPILED_DIR.mkdir(parents=True, exist_ok=True)

    print()
    print(f"  Source:  {server_jar}")
    print(f"  Output:  {DECOMPILED_DIR}")
    print(f"  RAM:     {ram_gb}GB")
    print()
    print("  Decompiling... (this may take several minutes)")
    print()

    # Run Vineflower with progress output
    cmd = [
        "java",
        "-Xms2G",           # Initial heap size
        f"-Xmx{ram_gb}G",   # Maximum heap size (user-configured)
        "-jar", str(VINEFLOWER_JAR),
        "-dgs=1",  # Decompile generic signatures
        "-asc=1",  # ASCII string characters
        "-rsy=1",  # Remove synthetic class members
        str(server_jar),
        str(DECOMPILED_DIR)
    ]

    try:
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            encoding='utf-8',
            errors='replace',
            bufsize=1
        )

        # Show progress with single-line updates
        class_count = 0
        for line in process.stdout:
            line = line.strip()
            if line:
                # Handle "INFO:  Loading Class:" or "Decompiling class" lines
                if "Loading Class:" in line or "Decompiling class" in line:
                    class_count += 1
                    # Extract class name from the line
                    if "Loading Class:" in line:
                        class_name = line.split("Loading Class:")[-1].split()[0] if "Loading Class:" in line else ""
                    else:
                        class_name = line.split()[-1] if line.split() else ""
                    short_name = class_name.split("/")[-1] if "/" in class_name else class_name
                    # Overwrite the same line with progress
                    print(f"\r  [{class_count:,} classes] {short_name[:45]:<45}", end="", flush=True)
                elif "error" in line.lower() and "info:" not in line.lower():
                    # Skip benign decompiler warnings
                    if any(skip in line for skip in [
                        "Cannot copy entry META-INF",
                        "cannot be decomposed",
                        "Unable to simplify switch",
                    ]):
                        continue
                    # Only show actual errors, not INFO lines containing 'error' in class names
                    print(f"\n  {line}")

        process.wait()
        print()  # New line after progress

        if process.returncode == 0:
            if log:
                log.info(f"Decompilation completed successfully ({class_count} classes)")
            print("  Decompilation complete!")
            # Fix decompilation artifacts for javadoc compatibility
            fix_decompiled_files()
            return True
        else:
            if log:
                log.error(f"Decompilation failed with exit code {process.returncode}")
            print(f"  Decompilation failed with exit code {process.returncode}")
            return False

    except FileNotFoundError:
        if log:
            log.error("Java not found in PATH")
            log_exception(log, "decompile_server")
        print("\n  ERROR: Java not found. Please install Java and try again.")
        return False
    except Exception as e:
        if log:
            log.error(f"Decompilation exception: {e}")
            log_exception(log, "decompile_server")
        print(f"\n  ERROR: {e}")
        return False


# ============================================================================
#  Fix Decompilation Artifacts
# ============================================================================

def fix_decompiled_file(filepath: Path) -> bool:
    """
    Fix a single Java file with decompilation artifacts.
    Returns True if modifications were made.
    """
    try:
        content = filepath.read_text(encoding='utf-8', errors='replace')
    except Exception:
        return False

    original = content

    # Replace <unrepresentable> with a valid Java identifier
    # This token appears where the decompiler couldn't resolve a class name
    content = content.replace('<unrepresentable>', 'DecompilerPlaceholder')

    # Replace $assertionsDisabled references with false
    # These are assertion-related checks that aren't meaningful in decompiled code
    content = re.sub(r'DecompilerPlaceholder\.\$assertionsDisabled', 'false', content)

    # Fix interfaces with static initializer blocks (not valid Java)
    if re.search(r'^public\s+interface\s+\w+', content, re.MULTILINE):
        # Find CODEC field declaration without initialization
        codec_match = re.search(r'(BuilderCodecMapCodec<[^>]+>)\s+CODEC\s*;', content)
        if codec_match:
            # Find the initialization in the static block
            init_match = re.search(r'CODEC\s*=\s*(new\s+BuilderCodecMapCodec<>\([^)]*\))\s*;', content)
            if init_match:
                # Replace uninitialized field with initialized one
                content = re.sub(
                    r'(BuilderCodecMapCodec<[^>]+>)\s+CODEC\s*;',
                    f'\\1 CODEC = {init_match.group(1)};',
                    content
                )

        # Remove static blocks from interfaces (they're not allowed)
        lines = content.split('\n')
        new_lines = []
        in_static_block = False
        brace_count = 0

        for line in lines:
            stripped = line.strip()
            if stripped.startswith('static {') or stripped == 'static {':
                in_static_block = True
                brace_count = line.count('{') - line.count('}')
                continue

            if in_static_block:
                brace_count += line.count('{') - line.count('}')
                if brace_count <= 0:
                    in_static_block = False
                continue

            new_lines.append(line)

        content = '\n'.join(new_lines)

    if content != original:
        try:
            filepath.write_text(content, encoding='utf-8')
            return True
        except Exception:
            return False

    return False


def fix_decompiled_files() -> int:
    """
    Fix decompilation artifacts in all Java files.
    Returns the number of files fixed.
    """
    if not DECOMPILED_DIR.exists():
        return 0

    print("  Fixing decompilation artifacts...")

    count = 0
    java_files = list(DECOMPILED_DIR.rglob("*.java"))
    total = len(java_files)

    for i, filepath in enumerate(java_files):
        if fix_decompiled_file(filepath):
            count += 1
        # Show progress every 100 files
        if (i + 1) % 100 == 0:
            print(f"\r  Processed {i + 1}/{total} files ({count} fixed)...", end="", flush=True)

    print(f"\r  Fixed {count} files with decompilation artifacts.    ")
    return count


# ============================================================================
#  Javadoc Generation
# ============================================================================

def generate_javadocs(include_private: bool = False, ram_gb: int = 8, javadoc_path: str = "javadoc") -> bool:
    """Generate Javadocs from decompiled source."""
    if not DECOMPILED_DIR.exists():
        print("  ERROR: No decompiled code found. Please decompile first.")
        return False

    if JAVADOCS_DIR.exists():
        if not prompt_yes_no("Javadocs already exist. Regenerate?", default=False):
            return True
        shutil.rmtree(JAVADOCS_DIR)

    JAVADOCS_DIR.mkdir(parents=True, exist_ok=True)

    # Ensure decompilation artifacts are fixed before javadoc
    fix_decompiled_files()

    print()
    print(f"  Generating Javadocs (using {ram_gb}GB RAM)... (this may take a while)")
    print()

    # Find Hytale Java files only (com/hypixel/**), excluding third-party libs
    # Also exclude package-info.java (malformed from decompilation)
    hytale_src = DECOMPILED_DIR / "com" / "hypixel"
    if not hytale_src.exists():
        print(f"  ERROR: Hytale source not found at {hytale_src}")
        return False
    java_files = [f for f in hytale_src.rglob("*.java") if f.name != "package-info.java"]
    print(f"  Found {len(java_files)} Hytale Java files (excluding third-party libs)")

    # Build javadoc command (no -quiet so we can show progress)
    cmd = [
        javadoc_path,
        "-J-Xms2G",             # Initial heap size
        f"-J-Xmx{ram_gb}G",     # Maximum heap size (user-configured)
        "-d", str(JAVADOCS_DIR),
        "-Xdoclint:none",       # Suppress documentation warnings
        "--ignore-source-errors",  # Continue despite syntax errors in decompiled code
    ]

    if include_private:
        cmd.append("-private")

    # Add source files (via argfile to avoid command line length limits)
    # Use forward slashes - Java's @argfile parser treats backslashes as escape chars
    argfile = SCRIPT_DIR / ".javadoc-files.txt"
    with open(argfile, "w") as f:
        for java_file in java_files:
            path_str = str(java_file).replace("\\", "/")
            f.write(f'"{path_str}"\n')

    cmd.append(f"@{argfile}")

    try:
        # Use Popen to stream output and show progress
        process = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            encoding='utf-8',
            errors='replace',
            bufsize=1
        )

        # Third-party packages to suppress errors for (missing imports are expected)
        suppressed_packages = [
            "io.sentry", "io.netty", "it.unimi.dsi", "javax.annotation",
            "com.google.common", "org.bson", "ch.randelshofer", "joptsimple",
            "org.bouncycastle", "com.github.luben", "com.nimbusds", "org.slf4j",
        ]

        file_count = 0
        gen_count = 0
        error_count = 0
        total_files = len(java_files)
        # Estimate ~1.5x files for generation (classes + packages + index files)
        estimated_gen_total = int(total_files * 1.5)

        for line in process.stdout:
            line = line.strip()
            if line:
                # Count "Loading source file" lines for progress
                if "Loading source file" in line:
                    file_count += 1
                    # Extract just the class name
                    parts = line.split("Loading source file")[-1].strip()
                    class_name = parts.split("/")[-1].replace(".java...", "")
                    percent = (file_count * 100) // total_files
                    print(f"\r  [{percent:3d}%] Loading: {class_name[:50]:<50}", end="", flush=True)
                elif "Generating" in line:
                    # Show generation progress
                    gen_count += 1
                    percent = min(99, (gen_count * 100) // estimated_gen_total)
                    # Extract the file being generated
                    gen_file = line.split("/")[-1].replace("...", "") if "/" in line else "docs"
                    print(f"\r  [{percent:3d}%] Generating: {gen_file[:50]:<50}", end="", flush=True)
                elif "error:" in line.lower() and "warning" not in line.lower():
                    # Suppress errors about missing third-party packages
                    # Also suppress "cannot find symbol" errors (cascading from missing imports)
                    if any(pkg in line for pkg in suppressed_packages):
                        error_count += 1
                    elif "cannot find symbol" in line or "package" in line and "does not exist" in line:
                        error_count += 1
                    else:
                        # Show non-suppressed errors
                        print(f"\n  {line}")

        process.wait()
        print(f"\r  [100%] Done!{' ' * 60}")  # Clear line and show completion

        if error_count > 0:
            print(f"  (Suppressed {error_count} missing third-party import errors)")

        # Check if any output was actually generated
        index_html = JAVADOCS_DIR / "index.html"
        generated_files = list(JAVADOCS_DIR.glob("**/*.html"))

        if index_html.exists():
            argfile.unlink()  # Clean up on success
            print(f"  Javadocs generated successfully!")
            print(f"  Generated {len(generated_files)} HTML files.")
            print(f"  Open {index_html} in a browser to view.")
            return True
        elif generated_files:
            argfile.unlink()  # Clean up if we got partial output
            print("  Javadoc generation completed with warnings.")
            print(f"  Generated {len(generated_files)} HTML files.")
            return True
        else:
            print("  ERROR: Javadoc failed to generate any output.")
            print(f"  Argfile preserved at: {argfile}")
            return False
    except Exception as e:
        print(f"  ERROR: {e}")
        return False


# ============================================================================
#  Node.js / npm Setup
# ============================================================================

def check_npm_installed() -> bool:
    """Check if npm is installed."""
    return command_exists("npm")


def check_node_installed() -> bool:
    """Check if Node.js is installed."""
    return command_exists("node")


def install_nodejs() -> bool:
    """Install Node.js (which includes npm) based on the current OS."""
    system = platform.system()

    if system == "Windows":
        if not is_admin():
            print("\n  ERROR: Administrator privileges required to install Node.js.")
            print("  Please restart this script as Administrator, or install Node.js manually:")
            print("    https://nodejs.org/")
            return False
        print("  Installing Node.js via winget...", flush=True)
        exit_code, output = run_command([
            "winget", "install", "-e", "--id", "OpenJS.NodeJS.LTS",
            "--accept-source-agreements", "--accept-package-agreements"
        ])
        if exit_code != 0:
            print(f"  Installation failed. Please install manually: https://nodejs.org/")
            return False

    elif system == "Darwin":  # macOS
        if command_exists("brew"):
            print("  Installing Node.js via Homebrew...", flush=True)
            exit_code, output = run_command(["brew", "install", "node"], shell=False)
            if exit_code != 0:
                print(f"  Installation failed. Please install manually: https://nodejs.org/")
                return False
        else:
            print("  Homebrew not found. Please install Node.js manually:")
            print("    https://nodejs.org/")
            print("  Or install Homebrew first: https://brew.sh")
            return False

    elif system == "Linux":
        print("  Installing Node.js...", flush=True)
        # Use NodeSource setup script for latest LTS
        exit_code, output = run_command(
            ["bash", "-c", "curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash - && sudo apt-get install -y nodejs"],
            shell=False
        )
        if exit_code != 0:
            # Try alternative for non-Debian systems
            print("  Debian-based install failed, trying alternative...")
            exit_code, output = run_command(
                ["bash", "-c", "curl -fsSL https://rpm.nodesource.com/setup_lts.x | sudo bash - && sudo yum install -y nodejs"],
                shell=False
            )
            if exit_code != 0:
                print(f"  Installation failed. Please install manually: https://nodejs.org/")
                return False

    else:
        print(f"  Unsupported OS: {system}")
        print("  Please install Node.js manually: https://nodejs.org/")
        return False

    print("  Node.js installed!")
    return True


def setup_npm() -> bool:
    """Ensure npm is installed, installing Node.js if needed."""
    print("  Checking for npm...", flush=True)

    if check_npm_installed():
        # Get version for display
        exit_code, output = run_command(["npm", "--version"])
        version = output.strip() if exit_code == 0 else "unknown"
        print(f"  npm is installed (v{version}).")
        return True

    print("  npm is not installed.")
    print()
    print("  npm is required to run the MCP server. It comes bundled with Node.js.")
    print()

    if not prompt_yes_no("Install Node.js (includes npm)?", default=True):
        print("  Please install Node.js manually: https://nodejs.org/")
        return False

    if not install_nodejs():
        return False

    # Verify installation worked
    # On Windows, we may need to refresh the PATH
    if platform.system() == "Windows":
        print("  Verifying installation...", flush=True)
        # Try to find npm in common install locations
        possible_paths = [
            Path(os.environ.get("ProgramFiles", "C:\\Program Files")) / "nodejs",
            Path(os.environ.get("ProgramFiles(x86)", "C:\\Program Files (x86)")) / "nodejs",
            Path.home() / "AppData" / "Roaming" / "npm",
        ]
        for npm_dir in possible_paths:
            npm_exe = npm_dir / "npm.cmd"
            if npm_exe.exists():
                print(f"  Found npm at: {npm_dir}")
                print()
                print("  NOTE: You may need to restart your terminal for npm to be in your PATH.")
                print("  If the next step fails, please close and reopen your terminal,")
                print("  then run setup.py again.")
                print()
                return True

    if check_npm_installed():
        print("  npm is now available!")
        return True

    print("  WARNING: npm was installed but may not be in PATH yet.")
    print("  Please restart your terminal and run setup.py again.")
    return False


# ============================================================================
#  Ollama Setup
# ============================================================================

def check_ollama_installed() -> bool:
    return command_exists("ollama")


def check_ollama_running() -> bool:
    try:
        req = urllib.request.Request("http://localhost:11434/api/tags")
        with urllib.request.urlopen(req, timeout=5) as response:
            return response.status == 200
    except:
        return False


def check_ollama_model_available(model: str = OLLAMA_MODEL) -> bool:
    try:
        req = urllib.request.Request("http://localhost:11434/api/tags")
        with urllib.request.urlopen(req, timeout=5) as response:
            data = json.loads(response.read().decode())
            models = data.get("models", [])
            return any(m["name"] == model or m["name"].startswith(f"{model}:") for m in models)
    except:
        return False


def install_ollama() -> bool:
    """Install Ollama based on the current OS."""
    system = platform.system()

    if system == "Windows":
        if not is_admin():
            print("\n  ERROR: Administrator privileges required to install Ollama.")
            print("  Please restart this script as Administrator.")
            return False
        print("  Installing Ollama via winget...", flush=True)
        exit_code, output = run_command([
            "winget", "install", "-e", "--id", "Ollama.Ollama",
            "--accept-source-agreements", "--accept-package-agreements"
        ])
        if exit_code != 0:
            print(f"  Installation failed. Please install manually: https://ollama.com/download")
            return False

    elif system == "Darwin":  # macOS
        if not command_exists("brew"):
            print("  Homebrew is required. Install from: https://brew.sh")
            return False
        print("  Installing Ollama via Homebrew...", flush=True)
        exit_code, output = run_command(["brew", "install", "ollama"], shell=False)
        if exit_code != 0:
            print(f"  Installation failed. Please install manually: https://ollama.com/download")
            return False

    elif system == "Linux":
        print("  Installing Ollama...", flush=True)
        exit_code, output = run_command(
            ["bash", "-c", "curl -fsSL https://ollama.com/install.sh | sh"],
            shell=False
        )
        if exit_code != 0:
            print(f"  Installation failed. Please install manually: https://ollama.com/download")
            return False

    else:
        print(f"  Unsupported OS: {system}")
        return False

    print("  Ollama installed!")
    return True


def start_ollama() -> bool:
    """Start Ollama server in the background."""
    system = platform.system()

    if system == "Windows":
        subprocess.Popen(
            ["ollama", "serve"],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            creationflags=subprocess.CREATE_NEW_PROCESS_GROUP | subprocess.DETACHED_PROCESS
        )
    else:
        subprocess.Popen(
            ["ollama", "serve"],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            start_new_session=True
        )

    # Wait for startup
    for _ in range(15):
        time.sleep(1)
        if check_ollama_running():
            return True
    return False


def pull_ollama_model(model: str = OLLAMA_MODEL) -> bool:
    """Pull the required embedding model with progress."""
    print(f"  Downloading {model} model...", flush=True)
    print()

    try:
        process = subprocess.Popen(
            ["ollama", "pull", model],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            encoding='utf-8',
            errors='replace'
        )

        for line in process.stdout:
            line = line.strip()
            if line:
                print(f"  {line}")

        process.wait()
        return process.returncode == 0
    except Exception as e:
        print(f"  ERROR: {e}")
        return False


def setup_ollama() -> bool:
    """Full Ollama setup: install, start, pull model."""
    print("  Checking Ollama...", flush=True)

    if not check_ollama_installed():
        print("  Ollama is not installed.")
        if not prompt_yes_no("Install Ollama?", default=True):
            print("  Please install Ollama manually: https://ollama.com/download")
            return False
        if not install_ollama():
            return False

    print("  Ollama is installed.")

    if not check_ollama_running():
        print("  Starting Ollama server...", flush=True)
        if not start_ollama():
            print("  Failed to start. Please run 'ollama serve' manually.")
            return False
    print("  Ollama server is running.")

    if not check_ollama_model_available(OLLAMA_MODEL):
        print(f"  Model '{OLLAMA_MODEL}' not found.")
        if not pull_ollama_model(OLLAMA_MODEL):
            return False
    print(f"  Model '{OLLAMA_MODEL}' is ready.")

    return True


# ============================================================================
#  JDK Setup (for Javadoc generation)
# ============================================================================

def detect_jdk_path(version: int) -> str | None:
    """Try to detect JDK installation path for a specific version."""
    system = platform.system()
    version_str = str(version)
    possible_paths = []

    if system == "Windows":
        possible_paths = [
            Path(f"C:/Program Files/Java/jdk-{version}"),
            Path(f"C:/Program Files/Eclipse Adoptium/jdk-{version}"),
        ]
        # Check JAVA_HOME if it contains the version
        java_home = os.environ.get("JAVA_HOME", "")
        if version_str in java_home:
            possible_paths.insert(0, Path(java_home))

        # Check for versioned directories like jdk-21.0.1
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

    javadoc_exe = "javadoc.exe" if system == "Windows" else "javadoc"
    for path in possible_paths:
        if path and path.exists() and (path / "bin" / javadoc_exe).exists():
            return str(path)

    return None


def get_adoptium_download_url(version: int) -> tuple[str, str] | None:
    """Get the Adoptium download URL for a specific JDK version.

    Returns (url, filename) or None if not available.
    """
    system = platform.system()
    machine = platform.machine().lower()

    # Map platform to Adoptium API values
    if system == "Windows":
        os_name = "windows"
    elif system == "Darwin":
        os_name = "mac"
    else:
        os_name = "linux"

    # Map architecture
    if machine in ("x86_64", "amd64"):
        arch = "x64"
    elif machine in ("aarch64", "arm64"):
        arch = "aarch64"
    else:
        arch = "x64"  # Default fallback

    # Try to get the latest release info from Adoptium API
    api_url = f"https://api.adoptium.net/v3/assets/latest/{version}/ga"
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
                percent = min(100, block_num * block_size * 100 // total_size)
                mb_downloaded = block_num * block_size / (1024 * 1024)
                mb_total = total_size / (1024 * 1024)
                print(f"\r    [{percent:3d}%] {mb_downloaded:.1f} / {mb_total:.1f} MB", end="", flush=True)

        urllib.request.urlretrieve(download_url, archive_path, reporthook=report_progress)
        print()  # Newline after progress
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


def get_jdk_javadoc_path() -> str | None:
    """Get the path to javadoc, downloading JDK if needed.

    Returns the full path to javadoc executable, or None if unavailable.
    """
    # First check if javadoc is already in PATH
    if command_exists("javadoc"):
        return "javadoc"  # Use system javadoc

    # Try to detect installed JDK
    jdk_path = detect_jdk_path(JDK_VERSION)
    if jdk_path:
        javadoc_exe = "javadoc.exe" if platform.system() == "Windows" else "javadoc"
        return str(Path(jdk_path) / "bin" / javadoc_exe)

    # JDK not found - offer to download
    print(f"  JDK not found. Javadoc requires a Java Development Kit.")
    print()

    options = [
        ("Download JDK automatically", f"Downloads JDK {JDK_VERSION} from Eclipse Adoptium (~200MB)"),
        ("Skip Javadoc generation", "Continue without generating Javadocs"),
    ]

    choice = prompt_choice(options, "How would you like to proceed?")

    if choice == 0:
        # Download JDK
        jdks_dir = SCRIPT_DIR / ".jdks"
        print()
        jdk_path = download_and_extract_jdk(JDK_VERSION, jdks_dir)
        if jdk_path:
            print(f"    JDK {JDK_VERSION} installed successfully!")
            javadoc_exe = "javadoc.exe" if platform.system() == "Windows" else "javadoc"
            return str(Path(jdk_path) / "bin" / javadoc_exe)
        else:
            print("    WARNING: Failed to install JDK")
            return None

    # User chose to skip
    return None


# ============================================================================
#  Database Download & Verification
# ============================================================================

def verify_database_files(lancedb_dir: Path) -> tuple[bool, str]:
    """
    Verify database files exist and appear valid.
    Returns (success, error_message).
    """
    if not lancedb_dir.exists():
        return False, "Database directory does not exist"

    print("  Verifying database files...")
    print()

    # Collect all files to verify
    all_files = []
    for table in DATA_TABLES:
        table_dir = lancedb_dir / table
        if not table_dir.exists():
            return False, f"Missing table: {table}"
        # Collect all files in the table directory
        table_files = list(table_dir.rglob("*"))
        all_files.extend([(table, f) for f in table_files if f.is_file()])

    if not all_files:
        return False, "No files found in database"

    total_files = len(all_files)
    verified = 0
    errors = []
    current_table = ""

    bar_width = 30

    for table, filepath in all_files:
        # Update display when table changes
        if table != current_table:
            current_table = table
            table_name = table.replace(".lance", "")

        # Verify file is readable and non-empty
        try:
            size = filepath.stat().st_size
            if size == 0:
                errors.append(f"Empty file: {filepath.name}")
        except Exception as e:
            errors.append(f"Cannot read {filepath.name}: {e}")

        verified += 1
        percent = verified * 100 / total_files
        filled = int(bar_width * percent / 100)
        bar = "=" * filled + "-" * (bar_width - filled)
        print(f"\r  [{bar}] {percent:5.1f}% ({verified}/{total_files} files) {table_name:<20}", end="", flush=True)

    print()  # New line after progress bar

    if errors:
        print()
        for error in errors[:5]:  # Show first 5 errors
            print(f"  WARNING: {error}")
        if len(errors) > 5:
            print(f"  ... and {len(errors) - 5} more warnings")
        print()

    # Check for required manifest files in each table
    for table in DATA_TABLES:
        table_dir = lancedb_dir / table
        # LanceDB tables should have at least a _versions directory or manifest
        has_versions = (table_dir / "_versions").exists()
        has_manifest = any(table_dir.glob("*.manifest"))
        if not has_versions and not has_manifest:
            return False, f"Table {table} appears corrupted (missing manifest)"

    return True, ""


def download_database(dest_dir: Path, provider: str) -> bool:
    """Download and extract the LanceDB database from GitHub releases."""
    import ssl

    if log:
        log_section(log, "Database Download")
        log.info(f"Downloading database for provider: {provider}")
        log.info(f"Destination: {dest_dir}")

    dest_dir.mkdir(parents=True, exist_ok=True)
    asset_name = f"lancedb-{provider}-all.tar.gz"
    tarball_path = dest_dir / asset_name

    api_url = f"https://api.github.com/repos/{GITHUB_REPO}/releases/latest"

    print("  Fetching latest release info...")
    if log:
        log.debug(f"API URL: {api_url}")
    try:
        ctx = ssl.create_default_context()
        ctx.check_hostname = False
        ctx.verify_mode = ssl.CERT_NONE

        req = urllib.request.Request(api_url, headers={"User-Agent": "Hytale-RAG-Setup"})
        with urllib.request.urlopen(req, context=ctx, timeout=30) as response:
            release_info = json.loads(response.read().decode())

        download_url = None
        for asset in release_info.get("assets", []):
            if asset["name"] == asset_name:
                download_url = asset["browser_download_url"]
                break

        if not download_url:
            if log:
                log.error(f"Asset {asset_name} not found in release")
                log.error(f"Available assets: {[a['name'] for a in release_info.get('assets', [])]}")
            print(f"  ERROR: Could not find {asset_name} in latest release.")
            print(f"  Available assets:")
            for asset in release_info.get("assets", []):
                print(f"    - {asset['name']}")
            return False

        if log:
            log.info(f"Found download URL: {download_url}")

    except Exception as e:
        if log:
            log.error(f"Failed to fetch release info: {e}")
            log_exception(log, "download_database")
        print(f"  ERROR: Failed to fetch release info: {e}")
        return False

    print(f"  Downloading {asset_name}...")
    try:
        def show_progress(block_num, block_size, total_size):
            downloaded = block_num * block_size
            if total_size > 0:
                percent = min(100, downloaded * 100 / total_size)
                mb_downloaded = downloaded / (1024 * 1024)
                mb_total = total_size / (1024 * 1024)
                bar_width = 30
                filled = int(bar_width * percent / 100)
                bar = "=" * filled + "-" * (bar_width - filled)
                print(f"\r  [{bar}] {percent:5.1f}% ({mb_downloaded:.1f}/{mb_total:.1f} MB)", end="", flush=True)

        urllib.request.urlretrieve(download_url, tarball_path, reporthook=show_progress)
        print()
        if log:
            log.info(f"Download complete: {tarball_path}")

    except Exception as e:
        if log:
            log.error(f"Download failed: {e}")
            log_exception(log, "download_database")
        print(f"\n  ERROR: Download failed: {e}")
        return False

    print("  Extracting database...")
    try:
        with tarfile.open(tarball_path, "r:gz") as tar:
            tar.extractall(path=dest_dir)
        tarball_path.unlink()
        if log:
            log.info("Extraction complete")
        print("  Extraction complete!")
    except Exception as e:
        if log:
            log.error(f"Extraction failed: {e}")
            log_exception(log, "download_database")
        print(f"  ERROR: Extraction failed: {e}")
        return False

    # Save the version to a .version file for the MCP server to read
    version_tag = release_info.get("tag_name", "unknown")
    version_file = dest_dir / ".version"
    try:
        version_file.write_text(version_tag)
        if log:
            log.info(f"Database version: {version_tag}")
    except Exception:
        pass  # Non-critical if this fails

    return True


# ============================================================================
#  MCP Client Integration
# ============================================================================

# Supported MCP clients and their configurations
MCP_CLIENTS = {
    "claude_code": {
        "name": "Claude Code",
        "description": "Anthropic's CLI tool for Claude",
        "config_type": "json",
    },
    "vscode": {
        "name": "VS Code / GitHub Copilot",
        "description": "Works with Copilot in Agent mode (VS Code 1.102+)",
        "config_type": "vscode",
    },
    "cursor": {
        "name": "Cursor",
        "description": "AI-first code editor",
        "config_type": "cursor",
    },
    "windsurf": {
        "name": "Windsurf",
        "description": "Codeium's AI code editor",
        "config_type": "json",
    },
    "codex": {
        "name": "Codex CLI",
        "description": "OpenAI's command-line coding tool",
        "config_type": "toml",
    },
}


def get_mcp_command_stdio(script_dir: Path) -> dict:
    """Generate the MCP server command for stdio-based clients."""
    system = platform.system()

    if system == "Windows":
        cmd = (
            f"Set-Location '{script_dir}'; "
            "Get-Content .env | ForEach-Object { "
            "if ($_ -match '^([^=]+)=(.*)$') { "
            "[Environment]::SetEnvironmentVariable($matches[1], $matches[2]) "
            "} }; "
            "npx tsx src/index.ts"
        )
        return {
            "type": "stdio",
            "command": "powershell",
            "args": ["-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", cmd],
            "env": {"HYTALE_RAG_MODE": "mcp"}
        }
    else:
        cmd = f"cd '{script_dir}' && set -a && source .env && set +a && npx tsx src/index.ts"
        return {
            "type": "stdio",
            "command": "bash",
            "args": ["-c", cmd],
            "env": {"HYTALE_RAG_MODE": "mcp"}
        }


def get_mcp_command_simple(script_dir: Path) -> dict:
    """Generate a simpler MCP command for VS Code/Cursor (they handle env differently)."""
    system = platform.system()

    if system == "Windows":
        return {
            "type": "stdio",
            "command": "powershell",
            "args": [
                "-NoProfile",
                "-ExecutionPolicy", "Bypass",
                "-File", str(script_dir / "start-mcp.ps1")
            ]
        }
    else:
        return {
            "type": "stdio",
            "command": "bash",
            "args": [str(script_dir / "start-mcp.sh")]
        }


def check_powershell_execution_policy() -> tuple[bool, str]:
    """
    Check if PowerShell scripts can be executed.
    Returns (can_execute, policy_name).
    """
    try:
        result = subprocess.run(
            ["powershell", "-NoProfile", "-Command", "Get-ExecutionPolicy"],
            capture_output=True,
            text=True,
            timeout=10
        )
        policy = result.stdout.strip().lower()

        # These policies allow script execution
        allowed_policies = ["unrestricted", "remotesigned", "bypass", "allsigned"]
        can_execute = policy in allowed_policies

        return can_execute, result.stdout.strip()
    except Exception:
        return False, "Unknown"


def verify_powershell_bypass() -> bool:
    """
    Test if -ExecutionPolicy Bypass actually works (some enterprise policies block it).
    Returns True if bypass works.
    """
    try:
        # Create a temp script and try to run it with bypass
        test_script = Path.home() / ".hytale-ps-test.ps1"
        test_script.write_text("Write-Output 'OK'")

        result = subprocess.run(
            ["powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", str(test_script)],
            capture_output=True,
            text=True,
            timeout=10
        )

        test_script.unlink(missing_ok=True)
        return "OK" in result.stdout
    except Exception:
        return False


def create_start_scripts(script_dir: Path):
    """Create helper scripts for starting the MCP server."""
    system = platform.system()

    if system == "Windows":
        # Check PowerShell execution policy
        can_execute, policy = check_powershell_execution_policy()

        if not can_execute:
            print()
            print(f"    NOTE: Your PowerShell execution policy is '{policy}'.")
            print("    The MCP config uses '-ExecutionPolicy Bypass' which should work,")
            print("    but if you encounter issues, you may need to run:")
            print()
            print("      Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser")
            print()

            # Test if bypass actually works
            if not verify_powershell_bypass():
                print("    WARNING: ExecutionPolicy Bypass appears to be blocked.")
                print("    This may be an enterprise policy restriction.")
                print("    Contact your IT administrator if scripts fail to run.")
                print()

        ps1_path = script_dir / "start-mcp.ps1"
        ps1_content = f"""# Hytale RAG MCP Server Startup Script
Set-Location '{script_dir}'
Get-Content .env | ForEach-Object {{
    if ($_ -match '^([^=]+)=(.*)$') {{
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2])
    }}
}}
$env:HYTALE_RAG_MODE = "mcp"
npx tsx src/index.ts
"""
        ps1_path.write_text(ps1_content)
        print(f"    Created {ps1_path}")
    else:
        sh_path = script_dir / "start-mcp.sh"
        sh_content = f"""#!/bin/bash
# Hytale RAG MCP Server Startup Script
cd '{script_dir}'
set -a
source .env
set +a
export HYTALE_RAG_MODE=mcp
npx tsx src/index.ts
"""
        sh_path.write_text(sh_content)
        sh_path.chmod(0o755)
        print(f"    Created {sh_path}")


def get_client_config_path(client_id: str) -> Path | None:
    """Get the config file path for a given MCP client."""
    home = Path.home()

    paths = {
        "claude_code": home / ".claude.json",
        "windsurf": home / ".codeium" / "windsurf" / "mcp_config.json",
        "codex": home / ".codex" / "config.toml",
        "cursor": None,  # Cursor uses settings UI, we'll show instructions
        "vscode": None,  # VS Code uses workspace .vscode/mcp.json
    }
    return paths.get(client_id)


def setup_claude_code(script_dir: Path) -> bool:
    """Configure Claude Code MCP server."""
    config_path = Path.home() / ".claude.json"
    mcp_config = get_mcp_command_stdio(script_dir)

    if config_path.exists():
        try:
            config = json.loads(config_path.read_text(encoding='utf-8'))
        except json.JSONDecodeError:
            config = {}
    else:
        config = {}

    if "mcpServers" not in config:
        config["mcpServers"] = {}

    config["mcpServers"]["hytale-rag"] = mcp_config
    config_path.write_text(json.dumps(config, indent=2), encoding='utf-8')

    print(f"    Added 'hytale-rag' to {config_path}")
    return True


def setup_vscode(script_dir: Path) -> bool:
    """Configure VS Code / GitHub Copilot MCP server."""
    # Create .vscode/mcp.json in the repo root
    vscode_dir = REPO_ROOT / ".vscode"
    vscode_dir.mkdir(exist_ok=True)
    config_path = vscode_dir / "mcp.json"

    mcp_config = get_mcp_command_simple(script_dir)
    # VS Code's mcp.json format doesn't use "type" field - remove it
    mcp_config.pop("type", None)

    if config_path.exists():
        try:
            config = json.loads(config_path.read_text(encoding='utf-8'))
        except json.JSONDecodeError:
            config = {}
    else:
        config = {}

    if "servers" not in config:
        config["servers"] = {}

    config["servers"]["hytale-rag"] = mcp_config
    config_path.write_text(json.dumps(config, indent=2), encoding='utf-8')

    print(f"    Added 'hytale-rag' to {config_path}")
    print()
    print("    To use with GitHub Copilot:")
    print("      1. Open VS Code 1.102+ in this workspace")
    print("      2. Enable Copilot Agent mode")
    print("      3. The MCP server will be available automatically")
    return True


def setup_cursor(script_dir: Path) -> bool:
    """Show instructions for Cursor setup."""
    mcp_config = get_mcp_command_simple(script_dir)
    system = platform.system()

    print()
    print("    Cursor Setup Instructions:")
    print("    ---------------------------")
    print("    1. Open Cursor Settings (Cmd/Ctrl + ,)")
    print("    2. Go to: Tools & Integrations > New MCP Server")
    print("    3. Name: hytale-rag")
    print("    4. Type: command")
    print(f"    5. Command: {mcp_config['command']}")
    print(f"    6. Args: {' '.join(mcp_config['args'])}")
    print()

    # Also create a cursor config file for manual copy
    cursor_config = {
        "mcpServers": {
            "hytale-rag": mcp_config
        }
    }
    cursor_snippet_path = script_dir / "cursor-mcp-config.json"
    cursor_snippet_path.write_text(json.dumps(cursor_config, indent=2), encoding='utf-8')
    print(f"    Config snippet saved to: {cursor_snippet_path}")
    print("    (You can copy this into Cursor's MCP settings)")
    return True


def setup_windsurf(script_dir: Path) -> bool:
    """Configure Windsurf MCP server."""
    config_dir = Path.home() / ".codeium" / "windsurf"
    config_dir.mkdir(parents=True, exist_ok=True)
    config_path = config_dir / "mcp_config.json"

    mcp_config = get_mcp_command_simple(script_dir)

    if config_path.exists():
        try:
            config = json.loads(config_path.read_text(encoding='utf-8'))
        except json.JSONDecodeError:
            config = {}
    else:
        config = {}

    if "mcpServers" not in config:
        config["mcpServers"] = {}

    config["mcpServers"]["hytale-rag"] = mcp_config
    config_path.write_text(json.dumps(config, indent=2), encoding='utf-8')

    print(f"    Added 'hytale-rag' to {config_path}")
    return True


def setup_codex(script_dir: Path) -> bool:
    """Configure Codex CLI MCP server."""
    config_dir = Path.home() / ".codex"
    config_dir.mkdir(parents=True, exist_ok=True)
    config_path = config_dir / "config.toml"

    system = platform.system()

    # Read existing config or create new
    existing_content = ""
    if config_path.exists():
        existing_content = config_path.read_text(encoding='utf-8')

    # Check if hytale-rag already configured
    if "[mcp_servers.hytale-rag]" in existing_content:
        print(f"    'hytale-rag' already configured in {config_path}")
        return True

    # Generate the TOML config
    if system == "Windows":
        start_script = str(script_dir / "start-mcp.ps1").replace("\\", "\\\\")
        toml_entry = f'''
[mcp_servers.hytale-rag]
command = "powershell"
args = ["-NoProfile", "-ExecutionPolicy", "Bypass", "-File", "{start_script}"]
'''
    else:
        start_script = str(script_dir / "start-mcp.sh")
        toml_entry = f'''
[mcp_servers.hytale-rag]
command = "bash"
args = ["{start_script}"]
'''

    # Append to config
    with open(config_path, "a", encoding='utf-8') as f:
        f.write(toml_entry)

    print(f"    Added 'hytale-rag' to {config_path}")
    return True


def prompt_multi_choice(options: list[tuple[str, str]], prompt_text: str = "Select options") -> list[int]:
    """Prompt user to select multiple options from numbered list. Returns list of 0-based indices."""
    for i, (label, desc) in enumerate(options, 1):
        print(f"  [{i}] {label}")
        if desc:
            for line in desc.split("\n"):
                print(f"      {line}")
        print()

    print(f"  [A] All of the above")
    print(f"  [0] Skip / None")
    print()

    while True:
        response = input(f"  {prompt_text} (e.g., 1,2,3 or A for all): ").strip().lower()

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
            # Parse comma-separated numbers
            indices = []
            for part in response.replace(" ", "").split(","):
                idx = int(part) - 1
                if 0 <= idx < len(options):
                    indices.append(idx)
                else:
                    raise ValueError()
            if indices:
                result = list(set(indices))  # Remove duplicates
                if log:
                    selected = [options[i][0] for i in result]
                    log.info(f"User multi-choice: '{prompt_text}' -> {selected}")
                return result
        except ValueError:
            pass

        print(f"  Please enter numbers 1-{len(options)} separated by commas, 'A' for all, or '0' to skip.")


def setup_mcp_clients(selected_clients: list[str], script_dir: Path) -> dict[str, bool]:
    """Set up MCP integration for selected clients. Returns dict of client_id -> success."""
    results = {}

    # First, create the start scripts that some clients need
    if any(c in selected_clients for c in ["vscode", "cursor", "windsurf", "codex"]):
        print("  Creating MCP start scripts...")
        create_start_scripts(script_dir)
        print()

    setup_functions = {
        "claude_code": setup_claude_code,
        "vscode": setup_vscode,
        "cursor": setup_cursor,
        "windsurf": setup_windsurf,
        "codex": setup_codex,
    }

    for client_id in selected_clients:
        client_info = MCP_CLIENTS[client_id]
        print(f"  Setting up {client_info['name']}...", flush=True)

        setup_fn = setup_functions.get(client_id)
        if setup_fn:
            try:
                results[client_id] = setup_fn(script_dir)
            except Exception as e:
                print(f"    ERROR: {e}")
                results[client_id] = False
        else:
            print(f"    Skipped: No setup function for {client_id}")
            results[client_id] = False

        print()

    return results


# ============================================================================
#  Main Installation Wizard
# ============================================================================

def main():
    global log, log_file

    # Initialize logging
    log, log_file = setup_logging("setup", REPO_ROOT)
    log_section(log, "Initialization")
    log.info(f"SCRIPT_DIR: {SCRIPT_DIR}")
    log.info(f"REPO_ROOT: {REPO_ROOT}")
    log.info(f"ENV_FILE: {ENV_FILE}")

    clear_screen()

    print()
    print("  +----------------------------------------------------------+")
    print("  |                                                          |")
    print("  |       Welcome to the Hytale Toolkit Setup Wizard!        |")
    print("  |                                                          |")
    print("  +----------------------------------------------------------+")
    print()
    print("  This wizard will help you set up:")
    print("    - Decompiled Hytale source code")
    print("    - Javadoc documentation (optional)")
    print("    - Semantic code search (RAG)")
    print("    - Claude Code integration (optional)")
    print()

    input("  Press Enter to begin...")

    # Validate we're in the right directory
    if not (SCRIPT_DIR / "package.json").exists():
        print("\n  ERROR: Please run this script from the hytale-rag directory.")
        sys.exit(1)

    env = load_env()
    total_steps = 6

    # =========================================================================
    # Step 1: Hytale Installation Path
    # =========================================================================
    print_step(1, total_steps, "Hytale Installation")

    install_path = get_hytale_install_path(env)
    if not install_path:
        print("\n  Setup cancelled: No valid installation path provided.")
        sys.exit(1)

    env["HYTALE_INSTALL_PATH"] = install_path
    save_env(env)
    print(f"\n  Saved installation path to .env")

    # =========================================================================
    # Step 2: Decompile Source Code
    # =========================================================================
    print_step(2, total_steps, "Decompile Source Code")

    print("  Decompiling the server source code gives your AI assistant access to")
    print("  the actual Java implementation. This is highly recommended for")
    print("  accurate code assistance and for your own reference.")
    print()

    # Prompt for RAM allocation (used for both decompile and javadocs)
    ram_gb = 8  # Default
    if prompt_yes_no("Decompile server source code? (recommended)", default=True):
        print()
        ram_gb = prompt_ram_allocation(default=8)
        print()
        if not decompile_server(install_path, env, ram_gb=ram_gb):
            if not (DECOMPILED_DIR.exists() and any(DECOMPILED_DIR.iterdir())):
                print("\n  WARNING: Decompilation failed and no existing code found.")
                if not prompt_yes_no("Continue anyway?", default=False):
                    sys.exit(1)
    else:
        if DECOMPILED_DIR.exists() and any(DECOMPILED_DIR.iterdir()):
            print("  Using existing decompiled code.")
        else:
            print("  Skipping decompilation. You can run index-all.py later to decompile.")

    # =========================================================================
    # Step 3: Generate Javadocs
    # =========================================================================
    print_step(3, total_steps, "Generate Javadocs (Optional)")

    print("  Javadocs provide browsable HTML documentation of all classes")
    print("  and methods. Useful for exploring the codebase in a browser.")
    print()

    if DECOMPILED_DIR.exists() and any(DECOMPILED_DIR.iterdir()):
        if prompt_yes_no("Generate Javadocs?", default=False):
            print()
            # Check for JDK / offer to download
            javadoc_path = get_jdk_javadoc_path()
            if javadoc_path:
                print()
                include_private = prompt_yes_no(
                    "Include private methods? (Much more verbose, 2-3x larger)",
                    default=False
                )
                generate_javadocs(include_private, ram_gb=ram_gb, javadoc_path=javadoc_path)
            else:
                print("  Skipping Javadoc generation.")
    else:
        print("  Skipped: No decompiled code available.")

    # =========================================================================
    # Step 4: Select Embedding Provider
    # =========================================================================
    print_step(4, total_steps, "Embedding Provider")

    print("  The RAG system uses embeddings to enable semantic code search.")
    print("  Choose how you want to generate these embeddings:")
    print()

    provider_options = [
        ("Voyage AI (Recommended)",
         "Cloud-based, high quality embeddings.\n"
         "Free tier with 3 queries/min unless you add a payment method.\n"
         "Extremely generous limits - you're unlikely to ever be charged.\n"
         "You can set a $0 spending limit on their dashboard.\n"
         "Best choice if you have slower hardware."),
        ("Ollama (Local)",
         "Runs entirely on your machine, no API key needed.\n"
         "Requires ~2GB disk space for the model. No rate limits.\n"
         "Still very good quality, just slightly below Voyage.")
    ]

    provider_idx = prompt_choice(provider_options, "Select provider")
    provider = "voyage" if provider_idx == 0 else "ollama"

    env["EMBEDDING_PROVIDER"] = provider

    if provider == "voyage":
        print()
        existing_key = env.get("VOYAGE_API_KEY", "")
        if existing_key:
            print(f"  Found existing API key.")
            if not prompt_yes_no("Use existing key?", default=True):
                existing_key = ""

        if not existing_key:
            print()
            print("  Get a free API key at: https://www.voyageai.com/")
            print("  (You can set a $0 spending limit in their dashboard)")
            print()
            api_key = input("  Enter your Voyage API key: ").strip()
            if not api_key:
                print("  ERROR: API key is required for Voyage AI.")
                sys.exit(1)
            env["VOYAGE_API_KEY"] = api_key
            if log:
                log.info("User entered Voyage API key (value redacted)")

    elif provider == "ollama":
        print()
        if not setup_ollama():
            print("\n  ERROR: Ollama setup failed.")
            sys.exit(1)
        env["OLLAMA_MODEL"] = OLLAMA_MODEL

    save_env(env)

    # =========================================================================
    # Step 5: Install Database
    # =========================================================================
    print_step(5, total_steps, "Install Database")

    print("  The RAG database contains pre-indexed embeddings for fast search.")
    print("  Downloading the pre-built database is much faster than indexing locally.")
    print()

    data_dir = SCRIPT_DIR / "data"
    provider_dir = data_dir / provider
    lancedb_dir = provider_dir / "lancedb"

    needs_download = not lancedb_dir.exists() or not all(
        (lancedb_dir / table).exists() for table in DATA_TABLES
    )

    if needs_download:
        print("  Downloading pre-built database...", flush=True)
        print()

        if lancedb_dir.exists():
            shutil.rmtree(lancedb_dir, ignore_errors=True)

        if not download_database(provider_dir, provider):
            print()
            print("  Download failed. You have two options:")
            print("    1. Check your internet connection and run setup.py again")
            print("    2. Run index-all.py to build the database locally")
            print()
            if not prompt_yes_no("Continue without database?", default=False):
                sys.exit(1)
        else:
            print()
            # Verify all database files with progress bar
            success, error = verify_database_files(lancedb_dir)
            if not success:
                print(f"  ERROR: {error}")
                print("  Please run setup again or run index-all.py to rebuild.")
                sys.exit(1)

            # Also run a quick functional test
            print("  Running functional test...", flush=True)
            exit_code, output = run_command(
                ["npx", "tsx", "src/search.ts", "--stats"],
                cwd=SCRIPT_DIR
            )

            if "panic" in output.lower() or "range start" in output:
                print("  ERROR: Database appears corrupted. Please run setup again.")
                sys.exit(1)

            print("  Database verified successfully!")
    else:
        print("  Database already exists.")
        if prompt_yes_no("Re-download?", default=False):
            shutil.rmtree(lancedb_dir, ignore_errors=True)
            if not download_database(provider_dir, provider):
                print("  Download failed.")
            else:
                print()
                success, error = verify_database_files(lancedb_dir)
                if not success:
                    print(f"  WARNING: Verification issue: {error}")
                else:
                    print("  Database verified successfully!")

    # Ensure npm is installed
    print()
    if not setup_npm():
        print()
        print("  WARNING: npm is required for the MCP server.")
        print("  You can continue setup, but MCP integration won't work until npm is installed.")
        if not prompt_yes_no("Continue anyway?", default=False):
            sys.exit(1)
    else:
        # Install npm dependencies
        print()
        print("  Installing npm dependencies...", flush=True)
        exit_code, output = run_command(["npm", "install"], cwd=SCRIPT_DIR)
        if exit_code != 0:
            print(f"  WARNING: npm install had issues: {output[:200]}")
        else:
            print("  Dependencies installed.")

    # =========================================================================
    # Step 6: AI Tool Integration
    # =========================================================================
    print_step(6, total_steps, "AI Tool Integration (MCP)")

    print("  The RAG server uses the Model Context Protocol (MCP) to integrate")
    print("  with AI coding assistants. Select which tools you want to configure:")
    print()

    # Build options list from MCP_CLIENTS
    client_ids = list(MCP_CLIENTS.keys())
    client_options = [
        (MCP_CLIENTS[cid]["name"], MCP_CLIENTS[cid]["description"])
        for cid in client_ids
    ]

    selected_indices = prompt_multi_choice(client_options, "Select AI tools to configure")

    if selected_indices:
        selected_clients = [client_ids[i] for i in selected_indices]
        print()
        print(f"  Configuring {len(selected_clients)} tool(s)...")
        print()

        results = setup_mcp_clients(selected_clients, SCRIPT_DIR)

        # Show summary
        successful = [c for c, success in results.items() if success]
        failed = [c for c, success in results.items() if not success]

        print()
        print("  +----------------------------------------------------------+")
        print("  |                   Setup Complete!                        |")
        print("  +----------------------------------------------------------+")
        print()

        if successful:
            print("  MCP integration configured for:")
            for client_id in successful:
                print(f"    - {MCP_CLIENTS[client_id]['name']}")
            print()
            print("  To use the Hytale RAG:")
            print("    1. Restart your AI tool (or open a new session)")
            print("    2. Ask it to search the Hytale codebase, e.g.:")
            print("       - 'Search the Hytale code for player movement'")
            print("       - 'Find methods related to inventory'")
            print("       - 'How does the NPC AI work?'")

        if failed:
            print()
            print("  Failed to configure:")
            for client_id in failed:
                print(f"    - {MCP_CLIENTS[client_id]['name']}")
    else:
        print()
        print("  +----------------------------------------------------------+")
        print("  |                   Setup Complete!                        |")
        print("  +----------------------------------------------------------+")
        print()
        print("  No AI tool integration configured.")
        print()
        print("  To use the RAG server manually:")
        print()
        print(f"    cd {SCRIPT_DIR}")
        print("    npx tsx src/search.ts \"your search query\"")
        print()
        print("  Examples:")
        print("    npx tsx src/search.ts \"player movement\"")
        print("    npx tsx src/search.ts \"inventory\" --limit 10")
        print("    npx tsx src/search.ts --stats")
        print()
        print("  Run setup.py again to configure AI tool integration later.")

    print()
    print("  ----------------------------------------------------------")
    print("  Additional tools:")
    print("    - index-all.py  : Re-index the database after Hytale updates")
    if DECOMPILED_DIR.exists():
        print(f"    - Decompiled source: {DECOMPILED_DIR}")
    if JAVADOCS_DIR.exists():
        print(f"    - Javadocs: {JAVADOCS_DIR / 'index.html'}")
    print("  ----------------------------------------------------------")
    print()

    # Show log file location
    if log_file:
        print(f"  Log file: {log_file}")
        print("  (Share this file if you need help troubleshooting)")
        print()
        log.info("=== Setup completed successfully ===")


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
