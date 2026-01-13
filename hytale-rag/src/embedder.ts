import type { MethodChunk } from "./parser.js";

const VOYAGE_API_URL = "https://api.voyageai.com/v1/embeddings";
const MODEL = "voyage-code-2";
const BATCH_SIZE = 128; // Voyage supports up to 128 texts per request

export interface EmbeddedChunk extends MethodChunk {
  vector: number[];
  textForEmbedding: string;
}

function buildTextForEmbedding(chunk: MethodChunk): string {
  // Build a rich text representation that captures context
  const parts: string[] = [];

  // Package and class context
  parts.push(`// Package: ${chunk.packageName}`);
  parts.push(`// Class: ${chunk.className}`);

  // Key imports (limit to avoid bloat)
  const relevantImports = chunk.imports
    .filter((imp) => imp.includes("hypixel") || imp.includes("hytale"))
    .slice(0, 10);
  if (relevantImports.length > 0) {
    parts.push(`// Imports: ${relevantImports.join(", ")}`);
  }

  // Field context (condensed)
  if (chunk.fields.length > 0) {
    parts.push(`// Fields: ${chunk.fields.slice(0, 5).join(" ")}`);
  }

  // The actual method
  parts.push(chunk.content);

  return parts.join("\n");
}

function truncateToTokenLimit(text: string, maxChars: number = 32000): string {
  // Rough approximation: 1 token ~= 4 chars for code
  if (text.length <= maxChars) return text;
  return text.substring(0, maxChars) + "\n// ... truncated";
}

export async function embedChunks(
  chunks: MethodChunk[],
  apiKey: string,
  onProgress?: (current: number, total: number) => void
): Promise<EmbeddedChunk[]> {
  const results: EmbeddedChunk[] = [];

  // Prepare texts
  const textsWithChunks = chunks.map((chunk) => ({
    chunk,
    text: truncateToTokenLimit(buildTextForEmbedding(chunk)),
  }));

  // Process in batches
  for (let i = 0; i < textsWithChunks.length; i += BATCH_SIZE) {
    const batch = textsWithChunks.slice(i, i + BATCH_SIZE);
    const texts = batch.map((b) => b.text);

    if (onProgress) {
      onProgress(Math.min(i + BATCH_SIZE, textsWithChunks.length), textsWithChunks.length);
    }

    const response = await fetch(VOYAGE_API_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${apiKey}`,
      },
      body: JSON.stringify({
        model: MODEL,
        input: texts,
        input_type: "document",
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Voyage API error: ${response.status} - ${errorText}`);
    }

    const data = (await response.json()) as {
      data: Array<{ embedding: number[]; index: number }>;
      usage: { total_tokens: number };
    };

    // Match embeddings back to chunks
    for (const item of data.data) {
      const batchItem = batch[item.index];
      results.push({
        ...batchItem.chunk,
        vector: item.embedding,
        textForEmbedding: batchItem.text,
      });
    }

    // Rate limiting - be nice to the API
    if (i + BATCH_SIZE < textsWithChunks.length) {
      await new Promise((resolve) => setTimeout(resolve, 100));
    }
  }

  return results;
}

export async function embedQuery(query: string, apiKey: string): Promise<number[]> {
  const response = await fetch(VOYAGE_API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${apiKey}`,
    },
    body: JSON.stringify({
      model: MODEL,
      input: [query],
      input_type: "query",
    }),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Voyage API error: ${response.status} - ${errorText}`);
  }

  const data = (await response.json()) as {
    data: Array<{ embedding: number[] }>;
  };

  return data.data[0].embedding;
}
