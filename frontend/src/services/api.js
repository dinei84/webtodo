import axios from 'axios';
import { auth } from './firebase';
import { API_BASE_URL } from '../config/firebase';

// Cria uma instância do Axios
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor de Request: Adiciona o Firebase Token em todas as requisições
api.interceptors.request.use(
    async (config) => {
        const user = auth.currentUser;

        if (user) {
            try {
                // Obtém o token JWT do Firebase
                const token = await user.getIdToken();
                config.headers.Authorization = `Bearer ${token}`;
            } catch (error) {
                console.error('Erro ao obter token:', error);
            }
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor de Response: Trata erros globalmente
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            // Resposta com erro do servidor
            switch (error.response.status) {
                case 401:
                    console.error('Não autenticado. Faça login novamente.');
                    // Aqui você pode redirecionar para login
                    break;
                case 403:
                    console.error('Acesso negado.');
                    break;
                case 404:
                    console.error('Recurso não encontrado.');
                    break;
                case 500:
                    console.error('Erro interno do servidor.');
                    break;
                default:
                    console.error('Erro na requisição:', error.response.data);
            }
        } else if (error.request) {
            console.error('Sem resposta do servidor:', error.request);
        } else {
            console.error('Erro:', error.message);
        }

        return Promise.reject(error);
    }
);

export default api;
