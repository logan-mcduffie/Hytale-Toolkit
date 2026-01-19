/**
 * MCP Server Adapter
 *
 * Exposes tools via the Model Context Protocol for Claude integration.
 */

import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { ToolRegistry, ToolContext } from "../../core/tools/index.js";
import { getLogger } from "../../utils/logger.js";
import { formatCodeResults } from "../../core/tools/search-code.js";
import { formatClientUIResults } from "../../core/tools/search-client-code.js";
import { formatGameDataResults } from "../../core/tools/search-gamedata.js";
import { formatCodeStats } from "../../core/tools/code-stats.js";
import { formatClientUIStats, type ClientUIStats } from "../../core/tools/client-code-stats.js";
import { formatGameDataStats } from "../../core/tools/gamedata-stats.js";
import type { CodeSearchResult, CodeStats, GameDataSearchResult, GameDataStats } from "../../core/types.js";
import type { ClientUISearchResult } from "../../core/tools/search-client-code.js";
import type { VersionInfo } from "../../core/version-checker.js";

/**
 * Format tool result for MCP response
 */
function formatToolResult(toolName: string, data: unknown, versionInfo?: VersionInfo | null): string {
  switch (toolName) {
    case "search_hytale_code":
      return formatCodeResults(data as CodeSearchResult[]);
    case "search_hytale_client_code":
      return formatClientUIResults(data as ClientUISearchResult[]);
    case "search_hytale_gamedata":
      return formatGameDataResults(data as GameDataSearchResult[]);
    case "hytale_code_stats":
      return formatCodeStats(data as CodeStats, versionInfo);
    case "hytale_client_code_stats":
      return formatClientUIStats(data as ClientUIStats, versionInfo);
    case "hytale_gamedata_stats":
      return formatGameDataStats(data as GameDataStats, versionInfo);
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
  const logger = getLogger();

  // Get version from version checker or fall back to default
  const version = context.versionChecker?.getCachedVersion()?.currentVersion ?? "2.0.0";
  logger.info(`Initializing MCP server (version: ${version})`);

  const server = new McpServer({
    name: "hytale-rag",
    version,
  });
  logger.debug("McpServer instance created");

  // Register all tools from the registry using the new registerTool API
  logger.info(`Registering ${registry.getAll().length} tools with MCP server`);
  for (const tool of registry.getAll()) {
    logger.debug(`Registering tool: ${tool.name}`);
    server.registerTool(
      tool.name,
      {
        description: tool.description,
        inputSchema: tool.inputSchema,
      },
      async (input: unknown) => {
        const startTime = Date.now();
        logger.info(`Tool '${tool.name}' called`);
        logger.debug(`Tool '${tool.name}' input: ${JSON.stringify(input)}`);

        const result = await registry.execute(tool.name, input, context);
        const executionTimeMs = Date.now() - startTime;

        if (!result.success) {
          logger.error(`Tool '${tool.name}' failed after ${executionTimeMs}ms: ${result.error}`);
          return {
            content: [{ type: "text" as const, text: `Error: ${result.error}` }],
            isError: true,
          };
        }

        logger.info(`Tool '${tool.name}' completed successfully in ${executionTimeMs}ms`);

        // Get cached version info for stats tools (non-blocking)
        const versionInfo = context.versionChecker?.getCachedVersion() ?? null;
        const formatted = formatToolResult(tool.name, result.data, versionInfo);
        return {
          content: [{ type: "text" as const, text: formatted }],
        };
      }
    );
  }

  // Connect via stdio transport
  logger.info("Creating stdio transport");
  const transport = new StdioServerTransport();
  logger.info("Connecting MCP server to transport...");
  await server.connect(transport);
  logger.info("MCP server connected and ready");
}
