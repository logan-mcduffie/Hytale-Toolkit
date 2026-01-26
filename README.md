<div align="center">

<img src=".github/logo.png" alt="Hytale Modding Toolkit" width="200">

# Hytale Modding Toolkit

*Searchable docs • AI-powered code search • Ready-to-use templates*

[![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)
[![Wiki](https://img.shields.io/badge/Docs-Wiki-green?style=flat-square&logo=github)](https://github.com/logan-mcduffie/Hytale-Toolkit/wiki)
[![Discord](https://img.shields.io/badge/Discord-Join-5865F2?style=flat-square&logo=discord&logoColor=white)](https://discord.gg/WzPC6pXpqk)
[![Demo Video](https://img.shields.io/badge/Demo-Watch_Video-FF0000?style=flat-square&logo=youtube&logoColor=white)](https://youtu.be/txK8oWLufjA)

<sub><i>"Give me six hours to chop down a tree and I will spend the first four sharpening the axe."</i>
<br>
— Abraham Lincoln</sub>

</div>

## The Problem

Hytale's modding API is powerful but undocumented. As a mod developer, you're working blind:

- **No official documentation** - The server JAR has thousands of classes, but no guides on how to use them
- **No searchable reference** - You can't easily find the right class or method for what you need
- **No project templates** - Setting up a mod project from scratch means guessing at the build configuration
- **No AI assistance** - LLMs can't help because they don't have access to Hytale's codebase

You end up spending more time reverse-engineering the server than actually building your mod.

## The Solution

This toolkit gives you everything you need to understand and work with the Hytale server:

### 📚 Searchable Javadocs
Generate browsable API documentation from the decompiled server. Find classes, methods, and their signatures instantly.

### 🔍 AI-Powered Code Search
Ask natural language questions like *"how does player inventory work?"* and get relevant code snippets. Works with Claude Code, REST APIs, or any OpenAI-compatible client.

### 🚀 Ready-to-Use Templates
Start building immediately with a pre-configured Maven project. Or use the `/init-mod` command with your AI assistant to scaffold a complete mod in seconds.

### 💻 Full Source Access
When the docs aren't enough, browse the actual decompiled implementation to understand exactly how things work.

## Quick Start

Download and run **`hytale-setup.exe`** from the [latest release](https://github.com/logan-mcduffie/Hytale-Toolkit/releases/latest).

<details>
<summary>Or run from source (requires Python 3.10+)</summary>

```bash
pip install PyQt6 Pillow
python hytale-rag/setup_gui_pyqt.py
```
</details>

The interactive wizard will:

1. **Locate your Hytale installation** - Point it to your `latest` folder
2. **Decompile the server** - Extract readable Java source code
3. **Generate Javadocs** (optional) - Browsable HTML documentation
4. **Set up semantic search** - Choose Voyage AI (cloud) or Ollama (local)
5. **Download the RAG database** - Pre-indexed for instant search
6. **Configure your AI tools** - VS Code, JetBrains, or CLI integrations
7. **Install CLI tools** (optional) - `hytale-mod init` command for scaffolding mods

Once complete, restart your AI assistant and ask it questions like:
- *"How does player movement work in Hytale?"*
- *"Find methods related to inventory management"*
- *"What NPCs drop iron ore?"*

### Manual Search (No AI Required)

```bash
cd hytale-rag
npx tsx src/search.ts "player inventory"
npx tsx src/search.ts --stats
```

## Making Your First Mod

```bash
hytale-mod init
```
<div align="center">
<sub>This toolkit is for educational and modding purposes. Hytale is a trademark of Hypixel Studios.</sub>
</div>
