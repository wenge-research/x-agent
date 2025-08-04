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
          imgSuffix="workflow-damoxing"
        />
        <div style="width: 1px;height: 20px;background: #D3D9E6;margin: 0 12px 12px;"></div>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer" style="margin-bottom: 10px;cursor: pointer"></iconpark-icon>
      </div>
      <div class="sub">调用大语言模型,使用变量和提示词生成回复</div>
      <div class="drawer-content">
        <el-collapse v-model="activeNames" >
          <el-collapse-item name="0">
			<template slot="title">
			  <div class="title-flex">
			    <span>{{ $t('model') }}</span>
			    
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
			        <el-switch v-model="appForm.modelAnswerFlag" active-color="#1747E5"
                inactive-color="#B4BCCC" />	        
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
          <el-collapse-item name="1">
            <template slot="title">
              <div class="title-flex">
                <span>{{ $t('input') }}</span>
                <div class="icon-ctn">
                  <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'输入需添加到提示词中的信息，可被提示词引用'" placement="top" :effect="'light'">
                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                  </el-tooltip>
                </div>
              </div>
            </template>
            <el-checkbox v-model="appForm.historyFlag" style=" position: absolute; right: 20px; top: 12px; " @change="checkChange">对话历史</el-checkbox>
           <inputList  v-if="drawerVisible" :inputs="appForm.inputs"
                            :key="inputKey" @updateInputList="updateInputList" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList>
          </el-collapse-item>
          <el-collapse-item name="2" >
            <template #title>
              <div class="systemPromptTitle">
                <div class="title-flex">
                <span @click="addClick">系统提示词</span>
                <div class="icon-ctn">
                  <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'给大模型的指令性信息，用于约束、引导大模型的行为和回答方式，如角色身份、回答规范等'" placement="top" :effect="'light'">
                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                  </el-tooltip>
                </div>
              </div>
                <!-- <div>系统提示词</div> -->
                <div>
                  <!-- <span>
                    <iconpark-icon name="variable" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                  </span> -->
                  <span @click.stop="openVocabularyDrawer('systemPrompt')">
                    <iconpark-icon name="pages-line" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                  </span>
                  <span @click.stop="openMagnifiedEditBox('systemPrompt')">
                    <iconpark-icon name="expand-diagonal-line" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                    <!-- <el-button type="text" size="small" @click.stop="openMagnifiedEditBox">放大</el-button> -->
                  </span>
                  <el-popover
                    style="border: none;"
                    placement="bottom-start"
                    v-model="roleFlag"
                    trigger="click">
                    <el-button
                    slot="reference"
                    class="btn btn1"
                    type="text"
                    :loading="autoOptimizationLoading"
                    @click.stop
                  >
                    <img  style="width: 18px;height: 18px;" src="@/assets/images/ai-btn.svg" alt="" />
                  </el-button>

                  <div class="autoOp-ctn">
                    <div class="app-create" @click="autoOptimization(applicationName)">
                      <div class="edit">
                        <iconpark-icon name="edit-line" size="16" color="#1d2129"></iconpark-icon>
                      </div>
                      <div class="word">根据应用名生成</div>
                    </div>

                    <div class="roleKey-create">
                      <img src="@/assets/images/ai-btn.svg" class="logo" alt="" />
                      <input type="text" class="input-ctn" placeholder="输入你想要的角色关键词" v-model="roleKeyWord" @keydown.enter="autoOptimization(roleKeyWord)">
                      <div class="icon-fasong" @click="autoOptimization(roleKeyWord)">
                        <img src="@/assets/images/fasong.svg" alt="">
                      </div>
                    </div>
                  </div>

                    
                  </el-popover>
                  
                </div>
              </div>
            </template>
            <el-input
              v-model="appForm.systemPrompt"
              type="textarea"
              :placeholder="modelTip"
              :rows="3"
              @blur="inputBlur"
			  @input="inputsBlur"
              @keydown.delete="handleKeyDown"
              :disabled="magnifiedBoxVisible && editName === 'systemPrompt'"
            ></el-input>
			<div class="variableBox" v-if="variableBoxFlag"  @mousedown.stop @touchstart.stop
			  >
			  <div class="variableBox-item" v-for="(item,index) in appForm.inputs" :key="index"
			    @click="insertSelectedVariable(item.label)">
			    <div v-if="item.label">{{item.label}}</div>
			  </div>
			  <div class="no-data1" v-if="appForm.inputs&&appForm.inputs.length==0">
			    无匹配的变量
			  </div>
			</div>
          </el-collapse-item>
          <el-collapse-item name="3">
            <template #title>
              <div class="systemPromptTitle">
                <div class="title-flex">
                <span>{{$t('userPrompt')}}</span>
                <div class="icon-ctn">
                  <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'向大模型输入的文本内容，用于表达自己的需求、提问、请求生成特定类型内容等'" placement="top" :effect="'light'">
                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                  </el-tooltip>
                </div>
              </div>
                <!-- <div>{{$t('userPrompt')}}</div> -->
                <div>
                  <span @click.stop="openVocabularyDrawer('userPrompt')">
                    <iconpark-icon name="pages-line" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                  </span>
                  <span @click.stop="openMagnifiedEditBox('userPrompt')">
                    <iconpark-icon name="expand-diagonal-line" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                  </span>
                </div>
              </div>
            </template>
            <el-input
              v-model="appForm.userPrompt"
              type="textarea"
              :placeholder="userTip"
              :rows="3"
			  @input="inputsBlur1"
              @keydown.delete="handleKeyDown"
              :disabled="magnifiedBoxVisible && editName === 'userPrompt'"
            ></el-input>
			<div class="variableBox" v-if="variableBoxFlag1"  @mousedown.stop @touchstart.stop
			  >
			  <div class="variableBox-item" v-for="(item,index) in appForm.inputs" :key="index"
			    @click="insertSelectedVariable1(item.label)">
			    <div v-if="item.label">{{item.label}}</div>
			  </div>
			  <div class="no-data1" v-if="appForm.inputs&&appForm.inputs.length==0">
			    无匹配的变量
			  </div>
			</div>
          </el-collapse-item>
          <el-collapse-item name="4" :title="$t('outputVariable')">
            <div class="result-cont">text<span>string</span></div>
            <div class="result-cont">reasoningContent<span>string</span></div>
          </el-collapse-item>
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
import drawerMixins from './drawerMixins'
import magnifiedEditBox from '@/views/workflowConfig/dragDemo/components/magnifiedEditBox.vue'
import vocabularyDrawer from '@/views/workflowConfig/dragDemo/components/vocabularyDrawer'
import modelSelect from "@/components/ModelSelect.vue";
import { fetchEventSource } from "@microsoft/fetch-event-source";
import md5 from "js-md5";

export default {
  name: "modelDrawer",
  mixins: [drawerMixins],
  components: { magnifiedEditBox, vocabularyDrawer,modelSelect },
  data() {
    return {
      modelTip:'系统提示词，可以使用${变量名}的方式引用输入参数中的变量',
      userTip:'用户提示词，可以使用${变量名}的方式引用输入参数中的变量',
      magnifiedBoxVisible: false,
	  variableBoxFlag: false,
	  variableBoxFlag1: false,
	  visiblePopover: false,
      editName:'',
      appForm: {
		multiDialogueNum:3,
		maxToken:'',
        historyFlag:false,
        userPrompt: "",
        systemPrompt: "",
        introduce: "",
        modelId: "",
        modelAnswerFlag: true,
        ocrFlag: true,
        videoFlag: true,
        multiDialogueFlag: true,
        rewritingFlag: true,
        contentScore: 1.76,
		
      },
      activeNames: ["0", "1", "2", "3"],
      autoOptimizationLoading:false,
      roleFlag:false,
      roleKeyWord:"",
      applicationName:"",
      startAppForm:{
        systemPrompt:""
      }
    };
  },
  mounted() {
    this.applicationName=JSON.parse(sessionStorage.getItem("nodeInfo")).applicationInfo.applicationName
  },
  beforeDestroy() {},
  methods: {
	setDefaultValue(key, value) {
	  this.appForm[key] = value;
	},
    inputBlur() {
		
        this.$EventBus.$emit("saveWorkflow");
    },
	inputsBlur() {		
		if(this.appForm.systemPrompt.includes('$')){
			this.variableBoxFlag = true
		}else{
			this.variableBoxFlag = false
		}   
    },
	inputsBlur1() {
		
		if(this.appForm.userPrompt.includes('$')){
			this.variableBoxFlag1 = true
		}else{
			this.variableBoxFlag1 = false
		}   
	},
	
    onChange() {
          this.$EventBus.$emit("saveWorkflow");
    },
    switchChange() {
          this.$EventBus.$emit("saveWorkflow");
    },
    checkChange() {
          this.$EventBus.$emit("saveWorkflow");
    },
    sliderChange() {
          this.$EventBus.$emit("saveWorkflow");
    },
    handleKeyDown(event) {
      event.preventDefault()
    },
	addClick(){
		console.log('‘112',this.appForm.inputs)
	},
	insertSelectedVariable(label){
		this.appForm.systemPrompt = this.appForm.systemPrompt+'{'+label+'}'
		this.variableBoxFlag = false
	},
	insertSelectedVariable1(label){
		this.appForm.userPrompt = this.appForm.userPrompt+'{'+label+'}'
		this.variableBoxFlag1 = false
	},
    autoOptimization(keyWord) {
      let that = this;
      that.autoOptimizationLoading = true;
      that.roleFlag=false;
      const params = {
        topic: keyWord,
        description: this.appForm.introduce,
        clientId: Math.floor(Math.random() * 10000000000),
      };
      let ctrlAbout = new AbortController();
      let manageAccessToken = sessionStorage.getItem("manageAccessToken");
      let timer = new Date().getTime();
      fetchEventSource(
        `${process.env.VUE_APP_BASE_API}/applicationInfo/generatePrompt`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${manageAccessToken}`,
            "Content-Type": "application/json",
            timestamp: timer,
            cipher: md5(
              timer +
                `/applicationInfo/generatePrompt${JSON.stringify(
                  params
                )}xxxxxxxxxxx`
            ),
          },
          signal: ctrlAbout.signal,
          body: JSON.stringify(params),
          openWhenHidden: true, //默认为false，监听visibilitychange，当页面不可见时关闭连接，当页面重新可见时重新打开连接。
          async onopen(response) {
            console.log("=======onopen", response);
          },
          onmessage(event) {
            let data = event.data;
            if (data) {
              let promptData = JSON.parse(data);
              that.appForm.systemPrompt = promptData.prompt;
            }
          },
          onerror(err) {
            that.autoOptimizationLoading = false;
          },
          onclose() {
            // 服务关闭
            console.log("guanbi");
            that.autoOptimizationLoading = false;
            // that.changeApplicationStatus(true);
            // that.handleAutoSave();
            ctrlAbout.abort();
            that.roleKeyWord=""
          },
        }
      );
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

</style>
<style lang="scss">
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
  .variableBox {
      position: fixed;
      z-index: 99;
      min-width: 128px;
      padding: 3px;
      box-sizing: border-box;
      background: #FFFFFF;
      box-shadow: 0px 0px 12px 0px rgba(26, 36, 70, 0.08);
      border-radius: 2px;
  
      &-item {
		  cursor: pointer;
        div {
          height: 32px;
          line-height: 32px;
          font-size: 14px;
          color: #36383D;
          padding: 0px 9px;
  
          &:hover {
            background: rgba(188, 193, 204, 0.2);
          }
        }
      }
  
      .no-data1 {
        height: 32px;
        line-height: 32px;
        font-size: 14px;
        color: #36383D;
        padding: 0px 9px;
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
