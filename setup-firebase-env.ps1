# Script para preparar credenciais do Firebase para variável de ambiente
# Usage: .\setup-firebase-env.ps1

param(
    [string]$FirebaseJsonPath = "backend\src\main\resources\firebase-service-account.json"
)

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Firebase Environment Variable Setup" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se o arquivo existe
if (-not (Test-Path $FirebaseJsonPath)) {
    Write-Host "❌ Error: File not found: $FirebaseJsonPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please ensure the Firebase service account JSON file is in the correct location." -ForegroundColor Yellow
    exit 1
}

# Ler e minimizar JSON (remover espaços e quebras de linha)
try {
    $FirebaseJson = Get-Content -Path $FirebaseJsonPath -Raw | ConvertFrom-Json | ConvertTo-Json -Compress -Depth 10
    
    Write-Host "✅ Successfully read Firebase credentials" -ForegroundColor Green
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host "Copy this value for FIREBASE_CREDENTIALS_JSON:" -ForegroundColor Cyan
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host $FirebaseJson -ForegroundColor White
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Cyan
    
    # Salvar em arquivo
    $OutputFile = "firebase-credentials-oneline.txt"
    $FirebaseJson | Out-File -FilePath $OutputFile -NoNewline -Encoding UTF8
    
    Write-Host ""
    Write-Host "✅ Also saved to: $OutputFile" -ForegroundColor Green
    Write-Host "⚠️  Remember: This file is in .gitignore for security!" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host "Next Steps:" -ForegroundColor Cyan
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host "1. Go to Render.com → Your Service → Environment" -ForegroundColor White
    Write-Host "2. Add variable: FIREBASE_CREDENTIALS_JSON" -ForegroundColor White
    Write-Host "3. Paste the JSON value shown above" -ForegroundColor White
    Write-Host "4. Save and redeploy" -ForegroundColor White
    Write-Host ""
    
} catch {
    Write-Host "❌ Error processing JSON file: $_" -ForegroundColor Red
    exit 1
}
