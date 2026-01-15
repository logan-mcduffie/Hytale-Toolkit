/**
 * OpenAI-Compatible Server
 *
 * Exposes tools in OpenAI function calling format.
 * This allows integration with OpenAI GPT models and compatible clients.
 */

import express, { Express, Request, Response } from "express";
import cors from "cors";
import { ToolRegistry, ToolContext } from "../../core/tools/index.js";
import type { AppConfig } from "../../config/schema.js";

/**
 * OpenAI function tool format
 */
interface OpenAITool {
  type: "function";
  function: {
    name: string;
    description: string;
    parameters: Record<string, unknown>;
  };
}

/**
 * OpenAI function call request
 */
interface FunctionCallRequest {
  name: string;
  arguments: string | Record<string, unknown>;
}

/**
 * Create an OpenAI-compatible Express app
 *
 * @param registry - Tool registry
 * @param context - Tool execution context
 * @param _config - Application configuration
 * @returns Configured Express app
 */
export function createOpenAIServer(
  registry: ToolRegistry,
  context: ToolContext,
  _config: AppConfig
): Express {
  const app = express();

  app.use(cors());
  app.use(express.json());

  // Get available tools in OpenAI format
  app.get("/v1/tools", (_req, res) => {
    const tools: OpenAITool[] = registry.toOpenAITools();
    res.json({ tools });
  });

  // Execute a function call
  app.post("/v1/function-call", async (req: Request, res: Response) => {
    const { name, arguments: args } = req.body as FunctionCallRequest;

    if (!name) {
      res.status(400).json({
        error: { message: "Function name is required" },
      });
      return;
    }

    const tool = registry.get(name);
    if (!tool) {
      res.status(404).json({
        error: { message: `Function '${name}' not found` },
      });
      return;
    }

    try {
      // Parse arguments if string
      const parsedArgs = typeof args === "string" ? JSON.parse(args) : args || {};

      const result = await registry.execute(name, parsedArgs, context);

      if (!result.success) {
        res.status(400).json({
          error: { message: result.error },
        });
        return;
      }

      res.json({
        name,
        result: result.data,
        metadata: result.metadata,
      });
    } catch (error) {
      res.status(400).json({
        error: {
          message: error instanceof Error ? error.message : "Invalid arguments",
        },
      });
    }
  });

  // Simplified chat completions endpoint for tool routing
  // This doesn't do actual chat completion - it just handles tool calls
  app.post("/v1/chat/completions", async (req: Request, res: Response) => {
    const { tool_choice, tools } = req.body;

    // Return available tools if no specific tool requested
    if (!tool_choice || tool_choice === "auto" || tool_choice === "none") {
      const availableTools = registry.toOpenAITools();

      res.json({
        id: `chatcmpl-${Date.now()}`,
        object: "chat.completion",
        created: Math.floor(Date.now() / 1000),
        model: "hytale-rag",
        choices: [
          {
            index: 0,
            message: {
              role: "assistant",
              content:
                "I have access to Hytale RAG tools. Available tools: " +
                availableTools.map((t) => t.function.name).join(", "),
              tool_calls: null,
            },
            finish_reason: "stop",
          },
        ],
        usage: { prompt_tokens: 0, completion_tokens: 0, total_tokens: 0 },
      });
      return;
    }

    // Handle specific tool choice
    if (tool_choice?.type === "function" && tool_choice?.function?.name) {
      const toolName = tool_choice.function.name;
      const tool = registry.get(toolName);

      if (!tool) {
        res.status(400).json({
          error: { message: `Tool '${toolName}' not found` },
        });
        return;
      }

      // Try to extract arguments from the last user message or function arguments
      let args: Record<string, unknown> = {};

      if (tool_choice.function.arguments) {
        args =
          typeof tool_choice.function.arguments === "string"
            ? JSON.parse(tool_choice.function.arguments)
            : tool_choice.function.arguments;
      }

      const result = await registry.execute(toolName, args, context);

      const toolCallId = `call_${Date.now()}`;

      res.json({
        id: `chatcmpl-${Date.now()}`,
        object: "chat.completion",
        created: Math.floor(Date.now() / 1000),
        model: "hytale-rag",
        choices: [
          {
            index: 0,
            message: {
              role: "assistant",
              content: null,
              tool_calls: [
                {
                  id: toolCallId,
                  type: "function",
                  function: {
                    name: toolName,
                    arguments: JSON.stringify(args),
                  },
                },
              ],
            },
            finish_reason: "tool_calls",
          },
        ],
        // Include the actual result as a follow-up message
        tool_results: [
          {
            tool_call_id: toolCallId,
            role: "tool",
            name: toolName,
            content: result.success
              ? JSON.stringify(result.data)
              : JSON.stringify({ error: result.error }),
          },
        ],
        usage: { prompt_tokens: 0, completion_tokens: 0, total_tokens: 0 },
      });
      return;
    }

    // Invalid tool_choice format
    res.status(400).json({
      error: {
        message:
          "Invalid tool_choice format. Use 'auto', 'none', or {type: 'function', function: {name: 'tool_name'}}",
      },
    });
  });

  // Models endpoint (for compatibility)
  app.get("/v1/models", (_req, res) => {
    res.json({
      object: "list",
      data: [
        {
          id: "hytale-rag",
          object: "model",
          created: Math.floor(Date.now() / 1000),
          owned_by: "hytale-toolkit",
          permission: [],
          root: "hytale-rag",
          parent: null,
        },
      ],
    });
  });

  return app;
}

/**
 * Start the OpenAI-compatible server
 *
 * @param app - Express app
 * @param host - Host to bind to
 * @param port - Port to listen on
 */
export function startOpenAIServer(app: Express, host: string, port: number): void {
  app.listen(port, host, () => {
    console.log(`OpenAI-compatible API listening on http://${host}:${port}`);
    console.log(`  List tools:     GET  http://${host}:${port}/v1/tools`);
    console.log(`  Function call:  POST http://${host}:${port}/v1/function-call`);
    console.log(`  Chat endpoint:  POST http://${host}:${port}/v1/chat/completions`);
  });
}
