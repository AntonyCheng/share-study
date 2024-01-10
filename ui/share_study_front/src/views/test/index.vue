<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import { defineComponent } from "vue";
import WangEditor from "@/components/WangEditor/index.vue";

export default defineComponent({
  components: {
    WangEditor,
  },
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
    };
  },

  mounted() {},
});
</script>

<template>
  <div>
    <wang-editor></wang-editor>
  </div>
</template>

<style src="@wangeditor/editor/dist/css/style.css"></style>
<style lang="scss">
.wang-editor-container {
  z-index: 10;

  .editor-post {
    width: 100%;
    display: flex;
    flex: 1;

    .post-title {
      flex: 90%;
      height: 40px;
      padding: 8px 12px;
      outline: none;
      border: none;
      font-size: 20px;
      font-weight: bold;
    }
    .el-button {
      flex: 20%;
    }

    .w-e-text-container {
      text-align: start;

      .w-e-scroll {
        text-align: start;

        #w-e-textarea-1 {
          text-align: start;
          #w-e-element-0 {
            text-align: start;
          }
        }
      }
    }
  }
}
</style>
