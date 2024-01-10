/* 用户操作接口 */
import request from '@/utils/request'

/** 用户 教学资料分页查询
 * @param {string} id
 * @param {number} current
 * @param {number} pageSize
 * @method get
 * @returns {object}
  */
export function getResourceInfoPage(id: string, current: number | string, pageSize: number | string) {
  return request({
    url: `/user/resource/page/${id}/${current}/${pageSize}`,
    method: 'get',
  })
}

/** 用户 获取教学资料信息
 * @param {string} id
 * @method get
 * @returns {object}
  */
export function getResourceInfo(id: string) {
  return request({
    url: `/user/resource/get/${id}`,
    method: 'get',
  })
}

/** 用户 修改教学资料信息
 * @param {object} data
 * @method put
 * @returns {object}
  */
export function updateResourceInfo(data: object) {
  return request({
    url: `/user/resource/update`,
    method: 'put',
  })
}

/** 用户 删除教学资料信息
 * @param {number|string} id
 * @method delete
 * @returns {object}
  */
export function delResourceInfo(id: string) {
  return request({
    url: `/user/resource/update`,
    method: 'delete',
  })
}

/** 用户 获取评论资料信息
 * @param {number} current
 * @param {number} pageSize
 * @method get
 * @returns {object}
  */
export function getCommentInfo(current: number, pageSize: number) {
  return request({
    url: `/user/comment/page/${current}/${pageSize}`,
    method: 'get',
  })
}

/** 用户 删除单条评论信息
 * @param {string} id
 * @method delete
 * @returns {object}
  */
export function delCommentInfo(id: string) {
  return request({
    url: `/user/comment/delete/${id}`,
    method: 'delete',
  })
}

/** 用户 清空条评论信息
 * @param {number} id
 * @method delete
 * @returns {object}
  */
export function delBatchCommentInfo(id: string) {
  return request({
    url: `/user/comment/delete/${id}`,
    method: 'delete',
  })
}

/** 用户 收藏信息分页
 * @param {number} id
 * @param {number} current
 * @param {number} pageSize
 * @method get
 * @returns {object}
  */
export function getCollectInfo(id: string, current: number, pageSize: number) {
  return request({
    url: `/user/collect/page/${id}/${current}/${pageSize}`,
    method: 'get',
  })
}

/** 用户 收藏信息分页
 * @param {number} id
 * @method get
 * @returns {object}
  */
export function delCollectInfo(id: string) {
  return request({
    url: `/user/collect/delete/${id}`,
    method: 'get',
  })
}


