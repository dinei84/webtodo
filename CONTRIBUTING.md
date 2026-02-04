# Contribuindo para Todo List App

Obrigado por considerar contribuir para este projeto! 🎉

## 📋 Como Contribuir

### 1. Fork e Clone
```bash
# Fork o repositório no GitHub
# Clone seu fork
git clone https://github.com/SEU-USUARIO/webtodo.git
cd webtodo

# Adicione o repositório original como upstream
git remote add upstream https://github.com/dinei84/webtodo.git
```

### 2. Crie um Branch
```bash
git checkout -b feature/minha-nova-feature
# ou
git checkout -b fix/corrigir-bug
```

**Convenção de nomes de branches:**
- `feature/nome-da-feature` - para novas funcionalidades
- `fix/nome-do-bug` - para correções de bugs
- `docs/descricao` - para mudanças na documentação
- `refactor/descricao` - para refatorações
- `test/descricao` - para adicionar ou melhorar testes

### 3. Desenvolvimento

#### Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```

### 4. Commits

Siga o padrão [Conventional Commits](https://www.conventionalcommits.org/):

```bash
git commit -m "feat: adiciona filtro por prioridade"
git commit -m "fix: corrige bug no login"
git commit -m "docs: atualiza README"
git commit -m "style: formata código do TaskService"
git commit -m "refactor: reorganiza estrutura de pastas"
git commit -m "test: adiciona testes para TaskController"
```

**Tipos de commit:**
- `feat`: nova funcionalidade
- `fix`: correção de bug
- `docs`: documentação
- `style`: formatação, falta de ponto-e-vírgula, etc (sem mudança de código)
- `refactor`: refatoração de código
- `test`: adição ou modificação de testes
- `chore`: atualização de tarefas de build, configurações, etc

### 5. Testes

Certifique-se de que seus testes passam:

**Backend:**
```bash
cd backend
./mvnw test
```

**Frontend:**
```bash
cd frontend
npm run lint
npm run build
```

### 6. Push e Pull Request

```bash
git push origin feature/minha-nova-feature
```

Depois vá ao GitHub e abra um Pull Request com:
- Título descritivo
- Descrição detalhada das mudanças
- Screenshots (se aplicável)
- Referência à issue relacionada

## 🎨 Padrões de Código

### Backend (Java)
- Siga as convenções Java padrão
- Use Lombok para reduzir boilerplate
- Adicione JavaDoc para métodos públicos complexos
- Mantenha classes coesas e com responsabilidade única

### Frontend (React)
- Use componentes funcionais com Hooks
- Mantenha componentes pequenos e reutilizáveis
- Use nomes descritivos para variáveis e funções
- Organize imports (React → Third-party → Local)

### CSS
- Use CSS modular (um arquivo por componente)
- Siga a metodologia BEM para nomes de classes
- Prefira variáveis CSS para cores e espaçamentos

## 🐛 Reportando Bugs

Use o [template de bug report](.github/ISSUE_TEMPLATE/bug_report.md) e inclua:
- Descrição clara do bug
- Passos para reproduzir
- Comportamento esperado vs atual
- Screenshots (se aplicável)
- Informações de ambiente

## 💡 Sugerindo Funcionalidades

Use o [template de feature request](.github/ISSUE_TEMPLATE/feature_request.md) e inclua:
- Descrição clara da funcionalidade
- Problema que ela resolve
- Solução proposta
- Mockups/screenshots (se aplicável)

## 📝 Estilo de Código

### Java
```java
// ✅ Bom
public class TaskService {
    private final TaskRepository taskRepository;
    
    public Task createTask(TaskDTO taskDTO, String userId) {
        // Lógica clara e concisa
    }
}

// ❌ Evitar
public class TaskService {
    public Task createTask(TaskDTO taskDTO,String userId){
        // Sem formatação adequada
    }
}
```

### React
```jsx
// ✅ Bom
const TaskItem = ({ task, onUpdate, onDelete }) => {
    const handleComplete = () => {
        onUpdate({ ...task, completed: !task.completed });
    };
    
    return (
        <div className="task-item">
            {/* JSX claro */}
        </div>
    );
};

// ❌ Evitar
const TaskItem = (props) => {
    return <div className="task-item">{/* Sem destructuring */}</div>;
};
```

## 🔍 Review Process

1. **Automated Checks**: CI/CD roda testes automaticamente
2. **Code Review**: Mantenedor revisa o código
3. **Feedback**: Discussão e possíveis mudanças
4. **Approval**: PR é aprovado e merged

## 📚 Recursos

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev/)
- [Firebase Docs](https://firebase.google.com/docs)
- [Vite Docs](https://vitejs.dev/)

## 🙏 Agradecimentos

Toda contribuição é valiosa! Desde correção de typos até grandes features. Obrigado por fazer parte deste projeto! ❤️

---

**Questions?** Abra uma issue ou entre em contato!
