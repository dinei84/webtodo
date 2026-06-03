# Diagnóstico e Correção de Deploy — Todo List App

> **Para agentes:** USE a skill `superpowers:subagent-driven-development` (recomendado) ou `superpowers:executing-plans` para implementar este plano tarefa por tarefa. Os passos usam checkbox (`- [ ]`) para tracking.

**Objetivo:** Diagnosticar e corrigir os problemas que impedem o deploy da aplicação no Render (backend) e Vercel (frontend).

**Arquitetura:** Aplicação full-stack com React/Vite (frontend, Vercel), Spring Boot 3.2.2 + Java 17 (backend Docker, Render), Firebase Auth + Firestore como banco.

**Stack:** React 18, Vite 5, Axios, Firebase Client SDK 10.7, Spring Boot 3.2.2, Java 17, Maven, Docker, Firestore, Caffeine Cache.

---

## 🔍 Diagnóstico: 7 Problemas Encontrados

| # | Severidade | Onde | Problema |
|---|---|---|---|
| 1 | 🔴 **Crítico** | Frontend | `vite.config.js` usa `minify: 'terser'` mas o pacote `terser` **NÃO** está no `package.json` → build **FALHA** no Vercel |
| 2 | 🔴 **Crítico** | Backend | HEALTHCHECK do Dockerfile usa `wget`, mas a imagem `eclipse-temurin:17-jre-alpine` **NÃO** inclui `wget` → container marcado como **unhealthy** |
| 3 | 🔴 **Crítico** | Frontend | `.env.production.example` só tem `VITE_API_BASE_URL` — faltam as 7 variáveis `VITE_FIREBASE_*` → Firebase não inicializa no Vercel |
| 4 | 🔴 **Crítico** | Backend | `SecurityConfig.java` tem CORS fallback hardcoded para `localhost:3000` e `webtodo-pink.vercel.app` — se o domínio for diferente, **CORS bloqueia** todas as requisições |
| 5 | 🟡 **Alto** | Backend | Firestore exige **índice composto** `userId ASC + createdAt DESC` que precisa ser criado manualmente — queries **falham** sem ele |
| 6 | 🟡 **Alto** | Backend/Config | `FirebaseConfig.java` importa `javax.annotation.PostConstruct` — com Spring Boot 3.x (Jakarta), isso pode causar **erro de compilação** ou comportamento silencioso |
| 7 | 🟠 **Médio** | Frontend | Tratamento de erros da API apenas loga no console — usuário não vê **nenhum feedback** quando o backend falha |

---

## 📋 Plano de Correção

### Tarefa 1: Corrigir `minify: 'terser'` → `minify: 'esbuild'`

**Arquivos:**
- Modificar: `frontend/vite.config.js`
- Modificar: `frontend/package.json` (opcional — remover dependência não utilizada)

**Diagnóstico:** `vite.config.js` linha 37 define `minify: 'terser'`, mas o pacote `terser` não existe no `package.json`. O build do Vite falha com erro `terser not found` ou similar.

- [ ] **Passo 1: Alterar `vite.config.js` para usar o minificador padrão do Vite (esbuild)**

```js
// frontend/vite.config.js — linha 37
// ALTERAR DE:
        minify: 'terser',

// PARA:
        // minify usa esbuild por padrão no Vite — mais rápido e sem dependência extra
```

Simplesmente **remova** a linha `minify: 'terser',`. O Vite usa `esbuild` como padrão, que é mais rápido e já vem integrado.

- [ ] **Passo 2: Verificar o build localmente**

```bash
cd frontend
npm ci
npm run build
```

Esperado: `✓ built in X.XXs` sem erros. Verificar que `dist/` foi gerado.

- [ ] **Passo 3: Commit**

```bash
git add frontend/vite.config.js
git commit -m "fix: remove terser minifier (not installed), use default esbuild"
```

---

### Tarefa 2: Instalar `wget` no HEALTHCHECK do Dockerfile

**Arquivos:**
- Modificar: `backend/Dockerfile`
- Modificar: `Dockerfile` (raiz — para consistência)

**Diagnóstico:** `eclipse-temurin:17-jre-alpine` é uma imagem Alpine mínima que **não** inclui `wget`. O HEALTHCHECK na linha 32 executa `wget --no-verbose ...` e sempre falha, marcando o container como unhealthy. O Render pode reiniciar ou recusar o deploy.

- [ ] **Passo 1: Adicionar instalação do `wget` no Dockerfile do backend**

```dockerfile
# backend/Dockerfile — após a linha 16 (FROM eclipse-temurin:17-jre-alpine)

FROM eclipse-temurin:17-jre-alpine

# Instalar wget para o healthcheck
RUN apk add --no-cache wget

WORKDIR /app
...
```

- [ ] **Passo 2: Aplicar a mesma correção no Dockerfile da raiz**

```dockerfile
# Dockerfile (raiz) — após a linha equivalente do FROM

FROM eclipse-temurin:17-jre-alpine

# Instalar wget para o healthcheck
RUN apk add --no-cache wget

WORKDIR /app
...
```

- [ ] **Passo 3: Verificar build do Docker localmente**

```bash
docker build -f backend/Dockerfile -t todo-backend:test ./backend
```

Esperado: build concluído sem erros, HEALTHCHECK configurado.

- [ ] **Passo 4: Commit**

```bash
git add backend/Dockerfile Dockerfile
git commit -m "fix: install wget in Docker image for healthcheck"
```

---

### Tarefa 3: Completar `.env.production.example` com variáveis Firebase

**Arquivos:**
- Modificar: `frontend/.env.production.example`

**Diagnóstico:** O arquivo template de produção só contém `VITE_API_BASE_URL`. Faltam todas as 7 variáveis `VITE_FIREBASE_*` que `frontend/src/config/firebase.js` lê via `import.meta.env`. No Vercel, se o usuário seguir o template e só definir a URL da API, o Firebase não será configurado e a autenticação falhará silenciosamente.

- [ ] **Passo 1: Substituir o conteúdo de `frontend/.env.production.example`**

```
# =============================================
# Variáveis de Ambiente — Produção (Vercel)
# =============================================
# Copie este arquivo, preencha os valores e configure
# no painel do Vercel em: Settings > Environment Variables

# URL do backend no Render (ex: https://todo-backend.onrender.com/api)
VITE_API_BASE_URL=https://your-backend-url.onrender.com/api

# Configuração do Firebase (obrigatório para autenticação)
VITE_FIREBASE_API_KEY=
VITE_FIREBASE_AUTH_DOMAIN=
VITE_FIREBASE_PROJECT_ID=
VITE_FIREBASE_STORAGE_BUCKET=
VITE_FIREBASE_MESSAGING_SENDER_ID=
VITE_FIREBASE_APP_ID=
VITE_FIREBASE_MEASUREMENT_ID=
```

- [ ] **Passo 2: Commit**

```bash
git add frontend/.env.production.example
git commit -m "docs: add missing Firebase env vars to production template"
```

---

### Tarefa 4: Corrigir CORS — adicionar log claro e documentar `ALLOWED_ORIGINS`

**Arquivos:**
- Modificar: `backend/src/main/java/com/todo/config/SecurityConfig.java`
- Modificar: `docs/DEPLOYMENT.md` (adicionar troubleshooting)

**Diagnóstico:** `SecurityConfig.java` tem fallback hardcoded (linhas 70-73) que só permite `localhost:3000` e `webtodo-pink.vercel.app`. Se o domínio do Vercel for outro, o CORS bloqueia e o frontend não consegue se comunicar. O `render.yaml` tem `sync: false` para `ALLOWED_ORIGINS`, então essa variável precisa ser configurada manualmente no dashboard do Render — e o checklist de deploy já menciona isso, mas é fácil de passar batido.

- [ ] **Passo 1: Melhorar segurança e logging do CORS no `SecurityConfig.java`**

```java
// backend/src/main/java/com/todo/config/SecurityConfig.java
// ALTERAR o bloco dentro de corsConfigurationSource():

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        List<String> origins;
        
        if (allowedOrigins != null && allowedOrigins.length > 0) {
            origins = Arrays.stream(allowedOrigins)
                    .map(origin -> origin == null ? "" : origin.trim())
                    .filter(origin -> !origin.isBlank())
                    .map(origin -> origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin)
                    .collect(Collectors.toList());
        } else {
            origins = Collections.emptyList();
        }

        if (origins.isEmpty()) {
            // Em produção, CORS sem origens definidas = BLOQUEIA TUDO
            // Isso força o usuário a configurar ALLOWED_ORIGINS explicitamente
            String profile = System.getProperty("spring.profiles.active", "default");
            if ("production".equals(profile)) {
                System.err.println("============================================");
                System.err.println("ERRO: ALLOWED_ORIGINS não configurado em produção!");
                System.err.println("Configure a variável ALLOWED_ORIGINS no Render");
                System.err.println("com a URL do seu frontend no Vercel.");
                System.err.println("Ex: https://seu-app.vercel.app");
                System.err.println("============================================");
            } else {
                // Fallback de desenvolvimento
                origins = Arrays.asList("http://localhost:3000", "http://localhost:5173");
                System.out.println("CORS (dev): " + origins);
            }
        } else {
            System.out.println("CORS configurado com origens: " + origins);
        }
        
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
```

Também adicionar o import no topo do arquivo:
```java
import java.util.Collections;
```

- [ ] **Passo 2: Commit**

```bash
git add backend/src/main/java/com/todo/config/SecurityConfig.java
git commit -m "fix: improve CORS to fail loudly when ALLOWED_ORIGINS is not set in production"
```

---

### Tarefa 5: Criar script/instrução para índice composto do Firestore

**Arquivos:**
- Modificar: `docs/DEPLOYMENT.md` (adicionar seção sobre índice)

**Diagnóstico:** `TaskService.getTasksByUserId()` e `getTasksByUserIdPaginated()` usam `.whereEqualTo("userId").orderBy("createdAt", Direction.DESCENDING)`. O Firestore exige um índice composto para essa query. Sem ele, a API retorna erro 500 com uma mensagem que inclui um link para criar o índice. Isso é confuso para o usuário final e faz parecer que o deploy falhou.

**Como verificar:** Acessar `https://todo-backend.onrender.com/api/tasks` com um token válido. Se retornar erro com `FAILED_PRECONDITION` e mencionar índice, é esse o problema.

- [ ] **Passo 1: Criar o índice composto via Firebase Console**

1. Acessar [Firebase Console](https://console.firebase.google.com/) → projeto `webtodo-b1aa8`
2. Ir em **Firestore Database** → aba **Indexes**
3. Clicar em **Add Index**
4. Preencher:
   - Collection ID: `tasks`
   - Fields:
     - `userId` → Ascending
     - `createdAt` → Descending
   - Query scope: Collection

Alternativamente, executar uma query que gere o erro e clicar no link fornecido na mensagem de erro para criar o índice automaticamente.

- [ ] **Passo 2: Adicionar documentação sobre o índice**

Adicionar ao `docs/DEPLOYMENT.md` após a seção de configuração do Render, antes da seção do Vercel:

```markdown
### Índice Composto do Firestore (OBRIGATÓRIO)

⚠️ **Este passo é essencial!** Sem ele as queries de tasks vão falhar.

1. Após o primeiro deploy do backend, acesse a URL de health:
   `https://todo-backend.onrender.com/api/health`
   
2. Tente acessar as tasks (vai falhar de propósito):
   Use o frontend ou faça uma requisição GET para `/api/tasks`
   
3. O erro retornado conterá um link para criar o índice composto.
   **Clique no link** ou crie manualmente no Firebase Console:
   - Coleção: `tasks`
   - Campos: `userId` (ASC) + `createdAt` (DESC)

4. Aguarde ~2-5 minutos para o índice ser criado.
```

- [ ] **Passo 3: Commit**

```bash
git add docs/DEPLOYMENT.md
git commit -m "docs: add Firestore composite index creation step to deployment guide"
```

---

### Tarefa 6: Verificar e corrigir import `javax.annotation.PostConstruct`

**Arquivos:**
- Verificar: `backend/pom.xml`
- Possível modificar: `backend/src/main/java/com/todo/config/FirebaseConfig.java`

**Diagnóstico:** `FirebaseConfig.java` importa `javax.annotation.PostConstruct` (linha 17). Spring Boot 3.2.2 usa Spring Framework 6.x que migrou para o namespace Jakarta (`jakarta.annotation.PostConstruct`). Se o `pom.xml` não tiver `javax.annotation-api` como dependência explícita, esse import **não compila** — mas o Maven pode estar trazendo via transitiva. Precisamos verificar.

- [ ] **Passo 1: Verificar se `javax.annotation-api` está no classpath**

```bash
cd backend
./mvnw dependency:tree -Dincludes=javax.annotation:javax.annotation-api
```

Se o resultado mostrar a dependência presente, está OK. Se não mostrar nada, precisamos corrigir.

- [ ] **Passo 2a: Se `javax.annotation-api` NÃO estiver presente — migrar para Jakarta**

```java
// backend/src/main/java/com/todo/config/FirebaseConfig.java — linha 17
// ALTERAR DE:
import javax.annotation.PostConstruct;

// PARA:
import jakarta.annotation.PostConstruct;
```

E adicionar a dependência no `pom.xml` (caso não esteja):
```xml
<dependency>
    <groupId>jakarta.annotation</groupId>
    <artifactId>jakarta.annotation-api</artifactId>
</dependency>
```

- [ ] **Passo 2b: Se `javax.annotation-api` estiver presente — manter, mas documentar**

O Spring Boot 3.x ainda tolera `javax.annotation` se a dependência estiver no classpath. Nesse caso, não precisa alterar, mas é boa prática migrar para Jakarta.

- [ ] **Passo 3: Verificar compilação**

```bash
cd backend
./mvnw clean compile
```

Esperado: `BUILD SUCCESS`.

- [ ] **Passo 4: Commit** (apenas se houve alteração)

```bash
git add backend/src/main/java/com/todo/config/FirebaseConfig.java
git commit -m "fix: migrate javax.annotation.PostConstruct to jakarta for Spring Boot 3.x compatibility"
```

---

### Tarefa 7: Melhorar tratamento de erros no frontend

**Arquivos:**
- Modificar: `frontend/src/services/api.js`
- Criar: `frontend/src/components/ErrorMessage.jsx`
- Modificar: `frontend/src/components/Dashboard.jsx`

**Diagnóstico:** O interceptor de resposta do Axios (`api.js`) apenas faz `console.error()`. Quando o backend falha (Render cold start, CORS, etc.), o usuário vê uma tela em branco ou "Carregando..." eterno, sem entender o que aconteceu. Isso gera falsos positivos de "deploy quebrado" quando na verdade é um cold start ou erro de rede.

- [ ] **Passo 1: Criar componente de mensagem de erro**

```jsx
// Criar: frontend/src/components/ErrorMessage.jsx

import { useState, useEffect } from 'react';

/**
 * Exibe mensagens de erro da aplicação com auto-dismiss.
 * Registra um listener global para erros da API.
 */
export function ErrorMessage() {
    const [error, setError] = useState(null);

    useEffect(() => {
        const handler = (e) => {
            if (e.detail?.message) {
                setError(e.detail.message);
                setTimeout(() => setError(null), 8000);
            }
        };
        window.addEventListener('app:error', handler);
        return () => window.removeEventListener('app:error', handler);
    }, []);

    if (!error) return null;

    return (
        <div className="error-banner">
            <span className="error-icon">⚠️</span>
            <span className="error-text">{error}</span>
            <button className="error-close" onClick={() => setError(null)}>×</button>
        </div>
    );
}

// Função utilitária para disparar erros de qualquer lugar
export function showError(message) {
    window.dispatchEvent(new CustomEvent('app:error', { detail: { message } }));
}
```

- [ ] **Passo 2: Atualizar o interceptor do Axios para disparar erros visuais**

```js
// frontend/src/services/api.js — substituir o interceptor de resposta

// Interceptor de Response: Trata erros globalmente
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    cachedToken = null;
                    tokenExpiresAt = 0;
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Sessão expirada. Faça login novamente.' }
                    }));
                    break;
                case 403:
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Acesso negado a este recurso.' }
                    }));
                    break;
                case 404:
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Recurso não encontrado.' }
                    }));
                    break;
                case 500:
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Erro interno do servidor. Tente novamente em instantes.' }
                    }));
                    break;
                default:
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Erro na requisição. Tente novamente.' }
                    }));
            }
        } else if (error.request) {
            // Sem resposta = backend offline ou cold start
            window.dispatchEvent(new CustomEvent('app:error', { 
                detail: { message: 'Servidor não está respondendo. O backend pode estar "acordando" (até 30s no plano gratuito).' }
            }));
        } else {
            window.dispatchEvent(new CustomEvent('app:error', { 
                detail: { message: 'Erro de conexão. Verifique sua internet.' }
            }));
        }
        return Promise.reject(error);
    }
);
```

- [ ] **Passo 3: Adicionar o ErrorMessage ao Dashboard**

```jsx
// frontend/src/components/Dashboard.jsx — adicionar no topo do JSX

import { ErrorMessage } from './ErrorMessage';

// Dentro do return, como primeiro elemento:
return (
    <div className="dashboard">
        <ErrorMessage />
        {/* resto do conteúdo... */}
    </div>
);
```

- [ ] **Passo 4: Adicionar estilos para o banner de erro**

```css
/* Adicionar em frontend/src/styles/App.css */

.error-banner {
    position: fixed;
    top: 16px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 9999;
    background: #dc3545;
    color: white;
    padding: 12px 20px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 10px;
    box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
    max-width: 90vw;
    animation: slideDown 0.3s ease-out;
}

.error-banner .error-icon { font-size: 18px; }
.error-banner .error-text { flex: 1; font-size: 14px; }
.error-banner .error-close {
    background: none; border: none; color: white;
    font-size: 20px; cursor: pointer; padding: 0 4px;
}

@keyframes slideDown {
    from { opacity: 0; transform: translateX(-50%) translateY(-20px); }
    to { opacity: 1; transform: translateX(-50%) translateY(0); }
}
```

- [ ] **Passo 5: Verificar build**

```bash
cd frontend
npm run build
```

Esperado: `BUILD SUCCESS`.

- [ ] **Passo 6: Commit**

```bash
git add frontend/src/services/api.js frontend/src/components/ErrorMessage.jsx frontend/src/components/Dashboard.jsx frontend/src/styles/App.css
git commit -m "feat: add user-visible error messages for API failures"
```

---

### Tarefa 8: Checklist final de verificação pós-correções

- [ ] **Passo 1: Rodar build completo do frontend**

```bash
cd frontend
npm ci
npm run build
```

Esperado: build sem erros, `dist/` gerado.

- [ ] **Passo 2: Rodar build do backend**

```bash
cd backend
./mvnw clean package -DskipTests
```

Esperado: `BUILD SUCCESS`, JAR em `target/`.

- [ ] **Passo 3: Build Docker local**

```bash
docker build -f backend/Dockerfile -t todo-backend:test ./backend
```

Esperado: build sem erros, HEALTHCHECK configurado.

- [ ] **Passo 4: Push para GitHub e verificar CI/CD**

```bash
git push origin main
```

Verificar [GitHub Actions](https://github.com/usuario/webtodo/actions) — todos os jobs devem passar (backend, frontend, docker).

- [ ] **Passo 5: Verificar deploy no Render**

1. Acessar [Render Dashboard](https://dashboard.render.com/)
2. Verificar se o deploy foi acionado automaticamente
3. Verificar logs — procurar por `CORS configurado com origens:` e `Firebase Admin SDK inicializado`
4. Testar health: `https://todo-backend.onrender.com/api/health`

- [ ] **Passo 6: Configurar `ALLOWED_ORIGINS` no Render**

No dashboard do Render → todo-backend → Environment:
```
ALLOWED_ORIGINS = https://seu-dominio.vercel.app
```

- [ ] **Passo 7: Configurar variáveis Firebase no Vercel**

No dashboard do Vercel → projeto → Settings → Environment Variables, adicionar todas:
```
VITE_API_BASE_URL = https://todo-backend.onrender.com/api
VITE_FIREBASE_API_KEY = (copiar do .env local)
VITE_FIREBASE_AUTH_DOMAIN = (copiar do .env local)
VITE_FIREBASE_PROJECT_ID = (copiar do .env local)
VITE_FIREBASE_STORAGE_BUCKET = (copiar do .env local)
VITE_FIREBASE_MESSAGING_SENDER_ID = (copiar do .env local)
VITE_FIREBASE_APP_ID = (copiar do .env local)
VITE_FIREBASE_MEASUREMENT_ID = (copiar do .env local)
```

- [ ] **Passo 8: Verificar deploy no Vercel**

1. Acessar [Vercel Dashboard](https://vercel.com/)
2. Verificar se o build passou (sem erro de `terser`)
3. Abrir o site de produção
4. Testar: registro → login → criar task → editar → deletar

- [ ] **Passo 9: Criar índice composto no Firestore**

1. Acessar Firebase Console → Firestore → Indexes
2. Criar índice: `tasks` / `userId ASC` + `createdAt DESC`
3. Aguardar ~2-5 minutos

- [ ] **Passo 10: Commit final com todas as alterações**

```bash
git push origin main
```

---

## 📊 Resumo das Alterações

| Arquivo | Tipo | Mudança |
|---|---|---|
| `frontend/vite.config.js` | 🔧 Fix | Remover `minify: 'terser'` (usa esbuild padrão) |
| `backend/Dockerfile` | 🔧 Fix | Adicionar `RUN apk add --no-cache wget` |
| `Dockerfile` (raiz) | 🔧 Fix | Adicionar `RUN apk add --no-cache wget` |
| `frontend/.env.production.example` | 📝 Doc | Adicionar 7 variáveis `VITE_FIREBASE_*` |
| `backend/.../SecurityConfig.java` | 🔧 Fix | Melhorar CORS: logar erro claramente em produção |
| `docs/DEPLOYMENT.md` | 📝 Doc | Adicionar passo do índice composto Firestore |
| `backend/.../FirebaseConfig.java` | 🔧 Fix | Migrar `javax.annotation` → `jakarta.annotation` (se necessário) |
| `frontend/src/services/api.js` | ✨ Feat | Erros da API agora mostram banner visível |
| `frontend/src/components/ErrorMessage.jsx` | ✨ Novo | Componente de banner de erro com auto-dismiss |
| `frontend/src/components/Dashboard.jsx` | ✨ Feat | Integrar ErrorMessage |
| `frontend/src/styles/App.css` | ✨ Feat | Estilos do banner de erro |

---

## 🔄 Ordem de Execução Recomendada

1. **Tarefa 1** (terser) — resolve build do frontend imediatamente
2. **Tarefa 2** (wget) — resolve healthcheck do backend
3. **Tarefa 3** (env vars) — documenta variáveis necessárias
4. **Tarefa 4** (CORS) — evita bloqueios de requisição
5. **Tarefa 5** (índice Firestore) — evita queries quebradas
6. **Tarefa 6** (javax.annotation) — verifica compatibilidade
7. **Tarefa 7** (erros no frontend) — melhora diagnóstico para o usuário
8. **Tarefa 8** (checklist final) — validação completa

As tarefas 1-2-3-4 podem ser feitas em paralelo. As tarefas 5-6-7 são independentes entre si. A tarefa 8 é sequencial (depende de todas as anteriores).

---

**Plano concluído.** Dois modos de execução:

1. **Subagent-Driven (recomendado)** — Disparo um subagent por tarefa, reviso entre tarefas, iteração rápida
2. **Execução Inline** — Executo as tarefas nesta sessão, com checkpoints para revisão

**Qual abordagem você prefere?**
