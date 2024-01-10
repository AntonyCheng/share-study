import request from '@/utils/request'

/** 管理员 获取资料标签
 * @param {number} current
 * @param {number} pageSize
 * @method get
  */
export function getMaterialTagList(current, pageSize) {
  return request({
    url: `/tag/page/${current}/${pageSize}`,
    method: 'get'
  })
}

/** 管理员 添加资料标签
 * @param {object} data
 * @method post
  */
export function addMaterialTag(data) {
  return request({
    url: `/tag/add/`,
    method: 'post',
    data
  })
}

/** 管理员 删除资料标签
 * @param {number | string} id
 * @method get
  */
export function deleteMaterialTag(id) {
  return request({
    url: `/tag/delete/${id}`,
    method: 'delete'
  })
}

/** 管理员 批量删除资料标签
 * @param {number | string} id
 * @method get
  */
export function deleteBatchMaterialTag(id) {
  return request({
    url: `/tag/deleteBatch`,
    method: 'delete'
  })
}

/** 管理员 获取单个资料标签
 * @param {number | string} id
 * @method get
  */
export function getMaterialTagOne(id) {
  return request({
    url: `/tag/get/${id}`,
    method: 'get'
  })
}

/** 管理员 修改资料标签
 * @param {number | string} id
 * @method get
  */
export function updateMaterialTag(data) {
  return request({
    url: `/tag/update`,
    method: 'put',
    data
  })
}
