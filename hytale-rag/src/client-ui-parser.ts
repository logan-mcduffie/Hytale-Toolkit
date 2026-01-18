/**
 * Client UI Parser
 *
 * Parses Hytale client UI files (.xaml, .ui, .json) for indexing.
 * These files define the visual appearance and layout of the game UI.
 */

import * as fs from "fs";
import * as path from "path";
import * as crypto from "crypto";

/**
 * Types of client UI content
 */
export type ClientUIType =
  | "xaml"          // Noesis GUI XAML templates
  | "ui"            // Hytale's custom .ui DSL
  | "node_schema"   // NodeEditor node definitions
  | "config";       // JSON configuration files

/**
 * A chunk of client UI content ready for embedding
 */
export interface ClientUIChunk {
  id: string;                    // e.g., "xaml:DesignSystem/Button"
  type: ClientUIType;
  name: string;                  // Human-readable name
  filePath: string;              // Full path to the file
  relativePath: string;          // Path relative to Client/Data
  fileHash: string;              // SHA-256 hash for incremental indexing
  content: string;               // Raw file content
  category?: string;             // e.g., "DesignSystem", "InGame", "MainMenu"
  textForEmbedding: string;      // Text optimized for semantic search
}

/**
 * Result from parsing client UI files
 */
export interface ClientUIParseResult {
  chunks: ClientUIChunk[];
  errors: string[];
}

/**
 * Determine the UI type from file extension and path
 */
function getUIType(filePath: string): ClientUIType | null {
  const ext = path.extname(filePath).toLowerCase();

  if (ext === ".xaml") return "xaml";
  if (ext === ".ui") return "ui";
  if (ext === ".json") {
    // NodeEditor schemas have specific structure
    if (filePath.includes("NodeEditor")) return "node_schema";
    return "config";
  }

  return null;
}

/**
 * Extract category from file path
 */
function extractCategory(relativePath: string): string | undefined {
  const parts = relativePath.split(/[/\\]/);

  // Look for meaningful category names
  for (const part of parts) {
    if (["DesignSystem", "InGame", "MainMenu", "Common", "Services",
         "GameLoading", "DevTools", "Editor", "Shared"].includes(part)) {
      return part;
    }
  }

  // Use first directory as category
  if (parts.length > 1) {
    return parts[0];
  }

  return undefined;
}

/**
 * Extract a meaningful name from the file path
 */
function extractName(filePath: string): string {
  const basename = path.basename(filePath);
  const ext = path.extname(basename);
  return basename.replace(ext, "");
}

/**
 * Build embedding text for XAML files
 */
function buildXamlEmbeddingText(chunk: ClientUIChunk): string {
  const parts = [
    `XAML UI Template: ${chunk.name}`,
    `Category: ${chunk.category || "General"}`,
    `Path: ${chunk.relativePath}`,
    "",
    "This is a Noesis GUI XAML template that defines UI styling and layout.",
    "",
    chunk.content,
  ];
  return parts.join("\n");
}

/**
 * Build embedding text for .ui files (Hytale's custom DSL)
 */
function buildUIEmbeddingText(chunk: ClientUIChunk): string {
  const parts = [
    `Hytale UI Component: ${chunk.name}`,
    `Category: ${chunk.category || "General"}`,
    `Path: ${chunk.relativePath}`,
    "",
    "This is a Hytale .ui file that defines UI component layout and behavior.",
    "",
    chunk.content,
  ];
  return parts.join("\n");
}

/**
 * Build embedding text for NodeEditor schemas
 */
function buildNodeSchemaEmbeddingText(chunk: ClientUIChunk): string {
  let description = "NodeEditor node definition";

  // Try to extract node info from JSON
  try {
    const json = JSON.parse(chunk.content);
    if (json.Title) {
      description = `NodeEditor node: ${json.Title}`;
    }
    if (json.Id) {
      description += ` (${json.Id})`;
    }
  } catch {
    // Not valid JSON, use default description
  }

  const parts = [
    description,
    `Path: ${chunk.relativePath}`,
    "",
    "This defines a visual scripting node for the Hytale NodeEditor.",
    "",
    chunk.content,
  ];
  return parts.join("\n");
}

/**
 * Build embedding text for config files
 */
function buildConfigEmbeddingText(chunk: ClientUIChunk): string {
  const parts = [
    `Client Config: ${chunk.name}`,
    `Category: ${chunk.category || "General"}`,
    `Path: ${chunk.relativePath}`,
    "",
    chunk.content,
  ];
  return parts.join("\n");
}

/**
 * Build embedding text based on file type
 */
function buildEmbeddingText(chunk: ClientUIChunk): string {
  switch (chunk.type) {
    case "xaml":
      return buildXamlEmbeddingText(chunk);
    case "ui":
      return buildUIEmbeddingText(chunk);
    case "node_schema":
      return buildNodeSchemaEmbeddingText(chunk);
    case "config":
      return buildConfigEmbeddingText(chunk);
    default:
      return chunk.content;
  }
}

/**
 * Parse a single UI file
 */
function parseUIFile(
  filePath: string,
  basePath: string
): ClientUIChunk | null {
  const type = getUIType(filePath);
  if (!type) return null;

  let content: string;
  try {
    content = fs.readFileSync(filePath, "utf-8");
  } catch {
    return null;
  }

  // Skip empty files
  if (content.trim().length === 0) return null;

  const relativePath = path.relative(basePath, filePath).replace(/\\/g, "/");
  const name = extractName(filePath);
  const category = extractCategory(relativePath);
  const fileHash = crypto.createHash("sha256").update(content).digest("hex");

  const chunk: ClientUIChunk = {
    id: `${type}:${relativePath}`,
    type,
    name,
    filePath,
    relativePath,
    fileHash,
    content,
    category,
    textForEmbedding: "", // Will be set below
  };

  chunk.textForEmbedding = buildEmbeddingText(chunk);

  return chunk;
}

/**
 * Recursively collect all UI files from a directory
 */
function collectUIFiles(dir: string): string[] {
  const files: string[] = [];

  function walk(currentDir: string) {
    let entries: fs.Dirent[];
    try {
      entries = fs.readdirSync(currentDir, { withFileTypes: true });
    } catch {
      return;
    }

    for (const entry of entries) {
      const fullPath = path.join(currentDir, entry.name);

      if (entry.isDirectory()) {
        walk(fullPath);
      } else if (entry.isFile()) {
        const ext = path.extname(entry.name).toLowerCase();
        if ([".xaml", ".ui", ".json"].includes(ext)) {
          files.push(fullPath);
        }
      }
    }
  }

  walk(dir);
  return files;
}

/**
 * Parse all client UI files from the Client/Data directory
 */
export async function parseClientUIDirectory(
  clientDataPath: string,
  onProgress?: (current: number, total: number, file: string) => void
): Promise<ClientUIParseResult> {
  const chunks: ClientUIChunk[] = [];
  const errors: string[] = [];

  // Collect all UI files
  const files = collectUIFiles(clientDataPath);

  // Parse each file
  for (let i = 0; i < files.length; i++) {
    const file = files[i];

    if (onProgress) {
      onProgress(i + 1, files.length, file);
    }

    try {
      const chunk = parseUIFile(file, clientDataPath);
      if (chunk) {
        chunks.push(chunk);
      }
    } catch (e: any) {
      errors.push(`Error parsing ${file}: ${e.message}`);
    }
  }

  return { chunks, errors };
}

/**
 * Get statistics about the client UI files
 */
export async function getClientUIStats(clientDataPath: string): Promise<{
  totalFiles: number;
  xamlFiles: number;
  uiFiles: number;
  jsonFiles: number;
  byCategory: Record<string, number>;
}> {
  const files = collectUIFiles(clientDataPath);

  const stats = {
    totalFiles: files.length,
    xamlFiles: 0,
    uiFiles: 0,
    jsonFiles: 0,
    byCategory: {} as Record<string, number>,
  };

  for (const file of files) {
    const ext = path.extname(file).toLowerCase();
    const relativePath = path.relative(clientDataPath, file);
    const category = extractCategory(relativePath) || "Other";

    if (ext === ".xaml") stats.xamlFiles++;
    else if (ext === ".ui") stats.uiFiles++;
    else if (ext === ".json") stats.jsonFiles++;

    stats.byCategory[category] = (stats.byCategory[category] || 0) + 1;
  }

  return stats;
}
