<template>
    <div class="usage-info-component">
        <div class="usage-info-component-one">
            <div><slot name="icon"></slot></div>
            <div>{{ usageInfoData.title }}</div>
            <div :title="usageInfoData.remark">
                <tooltipComponent :remark="usageInfoData.remark" />
            </div>
        </div>
        <div class="usage-info-component-two">
            <div class="usage-info-component-two-left">
                <span>{{ usageInfoData.number }}</span><span>次</span>
            </div>
            <div class="usage-info-component-two-right">
                <span>环比</span><span :class="usageInfoData.proportionStatus === 0 ? 'proportion-color-one' : 'proportion-color-two'">{{ usageInfoData.proportion }}</span>
            </div>
        </div>
    </div>
</template>
<script>
import tooltipComponent from "./tooltipComponent";
export default {
    name: 'usageInfoComponent',
    components: {
        tooltipComponent
    },
    data(){
        return {
            usageInfoData: {
                title: '',
                remark: '',
                number: 0,
                proportion: '0%',
                proportionStatus: 0  // 0 下降 1 增长
            }
        }
    },
    methods: {
        setUsageInfoData(data){
            this.usageInfoData = data;

            // usageInfoData: {
            //     title: '总使用量',
            //     remark: '使用情况介绍',
            //     number: 8494,
            //     proportion: '30%',
            //     proportionStatus: 0  // 0 下降 1 增长
            // }

        }
    }
}
</script>
<style lang="scss" scoped>
.usage-info-component{
    padding: 16px 24px;
    font-size: 16px;
    background-color: #fff;
    border: 1px solid #D5D8DE;
    .usage-info-component-one{
        display: flex;
        align-items: center;
        margin-bottom: 14px;
    }
    .usage-info-component-two{
        display: flex;
        align-items: center;
        justify-content: space-between;
        .usage-info-component-two-left{
            span:nth-child(1){
                font-size: 32px;
            }
        }
        .usage-info-component-two-right{
            span:nth-child(1){
                margin-right: 5px;
            }
            span:nth-child(2){
                position: relative;
                padding-left: 15px;
            }
            span:nth-child(2)::before{
                content: '';
                position: absolute;
                top: 50%;
                left: 0;
                transform: translateY(-50%);
                width: 0;
                height: 0;
                border-left: 5px solid transparent;
                border-right: 5px solid transparent;
                border-top: 5px solid transparent;
                border-bottom: 5px solid transparent;
            }
            // 环比颜色
            .proportion-color-one{
                color: #3FB816;
                &::before{
                    border-top: 5px solid #3FB816 !important;
                }
            }
            .proportion-color-two{
                color: #FF4D4F;
                &::before{
                    border-bottom: 5px solid #FF4D4F !important;
                }
            }
        }
    }

    

}
</style>