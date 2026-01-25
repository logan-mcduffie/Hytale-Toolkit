/**
 * Search Code Tool
 *
 * Semantic search over the decompiled Hytale codebase.
 */

import { searchCodeSchema, type SearchCodeInput } from "../schemas.js";
import type { ToolDefinition, ToolContext, ToolResult } from "./index.js";
import type { CodeSearchResult } from "../types.js";
import { resolveCodePath } from "../../utils/paths.js";

/**
 * Search code tool definition
 */
export const searchCodeTool: ToolDefinition<SearchCodeInput, CodeSearchResult[]> = {
  name: "search_hytale_code",
  description:
    "Search the decompiled Hytale codebase using semantic search. " +
    "Use this to find methods, classes, or functionality by describing what you're looking for. " +
    "Returns relevant Java methods with their full source code.",
  inputSchema: searchCodeSchema,

  async handler(input, context): Promise<ToolResult<CodeSearchResult[]>> {
    // Check for configuration errors (e.g., missing API key)
    if (context.configError || !context.embedding) {
      return {
        success: false,
        error: context.configError || "Embedding provider not configured",
      };
    }

    // Clamp limit
    const limit = Math.min(Math.max(1, input.limit ?? 5), 20);

    // Get embedding for the query
    const queryVector = await context.embedding.embedQuery(input.query, "code");

    // Build filter
    const filter = input.classFilter
      ? { className: input.classFilter }
      : undefined;

    // Search
    const results = await context.vectorStore.search<CodeSearchResult>(
      context.config.tables.code,
      queryVector,
      { limit, filter }
    );

    // Map results
    const data: CodeSearchResult[] = results.map((r) => ({
      id: r.data.id,
      className: r.data.className,
      packageName: r.data.packageName,
      methodName: r.data.methodName,
      methodSignature: r.data.methodSignature,
      content: r.data.content,
      filePath: r.data.filePath,
      lineStart: r.data.lineStart,
      lineEnd: r.data.lineEnd,
      score: r.score,
    }));

    return { success: true, data };
  },
};

/**
 * Format code search results as markdown (for MCP/display)
 */
export function formatCodeResults(results: CodeSearchResult[]): string {
  if (results.length === 0) {
    return "No results found for your query.";
  }

  return results
    .map((r, i) => {
      const fullPath = resolveCodePath(r.filePath);
      return `## Result ${i + 1}: ${r.className}.${r.methodName}
**Package:** ${r.packageName}
**File:** ${fullPath}:${r.lineStart}-${r.lineEnd}
**Signature:** \`${r.methodSignature}\`
**Relevance:** ${(r.score * 100).toFixed(1)}%

\`\`\`java
${r.content}
\`\`\``;
    })
    .join("\n\n---\n\n");
}
