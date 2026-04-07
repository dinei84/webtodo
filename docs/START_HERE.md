# 🎯 Início Rápido - Deploy em 3 Passos

**Tempo estimado: 20 minutos**

## ⚡ Passo 1: GitHub (5 minutos)

### 1.1 - Commit e Push
```bash
# No terminal, na pasta do projeto
git add .
git commit -m "Prepare for deployment"
git push origin main
```

### 1.2 - Verificar
✅ Acesse: `https://github.com/seu-usuario/webtodo`  
✅ Confirme que todos os arquivos estão lá

---

## 🚀 Passo 2: Backend - Render (10 minutos)

### 2.1 - Criar Serviço
1. Acesse: [render.com](https://render.com)
2. Clique: **New +** → **Web Service**
3. Conecte GitHub → Selecione `webtodo`

### 2.2 - Configurar
```
Name:               todo-backend
Region:             Oregon
Branch:             main
Runtime:            Docker
Dockerfile Path:    ./backend/Dockerfile
```

### 2.3 - Variáveis de Ambiente

**Preparar Firebase primeiro:**
```powershell
# Windows - Execute na pasta do projeto
.\setup-firebase-env.ps1
```

Copie o JSON que apareceu e adicione no Render:

```
FIREBASE_CREDENTIALS_JSON = {cole o JSON aqui}
SPRING_PROFILES_ACTIVE    = production
ALLOWED_ORIGINS           = https://todo-app.vercel.app
```
> ⚠️ Você vai atualizar `ALLOWED_ORIGINS` depois com a URL real do Vercel

### 2.4 - Deploy
1. Clique **Create Web Service**
2. ⏱️ Aguarde ~5 minutos
3. 📋 **COPIE A URL**: `https://todo-backend-XXXX.onrender.com`

---

## 🌐 Passo 3: Frontend - Vercel (5 minutos)

### 3.1 - Criar Projeto
1. Acesse: [vercel.com](https://vercel.com)
2. Clique: **New Project**
3. Import `webtodo` do GitHub

### 3.2 - Configurar
```
Framework:          Vite (detectado automaticamente)
Root Directory:     frontend
Build Command:      npm run build
Output Directory:   dist
```

### 3.3 - Variável de Ambiente
```
VITE_API_BASE_URL = https://todo-backend-XXXX.onrender.com/api
```
> 🔗 Use a URL do Render que você copiou (com `/api` no final)

### 3.4 - Deploy
1. Clique **Deploy**
2. ⏱️ Aguarde ~2 minutos
3. 📋 **COPIE A URL**: `https://todo-app-XXXX.vercel.app`

---

## 🔄 Passo Final: Conectar Tudo

### Atualizar CORS
1. Volte ao Render → Seu serviço
2. Vá em **Environment**
3. Edite `ALLOWED_ORIGINS`
4. Cole a URL do Vercel (sem barra no final)
5. Aguarde redeploy (~2 minutos)

---

## ✅ Testar

1. Abra a URL do Vercel
2. Crie uma conta
3. Faça login
4. Crie uma tarefa

**Funcionou? 🎉 PARABÉNS!**

**Erro de CORS?** Verifique se:
- `ALLOWED_ORIGINS` tem a URL correta do Vercel
- Backend redeployou após mudar CORS
- URL não tem `/` no final

---

## 📚 Precisa de Mais Detalhes?

- **Problemas?** → [`DEPLOYMENT.md`](DEPLOYMENT.md) (Seção Troubleshooting)
- **Checklist?** → [`DEPLOY_CHECKLIST.md`](DEPLOY_CHECKLIST.md)
- **Arquitetura?** → [`DEPLOY_ARCHITECTURE.md`](DEPLOY_ARCHITECTURE.md)
- **Comandos?** → [`COMMANDS.md`](COMMANDS.md)

---

## 🎬 Fluxo Visual

```
📝 Git Push
    ↓
🔧 GitHub
    ↓
    ├─→ 🚀 Render (Backend)  → https://backend.onrender.com
    │       ↑
    │       └─ 🔥 Firebase (Auth + Firestore)
    │
    └─→ ☁️  Vercel (Frontend) → https://app.vercel.app
            ↓
        👤 Usuário
```

---

**🚀 Boa sorte com o deploy!**
