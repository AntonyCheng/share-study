/* 高校接口 */

import request from '@/utils/request'

/** 获取单个高校信息
 * @param {string} id
 * @method get
  */
export function getCollegeName(id) {
  return request({
    url: `/college/get/${id}`,
    method: 'get'
  })
}

/** 高校分页查询
 * @param {number} current
 * @param {number} page
 * @method post
 * @returns []
  */
export function getCollegePageInfo(current, page) {
  return request({
    url: `/college/page/${current}/${page}`,
    // method: "post",
    method: 'get'
  })
}

/** 高校ID和对应名称的List
 * @param {null}
 * @method get
 * @returns []
  */
export function getCollegeList() {
  return request({
    url: `/college/list`,
    method: 'get'
  })
}

/** 添加高校信息
 * @param {object} data
 * @method post
 * @returns null
  */
export function addCollegeName(data) {
  return request({
    url: `/college/add`,
    method: 'post',
    data
  })
}

/** 删除高校信息
 * @param {string} id
 * @method delete
 * @returns {null}
  */
export function delCollege(id) {
  return request({
    url: `/college/delete/${id}`,
    method: 'delete'
  })
}

/** 批量删除高校信息
 * @param {object} data
 * @method delete
 * @returns {null}
  */
export function delCollegeBatch(data) {
  return request({
    url: `/college/deleteBatch`,
    method: 'delete',
    data
  })
}

/** 修改高校信息
 * @param {object} data
 * @method put
 * @returns {null}
  */
export function updateCollege(data) {
  return request({
    url: `/college/update`,
    method: 'put',
    data
  })
}

/** 修改高校Excel导出
 * @param {null}
 * @method get
 * @returns {null}
  */
export function getCollegeExcel() {
  return request({
    url: `/college/download`,
    method: 'get'
  })
}

