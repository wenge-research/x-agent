<template>
    <div class="node" :id="node.id">
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
                imgSuffix="workflow-damoxing"
            />
            <div v-show="showSub">
                <div class="model flex items-center justify-between">
                    <div class="model-label">大模型</div>
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
                        <div v-else class="no-config">未配置输入</div>
                    </div>
                </div>
                <div class="model flex items-center justify-between">
                  <div class="model-label" style=" height: 48px; ">输出</div>
                  <div class="w-full h-full items-center flex-1" style=" line-height: 24px; ">
                      <div class="result-cont">
                        text<span>string</span>
                      </div>
                      <div class="result-cont">
                        reasoningContent<span>string</span>
                      </div>
                  </div>
              </div>
                <ul class="type-list" v-if="appForm.systemPrompt">
                    <li>
                        <div class="name">系统提示词</div>
                        <div class="content" :title="appForm.systemPrompt">
                            {{ appForm.systemPrompt }}
                        </div>
                    </li>
                </ul>
                <ul class="type-list" v-if="appForm.userPrompt">
                    <li>
                        <div class="name">用户提示词</div>
                        <div class="content" :title="appForm.userPrompt">
                            {{ appForm.userPrompt }}
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <!-- <i class="el-icon-error" @click.stop="removeNode"></i> -->
    </div>
</template>

<script>
// mixins
import nodeThemeMixins from "./nodeThemeMixins";
export default {
    name: "model",
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
