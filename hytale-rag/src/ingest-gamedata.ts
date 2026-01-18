#!/usr/bin/env node
/**
 * Ingest Hytale game data from Assets.zip into LanceDB.
 *
 * Supports multiple embedding providers via EMBEDDING_PROVIDER env var.
 * Outputs to data/{provider}/lancedb by default.
 *
 * Incremental: Only re-embeds files that have changed (based on content hash).
 * Use --full flag to force complete re-index.
 *
 * Usage:
 *   EMBEDDING_PROVIDER=voyage VOYAGE_API_KEY=xxx npm run ingest-gamedata [assets-zip-path]
 *   EMBEDDING_PROVIDER=ollama npm run ingest-gamedata [assets-zip-path]
 *   npm run ingest-gamedata [assets-zip-path] --full  # Force full re-index
 */

import { parseAssetsZip, getZipStats } from "./gamedata-parser.js";
import { embedGameDataChunks, getModelName, type IngestEmbeddingConfig } from "./embedder.js";
import {
  createGameDataTable,
  tableExists,
  getGameDataFileHashes,
  deleteGameDataByFilePaths,
  addGameDataChunks,
} from "./db.js";
import type { GameDataChunk, EmbeddedGameDataChunk } from "./types.js";
import * as path from "path";
import * as fs from "fs";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const DEFAULT_ASSETS_ZIP = "D:/Roaming/install/release/package/game/latest/Assets.zip";

function getDefaultDbPath(provider: string): string {
  return path.resolve(__dirname, "..", "data", provider, "lancedb");
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

/**
 * Compute which files are new, changed, or deleted compared to existing database
 */
function computeChanges(
  chunks: GameDataChunk[],
  existingHashes: Map<string, string>
): {
  newFiles: string[];
  changedFiles: string[];
  deletedFiles: string[];
  unchangedFiles: string[];
} {
  const currentFileHashes = new Map<string, string>();
  for (const chunk of chunks) {
    currentFileHashes.set(chunk.filePath, chunk.fileHash);
  }

  const newFiles: string[] = [];
  const changedFiles: string[] = [];
  const unchangedFiles: string[] = [];

  for (const [filePath, hash] of currentFileHashes) {
    const existingHash = existingHashes.get(filePath);
    if (!existingHash) {
      newFiles.push(filePath);
    } else if (existingHash !== hash) {
      changedFiles.push(filePath);
    } else {
      unchangedFiles.push(filePath);
    }
  }

  // Find deleted files (in existing but not in current)
  const deletedFiles: string[] = [];
  for (const filePath of existingHashes.keys()) {
    if (!currentFileHashes.has(filePath)) {
      deletedFiles.push(filePath);
    }
  }

  return { newFiles, changedFiles, deletedFiles, unchangedFiles };
}

async function main() {
  const args = process.argv.slice(2);
  const forceFullIndex = args.includes("--full");
  const nonFlagArgs = args.filter((a) => !a.startsWith("--"));

  const zipPath = nonFlagArgs[0] || DEFAULT_ASSETS_ZIP;
  const embeddingConfig = getEmbeddingConfig();
  const dbPath = nonFlagArgs[1] || getDefaultDbPath(embeddingConfig.provider);

  console.log("=== Hytale Game Data Ingestion ===");
  console.log("");
  console.log(`Provider: ${embeddingConfig.provider}`);
  console.log(`Assets zip: ${zipPath}`);
  console.log(`Database path: ${dbPath}`);
  console.log(`Mode: ${forceFullIndex ? "Full re-index" : "Incremental"}`);
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
    const errorFile = path.join(dbPath, "gamedata_parse_errors.txt");
    fs.writeFileSync(errorFile, errors.join("\n"));
    console.log(`  Error log: ${errorFile}`);
  }
  console.log("");

  if (chunks.length === 0) {
    console.error("No game data found to embed. Check the Assets.zip path.");
    process.exit(1);
  }

  // Step 2: Check for incremental update
  let hasExistingTable = !forceFullIndex && (await tableExists(dbPath, "hytale_gamedata"));
  let chunksToEmbed: GameDataChunk[] = chunks;
  let filesToDelete: string[] = [];
  let isIncremental = false;

  if (hasExistingTable) {
    console.log("Step 2: Checking for changes (incremental mode)...");
    const existingHashes = await getGameDataFileHashes(dbPath);
    console.log(`  Found ${existingHashes.size} existing files in database`);

    // If table exists but has no hashes, schema is incompatible (pre-fileHash version)
    // Fall back to full re-index
    if (existingHashes.size === 0 && chunks.length > 0) {
      console.log("  Schema incompatible (missing fileHash column), forcing full re-index...");
      hasExistingTable = false;
    } else {
      const changes = computeChanges(chunks, existingHashes);
      console.log(`  New files: ${changes.newFiles.length}`);
      console.log(`  Changed files: ${changes.changedFiles.length}`);
      console.log(`  Deleted files: ${changes.deletedFiles.length}`);
      console.log(`  Unchanged files: ${changes.unchangedFiles.length}`);

      // Filter chunks to only those from new/changed files
      const filesToEmbed = new Set([...changes.newFiles, ...changes.changedFiles]);
      chunksToEmbed = chunks.filter((c) => filesToEmbed.has(c.filePath));
      filesToDelete = [...changes.changedFiles, ...changes.deletedFiles];
      isIncremental = true;

      if (chunksToEmbed.length === 0 && filesToDelete.length === 0) {
        console.log("");
        console.log("=== No Changes Detected ===");
        console.log("Database is already up to date.");
        return;
      }

      console.log(`  Chunks to embed: ${chunksToEmbed.length}`);
      console.log(`  Files to delete: ${filesToDelete.length}`);
      console.log("");
    }
  }

  // Step 3: Embed chunks (only new/changed ones in incremental mode)
  const embedStepNum = hasExistingTable ? 3 : 2;
  let embeddedChunks: EmbeddedGameDataChunk[] = [];

  if (chunksToEmbed.length > 0) {
    const modelName = getModelName(embeddingConfig, "text");
    console.log(
      `Step ${embedStepNum}: Embedding ${isIncremental ? "changed " : ""}game data with ${embeddingConfig.provider} (${modelName})...`
    );
    const startEmbed = Date.now();

    try {
      embeddedChunks = await embedGameDataChunks(chunksToEmbed, embeddingConfig, (current, total) => {
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
  }

  // Step 4: Store in LanceDB
  const storeStepNum = embedStepNum + 1;
  console.log(`Step ${storeStepNum}: Storing in LanceDB (hytale_gamedata table)...`);
  const startStore = Date.now();

  try {
    if (isIncremental) {
      // Delete old entries first, then add new ones
      if (filesToDelete.length > 0) {
        const deleted = await deleteGameDataByFilePaths(dbPath, filesToDelete);
        console.log(`  Deleted ${deleted} entries from ${filesToDelete.length} files`);
      }
      if (embeddedChunks.length > 0) {
        await addGameDataChunks(dbPath, embeddedChunks);
        console.log(`  Added ${embeddedChunks.length} new entries`);
      }
    } else {
      // Full index - create new table
      await createGameDataTable(dbPath, embeddedChunks);
    }
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
  console.log(`Provider: ${embeddingConfig.provider}`);
  console.log(`Mode: ${isIncremental ? "Incremental" : "Full"}`);
  if (isIncremental) {
    console.log(`Chunks embedded: ${embeddedChunks.length}`);
    console.log(`Files deleted: ${filesToDelete.length}`);
  } else {
    console.log(`Total items indexed: ${embeddedChunks.length}`);
  }
  console.log(`Total time: ${totalTime}s`);
  console.log(`Database location: ${dbPath}`);
}

main().catch((e) => {
  console.error("Fatal error:", e);
  process.exit(1);
});
