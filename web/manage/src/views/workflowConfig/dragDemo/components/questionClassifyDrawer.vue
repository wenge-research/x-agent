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
                    imgSuffix="wentifenlei"
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
            <div class="sub">定义用户问题的分类条件，大模型能够根据分类描述定义对话的进展方式</div>
            <div class="drawer-content">
                <el-collapse v-model="activeNames">
                    <el-collapse-item name="0">
                        <template slot="title">
                            <div class="title-flex">
                                <span>{{ $t('input') }}</span>
                                <div class="icon-ctn">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'输入用于做分类判断的参数'" placement="top" :effect="'light'">
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
                    <el-collapse-item name="1">
                        <template slot="title">
                            <div class="title-flex">
                                <span>{{ $t('model') }}</span>
                                <div class="icon-ctn">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'用于进行分类意图识别'" placement="top" :effect="'light'">
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
                    <el-collapse-item name="2" :title="$t('classification')">
                        <template slot="title">
                            <div class="title-flex">
                                <span>{{ $t('classification') }}</span>
                                <div class="icon-ctn">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'通过预设不同类别及描述，使大模型能精准判别输入内容所匹配的分类'" placement="top" :effect="'light'">
                                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                                </el-tooltip>
                                </div>
                            </div>
                        </template>
                        <div
                            v-for="(category, index) in configurationList"
                            :key="index"
                            class="category"
                            :class="{ active: categoryIndex === index }"
                        >
                            <div class="category-header">
                                <span class="tit">分类{{
                                  numberToChinese(index + 1)
                                }}</span>
                                <el-button
                                    @click="removeCategory(index)"
                                    type="text"
                                    class="delete-button"
                                    icon="el-icon-delete"
                                    style="color: #676f83"
                                ></el-button>
                            </div>
                            <textarea
                                v-model="category.content"
                                rows="4"
                                @focus="categoryIndex = index"
                                :placeholder="$t('enterClassificationContent')"
                                @blur="inputBlur"
                            ></textarea>
                        </div>
                        <el-button
                                @click="addCategory"
                                plain
                                icon="el-icon-plus"
                                style="height: 32px;border-radius: 4px;border: 1px solid #D3D9E6;line-height: 32px;width: 100%;padding: 0;color: #383D47;background: transparent"
                                >{{ $t("addClassification") }}</el-button
                            >
                    </el-collapse-item>
                    <el-collapse-item name="3" :title="$t('output')">
                        <div class="result-cont">
                            result<span>array[object]</span>
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
import modelSelect from "@/components/ModelSelect.vue";
export default {
    name: "CustomDrawer",
    props: ["panels", "params"],
    components: { modelSelect },
    mixins: [drawerMixins],
    data() {
        return {
            categoryIndex: -1,
			
            visiblePopover: false,
                      appForm:{
            			  multiDialogueNum:3,
            			  maxToken:'',
            			  modelAnswerFlag: true,
                modelId: "",
                applicationName: "",
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
            configurationList: [],
        };
    },
    watch: {
        configurationList: {
            handler(newVal, oldVal) {
                // 当 variables 发生变化时，通知父组件更新
                this.$emit(
                    "updateCellData",
                    { configurationList: JSON.stringify(newVal) },
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
        inputChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        addCategory() {
            const time = new Date().getTime();
            let caseId = `out-${time}`;
            let index = this.configurationList.length + 1;
            let typeName = "分类" + this.numberToChinese(index);
            this.configurationList.push({
                sourceNodeId: this.curNode.id,
                caseId,
                className: typeName,
                content: "",
            });
            this.$emit("addPorts", this.configurationList, this.curNode);
            this.$EventBus.$emit("saveWorkflow");
        },
        removeCategory(index) {
            this.configurationList.splice(index, 1);
            this.$emit(
                "removePorts",
                {
                    configurationList: JSON.parse(
                        JSON.stringify(this.configurationList)
                    ),
                },
                this.curNode,
                index
            );
            this.$EventBus.$emit("saveWorkflow");
        },
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
        openDrawer() {
            this.initDrawer();
            const time = new Date().getTime();
            let caseId = `out-other`;
            this.configurationList =
                this.nodeStoreData.configurationList ?
                JSON.parse(this.nodeStoreData.configurationList) : [{
                  caseId: caseId,
                  className: "分类一",
                  content: "其他",
                  sourceNodeId: this.curNode.id
                  }
                ];
        },
        
        
    },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style scoped lang="scss">
.setting {
    top: 7px!important;
}
.category {
    margin-bottom: 1em;
    padding: 12px;
    background-color: #f2f4f7;
    border-radius: 12px;
    border: 1px solid #D3D9E6;
    &.active {
        background: #fff;
    }
    textarea {
        width: 100%;
        border: none;
        resize: none;
        background: none;
        outline: none;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #494C4F;
        line-height: 22px;
    }
}
.category-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 20px;
    margin-bottom: 8px;
    .tit {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 14px;
        color: #383D47;
        line-height: 20px;
    }
}

::v-deep(.el-collapse-item__content) {
    .input-item {
    .name-text {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #383D47;
        line-height: 20px;
    }
}
}
::v-deep(.el-select .el-input__inner) {
    border-radius: 8px!important;
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
