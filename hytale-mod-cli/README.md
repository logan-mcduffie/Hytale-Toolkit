# Hytale Mod CLI

Command-line tool for creating new Hytale mod projects.

## Installation

```bash
pip install -e .
```

## Usage

```bash
# Interactive mode
hytale-mod init

# Use current directory as project location (skips folder picker)
hytale-mod init --here

# With project name
hytale-mod init my-awesome-mod

# Full CLI mode (non-interactive)
hytale-mod init --name my-mod --group com.example --parent-dir ./projects --hytale-path "C:/path/to/hytale"

# See all options
hytale-mod init --help
```

## Features

- Creates Maven or Gradle project structure
- Configures HytaleGradle plugin
- Sets up IDE integration (IntelliJ / VS Code)
- Optionally initializes git repository
