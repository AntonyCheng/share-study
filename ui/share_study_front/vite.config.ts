
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { chunkSplitPlugin } from 'vite-plugin-chunk-split';

import path from 'path' // 用于路径

/* element-plus按需引入 */
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

const resolve = (dir: string) => path.join(__dirname, dir)
// const VITE_APP_BASE_API = import.meta.env.VITE_APP_BASE_API
// console.log(VITE_APP_BASE_API);


// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    /* element按需引入 */
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
    chunkSplitPlugin({
      strategy: 'default',
    })
  ],
  server: {
    port: 5173,
    host: 'localhost',
    proxy: {
      '/api': {
        target: `http://120.46.214.246:8080`,
        changeOrigin: true,
        rewrite: (path) => path.replace(`/^\/api/`, ''),
      }
    }
  },
  define: {
    'process.env': {}
  },
  resolve: {
    // 配置路径别名
    alias: {
      '@': resolve('src'), // 源码根目录
      '@img': resolve('src/assets/img'), // 图片
      '@less': resolve('src/assets/less'), // 预处理器
      '@libs': resolve('src/libs'), // 本地库
      '@plugins': resolve('src/plugins'), // 本地插件
      '@cp': resolve('src/components'), // 公共组件
      '@views': resolve('src/views'), // 路由组件
    },
    //配置文件扩展名
    extensions: [".mjs", ".js", ".ts", ".jsx", ".tsx", ".json"],
  },
  css: { // css预处理器
    preprocessorOptions: {
      scss: {
        additionalData: `@import '@/assets/scss/variable.scss';`
      }
    }
  }
})

