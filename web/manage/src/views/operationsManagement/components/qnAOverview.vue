<template>
    <div class="qnA-overview" v-loading="loading">
        
        <div class="qnA-overview-two">
            <div class="qnA-overview-two-header" >
                <div>
                    <span>问答排行榜Top50</span><span>?</span>
                </div>
                <div>
                    <el-dropdown @command="handleCommand" >
                        <span class="el-dropdown-link">
                            {{listTypeStr}}<i class="el-icon-arrow-down el-icon--right"></i>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="count" >提问次数最多</el-dropdown-item>
                            <el-dropdown-item command="time" >时间排序</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </div>
            </div>
            <div class="qnA-overview-two-content" >
                <div class="qnA-overview-two-content-item" v-for="(item,index) in qnAOverviewListData" :key="index">
                    <div class="qnA-overview-two-content-item-left" >
                        <span class="qnA-overview-two-content-item-sort" :style="{backgroundImage: `url(${judgeBackgroundIcon(index+1)})`}">{{ index+1 }}</span>
                        <span class="qnA-overview-two-content-item-title" :title="item.question" :style="{maxWidth: '380px'}" >{{item.question}}</span>
                    </div>
                    <div>
                        <span class="qnA-overview-two-content-item-number" :class="judgeNumberColor(index+1)">{{item.count}}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>

// {
//     id: 1,
//     sort: 1,
//     title: '好设计是不唐突的',
//     questionsNumber: 320,
// },
import { questionChartsTop } from '@/api/operation'
export default {
    name: 'QnAOverview',
    mounted(){
        this.getQuestionChartsTop();
    },
    data(){
        return {
            loading:false,
            listType:'count',
            listTypeStr:'提问次数最多',
            qnAOverviewListData: [],
            iconBackgroundOne: require('@/assets/images/qnAOverviewSort1.png'),
            iconBackgroundTwo: require('@/assets/images/qnAOverviewSort2.png'),
            iconBackgroundThree: require('@/assets/images/qnAOverviewSort3.png'),
            iconBackgroundOther: require('@/assets/images/qnAOverviewSortOther.png'),
        }
    },
    methods:{
        handleCommand(command){
            this.listTypeStr = command=='time'?'时间排序':'提问次数最多';
            this.listType = command;
            this.getQuestionChartsTop();
        },
        getQuestionChartsTop(){
            this.loading = true;
            questionChartsTop({
                top50Type:this.listType
            }).then(data => {
                console.log(data,'问答排行榜');
                if(data.code == '000000'){
                    this.qnAOverviewListData = data.data;
                }
            }).finally(() => {
                this.loading = false;
            })
        },
        judgeBackgroundIcon(sort){
            if(sort === 1){
                return this.iconBackgroundOne;
            }else if(sort === 2){
                return this.iconBackgroundTwo;
            }else if(sort === 3){
                return this.iconBackgroundThree;
            }else{
                return this.iconBackgroundOther;
            }
        },
        judgeNumberColor(sort){
            if(sort === 1){
                return 'qnA-overview-two-content-item-number-red';
            }else if(sort === 2){
                return 'qnA-overview-two-content-item-number-yellow';
            }else if(sort === 3){
                return 'qnA-overview-two-content-item-number-blue';
            }else{
                return '';
            }
        }
    }
}
</script>
<style lang="scss" scoped>
.qnA-overview{
    width: 512px;
    box-sizing: border-box;
    padding-top: 30px;
    height: 100%;
    background-color: #fff;
    
        
    span{
        display: inline-block;
    }
    .qnA-overview-two{
        width: 100%;
        .qnA-overview-two-header{
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding: 0px 15px;
            .el-dropdown-link {
                cursor: pointer;
                color: #409EFF;
            }
            .el-icon-arrow-down {
                font-size: 12px;
            }
        }
        .qnA-overview-two-content{
            height: 640px;
            overflow-y: auto;
            overflow-x: hidden;
            .qnA-overview-two-content-item{
                padding: 0px 15px;
                width: 100%;
                display: flex;
                justify-content: space-between;
                align-items: center;
                height: 56px;
                border-top: 1px solid #D5D8DE;
                color: #494E57;
                cursor: pointer;
                user-select: none;
                .qnA-overview-two-content-item-left{
                    display: flex;
                    align-items: center;
                }
                .qnA-overview-two-content-item-sort{
                    background-size: 100% 100%;
                    width: 24px;
                    height: 24px;
                    margin-right: 16px;
                    text-align: center;
                    line-height: 24px;
                    font-size: 12px;
                    color: #fff;
                }
                .qnA-overview-two-content-item-title{
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                }
                .qnA-overview-two-content-item-number{
                    font-weight: 600;
                }
                .qnA-overview-two-content-item-number-red{
                    color: #FF4D4F;
                }
                .qnA-overview-two-content-item-number-yellow{
                    color: #FFC107;
                }
                .qnA-overview-two-content-item-number-blue  {
                    color: #1747E5;
                }

            }
        }
    }
}
</style>
