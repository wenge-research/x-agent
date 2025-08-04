<template>
    <div class="operationsManagement-box" style="height: 100%;" >

        <div class="operationsManagement" v-show="bankuaiStatus" style="min-height: 100%;" >
            <div class="operationsManagement-title">{{ $t("operationsManagement") }}</div>
            <div class="operationsManagement-content">
                <div class="operationsManagement-content-left">
                  <div v-loading="loading">
                    <!-- 统计数据 -->
                    <div class="operationsManagement-content-left-one" >
                      <div>
                          <statisticsOperation ref="statisticsDataOne" />
                      </div>
                      <div>
                          <statisticsOperation ref="statisticsDataTwo"/>
                      </div>
                    </div>
                    <!-- 使用情况 -->
                    <div class="operationsManagement-content-left-two" >
                      <div><usageInfoComponent ref="usageInfoComponentOneDom" /></div>
                      <div><usageInfoComponent ref="usageInfoComponentTwoDom"/></div>
                    </div>
                  </div>
                    <!-- 折线图 -->
                    <div class="operationsManagement-content-left-three" >
                        <consumptionLineChart ref="consumptionLineChartDom" />
                    </div>
                </div>
                <!-- 问答概览列表 -->
                <div class="operationsManagement-content-right">
                    <div>
                        <QATotalCount ref="QATotalCountDom" />
                    </div>
                    <div>
                        <qnAOverview  />
                    </div>
                </div>
            </div>
        </div>

        <div class="project-detail" 
            style="height: 100%;" 
            v-show="!bankuaiStatus"
        >
            <projectDetail :key="projectDetailKey" :data="projectDetailData" />
        </div>
        <!-- 选择应用弹出框 -->
        <dialogOne :dialogVisibleApplication="dialogVisibleApplication" v-if="dialogVisibleApplication"  />
    </div>
</template>
<script>
import statisticsOperation from './components/statisticsOperation.vue'
import usageInfoComponent from './components/usageInfoComponent.vue'

import consumptionLineChart from './components/consumptionLineChart.vue'
import qnAOverview from './components/qnAOverview.vue'
import QATotalCount from './components/QATotalCount.vue'
import projectDetail from './components/projectDetail/projectDetail.vue'

import { applicationCount,knowledgeCount,getUsingInfo,getQaCount } from '@/api/operation'
// 应用弹出框
import dialogOne from "./components/dialogOne";

export default {
  name: 'operationsManagement',
  components: {
    statisticsOperation,
    usageInfoComponent,
    consumptionLineChart,
    qnAOverview,
    QATotalCount,
    projectDetail,
    dialogOne
  },
  data(){
    return {
        projectDetailKey:0,
        projectDetailData:{},
        bankuaiStatus:true,
        loading:false,
        dialogVisibleApplication:false,
        statisticsDataOne: {
            title: '应用总数',
            remark: '应用总数',
            number: 0,
            biaoShi: 'YY',
            backgroundImage: require('@/assets/images/应用.png'),
            list: [
                {
                    name: '',
                    number: 0
                },
                {
                    name: '',
                    number: 0
                },  
                {
                    name: '',
                    number: 0
                }
            ]
        },
        statisticsDataTwo: {
            title: '知识库总数',
            remark: '知识库总数',
            number: 0,
            biaoShi: 'ZH',
            backgroundImage: require('@/assets/images/知识库.png'),
            list: [
                {
                    name: '',
                    number: 0
                },
                {
                    name: '',
                    number: 0
                },  
                {
                    name: '',
                    number: 0
                }
            ]
        },
        usageInfoComponentOneData: {
            title: '总使用量',
            remark: '总使用量',
            number: 0,
            unit: '',
            proportion: '',
            proportionStatus: 0  // 0 下降 1 增长  
        },
        usageInfoComponentTwoData: {
            title: '总使用用户数',
            remark: '总使用用户数',
            number: 0,
            unit: '',
            proportion: '',
            proportionStatus: 0  // 0 下降 1 增长  
        },
        
    }
  },
  mounted(){
    this.setStatisticsData()
    this.getknowledgeCount()
    this.setUsageInfoComponent()
    this.setQaCount()

    
    // 应用选择框打开
    this.$EventBus.$on("selectProjectOpen", (item)=>{
        this.dialogVisibleApplication = true;
    });

    // 选择框关闭
    this.$EventBus.$on("selectProjectClose", (item)=>{
        this.dialogVisibleApplication = false;
    });

    // 应用选择
    this.$EventBus.$on("selectProject", (item)=>{
        this.projectDetailData = item;
        this.projectDetailKey++;
    });

    // 应用切换
    this.$EventBus.$on("switchingModule", (item)=>{
        this.switchBankuai(item.status);
    });
  },
  methods: {

    // 切换应用  接受布尔值
    switchBankuai(status){
        this.bankuaiStatus = status;
    },
    // 设置统计数据
    async setStatisticsData(){
        // 应用总数
        let data = await applicationCount();
        if(data.code == '000000'){
                this.statisticsDataOne.number = data.data.allCount;
                this.statisticsDataOne.list = [
                    {
                        name: '聊天助手',
                        number: data.data.zntCount
                    },
                    {
                        name: '文本生成',
                        number: data.data.gzlCount
                    },  
                    // {
                    //     name: '',
                    //     number: data.data.ptCount
                    // }
                ];
            this.$refs.statisticsDataOne.setStatisticsData(this.statisticsDataOne);
        }
    },
    async getknowledgeCount(){
        // 知识库总数
        let data = await knowledgeCount();
        if(data.code == '000000'){
                this.statisticsDataTwo.number = data.data.allCount;
                this.statisticsDataTwo.list = [
                    {
                        name: '文件',
                        number: data.data.wjCount
                    },
                    {
                        name: '问答对',
                        number: data.data.wddCount
                    },
                    {
                        name: 'URL',
                        number: data.data.urlCount
                    },
                    {
                        name: '结构化',
                        number: data.data.jghCount
                    },
                ]
            this.$refs.statisticsDataTwo.setStatisticsData(this.statisticsDataTwo);
        }
    },

    // 设置使用情况
    async setUsageInfoComponent(){
        this.loading = true;
        let data = await getUsingInfo();
        this.loading = false;
        if(data.code == '000000'){
            let {zsylInfo,zsyyhInfo} = data.data;

                // 总使用量
                this.usageInfoComponentOneData.number = zsylInfo.total;
                this.usageInfoComponentOneData.unit = zsylInfo.unit;
                if(zsylInfo?.QOQ?.includes('-')){
                    this.usageInfoComponentOneData.proportionStatus = 0;
                }else{
                    this.usageInfoComponentOneData.proportionStatus = 1;
                }
                this.usageInfoComponentOneData.proportion = zsylInfo?.QOQ;

                // 总使用用户数
                this.usageInfoComponentTwoData.number = zsyyhInfo.total;
                this.usageInfoComponentTwoData.unit = zsyyhInfo.unit;
                if(zsyyhInfo?.QOQ?.includes('-')){
                    this.usageInfoComponentTwoData.proportionStatus = 0;
                }else{
                    this.usageInfoComponentTwoData.proportionStatus = 1;
                }
                this.usageInfoComponentTwoData.proportion = zsyyhInfo?.QOQ;

                this.$refs.usageInfoComponentOneDom.setUsageInfoData(this.usageInfoComponentOneData);
            this.$refs.usageInfoComponentTwoDom.setUsageInfoData(this.usageInfoComponentTwoData);
        }
    },
    // 产生问答总数
    async setQaCount(){
        let data = await getQaCount();
        if(data.code == '000000'){
            this.$refs.QATotalCountDom.setQaCount(data.data.qaCount);
        }
    },
    
  }
}
</script>
<style lang="scss" scoped>

.operationsManagement{
    padding: 32px;
    background-color: #f0f2f5;
    .operationsManagement-title{
        font-size: 32px;
        margin-bottom: 16px;
    }
    .operationsManagement-content{
        display: flex;
        justify-content: space-between;
        align-items: stretch; /* 添加此行以使子元素填满高度 */
        .operationsManagement-content-left{
            width: 1088px;
            height: 100%;
            .operationsManagement-content-left-one{
                display: flex;
                justify-content: space-between;
                margin-bottom: 16px;
                >div{
                    width: 536px;
                    height: 199px;
                }
            }
            // 使用统计
            .operationsManagement-content-left-two{
                display: flex;
                justify-content: space-between;
                >div{
                    width: 536px;
                    height: 108px;
                }
            }
            // 折线图
            .operationsManagement-content-left-three{
                
            }
        }
        .operationsManagement-content-right{
            width: 512px;
            border: 1px solid #D5D8DE;
            display: flex;
            flex-direction: column;
            >div:nth-child(2){
                flex: 1;
            }
        }
    }
}
</style>
