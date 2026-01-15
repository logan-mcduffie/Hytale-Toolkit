/**
 * Shared types for Hytale game data indexing.
 */

// Document types based on file path within Assets.zip
export type GameDataType =
  | "item"           // Server/Item/Items/**
  | "recipe"         // Server/Item/Recipes/**
  | "block"          // Server/Item/Block/**
  | "interaction"    // Server/Item/Interactions/** + RootInteractions/**
  | "drop"           // Server/Drops/**
  | "npc"            // Server/NPC/Roles/**
  | "npc_group"      // Server/NPC/Groups/**
  | "npc_ai"         // Server/NPC/DecisionMaking/**
  | "entity"         // Server/Entity/**
  | "projectile"     // Server/Projectiles/**
  | "farming"        // Server/Farming/**
  | "shop"           // Server/BarterShops/**
  | "environment"    // Server/Environments/**
  | "weather"        // Server/Weathers/**
  | "biome"          // Server/HytaleGenerator/Biomes/**
  | "worldgen"       // Server/HytaleGenerator/Assignments/**
  | "camera"         // Server/Camera/**
  | "objective"      // Server/Objective/**
  | "gameplay"       // Server/GameplayConfigs/**
  | "localization"   // Common/Languages/**
  | "zone"           // Server/World/**/Zones/**/*.json (tiles, customs, zones)
  | "terrain_layer"  // Server/World/**/Zones/**/Layers/**
  | "cave"           // Server/World/**/Zones/**/Cave/**
  | "prefab";        // Server/Prefabs/**

// A chunk of game data ready for embedding
export interface GameDataChunk {
  id: string;                    // e.g., "item:Soil_Clay_Smooth_Blue"
  type: GameDataType;
  name: string;                  // Human-readable name or item ID
  filePath: string;              // Path within Assets.zip

  // Raw content
  rawJson: string;               // Original JSON content (for display)

  // Searchable metadata
  category?: string;
  tags?: string[];
  parentId?: string;             // For inheritance (Parent field)
  relatedIds?: string[];         // Referenced items, NPCs, etc.

  // For embedding
  textForEmbedding: string;
}

// A chunk with its embedding vector
export interface EmbeddedGameDataChunk extends GameDataChunk {
  vector: number[];
}

// Search result returned from the database
export interface GameDataSearchResult {
  id: string;
  type: GameDataType;
  name: string;
  filePath: string;
  rawJson: string;
  category?: string;
  tags: string[];
  parentId?: string;
  score: number;
}

// Stats about indexed game data
export interface GameDataStats {
  totalItems: number;
  byType: Record<GameDataType, number>;
}
