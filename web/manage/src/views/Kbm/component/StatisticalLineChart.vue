<template>
  <div class="statistical-line-chart" v-loading="loading">
    <div class="statistical-line-chart-title">
      <div>
        <span style="font-size: 18px">数据量增长情况</span>
        <i class="el-icon-caret-bottom"></i>
      </div>
      <div>单位：条</div>
    </div>
    <div class="statistical-line-chart-echart">
      <div class="echartClass" ref="echart"></div>
    </div>
  </div>
</template>
<script>
import echarts from "echarts";
import { knowledgeCountTrend } from "@/api/operation";
export default {
  name: "StatisticalLineChart",
  props: {
  	xAxisData: {
  		type: Array,
  		default: () => {
  		  return []
  		}
  	},
  	yAxisData: {
  		type: Array,
  		default: () => {
  		  return []
  		}
  	},
   
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
	  startTime:'',
	  endTime:'',
      loading: false,
      eChartOption: {
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: [],
          axisTick: {
            show: false,
          },
          axisLine: {
            show: false,
          },
          axisLabel: {
            color: "#86909C",
          },
        },
        yAxis: {
          type: "value",
          axisTick: {
            show: false,
          },
          axisLine: {
            show: false,
          },
          axisLabel: {
            color: "#86909C",
          },
        },
        grid: {
          left: "56px",
          right: "36px",
          top: "30px",
          bottom: "50px",
        },
        dataZoom: [
          {
            type: "slider",
            xAxisIndex: 0,
            filterMode: "none",
            height: "20",
            bottom: "0",
            borderColor: "#fff",
            backgroundColor: "rgba(247, 248, 250, 1)",
            fillerColor: "rgba(160, 189, 252, 0.5)",
            dataBackground: {
              lineStyle: {
                width: 1,
                color: "rgba(113, 151, 247, 1)",
              },
              areaStyle: {
                color: "rgba(255,255,255,0)",
              },
            },
          },
          {
            type: "inside",
            xAxisIndex: 0,
            filterMode: "none",
          },
        ],
        series: [
          {
            data: [],
            type: "line",
            showSymbol: false, // 移除线上的点
            lineStyle: {
              color: "#7197F7",
            },
            areaStyle: {
              opacity: 0.1,
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: "#7197F7",
                },
                {
                  offset: 1,
                  color: "#AAC8FC",
                },
              ]),
            },
          },
        ],
      },
    };
  },
  watch: {
    loading(val) {
  		if(!val){
  			this.setConsumptionLineChart(this.xAxisData, this.yAxisData);
  		}
       
    },
  },
  methods: {
    
    setConsumptionLineChart(xAxisData, yAxisData) {
      this.eChartOption.xAxis.data = xAxisData;
      this.eChartOption.series[0].data = yAxisData;
      var myChart = echarts.init(this.$refs.echart);
      myChart.setOption(this.eChartOption);
    },
  },
};
</script>
<style lang="scss" scoped>
.statistical-line-chart {
  width: 100%;
  height: 100%;
  background-color: #fff;
  border-radius: 4px;
  span {
    display: inline-block;
  }
  .statistical-line-chart-title {
    padding: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    div:nth-child(1) {
      span:nth-child(1) {
        font-size: 18px;
      }
      span:nth-child(2) {
        margin-left: 10px;
      }
    }
    div:nth-child(2) {
      font-size: 14px;
      color: #828894;
    }
  }
  .statistical-line-chart-echart {
     height: calc(100% - 80px);
    .echartClass {
      width: 100%;
      height: 100%;
    }
  }
}
</style>
