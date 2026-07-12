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
})
