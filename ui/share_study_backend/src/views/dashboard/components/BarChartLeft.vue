<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
// import echarts from "echarts";
import resize from './mixins/resize'
import { getResourceCollectTop } from '@/api/chart.js'

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
      resourceCollectList: []
    }
  },
  mounted() {
    this.getResourceCollectTopInfo()
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    async getResourceCollectTopInfo() {
      const { data } = await getResourceCollectTop()
      this.resourceCollectList = data.map((c, i) => {
        for (let j = 0; j < i; j++) {
          // echarts 对相同数据 会合并，添加空字符标记
          c.belongName = ' ' + c.belongName
        }
        return [c.belongName, c.score, c.name]
      })
      console.log(this.resourceCollectList)
      this.initChart()
    },
    initChart() {
      this.chart = this.$echarts.init(this.$el, 'macarons')

      this.chart.setOption(
        {
          title: {
            text: '资料收藏TOP5',
            right: '10',
            top: '-2'
          },
          dataset: [
            {
              dimensions: ['name', 'score', 'resourceName'],
              source: this.resourceCollectList
            },
            {
              transform: {
                type: 'sort',
                config: { dimension: 'score', order: 'asc' }
              }
            }
          ],
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              // 坐标轴指示器，坐标轴触发有效
              type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
            },

            formatter: function(params) {
              console.log(params)
              var dotHtml =
                '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:8px;height:8px;background-color:#F1E67F"></span>' // 定义第一个数据前的圆点颜色
              var dotHtml2 =
                '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:8px;height:8px;background-color:#2BA8F1"></span>' // 定义第二个数据前的圆点颜色

              var result =
                '作者:' +
                params[0].value[0] +
                '<br/>' +
                dotHtml +
                params[0].value[2] +
                '<br/>' +
                dotHtml2 +
                '收藏数:' +
                params[0].value[1]
              return result
            }
          },
          grid: {
            top: 25,
            left: '2%',
            right: '2%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: [
            {
              // type: "category",
              // data: ["super", "admin", "user1", "user2", "user3", "user4"],
              type: 'value',
              axisTick: {
                alignWithLabel: true
              }
            }
          ],
          yAxis: [
            {
              type: 'category',
              // data: ["super", "admin", "user1", "user2", "user3", "user4"],
              axisTick: {
                show: false
              }
            }
          ],
          series: {
            type: 'bar',
            encode: { x: 'score', y: 'name' },
            datasetIndex: 1
          }
        },
        { notMerge: true }
      )
      // notMerge:true 不合并相同数据局
    }
  }
}
</script>
