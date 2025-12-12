# PowerShell script to inject CSS and JavaScript into Java documentation HTML files

param(
    [string]$CssFileName = "javadoc-readable.css",
    [string]$JsFileName = "javadoc-enhanced.js",
    [switch]$DryRun
)

$cssLink = "<link rel=`"stylesheet`" type=`"text/css`" href=`"$CssFileName`">"
$jsScript = "<script src=`"$JsFileName`"></script>"

Write-Host "Searching for HTML files..." -ForegroundColor Cyan
$htmlFiles = Get-ChildItem -Path . -Filter *.html -Recurse

if ($htmlFiles.Count -eq 0) {
    Write-Host "No HTML files found!" -ForegroundColor Red
    exit
}

Write-Host "Found $($htmlFiles.Count) HTML files" -ForegroundColor Green
Write-Host "CSS file to link: $CssFileName"
Write-Host "JS file to link: $JsFileName"
Write-Host "Dry run mode: $DryRun"
Write-Host ("-" * 60)

$modifiedCount = 0
$skippedCount = 0
$errorCount = 0

foreach ($file in $htmlFiles) {
    try {
        $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
        $modified = $false
        $newContent = $content
        
        # Check and add CSS link
        if ($newContent -notlike "*$cssLink*") {
            if ($newContent -like "*</head>*") {
                $newContent = $newContent -replace "</head>", "$cssLink`n</head>"
                $modified = $true
            }
        }
        
        # Check and add JavaScript
        if ($newContent -notlike "*$jsScript*") {
            if ($newContent -like "*</body>*") {
                $newContent = $newContent -replace "</body>", "$jsScript`n</body>"
                $modified = $true
            }
        }
        
        if (-not $modified) {
            Write-Host "SKIP: $($file.Name) (already has CSS and JS)" -ForegroundColor Yellow
            $skippedCount++
            continue
        }
        
        if ($DryRun) {
            Write-Host "WOULD MODIFY: $($file.FullName)" -ForegroundColor Cyan
            $modifiedCount++
        } else {
            Set-Content -Path $file.FullName -Value $newContent -Encoding UTF8 -NoNewline
            Write-Host "MODIFIED: $($file.Name)" -ForegroundColor Green
            $modifiedCount++
        }
        
    } catch {
        Write-Host "ERROR: $($file.Name) - $($_.Exception.Message)" -ForegroundColor Red
        $errorCount++
    }
}

Write-Host ("-" * 60)
Write-Host "SUMMARY:" -ForegroundColor Cyan
Write-Host "  Total files found: $($htmlFiles.Count)"
Write-Host "  Modified: $modifiedCount" -ForegroundColor Green
Write-Host "  Skipped: $skippedCount" -ForegroundColor Yellow
Write-Host "  Errors: $errorCount" -ForegroundColor Red

if ($DryRun) {
    Write-Host "`nThis was a DRY RUN - no files were actually modified." -ForegroundColor Cyan
    Write-Host "Run again without -DryRun to make actual changes."
}