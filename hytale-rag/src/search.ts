#!/usr/bin/env node
import "dotenv/config";
import { embedQuery, type IngestEmbeddingConfig } from "./embedder.js";
import { search, getStats, type SearchResult } from "./db.js";
import * as fs from "fs";
import { resolveCodePath } from "./utils/paths.js";

import { fileURLToPath } from "url";
import { dirname, join, resolve } from "path";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

function getDefaultDbPath(provider: string): string {
  return join(__dirname, "..", "data", provider, "lancedb");
}

function getEmbeddingConfig(): IngestEmbeddingConfig {
  const provider = (process.env.EMBEDDING_PROVIDER || "voyage") as "voyage" | "ollama";

  if (provider === "voyage") {
    const apiKey = process.env.VOYAGE_API_KEY;
    if (!apiKey) {
      console.error("Error: VOYAGE_API_KEY environment variable is required for Voyage AI");
      process.exit(1);
    }
    return { provider: "voyage", apiKey };
  } else if (provider === "ollama") {
    return {
      provider: "ollama",
      baseUrl: process.env.OLLAMA_BASE_URL,
      model: process.env.OLLAMA_MODEL || "nomic-embed-text",
    };
  } else {
    console.error(`Error: Unknown provider "${provider}". Use "voyage" or "ollama".`);
    process.exit(1);
  }
}

function formatResult(result: SearchResult, index: number, verbose: boolean): string {
  const lines: string[] = [];
  const fullPath = resolveCodePath(result.filePath);

  lines.push(`--- Result ${index + 1} (score: ${result.score.toFixed(3)}) ---`);
  lines.push(`ID: ${result.id}`);
  lines.push(`File: ${fullPath}:${result.lineStart}-${result.lineEnd}`);
  lines.push(`Signature: ${result.methodSignature}`);

  if (verbose) {
    lines.push("");
    lines.push("Code:");
    lines.push(result.content);
  }

  return lines.join("\n");
}

function formatCompact(results: SearchResult[]): string {
  // Compact JSON output for programmatic use
  return JSON.stringify(
    results.map((r) => ({
      id: r.id,
      file: resolveCodePath(r.filePath),
      line: r.lineStart,
      signature: r.methodSignature,
      score: r.score,
      code: r.content,
    })),
    null,
    2
  );
}

async function main() {
  const args = process.argv.slice(2);

  // Parse flags
  let verbose = false;
  let compact = false;
  let limit = 5;
  let dbPath = "";
  let query = "";
  let showStats = false;
  let classFilter = "";

  for (let i = 0; i < args.length; i++) {
    const arg = args[i];
    if (arg === "-v" || arg === "--verbose") {
      verbose = true;
    } else if (arg === "-c" || arg === "--compact") {
      compact = true;
    } else if (arg === "-n" || arg === "--limit") {
      limit = parseInt(args[++i], 10);
    } else if (arg === "-d" || arg === "--db") {
      dbPath = args[++i];
    } else if (arg === "--stats") {
      showStats = true;
    } else if (arg === "--class") {
      classFilter = args[++i];
    } else if (arg === "-h" || arg === "--help") {
      printHelp();
      process.exit(0);
    } else if (!arg.startsWith("-")) {
      // Collect remaining args as query
      query = args.slice(i).join(" ");
      break;
    }
  }

  // Get embedding config (validates API key for voyage)
  let embeddingConfig: IngestEmbeddingConfig | undefined;
  if (!showStats) {
    embeddingConfig = getEmbeddingConfig();
  }

  // Set default DB path based on provider
  if (!dbPath) {
    const provider = process.env.EMBEDDING_PROVIDER || "voyage";
    dbPath = getDefaultDbPath(provider);
  }

  // Show stats if requested
  if (showStats) {
    try {
      const stats = await getStats(dbPath);
      console.log("Database Statistics:");
      console.log(`  Total methods: ${stats.totalMethods}`);
      console.log(`  Unique classes: ${stats.uniqueClasses}`);
      console.log(`  Unique packages: ${stats.uniquePackages}`);
    } catch (e: any) {
      console.error(`Failed to get stats: ${e.message}`);
      process.exit(1);
    }
    return;
  }

  if (!query) {
    console.error("Error: Query required");
    printHelp();
    process.exit(1);
  }

  // Embed the query
  let queryVector: number[];
  try {
    queryVector = await embedQuery(query, embeddingConfig!);
  } catch (e: any) {
    console.error(`Failed to embed query: ${e.message}`);
    process.exit(1);
  }

  // Build filter if class specified
  // Use backticks for column name to preserve case sensitivity (LanceDB requires backticks for camelCase)
  let filter: string | undefined;
  if (classFilter) {
    filter = `\`className\` = '${classFilter}'`;
  }

  // Search
  let results: SearchResult[];
  try {
    results = await search(dbPath, queryVector, limit, filter);
  } catch (e: any) {
    console.error(`Search failed: ${e.message}`);
    process.exit(1);
  }

  // Output results
  if (compact) {
    console.log(formatCompact(results));
  } else {
    if (results.length === 0) {
      console.log("No results found.");
    } else {
      console.log(`Found ${results.length} results for: "${query}"\n`);
      for (let i = 0; i < results.length; i++) {
        console.log(formatResult(results[i], i, verbose));
        console.log("");
      }
    }
  }
}

function printHelp() {
  console.log(`
hytale-rag search - Search the Hytale codebase using semantic similarity

Usage:
  npm run search -- [options] <query>

Options:
  -v, --verbose     Show full code content in results
  -c, --compact     Output JSON format (for programmatic use)
  -n, --limit N     Number of results (default: 5)
  -d, --db PATH     Database path (default: ./data/{provider}/lancedb)
  --class NAME      Filter by class name
  --stats           Show database statistics
  -h, --help        Show this help

Examples:
  npm run search -- "how to load assets from disk"
  npm run search -- -v -n 10 "event bus dispatch"
  npm run search -- --class AssetStore "validate codec"
  npm run search -- -c "plugin loading" > results.json

Environment:
  EMBEDDING_PROVIDER   Provider to use: "voyage" (default) or "ollama"
  VOYAGE_API_KEY       Required when using Voyage AI
  OLLAMA_BASE_URL      Ollama server URL (default: http://localhost:11434)
  OLLAMA_MODEL         Ollama model name (default: nomic-embed-text)
`);
}

main().catch((e) => {
  console.error("Fatal error:", e);
  process.exit(1);
});
