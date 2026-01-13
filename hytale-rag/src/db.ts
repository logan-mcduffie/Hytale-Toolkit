import * as lancedb from "@lancedb/lancedb";
import * as path from "path";
import type { EmbeddedChunk } from "./embedder.js";

const TABLE_NAME = "hytale_methods";

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

let dbInstance: lancedb.Connection | null = null;

export async function getDb(dbPath: string): Promise<lancedb.Connection> {
  if (!dbInstance) {
    dbInstance = await lancedb.connect(dbPath);
  }
  return dbInstance;
}

export async function createTable(
  dbPath: string,
  chunks: EmbeddedChunk[]
): Promise<void> {
  const db = await getDb(dbPath);

  // Prepare data for LanceDB
  const data = chunks.map((chunk) => ({
    id: chunk.id,
    className: chunk.className,
    packageName: chunk.packageName,
    methodName: chunk.methodName,
    methodSignature: chunk.methodSignature,
    content: chunk.content,
    filePath: chunk.filePath,
    lineStart: chunk.lineStart,
    lineEnd: chunk.lineEnd,
    imports: JSON.stringify(chunk.imports),
    fields: JSON.stringify(chunk.fields),
    textForEmbedding: chunk.textForEmbedding,
    vector: chunk.vector,
  }));

  // Drop existing table if it exists
  try {
    await db.dropTable(TABLE_NAME);
  } catch (e) {
    // Table doesn't exist, that's fine
  }

  // Create new table
  await db.createTable(TABLE_NAME, data);
  console.log(`Created table '${TABLE_NAME}' with ${data.length} rows`);
}

export async function appendToTable(
  dbPath: string,
  chunks: EmbeddedChunk[]
): Promise<void> {
  const db = await getDb(dbPath);

  const data = chunks.map((chunk) => ({
    id: chunk.id,
    className: chunk.className,
    packageName: chunk.packageName,
    methodName: chunk.methodName,
    methodSignature: chunk.methodSignature,
    content: chunk.content,
    filePath: chunk.filePath,
    lineStart: chunk.lineStart,
    lineEnd: chunk.lineEnd,
    imports: JSON.stringify(chunk.imports),
    fields: JSON.stringify(chunk.fields),
    textForEmbedding: chunk.textForEmbedding,
    vector: chunk.vector,
  }));

  const table = await db.openTable(TABLE_NAME);
  await table.add(data);
  console.log(`Appended ${data.length} rows to '${TABLE_NAME}'`);
}

export async function search(
  dbPath: string,
  queryVector: number[],
  limit: number = 10,
  filter?: string
): Promise<SearchResult[]> {
  const db = await getDb(dbPath);
  const table = await db.openTable(TABLE_NAME);

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
    score: row._distance != null ? 1 - row._distance : 1,
  }));
}

export async function getStats(dbPath: string): Promise<{
  totalMethods: number;
  uniqueClasses: number;
  uniquePackages: number;
}> {
  const db = await getDb(dbPath);
  const table = await db.openTable(TABLE_NAME);
  const count = await table.countRows();

  // Get unique counts - fetch all rows (no select to avoid LanceDB column name case issues)
  const allRows = await table.query().toArray();
  const uniqueClasses = new Set(allRows.map((r: any) => r.className)).size;
  const uniquePackages = new Set(allRows.map((r: any) => r.packageName)).size;

  return {
    totalMethods: count,
    uniqueClasses,
    uniquePackages,
  };
}
