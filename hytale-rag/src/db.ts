import * as lancedb from "@lancedb/lancedb";
import * as path from "path";
import type { EmbeddedChunk } from "./embedder.js";
import type { EmbeddedGameDataChunk, GameDataType, GameDataSearchResult, GameDataStats } from "./types.js";

const TABLE_NAME = "hytale_methods";
const GAMEDATA_TABLE = "hytale_gamedata";

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

  // For unique counts, we need to query the data
  // Use a streaming approach to avoid loading all 37K+ rows at once
  const classNames = new Set<string>();
  const packageNames = new Set<string>();

  // Process in batches using limit/offset pattern
  const batchSize = 5000;
  let offset = 0;

  while (true) {
    const batch = await table.query()
      .limit(batchSize)
      .offset(offset)
      .toArray();

    if (batch.length === 0) break;

    for (const row of batch) {
      classNames.add((row as any).className);
      packageNames.add((row as any).packageName);
    }

    offset += batch.length;
    if (batch.length < batchSize) break;
  }

  return {
    totalMethods: count,
    uniqueClasses: classNames.size,
    uniquePackages: packageNames.size,
  };
}

// =====================================================
// Game Data Table Operations
// =====================================================

export async function createGameDataTable(
  dbPath: string,
  chunks: EmbeddedGameDataChunk[]
): Promise<void> {
  const db = await getDb(dbPath);

  // Prepare data for LanceDB
  const data = chunks.map((chunk) => ({
    id: chunk.id,
    type: chunk.type,
    name: chunk.name,
    filePath: chunk.filePath,
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
    await db.dropTable(GAMEDATA_TABLE);
  } catch (e) {
    // Table doesn't exist, that's fine
  }

  // Create new table
  await db.createTable(GAMEDATA_TABLE, data);
  console.log(`Created table '${GAMEDATA_TABLE}' with ${data.length} rows`);
}

export async function appendToGameDataTable(
  dbPath: string,
  chunks: EmbeddedGameDataChunk[]
): Promise<void> {
  const db = await getDb(dbPath);

  const data = chunks.map((chunk) => ({
    id: chunk.id,
    type: chunk.type,
    name: chunk.name,
    filePath: chunk.filePath,
    rawJson: chunk.rawJson,
    category: chunk.category || "",
    tags: JSON.stringify(chunk.tags || []),
    parentId: chunk.parentId || "",
    relatedIds: JSON.stringify(chunk.relatedIds || []),
    textForEmbedding: chunk.textForEmbedding,
    vector: chunk.vector,
  }));

  const table = await db.openTable(GAMEDATA_TABLE);
  await table.add(data);
  console.log(`Appended ${data.length} rows to '${GAMEDATA_TABLE}'`);
}

export async function searchGameData(
  dbPath: string,
  queryVector: number[],
  limit: number = 10,
  typeFilter?: GameDataType
): Promise<GameDataSearchResult[]> {
  const db = await getDb(dbPath);
  const table = await db.openTable(GAMEDATA_TABLE);

  let query = table.vectorSearch(queryVector).limit(limit);

  if (typeFilter) {
    query = query.where(`type = '${typeFilter}'`);
  }

  const results = await query.toArray();

  return results.map((row: any) => ({
    id: row.id,
    type: row.type as GameDataType,
    name: row.name,
    filePath: row.filePath,
    rawJson: row.rawJson,
    category: row.category || undefined,
    tags: JSON.parse(row.tags || "[]"),
    parentId: row.parentId || undefined,
    score: row._distance != null ? 1 - row._distance : 1,
  }));
}

export async function getGameDataStats(dbPath: string): Promise<GameDataStats> {
  const db = await getDb(dbPath);
  const table = await db.openTable(GAMEDATA_TABLE);
  const count = await table.countRows();

  // Count by type using batched queries
  const byType: Record<GameDataType, number> = {
    item: 0,
    recipe: 0,
    block: 0,
    interaction: 0,
    drop: 0,
    npc: 0,
    npc_group: 0,
    npc_ai: 0,
    entity: 0,
    projectile: 0,
    farming: 0,
    shop: 0,
    environment: 0,
    weather: 0,
    biome: 0,
    worldgen: 0,
    camera: 0,
    objective: 0,
    gameplay: 0,
    localization: 0,
  };

  // Process in batches
  const batchSize = 5000;
  let offset = 0;

  while (true) {
    const batch = await table.query()
      .limit(batchSize)
      .offset(offset)
      .toArray();

    if (batch.length === 0) break;

    for (const row of batch) {
      const type = (row as any).type as GameDataType;
      if (type in byType) {
        byType[type]++;
      }
    }

    offset += batch.length;
    if (batch.length < batchSize) break;
  }

  return {
    totalItems: count,
    byType,
  };
}
