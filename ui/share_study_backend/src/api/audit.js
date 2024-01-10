/* 待审批接口 */
import request from '@/utils/request'

/** 待审批资料 删除接口
 * @param {string} id
 * @method delete
  */
export function delAudit(id) {
  return request({
    url: `/resource_censor/delete/${id}`,
    method: 'delete'
  })
}

/** 待审批资料 分页查询接口
 * @param {number} current
 * @param {number} pageSize
 * @method post
  */
export function getAuditPageInfo(current, pageSize) {
  return request({
    url: `/resource_censor/page/${current}/${pageSize}`,
    method: 'get'
  })
}

/** 审批资料
 * @param {object} data
 * @method put
  */
export function updateAuditResource(data) {
  return request({
    url: `/resource_censor/update`,
    method: 'put',
    data
  })
}

/** 审批资料 超管发布审批资料
 * @param {string} id
 * @method put
  */
export function publishAuditResource(id) {
  return request({
    url: `/resource_censor/publish/${id}`,
    method: 'post'
  })
}

/* 注册审批 */

/** 注册审批 分页查询接口
 * @param {number} current
 * @param {number} pageSize
 * @method post
  */
export function getRegisterAuditPageInfo(current, pageSize) {
  return request({
    url: `/teacher_censor/page/${current}/${pageSize}`,
    method: 'get'
  })
}

/** 注册审批 注册审核状态更新
 * @param {object} data
 * @method post
  */
export function updateRegisterAudit(data) {
  return request({
    url: `/teacher_censor/update`,
    method: 'put',
    data
  })
}

/** 注册审批 发布审核完成的注册申请
 * @param {number | string} id
 * @method post
  */
export function publishRegisterAudit(id) {
  return request({
    url: `/teacher_censor/publish/${id}`,
    method: 'post'
  })
}

/** 注册审批 删除单个接口
 * @param {string} id
 * @method delete
  */
export function delRegisterAudit(id) {
  return request({
    url: `/teacher_censor/delete/${id}`,
    method: 'delete'
  })
}

/** 注册审批 批量删除
 * @param {Array} idList
 * @method delete
  */
export function deleteBatchRegisterAudit(idList) {
  return request({
    url: `/teacher_censor/deleteBatch/`,
    method: 'delete',
    idList
  })
}
