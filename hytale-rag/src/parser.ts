import { parse } from "java-parser";
import * as fs from "fs";
import * as path from "path";

export interface MethodChunk {
  id: string; // unique identifier: package.ClassName.methodName(params)
  className: string;
  packageName: string;
  methodName: string;
  methodSignature: string;
  content: string; // the actual code
  filePath: string;
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
  filePath: string
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

  let source: string;
  try {
    source = fs.readFileSync(filePath, "utf-8");
  } catch (e) {
    return { chunks: [], errors: [`Failed to read file: ${filePath}`] };
  }

  // Skip very small files (probably just package-info.java or similar)
  if (source.length < 50) {
    return { chunks: [], errors: [] };
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

    const methods = extractMethods(decl, source, packageName, imports, filePath);
    chunks.push(...methods);
  }

  return { chunks, errors };
}

export async function parseDirectory(
  dirPath: string,
  onProgress?: (current: number, total: number, file: string) => void
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
        javaFiles.push(fullPath);
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
