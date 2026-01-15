#!/usr/bin/env node
import { embedQuery } from "./embedder.js";
import { search, getStats, type SearchResult } from "./db.js";

import { fileURLToPath } from "url";
import { dirname, join } from "path";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const DEFAULT_DB_PATH = join(__dirname, "..", "data", "lancedb");

function formatResult(result: SearchResult, index: number, verbose: boolean): string {
  const lines: string[] = [];

  lines.push(`--- Result ${index + 1} (score: ${result.score.toFixed(3)}) ---`);
  lines.push(`ID: ${result.id}`);
  lines.push(`File: ${result.filePath}:${result.lineStart}-${result.lineEnd}`);
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
      file: r.filePath,
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
  let dbPath = DEFAULT_DB_PATH;
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

  const apiKey = process.env.VOYAGE_API_KEY;
  if (!apiKey && !showStats) {
    console.error("Error: VOYAGE_API_KEY environment variable is required");
    process.exit(1);
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
    queryVector = await embedQuery(query, apiKey!);
  } catch (e: any) {
    console.error(`Failed to embed query: ${e.message}`);
    process.exit(1);
  }

  // Build filter if class specified
  let filter: string | undefined;
  if (classFilter) {
    filter = `className = '${classFilter}'`;
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
  -d, --db PATH     Database path (default: ./data/lancedb)
  --class NAME      Filter by class name
  --stats           Show database statistics
  -h, --help        Show this help

Examples:
  npm run search -- "how to load assets from disk"
  npm run search -- -v -n 10 "event bus dispatch"
  npm run search -- --class AssetStore "validate codec"
  npm run search -- -c "plugin loading" > results.json

Environment:
  VOYAGE_API_KEY    Required for search queries
`);
}

main().catch((e) => {
  console.error("Fatal error:", e);
  process.exit(1);
});
