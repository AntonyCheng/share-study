<template>
  <div class="default-avater" :style="avaterSize">
    <span class="avater-name" :style="fontSize">{{ avaterName }}</span>
  </div>
</template>

<script>
export default {
  name: 'DefaultAvater',
  props: {
    avaterName: {
      // 标识emit
      require: true,
      type: String
    },
    width: {
      // 标识emit
      require: true,
      type: String
    },
    height: {
      // 标识emit
      require: true,
      type: String
    }
  },
  data() {
    return {}
  },
  computed: {
    avaterSize() {
      return `width:${this.width};height:${this.height}`
    },
    fontSize() {
      // console.log(this.width.split("px")[0]);
      const styleFont = `fontSize:${this.width.split('px')[0] / 2}px;`
      return styleFont
    }
  }
}
</script>

<style lang="scss">
.default-avater {
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
