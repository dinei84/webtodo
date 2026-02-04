# Quick Deploy Guide

## 🚦 Passos Rápidos

### 1️⃣ GitHub (5 minutos)
```bash
git add .
git commit -m "Prepare for deployment"
git push origin main
```

### 2️⃣ Backend - Render.com (10 minutos)
1. Vá em [render.com](https://render.com) → New → Web Service
2. Conecte GitHub → Selecione `webtodo`
3. Configurações:
   - **Name**: `todo-backend`
   - **Region**: Oregon
   - **Branch**: main
   - **Runtime**: Docker
   - **Dockerfile Path**: `./backend/Dockerfile`
4. Environment Variables:
   ```
   FIREBASE_CREDENTIALS_JSON=<cole o JSON do Firebase aqui>
   SPRING_PROFILES_ACTIVE=production
   ALLOWED_ORIGINS=<URL do Vercel - adicione depois>
   ```
5. Deploy → Copie a URL (ex: `https://todo-backend.onrender.com`)

### 3️⃣ Frontend - Vercel (5 minutos)
1. Vá em [vercel.com](https://vercel.com) → New Project
2. Import `webtodo`
3. Configurações:
   - **Framework**: Vite
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. Environment Variables:
   ```
   VITE_API_BASE_URL=https://todo-backend.onrender.com/api
   ```
5. Deploy → Copie a URL

### 4️⃣ Atualizar CORS
Volte ao Render → Environment → `ALLOWED_ORIGINS` → Cole URL do Vercel

### ✅ Testar
Acesse a URL do Vercel e teste a aplicação!

---

## 🔧 Scripts Úteis

### Preparar credenciais Firebase (Linux/Mac):
```bash
chmod +x setup-firebase-env.sh
./setup-firebase-env.sh backend/src/main/resources/firebase-service-account.json
```

### Preparar credenciais Firebase (Windows):
```powershell
.\setup-firebase-env.ps1
```

---

## 📋 Checklist
- [ ] GitHub repository criado
- [ ] Backend no Render
- [ ] Frontend no Vercel
- [ ] Variáveis de ambiente configuradas
- [ ] CORS atualizado
- [ ] Aplicação testada

---

Para mais detalhes, veja [DEPLOYMENT.md](./DEPLOYMENT.md)
