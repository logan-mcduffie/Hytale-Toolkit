/**
 * Client UI Stats Tool
 *
 * Get statistics about the indexed Hytale client UI files.
 */

import { emptySchema, type EmptyInput } from "../schemas.js";
import type { ToolDefinition, ToolContext, ToolResult } from "./index.js";
import { formatVersionNotice, type VersionInfo } from "../version-checker.js";

/**
 * Client UI statistics
 */
export interface ClientUIStats {
  totalFiles: number;
  uniqueCategories: number;
  byType: Record<string, number>;
}

/**
 * Client UI stats tool definition
 */
export const clientCodeStatsTool: ToolDefinition<EmptyInput, ClientUIStats> = {
  name: "hytale_client_code_stats",
  description:
    "Get statistics about the indexed Hytale client UI files (.xaml, .ui, .json).",
  inputSchema: emptySchema,

  async handler(_input, context): Promise<ToolResult<ClientUIStats>> {
    const tableName = context.config.tables.clientUI;

    // Get basic stats
    const tableStats = await context.vectorStore.getStats(tableName);

    // Count by category and type
    const categories = new Set<string>();
    const byType: Record<string, number> = {};

    for await (const batch of context.vectorStore.queryAll<{
      category: string;
      type: string;
    }>(tableName)) {
      for (const row of batch) {
        if (row.category) categories.add(row.category);
        if (row.type) {
          byType[row.type] = (byType[row.type] || 0) + 1;
        }
      }
    }

    const data: ClientUIStats = {
      totalFiles: tableStats.rowCount,
      uniqueCategories: categories.size,
      byType,
    };

    return { success: true, data };
  },
};

/**
 * Format client UI stats as markdown (for MCP/display)
 */
export function formatClientUIStats(stats: ClientUIStats, versionInfo?: VersionInfo | null): string {
  const typeBreakdown = Object.entries(stats.byType)
    .map(([type, count]) => `  - ${type.toUpperCase()}: ${count.toLocaleString()}`)
    .join("\n");

  return `# Hytale Client UI Statistics

- **Total Files:** ${stats.totalFiles.toLocaleString()}
- **Unique Categories:** ${stats.uniqueCategories.toLocaleString()}

**By Type:**
${typeBreakdown}

The client UI database is ready for semantic search.${formatVersionNotice(versionInfo ?? null)}`;
}
