/**
 * Version Checker
 *
 * Checks for updates by comparing the current version against
 * the latest GitHub release. Results are cached to avoid excessive API calls.
 */

const GITHUB_REPO = "logan-mcduffie/Hytale-Toolkit";
const CACHE_TTL_MS = 3600000; // 1 hour
const FETCH_TIMEOUT_MS = 5000; // 5 seconds

/**
 * Version information from the checker
 */
export interface VersionInfo {
  /** Currently installed version */
  currentVersion: string;
  /** Latest available version (null if check failed) */
  latestVersion: string | null;
  /** Whether an update is available */
  updateAvailable: boolean;
  /** URL to the latest release (null if check failed) */
  releaseUrl: string | null;
  /** When the version was last checked (null if never checked) */
  checkedAt: Date | null;
}

/**
 * Configuration options for VersionChecker
 */
export interface VersionCheckerOptions {
  /** GitHub repository in "owner/repo" format */
  githubRepo?: string;
  /** Current installed version */
  currentVersion: string;
  /** How long to cache version info in milliseconds */
  cacheTtlMs?: number;
}

/**
 * GitHub release API response (partial)
 */
interface GitHubRelease {
  tag_name: string;
  html_url: string;
  published_at: string;
}

/**
 * Version checker service
 *
 * Fetches the latest release version from GitHub and caches the result.
 * All operations are non-blocking and fail gracefully.
 */
export class VersionChecker {
  private cache: VersionInfo | null = null;
  private readonly githubRepo: string;
  private readonly currentVersion: string;
  private readonly cacheTtlMs: number;

  constructor(options: VersionCheckerOptions) {
    this.githubRepo = options.githubRepo ?? GITHUB_REPO;
    this.currentVersion = options.currentVersion;
    this.cacheTtlMs = options.cacheTtlMs ?? CACHE_TTL_MS;
  }

  /**
   * Get cached version info (synchronous, never blocks)
   * Returns null if no cached data available
   */
  getCachedVersion(): VersionInfo | null {
    return this.cache;
  }

  /**
   * Check version asynchronously (fire-and-forget)
   * Does not throw errors - logs them to stderr
   */
  checkVersionAsync(): void {
    this.checkVersion().catch((error) => {
      console.error("[VersionChecker] Background check failed:", error);
    });
  }

  /**
   * Check for updates and return version info
   * Returns cached data if still valid, otherwise fetches fresh data
   */
  async checkVersion(): Promise<VersionInfo> {
    // Return cached data if still valid
    if (this.cache && !this.isCacheExpired()) {
      return this.cache;
    }

    try {
      const release = await this.fetchLatestRelease();
      const latestVersion = this.normalizeVersion(release.tag_name);

      this.cache = {
        currentVersion: this.currentVersion,
        latestVersion,
        updateAvailable: this.isNewerVersion(latestVersion, this.currentVersion),
        releaseUrl: release.html_url,
        checkedAt: new Date(),
      };

      return this.cache;
    } catch (error) {
      // Log error but don't throw - return unknown state
      console.error("[VersionChecker] Failed to check version:", error);
      return this.createUnknownState();
    }
  }

  /**
   * Force a fresh version check (ignores cache)
   */
  async forceCheck(): Promise<VersionInfo> {
    this.cache = null;
    return this.checkVersion();
  }

  /**
   * Check if cache has expired
   */
  private isCacheExpired(): boolean {
    if (!this.cache?.checkedAt) return true;
    const elapsed = Date.now() - this.cache.checkedAt.getTime();
    return elapsed > this.cacheTtlMs;
  }

  /**
   * Fetch the latest release from GitHub API
   */
  private async fetchLatestRelease(): Promise<GitHubRelease> {
    const url = `https://api.github.com/repos/${this.githubRepo}/releases/latest`;

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), FETCH_TIMEOUT_MS);

    try {
      const response = await fetch(url, {
        headers: {
          "User-Agent": "Hytale-RAG",
          Accept: "application/vnd.github.v3+json",
        },
        signal: controller.signal,
      });

      if (!response.ok) {
        throw new Error(`GitHub API returned ${response.status}: ${response.statusText}`);
      }

      return (await response.json()) as GitHubRelease;
    } finally {
      clearTimeout(timeoutId);
    }
  }

  /**
   * Normalize version string (remove 'v' prefix)
   */
  private normalizeVersion(version: string): string {
    return version.replace(/^v/i, "");
  }

  /**
   * Compare versions to check if latest is newer than current
   * Uses simple semver comparison (major.minor.patch)
   */
  private isNewerVersion(latest: string, current: string): boolean {
    const latestParts = latest.split(".").map((p) => parseInt(p, 10) || 0);
    const currentParts = current.split(".").map((p) => parseInt(p, 10) || 0);

    // Pad arrays to same length
    const maxLength = Math.max(latestParts.length, currentParts.length);
    while (latestParts.length < maxLength) latestParts.push(0);
    while (currentParts.length < maxLength) currentParts.push(0);

    // Compare each part
    for (let i = 0; i < maxLength; i++) {
      if (latestParts[i] > currentParts[i]) return true;
      if (latestParts[i] < currentParts[i]) return false;
    }

    return false; // Versions are equal
  }

  /**
   * Create an "unknown" state when version check fails
   */
  private createUnknownState(): VersionInfo {
    return {
      currentVersion: this.currentVersion,
      latestVersion: null,
      updateAvailable: false,
      releaseUrl: null,
      checkedAt: null,
    };
  }
}

/**
 * Format version update notice for display
 * Returns empty string if no update available or version unknown
 */
export function formatVersionNotice(versionInfo: VersionInfo | null): string {
  if (!versionInfo?.updateAvailable || !versionInfo.latestVersion) {
    return "";
  }

  return `\n---\n**Update available:** v${versionInfo.latestVersion} is available (current: v${versionInfo.currentVersion}). Run \`python setup.py\` to update.`;
}
