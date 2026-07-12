import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // SockJS (used by our WebSocket client) expects a Node-style `global` object,
  // which browsers don't have. This maps it to the browser's `globalThis`.
  define: {
    global: 'globalThis',
  },
  server: {
    proxy: {
      // Forward REST + WebSocket to the Spring Boot backend so the browser
      // sees ONE origin (no cross-origin blocking). 'ws: true' proxies the socket.
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
      '/ws': { target: 'http://localhost:8080', ws: true, changeOrigin: true },
      '/uploads': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
})
