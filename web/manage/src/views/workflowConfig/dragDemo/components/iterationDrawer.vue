<template>
  <div class="Ec-x6-icon">
    <el-drawer title="" :visible.sync="drawerVisible" :modal="false" :modal-append-to-body="false"
      :direction="direction" v-if="drawerVisible" size="500px" style="
                position: absolute;
                width: 500px;
                box-sizing: border-box;
                right: 0;
                left: inherit;
            " :show-close="false">
      <div style="
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                ">
        <HeadTool v-if="drawerVisible" :label="sourceData.label" :imgWidth="24" :imgHeight="24" @remove="handleRemove"
          @copy="copyHandler" @detail="detailHandler" @input="inputHandler" @testNodes="openRunDrawer"
          imgSuffix="workflow-gongzuoliu" />
        <div style="
                        width: 1px;
                        height: 20px;
                        background: #d3d9e6;
                        margin: 0 12px 12px;
                    "></div>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer"
          style="margin-bottom: 10px; cursor: pointer"></iconpark-icon>
      </div>
      <div class="sub">
        可引用当前工作流的输入输出
      </div>
      <div class="drawer-content">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="0">
            <template slot="title">
              <div class="title-flex">
                <span>{{ $t('inputVariable') }}</span>
                <div class="icon-ctn">
                  <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'输入工作流所需参数，节点运行时将自动传入这些参数以调用该工作流'"
                    placement="top" :effect="'light'">
                    <iconpark-icon name="question-line" size="16" color="#C9CDD4"
                      style="margin-left: 5px;"></iconpark-icon>
                  </el-tooltip>
                </div>
              </div>
            </template>
            <inputList v-if="drawerVisible" :inputs="appForm.inputs" :key="inputKey" @updateInputList="updateInputList"
              :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs"
              :curParentNodesData="curParentNodesData" :single="true" :defalutVal="'Query'"></inputList>
          </el-collapse-item>
          <el-collapse-item name="1">
            <template slot="title">
              <div class="title-flex">
                <span>{{ $t('output') }}</span>
                <div class="icon-ctn">
                  <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'可选择被引用的工作流中任一节点的输出变量'"
                    placement="top" :effect="'light'">
                    <iconpark-icon name="question-line" size="16" color="#C9CDD4"
                      style="margin-left: 5px;"></iconpark-icon>
                  </el-tooltip>
                </div>
              </div>
            </template>
            <!-- <el-form @submit.prevent style="margin: 0 0 0 0;">
                        <el-form-item label="是否同步">
                          <el-switch
                          size="small" 
                              v-model="appForm.asyncFlag"
                              :active-value="$t('yes')"
                              :inactive-value="$t('no')"
                          >
                          </el-switch>
                        </el-form-item>
                      </el-form> -->
            <div class="input-list-cont" :key="selectKey">
              <div class="input-list" v-for="(item, itemIndex) in outputList"
                :class="{'has-chname' : item.defaultChName}" :key="itemIndex">
                <div class="input-item" style="width: 165px;">
                  <div class="tip" v-if="itemIndex === 0">变量名</div>
                  <div>
                    <div>
                      <el-input size="small" v-model="item.label" :placeholder="$t('inputValue')" style="width: 160px"
                       ></el-input>
                    </div>
                  </div>
                </div>
                <div class="input-item" style="width: 90px;">
                  <div class="tip" v-if="itemIndex === 0">值类型</div>
                  <el-select v-model="item.inputType" @change="onChangeInputType($event, itemIndex)" size="small"
                    :placeholder="$t('selectPlaceholder')" style="width: 90px">
                    <el-option label="引用值" :value="1"> </el-option>
                    <!-- <el-option label="自定义" :value="2"> </el-option> -->
                  </el-select>
                </div>
                <div class="input-item" style="width: 140px;">
                  <div class="tip" v-if="itemIndex === 0"></div>
                  <el-select v-model="item.relateId" style="width: 140px;" v-if="outputList && item.inputType === 1"
                    size="small" :placeholder="$t('请选择选择变量')" @change="changeSelect($event,itemIndex)">
                    <el-option-group v-for="group in options" :key="group.label" :label="group.label"
                      v-if="group.options && group.options.length > 0">
                      <el-option v-for="item in group.options" :key="item.value" :label="item.label" :value="
                                                    group.id +
                                                    '_id_' +
                                                    item.value
                                                ">
                        <span style="float: left">{{
                          item.label
                          }}</span>
                        <span style="
                                                        float: right;
                                                        color: #8492a6;
                                                        font-size: 12px;
                                                    ">{{ item.type }}</span>
                      </el-option>
                    </el-option-group>
                  </el-select>
                  <el-input v-else size="small" v-model="item.cusInput" :placeholder="$t('inputValue')"
                    style="width: 140px" @blur="inputBlur"></el-input>
                </div>
                <div class="input-item">
                  <div class="tip" v-if="itemIndex === 0"></div>
                  <el-button @click="removeConditionItem(itemIndex)" type="text" icon="el-icon-delete"
                    style="color: #676f83"></el-button>
                </div>
              </div>
              <span @click="addConditionItem()" class="add-btn"
                style="margin: 10px auto; width: max-content"><iconpark-icon name="add-line"></iconpark-icon></span>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-drawer>
  </div>
</template>

<script>
  // mixins
  import drawerMixins from "./drawerMixins";
  import connectVar from "./connectVar";

  export default {
    name: "CustomDrawer",
    components: { connectVar },
    props: ["panels", "params"],
    mixins: [drawerMixins],
    data ()
    {
      return {
        selectKey: 1,
        outputList: [{
          inputType: 1,
          selectedGroup: "",
          cusInput: "",
          lan: "",
          label: '',
          value: "",
          relateId: '',
          type: '',
        }],
        startOutputList: [{
          inputType: 1,
          selectedGroup: "",
          cusInput: "",
          label: '',
          lan: "",
          value: "",
          relateId: '',
          type: '',
        }],
        workFlowDubunce: {
          task_id: "推演任务的唯一标识",
          max_epochs: "推演的最大轮次",
          initial_inputs: "推演各主体的初始输入内容",
          agent_configs: "参与推演的主体配置，如每个参与方的角色、策略、权限等",
          interaction_graph: "主体间的交互关系图",
          source_initial_request: "推演源行动方发起的请求"
        },
        appForm: {
          selectOutput: null,
          asyncFlag: this.$t('no'),
          outputs: [],
        },
        startAppForm: {
          selectOutput: null,
          output: [],
          asyncFlag: this.$t('no'),
        },
        nodeStoreData: {},
        iterationNodes: [],
        iterationNodesinputs: [],
        options: [],
        iterationOutputlists: [],
        variables: [
          {
            name: "",
            value: "",
            type: "",
            selectedGroup: "",
          },
        ],
      };
    },
    watch: {
      // 监听 variables 数组的变化
      variables: {
        handler (newVal, oldVal)
        {
          // 当 variables 发生变化时，通知父组件更新
          this.$emit(
            "updateCellData",
            { variables: newVal },
            this.curNode
          );
        },
        deep: true,
      },
      // 监听 variables 数组的变化
      outputList: {
        handler (newVal, oldVal)
        {
          // 当 variables 发生变化时，通知父组件更新
          this.$emit(
            "updateCellData",
            { outputs: JSON.stringify(newVal) },this.curNode
          );
        },
        deep: true,
      },
    },
    mounted ()
    {
      
    },
    beforeDestroy () { },
    methods: {
      handleInput (value, itemIndex)
      {
        // 使用正则表达式过滤非法字符
        //   this.outputList[itemIndex].label = value.replace(/[^a-zA-Z0-9]/g, '');
        // 如果以数字开头，清空输入
        // 如果包含中文字符，清空输入并提示
        if (/[\u4e00-\u9fa5]/.test(value)) {
          this.$message({
            type: "warning",
            message: "变量名不支持输入中文字符串",
          });
          this.outputList[itemIndex].label = value.replace(/[^a-zA-Z0-9]/g, '');
        }
        if (/^\d/.test(this.outputList[itemIndex].label)) {
          this.$message({
            type: "warning",
            message: "变量名首字符不支持数字格式",
          });
          this.outputList[itemIndex].label = '';
        }
      },
      inputBlur ()
      {
        //this.$EventBus.$emit("saveWorkflow");
      },
      onChangeInputType (data, itemIndex)
      {
        this.outputList[itemIndex].cusInput = '';
        this.outputList[itemIndex].selectedGroup = '';
        this.outputList[itemIndex].type = data === 1 ? '' : 'string'
        //this.$EventBus.$emit("saveWorkflow");
      },
      onChangeVoidValue (data, itemIndex, item)
      {
        if (!item.label) {
          this.outputList[itemIndex].label = data.value;
        }
        this.outputList[itemIndex].value = data.value;
        this.outputList[itemIndex].selectedGroup = data.selectedGroup;
        this.outputList[itemIndex].type = data.type;
        //this.$EventBus.$emit("saveWorkflow");
        this.selectKey++;
      },

      addConditionItem ()
      {
        this.outputList.push({
          inputType: 1,
          selectedGroup: "",
          cusInput: "",
          label: "",
          relateId: "",
          lan: "",
          value: "",
          type: "",
        });
        this.$EventBus.$emit("saveWorkflow");
      },
      removeConditionItem (itemIndex)
      {
        this.outputList.splice(itemIndex, 1);
        this.$EventBus.$emit("saveWorkflow");
      },
      handleClose ()
      {
        this.drawerVisible = false;
      },
      initData ()
      {
        // let selectedGroup = this.variables && this.variables.length ? this.variables[this.index].selectedGroup : ''
        // this.curValue = selectedGroup ? selectedGroup + '_id_' + this.variables[this.index].value : ''
        console.log(this.iterationNodes)
        let startNode = this.iterationNodes.filter(
          (item) => item.nodeType === 1
        );
        this.appForm.inputs = startNode[0].output.map((item) =>
        {
          return {
            inputType: 1,
            selectedGroup: "",
            cusInput: "",
            lan: "",
            label: item.label,
            value: "",
            type: item.type,
          };
        });
        this.startAppForm.inputs = JSON.parse(JSON.stringify(this.appForm.inputs));
        let tempOptions = this.iterationNodes.filter(
          (item) =>
          {
            let settings = item.settings ? JSON.parse(item.settings) : {}
            return item.nodeType !== 1 && !settings.nodeParent
          }
        );
        this.options = tempOptions.map((item) =>
        {
          let options = [];
          let variables = item.output && Array.isArray(item.input)
            ? item.output
            : item.output
              ? JSON.parse(item.output)
              : [];

          if (item.nodeType === 10) {
            variables = item.input && Array.isArray(item.input)
              ? item.input
              : item.input
                ? JSON.parse(item.input)
                : [];
          }
          options = variables.map((sub) =>
          {
            return {
              parentName: item.nodeName,
              value: sub.id,
              type: sub.type,
              label: sub.label,
            };
          });
          return {
            label: item.nodeName,
            id: item.id,
            options: options,
          };
        });
        //排序
        this.options = this.options.sort((a, b) =>
        {
          // 获取当前项的 nodeType
          const nodeTypeA = this.iterationNodes.find(item => item.id === a.id)?.nodeType;
          const nodeTypeB = this.iterationNodes.find(item => item.id === b.id)?.nodeType;

          // 如果 nodeType 不存在，默认放在前面
          if (nodeTypeA === undefined) return -1;
          if (nodeTypeB === undefined) return 1;

          // 将 nodeType === 10 的项排到最后
          if (nodeTypeA === 10 && nodeTypeB !== 10) return 1; // A 排在后面
          if (nodeTypeA !== 10 && nodeTypeB === 10) return -1; // B 排在后面

          return 0; // 其他情况保持原有顺序
        });
      },
      changeSelect (val,itemIndex)
      {
        let pid = Number(val.split('_id_')[0])
        let id = Number(val.split('_id_')[1])
        let selectParent = this.iterationNodes.find(item => item.id === pid)
        this.appForm.selectOutput = selectParent.nodeType === 10 ? selectParent.input.find(item => item.id === id) : selectParent.output.find(item => item.id === id)
        this.appForm.outputs = [{ "desc": "consequat", "type": this.appForm?.selectOutput?.type, "label": 'result', "value": this.appForm?.selectOutput?.variable, "nodeId": this.nodeStoreData.id, "required": true, "variable": 'result', "direction": 1, "maxLength": 500, "valueType": "reference", "referenceNodeId": this.appForm?.selectOutput?.nodeId }];
        if (!this.outputList[itemIndex].label) {
          this.outputList[itemIndex].label = this.appForm?.selectOutput?.variable;
        }
        this.outputList[itemIndex].value = this.appForm?.selectOutput?.variable;
        this.outputList[itemIndex].selectedGroup = this.appForm?.selectOutput?.nodeId;
        this.outputList[itemIndex].type = this.appForm?.selectOutput?.type;
      },
      openDrawer ()
      {
        let node = this.$store.state.workflow.editNode;
        let parentNodes = this.$store.state.workflow.parentNodes;
        this.nodeStoreData = node.store.data.data;
        this.drawerVisible = true;
        this.curNode = node;
        this.parentNodes = parentNodes;
        console.log(this.nodeStoreData)
        console.log(this.nodeStoreData.toolData)
        this.iterationNodes = this.nodeStoreData.toolData.nodes;
        this.initData();
        this.appForm = this.nodeStoreData.appForm
          ? JSON.parse(this.nodeStoreData.appForm)
          : JSON.parse(JSON.stringify(this.startAppForm));
          this.outputList= this.nodeStoreData.outputs ? JSON.parse(this.nodeStoreData.outputs) : this.startOutputList;
        this.inputKey++
        this.selectKey++

        if (this.curNode._parent) {
          let curParentNodesData = this.curNode._parent.store.data.data
          this.curParentNodesData = curParentNodesData
          if (this.curNode._parent.store.data.data.inputs) {
            let inputs = JSON.parse(this.curNode._parent.store.data.data.inputs)
            inputs = inputs.map(item =>
            {
              return {
                ...item,
                pid: this.curNode._parent.id,
                name: curParentNodesData.label
              }
            })
            this.curParentNodeInputs = inputs
          }
        }
      },
    },
  };
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style scoped lang="scss">
  .title-flex {
    display: flex;
    align-items: center;
    height: 20px;

    span {
      display: inline-block;
      height: 20px;
      line-height: 20px;
    }

    .icon-ctn {
      height: 16px;
      line-height: 19px;
      position: relative;

    }
  }
</style>

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

    &.has-chname {
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

  .ch-text {
    width: 150px;
    height: 34px;
    line-height: 34px;
  }

  .title-flex {
    display: flex;
    align-items: center;
    height: 40px;

    span {
      display: inline-block;
      height: 20px;
      line-height: 20px;
    }

    .icon-ctn {
      height: 16px;
      line-height: 19px;
      position: relative;

    }
  }
</style>