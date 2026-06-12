import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import path from 'path'


// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      //'@': path.resolve(__dirname, 'src'),
      'src': path.resolve(__dirname, 'src')  // 添加这个
    },
  },
   server: {
    port: 3000, // 前端开发服务器端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址，请根据实际情况修改
        changeOrigin: true,
        secure: false,  // 如果后端是 http 而不是 https
        // 确保路径正确转发
        rewrite: (path) => path.replace(/^\/api/, '/api')
       
      }
    }
  }
})
