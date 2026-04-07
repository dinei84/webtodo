# Regras de Segurança do Firestore

Este arquivo contém as regras de segurança recomendadas para o Firestore.

## Como Aplicar

### Via Firebase Console (Recomendado para iniciantes)

1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Selecione seu projeto
3. Vá em **Firestore Database** → **Rules**
4. Copie o conteúdo do arquivo `firestore.rules`
5. Cole na interface web
6. Clique em **Publish**

### Via Firebase CLI (Recomendado para produção)

```bash
# Instalar Firebase CLI (se ainda não instalou)
npm install -g firebase-tools

# Login no Firebase
firebase login

# Inicializar projeto (na raiz do webtodo/)
firebase init firestore

# O CLI vai criar/atualizar firebase.json e firestore.rules

# Deploy das regras
firebase deploy --only firestore:rules
```

## Regras Implementadas

### ✅ Autenticação Obrigatória
Apenas usuários autenticados podem acessar as tasks.

### ✅ Isolamento de Dados
Cada usuário só pode:
- **LER** suas próprias tasks
- **CRIAR** tasks com seu próprio userId
- **ATUALIZAR** apenas suas tasks (sem mudar o userId)
- **DELETAR** apenas suas tasks

### ✅ Validação de Dados
- `title` é obrigatório (1-200 caracteres)
- `userId` é obrigatório
- `completed` é obrigatório

## Testando as Regras

### Via Firebase Console

1. Vá em **Firestore Database** → **Rules**
2. Clique na aba **Rules Playground**
3. Configure o tipo de operação, caminho e dados
4. Simule a autenticação
5. Clique em **Run** para testar

### Exemplos de Testes

#### ✅ Deve Permitir: Usuário ler sua própria task
```
Operation: get
Path: /tasks/task123
Auth: { uid: 'user_abc' }
Data: { userId: 'user_abc', title: 'Test', completed: false }
```

#### ❌ Deve Negar: Usuário ler task de outro
```
Operation: get
Path: /tasks/task123
Auth: { uid: 'user_xyz' }
Data: { userId: 'user_abc', title: 'Test', completed: false }
```

#### ✅ Deve Permitir: Criar task com userId correto
```
Operation: create
Path: /tasks/task456
Auth: { uid: 'user_abc' }
New Data: { userId: 'user_abc', title: 'New Task', completed: false }
```

#### ❌ Deve Negar: Criar task com userId diferente
```
Operation: create
Path: /tasks/task456
Auth: { uid: 'user_abc' }
New Data: { userId: 'user_xyz', title: 'New Task', completed: false }
```

## Modo de Teste vs Produção

### ⚠️ Modo de Teste (NÃO use em produção!)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2024, 12, 31);
    }
  }
}
```

### ✅ Modo de Produção (Use as regras do arquivo firestore.rules)
Implementa validação adequada e isolamento de dados.

## Dicas de Segurança

1. **NUNCA** use `allow read, write: if true;` em produção
2. **SEMPRE** valide o userId nas operações de create/update
3. **SEMPRE** teste as regras antes de fazer deploy
4. Configure **alertas** no Firebase Console para detectar violações
5. Revise as regras regularmente conforme a aplicação evolui

## Logs de Segurança

Para monitorar tentativas de acesso:

1. Vá em **Firestore Database** → **Usage**
2. Configure alertas para:
   - Número alto de negações de acesso
   - Padrões suspeitos de requisições
   - Picos de uso inesperados

## Backup das Regras

Sempre mantenha as regras versionadas no Git e faça backup antes de mudanças:

```bash
# Backup atual
firebase firestore:rules:get > firestore.rules.backup

# Restaurar backup (se necessário)
firebase deploy --only firestore:rules
```

## Limitações

Estas regras **NÃO** protegem contra:
- Usuários autenticados criando muitas tasks (implementar rate limiting se necessário)
- Validação complexa de tipos de dados (o backend já faz isso)
- Ataques DDoS (use Cloud Armor ou similar)

Estas regras **PROTEGEM** contra:
- ✅ Usuário A ler/modificar tasks do usuário B
- ✅ Usuários não autenticados acessarem dados
- ✅ Criação de tasks com userId inválido
- ✅ Modificação do userId em updates

## Próximos Passos

Depois de aplicar as regras, recomendamos:

1. **Testar** extensivamente via Rules Playground
2. **Monitorar** logs de segurança pelos primeiros dias
3. **Adicionar** regras para novas collections conforme crescer
4. **Configurar** alertas de segurança
5. **Documentar** mudanças nas regras

## Recursos Adicionais

- [Documentação Oficial do Firestore Security Rules](https://firebase.google.com/docs/firestore/security/get-started)
- [Best Practices](https://firebase.google.com/docs/firestore/security/rules-conditions)
- [Common Patterns](https://firebase.google.com/docs/firestore/security/rules-structure)
