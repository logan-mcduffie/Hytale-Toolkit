/**
 * Embedder - CLI helper for embedding code and game data
 *
 * Provides standalone embedding functions for the ingest scripts.
 * Supports multiple embedding providers (Voyage AI, Ollama).
 */

import { createEmbeddingProvider } from "./providers/embedding/factory.js";
import type { EmbeddingProvider, EmbeddingProviderConfig } from "./providers/embedding/interface.js";
import type { MethodChunk } from "./parser.js";
import type { GameDataChunk, EmbeddedGameDataChunk } from "./types.js";
import type { ClientUIChunk } from "./client-ui-parser.js";

/**
 * Embedded chunk with vector
 */
export interface EmbeddedChunk extends MethodChunk {
  vector: number[];
}

/**
 * Embedded client UI chunk with vector
 */
export interface EmbeddedClientUIChunk extends ClientUIChunk {
  vector: number[];
}

/**
 * Embedding provider configuration for ingest
 */
export interface IngestEmbeddingConfig {
  provider: "voyage" | "ollama";
  apiKey?: string;  // Required for Voyage, not needed for Ollama
  baseUrl?: string; // Optional, for custom Ollama URL
  model?: string;   // Optional, for custom model
}

/**
 * Default model names by provider and purpose
 */
const DEFAULT_MODELS: Record<string, { code: string; text: string }> = {
  voyage: { code: "voyage-code-3", text: "voyage-4-large" },
  ollama: { code: "nomic-embed-text", text: "nomic-embed-text" },
};

/**
 * Get the model name that will be used for a given config and purpose
 */
export function getModelName(config: IngestEmbeddingConfig, purpose: "code" | "text" = "code"): string {
  if (config.model) {
    return config.model;
  }
  return DEFAULT_MODELS[config.provider]?.[purpose] || "unknown";
}

/**
 * Create an embedding provider from ingest config
 */
function createProviderFromConfig(config: IngestEmbeddingConfig): EmbeddingProvider {
  const providerConfig: EmbeddingProviderConfig = {
    type: config.provider,
    apiKey: config.apiKey,
    baseUrl: config.baseUrl,
    // Voyage: 32 to stay under 120K token limit per batch (voyage-4-large has large files)
    // Ollama: 10 since it's slower and runs locally
    batchSize: config.provider === "voyage" ? 32 : 10,
    rateLimitMs: config.provider === "voyage" ? 100 : 50,
  };

  if (config.model) {
    providerConfig.models = { code: config.model, text: config.model };
  }

  return createEmbeddingProvider(providerConfig);
}

/**
 * Build embedding text for a method chunk
 */
function buildMethodEmbeddingText(chunk: MethodChunk): string {
  const parts = [
    `// Package: ${chunk.packageName}`,
    `// Class: ${chunk.className}`,
    `// Method: ${chunk.methodName}`,
    "",
    chunk.methodSignature,
    "",
    chunk.content,
  ];
  return parts.join("\n");
}

/**
 * Embed code chunks using the specified provider
 */
export async function embedChunks(
  chunks: MethodChunk[],
  config: IngestEmbeddingConfig,
  onProgress?: (current: number, total: number) => void
): Promise<EmbeddedChunk[]> {
  const provider = createProviderFromConfig(config);

  // Build texts for embedding
  const texts = chunks.map(buildMethodEmbeddingText);

  // Embed all texts
  const result = await provider.embedBatch(
    texts,
    { purpose: "code", mode: "document" },
    onProgress
  );

  // Combine chunks with vectors
  return chunks.map((chunk, i) => ({
    ...chunk,
    vector: result.vectors[i],
  }));
}

/**
 * Build embedding text for a game data chunk
 */
function buildGameDataEmbeddingText(chunk: GameDataChunk): string {
  // Use the pre-built textForEmbedding field if available
  if (chunk.textForEmbedding) {
    return chunk.textForEmbedding;
  }

  // Fallback to building from fields
  const parts = [
    `Type: ${chunk.type}`,
    `ID: ${chunk.id}`,
    `Path: ${chunk.filePath}`,
  ];

  if (chunk.name) {
    parts.push(`Name: ${chunk.name}`);
  }

  if (chunk.tags && chunk.tags.length > 0) {
    parts.push(`Tags: ${chunk.tags.join(", ")}`);
  }

  if (chunk.relatedIds && chunk.relatedIds.length > 0) {
    parts.push(`Related: ${chunk.relatedIds.join(", ")}`);
  }

  parts.push("");
  parts.push("Data:");
  parts.push(chunk.rawJson);

  return parts.join("\n");
}

/**
 * Embed game data chunks using the specified provider
 */
export async function embedGameDataChunks(
  chunks: GameDataChunk[],
  config: IngestEmbeddingConfig,
  onProgress?: (current: number, total: number) => void
): Promise<EmbeddedGameDataChunk[]> {
  const provider = createProviderFromConfig(config);

  // Build texts for embedding
  const texts = chunks.map(buildGameDataEmbeddingText);

  // Embed all texts
  const result = await provider.embedBatch(
    texts,
    { purpose: "text", mode: "document" },
    onProgress
  );

  // Combine chunks with vectors
  return chunks.map((chunk, i) => ({
    ...chunk,
    vector: result.vectors[i],
  }));
}

/**
 * Embed client UI chunks using the specified provider
 */
export async function embedClientUIChunks(
  chunks: ClientUIChunk[],
  config: IngestEmbeddingConfig,
  onProgress?: (current: number, total: number) => void
): Promise<EmbeddedClientUIChunk[]> {
  const provider = createProviderFromConfig(config);

  // Use the pre-built textForEmbedding field
  const texts = chunks.map((chunk) => chunk.textForEmbedding);

  // Embed all texts (use "text" purpose since this is markup/config, not code)
  const result = await provider.embedBatch(
    texts,
    { purpose: "text", mode: "document" },
    onProgress
  );

  // Combine chunks with vectors
  return chunks.map((chunk, i) => ({
    ...chunk,
    vector: result.vectors[i],
  }));
}

/**
 * Embed a single query using the specified provider
 */
export async function embedQuery(
  query: string,
  config: IngestEmbeddingConfig
): Promise<number[]> {
  const provider = createProviderFromConfig(config);
  return provider.embedQuery(query, "code");
}

// ============ Legacy API (backwards compatibility) ============
// These functions maintain the old signature for any existing code

/**
 * @deprecated Use embedChunks with IngestEmbeddingConfig instead
 */
export async function embedChunksLegacy(
  chunks: MethodChunk[],
  apiKey: string,
  onProgress?: (current: number, total: number) => void
): Promise<EmbeddedChunk[]> {
  return embedChunks(chunks, { provider: "voyage", apiKey }, onProgress);
}

/**
 * @deprecated Use embedGameDataChunks with IngestEmbeddingConfig instead
 */
export async function embedGameDataChunksLegacy(
  chunks: GameDataChunk[],
  apiKey: string,
  onProgress?: (current: number, total: number) => void
): Promise<EmbeddedGameDataChunk[]> {
  return embedGameDataChunks(chunks, { provider: "voyage", apiKey }, onProgress);
}

/**
 * @deprecated Use embedClientUIChunks with IngestEmbeddingConfig instead
 */
export async function embedClientUIChunksLegacy(
  chunks: ClientUIChunk[],
  apiKey: string,
  onProgress?: (current: number, total: number) => void
): Promise<EmbeddedClientUIChunk[]> {
  return embedClientUIChunks(chunks, { provider: "voyage", apiKey }, onProgress);
}
