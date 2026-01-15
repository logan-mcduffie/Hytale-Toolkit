/**
 * MCP Server Adapter
 *
 * Exposes tools via the Model Context Protocol for Claude integration.
 */

import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { ToolRegistry, ToolContext, zodToJsonSchema } from "../../core/tools/index.js";
import { formatCodeResults } from "../../core/tools/search-code.js";
import { formatGameDataResults } from "../../core/tools/search-gamedata.js";
import { formatCodeStats } from "../../core/tools/code-stats.js";
import { formatGameDataStats } from "../../core/tools/gamedata-stats.js";
import type { CodeSearchResult, CodeStats, GameDataSearchResult, GameDataStats } from "../../core/types.js";

/**
 * Format tool result for MCP response
 */
function formatToolResult(toolName: string, data: unknown): string {
  switch (toolName) {
    case "search_hytale_code":
      return formatCodeResults(data as CodeSearchResult[]);
    case "search_hytale_gamedata":
      return formatGameDataResults(data as GameDataSearchResult[]);
    case "hytale_code_stats":
      return formatCodeStats(data as CodeStats);
    case "hytale_gamedata_stats":
      return formatGameDataStats(data as GameDataStats);
    default:
      return JSON.stringify(data, null, 2);
  }
}

/**
 * Start the MCP server
 *
 * @param registry - Tool registry with registered tools
 * @param context - Tool execution context
 */
export async function startMCPServer(
  registry: ToolRegistry,
  context: ToolContext
): Promise<void> {
  const server = new McpServer({
    name: "hytale-rag",
    version: "2.0.0",
  });

  // Register all tools from the registry
  for (const tool of registry.getAll()) {
    server.tool(
      tool.name,
      tool.description,
      zodToJsonSchema(tool.inputSchema) as Record<string, unknown>,
      async (input: unknown) => {
        const result = await registry.execute(tool.name, input, context);

        if (!result.success) {
          return {
            content: [{ type: "text" as const, text: `Error: ${result.error}` }],
            isError: true,
          };
        }

        const formatted = formatToolResult(tool.name, result.data);
        return {
          content: [{ type: "text" as const, text: formatted }],
        };
      }
    );
  }

  // Connect via stdio transport
  const transport = new StdioServerTransport();
  await server.connect(transport);
}
