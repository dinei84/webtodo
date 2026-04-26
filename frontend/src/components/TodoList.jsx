import { useState, useEffect, useMemo, useCallback } from 'react';
import { getTasks, createTask, updateTask, deleteTask, toggleTaskCompletion, getTaskCount } from '../services/taskService';
import TaskItem from './TaskItem';
import TaskForm from './TaskForm';
import Pagination from './Pagination';
import '../styles/TodoList.css';

const TodoList = () => {
    const [allTasks, setAllTasks] = useState([]); // Todas as tasks (sem paginacao)
    const [pageTasks, setPageTasks] = useState([]); // Tasks da pagina atual (com paginacao)
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filter, setFilter] = useState('all'); // all, active, completed
    
    // Estado de paginacao
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [totalElements, setTotalElements] = useState(0);
    const [isPaginated, setIsPaginated] = useState(false);
    const PAGE_SIZE = 50;

    useEffect(() => {
        loadTasks();
    }, [currentPage]);

    const loadTasks = async () => {
        try {
            setLoading(true);
            const result = await getTasks(currentPage, PAGE_SIZE);
            
            if (result.paginated) {
                // Modo paginado
                setIsPaginated(true);
                setPageTasks(result.tasks);
                setTotalPages(result.totalPages);
                setTotalElements(result.totalElements);
                setAllTasks([]);
            } else {
                // Modo normal (todas as tasks)
                setIsPaginated(false);
                setAllTasks(result.tasks);
                setPageTasks([]);
                setTotalPages(1);
                setTotalElements(result.totalElements);
            }
            
            setError('');
        } catch (err) {
            console.error('Erro ao carregar tarefas:', err);
            setError('Erro ao carregar tarefas. Tente novamente.');
        } finally {
            setLoading(false);
        }
    };

    const handlePageChange = useCallback((newPage) => {
        setCurrentPage(newPage);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }, []);

    const handleCreateTask = async (taskData) => {
        try {
            const newTask = await createTask(taskData);
            
            if (isPaginated) {
                // Recarrega a pagina atual para manter consistencia
                await loadTasks();
            } else {
                setAllTasks(prev => [newTask, ...prev]);
            }
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
            
            if (isPaginated) {
                setPageTasks(prev => prev.map(task => task.id === id ? updatedTask : task));
            } else {
                setAllTasks(prev => prev.map(task => task.id === id ? updatedTask : task));
            }
        } catch (err) {
            console.error('Erro ao atualizar tarefa:', err);
            setError('Erro ao atualizar tarefa. Tente novamente.');
        }
    };

    const handleToggleTask = async (task) => {
        try {
            const updatedTask = await toggleTaskCompletion(task);
            
            if (isPaginated) {
                setPageTasks(prev => prev.map(t => t.id === task.id ? updatedTask : t));
            } else {
                setAllTasks(prev => prev.map(t => t.id === task.id ? updatedTask : t));
            }
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
            
            if (isPaginated) {
                // Recarrega a pagina atual
                await loadTasks();
            } else {
                setAllTasks(prev => prev.filter(task => task.id !== id));
            }
        } catch (err) {
            console.error('Erro ao deletar tarefa:', err);
            setError('Erro ao deletar tarefa. Tente novamente.');
        }
    };

    // OTIMIZADO: useMemo para filteredTasks
    const filteredTasks = useMemo(() => {
        const tasks = isPaginated ? pageTasks : allTasks;
        switch (filter) {
            case 'active':
                return tasks.filter(task => !task.completed);
            case 'completed':
                return tasks.filter(task => task.completed);
            default:
                return tasks;
        }
    }, [allTasks, pageTasks, filter, isPaginated]);

    // OTIMIZADO: useMemo para stats
    const stats = useMemo(() => {
        const tasks = isPaginated ? pageTasks : allTasks;
        const total = tasks.length;
        const active = tasks.filter(t => !t.completed).length;
        return { total, active, completed: total - active };
    }, [allTasks, pageTasks, isPaginated]);

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
                    <span className="stat-label">Concluidas</span>
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
                    Concluidas
                </button>
            </div>

            <div className="tasks-list">
                {filteredTasks.length === 0 ? (
                    <div className="empty-state">
                        <p>Nenhuma tarefa encontrada</p>
                        <p className="empty-subtitle">
                            {filter === 'all'
                                ? 'Adicione uma nova tarefa acima'
                                : `Nenhuma tarefa ${filter === 'active' ? 'ativa' : 'concluida'}`
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

            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                totalElements={totalElements}
                paginated={isPaginated}
                onPageChange={handlePageChange}
            />
        </div>
    );
};

export default TodoList;
