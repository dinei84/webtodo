import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import viteCompression from 'vite-plugin-compression'

export default defineConfig({
    plugins: [
        react(),
        // Compressao gzip/brotli para producao
        viteCompression({
            algorithm: 'gzip',
            ext: '.gz',
            threshold: 8192, // Apenas arquivos > 8KB
        }),
        viteCompression({
            algorithm: 'brotliCompress',
            ext: '.br',
            threshold: 8192,
        }),
    ],
    server: {
        port: 3000,
        host: true,
    },
    build: {
        rollupOptions: {
            output: {
                // Code splitting automatico por rotasy
                manualChunks: {
                    // Bundle separado para Firebase (carregado async)
                    firebase: ['firebase/app', 'firebase/auth'],
                    // Bundle separado para vendor libs
                    vendor: ['react', 'react-dom', 'react-router-dom'],
                },
            },
        },
        // Compressao do chunk final (esbuild — padrão do Vite, mais rápido)
        sourcemap: false,
    },
})