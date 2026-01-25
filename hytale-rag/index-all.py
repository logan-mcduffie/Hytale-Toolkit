#!/usr/bin/env python3
"""
Hytale RAG - Re-Index Database

Use this script to rebuild the RAG database after a Hytale update.
Supports Voyage AI, Ollama, or both embedding providers.

Prerequisites:
  - Run setup.py first to configure your installation
  - Decompiled source code must exist (setup.py creates this)
"""

import os
import platform
import subprocess
import sys
import time
from pathlib import Path

SCRIPT_DIR = Path(__file__).parent.resolve()
REPO_ROOT = SCRIPT_DIR.parent
ENV_FILE = SCRIPT_DIR / ".env"
DECOMPILED_DIR = REPO_ROOT / "decompiled"  # Always in repo root
OLLAMA_MODEL = "nomic-embed-text"


def load_env() -> dict[str, str]:
    """Load .env file."""
    env = {}
    if ENV_FILE.exists():
        with open(ENV_FILE, "r") as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#") and "=" in line:
                    key, value = line.split("=", 1)
                    env[key.strip()] = value.strip()
    return env


def prompt_yes_no(question: str, default: bool = True) -> bool:
    """Prompt for yes/no input."""
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


def command_exists(cmd: str) -> bool:
    """Check if a command exists."""
    if platform.system() == "Windows":
        result = subprocess.run(["where", cmd], capture_output=True, shell=True)
    else:
        result = subprocess.run(["which", cmd], capture_output=True)
    return result.returncode == 0


def is_admin() -> bool:
    """Check if running with admin privileges."""
    if platform.system() == "Windows":
        try:
            import ctypes
            return ctypes.windll.shell32.IsUserAnAdmin() != 0
        except:
            return False
    return os.geteuid() == 0


# ============================================================================
#  Ollama Setup
# ============================================================================

def check_ollama_installed() -> bool:
    return command_exists("ollama")


def check_ollama_running() -> bool:
    try:
        import urllib.request
        req = urllib.request.Request("http://localhost:11434/api/tags")
        with urllib.request.urlopen(req, timeout=5) as response:
            return response.status == 200
    except:
        return False


def check_ollama_model_available(model: str = OLLAMA_MODEL) -> bool:
    try:
        import urllib.request
        import json
        req = urllib.request.Request("http://localhost:11434/api/tags")
        with urllib.request.urlopen(req, timeout=5) as response:
            data = json.loads(response.read().decode())
            models = data.get("models", [])
            return any(m["name"] == model or m["name"].startswith(f"{model}:") for m in models)
    except:
        return False


def install_ollama() -> bool:
    """Install Ollama based on OS."""
    system = platform.system()

    if system == "Windows":
        if not is_admin():
            print("\n  ERROR: Administrator privileges required to install Ollama.")
            print("  Please restart this script as Administrator, or install Ollama manually:")
            print("  https://ollama.com/download")
            return False
        print("  Installing Ollama via winget...")
        result = subprocess.run([
            "winget", "install", "-e", "--id", "Ollama.Ollama",
            "--accept-source-agreements", "--accept-package-agreements"
        ], shell=True)
        if result.returncode != 0:
            print("  Installation failed. Please install manually: https://ollama.com/download")
            return False

    elif system == "Darwin":  # macOS
        if not command_exists("brew"):
            print("  Homebrew is required. Install from: https://brew.sh")
            return False
        print("  Installing Ollama via Homebrew...")
        result = subprocess.run(["brew", "install", "ollama"])
        if result.returncode != 0:
            print("  Installation failed. Please install manually: https://ollama.com/download")
            return False

    elif system == "Linux":
        print("  Installing Ollama...")
        result = subprocess.run(
            ["bash", "-c", "curl -fsSL https://ollama.com/install.sh | sh"]
        )
        if result.returncode != 0:
            print("  Installation failed. Please install manually: https://ollama.com/download")
            return False

    else:
        print(f"  Unsupported OS: {system}")
        return False

    print("  Ollama installed!")
    return True


def start_ollama() -> bool:
    """Start Ollama server in background."""
    system = platform.system()

    # On Windows, Ollama desktop app usually auto-starts the server
    # Wait a bit to see if it comes up on its own
    print("  Waiting for Ollama server...")
    for i in range(10):
        time.sleep(1)
        print(f"\r  Waiting for Ollama server... ({i+1}s)", end="", flush=True)
        if check_ollama_running():
            print("\n  Ollama server is running!")
            return True

    # Try to start it manually
    print("\n  Attempting to start Ollama server...")

    try:
        if system == "Windows":
            # Try common install locations
            ollama_paths = [
                "ollama",
                os.path.expandvars(r"%LOCALAPPDATA%\Programs\Ollama\ollama.exe"),
                os.path.expandvars(r"%PROGRAMFILES%\Ollama\ollama.exe"),
            ]

            started = False
            for ollama_path in ollama_paths:
                try:
                    subprocess.Popen(
                        [ollama_path, "serve"],
                        stdout=subprocess.DEVNULL,
                        stderr=subprocess.DEVNULL,
                        creationflags=subprocess.CREATE_NEW_PROCESS_GROUP | subprocess.DETACHED_PROCESS
                    )
                    started = True
                    break
                except FileNotFoundError:
                    continue

            if not started:
                print("  Could not find Ollama executable.")
                print("  Please start Ollama manually (it should be in your Start menu).")
                print("  Then run this script again.")
                return False
        else:
            subprocess.Popen(
                ["ollama", "serve"],
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
                start_new_session=True
            )
    except Exception as e:
        print(f"  Error starting Ollama: {e}")
        print("  Please start Ollama manually and run this script again.")
        return False

    # Wait for startup
    for i in range(15):
        time.sleep(1)
        print(f"\r  Waiting for Ollama to start... ({i+1}s)", end="", flush=True)
        if check_ollama_running():
            print("\n  Ollama server started!")
            return True

    print("\n  Ollama server did not start.")
    print("  Please start Ollama manually (check your Start menu) and run this script again.")
    return False


def pull_ollama_model(model: str = OLLAMA_MODEL) -> bool:
    """Pull the embedding model."""
    print(f"  Pulling {model} model (this may take a few minutes)...")

    result = subprocess.run(["ollama", "pull", model])
    if result.returncode != 0:
        print(f"  Failed to pull model.")
        return False

    print(f"  Model {model} ready!")
    return True


def setup_ollama() -> bool:
    """Full Ollama setup: install, start, pull model."""
    # Check if installed
    if not check_ollama_installed():
        print("\n  Ollama is not installed.")
        if not prompt_yes_no("Install Ollama?", default=True):
            return False
        if not install_ollama():
            return False

    print("  Ollama is installed.")

    # Check if running
    if not check_ollama_running():
        if not start_ollama():
            print("  Please start Ollama manually with: ollama serve")
            return False

    print("  Ollama server is running.")

    # Check if model is available
    if not check_ollama_model_available(OLLAMA_MODEL):
        print(f"  Model '{OLLAMA_MODEL}' not found.")
        if not pull_ollama_model(OLLAMA_MODEL):
            return False

    print(f"  Model '{OLLAMA_MODEL}' is ready.")
    return True


# ============================================================================
#  Indexing
# ============================================================================

def run_ingest(name: str, script: str, args: list[str], provider: str, env_vars: dict) -> bool:
    """Run an ingest script with live output."""
    print(f"  [{provider}/{name}] Indexing...")
    print()

    proc_env = os.environ.copy()
    proc_env.update(env_vars)

    cmd = ["npx", "tsx", script] + args
    # Use shell=True only on Windows to resolve 'npx' from PATH
    # Linux and macOS (Darwin) must use shell=False when passing a list
    use_shell = platform.system() == "Windows"
    try:
        # Stream output live so user can see progress
        result = subprocess.run(
            cmd,
            cwd=str(SCRIPT_DIR),
            env=proc_env,
            shell=use_shell
        )
        print()
        if result.returncode == 0:
            print(f"  [{provider}/{name}] Complete!")
            return True
        else:
            print(f"  [{provider}/{name}] Failed!")
            return False
    except Exception as e:
        print(f"  [{provider}/{name}] Error: {e}")
        return False


def main():
    print()
    print("  +----------------------------------------------------------+")
    print("  |              Hytale RAG - Re-Index Database              |")
    print("  +----------------------------------------------------------+")
    print()

    # Load configuration
    env = load_env()
    install_path = env.get("HYTALE_INSTALL_PATH", "")
    voyage_key = env.get("VOYAGE_API_KEY", "")

    # Validate install path
    if not install_path:
        print("  ERROR: HYTALE_INSTALL_PATH not set.")
        print("  Please run setup.py first to configure your installation.")
        return 1

    # Check for decompiled source
    if not DECOMPILED_DIR.exists() or not any(DECOMPILED_DIR.iterdir()):
        print("  ERROR: No decompiled source code found.")
        print(f"  Expected at: {DECOMPILED_DIR}")
        print()
        print("  Run setup.py and select 'Yes' for decompilation.")
        return 1

    # Derive paths
    install_path = Path(install_path)
    client_data = install_path / "Client" / "Data"
    assets_zip = install_path / "Assets.zip"

    print("  Configuration:")
    print(f"    Decompiled code: {DECOMPILED_DIR}")
    print(f"    Client data:     {client_data}")
    print(f"    Assets zip:      {assets_zip}")
    print()

    # =========================================================================
    # Select Embedder
    # =========================================================================
    print("  Which embedder do you want to index with?")
    print()
    print("    [1] Voyage AI only")
    print("    [2] Ollama only")
    print("    [3] Both (recommended for releases)")
    print()

    while True:
        choice = input("  Select [1/2/3]: ").strip()
        if choice in ("1", "2", "3"):
            break
        print("  Please enter 1, 2, or 3.")

    use_voyage = choice in ("1", "3")
    use_ollama = choice in ("2", "3")

    # =========================================================================
    # Validate/Setup Providers
    # =========================================================================

    # Voyage validation
    if use_voyage:
        if not voyage_key:
            print("\n  Voyage AI selected but no API key found.")
            voyage_key = input("  Enter Voyage API key (or press Enter to skip): ").strip()
            if not voyage_key:
                print("  Skipping Voyage AI.")
                use_voyage = False

    # Ollama setup
    if use_ollama:
        print()
        if not setup_ollama():
            print("\n  Ollama setup failed.")
            if choice == "2":  # Ollama only
                return 1
            else:
                # Ask if they want to continue with Voyage only
                if prompt_yes_no("Continue with Voyage AI only?", default=False):
                    use_ollama = False
                else:
                    print("  Exiting.")
                    return 1

    if not use_voyage and not use_ollama:
        print("\n  ERROR: No providers available!")
        return 1

    # =========================================================================
    # Run Indexing
    # =========================================================================

    # Define data sources
    sources = [("Server Code", "src/ingest.ts", [str(DECOMPILED_DIR)])]
    if client_data.exists():
        sources.append(("Client UI", "src/ingest-client.ts", [str(client_data)]))
    if assets_zip.exists():
        sources.append(("Game Data", "src/ingest-gamedata.ts", [str(assets_zip)]))
    # Docs are always indexed (from cloned HytaleModding site repo)
    sources.append(("Docs", "src/ingest-docs.ts", []))

    results = {"voyage": [], "ollama": []}

    # Index with Voyage
    if use_voyage:
        print()
        print("  " + "=" * 56)
        print("  Indexing with Voyage AI")
        print("  " + "=" * 56)

        voyage_env = {"EMBEDDING_PROVIDER": "voyage", "VOYAGE_API_KEY": voyage_key}
        for name, script, args in sources:
            success = run_ingest(name, script, args, "voyage", voyage_env)
            results["voyage"].append((name, success))

    # Index with Ollama
    if use_ollama:
        print()
        print("  " + "=" * 56)
        print("  Indexing with Ollama")
        print("  " + "=" * 56)

        ollama_env = {"EMBEDDING_PROVIDER": "ollama", "OLLAMA_MODEL": OLLAMA_MODEL}
        for name, script, args in sources:
            success = run_ingest(name, script, args, "ollama", ollama_env)
            results["ollama"].append((name, success))

    # =========================================================================
    # Summary
    # =========================================================================
    print()
    print("  " + "=" * 56)
    print("  Summary")
    print("  " + "=" * 56)
    print()

    total_success = 0
    total_failed = 0

    if results["voyage"]:
        print("  Voyage AI:")
        for name, success in results["voyage"]:
            icon = "+" if success else "X"
            status = "success" if success else "FAILED"
            print(f"    [{icon}] {name}: {status}")
            total_success += 1 if success else 0
            total_failed += 0 if success else 1
        print()

    if results["ollama"]:
        print("  Ollama:")
        for name, success in results["ollama"]:
            icon = "+" if success else "X"
            status = "success" if success else "FAILED"
            print(f"    [{icon}] {name}: {status}")
            total_success += 1 if success else 0
            total_failed += 0 if success else 1
        print()

    print("  Database locations:")
    if results["voyage"]:
        print("    Voyage: data/voyage/lancedb/")
    if results["ollama"]:
        print("    Ollama: data/ollama/lancedb/")
    print()

    if total_failed == 0:
        print(f"  All {total_success} indexing jobs completed successfully!")
    else:
        print(f"  {total_success} succeeded, {total_failed} failed")

    print()
    return 1 if total_failed > 0 else 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except KeyboardInterrupt:
        print("\n\n  Cancelled by user.")
        sys.exit(1)
