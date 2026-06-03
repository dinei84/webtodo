import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import TodoList from './TodoList';
import { ErrorMessage } from './ErrorMessage';
import '../styles/Dashboard.css';

const Dashboard = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await logout();
            navigate('/login');
        } catch (error) {
            console.error('Erro ao fazer logout:', error);
        }
    };

    return (
        <div className="dashboard-container">
            <ErrorMessage />
            <header className="dashboard-header">
                <div className="header-content">
                    <h1>📝 Minhas Tarefas</h1>
                    <div className="user-info">
                        <span className="user-email">{user?.email}</span>
                        <button onClick={handleLogout} className="logout-button">
                            Sair
                        </button>
                    </div>
                </div>
            </header>

            <main className="dashboard-main">
                <TodoList />
            </main>
        </div>
    );
};

export default Dashboard;
