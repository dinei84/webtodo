# Security Policy

## 🔒 Reporting Security Vulnerabilities

Se você descobrir uma vulnerabilidade de segurança neste projeto, **NÃO** crie uma issue pública.

### Como Reportar

Por favor, reporte vulnerabilidades de segurança enviando um email para:

📧 **[seu-email@exemplo.com]** (substitua pelo seu email)

Incluir:
- Descrição da vulnerabilidade
- Passos para reproduzir
- Impacto potencial
- Possível solução (se houver)

Responderemos dentro de **48 horas** e trabalharemos com você para resolver o problema.

## 🛡️ Práticas de Segurança Implementadas

### Backend
- ✅ Autenticação via Firebase ID Tokens (JWT)
- ✅ Validação de tokens em todos os endpoints protegidos
- ✅ Isolamento de dados por usuário
- ✅ CORS configurado com origens específicas
- ✅ Stateless authentication (sem sessões)
- ✅ HTTPS obrigatório em produção
- ✅ Health check endpoint público

### Frontend
- ✅ Tokens JWT armazenados de forma segura
- ✅ Auto-refresh de tokens
- ✅ Proteção de rotas privadas
- ✅ Validação de inputs
- ✅ HTTPS obrigatório em produção

### Credenciais e Secrets
- ✅ Firebase credentials em variáveis de ambiente
- ✅ `.gitignore` protegendo arquivos sensíveis
- ✅ Nenhuma credencial hardcoded no código
- ✅ Variáveis de ambiente para todas as configs sensíveis

## ⚠️ Responsabilidades do Usuário

Ao fazer deploy desta aplicação, você é responsável por:

1. **Proteger suas credenciais Firebase**
   - Nunca commitar `firebase-service-account.json`
   - Usar variáveis de ambiente em produção
   - Rotacionar credenciais comprometidas

2. **Configurar CORS adequadamente**
   - Apenas origens confiáveis
   - Não usar `*` em produção

3. **Manter dependências atualizadas**
   - Verificar e aplicar updates de segurança
   - Usar Dependabot para automação

4. **Revisar Firebase Rules**
   - Garantir que apenas usuários autenticados acessam seus dados
   - Nunca permitir acesso público a dados sensíveis

## 🔐 Checklist de Segurança

Antes de fazer deploy em produção, verifique:

- [ ] Firebase credentials em variáveis de ambiente (não no código)
- [ ] CORS configurado com URLs específicas (não `*`)
- [ ] HTTPS habilitado (automático em Vercel e Render)
- [ ] Firestore rules configuradas corretamente
- [ ] Nenhum secret ou API key commitado no Git
- [ ] Dependências atualizadas
- [ ] Authentication habilitada no Firebase
- [ ] Environment variables configuradas nos serviços de deploy

## 🚨 Vulnerabilidades Conhecidas

Nenhuma vulnerabilidade conhecida no momento.

Última atualização: 2026-02-04

## 📋 Versões Suportadas

| Versão | Suportada          |
| ------ | ------------------ |
| 1.x    | :white_check_mark: |

## 🔄 Processo de Resposta a Incidentes

1. **Reporte** → Recebemos sua notificação
2. **Confirmação** → Confirmamos o problema (até 48h)
3. **Patch** → Desenvolvemos e testamos correção
4. **Release** → Publicamos versão corrigida
5. **Disclosure** → Divulgamos detalhes após patch

## 📚 Recursos de Segurança

- [Firebase Security Rules](https://firebase.google.com/docs/rules)
- [Spring Security](https://spring.io/projects/spring-security)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Vercel Security](https://vercel.com/docs/security)
- [Render Security](https://render.com/docs/security)

## 🙏 Agradecimentos

Agradecemos aos pesquisadores de segurança que reportam vulnerabilidades de forma responsável.

---

**Segurança é uma responsabilidade compartilhada. Obrigado por ajudar a manter este projeto seguro!** 🔒
