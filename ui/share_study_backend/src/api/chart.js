/* 图表类 接口 */

import request from '@/utils/request'

/** 获取高校坐标信息
 * @param {null}
 * @method get
  */
export function getCollegeMap() {
  return request({
    url: `/chart/college_location_number`,
    method: 'get'
  })
}

/** 获取资料收藏TOP5
 * @param {null}
 * @method get
  */
export function getResourceCollectTop() {
  return request({
    url: `/chart/resource_collect`,
    method: 'get'
  })
}

/** 获取高校贡献TOP5
 * @param {null}
 * @method get
  */
export function getCollegeScoreTop() {
  return request({
    url: `/chart/college_score`,
    method: 'get'
  })
}

/** 获取教师贡献TOP5
 * @param {null}
 * @method get
  */
export function getTeacherScoreTop() {
  return request({
    url: `/chart/teacher_score`,
    method: 'get'
  })
}
