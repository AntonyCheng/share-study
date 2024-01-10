import { YApiResult } from './interface';
import request from '@/utils/request'

/** 用户登录
 * @param {object} data
 * @method post
 * @returns {object}
  */
export function login(data: object) {
  return request({
    url: '/teacher/login',
    method: 'post',
    data
  })
}

/** 获取高校name / code
 * @param {string} id
 * @method get
 * @returns {object}
  */
export function getCollegeList() {
  return request({
    url: `/college/list`,
    method: 'get',
  })
}

/** 用户注册
 * @param {object} data
 * @method post
 * @returns {object}
  */
export function register(data: object) {
  return request({
    url: '/teacher/register',
    method: 'post',
    data
  })
}

/** 用户退出
 * @param {null}
 * @method post
 * @returns {null}
  */
export function logout() {
  return request({
    url: '/teacher/logout',
    method: 'post',
  })
}

/** 用户获取信息
 * @param { string} id
 * @method get
 * @returns {object}
  */
export function getInfo(id: string) {
  return request({
    url: `/user/info/get/${id}`,
    method: 'get'
  })
}

/** 用户获取最新信息
 * @param {number | string} id
 * @method get
 * @returns {object}
  */
export function getCurrentUser(id: string) {
  return request({
    url: `/user/login/get/${id}`,
    method: 'get'
  })
}

/** 用户修改自己信息
 * @param {object} data
 * @method post
 * @returns {object}
  */
export function updateUserInfo(data: object) {
  return request({
    url: `/user/info/self/update`,
    method: 'post',
    data
  })
}




