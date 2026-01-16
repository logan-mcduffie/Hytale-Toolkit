#!/usr/bin/env pwsh
# Fix <unrepresentable> tokens and empty static blocks in decompiled source

Write-Host "Fixing assertion tokens in decompiled source..." -ForegroundColor Cyan

# Find all Java files that need fixing
$files = Get-ChildItem -Path "decompiled" -Filter "*.java" -Recurse | Where-Object {
    $content = Get-Content $_.FullName -Raw
    # Check for unrepresentable tokens OR static blocks with AssertionHelper OR CODEC issues
    ($content -match "<unrepresentable>") -or
    ($content -match "static \{") -or
    ($content -match "BuilderCodecMapCodec<\w+> CODEC;")
}

Write-Host "Found $($files.Count) files to check" -ForegroundColor Yellow

$count = 0
foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    $modified = $false

    # Replace <unrepresentable> with AssertionHelper
    if ($content -match "<unrepresentable>") {
        $content = $content -replace '<unrepresentable>', 'AssertionHelper'
        $modified = $true
    }

    # Remove empty assertion static blocks
    if ($content -match "static \{\s+if \(AssertionHelper\.\`$assertionsDisabled\) \{\s+\}\s+\}") {
        $content = $content -replace '(?s)\s+static \{\s+if \(AssertionHelper\.\$assertionsDisabled\) \{\s+\}\s+\}', ''
        $modified = $true
    }

    # Fix CODEC field initialization in interfaces
    if ($content -match "BuilderCodecMapCodec<(\w+)> CODEC;\s+") {
        # Check if there's a static block initializing CODEC
        if ($content -match "(?s)static \{.*?CODEC = (new BuilderCodecMapCodec<>.*?);") {
            $initialization = $Matches[1]
            # Add initialization to field declaration
            $content = $content -replace "(BuilderCodecMapCodec<\w+> CODEC);", "`$1 = $initialization;"
            # Remove the static block
            $content = $content -replace '(?s)\s+static \{.*?CODEC = new BuilderCodecMapCodec<>.*?;\s+\}', ''
            $modified = $true
        }
    }

    if ($modified) {
        Set-Content -Path $file.FullName -Value $content -NoNewline
        $count++
        Write-Host "  Fixed: $($file.FullName.Replace((Get-Location).Path + '\', ''))" -ForegroundColor Green
    }
}

Write-Host "`nFixed $count files" -ForegroundColor Green
if ($count -gt 0) {
    Write-Host "You can now run javadoc successfully!" -ForegroundColor Green
} else {
    Write-Host "No files needed fixing." -ForegroundColor Yellow
}
