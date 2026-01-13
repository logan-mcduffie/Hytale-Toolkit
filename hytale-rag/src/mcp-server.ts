#!/usr/bin/env node
import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { z } from "zod";
import { search, getStats } from "./db.js";
import { embedQuery } from "./embedder.js";
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

// Start server
const transport = new StdioServerTransport();
await server.connect(transport);
