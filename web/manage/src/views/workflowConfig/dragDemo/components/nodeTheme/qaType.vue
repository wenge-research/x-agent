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
                imgSuffix="wentifenlei"
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
                <ul class="qaType-list">
                    <li
                        v-for="(el, index) in configurationList"
                        :key="index"
                        class="flex"
                    >
                        <div class="name">分类{{ numberToChinese(index + 1) }}</div>
                        <div class="content" :title="el.content">
                            {{ el.content }}
                        </div>
                    </li>
                </ul>
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
    name: "ConditionCom",
    inject: ["getGraph", "getNode"],
    mixins: [nodeThemeMixins],
    data() {
        return {};
    },
    computed: {},
    mounted() {},
    methods: {
      numberToChinese(num) {
            const chineseNums = [
                "零",
                "一",
                "二",
                "三",
                "四",
                "五",
                "六",
                "七",
                "八",
                "九",
            ];
            let result = "";
            for (let i = 0; i < num.toString().length; i++) {
                result += chineseNums[num.toString()[i]];
            }
            return result;
        },
    },
};
</script>
<style lang="scss" src="./common.scss" scoped></style>
<style lang="scss" scoped>
.qaType-list {
    padding-bottom: 8px;
    li {
        margin-bottom: 8px;
        line-height: 20px;
        border-radius: 4px;
    }
    .name {
        width: 50px;
        font-size: 12px;
        color: #383d47;
        margin-right: 14px;
    }
    .content {
        flex: 1;
        padding: 2px 4px;
        background: #f0f4fa;
        border-radius: 4px;
        font-size: 12px;
        color: #383d47;
        line-height: 16px;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2; /* 控制显示的行数 */
        overflow: hidden;
        text-overflow: ellipsis;
        height: 36px;
    }
}
</style>
