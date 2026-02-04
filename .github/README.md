# GitHub Configuration

Esta pasta contém configurações para automação e colaboração no GitHub.

## 📁 Conteúdo

### 🔄 Workflows (`.github/workflows/`)
- **`ci-cd.yml`**: Pipeline de CI/CD
  - Build e test do backend (Maven)
  - Build e test do frontend (npm)
  - Docker build test
  - Executa automaticamente em cada push/PR

### 📋 Issue Templates (`.github/ISSUE_TEMPLATE/`)
- **`bug_report.md`**: Template para reportar bugs
- **`feature_request.md`**: Template para solicitar features

### 🔧 Outros
- **`dependabot.yml`**: Configuração do Dependabot
  - Atualização automática de dependências Maven, npm e GitHub Actions
  - Cria PRs automaticamente toda segunda-feira
  
- **`PULL_REQUEST_TEMPLATE.md`**: Template para Pull Requests
  - Checklist completo para reviews

## 🚀 Como Funciona

### CI/CD Pipeline

Quando você faz push ou abre um PR:

1. **Backend Job**:
   ```
   ✓ Checkout code
   ✓ Setup JDK 17
   ✓ Build with Maven
   ✓ Run tests
   ✓ Upload artifact
   ```

2. **Frontend Job**:
   ```
   ✓ Checkout code
   ✓ Setup Node.js 18
   ✓ Install dependencies
   ✓ Run linter
   ✓ Build
   ✓ Upload artifact
   ```

3. **Docker Job**:
   ```
   ✓ Checkout code
   ✓ Setup Docker Buildx
   ✓ Build Docker image
   ✓ Test image
   ```

### Dependabot

Toda segunda-feira às 09:00:
- Verifica atualizações de dependências
- Cria PRs automaticamente
- Máximo de 5 PRs por vez
- PRs são rotulados e atribuídos automaticamente

## 🔒 Secrets

Para usar workflows que fazem deploy, você precisa configurar secrets:

1. Vá em: Repository → Settings → Secrets and variables → Actions
2. Adicione:
   - `VERCEL_TOKEN`: Token do Vercel (se quiser deploy automático)
   - `RENDER_TOKEN`: Token do Render (se quiser deploy automático)

> ⚠️ **Nota**: No setup atual, deploy é manual via Vercel/Render dashboards

## 📝 Modificando Workflows

### Adicionar novo step

Edite `.github/workflows/ci-cd.yml`:

```yaml
- name: Meu novo step
  run: |
    echo "Executando algo"
```

### Adicionar novo job

```yaml
jobs:
  # ... jobs existentes ...
  
  meu-novo-job:
    name: Meu Job
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
      - name: Fazer algo
        run: echo "Hello"
```

## 🎯 Boas Práticas

✅ Sempre teste workflows em branches antes de mergear para main  
✅ Use cache para dependências (já configurado)  
✅ Mantenha jobs rápidos (< 5 minutos idealmente)  
✅ Use artifacts para compartilhar arquivos entre jobs  
✅ Configure notifications (Settings → Notifications)  

## 📊 Status Badges

Adicione ao README:

```markdown
[![CI/CD](https://github.com/dinei84/webtodo/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/dinei84/webtodo/actions)
```

## 🔗 Recursos

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [Dependabot Docs](https://docs.github.com/en/code-security/dependabot)
