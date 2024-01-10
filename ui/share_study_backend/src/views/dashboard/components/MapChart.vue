<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
// 请确保在引入百度地图扩展之前已经引入百度地图 JS API 脚本并成功加载
// https://api.map.baidu.com/api?v=3.0&ak=你申请的AK
import 'echarts/extension/bmap/bmap'

import { getCollegeMap } from '@/api/chart.js'

/* 数据规范
        地图数据
        data = [
          ...
          {
            name: '高校名',
            value: '高校人数'
          }
          ...
        ]

        // 柱形图  教师贡献榜
        data =  [
          ...
          ["用户姓名", "贡献度", "时间"]
          ...
        ],

        // 饼图 高校贡献
        data = [
            ...
          { value: 贡献度/人数, name: "高校名" },
            ...
        ]

        // 折现图 用户访问数据
        const data = [
            ...
            ["时间", 访问量],
            ...
        ]
      */

const convertData = (data, geo) => {
  var res = []
  for (var i = 0; i < data.length; i++) {
    var geoCoord = geo[data[i].name]
    if (geoCoord) {
      res.push({
        name: data[i].name,
        value: geoCoord.concat(data[i].value)
      })
    }
  }
  // console.log("res=>", res);
  return res
}

export default {
  name: 'MapChart',
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
      default: '400px'
    }
  },
  data() {
    return {
      chart: null,
      title: {
        text: '用户分布',
        subtext: '本站用户在全国分布情况',
        // sublink: "http://www.pm25.in",
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      bmap: {
        center: [126.57088, 45.826205],
        zoom: 12,
        roam: true,
        mapStyle: {
          styleJson: [
            {
              featureType: 'water',
              elementType: 'all',
              stylers: {
                color: '#d1d1d1'
              }
            },
            {
              featureType: 'land',
              elementType: 'all',
              stylers: {
                color: '#f3f3f3'
              }
            },
            {
              featureType: 'railway',
              elementType: 'all',
              stylers: {
                visibility: 'off'
              }
            },
            {
              featureType: 'highway',
              elementType: 'all',
              stylers: {
                color: '#fdfdfd'
              }
            },
            {
              featureType: 'highway',
              elementType: 'labels',
              stylers: {
                visibility: 'off'
              }
            },
            {
              featureType: 'arterial',
              elementType: 'geometry',
              stylers: {
                color: '#fefefe'
              }
            },
            {
              featureType: 'arterial',
              elementType: 'geometry.fill',
              stylers: {
                color: '#fefefe'
              }
            },
            {
              featureType: 'poi',
              elementType: 'all',
              stylers: {
                visibility: 'off'
              }
            },
            {
              featureType: 'green',
              elementType: 'all',
              stylers: {
                visibility: 'off'
              }
            },
            {
              featureType: 'subway',
              elementType: 'all',
              stylers: {
                visibility: 'off'
              }
            },
            {
              featureType: 'manmade',
              elementType: 'all',
              stylers: {
                color: '#d1d1d1'
              }
            },
            {
              featureType: 'local',
              elementType: 'all',
              stylers: {
                color: '#d1d1d1'
              }
            },
            {
              featureType: 'arterial',
              elementType: 'labels',
              stylers: {
                visibility: 'off'
              }
            },
            {
              featureType: 'boundary',
              elementType: 'all',
              stylers: {
                color: '#fefefe'
              }
            },
            {
              featureType: 'building',
              elementType: 'all',
              stylers: {
                color: '#d1d1d1'
              }
            },
            {
              featureType: 'label',
              elementType: 'labels.text.fill',
              stylers: {
                color: '#999999'
              }
            }
          ]
        }
      },
      series: null,
      data: null,
      geoCoordMap: null,
      option: null,
      // 数据相关
      collegeMapList: [],
      mapPropData: []
    }
  },

  mounted() {
    this.getCollegeMapInfo()
    // console.log(this.$el);
    // this.productData();
    // this.initChart();
  },
  methods: {
    async getCollegeMapInfo() {
      const { data } = await getCollegeMap()
      this.collegeMapList = data
      this.productData()
    },

    // 加工数据
    productData() {
      // this.collegeMapList = Object.assign([], this.mapData);
      this.data = this.collegeMapList.map((c) => {
        // console.log(c);
        return { name: c.name, value: c.number }
      })
      // console.log("data=>", this.data);
      // this.geoCoordMap
      const splitData = this.collegeMapList.map((c) => {
        // console.log([c.name, c.location.split(",")]);
        return [c.name, c.location.split(',')]
      })
      // console.log("obj=>", Object.fromEntries(splitData));
      this.geoCoordMap = Object.fromEntries(splitData)

      // 初始化map图
      this.initChart()
    },
    initChart() {
      this.chart = this.$echarts.init(this.$el, 'macarons')

      this.series = [
        {
          name: '人数',
          type: 'scatter',
          coordinateSystem: 'bmap',
          data: convertData(this.data, this.geoCoordMap),
          symbolSize: function(val) {
            if (val[2] < 5) {
              console.log(val[2])
              return val[2] * 4
            } else if (val[2] < 10) {
              return val[2] * 3
            } else {
              return val[2]
            }
            // return val[2] / 10;
          },
          encode: {
            value: 2
          },
          label: {
            formatter: '{b}',
            position: 'right',
            show: false
          },
          emphasis: {
            label: {
              show: true
            }
          }
        },
        {
          name: '人数 Top 5',
          type: 'effectScatter',
          coordinateSystem: 'bmap',
          data: convertData(
            this.data
              .sort(function(a, b) {
                return b.value - a.value
              })
              .slice(0, 6),
            this.geoCoordMap
          ),
          symbolSize: function(val) {
            if (val[2] < 5) {
              // console.log(val[2]);
              return val[2] * 4
            } else if (val[2] < 10) {
              return val[2] * 3
            } else {
              return val[2]
            }
          },
          encode: {
            value: 2
          },
          showEffectOn: 'render',
          rippleEffect: {
            brushType: 'stroke'
          },
          label: {
            formatter: '{b}',
            position: 'right',
            show: true
          },
          itemStyle: {
            shadowBlur: 10,
            shadowColor: '#333'
          },
          emphasis: {
            scale: true
          },
          zlevel: 1
        }
      ]

      this.option = {
        title: this.title,
        tooltip: this.tooltip,
        bmap: this.bmap,
        series: this.series
      }

      this.chart.setOption(this.option)
    }
  }
}
</script>

<style lang="scss">
// 隐藏底部 百度logo相关消息
.anchorBL {
  display: none;
}
</style>
