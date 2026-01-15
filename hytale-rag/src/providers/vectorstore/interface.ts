/**
 * Vector Store Interface
 *
 * Abstraction layer for vector databases (LanceDB, Pinecone, Chroma, Weaviate, etc.)
 * This allows the RAG system to work with any vector storage backend.
 */

/**
 * Options for vector search operations
 */
export interface VectorSearchOptions {
  /** Maximum number of results to return */
  limit: number;
  /** Filter criteria (key-value pairs) */
  filter?: Record<string, string | number | boolean>;
  /** Minimum similarity score (0-1) to include in results */
  minScore?: number;
}

/**
 * A single search result with data and relevance score
 */
export interface VectorSearchResult<T> {
  /** Unique identifier */
  id: string;
  /** The stored data (without vector) */
  data: T;
  /** Similarity score (0-1, higher is better) */
  score: number;
  /** Raw distance from the vector store (optional) */
  distance?: number;
}

/**
 * Table statistics
 */
export interface TableStats {
  /** Total number of rows */
  rowCount: number;
  /** Additional provider-specific metadata */
  metadata?: Record<string, unknown>;
}

/**
 * Progress callback for batch operations
 */
export type VectorStoreProgressCallback = (current: number, total: number) => void;

/**
 * Abstract interface for vector stores.
 * Implementations handle connection management, batching, and provider-specific details.
 */
export interface VectorStore {
  /** Store name (e.g., "lancedb", "pinecone", "chroma") */
  readonly name: string;

  /**
   * Connect to the vector store.
   * Must be called before any other operations.
   */
  connect(): Promise<void>;

  /**
   * Close the connection and release resources.
   */
  close(): Promise<void>;

  /**
   * Check if a table exists.
   *
   * @param tableName - Name of the table
   * @returns true if the table exists
   */
  tableExists(tableName: string): Promise<boolean>;

  /**
   * Drop a table if it exists.
   *
   * @param tableName - Name of the table to drop
   */
  dropTable(tableName: string): Promise<void>;

  /**
   * Insert records into a table.
   * Creates the table if it doesn't exist.
   *
   * @param tableName - Name of the table
   * @param records - Records to insert (must include 'vector' field)
   * @param onProgress - Optional progress callback
   */
  insert<T extends Record<string, unknown>>(
    tableName: string,
    records: Array<T & { vector: number[] }>,
    onProgress?: VectorStoreProgressCallback
  ): Promise<void>;

  /**
   * Append records to an existing table.
   *
   * @param tableName - Name of the table
   * @param records - Records to append (must include 'vector' field)
   * @param onProgress - Optional progress callback
   */
  append<T extends Record<string, unknown>>(
    tableName: string,
    records: Array<T & { vector: number[] }>,
    onProgress?: VectorStoreProgressCallback
  ): Promise<void>;

  /**
   * Perform vector similarity search.
   *
   * @param tableName - Name of the table to search
   * @param queryVector - Query vector
   * @param options - Search options (limit, filter, etc.)
   * @returns Array of search results sorted by relevance
   */
  search<T>(
    tableName: string,
    queryVector: number[],
    options: VectorSearchOptions
  ): Promise<VectorSearchResult<T>[]>;

  /**
   * Get table statistics.
   *
   * @param tableName - Name of the table
   * @returns Table statistics
   */
  getStats(tableName: string): Promise<TableStats>;

  /**
   * Query all rows from a table in batches.
   * Used for aggregations and statistics.
   *
   * @param tableName - Name of the table
   * @param batchSize - Number of rows per batch
   * @returns Async generator yielding batches of rows
   */
  queryAll<T>(
    tableName: string,
    batchSize?: number
  ): AsyncGenerator<T[], void, unknown>;

  /**
   * Check if the store is healthy and ready.
   *
   * @returns true if the store is operational
   */
  healthCheck(): Promise<boolean>;
}

/**
 * Configuration for creating a vector store
 */
export interface VectorStoreConfig {
  /** Store type */
  type: "lancedb" | "pinecone" | "chroma" | "weaviate";

  // LanceDB-specific
  /** Path to the database directory (LanceDB) */
  path?: string;

  // Cloud providers
  /** API key for cloud providers */
  apiKey?: string;
  /** Environment name (Pinecone) */
  environment?: string;
  /** Host URL (Chroma, Weaviate) */
  host?: string;

  // Common options
  /** Namespace or index name */
  namespace?: string;
}
