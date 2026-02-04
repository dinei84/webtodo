import { useState, useEffect } from 'react';
import { getTasks, createTask, updateTask, deleteTask, toggleTaskCompletion } from '../services/taskService';
import TaskItem from './TaskItem';
import TaskForm from './TaskForm';
import '../styles/TodoList.css';

const TodoList = () => {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filter, setFilter] = useState('all'); // all, active, completed

    useEffect(() => {
        loadTasks();
    }, []);

    const loadTasks = async () => {
        try {
            setLoading(true);
            const data = await getTasks();
            setTasks(data);
            setError('');
        } catch (err) {
            console.error('Erro ao carregar tarefas:', err);
            setError('Erro ao carregar tarefas. Tente novamente.');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateTask = async (taskData) => {
        try {
            const newTask = await createTask(taskData);
            setTasks([newTask, ...tasks]);
            return true;
        } catch (err) {
            console.error('Erro ao criar tarefa:', err);
            setError('Erro ao criar tarefa. Tente novamente.');
            return false;
        }
    };

    const handleUpdateTask = async (id, taskData) => {
        try {
            const updatedTask = await updateTask(id, taskData);
            setTasks(tasks.map(task => task.id === id ? updatedTask : task));
        } catch (err) {
            console.error('Erro ao atualizar tarefa:', err);
            setError('Erro ao atualizar tarefa. Tente novamente.');
        }
    };

    const handleToggleTask = async (task) => {
        try {
            const updatedTask = await toggleTaskCompletion(task);
            setTasks(tasks.map(t => t.id === task.id ? updatedTask : t));
        } catch (err) {
            console.error('Erro ao alternar tarefa:', err);
            setError('Erro ao atualizar tarefa. Tente novamente.');
        }
    };

    const handleDeleteTask = async (id) => {
        if (!window.confirm('Tem certeza que deseja excluir esta tarefa?')) {
            return;
        }

        try {
            await deleteTask(id);
            setTasks(tasks.filter(task => task.id !== id));
        } catch (err) {
            console.error('Erro ao deletar tarefa:', err);
            setError('Erro ao deletar tarefa. Tente novamente.');
        }
    };

    const getFilteredTasks = () => {
        switch (filter) {
            case 'active':
                return tasks.filter(task => !task.completed);
            case 'completed':
                return tasks.filter(task => task.completed);
            default:
                return tasks;
        }
    };

    const filteredTasks = getFilteredTasks();
    const stats = {
        total: tasks.length,
        active: tasks.filter(t => !t.completed).length,
        completed: tasks.filter(t => t.completed).length,
    };

    if (loading) {
        return <div className="loading">Carregando tarefas...</div>;
    }

    return (
        <div className="todo-list-container">
            {error && <div className="error-banner">{error}</div>}

            <TaskForm onSubmit={handleCreateTask} />

            <div className="stats-bar">
                <div className="stat">
                    <span className="stat-value">{stats.total}</span>
                    <span className="stat-label">Total</span>
                </div>
                <div className="stat">
                    <span className="stat-value">{stats.active}</span>
                    <span className="stat-label">Ativas</span>
                </div>
                <div className="stat">
                    <span className="stat-value">{stats.completed}</span>
                    <span className="stat-label">Concluídas</span>
                </div>
            </div>

            <div className="filter-bar">
                <button
                    className={filter === 'all' ? 'active' : ''}
                    onClick={() => setFilter('all')}
                >
                    Todas
                </button>
                <button
                    className={filter === 'active' ? 'active' : ''}
                    onClick={() => setFilter('active')}
                >
                    Ativas
                </button>
                <button
                    className={filter === 'completed' ? 'active' : ''}
                    onClick={() => setFilter('completed')}
                >
                    Concluídas
                </button>
            </div>

            <div className="tasks-list">
                {filteredTasks.length === 0 ? (
                    <div className="empty-state">
                        <p>📭 Nenhuma tarefa encontrada</p>
                        <p className="empty-subtitle">
                            {filter === 'all'
                                ? 'Adicione uma nova tarefa acima'
                                : `Nenhuma tarefa ${filter === 'active' ? 'ativa' : 'concluída'}`
                            }
                        </p>
                    </div>
                ) : (
                    filteredTasks.map(task => (
                        <TaskItem
                            key={task.id}
                            task={task}
                            onToggle={handleToggleTask}
                            onUpdate={handleUpdateTask}
                            onDelete={handleDeleteTask}
                        />
                    ))
                )}
            </div>
        </div>
    );
};

export default TodoList;
