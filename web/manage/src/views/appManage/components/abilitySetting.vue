<template>
  <div class="outerDialog">
    <div class="headBar">
      <div class="leftSlide">
        <div class="closeImg" @click="closeDialog">
          <img src="@/assets/images/arrow-go-back-fill.svg" />
        </div>
        <div class="titleIcon">
          <div class="title-headImg" v-if="appForm.facadeImageUrl">
            <img :src="appForm.facadeImageUrl" alt="" />
          </div>
          <div class="title-info">
            <p :title="appForm.applicationName || $t('noApplicationName')" class="tit">
              {{ appForm.applicationName || $t("noApplicationName") }}
              <img v-if="isAdminOrUser"
                src="@/assets/images/appManagement/edit-line.svg"
                class="edit-icon"
                @click="openDialog('editName')"
              />
            </p>
            <p :title="appForm.introduce || $t('noDescription')" class="desc">
              {{ appForm.introduce || $t("noDescription") }}
            </p>
          </div>
        </div>
      </div>
      <div class="centerSlide" v-if="isAdminOrUser">
        <div class="head-tabs" v-if="headTabs.length > 1">
          <div
            v-for="(tab, index) in headTabs"
            :key="index"
            class="tab-item"
            :class="{ active: tab.value === activeHeadTab }"
            @click="handleTabClick(tab.value)"
          >
            <!-- <iconpark-icon class="icon" v-if="tab.icon" :name="tab.icon" :color="tab.value === activeHeadTab ? '#494E57' : '#828894'" size="16"></iconpark-icon> -->
            {{ tab.label }}
          </div>
        </div>
      </div>
      <div class="rightSlide">
        <el-button
          class="btn"
          type="text"
          :loading="loading2"
          :disabled="!isAdminOrUser"
          v-if="
            activeHeadTab == 1 &&
            (appForm.type === 'workflow' || appForm.type === 'dialogue' || appForm.type === 'multi_agent')
          "
          style="font-size: 16px;color: #494E57;width: 92px;border-radius: 2px;border: 1px solid #C9CCD1;display: flex;align-items-center;justify-content: center;"
          @click="submitWorkflow(2)"
        >
          <iconpark-icon
            name="save-line"
            size="18"
            color="#494E57"
            style="margin-right: 6px"
          ></iconpark-icon>
          <span>{{ $t("save") }}</span>
        </el-button>
        <el-button
          class="btn"
          type="text"
          :disabled="!isAdminOrUser"
          :loading="loading2"
          v-else-if="activeHeadTab == 1 && appForm.type === 'text-agent'"
          style="font-size: 16px;color: #494E57;width: 92px;border-radius: 2px;border: 1px solid #C9CCD1;display: flex;align-items-center;justify-content: center;"
          @click="textAgentSave"
        >
          <iconpark-icon
            name="save-line"
            size="18"
            color="#494E57"
            style="margin-right: 6px"
          ></iconpark-icon>
          <span>{{ $t("save") }}</span>
        </el-button>
        <el-button
          :disabled="!isAdminOrUser"
          v-else-if="activeHeadTab == 1"
          class="btn"
          :loading="loading2"
          type="text"
          style="font-size: 16px;color: #494E57;width: 92px;border-radius: 2px;border: 1px solid #C9CCD1;display: flex;align-items-center;justify-content: center;"
          @click="temporarSave(2)"
        >
          <iconpark-icon
            name="save-line"
            size="18"
            color="#494E57"
            style="margin-right: 6px"
          ></iconpark-icon>
          <span>{{ $t("save") }}</span>
        </el-button>
        <!-- <el-button
          class="btn btns"
          v-permission="'issue'"
          v-if="appForm.type === 'workflow' || appForm.type === 'dialogue'"
          @click="submitWorkflow(2)"
        >
          <img src="@/assets/images/send-plane-fill.svg" />
          <span>{{ ['1','2'].includes(appForm.publishStatus) ? '更新' : '发布'}}</span>
        </el-button>
        <el-button
          class="btns"
          v-else
          v-permission="'issue'"
          @click="openDialog('fabu')"
        >
          <img src="@/assets/images/send-plane-fill.svg" />
          <span>{{ ['1','2'].includes(appForm.publishStatus) ? '更新' : '发布'}}</span>
        </el-button> -->
      </div>
    </div>
    <div
      class="main-content"
      v-loading="typeLoading"
      v-show="activeHeadTab === 1"
    >
      <div class="dialogPower" v-if="appForm.type === 'workflow'">
        <dragDemo
          :ref="'dragDemo' + appForm.type"
          :params="component"
          :tabs="isAdminOrUser? workflowTabs:personWorkTabs"
          :key="dragDemoKey"
          :type="'workflow'"
          v-if="showDemo && appForm.type === 'workflow'"
          :canvas="component?.canvas"
        ></dragDemo>
      </div>
      <div class="dialogPower" v-if="appForm.type === 'dialogue'">
        <dragDemo
          :params="component"
          :tabs="isAdminOrUser?tabs:personDialogTabs"
          :ref="'dragDemo' + appForm.type"
          :key="dragDemoKey"
          :type="'dialogue'"
          v-if="showDemo && appForm.type === 'dialogue'"
          :canvas="component?.canvas"
        ></dragDemo>
      </div>
      <div class="dialogPower" v-if="appForm.type === 'multi_agent'">
        <dragDemo
          :params="component"
          :tabs="tabs"
          :ref="'dragDemo' + appForm.type"
          :key="dragDemoKey"
          :type="'multi_agent'"
          v-if="showDemo && appForm.type === 'multi_agent'"
          :canvas="component?.canvas"
        ></dragDemo>
      </div>
      <div class="dialogPower" v-if="appForm.type === 'text-agent'">
        <AgentPattern
          class="qieHuanKuang"
          ref="llmRef"
          :model="appForm.type"
          @updateModel="updateAgentPattern"
        >
        </AgentPattern>
        <modelAlgorithmPlugin
          ref="modelAlgorithmPluginRefDom"
          :appConfigForm="appForm"
          :headBarShow="false"
        >
        </modelAlgorithmPlugin>
      </div>

      <div class="dialogPower" v-show="appForm.type === 'qa'">
        <div class="left-tabs-content" ref="tabContainer">
          <div class="tabs-left">
            <AgentPattern ref="llmRef" :model="appForm.type" @updateModel="updateAgentPattern" :hideLabel="isHidLabel"></AgentPattern>
            <div class="tab-line"></div>
            <!-- 模型设置 -->
            <ModelSetting ref="modelSettingRef" :params="appForm" @switchChange="modelSettingChange" :hideLabel="isHidLabel"></ModelSetting>
            <el-popover
              style="border: none;"
              placement="bottom-start"
              :visible-arrow="false"
              trigger="click">
              <el-button
              slot="reference"
              type="text"
              ><iconpark-icon name="settings-4-line" size="16" color="#1d2129"></iconpark-icon></el-button
            >
            
            <div class="knowledgeset-ctn">
              <div class="knowledgeset-title-ctn">
                <div class="knowledgeset-title">
                  模型设置
                </div>
                <div class="advancedset-ctn" @click.stop="openDialog('setBase')">
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
                    <el-switch v-model="appForm.modelAnswerFlag"active-color="#1747E5"
                inactive-color="#B4BCCC"  />
                    <!-- <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultValue('filterNum', 10)">
                      <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
                    </span> -->
                  </div>
                </div>
              </div>
            </div>
            
              
            </el-popover>
		  </div>

          <div
            class="tabs-right-tool"
            :class="{ active: visibleChange }"
            @click="openFunctionOrTool"
          >
            <!-- 功能或插件 -->
            <!-- <FuntionOrTool
              ref="functionOrToolRef" 
              :funcOrToolArr="funcOrTool"
              :params="appForm"
              :pluginList="pluginList"
              @clickConfig="clickConfig"
              @clickConfigParams="clickConfigParams"
              @changeStatus="changeStatus"
              @addPluginDataEmit="addPluginDataEmit"></FuntionOrTool> -->
            <iconpark-icon class="btn-icon" name="function-add-fill" color="#1747E5" size="16"></iconpark-icon><span class="add-word">{{ $t("function") }}</span> 
          </div>
        </div>
        <div class="left-content">
          <div style="border: 0">
            <div class="abilityTabs">
              <div class="tabs-box">
                <div class="tabs-left">
                  <!-- <span
                    class="tabs"
                    :class="{ active: activeTabsName == 'first' }"
                    @click="activeTabsName = 'first'"
                    >{{ $t("roleSetting") }}</span
                  > -->
                  <!-- <span
                    class="tabs"
                    :class="{ active: activeTabsName == 'second' }"
                    @click="activeTabsName = 'second'"
                    >{{ $t("commandTemplate") }}</span
                  > -->
                  <span class="tabs active">{{$t("roleResponseSetting")}}</span>
                </div>
                <div class="tabs-right">
                  <el-button
                    v-if="activeTabsName == 'first'"
                    class="btn btn2"
                    type="text"
                    :disabled="activeTabsName == 'second'"
                    @click="openVocabularyDrawer"
                  >
                    <iconpark-icon
                      class="icon"
                      name="pages-line"
                      size="16"
                    ></iconpark-icon
                    >{{ $t("promptLibrary") }}
                  </el-button>
                  <el-popover
                    style="border: none;"
                    placement="bottom-start"
                    v-model="roleFlag"
                    trigger="click">
                    <el-button
                    slot="reference"
                    v-if="activeTabsName == 'first'"
                    class="btn btn1"
                    type="text"
                    :disabled="activeTabsName == 'second'"
                    :loading="autoOptimizationLoading"
                  >
                    <img src="@/assets/images/ai-btn.svg" alt="" />{{
                      $t("autoOptimize")
                    }}
                  </el-button>

                  <div class="autoOp-ctn">
                    <div class="app-create" @click="autoOptimization(appForm.applicationName)">
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

                  <!-- <el-button
                    v-if="activeTabsName == 'first'"
                    class="btn btn1"
                    type="text"
                    :disabled="activeTabsName == 'second'"
                    :loading="autoOptimizationLoading"
                    @click="autoOptimization"
                  >
                    <img src="@/assets/images/ai-btn.svg" alt="" />{{
                      $t("autoOptimize")
                    }}
                  </el-button> -->
                </div>
              </div>
              <div class="tabs-content">
                <el-input
                  v-show="activeTabsName == 'first'"
                  :placeholder="roleSettingPlaceholder"
                  v-model="appForm.systemPrompt"
                  show-word-limit
                  type="textarea"
                  :autosize="{ minRows: 6, maxRows: 12 }"
                  maxlength="3000"
                  @change="handleIpnutChange"
                ></el-input>
                <!-- <el-input
                  v-show="activeTabsName == 'second'"
                  :placeholder="$t('inputPlaceholder')"
                  type="textarea"
                  :autosize="{ minRows: 6, maxRows: 12 }"
                  maxlength="3000"
                  v-model="appForm.promptTemplate"
                  @change="handleIpnutChange"
                ></el-input> -->
              </div>
            </div>
          </div>
          <div class="marginTop16">
            <config-group
              :label="$t('knowledgeBase')"
              :showTip="false"
              :isArrowLeft="true"
              style="border: 0"
            >
              <template v-slot:button>
                <div class="flex-center config-group-button">
                  <!-- <el-button
                    type="text"
                    icon="el-icon-s-operation"
                    @click.stop="openDialog('setBase')"
                    >{{ $t("parameterSettings") }}</el-button
                  > -->
                  <el-popover
                    style="border: none;display: inline-block;height: 24px;"
                    placement="bottom-start"
                    :visible-arrow="false"
                    v-if="appForm?.knowledgeIds?.length"
                    trigger="click">
                    <el-button
                    slot="reference"
                    type="text"
                    ><iconpark-icon name="settings-4-line" size="16" color="#1d2129"></iconpark-icon></el-button
                  >

                  <div class="knowledgeset-ctn">
                    <div class="knowledgeset-title-ctn">
                      <div class="knowledgeset-title">
                        知识库设置
                      </div>
                      <div class="advancedset-ctn" @click.stop="openDialog('setBase')" v-if="isAdmin">
                        <div class="advancedset-word">高级设置</div>
                        <div class="advancedset-icon">
                          <iconpark-icon name="arrow-right-double-fill" size="16" color="#86909C"></iconpark-icon>
                        </div>
                      </div>
                    </div>

                    <div class="knowledgeset-options">
                      <div class="knowledgeset-options-list">
                        <div class="left-ctn">
                          <div class="words">重排模型</div>
                          <div class="tooltip-ctn"> 
                            <el-tooltip popper-class="workflow-tooltip-dark" content="对初步检索出的候选内容进行二次排序，提升传递给大模型的信息质量。" placement="top" effect="light">
                              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                            </el-tooltip>
                          </div>
                        </div>
                        <div class="knowledgeset-model">
                          <el-select v-model="appForm.rearrangeModel" style="width: 100%;" size="small" placeholder="请选择">
                            <el-option label="雅意" value="yayi"></el-option>
                            <el-option label="火山引擎" value="volcengine"></el-option>
                          </el-select>
                        </div>
                      </div>
                      <div class="knowledgeset-options-list">
                        <div class="left-ctn">
                          <div class="words">检索内容匹配度</div>
                          <div class="tooltip-ctn"> 
                            <el-tooltip popper-class="workflow-tooltip-dark" content="对检索出的候选内容根据设定的匹配度召回并提供给大模型，低于设定值的段落将不被召回。" placement="top" effect="light">
                              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                            </el-tooltip>
                          </div>
                        </div>
                        <div class="right-ctn">
                          <el-slider
                            v-model="appForm.contentScore"
                            :min="0"
                            :max="10"
                            :step="0.1"
                          ></el-slider>
                          <el-input-number
                            class="score-input"
                            v-model="appForm.contentScore"
                            controls-position="right"
                            :min="0"
                            :max="10"
                            :step="0.1"
                            size="small"
                            style="width: 80px"
                          ></el-input-number>
                          <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultValue('contentScore', 1.49)">
                            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
                          </span>
                        </div>
                      </div>
                      <div class="knowledgeset-options-list">
                        <div class="left-ctn">
                          <div class="words">重排内容匹配度</div>
                          <div class="tooltip-ctn"> 
                            <el-tooltip popper-class="workflow-tooltip-dark" content="对检索出的候选内容进行重新排序时，根据设定的匹配度召回并提供给大模型，低于设定值的段落将不被召回。" placement="top" effect="light">
                              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                            </el-tooltip>
                          </div>
                        </div>
                        <div class="right-ctn">
                          <el-slider
                            v-model="appForm.rangeContentScore"
                            :min="0"
                            :max="10"
                            :step="0.1"
                          ></el-slider>
                          <el-input-number
                            class="score-input"
                            v-model="appForm.rangeContentScore"
                            controls-position="right"
                            :min="0"
                            :max="10"
                            :step="0.1"
                            size="small"
                            style="width: 80px"
                          ></el-input-number>
                          <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultValue('rangeContentScore', 1.49)">
                            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
                          </span>
                        </div>
                      </div>
                      <div class="knowledgeset-options-list">
                        <div class="left-ctn">
                          <div class="words">最大召回数量</div>
                          <div class="tooltip-ctn"> 
                            <el-tooltip popper-class="workflow-tooltip-dark" content="从知识库中召回并提供给大模型的段落数量上限。" placement="top" effect="light">
                              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                            </el-tooltip>
                          </div>
                        </div>
                        <div class="right-ctn">
                          <el-slider
                            v-model="appForm.filterNum"
                            :min="0"
                            :max="10"
                            :step="0.1"
                          ></el-slider>
                          <el-input-number
                            class="score-input"
                            v-model="appForm.filterNum"
                            controls-position="right"
                            :min="0"
                            :max="10"
                            :step="0.1"
                            size="small"
                            style="width: 80px"
                          ></el-input-number>
                          <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultValue('filterNum', 10)">
                            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>

                    
                  </el-popover>
                  <!-- <el-button
                    type="text"
                    @click.stop="openDialog('setBase')"
                    ><iconpark-icon name="settings-4-line" size="16" color="#1d2129"></iconpark-icon></el-button
                  > -->
                  <el-button
                    type="text"
                    @click="openDialog('addBase')"
                  ><iconpark-icon name="add-line" size="16" color="#1d2129"></iconpark-icon></el-button>
                </div>
              </template>
              <div
                class="config-group-tip"
                v-if="!appForm?.knowledgeIds?.length"
              >
                用户发送消息时，智能体能够引用所选知识中的内容回答用户问题
              </div>
              <div
                v-if="appForm?.knowledgeIds?.length"
                :style="appForm?.knowledgeIds?.length ? 'margin-top: 12px' : ''"
              >
                <ul>
                  <li
                    class="base-li flex-center just"
                    v-for="(item, index) in appForm.knowledgeIds"
                    :key="index"
                    @click="konwlwdgeClick(item.knowledgeId)"
                  >
                    <div class="li-name flex-center">
                      <img src="@/assets/images/appManagement/zsk.svg" />
                      <!-- 新版本UIbug1 -->
                      <span>{{ item.knowledgeName }}</span>
                    </div>
                    <iconpark-icon
                      v-if="
                        konwlwdgeList.some(
                          (el) => el.knowledgeId == item.knowledgeId
                        )
                      "
                      class="delete-icon"
                      name="delete-bin-4-line"
                      size="16"
                      color="#494E57"
                      style="cursor: pointer"
                      @click.stop="deleteKnowledgeId(item)"
                    ></iconpark-icon>
                    <span class="konwlwdge-tip" v-else>知识库未授权</span>
                  </li>
                </ul>
              </div>
            </config-group>
          </div>
          <div class="marginTop16">
            <config-group
              :label="'MCP服务'"
              :showTip="false"
              :isArrowLeft="true"
              :isConfigLogo="true"
              :configLogoImg="mcpNewsLogo"
              style="border: 0"
            >
              <template v-slot:button>
                <div class="flex-center config-group-button">
                  
                  <el-button
                    type="text"
                    @click="openDialog('addMCP')"
                  ><iconpark-icon name="add-line" size="16" color="#1d2129"></iconpark-icon></el-button>
                </div>
              </template>
              <div
                class="config-group-tip"
                v-if="!appForm?.mcpServerIds?.length"
              >
                通过模型上下文协议（MCP）协议连接数据源或工具
              </div>
              <div
                v-if="appForm?.mcpServerIds?.length"
                :style="appForm?.mcpServerIds?.length ? 'margin-top: 12px' : ''"
              >
                <ul class="mcp-list-ctn">
                  <!-- <li
                    class="base-li flex-center just"
                    v-for="(item, index) in mcpServerIdList"
                    :key="index"
                    @click="konwlwdgeClick(item.knowledgeId)"
                  >
                    <div class="li-name flex-center">
                      <img :src="item.icon" />
                      <span>{{ item.mcpName }}</span>
                    </div>
                  </li> -->
                  <li class="mcp-list" v-for="(item, index) in mcpServerIdList" :key="index+'abmcpList'">
                    <div class="mcp-msg-ctn">
                      <div class="mcp-msg-left">
                        <img :src="item.icon" alt="" class="mcp-msg-img">
                        <div class="mcp-msg">
                          <div class="mcp-msg-title">
                            <div class="mcp-title">{{ item.mcpName }}</div>
                            <div class="mcp-official" v-if="item.ownerType=='official'">官方</div>
                          </div>

                          <div class="mcp-intro">{{ item.description || "暂无描述"}}</div>
                        </div>
                      </div>

                      <div class="mcp-msg-right">
                        <div class="icon-ctn remove" @click.stop="deleteMcp(index)">
                          <iconpark-icon name="indeterminate-circle-line" size="16" color="#1d2129"></iconpark-icon>
                        </div>
                        <div class="icon-ctn" v-if="item.mcpFunctionList&&item.mcpFunctionList.length" @click="expandTool(index,item)" :style="{transform:mcpHoverList[item.id] ?'rotate(180deg)':'rotate(0deg)'}">
                          <iconpark-icon name="arrow-down-s-line" size="16" color="#1d2129"></iconpark-icon>
                        </div>
                      </div>
                    </div>
                    <div class=mcp-tool-h :style="{'height': mcpHoverList[item.id] ? mcpHeightList[item.id]+'px' : '0px',
                      marginTop:mcpHoverList[item.id] ? '11px':0
                    }">
                      <div class="mcp-tool-ctn" v-show="item.mcpFunctionList&&item.mcpFunctionList.length" ref="mcptoolRef">
                        <div class="mcp-tool-list" v-for="(ele,index) in item.mcpFunctionList" :key="index+'toolmcp'">
                          <div class="mcp-tool-img">
                            <iconpark-icon name="tools-line" size="10" color="#1d2129"></iconpark-icon>
                          </div>
                          <div class="mcp-tool-msg">
                            <div class="mcp-tool-name">
                              {{ ele.functionName }}
                            </div>
                            <div class="mcp-tool-intro">{{ele.description || "暂无描述"}}</div>
                          </div>
                        </div>
                        
                      </div>
                    </div>
                    
                  </li>
                </ul>
              </div>
            </config-group>
          </div>
          <!-- <div class="marginTop16">
            <config-group 
              label="工作流" 
              :showTip="false"
            >
              <template v-slot:button>
                <div class="flex-center config-group-button">
                  <el-button
                    type="text"
                    icon="el-icon-plus"
                    @click="openDialog('addWorkflow')"
                    ></el-button
                  >
                </div>
              </template>
              <div class="config-group-tip" v-if="!appForm?.workflowIds?.length">
                可视化定制任务流程，精细设定执行顺序与触发条件，驱动智能体有序、协同完成复杂工作链路
              </div>
              <div v-if="appForm?.workflowIds?.length" :style="appForm?.workflowIds?.length ? 'margin-top: 12px' : ''">
                <ul>
                  <li
                    class="base-li flex-center just"
                    v-for="(item, index) in appForm.workflowIds"
                    :key="index"
                  >
                    <div class="li-name flex-center">
                      <img src="@/assets/images/appManagement/workflow.svg" />
                      <span>{{ item.componentName }}</span>
                    </div>
                    <iconpark-icon class="delete-icon" name="delete-bin-4-line" size="16" color="#494E57" style="cursor: pointer"
                    @click="deleteWorkflowId(item)"></iconpark-icon>
                  </li>
                </ul>
              </div>
            </config-group>
          </div> -->

          <!-- 功能版块内容 -->
          <!-- <div
            class="marginTop32"
            v-if="funcOrTool.length > 0 || mcpServerIdList.length > 0"
          > -->
          <div class="marginTop32">
            <div class="tabs-box-mcp">
              <p
                class="tabs-item"
                :class="{ active: activeName == 'first' }"
                @click="activeName = 'first'"
              >
                {{ $t("function") }} {{ funcOrTool.length?funcOrTool.length:"" }}
              </p>
              
              <!-- <p
                class="tabs-item"
                :class="{ active: activeName == 'mcp' }"
                @click="activeName = 'mcp'"
              >
                {{ $t("mcp") }} {{ mcpServerIdList.length?mcpServerIdList.length:"" }}
            </p> -->
            </div>
            <div v-if="activeName == 'first'">
              <!-- 虚拟人先隐藏  ||  funcOrTool.includes('virtual') -->
              <!-- <div class="boxName" v-if="funcOrTool.includes('experience') ||
                funcOrTool.includes('recommendation') || 
                funcOrTool.includes('answerSource') ||
                funcOrTool.includes('DisableIP') ||
                funcOrTool.includes('Authentication') ||
                funcOrTool.includes('TouchAnswer') ||
                funcOrTool.includes('voice') ||
                funcOrTool.includes('feedback')
              ">
              <span class="flex-title">{{ $t("function") }}</span>
            </div> -->
              <div class="dialog experience" v-if="funcOrTool.includes('experience')">
                <config-group
                  label="对话体验"
                  :icon="require('@/assets/images/appManagement/icon1.svg')"
                  tip="对话的相关设置，包括开场白、无法回答话术、免责声明等"
                  style="border: 0"
                >
                  <template v-slot:button>
                    <div class="flex-center config-group-button">
                      <el-popover
                        placement="right"
                        width="400"
                        popper-class="auto-fill-popover"
                        :visible-arrow="false"
                        v-model="autoFillVisible"
                        trigger="click"
                      >
                        <el-button
                          slot="reference"
                          class="experience-btn"
                          type="text"
                          @click="confirmTips('1')"
                          ><img src="@/assets/images/ai-btn.svg" alt="" />{{
                            $t("autoFill")
                          }}</el-button
                        >
                        <div class="auto-fill-content">
                          <div class="auto-fill-title">
                            <div class="title-left">
                              <iconpark-icon
                                class="title-icon"
                                name="alert-fill"
                                color="#F9A602"
                                size="20"
                              ></iconpark-icon>
                              <span>{{ $t("autoFill") }}</span>
                            </div>
                            <iconpark-icon
                              name="close-large-line"
                              @click="autoFillVisible = false"
                            ></iconpark-icon>
                          </div>
                          <div class="auto-fill-desc">
                            {{ $t("autoFillTips") }}
                          </div>
                          <div class="auto-fill-footer">
                            <el-button
                              type="primary"
                              @click="filldefaultValues(defaultValuesId)"
                              >{{ $t("confirm") }}</el-button
                            >
                            <el-button @click="autoFillVisible = false">{{
                              $t("cancel")
                            }}</el-button>
                          </div>
                        </div>
                      </el-popover>
                    </div>
                  </template>
                  <div>
                    <div class="marginTop10">
                      <div class="formTitle">
                        <span>{{ $t("openingStatement") }}</span>
                      </div>
                      <el-input
                        :placeholder="$t('inputPlaceholder')"
                        v-model="appForm.prologue"
                        @change="handleIpnutChange"
                      ></el-input>
                    </div>
                    <div class="marginTop10 init-ques">
                      <div class="flex-center just">
                        <div class="formTitle">
                          <span
                            >{{ $t("addInitialQuestion") }}（{{
                              appForm.presetQuestionList?.length
                            }}/4）</span
                          >
                        </div>
                      </div>
                      <div
                        style="margin-bottom: 8px"
                        v-for="(item, index) in appForm.presetQuestionList"
                        :key="'question' + index"
                      >
                        <el-input
                          class="question-input"
                          :placeholder="$t('inputPlaceholder')"
                          v-model="appForm.presetQuestionList[index]"
                          @change="handleIpnutChange"
                        >
                          <!-- <iconpark-icon
                            slot="prepend"
                            name="draggable"
                            color="#828894"
                          ></iconpark-icon> -->

                          <i
                            slot="suffix"
                            class="el-icon-delete"
                            style="
                              cursor: pointer;
                              margin-right: 10px;
                              padding-top: 12px;
                            "
                            @click="deleteQues(index)"
                          >
                          </i>
                        </el-input>
                      </div>
                      <div
                        v-if="appForm.presetQuestionList.length < 4"
                        class="flex-center add-ques"
                        style="cursor: pointer"
                        @click="addQues"
                      >
                        <iconpark-icon
                          class="icon"
                          name="add-line"
                          color="#1747E5"
                        ></iconpark-icon>
                        添加问题
                      </div>
                    </div>
                    <div class="marginTop10 flex-center just">
                      <div style="flex: 1">
                        <div class="formTitle">
                          <span>{{ $t("answerFailurePrompt") }}</span>
                          <!-- <img src="@/assets/images/question-line.svg" /> -->
                        </div>
                        <el-input
                          :placeholder="$t('inputPlaceholder')"
                          v-model="appForm.failureTalk"
                          maxlength="50"
                          show-word-limit
                          style="width: 100%"
                          @change="handleIpnutChange"
                        ></el-input>
                      </div>
                      <div style="width: 200px; margin-left: 12px" class="timeout">
                        <div class="formTitle">
                          <span>{{ $t("responseTimeout") }}</span>
                          <!-- <img src="@/assets/images/question-line.svg" /> -->
                        </div>
                        
                        <el-input-number
                          style="width: 100%"
                          v-model="appForm.answerTimeout"
                          :min="1"
                          :max="100000"
                          :step="1"
                          controls-position="right"
                          data-unit="秒"
                          class="my-el-input-number"
                          @change="handleIpnutChange"
                        >
                        </el-input-number>
                      </div>
                    </div>
                    <div class="marginTop10 flex" style="gap: 16px;">
                      <div style="flex: 1;">
                        <div class="formTitle">
                          <!-- <span>{{ $t("learningGrowthPhrases") }}</span> -->
                          <span>{{ $t("generatePromptWords") }}</span>
                          <!-- <img src="@/assets/images/question-line.svg" /> -->
                        </div>
                        <el-input
                          :placeholder="$t('inputPlaceholder')"
                          maxlength="100"
                          show-word-limit
                          v-model="appForm.tailTalk"
                          @change="handleIpnutChange"
                        ></el-input>
                      </div>
                      <div style="flex: 1;">
                        <div class="formTitle">
                          <span>{{ $t("disclaimer") }}</span>
                        </div>
                        <!-- <el-input
                          :placeholder="$t('inputPlaceholder')"
                          v-model="appForm.disclaimer"
                          maxlength="50"
                          show-word-limit
                          @change="handleIpnutChange"
                        ></el-input> -->
                        <!-- {{appForm.disclaimer}} -->
                        <div
                          class="flex-center edit-ques"
                          style="cursor: pointer"
                          @click="openEditorDialog()"
                        >
                        <p :title="appForm.disclaimer">{{ appForm.disclaimer?appForm.disclaimer:"编辑内容" }}</p>
                        <iconpark-icon
                            name="edit-box-line"
                            color="#1d2129"
                            size="16"
                            class="icon"
                          ></iconpark-icon
                          >
                        </div>
                      </div>
                    </div>
                    
                  </div>
                </config-group>
              </div>
              <div class="dialog" v-if="funcOrTool.includes('recommendation')">
                <config-group
                  :disabled="true"
                  label="问题建议"
                  :icon="require('@/assets/images/appManagement/icon2.svg')"
                  :tip="recommendationTip"
                  style="border: 0"
                >
                <template #button>
                  <div class="flex-center config-group-button">
                    <iconpark-icon name="settings-4-line" size="16" style="margin-left:12px;cursor: pointer;" color="#1d2129" @click="setQuestionImagineVisible=true"></iconpark-icon>
                  </div>
                </template>
                  <!-- <div class="marginTop10 flex-center recommendation-box">
                    <div class="formTitle recommendation-strategy">
                      <span class="title">{{
                        $t("recommendationStrategy")
                      }}</span>
                      <div>
                        <el-select
                          :placeholder="$t('pleaseSelect')"
                          v-model="appForm.recommendation"
                          style="width: 100%"
                          clearable
                          @change="handleIpnutChange"
                        >
                          <el-option
                            v-for="item in clOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          >
                          </el-option>
                        </el-select>
                      </div>
                    </div>
                    <div class="formTitle recommendation-question-count">
                      <span class="title">{{
                        $t("recommendedQuestionCount")
                      }}</span>
                      <div>
                        <el-input
                          :placeholder="$t('inputPlaceholder')"
                          v-model="appForm.recommendationNum"
                          type="number"
                          :step="1"
                          :min="0"
                          :max="20"
                          @change="handleIpnutChange"
                        ></el-input>
                      </div>
                    </div>
                  </div> -->
                </config-group>
              </div>
              <div class="dialog" v-if="funcOrTool.includes('answerSource')">
                <config-group
                  :disabled="true"
                  :label="$t('answerTraceability')"
                  :showTip="false"
                  :icon="require('@/assets/images/appManagement/icon5.svg')"
                  tip="将在回答中附带知识来源文档和生成内容的归属部分"
                  style="border: 0"
                >
                <template #button>
                  <div class="flex-center config-group-button">
                    <iconpark-icon name="settings-4-line" size="16" style="margin-left:12px;cursor: pointer;" color="#1d2129" @click="setAnswerSourceVisible=true"></iconpark-icon>
                  </div>
                </template>
                  <!-- <div class="marginTop10 flex-center recommendation-box">
                    <div class="formTitle recommendation-strategy">
                      <span class="title">{{ $t("documentLinkType") }}</span>
                      <div>
                        <el-select
                          :placeholder="$t('pleaseSelect')"
                          v-model="appForm.docLinkType"
                          style="width: 100%"
                          @change="handleIpnutChange"
                        >
                          <el-option
                            :label="$t('originalFileLink')"
                            value="1"
                          ></el-option>
                          <el-option
                            :label="$t('webPageLink')"
                            value="2"
                          ></el-option>
                        </el-select>
                      </div>
                    </div>
                    <div class="formTitle recommendation-question-count">
                      <span class="title">{{ $t("previewFile") }}</span>
                      <div>
                        <el-switch
                          v-model="appForm.previewDoc"
                          active-color="#4157FE"
                          inactive-color="#CED4E0"
                          active-value="是"
                          inactive-value="否"
                          @change="handleIpnutChange"
                        >
                        </el-switch>
                      </div>
                    </div>
                    <div class="formTitle recommendation-question-count">
                      <span class="title">{{ $t("indexOrNot") }}</span>
                      <div>
                        <el-switch
                          v-model="appForm.refIndexFlag"
                          active-color="#4157FE"
                          inactive-color="#CED4E0"
                          active-value="是"
                          inactive-value="否"
                          @change="handleIpnutChange"
                        >
                        </el-switch>
                      </div>
                    </div>
                  </div> -->
                </config-group>
              </div>
              <!-- v-if="funcOrTool.includes('DisableIP')" -->
              <div class="dialog" v-if="false">
                <config-group
                  :disabled="true"
                  :label="$t('disableIP')"
                  :icon="require('@/assets/images/appManagement/icon6.svg')"
                  tip="应用将检测用户IP地址，多次恶意问答的IP地址会被限制访问"
                  style="border: 0"
                >
                  <div style="color: #828894; margin: 5px 0px 0px 20px">
                    {{ $t("allowRestrictedAccessToIP") }}
                  </div>
                </config-group>
              </div>
              <!-- funcOrTool.includes('Authentication') -->
              <div class="dialog" v-if="false">
                <config-group
                  label="认证策略"
                  style="border: 0"
                  :icon="require('@/assets/images/appManagement/icon7.svg')"
                >
                  <div class="marginTop10">
                    <div class="flex-center recommendation-box-authentication">
                      <div
                        class="formTitle recommendation-strategy"
                        style="width: 49%"
                      >
                        <span class="title">{{
                          $t("h5AuthenticationMethod")
                        }}</span>
                        <div>
                          <el-select
                            style="width: 100%"
                            :placeholder="$t('pleaseSelect')"
                            v-model="appForm.clientAuthChannel"
                            clearable
                            @change="handleIpnutChange"
                          >
                            <el-option
                              v-for="item in h5AuthChannels"
                              :key="item.authChannelId"
                              :label="item.channelName"
                              :value="item.authChannelId"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </div>
                      <div class="formTitle" style="width: 49%">
                        <span class="title">{{
                          $t("pcAuthenticationChannel")
                        }}</span>
                        <div>
                          <el-select
                            style="width: 100%"
                            :placeholder="$t('pleaseSelect')"
                            v-model="appForm.pcAuthChannel"
                            clearable
                            @change="handleIpnutChange"
                          >
                            <el-option
                              v-for="item in pcAuthChannels"
                              :key="item.authChannelId"
                              :label="item.channelName"
                              :value="item.authChannelId"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </div>
                    </div>
                    <div class="marginTop10">
                      <el-switch
                        :active-text="$t('needTransferToHuman')"
                        v-model="appForm.toHumanFlag"
                        active-value="是"
                        inactive-value="否"
                        @change="handleIpnutChange"
                      ></el-switch>
                    </div>
                  </div>
                </config-group>
              </div>
              <div class="dialog" v-if="funcOrTool.includes('TouchAnswer')">
                <config-group
                  :label="$t('answerPolishing')"
                  :icon="require('@/assets/images/appManagement/icon10.svg')"
                  tip="大模型将会润色问答模版的输出内容"
                  style="border: 0"
                >
                  <div class="marginTop10 flex-center">
                    <el-input
                      :placeholder="detailedSettings"
                      v-model="appForm.polishPrompt"
                      show-word-limit
                      type="textarea"
                      :autosize="{ minRows: 6, maxRows: 12 }"
                      maxlength="3000"
                      @change="handleIpnutChange"
                    ></el-input>
                  </div>
                </config-group>
              </div>
              <div class="dialog" v-if="funcOrTool.includes('feedback')">
                <config-group
                  :disabled="true"
                  label="输出结果评价"
                  :icon="require('@/assets/images/appManagement/icon12.svg')"
                  tip="允许用户对输出结果进行评价反馈"
                  style="border: 0"
                >
                </config-group>
              </div>
              <div class="dialog" v-if="funcOrTool.includes('interception')">
                <config-group
                  style="border: 0"
                  :showTip="false"
                  :label="$t('safetyInterception')"
                  :icon="require('@/assets/images/appManagement/anquan.png')"
                >
                  <template v-slot:button>
                    <div class="flex-center config-group-button">
                      <el-button
                        type="text"
                        icon="el-icon-plus"
                        @click="openDialog('addSensitive')"
                      ></el-button>
                    </div>
                  </template>
                  <div class="marginTop10">
                    <!-- 敏感词 -->
                    <div
                      class="dialog plugin-item"
                      v-if="
                        funcOrTool.includes('interception') &&
                        appForm.interceptWordHouses?.length
                      "
                    >
                      <!-- <div class="flex-center just">
                <div class="flex-center">
                  <img
                    class="func-iconImg"
                    :src="require('@/assets/images/appManagement/anquanlanjie1.png')"
                  />
                  <div>
                    <span class="boxName">{{
                      $t("safetyInterception")
                    }}</span>
                    <p style="color: #828894; margin-top: 5px">
                      {{ $t("sensitiveWordLibraries") }}
                    </p>
                  </div>
                </div>
                <div class="flex-center" style="cursor: pointer;">
                  <el-button
                    class="tool-set-btn"
                    v-if="!appForm?.interceptWordHouses?.length"
                    type="text"
                    style="color: #494E57;"
                    @click="openDialog('addSensitive')"
                    ><iconpark-icon name="settings-4-line" size="16" color="#494E57"></iconpark-icon>{{ $t("settings") }}</el-button
                  >
                  <iconpark-icon name="indeterminate-circle-line" size="16" color="#494E57" style="margin-left: 12px" @click="deleteTool('interception')"></iconpark-icon>
                </div>
              </div> -->
                      <div style="margin-top: 10px">
                        <ul>
                          <li
                            class="base-li flex-center just base-li-anquan"
                            v-for="(item, index) in appForm.interceptWordHouses"
                            :key="index"
                          >
                            <div class="li-name flex-center">
                              <img
                                src="@/assets/images/appManagement/anquanlanjie1.png"
                              />
                              <div>
                                <div class="name">
                                  {{ filterInterceptWordHouses(item) }}
                                </div>
                                <div class="remark">
                                  {{ filterInterceptWordHousesRemark(item) }}
                                </div>
                              </div>
                            </div>
                            <iconpark-icon
                              class="delete-icon"
                              name="delete-bin-4-line"
                              size="16"
                              color="#494E57"
                              style="cursor: pointer"
                              @click="deleteInterceptWordHousesId(item)"
                            ></iconpark-icon>
                          </li>
                        </ul>
                        <!-- <el-button
                  size="small"
                  icon="el-icon-plus"
                  @click="openDialog('addSensitive')"
                  >添加</el-button
                > -->
                    </div>
                  </div>
                </div>
              </config-group>
            </div>
			<!-- 民政智能体新增 -->
			<!-- vagueQuestion  模糊问题引导 -->
			<div class="dialog" v-if="funcOrTool.includes('vagueQuestion')">
			  <config-group
          :disabled="true"
			    label="模糊问题引导"
			    :icon="require('@/assets/images/appManagement/icon10.svg')"
			    tip="用户问题意图不明确时将启用模糊问题引导"
          :showTip="false"
			    style="border: 0"
			  > 
          <template #button>
            <div class="flex-center config-group-button">
              <iconpark-icon name="settings-4-line" size="16" style="margin-left:12px;cursor: pointer;" color="#1d2129" @click="openDialog('setVagueQuestion')"></iconpark-icon>
            </div>
          </template>
			    <!-- <div class="marginTop10 flex-center formTitle recommendation-strategy">
			     <span class="title">判断用户输入的问题,问题意图不明时将推荐相关问题供用户选择</span>
			    </div> -->
			    <!-- <div class="marginTop10 flex-center">
			      <el-input
			        :placeholder="detailedSettings"
			        v-model="appForm.vagueConfig"
			        show-word-limit
			        type="textarea"
			        :autosize="{ minRows: 6, maxRows: 12 }"
			        maxlength="2000"
			        @change="handleIpnutChange"
			      ></el-input>
			    </div> -->
			  </config-group>
			</div>
			<!-- 问题联想 -->
			<div class="dialog" v-if="funcOrTool.includes('association')">
			  <config-group
          :disabled="true"
			    label="问题联想"
			    :icon="require('@/assets/images/appManagement/icon2.svg')"
			    tip="在输入问题时提供最多 5 条用户提问建议"
          :showTip="false"
			    style="border: 0"
			  >
        <template #button>
          <div class="flex-center config-group-button">
            <iconpark-icon name="settings-4-line" size="16" style="margin-left:12px;cursor: pointer;" color="#1d2129" @click="setAssociationVisible=true"></iconpark-icon>
          </div>
        </template>
			    <!-- <div class="marginTop10 flex-center recommendation-box">
			      <div class="formTitle recommendation-strategy">
			        <span class="title">{{
			          $t("recommendationStrategy")
			        }}</span>
			        <div>
			          <el-select
			            :placeholder="$t('pleaseSelect')"
			            v-model="appForm.association"
			            style="width: 100%"
			            clearable
			            @change="handleIpnutChange"
			          >
			            <el-option
			              v-for="item in clOptions"
			              :key="item.value"
			              :label="item.label"
			              :value="item.value"
			            >
			            </el-option>
			          </el-select>
			        </div>
			      </div>
			      <div class="formTitle recommendation-question-count">
			        <span class="title">{{
			          $t("recommendedQuestionCount")
			        }}</span>
			        <div>
			          <el-input
			            :placeholder="$t('inputPlaceholder')"
			            v-model="appForm.associationNum"
			            type="number"
			            :step="1"
			            :min="0"
			            :max="20"
			            @change="handleIpnutChange"
			          ></el-input>
			        </div>
			      </div>
			    </div> -->
			  </config-group>
			</div>
			<div class="dialog" v-if="funcOrTool.includes('quickCommand')">
			    <config-group
			      style="border: 0"
			      
			      :showTip="false"
			      label="快捷指令"
			      :icon="require('@/assets/images/appManagement/anquan.png')"
			    >
			      <template v-slot:button>
			        <div class="flex-center config-group-button">
			          <el-button
			            type="text"
			            icon="el-icon-plus"
			            @click="dialogVisibleApplicationFlag = true"
			          ></el-button>
			        </div>
			      </template>
			      <div class="marginTop10">
			        <!-- 敏感词 -->			             			          
			            <ul>
			              <li class="base-li flex-center just base-li-anquan" v-for="(item,index) in appForm.applicationQuickCommandList" :key="index">
			                  <div class="li-name flex-center">
			                    <img
			                      src="@/assets/images/appManagement/anquanlanjie1.png"
			                    />
			                    <div>
			                      <div class="name">
			                        {{ item.commandName }}
			                      </div>
			                      
			                    </div>
			                  </div>
			                  <iconpark-icon
			                    class="delete-icon"
			                    name="delete-bin-4-line"
			                    size="16"
			                    color="#494E57"
			                    style="cursor: pointer"
			                    @click="deleteApplicationQuickCommandList(index)"
			                  ></iconpark-icon>
			              </li>
			            </ul>			           			       
			      
			    </div>
			  </config-group>
			</div>
            <div class="dialog" v-if="funcOrTool.includes('voice')">
                            <!-- <el-popover popper-class="auto-fill-popover" placement="top" width="730" trigger="click"
                                style="position: relative; left: 10px; top: -172px;" :hide-on-click="true" ref="popoverRef"
                                v-model="visibleVoice" :visible-arrow="false">
                                <div class="popover-conten">
                                    <div class="popover-list-left">
                                        <div class="popover-list-tit">
                                            <h1 class="title">{{ $t("selectPronunciationPerson") }} </h1>
                                            <div class="title-rig">
                                                <h1 class="text-vo">{{$t("morePronunciationPerson") }}</h1>
                                                <iconpark-icon name="arrow-right-s-line" size="16" color="#86909C"
                                style="margin-left: 8px; cursor: pointer"></iconpark-icon>
                                            </div>
                                        </div>
                                        <div class="popover-list" v-for="(item, index) in voiceList" :key="index"
                                            :class="{ selected: selectedIndex === index }"
                                            @click="handelVoiceItem(item, index)">
                                            <div class="voice-info-left">
                                                <img style="width: 24px; height:24px;"
                                                    :src="require('@/assets/images/appManagement/icon4.svg')" alt="">
                                                <h1 class="voicev-text">{{ item.fullStyle }}</h1>
                                                <iconpark-icon name="volume-up-line" size="16" color="#494E57"
                                                    style="cursor: pointer"></iconpark-icon>
                                            </div>
                                            <iconpark-icon v-if="selectedIndex === index" name="check-line" size="16"
                                                color="#494E57" style="cursor: pointer"></iconpark-icon>
                                        </div>

                                    </div>
                                    <div class="popover-right">
                                        <div class="popover-list-tit">
                                            <h1 class="title">{{ $t('pronunciationSettings') }}</h1>
                                            <el-switch :active-text="$t('isStreamingPlay')"
                                                v-model="appForm.streamVoice" active-color="#4157FE"
                                                inactive-color="#CED4E0" active-value="是" inactive-value="否"
                                                @change="handleIpnutChange"></el-switch>
                                        </div>
                                        <div class="info-vo">
                                            <h1 class="intonation">{{ $t('intonationFast') }}</h1>
                                            <iconpark-icon name="question-line" size="16" color="#86909C"
                                                style="margin-left: 8px; cursor: pointer"></iconpark-icon>
                                        </div>
                                        <div class="info-vo-slider">
                                            <el-slider v-model="appForm.voiceSpeed" style="width: 210px; height: 4px;"
                                                :min="0" :max="10" :step="0.1" @input="handleSliderChange"></el-slider>
                                            <el-input-number class="score-input" style="width: 72px;"
                                                v-model="appForm.voiceSpeed" controls-position="right"
                                                @change="handleInputChange" :min="0" :max="10"
                                                :step="0.1"></el-input-number>
                                        </div>
                                        <div class="info-vo" style="margin-top:16px;">
                                            <h1 class="intonation">{{ $t('intonation') }}</h1>
                                            <iconpark-icon name="question-line" size="16" color="#86909C"
                                                style="margin-left: 8px; cursor: pointer"></iconpark-icon>
                                        </div>
                                        <div class="info-vo-slider">
                                            <el-slider v-model="appForm.pitch" style="width: 210px; height: 4px;"
                                                :min="0" :max="10" :step="0.1" @input="handlePitchChange"></el-slider>
                                            <el-input-number class="score-input" style="width: 72px;"
                                                v-model="appForm.pitch" controls-position="right"
                                                @change="handlePitchiInputChange" :min="0" :max="10"
                                                :step="0.1"></el-input-number>
                                        </div>
                                    </div>
                                </div>
                            </el-popover> -->
                            <config-group style="border:0;" :label="$t('voiceSettings')"
                                :icon="require('@/assets/images/appManagement/icon4.svg')">
                                <div class="flex" style="gap: 16px;margin-top: 12px;">
                                  
                                <div style="flex: 1;">
                                  <div class="voiceset-set">
                                      <el-switch v-model="isSound" active-color="#4157FE" inactive-color="#CED4E0"
                                        style="color: #000000;">
                                    </el-switch>
                                    <span>{{$t('enableVoiceBroadcast')}}</span>
                                    </div>
                                    <div class="flex-center recommendation-box-authentication voice-bot" @click="handelVoice"
                                        ref="popoverBtnRef" style="margin-top: 8px; cursor:pointer;">
                                        <div class="voice-left">
                                            <img style="width: 20px; height:20px; border-radius: 50%;" v-show="voiceTitle!='请选择发音'"
                                                :src="require('@/assets/images/appManagement/icon4.svg')" alt="">
                                            <h1 class="voicev-text">{{ voiceTitle }}</h1>
                                            <iconpark-icon name="volume-up-line" size="16" color="#494E57" v-show="voiceTitle!='请选择发音'"
                                                style="margin-left: auto;cursor: pointer"></iconpark-icon>
                                        </div>
                                        <div class="voice-left">
                                            <h1 class="voicev-text intonation" v-if="!isHidLabel" v-show="voiceTitle!='请选择发音'">{{ $t('intonationFast') }}：{{appForm.voiceSpeed}}</h1>
                                            <h1 class="voicev-text intonation" v-if="!isHidLabel" v-show="voiceTitle!='请选择发音'">{{ $t('intonation') }}：{{ appForm.pitch }}</h1>
                                            <iconpark-icon name="settings-4-line" size="16" color="#494E57"
                                                style="margin-left: 8px;cursor: pointer"></iconpark-icon>
                                        </div>
                                        <!-- <div class="formTitle recommendation-strategy" style="width: 49%">
                        <span class="title">{{ $t("textToSpeech") }}</span>
                        <div>
                          <el-select :placeholder="$t('pleaseSelect')" v-model="appForm.ttsId" style="width: 100%"
                            @change="handleIpnutChange">
                            <el-option v-for="item in ttsListData" :key="item.id" :value="item.id"
                              :label="item.componentName"></el-option>
                          </el-select>
                        </div>
                      </div>
                      <div class="formTitle" style="width: 49%">
                        <span class="title">{{ $t("speechToText") }}</span>
                        <div>
                          <el-select :placeholder="$t('pleaseSelect')" v-model="appForm.sttId" style="width: 100%"
                            @change="handleIpnutChange">
                            <el-option v-for="item in ttsListData" :key="item.id" :value="item.id"
                              :label="item.componentName"></el-option>
                          </el-select>
                        </div>
                      </div> -->
                    </div>
                                </div>
                                <div class="config-group-button" style="flex: 1;">
                                    <div class="voiceset-set">
                                      <el-switch v-model="isVoice" active-color="#4157FE" inactive-color="#CED4E0"
                                        style="color: #000000;" @change="handleVoiceChange()"
                                      >
                                    </el-switch>
                                    <span>{{$t('enableVoiceIdentify')}}</span>
                                    </div>
                                    
                                    <el-select :placeholder="$t('pleaseSelect')" v-model="appForm.sttId"
                                        style="width: 100%;margin-top: 8px;" @change="handleIpnutChange">
                                        <el-option v-for="item in ttsListData" :key="item.id" :value="item.id"
                                            :label="item.componentName"></el-option>
                                    </el-select>
                                </div>
                    <!-- <div class="marginTop10">
                      <el-switch :active-text="$t('isStreamingPlay')" v-model="appForm.streamVoice" active-color="#4157FE"
                        inactive-color="#CED4E0" active-value="是" inactive-value="否"
                        @change="handleIpnutChange"></el-switch>
                    </div> -->
                  </div>
                </config-group>
              </div>
              <!-- v-if="funcOrTool.includes('virtual')" -->
              <div class="dialog" v-if="false">
                <config-group
                  :label="$t('virtualHuman')"
                  :icon="require('@/assets/images/appManagement/icon3.svg')"
                  style="border: 0"
                >
                  <div class="marginTop10 flex-center">
                    <div class="formTitle">
                      <span class="widthSpan">{{ $t("virtualPersonId") }}</span>
                    </div>
                    <el-input
                      :placeholder="$t('inputPlaceholder')"
                      v-model="appForm.virtualHumanId"
                      style="width: 100%"
                      @change="handleIpnutChange"
                    ></el-input>
                  </div>
                </config-group>
              </div>
              <!-- 检索引擎 -->
            <div class="dialog" v-if="funcOrTool.includes('SearchEngine')">
              <config-group
                  :disabled="true"
                  :label="$t('searchEngine')"
                  icon="gongneng-lianwangsousuo"
                  :tip="networkChannelName"
                  :showTip="false"
                  style="border: 0"
                >
                <template #button>
                  <div class="flex-center config-group-button">
                    <iconpark-icon name="settings-4-line" size="16" style="margin-left:12px;cursor: pointer;" color="#1d2129" @click="setSearchEngineVisible=true"></iconpark-icon>
                  </div>
                </template>
                </config-group>
                <!-- <div class="flex-center just">
                <div class="flex-center" style="width: 100%">
                  <img class="func-iconImg" :src="require('@/assets/images/appManagement/icon9.svg')" />
                  <span class="boxName">{{ $t("searchEngine") }}</span>
                  <iconpark-icon name="indeterminate-circle-line" size="16" color="#494E57"
                    style="margin-left: auto;cursor: pointer" @click="deleteTool('SearchEngine')"></iconpark-icon>
                </div>
              </div> -->
              <!-- <div class="marginTop10 flex-center">
                <div class="flex-center" style="flex: 1">
                  <div class="formTitle" style="margin-right: 8px">
                    <span class="widthSpan">{{ $t("searchEngine") }}</span>
                  </div>
                  <el-select :placeholder="$t('pleaseSelect')" v-model="appForm.networkChannel"
                    style="width: calc(100% - 60px)" @change="handleIpnutChange">
                    <el-option v-for="item in networkChannelList" :label="item.label" :value="item.value"
                      :key="item.id"></el-option>
                  </el-select>
                </div>
                <div class="flex-center" style="margin-left: 24px">
                  <el-switch v-model="appForm.finalNetworkFlag" active-color="#4157FE" inactive-color="#CED4E0"
                    active-value="是" inactive-value="否" @change="handleIpnutChange">
                  </el-switch>
                  <span class="widthSpan" style="margin-left: 8px">联网兜底</span>

                </div>
              
              </div> -->

            </div>
            <!-- 场景调用 -->
            <div class="dialog" v-if="funcOrTool.includes('Scenesetting')">
              <!-- <div class="flex-center just">
                <div class="flex-center">
                  <img class="func-iconImg" :src="require('@/assets/images/appManagement/icon11.svg')" />
                  <div>
                    <div>
                      <span class="boxName">{{ $t("sceneCall") }} </span>
                      <span class="disabled" v-if="appForm.subjectFlag !== '是'">{{ $t("disabled") }}</span>
                    </div>
                    <p style="color: #828894; margin-top: 5px">
                      {{ $t("sceneCallTips") }}
                    </p>
                  </div>
                </div>
                <div class="flex-center">
                  <ModelSetting :params="sceneForm"  v-if="funcOrTool.includes('Scenesetting') && activeName == 'second'" :isModelFallback="false" :hideLabel="isHidLabel" @modelChanges="sceneModelChange"></ModelSetting>
                  <el-button class="tool-set-btn" type="text" style="color: #494E57;"
                    @click="openDialog('sceneBinding')"><iconpark-icon name="settings-4-line" size="16"
                      color="#494E57"></iconpark-icon>{{ $t("sceneBinding") }}</el-button>
                  <el-button class="tool-set-btn" type="text" style="color: #494E57;"
                    @click="openDialog('sceneSetting')"><iconpark-icon name="settings-4-line" size="16"
                      color="#494E57"></iconpark-icon>{{ $t("settings") }}</el-button>
                  <iconpark-icon name="indeterminate-circle-line" size="16" color="#494E57"
                    style="margin-left: 12px;cursor: pointer" @click="deleteTool('Scenesetting')"></iconpark-icon>
                </div>
              </div> -->
              <config-group
                  :disabled="false"
                  :label="'场景设置'"
                  icon="gongneng-changjing"
                  :showSwitch="true"
                  :switchValue.sync="appForm.subjectFlag"
                  style="border: 0"
                >
                <template v-slot:button>
                  <div class="flex-center config-group-button">
                    <iconpark-icon name="settings-4-line" size="16" style="cursor: pointer;" color="#1d2129" @click="setSceneVisible=true"></iconpark-icon>
                    <iconpark-icon name="add-line" size="16" style="margin-left:20px;cursor: pointer;" color="#1d2129" @click.stop="openDialog('setScene')"></iconpark-icon>
                    <!-- <el-button
                      type="text"
                      icon="el-icon-plus"
                      @click.stop="openDialog('setScene')"
                    ></el-button> -->
                  </div>
                </template>
                <div class="marginTop10">
                    <!-- 场景设置 -->
                    <div
                      class="dialog plugin-item"
                      v-if="
                        funcOrTool.includes('Scenesetting') &&
                        sceneListSelected?.length
                      "
                    >
                      <div style="margin-top: 10px">
                        <ul>
                          <li
                            class="base-li flex-center just base-li-anquan"
                            v-for="(item, index) in sceneListSelected"
                            :key="index+'scene'"
                          >
                            <div class="li-name flex-center">
                              <img
                                src="@/assets/images/appManagement/scenesetting.png"
                              />
                              <div>
                                <div class="name">
                                  {{ item.sceneName }}
                                </div>
                                <div class="remark">
                                  {{ item.matterDesc }}
                                </div>
                              </div>
                            </div>
                            <iconpark-icon
                              class="delete-icon"
                              name="delete-bin-4-line"
                              size="16"
                              color="#494E57"
                              style="cursor: pointer"
                              @click="deleteScene(item)"
                            ></iconpark-icon>
                          </li>
                        </ul>
                        <!-- <el-button
                  size="small"
                  icon="el-icon-plus"
                  @click="openDialog('addSensitive')"
                  >添加</el-button
                > -->
                    </div>
                  </div>
                </div>
              </config-group>
            </div>
            <!-- 问数检索 funcOrTool.includes('wenshuFlag')-->
            <div class="dialog" v-if="false">
              <div class="flex-center just" style="width: 100%">
                <div class="flex-center" style="width: 100%">
                  <img class="func-iconImg" :src="require('@/assets/images/appManagement/wsjs.svg')" />
                  <div style="width: 100%">
                    <div class="flex-center" style="width: 100%">
                      <span class="boxName">问数检索 </span>
                      <span style="color: #D33A22; font-size: 12px; margin-left: 4px">知识库无可检索的内容，设置未生效</span>
                      <iconpark-icon name="indeterminate-circle-line" size="16" color="#494E57"
                        style="margin-left: auto;cursor: pointer" @click="deleteTool('wenshuFlag')"></iconpark-icon>
                    </div>
                    <p style="color: #828894; margin-top: 5px">
                      允许应用检索知识库中的excel数据，并返回计算结果。需关联的知识库中包含excel结构化数据
                    </p>
                  </div>
                </div>
              </div>
            </div>
            </div>
          </div>
          

		<div v-if="activeName == 'mcp'">
			<div class="marginTop32">
			<div class="dialog plugin-item" v-for="(item,index) in mcpServerIdList" :key="index">
			  <div class="flex-center just">
			    <div class="flex-center" style="width: 100%">
			      <img class="func-iconImg" :src="item.icon" />
			      <span class="boxName">{{ item.mcpName }}</span>
			      <iconpark-icon name="indeterminate-circle-line" size="16" color="#494E57"
			        style="margin-left: auto;cursor: pointer" @click="deleteMcp(index)"></iconpark-icon>
			    </div>
			  </div>
			  <div class="marginTop10 flex-center">
			    <div class="flex-center" style="flex: 1">
			      <div class="formTitle" :title="item.description">
			        {{ item.description }}
			      </div>
		
			    </div>			    		
			  </div>
			</div>
			</div>
		</div>
    <div class="click-add-ctn" @click="showAddFuctoinTool">
      <iconpark-icon
        class="btn-icon"
        name="function-add-fill"
        color="#c9cdd4"
        size="24"
      ></iconpark-icon>

      <p class="word">点击添加</p>
      </div>
		</div>
		<!-- 插件内容结束 -->
		
      </div>
      <div
        v-if="appForm.type === 'qa' && visibleChange"
        class="functionalPlugins"
      >
        <!-- 功能或插件 -->
        <NewAddFunctionOrTool
          ref="functionOrToolRef"
          :funcOrToolArr="funcOrTool"
          :mcpIdList="mcpIdList"
          :params="appForm"
          :pluginList="pluginList"
          @clickConfig="clickConfig"
          @clickConfigParams="clickConfigParams"
          @changeStatus="changeStatus"
          @addPluginDataEmit="addPluginDataEmit"
          @addMcpDataEmit="addMcpDataEmit"
          @closeFunctionOrTool="closeFunctionOrTool"
        ></NewAddFunctionOrTool>
      </div>
      <div class="previewDebugging" v-if="appForm.type === 'qa'">
        <!-- padding: 0 16px 16px -->
        <div class="flex-center just" style="padding: 0 0 16px 0">
          <div class="formTitle flex-center">
            <!-- <img
              src="@/assets/images/console-fill.svg"
              style="margin-right: 5px"
            /> -->
            <span class="previewAndDebug">{{ $t("preview") }}</span>
            <iconpark-icon
              name="reset-left-line"
              size="16"
              color="#494E57"
              @click="handleToPreview(1, false)"
              style="margin-left: 12px; cursor: pointer"
            ></iconpark-icon>
          </div>
          <div class="formTitle flex-center debugging" @click="handleDebug()">
            <!-- <img
              src="@/assets/images/console-fill.svg"
              style="margin-right: 5px"
            /> -->
            <iconpark-icon
              name="menu-unfold-line"
              size="16"
              color="#494E57"
              style="margin-right: 12px; cursor: pointer"
            ></iconpark-icon>
            <span class="previewAndDebug">{{ $t("debugging") }}</span>
          </div>
          <!-- <div class="flex-center">
            <el-button
              type="text"
              style="color: #494E57"
              @click="openDialog('order')"
            >
              <iconpark-icon name="list-ordered-2" color="#494E57" size="16" style="margin-right: 4px;"></iconpark-icon>{{ $t("executeSequentialSteps") }}
            </el-button>
          </div> -->
        </div>
        <div style="width: 100%">
          <PreView
            ref="previewRef"
            :params="appForm"
            @updateLoading="updateLoading"
            @handleToPreview="handleToPreview"
            style="width: 100%; height: calc(100vh - 140px)"
          ></PreView>
        </div>
      </div>
      <div class="previewDebugging addDebug" v-if="debugType">
        <div class="flex-center just heaed" style="padding: 16px">
          <div class="formTitle flex-center heaed-top">
            <h3 class="previewAndDebug">{{ $t("debugging") }}</h3>
            <iconpark-icon
              name="close-large-fill"
              size="16"
              color="#1D2129"
              @click="handleClose()"
              style="margin-left: 12px; cursor: pointer"
            ></iconpark-icon>
          </div>
        </div>
        <div>
          <ModalDebug
            v-if="debugType"
            :applicationId="appForm.applicationId"
            :conversationId="conversationId"
          ></ModalDebug>
        </div>
      </div>
    </div>
    <div class="main-content" v-if="activeHeadTab === 2">
      <div class="main-content-left">
        <div class="left-tabs-content">
          <div class="formTitle flex-center" style="margin-bottom: 0px">
            <span class="previewAndDebug">{{ $t("effectPreview") }}</span>
          </div>
          <div class="flex-center preview-tabs">
            <div class="tabs">
              <div
                v-for="(tab, index) in appTemplateTypeTabs"
                :key="index"
                class="tab-item"
                :class="{ active: activeAppTemplateType === tab.value }"
                @click="handleAppTemplateTypeClick(tab.value)"
              >
                {{ tab.label }}
              </div>
            </div>
          </div>
          <div>
            <el-button
              type="text"
              style="color: #494e57; height: 40px; font-size: 16px"
              class="change-template-btn"
              @click="changeTemplate"
              ><iconpark-icon
                name="refresh-line"
                color="#494E57"
                size="18"
                style="margin-right: 4px"
              ></iconpark-icon>
              {{ $t("replaceTemplate") }}
            </el-button>
          </div>
        </div>
        <div v-loading="rightLoading" class="preview-iframe-box">
          <iframe
            ref="previewIframeRef"
            class="preview-iframe"
            :class="
              activeAppTemplateType === 1
                ? 'preview-iframe-pc'
                : 'preview-iframe-H5'
            "
          ></iframe>
        </div>
      </div>
      <div class="main-content-right">
        <DisplaySettings
          ref="displaySettingsRef"
          :params="appForm"
          @clickConfigParams="clickConfigParams"
          @update="toPreview()"
        ></DisplaySettings>
      </div>
    </div>
    <!-- 发布 -->
    <PublishComponent
      ref="PublishComponent"
      v-if="activeHeadTab === 3"
      :params="appForm"
      :appConfigForm="appConfigForm"
      :selectIds="selectIds"
      :saveTime="saveTime"
      @closePrantDialog="closeDialog"
      @workflowSubmit="workflowSubmit"
      @openComponents="openComponents"
      :mcpServerIdList="mcpServerIdList"
    />
    <!-- 分析 -->
    <AnalysisComponent v-if="activeHeadTab === 4" />
    <!-- 知识库参数设置 -->
    <knowledgeBaseSet
      v-if="setBaseVisible"
      :dialogVisible="setBaseVisible"
      :params="appForm"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
    ></knowledgeBaseSet>
    <!-- 新增知识库 -->
    <addKnowledgeBase
      v-if="addBaseVisible"
      :dialogVisible="addBaseVisible"
      :configData="appForm.knowledgeIds"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
      @updateAll="knowledgeList"
      @updateKnowledgeIds="updateKnowledgeIds"
    ></addKnowledgeBase>
    <!-- 新增MCP -->
    <selectMcp v-if="selectMcpVisible"
    :configData.sync="appForm.mcpServerIds"
    :dataList.sync="mcpServerIdList"
    :dialogVisible.sync="selectMcpVisible" ref="selectMcp" />
    <!-- 添加工具功能 -->
    <addFunctionOrTool
      v-if="toolVisible"
      :dialogVisible="toolVisible"
      :funcOrToolArr="funcOrTool"
      :params="appForm"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
      @changeStatus="changeStatus"
      :pluginList="pluginList"
      @addPluginDataEmit="addPluginDataEmit"
    ></addFunctionOrTool>
    <!-- 执行顺序步骤 -->
    <orderStep
      v-if="orderStepVisible"
      :dialogVisible="orderStepVisible"
      :params="
        Array.isArray(appForm.processStep)
          ? appForm.processStep
          : appForm.processStep
          ? appForm.processStep.split(',')
          : []
      "
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
    ></orderStep>
    <!-- 展示设置 -->
    <showSetting
      v-if="showSettingVisible && !type"
      :dialogVisible="showSettingVisible"
      :params="appForm"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
    ></showSetting>
    <!-- 智能搜索展示设置 -->
    <SearchShowSetting
      v-if="showSettingVisible && type == 'search'"
      :dialogVisible="showSettingVisible"
      :params="appForm"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
    ></SearchShowSetting>
    <!-- 发布 -->
    <publishWay
      v-if="fabuDialogVisible"
      :fabuDialogVisible="fabuDialogVisible"
      @confirmPublishWay="clickConfig"
      :params="appForm"
      @clickConfigParams="clickConfigParams"
    ></publishWay>
    <!-- 发布成功 -->
    <publishSuccess
      v-if="fabuSuccessVisible"
      :fabuSuccessVisible="fabuSuccessVisible"
      :params="fabuSuccessData"
      :showUrlBtn="showUrlBtn"
      @clickConfig="closeDialog"
      @clickConfigParams="clickConfigParams"
    >
    </publishSuccess>
    <!-- 自动填充 -->
    <el-dialog
      title=""
      :visible.sync="defaultDialogVisible"
      width="400px"
      class="defaultDialo"
      :before-close="defaultDialogClose"
      append-to-body
    >
      <div slot="title" class="dialog-title">
        <img :src="require('@/assets/images/appManagement/warning.svg')" />
        <span>{{ $t("autoFill") }}</span>
      </div>
      <span class="tipsText">{{ $t("autoFillTips") }}</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="filldefaultValues(defaultValuesId)">{{
          $t("confirm")
        }}</el-button>
        <el-button @click="defaultDialogVisible = false">{{
          $t("cancel")
        }}</el-button>
      </span>
    </el-dialog>
    <!-- 创建问答助手 名称弹窗 -->
    <createApplication
      v-if="editApplicationVisible"
      :dialogVisibleApplication="editApplicationVisible"
      :params="appForm"
      :type="'edit'"
      @cancelEditApplication="clickConfig"
      @confirmEditApplication="clickConfigParams"
    >
    </createApplication>
    <!-- 场景设置 -->
    <el-dialog
      title=""
      :visible.sync="sceneSettingVisible"
      width="960px"
      class="defaultDialo sceneSettingDialog"
      :before-close="clickConfig"
      append-to-body
    >
      <div slot="title" class="dialog-title">
        <span style="margin-right: 15px">{{ $t("sceneSetting") }}</span>
        <el-switch
          v-model="appForm.subjectFlag"
          active-color="#4157FE"
          inactive-color="#CED4E0"
          active-value="是"
          inactive-value="否"
          @change="changeApplicationStatus(true)"
        >
        </el-switch>
      </div>
      <div class="marginTop10 flex-center">
        <el-input
          :placeholder="detailedSettings"
          v-model="appForm.subjectPrompt"
          show-word-limit
          type="textarea"
          :autosize="{ minRows: 20, maxRows: 20 }"
          maxlength="3000"
          @change="changeApplicationStatus(true)"
        ></el-input>
      </div>
      <span slot="footer" class="dialog-footer" style="text-align: right">
        <el-button @click="sceneSettingConfig">{{ $t("cancel") }}</el-button>
        <el-button type="primary" @click="sceneSettingConfig">{{
          $t("confirm")
        }}</el-button>
      </span>
    </el-dialog>
    <!-- 关联敏感词库 -->
    <addSensitiveDialog
      v-if="addSensitiveVisible"
      :dialogVisible="addSensitiveVisible"
      :configData="appForm.interceptWordHouses"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
      :appConfigForm="appConfigForm"
      @updateAll="getInterceptWordHousesList"
    ></addSensitiveDialog>
    <!-- 场景绑定 -->
    <sceneBindingVisibleDialog
      v-if="sceneBindingVisible"
      :dialogVisible="sceneBindingVisible"
      @clickConfig="clickConfig"
      :appConfigForm="appConfigForm"
      @sceneChange="getSceneApplicationRefListAll"
    ></sceneBindingVisibleDialog>
    <!-- 免责声明 -->
    <wangeditor
      ref="wangeditor"
      v-model="wangeditorVisible"
      @close="closeWangeditorDialog"
      :statement="appForm.disclaimer"
      title="免责声明"
      @editStatementValue="editStatementValue"
    ></wangeditor>
    <!-- 提示词库 -->
    <vocabularyDrawer
      ref="vocabularyDrawer"
      @insertVocabulary="insertVocabularyFn"
    />
    <!-- 选择模板 -->
    <SelectTemplate
      ref="selectTemplateRef"
      :type="appForm.type"
      @updateTemplate="updateTemplate"
      :defaultId="defaultId"
    ></SelectTemplate>
    <!-- 添加工作流 -->
    <AddWorkflowDialog
      v-if="addWorkflowVisible"
      :dialogVisible="addWorkflowVisible"
      :configData="appForm.workflowIds"
      :sourceData="appForm"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
      @updateAll="getWorkflowList"
      @updateWorkflowIds="updateWorkflowIds"
    ></AddWorkflowDialog>
	<el-dialog
	  title="创建快捷指令"
	  :visible.sync="dialogVisibleApplicationFlag"
	  width="480px"
	  :before-close="cancelTemplate"
	  class="applicationDialog"
	  append-to-body
	  :close-on-click-modal="false"
	  :close-on-press-escape="false"
	>
	  <div style="position: relative">
	    <div class="formOuter">
	      <div class="uploadImg">
	        <div>
	          <el-form
	            :model="applicationQuickCommandObj"          
	            ref="ruleForm"
	            class="demo-ruleForm"
	          >
	            <el-form-item
	              label="指令类型"
	              prop="type"
	             
	            >
	              <div class="application-type-box">
	                <el-select class="app-type-select" placeholder="请选择指令类型" style="width: 100%" v-model="applicationQuickCommandObj.commandType">
	                  <el-option value="command" label="指令"></el-option>
	                </el-select>	                
	              </div>
	            </el-form-item>
	            
	            <el-form-item
	              label="指令内容"
	              prop="introduce"
	            >
	              <el-input
	                class="inputTextarea"
	                type="textarea"
	                :rows="7"
	                v-model="applicationQuickCommandObj.commandContent"
	                show-word-limit
	                maxlength="200"
	                style="width: 100%"
	                placeholder="请输入指令内容"
	              />
	            </el-form-item>
	            <el-form-item
	              label="快捷按钮名称"
	              prop="commandName"
	            >
	              <el-input
	                v-model="applicationQuickCommandObj.commandName"
	                show-word-limit
	                maxlength="100"
	                style="width: 100%"
	                placeholder="请输入名称"
	              />
	            </el-form-item>
	          </el-form>
	        </div>
	      </div>
	    </div>
	  </div>
	  <span slot="footer" class="dialog-footer">
	    <el-button @click="cancelTemplate">{{ $t("cancel") }}</el-button>
	    <el-button type="primary" @click="confirmTemplate" style="background: #1747E5;">{{
	      $t("confirm")
	    }}</el-button>
	  </span>
	</el-dialog>


  <!-- 模糊问题引导 -->
  <el-dialog
    title=""
    :visible.sync="setVagueQuestionVisible"
    class="settings-dialog-ctn"
    custom-class="settings-dialog"
    :before-close="clearVagueQuestion"
    append-to-body
    width="680px">
    <template #title>
      <p class="title">模糊问题引导</p>
    </template>
    <div class="vague-ctn">
      <div class="vague-ctn-list">
        <div class="vague-title-ctn" style="margin-bottom: 8px;">
          <div class="vague-title">大模型</div>
          <!-- <div>
            <el-radio v-model="regularModelRadio" label="1">与应用保持一致</el-radio>
            <el-radio v-model="regularModelRadio" label="2">自定义</el-radio>
          </div> -->
        </div>

        <div>
          <!-- 模型设置 -->
            <ModelSetting ref="modelSettingRef" :params="modelForm" @modelChanges="modelChanges" :bgColor="'#F7F8FA'" :borderR="'8px'"></ModelSetting>
        </div>
      </div>

      <div class="vague-ctn-list">
        <div class="vague-title-ctn">
          <div class="vague-title">模糊判断提示词</div>
        </div>
        <el-input
            style="margin-top: 8px;"
            :rows="5"
            type="textarea"
            placeholder="请输入提示词"
            v-model="configExtend.doorsill_llm_usr_prompt"
            maxlength="2000"
            show-word-limit
          >
          </el-input>
      </div>
      <div class="vague-ctn-list">
        <div class="vague-title-ctn">
          <div class="vague-title">问题生成规则</div>
        </div>
        <div class="vague-tip">触发时，将按顺序执行以下规则生成新问题</div>
        <div class="regular">
          <div class="regular-list" v-for="(item,index) in regularList" :key="item.key">
            <div class="check-box" @click="changeRegular(item.key)">
              <img src="@/assets/images/appManagement/function_checked.png" v-if="configExtend[item.key]=='是'" alt="">
              <img src="@/assets/images/appManagement/function_default.png" v-else alt="">
            </div>
            
            <div class="regular-type">
              <div class="regular-title">
                {{ item.title }}
              </div>
              <div class="regular-tip">
                {{ item.tip }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="vague-ctn-list">
        <div class="vague-title-ctn">
          <div class="vague-title">新问题作为推荐问题</div>
          <el-switch
            v-model="configExtend.vague_not_answer_flag"
            active-value="是"
            inactive-value="否"
            active-color="#4157FE"
            inactive-color="#CED4E0">
          </el-switch>
        </div>
        <div class="vague-tip">开启后，新问题将作为推荐问题输出</div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="clearVagueQuestion">取 消</el-button>
      <el-button type="primary" @click="confirmVagueQuestion('vagueQuestion')">确 定</el-button>
    </span>
  </el-dialog>

  <!-- 答案溯源 -->
  <el-dialog
    title=""
    :visible.sync="setAnswerSourceVisible"
    class="settings-dialog-ctn"
    custom-class="settings-dialog"
    append-to-body
    width="680px">
    <template #title>
      <p class="title">答案溯源</p>
    </template>
    <div class="answersource-ctn">
      <div class="answersource-ctn-list">
        <div class="answer-left">
          <el-switch
            v-model="appForm.refIndexFlag"
            active-value="是"
            inactive-value="否"
            active-color="#4157FE"
            inactive-color="#CED4E0">
          </el-switch>
        </div>
        <div class="answer-right">高亮匹配结果</div>
      </div>
      <div class="answersource-ctn-list">
        <div class="answer-left"></div>
        <div class="answer-right" style="color: #86909c;">突出显示答案来源内容的匹配部分，开启后可能会使结果输出时间延长</div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="setAnswerSourceVisible = false">取 消</el-button>
      <el-button type="primary" @click="setAnswerSourceVisible = false">确 定</el-button>
    </span>
  </el-dialog>
  <el-dialog title="温馨提示" :visible.sync="deleteDialogVisible" width="560px" class="deleteDialog"
  	 append-to-body>
  	<p class="desc">内容尚未保存，是否确认退出？</p>
  	<span slot="footer" class="dialog-footer">
  		<el-button @click="deleteDialogVisible = false">{{ $t("cancel") }}</el-button>
  		<el-button type="primary" @click="confirmDelete">确定</el-button>
  	</span>
  </el-dialog>

  <!-- 检索引擎 -->
  <el-dialog
    title=""
    :visible.sync="setSearchEngineVisible"
    class="settings-dialog-ctn"
    custom-class="settings-dialog"
    append-to-body
    width="560px">
    <template #title>
      <p class="title">检索引擎</p>
    </template>
    <div class="searchengine-ctn">
      <div class="engine-ctn">
        <div class="engine-ctn-title">搜索引擎</div>
        <el-select :placeholder="$t('pleaseSelect')" v-model="appForm.networkChannel" style="width: 100%;"
          @change="handleIpnutChange">
          <el-option v-for="item in networkChannelList" :label="item.label" :value="item.value"
            :key="item.id"></el-option>
        </el-select>
      </div>

      <div class="switch-ctn">
        <div class="flex-center" style="gap: 8px">
          <el-switch v-model="functionForm.SearchEngine.internet_redis_enable" active-color="#4157FE" inactive-color="#CED4E0"
            active-value="是" inactive-value="否" @change="handleIpnutChange">
          </el-switch>
          <span class="switch-name">缓存检索结果</span>
          <div class="switch-tip"> 
            <el-tooltip popper-class="workflow-tooltip" content="缓存首次互联网检索结果，用于二次搜索时维持结果一致性" placement="top" effect="light">
              <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
            </el-tooltip>
          </div>
        </div>
        <div class="cache-result" v-if="functionForm.SearchEngine.internet_redis_enable=='是'">
          <div class="cache-result-list">
            <div class="cache-label-ctn">
              <div class="cache-label">
                缓存结果数
                <sup style="color: #ff0000;">*</sup>
              </div>
              <div class="icon-ctn">
                <el-tooltip popper-class="workflow-tooltip" content="对检索结果重排后被保留的条目数量" placement="top" effect="light">
                  <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                </el-tooltip>
              </div>
            </div>
            <div class="cache-slider">
              <el-slider style="width: 166px;"
                v-model="functionForm.SearchEngine.internet_redis_count"
                :min="0"
                :max="30"
                :step="1"
              ></el-slider>
              <el-input-number
                class="score-input"
                v-model="functionForm.SearchEngine.internet_redis_count"
                controls-position="right"
                :precision="0"
                :min="0"
                :max="30"
                :step="1"
                size="small"
                style="width: 80px"
              ></el-input-number>
              <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultFunValue('SearchEngine','internet_redis_count', 10)">
                <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
              </span>
            </div>
          </div>
          <div class="cache-result-list">
            <div class="cache-label-ctn">
              <div class="cache-label">
                重排分数阈值
                <sup style="color: #ff0000;">*</sup>
              </div>
              <div class="icon-ctn">
                <el-tooltip popper-class="workflow-tooltip" content="对检索结果进行重排时，依据设定的阈值进行筛选，仅保留分数高于该阈值的结果" placement="top" effect="light">
                  <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                </el-tooltip>
              </div>
            </div>
            <div class="cache-slider">
              <el-slider style="width: 166px;"
                v-model="functionForm.SearchEngine.internet_rewriting_score"
                :min="0"
                :max="1"
                :step="0.01"
              ></el-slider>
              <el-input-number
                class="score-input"
                v-model="functionForm.SearchEngine.internet_rewriting_score"
                controls-position="right"
                :precision="2"
                :min="0"
                :max="1"
                :step="0.01"
                size="small"
                style="width: 80px"
              ></el-input-number>
              <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultFunValue('SearchEngine','internet_rewriting_score', 0.64)">
                <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
              </span>
            </div>
          </div>
          <div class="cache-result-list">
            <div class="cache-label-ctn">
              <div class="cache-label">
                检索次数
                <sup style="color: #ff0000;">*</sup>
              </div>
              <div class="icon-ctn">
                <el-tooltip popper-class="workflow-tooltip" content="检索互联网的次数，会对多次检索的结果进行重排" placement="top" effect="light">
                  <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                </el-tooltip>
              </div>
            </div>
            <div class="cache-slider">
              <el-slider style="width: 166px;"
                v-model="functionForm.SearchEngine.internet_search_count"
                :min="0"
                :max="10"
                :step="1"
              ></el-slider>
              <el-input-number
                class="score-input"
                v-model="functionForm.SearchEngine.internet_search_count"
                controls-position="right"
                :precision="0"
                :min="1"
                :max="10"
                :step="1"
                size="small"
                style="width: 80px"
              ></el-input-number>
              <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultFunValue('SearchEngine','internet_search_count', 1)">
                <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
              </span>
            </div>
          </div>
        </div>
        <div class="flex-center" style="gap: 8px">
          <el-switch v-model="appForm.finalNetworkFlag" active-color="#4157FE" inactive-color="#CED4E0"
            active-value="是" inactive-value="否" @change="handleIpnutChange">
          </el-switch>
          <span class="switch-name">联网兜底</span>
          <div class="switch-tip"> 
            <el-tooltip popper-class="workflow-tooltip" content="当其他信息渠道无法提供有效结果时，作为最终信息来源进行补充，不建议与模型兜底同时开启" placement="top" effect="light">
              <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
            </el-tooltip>
          </div>
        </div>
        <!-- <div class="flex-center" style="gap: 8px">
          <el-switch v-model="appForm.modelProblem" active-color="#4157FE" inactive-color="#CED4E0"
            active-value="是" inactive-value="否" @change="handleIpnutChange">
          </el-switch>
          <span class="switch-name">模型问题理解</span>
          <div class="switch-tip"> 
            <el-tooltip popper-class="workflow-tooltip" content="模型问题理解" placement="top" effect="light">
              <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
            </el-tooltip>
          </div>
        </div> -->
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="setSearchEngineVisible = false">取 消</el-button>
      <el-button type="primary" @click="confirmVagueQuestion('SearchEngine')">确 定</el-button>
    </span>
  </el-dialog>

  <!-- 问题建议 -->
  <el-dialog
    title=""
    :visible.sync="setQuestionImagineVisible"
    class="settings-dialog-ctn"
    custom-class="settings-dialog"
    append-to-body
    width="680px">
    <template #title>
      <p class="title">问题建议</p>
    </template>
    <div class="quesimagine-ctn">
      <div class="quesimagine-tip">触发时，将按顺序执行以下规则生成建议，直到满足数量要求或全部执行完成</div>
      <div class="regular" style="margin-top: 10px;">
          <div class="regular-list" v-for="(item,index) in quesImagineList" :key="item.key">
            <div class="check-box" @click="changeQuesImagine(item.key)">
              <img src="@/assets/images/appManagement/function_checked.png" v-if="quesImagineForm[item.key]=='是'" alt="">
              <img src="@/assets/images/appManagement/function_default.png" v-else alt="">
            </div>
            
            <div class="regular-type">
              <div class="regular-title">
                {{ item.title }}
              </div>
              <div class="regular-tip">
                {{ item.tip }}
              </div>
            </div>
          </div>
        </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="setQuestionImagineVisible = false">取 消</el-button>
      <el-button type="primary" @click="confirmVagueQuestion('recommendation')">确 定</el-button>
    </span>
  </el-dialog>

  <!-- 问题联想 -->
  <el-dialog
    title=""
    :visible.sync="setAssociationVisible"
    class="settings-dialog-ctn"
    custom-class="settings-dialog"
    append-to-body
    width="680px">
    <template #title>
      <p class="title">问题联想</p>
    </template>
    <div class="quesimagine-ctn">
      <div class="quesimagine-tip">触发时，将按顺序执行以下规则生成建议，直到满足数量要求或全部执行完成</div>
      <div class="regular" style="margin-top: 10px;">
          <div class="regular-list" v-for="(item,index) in associationList" :key="item.key+'association'">
            <div class="check-box" @click="changeAssociation(item.key)">
              <img src="@/assets/images/appManagement/function_checked.png" v-if="functionForm.association[item.key]=='是'" alt="">
              <img src="@/assets/images/appManagement/function_default.png" v-else alt="">
            </div>
            
            <div class="regular-type">
              <div class="regular-title">
                {{ item.title }}
              </div>
              <div class="regular-tip">
                {{ item.tip }}
              </div>
            </div>
          </div>
        </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="setAssociationVisible = false">取 消</el-button>
      <el-button type="primary" @click="confirmVagueQuestion('association')">确 定</el-button>
    </span>
  </el-dialog>

  <!-- 场景设置 -->
  <el-dialog
    title=""
    :visible.sync="setSceneVisible"
    class="settings-dialog-ctn"
    custom-class="settings-dialog"
    :before-close="clickConfig"
    append-to-body
    width="680px">
    <template #title>
      <p class="title">场景设置</p>
    </template>
    <div class="vague-ctn">
      <div class="vague-ctn-list">
        <div class="vague-title-ctn" style="margin-bottom: 8px;">
          <div class="vague-title">大模型</div>
          <!-- <div>
            <el-radio v-model="regularModelRadio" label="1">与应用保持一致</el-radio>
            <el-radio v-model="regularModelRadio" label="2">自定义</el-radio>
          </div> -->
        </div>

        <div>
          <!-- 模型设置 -->
            <ModelSetting ref="modelSettingRef" :params="sceneModelForm" @modelChanges="modelSceneChanges" :bgColor="'#F7F8FA'" :borderR="'8px'"></ModelSetting>
        </div>
      </div>

      <div class="vague-ctn-list">
        <div class="vague-title-ctn">
          <div class="vague-title">场景触发提示词</div>
        </div>
        <el-input
            style="margin-top: 8px;"
            :rows="10"
            type="textarea"
            placeholder="请输入提示词"
            v-model="appForm.subjectPrompt"
            maxlength="2000"
            show-word-limit
          >
          </el-input>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="setSceneVisible=false">取 消</el-button>
      <el-button type="primary" @click="setSceneVisible=false">确 定</el-button>
    </span>
  </el-dialog>

  <!-- 语音设置：选择发音人 -->
  <el-dialog
    title=""
    :visible.sync="visibleVoice"
    class="settings-dialog-ctn"
    custom-class="settings-dialog"
    append-to-body
    width="560px">
    <template #title>
      <p class="title">播报设置</p>
    </template>
    <div class="voiceset-ctn">
      <div class="voiceset-title-ctn">
        <div class="voiceset-title">
          发音设置
        </div>
        <!-- <div class="voiceset-more">
          更多发音人
          <iconpark-icon name="arrow-right-s-line" size="16" color="#86909c"></iconpark-icon>
        </div> -->
      </div>

      <div class="voiceset-list-ctn">
        <div class="voiceset-list" v-for="(item,index) in voiceList" :key="index+'voiceset'"  @click="handelVoiceItem(item, index)"
        :style="{borderColor:selectedIndex === index?'#1747E5':'#E7E7E7'}">
          <img class="voiceset-logo" :src="require('@/assets/images/appManagement/icon4.svg')" alt="">
          <p class="voiceset-name">{{ item.fullStyle }}</p>
          <iconpark-icon name="volume-up-line" size="16" color="#494E57"
              style="cursor: pointer"></iconpark-icon>
          <div class="voiceset-checked" v-show="selectedIndex === index">
            <iconpark-icon name="check-line" size="16" color="#1d2129"
              style="cursor: pointer"></iconpark-icon>
          </div>
        </div>
      </div>

      <div class="voiceset-title-ctn">
        <div class="voiceset-title">
          发音设置
        </div>
        <div class="voiceset-select">
          <el-switch v-model="appForm.streamVoice" active-color="#4157FE" inactive-color="#CED4E0"
            active-value="是" inactive-value="否">
          </el-switch>
          <p>启用流式输出</p>
        </div>
      </div>
      <div class="voiceset-other-ctn">
        <p>语速</p>
        <el-slider v-model="appForm.voiceSpeed" style="width: 360px; height: 4px;"
            :min="0" :max="10" :step="0.1" @input="handleSliderChange"></el-slider>
        <el-input-number style="width: 80px;" size="small"
            v-model="appForm.voiceSpeed" controls-position="right"
            @change="handleInputChange" :min="0" :max="10"
            :step="0.1"></el-input-number>
      </div>
      <div class="voiceset-other-ctn">
        <p>语调</p>
        <el-slider v-model="appForm.pitch" style="width: 360px; height: 4px;"
            :min="0" :max="10" :step="0.1" @input="handlePitchChange"></el-slider>
        <el-input-number style="width: 80px;" size="small"
            v-model="appForm.pitch" controls-position="right"
            @change="handlePitchiInputChange" :min="0" :max="10"
            :step="0.1"></el-input-number>
      </div>
    </div>
    
    <span slot="footer" class="dialog-footer">
      <el-button @click="visibleVoice = false">取 消</el-button>
      <el-button type="primary" @click="visibleVoice = false">确 定</el-button>
    </span>
  </el-dialog>

  </div>
</template>

<script>
  import {
    addApplication,
    modelList,
    ttsList,
    defaultApp,
    getQuestionByAI,
    getAuthChannels,
    applicationPluginDataList, getDictItemByType,applicationPluginList,
    addApplicationPluginData,
    pageQueryVoice
  } from "@/api/app";
  import {
    appList, 
  } from "@/api/workflow";
  // import textAgentDemo from "./textAgentDemo.vue";
  import { fetchEventSource } from "@microsoft/fetch-event-source";
  import { mapMutations, mapActions } from "vuex";
  import { getInterceptWordHouseListAll } from "@/api/toolManager";
  import knowledgeBaseSet from "./knowledgeBaseSet.vue";
  import addKnowledgeBase from "./addKnowledgeBase.vue";
  import addFunctionOrTool from "./addFunctionOrTool.vue";
  import publishSuccess from "./publishSuccess.vue";
  import orderStep from "./orderStep.vue";
  import showSetting from "./showSetting.vue";
  import SearchShowSetting from '@/views/intelligentSearch/components/showSetting.vue';
  import publishWay from "./publishWay";
  import createApplication from "./createApplication";
  import { getKnowledgeInfoList } from "@/api/index";
  import addSensitiveDialog from "@/views/appManage/components/addSensitiveDialog.vue";
  import sceneBindingVisibleDialog from "@/views/appManage/components/sceneBindingVisibleDialog.vue";
  import ConfigGroup from "./ConfigGroup.vue";
  import wangeditor from "./wangeditor";
  import vocabularyDrawer from '@/views/workflowConfig/dragDemo/components/vocabularyDrawer.vue';
  import dragDemo from "@/views/workflowConfig/dragDemo/index.vue";
  import selectMcp from "@/views/workflowConfig/dragDemo/components/selectMcp.vue"
  import ModelSetting from './modelSetting.vue';
  import AgentPattern from './agentPattern.vue';
  import FuntionOrTool from './addFunctionOrToolNew.vue';
  import NewAddFunctionOrTool from './newAddFunctionOrTool.vue';
  import SelectTemplate from './selectTemplate.vue';
  import DisplaySettings from './displaySettings.vue'
  import AddWorkflowDialog from './addWorkFlowDialog.vue';
  import modelAlgorithmPlugin from "@/views/systemManage/pluginManage/components/modelAlgorithmPlugin.vue";
  import PreView from './preview.vue';
  import ModalDebug from './modalDebug.vue';
  import { debounce } from 'lodash';
  import { saveWorkflow,
      queryWorkflowDetail,
      copyWorkflowApp,saveWorkflowComponent, updateWorkflowComponent } from "@/api/workflow";
  import {
  	getListAndDetail
  } from "@/api/mcp";
  import {
    apiGetSceneApplicationRefList,
    apiDeleteSceneApplicationRef,
    apiGetSceneManagementList,
  } from "@/api/scene";
  // 发布
  import PublishComponent from "./publishComponent.vue";
  // 分析
  import AnalysisComponent from "./analysisComponent.vue";
  import md5 from "js-md5";
  
  export default {
    components: {
      // textAgentDemo,
      modelAlgorithmPlugin,
      knowledgeBaseSet,
      dragDemo,
      selectMcp,
      addKnowledgeBase,
      addFunctionOrTool,
      orderStep,
      showSetting,
      publishWay,
      publishSuccess,
      createApplication,
      addSensitiveDialog,
      sceneBindingVisibleDialog,
      ConfigGroup,
      wangeditor,
      SearchShowSetting,
      vocabularyDrawer,
      ModelSetting,
      AgentPattern,
      FuntionOrTool,
      NewAddFunctionOrTool,
      SelectTemplate,
      DisplaySettings,
      AddWorkflowDialog,
      PreView,
      PublishComponent,
      AnalysisComponent,
      ModalDebug
    },
    data() {
      return {
        defaultId: '',
		  dialogVisibleApplicationFlag: false,
		  applicationQuickCommandObj:{
			  commandContent:'',
			  commandName:'',
			  commandType:'command'
		  },
		applicationQuickCommandList:[],
		mcpIdList:[],
		mcpServerIdList:[],
    mcpHoverList:{},
    mcpHeightList:{},
		activeName: "first",
        isVoice: false,
        isSound:false,
        visibleVoice:false,
        typeLoading: false,
		loadingDe: false,
        changeAppStatus: false,
        autoFillVisible: false,
        debugType: false,
		deleteDialogVisible:false,
        startTab:1,
        dragDemoKey:1,
        component:{},
        showDemo:false,
        workflowTabs: [
            {
                label: this.$t('node'),
                ref:'nodeDrawer',
                icon: "add-circle-line",
                activeIcon: "add-circle-fill",
            },
            {
              label: this.$t('test'),
              ref:'runDrawer',
              icon: "terminal-box-line",
              activeIcon: "terminal-box-fill",
            },
        ],
        personWorkTabs:[
          {
            label: this.$t('test'),
            ref:'runDrawer',
            icon: "terminal-box-line",
            activeIcon: "terminal-box-fill",
          },
        ],
        tabs: [
            {
                label: this.$t('node'),
                ref:'nodeDrawer',
                icon: "add-circle-line",
                activeIcon: "add-circle-fill",
            },
            {
                label: this.$t('function'),
                ref:'funcDrawer',
                icon: "shapes-line",
                activeIcon: "shapes-fill",
            },
            {
                label: this.$t('debug'),
                ref:'previewDrawer',
                icon: "terminal-box-line",
                activeIcon: "terminal-box-fill",
            },
        ],
        personDialogTabs:[
          {
            label: this.$t('debug'),
            ref:'previewDrawer',
            icon: "terminal-box-line",
            activeIcon: "terminal-box-fill",
          },
        ],
        activeHeadTab: 1,
        leftTabs: [
          {
            label: this.$t('addFeatures'),
            icon: 'function-add-fill',
            ref: 'functionOrToolRef',
          },
        ],
        activeLeftTab: null,
        appTemplateTypeTabs: [
          {label: this.$t('desktop'), value: 1},
          {label: this.$t('mobile'), value: 2}
        ],
        activeAppTemplateType: 1,
        appForm: {
          logo: require("@/assets/images/applicationlogo.svg"),
          modelId: "f570229417ef4d79814ff51a65447eb5",
          templateId: "",
          mobileTemplateId: "",
          applicationName: "",
          applicationCode: "",
          clientAuthChannel: "",
          introduce: "",
          webTitle: "",
          greeting: "",
          disclaimer: "",
          tailTalk: "",
          failureTalk: "",
          ttsId: "",
          streamVoice: "",
          sttId: "",
          buttonStyle: "",
          buttonText: "",
          
          answerTimeout: "",
          notAnswer: "",
          promptTemplate: "",
          subjectPrompt: "",
          ocrFlag: false,
          videoFlag: false,
          multiDialogueFlag: false,
          // multiDialogueNum: 1,
          knowledgeFlag: false,
          rewritingFlag: false,
		  modelFallbackFlag: false,
		  polishFlag: false,
		  modelAnswerFlag: true,
		  sensitiveFlag: false,
		  wenshuFlag: false,
		  networkFlag: false,
		  ipFlag: false,
          recommendation: "",
          recommendationNum: "",
          systemPrompt: "",
          remark: "",
          rearrangeModel: 'yayi',
		  multiDialogueNum:3,
		  maxToken:2048,
          contentScore: 1.76,
          rangeContentScore: 0.88,
          qaTitleScore: 1.91,
          qaRangeTitleScore: 0.91,
          qaContentScore: 0.88,
          qaRangeContentScore: 0.88,
          filterNum: 3,
          prepareNum: 60,
          publishStatus: "1",
            previewDoc: "",
          finalNetworkFlag: this.$t('no'),
          cacheSearchResult:this.$t('no'),
          modelProblem:this.$t('no'),
          robotIcon: "",
          virtualHumanLogo: "",
          webIcon: "",
          attributionLogo: "",
          userIcon: "",
          knowledgeIds: [],
          interceptWordHouses: [],
          presetQuestionList: [""],
          identityIcon: "",
          networkChannel: "",
          processStep: [
            "builtIn",
            "interceptSensitive",
            "subjectTalk",
            "findQaTitle",
            "findQaContent",
            "finalCollectStrategy",
            "findAnswerByModel",
            "yayiKnowldegeStrategy",
          ],
		  
          polishPrompt: "",
          inputPlaceholder: "",
          prologue: "",
          pcAuthChannel: "",
          toHumanFlag: "",
          docLinkType: "",
          backgroundImageUrl: "",
          facadeImageUrl: "",
          subjectFlag: "",
          backOldVersionLink: "",
          aboutWebsite: "",
          menuServiceFlag: "",
          policyListFlag: "",
          feedbackFlag: "",
          historyFlag: "",
          videoResolveFlag: "",
          aiQuestionFlag: "",
          networkWebSearchingFlag: "",
          implantSwitch: "",
          type: "",
          
          workflowIds: [],
          picture:'',
          url:'',
          refIndexFlag:"否",
        },
        voiceBroadcastConfig:{},
        selectedIndex:-1,
        uploadBtnLogo: false,
        fileListLogo: [],
        actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
        rules: {
          applicationName: [
            { required: true, message: this.$t('pleaseEnterName'), trigger: "blur" },
          ],
          introduce: [
            { required: true, message: this.$t('pleaseEnterDescription'), trigger: "blur" },
          ],
        },
        chatGptList: {
          智谱清言: require("@/assets/images/zpqy.png"),
          文心一言: require("@/assets/images/wxyy.png"),
          deepseek: require("@/assets/images/deepseek.png"),
          kimi: require("@/assets/images/kimi.png"),
          雅意: require("@/assets/images/yayi.png"),
          百川: require("@/assets/images/baichuan.png"),
          星火: require("@/assets/images/xinghuo.png"),
          openAI: require("@/assets/images/openai.png"),
          MINIMAX: require("@/assets/images/MiniMax.png")
        },
        chatGptIdList: {
          f570229417e36672814ff51a695447eb5: require("@/assets/images/zpqy.png"),
          f570229417ef4672814ff51a695447eb5: require("@/assets/images/wxyy.png"),
          f570229417ef4672814ff51a65447eb5: require("@/assets/images/deepseek.png"),
          f570229417ef4d72814ff51a65447eb5: require("@/assets/images/kimi.png"),
          f570229417ef4d79814ff51a65447eb5: require("@/assets/images/yayi.png"),
          e4cc6a8d45f74e6099cae1f56a0fe5e9: require("@/assets/images/doubao.png"),
        },
        modelCheckName: [],
        setBaseVisible: false, // 设置知识库参数
        addBaseVisible: false, // 新增知识库
        konwlwdgeList: [], //知识库数据
        knowledgeIdArr: [],
        selectMcpVisible:false,//新增MCP
        interceptWordHouses: [],
        toolVisible: false, // 功能工具添加
        orderStepVisible: false, //执行顺序步骤
        showSettingVisible: false, // 暂时设置
        fabuDialogVisible: false, // 发布
        clOptions: [
          {
            label: this.$t('matchingQuestion'),
            value: "qa-question",
          },
          {
            label: this.$t('matchingAnswer'),
            value: "qa-content",
          },
          {
            label: this.$t('matchingKnowledgeBase'),
            value: "knowledge",
          },
          {
            label: this.$t('largeModel'),
            value: "qa-largemodel",
          },
        ],
        h5AuthChannels: [],
        pcAuthChannels: [],
        funcOrTool: [], //功能或工具
        detailedSettings:
          this.$t('roleDescription'),
        roleSettingPlaceholder:`用结构化的方式，描述智能体的角色设定、工作流程、原则。
比如：
# 角色：你是一个天气预报员，可以查询天气信息。
## 技能
- 询问用户地理位置、时间天气需求
- 提供详细的天气预报：包含气温、湿度、天气状况、风速、紫外线和PM2.5值。
## 原则
- 只能提供天气信息，不回答其他问题；
- 所有数据都要从插件中获取，不能自行编造；`,
        ttsListData: [],
        fabuSuccessVisible: false, // 发布成功
        fabuSuccessData: {},
        defaultDialogVisible: false, //自动填充
        defaultValuesId: "",
        editApplicationVisible: false, //编辑应用名称
        activeTabsName: "first", //角色设定
        sceneSettingVisible: false, // 场景设置
        sceneSettingSwitch: false, // 场景设置开关
        addSensitiveVisible: false, // 关联敏感词库
        networkChannelList: [], // 关联敏感词库
        rightLoading: false,
        selectIds: [],
        pluginList: [],
        sceneBindingVisible: false, // 场景绑定开关
        firstApplicationId: '',
        firstId: '',
        wangeditorVisible: false,
        addWorkflowVisible: false,
        workflowList: [], //工作流数据
        workflowIdArr: [],
        interceptWordHousesList: [], //敏感词库
        autoOptimizationLoading: false,
        visibleChange: false,
        saveTime: "",
        loading2: false,
        voiceList: [],
        voiceTitle: '请选择发音',
        voiceTitleId:null,
        isHidLabel:false,
        showUrlBtn:true,
        sceneForm:{
          modelId:"",
          modelName:""
        },
        roleKeyWord:"",
        roleFlag:false,
        // 问题建议
        setQuestionAdviceVisible:false,
        // 模糊问题引导
        setVagueQuestionVisible:false,
        doorsill_llm_usr_prompt:`原问题:{question} 。
以上是一个民众的提问，请按照以下步骤进行。 
##**步骤** 
#步骤一.首先判断原问题【{question}】的语义是否存在风险，如果你认为存在风险，则\"是否存在风险\"字段值为\"是\",；如果你认为无任何风险，则\"是否存在风险\"字段值为\"否\"。
#步骤二.首先判断用户输入的原问题【{question}】的描述内容是否模糊，如果原问题中包含字段【是什么，如何，怎么，为什么，什么是】其中一个，可视为非模糊问题；如果原问题中包含完整的问题描述，可视为非模糊问题；如果原问题意图不明或者仅为一个关键词或者仅为领域专有名词，并且没有疑问语义，则视为模糊问题。 
#步骤三.如果在步骤二中原问题被视为【模糊问题】，则不需要进行问题优化，即\"新问题\"字段值为空。 
#步骤四.如果在步骤二中原问题被视为【非模糊问题】，则在\"新问题\"字段中给出一个优化后的新问题（**【注意】此处的“新问题”为对原问题【{question}】的改写，而非对原问题进行回答。**）。确保新问题和原问题保持一定的关联性，不要有过大的跳跃，存在\"\"内。
#步骤五.请用json格式输出，字段包括：是否存在风险，是否模糊，新问题，原问题，其中是否模糊仅包含是或否。 
输出要求:用json{\"是否存在风险\":\"\",\"是否模糊\":\"\",\"新问题\":\"\",\"原问题\":\"\"}输出。`,
        configExtend:{
          vagueGuideModelId:"",
          doorsill_llm_usr_prompt:this.doorsill_llm_usr_prompt,
          vague_guide_qa_enable:"否",
          vague_prompt_from_knowledge_enable:"否",
          vague_from_llm_enable:"否",
          vague_not_answer_flag:"否"
        },
        scenePrompt:`今天是{date}，我将给你{num}个场景和一个问题，最后一句为问句并且只有一个问句，请分析问句是属于哪几个场景。
以json格式输出{ "场景"}，参考输出例子：{"场景":"[【场景1】,【场景2】,【场景3】]"}
所有场景如下：{scenes}
我的问题是：{question}`,
        regularList:[
          {
            key:"vague_guide_qa_enable",
            title:"匹配QA对",
            tip:"基于关联知识库中的QA对生成问题建议"
          },
          {
            key:"vague_prompt_from_knowledge_enable",
            title:"匹配知识库",
            tip:"基于关联知识库中除QA对外的其他知识生成"
          },
          {
            key:"vague_from_llm_enable",
            title:"大模型发散",
            tip:"通过大模型发散生成问题建议"
          },
        ],
        regularModelRadio:"1",
        functionForm:{
          SearchEngine:{
            internet_search_count:1,
            internet_rewriting_score:0.64,
            internet_redis_count:10,
            internet_redis_enable:"是"
          },
          Scenesetting:{
            sceneModelId:"",
            scenePrompt:this.scenePrompt
          },
          association:{
            association_q_flag:"否",
            association_a_flag :"否",
            association_knn_flag :"是",
            association_llm_flag :"是"
          }
        },
        // 答案溯源
        setAnswerSourceVisible:false,
        // 检索引擎
        setSearchEngineVisible:false,
        // 问题建议
        setQuestionImagineVisible:false,
        quesImagineList:[
          {
            key:"recommended_q_flag",
            title:"匹配问题",
            tip:"基于关联知识库中QA对的问题生成"
          },
          {
            key:"recommended_a_flag",
            title:"匹配答案",
            tip:"基于关联知识库中QA对的答案生成"
          },
          {
            key:"recommended_knn_flag",
            title:"匹配知识库",
            tip:"基于关联知识库中除QA对外的其他知识生成"
          },
          {
            key:"recommended_llm_flag",
            title:"大模型发散",
            tip:"通过大模型发散生成问题建议"
          },
        ],
        quesImagineForm:{
          recommended_q_flag:"否",
          recommended_a_flag :"否",
          recommended_knn_flag :"是",
          recommended_llm_flag :"是"
        },
        // 问题联想
        setAssociationVisible:false,
        associationList:[
          {
            key:"association_q_flag",
            title:"匹配问题",
            tip:"基于关联知识库中QA对的问题生成"
          },
          {
            key:"association_a_flag",
            title:"匹配答案",
            tip:"基于关联知识库中QA对的答案生成"
          },
          {
            key:"association_knn_flag",
            title:"匹配知识库",
            tip:"基于关联知识库中除QA对外的其他知识生成"
          },
          {
            key:"association_llm_flag",
            title:"大模型发散",
            tip:"通过大模型发散生成问题建议"
          },
        ],
        listAllData:[],
        // 场景设置
        setSceneVisible:false,
        sceneList: [], //知识库 分页
        sceneAllList: [], //知识库
        sceneListSelected: [], // 已绑定知识库
        sceneListSelectedId: [],
        sceneSelectedIdOld: [],
        mcpNewsLogo:require("@/assets/images/mcp_news.svg")
      };
    },
    props: {
      appConfigForm: Object,
      type: {
        type: String,
        default: ""
      }
    },
    // watch: {
    // 'appForm.type': {
    //     handler(newVal, oldVal) {
    //       this.submitAddApp(2,true)
    //     },
    //     immediate: true, // 可选：立即执行一次 handler
    //     deep: true // 可选：深度监听，如果 appForm 是一个对象
    //   }
    // },
    computed: {
      isAdmin(){
        return JSON.parse(sessionStorage.getItem("user")).powerType=="0" 
      },
      isAdminOrUser(){
        let obj=JSON.parse(sessionStorage.getItem("user"))
        return this.isAdmin || obj.accountName == this.appForm.createUser
      },
      recommendationTip() {
        return this.appForm.recommendationNum ? `在回复后自动提供${this.appForm.recommendationNum}个提问建议` : this.$t('zeroAutoSuggestion');
      },
      settingTip(){
        return `<iconpark-icon name="settings-4-line" size="16" style="margin-left:12px;" color="#1d2129"></iconpark-icon>`
      },
      headTabs() {
        let arr = [];
        let quXiaoYangShi = ['workflow', 'text-agent']
        if(quXiaoYangShi.includes(this.appForm.type)) {
          arr = [
            {label: this.$t('arrangement'), value: 1},
            {label: this.$t('style'), value: 2},
            {label: this.$t('publish'), value: 3},
          ] 
        }else{
          arr = [
            {label: this.$t('arrangement'), value: 1},
            {label: this.$t('style'), value: 2},
            {label: this.$t('publish'), value: 3},
          ]
        }
        return arr;
      },
      networkChannelName(){
        let obj=this.networkChannelList.find(item=>item.value==this.appForm.networkChannel)
        return obj?obj.label+"搜索":""
      },
      modelForm(){
        return {
          modelId:this.configExtend.vagueGuideModelId?this.configExtend.vagueGuideModelId:this.appForm.modelId
        }
      },
      sceneModelForm(){
        return {
          modelId:this.appForm.subjectLlmName?this.appForm.subjectLlmName:this.appForm.modelId
        }
      },
      conversationId() {
      const conversationId = this.$store.state.debug.questions.filter(obj => obj.conversationId && obj.conversationId.trim() !== '') 
      .map(obj => obj.conversationId);
      return conversationId.pop();
    }
    },
    created() {
      this.$store.commit('setWorkFlowType', this.appConfigForm.type)
      this.getfuntionList()
      this.configExtend.doorsill_llm_usr_prompt=this.doorsill_llm_usr_prompt
    },
   beforeDestroy() {
        this.$EventBus.$off("updateApplicationType");
        sessionStorage.removeItem('startValues');
        sessionStorage.removeItem('isH5');
    sessionStorage.removeItem('nodeInfo')
    document.removeEventListener('click', this.handleClickOutside);
 
    },
    mounted() {
	  
      document.addEventListener('click', this.handleClickOutside);
      this.startTab = this.appConfigForm.tab
      if(this.startTab && this.startTab === 3){
        this.handleTabClick(this.startTab)
        this.startTab = 1
      }
      this.fetchModleOptions();
      this.fetchKnowledgeList();
      this.changeAppStatus = false;
      if(this.appConfigForm.type == 'workflow' || this.appConfigForm.type == 'dialogue' || this.appConfigForm.type == "multi_agent") {
        this.queryWorkflowDetail(this.appConfigForm.applicationId, this.appConfigForm.type)
      }

      this.getTtsId(); //语音列表
      this.knowledgeList(); // 知识库
      this.getSceneApplicationRefListAll(true); // 场景
      this.getH5AuthChannelList();
      this.getPcAuthChannelList();
      this.defaultAppInfoSet(this.appConfigForm);
      // this.defaulFunctiontList();
      this.initNetworkChannel();
      // this.getWorkflowList();
      this.getInterceptWordHousesList();
      this.$EventBus.$on('saveApplication', () => {
        this.handleAutoSave();
      })
      this.$EventBus.$on('saveWorkflowAgain', () => {
          console.log(666);
          
          this.submitWorkflow(5)
      })
      this.$EventBus.$on('updateApplicationType', (val) => {
        this.updateAgentPattern(val);
      })
      this.$EventBus.$on('changeApplicationStatus', (val) => {
        this.changeApplicationStatus(val)
      })
      this.$EventBus.$on('addKnowledgeToApp', (val) => {
        this.addKnowledgeToApp(val)
      })
      this.$EventBus.$on('deleteKnowledgeToApp', (val) => {
        this.deleteKnowledgeToApp(val)
      })
	  console.log('this.appConfigForm.mcpServerIds11',this.appConfigForm.mcpServerIds)
	  if(this.appConfigForm.mcpServerIds){
	  		this.mcpIdList = this.appConfigForm.mcpServerIds
				  this.getListAndDetails()
	  }
    setTimeout(()=>{
      this.showLabel()
    },100)
    window.addEventListener("resize",()=>{
     this.showLabel()
    })
  },

    methods: {
      ...mapMutations(["setFuncData", "setShowDrawerData"]),
      ...mapActions(['fetchModleOptions','fetchKnowledgeList']),
      modelSettingChange(val) {
        this.appForm.modelAnswerFlag = val;
        this.temporarSave(2);
      },
      sceneModelChange(val){
        this.sceneForm={...val}
      },
      closeFunctionOrTool() {
        this.visibleChange = false;
        this.showLabel()
      },
      openFunctionOrTool() {
        this.visibleChange = true;
        this.debugType = false;
        this.showLabel()
      },
      showAddFuctoinTool(){
        this.openFunctionOrTool()
        console.log("~~~~~~~开启",this.$refs.functionOrToolRef);
        this.$nextTick(()=>{
          this.$refs.functionOrToolRef.activeName=this.activeName
        })
        
      
      },
      cancelTemplate(){
      	this.dialogVisibleApplicationFlag = false
      },
      confirmTemplate(){
		  this.applicationQuickCommandList.push(JSON.parse(JSON.stringify(this.applicationQuickCommandObj)))
      	this.appForm.applicationQuickCommandList.push(JSON.parse(JSON.stringify(this.applicationQuickCommandObj)))
		this.dialogVisibleApplicationFlag = false
      },
      textAgentSave() {
        this.loading2 = true;
        // 保存文本生成模式
        this.$refs.modelAlgorithmPluginRefDom.yingYongTextAgentSave();
        setTimeout(() => {
          this.loading2 = false;
        }, 500);
      },
      addWorkflow() {
        let params = {
            componentDesc: this.appForm.introduce,
            componentName: this.appForm.applicationName,
            type: this.appForm.type === 'multi_agent' ? 7 : this.appForm.type === 'workflow' ? 4 : 5,
            icon: this.appForm.logo,
            componentId: this.appForm.applicationId,
        };
        this.typeLoading = true
        saveWorkflowComponent(params).then((res) => {
            if (res.code == "000000") {
              this.queryWorkflowDetail(this.appForm.applicationId, this.appForm.type)
            } else {
              this.typeLoading = false
            }
        });
      },
      // 新增应用插件关联表
      addWorkflowApplicationPlugin() {
          const applicationId =
              this.$store.state?.workflow?.funcData?.applicationId;
          const pluginList = this.$store.state?.workflow?.funcPluginList;
          addApplicationPluginData({
              applicationId,
              pluginList,
          }).then((res) => {
              if (res.code == "000000") {
                  this.$message.success(this.$t("successed"));
              }
          });
      },
      removeSuffix(str) {
        // 使用正则表达式检查字符串是否以 '_4' 或 '_5' 结尾
        const suffixPattern = /(_4|_5)$/;
        if (suffixPattern.test(str)) {
          // 使用 replace 方法去掉后缀
          return str.replace(suffixPattern, '');
        }
        return str;
      },
      submitWorkflow(flag) {
        let dragDemo = 'dragDemo' + this.appForm.type;
        let {nodes,edges,msg} = this.$refs[dragDemo]?.getNodesAndEdges(flag, this.component);
        if(!nodes) return false
        if (msg && flag === 2) {
            this.$message({
                type: "warning",
                message: msg,
            });
            return;
        }
        const pluginList =
            this.$store.state?.workflow?.funcPluginList || [];
            console.log("length",pluginList);
            
        if (pluginList.length) {
            this.addWorkflowApplicationPlugin();
        }
        const param = this.$store.state.workflow.funcData;
        if(flag == 2) {
          this.loading2 = true;
        }
        let applicationInfo = {
            ...param,
            ...this.$store.state.workflow.showDrawerData,
            applicationId:this.removeSuffix(param.applicationId),
            type:this.appForm.type,
            applicationName: this.appForm.applicationName,
            facadeImageUrl: this.appForm.facadeImageUrl,
            introduce: this.appForm.introduce,
            virtualHumanFlag: param.virtualHumanFlag ? '是' : '否',
            voiceDialogueFlag: param.voiceDialogueFlag ? '是' : '否',
            modelAnswerFlag: param.modelAnswerFlag ? '是' : '否',
            sensitiveFlag: param.sensitiveFlag ? '是' : '否',
            networkFlag: param.networkFlag ? '是' : '否',
            ipFlag: param.ipFlag ? '是' : '否',
            ocrFlag: param.ocrFlag ? '是' : '否',
            videoFlag: param.videoFlag ? '是' : '否',
            multiDialogueFlag: param.multiDialogueFlag ? '是' : '否',
            knowledgeFlag: param.knowledgeFlag ? '是' : '否',
            rewritingFlag: param.rewritingFlag ? '是' : '否',
            polishFlag: param.polishFlag ? '是' : '否',
            makeType: null,
            recommendQuestionsShowFlag: param.recommendQuestionsShowFlag
                ? "1"
                : "0",
			associationQuestionsShowFlag: param.associationQuestionsShowFlag
                ? "1"
                : "0",
            sourceShowFlag: param.sourceShowFlag ? "1" : "0",
            processStep: param.processStep
                ? param.processStep.join(",")
                : "",
            deleteFlag: "0",
            publishStatus: flag == 1 ? "4" : "1", // 发布传1 暂存传4
        };
        console.log("~~~~~~~提交参数",applicationInfo);
        flag === 1 && (this.saveLoading = true);
        flag === 2 && (this.publishLoading = true);
        let canvas  = JSON.stringify(this.$refs[dragDemo].graph.toJSON())
        console.log("~~~~~~~提交参数",this.component);
        saveWorkflow({
            ...this.component,
            applicationInfo,
            canvas: canvas,
            nodes,
            nodeRel: edges,
            status: 1,
            updateTime: this.saveTime
        })
            .then((res) => {
                this.publishLoading = false;
                this.saveLoading = false;
                if (res.code == "000000") {
                    // flag === 2 && this.closeDialog();
                } else {
                    this.$message({
                        type: "error",
                        message: res.msg,
                    });
                }
                if(flag == 2) {
                  this.loading2 = false;
                }
            })
            .catch((err) => {
                if(flag == 2) {
                  this.loading2 = false;
                }
                this.publishLoading = false
                this.saveLoading = false
                this.$message({
                    type: "error",
                    message: err,
                });
            });
        },
      queryWorkflowDetail(componentId, type) {
        queryWorkflowDetail({
            componentId,
            type: type === 'multi_agent' ? 7 : type === 'workflow' ? 4 : 5,
        }).then((res) => {
            this.typeLoading = false
            if (res.code == "000000") {
                this.init(res.data);
                this.saveTime = res.data?.updateTime?.split(' ')[1] || res.time?.split(' ')[1]?.split('.')[0];
              } else {
                  this.$message({
                      type: "error",
                      message: res.msg,
                  });
              }
            })
            .catch((err) => {
                this.$message({
                    type: "error",
                    message: err,
                });
            });
      },
      init(data) {
        this.component = JSON.parse(JSON.stringify(data));
        console.log(this.component,"initdata");
        
        this.dragDemoKey++
        if (
            JSON.stringify(this.component) != "{}" &&
            this.component?.applicationInfo &&
            JSON.stringify(this.component.applicationInfo) != "{}"
        ) {
          this.initFuncData(this.component.applicationInfo);
          //this.initShowDrawerData(this.component.applicationInfo);
        }
        this.showDemo = true;
      },
      initFuncData(item) {
            let funcData = {
                ...item,
                id: item.id,
                applicationId: item.applicationId,
                templateId: item.templateId,
                mobileTemplateId: item.mobileTemplateId,
                applicationName: item.applicationName,
                applicationCode: item.applicationCode,
                introduce: item.introduce,
                greeting: item.greeting,
                prologue: item.prologue,
                inputPlaceholder: item.inputPlaceholder,
                webTitle: item.webTitle,
                disclaimer: item.disclaimer,
                docLinkType: item.docLinkType,
                tailTalk: item.tailTalk,
                failureTalk: item.failureTalk,
                virtualHumanId: item.virtualHumanId,
                clientAuthChannel: item.clientAuthChannel,
                pcAuthChannel: item.pcAuthChannel,
                toHumanFlag: item.toHumanFlag,
                ttsId: item.ttsId ? parseInt(item.ttsId) : null,
                sttId: item.sttId ? parseInt(item.sttId) : null,
                buttonStyle: item.buttonStyle,
                buttonText: item.buttonText,
                modelId: item.modelId,
                answerTimeout: item.answerTimeout,
                notAnswer: item.notAnswer,
                promptTemplate: item.promptTemplate,
                subjectPrompt: item.subjectPrompt,
                multiDialogueNum: item.multiDialogueNum,
                recommendation: item.recommendation,
                recommendationNum: item.recommendationNum,
                systemPrompt: item.systemPrompt,
                remark: item.remark,
                contentScore: item.contentScore,
                rearrangeModel: item.rearrangeModel,
                rangeContentScore: item.rangeContentScore,
                qaTitleScore: item.qaTitleScore,
                qaRangeTitleScore: item.qaRangeTitleScore,
                qaContentScore: item.qaContentScore,
                qaRangeContentScore: item.qaRangeContentScore,
                filterNum: item.filterNum,
                prepareNum: item.prepareNum,
                publishStatus: item.publishStatus,
                logo: item.logo,
                robotIcon: item.robotIcon,
                virtualHumanLogo: item.virtualHumanLogo,
                picture: item.picture,
                url: item.url,
                webIcon: item.webIcon,
                userIcon: item.userIcon,
                identityIcon: item.identityIcon,
                previewDoc: item.previewDoc,
                virtualHumanFlag: [this.$t('yes'), true, "true"].includes(
                    item.virtualHumanFlag
                )
                    ? true
                    : false,
                voiceDialogueFlag: [this.$t('yes'), true, "true"].includes(
                    item.voiceDialogueFlag
                )
                    ? true
                    : false,
                modelAnswerFlag: [this.$t('yes'), true, "true"].includes(
                    item.modelAnswerFlag
                )
                    ? true
                    : false,
                sensitiveFlag: [this.$t('yes'), true, "true"].includes(item.sensitiveFlag)
                    ? true
                    : false,
                networkFlag: [this.$t('yes'), true, "true"].includes(item.networkFlag)
                    ? true
                    : false,
                ipFlag: [this.$t('yes'), true, "true"].includes(item.ipFlag)
                    ? true
                    : false,
                ocrFlag: [this.$t('yes'), true, "true"].includes(item.ocrFlag)
                    ? true
                    : false,
                videoFlag: [this.$t('yes'), true, "true"].includes(item.videoFlag)
                    ? true
                    : false,
                multiDialogueFlag: [this.$t('yes'), true, "true"].includes(
                    item.multiDialogueFlag
                )
                    ? true
                    : false,
                knowledgeFlag: [this.$t('yes'), true, "true"].includes(item.knowledgeFlag)
                    ? true
                    : false,
                sourceShowFlag: item.sourceShowFlag,
                recommendQuestionsShowFlag: item.recommendQuestionsShowFlag,
				associationQuestionsShowFlag: item.associationQuestionsShowFlag,
                rewritingFlag: [this.$t('yes'), true, "true"].includes(item.rewritingFlag)
                    ? true
                    : false,
                subjectFlag: item.subjectFlag,
                presetQuestionList:
                    item.presetQuestionList &&
                    item.presetQuestionList.length > 0
                        ? item.presetQuestionList
                        : [""],
                knowledgeIds: item.knowledgeIds,
                networkChannel: item.networkChannel,
                polishFlag: [this.$t('yes'), true, "true"].includes(item.polishFlag)
                    ? true
                    : false,
                polishPrompt: item.polishPrompt,
                backgroundImageUrl: item.backgroundImageUrl,
                facadeImageUrl: item.facadeImageUrl,
                type:
                    item.type &&
                    [
                        "default",
                        "image",
                        "doc",
                        "code",
                        "ppt",
                        "txt",
                        "excel",
                    ].includes(item.type)
                        ? "file"
                        : item.type,
            };

            if (item.processStep && Array.isArray(item.processStep)) {
                funcData.processStep = [...item.processStep];
            } else {
                funcData.processStep = item.processStep
                    ? item.processStep.split(",")
                    : [];
            }
            this.setFuncData(funcData);
        },
      sendMessage() {
        const iframe = this.$refs.iframe;
        if (iframe) {
          const iframeWindow = iframe.contentWindow;
          // 现在可以安全地使用iframeWindow对象了
          iframeWindow.postMessage({ param1: this.$t('testChildComponentParams') }, "*");
        }
      },
      closeDialog() {
		
		 
		 
		  
		 
		 
		
		 console.log('this.appForm.sensitiveFlag',this.appForm.sensitiveFlag) 
		
		console.log('this.appForm.wenshuFlag',this.appForm.wenshuFlag)  
		console.log('this.appForm.networkFlag',this.appForm.networkFlag)  
		console.log('this.appForm.ipFlag',this.appForm.ipFlag)  
		console.log('this.appForm.videoFlag',this.appForm.videoFlag)  
		console.log('this.appForm.polishFlag',this.appForm.polishFlag) 
		 console.log('this.appForm.modelFallbackFlag',this.appForm.modelFallbackFlag)
		 console.log('this.appForm.rewritingFlag',this.appForm.rewritingFlag)  
		 console.log('this.appForm.multiDialogueFlag',this.appForm.multiDialogueFlag) 
		 console.log('this.appForm.knowledgeFlag',this.appForm.knowledgeFlag)
		 console.log('this.appForm.ocrFlag',this.appForm.ocrFlag)
		// if(this.appForm.virtualHumanId!=''||this.appForm.sensitiveFlag||
		// this.appForm.wenshuFlag||this.appForm.networkFlag||this.appForm.ipFlag||this.appForm.ocrFlag||this.appForm.videoFlag||
		// this.appForm.multiDialogueFlag||this.appForm.knowledgeFlag||this.appForm.rewritingFlag||this.appForm.polishFlag||
		// this.appForm.modelFallbackFlag){
		// 	this.deleteDialogVisible = true
		// }else{
		// 	this.fabuSuccessVisible = false;
		// 	this.$emit("configCloseDialog", false);
		// }
		if(this.appForm.virtualHumanId!=''){
			this.deleteDialogVisible = true
		}else{
			this.fabuSuccessVisible = false;
			this.$emit("configCloseDialog", false);
		}
        // this.fabuSuccessVisible = false;
        // this.$emit("configCloseDialog", false);
      },
	  confirmDelete(){
	  	this.fabuSuccessVisible = false;
	  	this.$emit("configCloseDialog", false);
	  },
      defaultAppInfoSet(item) {
        this.knowledgeIdArr = item.knowledgeIds || [];
        this.uploadBtnLogo = item.logo ? true : false;
        this.workflowIdArr = item.workflowIds || [];
        console.log(item,"&&&&&&&");
        this.voiceBroadcastConfig =JSON.parse(item.voiceBroadcastConfig)
        this.isVoice=item.sttId?true:false
        this.isSound=item.ttsId?true:false
        if(this.voiceBroadcastConfig){
        this.voiceTitleId=this.voiceBroadcastConfig.id
        pageQueryVoice({
        pageNo: 1,
        pageSize: 8,
        frequenceUseFlag: 1,
        tag: '',
        category: '语音输出',
      }).then((res) => {
        if (res.code == "000000") {
          this.voiceList = res.data.records || []
          if(this.voiceList.length==0 || !this.voiceTitleId) return
          let voiceObj=this.voiceList.find(ele=>ele.id==this.voiceTitleId)
          this.voiceTitle= voiceObj?voiceObj.fullStyle:"请选择发音"
          this.selectedIndex=this.voiceList.findIndex(ele=>ele.id==this.voiceTitleId)
          console.log(this.selectedIndex,'index');
          
        }
      })
        }
        this.sceneForm.modelId=item.subjectLlmName
        console.log(item.networkChannel);
        
        this.appForm = {
          ...item,
          id: item.id,
          applicationId: item.applicationId,
          templateId:  item.templateId,
          mobileTemplateId: item.mobileTemplateId,
          applicationName: item.applicationName,
          applicationCode: item.applicationCode,
          introduce: item.introduce,
          greeting: item.greeting,
          prologue: item.prologue,
          inputPlaceholder: item.inputPlaceholder,
          webTitle: item.webTitle,
          disclaimer: item.disclaimer,
          docLinkType: item.docLinkType,
          tailTalk: item.tailTalk,
          failureTalk: item.failureTalk,
          virtualHumanId: item.virtualHumanId,
          clientAuthChannel: item.clientAuthChannel,
          pcAuthChannel: item.pcAuthChannel,
          toHumanFlag: item.toHumanFlag,
          ttsId: item.ttsId ? parseInt(item.ttsId) : null,
          sttId: item.sttId ? parseInt(item.sttId) : null,
          buttonStyle: item.buttonStyle,
          buttonText: item.buttonText,
          modelId: item.modelId || "f570229417ef4d79814ff51a65447eb5",
          answerTimeout: item.answerTimeout,
          notAnswer: item.notAnswer,
          promptTemplate: item.promptTemplate,
          subjectPrompt: item.subjectPrompt?item.subjectPrompt:this.scenePrompt,
          multiDialogueNum: item.multiDialogueNum,
          recommendation: item.recommendation,
          interceptWordHouses: item.interceptWordHouses,
          recommendationNum: item.recommendationNum,
          systemPrompt: item.systemPrompt || '',
          remark: item.remark,
          contentScore: item.contentScore,
          rearrangeModel: item.rearrangeModel,
          rangeContentScore: item.rangeContentScore,
          qaTitleScore: item.qaTitleScore,
          qaRangeTitleScore: item.qaRangeTitleScore,
          qaContentScore: item.qaContentScore,
          qaRangeContentScore: item.qaRangeContentScore,
          filterNum: item.filterNum,
          prepareNum: item.prepareNum,
          publishStatus: item.publishStatus,
          logo: item.logo,
          robotIcon: item.robotIcon,
          virtualHumanLogo: item.virtualHumanLogo,
          picture: item.picture,
          url: item.url,
          webIcon: item.webIcon,
          attributionLogo: item.attributionLogo,
          userIcon: item.userIcon,
          identityIcon: item.identityIcon,
          previewDoc: item.previewDoc,
          finalNetworkFlag: item.finalNetworkFlag,
          streamVoice: item.streamVoice,
          virtualHumanFlag: [this.$t('yes'), true, "true"].includes(item.virtualHumanFlag)
            ? true
            : false,
          voiceDialogueFlag: [this.$t('yes'), true, "true"].includes(item.voiceDialogueFlag)
            ? true
            : false,
          modelAnswerFlag: [this.$t('yes'), true, "true"].includes(item.modelAnswerFlag)
            ? true
            : false,
          sensitiveFlag: [this.$t('yes'), true, "true"].includes(item.sensitiveFlag)
            ? true
            : false,
          wenshuFlag: [this.$t('yes'), true, "true"].includes(item.wenshuFlag)
            ? true
            : false,
          networkFlag: [this.$t('yes'), true, "true"].includes(item.networkFlag)
            ? true
            : false,
          ipFlag: [this.$t('yes'), true, "true"].includes(item.ipFlag) ? true : false,
          ocrFlag: [this.$t('yes'), true, "true"].includes(item.ocrFlag) ? true : false,
          videoFlag: [this.$t('yes'), true, "true"].includes(item.videoFlag) ? true : false,
          multiDialogueFlag: [this.$t('yes'), true, "true"].includes(item.multiDialogueFlag)
            ? true
            : false,
          knowledgeFlag: [this.$t('yes'), true, "true"].includes(item.knowledgeFlag)
            ? true
            : false,
          sourceShowFlag: item.sourceShowFlag,
          recommendQuestionsShowFlag: item.recommendQuestionsShowFlag,
		  associationQuestionsShowFlag: item.associationQuestionsShowFlag,
          rewritingFlag: [this.$t('yes'), true, "true"].includes(item.rewritingFlag)
            ? true
            : false,
          subjectFlag: item.subjectFlag,
          presetQuestionList:
            item.presetQuestionList && item.presetQuestionList.length > 0
              ? item.presetQuestionList
              : [""],
          knowledgeIds: item.knowledgeIds || [],
          networkChannel: item.networkChannel,
          polishFlag: [this.$t('yes'), true, "true"].includes(item.polishFlag)
            ? true
            : false,
          polishPrompt: item.polishPrompt,
          backgroundImageUrl: item.backgroundImageUrl,
          facadeImageUrl: item.facadeImageUrl,
          backOldVersionLink: item.backOldVersionLink,
          aboutWebsite: item.aboutWebsite,
          menuServiceFlag: item.menuServiceFlag,
          policyListFlag: item.policyListFlag,
          feedbackFlag: item.feedbackFlag,
          historyFlag: item.historyFlag,
          videoResolveFlag: item.videoResolveFlag,
          aiQuestionFlag: item.aiQuestionFlag,
          networkWebSearchingFlag: item.networkWebSearchingFlag,
          implantSwitch: item.implantSwitch,
          type: item.type || 'qa',
          workflowIds: item.workflowIds || [],
          publishAppStore: item.publishAppStore,
          clientAuthChannel: item.clientAuthChannel,
          pcAuthChannel: item.pcAuthChannel,
          publishType: item.publishType,
          apiKey: item.apiKey,
          apiSecret: item.apiSecret,
          modelFallbackFlag: [this.$t('yes'), true, "true"].includes(item.modelFallbackFlag)
            ? true
            : false,
        };
        console.log(this.appForm,"appform");
        
        if (item.processStep && Array.isArray(item.processStep)) {
          this.appForm.processStep = [...item.processStep];
        } else {
          this.appForm.processStep = item.processStep
            ? item.processStep.split(",")
            : [];
        }
      },
      // 查询列表应用插件关联表
      defaulFunctiontList() {
        applicationPluginDataList({
          applicationId: this.appForm.applicationId,
        }).then((res) => {
          if (res.code == "000000") {
            this.funcOrTool = [];
            this.pluginList = res.data || [];
            this.listAllData.forEach((element)=>{
              const matchingObject = this.pluginList.find(
                (item) => item.pluginId === element.pluginId
              );
              if (matchingObject) {
                element.status = matchingObject.status;
                element.configExtend=matchingObject.configExtend
              } else {
                element.status = '否';
              }
            })
            if (res.data.length) {
              res.data.forEach((element) => {
                if (element.status === this.$t('yes')) {
                  this.funcOrTool.push(element.pluginCode);
                }
                if(element.pluginCode=='vagueQuestion'){
                  let initConfigExtend={
                    vagueGuideModelId:"",
                    doorsill_llm_usr_prompt:this.doorsill_llm_usr_prompt,
                    vague_guide_qa_enable:"否",
                    vague_prompt_from_knowledge_enable:"否",
                    vague_from_llm_enable:"否",
                    vague_not_answer_flag:"否"
                  }
                  this.configExtend=element.configExtend&&Object.keys(element.configExtend).length?element.configExtend:initConfigExtend
                }else if(element.pluginCode=='recommendation'){
                  let initConfigExtend={
                    recommended_q_flag:"否",
                    recommended_a_flag :"否",
                    recommended_knn_flag :"是",
                    recommended_llm_flag :"是"
                  }
                  this.quesImagineForm=element.configExtend&&Object.keys(element.configExtend).length&&element.configExtend.recommended_q_flag?element.configExtend:initConfigExtend
                }else if(element.pluginCode=='SearchEngine'){
                  let initConfigExtend={
                    internet_search_count:1,
                    internet_rewriting_score:0.64,
                    internet_redis_count:10,
                    internet_redis_enable:"是"
                  }
                  this.functionForm.SearchEngine=element.configExtend&&Object.keys(element.configExtend).length?element.configExtend:initConfigExtend
                }else if(element.pluginCode=='association'){
                  let initConfigExtend={
                    association_q_flag:"否",
                    association_a_flag :"否",
                    association_knn_flag :"是",
                    association_llm_flag :"是"
                  }
                  this.functionForm.association=element.configExtend&&Object.keys(element.configExtend).length&&element.configExtend.association_q_flag?element.configExtend:initConfigExtend
                }
              });
              // console.log(this.funcOrTool,"this.funcOrTool");
              
            }
            this.clearDefault();
          }
        });
      },
      // 打开弹窗
      openDialog(type) {
        if (type == "setBase") {
          this.setBaseVisible = true;
        } else if (type == "addBase") {
          this.knowledgeIdArr = this.appForm.knowledgeIds;
          this.addBaseVisible = true;
        } else if (type == "tool") {
          this.toolVisible = true;
        } else if (type == "order") {
          this.orderStepVisible = true;
        } else if (type == "setting") {
          this.showSettingVisible = true;
        } else if (type == "fabu") {
          this.appForm.publishStatus = "1";
          this.fabuDialogVisible = true;
        } else if (type == "editName") {
          this.editApplicationVisible = true;
        } else if (type == "sceneSetting") {
          this.sceneSettingVisible = true;
        } else if (type == "addSensitive") {
          this.interceptWordHouses = this.appForm.interceptWordHouses;
          this.addSensitiveVisible = true;
        } else if (type == "sceneBinding") {
          this.sceneBindingVisible = true;
        }else if(type == 'addWorkflow') {
          this.workflowIdArr = this.appForm.workflowIds;
          this.addWorkflowVisible = true
        }else if(type == "setScene"){
          this.sceneBindingVisible=true
        }else if(type == "setVagueQuestion"){
          if(!this.configExtend.vagueGuideModelId){
            this.configExtend.vagueGuideModelId=this.appForm.modelId
          }
          this.setVagueQuestionVisible=true
        }else if(type == "addMCP"){
          this.selectMcpVisible=true
        }
      },
      // 弹窗回调
      clickConfig() {
        this.addBaseVisible = false;
        this.setBaseVisible = false;
        this.toolVisible = false;
        this.orderStepVisible = false;
        this.showSettingVisible = false;
        this.fabuDialogVisible = false;
        this.fabuSuccessVisible = false;
        this.editApplicationVisible = false;
        this.sceneSettingVisible = false;
        this.addSensitiveVisible = false;
        this.sceneBindingVisible = false;
        this.addWorkflowVisible = false;
        this.setSceneVisible=false
      },
      changeStatus(filed, value) {
        console.log("--LL>>", filed, value);
        filed = filed === 'voice' ? 'voiceDialogueFlag' : filed;
        if (this.$t('yes') === value) {
          this.appForm[filed] = value;
          this.activeName = "first";
        } else {
          this.appForm[filed] = "";
        }
      },
      // 弹窗回调带参数
      clickConfigParams(type, data) {
        console.log('---------------clickConfigParams', type, data);
        if (type == "addBaseVisible") { 
          // this.addBaseVisible = false;
          // 新增知识库
          if (data.length > 0) {
            this.knowledgeIdArr = this.appForm.knowledgeIds = data || [];
          }
        }else if (type == "addSensitive") {
          // this.addSensitiveVisible = false;
          // 新增知识库
          if (data.length > 0) {
            this.interceptWordHouses = this.appForm.interceptWordHouses = data || [];
          }
        } else if (type == "setBaseVisible") {
          this.setBaseVisible = false;
          // 知识库参数设置
          let isChange = false;
          if(Object.keys(data).length > 0) {
            Object.keys(data).forEach(item => {
              if(this.appForm[item] != data[item]) {
                isChange = true;
              }
              this.appForm[item] = data[item];
            });
          }
          if(isChange) {
            this.changeApplicationStatus(true);
          }
        } else if (type == "toolVisible") {
          this.toolVisible = false;
          // 添加工具或者功能
          this.defaulFunctiontList();
        } else if (type == "orderStepVisible") {
          this.orderStepVisible = false;
          // 执行顺序步骤
          this.appForm.processStep = data;
        } else if (type == "showSettingVisible") {
          // 展示设置
          this.appForm.webTitle = data.webTitle;
          this.appForm.applicationCode = data.applicationCode;
          this.appForm.robotIcon = data.robotIcon;
          this.appForm.virtualHumanLogo = data.virtualHumanLogo;
          this.appForm.webIcon = data.webIcon;
          this.appForm.attributionLogo = data.attributionLogo;
          this.appForm.userIcon = data.userIcon;
          this.appForm.identityIcon = data.identityIcon;
          this.appForm.backgroundImageUrl = data.backgroundImageUrl; // 模版上传背景
          this.appForm.logo = data.logo;
          // this.appForm.mobileTemplateId = data.mobileTemplateId;
          // this.appForm.templateId = data.templateId;
          this.appForm.greeting = data.greeting;
          this.appForm.inputPlaceholder = data.inputPlaceholder;
          this.appForm.backOldVersionLink = data.backOldVersionLink;
          this.appForm.aboutWebsite = data.aboutWebsite;
          this.appForm.menuServiceFlag = data.menuServiceFlag;
          this.appForm.policyListFlag = data.policyListFlag;
          this.appForm.feedbackFlag = data.feedbackFlag;
          this.appForm.historyFlag = data.historyFlag;
          this.appForm.videoResolveFlag = data.videoResolveFlag;
          this.appForm.aiQuestionFlag = data.aiQuestionFlag;
          this.appForm.networkWebSearchingFlag = data.networkWebSearchingFlag;
          this.appForm.implantSwitch = data.implantSwitch;
          this.appForm.url = data.url;
          this.appForm.picture=data.picture
          this.showSettingVisible = false;
          
        } else if (type == "fabuDialogVisible") {
          // 发布
          this.fabuDialogVisible = false;
          this.appForm.publishStatus = data;
          this.submitAddApp(3);
        } else if (type == "fabuSuccessVisible") {
          // 发布成功 查看秘钥
          this.fabuSuccessVisible = false;
          this.$emit("configShowSecretKey", this.fabuSuccessData);
        } else if (type == "fabuSuccessVisible1") {
          // 发布成功
          this.closeDialog();
        } else if (type == "editApplicationName") {
          
          let isChange = false;
          if(Object.keys(data).length > 0) {
            Object.keys(data).forEach(item => {
              if(this.appForm[item] != data[item]) {
                isChange = true;
              }
            });
          }
          if(isChange) {
            this.changeApplicationStatus(true);
          }
          this.appForm.applicationName = data.applicationName;
          this.appForm.introduce = data.introduce;
          this.appForm.facadeImageUrl = data.facadeImageUrl;
          this.editApplicationVisible = false;
        } else if(type == 'closeDrawer') {
          this.showSettingVisible = false;
        }if (type == "addWorkflowVisible") { 
          this.addWorkflowVisible = false;
          // 新增知识库
          if (data.length > 0) {
            this.workflowIdArr = this.appForm.workflowIds = data || [];
          }
        }
        if(['fabuDialogVisible', 'fabuSuccessVisible1'].includes(type)) return;
        this.handleAutoSave();
    },
      // 显示关联知识库
      filterKnowledge(item) {
        let findItem = this.konwlwdgeList.find(
          (items) => items.knowledgeId == item
        );
        return findItem?.knowledgeName;
      },
      // 知识库
      knowledgeList() {
        getKnowledgeInfoList({
          pageNo: 1,
          pageSize: 2000,
          order: "",
          sort: "",
          ownerType:'all'
        }).then((res) => {
          if (res.code == "000000") {
            this.konwlwdgeList = res.data?.records;
          } else {
            this.konwlwdgeList = [];
          }
        });
      },
      // 删除知识库
      deleteKnowledgeId(item) {
        let filterArr = this.knowledgeIdArr.filter((items) => items != item);
        this.knowledgeIdArr = this.appForm.knowledgeIds = filterArr || [];
        this.changeApplicationStatus(true);
        this.handleAutoSave();
      },
      addQues() {
        this.appForm.presetQuestionList.push("");
      },
      deleteQues(index) {
        this.appForm.presetQuestionList.splice(index, 1);
        this.changeApplicationStatus(true);
        this.handleAutoSave();
      },
	  deleteApplicationQuickCommandList(index) {
        this.appForm.applicationQuickCommandList.splice(index, 1);
        
      },
      getH5AuthChannelList() {
        getAuthChannels({
          clientType: "H5",
        }).then((res) => {
          if (res.code == "000000") {
            this.h5AuthChannels = res?.data;
          } else {
            this.h5AuthChannels = [];
          }
        });
      },
      getPcAuthChannelList() {
        getAuthChannels({
          clientType: "PC",
        }).then((res) => {
          if (res.code == "000000") {
            this.pcAuthChannels = res?.data;
          } else {
            this.pcAuthChannels = [];
          }
        });
      },
      // 查看渲染效果
      temporarSave(flag, autoSave) {
        if((flag == 2 || flag == 1 || flag == 4) && !['1', '2'].includes(this.appForm.publishStatus)) {
          this.appForm.publishStatus = "4";
        }
        this.submitAddApp(flag, autoSave);
      },
      submitAddApp(flag, autoSave) {
        if(flag == 2) {
          this.loading2 = true;
        }
        if(!autoSave) this.rightLoading = true;
        if (!this.appForm.templateId && this.appForm.publishStatus == "1") {
          this.$message({
            type: "warning",
            message: this.$t('selectPcTemplateBeforePublish'),
          });
          return;
        }
        if (!this.appForm.mobileTemplateId && this.appForm.publishStatus == "1") {
          this.$message({
            type: "warning",
            message: this.$t('selectH5TemplateBeforePublish'),
          });
          return;
        }
        const params = Object.assign(this.appConfigForm, this.appForm);
        
        if (!this.appForm.sourceShowFlag) {
          params.previewDoc = '否';
        }
        if (!this.appForm.networkChannel) {
          params.finalNetworkFlag = '否';
        }
        console.log(this.selectIds,"this.selectIds");
        
        if(this.selectIds.length) {
          this.addApplicationPluginDataHandler(autoSave);
        }
        // 暂存：2与发布：3
        if(flag == 3 || flag == 2 ) {
          params.applicationId = params.applicationId ? params.applicationId : this.firstApplicationId;
          params.id = params.id ? params.id : this.firstId;
        }
        if(flag == 3){
          params.makeType = '1'
        } else {
          params.makeType = ''
        }
        let workflowIds = []
        if(params.workflowIds?.length) {
          workflowIds = params.workflowIds?.map(j => {
            return {
              knowledgeId: j.componentId
            }
          })
        }
		//mcp数组集合
		let mcpServerIds = []
		this.mcpServerIdList.forEach((element) => {
			mcpServerIds.push(element.mcpId)
		})
        addApplication({
          ...params,
		  mcpServerIds:mcpServerIds,
		  applicationQuickCommandList:this.appForm.applicationQuickCommandList,
          type: this.appForm.type ? this.appForm.type : this.type || "",
          virtualHumanFlag: this.appForm.virtualHumanFlag ? '是' : '否',
          voiceDialogueFlag: this.appForm.voiceDialogueFlag ? '是' : '否',
          modelAnswerFlag: this.appForm.modelAnswerFlag ? '是' : '否',
          streamVoice: this.isSound?this.appForm.streamVoice:'否',
          sensitiveFlag: this.appForm.sensitiveFlag ? '是' : '否',
          wenshuFlag: this.appForm.wenshuFlag ? '是' : '否',
          networkFlag: this.appForm.networkFlag ? '是' : '否',
          ipFlag: this.appForm.ipFlag ? '是' : '否',
          ocrFlag: this.appForm.ocrFlag ? '是' : '否',
          videoFlag: this.appForm.videoFlag ? '是' : '否',
          multiDialogueFlag: this.appForm.multiDialogueFlag ? '是' : '否',
          knowledgeFlag: this.appForm.knowledgeFlag ? '是' : '否',
          rewritingFlag: this.appForm.rewritingFlag ? '是' : '否',
          polishFlag: this.appForm.polishFlag ? '是' : '否',
          modelFallbackFlag: this.appForm.modelFallbackFlag ? '是' : '否',
          recommendQuestionsShowFlag: this.appForm.recommendQuestionsShowFlag
            ? "1"
            : "0",
		 associationQuestionsShowFlag: this.appForm.associationQuestionsShowFlag
		     ? "1"
		     : "0",
          sourceShowFlag: this.appForm.sourceShowFlag ? "1" : "0",
          interceptWordHouses: this.appForm.interceptWordHouses ? this.appForm.interceptWordHouses : [],
          processStep: this.appForm.processStep
            ? typeof this.appForm.processStep === 'string' ?  this.appForm.processStep : this.appForm.processStep.join(",")
            : "",
          presetQuestionList: this.appForm.presetQuestionList.filter((item) => item),
          workflowIds,
          voiceBroadcastConfig:this.selectedIndex>=0&&this.isSound?JSON.stringify(this.voiceList[this.selectedIndex]):null,
          sttId:this.isVoice?this.appForm.sttId:null,
          ttsId:this.selectedIndex>=0&&this.isSound?this.voiceList[this.selectedIndex].voiceId:null,
          pitch:this.isSound?this.appForm.pitch:0,
          voiceSpeed:this.isSound?this.appForm.voiceSpeed:0,
          subjectLlmName:this.appForm.subjectLlmName
        }).then((res) => {
          if (res.code == "000000") {
            this.defaultId = res.data.templateId;
            // console.log('this.defaultId',this.defaultId)
            if (flag != 1 && flag != 4 && flag != 2 && !autoSave) {
              this.fabuSuccessData = res.data;
              this.fabuSuccessVisible = true;
            }
            // if (flag == 2 && !autoSave) {
            //   this.closeDialog();
            // }
            if (flag == 2) {
              this.loading2 = false;
            }
            // 左侧配置失去焦点 不触发基础模板刷新
            if (flag == 1) {
              if (!autoSave) {
                const iframe = this.$refs.iframe;
                let that = this;
                this.firstApplicationId = res.data?.applicationId;
                this.firstId = res.data?.id;
                if (iframe) {
                  this.$refs.iframe.src = "about:blank"; // 作为一个临时的链接，如果是其它正常可访问URL，会浪费一些不必要流量
                  const _t = setTimeout(() => {
                    // const prefixUrl = res.data?.clientLink?.split('#')?.length ? res.data.clientLink.split('#')[0] : ''
                    if (res.data?.clientLink) {
                      // that.$refs.iframe.src = `${prefixUrl}#/basicTemplate/template`;
                      that.$refs.iframe.src = res.data?.clientLink.replace(
                        "knowledgeDetails",
                        "basicTemplate"
                      );
                    }
                    // that.$refs.iframe.src = `${prefixUrl}#/basicTemplate/3f03c591df1a4fa99e4a1cf420523860`;

                    // that.$refs.iframe.src = 'http://localhost:8082/#/knowledgeDetails/zgc'
                    that.$refs.iframe.src =
                      "http://localhost:8082/#/basicTemplate/cec67de4da4d4c548a8ba16d0825e606/40603";
                    window.addEventListener("message", (event) => {
                      console.log(event.data, "data");
                      if (event.data == "ready")
                        iframe.contentWindow.postMessage(
                          { isIframe: true },
                          "*"
                        );
                    });
                    clearTimeout(_t);
                  }, 300);
                }
              } else {
                const iframe = this.$refs.iframe;
                iframe.contentWindow.postMessage(
                  { isIframe: true, refresh: true },
                  "*"
                );
              }
            }
            if (flag == 4) {
              const iframe = this.$refs.previewIframeRef;
              let that = this;
              this.firstApplicationId = res.data?.applicationId;
              this.firstId = res.data?.id;
              if (iframe) {
                this.$refs.previewIframeRef.src = "about:blank"; // 作为一个临时的链接，如果是其它正常可访问URL，会浪费一些不必要流量
                const _t = setTimeout(() => {
                  if (that.activeAppTemplateType === 1) {
                    sessionStorage.removeItem("isH5");
                    that.$refs.previewIframeRef.src = res.data.clientLink;
                  } else {
                    that.$refs.previewIframeRef.src = `${res.data.clientLink}?isH5=true`;
                  }
                  // that.$refs.previewIframeRef.src = 'http://192.168.1.140:8082/#/knowledgeDetails/wxb?isH5=true';
                  console.log("that.editItem.clientLink", res.data.clientLink);
                  // that.$refs.iframe.src = 'http://localhost:8082/#/knowledgeDetails/zgc'
                  window.addEventListener("message", (event) => {
                    console.log(event.data, "data");
                    if (event.data == "ready")
                      if (that.activeAppTemplateType === 1) {
                        iframe.contentWindow.postMessage(
                          { isIframe: true },
                          "*"
                        );
                      } else {
                        iframe.contentWindow.postMessage(
                          { isIframe: true, isH5: true },
                          "*"
                        );
                      }
                  });
                  clearTimeout(_t);
                }, 300);
              }
            }
            if (["workflow", "dialogue"].includes(this.appForm.type) && this.activeHeadTab == 3) {
              this.queryWorkflowDetail(
                this.appConfigForm.applicationId,
                this.appConfigForm.type
              );
            }
          } else {
            if (!autoSave) {
              this.$message({
                type: "error",
                message: res.msg,
              });
            }
          }
          this.changeApplicationStatus(false);
        })
        .finally(() => {
          if (flag == 2) {
            this.loading2 = false;
          }
          this.changeApplicationStatus(false);
          setTimeout(() => {
            this.rightLoading = false;
          }, 3000);
        });
      },
      getTtsId() {
        ttsList({
          pageNo: 1,
          pageSize: 1000,
          category:"语音识别",
          frequenceUseFlag:1
        }).then((res) => {
          if (res.code == "000000") {
            this.ttsListData = res.data?.records || [];
          } else {
            this.ttsListData = [];
          }
        });
      },
      isEmpty(prop) {
        return !this.appForm || !this.appForm[prop] || this.appForm[prop] === "";
      },
      isEmptyAll(props) {
        for (let key of props) {
          if (
            this.appForm.hasOwnProperty(key) &&
            this.appForm[key] !== null &&
            this.appForm[key] !== "" &&
            this.appForm[key]
          ) {
            return true; // 至少一个属性值不为空
          }
        }
        return false; // 所有属性值为空或空字符串
      },
      confirmTips(type) {
        this.defaultValuesId = type;
        // this.defaultDialogVisible = true;
      },
      defaultDialogClose() {
        this.defaultDialogVisible = false;
      },
      // 自动填充
      filldefaultValues(type) {
        this.defaultDialogVisible = false;
        this.autoFillVisible = false;
        Promise.all([
          defaultApp({}).then(res => {
            if (res.code === "000000") {
              const defaultInfo = res.data;
              if (type === "1") {
                this.appForm.prologue = defaultInfo.prologue;
                this.appForm.tailTalk = defaultInfo.tailTalk;
                this.appForm.failureTalk = defaultInfo.failureTalk;
                this.appForm.answerTimeout = defaultInfo.answerTimeout;
                this.appForm.disclaimer = defaultInfo.disclaimer;
                
                // this.appForm.presetQuestionList = 
                //   defaultInfo.presetQuestionList && defaultInfo.presetQuestionList.length > 0
                //     ? defaultInfo.presetQuestionList
                //     : [""];
                // console.log(this.appForm.presetQuestionList, " this.appForm.presetQuestionList");
              }
            }
            return res; // 确保返回结果，以便后续可能的处理
          }),
          getQuestionByAI({ applicationId: this.appForm.applicationId }).then(res => {
            if (res.code === "000000") {
              if (type === "1") {
                this.appForm.presetQuestionList = res.data;
              }
            }
            return res; // 确保返回结果
          })
        ]).then(() => {
          // 两个请求都完成后执行
          this.changeApplicationStatus(true);
          this.handleAutoSave();
        }).catch(error => {
          // 处理任何一个请求中可能出现的错误
          console.error('并行请求中出现错误:', error);
        });
      },
      // 关掉功能 清除值
      clearDefault() {
        if (!this.funcOrTool.includes("experience")) {
          this.appForm.prologue = "";
          this.appForm.tailTalk = "";
          this.appForm.failureTalk = "";
          this.appForm.answerTimeout = null;
          this.appForm.disclaimer = "";
          this.appForm.presetQuestionList = [""];
        }
        if (!this.funcOrTool.includes("recommendation")) {
          this.appForm.recommendation = "";
        }
  
        if (!this.funcOrTool.includes("virtual")) {
          this.appForm.virtualHumanId = "";
          this.appForm.virtualHumanFlag = false;
        }
  
        if (!this.funcOrTool.includes("voice")) {
          this.appForm.ttsId = null;
          this.appForm.sttId = null;
        }
  
        if (!this.funcOrTool.includes("answerSource")) {
          this.appForm.docLinkType = "";
        }
  
        if (!this.funcOrTool.includes("DisableIP")) {
          this.appForm.ipFlag = false;
        } else {
          this.appForm.ipFlag = true;
        }
  
        // if (!this.funcOrTool.includes("Authentication")) {
        //   this.appForm.clientAuthChannel = "";
        //   this.appForm.pcAuthChannel = "";
        //   this.appForm.toHumanFlag = null;
        // }
        if (!this.funcOrTool.includes("interception")) {
          this.appForm.sensitiveFlag = false;
        } else {
          this.appForm.sensitiveFlag = true;
        }
        if (!this.funcOrTool.includes("wenshuFlag")) {
          this.appForm.wenshuFlag = false;
        } else {
          this.appForm.wenshuFlag = true;
        }
  
        if (!this.funcOrTool.includes("SearchEngine")) {
          this.appForm.networkChannel = "quark";
          this.appForm.finalNetworkFlag = this.$t('no');
          this.appForm.networkFlag = false;
        } else {
          this.appForm.networkFlag = true;
        }
      },
      sceneSettingConfig() {
        this.sceneSettingVisible = false;
        this.handleAutoSave();
      },
	  //获取mcp选中数据
	  addMcpDataEmit(data,yes){
		  console.log('data',data)
		  this.mcpServerIdList = data
      if (yes == this.$t("yes")) {
        this.activeName = "mcp";
      }
	  },
      addPluginDataEmit(selectIds,filed) {
        this.selectIds = selectIds;
        this.pluginList = selectIds;
        this.toolVisible = false;
        this.funcOrTool = [];
        selectIds.forEach((element) => {
          if (element.status === this.$t('yes')) {
            this.funcOrTool.push(element.pluginCode);
          }
        });
        this.clearDefault();
        if (filed == "插件") {
          this.activeName = "second";
        }
        // if (!this.funcOrTool.includes("voice")) {
        //   this.appForm.ttsId = null;
        //   this.appForm.sttId = null;
        //   // this.appForm.voiceDialogueFlag = false;
        // }
      },
      // 新增应用插件关联表
      addApplicationPluginDataHandler(autoSave) {
        addApplicationPluginData({
          applicationId: this.appForm.applicationId,
          pluginList: this.selectIds,
        }).then((res) => {
          if (res.code == "000000") {
            if(!autoSave){
            // this.$message.success(this.$t("successed"));
          }
        }
      });
    },
    initNetworkChannel() {
      this.networkChannelList = [];
      getDictItemByType({ param: "互联网搜索引擎" }).then((res) => {
        if (res.code === "000000") {
          let datas = res?.data || [];
          if (datas) {
            // this.networkChannelList
            // 将datas数组中的对象转为另外一个对象
            this.networkChannelList = datas.map((item) => {
              return {
                id: item.itemId,
                label: item.itemLabel,
                value: item?.itemName,
              };
            });
            
            this.appForm.networkChannel = this.appForm.networkChannel
              ? this.appForm.networkChannel
              : 'quark';
          }
        } else {
          this.networkChannelList = [];
        }
      });
    },
    // 打开个人信息收集声明弹框
    openEditorDialog() {
      this.wangeditorVisible = true;
    },
    // 关闭个人信息收集声明弹窗
    closeWangeditorDialog() {
      this.wangeditorVisible = false;
    },
    // 修改个人声明的值
    editStatementValue(html) {
      if (this.appForm.disclaimer !== html) {
        this.appForm.disclaimer = html;
        this.changeApplicationStatus(true);
      }
      this.wangeditorVisible = false;
      this.handleAutoSave();
    },
    openVocabularyDrawer() {
      let type =
        this.activeTabsName == "first" ? "systemPrompt" : "promptTemplate";
      this.$refs.vocabularyDrawer.openDarwer(this.appForm[type]);
    },
    insertVocabularyFn(val) {
      // 判断插入到哪里
      if (this.activeTabsName == "first") {
        this.$set(this.appForm, "systemPrompt", val);
      } else {
        this.$set(this.appForm, "promptTemplate", val);
      }
      this.changeApplicationStatus(true);
      this.handleAutoSave();
    },
    handleTabClick(tab) {
      if (this.activeHeadTab === tab) return;
      if (["workflow", "dialogue"].includes(this.appForm.type)) {
        this.queryWorkflowDetail(
          this.appConfigForm.applicationId,
          this.appConfigForm.type
        );
        const isTrue = this.workflowCheckTabs();
        if (!isTrue) return;
      }

      sessionStorage.removeItem("isH5");
      this.activeHeadTab = tab;
      this.activeAppTemplateType = 1;
      if (this.activeHeadTab === 2) {
        this.$refs?.functionOrToolRef?.closePopover();
        this.toPreview();
      } else {
        this.handleAutoSave();
      }
    },
    handleLeftTabClick(tab, index) {
      this.activeLeftTab = index;
      this.colseLeftAllDrawer();
      this.$refs[tab.ref].openDrawer();
    },
    colseLeftAllDrawer() {
      this.leftTabs.forEach((element) => {
        if (element.ref) {
          this.$refs[element.ref].closeDrawer();
        }
      });
    },
    closeAllDrawer() {
      this.colseLeftAllDrawer();
      this.activeLeftTab = null;
    },
    updateAgentPattern(val) {
      if (this.appForm.type == val) return;
      this.appForm.type = val;
      this.changeAppStatus = true;
      this.handleAutoSave();
      this.$store.commit("setWorkFlowType", val);
      if (val === "workflow" || val === "dialogue" || val === "multi_agent") {
        this.addWorkflow();
      }
    },
    toPreview() {
      this.temporarSave(4, false);
    },
    changeTemplate() {
      this.$refs.selectTemplateRef?.openDrawer({
        templateId: this.appForm.templateId,
        mobileTemplateId: this.appForm.mobileTemplateId,
      });
    },
    updateTemplate(data) {
      if (data.templateId) {
        if (this.appForm.templateId != data.templateId) {
          this.changeApplicationStatus(true);
        }
        this.appForm.templateId = data.templateId;
      }
      if (data.mobileTemplateId) {
        if (this.appForm.mobileTemplateId != data.mobileTemplateId) {
          this.changeApplicationStatus(true);
        }
        this.appForm.mobileTemplateId = data.mobileTemplateId;
      }
      this.handleAutoSave();
    },
    // 显示关联知识库
    filterWorkflow(item) {
      let findItem = this.workflowList.find(
        (items) => items.componentId == item
      );
      return findItem?.componentName;
    },
    // 知识库
    getWorkflowList() {
      appList({
        pageNo: 1,
        type: 2,
        pageSize: 2000,
        order: "create_time",
        sort: "desc",
      }).then((res) => {
        if (res.code == "000000") {
          this.workflowList = res.data?.records;
        } else {
          this.workflowList = [];
        }
      });
    },
    // 删除工作流
    deleteWorkflowId(item) {
      let filterArr = this.workflowIdArr.filter((items) => items != item);
      this.workflowIdArr = this.appForm.workflowIds = filterArr || [];
      this.changeApplicationStatus(true);
      this.handleAutoSave();
    },
    getInterceptWordHousesList() {
      getInterceptWordHouseListAll({
        pageNo: 1,
        pageSize: 2000,
        applicationId: this.appForm.applicationId,
        order: "create_time",
        sort: "desc",
        template:false
      }).then((res) => {
        if (res.code == "000000") {
          this.interceptWordHousesList = res.data?.records;
        } else {
          this.interceptWordHousesList = [];
        }
      });
    },
    filterInterceptWordHouses(item) {
      let findItem = this.interceptWordHousesList.find(
        (items) => items.id == item
      );
      return findItem?.name;
    },
    filterInterceptWordHousesRemark(item) {
      let findItem = this.interceptWordHousesList.find(
        (items) => items.id == item
      );
      return findItem?.remark;
    },
    deleteInterceptWordHousesId(item) {
      let filterArr = this.interceptWordHouses.filter((items) => items != item);
      this.interceptWordHouses = this.appForm.interceptWordHouses =
        filterArr || [];
      this.changeApplicationStatus(true);
      this.handleAutoSave();
    },
    deleteMcp: debounce(function (index) {
      const mcpId=this.mcpServerIdList[index].mcpId
      this.mcpServerIdList.splice(index, 1);
      const cur=this.appForm.mcpServerIds.findIndex(item=>item==mcpId)
      this.appForm.mcpServerIds.splice(cur,1)
      // let mcpIdList = [];
      // this.mcpServerIdList.forEach((item) => {
      //   mcpIdList.push(item.mcpId);
      // });
      // this.$EventBus.$emit("deleteMcps", mcpIdList);
    }, 400),
    deleteTool(val) {
      if (val == "interception") {
        this.appForm.interceptWordHouses = [];
        this.interceptWordHouses = [];
        this.appForm.sensitiveFlag = false;
      }
      if (val == "SearchEngine") {
        this.appForm.networkChannel = "";
        this.appForm.networkFlag = false;
      }
      const index = this.funcOrTool.findIndex((item) => item == val);
      this.funcOrTool.splice(index, 1);
      console.log("点了删除", this.funcOrTool);
      this.pluginList.forEach((ele) => {
        if (this.funcOrTool.includes(ele.pluginCode)) {
          ele.status = "是";
        } else {
          ele.status = "否";
        }
      });
      console.log("pluginList", this.pluginList);
      this.selectIds = this.pluginList;
      this.changeApplicationStatus(true);
      this.handleAutoSave();
      this.$EventBus.$emit("deleteOrTool", this.funcOrTool);
    },
    handleAutoSave: debounce(function () {
      if (!this.changeAppStatus) return;
      if (this.activeHeadTab == 1) {
        // this.handleToPreview(1, true)
      } else {
        this.temporarSave(4, true);
      }
    }, 100),
    changeApplicationStatus(val) {
      this.changeAppStatus = val;
    },
    handleIpnutChange() {
      this.changeAppStatus = true;
      this.handleAutoSave();
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
            that.changeApplicationStatus(true);
            that.handleAutoSave();
            ctrlAbout.abort();
            that.roleKeyWord=""
          },
        }
      );
    },
    konwlwdgeClick(id) {
      let findItem = this.konwlwdgeList.find(
        (items) => items.knowledgeId == id
      );
      console.log("findItem", findItem);
      if (!findItem) return false;
      this.$store.commit("setIsAddedToApp", true);
      this.$store.commit("setKonwledge", findItem);
    },
    addKnowledgeToApp(data) {
      // this.knowledgeIdArr = this.appForm.knowledgeIds
      if (!this.appForm.knowledgeIds.includes(data.knowledgeId)) {
        // this.appForm.knowledgeIds.push(data.knowledgeId);
        this.appForm.knowledgeIds.push(data);
        this.$forceUpdate();
        // this.knowledgeIdArr.push(data.knowledgeId);
      }
    },
    deleteKnowledgeToApp(data) {
      const index = this.appForm.knowledgeIds && this.appForm.knowledgeIds.findIndex(
        (items) => items == data.knowledgeId
      );
      this.appForm.knowledgeIds.splice(index, 1);
      this.changeApplicationStatus(true);
      // this.handleAutoSave();
    },
    handleToPreview(flag, autoSave) {
      console.log("123");
      this.$nextTick(() => {
        this.$refs.previewRef.temporarSave(flag, autoSave);
      });
    },
    updateLoading(val) {
      this.rightLoading = val;
      this.changeApplicationStatus(false);
    },
    handleAppTemplateTypeClick(val) {
      if (this.activeAppTemplateType == val) return;
      this.activeAppTemplateType = val;
      this.toPreview();
    },
    // 工作流模式切换到发布时的校验
    workflowCheckTabs() {
      let dragDemo = "dragDemo" + this.appForm.type;
      let { nodes, edges, msg } = this.$refs[dragDemo]?.getNodesAndEdges(
        2,
        this.component
      );
      if(!nodes) return false
      if (msg) {
        this.$message({
          type: "warning",
          message: msg,
        });
        return false;
      } else {
        sessionStorage.setItem("workflowNodes", JSON.stringify(nodes));
        sessionStorage.setItem("workflowedges", JSON.stringify(edges));
        sessionStorage.setItem(
          "workflowcomponent",
          JSON.stringify(this.component)
        );
        return true;
      }
    },
    // 工作流发布
    workflowSubmit(data, flag) {
      let dragDemo = "dragDemo" + this.appForm.type;
      let { nodes, edges, msg } = this.$refs[dragDemo]?.getNodesAndEdges(
        2,
        this.component
      );
      if(!nodes) return false
      if (msg) {
        this.$message({
          type: "warning",
          message: msg,
        });
        return;
      }
      const pluginList = this.$store.state?.workflow?.funcPluginList || [];
      if (pluginList.length) {
        this.addWorkflowApplicationPlugin();
      }
      if (flag) {
        data.makeType = "1";
      } else {
        data.makeType = null;
      }
      const param = this.$store.state.workflow.funcData;
      let applicationInfo = {
        ...param,
        ...this.$store.state.workflow.showDrawerData,
        applicationId: this.removeSuffix(param.applicationId),
        type: this.appForm.type,
        virtualHumanFlag: param.virtualHumanFlag ? "是" : "否",
        voiceDialogueFlag: param.voiceDialogueFlag ? "是" : "否",
        modelAnswerFlag: param.modelAnswerFlag ? "是" : "否",
        sensitiveFlag: param.sensitiveFlag ? "是" : "否",
        networkFlag: param.networkFlag ? "是" : "否",
        ipFlag: param.ipFlag ? "是" : "否",
        ocrFlag: param.ocrFlag ? "是" : "否",
        videoFlag: param.videoFlag ? "是" : "否",
        multiDialogueFlag: param.multiDialogueFlag ? "是" : "否",
        knowledgeFlag: param.knowledgeFlag ? "是" : "否",
        rewritingFlag: param.rewritingFlag ? "是" : "否",
        polishFlag: param.polishFlag ? "是" : "否",
        recommendQuestionsShowFlag: param.recommendQuestionsShowFlag
          ? "1"
          : "0",
		  associationQuestionsShowFlag: param.associationQuestionsShowFlag
		      ? "1"
		      : "0",
        sourceShowFlag: param.sourceShowFlag ? "1" : "0",
        processStep: param.processStep ? param.processStep.join(",") : "",
        deleteFlag: "0",
        // publishStatus: flag == 1 ? "4" : "1", // 发布传1 暂存传4
        publishStatus: 1,
        ...data,
      };
      // flag === 1 && (this.saveLoading = true);
      // flag === 2 && (this.publishLoading = true);
      let canvas = JSON.stringify(this.$refs[dragDemo].graph.toJSON());
      saveWorkflow({
        ...this.component,
        applicationInfo,
        canvas: canvas,
        nodes,
        nodeRel: edges,
        status: 1,
        updateTime: this.saveTime,
      })
        .then((res) => {
          if (res.code == "000000") {
            this.fabuSuccessVisible = true;
            // this.showUrlBtn = false;
            this.fabuSuccessData={...applicationInfo}
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        })
        .catch((err) => {
          // this.publishLoading = false
          // this.saveLoading = false
          this.$message({
            type: "error",
            message: err,
          });
        })
        .finally(() => {
          this.$refs.PublishComponent.saveLoading = false;
        });
    },
    //查询mcp
    getListAndDetails() {
      this.loadingDe = true;
      getListAndDetail({
        mcpIdList: this.mcpIdList,
      }).then((data) => {
        this.loadingDe = false;
        if (data.code == "000000") {
          this.mcpServerIdList = data.data;
          this.mcpHoverList=new Array(this.mcpServerIdList.length).fill(false);
          this.mcpHeightList=new Array(this.mcpServerIdList.length).fill(0);
        }
      });
    },
    openComponents(data) {
      this.fabuSuccessVisible = true;
      this.showUrlBtn = true;
      this.fabuSuccessData = data;
    },
    // 更新数组-工作流
    updateWorkflowIds(list) {
      this.appForm.workflowIds = list;
    },
    // 更新数组-知识库
    updateKnowledgeIds(list) {
      this.appForm.knowledgeIds = list;
    },
    //调试
    handleDebug() {
      this.debugType = true;
      this.visibleChange = false;
    },
    handleClose() {
      this.debugType = false;
    },
    handleVoiceChange() {
      console.log(this.isVoice, "this.isVoice");
    },
    //打开语音弹窗
    handelVoice() {
      this.visibleVoice = !this.visibleVoice;
      pageQueryVoice({
        pageNo: 1,
        pageSize: 8,
        frequenceUseFlag: 1,
        tag: "",
        category: "语音输出",
      }).then((res) => {
        if (res.code == "000000") {
          this.voiceList = res.data.records || [];
        }
      })
    },
    // 点击外部区域
    handleClickOutside(event){
      if(!this.$refs.popoverRef || !this.$refs.popoverBtnRef) return;
      let popoverElement=this.$refs.popoverRef.$el
      let popoverBtn=this.$refs.popoverBtnRef
      if(!popoverElement.contains(event.srcElement)&&!popoverBtn.contains(event.srcElement)){
        this.visibleVoice=false
      }

    },
    //设置语音
    handelVoiceItem(item, index) {
      this.selectedIndex = index
      this.voiceTitle = item.fullStyle
      this.voiceTitleId=item.id
    },
    handleSliderChange(value) {
      this.appForm.voiceSpeed = value;
    },
    handleInputChange(value) {
      this.appForm.voiceSpeed = value;
    },
    handlePitchChange(value) {
      this.appForm.pitch = value;
    },
    handlePitchiInputChange(value) {
      this.appForm.pitch = value;
    },
    showLabel(){ 
      this.$nextTick(()=>{
        if(!this.$refs.tabContainer) return;
        if(this.$refs.tabContainer.clientWidth<992){
          this.isHidLabel=true
          return
        }
        this.isHidLabel=false
      })
      
    },
    seenterOptimize(){
      this.roleFlag=true
    },
    changeRegular(key){
      if(this.configExtend[key]=="是"){
        this.configExtend[key]="否"
        return
      }
      this.configExtend[key]="是"
    },
    changeQuesImagine(key){
      if(this.quesImagineForm[key]=="是"){
        this.quesImagineForm[key]="否"
        return
      }
      this.quesImagineForm[key]="是"
    },
    changeAssociation(key){
      if(this.functionForm.association[key]=="是"){
        this.functionForm.association[key]="否"
        return
      }
      this.functionForm.association[key]="是"
    },
    clearVagueQuestion(){
      // this.configExtend={
      //     vagueGuideModelId:"",
      //     doorsill_llm_usr_prompt:"",
      //     vague_guide_qa_enable:"否",
      //     vague_prompt_from_knowledge_enable:"否",
      //     vague_from_llm_enable:"否",
      //     vague_not_answer_flag:"否"
      // }
      this.setVagueQuestionVisible=false
    },
    confirmVagueQuestion(code){
      // this.listAllData.forEach((element) => {
      //   if (this.funcOrTool.includes(element.pluginCode)) {
      //     element.status = "是";
      //   } else {
      //     element.status = "否";
      //   }
      // });
      console.log(this.listAllData);
      this.selectIds=this.listAllData
      // this.selectIds=this.pluginList
      if(code=="vagueQuestion"){
        this.selectIds.map(item=>{
          if(item.pluginCode=="vagueQuestion"){
            item.configExtend=this.configExtend
          }
        })
        this.setVagueQuestionVisible=false
      }else if(code=="recommendation"){
        this.selectIds.map(item=>{
          if(item.pluginCode=="recommendation"){
            item.configExtend=this.quesImagineForm
          }
        })
        this.setQuestionImagineVisible=false
      }else if(code=="SearchEngine"){
        this.selectIds.map(item=>{
          if(item.pluginCode=="SearchEngine"){
            item.configExtend=this.functionForm.SearchEngine
          }
        })
        this.setSearchEngineVisible=false
      }else if(code == "association"){
        this.selectIds.map(item=>{
          if(item.pluginCode=="association"){
            item.configExtend=this.functionForm.association
          }
        })
        this.setAssociationVisible=false
      }
      // this.clearVagueQuestion()
    },
    modelChanges(val){
      this.configExtend.vagueGuideModelId=val.modelId
    },
    modelSceneChanges(val){
      this.appForm.subjectLlmName=val.modelId
    },
    // 查询列表
    getfuntionList() {
      applicationPluginList({
        applicationId: this.appForm.applicationId,
      }).then((res) => {
        if (res.code == "000000") {
          this.listAllData = res.data.filter(
            (item) =>
              ![
                "wenshuFlag",
                "virtual",
                "DisableIP",
                "Authentication",
              ].includes(item.pluginCode)
          );
          this.defaulFunctiontList()
          // this.listAllData.forEach((element) => {
          //   if (this.funcOrToolArr.includes(element.pluginCode)) {
          //     element.status = "是";
          //   } else {
          //     element.status = "否";
          //   }
          // });
          
        }
      });
    },
    setDefaultValue(key, value) {
      this.appForm[key] = value;
    },
    setDefaultFunValue(name,key,value){
      this.functionForm[name][key]=value
    },
    // 全部
    getSceneApplicationRefList(start) {
      apiGetSceneManagementList({
        pageNo: 1,
        pageSize: 9999,
        sceneName: "",
      }).then((res) => {
        if (res.code == "000000") {
          // 已绑定
          const list = this.sceneAllList.map((i) => i.sceneId);
          console.log("list", list);
          if(start) {
            this.sceneSelectedIdOld = JSON.parse(JSON.stringify(list));
            this.sceneListSelectedId = JSON.parse(JSON.stringify(list));
          }else {
            this.sceneListSelectedId = JSON.parse(JSON.stringify(list));
          }
          this.sceneList = res.data?.records || [];
          this.sceneList.forEach((item) => {
            if (list.includes(item.sceneId)) {
              item.checked = true;
            } else {
              item.checked = false;
            }
          });
          this.sceneListSelected = this.sceneList.filter(
            (item) => item.checked
          );
          this.total = res.data.totalRow;
          
        } else {
          this.sceneList = [];
        }
      });
    },
    // 已绑定
    getSceneApplicationRefListAll(start) {
      apiGetSceneApplicationRefList({
        pageNo: 1,
        pageSize: 9999,
        applicationId: this.appConfigForm.applicationId,
      }).then(async (res) => {
        if (res.code == "000000") {
          this.sceneAllList = res.data || [];
          await this.getSceneApplicationRefList(start);
        } else {
          this.sceneAllList = [];
        }
      });
    },
    // 移除场景
    async deleteScene(data) {
      const params = {
        sceneId: data.sceneId,
        applicationId: this.appForm.applicationId,
      };
      let res = await apiDeleteSceneApplicationRef(params);
      if (res.code == "000000") {
        await this.getSceneApplicationRefListAll();
      }
    },
    expandTool(index,item){
      this.$set(this.mcpHoverList,item.id,!this.mcpHoverList[String(item.id)])
      this.$set(this.mcpHeightList,item.id,this.$refs.mcptoolRef[index].clientHeight)
    }
    },
    // watch: {
    //   appForm: {
    //     handler(val) {
    //       this.handleAutoSave();
    //     },
    //     deep: true,
    //   },
    // }
  };
  </script>
  
<style lang="scss" scoped>
.qieHuanKuang {
  position: absolute;
  top: 21px;
  left: 88px;
  z-index: 9;
}
.largeModel {
  width: 240px;
  border-radius: 2px;
  border: 1px solid #d5d8de;
  padding: 7px 12px;
}
.flex {
  display: flex;
}
.flex-center {
  display: flex;
  align-items: center;
}
.flex-title {
  font-weight: 600;
  font-size: 20px;
  color: #494e57;
  line-height: 28px;
}
.config-group-button {
  :deep(.el-button) {
    height: 24px;
    padding: 0 8px;
    line-height: 24px;
    border: 0px;
    font-size: 14px;
    color: #494e57;
    display: inline-flex;
    align-items: center;
    border-radius: 2px;
    > span {
      display: inline-flex;
      align-items: center;
      line-height: 24px;
      img {
        width: 16px;
        height: 16px;
        margin-right: 4px;
        vertical-align: middle;
      }
    }
    &.experience-btn {
      background: rgba(188, 193, 204, 0.2);
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #1747e5;
    }
  }
}
.config-group-tip {
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 14px;
  color: #828894;
  line-height: 20px;
  margin-top: 12px;
}
.aligns {
  align-items: center;
}
.just {
  justify-content: space-between;
}

.abilityInter {
  padding: 0 16px;
}

.demo-ruleForm {
  ::v-deep .el-form-item__label {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #494e57;
    line-height: 20px;
    margin-bottom: 4px;
  }
  .assModel {
    ::v-deep .el-input__inner {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 16px;
      color: #383d47;
    }
  }

  .flexOuter {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    width: 100%;
    ::v-deep .el-slider__runway {
      width: 140px;
    }
    > span:first-child {
      display: inline-block;
      width: 120px;
      position: relative;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #828894;
    }
  }

  ::v-deep .el-switch .el-switch__label.is-active {
    color: #3666ea;
  }
  ::v-deep .el-switch.is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%) !important;
    border-radius: 12px;
    border-color: transparent;
  }
  ::v-deep .el-slider__runway {
    height: 4px;
  }
  ::v-deep .el-slider__bar {
    height: 4px;
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
    border-radius: 4px;
  }
  ::v-deep .el-slider__button {
    width: 18px;
    height: 18px;
    background: #ffffff;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.12);
    border: 1px solid #f2f5fa;
    margin-top: -4px;
  }
  ::v-deep .el-slider__input {
    width: 110px;
  }
  ::v-deep .el-slider__runway.show-input {
    margin-right: 120px;
  }
}

.modelDebugger {
  height: 56px;
  background: #f2f5fa;

  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 16px;
  color: #1c50fd;
  padding: 15px 20px;
}

.modelName {
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 16px;
  color: #494c4f;
  line-height: 20px;
}

.outerDialog {
  display: flex;
  height: calc(100vh - 72px);
  overflow: hidden;
  .main-content {
    height: 100%;
    width: 100%;
    display: flex;
  }
  .dialogPower {
    flex: 1;
    padding: 0px;
    position: relative;
    height: 100%;
    overflow: hidden;
    background: #f7f8fa;
    .left-content {
      height: calc(100% - 64px);
      padding: 24px;
      overflow-y: auto;
      &::-webkit-scrollbar {
        background-color: #f7f8fa; /* 滚动条背景色 */
      }
      &::-webkit-scrollbar-track {
        -webkit-box-shadow: none; /* 内阴影效果 */
        background-color: transparent; /* 轨道背景色 */
      }
      /* 设置滑块样式 */
      &::-webkit-scrollbar-thumb {
        -webkit-box-shadow: none; /* 内阴影效果 */
        background-color: #f7f8fa;
      }

      &:hover {
        &::-webkit-scrollbar {
          background-color: #f5f5f5; /* 滚动条背景色 */
        }
        &::-webkit-scrollbar-track {
          -webkit-box-shadow: inset 0 0 2px rgba(0, 0, 0, 0.2); /* 内阴影效果 */
          background-color: #f5f5f5; /* 轨道背景色 */
        }
        &::-webkit-scrollbar-thumb {
          -webkit-box-shadow: inset 0 0 2px rgba(0, 0, 0, 0.4); /* 内阴影效果 */
          background: #ccc;
        }
      }
    }

    ::v-deep .el-textarea__inner {
      font-family: MiSans, MiSans;
    }
  }
  .functionalPlugins {
    width: 560px;
    background: #fff;
    border-right: 1px solid rgba(0, 0, 0, 0.12);
  }
  .previewDebugging {
    width: 560px;
    background: #fff;
    border-radius: 0px 0px 8px 0px;
    // border: 1px solid #e1e4eb;
    padding: 16px;
    // box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.1);
    position: relative;
    z-index: 100;
    ::v-deep(.el-button) {
      span {
        display: flex;
        align-items: center;
      }
    }
    .heaed {
      height: 56px;
      background-color: #f7f8fa;
      .heaed-top {
        margin-top: 16px;
        width: 528px;
        display: flex;
        justify-content: space-between;
      }
    }
  }
  .addDebug {
    padding: 0;
    border-radius: 0;
    border-left: 1px solid #e5e6ea;
  }
  .headBar {
    background: #ffffff !important;
    padding: 12px 32px 11px !important ;
    display: flex;
    align-items: center;
    margin-bottom: 0px !important;
    width: 100%;
    position: absolute;
    top: 0;
    right: 0;
    border-bottom: 1px solid rgba(0, 0, 0, 0.12);

    .leftSlide {
      flex: 1;
      display: flex;
      justify-content: flex-start !important;
      align-items: center;
      overflow: hidden;
      .closeImg {
        width: 32px;
        height: 32px;
        margin-right: 8px;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
        > img {
          width: 20px;
          height: 20px;
          cursor: pointer;
        }
      }
        .titleIcon {
          display: flex !important;
          width: calc(100% - 60px);
          overflow: hidden;
          .title-headImg {
            width: 40px;
            height: 40px;
            border-radius: 2px;
            display: inline-flex;
            align-items: center;
            margin-right: 12px;
            img {
              width: 100%;
              height: auto;
            }
          }
          .title-info {
            display: flex;
            flex-direction: column;
            flex: 1;
            overflow: hidden;
          }
          p {
            max-width: 400px;
            width: fit-content;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            position: relative;
            padding-right: 20px;
            display: block;
            .edit-icon {
              width: 16px;
              height: 17px;
              cursor: pointer;
              position: absolute;
              right: 0;
              top: 4px;
            }
          }
          p.tit {
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 18px;
            color: #494E57;
            line-height: 24px;
            text-align: left;
            font-style: normal;
            margin-bottom: 4px;
          }
          p.decs {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #828894;
            line-height: 20px;
            text-align: left;
            font-style: normal;
          }
        }
      }
      .centerSlide {
        flex: 1;
        display: flex;
        justify-content: center;
        .head-tabs {
          display: flex;
          padding: 2px;
          background: #F0F1F5;
          border-radius: 4px;
          .tab-item {
            display: inline-flex;
            align-items: center;
            padding: 6px 24px;
            cursor: pointer;
            border-radius: 2px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 16px;
            color: #828894;
            line-height: 24px;
            .icon {
              margin-right: 4px;
            }
            &.active {
              background: #FFFFFF;
              box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
              border-radius: 2px;
              font-weight: 500;
              color: #494E57;
            }
          }
        }
      }
      .rightSlide {
        display: flex;
        justify-content: flex-end !important;
        align-items: center;
        flex: 1;
        .preview {
          line-height: 36px;
          margin-right: 28px;
          cursor: pointer;
          > span {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 16px;
            color: #3666ea;
            line-height: 24px;
            text-align: left;
            font-style: normal;
            text-transform: none;
          }
          > img {
            margin-right: 4px;
          }
          > span,
          > img {
            vertical-align: middle;
          }
        }
        :deep(.btn) {
          height: 40px;
          color: #1747E5;
          border: 1px solid #1747E5;
          border-radius: 2px;
          padding: 0 12px;
          display: inline-flex;
          align-items: center;
          > span {
            display: inline-flex;
            align-items: center;
          }
          img {
            margin-right: 4px;
          }
          img,
          span {
            vertical-align: middle;
          }
        }
        .btns {
          height: 40px;
          background: #1747E5;
          border-radius: 2px;
          padding: 0px 16px;
          img {
            margin-right: 4px;
          }
          img,
          span {
            vertical-align: middle;
          }
        }
      }
    }
    .left-tabs-content {
      height: 64px;
      padding: 0 24px;
      background: #FBFCFD;
      display: flex;
      align-items: center;
      justify-content: space-between;
      .tabs-left {
        display: flex;
        align-items: center;
        .tab-line {
          width: 1px;
          height: 24px;
          background: rgba(0,0,0,0.12);
          margin-left: 16px;
          margin-right: 16px;
        }
      }
      .tabs-right {
        display: flex;
        align-items: center;
      }
      .tabs-right-tool {
        display: flex;
        align-items: center;
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 14px;
        color: #494E57;
        height: 32px;
        border-radius: 2px;
        padding: 0px 10px;
        border: 1px solid #1747E5;
        box-sizing: border-box;
        border-radius: 4px;
        cursor: pointer;
        .btn-icon {
          margin-right: 6px;

        }
        .add-word{
          display: inline-block;
          color: #1747E5;
          min-width: 30px;
          text-align: center;
        }
        // &.active {
        //   background: rgba(188,193,204,0.2);
        // }
      }
      
    }
    .main-content-left {
      flex: 1;
      overflow: hidden;
      .preview-iframe-box {
        width: 100%;
        height: 100%;
        padding: 24px;
        .preview-iframe {
          border-radius: 18px;
          border: 4px solid #464A53;
          width: 100%;
          aspect-ratio: 16 / 9;
          &.preview-iframe-pc {
            width: 100%;
            aspect-ratio: 16 / 9;
          }
          &.preview-iframe-H5 {
            display: block;
            width: 375px;
            aspect-ratio: 9 / 16;
            margin: 0 auto;
          }
        }
      }
      :deep(.change-template-btn) {
        display: inline-flex;
        align-items: center;
        padding: 0 12px;
        > span {
          display: inline-flex;
          align-items: center;
        }
        
      }
    }
    .main-content-right {
      width: 592px;
      background: #FFFFFF;
      height: 100%;
    }
  }
  .marginTop16 {
    margin-top: 16px;
  }
  .marginTop32 {
    margin-top: 32px;
    .widthSpan {
      display: inline-block;
      min-width: 56px;
      color: #768094 !important;
    }
  }
  .click-add-ctn {
  width: 100%;
  height: 80px;
  box-sizing: border-box;
  background: #f2f3f5;
  border-radius: 4px;
  border: 1px solid #e7e7e7;
  margin-top: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 11px;
  cursor: pointer;
    .word {
      line-height: 24px;
      color: #86909c;
      font-size: 16px;
    }
  }

  .formTitle {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 14px;
    color: #494E57;
    line-height: 20px;
    text-align: left;
    font-style: normal;
    text-transform: none;
    margin-bottom: 8px;
    > img {
      width: 16px;
      height: 16px;
    }
    > img,
    span {
      vertical-align: middle;
      font-size: 14px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      // color: #828894 !important;
      line-height: 20px !important;
      display: inline-block;
    }
    .previewAndDebug {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 18px;
      color: #1D2129;
    }
   
    .title {
      vertical-align: middle;
      font-size: 14px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px !important;
      color: #828894 !important;
      line-height: 20px !important;
      display: inline-block !important;
      margin-bottom: 8px !important;
    }
    
  }

  .timeout{
    ::v-deep .el-input__inner{
      padding-left: 50px;
      padding-right: 15px;
      text-align: left;
    }

    ::v-deep .el-input-number__decrease{
      left: 1px;
      border: none;
      background: #F2F3F5;
      border-radius: 0 !important;
      border-top-left-radius: 4px;
    }
    ::v-deep .el-input-number__increase{
      left: 1px;
      border: none;
      background: #F2F3F5;
      border-radius: 0 !important;
      border-bottom-left-radius: 4px;
    }

    ::v-deep .my-el-input-number[data-unit][data-v-a70d2268]::after{
      right: 12px;
    }
  }
  .debugging {
    padding: 6px 14px;
    background: rgba(188,193,204,0.2);
    border-radius: 4px;
  }
  .preview-tabs {
    display: flex;
    .tabs {
      display: flex;
      align-items: center;
      .tab-item {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 18px;
        color: #828894;
        line-height: 28px;
        margin-right: 16px;
        cursor: pointer;
        &:last-child {
          margin-right: 0px;
        }
        &.active {
          font-weight: 500;
          color: #1d2129;
        }
      }
    }
  }
  .tool-set-btn {
    padding: 0 8px;
    border: 0px;
    display: inline-flex;
    align-items: center;
    :deep(span) {
      display: inline-flex;
      align-items: center;
    }
  }
  .preview-title {
    span {
      font-weight: 500;
      font-size: 20px;
      color: #494E57;
      line-height: 28px;
    }
  }
  
  .base-li {
    height: 40px;
    background: #ffffff;
    border-radius: 2px;
    border: 1px solid #D5D8DE;
    padding: 0 8px;
    margin-bottom: 8px;
    cursor: pointer;
  
    .li-name {
      color: #494E57;
      font-size: 14px;
      line-height: 20px;
      text-align: left;
      font-style: normal;
  
      > img {
        width: 24px;
        height: 24px;
        border-radius: 2px;
        margin-right: 8px;
      }
    }
    .delete-icon {
      display: none;
    }
    &:hover {
      background: #F2F4F7;
      .delete-icon {
        display: inline-block;
      }
    }
  }
  .base-li-anquan {
    height: 56px;
    .name {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #36383D;
      line-height: 20px;
    }
    .remark {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #828894;
      line-height: 16px;
    }
  }
  
  .bottom-btn {
    position: absolute;
    left: 32px;
    bottom: 20px;
    width: calc(100% - 64px);
    height: 40px;
    border-radius: 4px;
    border: 1px solid #3666ea;
    text-align: center;
  }
  
  .preview {
    margin-top: 26px;
    height: 144px;
    background: #ffffff;
    border-radius: 16px;
    padding: 20px;
  
    > img {
      width: 104px;
      height: 104px;
      margin-right: 24px;
    }
  
    .title1 {
      height: 32px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      line-height: 32px;
      color: #383d47;
      text-align: left;
      font-style: normal;
    }
    .title2 {
      margin-top: 8px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #768094;
    }
  }
  
  .question-list {
    display: inline-block;
    height: 48px;
    background: #ffffff;
    border-radius: 2px 12px 12px 12px;
    border: 1px solid #e1e4eb;
    color: #383d47;
    font-size: 16px;
    padding: 0px 16px;
    line-height: 48px;
    margin-top: 8px;
  }
  
  .input-box {
    position: absolute;
    left: 32px;
    bottom: 40px;
    width: calc(100% - 64px);
    .prefix {
      width: 40px;
      height: 40px;
      background: #f2f5fa;
      margin-top: 8px;
      border-radius: 4px;
  
      img {
        width: 15px;
        height: 15px;
        margin-top: 12.5px;
      }
    }
  
    ::v-deep .el-input__inner {
      height: 56px;
      background: #ffffff;
      box-shadow: 0px 6px 20px 0px rgba(34, 107, 144, 0.1);
      border-radius: 12px;
      border: 1px solid #d1e0fe;
    }
    ::v-deep .el-input__icon {
      width: 40px;
      font-size: 24px;
    }
    ::v-deep .el-input--prefix .el-input__inner {
      padding-left: 60px;
    }
    ::v-deep .el-input--suffix .el-input__inner {
      padding-right: 60px;
    }
  }
  
  .dialog {
    margin-top: 20px;
    .title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 16px;
      color: #383d47;
      line-height: 28px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
    .func-iconImg {
      width: 20px;
      height: 20px;
      border-radius: 4px;
      margin-right: 8px;
    }

    .voiceset-set{
      display: flex;
      align-items: center;
      gap: 4px;

      span{
        display: inline-block;
        height: 20px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #1D2129;
        line-height: 20px;
      }
      ::v-deep .el-switch__core{
        width: 24px !important;
        height: 16px;

        &:after{
          width: 10px;
          height: 10px;
          top: 1.8px;
        }
      }
      ::v-deep .el-switch.is-checked .el-switch__core::after {
          left: 100%;
          margin-left: -11px;
      }
    }
  }

  .experience{
    ::v-deep .el-input__inner{
      background-color: #F7F8FA ;
      border: none !important;
    }

    .init-ques{
      // ::v-deep .el-input__inner{
      //   border: 1px solid #C9CDD4 !important;
      // }
      //  &:hover{
      //   ::v-deep .el-input__inner{
      //     border: 1px solid #C9CDD4 !important;
      //   }
      // }
      .question-input{
        ::v-deep .el-input__inner{
        &:hover{
          border: 1px solid #C9CDD4 !important;
        }
        &:focus{
          border: 1px solid #C9CDD4 !important;
        }
        }
      }
    }

    ::v-deep .el-input-group__prepend{
      background-color: #F7F8FA !important;

      .el-input__inner{
        border: none;

        
      }
    }
    ::v-deep .el-input__count-inner{
      background-color: #F7F8FA ;
    }
  }
  .marginTop10 {
    margin-top: 10px;
  }
  .boxName {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #383d47;
    line-height: 28px;
    text-align: left;
    font-style: normal;
  }
  .disabled {
    padding: 4px 10px;
    color: #828894;
    background: #e1e4eb;
    border-radius: 20px;
  }
  .my-el-input-number[data-unit] {
    --el-input-number-unit-offset-x: 45px;
    position: relative;
  }
  .my-el-input-number[data-unit]::after {
    content: attr(data-unit);
    height: 100%;
    display: flex;
    align-items: center;
    position: absolute;
    top: 0;
    right: var(--el-input-number-unit-offset-x);
    color: #C9CDD4;
  }
  .my-el-input-number[data-unit] .el-input__inner {
    padding-left: 30px;
    padding-right: calc(var(--el-input-number-unit-offset-x) + 12px);
  }
  .defaultDialo {
    ::v-deep .el-dialog {
      border-radius: 4px;
      .el-dialog__body {
        padding: 0px 32px 16px;
      }
      .el-dialog__header {
        background-color: #ffffff;
        padding: 32px 32px 16px;
        font-weight: 500;
        font-size: 20px;
        color: #494E57;
        line-height: 24px;
        .el-dialog__headerbtn {
          top: 36px;
          right: 32px;
        }
      }
      .el-dialog__footer {
        text-align: left;
        padding-left: 32px;
        padding-right: 32px;
      }
    }
    ::v-deep .el-textarea__inner {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #494E57;
      line-height: 22px;
    }
    .tipsText {
      font-weight: 400;
      font-size: 16px;
      line-height: 20px;
      color: #828894;
    }
    &.sceneSettingDialog {
      ::v-deep .el-dialog {
        .el-dialog__footer {
          text-align: right;
        }
      }
    }
  }
  .dialog-title {
    display: flex;
    align-items: center;
    font-weight: 500;
    font-size: 20px;
    color: #383d47;
    img {
      width: 20px;
      height: 20px;
      margin-right: 10px;
    }
  }
  .edit-icon {
    width: 16px;
    height: 17px;
    cursor: pointer;
  }
  .abilityTabs {
    background: #FFFFFF;
    border-radius: 4px;
    // border: 1px solid #D5D8DE;
    padding: 12px;
    .tabs-box {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 10px;
    }
    .tabs {
      display: inline-block;
      height: 28px;
      line-height: 28px;
      font-size: 16px;
      color: #828894;
      margin-right: 16px;
      cursor: pointer;
      font-family: MiSans, MiSans;
      &.active {
        font-weight: 500;
        font-size: 18px;
        color: #1d2129;
        position: relative;
        // &::after {
        //   content: "";
        //   display: block;
        //   width: 20px;
        //   height: 3px;
        //   background: #603ECA;
        //   border-radius: 2px;
        //   position: absolute;
        //   left: 50%;
        //   bottom: -4px;
        //   margin-left: -10px;
        // }
      }
    }
    .tabs-right {
      :deep(.btn) {
        display: inline-flex;
        height: 28px;
        padding: 0px 8px;
        line-height: 28px;
        align-items: center;
          
        img {
          width: 16px;
          height: 16px;
          margin-right: 4px;
          vertical-align: middle;
        }
        .icon {
          // vertical-align: middle;
          margin-right: 4px;
        }
        &.btn1 {
          color: #1747E5;
        }
        &.btn2 {
          color: #494E57;
        }
        
      }
    }
    .tabs-content {
      :deep(.el-textarea) {
        .el-textarea__inner {
          font-weight: 400;
          font-size: 16px;
          color: #494C4F;
          line-height: 22px;
        }

        textarea{

          &::placeholder{
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #C9CDD4;
            line-height: 22px;
          }
        }
      }
    }
  }
  :deep(.question-input) {
    position: relative;
    .el-input-group__prepend {
      background-color: #fff;
      padding: 0 8px;
      position: absolute;
      left: 1px;
      top: 1px;
      bottom: 1px;
      height: 38px;
      border-radius: 4px;
      display: inline-flex;
      border: 0px !important;
    }
    .el-input__inner {
      // padding-left: 32px;
    }
  }
  .add-ques {
    // justify-content: center;
    padding: 0 15px;
    height: 40px;
    border-radius: 4px;
    // border: 1px solid #E7E7E7;
    font-size: 14px;
    color: #1747E5;
    padding: 0 12px;
    box-sizing: border-box;
    gap: 8px;
    width: 70%;

    .icon{
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  .edit-ques {
    justify-content: space-between;
    height: 40px;
    border-radius: 4px;
    background: rgba(188,193,204,0.2);
    font-size: 14px;
    color: #494E57;
    padding: 0 12px;
    box-sizing: border-box;

    p{
      max-width: 220px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
  }
  .recommendation-box {
    .recommendation-strategy {
      flex: 1;
      .title {
        vertical-align: middle;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894 !important;
        line-height: 20px;
        margin-bottom: 8px;
        display: inline-block !important;
      }
    }
    .recommendation-question-count {
      width: 120px;
      margin-left: 12px;
    }
    .title {
      vertical-align: middle;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #828894 !important;
      line-height: 20px !important;
      margin-bottom: 8px;
      display: inline-block !important;
    }
  
  }
  .recommendation-box-authentication {
    display: flex;
    justify-content: space-between;
  }
  .voice-bot {
    padding: 0 8px;
    height: 40px;
    border-radius: 4px;
    border: 1px solid #E7E7E7;
    // cursor: pointer;

  .voice-left {
    display: flex;
    align-items: center;

    .voicev-text {
      height: 20px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #1d2129;
      line-height: 20px;
      text-align: left;
      font-style: normal;
      text-transform: none;
      display: inline-block;
      margin-left: 8px;
      margin-right: 8px;
    }

    .intonation {
      color: #86909c;
    }
  }
}
.plugin-item {
  border-radius: 2px;
  border: 1px solid #d5d8de;
  padding: 12px;
  margin-top: 10px;
  background: #ffffff;
  .func-iconImg {
    width: 40px;
    height: 40px;
  }
  .boxName {
    font-weight: 500;
    font-size: 16px;
    color: #494e57;
    line-height: 24px;
    margin-bottom: 4px;
  }
  p {
    font-size: 14px;
    color: #828894;
    line-height: 20px;
  }
}
.tabs-box-mcp {
  // width: 224px;
  height: 28px;
  // background: #F2F4F7;
  // border-radius: 4px;
  display: flex;
  align-items: center;
  padding: 2px;
  .tabs-item {
    // min-width: 82px;
    margin-right: 20px;
    height: 28px;
    line-height: 28px;
    text-align: center;
    border-radius: 2px;
    font-size: 18px;
    color: #828894;
    cursor: pointer;
    &.active {
      font-weight: 600;
      font-size: 18px;
      color: #603eca;
      // background: #FFFFFF;
      // box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
    }
  }
}

.mcp-list-ctn{
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;

  .mcp-list{
    width: 100%;
    padding: 12px 16px;
    background: #FFFFFF;
    border-radius: 8px;
    border: 1px solid #E5E6EA;
    box-sizing: border-box;

    &:hover{
      .remove{
        display: flex !important;
      }
    }

    .mcp-msg-ctn{
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;


      .mcp-msg-left{
        display: flex;
        align-items: center;
        gap: 16px;
        .mcp-msg-img{
          width: 32px;
          height: 32px;
          border-radius: 2px;
        }

        .mcp-msg{
          .mcp-msg-title{
            display: flex;
            height: 20px;
            align-items: center;
            gap: 8px;


            .mcp-title{
              height: 20px;
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 14px;
              color: #1D2129;
              line-height: 20px;
            }

            .mcp-official{
              width: 32px;
              height: 20px;
              background: linear-gradient( 270deg, #8E65FF33 0%, #1747E533 100%);
              border-radius: 4px;
              line-height: 20px;
              text-align: center;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 12px;
              color: #1D2129;
            }
          }

          .mcp-intro{
            height: 16px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #86909C;
            line-height: 16px;
            max-width: 600px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

        }

      }

      .mcp-msg-right{
        display: flex;
        align-items: center;
        gap: 8px;

        .icon-ctn{
          width: 32px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: .3s;
        }
        .remove{
          display: none;
        }
        
      }
      
    }

    .mcp-tool-ctn{
      width: 100%;
      padding-left: 48px;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      gap: 8px;

      .mcp-tool-list{
        width: 100%;
        padding-top: 9px;
        border-top: 1px solid #e7e7e7;
        box-sizing: border-box;
        display: flex;
        gap: 5px;


        .mcp-tool-img{
          width: 16px;
          height: 16px;
          border-radius: 4px;
          background: #F2F3F5;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .mcp-tool-msg{
          display: flex;
          flex-direction: column;
          gap: 4px;

          .mcp-tool-name{
            height: 16px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 12px;
            color: #1D2129;
            line-height: 16px;
            max-width: 600px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .mcp-tool-intro{
            height: 16px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #86909C;
            line-height: 16px;
            max-width: 600px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
      }
    }

    .mcp-tool-h{
      height: 0;
      overflow: hidden;
      transition: .3s;
      will-change: true;
    }
  }

}
</style>
<style lang="scss">
.auto-fill-popover {
  background: #ffffff !important;
  box-shadow: 0px 4px 9px 0px rgba(0, 0, 0, 0.1) !important;
  border: none !important;

  .auto-fill-content {
    padding: 24px;

    .auto-fill-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 18px;
      color: #494e57;
      line-height: 24px;
      margin-bottom: 12px;
      cursor: pointer;

      .title-left {
        display: flex;
        align-items: center;

        .title-icon {
          margin-right: 12px;
        }
      }
    }

    .auto-fill-desc {
      padding-left: 32px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #828894;
      line-height: 18px;
      margin-bottom: 16px;
    }

    .auto-fill-footer {
      padding-left: 32px;

      .el-button {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        border-radius: 2px;
      }

      .el-button--primary {
        background: #1747e5;
      }
    }
  }

  .popover-conten {
    padding: 0 24px;
    height: auto;
    display: flex;

    // justify-content: space-between;
    .popover-list-left {
      padding: 24px 24px 16px 0;
      border-right: 1px solid #e7e7e7;
    }

    .popover-list-tit {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .title {
        height: 24px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 16px;
        color: #494c4f;
        line-height: 24px;
        text-align: left;
        font-style: normal;
      }

      .title-rig {
        display: flex;
      }

      .text-vo {
        height: 24px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #86909c;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        text-transform: none;
      }
    }

    .popover-right {
      padding: 24px 0 0 24px;
      width: 100%;

      .popover-right-tit {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .info-vo {
        display: flex;
        align-items: center;
        margin-bottom: 8px;

        .intonation {
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #1d2129;
          line-height: 20px;
          text-align: left;
          font-style: normal;
          text-transform: none;
          display: inline-block;
        }
      }

      .info-vo-slider {
        display: flex;
        justify-content: space-between;

        // align-items: center;
        .score-input {
          width: 72px;

          // margin-right: 8px;
          .el-input-number__decrease,
          .el-input-number__increase {
            width: 20px;
          }

          .el-input {
            .el-input__inner {
              padding-left: 8px;
              padding-right: 26px;
            }
          }
        }
      }
    }

    .popover-list {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 332px;
      height: 40px;
      border-radius: 4px;
      border: 1px solid #e7e7e7;
      padding: 8px;
      margin-bottom: 8px;
      cursor: pointer;

      .voice-info-left {
        display: flex;
      }

      .voicev-text {
        margin: 0 8px;
      }
    }

    .popover-list.selected {
      border: 1px solid #4157fe;
    }
  }
}
.konwlwdge-tip {
  padding: 6px 10px;
  background: #eaeef1;
  font-size: 12px;
  border-radius: 4px;
}
.applicationDialog {
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__body {
      padding: 16px 32px;
    }
    .el-dialog__header {
      background: #FFFFFF;
      border-radius: 8px 8px 0px 0px;
      padding: 32px 32px 16px 32px;
      font-weight: 500;
      font-size: 20px;
      color: #494E57;
      line-height: 24px;
      .el-dialog__title {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        color: #494E57;
        line-height: 32px;
      }
      .el-dialog__headerbtn {
        right: 32px;
        top: 36px;
      }
    }
    .el-dialog__footer {
      text-align: right;
    }
  }
  .opts-btn {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    background: rgba(0, 0, 0, .4);
    backdrop-filter: blur(1px);
    position: absolute;
    bottom: 0;
    right: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
  }

  .delete {
    // position: absolute;
    // top: 8px;
    // right: 8px;
  }
}
.inputTextarea {
  ::v-deep .el-textarea__inner {
    font-family: MiSans, MiSans;
  }
}
.logoAppUpload {
  text-align: center;
  ::v-deep .el-upload--picture-card {
    border: 0;
    width: 80px;
    height: 80px;
  }
}

.demo-ruleForm {
  ::v-deep .el-form-item__label {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #383d47;
    line-height: 32px;
  }
}
.flex {
  display: flex;
}
.flex-center {
  display: flex;
  align-items: center;
}

.aligns {
  align-items: center;
}
.just {
  justify-content: space-between;
}
.application-type-box {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 16px;
  .application-type {
    width: 202px;
    height: 80px;
    display: flex;
    justify-content: center;
    background: #FFFFFF;
    border-radius: 2px;
    border: 1px solid #D5D8DE;
    align-items: center;
    cursor: pointer;
    .type-item {
      display: inline-flex;
      flex-direction: column;
      text-align: center;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 18px;
      img {
        width: 20px;
        height: 20px;
        margin: 0px auto 8px;
      }
    }
    &.active {
      background: rgba(28,80,253,0.05);
      border: 1px solid #1747E5;
    }
  }
}

.el-popover{
  border: none !important;
  border-radius: 8px;
}

.autoOp-ctn{
  width: 440px;
  height: 122px;
  background: #FFFFFF;
  box-shadow: 0px 2px 12px 0px rgba(26,36,70,0.1);
  border-radius: 8px !important;
  border: 1px solid #E5E6EA;
  padding: 18px 16px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  .app-create{
    width: 146px;
    height: 32px;
    background: rgba(188,193,204,0.2);
    border-radius: 4px;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    .edit{
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .word{
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #1D2129;
      line-height: 20px;
    }
  }

  .roleKey-create{
    width: 100%;
    height: 40px;
    background: #FFFFFF;
    border-radius: 4px;
    border: 1px solid #4D77EF;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    padding: 11px 13px;
    gap: 8px;
    .logo{
      width: 20px;
      height: 20px;
    }

    .input-ctn{
      border: none;
      outline: none;
      flex: 1;
      height: 20px;
      color: #1d2129;
      &::placeholder{
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #C9CDD4;
        line-height: 20px;
      }
    }

    .icon-fasong{
      width: 20px;
      height: 20px;
      cursor: pointer;

      img{
        width: 16px;
        height: 16px;
        transform: rotate(45deg);
      }
    }
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

.settings-dialog-ctn{

  .settings-dialog{
    border-radius: 12px !important;

    .el-dialog__header{
      padding: 32px 32px 8px 32px;
      position: relative;
      .title{
        height: 32px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        color: #1D2129;
        line-height: 32px;
      }

      .el-dialog__headerbtn{
        top: 40px;
        right: 32px;

        .el-icon-close{
          color: #1d2129;
          transform: scale(1.2);
        }
      }
    }

    .el-dialog__body{
      padding: 8px 32px 0 32px;

      // 模糊问题引导
      .vague-ctn{
        display: flex;
        flex-direction: column;
        gap: 20px;

        &-list{
          width: 100%;

          .vague-title-ctn{
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: space-between;

            .vague-title{
              height: 20px;
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 14px;
              color: #1D2129;
              line-height: 20px;
            }
            

            // switch
            .el-switch__core{
              width: 24px !important;
              height: 16px;

              &:after{
                width: 10px;
                height: 10px;
                top: 2px;
              }
            }
            .el-switch.is-checked .el-switch__core::after {
                margin-left: -12px;
            }

            // radio
            .el-radio__label{
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #1D2129 !important;
            }
          }

           // textarea
          .el-textarea{
            .el-textarea__inner{
              background-color: #F7F8FA;
              border-radius: 8px;
              border: none !important;
            }
          }

          .vague-tip{
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #86909C;
            line-height: 20px;
            margin-top: 4px;
          }
        }
      }

      // 答案溯源
      .answersource-ctn{
        margin-top: 8px;
        display: flex;
        flex-direction: column;
        gap: 8px;

        &-list{
          height: 24px;
          display: flex;
          align-items: center;
          gap: 6px;

          .answer-left{
            width: 32px;
            height: 20px;
            .el-switch__core{
              width: 32px !important;
              height: 20px;

              &:after{
                width: 14px;
                height: 14px;
                top: 50%;
                transform: translateY(-50%);
              }
            }
          }

          .answer-right{
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #1D2129;
            line-height: 20px;
          }

        }
      }

      // 检索引擎
      .searchengine-ctn{
        margin-top: 8px;

        .engine-ctn{
          .engine-ctn-title{
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #1D2129;
            line-height: 20px;
            margin-bottom: 8px;
          }

          .el-input__inner{
            background-color: #F7F8FA;
            border-radius: 8px;
            border: none !important;
          }
          .el-select__caret{
            color: #1d2129;
          }
          
        }

        .switch-ctn{
          margin-top: 26px;
          display: flex;
          flex-direction: column;
          gap: 30px;

          .cache-result{
            width: 100%;
            padding-left: 40px;
            box-sizing: border-box;
            display: flex;
            flex-direction: column;
            gap: 16px;

            .cache-result-list{
              height: 32px;
              display: flex;
              align-items: center;
              justify-content: space-between;

              .cache-label-ctn{
                height: 20px;
                display: flex;
                align-items: center;
                gap: 4px;

                .cache-label{
                  height: 20px;
                  font-family: MiSans, MiSans;
                  font-weight: 400;
                  font-size: 14px;
                  color: #86909C;
                  line-height: 20px;
                }

                .icon-ctn{
                  width: 20px;
                  height: 20px;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                }

              }

              .cache-slider{
                height: 32px;
                display: flex;
                align-items: center;
                gap: 9px;

                .el-slider__runway{
                  height: 4px;
                }
                .el-slider__bar{
                  background: linear-gradient( 270deg, #8E65FF 0%, #1747E5 100%) !important;
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

          .el-switch__core{
            width: 32px !important;
          }

          .switch-name{
            display: inline-block;
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #1D2129;
            line-height: 20px;
          }

          .switch-tip{
            width: 20px;
            height: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
          }
        }
      }

      // 问题联想
      .quesimagine-ctn{
        margin-top: 8px;

        .quesimagine-tip{
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #86909C;
          line-height: 20px;
        }
      }
       // 规则
      .regular{
        margin-top: 8px;
        display: flex;
        flex-direction: column;
        gap: 8px;

        .regular-list{
          width: 100%;
          height: 64px;
          background: #FFFFFF;
          border-radius: 8px;
          border: 1px solid #E5E6EA;
          padding: 11px 12px;
          box-sizing: border-box;
          display: flex;
          align-items: center;
          gap: 12px;

          .check-box{
            width: 16px;
            height: 16px;
            cursor: pointer;
            img{
              width: 16px;
              height: 16px;
            }
          }

          .regular-type{
            height: 100%;
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .regular-title{
              height: 20px;
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 14px;
              color: #1D2129;
              line-height: 20px;
            }

            .regular-tip{
              height: 18px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 12px;
              color: #86909C;
              line-height: 18px;
            }
          }
        }
      }

      // 语音设置：选择发音人
      .voiceset-ctn{
        width: 100%;
        margin-top: 8px;
        .voiceset-title-ctn{
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: space-between;

          .voiceset-title{
            height: 24px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 16px;
            color: #494C4F;
            line-height: 24px;
          }

          .voiceset-more{
            height: 24px;
            display: flex;
            align-items: center;
            gap: 5px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #86909C;
            cursor: pointer;
          }

          .voiceset-select{
            height: 20px;
            display: flex;
            align-items: center;
            gap: 4px;

            .el-switch__core{
              width: 24px !important;
              height: 16px;

              &:after{
                width: 10px;
                height: 10px;
                top: 1.5px;
              }
            }
            .el-switch.is-checked .el-switch__core::after {
              left: 100%;
              margin-left: -11px;
            }
            

            p{
              height: 20px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #1D2129;
              line-height: 20px;
            }
          }
        }

        .voiceset-list-ctn{
          width: 100%;
          margin-top: 12px;
          margin-bottom: 24px;
          display: flex;
          flex-direction: column;
          gap: 8px;

          .voiceset-list{
            height: 38px;
            border-radius: 8px;
            border: 1px solid #E7E7E7;
            box-sizing: border-box;
            display: flex;
            align-items: center;
            padding: 8px;
            gap: 8px;
            position: relative;

            .voiceset-logo{
              width: 20px;
              height: 20px;
            }

            .voiceset-name{
              height: 20px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #1D2129;
              line-height: 20px;
            }

            .voiceset-sound{
              width: 20px;
              height: 20px;
              display: flex;
              align-items: center;
              justify-content: center;
            }

            .voiceset-checked{
              width: 20px;
              height: 20px;
              display: flex;
              align-items: center;
              justify-content: center;
              position: absolute;
              top: 50%;
              transform: translateY(-50%);
              right: 8px;
            }
          }
        }

        .voiceset-other-ctn{
          width: 100%;
          height: 32px;
          margin-top: 16px;
          display: flex;
          justify-content: space-between;
          align-items: center;

          p{
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #1D2129;
            line-height: 20px;
          }
          .el-slider__runway{
            border-radius: 4px;
            margin: 0;
            .el-slider__bar{
              background: linear-gradient( 270deg, #8E65FF 0%, #1747E5 100%) !important;
            }
          }

          .el-input__inner{
            background-color: #F7F8FA !important;
            border-radius: 4px;
            padding-left: 28px;
            padding-right: 8px;
            border: none !important;
            overflow: hidden;
          }

          .el-input-number__increase,.el-input-number__decrease{
            width: 19px;
            left: 1px;
            border: none;
          }
          
        }


      }
    }

    .el-dialog__footer{
      padding: 16px 32px 32px 32px;
      .el-button{
        border-radius: 8px;
      }
    }
  }

}

.workflow-tooltip-dark{
    border: none !important;
    box-shadow: 0px 6px 12px 0px rgba(0,0,0,0.1);
    padding: 8px 12px !important;
    line-height: 20px;
    font-size: 14px;
    color: #fff ;
    font-family: MiSans Regular;
    max-width: 336px !important;
    background: rgba(51,51,51,0.8) !important;
    backdrop-filter: blur(4px);
    // background: #1d2129 !important;
    .popper__arrow{
      border-top-color: rgba(0,0,0,0) !important;
      bottom: -5px !important;

      &::after{
        border-top-color:rgba(51,51,51,0.8) !important;
      }
    }
  }

</style>
