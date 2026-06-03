import { useState, useEffect } from 'react';

/**
 * Exibe mensagens de erro da aplicação com auto-dismiss.
 * Registra um listener global para erros da API.
 */
export function ErrorMessage() {
    const [error, setError] = useState(null);

    useEffect(() => {
        const handler = (e) => {
            if (e.detail?.message) {
                setError(e.detail.message);
                setTimeout(() => setError(null), 8000);
            }
        };
        window.addEventListener('app:error', handler);
        return () => window.removeEventListener('app:error', handler);
    }, []);

    if (!error) return null;

    return (
        <div className="error-banner">
            <span className="error-icon">⚠️</span>
            <span className="error-text">{error}</span>
            <button className="error-close" onClick={() => setError(null)}>×</button>
        </div>
    );
}

/**
 * Função utilitária para disparar erros de qualquer lugar.
 */
export function showError(message) {
    window.dispatchEvent(new CustomEvent('app:error', { detail: { message } }));
}
