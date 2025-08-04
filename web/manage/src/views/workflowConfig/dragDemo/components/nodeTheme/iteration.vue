<template>
    <div class="node" :id="node.id">
        <RunningResult
            v-show="showResult" :outputJsonData="outputJsonData"
            :inputList="appForm.inputs"
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
                @detail="detailHandler"
                @input="inputChange"
                imgSuffix="workflow-gongzuoliu"
            />
            <div v-show="showSub">
                <ul class="type-list">
                    <li>
                        <div class="content" :title="toolData.componentName">
                            {{ toolData.componentName }}
                        </div>
                    </li>
                </ul>

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
                      
                        <div class="result-cont">
                            result
                            <span v-if="appForm && appForm.selectOutput">{{appForm.selectOutput.type}}</span>
                            <span v-else>array[string]</span>
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
        };
    },
    mounted() {
    },
    methods: {
      
    },
};
</script>
<style lang="scss" src="./common.scss" scoped></style>
<style lang="scss">
.know-list {
    li {
        background: #f0f4f9;
        margin: 6px 0;
        padding: 10px 10px 10px 40px;
        line-height: 20px;
        border-radius: 4px;
        position: relative;
        img {
            position: absolute;
            left: 10px;
            width: 20px;
        }
    }

    .name {
        font-size: 14px;
        height: 18px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        color: #333;
    }
}
</style>
