/**
 * Configuration Schema
 *
 * Zod schemas for validating configuration.
 */

import { z } from "zod";

/**
 * Server configuration schema
 */
export const serverConfigSchema = z.object({
  /** Server mode: which servers to start */
  mode: z.enum(["mcp", "rest", "openai", "all"]).default("mcp"),
  /** HTTP port for REST/OpenAI servers */
  port: z.number().int().min(1).max(65535).default(3000),
  /** Host to bind to */
  host: z.string().default("localhost"),
});

/**
 * Embedding provider configuration schema
 */
export const embeddingConfigSchema = z.object({
  /** Embedding provider type */
  provider: z.enum(["voyage", "openai", "cohere", "ollama"]).default("voyage"),
  /** API key (can also be set via environment variable) */
  apiKey: z.string().optional(),
  /** Base URL for self-hosted providers */
  baseUrl: z.string().url().optional(),
  /** Model overrides */
  models: z
    .object({
      /** Model for code embeddings */
      code: z.string().optional(),
      /** Model for text/gamedata embeddings */
      text: z.string().optional(),
    })
    .default({}),
  /** Maximum texts per batch */
  batchSize: z.number().int().min(1).max(1000).default(128),
  /** Rate limit delay between batches (ms) */
  rateLimitMs: z.number().int().min(0).default(100),
});

/**
 * Vector store configuration schema
 */
export const vectorStoreConfigSchema = z.object({
  /** Vector store provider type */
  provider: z.enum(["lancedb", "pinecone", "chroma", "weaviate"]).default("lancedb"),
  /** Path to database directory (LanceDB) */
  path: z.string().optional(),
  /** API key for cloud providers */
  apiKey: z.string().optional(),
  /** Host URL (Chroma, Weaviate) */
  host: z.string().optional(),
  /** Environment (Pinecone) */
  environment: z.string().optional(),
  /** Namespace/index name */
  namespace: z.string().optional(),
});

/**
 * Table names configuration schema
 */
export const tablesConfigSchema = z.object({
  /** Table name for code/methods */
  code: z.string().default("hytale_methods"),
  /** Table name for game data */
  gamedata: z.string().default("hytale_gamedata"),
});

/**
 * API rate limiting configuration schema
 */
export const rateLimitConfigSchema = z.object({
  /** Time window in milliseconds */
  windowMs: z.number().int().min(0).default(60000),
  /** Maximum requests per window */
  max: z.number().int().min(1).default(100),
});

/**
 * API authentication configuration schema
 */
export const authConfigSchema = z.object({
  /** Enable API key authentication */
  enabled: z.boolean().default(false),
  /** Allowed API keys */
  apiKeys: z.array(z.string()).default([]),
});

/**
 * API configuration schema
 */
export const apiConfigSchema = z.object({
  /** Rate limiting settings */
  rateLimit: rateLimitConfigSchema.default({}),
  /** Authentication settings */
  auth: authConfigSchema.default({}),
});

/**
 * Complete application configuration schema
 */
export const configSchema = z.object({
  /** Server configuration */
  server: serverConfigSchema.default({}),
  /** Embedding provider configuration */
  embedding: embeddingConfigSchema.default({}),
  /** Vector store configuration */
  vectorStore: vectorStoreConfigSchema.default({}),
  /** Table names */
  tables: tablesConfigSchema.default({}),
  /** API settings */
  api: apiConfigSchema.default({}),
});

/**
 * Inferred TypeScript types from schemas
 */
export type ServerConfig = z.infer<typeof serverConfigSchema>;
export type EmbeddingConfig = z.infer<typeof embeddingConfigSchema>;
export type VectorStoreConfig = z.infer<typeof vectorStoreConfigSchema>;
export type TablesConfig = z.infer<typeof tablesConfigSchema>;
export type RateLimitConfig = z.infer<typeof rateLimitConfigSchema>;
export type AuthConfig = z.infer<typeof authConfigSchema>;
export type ApiConfig = z.infer<typeof apiConfigSchema>;
export type AppConfig = z.infer<typeof configSchema>;
