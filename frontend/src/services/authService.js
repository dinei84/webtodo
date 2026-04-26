import {
    signInWithEmailAndPassword,
    createUserWithEmailAndPassword,
    signOut as firebaseSignOut,
    onAuthStateChanged
} from 'firebase/auth';
import { auth } from './firebase';
import { invalidateTokenCache } from './api';

/**
 * Service para autenticacao Firebase.
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

// Registro de novo usuario
export const signUp = async (email, password) => {
    try {
        const userCredential = await createUserWithEmailAndPassword(auth, email, password);
        return userCredential.user;
    } catch (error) {
        console.error('Erro ao criar conta:', error);
        throw error;
    }
};

// Logout - invalida cache de token
export const signOut = async () => {
    try {
        invalidateTokenCache(); // Limpa cache do token
        await firebaseSignOut(auth);
    } catch (error) {
        console.error('Erro ao fazer logout:', error);
        throw error;
    }
};

// Observa mudancas no estado de autenticacao
export const observeAuthState = (callback) => {
    return onAuthStateChanged(auth, callback);
};

// Obtem o usuario atual
export const getCurrentUser = () => {
    return auth.currentUser;
};