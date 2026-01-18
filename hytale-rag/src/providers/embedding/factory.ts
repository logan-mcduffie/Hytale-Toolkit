/**
 * Embedding Provider Factory
 *
 * Creates embedding provider instances based on configuration.
 */

import { EmbeddingProvider, EmbeddingProviderConfig } from "./interface.js";
import { VoyageEmbeddingProvider } from "./voyage.js";
import { OllamaEmbeddingProvider } from "./ollama.js";

/**
 * Create an embedding provider based on configuration.
 *
 * @param config - Provider configuration
 * @returns Configured embedding provider instance
 * @throws Error if provider type is not supported
 */
export function createEmbeddingProvider(config: EmbeddingProviderConfig): EmbeddingProvider {
  switch (config.type) {
    case "voyage":
      return new VoyageEmbeddingProvider(config);

    case "ollama":
      return new OllamaEmbeddingProvider(config);

    case "openai":
      // TODO: Implement OpenAI provider
      throw new Error(
        "OpenAI embedding provider not yet implemented. " +
          "Contributions welcome! See providers/embedding/openai.ts"
      );

    case "cohere":
      // TODO: Implement Cohere provider
      throw new Error(
        "Cohere embedding provider not yet implemented. " +
          "Contributions welcome! See providers/embedding/cohere.ts"
      );

    default:
      throw new Error(`Unknown embedding provider: ${config.type}`);
  }
}

/**
 * Get the list of supported embedding providers
 */
export function getSupportedEmbeddingProviders(): string[] {
  return ["voyage", "ollama"];
}

/**
 * Check if an embedding provider is supported
 */
export function isEmbeddingProviderSupported(provider: string): boolean {
  return getSupportedEmbeddingProviders().includes(provider);
}

// Re-export interfaces
export * from "./interface.js";
