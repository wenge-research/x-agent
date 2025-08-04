<template>
  <div class="node" :id="node.id">
      <!-- <i class="el-icon-error" @click.stop="removeNode"></i> -->
      <RunningResult
          v-show="showResult"
          :inputList="appForm.inputs" :outputJsonData="outputJsonData"
          @closeRunningResult="handleCloseResult"
      />
      <RunningState
          ref="RunningState"
          :show="isRunning" :isShowBtn="isRunningBtn" :isRunningText="isRunningText"
          @openResultHandler="handleOpenResult"
      />
      <div class="node-content">
          <HeadTool
              :label="label"
              @remove="removeHandler"
              @testNodes="openRunDrawer"
              :showSub="showSub"
              :showDraw="true"
              @hideNode="hideCurNode"
              @copy="copyHandler"
              @input="inputChange"
              :imgSuffix="img"
          />
          <div v-show="showSub">
            <div class="model flex justify-between mb8 sence">
              <div v-if="appForm && appForm.condition" v-html="appForm.condition.replace(/\n/g, '<br>')"></div>
            </div>
          </div>
      </div>
  </div>
</template>

<script>
// mixins
import nodeThemeMixins from "./nodeThemeMixins";

export default {
  name: "globalCondition",
  inject: ["getGraph", "getNode"],
  mixins: [nodeThemeMixins],
  data() {
      return {
          node: {},
          appForm: {},
          konwlwdgeAllList: [],
          selectedList: [],
          label: "",
          img: "",
      };
  },
  mounted() {
   
  },
  beforeDestroy() {
  },
  methods: {
    
  },
};
</script>
<style lang="scss" src="./common.scss" scoped></style>
<style lang="scss" scoped>
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