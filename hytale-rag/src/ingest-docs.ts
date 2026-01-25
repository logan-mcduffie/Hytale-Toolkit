#!/usr/bin/env node
import "dotenv/config";
/**
 * Ingest HytaleModding.dev documentation into LanceDB.
 *
 * Supports multiple embedding providers via EMBEDDING_PROVIDER env var.
 * Outputs to data/{provider}/lancedb by default.
 *
 * Incremental: Only re-embeds files that have changed (based on content hash).
 * Use --full flag to force complete re-index.
 *
 * Usage:
 *   EMBEDDING_PROVIDER=voyage VOYAGE_API_KEY=xxx npm run ingest-docs
 *   EMBEDDING_PROVIDER=ollama npm run ingest-docs
 *   npm run ingest-docs --full  # Force full re-index
 */

import { parseDocsDirectory, getDocsStats, type DocsChunk } from "./docs-parser.js";
import { embedDocsChunks, getModelName, type IngestEmbeddingConfig } from "./embedder.js";
import type { EmbeddedDocsChunk } from "./types.js";
import {
  createDocsTable,
  tableExists,
  getDocsFileHashes,
  deleteDocsByFilePaths,
  addDocsChunks,
} from "./db.js";
import * as path from "path";
import * as fs from "fs";
import { fileURLToPath } from "url";
import { execSync } from "child_process";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Configuration
const REPO_URL = "https://github.com/HytaleModding/site.git";
// Use existing clone in Hytale-Toolkit/site if it exists, otherwise fall back to data folder
const DEFAULT_REPO_DIR = fs.existsSync(path.resolve(__dirname, "..", "..", "site", ".git"))
  ? path.resolve(__dirname, "..", "..", "site")
  : path.resolve(__dirname, "..", "data", "hytalemodding-site");
const DOCS_SUBPATH = "content/docs/en"; // Path within the repo to English docs
const TABLE_NAME = "hytale_docs";

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
 * Clone or update the HytaleModding site repository
 */
function syncRepository(repoDir: string): void {
  if (fs.existsSync(path.join(repoDir, ".git"))) {
    // Repository exists, pull latest changes
    console.log("Pulling latest changes from repository...");
    try {
      execSync("git pull --ff-only", { cwd: repoDir, stdio: "inherit" });
    } catch (error) {
      console.warn("Warning: git pull failed, using existing files");
    }
  } else {
    // Clone the repository
    console.log("Cloning HytaleModding site repository...");
    fs.mkdirSync(path.dirname(repoDir), { recursive: true });
    execSync(`git clone --depth 1 ${REPO_URL} "${repoDir}"`, { stdio: "inherit" });
  }
}

/**
 * Compute which files are new, changed, or deleted compared to existing database
 */
function computeChanges(
  chunks: DocsChunk[],
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
  const skipSync = args.includes("--skip-sync");
  const nonFlagArgs = args.filter((a) => !a.startsWith("--"));

  const repoDir = nonFlagArgs[0] || DEFAULT_REPO_DIR;
  const embeddingConfig = getEmbeddingConfig();
  const dbPath = nonFlagArgs[1] || getDefaultDbPath(embeddingConfig.provider);

  console.log("=== HytaleModding.dev Documentation Ingestion ===");
  console.log("");
  console.log(`Provider: ${embeddingConfig.provider}`);
  console.log(`Repository: ${repoDir}`);
  console.log(`Database path: ${dbPath}`);
  console.log(`Table name: ${TABLE_NAME}`);
  console.log(`Mode: ${forceFullIndex ? "Full re-index" : "Incremental"}`);
  console.log("");

  // Step 1: Sync repository
  if (!skipSync) {
    console.log("Step 1: Syncing repository...");
    syncRepository(repoDir);
    console.log("");
  } else {
    console.log("Step 1: Skipping repository sync (--skip-sync)");
    console.log("");
  }

  // Check if docs directory exists
  const docsPath = path.join(repoDir, DOCS_SUBPATH);
  if (!fs.existsSync(docsPath)) {
    console.error(`Error: Documentation directory not found: ${docsPath}`);
    console.error("");
    console.error("Make sure the repository was cloned correctly.");
    process.exit(1);
  }

  // Ensure db directory exists
  fs.mkdirSync(dbPath, { recursive: true });

  // Show stats preview
  console.log("Step 2: Scanning documentation...");
  const stats = await getDocsStats(docsPath);
  console.log(`  Total docs: ${stats.totalFiles}`);
  console.log("");
  console.log("Docs by category:");
  for (const [category, count] of Object.entries(stats.byCategory).sort((a, b) => b[1] - a[1])) {
    console.log(`  ${category}: ${count}`);
  }
  console.log("");
  console.log("Docs by type:");
  for (const [type, count] of Object.entries(stats.byType).sort((a, b) => b[1] - a[1])) {
    console.log(`  ${type}: ${count}`);
  }
  console.log("");

  // Step 3: Parse documentation files
  console.log("Step 3: Parsing documentation files...");
  const startParse = Date.now();

  const { chunks, errors } = await parseDocsDirectory(docsPath, (current, total, file) => {
    if (current % 10 === 0 || current === total) {
      process.stdout.write(`\r  Parsed ${current}/${total} files`);
    }
  });
  console.log("");

  // Normalize paths: Use relative path as the file path for portability
  for (const chunk of chunks) {
    chunk.filePath = chunk.relativePath;
  }

  const parseTime = ((Date.now() - startParse) / 1000).toFixed(1);
  console.log(`  Parsed ${chunks.length} documentation files`);
  console.log(`  Parse time: ${parseTime}s`);

  if (errors.length > 0) {
    console.log(`  Errors: ${errors.length}`);
    const errorFile = path.join(dbPath, "docs_parse_errors.txt");
    fs.writeFileSync(errorFile, errors.join("\n"));
    console.log(`  Error log: ${errorFile}`);
  }
  console.log("");

  if (chunks.length === 0) {
    console.error("No documentation files found to embed. Check the repository path.");
    process.exit(1);
  }

  // Step 4: Check for incremental update
  let hasExistingTable = !forceFullIndex && (await tableExists(dbPath, TABLE_NAME));
  let chunksToEmbed: DocsChunk[] = chunks;
  let filesToDelete: string[] = [];
  let isIncremental = false;

  if (hasExistingTable) {
    console.log("Step 4: Checking for changes (incremental mode)...");
    const existingHashes = await getDocsFileHashes(dbPath);
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

  // Step 5: Embed chunks (only new/changed ones in incremental mode)
  const embedStepNum = hasExistingTable ? 5 : 4;
  let embeddedChunks: EmbeddedDocsChunk[] = [];

  if (chunksToEmbed.length > 0) {
    const modelName = getModelName(embeddingConfig, "text");
    console.log(
      `Step ${embedStepNum}: Embedding ${isIncremental ? "changed " : ""}docs with ${embeddingConfig.provider} (${modelName})...`
    );
    const startEmbed = Date.now();

    try {
      embeddedChunks = await embedDocsChunks(chunksToEmbed, embeddingConfig, (current, total) => {
        process.stdout.write(`\r  Embedded ${current}/${total} docs`);
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

  // Step 6: Store in LanceDB
  const storeStepNum = embedStepNum + 1;
  console.log(`Step ${storeStepNum}: Storing in LanceDB (${TABLE_NAME} table)...`);
  const startStore = Date.now();

  try {
    if (isIncremental) {
      // Delete old entries first, then add new ones
      if (filesToDelete.length > 0) {
        const deleted = await deleteDocsByFilePaths(dbPath, filesToDelete);
        console.log(`  Deleted ${deleted} entries from ${filesToDelete.length} files`);
      }
      if (embeddedChunks.length > 0) {
        await addDocsChunks(dbPath, embeddedChunks);
        console.log(`  Added ${embeddedChunks.length} new entries`);
      }
    } else {
      // Full index - create new table
      await createDocsTable(dbPath, embeddedChunks, TABLE_NAME);
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
  console.log("=== Documentation Ingestion Complete ===");
  console.log(`Provider: ${embeddingConfig.provider}`);
  console.log(`Mode: ${isIncremental ? "Incremental" : "Full"}`);
  if (isIncremental) {
    console.log(`Docs embedded: ${embeddedChunks.length}`);
    console.log(`Files deleted: ${filesToDelete.length}`);
  } else {
    console.log(`Total docs indexed: ${embeddedChunks.length}`);
  }
  console.log(`Total time: ${totalTime}s`);
  console.log(`Database location: ${dbPath}`);
}

main().catch((e) => {
  console.error("Fatal error:", e);
  process.exit(1);
});
