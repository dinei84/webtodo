import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import '../styles/Login.css';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [isRegister, setIsRegister] = useState(false);

    const { login, register } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            if (isRegister) {
                await register(email, password);
            } else {
                await login(email, password);
            }
            navigate('/dashboard');
        } catch (error) {
            console.error('Erro de autenticação:', error);

            // Tratamento de erros do Firebase
            switch (error.code) {
                case 'auth/user-not-found':
                    setError('Usuário não encontrado');
                    break;
                case 'auth/wrong-password':
                    setError('Senha incorreta');
                    break;
                case 'auth/email-already-in-use':
                    setError('Este email já está em uso');
                    break;
                case 'auth/weak-password':
                    setError('A senha deve ter pelo menos 6 caracteres');
                    break;
                case 'auth/invalid-email':
                    setError('Email inválido');
                    break;
                default:
                    setError('Erro ao autenticar. Tente novamente.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="login-header">
                    <h1>📝 Todo List</h1>
                    <p>{isRegister ? 'Crie sua conta' : 'Entre na sua conta'}</p>
                </div>

                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleSubmit} className="login-form">
                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input
                            id="email"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="seuemail@exemplo.com"
                            required
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Senha</label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="••••••••"
                            required
                            minLength={6}
                            disabled={loading}
                        />
                    </div>

                    <button type="submit" className="submit-button" disabled={loading}>
                        {loading ? 'Carregando...' : isRegister ? 'Criar Conta' : 'Entrar'}
                    </button>
                </form>

                <div className="toggle-mode">
                    <p>
                        {isRegister ? 'Já tem uma conta? ' : 'Não tem uma conta? '}
                        <button
                            type="button"
                            onClick={() => {
                                setIsRegister(!isRegister);
                                setError('');
                            }}
                            className="link-button"
                        >
                            {isRegister ? 'Entrar' : 'Criar conta'}
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;
