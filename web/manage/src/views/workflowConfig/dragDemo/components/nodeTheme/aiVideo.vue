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
              imgSuffix="workfolw-shipinshengcheng"
          />
          <div v-show="showSub">
            <div class="model flex items-center justify-between">
                    <div class="model-label">模型</div>
                    <div class="model-right flex items-center">
                        <div
                            class="model-right-box flex items-center"
                            v-if="appForm.modelId"
                        >
                            <img :src="chatGptIdListNew[appForm.modelInfo.manufacturer]" />
                            {{ appForm.modelInfo.modelName }}
                        </div>
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
                                <span>
                                    {{ el.label }}
                                </span>
                            </li>
                        </ul>
                        <div v-else class="no-config"><iconpark-icon name="alert-fill" color="#FF9A2F"></iconpark-icon>未定义</div>
                    </div>
                </div>
                <div class="model flex justify-between">
                  <div class="model-label">输出</div>
                  <div class="w-full h-full flex-1 flex" style=" line-height: 24px; ">
                      <div class="result-content">
                        <iconpark-icon name="string"></iconpark-icon><span>result</span>
                      </div>
                      <!-- <div class="result-content">
                        <iconpark-icon :name="getIcon(el.type)"></iconpark-icon><span>content.video_url</span>
                      </div> -->
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
  name: "aiVideo",
  inject: ["getGraph", "getNode"],
  mixins: [nodeThemeMixins],
  data() {
      return {};
  },
  mounted() {},
  methods: {},
};
</script>
<style lang="scss" src="./common.scss" scoped></style>
<style lang="scss" scoped>
  .no-config{
    background: #FFF7E7;
    padding: 0 8px;
    border-radius: 2px;
    height: 20px;
    display: flex;
    align-items: center;
    gap: 2px;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 12px;
    color: #FF7C00;
  }
  .result-content{
    display: flex;
    background: #F7F8FA;
    border-radius: 2px;
    padding:0 5px;
    margin-right: 8px;
    height: 20px;
    align-items: center;
    gap: 2px;

    span{
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #1D2129;
      display: inline-block;
      height: 16px;
      line-height: 16px;
    }
  }
</style>