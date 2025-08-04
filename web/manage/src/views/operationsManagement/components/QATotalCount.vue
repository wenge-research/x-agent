<template>
    <div class="QATotalCount">
        <div class="qnA-overview-one" :style="{backgroundImage: `url(${imgUrl})`}" >
            <div style="padding-top: 40px;padding-left: 60px;">
                <span class="qnA-overview-one-title">产生问答总数</span>
                <span>  
                    <tooltipComponent remark="问答情况" />
                </span>
            </div>
            <div style="padding-top: 20px;padding-left: 60px;" class="qnA-overview-one-number" >
                <span style="font-size: 40px;">{{qaCount}}</span><span style="font-size: 20px;">次</span>
            </div>
        </div>
    </div>
</template>
<script>
import tooltipComponent from "./tooltipComponent";
export default {
    name: 'QATotalCount',
    components: {
        tooltipComponent
    },
    data() {
        return {
            imgUrl: require('@/assets/images/问答总数.png'),
            qaCount: 0
        }
    },
    methods: {
        setQaCount(data){
            this.qaCount = data;
        }
    }
}
</script>
<style lang="scss" scoped>
.QATotalCount{
    .qnA-overview-one{
        width: 100%;
        height: 148px;
        
        background-size: 100% 100%;
        span{
            display: inline-block;
        }
        .qnA-overview-one-title{
            font-size: 18px;
            color: #494E57;
        }
        .qnA-overview-one-number{
            color: #1747E5;
        }
    }
}
</style>

