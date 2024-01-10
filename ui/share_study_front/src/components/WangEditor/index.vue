<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件

import "@wangeditor/editor/dist/css/style.css"; // 引入 css

import {
  defineComponent,
  onBeforeUnmount,
  ref,
  shallowRef,
  onMounted,
  nextTick,
} from "vue";
import $bus from "@/libs/eventBus";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import { IDomEditor } from "@wangeditor/editor";

export interface WangEditorFormMember {
  html: any;
  postTitle: string;
}

export default defineComponent({
  props: {
    postId: {
      type: String,
      default: "",
    },
    title: {
      type: String,
      default: "",
    },
    info: {
      type: String,
      default: "",
    },
  },
  components: {
    Editor,
    Toolbar,
  },
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例

    // 编辑器实例，必须用 shallowRef
    const editorRef = shallowRef();

    // 内容 HTML
    // const html = ref("<p>hello</p>");
    // const postTitle = ref("");
    const wangEditorForm = ref<WangEditorFormMember>({
      html: "",
      postTitle: "",
    });

    // 生命周期钩子
    // 模拟 ajax 异步获取内容
    onMounted(() => {
      // setTimeout(() => {
      //   wangEditorForm.html.value = "<p>模拟 Ajax 异步设置内容</p>";
      // }, 1500);
      nextTick(() => {
        wangEditorForm.value.postTitle = props.title;
        wangEditorForm.value.html = props.info;
      });
    });

    const toolbarConfig = {
      excludeKeys: [
        "insertVideo",
        "uploadVideo",
        "group-video",

        // "uploadImage",
        // "insertImage",
        // "deleteImage",
        // "group-image",

        "fullScreen",
      ],
    };
    const editorConfig = { placeholder: "请输入内容..." };

    // 组件销毁时，也及时销毁编辑器
    onBeforeUnmount(() => {
      const editor: IDomEditor = editorRef.value;
      if (editor == null) return;
      editor.destroy();
    });

    // 编辑器创建完毕时的回调函数。
    const handleCreated = (editor: IDomEditor) => {
      editorRef.value = editor; // 记录 editor 实例，重要！
    };
    // 方法 methods

    // 提交
    const handleSubmit = () => {
      // console.log("html=>", this.html);
      $bus.emit("tranHtmlFromEditor", wangEditorForm.value);
    };
    // 修改
    const updateSubmit = () => {
      $bus.emit("updateHtmlFromEditor", wangEditorForm.value);
    };
    // 自定义粘贴。可阻止编辑器的默认粘贴，实现自己的粘贴逻辑。
    const customPaste = (editor: IDomEditor, event: any, callback: any) => {
      // console.log("ClipboardEvent 粘贴事件对象", event);
      const html = event.clipboardData.getData("text/html"); // 获取粘贴的 html
      const text = event.clipboardData.getData("text/plain"); // 获取粘贴的纯文本
      const rtf = event.clipboardData.getData("text/rtf"); // 获取 rtf 数据（如从 word wsp 复制粘贴）
      // console.log('html=>',html);
      // console.log("text=>", text);
      // console.log('rtf=>',rtf);

      // 自定义插入内容
      editor.insertText(text);

      // 返回 false ，阻止默认粘贴行为
      event.preventDefault();
      callback(false); // 返回值（注意，vue 事件的返回值，不能用 return）

      // 返回 true ，继续默认的粘贴行为
      // callback(true)
    };

    // 计算方法 computed

    // 监听 watch

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
      editorRef,
      // html,
      // postTitle,
      wangEditorForm,
      mode: "default", // 或 'simple'
      toolbarConfig,
      editorConfig,

      // 方法
      handleCreated,
      handleSubmit,
      updateSubmit,
      customPaste,
    };
  },
});
</script>

<template>
  <div class="wang-editor-container" style="border: 1px solid #ccc">
    <div class="editor-post">
      <input
        class="post-title"
        type="text"
        placeholder="请输入标题"
        v-model="wangEditorForm.postTitle"
      />
      <el-button type="primary" @click="handleSubmit" v-if="postId === '1'"
        >发布</el-button
      >
      <el-button
        class="wangeditor-update-post-btn"
        type="primary"
        @click="updateSubmit"
        v-else
        >修改</el-button
      >
    </div>
    <Toolbar
      style="border-bottom: 1px solid #ccc"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
      :mode="mode"
    />
    <Editor
      style="height: 500px; overflow-y: hidden"
      v-model="wangEditorForm.html"
      :defaultConfig="editorConfig"
      :mode="mode"
      @onCreated="handleCreated"
      @customPaste="customPaste"
    />
  </div>
</template>

<style lang="scss">
.wang-editor-container {
  z-index: 10;
  // padding-top: 90px;

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
    .wangeditor-update-post-btn {
      flex: 20%;
      height: 56px;
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
