/**
 * LanceDB Vector Store
 *
 * Implementation of VectorStore for LanceDB.
 * LanceDB is a local, file-based vector database optimized for developer workflows.
 */

import * as lancedb from "@lancedb/lancedb";
import {
  VectorStore,
  VectorStoreConfig,
  VectorSearchOptions,
  VectorSearchResult,
  TableStats,
  VectorStoreProgressCallback,
} from "./interface.js";

/** Default batch size for queries */
const DEFAULT_BATCH_SIZE = 5000;

/**
 * LanceDB Vector Store Implementation
 */
export class LanceDBVectorStore implements VectorStore {
  readonly name = "lancedb";

  private dbPath: string;
  private connection: lancedb.Connection | null = null;

  constructor(config: VectorStoreConfig) {
    if (!config.path) {
      throw new Error("LanceDB path is required. Set vectorStore.path in config.");
    }
    this.dbPath = config.path;
  }

  /**
   * Connect to the LanceDB database
   */
  async connect(): Promise<void> {
    if (!this.connection) {
      this.connection = await lancedb.connect(this.dbPath);
    }
  }

  /**
   * Close the connection
   */
  async close(): Promise<void> {
    // LanceDB doesn't require explicit close, but we clear the reference
    this.connection = null;
  }

  /**
   * Get the connection, connecting if necessary
   */
  private async getConnection(): Promise<lancedb.Connection> {
    if (!this.connection) {
      await this.connect();
    }
    return this.connection!;
  }

  /**
   * Check if a table exists
   */
  async tableExists(tableName: string): Promise<boolean> {
    const db = await this.getConnection();
    try {
      await db.openTable(tableName);
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Drop a table if it exists
   */
  async dropTable(tableName: string): Promise<void> {
    const db = await this.getConnection();
    try {
      await db.dropTable(tableName);
    } catch {
      // Table doesn't exist, that's fine
    }
  }

  /**
   * Insert records into a table (creates table if needed)
   */
  async insert<T extends Record<string, unknown>>(
    tableName: string,
    records: Array<T & { vector: number[] }>,
    onProgress?: VectorStoreProgressCallback
  ): Promise<void> {
    const db = await this.getConnection();

    // Prepare data (convert arrays to JSON strings for LanceDB)
    const data = records.map((record) => this.prepareRecord(record));

    // Drop existing table if it exists
    await this.dropTable(tableName);

    // Create new table with data
    await db.createTable(tableName, data);

    if (onProgress) {
      onProgress(records.length, records.length);
    }
  }

  /**
   * Append records to an existing table
   */
  async append<T extends Record<string, unknown>>(
    tableName: string,
    records: Array<T & { vector: number[] }>,
    onProgress?: VectorStoreProgressCallback
  ): Promise<void> {
    const db = await this.getConnection();

    const data = records.map((record) => this.prepareRecord(record));

    const table = await db.openTable(tableName);
    await table.add(data);

    if (onProgress) {
      onProgress(records.length, records.length);
    }
  }

  /**
   * Perform vector similarity search
   */
  async search<T>(
    tableName: string,
    queryVector: number[],
    options: VectorSearchOptions
  ): Promise<VectorSearchResult<T>[]> {
    const db = await this.getConnection();
    const table = await db.openTable(tableName);

    // Use query().nearestTo() pattern for better filter support
    let query = table.query().nearestTo(queryVector);

    // Apply filter if provided - use prefilter (where) for accurate filtering
    if (options.filter) {
      const filterStr = this.buildFilterString(options.filter);
      if (filterStr) {
        // Use where() (prefilter) - filters before vector search for accurate results
        query = query.where(filterStr);
      }
    }

    query = query.limit(options.limit);

    const results = await query.toArray();

    const mapped: VectorSearchResult<T>[] = [];

    for (const row of results) {
      const rowData = row as Record<string, unknown>;
      // Convert distance to score (LanceDB uses L2 distance, lower is better)
      const distance = rowData._distance as number | undefined;
      const score = distance != null ? 1 - distance : 1;

      // Apply minimum score filter
      if (options.minScore !== undefined && score < options.minScore) {
        continue;
      }

      mapped.push({
        id: rowData.id as string,
        data: this.parseRecord<T>(rowData),
        score,
        distance,
      });
    }

    return mapped;
  }

  /**
   * Get table statistics
   */
  async getStats(tableName: string): Promise<TableStats> {
    const db = await this.getConnection();
    const table = await db.openTable(tableName);
    const rowCount = await table.countRows();

    return { rowCount };
  }

  /**
   * Query all rows in batches
   */
  async *queryAll<T>(
    tableName: string,
    batchSize: number = DEFAULT_BATCH_SIZE
  ): AsyncGenerator<T[], void, unknown> {
    const db = await this.getConnection();
    const table = await db.openTable(tableName);

    let offset = 0;

    while (true) {
      const batch = await table.query().limit(batchSize).offset(offset).toArray();

      if (batch.length === 0) break;

      yield batch.map((row) => this.parseRecord<T>(row as Record<string, unknown>));

      offset += batch.length;
      if (batch.length < batchSize) break;
    }
  }

  /**
   * Check if the store is healthy
   */
  async healthCheck(): Promise<boolean> {
    try {
      await this.getConnection();
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Prepare a record for storage (convert arrays to JSON strings)
   */
  private prepareRecord<T extends Record<string, unknown>>(
    record: T & { vector: number[] }
  ): Record<string, unknown> {
    const prepared: Record<string, unknown> = {};

    for (const [key, value] of Object.entries(record)) {
      if (key === "vector") {
        // Keep vector as-is
        prepared[key] = value;
      } else if (Array.isArray(value)) {
        // Convert arrays to JSON strings
        prepared[key] = JSON.stringify(value);
      } else if (value === undefined) {
        // Convert undefined to empty string
        prepared[key] = "";
      } else {
        prepared[key] = value;
      }
    }

    return prepared;
  }

  /**
   * Parse a record from storage (convert JSON strings back to arrays)
   */
  private parseRecord<T>(row: Record<string, unknown>): T {
    const parsed: Record<string, unknown> = {};

    for (const [key, value] of Object.entries(row)) {
      // Skip internal LanceDB fields
      if (key === "_distance" || key === "vector") continue;

      if (typeof value === "string") {
        // Try to parse as JSON array
        if (value.startsWith("[") && value.endsWith("]")) {
          try {
            parsed[key] = JSON.parse(value);
            continue;
          } catch {
            // Not valid JSON, keep as string
          }
        }
        // Convert empty strings back to undefined for optional fields
        parsed[key] = value === "" ? undefined : value;
      } else {
        parsed[key] = value;
      }
    }

    return parsed as T;
  }

  /**
   * Build a SQL WHERE filter string from key-value pairs
   * Column names are wrapped in backticks to preserve case sensitivity
   * (LanceDB/DataFusion normalizes unquoted identifiers to lowercase;
   * backticks are required for camelCase column names)
   */
  private buildFilterString(filter: Record<string, string | number | boolean>): string {
    const conditions = Object.entries(filter)
      .map(([key, value]) => {
        // Use backticks for case-sensitive column names (LanceDB-specific)
        const quotedKey = '`' + key + '`';
        if (typeof value === "string") {
          // Escape single quotes in strings
          const escaped = value.replace(/'/g, "''");
          return `${quotedKey} = '${escaped}'`;
        }
        return `${quotedKey} = ${value}`;
      });

    return conditions.join(" AND ");
  }
}
