<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
// import echarts from "echarts";
import resize from './mixins/resize'

import { getTeacherScoreTop } from '@/api/chart.js'
const animationDuration = 6000

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
      teacherScoreList: [], // echart数据格式
      teacherNameList: [] // 教师姓名
    }
  },
  mounted() {
    this.getTeacherScoreTopInfo()
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    // 获取数据
    async getTeacherScoreTopInfo() {
      const { data } = await getTeacherScoreTop()

      this.teacherScoreList = data.map((c) => {
        return [c.name, c.score]
      })
      this.teacherNameList = data.map((c) => {
        return c.name
      })
      console.log(this.teacherScoreList)
      // 初始化
      this.initChart()
    },
    initChart() {
      this.chart = this.$echarts.init(this.$el, 'macarons')

      this.chart.setOption({
        title: {
          text: '教师贡献TOP5',
          right: '10',
          top: '-2'
        },
        dataset: [
          {
            dimensions: ['name', '贡献度'],
            source: this.teacherScoreList
          },
          {
            transform: {
              type: 'sort',
              config: { dimension: '贡献度', order: 'desc' }
            }
          }
        ],
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          top: 25,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          // data: this.teacherNameList,
          // axisTick: {
          //   alignWithLabel: true,
          // },
          axisLabel: {
            interval: 0
          }
        },

        yAxis: {
          // type: "category",
          type: 'value'
          // data: ["super", "admin", "user1", "user2", "user3", "user4"],
          // axisTick: {
          //   show: false,
          // },
        },

        series: {
          type: 'bar',
          // encode: { x: "name", y: "score" },
          datasetIndex: 1
        }
      })
    }
  }
}
</script>
