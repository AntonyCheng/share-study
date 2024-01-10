/* 教学资料接口 */
import request from '@/utils/request'

/** 获取教学资料Excel
 * @param {null}
 * @method get
  */
export function getMaterialExcel() {
  return request({
    url: `/resource/download`,
    method: 'get',
    responseType: 'blob'
  })
}

/** 教学资料删除接口
 * @param {string} id
 * @method delete
  */
export function delMaterial(id) {
  return request({
    url: `/resource/delete/${id}`,
    method: 'delete'
  })
}

/** 教学资料批量删除接口
 * @param {object} data
 * @method delete
  */
export function delMaterialBatch(data) {
  return request({
    url: `/resource/deleteBatch`,
    method: 'get',
    data
  })
}

/** 管理员根据id获取教学资料
 * @param {string} id
 * @method get
  */
export function getMaterial(id) {
  return request({
    url: `/resource/get/${id}`,
    method: 'get'
  })
}

/** 修改教学资料接口
 * @param {object} data
 * @method put
  */
export function updateMaterial(data) {
  return request({
    url: `/resource/update`,
    method: 'put',
    data
  })
}

/** 教学资料分页查询接口
 * @param {number} current
 * @param {number} pageSize
 * @method post
  */
export function getMaterialPageInfo(current, pageSize) {
  return request({
    url: `/resource/page/${current}/${pageSize}`,
    // method: "post",
    method: 'get'
  })
}
