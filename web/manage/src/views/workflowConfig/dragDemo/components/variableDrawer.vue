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
          imgSuffix="bianliang"
        />
        <div style="width: 1px;height: 20px;background: #D3D9E6;margin: 0 12px 12px;"></div>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer" style="margin-bottom: 10px;cursor: pointer"></iconpark-icon>
      </div>
      <div class="sub">用于读取和写入项目中的变量，变量名称必须与项目中的变量名称相匹配</div>
      <div class="drawer-content">
        <el-collapse v-model="activeNames" >
          <el-collapse-item name="0" :title="$t('input')">
            <inputList   v-if="drawerVisible" :single="true" :inputs="appForm.inputs" 
            :key="inputKey" @updateInputList="updateInputList" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList>
            
          </el-collapse-item>
          <el-collapse-item name="1" >
            <template #title>
              <div class="systemPromptTitle">
                <div class="title-flex">
                <span>{{$t('typeChangeSetting')}}</span>
                <div class="icon-ctn">
                  <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'将输入参数类型转换为目标选择的参数类型'" placement="top" :effect="'light'">
                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                  </el-tooltip>
                </div>
              </div>
              </div>
            </template>
            <div class="input-item" style="width: 160px;">
                <div class="tip">变量类型</div>
                <el-select
                    v-model="appForm.cusType"
                    :disabled="disableFlag"
                    size="small"
                    :placeholder="$t('selectPlaceholder')"
                    style="width: 160px"
                >
                    <el-option label="string" value="string"> </el-option>
                    <el-option label="object" value="object"> </el-option>
                    <el-option label="array[object]" value="array[object]"> </el-option>
                    <el-option label="array[string]" value="array[string]"> </el-option>
                </el-select>
            </div>
          </el-collapse-item>
          <el-collapse-item name="2" :title="$t('outputVariable')">
            <div class="result-cont" v-if="showOutput">{{ appForm.inputs[0].label }}<span>{{ appForm.cusType }}</span></div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-drawer>
  </div>
</template>

<script>
// mixins
import drawerMixins from './drawerMixins'

export default {
  name: "modelDrawer",
  mixins: [drawerMixins],
  data() {
    return {
      showOutput: false,
      disableFlag: false,
      appForm: {
        historyFlag:false,
        cusType: "string",
        userPrompt: "",
        systemPrompt: "",
        introduce: "",
        modelId: "",
        modelAnswerFlag: false,
        ocrFlag: false,
        videoFlag: false,
        multiDialogueFlag: false,
        rewritingFlag: false,
        contentScore: 1.76,
      },
      activeNames: ["0", "1", "2", "3"],
    };
  },
  watch: {
        // 监听 variables 数组的变化
        appForm: {
            handler(newVal, oldVal) {
              this.showOutput = newVal.inputs && newVal.inputs[0] && newVal.inputs[0].label && newVal.inputs[0].label !== ""
              this.disableFlag = newVal.inputs && newVal.inputs[0]  && newVal.inputs[0].inputType && newVal.inputs[0].inputType === 2
              if(newVal.inputs && newVal.inputs[0].inputType && newVal.inputs[0].inputType === 2){
                this.appForm.cusType = 'string'
              }
              this.$emit(
                    "updateAppForm",
                    {
                        appForm: JSON.stringify(newVal),
                    },
                    this.curNode
                );
                // 当 variables 发生变化时，通知父组件更新
                this.$emit(
                    "updateVariables",
                    { variables: {
                      value: newVal.inputs[0].label,
                      type: newVal.cusType,
                      label: newVal.inputs[0].label,
                  } },
                    this.curNode
                );
            },
            deep: true,
        },
    },
  mounted() {
  },
  beforeDestroy() {},
  methods: {
    
  },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style lang="scss" scoped>
  .systemPromptTitle{
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    div{
      >span{
        display: inline-block;
        margin-left: 10px;
      }
    }
  }
  .title-flex{
    display: flex;
    align-items: center;
    height: 20px;
  
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
  .workflow-tooltip{
    border: none !important;
    box-shadow: 0px 6px 12px 0px rgba(0,0,0,0.1);
    padding: 8px 12px !important;
    line-height: 20px;
    font-size: 14px;
    color: #1D2129 ;
    font-family: MiSans Regular;
    max-width: 336px !important;
    .popper__arrow{
      border-top-color: rgba(0,0,0,0) !important;
      bottom: -5px !important;
    }
  }
  .is-dark {
    max-width: 336px;
  }
</style>
  