/**
 * Game Data Stats Tool
 *
 * Get statistics about the indexed Hytale game data.
 */

import { emptySchema, type EmptyInput } from "../schemas.js";
import type { ToolDefinition, ToolContext, ToolResult } from "./index.js";
import type { GameDataStats, GameDataType } from "../types.js";

/**
 * All possible game data types
 */
const ALL_GAME_DATA_TYPES: GameDataType[] = [
  "item",
  "recipe",
  "block",
  "interaction",
  "drop",
  "npc",
  "npc_group",
  "npc_ai",
  "entity",
  "projectile",
  "farming",
  "shop",
  "environment",
  "weather",
  "biome",
  "worldgen",
  "camera",
  "objective",
  "gameplay",
  "localization",
  "zone",
  "terrain_layer",
  "cave",
  "prefab",
];

/**
 * Game data stats tool definition
 */
export const gameDataStatsTool: ToolDefinition<EmptyInput, GameDataStats> = {
  name: "hytale_gamedata_stats",
  description: "Get statistics about the indexed Hytale game data.",
  inputSchema: emptySchema,

  async handler(_input, context): Promise<ToolResult<GameDataStats>> {
    const tableName = context.config.tables.gamedata;

    // Get basic stats
    const tableStats = await context.vectorStore.getStats(tableName);

    // Count by type by iterating through all rows
    const byType: Record<GameDataType, number> = Object.fromEntries(
      ALL_GAME_DATA_TYPES.map((t) => [t, 0])
    ) as Record<GameDataType, number>;

    for await (const batch of context.vectorStore.queryAll<{ type: string }>(tableName)) {
      for (const row of batch) {
        const type = row.type as GameDataType;
        if (type && type in byType) {
          byType[type]++;
        }
      }
    }

    const data: GameDataStats = {
      totalItems: tableStats.rowCount,
      byType,
    };

    return { success: true, data };
  },
};

/**
 * Format game data stats as markdown (for MCP/display)
 */
export function formatGameDataStats(stats: GameDataStats): string {
  // Format by-type stats, sorted by count
  const byTypeLines = Object.entries(stats.byType)
    .filter(([_, count]) => count > 0)
    .sort((a, b) => b[1] - a[1])
    .map(([type, count]) => `- **${type}:** ${count.toLocaleString()}`)
    .join("\n");

  return `# Hytale Game Data Statistics

**Total Items:** ${stats.totalItems.toLocaleString()}

## By Type:
${byTypeLines}

The game data database is ready for semantic search.`;
}
