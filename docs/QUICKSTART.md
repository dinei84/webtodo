# 🚀 Guia de Início Rápido

## Pré-requisitos
- ✅ Java 17 ou superior
- ✅ Maven 3.6+
- ✅ Node.js 18+ e npm
- ✅ Conta no Firebase

## ⚡ Configuração Rápida (5 minutos)

### 1️⃣ Configurar Firebase (2 minutos)

1. Acesse https://console.firebase.google.com/
2. Crie um novo projeto (ou use existente)
3. Ative **Authentication** → Email/Password
4. Ative **Firestore Database** → Modo de teste

### 2️⃣ Backend - Service Account (1 minuto)

1. Firebase Console → ⚙️ → **Service Accounts**
2. Clique **Generate New Private Key**
3. Salve como: `backend/src/main/resources/firebase-service-account.json`

### 3️⃣ Frontend - Web App Config (1 minuto)

1. Firebase Console → ⚙️ → **General** → Your apps
2. Clique no ícone **</>** (Web)
3. Registre o app e copie as credenciais
4. Cole em `frontend/src/config/firebase.js`:

```javascript
export const firebaseConfig = {
  apiKey: "SUA_API_KEY_AQUI",
  authDomain: "SEU_PROJECT_ID.firebaseapp.com",
  projectId: "SEU_PROJECT_ID",
  storageBucket: "SEU_PROJECT_ID.appspot.com",
  messagingSenderId: "SEU_SENDER_ID",
  appId: "SEU_APP_ID"
};
```

### 4️⃣ Executar Backend (30 segundos)

```bash
cd backend
mvnw spring-boot:run
```

✅ Backend rodando em: http://localhost:8080

### 5️⃣ Executar Frontend (30 segundos)

**Em outro terminal:**

```bash
cd frontend
npm install
npm run dev
```

✅ Frontend rodando em: http://localhost:3000

## 🎉 Pronto!

Acesse http://localhost:3000 e:
1. Clique em "Criar conta"
2. Registre-se com email/senha
3. Comece a criar suas tarefas!

## 🆘 Problemas?

### Backend não inicia
```bash
# Verifique se o arquivo firebase-service-account.json existe
ls backend/src/main/resources/firebase-service-account.json
```

### Frontend não encontra API
Edite `frontend/src/config/firebase.js` e confirme:
```javascript
export const API_BASE_URL = 'http://localhost:8080/api';
```

### Erro de CORS
Verifique `backend/src/main/resources/application.properties`:
```properties
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

---

**Dica:** Use o endpoint de health check para testar se o backend está funcionando:
```bash
curl http://localhost:8080/api/health
```

Resposta esperada:
```json
{"status":"UP","service":"todo-backend"}
```
