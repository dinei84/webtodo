import api from './api';

/**
 * Service para gerenciar operações de Tasks via API.
 */

// Busca todas as tasks do usuário
export const getTasks = async () => {
    const response = await api.get('/tasks');
    return response.data;
};

// Busca uma task específica por ID
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

// Alterna o status de conclusão de uma task
export const toggleTaskCompletion = async (task) => {
    const updatedTask = { ...task, completed: !task.completed };
    return await updateTask(task.id, updatedTask);
};
