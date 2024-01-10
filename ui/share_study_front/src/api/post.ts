import request from '@/utils/request'
/* 用户帖子相关接口 */


/** 用户帖子分页查询
 * @param {number} current
 * @param {number} pageSize
 * @method post
 * @returns {object}
  */
export function getPostPage(current: number, pageSize: number) {
  return request({
    url: `/post/page/${current}/${pageSize}`,
    method: "get",
  })
}

/** 用户帖子发布
 * @param {object} data
 * @method post
 * @returns {object}
  */
export function addPost(data: object) {
  return request({
    url: `/post/add`,
    method: "post",
    data
  })
}

/** 用户获取单个帖子信息
 * @param {string} id
 * @method get
 * @returns {object}
  */
export function getPostInfo(id: string) {
  return request({
    url: `/user/resource/get/${id}`,
    method: "get",
  })
}

/** 用户帖子修改
 * @param {object} data
 * @method put
 * @returns {object}
  */
export function updatePost(data: object) {
  return request({
    url: `/user/resource/update`,
    method: "put",
    data
  })
}

/** 用户帖子详情
 * @param {number|string} id
 * @method post
 * @returns {object}
  */
export function getPostDetail(id: string) {
  return request({
    url: `/post/info/${id}`,
    method: "get",
  })
}

/** 资料详情评论分页
 * @param {number|string} id
 * @param {string} current
 * @param {string} pageSize
 * @method get
 * @returns {object}
  */
export function getPostCommentPage(id: string, current: number, pageSize: number) {
  return request({
    url: `/post/page/${id}/${current}/${pageSize}`,
    method: "get",
  })
}

/** 用户获取发帖
 * @param {number|string} id
 * @param {string} current
 * @param {string} pageSize
 * @method get
 * @returns {object}
  */
export function getUserPostPage(id: string, current: number, pageSize: number) {
  return request({
    url: `/user/resource/page/${id}/${current}/${pageSize}`,
    method: "get",
  })
}

/** 获取用户收到评论分页
 * @param {number} current
 * @param {number} pageSize
 * @method get
 * @returns {object}
  */
export function getUserCommentPage(current: number, pageSize: number) {
  return request({
    url: `/user/comment/page/${current}/${pageSize}`,
    method: "get",
  })
}

/** 新增评论
 * @param {object} data
 * @method post
 * @returns {object}
  */
export function addPostComment(data: object) {
  return request({
    url: `/post/comment/add`,
    method: "post",
    data
  })
}

/** 新增评论
 * @param {string} data
 * @method post
 * @returns {object}
  */
export function delPostComment(id: string) {
  return request({
    url: `/post/comment/delete/${id}`,
    method: "delete",
  })
}

/** 用户修改消息是否已读
 * @param {string} id
 * @method put
 * @returns {object}
  */
export function readComment(id: string) {
  return request({
    url: `/user/comment/update/read/${id}`,
    method: 'put',
  })
}

/** 用户删除单条信息
 * @param {string} id
 * @method delete
 * @returns {object}
  */
export function readCommentOne(id: string) {
  return request({
    url: `/user/resource/delete/${id}`,
    method: 'delete',
  })
}

/** 用户清空消息
 * @param {null}
 * @method del
 * @returns {object}
  */
export function readCommentAll() {
  return request({
    url: `/user/comment/deleteBatch`,
    method: 'delete',
  })
}


/** 添加收藏 接口
 * @param {object} data = {belong resource}
 * @method post
 * @returns {object}
  */
export function addCollect(data: object) {
  return request({
    url: `/post/update/collect`,
    method: "put",
    data
  })
}

/** 删除收藏 接口
 * @param {string} id
 * @method post
 * @returns {object}
  */
export function delCollect(id: string) {
  return request({
    url: `/user/collect/delete/${id}`,
    method: "delete",
  })
}

/** 获取收藏分页接口
 * @param {string} id
 * @param {number} current
 * @param {number} pageSize
 * @method get
 * @returns {object}
  */
export function getCollectPage(id: string, current: number, pageSize: number) {
  return request({
    url: `/user/collect/page/${id}/${current}/${pageSize}`,
    method: "get",
  })
}

