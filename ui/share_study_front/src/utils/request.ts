import { ElMessage } from 'element-plus';
import axios, { AxiosRequestConfig, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

import { useUserStore } from "@/store/index";
// import { useRouter } from 'vue-router'
import router from "@/router/index";
import $bus from "@/libs/eventBus";
import { getToken, removeToken } from "@/utils/auth";

const axiosInstance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API, // Vite环境变量
  timeout: 30 * 1000 // 通用设置 30 秒
});

// console.log(import.meta.env)
// 添加请求拦截器
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 在发送请求之前做些什么
    console.log(1);

    return config;
  }, (error: any) => {
    // 处理请求错误
    return Promise.reject(error);
  },
);
// 添加响应拦截器
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;
    console.log(res);

    // useUserStore
    // const userStore = useUserStore(); // store
    // const router = useRouter(); // router


    // console.log(userStore);


    if (res.code !== "00000") {
      // 未登录
      if (res.code === "D0112") {
        console.log("未登录");
        // userStore.logout()
        // console.log('Vue=>',Vue.prototype.$bus);

        if (getToken()) {
          console.log('noLogin');
          // 未登录
          $bus.emit('noLogin', 'noLogin')
          removeToken()
        }

        // if (window.location.href.split('#')[1] !== '/login') {
        if (window.location.href.split('#')[1] !== '/intro') {
          // console.log(1);
          console.log(router);
          // router.push({ path: '/login' });
          router.push({ path: "/intro" })
          // window.location.href = `/login`
        }
      } else if (res.code === 'A0410') {
        ElMessage({
          message: res.message || 'Error',
          type: 'error',
          duration: 2 * 1000
        })
        console.error('请求必填参数为空!!!');
        return Promise.reject(new Error(res.message || 'Error'))
        // console.log(res);
      } else if (res.code === 'A0301') {
        ElMessage({
          message: res.message || 'Error',
          type: 'error',
          duration: 2 * 1000
        })
        console.log("访问未授权!!");
        return Promise.reject(new Error(res.message || 'Error'))
        // console.log(res);
      } else {
        // 暂时关闭全局 error
        ElMessage({
          message: res.message || 'Error',
          type: 'error',
          duration: 2 * 1000
        })

        return Promise.reject(new Error(res.message || 'Error'))
      }
    } else {

      // 对响应数据做点什么
      return res;
    }
  }, (error: any) => {
    // 处理响应错误
    return Promise.reject(error);
  },
);
export default axiosInstance;