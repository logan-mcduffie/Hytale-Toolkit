#!/usr/bin/env node
import "dotenv/config";
/**
 * Hytale RAG - Main Entry Point
 *
 * A semantic search system for the Hytale codebase and game data.
 * Supports multiple server modes: MCP (Claude), REST API, and OpenAI-compatible.
 */

import * as fs from "fs";
import * as path from "path";
import { fileURLToPath } from "url";
import { loadConfig } from "./config/index.js";
import { setupLogger, getLogger } from "./utils/logger.js";
import { createEmbeddingProvider } from "./providers/embedding/factory.js";
import { createVectorStore } from "./providers/vectorstore/factory.js";
import { ToolRegistry, type ToolContext } from "./core/tools/index.js";
import { searchCodeTool } from "./core/tools/search-code.js";
import { searchClientCodeTool } from "./core/tools/search-client-code.js";
import { searchGameDataTool } from "./core/tools/search-gamedata.js";
import { codeStatsTool } from "./core/tools/code-stats.js";
import { clientCodeStatsTool } from "./core/tools/client-code-stats.js";
import { gameDataStatsTool } from "./core/tools/gamedata-stats.js";
import { startMCPServer } from "./servers/mcp/index.js";
import { createRESTServer, startRESTServer } from "./servers/rest/index.js";
import { createOpenAIServer, startOpenAIServer } from "./servers/openai/index.js";
import { VersionChecker } from "./core/version-checker.js";

/**
 * Get current version from package.json
 */
function getPackageVersion(): string {
  const __dirname = path.dirname(fileURLToPath(import.meta.url));
  const packagePath = path.resolve(__dirname, "../package.json");
  try {
    const packageJson = JSON.parse(fs.readFileSync(packagePath, "utf-8"));
    return packageJson.version || "0.0.0";
  } catch {
    return "0.0.0";
  }
}

/**
 * Get database version from .version file in data directory
 * Falls back to package version if not found
 */
function getDatabaseVersion(dataPath: string): string {
  const versionFile = path.join(dataPath, ".version");
  try {
    const version = fs.readFileSync(versionFile, "utf-8").trim();
    return version || getPackageVersion();
  } catch {
    return getPackageVersion();
  }
}

/**
 * Check .env file for common misconfigurations
 * Returns a helpful error message if issues are found
 */
function checkEnvFile(): string | undefined {
  const envPath = path.resolve(process.cwd(), ".env");

  if (!fs.existsSync(envPath)) {
    return undefined; // No .env file - will be handled by main config check
  }

  try {
    const content = fs.readFileSync(envPath, "utf-8");
    const lines = content.split("\n").map((l) => l.trim()).filter((l) => l && !l.startsWith("#"));

    // Check for common issues
    for (const line of lines) {
      // Check if someone put just the API key without the variable name
      if (line.match(/^pa-[A-Za-z0-9_-]+$/)) {
        return `.env file misconfigured: Found what looks like a Voyage API key without the variable name.\n\nYour .env file contains:\n  ${line}\n\nIt should be:\n  VOYAGE_API_KEY=${line}\n\nPlease fix your .env file and restart Claude Code.`;
      }

      // Check if someone put just "sk-" style key (OpenAI format)
      if (line.match(/^sk-[A-Za-z0-9_-]+$/)) {
        return `.env file misconfigured: Found what looks like an OpenAI API key without the variable name.\n\nYour .env file contains:\n  ${line}\n\nIt should be:\n  OPENAI_API_KEY=${line}\n\nPlease fix your .env file and restart Claude Code.`;
      }
    }
  } catch {
    // Ignore read errors
  }

  return undefined;
}

/**
 * Validate API key format for the given provider
 * Returns an error message if the key appears invalid
 */
function validateApiKeyFormat(provider: string, apiKey: string): string | undefined {
  switch (provider) {
    case "voyage":
      if (!apiKey.startsWith("pa-")) {
        return `Invalid Voyage API key format. Voyage API keys should start with "pa-".\n\nYour key starts with: "${apiKey.substring(0, 3)}..."\n\nGet a valid API key at https://www.voyageai.com/ and update your .env file.`;
      }
      break;
    case "openai":
      if (!apiKey.startsWith("sk-")) {
        return `Invalid OpenAI API key format. OpenAI API keys should start with "sk-".\n\nYour key starts with: "${apiKey.substring(0, 3)}..."\n\nCheck your API key and update your .env file.`;
      }
      break;
  }
  return undefined;
}

/**
 * Expected LanceDB tables and their minimum sizes (in bytes)
 * These are rough minimums to detect obviously corrupted/incomplete extractions
 */
const EXPECTED_TABLES = [
  { name: "hytale_methods.lance", minSize: 50_000_000 },    // ~50MB minimum for code
  { name: "hytale_client_ui.lance", minSize: 1_000_000 },   // ~1MB minimum for UI
  { name: "hytale_gamedata.lance", minSize: 5_000_000 },    // ~5MB minimum for gamedata
];

/**
 * Get the total size of a directory recursively
 */
function getDirectorySize(dirPath: string): number {
  let totalSize = 0;
  try {
    const entries = fs.readdirSync(dirPath, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(dirPath, entry.name);
      if (entry.isDirectory()) {
        totalSize += getDirectorySize(fullPath);
      } else if (entry.isFile()) {
        totalSize += fs.statSync(fullPath).size;
      }
    }
  } catch {
    // Ignore errors
  }
  return totalSize;
}

/**
 * Validate LanceDB database files
 * Returns an error message if the database is missing or corrupted
 */
function validateDatabase(dbPath: string): string | undefined {
  const releaseUrl = "https://github.com/logan-mcduffie/Hytale-Toolkit/releases";

  // Check if database directory exists
  if (!fs.existsSync(dbPath)) {
    return `Database not found at: ${dbPath}\n\nThe LanceDB database is required for semantic search.\n\nTo fix this:\n1. Download lancedb-{provider}-all.tar.gz from ${releaseUrl}\n2. Extract it to the data/ folder\n\nExpected structure: data/{provider}/lancedb/hytale_methods.lance/`;
  }

  // Check each expected table
  const missingTables: string[] = [];
  const corruptedTables: string[] = [];

  for (const table of EXPECTED_TABLES) {
    const tablePath = path.join(dbPath, table.name);
    const versionsPath = path.join(tablePath, "_versions");

    if (!fs.existsSync(tablePath)) {
      missingTables.push(table.name);
      continue;
    }

    // Check for _versions folder with manifest files (indicates valid LanceDB table)
    let hasManifest = false;
    if (fs.existsSync(versionsPath)) {
      try {
        const files = fs.readdirSync(versionsPath);
        hasManifest = files.some(f => f.endsWith(".manifest"));
      } catch {
        // Ignore read errors
      }
    }
    if (!hasManifest) {
      corruptedTables.push(`${table.name} (missing manifest)`);
      continue;
    }

    // Check minimum size to detect incomplete extractions
    const tableSize = getDirectorySize(tablePath);
    if (tableSize < table.minSize) {
      const expectedMB = (table.minSize / 1_000_000).toFixed(0);
      const actualMB = (tableSize / 1_000_000).toFixed(1);
      corruptedTables.push(`${table.name} (size: ${actualMB}MB, expected: >${expectedMB}MB)`);
    }
  }

  if (missingTables.length > 0) {
    return `Database incomplete - missing tables:\n  ${missingTables.join("\n  ")}\n\nTo fix this:\n1. Delete the existing database folder\n2. Download lancedb-{provider}-all.tar.gz from ${releaseUrl}\n3. Extract it to the data/ folder`;
  }

  if (corruptedTables.length > 0) {
    return `Database appears corrupted or incomplete:\n  ${corruptedTables.join("\n  ")}\n\nThis usually happens when the extraction was interrupted or incomplete.\n\nTo fix this:\n1. Delete the existing database folder\n2. Re-download lancedb-{provider}-all.tar.gz from ${releaseUrl}\n3. Extract it again to the data/ folder`;
  }

  return undefined;
}

/**
 * Main entry point
 */
async function main() {
  // Initialize logging first (before anything else can fail)
  const { logger, logFile } = setupLogger("hytale-rag");

  // Load configuration
  const config = loadConfig();
  logger.section("Configuration");
  logger.info(`Server mode: ${config.server.mode}`);
  logger.info(`Embedding provider: ${config.embedding.provider}`);
  logger.info(`Vector store: ${config.vectorStore.provider}`);
  logger.info(`Database path: ${config.vectorStore.path || "not set"}`);
  logger.debug(`Full config: ${JSON.stringify(config, null, 2)}`);

  // Check for embedding configuration - warn but don't exit (for MCP mode)
  let configError: string | undefined;
  let embedding: ReturnType<typeof createEmbeddingProvider> | undefined;

  // Providers that don't require an API key
  const noApiKeyProviders = ["ollama"];
  const requiresApiKey = !noApiKeyProviders.includes(config.embedding.provider);

  // First, check for .env file misconfigurations
  logger.section("Validation");
  const envFileError = checkEnvFile();
  if (envFileError) {
    configError = envFileError;
    logger.error(`Environment file error: ${envFileError}`);
  } else if (requiresApiKey && !config.embedding.apiKey) {
    const envVar =
      config.embedding.provider === "voyage"
        ? "VOYAGE_API_KEY"
        : config.embedding.provider === "openai"
          ? "OPENAI_API_KEY"
          : `${config.embedding.provider.toUpperCase()}_API_KEY`;

    configError = `API key not configured. Get a free Voyage API key at https://www.voyageai.com/ and add it to your .env file:\n\n${envVar}=your-key-here\n\nThen restart Claude Code.`;
    logger.error(`API key not configured for provider: ${config.embedding.provider}`);
  } else if (requiresApiKey && config.embedding.apiKey) {
    // Validate API key format for providers that need one
    const formatError = validateApiKeyFormat(config.embedding.provider, config.embedding.apiKey);
    if (formatError) {
      configError = formatError;
      logger.error(`API key format invalid: ${formatError}`);
    } else {
      logger.info(`API key validated for provider: ${config.embedding.provider}`);
    }
  }

  // Only exit for non-MCP modes if there's an error (they need the API key to function)
  if (configError && config.server.mode !== "mcp") {
    console.error(`Error: ${configError}`);
    process.exit(1);
  }

  // Initialize embedding provider only if no config errors
  if (!configError) {
    logger.section("Initialization");
    logger.info(`Creating embedding provider: ${config.embedding.provider}`);
    embedding = createEmbeddingProvider({
      type: config.embedding.provider,
      apiKey: config.embedding.apiKey,
      baseUrl: config.embedding.baseUrl,
      models: config.embedding.models,
      batchSize: config.embedding.batchSize,
      rateLimitMs: config.embedding.rateLimitMs,
    });
    logger.info("Embedding provider created successfully");
  }

  // Validate database files before trying to connect
  if (!configError && config.vectorStore.path) {
    logger.debug(`Validating database at: ${config.vectorStore.path}`);
    const dbError = validateDatabase(config.vectorStore.path);
    if (dbError) {
      configError = dbError;
      logger.error(`Database validation failed: ${dbError}`);

      // For non-MCP modes, exit immediately
      if (config.server.mode !== "mcp") {
        console.error(`Error: ${dbError}`);
        process.exit(1);
      }
    } else {
      logger.info("Database validation passed");
    }
  }

  // Initialize vector store
  logger.info(`Creating vector store: ${config.vectorStore.provider}`);
  const vectorStore = createVectorStore({
    type: config.vectorStore.provider,
    path: config.vectorStore.path,
    apiKey: config.vectorStore.apiKey,
    host: config.vectorStore.host,
    environment: config.vectorStore.environment,
    namespace: config.vectorStore.namespace,
  });

  // Connect to vector store (skip if database validation failed)
  if (!configError) {
    logger.info("Connecting to vector store...");
    await vectorStore.connect();
    logger.info("Vector store connected successfully");
  }

  // Create tool registry and register tools
  logger.section("Tools");
  const registry = new ToolRegistry();
  registry.register(searchCodeTool);
  registry.register(searchClientCodeTool);
  registry.register(searchGameDataTool);
  registry.register(codeStatsTool);
  registry.register(clientCodeStatsTool);
  registry.register(gameDataStatsTool);
  logger.info(`Registered ${registry.getAll().length} tools: ${registry.getAll().map(t => t.name).join(", ")}`);

  // Initialize version checker (non-blocking background check)
  // Read version from .version file in data/{provider}/ directory
  const dataDir = config.vectorStore.path ? path.dirname(config.vectorStore.path) : "";
  const versionChecker = new VersionChecker({
    currentVersion: getDatabaseVersion(dataDir),
  });
  versionChecker.checkVersionAsync();

  // Create tool context
  const context: ToolContext = {
    embedding,
    vectorStore,
    config,
    configError,
    versionChecker,
  };

  const { mode, host, port } = config.server;

  // Start servers based on mode
  logger.section("Server Startup");
  if (mode === "mcp") {
    // MCP-only mode (for Claude)
    logger.info("Starting MCP server (stdio transport)");
    await startMCPServer(registry, context);
    logger.info("MCP server started successfully");
  } else if (mode === "rest") {
    // REST API only
    logger.info(`Starting REST API server on ${host}:${port}`);
    const restApp = createRESTServer(registry, context, config);
    startRESTServer(restApp, config);
    logger.info("REST API server started");
  } else if (mode === "openai") {
    // OpenAI-compatible only
    logger.info(`Starting OpenAI-compatible server on ${host}:${port}`);
    const openaiApp = createOpenAIServer(registry, context, config);
    startOpenAIServer(openaiApp, host, port);
    logger.info("OpenAI-compatible server started");
  } else if (mode === "all") {
    // Start all servers
    // Note: MCP uses stdio, so it runs in the background
    // REST and OpenAI use HTTP on different ports
    logger.info("Starting all servers (multi-server mode)");

    console.log("Starting Hytale RAG in multi-server mode...\n");

    // REST API on configured port
    logger.info(`Starting REST API server on ${host}:${port}`);
    const restApp = createRESTServer(registry, context, config);
    startRESTServer(restApp, config);

    // OpenAI-compatible on port + 1
    logger.info(`Starting OpenAI-compatible server on ${host}:${port + 1}`);
    const openaiApp = createOpenAIServer(registry, context, config);
    startOpenAIServer(openaiApp, host, port + 1);

    logger.info("All servers started successfully");
    console.log("\nTo use with Claude, run: npx tsx src/index.ts");
    console.log("(MCP mode is the default when no HTTP servers are needed)\n");
  }
}

// Run main
main().catch((error) => {
  // Try to log the fatal error (logger may not be initialized)
  const logger = getLogger();
  logger.error("Fatal error during startup", error instanceof Error ? error : new Error(String(error)));
  console.error("Fatal error:", error);
  process.exit(1);
});
