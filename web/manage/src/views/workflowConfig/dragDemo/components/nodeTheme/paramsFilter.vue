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
                imgSuffix="canshutiqu"
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
                                    {{ el.label ? el.label : defaultLabel }}
                                </span>
                            </li>
                        </ul>
                        <div v-else class="no-config">未配置输入</div>
                    </div>
                </div>
                <!-- <i class="el-icon-error" @click.stop="removeNode"></i> -->
                <ul class="type-list">
                    <li v-for="(el, index) in filterVariables" :key="index">
                        <div class="name">{{ el.name }}</div>
                        <div class="content2" :title="el.description">
                            {{ el.description }}
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
// mixins
import nodeThemeMixins from "./nodeThemeMixins";
export default {
    name: "paramsFilter",

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
