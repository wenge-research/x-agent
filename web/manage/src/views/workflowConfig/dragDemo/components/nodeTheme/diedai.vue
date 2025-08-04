<template>
  <div class="node">
      <RunningResult v-show="showResult" :inputList="appForm.inputs" :outputJsonData="outputJsonData" @closeRunningResult="handleCloseResult" />
      <RunningState ref="RunningState"  :isRunningText="isRunningText" :isShowBtn="isRunningBtn" :show="isRunning" @openResultHandler="handleOpenResult" />
      <HeadTool
        :label="label"
        @remove="removeHandler"
        @testNodes="openRunDrawer"
                :showSub="showSub"
                :showDraw="true"
                @hideNode="hideCurNode"
        @copy="copyHandler"
        @input="inputChange"
        imgSuffix="diedai"
      />
    <div class="left"></div>
    <div class="rigtht"></div>
    <div class="bottom"></div>
  </div>
</template>

<script>
// mixins
import nodeThemeMixins from "./nodeThemeMixins";
export default {
  name: "http",
  mixins: [nodeThemeMixins],
  inject: ["getGraph", "getNode"],
  data() {
      return {
          node: {},
          label: "",
      };
  },
  mounted() {
      const self = this;
      this.node = this.getNode();
      this.label = this.node.data.label;
      // 监听数据改变事件
      this.node.on("change:data", ({ current }) => {
          self.label = current.label;
      });
  },
  methods: {
      removeNode() {
          this.getGraph().removeNode(this.node);
          this.$EventBus.$emit("removeNode");
      },
  },
};
</script>
<style lang="scss" src="./common.scss" scoped></style>
<style lang="scss" scoped>
.node {
  width: 100%;
  height: 100%;
  background: rgb(202 223 255 / 30%);
  border: 2px solid #fff;
  padding: 0;
  position: relative;
  .left{
    position: absolute;
    background: #fff;
    width: 10px;
    bottom: 0;
    top: 40px;
    left: 0;
  }
  .rigtht{
    position: absolute;
    background: #fff;
    width: 10px;
    bottom: 0;
    top: 40px;
    right: 0;
  }
  .bottom{
    position: absolute;
    background: #fff;
    height: 10px;
    bottom: 0;
    width: 100%;
  }
}
::v-deep(.head-tool) {
    margin-bottom: 9px;
    background: #fff;
    padding: 10px;
}
</style>