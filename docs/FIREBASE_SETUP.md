# Firebase Service Account Configuration Template

**IMPORTANTE:** Este é um arquivo de exemplo. NÃO commite o arquivo real com suas credenciais!

## Como obter o arquivo de credenciais

1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Selecione seu projeto
3. Clique no ícone de configurações ⚙️ → **Project Settings**
4. Vá para a aba **Service Accounts**
5. Clique em **Generate New Private Key**
6. O arquivo JSON será baixado automaticamente

## Onde colocar o arquivo

Salve o arquivo baixado como:
```
backend/src/main/resources/firebase-service-account.json
```

## Estrutura do arquivo (exemplo)

```json
{
  "type": "service_account",
  "project_id": "seu-project-id",
  "private_key_id": "key-id",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-xxxxx@seu-project-id.iam.gserviceaccount.com",
  "client_id": "123456789",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/..."
}
```

## Alternativa: Usar variável de ambiente

Se preferir não colocar o arquivo no projeto, você pode usar uma variável de ambiente:

### Windows (PowerShell)
```powershell
$env:FIREBASE_CREDENTIALS_PATH="C:\caminho\para\firebase-service-account.json"
```

### Windows (CMD)
```cmd
set FIREBASE_CREDENTIALS_PATH=C:\caminho\para\firebase-service-account.json
```

### Linux/Mac
```bash
export FIREBASE_CREDENTIALS_PATH="/caminho/para/firebase-service-account.json"
```

## Segurança

⚠️ **NUNCA compartilhe ou commite este arquivo!**
- Ele está no `.gitignore` por padrão
- Qualquer pessoa com acesso a este arquivo pode gerenciar seu projeto Firebase
- Em produção, use secrets/variáveis de ambiente do seu provedor de cloud

## Troubleshooting

### Erro: "Failed to initialize Firebase"
- Verifique se o arquivo está no caminho correto
- Confirme que o JSON é válido
- Verifique as permissões do arquivo

### Erro: "Access Denied"
- Confirme que a Service Account tem as permissões necessárias no Firebase Console
- Vá em IAM & Admin → Service Accounts e verifique as roles

### Arquivo não encontrado em produção
- Configure a variável de ambiente `FIREBASE_CREDENTIALS_PATH`
- Use o sistema de secrets do seu provedor de cloud (AWS Secrets Manager, Google Cloud Secret Manager, etc.)
