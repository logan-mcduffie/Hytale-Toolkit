/**
 * Search Client UI Tool
 *
 * Semantic search over Hytale client UI files (.xaml, .ui, .json).
 */

import { searchClientCodeSchema, type SearchClientCodeInput } from "../schemas.js";
import type { ToolDefinition, ToolContext, ToolResult } from "./index.js";
import { resolveClientDataPath } from "../../utils/paths.js";

/**
 * Client UI search result
 */
export interface ClientUISearchResult {
  id: string;
  type: string;
  name: string;
  filePath: string;
  relativePath: string;
  content: string;
  category?: string;
  score: number;
}

/**
 * Search client UI tool definition
 */
export const searchClientCodeTool: ToolDefinition<SearchClientCodeInput, ClientUISearchResult[]> = {
  name: "search_hytale_client_code",
  description:
    "Search Hytale client UI files using semantic search. " +
    "Use this to find UI templates (.xaml), UI components (.ui), and NodeEditor definitions. " +
    "Useful for modifying game UI appearance like inventory layout, hotbar, health bars, etc.",
  inputSchema: searchClientCodeSchema,

  async handler(input, context): Promise<ToolResult<ClientUISearchResult[]>> {
    // Check for configuration errors (e.g., missing API key)
    if (context.configError || !context.embedding) {
      return {
        success: false,
        error: context.configError || "Embedding provider not configured",
      };
    }

    // Clamp limit
    const limit = Math.min(Math.max(1, input.limit ?? 5), 20);

    // Get embedding for the query (use "text" since UI files are markup, not code)
    const queryVector = await context.embedding.embedQuery(input.query, "text");

    // Build filter
    const filter = input.classFilter
      ? { category: input.classFilter }
      : undefined;

    // Search
    const results = await context.vectorStore.search<ClientUISearchResult>(
      context.config.tables.clientUI,
      queryVector,
      { limit, filter }
    );

    // Map results
    const data: ClientUISearchResult[] = results.map((r) => ({
      id: r.data.id,
      type: r.data.type,
      name: r.data.name,
      filePath: r.data.filePath,
      relativePath: r.data.relativePath,
      content: r.data.content,
      category: r.data.category,
      score: r.score,
    }));

    return { success: true, data };
  },
};

/**
 * Format client UI search results as markdown (for MCP/display)
 */
export function formatClientUIResults(results: ClientUISearchResult[]): string {
  if (results.length === 0) {
    return "No results found for your query in the client UI files.";
  }

  return results
    .map((r, i) => {
      const fullPath = resolveClientDataPath(r.filePath);
      const fileType = r.type === "xaml" ? "xml" : r.type === "ui" ? "css" : "json";
      return `## Result ${i + 1}: ${r.name}
**Type:** ${r.type.toUpperCase()}
**Category:** ${r.category || "General"}
**Path:** ${fullPath}
**Relevance:** ${(r.score * 100).toFixed(1)}%

\`\`\`${fileType}
${r.content}
\`\`\``;
    })
    .join("\n\n---\n\n");
}
