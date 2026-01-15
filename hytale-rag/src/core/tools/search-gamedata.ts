/**
 * Search Game Data Tool
 *
 * Semantic search over Hytale game data (items, recipes, NPCs, etc.)
 */

import { searchGameDataSchema, type SearchGameDataInput } from "../schemas.js";
import type { ToolDefinition, ToolContext, ToolResult } from "./index.js";
import type { GameDataSearchResult, GameDataType } from "../types.js";

/**
 * Search game data tool definition
 */
export const searchGameDataTool: ToolDefinition<SearchGameDataInput, GameDataSearchResult[]> = {
  name: "search_hytale_gamedata",
  description:
    "Search vanilla Hytale game data including items, recipes, NPCs, drops, blocks, and more. " +
    "Use this for modding questions like 'how to craft X', 'what drops Y', 'NPC behavior for Z', " +
    "'what items use tag T', or 'how does the farming system work'.",
  inputSchema: searchGameDataSchema,

  async handler(input, context): Promise<ToolResult<GameDataSearchResult[]>> {
    // Clamp limit
    const limit = Math.min(Math.max(1, input.limit ?? 5), 20);

    // Get embedding for the query (using text model)
    const queryVector = await context.embedding.embedQuery(input.query, "text");

    // Build filter (null/undefined means no filter)
    const typeFilter =
      input.type && input.type !== "all"
        ? { type: input.type as GameDataType }
        : undefined;

    // Search
    const results = await context.vectorStore.search<GameDataSearchResult>(
      context.config.tables.gamedata,
      queryVector,
      { limit, filter: typeFilter }
    );

    // Map results
    const data: GameDataSearchResult[] = results.map((r) => ({
      id: r.data.id,
      type: r.data.type,
      name: r.data.name,
      filePath: r.data.filePath,
      rawJson: r.data.rawJson,
      category: r.data.category,
      tags: r.data.tags || [],
      parentId: r.data.parentId,
      score: r.score,
    }));

    return { success: true, data };
  },
};

/**
 * Format game data search results as markdown (for MCP/display)
 */
export function formatGameDataResults(results: GameDataSearchResult[]): string {
  if (results.length === 0) {
    return "No game data found for your query.";
  }

  return results
    .map((r, i) => {
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
    })
    .join("\n\n---\n\n");
}
