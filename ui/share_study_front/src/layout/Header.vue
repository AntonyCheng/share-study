<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import {
  defineComponent,
  onMounted,
  onBeforeUnmount,
  computed,
  watch,
  ref,
  inject,
  nextTick,
} from "vue";
import { useRouter, useRoute } from "vue-router";

import $bus from "@libs/eventBus";
import { removeToken } from "@/utils/auth";
import { debounce, throttle } from "@/utils"; // 防抖节流

import DefaultAvatar from "@/components/DefaultAvatar/index.vue";
import { getUserCommentPage, readCommentAll } from "@/api/post";
import { getToken } from "@/utils/auth";
import { useUserStore } from "@/store/index";
import { UserInfoMember } from "@/store/index";

export interface CommentListMember {
  id: string;
  resourceId: string;
  belongName: string;
  createTime: string;
  content: string;
}

export default defineComponent({
  components: {
    DefaultAvatar,
  },
  setup() {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例
    // 获取路由信息
    const router = useRouter();
    const route = useRoute();
    const userStore = useUserStore();
    const $message: any = inject("$message");

    // 用户信息
    const userInfo = ref<UserInfoMember | {}>({});
    //   {
    //   account: "",
    //   avatar: "",
    //   collegeName: "",
    //   createTime: "",
    //   email: "",
    //   gender: 0,
    //   id: "",
    //   isLogin: false,
    //   messageNumber: 0,
    //   name: "",
    //   role: 0,
    //   score: 0,
    // }
    // 信息
    const showHeader = ref<boolean>(true);
    const showDialog = ref<boolean>(false);
    const currentPath = ref<string>("");
    const searchInfo = ref<string>("");
    // 收到的评论
    const userCommentList = ref<CommentListMember[]>([]);
    const userCommentLength = ref<number>(0);
    const currentPage = ref<number>(1);
    const pageSize = ref<number>(10);

    // 事件
    const updateUserInfoEvent = (val: any) => {
      // 会被调用多次
      if (val) {
        userInfo.value = val;
        getUserCommentPageInfo(currentPage.value, pageSize.value);
      }
    };

    const noLoginEvent = (val: any) => {
      // userInfo.value = {
      //   account: "",
      //   avatar: "",
      //   collegeName: "",
      //   createTime: "",
      //   email: "",
      //   gender: 0,
      //   id: "",
      //   isLogin: false,
      //   messageNumber: 0,
      //   name: "",
      //   role: 0,
      //   score: 0,
      // };
      userInfo.value = {};
      // console.log(val);
      noLogin();
    };

    // 事件 Event
    const changeShowHeaderEvent = (val: any) => {
      // console.log(val);
      showHeader.value = val;
    };

    // created
    console.log(route.path);
    userInfo.value = getTokenData();
    getUserCommentPageInfo(currentPage.value, pageSize.value);

    // 生命周期钩子
    onMounted(() => {
      console.log("mounted");
      initScroll();
      // debounce(1000, initScroll);
      // throttle(1000, initScroll, 1);
      // console.log(window);
      // window.onscroll = debounce(200, (a: any) => console.log(a), 1);
      // window.onscroll = throttle(1000, (a: any) => console.log(a), 1);

      // 未登录
      $bus.on("noLogin", noLoginEvent);
      // 用户信息变更
      $bus.on("updateUserInfo", updateUserInfoEvent);
      // $message.info("这是一段消息");
      $bus.on("changeShowHeaderEvent", changeShowHeaderEvent);
    });

    onBeforeUnmount(() => {
      // 未登录
      $bus.off("noLogin", noLoginEvent);
      // 用户信息变更
      $bus.off("updateUserInfo", updateUserInfoEvent);
      // $message.info("这是一段消息");
      $bus.off("changeShowHeaderEvent", changeShowHeaderEvent);
    });

    // 计算属性 computed
    // 关于我 路径
    const aboutPath = computed(() => {
      if (userInfo.value) {
        return "/about?id=" + userInfo.value.id;
      } else {
        return "/about?id=";
      }
    });

    // 方法 methods
    async function getUserCommentPageInfo(current: number, pageSize: number) {
      const result = await getUserCommentPage(current, pageSize);
      // console.log(result);

      if (result === undefined) return;
      userCommentList.value = result.data.records;
      userCommentLength.value = result.data.records.length;
    }

    // 消息已读
    async function commentReadAll() {
      try {
        await readCommentAll();
        // 获取最新消息
        // 评论
        await getUserCommentPageInfo(currentPage.value, pageSize.value);
        // 个人信息
        // const data  = await this.$store.dispatch("user/getInfo", this.userInfo.id)
      } catch (error) {
        $message.error("你没有需要清空的评论");
        return error;
      }
    }

    // 获取token
    function getTokenData() {
      let token: string | null = getToken();
      let data = JSON.parse(token as string); // token为string时
      return data;
    }

    // 跳转登录页面
    function login() {
      if (window.location.href.split("#")[1] !== "/login") {
        router.push({ path: "/login" });
      } else {
        $message.info("你已经处在登录页了~");
      }
      // this.loginOrRegister = true;
      // this.showDialog = true;
    }

    function register() {
      if (window.location.href.split("#")[1] !== "/register") {
        router.push({ path: "/register" });
      } else {
        $message.info("你已经处在注册页了~");
      }
      // this.loginOrRegister = false;
      // this.showDialog = true;
    }

    // 退出
    function logout() {
      // if (route.path != "/login") {
      //   router.push({ path: "/login" });
      // }
      if (route.path != "/intro") {
        router.push({ path: "/intro" });
      }
      removeToken(); // 清除token
      // 重置store
      userStore.$reset();
      // userInfo.value = {
      //   account: "",
      //   avatar: "",
      //   collegeName: "",
      //   createTime: "",
      //   email: "",
      //   gender: 0,
      //   id: "",
      //   isLogin: false,
      //   messageNumber: 0,
      //   name: "",
      //   role: 0,
      //   score: 0,
      // };
      userInfo.value = {};
      // 修改登录状态
      // this.$store.dispatch("user/logout"); // 清除user相关token
      $message({
        type: "info",
        message: "退出成功",
      });

      $bus.emit("resetUserInfo", undefined);
    }
    // 未登录情况
    function noLogin() {
      // userInfo.value = {
      //   account: "",
      //   avatar: "",
      //   collegeName: "",
      //   createTime: "",
      //   email: "",
      //   gender: 0,
      //   id: "",
      //   isLogin: false,
      //   messageNumber: 0,
      //   name: "",
      //   role: 0,
      //   score: 0,
      // }; // 修改登录状态

      userInfo.value = {};

      // console.log('userInfo=>', userInfo);
      // this.$store.dispatch("user/logout"); // 清除user相关token
      $bus.emit("resetUserInfo", {});
    }

    function getScorllTop(): number {
      // 获取滚动条滚动的高度
      let scrollTop =
        document.documentElement.scrollTop ||
        window.pageYOffset ||
        document.body.scrollTop;

      return scrollTop;
    }

    // 滚动事件相关
    function initScroll(): void {
      console.log("initScroll");
      let initScrollTop: number = getScorllTop();
      let scrollType: number = 0;

      // const scrollMain = debounce((initScrollTop: number, scrollType: number) => {
      // 节流 闭包
      const scrollMain = throttle(() => {
        // console.log("scrollMain");
        let currentScrollTop = getScorllTop();
        if (currentScrollTop > initScrollTop) {
          // 往下滚动
          scrollType = 1;
        } else {
          // 往上滚动
          scrollType = 0;
        }
        // console.log(initScrollTop);
        // console.log(currentScrollTop);
        // console.log(scrollType);
        initScrollTop = currentScrollTop;
        if (scrollType == 1 && currentScrollTop > 100) {
          showHeader.value = false;
        } else {
          showHeader.value = true;
        }
      }, 300);

      // console.log(scrollMain);
      window.addEventListener("scroll", () => scrollMain());
    }

    // 跳转 addPost页面
    function pushAddPost(): void {
      // console.log(this.$route.path);
      if (route.path !== "/addPost/1") {
        router.push({ path: "/addPost/1" });
      } else {
        $message.info("你已经在发帖页面了");
      }

      $bus.emit("resetFormFromHeader", 1);
    }
    // 跳转 sendPost页面
    function pushSendPost(): void {
      // console.log(this.$route.path);
      if (route.path !== "/sendPost/1") {
        router.push({ path: "/sendPost/1" });
      } else {
        $message.info("你已经在发帖页面了");
      }
      $bus.emit("resetFormFromHeader", 1);
    }
    // 跳转到帖子详情页面
    function toPostDetail(id: string): void {
      // console.log(window.location.href.split('/').reverse()[0] === id)

      if (window.location.href.split("/").reverse()[0] != id) {
        router.push({ path: "/postDetail/" + id }).catch((error: any) => {
          console.log(error);
          return error;
        });
      } else {
        $message.info("你已经在当前页面了!");
      }
    }

    // 防抖
    const searchInfoDebounce = debounce(
      (that: any, newVal: string) => $bus.emit("tranSearchInfo", newVal),
      // () => console.log(1),
      300
    );

    // watch
    watch(searchInfo, (newVal) => {
      // console.log(2);
      // console.log(newVal);
      searchInfoDebounce(this, newVal);
    });

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
      // 变量
      userInfo,
      showHeader,
      showDialog,
      currentPath,
      searchInfo,
      userCommentList,
      userCommentLength,
      currentPage,
      pageSize,

      // 方法
      pushAddPost,
      pushSendPost,
      logout,
      toPostDetail,
      commentReadAll,
      login,
      register,

      // 计算属性
      aboutPath,
    };
  },
});
</script>

<template>
  <!-- v-show="showHeader" -->
  <div
    class="header"
    :class="showHeader ? 'header-up' : 'header-down'"
    v-if="userInfo && Object.keys(userInfo).length != 0"
  >
    <div class="header-content">
      <!-- logo -->
      <router-link class="logo" to="/">
        <!-- <span>T</span>
        <span>e</span>
        <span>s</span>
        <span>t</span> -->
        <img src="../assets/logo.png" alt="logo" />
        <span class="logo-title">烛光之窗</span>
      </router-link>

      <!-- 导航 -->
      <div class="menu-panel">
        <router-link class="nav-item" to="/">首页</router-link>
        <router-link class="nav-item" :to="aboutPath">关于</router-link>
        <router-link class="nav-item" :to="aboutPath">资源共享</router-link>
        <router-link class="nav-item" :to="aboutPath">科研成功</router-link>
        <router-link class="nav-item" :to="aboutPath">项目落地</router-link>
        <router-link class="nav-item" :to="aboutPath">共商共建</router-link>
        <!-- <router-link class="nav-item" to="/intro">项目介绍</router-link> -->
        <!-- <router-link class="nav-item" to="/">相关高校</router-link>
        <router-link class="nav-item" to="/">教学资料</router-link> -->
      </div>

      <!-- 登录注册 用户信息 -->
      <div class="user-info-panel">
        <div class="input-search">
          <input class="input-text" placeholder="搜索" v-model="searchInfo" />
          <span class="iconfont icon-fenxiang search-icon"></span>
        </div>
        <div class="op-btn">
          <!-- <el-button
            type="primary"
            v-if="currentPath === '/addPost/1'"
            class="op-btn"
            @click="pushAddPost"
            >返回<span class="iconfont icon-left-open"></span
          ></el-button> -->
          <el-button type="primary" class="op-btn" @click="pushAddPost"
            >短篇<span class="iconfont icon-add"></span
          ></el-button>
          <el-button type="primary" class="op-btn" @click="pushSendPost"
            >长篇<span class="iconfont icon-add"></span
          ></el-button>
        </div>

        <div
          class="userInfo-avatar"
          v-if="userInfo && userInfo.id != ''"
          :style="{ 'margin-left': '20px' }"
        >
          <!-- 有头像时 -->
          <el-dropdown placement="bottom" trigger="click">
            <default-avatar
              class="dropdown-avatar"
              width="40px"
              height="40px"
              :avatarName="userInfo.name.split('')[0]"
              v-if="userInfo.avatar === ''"
            ></default-avatar>
            <el-avatar
              v-else
              class="dropdown-avatar"
              size="large"
              :src="userInfo.avatar"
            ></el-avatar>
            <!--   头像 退出登录 -->
            <template #dropdown class="user-dropdown">
              <el-dropdown-menu>
                <router-link to="/">
                  <el-dropdown-item> 首页 </el-dropdown-item>
                </router-link>
                <router-link :to="aboutPath">
                  <el-dropdown-item> 个人中心 </el-dropdown-item>
                </router-link>
                <!-- <a target="_blank" href="https://github.com/PanJiaChen/vue-admin-template/">
                  <el-dropdown-item>Github</el-dropdown-item>
                </a>
                <a target="_blank" href="https://panjiachen.github.io/vue-element-admin-site/#/">
                  <el-dropdown-item>Docs</el-dropdown-item>
                </a> -->
                <el-dropdown-item divided @click.native="logout">
                  <span style="display: block">退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 无头像使用默认头像 -->

          <!-- badge提示 -->
          <el-dropdown
            trigger="click"
            placement="bottom"
            class="dropdown-comment-badge"
          >
            <div class="comment-badge" style="width: 50px; height: 50px">
              <el-badge
                :max="10"
                :hidden="userCommentLength === 0 ? true : false"
                :value="userCommentLength"
                class="item"
              >
                <el-button size="large"
                  ><el-icon><ChatDotRound /></el-icon
                ></el-button>
              </el-badge>
            </div>
            <template #dropdown>
              <el-dropdown-menu
                placement="bottom"
                slot="dropdown"
                class="comment-dropdown"
              >
                <el-dropdown-item
                  v-for="commentItem in userCommentList"
                  :key="commentItem.id"
                  @click.native="toPostDetail(commentItem.resourceId)"
                >
                  <div class="dropdown-card">
                    <div class="dropdown-avatar"></div>
                    <div class="dropdown-textBox">
                      <div class="dropdown-textContent">
                        <p class="dropdown-h1">{{ commentItem.belongName }}</p>
                        <span class="dropdown-span">{{
                          commentItem.createTime.split(" ")[0]
                        }}</span>
                      </div>
                      <p class="dropdown-p">{{ commentItem.content }}</p>
                    </div>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item v-if="userCommentList.length === 0"
                  >暂无消息</el-dropdown-item
                >
                <el-dropdown-item
                  class="dropdown-no-comment"
                  v-else
                  @click.native="commentReadAll"
                  >清空消息</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <!-- 登录注册 -->
        <el-button-group v-else :style="{ 'margin-left': '10px' }">
          <el-button type="primary" class="btn-group" plain @click="login"
            >登录</el-button
          >
          <el-button type="primary" class="btn-group" plain @click="register"
            >注册</el-button
          >
        </el-button-group>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
/*大型屏幕pc 超大屏*/
@media screen and (min-width: 1200px) {
  .dropdown-card {
    width: 290px;
  }
}
/*1200>=pc>=992 大屏，字体红色，背景黑色*/
@media screen and (min-width: 992px) and (max-width: 1199px) {
  .header {
    width: 100%;

    .header-content {
      width: 1100px !important;

      .user-info-panel {
        .input-search {
          display: none;
        }
      }
    }
  }

  .dropdown-card {
    width: 290px;
  }
}
/*768<=pad<992 中屏，字体黄色，背景红色*/
@media screen and (min-width: 768px) and (max-width: 991px) {
  .header .header-content .user-info-panel .input-search {
    display: none !important;
  }

  .dropdown-card {
    width: 200px;
  }
}
/*phone<768  小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 767px) and (min-width: 480px) {
  .header .header-content .menu-panel {
    display: none !important;
  }

  .header .header-content .user-info-panel .input-search {
    display: none !important;
  }

  .dropdown-card {
    width: 110px;
  }
}
/* 超小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 480px) {
  .header .header-content .menu-panel {
    display: none !important;
  }

  .header .header-content .user-info-panel .input-search {
    display: none !important;
  }

  .header .header-content .user-info-panel .op-btn button {
    font-size: 8px;
    width: 55px !important;
    padding: 4px 8px !important;

    span {
      font-size: 4px;
    }
  }

  .dropdown-card {
    width: 290px;

    .dropdown-span {
      font-size: 4px;
    }

    .dropdown-h1 {
      font-size: 8px;
    }

    .dropdown-p {
      font-size: 6px;
    }
  }

  .header .header-content .logo {
    width: 120px !important;
  }

  // .header .header-content .logo img {
  //   // width: 120px !important;
  //   // height: 64px;
  //   // position: absolute;
  //   // top: -10px !important;
  //   // left: -5px;
  // }
}

.header .header-content .logo img {
  height: 64px;
}

.header-down {
  height: 0;
  overflow: hidden;
  transition: all 400ms;
}

.header-up {
  transition: all 400ms;
  // height: 5em;
  height: 60px;
}

.header {
  width: 100%;
  position: fixed;
  box-shadow: 0 2px 6px 0 #ddd;
  // background-color: #fff;
  background-color: #f9fbff;
  z-index: 1;

  .header-content {
    max-width: 1300px;
    margin: 0px auto;
    height: 60px;
    align-items: center;
    display: flex;
    flex: 1;
    justify-content: space-between;

    a {
      text-decoration: none;
    }

    .logo {
      display: block;
      margin-right: 15px;
      // padding-right: 20px;
      // logo
      overflow: hidden;
      // width: 150px;
      // width: 200px;
      height: 64px;
      position: relative;

      img {
        // width: 160px;
        position: absolute;
        z-index: -1;
        // top: -28px;
        // // right: ;
        // // left: -5px;
        // left: 18px;
      }

      .logo-title {
        // width: 150px;
        height: 64px;
        line-height: 64px;
        font-weight: bold;
        color: #007fff;
        text-shadow: #007fff 1px 0 10px;
        // color: rgb(255, 0, 0);
        // color: #000;
      }

      // span {
      //   font-size: 35px;
      // }
    }

    // 导航信息
    .menu-panel {
      flex: 1;
      display: flex;

      .nav-item {
        // padding: 0 20px 0 0;
        margin: 0 10px 0 10px;
        line-height: 60px;
        color: #909399;
        display: inline-block;
        cursor: pointer;
        position: relative;
      }

      // 导航下划线动画效果
      .nav-item:hover {
        color: #409eff;
      }

      .nav-item:before {
        content: "";
        position: absolute;
        left: 50%;
        bottom: -2px;
        width: 0;
        height: 2px;
        background: #409eff;
        transition: all 0.3s;
      }

      .nav-item:hover:before {
        width: 100%;
        left: 0;
        right: 0;
      }
    }

    // 登录注册信息
    .user-info-panel {
      // width: 310px;
      display: flex;
      align-items: center;

      // 右侧搜索框
      .input-search {
        position: relative;
        display: inline-block;
        margin-right: 50px;

        // .input-text {
        // }
        input {
          padding: 0 40px 0 20px;
          width: 140px;
          height: 38px;
          font-size: 14px;
          border: 1px solid #eee;
          border-radius: 40px;
          background: #eee;
          transition: width 0.5s;
          transition-delay: 0.1s;
        }

        .input-text:focus {
          width: 240px;
          outline: none;
          box-shadow: none;
        }

        .input-text:focus + .search-icon {
          background-color: #66b1ff;
          color: #000;
        }

        .search-icon {
          position: absolute;
          top: 4px;
          right: 5px;
          width: 30px;
          height: 30px;
          line-height: 30px;
          padding: 0;
          color: #fff;
          text-align: center;
          background: #409eff;
          border-radius: 50%;
          font-size: 15px;
          cursor: pointer;
        }
      }

      // 右侧按钮
      .op-btn {
        font-size: 14px;
        // line-height: 50px;

        button {
          width: 100px;
          padding: 8px 15px 8px 15px;
        }

        .iconfont {
          margin-left: 5px;
        }
      }

      .el-button-group {
        button {
          padding: 8px 15px 8px 15px;
        }
      }

      .op-btn + .op-btn {
        margin-left: 5px;
      }

      /* 登录注册 头像相关 */
      .userInfo-avatar {
        display: flex;
        align-items: center;

        .el-dropdown {
          .dropdown-avatar {
            cursor: pointer;
          }
        }

        // 弹出框
        // .el-dropdown {

        //   .el-dropdown-menu {
        //     top: 60px;
        //     width: 120px;
        //   }
        // }

        .dropdown-comment-badge {
          // 消息icon
          .comment-badge {
            // margin-left: 10px;

            // 消息提示组件
            .el-badge {
              height: 50px;
              .el-button {
                height: 50px;
                // padding: 12px 10px 12px 10px;
                // padding: 0;
                font-size: 22px;
                border: none;
              }

              /* button动画效果 */
              .el-button:hover {
                -webkit-animation: shake-top 0.8s
                  cubic-bezier(0.455, 0.03, 0.515, 0.955) both;
                animation: shake-top 0.8s
                  cubic-bezier(0.455, 0.03, 0.515, 0.955) both;
                background-color: #fff;
              }

              @-webkit-keyframes shake-top {
                0%,
                100% {
                  -webkit-transform: rotate(0deg);
                  transform: rotate(0deg);
                  -webkit-transform-origin: 50% 0;
                  transform-origin: 50% 0;
                }

                10% {
                  -webkit-transform: rotate(2deg);
                  transform: rotate(2deg);
                }

                20%,
                40%,
                60% {
                  -webkit-transform: rotate(-4deg);
                  transform: rotate(-4deg);
                }

                30%,
                50%,
                70% {
                  -webkit-transform: rotate(4deg);
                  transform: rotate(4deg);
                }

                80% {
                  -webkit-transform: rotate(-2deg);
                  transform: rotate(-2deg);
                }

                90% {
                  -webkit-transform: rotate(2deg);
                  transform: rotate(2deg);
                }
              }

              @keyframes shake-top {
                0%,
                100% {
                  -webkit-transform: rotate(0deg);
                  transform: rotate(0deg);
                  -webkit-transform-origin: 50% 0;
                  transform-origin: 50% 0;
                }

                10% {
                  -webkit-transform: rotate(2deg);
                  transform: rotate(2deg);
                }

                20%,
                40%,
                60% {
                  -webkit-transform: rotate(-4deg);
                  transform: rotate(-4deg);
                }

                30%,
                50%,
                70% {
                  -webkit-transform: rotate(4deg);
                  transform: rotate(4deg);
                }

                80% {
                  -webkit-transform: rotate(-2deg);
                  transform: rotate(-2deg);
                }

                90% {
                  -webkit-transform: rotate(2deg);
                  transform: rotate(2deg);
                }
              }

              // 右上角 消息提示
              .is-fixed {
                top: 15px;
                right: 20px;
              }

              .el-icon-message-solid {
                display: inline-block;
                font-size: 20px;
              }
            }
          }
        }
      }
    }
  }
}

// 评论
// .comment-dropdown{
// comment-dropdown
.dropdown-card {
  // width: 290px;
  background: #f9fbff;
  // background: #353535;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: left;
  backdrop-filter: blur(10px);
  transition: 0.5s ease-in-out;
  margin-bottom: 10px;

  .dropdown-avatar {
    width: 50px;
    height: 50px;
    margin-left: 10px;
    border-radius: 10px;
    background: linear-gradient(#d7cfcf, #9198e5);
  }

  .dropdown-textBox {
    width: calc(100% - 90px);
    margin-left: 10px;
    color: #909399;
    // color: white;
    font-family: "Poppins" sans-serif;
  }

  .dropdown-textContent {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .dropdown-span {
    font-size: 10px;
  }

  .dropdown-h1 {
    color: #1d2129;
    font-size: 16px;
    font-weight: bold;
  }

  .dropdown-p {
    font-size: 12px;
    font-weight: lighter;
  }
}

.dropdown-card:hover {
  cursor: pointer;
  transform: scale(1.05);
}
.dropdown-card:hover > .dropdown-avatar {
  transition: 0.5s ease-in-out;
  background: linear-gradient(#9198e5, #712020);
}

.dropdown-no-comment {
  text-align: center;
  justify-content: center;
}

// }
</style>
