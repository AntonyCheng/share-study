<script setup lang="ts">
import Header from "@/layout/Header.vue";
import { useRoute } from "vue-router";
import $bus from "@/libs/eventBus";

const cancelFocus = () => {
  $bus.emit("cancelFocus", false);
};

const route = useRoute();
</script>

<template>
  <div @focus="cancelFocus" tabindex="0">
    <!-- <KeepAlive> -->
    <Header v-if="route.path != '/login' && route.path != '/register'"></Header>

    <!-- </KeepAlive> -->

    <router-view />
  </div>
</template>

<style lang="scss">
body,
html {
  font-size: 16px;
  // background-color: #E9ECEF;
  // background-color: #f9fbff;
  // overflow: hidden;
  background-color: #e9ecef;
}

#app {
  outline: none;
  // overflow-x: hidden;
  // 方案一
  height: 750px;
  // overflow: hidden;
  background-color: #e9ecef;

  // 方案二
  // min-height: 750px;
  // overflow: auto;
}

* {
  padding: 0;
  margin: 0;
}

.el-button {
  padding: 8px 15px;
}

a {
  outline: none;
  text-decoration: none;
}

/* 滚动条相关 */
/*定义滚动条高宽及背景
 高宽分别对应横竖滚动条的尺寸*/
::-webkit-scrollbar {
  width: 8px;
  height: 16px;
  background-color: #f5f5f5;
  // display: none;
}

/*定义滚动条轨道
 内阴影+圆角*/
::-webkit-scrollbar-track {
  -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
  border-radius: 10px;
  background-color: #f5f5f5;
}

/*定义滑块
 内阴影+圆角*/
::-webkit-scrollbar-thumb {
  border-radius: 10px;
  -webkit-box-shadow: inset 0 0 6px #40a0ff36;
  background-color: #409eff;
}
</style>
