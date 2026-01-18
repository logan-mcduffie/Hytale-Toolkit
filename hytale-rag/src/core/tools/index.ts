/**
 * Tool Registry
 *
 * Central registry for all tools. Provides protocol-agnostic tool definitions
 * that can be adapted to MCP, REST, OpenAI function calling, etc.
 */

import { z, ZodSchema } from "zod";
import type { EmbeddingProvider } from "../../providers/embedding/interface.js";
import type { VectorStore } from "../../providers/vectorstore/interface.js";
import type { AppConfig } from "../../config/schema.js";
import type { VersionChecker } from "../version-checker.js";

/**
 * Context provided to tool handlers
 */
export interface ToolContext {
  /** Embedding provider for query embedding (undefined if API key not configured) */
  embedding?: EmbeddingProvider;
  /** Vector store for search operations */
  vectorStore: VectorStore;
  /** Application configuration */
  config: AppConfig;
  /** Configuration error message (e.g., missing API key) */
  configError?: string;
  /** Version checker for update notifications */
  versionChecker?: VersionChecker;
}

/**
 * Result from a tool execution
 */
export interface ToolResult<T> {
  /** Whether the operation succeeded */
  success: boolean;
  /** Result data (if successful) */
  data?: T;
  /** Error message (if failed) */
  error?: string;
  /** Execution metadata */
  metadata?: {
    executionTimeMs: number;
    tokensUsed?: number;
  };
}

/**
 * Tool handler function type
 */
export type ToolHandler<TInput, TOutput> = (
  input: TInput,
  context: ToolContext
) => Promise<ToolResult<TOutput>>;

/**
 * Definition of a single tool
 */
export interface ToolDefinition<TInput = unknown, TOutput = unknown> {
  /** Unique tool name */
  name: string;
  /** Human-readable description */
  description: string;
  /** Zod schema for input validation */
  inputSchema: ZodSchema;
  /** Handler function */
  handler: ToolHandler<TInput, TOutput>;
}

/**
 * Tool registry - manages all available tools
 */
export class ToolRegistry {
  private tools = new Map<string, ToolDefinition>();

  /**
   * Register a tool
   */
  register<TInput, TOutput>(tool: ToolDefinition<TInput, TOutput>): void {
    this.tools.set(tool.name, tool as ToolDefinition);
  }

  /**
   * Get a tool by name
   */
  get(name: string): ToolDefinition | undefined {
    return this.tools.get(name);
  }

  /**
   * Get all registered tools
   */
  getAll(): ToolDefinition[] {
    return Array.from(this.tools.values());
  }

  /**
   * Check if a tool exists
   */
  has(name: string): boolean {
    return this.tools.has(name);
  }

  /**
   * Execute a tool by name
   */
  async execute<TOutput>(
    name: string,
    input: unknown,
    context: ToolContext
  ): Promise<ToolResult<TOutput>> {
    const tool = this.tools.get(name);

    if (!tool) {
      return {
        success: false,
        error: `Tool '${name}' not found`,
      };
    }

    const startTime = Date.now();

    try {
      // Validate input
      const validatedInput = tool.inputSchema.parse(input);

      // Execute handler
      const result = await tool.handler(validatedInput, context);

      // Add timing metadata
      if (!result.metadata) {
        result.metadata = { executionTimeMs: Date.now() - startTime };
      } else {
        result.metadata.executionTimeMs = Date.now() - startTime;
      }

      return result as ToolResult<TOutput>;
    } catch (error) {
      const executionTimeMs = Date.now() - startTime;

      if (error instanceof z.ZodError) {
        return {
          success: false,
          error: `Validation error: ${error.errors.map((e) => e.message).join(", ")}`,
          metadata: { executionTimeMs },
        };
      }

      return {
        success: false,
        error: error instanceof Error ? error.message : String(error),
        metadata: { executionTimeMs },
      };
    }
  }

  /**
   * Convert tools to OpenAI function calling format
   */
  toOpenAITools(): Array<{
    type: "function";
    function: {
      name: string;
      description: string;
      parameters: Record<string, unknown>;
    };
  }> {
    return this.getAll().map((tool) => ({
      type: "function" as const,
      function: {
        name: tool.name,
        description: tool.description,
        parameters: zodToJsonSchema(tool.inputSchema),
      },
    }));
  }
}

// Use 'any' for Zod internals since _def types are not exported
/* eslint-disable @typescript-eslint/no-explicit-any */

/**
 * Convert a Zod schema to JSON Schema format
 * (Simplified implementation - for production, use zod-to-json-schema package)
 */
export function zodToJsonSchema(schema: ZodSchema): Record<string, unknown> {
  // Get the schema definition (cast to any for internal access)
  const def = schema._def as any;

  if (def.typeName === "ZodObject") {
    const shape = def.shape();
    const properties: Record<string, unknown> = {};
    const required: string[] = [];

    for (const [key, value] of Object.entries(shape)) {
      const fieldSchema = value as ZodSchema;
      const fieldDef = fieldSchema._def as any;

      // Check if field is optional
      const isOptional = fieldDef.typeName === "ZodOptional" || fieldDef.typeName === "ZodDefault";

      if (!isOptional) {
        required.push(key);
      }

      // Get the inner schema for optional/default types
      let innerSchema = fieldSchema;
      if (fieldDef.typeName === "ZodOptional") {
        innerSchema = fieldDef.innerType;
      } else if (fieldDef.typeName === "ZodDefault") {
        innerSchema = fieldDef.innerType;
      }

      properties[key] = zodTypeToJsonSchema(innerSchema);
    }

    return {
      type: "object",
      properties,
      required: required.length > 0 ? required : undefined,
    };
  }

  return zodTypeToJsonSchema(schema);
}

/**
 * Convert a single Zod type to JSON Schema
 */
function zodTypeToJsonSchema(schema: ZodSchema): Record<string, unknown> {
  const def = schema._def as any;
  const result: Record<string, unknown> = {};

  // Handle wrapped types
  if (def.typeName === "ZodOptional" || def.typeName === "ZodDefault") {
    return zodTypeToJsonSchema(def.innerType);
  }

  // Add description if present
  if (def.description) {
    result.description = def.description;
  }

  switch (def.typeName) {
    case "ZodString":
      result.type = "string";
      break;

    case "ZodNumber":
      result.type = "number";
      if (def.checks) {
        for (const check of def.checks) {
          if (check.kind === "int") result.type = "integer";
          if (check.kind === "min") result.minimum = check.value;
          if (check.kind === "max") result.maximum = check.value;
        }
      }
      break;

    case "ZodBoolean":
      result.type = "boolean";
      break;

    case "ZodEnum":
      result.type = "string";
      result.enum = def.values;
      break;

    case "ZodArray":
      result.type = "array";
      result.items = zodTypeToJsonSchema(def.type);
      break;

    default:
      result.type = "string"; // Fallback
  }

  return result;
}
