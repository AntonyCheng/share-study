import { ElMessageBox } from 'element-plus';
// Vue 核心
import { createApp } from 'vue'
// import './style.css'
import App from '@/App.vue'

import router from './router'
import { createPinia } from 'pinia'

/* UI库 element-plus */
import ElementPlus from 'element-plus'
import 'element-plus/es/components/message/style/css'
// 单独引入ElMessageBox
import 'element-plus/es/components/message-box/style/css'

// lottiefiles
import Vue3Lottie from 'vue3-lottie'
import 'vue3-lottie/dist/style.css'


import '@/assets/icon/iconfont.css' // 图标字体相关
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 图标
import { ElMessage } from 'element-plus'



// import eventBus from '@libs/eventBus' // Vue3事件解决方案

/* 网页国际化 */
// import i18n from './language/i18n'


const app = createApp(App)
// 全局组件
app.provide('$message', ElMessage) // element-plus消息组件

// app.provide('$bus', eventBus) // element-plus消息组件
// app.config.globalProperties.$message = ElMessage

// 全局变量

// 全局方法

// app.use(i18n);// 网页国际化
app.use(router)
app.use(createPinia()) // 启用 Pinia
app.use(ElementPlus)
app.use(Vue3Lottie, { name: "Vue3Lottie" })
for (const [key, component] of Object.entries(ElementPlusIconsVue)) { // 注册所有图标
  app.component(key, component)
}
app.mount('#app')
