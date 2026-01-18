#!/usr/bin/env node
/**
 * Unified ingestion script that indexes all data for both providers in parallel.
 *
 * Runs Voyage AI (cloud) and Ollama (local) indexing simultaneously for:
 * - Server code (Java methods)
 * - Client UI (XAML/UI files)
 * - Game data (Assets.zip)
 *
 * Usage:
 *   npm run ingest-all <server-code-path> <client-data-path> <assets-zip-path>
 *
 * Environment:
 *   VOYAGE_API_KEY - Required for Voyage AI indexing
 *   OLLAMA_MODEL   - Ollama model (default: nomic-embed-text)
 *
 * Example:
 *   VOYAGE_API_KEY=xxx npm run ingest-all \
 *     /path/to/decompiled \
 *     /path/to/Client/Data \
 *     /path/to/Assets.zip
 */

import { spawn, ChildProcess } from "child_process";
import * as path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

interface IngestJob {
  name: string;
  provider: "voyage" | "ollama";
  script: string;
  args: string[];
  env: Record<string, string>;
}

interface JobResult {
  job: IngestJob;
  success: boolean;
  duration: number;
  error?: string;
}

/**
 * Run a single ingest job
 */
function runJob(job: IngestJob): Promise<JobResult> {
  return new Promise((resolve) => {
    const startTime = Date.now();

    console.log(`[${job.provider}/${job.name}] Starting...`);

    const proc = spawn("npx", ["tsx", job.script, ...job.args], {
      cwd: path.resolve(__dirname, ".."),
      env: { ...process.env, ...job.env },
      stdio: ["ignore", "pipe", "pipe"],
      shell: true,
    });

    let output = "";

    proc.stdout?.on("data", (data) => {
      output += data.toString();
    });

    proc.stderr?.on("data", (data) => {
      output += data.toString();
    });

    proc.on("close", (code) => {
      const duration = (Date.now() - startTime) / 1000;

      if (code === 0) {
        console.log(`[${job.provider}/${job.name}] Completed in ${duration.toFixed(1)}s`);
        resolve({ job, success: true, duration });
      } else {
        console.error(`[${job.provider}/${job.name}] Failed after ${duration.toFixed(1)}s`);
        resolve({ job, success: false, duration, error: output });
      }
    });

    proc.on("error", (err) => {
      const duration = (Date.now() - startTime) / 1000;
      console.error(`[${job.provider}/${job.name}] Error: ${err.message}`);
      resolve({ job, success: false, duration, error: err.message });
    });
  });
}

async function main() {
  const serverCodePath = process.argv[2];
  const clientDataPath = process.argv[3];
  const assetsZipPath = process.argv[4];

  if (!serverCodePath || !clientDataPath || !assetsZipPath) {
    console.error("Error: All three paths are required");
    console.error("");
    console.error("Usage: npm run ingest-all <server-code-path> <client-data-path> <assets-zip-path>");
    console.error("");
    console.error("Example:");
    console.error("  VOYAGE_API_KEY=xxx npm run ingest-all \\");
    console.error("    /path/to/decompiled \\");
    console.error("    /path/to/Client/Data \\");
    console.error("    /path/to/Assets.zip");
    process.exit(1);
  }

  const voyageApiKey = process.env.VOYAGE_API_KEY;
  if (!voyageApiKey) {
    console.error("Error: VOYAGE_API_KEY environment variable is required for Voyage AI indexing");
    process.exit(1);
  }

  const ollamaModel = process.env.OLLAMA_MODEL || "nomic-embed-text";

  console.log("=".repeat(60));
  console.log("  Hytale RAG - Full Index (Voyage + Ollama)");
  console.log("=".repeat(60));
  console.log("");
  console.log("Paths:");
  console.log(`  Server code:  ${serverCodePath}`);
  console.log(`  Client data:  ${clientDataPath}`);
  console.log(`  Assets zip:   ${assetsZipPath}`);
  console.log("");
  console.log("Providers:");
  console.log(`  Voyage AI:    API key configured`);
  console.log(`  Ollama:       Model: ${ollamaModel}`);
  console.log("");

  // Build job list
  const jobs: IngestJob[] = [];

  // Voyage jobs
  jobs.push({
    name: "server",
    provider: "voyage",
    script: "src/ingest.ts",
    args: [serverCodePath],
    env: { EMBEDDING_PROVIDER: "voyage", VOYAGE_API_KEY: voyageApiKey },
  });

  jobs.push({
    name: "client",
    provider: "voyage",
    script: "src/ingest-client.ts",
    args: [clientDataPath],
    env: { EMBEDDING_PROVIDER: "voyage", VOYAGE_API_KEY: voyageApiKey },
  });

  jobs.push({
    name: "gamedata",
    provider: "voyage",
    script: "src/ingest-gamedata.ts",
    args: [assetsZipPath],
    env: { EMBEDDING_PROVIDER: "voyage", VOYAGE_API_KEY: voyageApiKey },
  });

  // Ollama jobs
  jobs.push({
    name: "server",
    provider: "ollama",
    script: "src/ingest.ts",
    args: [serverCodePath],
    env: { EMBEDDING_PROVIDER: "ollama", OLLAMA_MODEL: ollamaModel },
  });

  jobs.push({
    name: "client",
    provider: "ollama",
    script: "src/ingest-client.ts",
    args: [clientDataPath],
    env: { EMBEDDING_PROVIDER: "ollama", OLLAMA_MODEL: ollamaModel },
  });

  jobs.push({
    name: "gamedata",
    provider: "ollama",
    script: "src/ingest-gamedata.ts",
    args: [assetsZipPath],
    env: { EMBEDDING_PROVIDER: "ollama", OLLAMA_MODEL: ollamaModel },
  });

  console.log(`Starting ${jobs.length} ingestion jobs in parallel...`);
  console.log("");

  const startTime = Date.now();

  // Run all jobs in parallel
  const results = await Promise.all(jobs.map(runJob));

  const totalTime = ((Date.now() - startTime) / 1000).toFixed(1);

  // Summary
  console.log("");
  console.log("=".repeat(60));
  console.log("  Summary");
  console.log("=".repeat(60));
  console.log("");

  const voyageResults = results.filter((r) => r.job.provider === "voyage");
  const ollamaResults = results.filter((r) => r.job.provider === "ollama");

  const voyageSuccess = voyageResults.filter((r) => r.success).length;
  const ollamaSuccess = ollamaResults.filter((r) => r.success).length;

  console.log("Voyage AI:");
  for (const r of voyageResults) {
    const status = r.success ? "OK" : "FAILED";
    console.log(`  ${r.job.name.padEnd(10)} ${status.padEnd(8)} (${r.duration.toFixed(1)}s)`);
  }
  console.log(`  Total: ${voyageSuccess}/${voyageResults.length} succeeded`);
  console.log("");

  console.log("Ollama:");
  for (const r of ollamaResults) {
    const status = r.success ? "OK" : "FAILED";
    console.log(`  ${r.job.name.padEnd(10)} ${status.padEnd(8)} (${r.duration.toFixed(1)}s)`);
  }
  console.log(`  Total: ${ollamaSuccess}/${ollamaResults.length} succeeded`);
  console.log("");

  console.log(`Total time: ${totalTime}s`);
  console.log("");

  // Show any errors
  const failures = results.filter((r) => !r.success);
  if (failures.length > 0) {
    console.log("Errors:");
    for (const f of failures) {
      console.log(`  [${f.job.provider}/${f.job.name}]`);
      if (f.error) {
        console.log(`    ${f.error.substring(0, 500)}`);
      }
    }
    console.log("");
  }

  // Output locations
  console.log("Database locations:");
  console.log(`  Voyage: data/voyage/lancedb/`);
  console.log(`  Ollama: data/ollama/lancedb/`);
  console.log("");

  const allSuccess = results.every((r) => r.success);
  if (allSuccess) {
    console.log("All indexing complete! Ready to package releases.");
  } else {
    console.log(`${failures.length} job(s) failed. Check errors above.`);
    process.exit(1);
  }
}

main().catch((e) => {
  console.error("Fatal error:", e);
  process.exit(1);
});
