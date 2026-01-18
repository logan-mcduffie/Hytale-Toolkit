/**
 * Ollama Embedding Provider
 *
 * Implementation of EmbeddingProvider for local Ollama.
 * Uses nomic-embed-text model (768 dimensions) by default.
 * No API key required - runs completely locally.
 */

import {
  EmbeddingProvider,
  EmbeddingProviderConfig,
  EmbeddingOptions,
  EmbeddingResult,
  EmbeddingPurpose,
  EmbeddingProgressCallback,
} from "./interface.js";

/** Default Ollama API URL */
const DEFAULT_OLLAMA_URL = "http://localhost:11434";

/** Default model - nomic-embed-text is good quality and ~274MB */
const DEFAULT_MODEL = "nomic-embed-text";

/** Vector dimensions by model */
const MODEL_DIMENSIONS: Record<string, number> = {
  "nomic-embed-text": 768,
  "mxbai-embed-large": 1024,
  "all-minilm": 384,
};

/** Default batch size for parallel requests */
const DEFAULT_BATCH_SIZE = 10;

/** Default delay between batches */
const DEFAULT_RATE_LIMIT_MS = 50;

/** Max characters before truncation for code (tokenizes less densely) */
const MAX_CHARS_CODE = 4000;

/** Max characters before truncation for text/markup (XAML tokenizes very densely) */
const MAX_CHARS_TEXT = 2500;

/**
 * Ollama embedding response format
 */
interface OllamaEmbeddingResponse {
  embedding: number[];
}

/**
 * Truncate text to limit
 */
function truncateToLimit(text: string, maxChars: number): string {
  if (text.length <= maxChars) return text;
  return text.substring(0, maxChars) + "\n// ... truncated";
}

/**
 * Ollama Embedding Provider
 */
export class OllamaEmbeddingProvider implements EmbeddingProvider {
  readonly name = "ollama";

  private baseUrl: string;
  private model: string;
  private batchSize: number;
  private rateLimitMs: number;

  constructor(config: EmbeddingProviderConfig) {
    this.baseUrl = config.baseUrl || DEFAULT_OLLAMA_URL;
    this.model = config.models?.code || config.models?.text || DEFAULT_MODEL;
    this.batchSize = config.batchSize || DEFAULT_BATCH_SIZE;
    this.rateLimitMs = config.rateLimitMs || DEFAULT_RATE_LIMIT_MS;
  }

  /**
   * Embed a single text using Ollama
   */
  private async embedSingle(text: string): Promise<number[]> {
    const response = await fetch(`${this.baseUrl}/api/embeddings`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: this.model,
        prompt: text,
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Ollama API error: ${response.status} - ${errorText}`);
    }

    const data = (await response.json()) as OllamaEmbeddingResponse;
    return data.embedding;
  }

  /**
   * Embed multiple texts in batches
   * Ollama doesn't support batch embedding natively, so we parallelize requests
   */
  async embedBatch(
    texts: string[],
    options: EmbeddingOptions,
    onProgress?: EmbeddingProgressCallback
  ): Promise<EmbeddingResult> {
    const allVectors: number[][] = [];

    // Use different limits: code tokenizes less densely than XAML/markup
    const maxChars = options.purpose === "code" ? MAX_CHARS_CODE : MAX_CHARS_TEXT;
    const truncatedTexts = texts.map((t) => truncateToLimit(t, maxChars));

    // Process in batches (parallel within each batch)
    for (let i = 0; i < truncatedTexts.length; i += this.batchSize) {
      const batch = truncatedTexts.slice(i, i + this.batchSize);

      // Embed batch in parallel
      const batchVectors = await Promise.all(
        batch.map((text) => this.embedSingle(text))
      );

      allVectors.push(...batchVectors);

      // Report progress
      if (onProgress) {
        onProgress(Math.min(i + this.batchSize, truncatedTexts.length), truncatedTexts.length);
      }

      // Rate limiting between batches
      if (i + this.batchSize < truncatedTexts.length) {
        await new Promise((resolve) => setTimeout(resolve, this.rateLimitMs));
      }
    }

    return {
      vectors: allVectors,
      model: this.model,
      dimensions: this.getDimensions(options.purpose),
    };
  }

  /**
   * Embed a single query
   */
  async embedQuery(text: string, purpose: EmbeddingPurpose): Promise<number[]> {
    const maxChars = purpose === "code" ? MAX_CHARS_CODE : MAX_CHARS_TEXT;
    const truncated = truncateToLimit(text, maxChars);
    return this.embedSingle(truncated);
  }

  /**
   * Get vector dimensions for a purpose
   * Ollama uses the same model for both code and text
   */
  getDimensions(_purpose: EmbeddingPurpose): number {
    return MODEL_DIMENSIONS[this.model] || 768;
  }

  /**
   * Validate the provider is configured correctly
   * Checks if Ollama is running and the model is available
   */
  async validate(): Promise<boolean> {
    try {
      // Check if Ollama is running
      const response = await fetch(`${this.baseUrl}/api/tags`);
      if (!response.ok) {
        return false;
      }

      // Check if the model is available
      const data = (await response.json()) as { models?: Array<{ name: string }> };
      const models = data.models || [];
      const hasModel = models.some(
        (m) => m.name === this.model || m.name.startsWith(`${this.model}:`)
      );

      return hasModel;
    } catch {
      return false;
    }
  }

  /**
   * Check if Ollama is running (static helper for setup scripts)
   */
  static async isOllamaRunning(baseUrl: string = DEFAULT_OLLAMA_URL): Promise<boolean> {
    try {
      const response = await fetch(`${baseUrl}/api/tags`, {
        signal: AbortSignal.timeout(5000),
      });
      return response.ok;
    } catch {
      return false;
    }
  }

  /**
   * Check if a model is available (static helper for setup scripts)
   */
  static async isModelAvailable(
    model: string = DEFAULT_MODEL,
    baseUrl: string = DEFAULT_OLLAMA_URL
  ): Promise<boolean> {
    try {
      const response = await fetch(`${baseUrl}/api/tags`);
      if (!response.ok) return false;

      const data = (await response.json()) as { models?: Array<{ name: string }> };
      const models = data.models || [];
      return models.some(
        (m) => m.name === model || m.name.startsWith(`${model}:`)
      );
    } catch {
      return false;
    }
  }
}
