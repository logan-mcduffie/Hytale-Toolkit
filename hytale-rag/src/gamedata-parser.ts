/**
 * Parser for Hytale game data from Assets.zip.
 * Extracts and categorizes JSON files, building natural language representations.
 * Uses yauzl for streaming to handle large (3GB+) zip files.
 */

import yauzl from "yauzl";
import { GameDataChunk, GameDataType } from "./types.js";

// Path patterns for classifying game data types
const PATH_TYPE_MAP: Array<[RegExp, GameDataType]> = [
  [/^Server\/Item\/Items\//, "item"],
  [/^Server\/Item\/Recipes\//, "recipe"],
  [/^Server\/Item\/Block\//, "block"],
  [/^Server\/Item\/Interactions\//, "interaction"],
  [/^Server\/Item\/RootInteractions\//, "interaction"],
  [/^Server\/Item\/Groups\//, "item"],
  [/^Server\/Item\/Category\//, "item"],
  [/^Server\/Item\/ResourceTypes\//, "item"],
  [/^Server\/Item\/Qualities\//, "item"],
  [/^Server\/Drops\//, "drop"],
  [/^Server\/NPC\/Roles\//, "npc"],
  [/^Server\/NPC\/Groups\//, "npc_group"],
  [/^Server\/NPC\/DecisionMaking\//, "npc_ai"],
  [/^Server\/NPC\/Flocks\//, "npc"],
  [/^Server\/NPC\/Attitude\//, "npc"],
  [/^Server\/Entity\//, "entity"],
  [/^Server\/Projectiles\//, "projectile"],
  [/^Server\/Farming\//, "farming"],
  [/^Server\/BarterShops\//, "shop"],
  [/^Server\/Environments\//, "environment"],
  [/^Server\/Weathers\//, "weather"],
  [/^Server\/HytaleGenerator\/Biomes\//, "biome"],
  [/^Server\/HytaleGenerator\/Assignments\//, "worldgen"],
  [/^Server\/Camera\//, "camera"],
  [/^Server\/Objective\//, "objective"],
  [/^Server\/GameplayConfigs\//, "gameplay"],
  [/^Common\/Languages\//, "localization"],
];

/**
 * Classifies a file path to a GameDataType.
 */
function classifyPath(filePath: string): GameDataType | null {
  // Normalize path separators
  const normalized = filePath.replace(/\\/g, "/");

  for (const [pattern, type] of PATH_TYPE_MAP) {
    if (pattern.test(normalized)) {
      return type;
    }
  }

  return null;
}

/**
 * Extracts the item/entity name from the file path.
 */
function extractName(filePath: string): string {
  const parts = filePath.replace(/\\/g, "/").split("/");
  const fileName = parts[parts.length - 1];
  return fileName.replace(/\.json$/i, "");
}

/**
 * Extract related IDs from JSON content (items, NPCs, etc referenced by this file).
 */
function extractRelatedIds(json: any): string[] {
  const ids: Set<string> = new Set();

  function traverse(obj: any) {
    if (obj === null || obj === undefined) return;

    if (typeof obj === "string") {
      // Look for item/entity references (commonly PascalCase with underscores)
      if (/^[A-Z][a-zA-Z0-9_]+$/.test(obj) && obj.length > 3) {
        ids.add(obj);
      }
    } else if (Array.isArray(obj)) {
      obj.forEach(traverse);
    } else if (typeof obj === "object") {
      Object.values(obj).forEach(traverse);
    }
  }

  traverse(json);
  return Array.from(ids);
}

/**
 * Extract tags from JSON (commonly in Tags, Type, Family fields).
 */
function extractTags(json: any): string[] {
  const tags: string[] = [];

  if (json.Tags && Array.isArray(json.Tags)) {
    tags.push(...json.Tags);
  }
  if (json.Type && typeof json.Type === "string") {
    tags.push(json.Type);
  }
  if (json.Family && typeof json.Family === "string") {
    tags.push(json.Family);
  }
  if (json.Category && typeof json.Category === "string") {
    tags.push(json.Category);
  }

  return [...new Set(tags)];
}

/**
 * Build natural language text for embedding based on data type.
 */
function buildTextForEmbedding(
  chunk: GameDataChunk,
  json: any
): string {
  switch (chunk.type) {
    case "item":
      return buildItemText(chunk, json);
    case "recipe":
      return buildRecipeText(chunk, json);
    case "block":
      return buildBlockText(chunk, json);
    case "drop":
      return buildDropText(chunk, json);
    case "npc":
      return buildNpcText(chunk, json);
    case "npc_group":
      return buildNpcGroupText(chunk, json);
    case "npc_ai":
      return buildNpcAiText(chunk, json);
    case "entity":
      return buildEntityText(chunk, json);
    case "farming":
      return buildFarmingText(chunk, json);
    case "shop":
      return buildShopText(chunk, json);
    case "biome":
      return buildBiomeText(chunk, json);
    case "interaction":
      return buildInteractionText(chunk, json);
    case "localization":
      return buildLocalizationText(chunk, json);
    default:
      return buildGenericText(chunk, json);
  }
}

function buildItemText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Item: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.Type) lines.push(`Type: ${json.Type}`);
  if (json.Family) lines.push(`Family: ${json.Family}`);
  if (json.Category) lines.push(`Category: ${json.Category}`);

  // Stats
  if (json.Stats) {
    lines.push("\nStats:");
    for (const [key, value] of Object.entries(json.Stats)) {
      lines.push(`  ${key}: ${value}`);
    }
  }

  // Recipe (embedded)
  if (json.Recipe) {
    lines.push("\nRecipe:");
    if (json.Recipe.Bench) lines.push(`  Bench: ${json.Recipe.Bench}`);
    if (json.Recipe.Inputs) {
      lines.push("  Inputs:");
      for (const input of json.Recipe.Inputs) {
        const count = input.Count || 1;
        const item = input.Item || input.Id || "unknown";
        lines.push(`    ${count}x ${item}`);
      }
    }
    if (json.Recipe.Output) {
      const count = json.Recipe.Output.Count || 1;
      lines.push(`  Output: ${count}x ${chunk.name}`);
    }
  }

  // Tags
  if (json.Tags && json.Tags.length > 0) {
    lines.push(`\nTags: ${json.Tags.join(", ")}`);
  }

  // Block properties
  if (json.Block) {
    lines.push("\nBlock Properties:");
    if (json.Block.Material) lines.push(`  Material: ${json.Block.Material}`);
    if (json.Block.DrawType) lines.push(`  DrawType: ${json.Block.DrawType}`);
    if (json.Block.SoundSet) lines.push(`  Sounds: ${json.Block.SoundSet}`);
  }

  return lines.join("\n");
}

function buildRecipeText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Recipe: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.Bench) lines.push(`Crafting Bench: ${json.Bench}`);
  if (json.Category) lines.push(`Category: ${json.Category}`);

  if (json.Inputs && Array.isArray(json.Inputs)) {
    lines.push("\nIngredients:");
    for (const input of json.Inputs) {
      const count = input.Count || 1;
      const item = input.Item || input.Id || "unknown";
      lines.push(`  ${count}x ${item}`);
    }
  }

  if (json.Output) {
    const count = json.Output.Count || 1;
    const item = json.Output.Item || json.Output.Id || chunk.name;
    lines.push(`\nOutput: ${count}x ${item}`);
  }

  return lines.join("\n");
}

function buildBlockText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Block: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.Material) lines.push(`Material: ${json.Material}`);
  if (json.DrawType) lines.push(`Draw Type: ${json.DrawType}`);
  if (json.SoundSet) lines.push(`Sound Set: ${json.SoundSet}`);
  if (json.Hardness) lines.push(`Hardness: ${json.Hardness}`);
  if (json.BlastResistance) lines.push(`Blast Resistance: ${json.BlastResistance}`);

  if (json.Drops) {
    lines.push("\nDrops:");
    if (Array.isArray(json.Drops)) {
      for (const drop of json.Drops) {
        const item = drop.Item || drop.Id || "unknown";
        const count = drop.Count || 1;
        lines.push(`  ${count}x ${item}`);
      }
    }
  }

  return lines.join("\n");
}

function buildDropText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Drop Table: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);

  if (json.Entries && Array.isArray(json.Entries)) {
    lines.push("\nDrop Entries:");
    for (const entry of json.Entries) {
      const item = entry.Item || entry.Id || "unknown";
      const min = entry.Min || entry.Count || 1;
      const max = entry.Max || min;
      const chance = entry.Chance || entry.Weight || 100;
      lines.push(`  ${item}: ${min}-${max} (${chance}% chance)`);
    }
  }

  if (json.Items && Array.isArray(json.Items)) {
    lines.push("\nDropped Items:");
    for (const item of json.Items) {
      if (typeof item === "string") {
        lines.push(`  ${item}`);
      } else {
        const name = item.Item || item.Id || "unknown";
        const count = item.Count || 1;
        lines.push(`  ${count}x ${name}`);
      }
    }
  }

  return lines.join("\n");
}

function buildNpcText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`NPC: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.DisplayName) lines.push(`Display Name: ${json.DisplayName}`);
  if (json.Type) lines.push(`Type: ${json.Type}`);
  if (json.Faction) lines.push(`Faction: ${json.Faction}`);

  // Stats
  if (json.Stats) {
    lines.push("\nStats:");
    for (const [key, value] of Object.entries(json.Stats)) {
      lines.push(`  ${key}: ${value}`);
    }
  }

  // Health
  if (json.Health || json.MaxHealth) {
    lines.push(`Health: ${json.Health || json.MaxHealth}`);
  }

  // Behavior
  if (json.Behavior || json.AI) {
    lines.push(`\nBehavior: ${json.Behavior || json.AI}`);
  }

  // Drops
  if (json.Drops) {
    lines.push(`Drops: ${json.Drops}`);
  }

  // Spawning
  if (json.SpawnWeight) lines.push(`Spawn Weight: ${json.SpawnWeight}`);
  if (json.Biomes) lines.push(`Biomes: ${json.Biomes.join(", ")}`);

  return lines.join("\n");
}

function buildNpcGroupText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`NPC Group: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);

  if (json.Members && Array.isArray(json.Members)) {
    lines.push("\nMembers:");
    for (const member of json.Members) {
      if (typeof member === "string") {
        lines.push(`  ${member}`);
      } else {
        lines.push(`  ${member.Role || member.Id || "unknown"}`);
      }
    }
  }

  if (json.SpawnRules) {
    lines.push("\nSpawn Rules:");
    lines.push(`  ${JSON.stringify(json.SpawnRules)}`);
  }

  return lines.join("\n");
}

function buildNpcAiText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`NPC AI/Decision: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.Type) lines.push(`Type: ${json.Type}`);

  if (json.Conditions && Array.isArray(json.Conditions)) {
    lines.push("\nConditions:");
    for (const cond of json.Conditions) {
      lines.push(`  ${JSON.stringify(cond)}`);
    }
  }

  if (json.Actions && Array.isArray(json.Actions)) {
    lines.push("\nActions:");
    for (const action of json.Actions) {
      lines.push(`  ${JSON.stringify(action)}`);
    }
  }

  return lines.join("\n");
}

function buildEntityText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Entity: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.Type) lines.push(`Type: ${json.Type}`);

  // Components
  if (json.Components && typeof json.Components === "object") {
    lines.push("\nComponents:");
    for (const [name, config] of Object.entries(json.Components)) {
      lines.push(`  ${name}`);
    }
  }

  return lines.join("\n");
}

function buildFarmingText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Farming: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.Crop) lines.push(`Crop: ${json.Crop}`);
  if (json.GrowthTime) lines.push(`Growth Time: ${json.GrowthTime}`);
  if (json.Stages) lines.push(`Growth Stages: ${json.Stages}`);

  if (json.Yield) {
    lines.push("\nYield:");
    if (Array.isArray(json.Yield)) {
      for (const y of json.Yield) {
        const item = y.Item || y.Id || "unknown";
        const count = y.Count || 1;
        lines.push(`  ${count}x ${item}`);
      }
    }
  }

  if (json.RequiredSoil) lines.push(`Required Soil: ${json.RequiredSoil}`);
  if (json.WaterNeeded !== undefined) lines.push(`Water Needed: ${json.WaterNeeded}`);

  return lines.join("\n");
}

function buildShopText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Shop: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.Merchant) lines.push(`Merchant: ${json.Merchant}`);

  if (json.Items && Array.isArray(json.Items)) {
    lines.push("\nItems for Sale:");
    for (const item of json.Items) {
      const name = item.Item || item.Id || "unknown";
      const price = item.Price || item.Cost || "?";
      lines.push(`  ${name}: ${price}`);
    }
  }

  if (json.Trades && Array.isArray(json.Trades)) {
    lines.push("\nTrades:");
    for (const trade of json.Trades) {
      const gives = trade.Gives || trade.Output || "?";
      const wants = trade.Wants || trade.Input || "?";
      lines.push(`  ${wants} -> ${gives}`);
    }
  }

  return lines.join("\n");
}

function buildBiomeText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Biome: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.Temperature) lines.push(`Temperature: ${json.Temperature}`);
  if (json.Humidity) lines.push(`Humidity: ${json.Humidity}`);
  if (json.Terrain) lines.push(`Terrain: ${json.Terrain}`);

  if (json.Blocks) {
    lines.push("\nBlocks:");
    for (const [layer, block] of Object.entries(json.Blocks)) {
      lines.push(`  ${layer}: ${block}`);
    }
  }

  if (json.Features && Array.isArray(json.Features)) {
    lines.push("\nFeatures:");
    for (const feature of json.Features) {
      if (typeof feature === "string") {
        lines.push(`  ${feature}`);
      } else {
        lines.push(`  ${feature.Type || feature.Id || "unknown"}`);
      }
    }
  }

  if (json.Spawns && Array.isArray(json.Spawns)) {
    lines.push("\nSpawns:");
    for (const spawn of json.Spawns) {
      if (typeof spawn === "string") {
        lines.push(`  ${spawn}`);
      } else {
        lines.push(`  ${spawn.Entity || spawn.NPC || "unknown"}`);
      }
    }
  }

  return lines.join("\n");
}

function buildInteractionText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Interaction: ${chunk.name}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);
  if (json.Type) lines.push(`Type: ${json.Type}`);
  if (json.Target) lines.push(`Target: ${json.Target}`);

  if (json.Actions && Array.isArray(json.Actions)) {
    lines.push("\nActions:");
    for (const action of json.Actions) {
      if (typeof action === "string") {
        lines.push(`  ${action}`);
      } else {
        lines.push(`  ${action.Type || action.Action || JSON.stringify(action)}`);
      }
    }
  }

  if (json.Conditions && Array.isArray(json.Conditions)) {
    lines.push("\nConditions:");
    for (const cond of json.Conditions) {
      lines.push(`  ${JSON.stringify(cond)}`);
    }
  }

  return lines.join("\n");
}

function buildLocalizationText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`Localization: ${chunk.name}`);

  // Sample some keys
  const entries = Object.entries(json);
  const sampleSize = Math.min(20, entries.length);

  if (entries.length > 0) {
    lines.push(`\nTotal entries: ${entries.length}`);
    lines.push("\nSample entries:");
    for (let i = 0; i < sampleSize; i++) {
      const [key, value] = entries[i];
      lines.push(`  ${key}: ${value}`);
    }
  }

  return lines.join("\n");
}

function buildGenericText(chunk: GameDataChunk, json: any): string {
  const lines: string[] = [];

  lines.push(`${chunk.type.toUpperCase()}: ${chunk.name}`);
  lines.push(`Path: ${chunk.filePath}`);

  if (json.Parent) lines.push(`Parent: ${json.Parent}`);

  // Include top-level keys as summary
  const keys = Object.keys(json);
  if (keys.length > 0) {
    lines.push(`\nProperties: ${keys.join(", ")}`);
  }

  // For small objects, include the full JSON
  const jsonStr = JSON.stringify(json, null, 2);
  if (jsonStr.length < 1000) {
    lines.push(`\nContent:\n${jsonStr}`);
  }

  return lines.join("\n");
}

/**
 * Read entry content from a yauzl zip file.
 */
function readEntryContent(zipfile: yauzl.ZipFile, entry: yauzl.Entry): Promise<string> {
  return new Promise((resolve, reject) => {
    zipfile.openReadStream(entry, (err, readStream) => {
      if (err) {
        reject(err);
        return;
      }
      if (!readStream) {
        reject(new Error("No read stream"));
        return;
      }

      const chunks: Buffer[] = [];
      readStream.on("data", (chunk: Buffer) => chunks.push(chunk));
      readStream.on("end", () => {
        resolve(Buffer.concat(chunks).toString("utf8"));
      });
      readStream.on("error", reject);
    });
  });
}

/**
 * Parse Assets.zip and extract game data chunks using streaming.
 */
export async function parseAssetsZip(
  zipPath: string,
  onProgress?: (current: number, total: number, file: string) => void
): Promise<{ chunks: GameDataChunk[]; errors: string[] }> {
  const chunks: GameDataChunk[] = [];
  const errors: string[] = [];

  return new Promise((resolve, reject) => {
    // Open with lazyEntries for streaming - doesn't load entire file into memory
    yauzl.open(zipPath, { lazyEntries: true }, (err, zipfile) => {
      if (err) {
        reject(err);
        return;
      }
      if (!zipfile) {
        reject(new Error("Failed to open zip file"));
        return;
      }

      const total = zipfile.entryCount;
      let current = 0;
      let matchingCount = 0;

      // Process entries one at a time
      zipfile.on("entry", async (entry: yauzl.Entry) => {
        current++;
        const filePath = entry.fileName;

        // Skip directories
        if (/\/$/.test(filePath)) {
          zipfile.readEntry();
          return;
        }

        // Skip non-JSON files
        if (!filePath.endsWith(".json")) {
          zipfile.readEntry();
          return;
        }

        // Check if this path matches our classification
        const type = classifyPath(filePath);
        if (!type) {
          zipfile.readEntry();
          return;
        }

        matchingCount++;
        if (onProgress) {
          onProgress(matchingCount, total, filePath);
        }

        try {
          // Read and parse JSON
          const content = await readEntryContent(zipfile, entry);
          const json = JSON.parse(content);

          const name = extractName(filePath);

          // Build chunk
          const chunk: GameDataChunk = {
            id: `${type}:${name}`,
            type,
            name,
            filePath,
            rawJson: content,
            category: json.Category,
            tags: extractTags(json),
            parentId: json.Parent,
            relatedIds: extractRelatedIds(json),
            textForEmbedding: "",
          };

          // Build natural language text for embedding
          chunk.textForEmbedding = buildTextForEmbedding(chunk, json);

          chunks.push(chunk);
        } catch (err) {
          errors.push(`${filePath}: ${err instanceof Error ? err.message : String(err)}`);
        }

        // Continue to next entry
        zipfile.readEntry();
      });

      zipfile.on("end", () => {
        resolve({ chunks, errors });
      });

      zipfile.on("error", reject);

      // Start reading entries
      zipfile.readEntry();
    });
  });
}

/**
 * Get summary statistics from a zip file using streaming (handles large files).
 */
export function getZipStats(zipPath: string): Promise<{
  totalFiles: number;
  jsonFiles: number;
  matchingFiles: number;
  byType: Record<string, number>;
}> {
  return new Promise((resolve, reject) => {
    yauzl.open(zipPath, { lazyEntries: true }, (err, zipfile) => {
      if (err) {
        reject(err);
        return;
      }
      if (!zipfile) {
        reject(new Error("Failed to open zip file"));
        return;
      }

      const byType: Record<string, number> = {};
      let jsonFiles = 0;
      let matchingFiles = 0;
      const totalFiles = zipfile.entryCount;

      zipfile.on("entry", (entry: yauzl.Entry) => {
        const filePath = entry.fileName;

        // Skip directories
        if (!/\/$/.test(filePath) && filePath.endsWith(".json")) {
          jsonFiles++;

          const type = classifyPath(filePath);
          if (type) {
            matchingFiles++;
            byType[type] = (byType[type] || 0) + 1;
          }
        }

        zipfile.readEntry();
      });

      zipfile.on("end", () => {
        resolve({
          totalFiles,
          jsonFiles,
          matchingFiles,
          byType,
        });
      });

      zipfile.on("error", reject);

      // Start reading
      zipfile.readEntry();
    });
  });
}
