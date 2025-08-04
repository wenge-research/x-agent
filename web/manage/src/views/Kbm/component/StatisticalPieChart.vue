<template>
  <div class="statistical-pie-chart" v-loading="loading">
	<div v-if="type=='1'">
		<div class="statistical-pie-chart-title">
		  <div>
			<span style="font-size: 18px">知识类型</span>
			<iconpark-icon name="question-line"></iconpark-icon>
		  </div>
		  <div>单位：条</div>
		</div>
		<div class="statistical-pie-chart-echart flex">
		  <div class="echartClass" ref="echart1"></div>
		  <div>
			<div class="pie-legend-list">
			  <div class="pie-legend-item" v-for="(item,index) in pieData1" :key="index">
				<div :style="{'background': colorList[index]}" class="pie-legend-item-icon"></div>
				<div class="pie-legend-item-name">{{ item.name }}</div>
				<div class="pie-legend-item-value">{{ formatAmount(item.value) }}</div>
			  </div>
			</div>
		  </div>
		</div>
	</div>
	<div v-if="type=='2'">
		<div class="statistical-pie-chart-title">
		  <div>
			<span style="font-size: 18px">关联应用类型</span>
			<iconpark-icon name="question-line"></iconpark-icon>
		  </div>
		  <div>单位：个</div>
		</div>
		<div class="statistical-pie-chart-echart flex">
		  <div class="echartClass" ref="echart2"></div>
		  <div>
			<div class="pie-legend-list">
			  <div class="pie-legend-item" v-for="(item,index) in pieData2" :key="index">
				<div :style="{'background': colorList[index]}" class="pie-legend-item-icon"></div>
				<div class="pie-legend-item-name">{{ item.name }}</div>
				<div class="pie-legend-item-value">{{ formatAmount(item.value) }}</div>
			  </div>
			</div>
		  </div>
		</div>
	</div>
  </div>
</template>
<script>
import echarts from "echarts";
export default {
  name: "StatisticalPieChart",
  props: {
	knowledgeCountInfoObj: {
		type: Object,
		default: () => {
		  return {}
		}
	},
	applicationCountInfoObj: {
		type: Object,
		default: () => {
		  return {}
		}
	},
    type: {
      type: String,
      default: ''
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      pieData1: [],
	  pieData2: [],
      colorList: ["#B89AF9", "#7197F7", "#FFCF8B", "#5AD8A6"],
      eChartOption: {
        tooltip: {
          trigger: "item",
        },
        legend: {
          // orient: "vertical",
          // right: "60",
          // top: "center",
          // icon: "pin",
          // formatter: function (name) {
          //     return  name;
          // }
          show: false,
        },
        color: ["#B89AF9", "#7197F7", "#FFCF8B", "#5AD8A6"],
        series: [
          {
            type: "pie",
            // left: "0",
            radius: ["40%", "70%"],
            center: ["50%", "50%"],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: "center",
              formatter: (item) => {
                return `{a|${item.name}}\n{b|${item.value}}`;
              },
              rich: {
                a: {
                  fontSize: 16,
                  color: "#1D2129",
                  lineHeight: 24,
                },
                b: {
                  fontSize: 20,
                  fontWeight: 600,
                },
              },
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: "400",
              },
            },
            labelLine: {
              show: false,
            },
            itemStyle: {
              borderWidth: 1,
              borderColor: "#fff",
            },
            data: [],
          },
        ],
      },
    };
  },
  watch: {
    loading(val) {
		console.log('loading',val)
		if(!val){
			this.getPieData()
		}
       
    },
  },
  // created() {
	 //  this.$nextTick(() => {
	 //     this.getPieData()
	 //  })    
  // },
  methods: {
    formatAmount(value) {
      return new Intl.NumberFormat('zh-CN').format(value);
    },
	getPieData(){	
		console.log('knowledgeCountInfoObj1',this.knowledgeCountInfoObj)
		if (this.type == '1' ){			
			let dataInfo = [{ value: this.knowledgeCountInfoObj.wddCount, name: "QA对" },
				  { value: this.knowledgeCountInfoObj.wjCount, name: "文档数据" },
				  { value: this.knowledgeCountInfoObj.urlCount, name: "URL" },
				  { value: this.knowledgeCountInfoObj.jghCount, name: "结构化数据" }]
			this.eChartOption.series[0].data = dataInfo
			this.pieData1 = dataInfo
			var myChart = echarts.init(this.$refs.echart1);
			myChart.setOption(this.eChartOption);			      			   			
		} else {			
				let dataInfo = [{ value: this.applicationCountInfoObj.llmCount, name: "LLM" },
					  { value: this.applicationCountInfoObj.dialogueCount, name: "对话流" },
					  { value: this.applicationCountInfoObj.textAgentCount, name: "文本生成" },
					  { value: this.applicationCountInfoObj.workflowCount, name: "工作流" }]
				this.eChartOption.series[0].data = dataInfo
				this.pieData2 = dataInfo
				var myChart = echarts.init(this.$refs.echart2);
				myChart.setOption(this.eChartOption);			     			   			
		}
	}
	
  },
};
</script>
<style lang="scss" scoped>
.statistical-pie-chart {
  width: 100%;
  height: 100%;
  background: #ffffff;
  border-radius: 4px;
  span {
    display: inline-block;
  }
  .statistical-pie-chart-title {
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
  .statistical-pie-chart-echart {
     height:240px;
    padding-left: 13px;
	justify-content: space-between;
    .echartClass {
      width: 70%;
      height: 100%;
    }
    .pie-legend-list{
      height: 90%;
      display: flex;
	  margin-right: 20px;
      flex-direction: column;
      justify-content: space-between;
      // align-items: center;
      .pie-legend-item{
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        .pie-legend-item-icon{
          height: 9px;
          width: 9px;
          border-radius: 50%;
          margin-right: 8px;
        }
        .pie-legend-item-name{
          color: #86909C;
          width: 100px;
          font-size: 14px;
        }
        .pie-legend-item-value{
          color: #1D2129;
          font-size: 14px;
          font-weight: bold;
        }
      }
    }
  }
}
</style>
