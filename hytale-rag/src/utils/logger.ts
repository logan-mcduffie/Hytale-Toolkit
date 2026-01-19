/**
 * File-based logging utility for Hytale RAG
 *
 * CRITICAL: MCP uses stdio for communication. All logging MUST go to files only.
 * Any console output would break the MCP protocol.
 */

import * as fs from "fs";
import * as path from "path";
import * as os from "os";
import { fileURLToPath } from "url";

type LogLevel = "DEBUG" | "INFO" | "WARN" | "ERROR";

export interface Logger {
  debug(message: string): void;
  info(message: string): void;
  warn(message: string): void;
  error(message: string, error?: Error): void;
  section(title: string): void;
}

let globalLogger: Logger | null = null;
let globalLogFile: string | null = null;

/**
 * Format a timestamp for log entries
 */
function formatTimestamp(): string {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

/**
 * Format a timestamp for log filenames
 */
function formatFilenameTimestamp(): string {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day}-${hours}-${minutes}-${seconds}`;
}

/**
 * Write a log entry to the log file
 */
function writeLog(logFile: string, level: LogLevel, message: string): void {
  const timestamp = formatTimestamp();
  const entry = `${timestamp} [${level}] ${message}\n`;

  try {
    fs.appendFileSync(logFile, entry, "utf-8");
  } catch {
    // Silently fail - we cannot output to console in MCP mode
  }
}

/**
 * Get the repository root directory (two levels up from src/utils/)
 */
function getRepoRoot(): string {
  const __dirname = path.dirname(fileURLToPath(import.meta.url));
  // From src/utils/ go up to hytale-rag/, then up to repo root
  return path.resolve(__dirname, "../../..");
}

/**
 * Initialize the logger and create a log file
 *
 * @param name - Name for the log file (e.g., "hytale-rag")
 * @returns Object containing the logger and log file path
 */
export function setupLogger(name: string): { logger: Logger; logFile: string } {
  const repoRoot = getRepoRoot();
  const logsDir = path.join(repoRoot, ".logs");

  // Create .logs directory if it doesn't exist
  if (!fs.existsSync(logsDir)) {
    fs.mkdirSync(logsDir, { recursive: true });
  }

  const timestamp = formatFilenameTimestamp();
  const logFile = path.join(logsDir, `${name}-${timestamp}.log`);

  // Create empty log file
  fs.writeFileSync(logFile, "", "utf-8");

  const logger: Logger = {
    debug(message: string): void {
      writeLog(logFile, "DEBUG", message);
    },

    info(message: string): void {
      writeLog(logFile, "INFO", message);
    },

    warn(message: string): void {
      writeLog(logFile, "WARN", message);
    },

    error(message: string, error?: Error): void {
      writeLog(logFile, "ERROR", message);
      if (error?.stack) {
        // Log each line of the stack trace
        for (const line of error.stack.split("\n")) {
          writeLog(logFile, "ERROR", `  ${line}`);
        }
      }
    },

    section(title: string): void {
      writeLog(logFile, "INFO", "");
      writeLog(logFile, "INFO", `--- ${title} ---`);
    },
  };

  // Log startup header
  logger.info("============================================================");
  logger.info(`=== ${name} started ===`);
  logger.info("============================================================");
  logger.info(`Timestamp: ${new Date().toISOString()}`);
  logger.info(`Node: ${process.version}`);
  logger.info(`Platform: ${os.platform()} ${os.release()}`);
  logger.info(`Architecture: ${os.arch()}`);
  logger.info(`Working directory: ${process.cwd()}`);
  logger.info(`Log file: ${logFile}`);
  logger.info("");

  // Store globally for getLogger()
  globalLogger = logger;
  globalLogFile = logFile;

  return { logger, logFile };
}

/**
 * Get the current logger instance
 * Returns a no-op logger if setupLogger hasn't been called
 */
export function getLogger(): Logger {
  if (globalLogger) {
    return globalLogger;
  }

  // Return a no-op logger if not initialized
  return {
    debug(): void {},
    info(): void {},
    warn(): void {},
    error(): void {},
    section(): void {},
  };
}

/**
 * Get the current log file path
 */
export function getLogFile(): string | null {
  return globalLogFile;
}
