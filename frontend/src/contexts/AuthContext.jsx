import { createContext, useContext, useState, useEffect } from 'react';
import { observeAuthState, signIn, signUp, signOut } from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth deve ser usado dentro de um AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const unsubscribe = observeAuthState((user) => {
            setUser(user);
            setLoading(false);
        });

        return unsubscribe;
    }, []);

    const login = async (email, password) => {
        return await signIn(email, password);
    };

    const register = async (email, password) => {
        return await signUp(email, password);
    };

    const logout = async () => {
        await signOut();
        setUser(null);
    };

    const value = {
        user,
        loading,
        login,
        register,
        logout,
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};
