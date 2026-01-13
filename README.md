# Hytale Modding Toolkit

A complete toolkit for Hytale server mod development.

## Contents

### `/decompiled`
Decompiled Hytale server source code. Browse and search the full server implementation.

### `/docs`
Javadocs generated from the decompiled source. Open `index.html` in a browser for searchable API documentation.

### `/hytale-rag`
Semantic code search powered by AI embeddings. Includes 37,000+ indexed methods searchable by natural language.

**Setup (for Claude Code users):**
```powershell
cd hytale-rag
.\setup.ps1 -VoyageApiKey "your-voyage-api-key"
```

Get a free Voyage API key at https://dash.voyageai.com/

After setup, ask Claude things like:
- "Search the Hytale code for player movement handling"
- "Find methods related to inventory management"
- "How does the server handle block placement?"

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
