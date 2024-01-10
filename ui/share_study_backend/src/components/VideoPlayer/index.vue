<template>
  <video
    ref="video"
    controls
    class="video-js vjs-default-skin vjs-big-play-centered"
    width="600"
    height="400"
  >
    <source :src="src">
  </video>
</template>

<script>
import videojs from 'video.js'
import 'video.js/dist/video-js.css'

export default {
  props: ['volume', 'src'],
  data() {
    return {
      player: null,
      volumeVideo: this.volume
    }
  },
  mounted() {
    const _this = this
    const options = {
      language: 'zh-CN', // 设置语言
      controls: true, // 是否显示控制条
      preload: 'auto', // 预加载
      autoplay: false, // 是否自动播放
      fluid: false, // 自适应宽高
      src: this.src // 要嵌入的视频源的源 URL
    }

    this.player = this.$video(this.$refs.video, options, function() {
      this.on('volumechange', () => {
        // 存储音量
        _this.volumeVideo = this.volume()
        window.localStorage.volume = this.volume()
      })
      this.on('play', () => {
        this.volume(this.volumeVideo)
      })
    })
    // 监听父组件 关闭dialog
    this.$bus.$on(`dialogCancel`, () => {
      // console.log('dialogCancel')
      this.stop()
    })
  },
  beforeDestroy() {
    if (this.player) {
      this.player.dispose()
    }
  },
  methods: {
    // 封装播放器方法
    play() {
      this.player.src({ src: this.src })
      this.player.load(this.src)
      this.player.play(this.volumeVideo)
    },
    stop() {
      this.player.pause()
    },
    reload() {
      this.stop()
      this.player.load({})
      this.play()
    },
    forward() {
      const currentTime = this.player.currentTime()
      this.player.currentTime(currentTime + 5)
    },
    back() {
      const currentTime = this.player.currentTime()
      this.player.currentTime(currentTime - 5)
    },
    volumeUp() {
      this.player.volume(this.volumeVideo + 0.1)
    },
    volumeDown() {
      this.player.volume(this.volumeVideo - 0.1)
    },
    toggleTv(obj) {
      this.player.src(obj.src)
      this.player.load(obj.load)
      this.player.play(this.volumeVideo)
    }
  }
}
</script>

<style>
.video-js .vjs-time-control {
  display: block !important;
  /* display: none !important; */
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
