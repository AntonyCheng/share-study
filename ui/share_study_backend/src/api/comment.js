// 评论交流相关接口
import request from '@/utils/request'

/** 管理员 获取评论交流
 * @param {number} current
 * @param {number} pageSize
 * @method get
  */
export function getCommentList(current, pageSize) {
  return request({
    url: `/comment/page/${current}/${pageSize}`,
    method: 'get'
  })
}

// /** 管理员 添加评论交流
//  * @param {object} data
//  * @method post
//   */
// export function addComment(data) {
//   return request({
//     url: `/comment/add/`,
//     method: 'post',
//     data
//   })
// }

/** 管理员 删除评论交流
 * @param {number | string} id
 * @method get
  */
export function deleteComment(id) {
  return request({
    url: `/comment/delete/${id}`,
    method: 'delete'
  })
}

/** 管理员 批量删除评论交流
 * @param {number | string} id
 * @method get
  */
export function deleteBatchComment(id) {
  return request({
    url: `/comment/deleteBatch`,
    method: 'delete'
  })
}

/** 管理员 获取单个评论交流
 * @param {number | string} id
 * @method get
  */
export function getCommentOne(id) {
  return request({
    url: `/comment/get/${id}`,
    method: 'get'
  })
}

/** 管理员 修改评论交流
 * @param {number | string} id
 * @method get
  */
export function updateComment(data) {
  return request({
    url: `/comment/update`,
    method: 'put',
    data
  })
}

/** 修改高校Excel导出
 * @param {null}
 * @method get
 * @returns {null}
  */
export function getCommentExcel() {
  return request({
    url: `/comment/download`,
    method: 'get'
  })
}

