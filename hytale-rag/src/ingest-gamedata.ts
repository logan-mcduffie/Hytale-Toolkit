#!/usr/bin/env node
/**
 * Ingest Hytale game data from Assets.zip into LanceDB.
 *
 * Usage: npm run ingest-gamedata [assets-zip-path] [db-path]
 */

import { parseAssetsZip, getZipStats } from "./gamedata-parser.js";
import { embedGameDataChunks } from "./embedder.js";
import { createGameDataTable } from "./db.js";
import type { EmbeddedGameDataChunk } from "./types.js";
import * as path from "path";
import * as fs from "fs";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const DEFAULT_ASSETS_ZIP = "D:/Roaming/install/release/package/game/latest/Assets.zip";
const DEFAULT_DB_PATH = path.resolve(__dirname, "..", "data", "lancedb");

async function main() {
  const zipPath = process.argv[2] || DEFAULT_ASSETS_ZIP;
  const dbPath = process.argv[3] || DEFAULT_DB_PATH;

  const apiKey = process.env.VOYAGE_API_KEY;
  if (!apiKey) {
    console.error("Error: VOYAGE_API_KEY environment variable is required");
    process.exit(1);
  }

  console.log(`Assets zip: ${zipPath}`);
  console.log(`Database path: ${dbPath}`);
  console.log("");

  // Check if zip file exists
  if (!fs.existsSync(zipPath)) {
    console.error(`Error: Assets.zip not found at ${zipPath}`);
    process.exit(1);
  }

  // Ensure db directory exists
  fs.mkdirSync(dbPath, { recursive: true });

  // Quick stats preview
  console.log("Scanning Assets.zip...");
  const zipStats = await getZipStats(zipPath);
  console.log(`  Total files: ${zipStats.totalFiles}`);
  console.log(`  JSON files: ${zipStats.jsonFiles}`);
  console.log(`  Matching game data files: ${zipStats.matchingFiles}`);
  console.log("");
  console.log("Files by type:");
  for (const [type, count] of Object.entries(zipStats.byType).sort((a, b) => b[1] - a[1])) {
    console.log(`  ${type}: ${count}`);
  }
  console.log("");

  // Step 1: Parse Assets.zip
  console.log("Step 1: Parsing game data from Assets.zip...");
  const startParse = Date.now();

  const { chunks, errors } = await parseAssetsZip(zipPath, (current, total, file) => {
    if (current % 100 === 0 || current === total) {
      process.stdout.write(`\r  Parsed ${current}/${total} files`);
    }
  });
  console.log("");

  const parseTime = ((Date.now() - startParse) / 1000).toFixed(1);
  console.log(`  Parsed ${chunks.length} game data chunks`);
  console.log(`  Parse time: ${parseTime}s`);

  if (errors.length > 0) {
    console.log(`  Errors: ${errors.length}`);
    // Write errors to file for debugging
    const errorFile = path.join(dbPath, "gamedata_parse_errors.txt");
    fs.writeFileSync(errorFile, errors.join("\n"));
    console.log(`  Error log: ${errorFile}`);
  }
  console.log("");

  if (chunks.length === 0) {
    console.error("No game data found to embed. Check the Assets.zip path.");
    process.exit(1);
  }

  // Step 2: Embed chunks with voyage-3
  console.log("Step 2: Embedding game data with Voyage AI (voyage-3)...");
  const startEmbed = Date.now();

  let embeddedChunks: EmbeddedGameDataChunk[];
  try {
    embeddedChunks = await embedGameDataChunks(chunks, apiKey, (current, total) => {
      process.stdout.write(`\r  Embedded ${current}/${total} chunks`);
    });
    console.log("");
  } catch (e: any) {
    console.error(`\nEmbedding failed: ${e.message}`);
    process.exit(1);
  }

  const embedTime = ((Date.now() - startEmbed) / 1000).toFixed(1);
  console.log(`  Embed time: ${embedTime}s`);
  console.log("");

  // Step 3: Store in LanceDB
  console.log("Step 3: Storing in LanceDB (hytale_gamedata table)...");
  const startStore = Date.now();

  try {
    await createGameDataTable(dbPath, embeddedChunks);
  } catch (e: any) {
    console.error(`Storage failed: ${e.message}`);
    process.exit(1);
  }

  const storeTime = ((Date.now() - startStore) / 1000).toFixed(1);
  console.log(`  Store time: ${storeTime}s`);
  console.log("");

  // Summary
  const totalTime = ((Date.now() - startParse) / 1000).toFixed(1);
  console.log("=== Game Data Ingestion Complete ===");
  console.log(`Total items indexed: ${embeddedChunks.length}`);
  console.log(`Total time: ${totalTime}s`);
  console.log(`Database location: ${dbPath}`);
  console.log("");
  console.log("You can now use the search_hytale_gamedata MCP tool to query this data.");
}

main().catch((e) => {
  console.error("Fatal error:", e);
  process.exit(1);
});
