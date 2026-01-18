# Hytale Modding RAG

Semantic code search for the decompiled Hytale server codebase. This MCP server lets Claude Code search 37,000+ methods using natural language queries.

## Prerequisites

- [Node.js](https://nodejs.org/) v18+
- [Claude Code](https://claude.ai/claude-code) CLI
- [Voyage AI API key](https://dash.voyageai.com/) (for query embedding)

## Quick Setup

1. Get a Voyage AI API key from https://dash.voyageai.com/
2. Run the setup script:

```powershell
.\setup.ps1 -VoyageApiKey "your-voyage-api-key"
```

3. Restart Claude Code

## What's Included

- **`data/lancedb/`** - Pre-built vector database with embeddings for all methods
- **`decompiled/`** - Decompiled Hytale server source code (symlink or copy from parent)
- **`src/mcp-server.ts`** - MCP server that Claude Code connects to

## Usage

Once set up, Claude Code will have access to two tools:

### `search_hytale_code`

Search the codebase using natural language:

- "How does player movement work?"
- "Find methods that handle block placement"
- "Show me inventory serialization code"
- "What handles entity collision?"

### `hytale_code_stats`

Show statistics about the indexed codebase.

## Manual Testing

You can test the search from the command line:

```powershell
$env:VOYAGE_API_KEY="your-key"
npm run search -- "player movement" -n 5
```

Options:
- `-n <num>` - Number of results (default 10)
- `-v` - Verbose output with full method code
- `--class <name>` - Filter to specific class

## Cost

Voyage AI charges per token for embeddings:
- The database is pre-built, so you only pay for **query embeddings**
- Each search query costs ~$0.0001 (a fraction of a cent)
- voyage-code-3 and voyage-4-large: generous free tier, then ~$0.05-0.12/M tokens

## Re-indexing (Optional)

If the decompiled code changes, you can re-index:

```powershell
$env:VOYAGE_API_KEY="your-key"
npm run ingest
```

This takes ~40 minutes and costs ~$1 in API calls.

## Project Structure

```
hytale-rag/
├── data/lancedb/          # Vector database (343MB)
├── src/
│   ├── mcp-server.ts      # MCP server for Claude Code
│   ├── search.ts          # CLI search tool
│   ├── ingest.ts          # Ingestion script
│   ├── parser.ts          # Java AST parser
│   ├── embedder.ts        # Voyage AI client
│   └── db.ts              # LanceDB operations
├── setup.ps1              # Windows setup script
├── package.json
└── README.md
```

## Troubleshooting

**"VOYAGE_API_KEY not set"**
- Make sure you ran `setup.ps1` with your API key
- Or set it manually: `$env:VOYAGE_API_KEY="your-key"`

**MCP server not appearing in Claude Code**
- Restart Claude Code after running setup
- Check `~/.claude.json` has the `hytale-rag` entry

**Search returns no results**
- Try broader queries
- Check that `data/lancedb/hytale_methods.lance` exists
