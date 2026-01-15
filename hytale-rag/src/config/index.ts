/**
 * Configuration Loader
 *
 * Loads configuration from environment variables and optional config files.
 * Priority: Environment variables > Config file > Defaults
 */

import * as fs from "fs";
import * as path from "path";
import { fileURLToPath } from "url";
import { configSchema, type AppConfig } from "./schema.js";

// Get the directory where this module is located (for resolving relative paths)
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
// Default data path is relative to the hytale-rag package root
const DEFAULT_DATA_PATH = path.resolve(__dirname, "..", "..", "data", "lancedb");

/**
 * Deep merge two objects, with source values taking precedence.
 * Handles undefined values by keeping target values.
 */
function deepMerge<T extends Record<string, unknown>>(
  target: T,
  source: Partial<T>
): T {
  const result = { ...target };

  for (const key of Object.keys(source) as Array<keyof T>) {
    const sourceValue = source[key];
    const targetValue = target[key];

    if (sourceValue === undefined) {
      continue;
    }

    if (
      sourceValue !== null &&
      typeof sourceValue === "object" &&
      !Array.isArray(sourceValue) &&
      targetValue !== null &&
      typeof targetValue === "object" &&
      !Array.isArray(targetValue)
    ) {
      result[key] = deepMerge(
        targetValue as Record<string, unknown>,
        sourceValue as Record<string, unknown>
      ) as T[keyof T];
    } else {
      result[key] = sourceValue as T[keyof T];
    }
  }

  return result;
}

/**
 * Parse a YAML-like config file (simple key: value format)
 * For full YAML support, add the 'yaml' package as a dependency
 */
function parseSimpleYaml(content: string): Record<string, unknown> {
  const result: Record<string, unknown> = {};
  const lines = content.split("\n");
  const stack: Array<{ obj: Record<string, unknown>; indent: number }> = [
    { obj: result, indent: -1 },
  ];

  for (const line of lines) {
    // Skip comments and empty lines
    if (line.trim().startsWith("#") || line.trim() === "") continue;

    const match = line.match(/^(\s*)(\w+):\s*(.*)$/);
    if (!match) continue;

    const [, spaces, key, rawValue] = match;
    const indent = spaces.length;

    // Pop stack until we find parent
    while (stack.length > 1 && stack[stack.length - 1].indent >= indent) {
      stack.pop();
    }

    const parent = stack[stack.length - 1].obj;

    // Remove inline comments and trim
    let value = rawValue.split("#")[0].trim();

    if (value === "" || value === undefined) {
      // Nested object
      const newObj: Record<string, unknown> = {};
      parent[key] = newObj;
      stack.push({ obj: newObj, indent });
    } else {
      // Scalar value - try to parse as number/boolean
      let parsed: unknown = value;
      if (value === "true") parsed = true;
      else if (value === "false") parsed = false;
      else if (/^-?\d+$/.test(value)) parsed = parseInt(value, 10);
      else if (/^-?\d+\.\d+$/.test(value)) parsed = parseFloat(value);

      parent[key] = parsed;
    }
  }

  return result;
}

/**
 * Get the embedding API key based on provider
 */
function getEmbeddingApiKey(provider: string): string | undefined {
  switch (provider) {
    case "voyage":
      return process.env.VOYAGE_API_KEY;
    case "openai":
      return process.env.OPENAI_API_KEY;
    case "cohere":
      return process.env.COHERE_API_KEY;
    default:
      return undefined;
  }
}

/**
 * Load configuration from environment variables
 */
function loadEnvConfig(): Record<string, unknown> {
  const embeddingProvider =
    process.env.HYTALE_RAG_EMBEDDING_PROVIDER ||
    process.env.HYTALE_RAG_EMBEDDING ||
    "voyage";

  const result: Record<string, unknown> = {};

  // Server config
  const server: Record<string, unknown> = {};
  if (process.env.HYTALE_RAG_MODE) server.mode = process.env.HYTALE_RAG_MODE;
  if (process.env.HYTALE_RAG_PORT) server.port = parseInt(process.env.HYTALE_RAG_PORT, 10);
  if (process.env.HYTALE_RAG_HOST) server.host = process.env.HYTALE_RAG_HOST;
  if (Object.keys(server).length > 0) result.server = server;

  // Embedding config
  const embedding: Record<string, unknown> = { provider: embeddingProvider };
  const apiKey = getEmbeddingApiKey(embeddingProvider);
  if (apiKey) embedding.apiKey = apiKey;
  if (process.env.OLLAMA_BASE_URL) embedding.baseUrl = process.env.OLLAMA_BASE_URL;
  result.embedding = embedding;

  // Vector store config
  const vectorStore: Record<string, unknown> = {};
  if (process.env.HYTALE_RAG_VECTORSTORE_PROVIDER) {
    vectorStore.provider = process.env.HYTALE_RAG_VECTORSTORE_PROVIDER;
  }
  if (process.env.HYTALE_RAG_DB_PATH) vectorStore.path = process.env.HYTALE_RAG_DB_PATH;
  if (process.env.PINECONE_API_KEY) vectorStore.apiKey = process.env.PINECONE_API_KEY;
  if (process.env.CHROMA_HOST) vectorStore.host = process.env.CHROMA_HOST;
  if (process.env.WEAVIATE_HOST) vectorStore.host = process.env.WEAVIATE_HOST;
  if (process.env.PINECONE_ENVIRONMENT) vectorStore.environment = process.env.PINECONE_ENVIRONMENT;
  if (Object.keys(vectorStore).length > 0) result.vectorStore = vectorStore;

  // API auth config
  if (process.env.HYTALE_RAG_API_KEYS) {
    const apiKeys = process.env.HYTALE_RAG_API_KEYS.split(",").filter(Boolean);
    if (apiKeys.length > 0) {
      result.api = { auth: { apiKeys } };
    }
  }

  return result;
}

/**
 * Load configuration from a file
 */
function loadFileConfig(configPath: string): Record<string, unknown> {
  if (!fs.existsSync(configPath)) {
    return {};
  }

  const content = fs.readFileSync(configPath, "utf-8");
  const ext = path.extname(configPath).toLowerCase();

  if (ext === ".json") {
    return JSON.parse(content);
  } else if (ext === ".yaml" || ext === ".yml") {
    // Use simple YAML parser (for full support, add yaml package)
    return parseSimpleYaml(content);
  }

  return {};
}

/**
 * Load and validate the complete configuration.
 *
 * Configuration sources (in order of priority):
 * 1. Environment variables
 * 2. Config file (if specified via HYTALE_RAG_CONFIG env var)
 * 3. Default config file at ./config/default.yaml
 * 4. Schema defaults
 *
 * @returns Validated configuration object
 */
export function loadConfig(): AppConfig {
  // Load config file
  const configPath =
    process.env.HYTALE_RAG_CONFIG ||
    path.resolve(process.cwd(), "config", "default.yaml");

  const fileConfig = loadFileConfig(configPath);

  // Load environment variables
  const envConfig = loadEnvConfig();

  // Merge configs (env > file > defaults)
  const merged = deepMerge(
    fileConfig as Record<string, unknown>,
    envConfig
  );

  // Validate and apply defaults
  const validated = configSchema.parse(merged);

  // Set default DB path if not specified
  if (!validated.vectorStore.path && validated.vectorStore.provider === "lancedb") {
    validated.vectorStore.path = DEFAULT_DATA_PATH;
  }

  return validated;
}

/**
 * Get a minimal config for testing or CLI tools
 */
export function getMinimalConfig(overrides?: Partial<AppConfig>): AppConfig {
  const defaults: AppConfig = {
    server: { mode: "mcp", port: 3000, host: "localhost" },
    embedding: {
      provider: "voyage",
      models: {},
      batchSize: 128,
      rateLimitMs: 100,
    },
    vectorStore: {
      provider: "lancedb",
      path: DEFAULT_DATA_PATH,
    },
    tables: { code: "hytale_methods", clientUI: "hytale_client_ui", gamedata: "hytale_gamedata" },
    api: {
      rateLimit: { windowMs: 60000, max: 100 },
      auth: { enabled: false, apiKeys: [] },
    },
  };

  return overrides ? deepMerge(defaults, overrides) : defaults;
}

// Re-export types
export * from "./schema.js";
