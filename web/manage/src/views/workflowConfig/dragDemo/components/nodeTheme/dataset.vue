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
              imgSuffix="workflow-shujuku"
          />
          <div v-show="showSub">
              <div class="model flex justify-between mb8">
                  <div class="model-label">数据集</div>
                  <div class="model-right flex items-center">
                      <ul
                          class="card-list flex items-center flex-wrap"
                          v-if="
                              appForm.collectIdList &&
                              appForm.collectIdList.length
                          "
                      >
                          <li
                              v-for="(el, index) in selectedList"
                              :key="index"
                              class="name flex items-center"
                              :title="el.name"
                          >
                              <img src="@/assets/images/sjk-icon1.png" v-if="el.type==1"/>
                              <img src="@/assets/images/sjk-icon2.png" v-else/>
                              <span class="sub-name" :title="el.name">{{
                                el.name
                            }}</span>
                          </li>
                      </ul>
                  </div>
              </div>
              <div class="model flex justify-between mb8">
                  <div class="model-label">输入</div>
                  <div class="model-right flex items-center">
                      <ul
                          class="card-list flex items-center flex-wrap"
                          v-if="appForm.inputs && appForm.inputs.length"
                      >
                          <li
                              v-for="(el, index) in appForm.inputs"
                              :key="index"
                              class="name flex items-center"
                              :title="el.label"
                          >
                              <iconpark-icon
                                  :name="getIcon(el.type)"
                                  class="icon"
                                  size="14"
                              ></iconpark-icon>
                              <span> Query </span>
                          </li>
                      </ul>
                      <div v-else class="no-config">未配置输入</div>
                  </div>
              </div>
              <div class="model flex items-center justify-between">
                  <div class="model-label">输出</div>
                  <div class="w-full h-full flex items-center flex-1">
                      <div
                          v-if="false"
                          class="model-right flex items-center"
                          :class="['model-quote']"
                      >
                          <div class="model-right-box w-full h-full">
                              <iconpark-icon
                                  v-if="false"
                                  name="Parameter-error"
                                  size="14"
                              ></iconpark-icon>
                              <iconpark-icon
                                  v-else
                                  :name="getIcon(el.type)"
                                  size="14"
                              ></iconpark-icon>
                              {{ chatGptNameList[modelId] }}
                          </div>
                      </div>
                      <div v-else class="no-config">
                          <div class="result-cont">
                            result<span>array[string]</span>
                          </div>
                      </div>
                  </div>
              </div>
          </div>
      </div>
  </div>
</template>

<script>
// mixins
import nodeThemeMixins from "./nodeThemeMixins";
import { getDataCollectInfoList } from "@/api/dataSj";

export default {
  name: "dataset",
  inject: ["getGraph", "getNode"],
  mixins: [nodeThemeMixins],
  data() {
      return {
          node: {},
          appForm: {},
          datasetList: [],
          selectedList: [],
          label: "",
      };
  },
  mounted() {
    this.knowledgeAllList();
  },
  methods: {
      init() {
          const self = this;
          this.appForm = this.node.data.appForm
              ? JSON.parse(this.node.data.appForm)
              : {};
          if (this.appForm.collectIdList) {
            //   let ids = this.appForm.collectIdList.map(({collectId}) => collectId)
              this.selectedList = this.datasetList.filter((el) =>
                this.appForm.collectIdList.includes(el.collectId)
              );
              
          }
          // 监听数据改变事件
          this.node.on("change:data", ({ current }) => {
              self.label = current.label;
              if (current.appForm) {
                  self.appForm = current.appForm
                      ? JSON.parse(current.appForm)
                      : {};
                  if (self.appForm.collectIdList) {
                    //   let ids = self.appForm.collectIdList.map(({collectId}) => collectId)
                      self.selectedList = self.datasetList.filter((el) =>
                        self.appForm.collectIdList.includes(el.collectId)
                      );
                  }
              }
          });
      },
      knowledgeAllList() {
          getDataCollectInfoList({
              pageNo: 1,
              pageSize: 99999,
          }).then((res) => {
              if (res.code == "000000") {
                  this.datasetList = res.data?.records;
                  this.init();
              } else {
                  this.datasetList = [];
              }
          });
      },
  },
};
</script>
<style lang="scss" src="./common.scss" scoped></style>
