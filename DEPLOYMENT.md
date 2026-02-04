# 🚀 Deploy - Todo List Application

Este guia fornece instruções passo a passo para fazer o deploy da aplicação na Vercel (frontend) e Render/Railway (backend).

## 📋 Pré-requisitos

- Conta no [GitHub](https://github.com)
- Conta no [Vercel](https://vercel.com)
- Conta no [Render](https://render.com) ou [Railway](https://railway.app)
- Projeto Firebase configurado

## 🎯 Estratégia de Deploy

### Frontend → Vercel
- Deploy automático a cada push no GitHub
- Variáveis de ambiente configuradas
- CDN global para performance

### Backend → Render/Railway
- Deploy via Docker ou Buildpack Java
- Variáveis de ambiente para Firebase
- Auto-scaling e monitoring

---

## 📤 Parte 1: Preparar o Repositório GitHub

### 1.1 Inicializar Git (se ainda não fez)

```bash
git init
git add .
git commit -m "Initial commit - Todo App"
```

### 1.2 Criar Repositório no GitHub

1. Acesse [github.com/new](https://github.com/new)
2. Nome do repositório: `webtodo`
3. Deixe como **público** ou **privado**
4. **NÃO** inicialize com README (já temos)

### 1.3 Push para GitHub

```bash
git remote add origin https://github.com/SEU-USUARIO/webtodo.git
git branch -M main
git push -u origin main
```

⚠️ **IMPORTANTE**: Certifique-se de que `firebase-service-account.json` **NÃO** foi commitado!

```bash
# Verificar
git status

# Se o arquivo aparecer, remova do histórico
git rm --cached backend/src/main/resources/firebase-service-account.json
git commit -m "Remove Firebase credentials"
git push origin main --force
```

---

## 🚀 Parte 2: Deploy do Backend (Render)

### 2.1 Preparar Backend para Produção

Criaremos um `Dockerfile` para o backend:

```dockerfile
# backend/Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Variável de ambiente para Firebase
ENV FIREBASE_CREDENTIALS_JSON=""

# Porta
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2.2 Criar `render.yaml` (opcional, mas recomendado)

```yaml
services:
  - type: web
    name: todo-backend
    env: java
    buildCommand: cd backend && mvn clean package -DskipTests
    startCommand: cd backend && java -jar target/*.jar
    envVars:
      - key: FIREBASE_CREDENTIALS_JSON
        sync: false
      - key: SPRING_PROFILES_ACTIVE
        value: production
      - key: ALLOWED_ORIGINS
        value: https://your-frontend-url.vercel.app
```

### 2.3 Deploy no Render

1. Acesse [render.com](https://render.com)
2. Clique em **New +** → **Web Service**
3. Conecte seu repositório GitHub
4. Configurações:
   - **Name**: `todo-backend`
   - **Region**: escolha a mais próxima
   - **Branch**: `main`
   - **Root Directory**: `backend`
   - **Runtime**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`

5. **Environment Variables**:
   - `FIREBASE_CREDENTIALS_JSON`: Cole o conteúdo COMPLETO do arquivo `firebase-service-account.json` (todo o JSON)
   - `SPRING_PROFILES_ACTIVE`: `production`
   - `ALLOWED_ORIGINS`: `https://your-app.vercel.app` (atualize depois)

6. Clique em **Create Web Service**

7. Aguarde o deploy (~5 minutos)

8. **Copie a URL do backend** (ex: `https://todo-backend.onrender.com`)

---

## 🌐 Parte 3: Deploy do Frontend (Vercel)

### 3.1 Atualizar Configurações

No arquivo `frontend/.env.production`, crie:

```env
VITE_API_BASE_URL=https://todo-backend.onrender.com/api
```

### 3.2 Commit as mudanças

```bash
git add .
git commit -m "Add production environment config"
git push origin main
```

### 3.3 Deploy na Vercel

#### Opção A: Via Dashboard

1. Acesse [vercel.com](https://vercel.com)
2. Clique em **Add New** → **Project**
3. **Import** seu repositório `webtodo`
4. Configurações:
   - **Framework Preset**: Vite
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`

5. **Environment Variables**:
   - `VITE_API_BASE_URL`: `https://todo-backend.onrender.com/api`

6. Clique em **Deploy**

#### Opção B: Via CLI

```bash
npm install -g vercel

cd frontend
vercel

# Siga as instruções:
# - Set up and deploy? Yes
# - Which scope? Sua conta
# - Link to existing project? No
# - Project name? todo-frontend
# - In which directory is your code located? ./
# - Want to override settings? Yes
# - Build Command? npm run build
# - Output Directory? dist
# - Development Command? npm run dev
```

### 3.4 Adicionar Variáveis de Ambiente

```bash
vercel env add VITE_API_BASE_URL production
# Cole: https://todo-backend.onrender.com/api

vercel --prod
```

### 3.5 Copiar URL do Frontend

Após o deploy, copie a URL (ex: `https://todo-app-xyz.vercel.app`)

---

## 🔄 Parte 4: Configurar CORS

Volte ao Render e atualize a variável `ALLOWED_ORIGINS`:

1. Vá em **Environment**
2. Edite `ALLOWED_ORIGINS`
3. Cole a URL do Vercel: `https://todo-app-xyz.vercel.app`
4. Salve e aguarde o redeploy automático

---

## ✅ Parte 5: Testar a Aplicação

1. Acesse sua URL do Vercel
2. Crie uma conta
3. Faça login
4. Crie algumas tarefas
5. Verifique se tudo funciona

---

## 🔧 Troubleshooting

### Erro de CORS

**Problema**: `Access-Control-Allow-Origin` error

**Solução**:
1. Certifique-se que a URL do frontend está em `ALLOWED_ORIGINS` no backend
2. Não use trailing slash (`/`) nas URLs

### Backend não inicia

**Problema**: `Failed to initialize Firebase`

**Solução**:
1. Verifique se `FIREBASE_CREDENTIALS_JSON` está configurado
2. O valor deve ser o JSON completo, incluindo `{` e `}`
3. Não use aspas adicionais

### Frontend não conecta ao backend

**Problema**: 404 ou Network Error

**Solução**:
1. Verifique se `VITE_API_BASE_URL` está correto
2. Teste o backend diretamente: `https://seu-backend.onrender.com/api/health`
3. Redeploy o frontend: `vercel --prod`

---

## 🔄 Deploy Contínuo

Após a configuração inicial:

1. **Push para GitHub** → Frontend e Backend fazem redeploy automático
2. **Vercel** detecta mudanças em `frontend/`
3. **Render** detecta mudanças em `backend/`

```bash
git add .
git commit -m "Update feature"
git push origin main
```

---

## 🌍 URLs da Aplicação

Após o deploy, você terá:

- **Frontend**: `https://seu-app.vercel.app`
- **Backend**: `https://seu-backend.onrender.com`
- **API Docs**: `https://seu-backend.onrender.com/api/health`

---

## 📊 Monitoramento

### Vercel
- Acesse o dashboard para ver Analytics
- Logs em tempo real
- Performance metrics

### Render
- Logs do servidor
- Metrics de CPU/RAM
- Health checks

---

## 💰 Custos

- **Vercel**: Free tier (100GB bandwidth/mês)
- **Render**: Free tier (750 horas/mês, dorme após inatividade)

⚠️ **Nota**: No free tier do Render, o backend "dorme" após 15 minutos de inatividade e leva ~30 segundos para "acordar".

---

## 🔒 Segurança

✅ Firebase credentials no backend via environment variables  
✅ CORS configurado corretamente  
✅ HTTPS em produção (automático na Vercel e Render)  
✅ Tokens JWT validados no backend  
✅ `.gitignore` protegendo arquivos sensíveis  

---

## 📝 Checklist de Deploy

- [ ] Repositório no GitHub
- [ ] `.gitignore` configurado
- [ ] Firebase credentials **NÃO** commitadas
- [ ] Backend no Render
- [ ] Variáveis de ambiente do backend configuradas
- [ ] Frontend na Vercel
- [ ] Variáveis de ambiente do frontend configuradas
- [ ] CORS configurado
- [ ] Aplicação testada em produção
- [ ] URLs documentadas

---

**Parabéns! 🎉 Sua aplicação está no ar!**

Qualquer dúvida, consulte:
- [Vercel Docs](https://vercel.com/docs)
- [Render Docs](https://render.com/docs)
- [Firebase Docs](https://firebase.google.com/docs)
