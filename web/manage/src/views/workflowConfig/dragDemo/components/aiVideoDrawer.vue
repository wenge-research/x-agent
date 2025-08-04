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
                  imgSuffix="workfolw-shipinshengcheng"
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
          <div class="sub">{{'通过文字描述或图片生成视频'}}</div>
          <div class="drawer-content">
              <el-collapse v-model="activeNames">
                <el-collapse-item name="0" :title="$t('largeModel')">
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
                  <el-select v-model="appForm.modelId" @change="changeModel">
                    <template #prefix>
                          <img v-if="getModelImage == '全部供应商'" src="@/assets/images/all.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == '雅意'" src="@/assets/images/yayi.png" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == 'Kimi'" src="@/assets/images/kimi.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == 'DeepSeek'" src="@/assets/images/deepseek.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == '文心一言'" src="@/assets/images/wenxinyiyan.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == '智谱清言'" src="@/assets/images/zhipuqingyan.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == '豆包'" src="@/assets/images/doubao.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == '通义千问'" src="@/assets/images/tongyi.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == '中国移动'" src="@/assets/images/deepseek.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == '百川'" src="@/assets/images/baichuan.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == '星火'" src="@/assets/images/xinghuo.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == 'openAI'" src="@/assets/images/openai.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                          <img v-if="getModelImage == 'MINIMAX'" src="@/assets/images/MiniMax.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                      </template>
                      <el-option
                          v-for="(item, index) in largeModelList"
                          :key="index+item.modelName"
                          :label="item.modelName"
                          :value="item.modelId">
                          <span style="float:left">
                              <img v-if="item.manufacturer == '全部供应商'" src="@/assets/images/all.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == '雅意'" src="@/assets/images/yayi.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == 'Kimi'" src="@/assets/images/kimi.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == 'DeepSeek'" src="@/assets/images/deepseek.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == '文心一言'" src="@/assets/images/wenxinyiyan.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == '智谱清言'" src="@/assets/images/zhipuqingyan.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == '豆包'" src="@/assets/images/doubao.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == '通义千问'" src="@/assets/images/tongyi.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == '中国移动'" src="@/assets/images/deepseek.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == '百川'" src="@/assets/images/baichuan.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == '星火'" src="@/assets/images/xinghuo.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == 'openAI'" src="@/assets/images/openai.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <img v-if="item.manufacturer == 'MINIMAX'" src="@/assets/images/MiniMax.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                              <!-- <img src="@/assets/images/all.png" alt="" style="width: 13px;height: 13px; margin-right:10px;"> -->
                          </span>
                          <span>{{ item.modelName }}</span>
                      </el-option>
                  </el-select>
                  </el-collapse-item>
                  <el-collapse-item name="1">
                    <template slot="title">
                      <div class="title-flex">
                        <span>{{ $t('input') }}</span>
                        <div class="icon-ctn">
                          <el-tooltip popper-class="workflow-tooltip" effect="light" :content="'输入需添加到提示词中的信息，可被提示词引用'" placement="top">
                            <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                          </el-tooltip>
                        </div>
                      </div>
                    </template>
                    <inputList  v-if="drawerVisible" :inputs.sync="appForm.inputs"
                                    :key="inputKey" @updateInputList="updateInputList" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList>
                  </el-collapse-item>
                  <!-- <el-collapse-item name="1">
                    <template slot="title">
                      <div class="systemPromptTitle">
                        <div class="title-flex">
                          <span>{{ $t('input') }}</span>
                        </div>
                        <div class="input-add">
                          <iconpark-icon name="add-line" color="#1d2129"></iconpark-icon>
                        </div>
                    </div>
                    </template>
                    <div class="input-line-ctn">
                      <el-row :gutter="8">
                        <el-col :span="12">
                          <newInputList :isTooltip="true" toolTipContent="这是变量名" :type="'select-select'" :isBoth="true" toolTipLabel="变量名" />
                        </el-col>
                        <el-col :span="12">
                          <newInputList :isTooltip="true" toolTipContent="这是变量值" :type="'select-select'" :isBoth="true" toolTipLabel="变量值"/>
                        </el-col>
                      </el-row>
                      <div class="remove-icon">
                        <iconpark-icon name="indeterminate-circle-line" color="#1D2129" size="20"></iconpark-icon>
                      </div>
                    </div>
                    
                  </el-collapse-item> -->
                  <el-collapse-item name="2">
                      <template slot="title">
                        <div class="systemPromptTitle">
                          <div class="title-flex">
                            <span>{{ $t('prompt') }}</span>
                            <div class="icon-ctn">
                            <el-tooltip popper-class="workflow-tooltip" :content="'提示词'" placement="top" effect="light">
                              <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                            </el-tooltip>
                          </div>
                          </div>
                      </div>
                      </template>
                      <el-switch style=" position: absolute; right: 20px; top: 14px; "
                        v-model="appForm.promptOptimizer" active-color="#1747E5"
                        inactive-text="自动优化提示词">
                      </el-switch>
                      <el-input
                        type="textarea"
                        placeholder="在此输入生成图片的提示词，可以使用${变量名}引入变量"
                        v-model="appForm.aiVideoUserPrompt"
                        maxlength="2000"
                        show-word-limit
                        resize="none"
                        :rows="4"
                      ></el-input>
                  </el-collapse-item>

                   <!-- <el-collapse-item name="3" :title="$t('videoControl')">
                    <el-row :gutter="8">
                      <el-col :span="6">
                        <newInputList :isTooltip="true" toolTipContent="这是位置" :type="'select'" toolTipLabel="位置" :isSelectDisabled="true"/>
                      </el-col>
                      <el-col :span="18">
                        <newInputList :type="'select-upload'" :isBoth="true" toolTipLabel="图像"/>
                      </el-col>
                    </el-row>
                    <el-row :gutter="8" style="margin-top: 8px;">
                      <el-col :span="6">
                        <newInputList :type="'select'" :isLabel="false" :isSelectDisabled="true"/>
                      </el-col>
                      <el-col :span="18">
                        <newInputList :type="'select-select'" :isBoth="true" :isLabel="false"/>
                      </el-col>
                    </el-row>
  
                  </el-collapse-item> -->
                  <!-- <el-collapse-item name="4" :title="$t('pictureControl')">
                    <el-row :gutter="8">
                      <el-col :span="8">
                        <newInputList :isTooltip="true" toolTipContent="这是主体参考" :type="'select'" toolTipLabel="主体参考"/>
                      </el-col>
                      <el-col :span="24">
                        <newInputList :type="'upload'" toolTipLabel="参考图" :modelValue.sync="appForm.subjectImageUrl"/>
                      </el-col>
                    </el-row>
                    <el-row :gutter="8" style="margin-top: 8px;">
                      <el-col>
                        <newInputList :isTooltip="true" toolTipContent="这是首帧控制" :type="'upload'" toolTipLabel="首帧控制" :modelValue.sync="appForm.imageUrl" />
                      </el-col>
                    </el-row>
                  </el-collapse-item> -->
                  <el-collapse-item name="5" :title="$t('videoParams')">
                    <el-row :gutter="8">
                      <el-col :span="12">
                        <newInputList :isTooltip="true" toolTipContent="这是视频分辨率" :type="'select'" toolTipLabel="视频分辨率" :options="resolutionOptions" :optionsValue.sync="appForm.resolution" tip="请选择分辨率"/>
                      </el-col>
                      <el-col :span="12">
                        <newInputList :isTooltip="true" toolTipContent="这是图像比例" :type="'select'" toolTipLabel="宽高比" :options="ratioOptions" :optionsValue.sync="appForm.ratio" tip="请选择宽高比"/>
                      </el-col>
                    </el-row>
                    <el-row :gutter="8" style="margin-top: 8px;">
                      <el-col :span="12">
                        <newInputList :isTooltip="true" toolTipContent="这是生成视频时长" :type="'select'" toolTipLabel="生成视频时长" :options="durationOptions" :optionsValue.sync="appForm.duration" tip="请选择视频时长"/>
                      </el-col>
                      <el-col :span="12">
                        <newInputList :isTooltip="true" toolTipContent="这是帧率" :type="'select'" toolTipLabel="帧率" :options="framepersecondOptions" :optionsValue.sync="appForm.framepersecond" tip="请选择帧率"/>
                      </el-col>
                    </el-row>
                    <el-row :gutter="8" style="margin-top: 8px;">
                      <el-col>
                        <newInputList :isTooltip="true" toolTipContent="这是固定摄像头" :type="'select'" toolTipLabel="固定摄像头" :options="camerafixedOptions" :optionsValue.sync="appForm.camerafixed" tip="请选择是否固定摄像头"/>
                      </el-col>
                    </el-row>
                    <!-- <inputList  v-if="drawerVisible" :inputs.sync="appForm.videoParams||videoParams" :single="true"
                    :key="inputKey" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList> -->
                  </el-collapse-item>
                  
                  <el-collapse-item name="6" :title="$t('output')">
                    <!-- <div class="result-cont">
                        status
                        <div class="icon-ctn"> 
                          <el-tooltip popper-class="workflow-tooltip" :content="''" placement="top" effect="light">
                            <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                          </el-tooltip>
                        </div>
                        <span>String</span>
                    </div>
                    <div class="result-cont">
                        content.video_url
                        <div class="icon-ctn"> 
                          <el-tooltip popper-class="workflow-tooltip" :content="''" placement="top" effect="light">
                            <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                          </el-tooltip>
                        </div>
                        <span>String</span>
                    </div> -->
                    <div class="result-cont" style="margin: 0;">
                        result
                        <div class="icon-ctn"> 
                          <el-tooltip popper-class="workflow-tooltip" :content="''" placement="top" effect="light">
                            <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                          </el-tooltip>
                        </div>
                        <span>string</span>
                    </div>
                  </el-collapse-item>
              </el-collapse>
          </div>
      </el-drawer>
      <addSensitiveDialog
          v-if="addSensitiveVisible"
          :dialogVisible="addSensitiveVisible"
          @clickConfig="clickConfig"
          :configData="interceptWordHouses"
          @clickConfigParams="clickConfigParams"
          :appConfigForm="params.applicationInfo"
      ></addSensitiveDialog>
  </div>
</template>

<script>
// mixins
import drawerMixins from "./drawerMixins";
import {modelList} from "@/api/app.js"
export default {
  name: "aiVideoDrawer",
  components: {  },
  props: ["panels", "params"],
  mixins: [drawerMixins],
  data() {
      return {
          configurations: [],
          interceptWordHouses: [],
          startConfigurations: [
              {
                  interceptWordType: "",
                  interceptWordContent: "",
              },
          ],
          // startAppForm:{
          //   inputs:[ { "inputType": 1, "defaultChName":"分辨率", "selectedGroup": "", "cusInput": "", "label": "resolution", "lan": "", "value": "", "type": "" }, { "inputType": 1, "defaultChName":"宽高比", "selectedGroup": "", "cusInput": "", "label": "ratio", "lan": "", "value": "", "type": "" }, { "inputType": 1, "defaultChName":"时长", "selectedGroup": "", "cusInput": "", "label": "duration", "lan": "", "value": "", "type": "" },{ "inputType": 1, "defaultChName":"帧率", "selectedGroup": "", "cusInput": "", "label": "framepersecond", "lan": "", "value": "", "type": "" },{ "inputType": 1, "defaultChName":"固定摄像头", "selectedGroup": "", "cusInput": "", "label": "camerafixed", "lan": "", "value": "", "type": "" } ]
          // },
		  visiblePopover: false,
          appForm:{
			  multiDialogueNum:3,
			  maxToken:'',
			  modelAnswerFlag: true,
            inputs:[],
            aiVideoUserPrompt:"",
            promptOptimizer:false,
            subjectImageUrl:{},
            imageUrl:{},
            resolution:"",
            ratio:"",
            duration:"",
            framepersecond:"",
            camerafixed:""
            // videoParams:[ { "inputType": 1, "defaultChName":"分辨率", "selectedGroup": "", "cusInput": "", "label": "resolution", "lan": "", "value": "", "type": "" }, { "inputType": 1, "defaultChName":"宽高比", "selectedGroup": "", "cusInput": "", "label": "ratio", "lan": "", "value": "", "type": "" }, { "inputType": 1, "defaultChName":"时长", "selectedGroup": "", "cusInput": "", "label": "duration", "lan": "", "value": "", "type": "" },{ "inputType": 1, "defaultChName":"帧率", "selectedGroup": "", "cusInput": "", "label": "framepersecond", "lan": "", "value": "", "type": "" },{ "inputType": 1, "defaultChName":"固定摄像头", "selectedGroup": "", "cusInput": "", "label": "camerafixed", "lan": "", "value": "", "type": "" } ],
          },
          startAppForm:{"resolution": "480p", "ratio": "16:9", "duration": "5", "framepersecond": "16", "camerafixed": true },
          // videoParams:[ { "inputType": 1, "defaultChName":"分辨率", "selectedGroup": "", "cusInput": "", "label": "resolution", "lan": "", "value": "", "type": "" }, { "inputType": 1, "defaultChName":"宽高比", "selectedGroup": "", "cusInput": "", "label": "ratio", "lan": "", "value": "", "type": "" }, { "inputType": 1, "defaultChName":"时长", "selectedGroup": "", "cusInput": "", "label": "duration", "lan": "", "value": "", "type": "" },{ "inputType": 1, "defaultChName":"帧率", "selectedGroup": "", "cusInput": "", "label": "framepersecond", "lan": "", "value": "", "type": "" },{ "inputType": 1, "defaultChName":"固定摄像头", "selectedGroup": "", "cusInput": "", "label": "camerafixed", "lan": "", "value": "", "type": "" } ],
          imageUrl:"",
          addSensitiveVisible: false,
          largeModelList:[],
          modelValue:"",
          imagePrompt:"",
          promptOptimizer:false,
          activeNames:["0","1","2","3","4","5","6"],
          manufacturer:"",
          resolutionOptions:[
            {
              value:"480p",
              label:"480p"
            },
            {
              value:"720p",
              label:"720p",
            },
            {
              value:"1080p",
              label:"1080p"
            }
          ],
          ratioOptions:[
            {
              label:"1024×1024（1:1）",
              value:"1:1"
            },
            {
              label:"864×1152（3:4）",
              value:"3:4"
            },
            {
              label:"1152×864（4:3）",
              value:"4:3"
            },
            {
              label:"1280×720（16:9）",
              value:"16:9"
            },
            {
              label:"720×1280（9:16）",
              value:"9:16"
            },
            {
              label:"832×1248（2:3）",
              value:"2:3"
            },
            {
              label:"1248×832（3:2）",
              value:"3:2"
            },
            {
              label:"1512×648（21:9）",
              value:"21:9"
            },
          ],
          durationOptions:[
            {
              label:"5",
              value:"5"
            },
            {
              label:"10",
              value:"10"
            }
          ],
          framepersecondOptions:[
            {
              label:"16",
              value:"16"
            },
            {
              label:"24",
              value:"24"
            }
          ],
          camerafixedOptions:[
            {
              label:"是",
              value:true
            },
            {
              label:"否",
              value:false
            }
          ]

      };
  },
  watch: {
      configurations: {
          handler(newVal, oldVal) {
              this.$emit(
                  "updateAppForm",
                  { configurations: JSON.stringify(newVal) },
                  this.curNode
              );
          },
          deep: true,
      },
      // videoParams: {
      //     handler(newVal, oldVal) {
      //         this.appForm.videoParams=[...this.videoParams]
      //     },
      //     deep: true,
      //     immediate:true
      // },
  },
  mounted() {
      this.getLargeModel()
  },
  beforeDestroy() {},
  computed:{
    getModelImage(){
      let obj=this.largeModelList.find(item=>item.modelId==this.appForm.modelId)
      return obj?obj.manufacturer:""
    }
  },
  methods: {
      clickConfigParams(type, data, selectList) {
          this.interceptWordHouses = data;
          if (selectList.length > 0) {
              this.configurations = selectList || [];
              this.$EventBus.$emit("saveWorkflow");
          }
      },
      openDialog() {
          this.addSensitiveVisible = true;
      },
      clickConfig() {
          this.addSensitiveVisible = false;
      },
      removeItem(index) {
          this.configurations.splice(index, 1);
          this.$EventBus.$emit("saveWorkflow");
      },
      openDrawer() {
          this.initDrawer();
          this.configurations = this.nodeStoreData.configurations
              ? JSON.parse(this.nodeStoreData.configurations)
              : [];
          this.interceptWordHouses = this.configurations.map(
              (item) => item.id
          );
      },
      getLargeModel(){
        modelList({
          modelName: "",
          status: "",
          manufacturer: "",
          tag:"视频",
          pageSize: 999,
          pageNo: 1,
          ownerType:'all'
        }).then(res=>{
          // console.log(res,"res");
          if(res.code=='000000'){
            this.largeModelList=[...res.data]
            // console.log(this.largeModelList,"item.manufacturer");
          }
        })
      },
      changeModel(val){
        console.log(val,"val");
        let obj=this.largeModelList.find(item=>item.modelId==this.appForm.modelId);
        this.appForm.modelId=obj.modelId
        this.appForm.modelInfo={...obj}
        this.manufacturer=obj.manufacturer
      }
      
  },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style scoped lang="scss">
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
.base-li {
  height: 40px;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #e1e4eb;
  padding: 0 12px;
  margin-bottom: 8px;

  .li-name {
      font-weight: 400;
      font-size: 14px;
      color: #383d47;
      text-align: left;
      font-style: normal;

      > img {
          width: 22px;
          height: 22px;
          color: #a4bffe;
          margin-right: 5px;
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

.input-add{
  width: 32px;
  height: 32px;
  color: #1D2129; 
  background: rgba(188,193,204,0.2);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.input-line-ctn{
  display: flex;
  align-items: flex-end;
  gap: 8px;


  .remove-icon{
    width: 32px;
    height: 32px;
    display: flex;
    align-content: center;
  }
}

.result-cont{
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  .icon-ctn{
  height: 16px;
  line-height: 19px;
  position: relative;
}
  span{
    display: inline-block;
    height: 20px;
    line-height: 20px;
    padding: 0 5px;
    background: #EBEEF2;
    border-radius: 2px;
    color: #1D2129;
    font-family: MiSans Regular;
  }
}

::v-deep .el-switch__label{
  span{
    color: #1D2129 !important;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
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
