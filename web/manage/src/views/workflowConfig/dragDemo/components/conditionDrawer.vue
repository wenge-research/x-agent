<template>
    <div class="Ec-x6-icon">
        <el-drawer
            title=""
            :visible.sync="drawerVisible"
            :modal="false"
            :modal-append-to-body="false"
            :direction="direction"
            v-if="drawerVisible"
            size="500px"
            style="
                position: absolute;
                width: 500px;
                box-sizing: border-box;
                right: 0;
                left: inherit;
            "
            :show-close="false"
            :before-close="handleClose"
        >
            <!-- <div class="drawer-header">
                <img src="@/assets/svg/gongzuoliujiedian-kaishi.svg" />
                <span>{{$t('conditionNode')}}</span>
                <i
                    class="el-icon-close custom-close-icon"
                    @click="closeDrawer"
                ></i>
                <div class="sub">{{$t('conditionNodeDefineConditions')}}</div>
            </div> -->
            <div style="display: flex;align-items: center;justify-content: space-between">
                <HeadTool
                  v-if="drawerVisible"
                  :label="sourceData.label"
                  :imgWidth="24"
                  :imgHeight="24"
                  @remove="handleRemove"
                  @copy="copyHandler"
                  @input="inputHandler"
                  @testNodes="openRunDrawer"
                  imgSuffix="tiaojianfenzhi"
                />
                <div style="width: 1px;height: 20px;background: #D3D9E6;margin: 0 12px 12px;"></div>
                <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer" style="margin-bottom: 10px;cursor: pointer"></iconpark-icon>
            </div>
            <div class="sub">若设定的条件成立则运行对应的分支，不成立则运行“否则“分支</div>
            <div class="drawer-content">
                <div
                    v-for="(logic, logicIndex) in cases"
                    :key="logicIndex"
                    class="condition-logic"
                >
                    <span class="tit"
                        >{{ logic.logicName }}
                        <span>
                            （{{ logic.tag ? "AND" : "OR"
                            }}<i
                                class="el-icon-refresh"
                                @click="logic.tag = !logic.tag"
                            ></i
                            >）
                        </span>
                    </span>
                    <div
                        v-for="(item, itemIndex) in logic.conditions"
                        :key="itemIndex"
                        class="condition-item"
                    >
                        <connectVar :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData" :variables="logic.conditions" :valueName="'left'"
                        :index="itemIndex" :value="item.left"  v-if="drawerVisible" @change="onChangeVoidValue($event, logicIndex, itemIndex)"  ></connectVar>
                        <el-select
                            @change="onChange"
                            v-model="item.operator"
                            :placeholder="$t('selectOperator')"
                            size="small"
                            style="width: 200px"
                        >
                            <el-option
                                v-for="operator in getOperators(item.type)"
                                :key="operator.value"
                                :label="operator.label"
                                :value="operator.value"
                            >
                            </el-option>
                        </el-select>
                        <el-input
                            v-model="item.right"
                            size="small"
                            :placeholder="$t('inputValue')"
                            style="width: 200px"
                            @blur="inputBlur"
                        ></el-input>
                        <el-button
                            @click="removeConditionItem(logicIndex, itemIndex)"
                            type="text"
                            icon="el-icon-delete"
                            style="color: #676f83"
                        ></el-button>
                    </div>
                    <el-button
                        @click="addConditionItem(logicIndex)"
                        type="primary"
                        style="margin: 10px auto; width: max-content"
                        >{{$t('addCondition')}}</el-button
                    >
                    <el-button
                        v-if="logicIndex !== 0 && cases.length > 1"
                        @click="removeLogic(logicIndex)"
                        type="text"
                        icon="el-icon-delete"
                        class="delete"
                        style="color: #676f83"
                    ></el-button>
                </div>
                <el-button
                    @click="addLogic()"
                    type="primary"
                    style="margin-top: 10px; width: max-content"
                    >{{$t('addElseIf')}}</el-button
                >
            </div>
        </el-drawer>
    </div>
</template>

<script>
// mixins
import drawerMixins from './drawerMixins'
import connectVar from "./connectVar";

export default {
    name: "CustomDrawer",
    props: ["panels", "graph"],
    components: {connectVar},
    mixins: [drawerMixins],
    data() {
        return {
            drawerVisible: false,
            direction: "rtl",
            curNode: {},
            size: "100%",
            drawerVisible: false,
            cases: [
                {
                    logicName: "如果",
                    tag: true,
                    caseId:1,
                    conditions: [
                        {
                            left: "",
                            operator: "",
                            right: "",
                        },
                    ],
                },
            ],
            startCases: [
                {
                    logicName: "如果",
                    tag: true,
                    caseId:1,
                    conditions: [
                        {
                            left: "",
                            operator: "",
                            right: "",
                        },
                    ],
                },
            ],
           
            parentNodes:[],
            operators: [
                { value: "contains", label: "包含"},
                { value: "not_contains", label: "不包含" },
                { value: "start_with", label: "开始是" },
                { value: "end_with", label:  "结束是" },
                { value: "match", label: "是" },
                { value: "not_match", label: "不是" },
                { value: "not_blank", label:"不为空" },
                { value: "is_blank", label: "为空" },
            ],
            intOperators: [
                { value: "greater", label: ">"},
                { value: "greater_or_equal", label: "≥" },
                { value: "less", label: "<" },
                { value: "less_or_equal", label:  "≤" },
                { value: "equal", label: "=" },
                { value: "not_equal", label: "≠" },
                { value: "is_null", label:"为空" },
                { value: "not_null", label: "不为空" },
            ],
            arrayOperators: [
                { value: "array_contains", label: "包含"},
                { value: "array_not_contains", label: "不包含" },
                { value: "match", label: "是" },
                { value: "array_is_empty", label: "为空" },
                { value: "array_is_not_empty", label:  "不为空" },
            ],
        };
    },
    mounted() {
    },
    watch: {
      // 监听 variables 数组的变化
      cases: {
        handler(newVal, oldVal) {
          // 当 variables 发生变化时，通知父组件更新
          this.$emit('updateConditionData', {variables:JSON.stringify(newVal)}, this.curNode);
        },
        deep: true
      }
    },
    methods: {
        getOperators(type) {
          if (type === 'number') {
            return this.intOperators;
          } else if (type === 'array[string]') {
            return this.arrayOperators;
          } else {
            return this.operators;
          }
        },
        inputBlur() {
            this.$EventBus.$emit("saveWorkflow");
        },
        onChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        onChangeVoidValue(data, logicIndex, itemIndex) {
            this.cases[logicIndex].conditions[itemIndex].left = data.value;
            this.cases[logicIndex].conditions[itemIndex].value = data.value;
            this.cases[logicIndex].conditions[itemIndex].selectedGroup = data.selectedGroup;
            this.cases[logicIndex].conditions[itemIndex].type = data.type;
            this.$EventBus.$emit("saveWorkflow");
        },
        openDrawer() {
            let node = this.$store.state.workflow.editNode;
            let parentNodes = this.$store.state.workflow.parentNodes;
            let nodeStoreData = node.store.data.data;
            this.drawerVisible = true;
            this.curNode = node;
            this.parentNodes = parentNodes;
            this.cases = nodeStoreData.variables ? JSON.parse(nodeStoreData.variables) : this.startCases;
            this.cases[0].caseId = `out-${node.store.data.time}-if`
            this.cases[0].sourceNodeId = node.id
        },
        closeDrawer() {
            this.drawerVisible = false;
        },
        addLogic() {
            const time = new Date().getTime();
            let caseId = `out-${time}-elseif`
            const newLogic = {sourceNodeId:this.curNode.id, caseId:caseId, logicName: "否则如果", tag: true, conditions: [] };
            this.cases.push(newLogic);
            this.$emit("addPorts", this.cases, this.curNode);
            this.$EventBus.$emit("saveWorkflow");
        },

        removeLogic(index) {
            if (this.cases.length > 1) {
                if (index === 0) {
                    this.cases[1].logicName = "如果";
                }
                this.cases.splice(index, 1);
            }
            this.$emit("removePorts", this.cases, this.curNode, index);
            this.$EventBus.$emit("saveWorkflow");
        },
        addConditionItem(logicIndex) {
            this.cases[logicIndex].conditions.push({
                left: "",
                operator: "",
                right: "",
            });
            this.$EventBus.$emit("saveWorkflow");
        },
        removeConditionItem(logicIndex, itemIndex) {
            this.cases[logicIndex].conditions.splice(itemIndex, 1);
            this.$EventBus.$emit("saveWorkflow");
        },
        handleClose() {
            this.drawerVisible = false;
        },
    },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style scoped lang="scss">
.custom-icon {
    font-size: 24px;
    margin-right: 10px;
}

.custom-close-icon {
    font-size: 20px;
    cursor: pointer;
    color: #999;
}

.custom-close-icon:hover {
    color: #666;
}

.section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    font-size: 16px;
    color: #383d47;
}

.add-icon {
    cursor: pointer;
    color: #409eff;
}
.delete-icon {
    font-size: 16px;
    cursor: pointer;
    color: #828894;
    margin-left: 10px;
    position: absolute;
    top: 16px;
    right: 16px;
}

.delete-icon:hover {
    color: #e6a23c;
}
</style>
<style scoped lang="scss">
.condition-logic {
    margin-bottom: 10px;
    display: flex;
    flex-direction: column;
    border-bottom: 1px solid #eee;
    position: relative;
    .tit {
        margin-bottom: 10px;
        font-size: 18px;
        font-weight: bold;
        span {
            font-size: 14px;
            font-weight: normal;
            color: #296dff;
        }
        i {
            cursor: pointer;
            margin: 0 0 0 4px;
        }
    }
    .delete {
        position: absolute;
        right: 0;
        top: 0;
    }
}

.condition-item {
    display: flex;
    align-items: center;
    margin-bottom: 5px;
}

.condition-item .el-select,
.condition-item .el-input {
    margin-right: 10px;
}

.condition-item .el-button--circle {
    margin-left: 10px;
}

.el-button--circle {
    margin-top: 5px;
}

</style>
