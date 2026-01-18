#!/usr/bin/env node
/**
 * Ingest script for server code (Java methods)
 *
 * Supports multiple embedding providers via EMBEDDING_PROVIDER env var.
 * Outputs to data/{provider}/lancedb by default.
 *
 * IMPORTANT: Only indexes Hytale code (com.hypixel.hytale.*), not third-party dependencies.
 *
 * Features:
 * - Incremental indexing: only re-embeds changed files (based on content hash)
 * - Use --full flag to force a complete re-index
 *
 * Usage:
 *   EMBEDDING_PROVIDER=voyage VOYAGE_API_KEY=xxx npm run ingest /path/to/decompiled
 *   EMBEDDING_PROVIDER=ollama npm run ingest /path/to/decompiled
 *   npm run ingest /path/to/decompiled --full  # Force full re-index
 */

import { parseDirectory, type MethodChunk } from "./parser.js";
import { embedChunks, getModelName, type EmbeddedChunk, type IngestEmbeddingConfig } from "./embedder.js";
import {
  createTable,
  tableExists,
  getExistingFileHashes,
  deleteByFilePaths,
  addChunks,
} from "./db.js";
import * as path from "path";
import * as fs from "fs";
import { fileURLToPath } from "url";

// Only index code from these packages (Hytale's own code, not dependencies)
// Path patterns to match (supports both / and \ separators)
const HYTALE_PATH_PATTERNS = [/[/\\]com[/\\]hypixel[/\\]hytale[/\\]/];

function isHytalePath(filePath: string): boolean {
  return HYTALE_PATH_PATTERNS.some((pattern) => pattern.test(filePath));
}

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

function getDefaultDbPath(provider: string): string {
  return path.join(__dirname, "..", "data", provider, "lancedb");
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

interface IncrementalChanges {
  newFiles: string[];
  changedFiles: string[];
  deletedFiles: string[];
  unchangedFiles: string[];
}

/**
 * Compare current file hashes with existing ones to determine what changed
 */
function computeChanges(
  currentChunks: MethodChunk[],
  existingHashes: Map<string, string>
): IncrementalChanges {
  const currentFileHashes = new Map<string, string>();

  // Build map of current file -> hash
  for (const chunk of currentChunks) {
    if (!currentFileHashes.has(chunk.filePath)) {
      currentFileHashes.set(chunk.filePath, chunk.fileHash);
    }
  }

  const newFiles: string[] = [];
  const changedFiles: string[] = [];
  const unchangedFiles: string[] = [];

  // Check each current file
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

  const sourceDir = nonFlagArgs[0];
  const embeddingConfig = getEmbeddingConfig();
  const dbPath = nonFlagArgs[1] || getDefaultDbPath(embeddingConfig.provider);

  if (!sourceDir) {
    console.error("Error: Source directory required");
    console.error("Usage: EMBEDDING_PROVIDER=voyage npm run ingest <source-dir> [db-path] [--full]");
    console.error("       EMBEDDING_PROVIDER=ollama npm run ingest <source-dir> [db-path] [--full]");
    console.error("");
    console.error("Options:");
    console.error("  --full    Force complete re-index (ignore existing data)");
    process.exit(1);
  }

  console.log(`Provider: ${embeddingConfig.provider}`);
  console.log(`Source directory: ${sourceDir}`);
  console.log(`Database path: ${dbPath}`);
  console.log(`Mode: ${forceFullIndex ? "Full re-index" : "Incremental"}`);
  console.log("");

  // Ensure db directory exists
  fs.mkdirSync(dbPath, { recursive: true });

  // Step 1: Parse Java files (only Hytale code, not dependencies)
  console.log("Step 1: Parsing Hytale Java files (filtering dependencies)...");
  const startParse = Date.now();

  const { chunks, errors } = await parseDirectory(
    sourceDir,
    (current, total, _file) => {
      if (current % 100 === 0 || current === total) {
        process.stdout.write(`\r  Parsed ${current}/${total} files`);
      }
    },
    isHytalePath // Only parse files in com/hypixel/hytale
  );
  console.log("");

  const parseTime = ((Date.now() - startParse) / 1000).toFixed(1);
  console.log(`  Parsed ${chunks.length} Hytale methods`);
  console.log(`  Parse time: ${parseTime}s`);

  if (errors.length > 0) {
    console.log(`  Errors: ${errors.length}`);
    const errorFile = path.join(dbPath, "parse_errors.txt");
    fs.writeFileSync(errorFile, errors.join("\n"));
    console.log(`  Error log: ${errorFile}`);
  }
  console.log("");

  if (chunks.length === 0) {
    console.error("No Hytale methods found to embed. Check the source directory.");
    process.exit(1);
  }

  // Step 2: Check for incremental update
  const hasExistingTable = !forceFullIndex && (await tableExists(dbPath));
  let chunksToEmbed: MethodChunk[] = chunks;
  let filesToDelete: string[] = [];
  let isIncremental = false;

  if (hasExistingTable) {
    console.log("Step 2: Checking for changes (incremental mode)...");
    const existingHashes = await getExistingFileHashes(dbPath);
    console.log(`  Found ${existingHashes.size} existing files in database`);

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

    console.log(`  Methods to embed: ${chunksToEmbed.length}`);
    console.log(`  Methods to delete: will remove from ${filesToDelete.length} files`);
    console.log("");
  }

  // Step 3: Embed chunks (only new/changed ones in incremental mode)
  const embedStepNum = hasExistingTable ? 3 : 2;
  if (chunksToEmbed.length > 0) {
    const modelName = getModelName(embeddingConfig, "code");
    console.log(
      `Step ${embedStepNum}: Embedding ${isIncremental ? "changed " : ""}methods with ${embeddingConfig.provider} (${modelName})...`
    );
    const startEmbed = Date.now();

    let embeddedChunks: EmbeddedChunk[];
    try {
      embeddedChunks = await embedChunks(chunksToEmbed, embeddingConfig, (current, total) => {
        process.stdout.write(`\r  Embedded ${current}/${total} methods`);
      });
      console.log("");
    } catch (e: any) {
      console.error(`\nEmbedding failed: ${e.message}`);
      process.exit(1);
    }

    const embedTime = ((Date.now() - startEmbed) / 1000).toFixed(1);
    console.log(`  Embed time: ${embedTime}s`);
    console.log("");

    // Step 4: Store in LanceDB
    const storeStepNum = embedStepNum + 1;
    console.log(`Step ${storeStepNum}: Storing in LanceDB...`);
    const startStore = Date.now();

    try {
      if (isIncremental) {
        // Delete old records for changed/deleted files
        if (filesToDelete.length > 0) {
          const deleted = await deleteByFilePaths(dbPath, filesToDelete);
          console.log(`  Deleted ${deleted} old methods`);
        }
        // Add new records
        await addChunks(dbPath, embeddedChunks);
        console.log(`  Added ${embeddedChunks.length} new methods`);
      } else {
        // Full index - create new table
        await createTable(dbPath, embeddedChunks);
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
    console.log("=== Ingestion Complete ===");
    console.log(`Provider: ${embeddingConfig.provider}`);
    console.log(`Mode: ${isIncremental ? "Incremental" : "Full"}`);
    console.log(`Methods embedded: ${embeddedChunks.length}`);
    console.log(`Total time: ${totalTime}s`);
    console.log(`Database location: ${dbPath}`);
  } else if (filesToDelete.length > 0) {
    // Only deletions, no new embeddings needed
    console.log(`Step ${embedStepNum}: Removing deleted files from database...`);
    const deleted = await deleteByFilePaths(dbPath, filesToDelete);
    console.log(`  Deleted ${deleted} methods from ${filesToDelete.length} files`);

    const totalTime = ((Date.now() - startParse) / 1000).toFixed(1);
    console.log("");
    console.log("=== Ingestion Complete ===");
    console.log(`Mode: Incremental (deletions only)`);
    console.log(`Methods removed: ${deleted}`);
    console.log(`Total time: ${totalTime}s`);
  }
}

main().catch((e) => {
  console.error("Fatal error:", e);
  process.exit(1);
});
