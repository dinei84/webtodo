#!/bin/bash

echo "🚀 Script de build para Vercel"
echo "================================"

# Navegar para o diretório frontend
cd frontend || exit 1

# Instalar dependências
echo "📦 Instalando dependências..."
npm ci

# Build do projeto
echo "🔨 Fazendo build do projeto..."
npm run build

echo "✅ Build concluído com sucesso!"
echo "📂 Arquivos de produção em: frontend/dist"
