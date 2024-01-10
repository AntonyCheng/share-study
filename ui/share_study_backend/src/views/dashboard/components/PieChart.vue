<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
// import echarts from 'echarts'
// require("echarts/theme/macarons"); // echarts theme
import resize from './mixins/resize'
import {
  getCollegeMap,
  getResourceCollectTop,
  getCollegeScoreTop,
  getTeacherScoreTop
} from '@/api/chart.js'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      chart: null,
      collegeScoreList: [],
      collegeNameList: []
    }
  },
  mounted() {
    this.getCollegeScoreTopInfo()
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    async getCollegeScoreTopInfo() {
      const { data } = await getCollegeScoreTop()
      this.collegeScoreList = data.map((c) => {
        return { value: c.score, name: c.name }
      })
      this.collegeNameList = data.map((c) => {
        return c.name
      })
      console.log(this.collegeScoreList)
      this.initChart()
    },
    initChart() {
      this.chart = this.$echarts.init(this.$el, 'macarons')

      this.chart.setOption({
        title: {
          text: '高校总贡献比',
          right: 10,
          top: -2
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: this.collegeNameList
        },
        series: [
          {
            name: '总贡献',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            data: this.collegeScoreList,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
