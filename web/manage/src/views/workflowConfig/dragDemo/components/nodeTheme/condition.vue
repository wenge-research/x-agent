<template>
    <div class="node">
        <!-- <div class="tit">
            <img src="@/assets/svg/gongzuoliujiedian-tiaojianfenzhi.svg" />
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
            :show="isRunning"
			:isShowBtn="isRunningBtn" :isRunningText="isRunningText"
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
                imgSuffix="tiaojianfenzhi"
            />
            <div v-show="showSub">
                <ul class="condition-list">
                    <li
                        v-for="(el, index) in variables"
                        :key="index"
                        class="condition-list-item"
                    >
                        <div class="name">
                            <span class="logicName">
                                {{ el.logicName }}
                            </span>
                        </div>
                        <ul class="sub-list">
                            <li
                                v-for="(item, i) in el.conditions"
                                :key="i"
                                class="flex items-center"
                            >
                                <div
                                    class="left"
                                    :title="
                                        item.left +
                                        operators[item.operator] +
                                        item.right
                                    "
                                >
                                    {{ item.left }}
                                    <span v-if="item.left">{{
                                        operators[item.operator]
                                    }}</span>

                                    {{ item.right }}
                                </div>
                                <b
                                    v-if="i + 1 < el.conditions.length"
                                    class="flex items-center"
                                >
                                    {{ el.tag ? "AND" : "OR" }}
                                    <iconpark-icon
                                        name="arrow-right-s-fill"
                                    ></iconpark-icon>
                                </b>
                            </li>
                        </ul>
                    </li>
                    <li class="condition-list-item">
                        <div class="name">否则</div>
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
    name: "ConditionCom",
    inject: ["getGraph", "getNode"],
    mixins: [nodeThemeMixins],
    data() {
        return {
            node: {},
            variables: {},
            label: "",
            startCases: [
                {
                    logicName: "如果",
                    tag: true,
                    caseId: 1,
                    conditions: [
                        {
                            left: "",
                            operator: "contains",
                            right: "",
                        },
                    ],
                },
            ],
        };
    },
    computed: {
        operators() {
            return {
                contains: "包含",
                not_contains: "不包含",
                start_with: "开始是",
                end_with: "结束是",
                match: "是",
                not_match: "不是",
                not_blank: "不为空",
                is_blank: "为空",
            };
        },
    },
    mounted() {
        const self = this;
        this.variables = this.node.data.variables
            ? JSON.parse(this.node.data.variables)
            : this.startCases;

        // 监听数据改变事件
        this.node.on("change:data", ({ current }) => {
            self.label = current.label;
            self.variables = current.variables
                ? JSON.parse(current.variables)
                : [];
        });
    },
    methods: {
        removeNode() {
            this.getGraph().removeNode(this.node);
            this.$EventBus.$emit("removeNode");
        },
    },
};
</script>
<style lang="scss" src="./common.scss" scoped></style>
<style lang="scss">
.condition-list {
    padding-bottom: 16px;
    .condition-list-item {
        background: #f9fafb;
        margin: 6px 0 0;
        padding: 4px 4px 2px 60px;
        line-height: 20px;
        border-radius: 4px;
        position: relative;
        min-height: 36px;
        img {
            position: absolute;
            left: 10px;
            width: 20px;
        }
    }

    .name {
        font-size: 14px;
        color: #383d47;
        left: 10px;
        position: absolute;
        top: 8px;
        // b {
        //   position: absolute;
        //   top: 20px;
        //   width: 100%;
        //   text-align: left;
        //   left: 0;
        // }
    }
    .sub-list {
        li {
            margin: 4px 0 6px 0;

            .left {
                flex: 1;
                background: #f0f4fa;
                padding: 0 6px;
                height: 20px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                border-radius: 4px;
                margin-right: 4px;
            }
            b {
                height: 20px;
                padding-left: 4px;
                background: #f0f4fa;
                border-radius: 4px;
                margin-left: auto;
            }
            span {
                margin: 0 6x;
            }
        }
    }
}
</style>
