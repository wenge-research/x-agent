<template>
    <div class="Ec-x6-icon" @click="handleDrawerClick">
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
            <div
                style="
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                "
            >
                <HeadTool
                    v-if="drawerVisible"
                    :label="sourceData.label"
                    :imgWidth="24"
                    :imgHeight="24"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                    @testNodes="openRunDrawer"
                    imgSuffix="canshutiqu"
                />
                <div
                    style="
                        width: 1px;
                        height: 20px;
                        background: #d3d9e6;
                        margin: 0 12px 12px;
                    "
                ></div>
                <iconpark-icon
                    name="close-line"
                    size="18"
                    color="#828894"
                    @click="closeDrawer"
                    style="margin-bottom: 10px; cursor: pointer"
                ></iconpark-icon>
            </div>
            <div class="sub">支持对输入进行参数提取</div>
            <div class="drawer-content">
                <el-collapse v-model="activeNames">
                  <el-collapse-item name="0">
                    <template slot="title">
                        <div class="title-flex">
                            <span>{{ $t('model') }}</span>
                            <div class="icon-ctn">
                            <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'选择进行参数提取的大模型，决定参数提取的逻辑和能力'" placement="top" :effect="'light'">
                                <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                            </el-tooltip>
                            </div>
                        </div>
                    </template>
					 <el-popover
									    style="border: none;"
									    placement="bottom-start"
									    :visible-arrow="false" v-model="visiblePopover"
									    trigger="click">
									    <el-button
									    slot="reference"
									    type="text"
									    ><iconpark-icon style=" position: absolute; right: 20px; top: 12px; " name="settings-4-line" size="16" color="#1d2129"></iconpark-icon></el-button
									  >
									  
									  <div class="knowledgeset-ctn">
									    <div class="knowledgeset-title-ctn">
									      <div class="knowledgeset-title">
									        模型设置
									      </div>
									      <div class="advancedset-ctn">
									        <div class="advancedset-word">默认值</div>
									        <div class="advancedset-icon">
									          <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
									        </div>
									      </div>
									    </div>
									  
									    <div class="knowledgeset-options">
									      
									      <div class="knowledgeset-options-list">
									        <div class="left-ctn">
									          <div class="words">携带上下文轮数</div>
									          <div class="tooltip-ctn"> 
									            <el-tooltip popper-class="workflow-tooltip" content="设置带入模型上下文的对话历史轮数。轮数越多，多轮对话的相关性越高，低于设定值的段落将不被召回。" placement="top" effect="light">
									              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
									            </el-tooltip>
									          </div>
									        </div>
									        <div class="right-ctn">
									          <el-slider
									            v-model="appForm.multiDialogueNum"
									            :min="1"
									            :max="100"
									            :step="1"
									          ></el-slider>
									          <el-input-number
									            class="score-input"
									            v-model="appForm.multiDialogueNum"
									            controls-position="right"
									            :min="1"
									            :max="100"
									            :step="1"
									            size="small"
									            style="width: 80px"
									          ></el-input-number>
									          <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultValue('multiDialogueNum', 3)">
									            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
									          </span>
									        </div>
									      </div>
									      <div class="knowledgeset-options-list">
									        <div class="left-ctn">
									          <div class="words">最大回复数</div>
									          <div class="tooltip-ctn"> 
									            <el-tooltip popper-class="workflow-tooltip" content="控制模型输出的 Token 长度上限。通常100Tokens 约等于 150 个中文汉字。" placement="top" effect="light">
									              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
									            </el-tooltip>
									          </div>
									        </div>
									        <div class="right-ctn">
									          <el-slider
									            v-model="appForm.maxToken"
									            :min="1"
									            :max="10000"
									            :step="1"
									          ></el-slider>
									          <el-input-number
									            class="score-input"
									            v-model="appForm.maxToken"
									            controls-position="right"
									            :min="1"
									            :max="100000"
									            :step="1"
									            size="small"
									            style="width: 80px"
									          ></el-input-number>
									          <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultValue('maxToken', 2048)">
									            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
									          </span>
									        </div>
									      </div>
									      <div class="knowledgeset-options-list">
									        <div class="left-ctn">
									          <div class="words">模型兜底</div>
									          <div class="tooltip-ctn"> 
									            <el-tooltip popper-class="workflow-tooltip" content="在其他知识来源均未召回相关内容时，直接使用模型总结输出结果，避免回答失败。默认开启。" placement="top" effect="light">
									              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
									            </el-tooltip>
									          </div>
									        </div>
									        <div class="right-ctn">
									          <el-switch v-model="appForm.modelAnswerFlag"  />	        
									        </div>
									      </div>
									  	<div  class="dialog-footer" style="display: flex;justify-content: end;">
									  	  <el-button @click="visiblePopover = false">{{ $t("cancel") }}</el-button>
									  	  <el-button type="primary" @click="visiblePopover = false">确定</el-button>
									  	</div>
									    </div>
									  </div>
									  
									    
									  </el-popover>
                    <modelSelect v-if="drawerVisible" :key="appForm.modelId" :id="appForm.modelId" @change="modelChange"></modelSelect>
                  </el-collapse-item>
                  <el-collapse-item name="2" >
                    <template #title>
                      <div class="systemPromptTitle">
                        <div class="title-flex">
                            <span>{{ $t('userPrompt') }}</span>
                            <div class="icon-ctn">
                            <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'给大模型的指令模板，定义参数提取规则'" placement="top" :effect="'light'">
                                <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                            </el-tooltip>
                            </div>
                        </div>
                        <!-- <div>{{$t('userPrompt')}}</div> -->
                        <div>
                          <!-- <span>
                            <iconpark-icon name="variable" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                          </span> -->
                          <span @click.stop="openVocabularyDrawer('userPrompt')">
                            <iconpark-icon name="pages-line" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                          </span>
                          <span @click.stop="openMagnifiedEditBox('userPrompt')">
                            <iconpark-icon name="expand-diagonal-line" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                            <!-- <el-button type="text" size="small" @click.stop="openMagnifiedEditBox">放大</el-button> -->
                          </span>
                        </div>
                      </div>
                    </template>
                    <el-input
                      v-model="appForm.userPrompt"
                      type="textarea"
                      :placeholder="modelTip"
                      :rows="3"
                      @blur="inputBlur"
                      @keydown.delete="handleKeyDown"
                      :disabled="magnifiedBoxVisible && editName === 'userPrompt'"
                    ></el-input>
                  </el-collapse-item>
                    <el-collapse-item name="1">
                        <template slot="title">
                            <div class="title-flex">
                                <span>{{ $t('input') }}</span>
                                <div class="icon-ctn">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'待提取的变量'" placement="top" :effect="'light'">
                                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                                </el-tooltip>
                                </div>
                            </div>
                        </template>
                        <inputList
                            v-if="drawerVisible"
                            :inputs="appForm.inputs"
                            :key="inputKey"
                            @updateInputList="updateInputList"
                            :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"
                            :single="true"
                            :defalutVal="'Query'"
                        ></inputList>
                    </el-collapse-item>
                </el-collapse>
                <div class="section-title">
                    <div class="title-flex">
                        <span>{{ $t('extractParameter') }}</span>
                        <div class="icon-ctn">
                            <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'需要提取的信息'" placement="top" :effect="'light'">
                                <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                            </el-tooltip>
                        </div>
                    </div>
                    <!-- {{ $t("extractParameter") }} -->
                    <i class="el-icon-plus add-icon" @click="addVariable"></i>
                </div>
                <el-collapse v-model="activeInputs" class="cus-collapse">
                    <el-collapse-item
                        v-for="(item, index) in filterVariables"
                        :key="index"
                        :name="item.id ? item.id : index.toString()"
                    >
                        <template #title>
                            {{ $t("parameter") }} {{ index + 1 }}
                            <i
                                class="el-icon-delete delete-icon"
                                @click.stop="removeVariable(index)"
                            ></i>
                        </template>
                        <el-form
                            :model="item"
                            ref="formRefs[index]"
                            :rules="rules"
                            label-width="80px"
                            @submit.prevent
                        >
                            <el-form-item
                                :label="$t('variableName')"
                                prop="name"
                            >
                                <el-input
                                    size="medium"
                                    v-model="item.name"
                                    @blur="inputBlur"
                                ></el-input>
                            </el-form-item>
                            <el-form-item
                                :label="$t('description')"
                                prop="description"
                            >
                                <el-input
                                    size="medium"
                                    v-model="item.description"
                                    type="textarea"
                                    :rows="2"
                                    @blur="inputBlur"
                                ></el-input>
                            </el-form-item>
                            <el-form-item :label="$t('fieldType')" prop="type">
                                <el-select
                                    size="medium"
                                    v-model="item.type"
                                    @change="onChange"
                                >
                                    <el-option
                                        label="string"
                                        value="string"
                                    ></el-option>
                                    <el-option
                                        label="integer"
                                        value="integer"
                                    ></el-option>
                                    <el-option
                                        label="array[string]"
                                        value="array[string]"
                                    ></el-option>
                                    <el-option
                                        label="object"
                                        value="object"
                                    ></el-option>
                                    <el-option
                                        label="array[object]"
                                        value="array[object]"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                            <!-- <el-form-item
                                :label="$t('maxLength')"
                                prop="maxLength"
                            >
                                <el-input-number
                                    size="medium"
                                    v-model="item.maxLength"
                                    :min="0"
                                    @blur="inputBlur"
                                ></el-input-number>
                            </el-form-item>
                            <el-form-item
                                :label="$t('isRequired')"
                                prop="required"
                            >
                                <el-radio-group
                                    size="medium"
                                    v-model="item.required"
                                    @change="radioChange"
                                >
                                    <el-radio :label="true">{{
                                        $t("yes")
                                    }}</el-radio>
                                    <el-radio :label="false">{{
                                        $t("no")
                                    }}</el-radio>
                                </el-radio-group>
                            </el-form-item> -->
                        </el-form>
                    </el-collapse-item>
                    <!-- <el-collapse-item name="system">
                        <template #title>
                            <div class="systemPromptTitle">
                                <div>系统提示词</div>
                            </div>
                        </template>
                        <el-input
                            v-model="appForm.userPrompt"
                            type="textarea"
                            :rows="3"
                        ></el-input>
                    </el-collapse-item>
                    <el-collapse-item name="user" :title="$t('userPrompt')">
                        <el-input
                            v-model="appForm.userPrompt"
                            type="textarea"
                            :rows="3"
                        ></el-input>
                    </el-collapse-item> -->
                </el-collapse>
            </div>
        </el-drawer>
        <!-- 放大框 -->
        <magnifiedEditBox v-if="drawerVisible" ref="magnifiedEditBox" @input="handleInput"  @openVocabularyDrawer="openVocabularyDrawer(editName)" @update:visible="handleMagnifiedBoxVisibleChange" />
        <!-- 提示词库抽屉 -->
        <vocabularyDrawer v-if="drawerVisible" ref="vocabularyDrawer" @insertVocabulary="insertVocabularyFn" />
    </div>
</template>

<script>
// mixins
import drawerMixins from "./drawerMixins";
import magnifiedEditBox from '@/views/workflowConfig/dragDemo/components/magnifiedEditBox.vue'
import vocabularyDrawer from '@/views/workflowConfig/dragDemo/components/vocabularyDrawer'
import modelSelect from "@/components/ModelSelect.vue";
const { v4: uuidv4 } = require('uuid');
export default {
    name: "paramsFilterDrawer",
    components: { magnifiedEditBox, vocabularyDrawer, modelSelect },
    props: ["panels", "params"],
    mixins: [drawerMixins],
    data() {
        return {
            activeInputs:['system','user'],
			
           visiblePopover: false,
                     appForm:{
           			  multiDialogueNum:3,
           			  maxToken:'',
           			  modelAnswerFlag: true,
                modelId: "",
                applicationName: "",
                userPrompt: "",
                modelAnswerFlag: true,
                ocrFlag: true,
                videoFlag: true,
                multiDialogueFlag: true,
                rewritingFlag: true,
                contentScore: 1.76,
                rangeContentScore: 0.88,
                qaTitleScore: 1.91,
                qaRangeTitleScore: 0.91,
                qaContentScore: 0.88,
                qaRangeContentScore: 0.88,
            },
            startAppForm: {
                modelId: "",
                applicationName: "",
                userPrompt: `\${question}。请根据以上内容，提取\${description}，结果用json返回，json的key分别对应为\${key}，如果没有提取到key对应的值，请将该参数值设置为空字符串，结果仅输出json即可。例如：{beijing:阴天, shenzhen:晴天, anhui: ""}`,
                modelAnswerFlag: true,
                ocrFlag: true,
                videoFlag: true,
                multiDialogueFlag: true,
                rewritingFlag: true,
                contentScore: 1.76,
                rangeContentScore: 0.88,
                qaTitleScore: 1.91,
                qaRangeTitleScore: 0.91,
                qaContentScore: 0.88,
                qaRangeContentScore: 0.88,
            },
            filterVariables: [],
            formRefs: [],
            rules: {
                name: [
                    {
                        required: true,
                        message: this.$t("pleaseEnterVariableName"),
                        trigger: "blur",
                    },
                ],
                description: [
                    {
                        required: true,
                        message: this.$t("pleaseEnterDescription"),
                        trigger: "blur",
                    },
                ],
                type: [
                    {
                        required: true,
                        message: this.$t("pleaseSelectFieldType"),
                        trigger: "change",
                    },
                ],
                maxLength: [
                    {
                        required: true,
                        message: this.$t("pleaseEnterMaxLength"),
                        trigger: "blur",
                    },
                ],
                required: [
                    {
                        required: true,
                        message: this.$t("pleaseSelectIsRequired"),
                        trigger: "change",
                    },
                ],
            },
        };
    },
    watch: {
        // 监听 filterVariables 数组的变化
        filterVariables: {
            handler(newVal, oldVal) {
                // 当 filterVariables 发生变化时，通知父组件更新
                this.$emit(
                    "updatefilterVariables",
                    { filterVariables: JSON.stringify(newVal) },
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
        inputBlur() {
            this.$EventBus.$emit("saveWorkflow");
        },
        onChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        switchChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        sliderChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        radioChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        async addVariable() {
            let id = await uuidv4()?.replace(/-/g, '')  
            const newVariable = {
                name: "",
                description: "",
                type: "string",
                id: id,
                maxLength: 20,
                required: true,
            };
            this.filterVariables.push(newVariable);
            this.activeInputs.push(id);
            this.formRefs.push(null); // 初始化表单引用
            this.$EventBus.$emit("saveWorkflow");
        },
        removeVariable(index) {
            this.filterVariables.splice(index, 1);
            this.formRefs.splice(index, 1);
            this.$EventBus.$emit("saveWorkflow");
        },
        openDrawer() {
            this.initDrawer();
            this.filterVariables = this.nodeStoreData.filterVariables
                ? JSON.parse(this.nodeStoreData.filterVariables)
                : [];
        },
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
.knowledgeset-ctn{
    width: 500px;
    // height: 122px;
    background: #FFFFFF;
    box-shadow: 0px 2px 12px 0px rgba(26,36,70,0.1);
    border-radius: 8px !important;
    border: 1px solid #E5E6EA;
    padding: 18px 16px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  
    .knowledgeset-title-ctn{
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: space-between;
  
  
      .knowledgeset-title{
        height: 32px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #1D2129;
        line-height: 32px;
      }
  
      .advancedset-ctn{
        height: 32px;
        display: flex;
        align-items: center;
        gap: 10px;
        cursor: pointer;
  
        .advancedset-word{
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #86909C;
          line-height: 20px;
        }
  
        .advancedset-icon{
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
    }
  
    .knowledgeset-options{
      margin-top: 16px;
      display: flex;
      flex-direction: column;
      gap: 16px;
  
      &-list{
        width: 100%;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: space-between;
  
        .left-ctn{
          height: 20px;
          display: flex;
          align-items: center;
          gap: 8px;
  
          .words{
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #86909C;
            line-height: 20px;
          }
  
          .tooltip-ctn{
            width: 20px;
            height: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
          }
        }
  
        .knowledgeset-model{
          width: 300px;
  
          .el-input__inner{
            border: none;
            background: #F7F8FA;
            border-radius: 4px;
            color: #494C4F;
          }
          .el-select__caret {
            color: #1d2129;
          }
        }
        .right-ctn{
          height: 32px;
          display: flex;
          align-items: center;
          gap: 8px;
          .el-slider__bar{
            background: linear-gradient( 270deg, #8E65FF 0%, #1747E5 100%);
          }
  
          .el-slider__runway {
            width: 172px;
            height: 4px;
            border-radius: 4px;
            // margin-right: 8px;
          }
          .el-input__inner{
            padding-left: 28px;
            padding-right: 8px;
            border: none;
            background: #F7F8FA;
            border-radius: 4px;
            overflow: hidden;
          }
  
          .el-input-number__decrease,.el-input-number__increase{
            left: 1px;
            border: none;
            width: 19px;
            background-color:#F2F3F5 ;
            color: #86909C;
          }
        }
      }
    }
  }
</style>