<script lang="ts">
import { ref, onMounted, defineComponent, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import $bus from "@/libs/eventBus";

import { getCollegeList } from "@/api/login";
import { getToken } from "@/utils/auth";
import { UserInfoMember } from "@/store";
import { CollegeMember } from "../register/index.vue";

// swiper
import { register } from "swiper/element/bundle";
register();

export interface IntroMenuListMember {
  name: string;
  active: string;
}

export default defineComponent({
  setup(proxy, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例
    // 全局属性
    // const route = useRoute();
    const router = useRouter();

    // swiper配置
    const swiperOption = ref<any>({
      slidesPerView: 1,
      spaceBetween: 10,
      centeredSlides: "true",
      pagination: {
        hideOnClick: true,
      },
      breakpoints: {
        slidesPerView: 1,
      },
      autoplay: {
        delay: 3000, // 每次轮播间隔的ms数，默认3000ms
        stopOnLastSlide: false, // 播放完最后一张图片后是否停留在最后一张图片上，停止继续轮播。默认false
        disableOnInteraction: false, // 用户操作轮播图后，比如点击轮播按钮或小圆点，停止轮播
      },
      loop: true,
      observer: true, //修改swiper自己或子元素时，自动初始化swiper
      observeParents: true, //修改swiper的父元素时，自动初始化swiper
      observeSlideChildren: true,
      // effect: "fade",
      // 其他配置...
    });

    // dom
    const introMenu_ = ref<HTMLElement[]>();

    // 响应式变量
    const btnActive = ref<string>("vue");
    const introMenuActive = ref<string>();
    const introMenuList = ref<IntroMenuListMember[]>([
      {
        name: "项目介绍",
        active: "intro__project",
      },
      {
        name: "技术架构",
        active: "skill__stack",
      },
    ]);
    const collegeList = ref<CollegeMember[]>();
    const userInfo = ref<UserInfoMember | {}>({});
    const lottieOptions = {
      minimizable: false,
      playerSize: "standard",
      backgroundColor: "#fafafa",
      backgroundStyle: "color",
      loop: true, // 循环播放
      autoplay: true, // 自动播放
      playerControl: false, // 是否显示控件
      theme: {
        controlsView: "standard",
        active: "light",
        light: {
          color: "#3D4852",
          backgroundColor: "#fff",
          opacity: "0.7",
        },
        dark: {
          color: "#fff",
          backgroundColor: "#202020",
          opacity: "0.7",
        },
      },
    };

    // 生命周期钩子
    onMounted(() => {
      // 获取用户信息
      userInfo.value = getTokenData();

      $bus.on("resetUserInfo", (val) => {
        // this.userInfo = this.getTokenData();
        userInfo.value = {};
      });

      // 获取高校信息
      getCollegeListInfo();
      nextTick(() => {
        // 默认展示第一个
        clickMenu(0, "intro__project");
      });
    });

    // 方法 methods

    // swiper相关
    const spaceBetween = 10;
    const onProgress = (e: any) => {
      const [swiper, progress] = e.detail;
      // console.log(progress);
    };

    const onSlideChange = (e: any) => {
      console.log("slide changed");
    };

    // 获取加入高校
    const getCollegeListInfo = async () => {
      const { data } = await getCollegeList();
      collegeList.value = data;
    };

    const toLogin = () => {
      if (window.location.href.split("#")[1] != "/login") {
        router.push({ path: "/login" });
      }
    };

    const toHome = () => {
      if (window.location.href.split("#")[1] != "/") {
        router.push({ path: "/" });
      }
    };

    const changeActive = (val: string) => {
      btnActive.value = val;

      nextTick(() => {
        console.log(1);
        // register();
      });
    };

    const getTokenData = (): UserInfoMember | {} => {
      let token = getToken();
      if (!token) return {};

      let data = JSON.parse(token);
      // console.log(data);
      // 如果是空值 undefined or null 返回空对象 | 适应Object.keys()
      return !data ? {} : data;
    };

    const clickMenu = (index: number, activeName: string) => {
      // console.log(index);
      console.log(introMenu_);
      if (!introMenu_.value) return;
      // 切换颜色
      for (let i = 0; i < introMenu_.value.length; i++) {
        if (i === index) {
          introMenu_.value[i].className = "intro-menu-item active";
        } else {
          introMenu_.value[i].className = "intro-menu-item";
        }
      }

      // 导航切换
      introMenuActive.value = activeName;
    };

    return {
      // dom
      introMenu_,
      // 响应式变量
      btnActive,
      introMenuActive,
      introMenuList,
      collegeList,
      userInfo,
      lottieOptions,
      // swiper相关方法
      swiperOption,
      spaceBetween,
      onProgress,
      onSlideChange,

      // 方法
      getCollegeListInfo,
      toLogin,
      toHome,
      changeActive,
      clickMenu,
    };
  },
});
</script>

<template>
  <div class="introduce-container">
    <div class="introduce-text-container">
      <!-- <div class="title">基于联盟链的高校课程资源共享平台</div>
      <div class="intro">
        利用区块链技术搭建课程资源共享平台，通过“共享账本”可以实现方便、快捷、灵活、低成本的共享多种数据格式的课程资源，打造“轻共享”、多边交流的新模式，以利于个性化的课程资源建设
      </div> -->
      <div class="title">
        烛光之窗——乡村地区中小学“点对点”式资源共享共建平台
      </div>
      <div class="intro">
        以网站为主要载体，引入区块链的技术确保平台用户各方“点对点”式的安全可靠的交流，致力于提高乡村教育软实力，使得科技反哺教育，推动乡村振兴。
      </div>
      <button
        class="intro-tologin"
        v-if="Object.keys(userInfo).length === 0"
        @click="toLogin"
      >
        登录
        <div class="arrow-wrapper">
          <div class="arrow"></div>
        </div>
      </button>
      <button class="intro-tologin" v-else @click="toHome">
        首页
        <div class="arrow-wrapper">
          <div class="arrow"></div>
        </div>
      </button>
      <div class="intro-bg-img01">
        <img src="@/assets/img/Coding_Two_Color.png" alt="代码" />
      </div>
      <div class="intro-bg-img02">
        <img src="@/assets/img/Java_Two_Color.png" alt="代码" />
      </div>
      <div class="intro-bg-img03">
        <img src="@/assets/img/Python_Flatline.png" alt="代码" />
      </div>
      <div class="intro-bg-img04">
        <img src="@/assets/img/Javascript_Two_Color.png" alt="代码" />
      </div>

      <!-- <div class="loader"></div> -->
    </div>

    <!-- 二级导航 -->
    <div class="intro-menu">
      <div
        v-for="(menuItem, index) in introMenuList"
        class="intro-menu-item"
        ref="introMenu_"
        @click="clickMenu(index, menuItem.active)"
      >
        <span>{{ menuItem.name }}</span>
      </div>
    </div>

    <!-- 项目介绍 -->
    <div v-show="introMenuActive === 'intro__project'">
      <!-- 轻共享 -->
      <div class="intro-light-sharing">
        <div class="light-sharing-left">
          <Vue3Lottie
            animationLink="https://assets8.lottiefiles.com/packages/lf20_9ti102vm.json"
            :autoplay="lottieOptions.autoplay"
            :loop="lottieOptions.loop"
            class="light-sharing-lottie"
            style="width: 500px; height: 400px"
          >
          </Vue3Lottie>
        </div>
        <div class="sharing-container">
          <div class="sharing-title">轻共享、多边化交流</div>
          <div class="sharing-description">
            平台基于区块链实现简单和灵活的方式共享资源、信息或技术。促进高校老师之间的合作和交流，实现更开放、包容和多元化的环境。
          </div>
        </div>
      </div>

      <!-- 加入高校 -->
      <div class="intro-college-join">
        <div class="college-intro-container">
          <div class="college-intro">
            <div class="college-title">加入的高校</div>
            <div class="college-description">
              在2019年和2020年两年里，教育部先后颁布了《关于深化本科教育教学改革全面提高人才培养质量的意见》《高等学校课程思政建设指导纲要》等文件，对高等教育提出了“以新工科、新医科、新农科、新文科建设引领带动高校专业结构调整优化和内涵提升”、“全面推进高校课程思政建设”、“发挥好每门课程的育人功能，提高高校人才培养质量”的指导思想。无论是“四新”建设还是课程思政都对任课教师提出了新的要求，如何机动、灵活、严谨、持续地建设个性化的、与之相适应的课程资源，是新一轮教学改革的重要组成部分。
            </div>
          </div>
          <div class="college-list">
            <ul class="intro-college">
              <li class="intro-college-item" v-for="collegeItem in collegeList">
                {{ collegeItem.name }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- 技术架构 -->
    <div v-show="introMenuActive === 'skill__stack'">
      <!-- 系统架构介绍 -->
      <div class="intro-framework-stack">
        <div class="framework-intro-container">
          <div class="framework-intro">
            <div class="framework-title">系统架构图</div>
            <div class="framework-description">
              前端系统使用目前主流的JavaScript框架——Vue+Element
              UI进行搭建，前后端交互通过RestFul风格约定接口格式，后端系统拿到数据之后进行参数校验、请求处理、响应脱敏、文件传输、增加操作区块等操作，后端系统对接数据库系统和区块链系统，前者负责对实体数据的持久化操作，后者负责对操作数据的持久化操作，最终实现整个系统的正向循环。<br />
              该项目的结构严格按照目前主流的商业软件架构标准进行设计，实现低耦合，高内聚的软件设计目标，后期维护项目简单方便，贯彻分布式项目思想，可拓展性强，系统鲁棒性强。
            </div>
          </div>
          <div class="framework-img">
            <img
              src="@/assets/img/整体系统架构设计图.png"
              alt="整体系统架构设计图"
            />
          </div>
        </div>
      </div>

      <!-- 技术栈介绍 -->
      <div class="intro-skill-stack">
        <div class="skill-container">
          <div class="skill-left-icon">
            <div class="up">
              <button class="card1" @click="changeActive('vue')">
                <i class="iconfont icon-Vue vue" style="font-size: 30px"></i>
              </button>
              <button class="card2" @click="changeActive('java')">
                <i class="iconfont icon-java java" style="font-size: 30px"></i>
              </button>
            </div>
            <div class="down">
              <button class="card3" @click="changeActive('hyperledger')">
                <i
                  class="iconfont icon-hyperledger hyperledger"
                  style="font-size: 30px"
                ></i>
              </button>
            </div>
          </div>
          <div class="skill-intro" v-if="btnActive === 'vue'">
            <div class="skill-title">前端技术栈</div>
            <!-- vue -->
            <swiper-container
              :slides-per-view="swiperOption.slidesPerView"
              :space-between="swiperOption.spaceBetween"
              :centered-slides="swiperOption.centeredSlides"
              :pagination="{
                hideOnClick: swiperOption.pagination?.hideOnClick,
              }"
              :breakpoints="{
                768: {
                  slidesPerView: swiperOption.breakpoints?.slidesPerView,
                },
              }"
              :autoplay="{
                delay: swiperOption.autoplay?.delay,
              }"
              @progress="onProgress"
              @slidechange="onSlideChange"
              class="swiper-container swiper-container-vue"
              v-if="btnActive === 'vue'"
            >
              <swiper-slide class="swiper-slide swiper-slide1 skill-intro-item">
                <div class="skill-item-title">Vue渐进式JS框架</div>
                <div class="skill-description">
                  Vue是一款功能强大的JavaScript框架，它可以用于构建用户界面并且覆盖了很多前端开发中常见的需求。Vue的特点包括轻量级、响应式、组件化、虚拟DOM等，并且拥有丰富的生态。例如，Vuex可以帮助管理状态，VueRouter可以帮助管理路由，Vue-cli可以帮助构建脚手架，而Devtools则可以帮助调试。使用Vue的开发者可以借助这些工具更方便地解决问题，提高开发效率。<br /><br />
                  在网络请求方面，我们可以选择基于Promise的Axios库来实现请求。Axios支持创建XMLHttpRequest、异步Promise
                  API以及请求和响应的拦截。借助Axios，我们可以更方便地实现数据的请求和处理。同时，我们可以通过封装和拓展Axios的一些功能来满足不同的需求。
                </div>
              </swiper-slide>
              <swiper-slide class="swiper-slide swiper-slide3">
                <div class="skill-item-title">组件库框架</div>
                <div class="skill-description">
                  Element UI是一套为开发者、设计师和产品经理准备的基于Vue
                  2.0的桌面端元组件库，但并不依赖于Vue，是一个十分适合Vue项目的框架，可使用Element
                  UI轻松制作出网页。<br /><br />
                  ECharts是一个开源可视化库，提供直观，交互丰富，可高度个性化定制的数据可视化图表。
                </div>
              </swiper-slide>
              <swiper-slide class="swiper-slide swiper-slide2">
                <div class="skill-item-title">Webpack构建工具</div>
                <div class="skill-description">
                  Webpack是一个用于现代JavaScript应用程序的
                  静态模块打包工具。它会在内部从一个或多个入口点构建一个依赖图(Dependency
                  Graph)，可以帮助项目对资源文件的压缩，比如JS压缩、CSS压缩、编译模板文件等待，从而减少前端的工作量。
                </div>
              </swiper-slide>
            </swiper-container>
          </div>
          <div class="skill-intro" v-if="btnActive === 'java'">
            <div class="skill-title">后端技术栈</div>
            <swiper-container
              :slides-per-view="swiperOption.slidesPerView"
              :space-between="swiperOption.spaceBetween"
              :centered-slides="swiperOption.centeredSlides"
              :pagination="{
                hideOnClick: swiperOption.pagination?.hideOnClick,
              }"
              :breakpoints="{
                768: {
                  slidesPerView: swiperOption.breakpoints?.slidesPerView,
                },
              }"
              :autoplay="{
                delay: swiperOption.autoplay?.delay,
              }"
              @progress="onProgress"
              @slidechange="onSlideChange"
              class="swiper-container swiper-container-java"
            >
              <swiper-slide class="swiper-slide swiper-slide1 skill-intro-item">
                <div class="skill-item-title">SpringBoot轻量级Java框架</div>
                <div class="skill-description">
                  Spring
                  Framework是一个开源的Java轻量级框架，它通过依赖注入（Dependency
                  Injection）和面向切面编程（Aspect Oriented
                  Programming）等技术为开发者提供了一个全面的编程和配置模型。它可以降低Java开发的复杂度，提高代码的可维护性和可测试性。<br /><br />
                  SpringBoot框架是基于Spring Framework
                  4.0的设计，不仅继承了Spring
                  Framework原有的优秀特性，而且还通过简化配置文件来进一步优化了Spring应用的整个搭建和开发过程。另SpringBoot通过集成大量的框架使得依赖包的版本冲突，以及引用的不稳定性等问题得到了良好的解决。
                </div>
              </swiper-slide>
              <swiper-slide class="swiper-slide swiper-slide3">
                <div class="skill-item-title">Mybatis-Plus 数据库框架</div>
                <div class="skill-description">
                  MyBatis最初是Apache的一个开源项目，同时是一个基于Java的持久层框架。iBatis提供的持久层框架包括SQL
                  Maps和Data Access
                  Objects（DAO）。MyBatis支持定制化SQL、存储过程以及高级映射，避免了几乎所有的JDBC代码和手动设置参数以及结果集解析操作，同时MyBatis是一个半自动的ORM（Object
                  Relation Mapping）框架。 <br /><br />
                  MyBatis-Plus是一个MyBatis的增强工具，在MyBatis的基础上只做增强不做改变，为简化开发、提高效率而生。
                </div>
              </swiper-slide>
              <swiper-slide class="swiper-slide swiper-slide2">
                <div class="skill-item-title">OSS对象存储服务技术</div>
                <div class="skill-description">
                  针对于该项目需要堆放大量课程资料的需求，使用OSS对象存储服务来进行文件存储；结合项目架构适配需求，在各大云服务厂商的相关产品中，该项目选择腾讯云的存储服务，使用OSS的优点如下：<br />
                  （1）文件上传下载速度不受项目服务器自身的影响，同时也不影响项目本身；<br />
                  （2）传输速度相较于直接存储在项目服务器中得到极大提升；<br />
                  （3）腾讯云自身的安防措施为项目提供了数据安全的保障；<br />
                  （4）对于项目而言，腾讯云对象存储服务相对资费较低，减少项目成本。<br />
                </div>
              </swiper-slide>
            </swiper-container>
          </div>
          <div class="skill-intro" v-if="btnActive === 'hyperledger'">
            <div class="skill-title">区块链技术栈</div>
            <swiper-container
              :slides-per-view="swiperOption.slidesPerView"
              :space-between="swiperOption.spaceBetween"
              :centered-slides="swiperOption.centeredSlides"
              :pagination="{
                hideOnClick: swiperOption.pagination?.hideOnClick,
              }"
              :breakpoints="{
                768: {
                  slidesPerView: swiperOption.breakpoints?.slidesPerView,
                },
              }"
              :autoplay="{
                delay: swiperOption.autoplay?.delay,
              }"
              @progress="onProgress"
              @slidechange="onSlideChange"
              class="swiper-container swiper-container-hyperledger"
            >
              <swiper-slide class="swiper-slide swiper-slide1 skill-intro-item">
                <div class="skill-item-title">Hyperledger Fabric框架</div>
                <div class="skill-description">
                  Hyperledger
                  Fabric是最流行的区块链开发框架之一，主要用来搭建区块链网络，用链码实现Fabric链上数据加密/解密，主要特点有可插拔组件、支持私密交易的通道、适合联盟链应用的开发。
                </div>
              </swiper-slide>
              <swiper-slide class="swiper-slide swiper-slide3">
                <div class="skill-item-title">
                  Hyperledger explorer区块链浏览器
                </div>
                <div class="skill-description">
                  Hyperledger
                  Explorer是一个简单，功能强大，易于使用，高度可维护的开源区块链浏览器，用于查看底层区块链网络上的活动，目的是为了能够监控链码、查看区块、调用链码等。
                </div>
              </swiper-slide>
              <swiper-slide class="swiper-slide swiper-slide2">
                <div class="skill-item-title">Prometheus + Grafana监控服务</div>
                <div class="skill-description">
                  Prometheus是一个开源的监控系统，由监控服务、告警服务、时序数据库等几个部分，及周边生态的各种指标收集器（Exporter）组成，是在当下主流的云原生监控告警系统，自动发现监控端点,
                  提供了动态的监控能力。<br /><br />
                  Grafana是用于可视化大型测量数据的开源程序，他提供了强大和优雅的方式去创建、共享、浏览数据。
                </div>
              </swiper-slide>
              <swiper-slide class="swiper-slide swiper-slide4">
                <div class="skill-item-title">Fabric Gateway</div>
                <div class="skill-description">
                  Fabric
                  Gateway提供给应用与Fabric网络交互的SDK，用Java来实现。目的是让客户端应用开发者能够专注于业务逻辑，而不必关系Fabric的基础架构逻辑。
                </div>
              </swiper-slide>
            </swiper-container>
          </div>
        </div>
      </div>
    </div>

    <!-- 末尾动画：联系我们 -->
    <div class="contact-us">
      <div class="contact-us-left">
        <div class="contact-title">联系我们</div>
        <!-- <p class="contact-description">
          前端存储库<a
            class="contact-github"
            href="https://github.com/0Shino0/share_study_ui"
            >https://github.com/0Shino0/share_study_ui</a
          >
        </p>
        <p class="contact-description">
          后端以及区块存储库<a
            class="contact-github"
            href="https://github.com/AntonyCheng/share_study"
            >https://github.com/AntonyCheng/share_study</a
          >
        </p>
        <p class="contact-description">欢迎Github提交Issues、PR</p> -->
        <p class="contact-description">邮箱：ShareStudy1952@163.com</p>
        <p class="contact-description">
          我们的作品还有很多不足的地方 <br />
          欢迎大家说出自己的建议和想法
        </p>
        <p class="contact-description">所属高校：哈尔滨商业大学</p>
      </div>
      <div class="contact-us-right">
        <Vue3Lottie
          animationLink="https://assets4.lottiefiles.com/packages/lf20_kdx6cani.json"
          :autoPlay="lottieOptions.autoplay"
          :loop="lottieOptions.loop"
          class="contact-us-lottie"
          style="width: 500px; height: 400px"
        >
        </Vue3Lottie>
      </div>
    </div>
  </div>
  <!-- Illustration by <a href="https://icons8.com/illustrations/author/zD2oqC8lLBBA">Icons 8</a> from <a href="https://icons8.com/illustrations">Ouch!</a>
  <a href="https://www.flaticon.com/free-icons/teacher" title="teacher icons">Teacher icons created by Freepik - Flaticon</a>
   -->
</template>

<style lang="scss" scoped>
.introduce-container {
  width: 100%;
  height: 1800px;
  // border: 1px solid #409eff;
  background-color: #fff;
}

.introduce-text-container {
  width: 1200px;
  padding: 232px 0 120px;
  margin: 0 auto;
  position: relative;

  .title {
    // background-color: #409eff;
    font-size: 56px;
    font-weight: bold;
    text-align: center;
    font-family: Apple System, SF Pro SC, SF Pro Display, Helvetica Neue, Arial,
      PingFang SC, Hiragino Sans GB, STHeiti, Microsoft YaHei,
      Microsoft JhengHei, Source Han Sans SC, Noto Sans CJK SC,
      Source Han Sans CN, sans-serif;
    margin-bottom: 23px;
  }

  // 介绍
  .intro {
    text-align: center;
    font-size: 20px;
    font-weight: 400;
    color: #6c7d8f;
    margin-bottom: 63px;
  }

  // 登录按钮
  .intro-tologin {
    cursor: pointer;
    margin: 0 auto;
    margin-top: 136px;
    margin-bottom: 68px;
    // font-size: 16px;
    font-size: 24px;
  }
  button {
    --primary-color: #645bff;
    --secondary-color: #fff;
    --hover-color: #111;
    --arrow-width: 10px;
    --arrow-stroke: 2px;
    box-sizing: border-box;
    border: 0;
    border-radius: 20px;
    color: var(--secondary-color);
    padding: 1em 1.8em;
    background: var(--primary-color);
    display: flex;
    transition: 0.2s background;
    align-items: center;
    gap: 0.6em;
    font-weight: bold;
  }

  button .arrow-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  button .arrow {
    margin-top: 1px;
    width: var(--arrow-width);
    background: var(--primary-color);
    height: var(--arrow-stroke);
    position: relative;
    transition: 0.2s;
  }

  button .arrow::before {
    content: "";
    box-sizing: border-box;
    position: absolute;
    border: solid var(--secondary-color);
    border-width: 0 var(--arrow-stroke) var(--arrow-stroke) 0;
    display: inline-block;
    top: -3px;
    right: 3px;
    transition: 0.2s;
    padding: 3px;
    transform: rotate(-45deg);
  }

  button:hover {
    background-color: var(--hover-color);
  }

  button:hover .arrow {
    background: var(--secondary-color);
  }

  button:hover .arrow:before {
    right: 0;
  }

  // 图片
  .intro-bg-img01 {
    position: absolute;
    top: 60px;

    img {
      width: 200px;
      transform: rotate(-0.1turn);
    }
  }

  .intro-bg-img02 {
    position: absolute;
    top: 40px;
    right: 0;
    img {
      width: 200px;
      transform: rotate(0.1turn);
    }
  }

  .intro-bg-img03 {
    position: absolute;
    top: 450px;
    right: 0;
    img {
      width: 200px;
      transform: rotate(0.4turn);
    }
  }

  .intro-bg-img04 {
    position: absolute;
    top: 450px;
    left: 0;
    img {
      width: 200px;
      transform: rotate(0.6turn);
    }
  }

  // 图片动画
  .intro-bg-img01,
  .intro-bg-img02,
  .intro-bg-img03,
  .intro-bg-img04 {
    animation: envFloating 1s ease-in infinite alternate;
  }

  .intro-bg-img04 {
    position: absolute;
    top: 450px;
    left: 0;
    width: 200px;
    transform: rotate(0.6turn);
  }

  // 上下跳动动画
  @keyframes envFloating {
    0% {
      transform: translate(-2px, -5px);
    }

    100% {
      transform: translate(0, 5px);
    }
  }

  @keyframes envDropping {
    0% {
      background-position: 100px 11px, 115px 35px, 105px 60px;
      opacity: 1;
    }

    50% {
      background-position: 0px 11px, 20px 35px, 5px 60px;
    }

    60% {
      background-position: -30px 11px, 0px 35px, -10px 60px;
    }

    75%,
    100% {
      background-position: -30px 11px, -30px 35px, -30px 60px;
      opacity: 0;
    }
  }
}

// 二级导航
.intro-menu {
  display: flex;
  justify-content: center;
  background-color: #fbfbfd;

  .intro-menu-item {
    padding: 15px 15px;
    font-size: 24px;
    color: #6c7d8f;
    cursor: pointer;
  }

  .active {
    color: #000;
    font-weight: bold;
  }
}

// 资源共享
.intro-light-sharing {
  display: flex;
  justify-content: center;
  background-color: #fbfbfd;
  padding-bottom: 112px;

  // lottiefile动画
  .light-sharing-left {
    .light-sharing-lottie {
      background-color: #fbfbfd;
    }
  }

  .sharing-container {
    width: 400px;
    display: flex;
    justify-content: center;
    flex-direction: column;
  }

  .sharing-title {
    font-size: 44px;
    font-weight: 700;
    color: #212930;
    text-align: left;
    white-space: nowrap;
  }

  .sharing-description {
    font-size: 16px;
    font-weight: 400;
    color: #6c7d8f;
    line-height: 24px;
    word-break: break-all;
    text-align: left;
    padding-top: 36px;
    margin-bottom: 56px;
  }
}

// 加入的高校
.intro-college-join {
  background-color: #fff;
  padding-bottom: 51px;

  .college-intro-container {
    display: flex;
    justify-content: center;
  }

  .college-intro {
    width: 400px;
    margin-right: 25px;
  }

  .college-title {
    margin-top: 224px;
    font-size: 44px;
    font-weight: 700;
    color: #212930;
    text-align: left;
    white-space: nowrap;
  }

  .college-description {
    font-size: 16px;
    font-weight: 400;
    color: #6c7d8f;
    line-height: 24px;
    word-break: break-all;
    text-align: left;
    padding-top: 36px;
    margin-bottom: 56px;
  }

  ul,
  li {
    list-style: none;
    padding: 0px;
    margin: 0px;
  }

  .college-list {
    // width: 100%;
    margin-top: 240px;
    text-align: center;

    .intro-college {
      width: 500px;
      height: 300px;
      display: flex;
      flex-wrap: wrap;
      flex-direction: column;
    }

    .intro-college-item {
      // padding: 15px 8px;
      padding: 5px;
      color: #6c7d8f;
    }
  }
}

// 架构图
.intro-framework-stack {
  background-color: #fbfbfd;
  padding-bottom: 51px;

  .framework-intro-container {
    display: flex;
    justify-content: center;
    // align-items: center;
  }

  .framework-intro {
    width: 400px;
    margin-right: 25px;
  }

  .framework-title {
    margin-top: 224px;
    font-size: 44px;
    font-weight: 700;
    color: #212930;
    text-align: left;
    white-space: nowrap;
  }

  .framework-description {
    font-size: 16px;
    font-weight: 400;
    color: #6c7d8f;
    line-height: 24px;
    word-break: break-all;
    text-align: left;
    padding-top: 36px;
    margin-bottom: 56px;
  }

  .framework-img {
    margin-top: 200px;
    text-align: center;
    img {
      width: 554px;
      border: 2px solid #fff;
      border-radius: 6px;
    }
  }
}

// 技术栈介绍
.intro-skill-stack {
  background-color: #fff;
  padding-bottom: 51px;

  .skill-container {
    display: flex;
    justify-content: center;
    // 左侧按钮
    .skill-left-icon {
      padding-top: 200px;
    }

    .skill-left-icon {
      display: flex;
      flex-direction: column;
      gap: 0.5em;
    }

    .up {
      display: flex;
      flex-direction: row;
      gap: 0.5em;
    }

    .down {
      display: flex;
      flex-direction: row;
      gap: 0.5em;
    }

    .card1 {
      width: 90px;
      height: 90px;
      outline: none;
      border: none;
      background: white;
      border-radius: 90px 5px 5px 5px;
      box-shadow: rgba(50, 50, 93, 0.25) 0px 2px 5px -1px,
        rgba(0, 0, 0, 0.3) 0px 1px 3px -1px;
      transition: 0.2s ease-in-out;
    }

    .vue {
      margin-top: 2em;
      margin-left: 1em;
      // fill: #cc39a4;
      // color: #41b883;
    }

    .card2 {
      width: 90px;
      height: 90px;
      outline: none;
      border: none;
      background: white;
      border-radius: 5px 90px 5px 5px;
      box-shadow: rgba(50, 50, 93, 0.25) 0px 2px 5px -1px,
        rgba(0, 0, 0, 0.3) 0px 1px 3px -1px;
      transition: 0.2s ease-in-out;
    }

    .java {
      margin-top: 1.5em;
      margin-left: -0.9em;
      // fill: #03a9f4;
      color: #03a9f4;
    }

    .card3 {
      width: 188px;
      height: 90px;
      outline: none;
      border: none;
      background: white;
      border-radius: 5px 5px 90px 90px;
      box-shadow: rgba(50, 50, 93, 0.25) 0px 2px 5px -1px,
        rgba(0, 0, 0, 0.3) 0px 1px 3px -1px;
      transition: 0.2s ease-in-out;
    }

    .hyperledger {
      margin-top: -0.6em;
      margin-left: 0;
    }

    .card1:hover {
      cursor: pointer;
      scale: 1.1;
      background-color: #41b883;
    }

    .card1:hover .vue {
      // fill: white;
      color: white;
    }

    .card2:hover {
      cursor: pointer;
      scale: 1.1;
      background-color: #03a9f4;
    }

    .card2:hover .java {
      // fill: white;
      color: white;
    }

    .card3:hover {
      cursor: pointer;
      scale: 1.1;
      background-color: black;
    }

    .card3:hover .hyperledger {
      // fill: white;
      color: white;
    }

    // .card4:hover {
    //   cursor: pointer;
    //   scale: 1.1;
    //   background-color: #8c9eff;
    // }

    // .card4:hover .discord {
    //   // fill: white;
    //   color: white;
    // }

    // 右侧描述
    .skill-intro {
      width: 500px;
      margin-left: 154px;
    }

    .skill-intro-item {
      width: 100%;
      position: relative;
      // right: 400px;
    }

    .skill-title {
      // margin-top: 200px;
      width: 100%;
      margin-top: 110px;
      font-size: 44px;
      font-weight: 700;
      color: #212930;
      // text-align: right;
      text-align: center;
      white-space: nowrap;
    }

    .skill-item-title {
      // margin-top: 200px;
      // margin-top: 110px;
      font-size: 24px;
      font-weight: 700;
      color: #212930;
      // text-align: right;
      text-align: center;
      white-space: nowrap;
    }

    .skill-description {
      font-size: 16px;
      font-weight: 400;
      color: #6c7d8f;
      line-height: 24px;
      word-break: break-all;
      // text-align: right;
      text-align: left;
      // padding-top: 36px;
      padding-top: 10px;
      // margin-bottom: 56px;
    }
  }
}

.swiper-container {
  width: 500px;
  height: 350px;
  margin-top: 44px;
}

.swiper-slide {
  // text-align: center;
  // line-height: 400px;
  color: #b0b0b0;
}

// .swiper-slide1 {
//   background: red;
// }

// .swiper-slide2 {
//   background: blue;
// }

// .swiper-slide3 {
//   background: yellow;
// }

// 联系我们
.contact-us {
  display: flex;
  justify-content: center;
  background-color: #fafafa;
  padding-top: 128px;
  padding-bottom: 128px;

  .contact-us-left {
    margin-top: 64px;
  }
  .contact-title {
    font-size: 44px;
    line-height: 64px;
    color: #000;
  }

  .contact-github {
    color: #409eff;
  }
  .contact-description {
    line-height: 30px;
    color: #6c7d8f;
  }

  // 右侧
  .contact-us-right {
    .contact-us-lottie {
      background-color: #fafafa;
    }
  }
}
</style>
