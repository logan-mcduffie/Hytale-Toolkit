#!/usr/bin/env python3
"""
Hytale Modding RAG Setup Script
Sets up the semantic code search MCP server for Claude Code.
Works on Windows, macOS, and Linux.
"""

import json
import os
import platform
import subprocess
import sys
from pathlib import Path


def get_claude_config_path() -> Path:
    """Get the path to Claude's config file based on OS."""
    home = Path.home()
    return home / ".claude.json"


def get_shell_command(script_dir: Path) -> dict:
    """Generate the MCP server command based on OS."""
    system = platform.system()

    if system == "Windows":
        # PowerShell command that loads .env and runs the server
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
            "args": ["-NoProfile", "-Command", cmd],
            "env": {"HYTALE_RAG_MODE": "mcp"}
        }
    else:
        # Bash command for macOS/Linux that sources .env and runs the server
        cmd = f"cd '{script_dir}' && set -a && source .env && set +a && npx tsx src/index.ts"
        return {
            "type": "stdio",
            "command": "bash",
            "args": ["-c", cmd],
            "env": {"HYTALE_RAG_MODE": "mcp"}
        }


def run_command(cmd: list[str], cwd: Path = None) -> tuple[int, str]:
    """Run a command and return exit code and output."""
    try:
        if platform.system() == "Windows":
            # On Windows, use shell=True with joined command to find npm/npx in PATH
            result = subprocess.run(
                " ".join(cmd),
                cwd=cwd,
                capture_output=True,
                text=True,
                shell=True
            )
        else:
            # On Unix, run directly without shell
            result = subprocess.run(
                cmd,
                cwd=cwd,
                capture_output=True,
                text=True
            )
        return result.returncode, result.stdout + result.stderr
    except Exception as e:
        return 1, str(e)


def main():
    print("=== Hytale Modding RAG Setup ===\n")

    # Get script directory
    script_dir = Path(__file__).parent.resolve()
    print(f"Script directory: {script_dir}\n")

    # Verify we're in the right place
    if not (script_dir / "package.json").exists():
        print("ERROR: package.json not found. Run this script from the hytale-rag directory.")
        sys.exit(1)

    if not (script_dir / "data" / "lancedb").exists():
        print("ERROR: LanceDB database not found at data/lancedb.")
        print("Download lancedb.tar.gz from GitHub Releases and extract it here.")
        sys.exit(1)

    # Step 1: Get API key
    print("Step 1: API Key Setup")
    env_file = script_dir / ".env"

    if env_file.exists():
        print(f"  Found existing .env file at {env_file}")
        response = input("  Do you want to use the existing API key? [Y/n]: ").strip().lower()
        if response in ('n', 'no'):
            api_key = input("  Enter your Voyage API key: ").strip()
            if not api_key:
                print("ERROR: API key is required.")
                sys.exit(1)
            env_file.write_text(f"VOYAGE_API_KEY={api_key}\n")
            print("  API key saved to .env")
        else:
            print("  Using existing API key.")
    else:
        api_key = input("  Enter your Voyage API key (get one free at https://www.voyageai.com/): ").strip()
        if not api_key:
            print("ERROR: API key is required.")
            sys.exit(1)
        env_file.write_text(f"VOYAGE_API_KEY={api_key}\n")
        print(f"  API key saved to {env_file}")

    # Step 2: Install dependencies
    print("\nStep 2: Installing dependencies...")
    exit_code, output = run_command(["npm", "install"], cwd=script_dir)
    if exit_code != 0:
        print(f"ERROR: npm install failed:\n{output}")
        sys.exit(1)
    print("  Dependencies installed.")

    # Step 3: Test API key
    print("\nStep 3: Testing Voyage API key...")

    # Set up environment for test
    env = os.environ.copy()
    with open(env_file) as f:
        for line in f:
            line = line.strip()
            if line and '=' in line and not line.startswith('#'):
                key, value = line.split('=', 1)
                env[key] = value

    exit_code, output = run_command(
        ["npx", "tsx", "src/search.ts", "--stats"],
        cwd=script_dir
    )
    # Note: --stats doesn't need the API key, so we just check if the DB loads
    if exit_code != 0 or "error" in output.lower():
        print(f"  Warning: Test may have failed. Output:\n  {output}")
    else:
        print("  Database loaded successfully!")

    # Step 4: Configure Claude Code
    print("\nStep 4: Configuring Claude Code MCP server...")

    claude_config_path = get_claude_config_path()
    mcp_config = get_shell_command(script_dir)

    # Read existing config or create new
    if claude_config_path.exists():
        try:
            config = json.loads(claude_config_path.read_text())
        except json.JSONDecodeError:
            print(f"  Warning: Could not parse existing {claude_config_path}, creating new config")
            config = {}
    else:
        config = {}

    # Ensure mcpServers exists
    if "mcpServers" not in config:
        config["mcpServers"] = {}

    # Add or update hytale-rag server
    config["mcpServers"]["hytale-rag"] = mcp_config

    # Write config
    claude_config_path.write_text(json.dumps(config, indent=2))
    print(f"  Added 'hytale-rag' MCP server to {claude_config_path}")

    # Done
    print("\n=== Setup Complete ===\n")
    print("The 'hytale-rag' MCP server has been configured for Claude Code.\n")
    print("To use it:")
    print("  1. Restart Claude Code (or any running Claude Code instances)")
    print("  2. Ask Claude to search the Hytale codebase, e.g.:")
    print("     'Search the Hytale code for player movement handling'")
    print("     'Find methods related to inventory management'")
    print("\nAvailable tools:")
    print("  Server Code Search:")
    print("    - search_hytale_code: Semantic search over 37,000+ server methods")
    print("    - hytale_code_stats: Show server code database statistics")
    print("  Client UI Search:")
    print("    - search_hytale_client_code: Search 353 UI files (XAML, .ui, JSON)")
    print("    - hytale_client_code_stats: Show client UI statistics")
    print("  Game Data Search:")
    print("    - search_hytale_gamedata: Search 8,400+ items, recipes, NPCs, drops, etc.")
    print("    - hytale_gamedata_stats: Show game data statistics")


if __name__ == "__main__":
    main()
