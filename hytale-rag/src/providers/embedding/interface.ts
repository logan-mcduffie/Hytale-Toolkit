/**
 * Embedding Provider Interface
 *
 * Abstraction layer for embedding providers (Voyage AI, OpenAI, Cohere, Ollama, etc.)
 * This allows the RAG system to work with any embedding service.
 */

/**
 * Purpose of the embedding - affects which model is used.
 * - "code": Optimized for source code (Java, etc.)
 * - "text": Optimized for natural language (game data descriptions, etc.)
 */
export type EmbeddingPurpose = "code" | "text";

/**
 * Mode of embedding - some providers optimize differently for documents vs queries.
 * - "document": For indexing/storing content (bulk embedding)
 * - "query": For search queries (asymmetric retrieval)
 */
export type EmbeddingMode = "document" | "query";

/**
 * Options for embedding operations
 */
export interface EmbeddingOptions {
  /** Purpose affects model selection (code vs text) */
  purpose: EmbeddingPurpose;
  /** Mode affects embedding optimization (document vs query) */
  mode: EmbeddingMode;
}

/**
 * Result from a batch embedding operation
 */
export interface EmbeddingResult {
  /** Array of embedding vectors, one per input text */
  vectors: number[][];
  /** Model name that was used */
  model: string;
  /** Dimensions of each vector */
  dimensions: number;
  /** Token usage information (if available) */
  usage?: {
    totalTokens: number;
  };
}

/**
 * Progress callback for batch operations
 */
export type EmbeddingProgressCallback = (current: number, total: number) => void;

/**
 * Abstract interface for embedding providers.
 * Implementations should handle batching, rate limiting, and error recovery internally.
 */
export interface EmbeddingProvider {
  /** Provider name (e.g., "voyage", "openai", "ollama") */
  readonly name: string;

  /**
   * Embed multiple texts in batches.
   * The provider handles batching according to its own limits.
   *
   * @param texts - Array of texts to embed
   * @param options - Embedding options (purpose, mode)
   * @param onProgress - Optional progress callback
   * @returns Embedding result with vectors
   */
  embedBatch(
    texts: string[],
    options: EmbeddingOptions,
    onProgress?: EmbeddingProgressCallback
  ): Promise<EmbeddingResult>;

  /**
   * Embed a single query text.
   * Convenience method for search queries.
   *
   * @param text - Query text to embed
   * @param purpose - Purpose (code or text)
   * @returns Single embedding vector
   */
  embedQuery(text: string, purpose: EmbeddingPurpose): Promise<number[]>;

  /**
   * Get the vector dimensions for a given purpose.
   * Useful for validating stored vectors match the current provider.
   *
   * @param purpose - Purpose (code or text)
   * @returns Number of dimensions
   */
  getDimensions(purpose: EmbeddingPurpose): number;

  /**
   * Validate that the provider is properly configured.
   * Should check API keys, connectivity, etc.
   *
   * @returns true if the provider is ready to use
   */
  validate(): Promise<boolean>;
}

/**
 * Configuration for creating an embedding provider
 */
export interface EmbeddingProviderConfig {
  /** Provider type */
  type: "voyage" | "openai" | "cohere" | "ollama";
  /** API key (required for cloud providers) */
  apiKey?: string;
  /** Base URL for self-hosted providers (e.g., Ollama) */
  baseUrl?: string;
  /** Model overrides */
  models?: {
    /** Model for code embeddings */
    code?: string;
    /** Model for text/gamedata embeddings */
    text?: string;
  };
  /** Maximum texts per batch (provider default if not specified) */
  batchSize?: number;
  /** Delay between batches in milliseconds */
  rateLimitMs?: number;
}
