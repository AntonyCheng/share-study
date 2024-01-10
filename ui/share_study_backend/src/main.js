import Vue from 'vue'

import App from './App'
import store from './store'
import router from './router'

import axios from 'axios'
import videojs from 'video.js'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

// elment-ui框架相关
import ElementUI from 'element-ui'
import { Message } from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
// import locale from 'element-ui/lib/locale/lang/en' // lang i18n

// echats
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme

// 分页组件
import Pagination from '@/components/Pagination/index.vue'

import '@/styles/index.scss' // global css

import '@/icons' // icon
import '@/permission' // permission control

import { resetForm, resetLoading } from '@/utils/index'

// 通用表格组件
// import Table from "@/components/Table" /* 废弃 */

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
// if (process.env.NODE_ENV === 'production') {
//   const { mockXHR } = require('../mock')
//   mockXHR()
// }

// set ElementUI lang to EN
// Vue.use(ElementUI, { locale })
// 如果想要中文版 element-ui，按如下方式声明
Vue.use(ElementUI)

/* 全局变量 */
Vue.prototype.tableColumnWidth = '150px'
Vue.prototype.$message = Message // 全局element
Vue.prototype.$echarts = echarts
Vue.prototype.$axios = axios // 请求
Vue.prototype.$video = videojs // 请求
Vue.prototype.$http = 'http://120.46.214.246:8080/api' // map数据相关

/* 全局方法 */
Vue.prototype.resetForm = resetForm
Vue.prototype.resetLoading = resetLoading

/* 全局组件 */
// Vue.component("Table", Table); /* 废弃 */
Vue.component('Pagination', Pagination)

Vue.config.productionTip = false

new Vue({
  el: '#app',
  beforeCreate() {
    // 1) 创建或指定事件总线对象，保存到Vue的原型上
    Vue.prototype.$bus = this
  },
  router,
  store,
  render: h => h(App)
})
