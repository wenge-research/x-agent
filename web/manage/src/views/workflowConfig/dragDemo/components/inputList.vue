<template>
    <div class="input-list-cont">
        <div class="input-list" v-for="(item, itemIndex) in inputList" :class="{'has-chname' : item.defaultChName}"  :key="itemIndex">
            <div class="input-item" style="width: 165px;">
                <div class="tip" v-if="itemIndex === 0">{{defalutFirstVal}}</div>
                <div class="name" v-if="single && defalutVal">
                    <div class="name-text" v-if="!isTooltip">{{ item.label ? item.label : defalutVal }}</div>
                    <div class="name-text" v-else>
                        <div class="title-flex">
                            <span>{{ item.label ? item.label : defalutVal }}</span>
                            <div class="icon-ctn" v-if="workFlowDubunce[item.label]">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="workFlowDubunce[item.label]" placement="top" :effect="'light'">
                                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                                </el-tooltip>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else>
                  <div v-if="item.defaultChName"  class="ch-text">
                    {{item.defaultChName}} : {{item.label}}
                  </div>
                  <div v-else>
                    <el-input size="small" v-model="item.label" :placeholder="$t('inputValue')" style="width: 160px"
                      @input="handleInput($event, itemIndex)" @blur="inputBlur"></el-input>
                    <div class="name-tip" v-if="single && defalutValTip">{{ defalutValTip }}</div>
                  </div>
                </div>
            </div>
            <div class="input-item" style="width: 90px;">
                <div class="tip" v-if="itemIndex === 0">值类型</div>
                <el-select v-model="item.inputType" @change="onChangeInputType($event, itemIndex)" size="small"
                    :placeholder="$t('selectPlaceholder')" style="width: 90px">
                    <el-option label="引用值" :value="1"> </el-option>
                    <el-option label="自定义" :value="2"> </el-option>
                </el-select>
            </div>
            <div class="input-item" style="width: 140px;">
                <div class="tip" v-if="itemIndex === 0"></div>
                <connectVar :parentNodes="parentNodes" :key="selectKey" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData" :variables="inputs"
                    :index="itemIndex" :value="item.value" style="width: 140px;"
                    @change="onChangeVoidValue($event, itemIndex, item)" v-if="inputList && item.inputType === 1">
                </connectVar>
                <el-input v-else size="small" v-model="item.cusInput" :placeholder="$t('inputValue')"
                    style="width: 140px" @blur="inputBlur"></el-input>
            </div>
            <div class="input-item">
                <div class="tip" v-if="itemIndex === 0"></div>
                <el-button @click="removeConditionItem(itemIndex)" type="text" icon="el-icon-delete" v-if="!single"
                    style="color: #676f83"></el-button>
            </div>
        </div>
        <span @click="addConditionItem()" v-if="!single" class="add-btn"
            style="margin: 10px auto; width: max-content"><iconpark-icon name="add-line"></iconpark-icon></span>
    </div>
</template>

<script>
// mixins
import connectVar from "./connectVar";

export default {
    name: "CustomDrawer",
    props: {
        single: {
            type: Boolean,
            default: false,
        },
        defalutFirstVal: {
            type: String,
            default: '变量名',
        },
        defalutVal: {
            type: String,
            default: '',
        },
        defalutType: {
            type: String,
            default: '',
        },
        defalutValTip: {
            type: String,
            default: '',
        },
        parentNodes: Array,
        curParentNodeInputs: Array,
        curParentNodesData: Object,
        inputs: Array,
        isTooltip:{
            type:Boolean,
            default:false
        }
    },
    components: { connectVar },
    data() {
        return {
            selectKey:1,
            inputList: [{
                inputType: 1,
                selectedGroup: "",
                cusInput: "",
                lan: "",
                label: this.defalutVal ? this.defalutVal : '',
                value: "",
                type: this.defalutType ? this.defalutType : '',
            }],
            startInputList: [{
                inputType: 1,
                selectedGroup: "",
                cusInput: "",
                label: this.defalutVal ? this.defalutVal : '',
                lan: "",
                value: "",
                type: this.defalutType ? this.defalutType : '',
            }],
            workFlowDubunce:{
                task_id:"推演任务的唯一标识",
                max_epochs:"推演的最大轮次",
                initial_inputs:"推演各主体的初始输入内容",
                agent_configs:"参与推演的主体配置，如每个参与方的角色、策略、权限等",
                interaction_graph:"主体间的交互关系图",
                source_initial_request:"推演源行动方发起的请求"
            }
        };
    },
    mounted() {
        this.inputList = this.inputs && this.inputs.length ? this.inputs : this.startInputList;
    },
    watch: {
        // 监听 variables 数组的变化
        inputList: {
            handler(newVal, oldVal) {
                // 当 variables 发生变化时，通知父组件更新
                this.$emit(
                    "updateInputList",
                    { inputs: JSON.stringify(newVal) }
                );
                this.$emit("update:inputs",newVal)
                this.selectKey++;
            },
            deep: true,
        },
    },
    methods: {
        handleInput(value, itemIndex) {
            // 使用正则表达式过滤非法字符
            //   this.inputList[itemIndex].label = value.replace(/[^a-zA-Z0-9]/g, '');
            // 如果以数字开头，清空输入
            // 如果包含中文字符，清空输入并提示
            if (/[\u4e00-\u9fa5]/.test(value)) {
                this.$message({
                    type: "warning",
                    message: "变量名不支持输入中文字符串",
                });
                this.inputList[itemIndex].label = value.replace(/[^a-zA-Z0-9]/g, '');
            }
            if (/^\d/.test(this.inputList[itemIndex].label)) {
                this.$message({
                    type: "warning",
                    message: "变量名首字符不支持数字格式",
                });
                this.inputList[itemIndex].label = '';
            }
        },
        inputBlur() {
            //this.$EventBus.$emit("saveWorkflow");
        },
        onChangeInputType(data, itemIndex) {
            this.inputList[itemIndex].cusInput = '';
            this.inputList[itemIndex].selectedGroup = '';
            this.inputList[itemIndex].type = data === 1 ? '' : 'string'
            //this.$EventBus.$emit("saveWorkflow");
        },
        onChangeVoidValue(data, itemIndex, item) {
            if (!item.label) {
                this.inputList[itemIndex].label = data.value;
            }
            this.inputList[itemIndex].value = data.value;
            this.inputList[itemIndex].selectedGroup = data.selectedGroup;
            this.inputList[itemIndex].type = data.type;
            //this.$EventBus.$emit("saveWorkflow");
        },

        addConditionItem() {
            this.inputList.push({
                inputType: 1,
                selectedGroup: "",
                cusInput: "",
                label: "",
                lan: "",
                value: "",
                type: "",
            });
            this.$EventBus.$emit("saveWorkflow");
        },
        removeConditionItem(itemIndex) {
            this.inputList.splice(itemIndex, 1);
            this.$EventBus.$emit("saveWorkflow");
        },
        handleClose() {
            this.drawerVisible = false;
        },
    },
};
</script>

<style scoped lang="scss">
.input-list-cont {
    position: relative;
    min-height: 20px;

    .add-btn {
        position: absolute;
        right: -1px;
        top: -10px;
        cursor: pointer;
        font-size: 18px;
    }
}

.input-list {
    display: flex;
    align-items: center;
    &.has-chname{
      margin-bottom: 6px;
    }
    .input-item {
        width: 120px;
        margin-right: 10px;

        .tip {
            font-size: 12px;
            color: #828894;
            height: 24px;
        }

        .name-tip {
            font-size: 12px;
            color: #828894;
            height: 24px;
            width: 160px;
            position: absolute;
        }

        .name {
            width: 160px;
            height: 34px;

            .name-text {
                position: relative;
                top: 2px;
            }
        }
    }
}
.ch-text{
  width: 150px;
  height: 34px;
  line-height: 34px;
}
.title-flex{
  display: flex;
  align-items: center;
  height: 40px;

  span{
    display: inline-block;
    height: 20px;
    line-height: 20px;
  }

  .icon-ctn{
    height: 16px;
    line-height: 19px;
    position: relative;

  }
}
</style>
