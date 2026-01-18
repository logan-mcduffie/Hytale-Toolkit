/**
 * Code Stats Tool
 *
 * Get statistics about the indexed Hytale codebase.
 */

import { emptySchema, type EmptyInput } from "../schemas.js";
import type { ToolDefinition, ToolContext, ToolResult } from "./index.js";
import type { CodeStats } from "../types.js";
import { formatVersionNotice, type VersionInfo } from "../version-checker.js";

/**
 * Code stats tool definition
 */
export const codeStatsTool: ToolDefinition<EmptyInput, CodeStats> = {
  name: "hytale_code_stats",
  description: "Get statistics about the indexed Hytale codebase.",
  inputSchema: emptySchema,

  async handler(_input, context): Promise<ToolResult<CodeStats>> {
    const tableName = context.config.tables.code;

    // Get basic stats
    const tableStats = await context.vectorStore.getStats(tableName);

    // Count unique classes and packages by iterating through all rows
    const classNames = new Set<string>();
    const packageNames = new Set<string>();

    for await (const batch of context.vectorStore.queryAll<{
      className: string;
      packageName: string;
    }>(tableName)) {
      for (const row of batch) {
        if (row.className) classNames.add(row.className);
        if (row.packageName) packageNames.add(row.packageName);
      }
    }

    const data: CodeStats = {
      totalMethods: tableStats.rowCount,
      uniqueClasses: classNames.size,
      uniquePackages: packageNames.size,
    };

    return { success: true, data };
  },
};

/**
 * Format code stats as markdown (for MCP/display)
 */
export function formatCodeStats(stats: CodeStats, versionInfo?: VersionInfo | null): string {
  const version = versionInfo?.currentVersion ?? "unknown";
  return `# Hytale Codebase Statistics

- **Total Methods:** ${stats.totalMethods.toLocaleString()}
- **Unique Classes:** ${stats.uniqueClasses.toLocaleString()}
- **Unique Packages:** ${stats.uniquePackages.toLocaleString()}

**Database Version:** ${version}

The database is ready for semantic code search.${formatVersionNotice(versionInfo ?? null)}`;
}
