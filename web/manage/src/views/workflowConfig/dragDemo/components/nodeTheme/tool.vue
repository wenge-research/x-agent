<template>
    <div class="node" :id="node.id">
        <!-- <div class="tit">
            <img src="@/assets/svg/gongzuoliujiedian-bianliang.svg" />
            <span class="label">{{ label }}</span>
        </div> -->
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
                imgSuffix="gongjujiedian"
            />
            <div v-show="showSub">
                <ul class="type-list" v-if="!toolData.hideToolName">
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
                        {{ item.label }}<span>{{ item.type }}</span>
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
        return {};
    },
    mounted() {},
    methods: {},
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
.result {
    position: relative;
    left: 66px;
    bottom: 17px;
    margin: 0 0 8px 0;
    span {
        position: relative;
        color: #676f83;
        left: 8px;
        font-size: 12px;
    }
}
</style>
