#!/usr/bin/env node
import { parseJavaFile } from "./parser.js";

const testFile = process.argv[2] || "C:/Users/logan/Documents/HytaleMods/decompiled/com/hypixel/hytale/Main.java";

console.log(`Testing parser on: ${testFile}\n`);

const result = parseJavaFile(testFile);

if (result.errors.length > 0) {
  console.log("Errors:");
  for (const error of result.errors) {
    console.log(`  - ${error}`);
  }
  console.log("");
}

console.log(`Found ${result.chunks.length} methods:\n`);

for (const chunk of result.chunks) {
  console.log(`--- ${chunk.id} ---`);
  console.log(`  Lines: ${chunk.lineStart}-${chunk.lineEnd}`);
  console.log(`  Signature: ${chunk.methodSignature}`);
  console.log(`  Fields: ${chunk.fields.length}`);
  console.log(`  Imports: ${chunk.imports.length}`);
  console.log("");
}
