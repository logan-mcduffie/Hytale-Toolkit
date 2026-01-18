/**
 * Database - CLI helper for LanceDB operations
 *
 * Provides standalone database functions for the ingest scripts.
 */

import * as lancedb from "@lancedb/lancedb";
import type { EmbeddedChunk, EmbeddedClientUIChunk } from "./embedder.js";
import type { EmbeddedGameDataChunk } from "./types.js";

/**
 * Search result from the database
 */
export interface SearchResult {
  id: string;
  className: string;
  packageName: string;
  methodName: string;
  methodSignature: string;
  content: string;
  filePath: string;
  lineStart: number;
  lineEnd: number;
  score: number;
}

/**
 * Database statistics
 */
export interface DbStats {
  totalMethods: number;
  uniqueClasses: number;
  uniquePackages: number;
}

/**
 * Create or replace the hytale_methods table with embedded chunks
 */
export async function createTable(
  dbPath: string,
  chunks: EmbeddedChunk[],
  tableName: string = "hytale_methods"
): Promise<void> {
  const db = await lancedb.connect(dbPath);

  // Prepare data for LanceDB (convert arrays to JSON strings)
  const data = chunks.map((chunk) => ({
    id: chunk.id,
    className: chunk.className,
    packageName: chunk.packageName,
    methodName: chunk.methodName,
    methodSignature: chunk.methodSignature,
    content: chunk.content,
    filePath: chunk.filePath,
    fileHash: chunk.fileHash,
    lineStart: chunk.lineStart,
    lineEnd: chunk.lineEnd,
    imports: JSON.stringify(chunk.imports),
    fields: JSON.stringify(chunk.fields),
    classJavadoc: chunk.classJavadoc || "",
    methodJavadoc: chunk.methodJavadoc || "",
    vector: chunk.vector,
  }));

  // Drop existing table if it exists
  try {
    await db.dropTable(tableName);
  } catch {
    // Table doesn't exist, that's fine
  }

  // Create new table
  await db.createTable(tableName, data);
  console.log(`  Created table '${tableName}' with ${data.length} rows`);
}

/**
 * Check if a table exists in the database
 */
export async function tableExists(
  dbPath: string,
  tableName: string = "hytale_methods"
): Promise<boolean> {
  try {
    const db = await lancedb.connect(dbPath);
    const tables = await db.tableNames();
    return tables.includes(tableName);
  } catch {
    return false;
  }
}

/**
 * Get existing file hashes from the table for incremental indexing
 * Returns a map of filePath -> fileHash
 */
export async function getExistingFileHashes(
  dbPath: string,
  tableName: string = "hytale_methods"
): Promise<Map<string, string>> {
  const fileHashes = new Map<string, string>();

  try {
    const db = await lancedb.connect(dbPath);
    const table = await db.openTable(tableName);

    // Query in batches to handle large tables
    const batchSize = 10000;
    let offset = 0;

    while (true) {
      const batch = await table
        .query()
        .select(["filePath", "fileHash"])
        .limit(batchSize)
        .offset(offset)
        .toArray();

      if (batch.length === 0) break;

      for (const row of batch as any[]) {
        // Only store one hash per file (all methods from same file have same hash)
        if (row.filePath && row.fileHash && !fileHashes.has(row.filePath)) {
          fileHashes.set(row.filePath, row.fileHash);
        }
      }

      offset += batch.length;
      if (batch.length < batchSize) break;
    }
  } catch {
    // Table doesn't exist or other error - return empty map
  }

  return fileHashes;
}

/**
 * Delete all rows for the given file paths
 */
export async function deleteByFilePaths(
  dbPath: string,
  filePaths: string[],
  tableName: string = "hytale_methods"
): Promise<number> {
  if (filePaths.length === 0) return 0;

  const db = await lancedb.connect(dbPath);
  const table = await db.openTable(tableName);

  const beforeCount = await table.countRows();

  // Delete in batches to avoid query size limits
  const batchSize = 100;
  for (let i = 0; i < filePaths.length; i += batchSize) {
    const batch = filePaths.slice(i, i + batchSize);
    // Escape single quotes in file paths and build filter
    const escapedPaths = batch.map((p) => `'${p.replace(/'/g, "''")}'`);
    const filter = `filePath IN (${escapedPaths.join(", ")})`;
    await table.delete(filter);
  }

  const afterCount = await table.countRows();
  return beforeCount - afterCount;
}

/**
 * Add new chunks to an existing table
 */
export async function addChunks(
  dbPath: string,
  chunks: EmbeddedChunk[],
  tableName: string = "hytale_methods"
): Promise<void> {
  if (chunks.length === 0) return;

  const db = await lancedb.connect(dbPath);
  const table = await db.openTable(tableName);

  // Prepare data for LanceDB
  const data = chunks.map((chunk) => ({
    id: chunk.id,
    className: chunk.className,
    packageName: chunk.packageName,
    methodName: chunk.methodName,
    methodSignature: chunk.methodSignature,
    content: chunk.content,
    filePath: chunk.filePath,
    fileHash: chunk.fileHash,
    lineStart: chunk.lineStart,
    lineEnd: chunk.lineEnd,
    imports: JSON.stringify(chunk.imports),
    fields: JSON.stringify(chunk.fields),
    classJavadoc: chunk.classJavadoc || "",
    methodJavadoc: chunk.methodJavadoc || "",
    vector: chunk.vector,
  }));

  await table.add(data);
}

/**
 * Create or replace the hytale_gamedata table with embedded chunks
 */
export async function createGameDataTable(
  dbPath: string,
  chunks: EmbeddedGameDataChunk[],
  tableName: string = "hytale_gamedata"
): Promise<void> {
  const db = await lancedb.connect(dbPath);

  // Prepare data for LanceDB
  const data = chunks.map((chunk) => ({
    id: chunk.id,
    type: chunk.type,
    name: chunk.name,
    filePath: chunk.filePath,
    fileHash: chunk.fileHash,
    rawJson: chunk.rawJson,
    category: chunk.category || "",
    tags: JSON.stringify(chunk.tags || []),
    parentId: chunk.parentId || "",
    relatedIds: JSON.stringify(chunk.relatedIds || []),
    textForEmbedding: chunk.textForEmbedding,
    vector: chunk.vector,
  }));

  // Drop existing table if it exists
  try {
    await db.dropTable(tableName);
  } catch {
    // Table doesn't exist, that's fine
  }

  // Create new table
  await db.createTable(tableName, data);
  console.log(`  Created table '${tableName}' with ${data.length} rows`);
}

/**
 * Get existing file hashes from game data table for incremental updates
 */
export async function getGameDataFileHashes(
  dbPath: string,
  tableName: string = "hytale_gamedata"
): Promise<Map<string, string>> {
  const fileHashes = new Map<string, string>();

  try {
    const db = await lancedb.connect(dbPath);
    const table = await db.openTable(tableName);

    // Query in batches to handle large tables
    const batchSize = 10000;
    let offset = 0;

    while (true) {
      const batch = await table
        .query()
        .select(["filePath", "fileHash"])
        .limit(batchSize)
        .offset(offset)
        .toArray();

      if (batch.length === 0) break;

      for (const row of batch) {
        if (row.filePath && row.fileHash) {
          fileHashes.set(row.filePath, row.fileHash);
        }
      }

      offset += batchSize;
    }
  } catch {
    // Table doesn't exist or is invalid - return empty map
  }

  return fileHashes;
}

/**
 * Delete game data entries by file paths (for incremental updates)
 */
export async function deleteGameDataByFilePaths(
  dbPath: string,
  filePaths: string[],
  tableName: string = "hytale_gamedata"
): Promise<number> {
  if (filePaths.length === 0) return 0;

  const db = await lancedb.connect(dbPath);
  const table = await db.openTable(tableName);

  const beforeCount = await table.countRows();

  // Delete in batches to avoid query size limits
  const batchSize = 100;
  for (let i = 0; i < filePaths.length; i += batchSize) {
    const batch = filePaths.slice(i, i + batchSize);
    const conditions = batch.map((fp) => `filePath = '${fp.replace(/'/g, "''")}'`);
    await table.delete(conditions.join(" OR "));
  }

  const afterCount = await table.countRows();
  return beforeCount - afterCount;
}

/**
 * Add game data chunks to existing table (for incremental updates)
 */
export async function addGameDataChunks(
  dbPath: string,
  chunks: EmbeddedGameDataChunk[],
  tableName: string = "hytale_gamedata"
): Promise<void> {
  if (chunks.length === 0) return;

  const db = await lancedb.connect(dbPath);
  const table = await db.openTable(tableName);

  // Prepare data for LanceDB
  const data = chunks.map((chunk) => ({
    id: chunk.id,
    type: chunk.type,
    name: chunk.name,
    filePath: chunk.filePath,
    fileHash: chunk.fileHash,
    rawJson: chunk.rawJson,
    category: chunk.category || "",
    tags: JSON.stringify(chunk.tags || []),
    parentId: chunk.parentId || "",
    relatedIds: JSON.stringify(chunk.relatedIds || []),
    textForEmbedding: chunk.textForEmbedding,
    vector: chunk.vector,
  }));

  await table.add(data);
}

/**
 * Search the code table using a query vector
 */
export async function search(
  dbPath: string,
  queryVector: number[],
  limit: number = 5,
  filter?: string,
  tableName: string = "hytale_methods"
): Promise<SearchResult[]> {
  const db = await lancedb.connect(dbPath);
  const table = await db.openTable(tableName);

  let query = table.vectorSearch(queryVector).limit(limit);

  if (filter) {
    query = query.where(filter);
  }

  const results = await query.toArray();

  return results.map((row: any) => ({
    id: row.id,
    className: row.className,
    packageName: row.packageName,
    methodName: row.methodName,
    methodSignature: row.methodSignature,
    content: row.content,
    filePath: row.filePath,
    lineStart: row.lineStart,
    lineEnd: row.lineEnd,
    score: 1 - (row._distance || 0), // Convert distance to similarity score
  }));
}

/**
 * Get database statistics
 */
export async function getStats(
  dbPath: string,
  tableName: string = "hytale_methods"
): Promise<DbStats> {
  const db = await lancedb.connect(dbPath);
  const table = await db.openTable(tableName);

  const rowCount = await table.countRows();

  // Query all rows to count unique classes and packages
  const classNames = new Set<string>();
  const packageNames = new Set<string>();

  const batchSize = 5000;
  let offset = 0;

  while (true) {
    const batch = await table.query().limit(batchSize).offset(offset).toArray();
    if (batch.length === 0) break;

    for (const row of batch as any[]) {
      if (row.className) classNames.add(row.className);
      if (row.packageName) packageNames.add(row.packageName);
    }

    offset += batch.length;
    if (batch.length < batchSize) break;
  }

  return {
    totalMethods: rowCount,
    uniqueClasses: classNames.size,
    uniquePackages: packageNames.size,
  };
}

/**
 * Create or replace the client UI table with embedded chunks
 */
export async function createClientUITable(
  dbPath: string,
  chunks: EmbeddedClientUIChunk[],
  tableName: string = "hytale_client_ui"
): Promise<void> {
  const db = await lancedb.connect(dbPath);

  // Prepare data for LanceDB
  const data = chunks.map((chunk) => ({
    id: chunk.id,
    type: chunk.type,
    name: chunk.name,
    filePath: chunk.filePath,
    relativePath: chunk.relativePath,
    fileHash: chunk.fileHash,
    content: chunk.content,
    category: chunk.category || "",
    textForEmbedding: chunk.textForEmbedding,
    vector: chunk.vector,
  }));

  // Drop existing table if it exists
  try {
    await db.dropTable(tableName);
  } catch {
    // Table doesn't exist, that's fine
  }

  // Create new table
  await db.createTable(tableName, data);
  console.log(`  Created table '${tableName}' with ${data.length} rows`);
}

/**
 * Get existing file hashes from client UI table for incremental updates
 */
export async function getClientUIFileHashes(
  dbPath: string,
  tableName: string = "hytale_client_ui"
): Promise<Map<string, string>> {
  const fileHashes = new Map<string, string>();

  try {
    const db = await lancedb.connect(dbPath);
    const table = await db.openTable(tableName);

    // Query in batches to handle large tables
    const batchSize = 10000;
    let offset = 0;

    while (true) {
      const batch = await table
        .query()
        .select(["filePath", "fileHash"])
        .limit(batchSize)
        .offset(offset)
        .toArray();

      if (batch.length === 0) break;

      for (const row of batch) {
        if (row.filePath && row.fileHash) {
          fileHashes.set(row.filePath, row.fileHash);
        }
      }

      offset += batchSize;
    }
  } catch {
    // Table doesn't exist or is invalid - return empty map
  }

  return fileHashes;
}

/**
 * Delete client UI entries by file paths (for incremental updates)
 */
export async function deleteClientUIByFilePaths(
  dbPath: string,
  filePaths: string[],
  tableName: string = "hytale_client_ui"
): Promise<number> {
  if (filePaths.length === 0) return 0;

  const db = await lancedb.connect(dbPath);
  const table = await db.openTable(tableName);

  const beforeCount = await table.countRows();

  // Delete in batches to avoid query size limits
  const batchSize = 100;
  for (let i = 0; i < filePaths.length; i += batchSize) {
    const batch = filePaths.slice(i, i + batchSize);
    const conditions = batch.map((fp) => `filePath = '${fp.replace(/'/g, "''")}'`);
    await table.delete(conditions.join(" OR "));
  }

  const afterCount = await table.countRows();
  return beforeCount - afterCount;
}

/**
 * Add client UI chunks to existing table (for incremental updates)
 */
export async function addClientUIChunks(
  dbPath: string,
  chunks: EmbeddedClientUIChunk[],
  tableName: string = "hytale_client_ui"
): Promise<void> {
  if (chunks.length === 0) return;

  const db = await lancedb.connect(dbPath);
  const table = await db.openTable(tableName);

  // Prepare data for LanceDB
  const data = chunks.map((chunk) => ({
    id: chunk.id,
    type: chunk.type,
    name: chunk.name,
    filePath: chunk.filePath,
    relativePath: chunk.relativePath,
    fileHash: chunk.fileHash,
    content: chunk.content,
    category: chunk.category || "",
    textForEmbedding: chunk.textForEmbedding,
    vector: chunk.vector,
  }));

  await table.add(data);
}
