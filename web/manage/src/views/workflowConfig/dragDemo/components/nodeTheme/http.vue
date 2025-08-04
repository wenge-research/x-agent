<template>
  <div class="node" :id="node.id">
      <RunningResult v-show="showResult" :inputList="appForm.inputs" :outputJsonData="outputJsonData" @closeRunningResult="handleCloseResult" />
      <RunningState ref="RunningState" :isRunningText="isRunningText" :isShowBtn="isRunningBtn" :show="isRunning" @openResultHandler="handleOpenResult" />
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
          imgSuffix="disanfangjiekou"
        />
        <div class="model flex justify-between mb8">
          <div class="model-label">输入</div>
          <div class="model-right flex items-center">
              <ul
                  class="card-list flex items-center flex-wrap"
                  v-if="
                      startNodevariables && startNodevariables.length
                  "
              >
                  <li
                      v-for="(el, index) in startNodevariables"
                      :key="index"
                      class="name flex items-center"
                      :title="el[0] ? el[0].label : ''"
                      v-if="el[0]"
                  >
                      <iconpark-icon
                          :name="getIcon(el.type)"
                          class="icon"
                          size="14"
                      ></iconpark-icon>
                      <span>
                          {{ el[0] ? el[0].label : "" }}
                      </span>
                  </li>
              </ul>
              <div v-else class="no-config">未配置输入</div>
          </div>
      </div>
      <div style="margin-bottom: 10px">
          <div class="model-label">输出</div>
          <div class="result" v-for="item in appForm.endNode">
              {{ item.name }}<span>{{ item.type }}</span>
          </div>
      </div>
    </div>
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

  methods: {
      removeNode() {
          this.getGraph().removeNode(this.node);
          this.$EventBus.$emit("removeNode");
      },
  },
};
</script>
<style lang="scss" src="./common.scss" scoped></style>