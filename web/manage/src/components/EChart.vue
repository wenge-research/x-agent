<template>
  <!--图表展示在这个div中-->
  <div style="height: 100%" ref="echart">
    echart
  </div>
</template>

<script>
import echarts from 'echarts'
export default {
  //接收父类两个数据 1、chartData (series数据 + x坐标系数据）2、isAxisChart （是否有x坐标系，如果false，那么上面的xData就为空）
  props: {
    chartData: {
      type: Object,
      default() {
        return {
          xData: [],
          series: []
        }
      }
    },
    isAxisChart: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    //计算 选择是有x轴 还是没有
    options() {
      return this.isAxisChart ? this.axisOption : this.normalOption
    },
    //用于下面的resize 改变图表尺寸，在容器大小发生改变时需要手动调用
    isCollapse() {
      return this.$store.state.tab.isCollapse
    }
  },
  watch: {
    //监听chartData数据
    chartData: {
      handler: function() {
        this.initChart()
      },
      deep: true
    },
    //监听isCollapse 因为头部水平扩展是一个动画需要时间，所以这里延迟300毫秒
    isCollapse() {
      setTimeout(() => {
        this.resizeChart()
      }, 300)
    }
  },
  data() {
    //在数据中有些数据在数据件中是写死的
    return {
      echart: null,
      axisOption: {
        legend: {
          textStyle: {
            color: '#333'
          }
        },
        grid: {
          left: '20%'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLine: {
            lineStyle: {
              color: '#17b3a3'
            }
          },
          axisLabel: {
            color: '#333'
          }
        },
        yAxis: [
          {
            type: 'value',
            axisLine: {
              lineStyle: {
                color: '#17b3a3'
              }
            }
          }
        ],
        color: [
          '#2ec7c9',
          '#b6a2de',
          '#5ab1ef',
          '#ffb980',
          '#d87a80',
          '#8d98b3',
          '#e5cf0d',
          '#97b552',
          '#95706d',
          '#dc69aa',
          '#07a2a4',
          '#9a7fd1',
          '#588dd5',
          '#f5994e',
          '#c05050',
          '#59678c',
          '#c9ab00',
          '#7eb00a',
          '#6f5553',
          '#c14089'
        ],
        series: []
      },
      normalOption: {
        tooltip: {
          trigger: 'item'
        },
        color: ['#0f78f4', '#dd536b', '#9462e5', '#a6a6a6', '#e1bb22', '#39c362', '#3ed1cf'],
        series: []
      }
    }
  },
  methods: {
    initChart() {
      //获取处理好的数据
      this.initChartData()
      //获取echart对象
      if (this.echart) {
        this.echart.setOption(this.options)
      } else {
        //通过refs获取
        this.echart = echarts.init(this.$refs.echart)
        this.echart.setOption(this.options)
      }
    },
    //处理好数据
    initChartData() {
      if (this.isAxisChart) {
        this.axisOption.xAxis.data = this.chartData.xData
        this.axisOption.series = this.chartData.series
      } else {
        this.normalOption.series = this.chartData.series
      }
    },
    resizeChart() {
      this.echart ? this.echart.resize() : ''
    }
  },
  mounted() {
    //resize 改变图表尺寸，在容器大小发生改变时需要手动调用（因为侧边栏是可以收缩的，所以这里图表根据是否收缩来改变图表尺寸）
    window.addEventListener('resize', this.resizeChart)
  },
  //销毁 防止内存泄漏
  destroyed() {
    window.removeEventListener('resize', this.resizeChart)
  }
}
</script>

<style lang="scss" scoped></style>
