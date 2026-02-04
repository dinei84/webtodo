// Configuração do Firebase Client SDK
// IMPORTANTE: Substitua com suas próprias credenciais do Firebase Console

export const firebaseConfig = {
    apiKey: "AIzaSyBQQLSDbaPLDN4HbCrwasu76F1he2uPjoY",
    authDomain: "webtodo-b1aa8.firebaseapp.com",
    projectId: "webtodo-b1aa8",
    storageBucket: "webtodo-b1aa8.firebasestorage.app",
    messagingSenderId: "105196394814",
    appId: "1:105196394814:web:4ef629aa170608ca1df46f",
    measurementId: "G-MJNYERNKMP"
};

// URL da API Backend
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
