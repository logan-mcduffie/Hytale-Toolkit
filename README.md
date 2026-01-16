# Hytale Modding Toolkit

A complete toolkit for Hytale server mod development.

## Setup

Before using this toolkit, you need to generate the decompiled source code and documentation locally. This is required due to [Hytale's EULA](https://hytale.com/eula) which prohibits distribution of game source code.

### 1. Decompile the Server

Run Vineflower to decompile the Hytale server JAR. Replace `<path-to-your-hytale-install>` with your Hytale installation directory (typically `C:\Users\<username>\AppData\Roaming\Hytale\install\release\package\game\latest`).

**Note:** This requires at least 8GB of heap space to complete successfully.

```bash
# Windows (PowerShell)
java -Xmx8G -jar tools/vineflower.jar -iec=1 -iib=1 -bsm=1 -dcl=1 -e=tools/annotations.jar "<path-to-your-hytale-install>/server/HytaleServer.jar" decompiled

# Linux/Mac
java -Xmx8G -jar tools/vineflower.jar -iec=1 -iib=1 -bsm=1 -dcl=1 -e=tools/annotations.jar "<path-to-your-hytale-install>/server/HytaleServer.jar" decompiled
```

This creates the `/decompiled` folder with the full server source code.

### 2. Fix Decompilation Artifacts

Fix assertion-related decompilation issues:

```bash
python fix-assertions.py
```

This fixes `<unrepresentable>` tokens that would prevent javadoc generation.

### 3. Generate Javadocs

Generate searchable API documentation from the decompiled source:

```bash
# Windows (PowerShell)
javadoc -d docs -sourcepath decompiled -subpackages com.hypixel -classpath tools/annotations.jar -quiet -Xdoclint:none --ignore-source-errors

# Linux/Mac
javadoc -d docs -sourcepath decompiled -subpackages com.hypixel -classpath tools/annotations.jar -quiet -Xdoclint:none --ignore-source-errors
```

This creates the `/docs` folder. Open `docs/index.html` in a browser.

**Note:** The `--ignore-source-errors` flag allows javadoc to generate documentation despite errors in third-party library code.

## Contents

### `/decompiled`
Decompiled Hytale server source code (generated locally). Browse and search the full server implementation.

### `/docs`
Javadocs generated from the decompiled source (generated locally). Open `index.html` in a browser for searchable API documentation.

### `/hytale-rag`
LLM-agnostic semantic search for Hytale code and game data. Includes indexed methods from both server and client code, plus 8,400+ game data items searchable by natural language.

**Option 1: Docker (Recommended)**

1. Download `lancedb.tar.gz` from [GitHub Releases](https://github.com/logan-mcduffie/Hytale-Toolkit/releases)
2. Get a free API key at https://www.voyageai.com/
3. Extract and set up:

```bash
tar -xzf lancedb.tar.gz  # Extract the pre-indexed database

# Save your API key (one-time setup)
echo "VOYAGE_API_KEY=your-key-here" > .env
```

4. Run the server:

```bash
# Linux/Mac
docker run --env-file .env -p 3000:3000 \
  -v $(pwd)/lancedb:/app/data/lancedb:ro \
  ghcr.io/logan-mcduffie/hytale-rag
```

```powershell
# Windows (PowerShell)
docker run --env-file .env -p 3000:3000 `
  -v "${PWD}/lancedb:/app/data/lancedb:ro" `
  ghcr.io/logan-mcduffie/hytale-rag
```

The REST API is now available at `http://localhost:3000`.

**Option 2: Local Development**

```bash
cd hytale-rag
npm install

# Set up your API key
cp .env.example .env
# Edit .env and add your Voyage API key

npm start
```

**Server Modes:**

Set `HYTALE_RAG_MODE` environment variable to choose which server(s) to run:
- `mcp` - MCP server for Claude Code (default when running via stdio)
- `rest` - REST API on port 3000 (default for Docker)
- `openai` - OpenAI-compatible function calling on port 3001
- `all` - All servers simultaneously

**REST API Example:**
```bash
# Search server code
curl -X POST http://localhost:3000/v1/search/code \
  -H "Content-Type: application/json" \
  -d '{"query": "player inventory management"}'

# Search client UI files
curl -X POST http://localhost:3000/v1/search/client-code \
  -H "Content-Type: application/json" \
  -d '{"query": "inventory hotbar layout"}'
```

**Indexing Client UI Files:**

To index the client UI files for semantic search:
```bash
cd hytale-rag
npm run ingest-client "<path-to-your-hytale-install>/Client/Data"
```

This indexes XAML templates, .ui components, and NodeEditor definitions for searching.

**Claude Code Integration:**

To use hytale-rag as an MCP server in Claude Code:

1. Get a free API key at https://www.voyageai.com/
2. Run the setup script:
   ```bash
   cd hytale-rag
   python setup.py
   ```
   The script will prompt for your API key, then install dependencies and configure Claude Code automatically. Works on Windows, macOS, and Linux.

3. Restart Claude Code

Then ask things like:
- "Search the Hytale server code for player movement handling"
- "Search the client UI files for inventory layout"
- "Find methods related to inventory management"
- "What items drop from zombies?"
- "How does the farming system work?"

### `/plugin-template`
A Maven project template for creating Hytale server plugins. Clone this as a starting point for your mod.

**Requirements:**
- Java 25
- Maven (or use your IDE's built-in Maven support)

**Setup:**
1. Copy the `plugin-template` folder
2. Update `pom.xml` with your plugin's groupId/artifactId
3. Install the Hytale server JAR to your local Maven repo:
   ```
   mvn install:install-file -Dfile="path/to/HytaleServer.jar" \
     -DgroupId=com.hypixel.hytale \
     -DartifactId=HytaleServer-parent \
     -Dversion=1.0-SNAPSHOT \
     -Dpackaging=jar
   ```

### `/tools`
Utilities used to generate this toolkit:
- `vineflower.jar` - Decompiler (for regenerating `/decompiled` when Hytale updates)
- `annotations.jar` - Annotation stubs for decompilation

## Quick Start

1. Run the [Setup](#setup) steps to generate `/decompiled` and `/docs`
2. Browse `/docs` for API reference
3. Use `/decompiled` to understand implementation details
4. Set up `/hytale-rag` for AI-powered code search
5. Copy `/plugin-template` to start your mod

## License

This toolkit is for educational and modding purposes. Hytale is a trademark of Hypixel Studios.
