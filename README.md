# Hytale Modding Toolkit

A complete toolkit for Hytale server mod development.

## Contents

### `/decompiled`
Decompiled Hytale server source code. Browse and search the full server implementation.

### `/docs`
Javadocs generated from the decompiled source. Open `index.html` in a browser for searchable API documentation.

### `/hytale-rag`
LLM-agnostic semantic search for Hytale code and game data. Includes 37,000+ indexed methods and 8,400+ game data items searchable by natural language.

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
VOYAGE_API_KEY=your-key npm start
```

**Server Modes:**

Set `HYTALE_RAG_MODE` environment variable to choose which server(s) to run:
- `mcp` - MCP server for Claude Code (default when running via stdio)
- `rest` - REST API on port 3000 (default for Docker)
- `openai` - OpenAI-compatible function calling on port 3001
- `all` - All servers simultaneously

**REST API Example:**
```bash
curl -X POST http://localhost:3000/v1/search/code \
  -H "Content-Type: application/json" \
  -d '{"query": "player inventory management"}'
```

**Claude Code Integration:**

Configure hytale-rag as an MCP server in Claude Code, then ask things like:
- "Search the Hytale code for player movement handling"
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

1. Browse `/docs` for API reference
2. Use `/decompiled` to understand implementation details
3. Set up `/hytale-rag` for AI-powered code search
4. Copy `/plugin-template` to start your mod

## License

This toolkit is for educational and modding purposes. Hytale is a trademark of Hypixel Studios.
