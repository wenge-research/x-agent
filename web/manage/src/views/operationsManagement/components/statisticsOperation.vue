<template>
    <div class="statisticsOperation "   :style="{ backgroundImage: `url(${statisticsData.backgroundImage})` }" >
        <div class="box-one"  >
            <span >{{ statisticsData.title }}</span>
            <!-- <span >
                <tooltipComponent iconColor="#fff" :remark="statisticsData.remark" />
            </span> -->
        </div>
        <div class="box-two" @click="selectContent(statisticsData.biaoShi)" :class="{'cuseorPointer': statisticsData.biaoShi == 'YY'}"  >
            <span :class="{'wenziDecoration': statisticsData.biaoShi == 'YY'}" >{{ statisticsData.number }}</span><span>个</span>
        </div>
        <div class="box-three" >
            <div class="box-three-item" v-for="(item, index) in statisticsData.list" :key="index">
                <div>
                    <span>{{ item.name }}</span>
                </div>
                <div>
                    <span>{{ item.number }}</span>
                    <span>个</span>
                </div>
            </div>
        </div>
        
    </div>
</template>
<script>
/**
 * 统计运营
 * */ 
import tooltipComponent from "./tooltipComponent";

export default {
    name: 'statisticsOperation',
    components: {
        tooltipComponent,
    },
    data(){
        return {
            dialogVisibleApplication: false,
            statisticsData: {
                title: '',
                remark: '',
                number: 0,
                biaoShi: 'YY',  // YY 应用总数  ZH 知识库总数
                backgroundImage: '',
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
            }
        }
    },
    methods: {
        // 设置组件数据
        setStatisticsData(data){
            this.statisticsData = data;


            // statisticsData: {
            //     title: '应用总数',
            //     remark: '运营统计数据',
            //     number: 100,
            //     backgroundImage: require('@/assets/images/应用.png'),
            //     list: [
            //         {
            //             name: '运营统计',
            //             number: 100
            //         },
            //         {
            //             name: '工作流智能体',
            //             number: 100
            //         },  
            //         {
            //             name: '普通应用',
            //             number: 100
            //         }
            //     ]
            // }

        },
        selectContent(biaoShi){
            if(biaoShi == 'YY'){
                this.$EventBus.$emit("selectProjectOpen", true);
            }
        }
    }

}
</script>
<style lang="scss" scoped>

.statisticsOperation{
    width: 100%;
    height: 100%;
    padding: 28px 20px;
    border-radius: 10px;
    background-color: #fff;
    background-size: 100% 100%;
    font-size: 16px;
    color: #fff;
    user-select: none;
    span{
        display: inline-block;
    }
    .box-one{
        margin-bottom: 12px;
    }
    .box-two{
        margin-bottom: 15px;
        span:nth-child(1){
            font-size: 40px;
        }
    }
    .wenziDecoration{
        text-decoration: underline;
    }
    .box-three{
        display: flex;
        .box-three-item{
            padding: 0px 24px;
            border-right: 1px solid #E5E5E5;
            div:nth-child(1){
                margin-bottom: 13px;
            }
            span:nth-child(1){
                font-size: 17px;
            }
            &:last-child{
                border-right: none;
            }
        }
    }
}

.cuseorPointer{
    cursor: pointer;
}

</style>
