# Todo List - Aplicação Full Stack

[![CI/CD](https://github.com/dinei84/webtodo/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/dinei84/webtodo/actions)
[![Deploy](https://img.shields.io/badge/deploy-vercel-black)](https://vercel.com)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61dafb.svg)](https://react.dev/)
[![Firebase](https://img.shields.io/badge/Firebase-10.7-ffca28.svg)](https://firebase.google.com/)

Aplicação Todo List Full Stack com **Spring Boot**, **React** e **Firebase**, oferecendo autenticação, CRUD de tarefas e interface moderna.

## 📚 Documentação

- [docs/DOCUMENTATION.md](docs/DOCUMENTATION.md) - Documentação unificada e atualizada
- [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md) - Como contribuir para o projeto
- [docs/SECURITY.md](docs/SECURITY.md) - Políticas de segurança e reporte de vulnerabilidades

## 🚀 Começar

### 1. Configurar Firebase

- Ative **Authentication** (Email/Password)
- Ative **Firestore Database**
- Gere o arquivo `firebase-service-account.json` em **Service Accounts**
- Coloque em `backend/src/main/resources/`

### 2. Configurar frontend

Crie `frontend/.env` com as variáveis do Firebase e a URL da API:

```env
VITE_FIREBASE_API_KEY=seu_api_key
VITE_FIREBASE_AUTH_DOMAIN=seu_project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=seu_project_id
VITE_FIREBASE_STORAGE_BUCKET=seu_project.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=seu_sender_id
VITE_FIREBASE_APP_ID=seu_app_id
VITE_FIREBASE_MEASUREMENT_ID=seu_measurement_id
VITE_API_BASE_URL=http://localhost:8080/api
```

### 3. Executar localmente

```bash
cd backend
./mvnw spring-boot:run
```

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

## 🧩 O que está incluído

### Backend

- Java 17
- Spring Boot 3.2.2
- Spring Security
- Firebase Admin SDK 9.2.0
- Firestore
- API REST com CRUD de tarefas

### Frontend

- React 18
- Vite
- Firebase Client SDK 10.7.2
- Axios
- React Router DOM 6
- Login, dashboard e gerenciamento de tarefas

## 🏗️ Estrutura da aplicação

- `backend/` → API REST, segurança Firebase e Firestore
- `frontend/` → interface em React, autenticação e consumo da API
- `docs/DOCUMENTATION.md` → documentação principal
- `README.md` → visão geral do projeto

## 🔒 Segurança básica

- Tokens Firebase validados no backend
- Usuário só acessa suas próprias tarefas
- CORS configurado para desenvolvimento
- Rotas protegidas no frontend

## 💡 Observação

A documentação detalhada do projeto agora está consolidada em `docs/DOCUMENTATION.md`. Use esse arquivo para configuração, arquitetura, API, troubleshooting e referências completas.

---

**Boa codificação! 🚀**