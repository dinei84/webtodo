# ✅ Checklist de Deploy - Todo List App

Use este checklist para garantir que todos os passos foram completados corretamente.

## 📋 Pré-Deploy

### Configuração Inicial
- [ ] Projeto Firebase criado
- [ ] Authentication ativada (Email/Password)
- [ ] Firestore Database criada
- [ ] Firestore Rules configuradas
- [ ] Firebase credentials baixadas (`firebase-service-account.json`)
- [ ] `.gitignore` configurado corretamente
- [ ] Nenhuma credencial no código-fonte

### Testes Locais
- [ ] Backend roda localmente (`mvnw spring-boot:run`)
- [ ] Frontend roda localmente (`npm run dev`)
- [ ] Login funciona
- [ ] CRUD de tarefas funciona
- [ ] Filtros funcionam
- [ ] Estatísticas aparecem corretamente
- [ ] Logout funciona

## 🔧 GitHub

### Repositório
- [ ] Repositório criado no GitHub
- [ ] Nome: `webtodo` ou similar
- [ ] Visibilidade definida (público/privado)
- [ ] README atualizado com suas informações

### Arquivos
- [ ] `.gitignore` commitado
- [ ] `LICENSE` commitado
- [ ] `CONTRIBUTING.md` disponível
- [ ] `SECURITY.md` disponível
- [ ] Documentação de deploy incluída

### Segurança
- [ ] Verificado que `firebase-service-account.json` **NÃO** está no Git
- [ ] Verificado que `.env` **NÃO** está no Git
- [ ] Secrets e credenciais protegidos

### Push Inicial
- [ ] `git init` executado
- [ ] `git add .` executado
- [ ] `git commit -m "Initial commit"` executado
- [ ] Remote adicionado (`git remote add origin ...`)
- [ ] Push realizado (`git push -u origin main`)

## 🚀 Deploy Backend (Render)

### Conta e Configuração
- [ ] Conta criada no Render.com
- [ ] Repositório GitHub conectado ao Render
- [ ] Web Service criado

### Configurações do Serviço
- [ ] Name: `todo-backend` ou similar
- [ ] Region selecionada (Oregon recomendado)
- [ ] Branch: `main`
- [ ] Runtime: Docker
- [ ] Dockerfile path: `./backend/Dockerfile`

### Environment Variables
- [ ] `FIREBASE_CREDENTIALS_JSON` configurada
  - [ ] JSON válido
  - [ ] Sem aspas adicionais
  - [ ] Formato minificado (uma linha)
- [ ] `SPRING_PROFILES_ACTIVE=production` configurada
- [ ] `ALLOWED_ORIGINS` configurada (atualizar depois com URL do Vercel)

### Deploy e Validação
- [ ] Deploy iniciado
- [ ] Build concluído com sucesso
- [ ] Health check passou
- [ ] URL do backend copiada (ex: `https://todo-backend.onrender.com`)
- [ ] Testado endpoint: `https://seu-backend.onrender.com/api/health`
- [ ] Resposta: `{"status":"UP","service":"todo-backend"}`

## 🌐 Deploy Frontend (Vercel)

### Conta e Configuração
- [ ] Conta criada no Vercel.com
- [ ] Repositório GitHub conectado ao Vercel
- [ ] Projeto importado

### Configurações do Projeto
- [ ] Framework: Vite detectado automaticamente
- [ ] Root Directory: `frontend`
- [ ] Build Command: `npm run build`
- [ ] Output Directory: `dist`

### Environment Variables
- [ ] `VITE_API_BASE_URL` configurada
  - [ ] Valor: URL do backend Render (ex: `https://todo-backend.onrender.com/api`)
  - [ ] Sem trailing slash
  - [ ] Incluindo `/api` no final

### Deploy e Validação
- [ ] Deploy iniciado
- [ ] Build concluído com sucesso
- [ ] Preview URL gerada
- [ ] URL de produção copiada (ex: `https://todo-app-xyz.vercel.app`)
- [ ] Site acessível

## 🔄 Integração

### Atualizar CORS
- [ ] Voltar ao Render → Backend Service
- [ ] Ir em Environment
- [ ] Atualizar `ALLOWED_ORIGINS` com URL do Vercel
- [ ] Exemplo: `https://todo-app-xyz.vercel.app`
- [ ] Aguardar redeploy automático (~2-3 minutos)
- [ ] Verificar logs do redeploy

### Testar Integração Completa
- [ ] Acessar URL do Vercel
- [ ] Fazer registro de novo usuário
- [ ] Login funciona
- [ ] Criar tarefa - **SUCESSO** (se falhar, verificar CORS)
- [ ] Editar tarefa - **SUCESSO**
- [ ] Marcar como concluída - **SUCESSO**
- [ ] Deletar tarefa - **SUCESSO**
- [ ] Filtros funcionam
- [ ] Estatísticas atualizam
- [ ] Logout funciona

## 📊 CI/CD (Opcional mas Recomendado)

### GitHub Actions
- [ ] Workflow CI/CD configurado (`.github/workflows/ci-cd.yml`)
- [ ] Push no GitHub dispara workflow
- [ ] Build do backend passa
- [ ] Build do frontend passa
- [ ] Docker build passa (se configurado)

### Auto-Deploy
- [ ] Push no GitHub → Vercel auto-deploy
- [ ] Push no GitHub → Render auto-deploy
- [ ] Preview deployments configurados para PRs

## 🔒 Segurança

### Credenciais
- [ ] Firebase credentials em variáveis de ambiente ✅
- [ ] Nenhum secret hardcoded ✅
- [ ] `.gitignore` protegendo arquivos sensíveis ✅

### CORS
- [ ] CORS configurado com URL específica ✅
- [ ] **NÃO** usando `*` em produção ✅

### HTTPS
- [ ] Frontend servido via HTTPS (Vercel automático) ✅
- [ ] Backend servido via HTTPS (Render automático) ✅

### Firebase
- [ ] Firestore Rules ativas ✅
- [ ] Authentication requirendo email/password ✅

## 📝 Documentação

### Atualizar Documentação
- [ ] URLs de produção no README
- [ ] Badges de deploy atualizados
- [ ] Links de demo funcionando
- [ ] Screenshots atualizados (se houver)

### Compartilhar
- [ ] README com instruções claras
- [ ] DEPLOYMENT.md disponível para outros devs
- [ ] QUICK_DEPLOY.md para início rápido

## 🎉 Finalização

### Últimos Passos
- [ ] Testar em diferentes navegadores
  - [ ] Chrome
  - [ ] Firefox
  - [ ] Safari (se disponível)
  - [ ] Edge
- [ ] Testar em mobile (responsividade)
- [ ] Verificar performance (Lighthouse)
- [ ] Configurar monitoring (opcional)
- [ ] Configurar alertas (opcional)

### Compartilhamento
- [ ] URL compartilhada com stakeholders
- [ ] Feedback coletado
- [ ] Bugs reportados (se houver)
- [ ] Iterações planejadas

## 📈 Monitoramento Contínuo

### Vercel
- [ ] Analytics configurado
- [ ] Error tracking ativo
- [ ] Performance monitoring

### Render
- [ ] Logs sendo monitorados
- [ ] Health checks ativos
- [ ] Alertas configurados (plano pago)

### Firebase
- [ ] Usage monitoring
- [ ] Quotas verificadas
- [ ] Billing alerts (se no plano Blaze)

---

## 🆘 Troubleshooting

Se algo não funcionar, verifique:

1. **Frontend não carrega**
   - [ ] Build passou no Vercel?
   - [ ] Variáveis de ambiente configuradas?
   - [ ] Erros no console do navegador?

2. **Backend não responde**
   - [ ] Service está "awake" (Render free tier)?
   - [ ] Health check passa?
   - [ ] Logs do Render mostram erros?

3. **CORS errors**
   - [ ] `ALLOWED_ORIGINS` tem a URL correta?
   - [ ] URL tem trailing slash (deve remover)?
   - [ ] Backend foi redeployed após mudar CORS?

4. **Authentication não funciona**
   - [ ] Firebase credentials válidas?
   - [ ] `FIREBASE_CREDENTIALS_JSON` é um JSON válido?
   - [ ] Firebase Auth está ativado?

5. **Tasks não salvam**
   - [ ] Firestore rules permitem write?
   - [ ] Token JWT está sendo enviado?
   - [ ] Backend consegue conectar ao Firestore?

---

## ✅ Deploy Completo!

**Parabéns!** 🎉 Se todos os itens acima estão marcados, seu deploy está completo e funcionando!

**URLs da sua aplicação:**
- Frontend: `_____________________________`
- Backend: `_____________________________`
- Repositório: `_____________________________`

**Data do Deploy:** `_____________________________`

**Próximos Passos:**
- Monitorar uso e performance
- Coletar feedback dos usuários
- Planejar próximas features
- Manter dependências atualizadas

---

**Precisa de ajuda?** Consulte [DEPLOYMENT.md](DEPLOYMENT.md) ou abra uma issue!
