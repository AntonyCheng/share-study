<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import {
  defineComponent,
  onMounted,
  onBeforeUnmount,
  ref,
  computed,
  watch,
} from "vue";
// import { useRoute } from "vue-router";
// import { Emitter } from "mitt";
import $bus from "@libs/eventBus";

import DefaultAvatar from "@/components/DefaultAvatar/index.vue";
import Tags from "./components/Tags.vue";
import { getPostPage } from "@/api/post";
import { UserInfoMember } from "@/store/user";
import { getTokenData } from "@/utils/index";

export interface PostListMember {
  collectStatus: string;
  collegeName: string;
  commentCount: number;
  createTime: string;
  resourceId: string;
  resourceInfo: string;
  resourceName: string;
  resourceScore: number;
  resourceStatus: number;
  resourceUrl: string;
  userAvatarUrl: string;
  userId: string;
  userName: string;
}

export default defineComponent({
  components: {
    DefaultAvatar,
    Tags,
  },
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例
    // 全局属性
    // const route = useRoute();

    // console.log("$bus=>", $bus);
    // dom
    const mainRef = ref<HTMLElement>();

    // 标识
    const loading = ref<boolean>(false);
    const skeletonLoading = ref<boolean>(true);
    const skeletonCount = ref<number>(6); // 初始骨架屏数量

    // 帖子card
    const count = ref<number>(1); // 默认第一页
    const pageSize = ref<number>(8); //初始加载帖子数量
    const postLength = ref<number>(8); // 总帖子数量
    const postAveList = ref<Array<PostListMember[]>>([]); // 均分数组 方便无限滚动加载
    const postList = ref<PostListMember[]>([]); // 当前展示数组 下滑push
    const filterPostList = ref<PostListMember[]>([]); // 过滤数组 用于搜索
    const totalPostList = ref<PostListMember[]>([]); // 存储所有数据
    const searchInfo = ref<string>(""); // 搜索信息
    // 用户信息
    const userInfo = ref<UserInfoMember>({
      account: "",
      avatar: "",
      collegeName: "",
      createTime: "",
      email: "",
      gender: 0,
      id: "",
      isLogin: false,
      messageNumber: 0,
      name: "",
      role: 0,
      score: 0,
    });
    const tokenData = ref<UserInfoMember | null>(null); // token数据
    // 更新信息
    const activeName = ref<string>("6"); // 当前选中
    const upgrateInfo = ref<object[]>([]);
    // 子组件传递数据
    const tagNameChild = ref<string>("");

    // 事件
    function resetUserInfoEvent(): void {
      // this.userInfo = this.getTokenData();
      userInfo.value = {
        account: "",
        avatar: "",
        collegeName: "",
        createTime: "",
        email: "",
        gender: 0,
        id: "",
        isLogin: false,
        messageNumber: 0,
        name: "",
        role: 0,
        score: 0,
      };
      postLength.value = 0;
      postAveList.value = [];
      postList.value = [];
    }
    // 退出登录时重置信息
    $bus.on("resetUserInfo", resetUserInfoEvent);
    // console.log(this.isLogin);

    // 传递
    function tranSearchInfoEvent(val: any) {
      console.log(val);
      searchInfo.value = val;
      // var reg = new RegExp("^[0-9]+"+param+"[a-z]+$","g");

      filterPostList.value = totalPostList.value.filter(
        (data) => {
          return data.resourceName
            .toLowerCase()
            .includes(searchInfo.value.toLowerCase());
        }
        // data.resourceName
        //   .toLowerCase()
        //   .includes(this.resourceName.toLowerCase())
      );
    }
    // 传递searchInfo
    $bus.on("tranSearchInfo", tranSearchInfoEvent);

    // 生命周期钩子
    onMounted(() => {
      // console.log(route.path);
      getPostPageInfo(1, 100);
      tokenData.value = getTokenData();

      if (tokenData.value != null) {
        userInfo.value = tokenData.value;
      }

      // nextTick(() => {
      //   initScroll();
      // });
    });

    // 在组件卸载之前移除侦听
    onBeforeUnmount(() => {
      $bus.off("tranSearchInfo", tranSearchInfoEvent);
      $bus.off("resetUserInfo", resetUserInfoEvent);
    });

    // 方法 methods
    // 获取子组件传递信息
    const getTagNameChild = (collegeName: string) => {
      tagNameChild.value = collegeName;
      // console.log(tagNameChild.value);
    };
    // 获取帖子数
    const getPostPageInfo = async (
      current: number,
      postPageSize: number,
      collegeName: string = "哈尔滨商业大学"
    ) => {
      // console.log(1);

      skeletonLoading.value = true;
      const result = await getPostPage(current, postPageSize);
      if (!result) return;
      const data = result.data;
      // 获取所有数据
      // totalPostList.value = data.records;
      if (collegeName === "综合") {
        totalPostList.value = data.records;
      } else {
        // console.log(collegeName);
        totalPostList.value = data.records.filter((data: any) => {
          // console.log(data);
          return data.collegeName.toLowerCase().includes(collegeName);
        });
      }
      // 原数组
      postLength.value = data.total;
      // this.postAveList = this.aveArr(data.records.reverse(), this.pageSize);
      postAveList.value = aveArr(totalPostList.value, pageSize.value);
      postList.value =
        postAveList.value[0] === undefined ? [] : postAveList.value[0];

      // 过滤数据
      filterPostList.value = totalPostList.value;

      if (tokenData.value != null) {
        userInfo.value = tokenData.value;
      }
      // console.log(postAveList.value[count.value]);

      skeletonLoading.value = false;
    };

    // 均分数组
    const aveArr = (
      data: PostListMember[],
      num: number
    ): Array<PostListMember[]> => {
      // console.log(Object.keys(data).length);
      if (Object.keys(data).length === 0) return [];
      // console.log(Object.keys(data).length);

      let result: Array<PostListMember[]> = [];
      for (let i = 0, len = data.length; i < len; i += num) {
        result.push(data.slice(i, i + num));
        // console.log(data.slice(i, i + num));
      }
      // console.log("result=>", result);
      return result;
    };

    // 无限滚动加载方法
    const load = () => {
      if (postList.value.length < pageSize.value) {
        // console.log(1);
        loading.value = false;
        return false;
      }
      loading.value = true;
      setTimeout(() => {
        // this.count += 4;
        try {
          let length = 0;
          // console.log(postAveList.value[count.value]);
          length = postAveList.value[count.value].length;
          for (let i = 0; i < length; i++) {
            postList.value.push(postAveList.value[count.value][i]);

            // 过滤数据
            filterPostList.value = totalPostList.value.filter(
              (data) => {
                return data.resourceName
                  .toLowerCase()
                  .includes(searchInfo.value.toLowerCase());
              }
              // data.resourceName
              //   .toLowerCase()
              //   .includes(this.resourceName.toLowerCase())
            );
          }
          count.value++;
          loading.value = false;
        } catch (error) {
          console.log(error);
        }
      }, 300);
    };
    // 滚动事件
    // 滚动事件相关
    // const initScroll = (): void => {
    //   let initScrollTop: number = getScorllTop();
    //   let scrollType: number = 0;
    //   console.log(mainRef.value);
    //   mainRef.value!.addEventListener("scroll", () => {
    //     // console.log("initScroll");
    //     let currentScrollTop = getScorllTop();
    //     if (currentScrollTop > initScrollTop) {
    //       // 往下滚动
    //       scrollType = 1;
    //     } else {
    //       // 往上滚动
    //       scrollType = 0;
    //     }
    //     console.log(currentScrollTop);
    //     initScrollTop = currentScrollTop;
    //     if (scrollType == 1 && currentScrollTop > 100) {
    //       // showHeader.value = false;
    //       $bus.emit("changeShowHeaderEvent", false);
    //     } else {
    //       // showHeader.value = true;
    //       $bus.emit("changeShowHeaderEvent", true);
    //     }
    //   });
    // };
    // const getScorllTop = (): number => {
    //   // 获取滚动条滚动的高度
    //   // console.log(mainRef.value);

    //   // let scrollTop =
    //   //   document.documentElement.scrollTop ||
    //   //   window.pageYOffset ||
    //   //   document.body.scrollTop;
    //   let scrollTop =
    //     mainRef.value!.scrollTop ||
    //     window.pageYOffset ||
    //     document.body.scrollTop;

    //   return scrollTop;
    // };

    // 计算方法 computed
    const noMore = computed(() => {
      // console.log(postList.value);
      if (postList.value === undefined) return true;

      // return postList.value.length >= postLength.value;
      return postList.value.length >= totalPostList.value.length;
    });

    const disabled = computed(() => {
      return loading.value || noMore.value;
    });

    const showTotalPost = computed(() => {
      return searchInfo.value === "" ? postList.value : filterPostList.value;
    });

    // 监听 watch
    watch(tagNameChild, (newVal) => {
      // console.log(newVal);
      getPostPageInfo(1, 100, newVal);
      count.value = 1; // 置一
    });

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
      // 变量
      mainRef,
      loading,
      skeletonLoading,
      skeletonCount,
      count,
      pageSize,
      postLength,
      postAveList,
      postList,
      filterPostList,
      searchInfo,
      userInfo,
      activeName,
      tagNameChild, // 传递高校名
      totalPostList,

      // 方法
      getPostPageInfo,
      aveArr,
      load,
      getTagNameChild,

      // 计算属性
      noMore,
      disabled,
      showTotalPost,
    };
  },
});
</script>

<template>
  <!-- style="overflow: auto" -->
  <div
    class="main"
    ref="mainRef"
    v-infinite-scroll="load"
    infinite-scroll-disabled="disabled"
    infinite-scroll-immediate="false"
    infinite-scroll-distance="1"
  >
    <div class="main-container">
      <Tags
        class="main-tags"
        @getTagNameChild="getTagNameChild"
        style="display: none"
      ></Tags>
      <!-- 骨架屏 -->
      <el-skeleton
        animated
        :loading="skeletonLoading"
        style="max-width: 700px; display: flex"
      >
        <template #template>
          <div class="main-left" style="width: 700px">
            <el-row class="post-container">
              <el-card
                class="post-card"
                shadow="hover"
                v-for="i in skeletonCount"
                :key="i"
              >
                <el-row :gutter="20" class="post-avater-info">
                  <el-col :span="2">
                    <el-skeleton-item></el-skeleton-item>
                  </el-col>
                  <el-col :span="4">
                    <el-skeleton-item></el-skeleton-item>
                  </el-col>
                  <el-col :span="7">
                    <el-skeleton-item></el-skeleton-item>
                  </el-col>
                </el-row>
                <el-row class="title item" style="margin: 8px 0">
                  <el-col :span="10">
                    <el-skeleton-item></el-skeleton-item>
                  </el-col>
                </el-row>
                <!-- 简介 -->
                <el-row class="post-description text item">
                  <el-skeleton-item></el-skeleton-item>
                </el-row>
                <!-- 收藏 -->
                <el-row :gutter="10" class="icon-container">
                  <el-col :span="2" class="icon-item">
                    <el-skeleton-item></el-skeleton-item>
                  </el-col>
                  <el-col :span="2" class="icon-item">
                    <el-skeleton-item></el-skeleton-item>
                  </el-col>
                  <el-col :span="2" class="icon-item">
                    <el-skeleton-item></el-skeleton-item>
                  </el-col>
                </el-row>
              </el-card>
            </el-row>
          </div>
          <!-- <div class="main-right" style="width: 237px; height: 237px">
            <el-row :gutter="20">
              <el-col :span="2"></el-col>
            </el-row>
          </div> -->
        </template>
      </el-skeleton>
      <!-- 帖子相关 -->
      <div class="main-left" v-show="!skeletonLoading">
        <!-- 无限滚动数据 -->
        <div
          v-if="userInfo && userInfo.isLogin && postList.length != 0"
          class="post-container infinite-list"
        >
          <!-- v-for="item in postList" -->
          <router-link
            class="infinite-list-item"
            v-for="item in showTotalPost"
            :key="item.resourceId"
            :to="
              '/postDetail/' +
              item.resourceId +
              '?collectStatus=' +
              item.collectStatus
            "
          >
            <div class="post-item">
              <el-card class="post-card" shadow="hover">
                <div class="post-avater-info">
                  <span class="post-user-name">{{ item.userName }}</span>
                  <span>{{ item.collegeName }}</span>
                  <span>发布时间{{ item.createTime }}</span>
                </div>
                <div class="title item">{{ item.resourceName }}</div>
                <!-- 简介 -->
                <div class="post-description text item">
                  {{ item.resourceInfo }}
                </div>
                <!-- 收藏 -->
                <ul class="icon-container">
                  <!-- 
                el-icon-star-on
                el-icon-star-off
                -->
                  <li class="icon-item">
                    <el-icon><Star /></el-icon>
                    <span>{{ item.resourceScore }}</span>
                  </li>
                  <!-- <li class="icon-item">
                      <i class="el-icon-view"> </i>
                      <span>666</span>
                    </li> -->
                  <li class="icon-item">
                    <el-icon><Comment /> </el-icon>
                    <span>{{ item.commentCount }}</span>
                  </li>
                </ul>
              </el-card>
            </div>
          </router-link>
          <p class="post-card-loading" v-if="loading">加载中...</p>
          <!-- <p class="post-card-loading" v-if="noMore">没有更多了</p> -->
        </div>
        <el-empty description="暂时还没有相关帖子哦~" v-else />
      </div>
      <!-- 用户信息相关 -->
      <div class="main-right">
        <!-- welcome-svg -->
        <!-- logo -->
        <div class="main-right-wecome">
          <img
            src="@/assets/img/support-team.png"
            alt="Welcome"
            style="width: 237px"
          />
        </div>

        <!-- 头部卡片 展示个人信息 -->
        <div class="card">
          <div class="card-info">
            <div class="card-avatar">
              <default-avatar
                v-if="userInfo.avatar === ''"
                width="60px"
                height="60px"
                :avatarName="userInfo.name.split('')[0]"
              ></default-avatar>
              <img
                v-else
                :src="userInfo.avatar"
                style="width: 60px; border-radius: 50%"
                alt="头像"
              />
            </div>
            <div class="card-title">{{ userInfo.name }}</div>
            <div class="card-subtitle">{{ userInfo.collegeName }}</div>
          </div>
          <ul class="card-social">
            <li class="card-social__item">
              <i class="iconfont icon-jifen">
                <br /><span>{{ userInfo.score }}</span></i
              >
            </li>
            <li class="card-social__item">
              <el-icon
                ><Comment /> <br /><span>{{ userInfo.messageNumber }}</span>
              </el-icon>
            </li>
            <li class="card-social__item">
              <el-icon
                ><Histogram /> <br /><span>{{ postLength }}</span>
              </el-icon>
            </li>
          </ul>
        </div>

        <!-- 更新消息 -->
        <div class="upgrate-card">
          <div class="cart-container">
            <div class="upgrate-card-title">更新公告</div>
            <el-collapse v-model="activeName" accordion>
              <el-collapse-item title="2023年5月17日" name="6">
                <div>- 添加高校分类管理模块</div>
              </el-collapse-item>
              <el-collapse-item title="2023年4月13日" name="5">
                <div>- 添加了发帖审核功能，三审三校</div>
                <div>- 添加空处理模块，优化骨架屏</div>
              </el-collapse-item>
              <el-collapse-item title="2023年4月2日" name="4">
                <div>- 界面美化：在登录注册以及首页加入一些插图，美化界面</div>
                <div>
                  - 项目重构：Vue3 + Element Plus + TypeScript + Pinia +
                  Vite对本项目进行重构重构基本完成，进入测试阶段，测试地址<a
                    href="http://43.142.74.200:82/"
                    >http://43.142.74.200:82/</a
                  >
                  。
                </div>
              </el-collapse-item>
              <el-collapse-item title="2023年3月24日" name="3">
                <div>
                  -
                  帖子修改：修改帖子经过测试已经相对完善，但是还有一些小Bug，比如发帖时没有添加附件那么修改时添加附件就会报错。
                </div>
                <div>
                  - 目前目标：利用Vue3 + Element Plus + TypeScript + Pinia +
                  Vite对本项目进行重构。
                </div>
              </el-collapse-item>
              <el-collapse-item title="2023年3月23日" name="2">
                <div>
                  -
                  富文本支持：更新了发帖页面，短篇为原来的发帖页，长篇为更新后的富文本编辑。支持
                  大部分word操作。
                </div>
                <div>- 移动端适配：目前已经初步适配移动端，后续还会优化。</div>
              </el-collapse-item>
              <el-collapse-item title="2023年3月22日" name="1">
                <div>
                  - docker时区问题：修复了实际发帖时间与北京时间的偏差。
                </div>
              </el-collapse-item>
              <el-collapse-item title="之前的更新" name="0">
                <div>
                  - 骨架屏支持：支持首页以及帖子详情页面的骨架屏，优化首屏加载。
                </div>
                <div>
                  - 文件上传优化：开放了大部分编程源文件格式的文件上传。
                </div>
                <div>- 文件的预览：支持图片、视频、文档的预览。</div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>

        <!-- logo  -->
        <div class="main-right-wecome">
          <img
            src="@/assets/img/forpeople.png"
            alt="forpeople"
            style="width: 237px"
          />
        </div>
      </div>
    </div>

    <!-- 无数据时使用 -->
    <div class="no-data" v-if="postLength === 0 || noMore">
      <div class="loader">
        <div>
          <ul>
            <li>
              <svg fill="currentColor" viewBox="0 0 90 120">
                <path
                  d="M90,0 L90,120 L11,120 C4.92486775,120 0,115.075132 0,109 L0,11 C0,4.92486775 4.92486775,0 11,0 L90,0 Z M71.5,81 L18.5,81 C17.1192881,81 16,82.1192881 16,83.5 C16,84.8254834 17.0315359,85.9100387 18.3356243,85.9946823 L18.5,86 L71.5,86 C72.8807119,86 74,84.8807119 74,83.5 C74,82.1745166 72.9684641,81.0899613 71.6643757,81.0053177 L71.5,81 Z M71.5,57 L18.5,57 C17.1192881,57 16,58.1192881 16,59.5 C16,60.8254834 17.0315359,61.9100387 18.3356243,61.9946823 L18.5,62 L71.5,62 C72.8807119,62 74,60.8807119 74,59.5 C74,58.1192881 72.8807119,57 71.5,57 Z M71.5,33 L18.5,33 C17.1192881,33 16,34.1192881 16,35.5 C16,36.8254834 17.0315359,37.9100387 18.3356243,37.9946823 L18.5,38 L71.5,38 C72.8807119,38 74,36.8807119 74,35.5 C74,34.1192881 72.8807119,33 71.5,33 Z"
                ></path>
              </svg>
            </li>
            <li>
              <svg fill="currentColor" viewBox="0 0 90 120">
                <path
                  d="M90,0 L90,120 L11,120 C4.92486775,120 0,115.075132 0,109 L0,11 C0,4.92486775 4.92486775,0 11,0 L90,0 Z M71.5,81 L18.5,81 C17.1192881,81 16,82.1192881 16,83.5 C16,84.8254834 17.0315359,85.9100387 18.3356243,85.9946823 L18.5,86 L71.5,86 C72.8807119,86 74,84.8807119 74,83.5 C74,82.1745166 72.9684641,81.0899613 71.6643757,81.0053177 L71.5,81 Z M71.5,57 L18.5,57 C17.1192881,57 16,58.1192881 16,59.5 C16,60.8254834 17.0315359,61.9100387 18.3356243,61.9946823 L18.5,62 L71.5,62 C72.8807119,62 74,60.8807119 74,59.5 C74,58.1192881 72.8807119,57 71.5,57 Z M71.5,33 L18.5,33 C17.1192881,33 16,34.1192881 16,35.5 C16,36.8254834 17.0315359,37.9100387 18.3356243,37.9946823 L18.5,38 L71.5,38 C72.8807119,38 74,36.8807119 74,35.5 C74,34.1192881 72.8807119,33 71.5,33 Z"
                ></path>
              </svg>
            </li>
            <li>
              <svg fill="currentColor" viewBox="0 0 90 120">
                <path
                  d="M90,0 L90,120 L11,120 C4.92486775,120 0,115.075132 0,109 L0,11 C0,4.92486775 4.92486775,0 11,0 L90,0 Z M71.5,81 L18.5,81 C17.1192881,81 16,82.1192881 16,83.5 C16,84.8254834 17.0315359,85.9100387 18.3356243,85.9946823 L18.5,86 L71.5,86 C72.8807119,86 74,84.8807119 74,83.5 C74,82.1745166 72.9684641,81.0899613 71.6643757,81.0053177 L71.5,81 Z M71.5,57 L18.5,57 C17.1192881,57 16,58.1192881 16,59.5 C16,60.8254834 17.0315359,61.9100387 18.3356243,61.9946823 L18.5,62 L71.5,62 C72.8807119,62 74,60.8807119 74,59.5 C74,58.1192881 72.8807119,57 71.5,57 Z M71.5,33 L18.5,33 C17.1192881,33 16,34.1192881 16,35.5 C16,36.8254834 17.0315359,37.9100387 18.3356243,37.9946823 L18.5,38 L71.5,38 C72.8807119,38 74,36.8807119 74,35.5 C74,34.1192881 72.8807119,33 71.5,33 Z"
                ></path>
              </svg>
            </li>
            <li>
              <svg fill="currentColor" viewBox="0 0 90 120">
                <path
                  d="M90,0 L90,120 L11,120 C4.92486775,120 0,115.075132 0,109 L0,11 C0,4.92486775 4.92486775,0 11,0 L90,0 Z M71.5,81 L18.5,81 C17.1192881,81 16,82.1192881 16,83.5 C16,84.8254834 17.0315359,85.9100387 18.3356243,85.9946823 L18.5,86 L71.5,86 C72.8807119,86 74,84.8807119 74,83.5 C74,82.1745166 72.9684641,81.0899613 71.6643757,81.0053177 L71.5,81 Z M71.5,57 L18.5,57 C17.1192881,57 16,58.1192881 16,59.5 C16,60.8254834 17.0315359,61.9100387 18.3356243,61.9946823 L18.5,62 L71.5,62 C72.8807119,62 74,60.8807119 74,59.5 C74,58.1192881 72.8807119,57 71.5,57 Z M71.5,33 L18.5,33 C17.1192881,33 16,34.1192881 16,35.5 C16,36.8254834 17.0315359,37.9100387 18.3356243,37.9946823 L18.5,38 L71.5,38 C72.8807119,38 74,36.8807119 74,35.5 C74,34.1192881 72.8807119,33 71.5,33 Z"
                ></path>
              </svg>
            </li>
            <li>
              <svg fill="currentColor" viewBox="0 0 90 120">
                <path
                  d="M90,0 L90,120 L11,120 C4.92486775,120 0,115.075132 0,109 L0,11 C0,4.92486775 4.92486775,0 11,0 L90,0 Z M71.5,81 L18.5,81 C17.1192881,81 16,82.1192881 16,83.5 C16,84.8254834 17.0315359,85.9100387 18.3356243,85.9946823 L18.5,86 L71.5,86 C72.8807119,86 74,84.8807119 74,83.5 C74,82.1745166 72.9684641,81.0899613 71.6643757,81.0053177 L71.5,81 Z M71.5,57 L18.5,57 C17.1192881,57 16,58.1192881 16,59.5 C16,60.8254834 17.0315359,61.9100387 18.3356243,61.9946823 L18.5,62 L71.5,62 C72.8807119,62 74,60.8807119 74,59.5 C74,58.1192881 72.8807119,57 71.5,57 Z M71.5,33 L18.5,33 C17.1192881,33 16,34.1192881 16,35.5 C16,36.8254834 17.0315359,37.9100387 18.3356243,37.9946823 L18.5,38 L71.5,38 C72.8807119,38 74,36.8807119 74,35.5 C74,34.1192881 72.8807119,33 71.5,33 Z"
                ></path>
              </svg>
            </li>
            <li>
              <svg fill="currentColor" viewBox="0 0 90 120">
                <path
                  d="M90,0 L90,120 L11,120 C4.92486775,120 0,115.075132 0,109 L0,11 C0,4.92486775 4.92486775,0 11,0 L90,0 Z M71.5,81 L18.5,81 C17.1192881,81 16,82.1192881 16,83.5 C16,84.8254834 17.0315359,85.9100387 18.3356243,85.9946823 L18.5,86 L71.5,86 C72.8807119,86 74,84.8807119 74,83.5 C74,82.1745166 72.9684641,81.0899613 71.6643757,81.0053177 L71.5,81 Z M71.5,57 L18.5,57 C17.1192881,57 16,58.1192881 16,59.5 C16,60.8254834 17.0315359,61.9100387 18.3356243,61.9946823 L18.5,62 L71.5,62 C72.8807119,62 74,60.8807119 74,59.5 C74,58.1192881 72.8807119,57 71.5,57 Z M71.5,33 L18.5,33 C17.1192881,33 16,34.1192881 16,35.5 C16,36.8254834 17.0315359,37.9100387 18.3356243,37.9946823 L18.5,38 L71.5,38 C72.8807119,38 74,36.8807119 74,35.5 C74,34.1192881 72.8807119,33 71.5,33 Z"
                ></path>
              </svg>
            </li>
          </ul>
        </div>
        <span>没有帖子了呢~~~,快点击右上角发帖吧！</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
/*大型屏幕pc 超大屏*/
@media screen and (min-width: 1200px) {
}
/*1200>=pc>=992 大屏，字体红色，背景黑色*/
@media screen and (min-width: 992px) and (max-width: 1199px) {
}
/*768<=pad<992 中屏，字体黄色，背景红色*/
@media screen and (min-width: 768px) and (max-width: 991px) {
  .main {
    // max-height: 730px;
    .main-container {
      // max-height: 730px;

      // .main-left {
      // }

      .main-right {
        display: none;
      }

      // .main-right {
      //   .card {
      //     width: 100%;
      //     width: 400px;
      //   }
      // }
    }

    .no-data {
      display: flex;
    }
  }
}
/*phone<768  小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 767px) and (min-width: 480px) {
  .main {
    .main-container {
      // .main-left {
      // }

      .main-right {
        display: none;
        // margin-top: 20px;
        // .card {
        //   width: 100%;
        //   // width: 400px;
        // }
      }
    }

    .no-data {
      display: flex;
    }
  }
}
/* 超小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 480px) {
  .main {
    .main-container {
      // .main-left {
      //   margin-bottom: 70px;
      //   padding-top: 70px;
      // }

      .main-right {
        display: none;
      }
      // .main-right {
      //   margin-top: 20px;
      //   width: 390px;

      //   // padding-top: 70px;
      //   .card {
      //     width: calc(100% - 34px) !important;
      //     margin: 0 auto;
      //   }
      // }
    }
  }
}

.main {
  // min-height: 730px;
  height: 100%;

  .main-container {
    // min-height: 730px;
    position: relative;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    padding-top: 70px;
    margin: 0 auto;

    .main-tags {
      margin: 0 0;
      // width: 120px;
      height: 500px;
    }

    // 左侧帖子
    .main-left {
      width: 700px;

      .post-container {
        max-width: 680px;
        // min-width: 460px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        margin: 0 auto;
        // flex-wrap: no-wrap;

        // .infinite-list-item:first-child {
        //   margin-top: 300px;
        // }

        .post-card-loading {
          align-items: center;
          margin: 10px auto;
        }

        // post 帖子
        .post-item {
          .post-card {
            position: relative;
            height: 127px;
            background-color: #f9fbff; // card 颜色

            .el-card__body {
              padding-top: 30px;
              // padding-bottom: 20px;

              .item {
              }

              .post-avater-info {
                // display: flex;
                position: absolute;
                top: 8px;
                left: 18px;

                .post-user-name {
                  padding-left: 0;
                }

                span {
                  font-size: 12px;
                  padding-right: 12px;
                  padding-left: 12px;
                  border-right: 1px solid #8a919f;
                  color: #8a919f;
                }
              }

              .title {
                font-weight: 700;
                font-size: 16px;
                line-height: 24px;
                color: #1d2129;
                margin-bottom: 8px;
                margin-top: 8px;
                display: -webkit-box;
                overflow: hidden;
                text-overflow: ellipsis;
                -webkit-box-orient: vertical;
                -webkit-line-clamp: 1;
              }

              .text {
                color: #8a919f;
                font-size: 13px;
                line-height: 22px;
                display: -webkit-box;
                overflow: hidden;
                text-overflow: ellipsis;
                -webkit-box-orient: vertical;
                -webkit-line-clamp: 1;
              }

              .item {
                margin-bottom: 8px;
              }

              .icon-container {
                display: flex;

                .icon-item {
                  text-decoration: none;
                  list-style: none;
                  margin-right: 20px;
                  color: #8a919f;
                  cursor: pointer;
                  font-size: 12px;

                  span {
                    margin-left: 4px;
                  }
                }

                .icon-item:hover {
                  color: #409eff;
                }
              }

              // .clearfix:before,
              // .clearfix:after {
              //   display: table;
              //   content: "";
              // }

              // .clearfix:after {
              //   clear: both;
              // }
            }
          }

          .post-card:hover {
            background-color: #fafafa;
          }
        }
      }

      // 动画效果
      .post-container {
        -webkit-animation: fade-in-fwd 0.6s cubic-bezier(0.39, 0.575, 0.565, 1)
          both;
        animation: fade-in-fwd 0.6s cubic-bezier(0.39, 0.575, 0.565, 1) both;
      }

      @-webkit-keyframes fade-in-fwd {
        0% {
          -webkit-transform: translateZ(-80px);
          transform: translateZ(-80px);
          opacity: 0;
        }

        100% {
          -webkit-transform: translateZ(0);
          transform: translateZ(0);
          opacity: 1;
        }
      }

      @keyframes fade-in-fwd {
        0% {
          -webkit-transform: translateZ(-80px);
          transform: translateZ(-80px);
          opacity: 0;
        }

        100% {
          -webkit-transform: translateZ(0);
          transform: translateZ(0);
          opacity: 1;
        }
      }
    }

    // 右侧信息
    .main-right {
      // position: fixed;
      .upgrate-card {
        width: 190px;
        // height: 254px;
        margin-top: 10px;
        background: #f9fbff;
        // background: #f5f5f5;
        padding: 1rem 1.5rem;
        transition: box-shadow 0.3s ease, transform 0.2s ease;

        .upgrate-card-title {
          margin-bottom: 5px;
          font-weight: bold;
        }
      }

      .upgrate-card:hover {
        box-shadow: 0 8px 50px #23232333;
      }

      // 头部展示个人信息
      .card {
        width: 190px;
        height: 254px;
        background: #f9fbff;
        // background: #f5f5f5;
        padding: 2rem 1.5rem;
        transition: box-shadow 0.3s ease, transform 0.2s ease;
      }

      .card-info {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        transition: transform 0.2s ease, opacity 0.2s ease;
      }

      /*Image*/
      .card-avatar {
        --size: 60px;
        background: linear-gradient(to top, #f1e1c1 0%, #fcbc97 100%);
        width: var(--size);
        height: var(--size);
        border-radius: 50%;
        transition: transform 0.2s ease;
        margin-bottom: 1rem;
      }

      /*Card footer*/
      .card-social {
        transform: translateY(200%);
        display: flex;
        justify-content: space-around;
        width: 100%;
        opacity: 0;
        transition: transform 0.2s ease, opacity 0.2s ease;
      }

      .card-social__item {
        list-style: none;
      }

      .card-social__item i {
        display: block;
        height: 18px;
        line-height: 18px;
        width: 18px;
        fill: #515f65;
        cursor: pointer;
        transition: fill 0.2s ease, transform 0.2s ease;
      }

      /*Text*/
      .card-title {
        color: #333;
        font-size: 1.5em;
        font-weight: 600;
        line-height: 2rem;
      }

      .card-subtitle {
        color: #859ba8;
        font-size: 0.8em;
      }

      /*Hover*/
      .card:hover {
        box-shadow: 0 8px 50px #23232333;
      }

      .card:hover .card-info {
        transform: translateY(-5%);
      }

      .card:hover .card-social {
        transform: translateY(100%);
        opacity: 1;
      }

      .card-social__item i:hover {
        fill: #232323;
        transform: scale(1.1);
      }

      .card-avatar:hover {
        transform: scale(1.1);
      }
    }
  }

  .no-data {
    display: flex;
    justify-content: center;
    margin-top: 40px;
    margin-bottom: 100px;

    // loading
    .loader {
      --background: linear-gradient(135deg, #23c4f8, #275efe);
      --shadow: rgba(39, 94, 254, 0.28);
      --text: #6c7486;
      --page: rgba(255, 255, 255, 0.36);
      --page-fold: rgba(255, 255, 255, 0.52);
      --duration: 3s;
      width: 200px;
      height: 140px;
      position: relative;
    }

    .loader:before,
    .loader:after {
      --r: -6deg;
      content: "";
      position: absolute;
      bottom: 8px;
      width: 120px;
      top: 80%;
      box-shadow: 0 16px 12px var(--shadow);
      transform: rotate(var(--r));
    }

    .loader:before {
      left: 4px;
    }

    .loader:after {
      --r: 6deg;
      right: 4px;
    }

    .loader div {
      width: 100%;
      height: 100%;
      border-radius: 13px;
      position: relative;
      z-index: 1;
      perspective: 600px;
      box-shadow: 0 4px 6px var(--shadow);
      background-image: var(--background);
    }

    .loader div ul {
      margin: 0;
      padding: 0;
      list-style: none;
      position: relative;
    }

    .loader div ul li {
      --r: 180deg;
      --o: 0;
      --c: var(--page);
      position: absolute;
      top: 10px;
      left: 10px;
      transform-origin: 100% 50%;
      color: var(--c);
      opacity: var(--o);
      transform: rotateY(var(--r));
      -webkit-animation: var(--duration) ease infinite;
      animation: var(--duration) ease infinite;
    }

    .loader div ul li:nth-child(2) {
      --c: var(--page-fold);
      -webkit-animation-name: page-2;
      animation-name: page-2;
    }

    .loader div ul li:nth-child(3) {
      --c: var(--page-fold);
      -webkit-animation-name: page-3;
      animation-name: page-3;
    }

    .loader div ul li:nth-child(4) {
      --c: var(--page-fold);
      -webkit-animation-name: page-4;
      animation-name: page-4;
    }

    .loader div ul li:nth-child(5) {
      --c: var(--page-fold);
      -webkit-animation-name: page-5;
      animation-name: page-5;
    }

    .loader div ul li svg {
      width: 90px;
      height: 120px;
      display: block;
    }

    .loader div ul li:first-child {
      --r: 0deg;
      --o: 1;
    }

    .loader div ul li:last-child {
      --o: 1;
    }

    .loader span {
      display: block;
      left: 0;
      right: 0;
      top: 100%;
      margin-top: 20px;
      text-align: center;
      color: var(--text);
    }

    @keyframes page-2 {
      0% {
        transform: rotateY(180deg);
        opacity: 0;
      }

      20% {
        opacity: 1;
      }

      35%,
      100% {
        opacity: 0;
      }

      50%,
      100% {
        transform: rotateY(0deg);
      }
    }

    @keyframes page-3 {
      15% {
        transform: rotateY(180deg);
        opacity: 0;
      }

      35% {
        opacity: 1;
      }

      50%,
      100% {
        opacity: 0;
      }

      65%,
      100% {
        transform: rotateY(0deg);
      }
    }

    @keyframes page-4 {
      30% {
        transform: rotateY(180deg);
        opacity: 0;
      }

      50% {
        opacity: 1;
      }

      65%,
      100% {
        opacity: 0;
      }

      80%,
      100% {
        transform: rotateY(0deg);
      }
    }

    @keyframes page-5 {
      45% {
        transform: rotateY(180deg);
        opacity: 0;
      }

      65% {
        opacity: 1;
      }

      80%,
      100% {
        opacity: 0;
      }

      95%,
      100% {
        transform: rotateY(0deg);
      }
    }
  }
}
</style>
