#!/usr/bin/env node
/**
 * Hytale RAG - Main Entry Point
 *
 * A semantic search system for the Hytale codebase and game data.
 * Supports multiple server modes: MCP (Claude), REST API, and OpenAI-compatible.
 */

import { loadConfig } from "./config/index.js";
import { createEmbeddingProvider } from "./providers/embedding/factory.js";
import { createVectorStore } from "./providers/vectorstore/factory.js";
import { ToolRegistry, type ToolContext } from "./core/tools/index.js";
import { searchCodeTool } from "./core/tools/search-code.js";
import { searchGameDataTool } from "./core/tools/search-gamedata.js";
import { codeStatsTool } from "./core/tools/code-stats.js";
import { gameDataStatsTool } from "./core/tools/gamedata-stats.js";
import { startMCPServer } from "./servers/mcp/index.js";
import { createRESTServer, startRESTServer } from "./servers/rest/index.js";
import { createOpenAIServer, startOpenAIServer } from "./servers/openai/index.js";

/**
 * Main entry point
 */
async function main() {
  // Load configuration
  const config = loadConfig();

  // Validate embedding API key
  if (!config.embedding.apiKey) {
    const envVar =
      config.embedding.provider === "voyage"
        ? "VOYAGE_API_KEY"
        : config.embedding.provider === "openai"
          ? "OPENAI_API_KEY"
          : `${config.embedding.provider.toUpperCase()}_API_KEY`;

    console.error(`Error: ${envVar} environment variable is required.`);
    console.error(`Set it with: export ${envVar}=your-api-key`);
    process.exit(1);
  }

  // Initialize embedding provider
  const embedding = createEmbeddingProvider({
    type: config.embedding.provider,
    apiKey: config.embedding.apiKey,
    baseUrl: config.embedding.baseUrl,
    models: config.embedding.models,
    batchSize: config.embedding.batchSize,
    rateLimitMs: config.embedding.rateLimitMs,
  });

  // Initialize vector store
  const vectorStore = createVectorStore({
    type: config.vectorStore.provider,
    path: config.vectorStore.path,
    apiKey: config.vectorStore.apiKey,
    host: config.vectorStore.host,
    environment: config.vectorStore.environment,
    namespace: config.vectorStore.namespace,
  });

  // Connect to vector store
  await vectorStore.connect();

  // Create tool registry and register tools
  const registry = new ToolRegistry();
  registry.register(searchCodeTool);
  registry.register(searchGameDataTool);
  registry.register(codeStatsTool);
  registry.register(gameDataStatsTool);

  // Create tool context
  const context: ToolContext = {
    embedding,
    vectorStore,
    config,
  };

  const { mode, host, port } = config.server;

  // Start servers based on mode
  if (mode === "mcp") {
    // MCP-only mode (for Claude)
    await startMCPServer(registry, context);
  } else if (mode === "rest") {
    // REST API only
    const restApp = createRESTServer(registry, context, config);
    startRESTServer(restApp, config);
  } else if (mode === "openai") {
    // OpenAI-compatible only
    const openaiApp = createOpenAIServer(registry, context, config);
    startOpenAIServer(openaiApp, host, port);
  } else if (mode === "all") {
    // Start all servers
    // Note: MCP uses stdio, so it runs in the background
    // REST and OpenAI use HTTP on different ports

    console.log("Starting Hytale RAG in multi-server mode...\n");

    // REST API on configured port
    const restApp = createRESTServer(registry, context, config);
    startRESTServer(restApp, config);

    // OpenAI-compatible on port + 1
    const openaiApp = createOpenAIServer(registry, context, config);
    startOpenAIServer(openaiApp, host, port + 1);

    console.log("\nTo use with Claude, run: npx tsx src/index.ts");
    console.log("(MCP mode is the default when no HTTP servers are needed)\n");
  }
}

// Run main
main().catch((error) => {
  console.error("Fatal error:", error);
  process.exit(1);
});
