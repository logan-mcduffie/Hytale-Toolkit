/**
 * OpenAPI Specification Generator
 *
 * Generates an OpenAPI 3.0 specification from the tool registry.
 */

import { ToolRegistry, zodToJsonSchema } from "../../core/tools/index.js";
import type { AppConfig } from "../../config/schema.js";

/**
 * OpenAPI specification type
 */
export interface OpenAPISpec {
  openapi: string;
  info: {
    title: string;
    description: string;
    version: string;
  };
  servers: Array<{ url: string; description: string }>;
  paths: Record<string, unknown>;
  components: {
    schemas: Record<string, unknown>;
    securitySchemes?: Record<string, unknown>;
  };
  security?: Array<Record<string, unknown>>;
}

/**
 * Generate OpenAPI specification from tool registry
 *
 * @param registry - Tool registry
 * @param config - Application configuration
 * @returns OpenAPI 3.0 specification
 */
export function generateOpenAPISpec(
  registry: ToolRegistry,
  config: AppConfig
): OpenAPISpec {
  const paths: Record<string, unknown> = {};
  const schemas: Record<string, unknown> = {};

  // Add health endpoint
  paths["/health"] = {
    get: {
      summary: "Health check",
      description: "Check if the API is healthy and ready to serve requests",
      operationId: "healthCheck",
      responses: {
        "200": {
          description: "API is healthy",
          content: {
            "application/json": {
              schema: {
                type: "object",
                properties: {
                  status: { type: "string", enum: ["ok"] },
                  timestamp: { type: "string", format: "date-time" },
                },
              },
            },
          },
        },
        "503": {
          description: "API is unhealthy",
          content: {
            "application/json": {
              schema: {
                type: "object",
                properties: {
                  status: { type: "string", enum: ["unhealthy"] },
                  error: { type: "string" },
                },
              },
            },
          },
        },
      },
    },
  };

  // Add tools list endpoint
  paths["/v1/tools"] = {
    get: {
      summary: "List available tools",
      description: "Get a list of all available tools and their parameters",
      operationId: "listTools",
      responses: {
        "200": {
          description: "List of tools",
          content: {
            "application/json": {
              schema: {
                type: "object",
                properties: {
                  tools: {
                    type: "array",
                    items: {
                      type: "object",
                      properties: {
                        name: { type: "string" },
                        description: { type: "string" },
                        parameters: { type: "object" },
                      },
                    },
                  },
                },
              },
            },
          },
        },
      },
    },
  };

  // Generate paths for each tool
  for (const tool of registry.getAll()) {
    const inputSchema = zodToJsonSchema(tool.inputSchema);
    const schemaName = `${tool.name}Input`;
    schemas[schemaName] = inputSchema;

    // Generic tool endpoint
    paths[`/v1/tools/${tool.name}`] = {
      post: {
        summary: tool.description,
        description: tool.description,
        operationId: tool.name,
        requestBody: {
          required: true,
          content: {
            "application/json": {
              schema: { $ref: `#/components/schemas/${schemaName}` },
            },
          },
        },
        responses: {
          "200": {
            description: "Successful response",
            content: {
              "application/json": {
                schema: {
                  type: "object",
                  properties: {
                    data: { type: "object" },
                    metadata: {
                      type: "object",
                      properties: {
                        executionTimeMs: { type: "number" },
                      },
                    },
                  },
                },
              },
            },
          },
          "400": {
            description: "Bad request or validation error",
            content: {
              "application/json": {
                schema: {
                  type: "object",
                  properties: {
                    error: { type: "string" },
                  },
                },
              },
            },
          },
          "404": {
            description: "Tool not found",
          },
        },
      },
    };
  }

  // Add convenience endpoints
  paths["/v1/search/code"] = {
    post: {
      summary: "Search Hytale code",
      description: "Semantic search over the decompiled Hytale codebase",
      operationId: "searchCode",
      requestBody: {
        required: true,
        content: {
          "application/json": {
            schema: { $ref: "#/components/schemas/search_hytale_codeInput" },
          },
        },
      },
      responses: {
        "200": {
          description: "Search results",
          content: {
            "application/json": {
              schema: {
                type: "object",
                properties: {
                  data: {
                    type: "array",
                    items: { $ref: "#/components/schemas/CodeSearchResult" },
                  },
                  metadata: { type: "object" },
                },
              },
            },
          },
        },
      },
    },
  };

  paths["/v1/search/gamedata"] = {
    post: {
      summary: "Search Hytale game data",
      description: "Semantic search over Hytale game data (items, recipes, NPCs, etc.)",
      operationId: "searchGameData",
      requestBody: {
        required: true,
        content: {
          "application/json": {
            schema: { $ref: "#/components/schemas/search_hytale_gamedataInput" },
          },
        },
      },
      responses: {
        "200": {
          description: "Search results",
          content: {
            "application/json": {
              schema: {
                type: "object",
                properties: {
                  data: {
                    type: "array",
                    items: { $ref: "#/components/schemas/GameDataSearchResult" },
                  },
                  metadata: { type: "object" },
                },
              },
            },
          },
        },
      },
    },
  };

  paths["/v1/stats/code"] = {
    get: {
      summary: "Get code statistics",
      description: "Get statistics about the indexed Hytale codebase",
      operationId: "getCodeStats",
      responses: {
        "200": {
          description: "Code statistics",
          content: {
            "application/json": {
              schema: { $ref: "#/components/schemas/CodeStats" },
            },
          },
        },
      },
    },
  };

  paths["/v1/stats/gamedata"] = {
    get: {
      summary: "Get game data statistics",
      description: "Get statistics about the indexed Hytale game data",
      operationId: "getGameDataStats",
      responses: {
        "200": {
          description: "Game data statistics",
          content: {
            "application/json": {
              schema: { $ref: "#/components/schemas/GameDataStats" },
            },
          },
        },
      },
    },
  };

  // Add result schemas
  schemas["CodeSearchResult"] = {
    type: "object",
    properties: {
      id: { type: "string" },
      className: { type: "string" },
      packageName: { type: "string" },
      methodName: { type: "string" },
      methodSignature: { type: "string" },
      content: { type: "string" },
      filePath: { type: "string" },
      lineStart: { type: "integer" },
      lineEnd: { type: "integer" },
      score: { type: "number" },
    },
  };

  schemas["GameDataSearchResult"] = {
    type: "object",
    properties: {
      id: { type: "string" },
      type: { type: "string" },
      name: { type: "string" },
      filePath: { type: "string" },
      rawJson: { type: "string" },
      category: { type: "string" },
      tags: { type: "array", items: { type: "string" } },
      parentId: { type: "string" },
      score: { type: "number" },
    },
  };

  schemas["CodeStats"] = {
    type: "object",
    properties: {
      totalMethods: { type: "integer" },
      uniqueClasses: { type: "integer" },
      uniquePackages: { type: "integer" },
    },
  };

  schemas["GameDataStats"] = {
    type: "object",
    properties: {
      totalItems: { type: "integer" },
      byType: {
        type: "object",
        additionalProperties: { type: "integer" },
      },
    },
  };

  const spec: OpenAPISpec = {
    openapi: "3.0.0",
    info: {
      title: "Hytale RAG API",
      description:
        "Semantic search API for the decompiled Hytale codebase and game data. " +
        "Use this API to search for code patterns, game items, recipes, NPCs, and more.",
      version: "2.0.0",
    },
    servers: [
      {
        url: `http://${config.server.host}:${config.server.port}`,
        description: "Local development server",
      },
    ],
    paths,
    components: {
      schemas,
    },
  };

  // Add security if auth is enabled
  if (config.api.auth.enabled) {
    spec.components.securitySchemes = {
      bearerAuth: {
        type: "http",
        scheme: "bearer",
        description: "API key authentication",
      },
    };
    spec.security = [{ bearerAuth: [] }];
  }

  return spec;
}
