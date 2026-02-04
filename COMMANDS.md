# 🛠️ Comandos Úteis - Todo List App

Referência rápida de comandos para desenvolvimento, deploy e troubleshooting.

## 📦 Instalação e Setup

### Clonar Projeto
```bash
git clone https://github.com/dinei84/webtodo.git
cd webtodo
```

### Configurar Backend
```bash
cd backend

# Instalar dependências
./mvnw clean install

# Ou no Windows
mvnw.cmd clean install
```

### Configurar Frontend
```bash
cd frontend

# Instalar dependências
npm install
```

## 🚀 Desenvolvimento Local

### Backend
```bash
# Iniciar servidor de desenvolvimento
cd backend
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run

# Com profile específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=production

# Rodar testes
./mvnw test

# Build para produção
./mvnw clean package

# Pular testes
./mvnw clean package -DskipTests
```

### Frontend
```bash
cd frontend

# Desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview

# Lint
npm run lint

# Lint + fix
npm run lint -- --fix
```

## 🐳 Docker

### Build da Imagem
```bash
# Navegue para o diretório backend
cd backend

# Build da imagem Docker
docker build -t todo-backend:latest .

# Build com tag específica
docker build -t todo-backend:v1.0.0 .
```

### Executar Container
```bash
# Executar com variáveis de ambiente
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e FIREBASE_CREDENTIALS_JSON='{"type":"service_account",...}' \
  -e ALLOWED_ORIGINS=http://localhost:3000 \
  --name todo-backend \
  todo-backend:latest

# Ver logs
docker logs todo-backend

# Ver logs em tempo real
docker logs -f todo-backend

# Parar container
docker stop todo-backend

# Remover container
docker rm todo-backend

# Acessar shell do container
docker exec -it todo-backend /bin/sh
```

### Docker Compose (Opcional)
```yaml
# docker-compose.yml
version: '3.8'

services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - FIREBASE_CREDENTIALS_JSON=${FIREBASE_CREDENTIALS_JSON}
      - ALLOWED_ORIGINS=http://localhost:3000
    restart: unless-stopped

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "3000:80"
    environment:
      - VITE_API_BASE_URL=http://localhost:8080/api
    depends_on:
      - backend
    restart: unless-stopped
```

```bash
# Rodar com Docker Compose
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar tudo
docker-compose down
```

## 🔧 Git Commands

### Setup Inicial
```bash
# Inicializar repositório
git init

# Adicionar remote
git remote add origin https://github.com/dinei84/webtodo.git

# Verificar remotes
git remote -v
```

### Workflow Básico
```bash
# Verificar status
git status

# Adicionar arquivos
git add .

# Commit
git commit -m "feat: adiciona nova funcionalidade"

# Push
git push origin main

# Pull (atualizar)
git pull origin main
```

### Branches
```bash
# Criar e mudar para novo branch
git checkout -b feature/nova-funcionalidade

# Listar branches
git branch

# Mudar de branch
git checkout main

# Merge
git merge feature/nova-funcionalidade

# Deletar branch
git branch -d feature/nova-funcionalidade
```

### Desfazer Mudanças
```bash
# Descartar mudanças não commitadas
git checkout -- .

# Desfazer último commit (mantém mudanças)
git reset --soft HEAD~1

# Desfazer último commit (descarta mudanças)
git reset --hard HEAD~1

# Reverter commit específico
git revert <commit-hash>
```

## 🚀 Deploy

### Preparar Firebase Credentials
```powershell
# Windows PowerShell
.\setup-firebase-env.ps1

# Output será salvo em firebase-credentials-oneline.txt
```

```bash
# Linux/Mac
chmod +x setup-firebase-env.sh
./setup-firebase-env.sh backend/src/main/resources/firebase-service-account.json
```

### Vercel CLI
```bash
# Instalar Vercel CLI
npm install -g vercel

# Login
vercel login

# Deploy (preview)
cd frontend
vercel

# Deploy (production)
vercel --prod

# Ver logs
vercel logs

# Listar deployments
vercel ls

# Adicionar variável de ambiente
vercel env add VITE_API_BASE_URL production
```

### Render CLI (Opcional)
```bash
# Instalar Render CLI
brew install render  # Mac
# ou baixe de https://render.com/docs/cli

# Login
render login

# Listar services
render services

# Ver logs
render logs todo-backend

# Trigger redeploy
render deploy todo-backend
```

## 🔍 Debugging

### Backend Logs (Local)
```bash
# Ver todos os logs
tail -f backend/logs/spring.log

# Buscar erro específico
grep "ERROR" backend/logs/spring.log

# Logs em tempo real (Maven)
./mvnw spring-boot:run | tee backend.log
```

### Frontend Logs (Browser)
```javascript
// No DevTools Console

// Ver todas chamadas API
localStorage.debug = 'axios:*'

// Limpar console
console.clear()

// Ver localStorage
console.log(localStorage)

// Ver token atual
console.log(localStorage.getItem('token'))
```

### Network Debugging
```bash
# Testar backend
curl http://localhost:8080/api/health

# Com autenticação
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8080/api/tasks

# POST request
curl -X POST \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -d '{"title":"Test","priority":"HIGH"}' \
     http://localhost:8080/api/tasks
```

## 📊 Performance Testing

### Backend Load Test
```bash
# Instalar Apache Bench
# Ubuntu: sudo apt-get install apache2-utils
# Mac: brew install httpd (já incluso)

# Teste simples
ab -n 100 -c 10 http://localhost:8080/api/health

# Com autenticação
ab -n 100 -c 10 \
   -H "Authorization: Bearer YOUR_TOKEN" \
   http://localhost:8080/api/tasks
```

### Frontend Performance
```bash
# Lighthouse CLI
npm install -g lighthouse

# Rodar análise
lighthouse http://localhost:3000 --view

# Com específico device
lighthouse http://localhost:3000 \
  --emulated-form-factor=mobile \
  --view
```

## 🧹 Limpeza

### Limpar Build Artifacts
```bash
# Backend
cd backend
./mvnw clean

# Frontend
cd frontend
npm run build -- --clean
rm -rf dist
rm -rf node_modules
npm install
```

### Limpar Docker
```bash
# Remover containers parados
docker container prune

# Remover imagens não usadas
docker image prune

# Remover tudo não usado
docker system prune -a

# Limpar cache de build
docker builder prune
```

### Limpar Git
```bash
# Remover arquivos não rastreados
git clean -fd

# Preview do que será removido
git clean -fd --dry-run

# Remover branches mergeados
git branch --merged | grep -v "\*" | xargs -n 1 git branch -d
```

## 🔐 Security Checks

### Verificar Secrets no Código
```bash
# Instalar gitleaks
brew install gitleaks  # Mac
# ou baixe de https://github.com/gitleaks/gitleaks

# Escanear repositório
gitleaks detect --source . --verbose

# Escanear histórico do Git
gitleaks detect --source . --log-opts="--all"
```

### Atualizar Dependências
```bash
# Backend
cd backend
./mvnw versions:display-dependency-updates

# Frontend
cd frontend
npm outdated
npm audit
npm audit fix
```

## 📱 Mobile Testing

### Testar em Dispositivos Locais
```bash
# Frontend - acessível na rede local
cd frontend
npm run dev -- --host

# O endereço será algo como:
# http://192.168.1.100:3000

# Backend - acessível na rede local
# Edite application.properties:
# server.address=0.0.0.0
```

## 🎯 Atalhos e Aliases

Adicione ao seu `.bashrc` ou `.zshrc`:

```bash
# Aliases úteis
alias todo-frontend="cd ~/webtodo/frontend && npm run dev"
alias todo-backend="cd ~/webtodo/backend && ./mvnw spring-boot:run"
alias todo-test-all="cd ~/webtodo && (cd backend && ./mvnw test) && (cd frontend && npm run lint)"
alias todo-logs="docker logs -f todo-backend"

# Functions
todo-deploy() {
    echo "🚀 Deploying..."
    git add .
    git commit -m "$1"
    git push origin main
    echo "✅ Pushed to GitHub. Vercel and Render will auto-deploy."
}

# Uso: todo-deploy "feat: add new feature"
```

## 📚 Referências Rápidas

### Portas Padrão
- Backend: `8080`
- Frontend: `3000`
- Docker Backend: `8080`
- Docker Frontend: `80` (interno) → `3000` (host)

### Endpoints Importantes
- Health: `/api/health`
- Tasks: `/api/tasks`
- Login: Frontend handle (Firebase)

### Variáveis de Ambiente Importantes

**Backend:**
- `FIREBASE_CREDENTIALS_JSON`
- `SPRING_PROFILES_ACTIVE`
- `ALLOWED_ORIGINS`

**Frontend:**
- `VITE_API_BASE_URL`

---

**💡 Dica**: Mantenha este arquivo aberto em uma aba do navegador para referência rápida durante o desenvolvimento!
