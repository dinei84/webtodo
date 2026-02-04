# 🔍 Script de Verificação de Pré-requisitos

Write-Host "================================" -ForegroundColor Cyan
Write-Host "  Verificação de Pré-requisitos" -ForegroundColor Cyan
Write-Host "  Todo List Application" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

$allOk = $true

# Verificar Java
Write-Host "Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String -Pattern "version"
    if ($javaVersion -match '(\d+)\.') {
        $version = [int]$matches[1]
        if ($version -ge 17) {
            Write-Host "✅ Java $version encontrado" -ForegroundColor Green
        } else {
            Write-Host "❌ Java 17+ necessário (encontrado: Java $version)" -ForegroundColor Red
            $allOk = $false
        }
    }
} catch {
    Write-Host "❌ Java não encontrado" -ForegroundColor Red
    $allOk = $false
}

# Verificar Maven
Write-Host "Verificando Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1 | Select-String -Pattern "Apache Maven"
    if ($mavenVersion) {
        Write-Host "✅ Maven encontrado" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Maven não encontrado" -ForegroundColor Red
    $allOk = $false
}

# Verificar Node.js
Write-Host "Verificando Node.js..." -ForegroundColor Yellow
try {
    $nodeVersion = node --version
    if ($nodeVersion -match 'v(\d+)\.') {
        $version = [int]$matches[1]
        if ($version -ge 18) {
            Write-Host "✅ Node.js $nodeVersion encontrado" -ForegroundColor Green
        } else {
            Write-Host "⚠️  Node.js 18+ recomendado (encontrado: $nodeVersion)" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "❌ Node.js não encontrado" -ForegroundColor Red
    $allOk = $false
}

# Verificar npm
Write-Host "Verificando npm..." -ForegroundColor Yellow
try {
    $npmVersion = npm --version
    Write-Host "✅ npm $npmVersion encontrado" -ForegroundColor Green
} catch {
    Write-Host "❌ npm não encontrado" -ForegroundColor Red
    $allOk = $false
}

# Verificar arquivo Firebase Service Account (Backend)
Write-Host "Verificando configuração do Firebase..." -ForegroundColor Yellow
$firebaseFile = "backend\src\main\resources\firebase-service-account.json"
if (Test-Path $firebaseFile) {
    Write-Host "✅ Firebase Service Account configurado" -ForegroundColor Green
} else {
    Write-Host "⚠️  Firebase Service Account não encontrado" -ForegroundColor Yellow
    Write-Host "   Configure seguindo: backend\FIREBASE_SETUP.md" -ForegroundColor Gray
}

# Verificar configuração Firebase Frontend
Write-Host "Verificando configuração do Frontend..." -ForegroundColor Yellow
$frontendConfig = "frontend\src\config\firebase.js"
if (Test-Path $frontendConfig) {
    $content = Get-Content $frontendConfig -Raw
    if ($content -match "YOUR_API_KEY") {
        Write-Host "⚠️  Frontend Firebase ainda não configurado" -ForegroundColor Yellow
        Write-Host "   Atualize: frontend\src\config\firebase.js" -ForegroundColor Gray
    } else {
        Write-Host "✅ Frontend Firebase configurado" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
if ($allOk) {
    Write-Host "✅ Tudo pronto para começar!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Próximos passos:" -ForegroundColor White
    Write-Host "1. Configure o Firebase (veja QUICKSTART.md)" -ForegroundColor Gray
    Write-Host "2. Execute: cd backend && mvnw spring-boot:run" -ForegroundColor Gray
    Write-Host "3. Execute: cd frontend && npm install && npm run dev" -ForegroundColor Gray
} else {
    Write-Host "❌ Alguns pré-requisitos estão faltando" -ForegroundColor Red
    Write-Host ""
    Write-Host "Instale os seguintes:" -ForegroundColor White
    Write-Host "- Java 17+: https://adoptium.net/" -ForegroundColor Gray
    Write-Host "- Maven: https://maven.apache.org/download.cgi" -ForegroundColor Gray
    Write-Host "- Node.js 18+: https://nodejs.org/" -ForegroundColor Gray
}
Write-Host "================================" -ForegroundColor Cyan
