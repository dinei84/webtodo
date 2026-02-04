import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { firebaseConfig } from '../config/firebase';

// Inicializa o Firebase
const app = initializeApp(firebaseConfig);

// Inicializa o Firebase Authentication
export const auth = getAuth(app);

export default app;
