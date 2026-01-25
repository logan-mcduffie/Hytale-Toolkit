import * as fs from "fs";
import { join, resolve } from "path";

/**
 * Resolve a relative path (stored in DB) to an absolute path on the current system.
 * Checks common locations for the decompiled code.
 */
export function resolveCodePath(relativePath: string): string {
  // Try to find the file in common locations
  const candidates = [
    process.cwd(),
    join(process.cwd(), "decompiled"),
    join(process.cwd(), "..", "decompiled"),
    process.env.HYTALE_DECOMPILED_DIR
  ];

  for (const base of candidates) {
    if (!base) continue;
    const fullPath = resolve(base, relativePath);
    if (fs.existsSync(fullPath)) {
      return fullPath;
    }
  }

  // If not found, just return the relative path
  return relativePath;
}

/**
 * Resolve a relative path for Client UI files.
 */
export function resolveClientDataPath(relativePath: string): string {
  const candidates = [
    process.env.HYTALE_CLIENT_DATA_DIR,
    join(process.cwd(), "Client", "Data"),
    join(process.cwd(), "..", "Client", "Data"),
    join(process.cwd(), "client-data"),
    join(process.cwd(), "..", "client-data")
  ];

  for (const base of candidates) {
    if (!base) continue;
    const fullPath = resolve(base, relativePath);
    if (fs.existsSync(fullPath)) {
      return fullPath;
    }
  }

  return relativePath;
}

/**
 * Resolve a relative path for Game Data files (assets).
 * These are usually inside Assets.zip, but might be extracted.
 */
export function resolveGameDataPath(relativePath: string): string {
  const candidates = [
    process.env.HYTALE_ASSETS_DIR,
    join(process.cwd(), "extracted-assets"),
    join(process.cwd(), "..", "extracted-assets"),
    join(process.cwd(), "assets"),
    join(process.cwd(), "..", "assets")
  ];

  for (const base of candidates) {
    if (!base) continue;
    const fullPath = resolve(base, relativePath);
    if (fs.existsSync(fullPath)) {
      return fullPath;
    }
  }

  // If not found on disk, indicate it's inside Assets.zip
  return `Assets.zip//${relativePath}`;
}
