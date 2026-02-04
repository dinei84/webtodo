import { useState } from 'react';
import '../styles/TaskForm.css';

const TaskForm = ({ onSubmit }) => {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [priority, setPriority] = useState('MEDIUM');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!title.trim()) return;

        setLoading(true);

        const success = await onSubmit({
            title: title.trim(),
            description: description.trim(),
            priority,
            completed: false,
        });

        if (success) {
            setTitle('');
            setDescription('');
            setPriority('MEDIUM');
        }

        setLoading(false);
    };

    return (
        <form onSubmit={handleSubmit} className="task-form">
            <div className="form-row">
                <input
                    type="text"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="O que você precisa fazer?"
                    className="task-input"
                    maxLength={200}
                    disabled={loading}
                    required
                />
                <select
                    value={priority}
                    onChange={(e) => setPriority(e.target.value)}
                    className="priority-select"
                    disabled={loading}
                >
                    <option value="LOW">🟢 Baixa</option>
                    <option value="MEDIUM">🟡 Média</option>
                    <option value="HIGH">🔴 Alta</option>
                </select>
                <button type="submit" className="add-button" disabled={loading}>
                    {loading ? '...' : '+ Adicionar'}
                </button>
            </div>

            {title && (
                <textarea
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    placeholder="Descrição (opcional)"
                    className="description-input"
                    maxLength={1000}
                    rows={2}
                    disabled={loading}
                />
            )}
        </form>
    );
};

export default TaskForm;
