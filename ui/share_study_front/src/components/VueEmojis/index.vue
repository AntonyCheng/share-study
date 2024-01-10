<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import { defineComponent, onMounted, ref, onBeforeUnmount } from "vue";
import $bus from "@/libs/eventBus";

export interface EmojisMember {
  text: string;
}

export default defineComponent({
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例
    const emojis = ref<EmojisMember[]>([]);

    // 生命周期钩子
    onMounted(() => {
      const emojiList: string[] = [
        "😀",
        "😄",
        "😅",
        "🤣",
        "😂",
        "😉",
        "😊",
        "😍",
        "😘",
        "😜",
        "😝",
        "😏",
        "😒",
        "🙄",
        "😔",
        "😴",
        "😷",
        "🤮",
        "🥵",
        "😎",
        "😮",
        "😰",
        "😭",
        "😱",
        "😩",
        "😡",
        "💀",
        "👽",
        "🤓",
        "🥳",
        "😺",
        "😹",
        "😻",
        "🤚",
        "💩",
        "👍",
        "👎",
        "👏",
        "🙏",
        "💪",
      ];
      emojis.value = emojiList.map((emoji: string) => {
        return { text: emoji };
      });
      // console.log(emojis.value);
    });

    // 方法 methods
    const handleEmoji = (item: any) => {
      // console.log(item);
      $bus.emit("addEmoji", item);
    };

    // 计算方法 computed

    // 监听 watch

    return {
      // 需要给 `<template, />` 用的数据或函数，在这里 `return` 出去
      emojis,
      handleEmoji,
    };
  },
});
</script>

<template>
  <ul class="emoji-container">
    <li
      class="emojiList"
      v-for="(item, index) in emojis"
      :key="index"
      @click="handleEmoji(item)"
    >
      {{ item.text }}
    </li>
  </ul>
</template>

<style lang="scss">
.emoji-container {
  position: absolute;
  width: 368px;
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 20px 20px 60px #d9d9d9, -20px -20px 60px #ffffff;

  .emojiList {
    width: 22px;
    list-style: none;
    margin: 8px 12px;
    cursor: pointer;
  }
}
</style>
