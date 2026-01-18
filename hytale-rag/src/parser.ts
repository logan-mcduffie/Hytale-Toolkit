import { parse } from "java-parser";
import * as fs from "fs";
import * as path from "path";
import * as crypto from "crypto";

/**
 * Decompilation artifact patterns that need to be fixed before parsing.
 * Vineflower sometimes produces invalid Java syntax for bytecode it can't represent.
 */
const DECOMPILATION_FIXES: Array<{ pattern: RegExp; replacement: string; description: string }> = [
  {
    // Remove empty if statements checking $assertionsDisabled
    // These appear in static blocks and are useless: if (<...>.$assertionsDisabled) { }
    pattern: /\s*if\s*\([^)]*\$assertionsDisabled[^)]*\)\s*\{\s*\}\s*\n?/g,
    replacement: "\n",
    description: "empty assertion if statement",
  },
  {
    // Remove now-empty static blocks (just whitespace/newlines inside braces)
    pattern: /\n\s*static\s*\{\s*\}/g,
    replacement: "",
    description: "empty static block",
  },
  {
    // <unrepresentable>.$assertionsDisabled -> false
    // This is a synthetic field for assertion state. Replacing with false means
    // the assertion check block is preserved but won't execute (same as runtime with assertions disabled)
    pattern: /<unrepresentable>\.\$assertionsDisabled/g,
    replacement: "false",
    description: "assertion disabled flag",
  },
  {
    // Any other <unrepresentable> references -> null
    // This catches any other decompilation artifacts we haven't seen yet
    pattern: /<unrepresentable>/g,
    replacement: "null /* <unrepresentable> */",
    description: "unrepresentable bytecode",
  },
  {
    // Empty enum converted to fields: "public enum Foo {\n   private" -> "public class Foo {\n   private"
    // Vineflower sometimes decompiles utility classes as empty enums
    pattern: /\benum\s+(\w+)\s*\{\s*\n(\s*)(private|protected|public|static|final)/g,
    replacement: "class $1 {\n$2$3",
    description: "empty enum with fields",
  },
  {
    // Qualified generic inner class instantiation: "new Outer<T>.Inner<U>()" -> "new Outer.Inner<U>()"
    // The java-parser doesn't handle type arguments on the outer class in qualified instantiation
    pattern: /new\s+(\w+)<[^>]+>\.(\w+)/g,
    replacement: "new $1.$2",
    description: "qualified generic inner class",
  },
];

/**
 * Preprocess Java source to fix common decompilation artifacts.
 * Returns the cleaned source and a list of fixes applied.
 */
function preprocessSource(source: string, filePath: string): { source: string; fixes: string[] } {
  const fixes: string[] = [];
  let cleaned = source;

  for (const fix of DECOMPILATION_FIXES) {
    const matches = cleaned.match(fix.pattern);
    if (matches && matches.length > 0) {
      fixes.push(`Fixed ${matches.length} ${fix.description} artifact(s) in ${path.basename(filePath)}`);
      cleaned = cleaned.replace(fix.pattern, fix.replacement);
    }
  }

  // Special handling for TOP-LEVEL interfaces: remove static initializer blocks
  // Interfaces can have static METHODS but not static BLOCKS - this is a decompilation artifact
  // Only apply this to files where the main type is an interface (not classes with nested interfaces)
  // Check if file declares a top-level interface by looking for "public interface X" or just "interface X"
  // at the beginning of a line (not nested inside a class body)
  const hasTopLevelClass = /^\s*(public\s+)?(abstract\s+)?(final\s+)?class\s+\w+/m.test(cleaned);
  const hasTopLevelInterface = /^\s*(public\s+)?interface\s+\w+/m.test(cleaned);

  if (hasTopLevelInterface && !hasTopLevelClass) {
    const staticBlockPattern = /\n\s*static\s*\{[\s\S]*?\}\s*(?=\n\s*\})/g;
    const staticMatches = cleaned.match(staticBlockPattern);
    if (staticMatches) {
      fixes.push(`Removed ${staticMatches.length} static block(s) from interface in ${path.basename(filePath)}`);
      cleaned = cleaned.replace(staticBlockPattern, "");
    }
  }

  return { source: cleaned, fixes };
}

export interface MethodChunk {
  id: string; // unique identifier: package.ClassName.methodName(params)
  className: string;
  packageName: string;
  methodName: string;
  methodSignature: string;
  content: string; // the actual code
  filePath: string;
  fileHash: string; // SHA-256 hash of file content for incremental indexing
  lineStart: number;
  lineEnd: number;
  imports: string[];
  fields: string[]; // condensed field declarations
  classJavadoc?: string;
  methodJavadoc?: string;
}

export interface ParseResult {
  chunks: MethodChunk[];
  errors: string[];
}

function extractTextFromNode(source: string, node: any): string {
  if (!node || !node.location) return "";
  const start = node.location.startOffset;
  const end = node.location.endOffset;
  return source.substring(start, end + 1);
}

function getLineNumber(source: string, offset: number): number {
  return source.substring(0, offset).split("\n").length;
}

function extractImports(cst: any): string[] {
  const imports: string[] = [];
  const ordinaryComp = cst.children?.ordinaryCompilationUnit?.[0];
  const importDecls = ordinaryComp?.children?.importDeclaration ||
                      cst.children?.importDeclaration || [];
  for (const imp of importDecls) {
    // Identifiers are under packageOrTypeName
    const potn = imp.children?.packageOrTypeName?.[0];
    const parts: string[] = [];
    for (const id of potn?.children?.Identifier || []) {
      parts.push(id.image);
    }
    if (parts.length > 0) {
      const isStatic = imp.children?.Static ? "static " : "";
      const isStar = imp.children?.Star ? ".*" : "";
      imports.push(`import ${isStatic}${parts.join(".")}${isStar};`);
    }
  }
  return imports;
}

function extractPackageName(cst: any): string {
  // Package declaration may be at top level or under ordinaryCompilationUnit
  const ordinaryComp = cst.children?.ordinaryCompilationUnit?.[0];
  const pkgDecl = ordinaryComp?.children?.packageDeclaration?.[0] ||
                  cst.children?.packageDeclaration?.[0];
  if (!pkgDecl) return "";

  // Identifiers are directly under packageDeclaration
  const parts: string[] = [];
  for (const id of pkgDecl.children?.Identifier || []) {
    parts.push(id.image);
  }
  return parts.join(".");
}

function extractFields(classBody: any, source: string): string[] {
  const fields: string[] = [];
  const bodyDecls = classBody?.children?.classBodyDeclaration || [];

  for (const decl of bodyDecls) {
    const memberDecl = decl.children?.classMemberDeclaration?.[0];
    if (!memberDecl) continue;

    const fieldDecl = memberDecl.children?.fieldDeclaration?.[0];
    if (fieldDecl) {
      // Get a condensed version - type and name only
      const fieldText = extractTextFromNode(source, fieldDecl);
      // Truncate long initializers
      const semicolonIdx = fieldText.indexOf(";");
      const equalsIdx = fieldText.indexOf("=");
      if (equalsIdx !== -1 && semicolonIdx !== -1) {
        const beforeEquals = fieldText.substring(0, equalsIdx).trim();
        fields.push(beforeEquals + ";");
      } else if (semicolonIdx !== -1) {
        fields.push(fieldText.substring(0, semicolonIdx + 1).trim());
      }
    }
  }
  return fields;
}

function extractMethods(
  classDecl: any,
  source: string,
  packageName: string,
  imports: string[],
  filePath: string,
  fileHash: string
): MethodChunk[] {
  const chunks: MethodChunk[] = [];

  // Get class name
  const classNameToken =
    classDecl.children?.normalClassDeclaration?.[0]?.children?.typeIdentifier?.[0]?.children?.Identifier?.[0] ||
    classDecl.children?.normalInterfaceDeclaration?.[0]?.children?.typeIdentifier?.[0]?.children?.Identifier?.[0] ||
    classDecl.children?.enumDeclaration?.[0]?.children?.typeIdentifier?.[0]?.children?.Identifier?.[0];

  if (!classNameToken) return chunks;
  const className = classNameToken.image;

  // Get class body
  const classBody =
    classDecl.children?.normalClassDeclaration?.[0]?.children?.classBody?.[0] ||
    classDecl.children?.normalInterfaceDeclaration?.[0]?.children?.interfaceBody?.[0] ||
    classDecl.children?.enumDeclaration?.[0]?.children?.enumBody?.[0];

  if (!classBody) return chunks;

  // Extract fields for context
  const fields = extractFields(classBody, source);

  // Extract methods
  const bodyDecls =
    classBody.children?.classBodyDeclaration ||
    classBody.children?.interfaceMemberDeclaration ||
    [];

  for (const decl of bodyDecls) {
    let methodDecl = null;
    let isConstructor = false;

    // Check for regular method
    const memberDecl = decl.children?.classMemberDeclaration?.[0];
    if (memberDecl?.children?.methodDeclaration?.[0]) {
      methodDecl = memberDecl.children.methodDeclaration[0];
    }
    // Check for constructor
    else if (decl.children?.constructorDeclaration?.[0]) {
      methodDecl = decl.children.constructorDeclaration[0];
      isConstructor = true;
    }
    // Check for interface method
    else if (decl.children?.interfaceMethodDeclaration?.[0]) {
      methodDecl = decl.children.interfaceMethodDeclaration[0];
    }

    if (!methodDecl) continue;

    // Get method name
    let methodName: string;
    if (isConstructor) {
      methodName = className; // constructor name = class name
    } else {
      const methodHeader = methodDecl.children?.methodHeader?.[0];
      const methodDeclarator = methodHeader?.children?.methodDeclarator?.[0];
      const methodNameToken = methodDeclarator?.children?.Identifier?.[0];
      if (!methodNameToken) continue;
      methodName = methodNameToken.image;
    }

    // Build method signature (simplified)
    const methodText = extractTextFromNode(source, methodDecl);
    const firstBrace = methodText.indexOf("{");
    const methodSignature = firstBrace !== -1
      ? methodText.substring(0, firstBrace).trim()
      : methodText.split("\n")[0].trim();

    // Get line numbers
    const lineStart = getLineNumber(source, methodDecl.location.startOffset);
    const lineEnd = getLineNumber(source, methodDecl.location.endOffset);

    // Build unique ID
    const id = `${packageName}.${className}.${methodName}`;

    chunks.push({
      id,
      className,
      packageName,
      methodName,
      methodSignature,
      content: methodText,
      filePath,
      fileHash,
      lineStart,
      lineEnd,
      imports,
      fields: fields.slice(0, 20), // limit to first 20 fields for context
    });
  }

  return chunks;
}

export function parseJavaFile(filePath: string): ParseResult {
  const errors: string[] = [];
  const chunks: MethodChunk[] = [];

  // Skip package-info.java files - they don't contain methods and the decompiled
  // version often has invalid syntax (e.g., "interface package-info")
  if (path.basename(filePath) === "package-info.java") {
    return { chunks: [], errors: [] };
  }

  let rawSource: string;
  try {
    rawSource = fs.readFileSync(filePath, "utf-8");
  } catch (e) {
    return { chunks: [], errors: [`Failed to read file: ${filePath}`] };
  }

  // Skip very small files
  if (rawSource.length < 50) {
    return { chunks: [], errors: [] };
  }

  // Compute file hash for incremental indexing
  const fileHash = crypto.createHash("sha256").update(rawSource).digest("hex");

  // Preprocess to fix decompilation artifacts
  const { source, fixes } = preprocessSource(rawSource, filePath);
  // Log fixes for visibility (these go to the errors array as info, not actual errors)
  // We could add a separate "fixes" array but for now just note them
  if (fixes.length > 0) {
    // Don't add to errors - these are successful fixes, not errors
    // console.log(`  ${fixes.join(", ")}`);
  }

  let cst: any;
  try {
    cst = parse(source);
  } catch (e: any) {
    return { chunks: [], errors: [`Parse error in ${filePath}: ${e.message}`] };
  }

  const packageName = extractPackageName(cst);
  const imports = extractImports(cst);

  // Find all type declarations (classes, interfaces, enums)
  const typeDecls = cst.children?.ordinaryCompilationUnit?.[0]?.children?.typeDeclaration || [];

  for (const typeDecl of typeDecls) {
    const classDecl = typeDecl.children?.classDeclaration?.[0];
    const interfaceDecl = typeDecl.children?.interfaceDeclaration?.[0];

    const decl = classDecl || interfaceDecl;
    if (!decl) continue;

    const methods = extractMethods(decl, source, packageName, imports, filePath, fileHash);
    chunks.push(...methods);
  }

  return { chunks, errors };
}

export async function parseDirectory(
  dirPath: string,
  onProgress?: (current: number, total: number, file: string) => void,
  pathFilter?: (filePath: string) => boolean
): Promise<{ chunks: MethodChunk[]; errors: string[] }> {
  const allChunks: MethodChunk[] = [];
  const allErrors: string[] = [];

  // Collect all Java files
  const javaFiles: string[] = [];

  function collectFiles(dir: string) {
    const entries = fs.readdirSync(dir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(dir, entry.name);
      if (entry.isDirectory()) {
        collectFiles(fullPath);
      } else if (entry.name.endsWith(".java")) {
        // Apply path filter if provided
        if (!pathFilter || pathFilter(fullPath)) {
          javaFiles.push(fullPath);
        }
      }
    }
  }

  collectFiles(dirPath);

  // Parse each file
  for (let i = 0; i < javaFiles.length; i++) {
    const file = javaFiles[i];
    if (onProgress) {
      onProgress(i + 1, javaFiles.length, file);
    }

    const result = parseJavaFile(file);
    allChunks.push(...result.chunks);
    allErrors.push(...result.errors);
  }

  return { chunks: allChunks, errors: allErrors };
}
