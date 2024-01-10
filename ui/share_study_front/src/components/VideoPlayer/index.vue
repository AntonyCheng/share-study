<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import { defineComponent, onMounted, ref } from "vue";

// vue中使用 video.js
// video v8.2.1 中缺少VideoPlayer类型 降级为 7.21.4 解决 // 2023-3-26 10:25:37
import videojs, {
  type VideoJsPlayer,
  type VideoJsPlayerOptions,
} from "video.js";
import "video.js/dist/video-js.css";

export default defineComponent({
  props: ["volume", "src"],
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例
    // console.log(props);
    // video标签
    const videoRef = ref<HTMLElement | null>(null);
    // video 对象
    let videoPlayer: videojs.Player | null = null;

    const volumeVideo = ref<number>(props.volume);
    // 初始化videojs
    const initVideo = () => {
      // https://gitcode.gitcode.host/docs-cn/video.js-docs-cn/docs/guides/options.html
      const options: VideoJsPlayerOptions = {
        language: "zh-CN", // 设置语言
        controls: true, // 是否显示控制条
        preload: "auto", // 预加载
        autoplay: true, // 是否自动播放
        fluid: false, // 自适应宽高
        src: props.src, // 要嵌入的视频源的源 URL
      };
      if (videoRef.value) {
        // 创建 video 实例
        videoPlayer = videojs(videoRef.value, options, onPlayerReady);
      }
    };
    // video初始化完成的回调函数
    const onPlayerReady = () => {};

    // 生命周期钩子
    onMounted(() => {
      initVideo();
    });

    // 方法 methods
    // 封装播放器方法
    const play = () => {
      if (videoPlayer === null) return;
      videoPlayer.src({ src: props.src });
      videoPlayer.load();
      videoPlayer.play();
    };
    const stop = () => {
      if (videoPlayer === null) return;
      videoPlayer.pause();
    };
    const reload = () => {
      if (videoPlayer === null) return;
      stop();
      videoPlayer.src("");
      videoPlayer.load();
      play();
    };
    const forward = () => {
      if (videoPlayer === null) return;
      const currentTime = videoPlayer.currentTime();
      videoPlayer.currentTime(currentTime + 5);
    };
    const back = () => {
      if (videoPlayer === null) return;
      const currentTime = videoPlayer.currentTime();
      videoPlayer.currentTime(currentTime - 5);
    };
    const volumeUp = () => {
      if (videoPlayer === null) return;
      videoPlayer.volume(volumeVideo.value + 0.1);
    };
    const volumeDown = () => {
      if (videoPlayer === null) return;
      videoPlayer.volume(volumeVideo.value - 0.1);
    };

    // 计算方法 computed

    // 监听 watch

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
    };
  },
});
</script>

<template>
  <video
    ref="videoRef"
    controls
    class="video-js vjs-default-skin vjs-big-play-centered"
    width="600"
    height="400"
  >
    <source :src="src" />
  </video>
</template>

<style>
.video-js .vjs-time-control {
  display: block !important;
}
.video-js .vjs-remaining-time {
  display: none !important;
}

.video-js .vjs-big-play-button {
  font-size: 2.5em !important;
  line-height: 2.3em !important;
  height: 2.5em !important;
  width: 2.5em !important;
  -webkit-border-radius: 2.5em !important;
  -moz-border-radius: 2.5em !important;
  border-radius: 2.5em !important;
  background-color: #73859f;
  background-color: rgba(115, 133, 159, 0.5) !important;
  border-width: 0.15em !important;
  margin-top: -1.25em !important;
  margin-left: -1.25em !important;
}
.vjs-big-play-button .vjs-icon-placeholder {
  font-size: 1.63em !important;
}

.vjs-paused .vjs-big-play-button,
.vjs-paused.vjs-has-started .vjs-big-play-button {
  display: block !important;
}
</style>
