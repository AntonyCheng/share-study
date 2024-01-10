/* 用户接口 */

import request from '@/utils/request'

/* 登录相关接口 */

/**
 * 登录
 * data
 * post
 *
*/
export function login(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

/**
 * 退出登录
 * 无参数
 */
export function logout() {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}

/* 管理员相关接口 */

/** 根据id获取管理员自己信息
 *
 * id
 * get
*/
export function getInfoAdminSelf(id) {
  return request({
    url: `/admin/getSelf`,
    method: 'get',
    params: { id }
  })
}

/** 普通管理员修改自己信息
 *
  * data = {
      "avatar": "http://dummyimage.com/100x100",
      "account": "ut esse",
      "name": "区般家律高线今",
      "gender": 31,
      "id": 16,
      "belong": 80,
      "password": "occaecat laborum",
      "email": "w.cass@qq.com"
    }
 * put
*/
export function updateInfoAdminSelf(data) {
  return request({
    url: `/admin/updateSelf`,
    method: 'put',
    data
  })
}

/** 超管根据id获取管理员信息
 * id
 * get
*/
export function getInfoAdminId(id) {
  return request({
    url: `/admin/get/${id}`,
    method: 'get'
  })
}

/** 超级管理员修改管理员信息
 *
  * data = {
      "avatar": "http://dummyimage.com/100x100",
      "account": "ut esse",
      "name": "区般家律高线今",
      "gender": 31,
      "id": 16,
      "belong": 80,
      "password": "occaecat laborum",
      "email": "w.cass@qq.com"
    }
 * put
*/
export function updateInfoAdmin(data) {
  return request({
    url: `/admin/update`,
    method: 'put',
    data
  })
}

/** 管理员分页查询（支持懒查询）
 * current,pageSize
 * post
*/
export function getAdminPageInfo(current, pageSize) {
  return request({
    url: `/admin/page/${current}/${pageSize}`,
    // method: 'post',
    method: 'get'

    // params: {
    //   current,
    //   pageSize
    // }
  })
}

/** 管理员信息Excel导出
 * get
*/
export function getAdminExcel() {
  return request({
    url: `/admin/download`,
    method: 'get',
    responseType: 'blob'
  })
}

/* 教师相关接口 */

/** 教师信息Excel导出
 * get
*/
export function getTeacherExcel() {
  return request({
    url: `/teacher/download`,
    method: 'get'
  })
}

/** 删除单个教师
 * id
 * delete
*/
export function deleteOneTeacher(id) {
  return request({
    url: `/teacher/delete/${id}`,
    method: 'delete'
  })
}

/** 批量删除教师
 * data
 * delete
*/
export function deleteTeacher(data) {
  return request({
    url: `/teacher/delete`,
    method: 'delete',
    data
  })
}

/** 获取教师信息
 * id
 * get
*/
export function getTeacherInfo(id) {
  return request({
    url: `/teacher/get/${id}`,
    method: 'get'
  })
}

/** 修改教师信息
 * id
 * put
*/
export function updateTeacherInfo(data) {
  return request({
    url: `/teacher/update`,
    method: 'put',
    data
  })
}

/** 教师分页查询
 * @param {number} current
 * @param {number} pageSize
 * @param {object} body
 *
*/
export function getTeacherPageInfo(current, pageSize, body = {}) {
  return request({
    url: `/teacher/page/${current}/${pageSize}`,
    // data: { name: 123 },
    method: 'get'
    // method: 'post',
  })
}

/** 教师分页查询
 * @param {number} current
 * @param {number} pageSize
 * @param {object} body
 *
*/
export function teacheResetPwd(id) {
  return request({
    url: `/teacher/resetPwd`,
    // data: { name: 123 },
    data: {
      id: id
    },
    method: 'put',
    // method: 'post',
  })
}

// 测试
/* export function login(data) {
  return request({
    url: '/vue-admin-template/user/login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/vue-admin-template/user/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/vue-admin-template/user/logout',
    method: 'post'
  })
}
*/
