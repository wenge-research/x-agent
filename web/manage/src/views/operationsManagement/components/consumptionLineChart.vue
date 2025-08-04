<template>
    <div class="consumption-line-chart" v-loading="loading">
        <div class="consumption-line-chart-title">
            <div>
                <span>token消耗</span>
            </div>
            <div  >单位：千tokens</div>
        </div>
        <div class="consumption-line-chart-echart">
            <div class="echartClass"  ref="echart" ></div>
        </div>
    </div>
</template>
<script>
import echarts from 'echarts'
import { getTokenConsumption } from '@/api/operation'
export default {
    name: 'consumptionLineChart',
    data(){
        return {
            loading:false,
            eChartOption : {
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: []
                },
                yAxis: {
                    type: 'value'
                },
                grid: {
                    left: '56px',
                    right: '36px',
                    top: '30px',
                    bottom: '50px'
                },
                series: [
                    {
                        data: [],
                        type: 'line',
                        showSymbol: false, // 移除线上的点
                        lineStyle: {
                            color: '#3cbdfe'
                        },
                        areaStyle: {
                            color: 'rgba(45, 184, 254, 0.1)'
                        }
                    }
                ]
            }    
        }
    },
    mounted(){
        this.getTokenConsumption();
    },
    methods:{
        getTokenConsumption(){
            this.loading = true;
            getTokenConsumption({}).then(data => {
                if(data.code=="000000"){
                    let xAxisData = data.data.map(item => item.date);
                    let yAxisData = data.data.map(item => item.tokenTotal/1000);
                    this.setConsumptionLineChart(xAxisData,yAxisData);
                }
            }).finally(() => {
                this.loading = false;
            })
        },
        setConsumptionLineChart(xAxisData,yAxisData){
            this.eChartOption.xAxis.data = xAxisData;
            this.eChartOption.series[0].data = yAxisData;
            var myChart = echarts.init(this.$refs.echart);
            myChart.setOption(this.eChartOption);
        }
    },
    

}
</script>
<style lang="scss" scoped>
.consumption-line-chart{
    width: 1088px;
    height: 540px;
    background-color: #fff;
    span{
        display: inline-block;
    }
    .consumption-line-chart-title{
        padding: 16px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        div:nth-child(1){
            span:nth-child(1){
                font-size: 18px;
            }
            span:nth-child(2){
                margin-left: 10px;
            }
        }
        div:nth-child(2){
            font-size: 14px;
            color: #828894;
        }
    }
    .consumption-line-chart-echart{
        height: 492px;
        .echartClass{
            width: 100%;
            height: 100%;
        }
    }
}
</style>