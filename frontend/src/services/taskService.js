import api from './api';

/**
 * Service para gerenciar operaÃ§Ãµes de Tasks via API.
 * SUPORTE A PAGINACAO: detecta resposta paginada automaticamente.
 */

// Busca tasks com suporte a paginacao
// Retorna { tasks, paginated, page, totalPages, totalElements }
export const getTasks = async (page = 0, size = 50) => {
    const response = await api.get('/tasks', {
        params: { page, size }
    });

    const data = response.data;

    // Detecta se a resposta Ã© paginada (tem campo paginated)
    if (data.paginated !== undefined) {
        return {
            tasks: data.content,
            paginated: data.paginated,
            page: data.page,
            totalPages: data.totalPages,
            totalElements: data.totalElements,
            hasNext: data.hasNext,
            hasPrevious: data.hasPrevious
        };
    }

    // Resposta legacy (lista simples)
    return {
        tasks: Array.isArray(data) ? data : [],
        paginated: false,
        page: 0,
        totalPages: 1,
        totalElements: Array.isArray(data) ? data.length : 0,
        hasNext: false,
        hasPrevious: false
    };
};

// Busca contagem de tasks
export const getTaskCount = async () => {
    const response = await api.get('/tasks/count');
    return response.data;
};

// Busca uma task especÃ­fica por ID
export const getTaskById = async (id) => {
    const response = await api.get(`/tasks/${id}`);
    return response.data;
};

// Cria uma nova task
export const createTask = async (taskData) => {
    const response = await api.post('/tasks', taskData);
    return response.data;
};

// Atualiza uma task existente
export const updateTask = async (id, taskData) => {
    const response = await api.put(`/tasks/${id}`, taskData);
    return response.data;
};

// Deleta uma task
export const deleteTask = async (id) => {
    await api.delete(`/tasks/${id}`);
};

// Alterna o status de conclusÃ£o de uma task
export const toggleTaskCompletion = async (task) => {
    const updatedTask = { ...task, completed: !task.completed };
    return await updateTask(task.id, updatedTask);
};
