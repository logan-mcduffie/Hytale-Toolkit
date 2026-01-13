#!/usr/bin/env node
import { parse } from "java-parser";
import * as fs from "fs";

const testFile = process.argv[2] || "C:/Users/logan/Documents/HytaleMods/decompiled/com/hypixel/hytale/Main.java";

const source = fs.readFileSync(testFile, "utf-8");
const cst = parse(source);

// Debug the CST structure
console.log("Top-level keys:", Object.keys(cst.children || {}));

const ordinaryComp = cst.children?.ordinaryCompilationUnit?.[0];
if (ordinaryComp) {
  console.log("\nordinaryCompilationUnit keys:", Object.keys(ordinaryComp.children || {}));

  const pkgDecl = ordinaryComp.children?.packageDeclaration?.[0];
  if (pkgDecl) {
    console.log("\npackageDeclaration keys:", Object.keys(pkgDecl.children || {}));
    console.log("\npackageDeclaration children:", JSON.stringify(pkgDecl.children, null, 2).slice(0, 2000));
  }
}
