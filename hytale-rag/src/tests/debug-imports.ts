#!/usr/bin/env node
import { parse } from "java-parser";
import * as fs from "fs";

const testFile = "C:/Users/logan/Documents/HytaleMods/decompiled/com/hypixel/hytale/Main.java";

const source = fs.readFileSync(testFile, "utf-8");
const cst = parse(source);

const ordinaryComp = cst.children?.ordinaryCompilationUnit?.[0];
const importDecls = ordinaryComp?.children?.importDeclaration || [];

console.log("Import count:", importDecls.length);

if (importDecls.length > 0) {
  const firstImport = importDecls[0];
  console.log("\nFirst import keys:", Object.keys(firstImport.children || {}));

  // Check for packageOrTypeName
  const potn = firstImport.children?.packageOrTypeName;
  if (potn) {
    console.log("\npackageOrTypeName found:");
    console.log("  keys:", Object.keys(potn[0]?.children || {}));
    console.log("  Identifiers:", potn[0]?.children?.Identifier?.map((i: any) => i.image));
  } else {
    console.log("\nNo packageOrTypeName, checking direct Identifier:");
    console.log("  Identifier:", firstImport.children?.Identifier?.map((i: any) => i.image));
  }
}
