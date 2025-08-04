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
                imgSuffix="workfolw-anquanlanjie"
            />
            <div v-show="showSub">
                <div class="model flex justify-between mb8">
                    <div class="model-label">词库</div>
                    <div class="model-right flex items-center">
                        <ul
                            class="card-list flex items-center flex-wrap"
                            v-if="configurations && configurations.length"
                            :id="node.id"
                        >
                            <li
                                v-for="(el, index) in configurations"
                                :key="index"
                                class="name flex items-center"
                                :title="el.name"
                            >
                                {{ el.name }}
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
                                <span>
                                    {{ el.label ? el.label : defaultLabel }}
                                </span>
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
                                result<span>array[object]</span>
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
export default {
    name: "interceptWord",
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
