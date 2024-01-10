export * from './user'


// 作用	Vue Component	Vuex	Pinia
// 数据管理	data	state	state
// 数据计算	computed	getters	getters
// 行为方法	methods	mutations / actions	actions

// 获取和更新 state
// Vuex 是 store.state.message ， Pinia 是 store.message 。

// Pinia 还提供了一个 storeToRefs API 用于把 state 的数据转换为 ref 变量。

// 为 store state 声明类型
// export interface AllStateTypes {
//   count: number,
//   locale: any,
//   userStatus: number
//   roomList: Array<any>,
//   pageNo: number,
//   pageSize: number,
//   total: number,
//   cityCode: string,
//   roomDetail: any,
//   roomId: null,
//   orderVisible: boolean
// }

// export const useStore = defineStore('main', {
//   // store选项
//   state: () => ({
//     userInfo: {}
//   }),

//   actions: {
//     // 更新 用户信息
//     async updateUserInfo(): Promise<object> {
//       return new Promise((resolve) => {

//       })
//     }
//   }
// })




