# Hytale Modding RAG Setup Script
# This script sets up the semantic code search MCP server for Claude Code

param(
    [Parameter(Mandatory=$true)]
    [string]$VoyageApiKey
)

# Wrap everything in try/catch so errors don't close the window
try {
    $ErrorActionPreference = "Stop"

    Write-Host "=== Hytale Modding RAG Setup ===" -ForegroundColor Cyan
    Write-Host ""

    # Get the script's directory (where hytale-rag lives)
    $ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
    $McpServerPath = Join-Path $ScriptDir "src\mcp-server.ts"

    Write-Host "Script directory: $ScriptDir" -ForegroundColor Gray
    Write-Host ""

    # Verify we're in the right place
    if (-not (Test-Path (Join-Path $ScriptDir "package.json"))) {
        throw "package.json not found. Run this script from the hytale-rag directory."
    }

    if (-not (Test-Path (Join-Path $ScriptDir "data\lancedb"))) {
        throw "LanceDB database not found at data\lancedb. The pre-built vector database should be included in this repo."
    }

    # Step 1: Install dependencies
    Write-Host "Step 1: Installing dependencies..." -ForegroundColor Yellow
    Push-Location $ScriptDir
    npm install
    if ($LASTEXITCODE -ne 0) {
        Pop-Location
        throw "npm install failed"
    }
    Pop-Location
    Write-Host "  Dependencies installed." -ForegroundColor Green

    # Step 2: Test the API key
    Write-Host ""
    Write-Host "Step 2: Testing Voyage API key..." -ForegroundColor Yellow
    $env:VOYAGE_API_KEY = $VoyageApiKey
    Push-Location $ScriptDir
    $testResult = npm run search -- "test" -n 1 2>&1
    Pop-Location

    if ($testResult -match "Error|error") {
        Write-Host "  Warning: API key test may have failed. Check your key." -ForegroundColor Yellow
        Write-Host "  $testResult" -ForegroundColor Gray
    } else {
        Write-Host "  API key works!" -ForegroundColor Green
    }

    # Step 3: Configure Claude Code
    Write-Host ""
    Write-Host "Step 3: Configuring Claude Code MCP server..." -ForegroundColor Yellow

    $ClaudeConfigPath = Join-Path $env:USERPROFILE ".claude.json"
    $McpServerPathUnix = $McpServerPath -replace '\\', '/'

    # The MCP server auto-detects its database path relative to itself
    $McpConfig = @{
        command = "npx"
        args = @("tsx", $McpServerPathUnix)
        env = @{
            VOYAGE_API_KEY = $VoyageApiKey
        }
    }

    # Read existing config or create new
    if (Test-Path $ClaudeConfigPath) {
        try {
            $config = Get-Content $ClaudeConfigPath -Raw | ConvertFrom-Json -AsHashtable
        } catch {
            Write-Host "  Warning: Could not parse existing .claude.json, creating new one" -ForegroundColor Yellow
            $config = @{}
        }
    } else {
        $config = @{}
    }

    # Ensure mcpServers exists
    if (-not $config.ContainsKey("mcpServers")) {
        $config["mcpServers"] = @{}
    }

    # Add or update hytale-rag server
    $config["mcpServers"]["hytale-rag"] = $McpConfig

    # Write config
    $config | ConvertTo-Json -Depth 10 | Set-Content $ClaudeConfigPath -Encoding UTF8
    Write-Host "  Added 'hytale-rag' MCP server to $ClaudeConfigPath" -ForegroundColor Green

    # Done
    Write-Host ""
    Write-Host "=== Setup Complete ===" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "The 'hytale-rag' MCP server has been configured for Claude Code." -ForegroundColor White
    Write-Host ""
    Write-Host "To use it:" -ForegroundColor White
    Write-Host "  1. Restart Claude Code (or any running Claude Code instances)" -ForegroundColor Gray
    Write-Host "  2. Ask Claude to search the Hytale codebase, e.g.:" -ForegroundColor Gray
    Write-Host "     'Search the Hytale code for player movement handling'" -ForegroundColor Cyan
    Write-Host "     'Find methods related to inventory management'" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Available tools:" -ForegroundColor White
    Write-Host "  - search_hytale_code: Semantic search over 37,000+ methods" -ForegroundColor Gray
    Write-Host "  - hytale_code_stats: Show database statistics" -ForegroundColor Gray

} catch {
    Write-Host ""
    Write-Host "=== ERROR ===" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "Stack trace:" -ForegroundColor Gray
    Write-Host $_.ScriptStackTrace -ForegroundColor Gray
}

# Keep window open
Write-Host ""
Write-Host "Press any key to close..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
