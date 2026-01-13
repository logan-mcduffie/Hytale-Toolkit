#!/usr/bin/env node
import { parseDirectory, type MethodChunk } from "./parser.js";
import { embedChunks, type EmbeddedChunk } from "./embedder.js";
import { createTable } from "./db.js";
import * as path from "path";
import * as fs from "fs";

const DEFAULT_SOURCE_DIR = "C:/Users/logan/Documents/HytaleMods/decompiled/com/hypixel";
const DEFAULT_DB_PATH = "C:/Users/logan/Documents/HytaleMods/hytale-rag/data/lancedb";

async function main() {
  const sourceDir = process.argv[2] || DEFAULT_SOURCE_DIR;
  const dbPath = process.argv[3] || DEFAULT_DB_PATH;

  const apiKey = process.env.VOYAGE_API_KEY;
  if (!apiKey) {
    console.error("Error: VOYAGE_API_KEY environment variable is required");
    process.exit(1);
  }

  console.log(`Source directory: ${sourceDir}`);
  console.log(`Database path: ${dbPath}`);
  console.log("");

  // Ensure db directory exists
  fs.mkdirSync(dbPath, { recursive: true });

  // Step 1: Parse Java files
  console.log("Step 1: Parsing Java files...");
  const startParse = Date.now();

  const { chunks, errors } = await parseDirectory(sourceDir, (current, total, file) => {
    if (current % 100 === 0 || current === total) {
      process.stdout.write(`\r  Parsed ${current}/${total} files`);
    }
  });
  console.log("");

  const parseTime = ((Date.now() - startParse) / 1000).toFixed(1);
  console.log(`  Parsed ${chunks.length} methods from ${sourceDir}`);
  console.log(`  Parse time: ${parseTime}s`);

  if (errors.length > 0) {
    console.log(`  Errors: ${errors.length}`);
    // Write errors to file for debugging
    const errorFile = path.join(dbPath, "parse_errors.txt");
    fs.writeFileSync(errorFile, errors.join("\n"));
    console.log(`  Error log: ${errorFile}`);
  }
  console.log("");

  if (chunks.length === 0) {
    console.error("No methods found to embed. Check the source directory.");
    process.exit(1);
  }

  // Step 2: Embed chunks
  console.log("Step 2: Embedding methods with Voyage AI...");
  const startEmbed = Date.now();

  let embeddedChunks: EmbeddedChunk[];
  try {
    embeddedChunks = await embedChunks(chunks, apiKey, (current, total) => {
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

  // Step 3: Store in LanceDB
  console.log("Step 3: Storing in LanceDB...");
  const startStore = Date.now();

  try {
    await createTable(dbPath, embeddedChunks);
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
  console.log(`Total methods indexed: ${embeddedChunks.length}`);
  console.log(`Total time: ${totalTime}s`);
  console.log(`Database location: ${dbPath}`);
}

main().catch((e) => {
  console.error("Fatal error:", e);
  process.exit(1);
});
