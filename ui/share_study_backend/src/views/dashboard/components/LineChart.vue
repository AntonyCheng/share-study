<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
// require("echarts/theme/macarons"); // echarts theme
import resize from './mixins/resize'
// import {} from "@/api/chart.js";

// const dateList = data.map(function (item) {
//   return item[0];
// });
// const valueList = data.map(function (item) {
//   return item[1];
// });

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
      default: '350px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Object,
      required: false // 真实数据 true |  mock数据  false
    }
  },
  data() {
    return {
      chart: null,
      dateList: [], // 日期数组
      valueList: [
        116, 129, 135, 86, 73, 85, 73, 68, 92, 130, 245, 139, 115, 111, 10, 206,
        137, 20, 85, 94, 71, 106, 84, 93, 85, 73, 83, 125, 107, 82, 44, 72, 106,
        107, 66, 91, 92, 113, 107, 20, 111, 64, 69, 88, 77, 83, 111, 57, 55, 60
      ], // 数值数组
      dateSize: 48, // 获取 dateSize 天
      valuemin: 0,
      valueMax: 100
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val)
      }
    }
  },
  mounted() {
    this.dateList = this.getDateList(this.dateSize)
    // this.valueList = this.randArray2(
    //   this.dateSize,
    //   this.valuemin,
    //   this.valueMax
    // );
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
    initChart() {
      this.chart = this.$echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions({ expectedData, actualData } = {}) {
      this.chart.setOption({
        title: {
          text: this.dateSize + '天内用户访问数据',
          left: 'center'
        },
        visualMap: [
          {
            show: false,
            type: 'continuous',
            seriesIndex: 0,
            min: 0,
            max: 100
          },
          {
            show: false,
            type: 'continuous',
            seriesIndex: 1,
            dimension: 0,
            min: 0,
            max: this.dateList.length - 1
          }
        ],
        xAxis: {
          data: this.dateList,
          boundaryGap: false,
          axisTick: {
            show: true
          }
        },
        grid: {
          left: 10,
          right: 10,
          bottom: 20,
          top: 30,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          padding: [5, 10],
          formatter: function(params) {
            console.log(params)
            var dotHtml =
              '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:8px;height:8px;background-color:#F1E67F"></span>' // 定义第一个数据前的圆点颜色
            var dotHtml2 =
              '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:8px;height:8px;background-color:#2BA8F1"></span>' // 定义第二个数据前的圆点颜色

            var result =
              '时间：' +
              params[0].axisValue +
              '<br/>' +
              dotHtml +
              '访问量：' +
              params[0].data
            return result
          }
        },
        yAxis: {
          axisTick: {
            show: false
          }
        },
        legend: {
          data: ['expected', 'actual']
        },
        series: [
          {
            type: 'line',
            showSymbol: false,
            data: this.valueList,
            smooth: false,
            areaStyle: {}
          }
        ]
      })
    },
    // 获取日期函数
    getDateList(size) {
      var lastMonth = []
      for (var i = 0; i < size; i++) {
        lastMonth.unshift(
          new Date(
            new Date().setDate(new Date().getDate() - i)
          ).toLocaleDateString()
        )
      }
      return lastMonth
    },
    randArray2(len, min, max) {
      return Array(len)
        .fill(1)
        .map((v) => Math.floor(Math.random() * (max - min)) + min)
    }
  }
}
</script>
