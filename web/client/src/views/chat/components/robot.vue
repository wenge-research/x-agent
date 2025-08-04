<!-- eslint-disable vue/no-async-in-computed-properties -->
<template>
	<div class="robotHome">
		<div id="videoDom" v-show="getresult">
        <div
          id="convasParent"
          ref="convasParent"
          :style="getVideoStyle"
        >
          <canvas id="convasVideo"></canvas>
          <video
            webkit-playsinline
            id="jswebrtc"
            ref="jswebrtc"
            autoplay
            :muted="muted"
          ></video>
          <div v-if="notShowIndividual">
            <div v-if="muted && getresult" class="ForMuted" @click="()=>ForMuted(false)">
              <img :src="ismutedImg" alt />
              <span>开启声音</span>
            </div>

            <div v-else-if="!muted && getresult" class="ForMuted" @click="()=>ForMuted(true)">
              <img :src="nomutedImg" alt />
              <span>关闭声音</span>
            </div>
          </div>
        </div>
		</div>
	</div>

</template>

<script lang="ts" setup>
import { ref,computed,watch, onMounted } from 'vue';
import ismutedImg from '/@/assets/videoPage/ismuted.svg';
import nomutedImg from '/@/assets/videoPage/nomuted.svg';
import mittBus from '/@/utils/mitt';
import { useRobotStore } from '/@/stores/robot';
import { Session } from '/@/utils/storage';

const robotStore = useRobotStore();

const getresult = computed(() => {return robotStore.getresult})
const notShowIndividual = computed(() => {return robotStore.notShowIndividual})
const muted = computed(() => {return robotStore.muted})

const getVideoStyle = computed(() => {
	return {
        // top: (robotStore.personPos[0] / robotStore.referY) * 100 + "%",
        // left: (robotStore.personPos[1] / robotStore.referX) * 100 + "%",
        // width: (robotStore.personSize[0] / robotStore.referX) * 100 + "%",
        // height: (robotStore.personSize[1] / robotStore.referY) * 100 + "%"
      }
})

const ForMuted = (op) => {
	robotStore.muted = op
}
onMounted(() => {
})
</script>
<style scoped lang="scss">
.robotHome {
  height: 100%;
  position: relative;
  overflow: hidden;
  background-repeat: no-repeat;
  background-size: cover;
  .ForSet {
    position: absolute;
    right: 5%;
    top: 5%;
    font-size: 24px;
    color: #383b40;
    opacity: 0.8;
    cursor: pointer;
  }
  .ForMuted {
    position: absolute;
    z-index: 1000;
    left: 10%;
    top: 4%;
    display: flex;
    align-items: center;
    cursor: pointer;
    img {
      width: 21px;
      cursor: pointer;
      opacity: 0.8;
    }
    span {
      color: #383b40;
      vertical-align: middle;
    }
  }
  .individualClass {
    position: absolute;
    left: 5%;
    top: 8%;
    display: flex;
    flex-direction: column;
    gap: 10px;
    .el-avatar {
      cursor: pointer;
    }
  }
  .breakIcon {
    position: absolute;

    .icon-duankai {
      font-size: 12px;
      padding-right: 5px;
    }
  }
  .pcBreakIcon {
    right: 11%;
    top: 7%;
  }
  .phoneBreakIcon {
    right: 5%;
    top: 5%;
  }
  #videoDom {
    height: 100%;
    width: 100%;
    z-index: 100;
    background-repeat: round;
    background-size: 100% 100%;
    .pcVideo {
      width: 100%;
      height: 100%;
    }
    #jswebrtc {
       display: none; //回头放开

      //  zhiyiwei 样式修改
       display: block;
       margin: 45px auto;
    }
    #convasVideo{
      display: none;
    }
    #jswebrtc.phoneVideo {
      height: calc(100%);
      width: auto;
      position: absolute;
    }
  }
  .charts-content {
    position: absolute;
    top: 164px;
    left: 45%;
    z-index: 1000;
	width: 700px;
	height: 624px;
    .pcCharts {
      height: 100%;
      .charts {
        height: 100%;
        .chart-box {
          height: calc(100% - 90px);
        }
      }
    }
  }
  .canvas {
    border: 1px solid red;
    position: absolute;
    top: 0px;
    z-index: 100;
  }
}
</style>
