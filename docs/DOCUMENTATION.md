# Todo List - Documentação Consolidada

[![CI/CD](https://github.com/dinei84/webtodo/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/dinei84/webtodo/actions)
[![Deploy](https://img.shields.io/badge/deploy-vercel-black)](https://vercel.com)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61dafb.svg)](https://react.dev/)
[![Firebase](https://img.shields.io/badge/Firebase-10.7-ffca28.svg)](https://firebase.google.com/)

## 1. Visão Geral

Aplicação Todo List Full Stack com:
- Backend em **Spring Boot** + **Firebase Admin SDK**
- Frontend em **React** + **Vite** + **Firebase Client SDK**
- Autenticação Firebase
- Firestore como banco de dados
- Segurança com Spring Security e validação de tokens JWT
- Dashboard protegido, CRUD de tarefas e filtragem por status

## 2. Documentação Unificada

Este arquivo é a fonte principal de documentação do projeto. Use-o para encontrar:
- configuração local
- arquitetura
- API
- segurança
- execução
- troubleshooting

Outros documentos permanecem como referências específicas, mas o fluxo principal está aqui.

## 3. Demonstração

- Frontend: `https://seu-app.vercel.app` _(configurar após deploy)_
- Backend API: `https://seu-backend.onrender.com` _(configurar após deploy)_

## 4. Guia Rápido

### 4.1 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Node.js 18+ e npm
- Conta no Firebase

### 4.2 Configurar Firebase

1. Acesse https://console.firebase.google.com/
2. Crie um novo projeto ou use um projeto existente
3. Ative **Authentication** → Email/Password
4. Ative **Firestore Database** → Modo de teste (para desenvolvimento)

### 4.3 Obter credenciais do backend

1. No Firebase Console, vá em **Configurações do Projeto** → **Contas de Serviço**
2. Clique em **Gerar nova chave privada**
3. Salve o arquivo JSON como `firebase-service-account.json`
4. Coloque o arquivo em `backend/src/main/resources/`

> Se preferir não colocar o arquivo no repositório, use a variável de ambiente `FIREBASE_CREDENTIALS_PATH`.

### 4.4 Configurar frontend

Crie um arquivo `.env` na raiz de `frontend/` com as seguintes variáveis:

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

O frontend lê essas variáveis em `frontend/src/config/firebase.js`.

### 4.5 Executar Backend

```bash
cd backend
./mvnw spring-boot:run
```

O backend ficará disponível em `http://localhost:8080`.

### 4.6 Executar Frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend ficará disponível em `http://localhost:3000`.

### 4.7 Testar saúde do backend

```bash
curl http://localhost:8080/api/health
```

Resposta esperada:

```json
{"status":"UP","service":"todo-backend"}
```

## 5. Estrutura do Projeto

```
webtodo/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/todo/
│   │   │   │   ├── config/
│   │   │   │   │   ├── FirebaseConfig.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── TaskController.java
│   │   │   │   │   └── HealthController.java
│   │   │   │   ├── dto/
│   │   │   │   │   └── TaskDTO.java
│   │   │   │   ├── model/
│   │   │   │   │   └── Task.java
│   │   │   │   ├── security/
│   │   │   │   │   ├── FirebaseAuthenticationFilter.java
│   │   │   │   │   └── UserPrincipal.java
│   │   │   │   ├── service/
│   │   │   │   │   └── TaskService.java
│   │   │   │   └── TodoBackendApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── firebase-service-account.json (gitignored)
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Dashboard.jsx
│   │   │   ├── Login.jsx
│   │   │   ├── PrivateRoute.jsx
│   │   │   ├── TaskForm.jsx
│   │   │   ├── TaskItem.jsx
│   │   │   └── TodoList.jsx
│   │   ├── contexts/
│   │   │   └── AuthContext.jsx
│   │   ├── services/
│   │   │   ├── api.js
│   │   │   ├── authService.js
│   │   │   ├── firebase.js
│   │   │   └── taskService.js
│   │   ├── styles/
│   │   │   ├── App.css
│   │   │   ├── Dashboard.css
│   │   │   ├── Login.css
│   │   │   ├── TaskForm.css
│   │   │   ├── TaskItem.css
│   │   │   └── TodoList.css
│   │   ├── config/
│   │   │   └── firebase.js
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   └── vite.config.js

└── README.md
```

## 6. Arquitetura

### Visão geral

O frontend React consome a API REST do backend Spring Boot. O backend valida tokens Firebase e persiste dados em Firestore.

```
┌──────────────────────────────┐        ┌───────────────────────────┐
│         FRONTEND             │        │         BACKEND           │
│       (React + Vite)         │  ←REST→ │   (Spring Boot + Firebase) │
└──────────────────────────────┘        └───────────────────────────┘
```

### Fluxo de autenticação

1. O usuário faz login no frontend com email/senha.
2. O Firebase Authentication gera um token JWT.
3. O frontend envia o token em `Authorization: Bearer {token}` para o backend.
4. O backend valida o token usando o Firebase Admin SDK.
5. Se válido, o backend processa a requisição Firestore.

### Camadas

#### Frontend
- Components: `Login`, `Dashboard`, `TodoList`, `TaskItem`, `TaskForm`
- Estado: `AuthContext`
- Serviços: `authService`, `taskService`, `api.js`
- Configuração Firebase em `frontend/src/config/firebase.js`

#### Backend
- Controllers: `TaskController`, `HealthController`
- Security: `FirebaseAuthenticationFilter`, `SecurityConfig`, `UserPrincipal`
- Serviço: `TaskService`
- Dados: Firestore via Firebase Admin SDK

## 7. Funcionalidades

### Backend
- Autenticação Firebase ID Token
- CRUD completo de tarefas
- Isolamento de dados por usuário
- Validação de entrada com Bean Validation
- Logging e tratamento de erros
- CORS configurado para desenvolvimento

### Frontend
- Login/registro com Firebase Authentication
- Dashboard protegido por autenticação
- Criar, editar e deletar tarefas
- Marcar tarefas concluídas
- Filtros: Todas, Ativas, Concluídas
- Estatísticas de tarefas
- Prioridade de tarefas
- Interface responsiva

## 8. API Endpoints

### Health Check

```http
GET /api/health
```

Resposta:

```json
{
  "status": "UP",
  "service": "todo-backend"
}
```

### Tasks

#### Listar tarefas

```http
GET /api/tasks
Authorization: Bearer {firebase-jwt-token}
```

#### Buscar tarefa por ID

```http
GET /api/tasks/{id}
Authorization: Bearer {firebase-jwt-token}
```

#### Criar tarefa

```http
POST /api/tasks
Authorization: Bearer {firebase-jwt-token}
Content-Type: application/json

{
  "title": "Minha tarefa",
  "description": "Descrição da tarefa",
  "priority": "HIGH",
  "completed": false
}
```

#### Atualizar tarefa

```http
PUT /api/tasks/{id}
Authorization: Bearer {firebase-jwt-token}
Content-Type: application/json

{
  "title": "Título atualizado",
  "description": "Nova descrição",
  "priority": "MEDIUM",
  "completed": true
}
```

#### Deletar tarefa

```http
DELETE /api/tasks/{id}
Authorization: Bearer {firebase-jwt-token}
```

## 9. Segurança

- Todos os endpoints `/api/tasks/**` exigem token Firebase válido.
- O backend valida o token JWT usando Firebase Admin SDK.
- Cada usuário só acessa as próprias tarefas.
- O backend utiliza CORS restrito para `http://localhost:3000` e `http://localhost:5173`.
- O frontend usa rotas protegidas e interceptadores Axios para injetar o token.

## 10. Configuração e variáveis de ambiente

### Backend

`backend/src/main/resources/application.properties`

```properties
server.port=8080
spring.application.name=todo-backend
firebase.credentials.path=${FIREBASE_CREDENTIALS_PATH:classpath:firebase-service-account.json}
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173
logging.level.com.todo=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Frontend

`frontend/src/config/firebase.js` lê variáveis Vite do `.env`:

```js
export const firebaseConfig = {
    apiKey: import.meta.env.VITE_FIREBASE_API_KEY || '',
    authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || '',
    projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || '',
    storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || '',
    messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || '',
    appId: import.meta.env.VITE_FIREBASE_APP_ID || '',
    measurementId: import.meta.env.VITE_FIREBASE_MEASUREMENT_ID || ''
};

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
```

## 11. Troubleshooting

### Backend não inicia

- Verifique se o arquivo `backend/src/main/resources/firebase-service-account.json` existe.
- Caso use caminho customizado, confirme `FIREBASE_CREDENTIALS_PATH`.
- Confira os logs do Spring Boot e do Firebase.

### Frontend não conecta ao backend

- Verifique se o backend está rodando em `http://localhost:8080`.
- Confira `VITE_API_BASE_URL` no `.env` do frontend.
- Veja se o navegador está bloqueando CORS.

### Erro de autenticação

- Verifique se as credenciais do Firebase estão corretas.
- Confirme que o Authentication está habilitado no Firebase Console.
- Limpe o cache do navegador e tente novamente.

## 12. Tecnologias

### Backend
- Java 17
- Spring Boot 3.2.2
- Spring Security
- Firebase Admin SDK 9.2.0
- Maven
- Lombok
- Bean Validation

### Frontend
- React 18
- Vite
- React Router DOM 6
- Firebase Client SDK 10.7.2
- Axios
- CSS customizado

### Cloud
- Firebase Authentication
- Firestore
- Vercel / Render (opcional para deploy)

## 13. Documentos complementares

- `CONTRIBUTING.md` → como contribuir
- `SECURITY.md` → políticas de segurança
- `FIRESTORE_RULES.md` → regras de segurança do Firestore
- `DEPLOYMENT.md` → guia detalhado de deploy avançado
