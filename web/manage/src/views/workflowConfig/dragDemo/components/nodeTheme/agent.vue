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
                <div class="model flex justify-between mb8">
                  <div class="model-label">场景</div>
                    <div class="model-right flex items-center sence">
                      <div v-if="appForm && appForm.sence" v-html="appForm.sence.replace(/\n/g, '<br>')"></div>
                    </div>
                </div>
                <div class="model flex justify-between mb8"  v-if="
                appForm.knnIdList &&
                appForm.knnIdList.length
            ">
                  <div class="model-label">知识库</div>
                  <div class="model-right flex items-center">
                      <ul
                          class="card-list flex items-center flex-wrap"
                         
                      >
                          <li
                              v-for="(el, index) in appForm.knnIdList"
                              :key="index"
                              class="name flex items-center"
                              :title="el.knowledgeName"
                          >
                              <img src="@/assets/svg/zhishiku-moren.svg" />
                              <span class="sub-name" :title="el.knowledgeName">{{
                                el.knowledgeName
                            }}</span>
                          </li>
                      </ul>
                  </div>
              </div>
              <div class="model flex justify-between mb8"  v-if="
              appForm.knnIdList &&
              appForm.knnIdList.length
          ">
                <div class="model-label">MCP</div>
                <div class="model-right flex items-center">
                    <ul
                        class="card-list flex items-center flex-wrap"
                        v-if="appForm.mcpIdList && appForm.mcpIdList.length"
                        :id="node.id"
                    >
                        <li
                            v-for="(el, index) in appForm.mcpIdList"
                            :key="index"
                            class="name flex items-center"
                            :title="el.mcpName"
                        >
                            {{ el.mcpName }}
                        </li>
                    </ul>
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
