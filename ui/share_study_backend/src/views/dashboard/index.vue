<template>
  <div class="dashboard-container">
    <!-- <div class="dashboard-text">可视化管理</div> -->

    <!-- map展示 -->
    <el-row
      class="map-charts"
      style="margin-bottom: 32px; border-radius: 10px; overflow: hidden"
    >
      <map-chart />
    </el-row>

    <!-- bar pie 图 -->
    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart-left />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart />
        </div>
      </el-col>
    </el-row>

    <!-- line图 -->
    <el-row
      class="line-charts"
      style="
        background: #fff;
        padding: 16px 16px 0;
        margin-top: 32px;
        border-radius: 10px;
        overflow: hidden;
      "
    >
      <line-chart />
    </el-row>
  </div>
</template>

<script>
import MapChart from './components/MapChart.vue'
import RaddarChart from './components/RaddarChart.vue'
import PieChart from './components/PieChart.vue'
import BarChart from './components/BarChart.vue'
import BarChartLeft from './components/BarChartLeft.vue'
import LineChart from './components/LineChart.vue'

import {
  getCollegeMap,
  getResourceCollectTop,
  getCollegeScoreTop,
  getTeacherScoreTop
} from '@/api/chart.js'

const lineChartData = {}
export default {
  name: 'Dashboard',
  components: {
    MapChart,
    RaddarChart,
    PieChart,
    BarChart,
    BarChartLeft,
    LineChart
  },
  data() {
    return {
      collegeMapList: []
    }
  },
  created() {},
  mounted() {},
  methods: {}
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .map-charts {
    border-radius: 16px;
    background: #f0f2f5;
    box-shadow: 7px 7px 0px #e6e8eb, -7px -7px 0px #fafcff;
  }

  .chart-wrapper {
    border-radius: 8px;
    background: #f0f2f5;
    box-shadow: 7px 7px 0px #e6e8eb, -7px -7px 0px #fafcff;
  }

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    border-radius: 16px;
    background: #f0f2f5;
    box-shadow: 7px 7px 0px #e6e8eb, -7px -7px 0px #fafcff;
    // margin-bottom: 32px;
  }
}

@media (max-width: 1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
