#!/usr/bin/env node
import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { z } from "zod";
import { search, getStats, searchGameData, getGameDataStats } from "./db.js";
import { embedQuery, embedGameDataQuery } from "./embedder.js";
import type { GameDataType } from "./types.js";
import * as path from "path";
import { fileURLToPath } from "url";

// Get the directory where this script lives
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Configuration - these can be overridden via environment variables
// Default to data/lancedb relative to the project root
const DB_PATH = process.env.HYTALE_RAG_DB_PATH ||
  path.resolve(__dirname, "..", "data", "lancedb");
const VOYAGE_API_KEY = process.env.VOYAGE_API_KEY || "";

if (!VOYAGE_API_KEY) {
  console.error("Warning: VOYAGE_API_KEY not set. Search will fail.");
}

const server = new McpServer({
  name: "hytale-rag",
  version: "1.0.0",
});

// Tool: Search for methods by semantic query
server.tool(
  "search_hytale_code",
  "Search the decompiled Hytale codebase using semantic search. " +
    "Use this to find methods, classes, or functionality by describing what you're looking for. " +
    "Returns relevant Java methods with their full source code.",
  {
    query: z.string().describe("Natural language description of what you're looking for"),
    limit: z.number().optional().default(5).describe("Number of results to return (default 5, max 20)"),
    classFilter: z.string().optional().describe("Filter results to a specific class name"),
  },
  async ({ query, limit = 5, classFilter }) => {
    try {
      // Clamp limit
      const resultLimit = Math.min(Math.max(1, limit), 20);

      // Get embedding for the query
      const queryVector = await embedQuery(query, VOYAGE_API_KEY);

      // Build filter if specified
      let filter: string | undefined;
      if (classFilter) {
        filter = `className = '${classFilter}'`;
      }

      // Search
      const results = await search(DB_PATH, queryVector, resultLimit, filter);

      if (results.length === 0) {
        return {
          content: [{ type: "text" as const, text: "No results found for your query." }],
        };
      }

      // Format results
      const formatted = results.map((r, i) => {
        return `## Result ${i + 1}: ${r.className}.${r.methodName}
**Package:** ${r.packageName}
**File:** ${r.filePath}:${r.lineStart}-${r.lineEnd}
**Signature:** \`${r.methodSignature}\`
**Relevance:** ${(r.score * 100).toFixed(1)}%

\`\`\`java
${r.content}
\`\`\``;
      }).join("\n\n---\n\n");

      return {
        content: [{ type: "text" as const, text: formatted }],
      };
    } catch (error: any) {
      return {
        content: [{ type: "text" as const, text: `Search failed: ${error.message}` }],
        isError: true,
      };
    }
  }
);

// Tool: Get database statistics
server.tool(
  "hytale_code_stats",
  "Get statistics about the indexed Hytale codebase.",
  {},
  async () => {
    try {
      const stats = await getStats(DB_PATH);

      const text = `# Hytale Codebase Statistics

- **Total Methods:** ${stats.totalMethods.toLocaleString()}
- **Unique Classes:** ${stats.uniqueClasses.toLocaleString()}
- **Unique Packages:** ${stats.uniquePackages.toLocaleString()}

The database is ready for semantic code search.`;

      return {
        content: [{ type: "text" as const, text }],
      };
    } catch (error: any) {
      return {
        content: [{ type: "text" as const, text: `Failed to get stats: ${error.message}` }],
        isError: true,
      };
    }
  }
);

// =====================================================
// Game Data Tools
// =====================================================

// Valid game data types for filtering
const GAME_DATA_TYPES = [
  "all", "item", "recipe", "block", "interaction", "drop",
  "npc", "npc_group", "npc_ai", "entity", "projectile",
  "farming", "shop", "environment", "weather", "biome",
  "worldgen", "camera", "objective", "gameplay", "localization"
] as const;

// Tool: Search game data
server.tool(
  "search_hytale_gamedata",
  "Search vanilla Hytale game data including items, recipes, NPCs, drops, blocks, and more. " +
    "Use this for modding questions like 'how to craft X', 'what drops Y', 'NPC behavior for Z', " +
    "'what items use tag T', or 'how does the farming system work'.",
  {
    query: z.string().describe("Natural language question about Hytale game data"),
    type: z.enum(GAME_DATA_TYPES)
      .optional()
      .default("all")
      .describe("Filter by data type (default: all)"),
    limit: z.number().optional().default(5).describe("Number of results (default 5, max 20)"),
  },
  async ({ query, type = "all", limit = 5 }) => {
    try {
      // Clamp limit
      const resultLimit = Math.min(Math.max(1, limit), 20);

      // Get embedding for the query using voyage-3
      const queryVector = await embedGameDataQuery(query, VOYAGE_API_KEY);

      // Type filter (null means no filter)
      const typeFilter = type === "all" ? undefined : type as GameDataType;

      // Search
      const results = await searchGameData(DB_PATH, queryVector, resultLimit, typeFilter);

      if (results.length === 0) {
        return {
          content: [{ type: "text" as const, text: "No game data found for your query." }],
        };
      }

      // Format results - show JSON content
      const formatted = results.map((r, i) => {
        const parts = [
          `## Result ${i + 1}: ${r.name}`,
          `**Type:** ${r.type}`,
          `**Path:** ${r.filePath}`,
        ];

        if (r.category) parts.push(`**Category:** ${r.category}`);
        if (r.parentId) parts.push(`**Parent:** ${r.parentId}`);
        if (r.tags && r.tags.length > 0) parts.push(`**Tags:** ${r.tags.join(", ")}`);
        parts.push(`**Relevance:** ${(r.score * 100).toFixed(1)}%`);

        // Pretty print the JSON
        let jsonContent = r.rawJson;
        try {
          jsonContent = JSON.stringify(JSON.parse(r.rawJson), null, 2);
        } catch {
          // Keep original if parse fails
        }

        parts.push("");
        parts.push("```json");
        parts.push(jsonContent);
        parts.push("```");

        return parts.join("\n");
      }).join("\n\n---\n\n");

      return {
        content: [{ type: "text" as const, text: formatted }],
      };
    } catch (error: any) {
      return {
        content: [{ type: "text" as const, text: `Game data search failed: ${error.message}` }],
        isError: true,
      };
    }
  }
);

// Tool: Get game data statistics
server.tool(
  "hytale_gamedata_stats",
  "Get statistics about the indexed Hytale game data.",
  {},
  async () => {
    try {
      const stats = await getGameDataStats(DB_PATH);

      // Format by-type stats, sorted by count
      const byTypeLines = Object.entries(stats.byType)
        .filter(([_, count]) => count > 0)
        .sort((a, b) => b[1] - a[1])
        .map(([type, count]) => `- **${type}:** ${count.toLocaleString()}`)
        .join("\n");

      const text = `# Hytale Game Data Statistics

**Total Items:** ${stats.totalItems.toLocaleString()}

## By Type:
${byTypeLines}

The game data database is ready for semantic search.`;

      return {
        content: [{ type: "text" as const, text }],
      };
    } catch (error: any) {
      return {
        content: [{ type: "text" as const, text: `Failed to get game data stats: ${error.message}` }],
        isError: true,
      };
    }
  }
);

// Start server
const transport = new StdioServerTransport();
await server.connect(transport);
