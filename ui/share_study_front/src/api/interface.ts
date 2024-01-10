import { AxiosRequestConfig } from 'axios'
// export interface IResultOr { // 定义interface规范返回结果的类型
//   code: string,
//   success: boolean,
//   message: string,
//   result: any
// }

// export interface IRoomListParams extends AxiosRequestConfig { // 定义interface规范返回结果的类型
//   pageNo: number,
//   pageSize: number,
//   cityCode: string,
// }

// export interface IRoomDetailParams extends AxiosRequestConfig { // 定义interface规范返回结果的类型
//   id: number
// }

export interface YApiResult { // 定义interface规范返回结果的类型
  code: string,
  data: any,
  message: string,
  map: any
}
