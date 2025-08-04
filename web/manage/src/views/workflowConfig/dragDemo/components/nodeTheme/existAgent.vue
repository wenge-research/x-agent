<template>
  <div class="node" :id="node.id">
    <!-- <div class="tit">
            <img src="@/assets/svg/gongzuoliujiedian-bianliang.svg" />
            <span class="label">{{ label }}</span>
        </div> -->
    <!-- <i class="el-icon-error" @click.stop="removeNode"></i> -->
    <RunningResult v-show="showResult" :inputList="appForm.inputs" :outputJsonData="outputJsonData"
      @closeRunningResult="handleCloseResult" />
    <RunningState ref="RunningState" :show="isRunning" :isShowBtn="isRunningBtn" :isRunningText="isRunningText"
      @openResultHandler="handleOpenResult" />
    <div class="node-content">
      <HeadTool :label="label" @remove="removeHandler" @testNodes="openRunDrawer" :showSub="showSub" :showDraw="true"
        @hideNode="hideCurNode" @copy="copyHandler" @input="inputChange" imgSuffix="yiyouagent" />
      <div v-show="showSub">
        <div class="model flex justify-between mb8" v-if="toolData && toolData.introduce">
          <div class="model-right flex items-center introduce" :title="toolData.introduce">
            {{toolData.introduce}}
          </div>
        </div>
        <div class="model flex justify-between mb8">
          <div class="model-label">场景</div>
            <div class="model-right flex items-center sence">
              <div v-if="appForm && appForm.sence" v-html="appForm.sence.replace(/\n/g, '<br>')"></div>
            </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  // mixins
  import nodeThemeMixins from "./nodeThemeMixins";
  export default {
    name: "knowledge",
    inject: ["getGraph", "getNode"],
    mixins: [nodeThemeMixins],
    data ()
    {
      return {};
    },
    mounted () { },
    methods: {},
  };
</script>
<style lang="scss" src="./common.scss" scoped></style>
<style lang="scss" scoped>
  .introduce {
    background: #F7F8FA;
    border-radius: 4px;
    width: 100%;
    padding: 2px 8px;
    color: #86909C;
    font-size: 14px;
    line-height: 22px;
    height: 26px;
    overflow: hidden;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
  }
  .sence {
    background: #F7F8FA;
    border-radius: 4px;
    width: 100%;
    padding: 8px;
    color: #86909C;
    font-size: 14px;
    line-height: 22px;
    height: 78px;
    overflow: hidden;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
  }
</style>