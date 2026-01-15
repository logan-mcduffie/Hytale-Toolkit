/**
 * Core Types
 *
 * Shared domain types for the Hytale RAG system.
 * These types are protocol-agnostic and used across all layers.
 */

// Re-export existing types for backwards compatibility
export {
  GameDataType,
  GameDataChunk,
  EmbeddedGameDataChunk,
  GameDataSearchResult,
  GameDataStats,
} from "../types.js";

export { MethodChunk, ParseResult } from "../parser.js";

/**
 * A method chunk with its embedding vector
 */
export interface EmbeddedMethodChunk {
  id: string;
  className: string;
  packageName: string;
  methodName: string;
  methodSignature: string;
  content: string;
  filePath: string;
  lineStart: number;
  lineEnd: number;
  imports: string[];
  fields: string[];
  classJavadoc?: string;
  methodJavadoc?: string;
  textForEmbedding: string;
  vector: number[];
}

/**
 * Code search result
 */
export interface CodeSearchResult {
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
 * Code database statistics
 */
export interface CodeStats {
  totalMethods: number;
  uniqueClasses: number;
  uniquePackages: number;
}

/**
 * Generic tool result wrapper
 */
export interface ToolResult<T> {
  /** Whether the operation succeeded */
  success: boolean;
  /** Result data (if successful) */
  data?: T;
  /** Error message (if failed) */
  error?: string;
  /** Execution metadata */
  metadata?: {
    /** Execution time in milliseconds */
    executionTimeMs: number;
    /** Tokens used for embedding (if applicable) */
    tokensUsed?: number;
  };
}

/**
 * Search input parameters (shared between code and gamedata search)
 */
export interface SearchInput {
  query: string;
  limit?: number;
}

/**
 * Code search input parameters
 */
export interface CodeSearchInput extends SearchInput {
  classFilter?: string;
}

/**
 * Game data search input parameters
 */
export interface GameDataSearchInput extends SearchInput {
  type?: string;
}
