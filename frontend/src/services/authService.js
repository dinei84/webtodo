import {
    signInWithEmailAndPassword,
    createUserWithEmailAndPassword,
    signOut as firebaseSignOut,
    onAuthStateChanged
} from 'firebase/auth';
import { auth } from './firebase';

/**
 * Service para autenticação Firebase.
 */

// Login com email e senha
export const signIn = async (email, password) => {
    try {
        const userCredential = await signInWithEmailAndPassword(auth, email, password);
        return userCredential.user;
    } catch (error) {
        console.error('Erro ao fazer login:', error);
        throw error;
    }
};

// Registro de novo usuário
export const signUp = async (email, password) => {
    try {
        const userCredential = await createUserWithEmailAndPassword(auth, email, password);
        return userCredential.user;
    } catch (error) {
        console.error('Erro ao criar conta:', error);
        throw error;
    }
};

// Logout
export const signOut = async () => {
    try {
        await firebaseSignOut(auth);
    } catch (error) {
        console.error('Erro ao fazer logout:', error);
        throw error;
    }
};

// Observa mudanças no estado de autenticação
export const observeAuthState = (callback) => {
    return onAuthStateChanged(auth, callback);
};

// Obtém o usuário atual
export const getCurrentUser = () => {
    return auth.currentUser;
};
