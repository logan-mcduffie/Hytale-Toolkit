/**
 * Vector Store Factory
 *
 * Creates vector store instances based on configuration.
 */

import { VectorStore, VectorStoreConfig } from "./interface.js";
import { LanceDBVectorStore } from "./lancedb.js";

/**
 * Create a vector store based on configuration.
 *
 * @param config - Store configuration
 * @returns Configured vector store instance
 * @throws Error if store type is not supported
 */
export function createVectorStore(config: VectorStoreConfig): VectorStore {
  switch (config.type) {
    case "lancedb":
      return new LanceDBVectorStore(config);

    case "pinecone":
      // TODO: Implement Pinecone store
      throw new Error(
        "Pinecone vector store not yet implemented. " +
          "Contributions welcome! See providers/vectorstore/pinecone.ts"
      );

    case "chroma":
      // TODO: Implement Chroma store
      throw new Error(
        "Chroma vector store not yet implemented. " +
          "Contributions welcome! See providers/vectorstore/chroma.ts"
      );

    case "weaviate":
      // TODO: Implement Weaviate store
      throw new Error(
        "Weaviate vector store not yet implemented. " +
          "Contributions welcome! See providers/vectorstore/weaviate.ts"
      );

    default:
      throw new Error(`Unknown vector store: ${config.type}`);
  }
}

/**
 * Get the list of supported vector stores
 */
export function getSupportedVectorStores(): string[] {
  return ["lancedb"]; // Add more as they're implemented
}

/**
 * Check if a vector store is supported
 */
export function isVectorStoreSupported(store: string): boolean {
  return getSupportedVectorStores().includes(store);
}

// Re-export interfaces
export * from "./interface.js";
