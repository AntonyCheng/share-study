import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login, logout, getInfo, register, getCurrentUser } from '@/api/login'
import { YApiResult } from '@/api/interface'
// import { reject } from 'lodash';
import { useRouter } from 'vue-router'

export interface LoginMember {
  account: string,
  password: string
}

export interface RegisterMember {
  account: string, // 账号
  password: string, // 密码
  checkPassword: string, // 确认密码
  name: string, // 姓名
  email: string, // 邮箱
  gender: number | string, // 性别
  avatar: string, // 头像
  belong: string, // 院校代码
}

export interface UserInfoMember {
  account: string;
  avatar: string;
  collegeName: string;
  createTime: string;
  email: string;
  gender: number;
  id: string;
  isLogin?: boolean;
  messageNumber: number;
  name: string;
  role: number;
  score: number;
}
// router对象
const router = useRouter()

export const useUserStore = defineStore('user', {
  // store选项
  state: () => ({
    userInfo: {

    },
    token: getToken(),
  }),

  actions: {
    // 登录 => 获取 用户信息
    login(userInfo: LoginMember) {
      const { account, password } = userInfo;

      return new Promise<YApiResult | UserInfoMember>((resolve, reject) => {
        login({ account: account.trim(), password: password }).then((response) => {
          const { data } = response; // 类型断言

          // 获取用户信息
          if (!data) {
            return reject('验证失败，请重新登录.')
          }

          // token鉴权

          const result: UserInfoMember = { ...data, isLogin: true }
          // 修改state
          this.token = JSON.stringify(result)
          setToken(JSON.stringify(result)) // localhost本地存储
          this.userInfo = result;

          resolve(result) // 传递
        }).catch(error => {
          reject(error)
        })
      })

    },

    // 获取用户信息
    getInfo(id: string) {
      return new Promise<YApiResult | UserInfoMember>((resolve, reject) => {
        getCurrentUser(id).then((response) => {
          const { data } = response;

          // const { name, avatar } = data
          const result: UserInfoMember = { ...data, isLogin: true }
          // 修改state
          this.token = JSON.stringify(result)
          setToken(JSON.stringify(result)) // localhost本地存储
          this.userInfo = result;

          resolve(result)
          return result;
        }).catch(error => {
          reject(error)
          return error;
        })
      })
    },

    // 用户登出
    logout() {

      // let {$userInfo} = Vue.prototype; // 全局属性相关
      // $userInfo = undefined;
      // // 清除用户数据
      // console.log('$userInfo=>',$userInfo);
      return new Promise((resolve, reject) => {
        logout().then(() => {
          removeToken() // 清除token
          // 跳转路由
          if (window.location.href.split('#')[1] !== '/intro') {
            // router.push(`/login`);
            router.push({ path: "/intro" })
          }
          // resetRouter()
          // commit('RESET_STATE') // 重置state
          // commit('SET_ISLOGIN', false)
          this.userInfo = {}
          resolve(false)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // remove token
    resetToken() {
      return new Promise(resolve => {
        removeToken() // must remove  token  first
        // commit('SET_ISLOGIN', false)
        // store.$reset() // 重置state
        resolve(false)
      })
    },
  }
})