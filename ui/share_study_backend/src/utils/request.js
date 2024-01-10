import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import router from '@/router' // 引入路由
import { getToken, removeToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    // session鉴权

    // token鉴权
    /*     if (store.getters.token) {
          // let each request carry token
          // ['X-Token'] is a custom headers key
          // please modify it according to the actual situation
          config.headers['userLoginState'] = getToken()
        } */

    // 添加 responseType
    if (config.responseType) {
      config['responseType'] = 'blob'
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data

    // if the custom code is not 20000, it is judged as an error.
    if (res.code !== '00000') {
      // if (res.code !== 20000) {

      // 未登录
      if (res.code === 'D0112') {
        console.log('未登录')
        // 清除token
        removeToken('adminLoginstate')
        // 未登录跳转至登录页
        router.push(`/login?redirect=${window.location.hash.replace(/#/, '')}`)
      } else {
        Message({
          message: res.message || 'Error',
          type: 'error',
          duration: 5 * 1000
        })

        // 50008:非法令牌;50012:已登录的其他客户端;50014:令牌过期;
        if (res.code === '50008' || res.code === '50012' || res.code === '50014') {
          // to re-login
          MessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again', 'Confirm logout', {
            confirmButtonText: 'Re-Login',
            cancelButtonText: 'Cancel',
            type: 'warning'
          }).then(() => {
            // token鉴权-清除token
            store.dispatch('user/resetToken').then(() => {
              location.reload()
            })
          })
        }
        return Promise.reject(new Error(res.message || 'Error'))
      }
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
