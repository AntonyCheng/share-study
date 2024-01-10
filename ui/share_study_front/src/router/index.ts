

import {
  createRouter,
  createWebHashHistory,
} from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录页',
      keepAlive: false
    },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/register/index.vue'),
    meta: {
      title: '注册页',
      keepAlive: false
    },
  },

  {
    path: '/',
    name: 'home',
    component: () => import('@/views/home/index.vue'),
    meta: {
      title: '首页',
      keepAlive: false
    }
  },
  {
    path: "/sendPost/:id",
    name: "sendPost",
    component: () => import('@/views/sendPost/index.vue'),
    meta: { title: '发布帖子测试' },
  },
  {
    path: "/addPost/:id",
    name: "addPost",
    component: () => import('@/views/addPost/index.vue'),
    meta: { title: '发布帖子测试' },
  },
  {
    path: "/postDetail/:id",
    name: "postDetail",
    component: () => import('@/views/postDetail/index.vue'),
    meta: { title: '帖子详情' },
  },
  {
    path: "/about",
    name: "about",
    component: () => import('@/views/about/index.vue'),
    meta: { title: '关于自己' },
  },
  {
    path: "/intro",
    name: "intro",
    component: () => import('@/views/intro/index.vue'),
    meta: { title: '项目介绍' },
  },
  {
    path: '/test',
    name: 'test',
    component: () => import('@/views/test/index.vue'),
    meta: {
      title: '测试页',
      keepAlive: false
    }
  },
]

// const router = createRouter({
//   history: createWebHistory(import.meta.env.BASE_URL),
//   routes,
// })
const router = createRouter({
  history: createWebHashHistory(),
  routes
})


export default router;