# Todo List - Aplicação Full Stack

[![CI/CD](https://github.com/dinei84/webtodo/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/dinei84/webtodo/actions)
[![Deploy](https://img.shields.io/badge/deploy-vercel-black)](https://vercel.com)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61dafb.svg)](https://react.dev/)
[![Firebase](https://img.shields.io/badge/Firebase-10.7-ffca28.svg)](https://firebase.google.com/)

Uma aplicação moderna de gerenciamento de tarefas (Todo List) construída com **Spring Boot** e **React**, integrada com **Firebase** para autenticação e armazenamento de dados.

## 🌐 Demo

- **Frontend**: [https://seu-app.vercel.app](https://seu-app.vercel.app) _(Configure após deploy)_
- **Backend API**: [https://seu-backend.onrender.com](https://seu-backend.onrender.com) _(Configure após deploy)_

## 📚 Documentação

- [🚀 Guia de Deploy Rápido](QUICK_DEPLOY.md) - Comece a fazer deploy em 20 minutos
- [📖 Guia Completo de Deploy](DEPLOYMENT.md) - Instruções detalhadas
- [🤝 Como Contribuir](CONTRIBUTING.md) - Guidelines para contribuidores
- [🔒 Política de Segurança](SECURITY.md) - Práticas e reporte de vulnerabilidades


## 🏗️ Arquitetura

### Backend (Spring Boot + Firebase Admin SDK)
- **Java 17** com **Spring Boot 3.2.2**
- **Firebase Admin SDK** para validação de tokens JWT
- **Firestore** como banco de dados NoSQL
- **Spring Security** para proteção de endpoints
- Endpoints REST completos com CRUD

### Frontend (React + Firebase Client SDK)
- **React 18** com **Vite**
- **Firebase Client SDK** para autenticação
- **Axios** com interceptors para chamadas à API
- **React Router** para navegação
- Interface moderna com CSS customizado

## 📋 Funcionalidades

### Backend
✅ Autenticação via Firebase ID Token  
✅ CRUD completo de tarefas  
✅ Isolamento de dados por usuário  
✅ Validação de dados com Bean Validation  
✅ Logging detalhado  
✅ Tratamento de erros  
✅ CORS configurado  

### Frontend
✅ Login/Registro com Firebase Authentication  
✅ Dashboard protegido por autenticação  
✅ Criar, editar e deletar tarefas  
✅ Marcar tarefas como concluídas  
✅ Filtros (Todas, Ativas, Concluídas)  
✅ Estatísticas de tarefas  
✅ Níveis de prioridade (Alta, Média, Baixa)  
✅ Interface responsiva e moderna  

## 🚀 Como Configurar

### 1. Configurar Firebase

#### 1.1 Criar Projeto no Firebase Console
1. Acesse [Firebase Console](https://console.firebase.google.com/)
2. Crie um novo projeto
3. Ative **Authentication** (Email/Password)
4. Ative **Firestore Database**

#### 1.2 Obter Credenciais para Backend
1. No Firebase Console, vá em **Project Settings** → **Service Accounts**
2. Clique em **Generate New Private Key**
3. Salve o arquivo JSON como `firebase-service-account.json`
4. Coloque o arquivo em `backend/src/main/resources/`

⚠️ **IMPORTANTE**: Adicione `firebase-service-account.json` ao `.gitignore`!

#### 1.3 Obter Credenciais para Frontend
1. No Firebase Console, vá em **Project Settings** → **General**
2. Em "Your apps", clique em **Web** (</>) para adicionar um app web
3. Copie as configurações do Firebase
4. Cole em `frontend/src/config/firebase.js`

### 2. Configurar Backend

#### 2.1 Variável de Ambiente (Opcional)
Se preferir usar variável de ambiente ao invés de colocar o arquivo no projeto:

**Windows (PowerShell):**
```powershell
$env:FIREBASE_CREDENTIALS_PATH="C:\caminho\para\firebase-service-account.json"
```

**Linux/Mac:**
```bash
export FIREBASE_CREDENTIALS_PATH="/caminho/para/firebase-service-account.json"
```

#### 2.2 Executar Backend
```bash
cd backend
./mvnw spring-boot:run
```

O backend estará rodando em: `http://localhost:8080`

### 3. Configurar Frontend

#### 3.1 Instalar Dependências
```bash
cd frontend
npm install
```

#### 3.2 Configurar URL da API (Opcional)
Crie um arquivo `.env` em `frontend/`:
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

#### 3.3 Executar Frontend
```bash
npm run dev
```

O frontend estará rodando em: `http://localhost:3000`

## 📁 Estrutura do Projeto

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
│   │   └── test/
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
│
└── README.md
```

## 🔒 Segurança

### Backend
- **Firebase ID Token Validation**: Todos os endpoints `/api/**` (exceto `/api/health` e `/api/public/**`) requerem um token JWT válido
- **User Isolation**: Cada usuário só pode acessar/modificar suas próprias tarefas
- **CORS**: Configurado para aceitar apenas origins autorizadas
- **Stateless Sessions**: Sem uso de sessões, apenas tokens JWT

### Frontend
- **Axios Interceptors**: Token JWT é automaticamente incluído em todas as requisições
- **Private Routes**: Páginas protegidas redirecionam usuários não autenticados
- **Context API**: Gerenciamento centralizado do estado de autenticação

## 🔌 API Endpoints

### Health Check
```
GET /api/health
```
Response:
```json
{
  "status": "UP",
  "service": "todo-backend"
}
```

### Tasks (Requerem Autenticação)

#### Listar todas as tasks
```
GET /api/tasks
Authorization: Bearer {firebase-jwt-token}
```

#### Buscar task por ID
```
GET /api/tasks/{id}
Authorization: Bearer {firebase-jwt-token}
```

#### Criar nova task
```
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

#### Atualizar task
```
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

#### Deletar task
```
DELETE /api/tasks/{id}
Authorization: Bearer {firebase-jwt-token}
```

## 🎨 Design

A interface foi desenvolvida com:
- **Dark Mode** moderno
- **Gradientes** vibrantes
- **Animações** suaves
- **Glassmorphism** no header
- **Responsividade** completa
- **Custom scrollbar**
- **Micro-interações**

## 🛠️ Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot 3.2.2
- Spring Security
- Firebase Admin SDK 9.2.0
- Maven
- Lombok

### Frontend
- React 18
- Vite
- Firebase Client SDK 10.7.2
- Axios
- React Router DOM 6

## 📝 Padrões e Boas Práticas

### Backend
✅ Arquitetura em camadas (Controller → Service → Firestore)  
✅ DTOs para validação de entrada  
✅ Tratamento centralizado de erros  
✅ Logging estruturado  
✅ Código limpo e documentado  

### Frontend
✅ Componentes funcionais com Hooks  
✅ Context API para estado global  
✅ Services para lógica de negócio  
✅ Separação de responsabilidades  
✅ CSS modular  

## 🐛 Troubleshooting

### Backend não inicia
- Verifique se o arquivo `firebase-service-account.json` está no lugar correto
- Confira se a variável `FIREBASE_CREDENTIALS_PATH` está configurada
- Verifique os logs para erros de inicialização do Firebase

### Frontend não conecta ao backend
- Verifique se o backend está rodando em `http://localhost:8080`
- Confira a configuração de `API_BASE_URL` em `frontend/src/config/firebase.js`
- Verifique o console do navegador para erros de CORS

### Erro de autenticação
- Confirme que as credenciais do Firebase estão corretas
- Verifique se o Authentication está ativado no Firebase Console
- Limpe o localStorage e tente fazer login novamente

## 📄 Licença

Este projeto é open source e está disponível para uso educacional.

## 👨‍💻 Desenvolvido por

Desenvolvedor Full Stack Senior - Seguindo as melhores práticas de desenvolvimento web moderno.

---

**Boa codificação! 🚀**