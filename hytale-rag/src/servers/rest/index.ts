/**
 * REST API Server
 *
 * Exposes tools via a REST API for any HTTP client.
 */

import express, { Express, Request, Response, NextFunction } from "express";
import cors from "cors";
import { ToolRegistry, ToolContext, zodToJsonSchema } from "../../core/tools/index.js";
import type { AppConfig } from "../../config/schema.js";
import { generateOpenAPISpec } from "./openapi.js";

/**
 * Rate limiter middleware (simple in-memory implementation)
 */
function createRateLimiter(windowMs: number, maxRequests: number) {
  const requests = new Map<string, { count: number; resetTime: number }>();

  return (req: Request, res: Response, next: NextFunction) => {
    const ip = req.ip || req.socket.remoteAddress || "unknown";
    const now = Date.now();

    let record = requests.get(ip);
    if (!record || record.resetTime < now) {
      record = { count: 0, resetTime: now + windowMs };
      requests.set(ip, record);
    }

    record.count++;

    if (record.count > maxRequests) {
      res.status(429).json({
        error: "Too many requests",
        retryAfter: Math.ceil((record.resetTime - now) / 1000),
      });
      return;
    }

    next();
  };
}

/**
 * API key authentication middleware
 */
function createAuthMiddleware(apiKeys: string[]) {
  return (req: Request, res: Response, next: NextFunction) => {
    const authHeader = req.headers.authorization;

    if (!authHeader) {
      res.status(401).json({ error: "Authorization header required" });
      return;
    }

    const [scheme, key] = authHeader.split(" ");

    if (scheme.toLowerCase() !== "bearer" || !key) {
      res.status(401).json({ error: "Invalid authorization format. Use: Bearer <api-key>" });
      return;
    }

    if (!apiKeys.includes(key)) {
      res.status(403).json({ error: "Invalid API key" });
      return;
    }

    next();
  };
}

/**
 * Create and configure the REST API Express app
 *
 * @param registry - Tool registry with registered tools
 * @param context - Tool execution context
 * @param config - Application configuration
 * @returns Configured Express app
 */
export function createRESTServer(
  registry: ToolRegistry,
  context: ToolContext,
  config: AppConfig
): Express {
  const app = express();

  // Middleware
  app.use(cors());
  app.use(express.json());

  // Rate limiting
  if (config.api.rateLimit) {
    app.use(
      createRateLimiter(config.api.rateLimit.windowMs, config.api.rateLimit.max)
    );
  }

  // Authentication (if enabled)
  if (config.api.auth.enabled && config.api.auth.apiKeys.length > 0) {
    // Skip auth for health and docs endpoints
    app.use((req, res, next) => {
      if (req.path === "/health" || req.path === "/openapi.json") {
        return next();
      }
      return createAuthMiddleware(config.api.auth.apiKeys)(req, res, next);
    });
  }

  // Health check endpoint
  app.get("/health", async (_req, res) => {
    try {
      const healthy = await context.vectorStore.healthCheck();
      res.status(healthy ? 200 : 503).json({
        status: healthy ? "ok" : "unhealthy",
        timestamp: new Date().toISOString(),
      });
    } catch (error) {
      res.status(503).json({
        status: "unhealthy",
        error: error instanceof Error ? error.message : "Unknown error",
      });
    }
  });

  // OpenAPI specification endpoint
  app.get("/openapi.json", (_req, res) => {
    res.json(generateOpenAPISpec(registry, config));
  });

  // List available tools
  app.get("/v1/tools", (_req, res) => {
    const tools = registry.getAll().map((tool) => ({
      name: tool.name,
      description: tool.description,
      parameters: zodToJsonSchema(tool.inputSchema),
    }));
    res.json({ tools });
  });

  // Generic tool execution endpoint
  app.post("/v1/tools/:toolName", async (req, res) => {
    const { toolName } = req.params;
    const tool = registry.get(toolName);

    if (!tool) {
      res.status(404).json({ error: `Tool '${toolName}' not found` });
      return;
    }

    const result = await registry.execute(toolName, req.body, context);

    if (!result.success) {
      res.status(400).json({
        error: result.error,
        metadata: result.metadata,
      });
      return;
    }

    res.json({
      data: result.data,
      metadata: result.metadata,
    });
  });

  // Convenience endpoints for common operations
  app.post("/v1/search/code", async (req, res) => {
    const result = await registry.execute("search_hytale_code", req.body, context);

    if (!result.success) {
      res.status(400).json({ error: result.error, metadata: result.metadata });
      return;
    }

    res.json({ data: result.data, metadata: result.metadata });
  });

  app.post("/v1/search/gamedata", async (req, res) => {
    const result = await registry.execute("search_hytale_gamedata", req.body, context);

    if (!result.success) {
      res.status(400).json({ error: result.error, metadata: result.metadata });
      return;
    }

    res.json({ data: result.data, metadata: result.metadata });
  });

  app.get("/v1/stats/code", async (_req, res) => {
    const result = await registry.execute("hytale_code_stats", {}, context);

    if (!result.success) {
      res.status(500).json({ error: result.error, metadata: result.metadata });
      return;
    }

    res.json({ data: result.data, metadata: result.metadata });
  });

  app.get("/v1/stats/gamedata", async (_req, res) => {
    const result = await registry.execute("hytale_gamedata_stats", {}, context);

    if (!result.success) {
      res.status(500).json({ error: result.error, metadata: result.metadata });
      return;
    }

    res.json({ data: result.data, metadata: result.metadata });
  });

  // Error handling middleware
  app.use((err: Error, _req: Request, res: Response, _next: NextFunction) => {
    console.error("Server error:", err);
    res.status(500).json({
      error: "Internal server error",
      message: err.message,
    });
  });

  return app;
}

/**
 * Start the REST API server
 *
 * @param app - Express app
 * @param config - Application configuration
 */
export function startRESTServer(app: Express, config: AppConfig): void {
  const { host, port } = config.server;

  app.listen(port, host, () => {
    console.log(`REST API listening on http://${host}:${port}`);
    console.log(`  Health check: http://${host}:${port}/health`);
    console.log(`  OpenAPI spec: http://${host}:${port}/openapi.json`);
    console.log(`  Search code:  POST http://${host}:${port}/v1/search/code`);
    console.log(`  Search data:  POST http://${host}:${port}/v1/search/gamedata`);
  });
}
