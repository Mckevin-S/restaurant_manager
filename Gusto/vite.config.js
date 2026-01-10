import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss()
  ],
  // ✅ Ajout pour corriger "global is not defined" (SockJS)
  define: {
    global: 'window',
  },
})