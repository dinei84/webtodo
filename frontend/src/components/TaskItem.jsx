import { useState } from 'react';
import '../styles/TaskItem.css';

const TaskItem = ({ task, onToggle, onUpdate, onDelete }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [editTitle, setEditTitle] = useState(task.title);
    const [editDescription, setEditDescription] = useState(task.description || '');
    const [editPriority, setEditPriority] = useState(task.priority || 'MEDIUM');

    const handleSave = async () => {
        if (!editTitle.trim()) return;

        await onUpdate(task.id, {
            title: editTitle.trim(),
            description: editDescription.trim(),
            priority: editPriority,
            completed: task.completed,
        });

        setIsEditing(false);
    };

    const handleCancel = () => {
        setEditTitle(task.title);
        setEditDescription(task.description || '');
        setEditPriority(task.priority || 'MEDIUM');
        setIsEditing(false);
    };

    const getPriorityIcon = (priority) => {
        switch (priority) {
            case 'HIGH': return '🔴';
            case 'LOW': return '🟢';
            default: return '🟡';
        }
    };

    if (isEditing) {
        return (
            <div className="task-item editing">
                <div className="edit-form">
                    <input
                        type="text"
                        value={editTitle}
                        onChange={(e) => setEditTitle(e.target.value)}
                        className="edit-input"
                        placeholder="Título da tarefa"
                        maxLength={200}
                    />
                    <textarea
                        value={editDescription}
                        onChange={(e) => setEditDescription(e.target.value)}
                        className="edit-textarea"
                        placeholder="Descrição (opcional)"
                        maxLength={1000}
                        rows={2}
                    />
                    <div className="edit-actions">
                        <select
                            value={editPriority}
                            onChange={(e) => setEditPriority(e.target.value)}
                            className="edit-priority"
                        >
                            <option value="LOW">🟢 Baixa</option>
                            <option value="MEDIUM">🟡 Média</option>
                            <option value="HIGH">🔴 Alta</option>
                        </select>
                        <button onClick={handleSave} className="save-button">Salvar</button>
                        <button onClick={handleCancel} className="cancel-button">Cancelar</button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className={`task-item ${task.completed ? 'completed' : ''}`}>
            <div className="task-checkbox">
                <input
                    type="checkbox"
                    checked={task.completed}
                    onChange={() => onToggle(task)}
                    id={`task-${task.id}`}
                />
                <label htmlFor={`task-${task.id}`}></label>
            </div>

            <div className="task-content" onClick={() => onToggle(task)}>
                <div className="task-header">
                    <span className="task-priority">{getPriorityIcon(task.priority)}</span>
                    <h3 className="task-title">{task.title}</h3>
                </div>
                {task.description && (
                    <p className="task-description">{task.description}</p>
                )}
                <div className="task-meta">
                    <span className="task-date">
                        {new Date(task.createdAt).toLocaleDateString('pt-BR')}
                    </span>
                </div>
            </div>

            <div className="task-actions">
                <button
                    onClick={() => setIsEditing(true)}
                    className="edit-button"
                    title="Editar"
                >
                    ✏️
                </button>
                <button
                    onClick={() => onDelete(task.id)}
                    className="delete-button"
                    title="Excluir"
                >
                    🗑️
                </button>
            </div>
        </div>
    );
};

export default TaskItem;
