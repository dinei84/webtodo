import axios from 'axios';
import { auth } from './firebase';
import { API_BASE_URL } from '../config/firebase';

// Cria uma instancia do Axios
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Cache de token - Evita chamar getIdToken() em cada request
let cachedToken = null;
let tokenExpiresAt = 0;
const TOKEN_CACHE_TTL = 5 * 60 * 1000; // 5 minutos

// Funcao para obter token com cache
const getCachedToken = async () => {
    const now = Date.now();
    
    if (cachedToken && now < tokenExpiresAt) {
        return cachedToken;
    }
    
    const user = auth.currentUser;
    if (user) {
        cachedToken = await user.getIdToken();
        tokenExpiresAt = now + TOKEN_CACHE_TTL;
        return cachedToken;
    }
    
    return null;
};

// Interceptor de Request: Adiciona o Firebase Token em todas as requisicoes
api.interceptors.request.use(
    async (config) => {
        try {
            const token = await getCachedToken();
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        } catch (error) {
            console.error('Erro ao obter token:', error);
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor de Response: Trata erros globalmente
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    cachedToken = null;
                    tokenExpiresAt = 0;
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Sessão expirada. Faça login novamente.' }
                    }));
                    break;
                case 403:
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Acesso negado a este recurso.' }
                    }));
                    break;
                case 404:
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Recurso não encontrado.' }
                    }));
                    break;
                case 500:
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Erro interno do servidor. Tente novamente em instantes.' }
                    }));
                    break;
                default:
                    window.dispatchEvent(new CustomEvent('app:error', { 
                        detail: { message: 'Erro na requisição. Tente novamente.' }
                    }));
            }
        } else if (error.request) {
            window.dispatchEvent(new CustomEvent('app:error', { 
                detail: { message: 'Servidor não está respondendo. O backend pode estar "acordando" (até 30s no plano gratuito).' }
            }));
        } else {
            window.dispatchEvent(new CustomEvent('app:error', { 
                detail: { message: 'Erro de conexão. Verifique sua internet.' }
            }));
        }
        return Promise.reject(error);
    }
);

// Funcao para invalidar cache (ex: logout)
export const invalidateTokenCache = () => {
    cachedToken = null;
    tokenExpiresAt = 0;
};

export default api;