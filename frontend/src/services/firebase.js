import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { firebaseConfig } from '../config/firebase';

const requiredFirebaseKeys = [
    'apiKey',
    'authDomain',
    'projectId',
    'storageBucket',
    'messagingSenderId',
    'appId',
];

const missingFirebaseKeys = requiredFirebaseKeys.filter((key) => !firebaseConfig[key]);

if (missingFirebaseKeys.length > 0) {
    throw new Error(
        `Firebase não configurado. Defina as variáveis VITE_FIREBASE_* ausentes: ${missingFirebaseKeys.join(', ')}`
    );
}

// Inicializa o Firebase
const app = initializeApp(firebaseConfig);

// Inicializa o Firebase Authentication
export const auth = getAuth(app);

export default app;
