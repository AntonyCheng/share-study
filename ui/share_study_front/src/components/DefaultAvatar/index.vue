<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import { defineComponent, onMounted, computed } from "vue";

export default defineComponent({
  props: {
    avatarName: {
      // 标识emit
      require: true,
      type: String,
    },
    width: {
      // 标识emit
      require: true,
      type: String,
    },
    height: {
      // 标识emit
      require: true,
      type: String,
    },
  },
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例
    // console.log(props);

    const avatarSize = computed(
      () => `width:${props.width};height:${props.height}`
    );
    const fontSize = computed(() => {
      if (props.width) {
        return `font-size:${parseInt(props.width.split("px")[0]) / 2}px;`;
      } else {
        return "font-size:20px;";
      }
    });

    onMounted(() => {});

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
      avatarSize,
      fontSize,
    };
  },

  mounted() {},
});
</script>

<template>
  <div class="default-avatar" :style="avatarSize">
    <span class="avater-name" :style="fontSize">{{ avatarName }}</span>
  </div>
</template>

<style lang="scss">
.default-avatar {
  // min-width: 40px;
  // height: 40px;
  background: #aaaaaa;
  border-radius: 50%;
  // line-height: 40px;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;

  // 默认头像
  .avater-name {
    font-family: "微软雅黑", arial;
    // font-size: 20px;
    font-weight: 700;
  }
}
</style>
