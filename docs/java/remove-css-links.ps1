# Remove old CSS links from HTML files

Write-Host "Removing old CSS links..." -ForegroundColor Cyan
$htmlFiles = Get-ChildItem -Path . -Filter *.html -Recurse

$count = 0

foreach ($file in $htmlFiles) {
    try {
        $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
        
        # Remove any CSS link that contains javadoc-readable.css
        if ($content -match '<link[^>]*javadoc-readable\.css[^>]*>') {
            $newContent = $content -replace '<link[^>]*javadoc-readable\.css[^>]*>\r?\n?', ''
            Set-Content -Path $file.FullName -Value $newContent -Encoding UTF8 -NoNewline
            Write-Host "Cleaned: $($file.Name)" -ForegroundColor Green
            $count++
        }
    } catch {
        Write-Host "ERROR: $($file.Name)" -ForegroundColor Red
    }
}

Write-Host "`nRemoved CSS links from $count files" -ForegroundColor Green