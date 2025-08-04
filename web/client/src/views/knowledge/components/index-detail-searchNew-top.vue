<template>
  <div class="top-box">
    <div class="top-box-icon-title">
      <div class="title">
        <span class="hot">{{type=='zh'?'热搜榜':'Popular searches'}}</span>
        <span class="text">大家都在搜</span>
      </div>
       <img :src="hot" alt="" />
    </div>
    <div class="top-box-container">
        <div class="top-box-container-item list" v-for="(item,index) in topList" :key="index" @click="searchNew(item.question)">
            <div class="top-box-container-item-content">
                <div class="top-box-container-item-content-left">
                  <div class="number">{{index+1}}</div>
                  <div class="question">{{item.question}}</div>
                </div>
                <!-- <div class="count">{{item.count}}{{type=='zh'?'次':'times'}}</div> -->
            </div>
        </div>
        <div class="top-box-container-item w-100"></div>
        <div class="top-box-container-item w-100"></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { defineEmits } from 'vue';
import hot from "/@/assets/ai/ai-hot.png";
interface Props {
  topList: any[];
  type?: string
}   
defineProps<Props>();
const emit = defineEmits(['emitQuestion']);
const searchNew = (question: string) => {
  emit('emitQuestion', question);
};
</script>

<style lang="scss" scoped>
.top-box{
    width: 358px;
    height: 662px;
    background: linear-gradient( 180deg, #FFD8C5 0%, #FFF5E2 100%);
    border-radius: 12px;
    backdrop-filter: blur(2px);
    // padding: 12px 20px;
    padding-top: 12px;
    box-sizing: border-box;
    // margin-top: 25px;
    // background: rgba($color: #FFF, $alpha: 0.3);
    // border-radius: 4px;
    // backdrop-filter: blur(2px);
    &-icon-title{
        display: flex;
        align-items: center;
        // margin-bottom: 16px;
        &>img{
            width: 16px;
            margin-right: 4px;
        }
        &>span{
            font-size: 16px;
            font-weight: 600;
            color: #383D47;
        }
    }
    .top-box-icon-title {
        display: flex;
        justify-content: space-between;
        padding-left: 16px;
        padding-right: 27px;
        position: relative;
        .title {
          .hot {
            height: 24px;
            font-family: YouSheBiaoTiHei;
            font-weight: bold;
            font-size: 24px;
            color: #383D47;
            line-height: 24px;
            text-align: left;
            font-style: normal;
            text-transform: uppercase;
            display: block;
            font-style: italic;
          }
          .text {
            height: 16px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 12px;
            color: #828894;
            line-height: 16px;
            text-align: left;
            font-style: normal;
            display: block;
            margin-bottom: 10px;
          }
        }
        &>img{
          width: 90px;
          height: 90px;
          position: absolute;
          right: 0;
          top: -30px;
        }
    }
    &-container{
      display: flex;
      justify-content: space-between;
      flex-wrap: wrap;
      // width: 100%;
      margin: 0 4px 4px 4px;
      width: 350px;
      height: 594px;
      padding: 12px 12px 18px 12px;
      background: #FFFFFF;
      border-radius: 8px;
        &-item-content{
            // width: 370px;
            line-height: 36px;
            height: 36px;
            display: flex;
            padding: 0 4px;
            box-sizing: border-box;
            justify-content: space-between;
            cursor: pointer;
            &-left{
                display: flex;
                .number{
                  // width: 35px;
                  font-weight: 500;
                  font-size: 15px;
                  color: #383D47;
                  font-style: italic;
                  margin-right: 12px;
              }
              .question{
                  width: 292px;
                  font-weight: 500;
                  font-size: 16px;
                  color: #383D47;
                  text-align: left;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
              }
            }
            
            .count{
                display: flex;
                align-self: flex-end;
                font-weight: 400;
                font-size: 12px;
                color: #828894;
            }
        }
        &-item-content:hover{
            background: #FFFFFF;
            border-radius: 2px;
        }
    }
    .list {
      border-bottom: 1px solid #E7E7E7;
    }
    .w-100{
        width: 100px;
    }
}
</style>
