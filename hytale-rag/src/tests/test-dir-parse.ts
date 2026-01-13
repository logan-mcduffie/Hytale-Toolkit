#!/usr/bin/env node
import { parseDirectory } from "./parser.js";

const dir = process.argv[2] || "C:/Users/logan/Documents/HytaleMods/decompiled/com/hypixel/hytale/assetstore";

console.log(`Parsing directory: ${dir}\n`);

async function main() {
  const { chunks, errors } = await parseDirectory(dir, (curr, total, file) => {
    if (curr % 20 === 0 || curr === total) {
      process.stdout.write(`\rParsed ${curr}/${total} files`);
    }
  });
  console.log("\n");
  console.log(`Total methods: ${chunks.length}`);
  console.log(`Parse errors: ${errors.length}`);

  if (errors.length > 0) {
    console.log("\nFirst 10 errors:");
    for (const err of errors.slice(0, 10)) {
      console.log(`  - ${err}`);
    }
  }

  // Show some stats
  const classes = new Set(chunks.map((c) => c.className));
  const packages = new Set(chunks.map((c) => c.packageName));
  console.log(`\nUnique classes: ${classes.size}`);
  console.log(`Unique packages: ${packages.size}`);

  // Show a few sample chunks
  console.log("\nSample methods:");
  for (const chunk of chunks.slice(0, 5)) {
    console.log(`  - ${chunk.id} (${chunk.lineStart}-${chunk.lineEnd})`);
  }
}

main().catch(console.error);
