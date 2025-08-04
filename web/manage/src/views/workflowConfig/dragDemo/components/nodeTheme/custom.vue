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
                imgSuffix="workflow-zidingyi"
            />
            <div v-show="showSub">
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
                    <div class="model-label">输出</div>
                    <div class="w-full h-full flex items-center flex-1">
                        <div
                            v-if="appForm.output"
                            style=" line-height: 22px; "
                        >
                          <div class="result-cont" v-for="item in appForm.output">
                            {{ item.label }}<span>{{ item.type }}</span>
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
    mounted() {},
    methods: {},
};
</script>
<style lang="scss" src="./common.scss" scoped></style>