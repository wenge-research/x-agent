<template>
  <div class="outerDialog">
    <div class="headBar">
      <div class="leftSlide">
        <img
          src="@/assets/images/arrow-go-back-fill.svg"
          @click="closeDialog"
        />
        <div class="titleIcon">
          <p>
            {{ appForm.applicationName || $t("noApplicationName") }}
            <img
              src="@/assets/images/appManagement/edit-line.svg"
              class="edit-icon"
              @click="openDialog('editName')"
            />
          </p>
          <p>{{ appForm.introduce || $t("noDescription") }}</p>
        </div>
      </div>
      <div class="rightSlide">
        <el-button class="btn" @click="temporarSave(2)">
          <img src="@/assets/images/save-line.svg" />
          <span>{{ $t("temporaryStorage") }}</span>
        </el-button>
        <el-button
          class="btns"
          v-permission="'issue'"
          @click="openDialog('fabu')"
        >
          <img src="@/assets/images/send-plane-fill.svg" />
          <span>{{ $t("publish") }}</span>
        </el-button>
      </div>
    </div>
    <div class="dialogPower">
      <div class="left-content">
        <div class="flex-center just">
          <span style="font-size: 22px">{{ $t("capabilitiesSettings") }}</span>
          <modelSelect :id="appForm.modelId" @change="modelChange"></modelSelect>
        </div>
        <div class="marginTop20">
          <div class="abilityTabs">
            <span
              class="tabs"
              :class="{ active: activeTabsName == 'first' }"
              @click="activeTabsName = 'first'"
              >{{ $t("roleSetting") }}</span
            >
            <span
              class="tabs"
              :class="{ active: activeTabsName == 'second' }"
              @click="activeTabsName = 'second'"
              >{{ $t("commandTemplate") }}</span
            >
            <el-input
              v-show="activeTabsName == 'first'"
              :placeholder="detailedSettings"
              v-model="appForm.systemPrompt"
              show-word-limit
              type="textarea"
              :autosize="{ minRows: 6, maxRows: 12 }"
              maxlength="2000"
            ></el-input>
            <el-input
              v-show="activeTabsName == 'second'"
              :placeholder="$t('inputPlaceholder')"
              type="textarea"
              :autosize="{ minRows: 6, maxRows: 12 }"
              maxlength="2000"
              v-model="appForm.promptTemplate"
            ></el-input>
          </div>
        </div>
        <div class="marginTop32">
          <div class="flex-center just">
            <span class="boxName">{{ $t("associateKnowledgeBase") }}</span>
            <div>
              <el-button
                type="text"
                icon="el-icon-s-operation"
                style="color: #383d47"
                @click.stop="openDialog('setBase')"
                >{{ $t("parameterSettings") }}</el-button
              >
              <el-button
                type="text"
                icon="el-icon-plus"
                style="color: #1c50fd"
                @click="openDialog('addBase')"
                >{{ $t("add") }}</el-button
              >
            </div>
          </div>
          <div>
            <ul>
              <li
                class="base-li flex-center just"
                v-for="(item, index) in appForm.knowledgeIds"
                :key="index"
              >
                <div class="li-name flex-center">
                  <img src="@/assets/images/appManagement/zsk.svg" />
                  <span>{{ filterKnowledge(item) }}</span>
                </div>
                <i
                  class="el-icon-close"
                  style="cursor: pointer"
                  @click="deleteKnowledgeId(item)"
                ></i>
              </li>
            </ul>
          </div>
        </div>
        <div class="marginTop32" v-if="funcOrTool.length > 0">
          <div class="boxName">
            <span>{{ $t("function") }}</span>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('experience')">
            <div class="flex-center just">
              <div class="flex-center">
                <span></span>
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon1.svg')"
                />
                <span class="boxName">{{ $t("dialogueExperience") }}</span>
              </div>
              <div class="flex-center">
                <el-button
                  type="text"
                  style="color: #828894"
                  @click="confirmTips('1')"
                  icon="el-icon-star-off"
                  >{{ $t("autoFill") }}</el-button
                >
              </div>
            </div>
            <!-- <div class="marginTop10">
              <div class="formTitle">
                <span>{{ $t("openingStatement") }}</span>
              </div>
              <el-input
                :placeholder="$t('inputPlaceholder')"
                v-model="appForm.prologue"
              ></el-input>
            </div> -->
            <div class="marginTop10">
              <div class="flex-center just">
                <div class="formTitle">
                  <span>{{ $t("addInitialQuestion") }}</span>
                </div>
                <el-button
                  type="text"
                  icon="el-icon-plus"
                  style="color: #1c50fd"
                  @click="addQues"
                  >{{ $t("addQuestion") }}</el-button
                >
              </div>
              <div
                style="margin-bottom: 8px"
                v-for="(item, index) in appForm.presetQuestionList"
                :key="'question' + index"
              >
                <el-input
                  :placeholder="$t('inputPlaceholder')"
                  v-model="appForm.presetQuestionList[index]"
                >
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
            </div>
            <div class="marginTop10">
              <div class="formTitle">
                <span>{{ $t("learningGrowthPhrases") }}</span>
                <img src="@/assets/images/question-line.svg" />
              </div>
              <el-input
                :placeholder="$t('inputPlaceholder')"
                v-model="appForm.tailTalk"
              ></el-input>
            </div>
            <div class="marginTop10 flex-center just">
              <div>
                <div class="formTitle">
                  <span>{{ $t("answerFailurePrompt") }}</span>
                  <img src="@/assets/images/question-line.svg" />
                </div>
                <el-input
                  :placeholder="$t('inputPlaceholder')"
                  v-model="appForm.failureTalk"
                  style="width: 454px"
                ></el-input>
              </div>
              <div>
                <div class="formTitle">
                  <span>{{ $t("responseTimeout") }}</span>
                  <img src="@/assets/images/question-line.svg" />
                </div>
                <el-input-number
                  style="width: 138px"
                  v-model="appForm.answerTimeout"
                  :min="1"
                  :max="100000"
                  :step="1"
                  controls-position="right"
                  data-unit="秒"
                  class="my-el-input-number"
                >
                </el-input-number>
              </div>
            </div>
            <div class="marginTop10">
              <div class="formTitle">
                <span>{{ $t("disclaimer") }}</span>
              </div>
              <el-input
                :placeholder="$t('inputPlaceholder')"
                v-model="appForm.disclaimer"
                type="textarea"
                :rows="6"
              ></el-input>
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('recommendation')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon2.svg')"
                />
                <span class="boxName">{{
                  $t("recommendedQuestionCount")
                }}</span>
              </div>
            </div>
            <div class="marginTop10 flex-center">
              <div class="formTitle">
                <span class="widthSpan">{{
                  $t("recommendationStrategy")
                }}</span>
              </div>
              <div>
                <el-select
                  :placeholder="$t('pleaseSelect')"
                  v-model="appForm.recommendation"
                  style="width: 100%"
                  clearable
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
              <div
                style="
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                  width: 100%;
                "
              >
                <div
                  class="formTitle"
                  style="width: 100px; margin-left: 40px; margin-right: 20px"
                >
                  <span>{{ $t("recommendedQuestionCount") }}</span>
                </div>
                <el-input
                  :placeholder="$t('inputPlaceholder')"
                  v-model="appForm.recommendationNum"
                  type="number"
                  :step="1"
                  :min="0"
                  :max="20"
                ></el-input>
              </div>
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('virtual')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon3.svg')"
                />
                <span class="boxName">{{ $t("virtualHuman") }}</span>
              </div>
            </div>
            <div class="marginTop10 flex-center">
              <div class="formTitle">
                <span class="widthSpan">{{ $t("virtualPersonId") }}</span>
              </div>
              <el-input
                :placeholder="$t('inputPlaceholder')"
                v-model="appForm.virtualHumanId"
                style="width: 100%"
              ></el-input>
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('voice')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon4.svg')"
                />
                <span class="boxName">{{ $t("voiceConversation") }}</span>
              </div>
            </div>
            <div class="marginTop10 flex-center">
              <div class="formTitle">
                <span class="widthSpan">{{ $t("textToSpeech") }}</span>
              </div>
              <el-select
                :placeholder="$t('pleaseSelect')"
                v-model="appForm.ttsId"
                style="width: 100%"
              >
                <el-option
                  v-for="item in ttsListData"
                  :key="item.id"
                  :value="item.id"
                  :label="item.componentName"
                ></el-option>
              </el-select>
            </div>
            <div class="marginTop10 flex-center">
              <div class="formTitle">
                <span class="widthSpan">{{ $t("speechToText") }}</span>
              </div>
              <el-select
                :placeholder="$t('pleaseSelect')"
                v-model="appForm.sttId"
                style="width: 100%"
              >
                <el-option
                  v-for="item in ttsListData"
                  :key="item.id"
                  :value="item.id"
                  :label="item.componentName"
                ></el-option>
              </el-select>
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('answerSource')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon5.svg')"
                />
                <span class="boxName">{{ $t("answerTraceability") }}</span>
              </div>
            </div>
            <div class="marginTop10 flex-center">
              <div class="formTitle">
                <span class="widthSpan">{{ $t("documentLinkType") }}</span>
              </div>
              <div style="display: flex; justify-content: space-between">
                <div>
                  <el-select
                    :placeholder="$t('pleaseSelect')"
                    v-model="appForm.docLinkType"
                    style="width: 100%"
                  >
                    <el-option
                      :label="$t('originalFileLink')"
                      value="1"
                    ></el-option>
                    <el-option :label="$t('webPageLink')" value="2"></el-option>
                  </el-select>
                </div>
                <div
                  style="
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                  "
                >
                  <div
                    class="formTitle"
                    style="width: 100px; margin-left: 40px; margin-right: 10px"
                  >
                    <span>{{ $t("previewFile") }}</span>
                  </div>
                  <el-switch
                    v-model="appForm.previewDoc"
                    active-color="#4157FE"
                    inactive-color="#CED4E0"
                    :active-value="$t('yes')"
                    :inactive-value="$t('no')"
                  >
                  </el-switch>
                </div>
              </div>
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('DisableIP')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon6.svg')"
                />
                <span class="boxName">{{ $t("disableIP") }}</span>
              </div>
            </div>
            <div style="color: #828894; margin: 5px 0px 0px 20px">
              {{ $t("allowRestrictedAccessToIP") }}
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('Authentication')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon7.svg')"
                />
                <span class="boxName">{{
                  $t("authenticationStrategySettings")
                }}</span>
              </div>
            </div>
            <div class="marginTop10 flex-center">
              <div class="formTitle">
                <span class="widthSpan">{{
                  $t("h5AuthenticationMethod")
                }}</span>
              </div>
              <el-select
                :placeholder="$t('pleaseSelect')"
                v-model="appForm.clientAuthChannel"
                clearable
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
            <div class="marginTop10 flex-center">
              <div class="formTitle">
                <span class="widthSpan">{{
                  $t("pcAuthenticationChannel")
                }}</span>
              </div>
              <el-select
                :placeholder="$t('pleaseSelect')"
                v-model="appForm.pcAuthChannel"
                clearable
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
            <div class="marginTop10">
              <el-switch
                :active-text="$t('needTransferToHuman')"
                v-model="appForm.toHumanFlag"
                :active-value="$t('yes')"
                :inactive-value="$t('no')"
              ></el-switch>
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('TouchAnswer')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon10.svg')"
                />
                <span class="boxName">{{ $t("answerPolishing") }}</span>
              </div>
            </div>
            <div class="marginTop10 flex-center">
              <el-input
                :placeholder="detailedSettings"
                v-model="appForm.polishPrompt"
                show-word-limit
                type="textarea"
                :autosize="{ minRows: 6, maxRows: 12 }"
                maxlength="2000"
              ></el-input>
            </div>
          </div>
        </div>
        <div class="marginTop32">
          <div
            class="boxName"
            v-if="
              funcOrTool.includes('interception') ||
              funcOrTool.includes('SearchEngine') ||
              funcOrTool.includes('Scenesetting')
            "
          >
            <span>{{ $t("plugInUnit") }}</span>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('interception')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon8.svg')"
                />
                <div>
                  <span class="boxName">{{
                    $t("securitySensitiveWordInterception")
                  }}</span>
                  <p style="color: #828894; margin-top: 5px">
                    {{ $t("sensitiveWordLibraries") }}
                  </p>
                </div>
              </div>
              <div class="flex-center">
                <el-button
                  type="text"
                  icon="el-icon-setting"
                  style="color: #1c50fd"
                  @click="openDialog('addSensitive')"
                  >{{ $t("settings") }}</el-button
                >
              </div>
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('SearchEngine')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon9.svg')"
                />
                <span class="boxName">{{ $t("searchEngine") }}</span>
              </div>
            </div>
            <div class="marginTop10 flex-center">
              <div class="formTitle">
                <span class="widthSpan">{{ $t("searchEngine") }}</span>
              </div>
              <el-select
                :placeholder="$t('pleaseSelect')"
                v-model="appForm.networkChannel"
                style="width: 100%"
              >
                <el-option :label="$t('baidu')" value="baidu"></el-option>
                <el-option :label="$t('toutiao')" value="toutiao"></el-option>
                <el-option :label="$t('bing')" value="bing"></el-option>
                <el-option
                  :label="$t('toutiaoImages')"
                  value="toutiaoImage"
                ></el-option>
                <el-option :label="$t('quark')" value="quark"></el-option>
              </el-select>
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('Scenesetting')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon11.svg')"
                />
                <div>
                  <div>
                    <span class="boxName">{{ $t("sceneCall") }} </span>
                    <span
                      class="disabled"
                      v-if="appForm.subjectFlag !== '是'"
                      >{{ $t("disabled") }}</span
                    >
                  </div>
                  <p style="color: #828894; margin-top: 5px">
                    {{ $t("sceneCallTips") }}
                  </p>
                </div>
              </div>
              <div class="flex-center">
                <el-button
                  type="text"
                  icon="el-icon-setting"
                  style="color: #1c50fd"
                  @click="openDialog('sceneSetting')"
                  >{{ $t("settings") }}</el-button
                >
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="bottom-btn">
        <el-button
          type="text"
          icon="el-icon-circle-plus"
          style="color: #1c50fd"
          @click="openDialog('tool')"
          >{{ $t("addFeatures") }}</el-button
        >
      </div>
    </div>
    <div class="previewDebugging">
      <div class="flex-center just">
        <div class="formTitle flex-center">
          <img
            src="@/assets/images/console-fill.svg"
            style="margin-right: 5px"
          />
          <span>{{ $t("previewAndDebug") }}</span>
        </div>
        <div>
          <el-button
            type="text"
            icon="el-icon-s-fold"
            style="color: #1c50fd"
            @click="openDialog('order')"
          >
            {{ $t("executeSequentialSteps") }}
          </el-button>
          <el-button
            type="text"
            icon="el-icon-menu"
            style="color: #1c50fd"
            @click="openDialog('setting')"
            >{{ $t("displaySettings") }}</el-button
          >
          <el-button
            type="text"
            icon="el-icon-s-platform"
            style="color: #1c50fd"
            @click="temporarSave(1, 3)"
            >{{ $t("viewRenderedEffect") }}</el-button
          >
        </div>
      </div>
      <div v-loading="rightLoading">
        <iframe
          ref="iframe"
          style="
            width: 100%;
            height: calc(100vh - 260px);
            margin: 30px auto 0px;
          "
          id="myiframe"
        ></iframe>
      </div>
    </div>
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
    ></addKnowledgeBase>
    <!-- 添加工具功能 -->
    <addFunctionOrTool
      v-if="toolVisible"
      :dialogVisible="toolVisible"
      :funcOrToolArr="funcOrTool"
      :params="appForm"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
      @changeStatus="changeStatus"
    ></addFunctionOrTool>
    <!-- 执行顺序步骤 -->
    <orderStep
      v-if="orderStepVisible"
      :dialogVisible="orderStepVisible"
      :params="appForm.processStep"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
    ></orderStep>
    <!-- 展示设置 -->
    <showSetting
      v-if="showSettingVisible"
      :dialogVisible="showSettingVisible"
      :params="appForm"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
    ></showSetting>
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
    ></createApplication>
    <!-- 场景设置 -->
    <el-dialog
      title=""
      :visible.sync="sceneSettingVisible"
      width="500px"
      class="defaultDialo"
      :before-close="clickConfig"
      append-to-body
    >
      <div slot="title" class="dialog-title">
        <span style="margin-right: 15px">{{ $t("sceneSetting") }}</span>
        <el-switch
          v-model="appForm.subjectFlag"
          active-color="#4157FE"
          inactive-color="#CED4E0"
          :active-value="$t('yes')"
          :inactive-value="$t('no')"
        >
        </el-switch>
      </div>
      <div class="marginTop10 flex-center">
        <el-input
          :placeholder="detailedSettings"
          v-model="appForm.subjectPrompt"
          show-word-limit
          type="textarea"
          :autosize="{ minRows: 12, maxRows: 20 }"
          maxlength="2000"
        ></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="sceneSettingConfig">{{
          $t("confirm")
        }}</el-button>
        <el-button @click="sceneSettingVisible = false">{{
          $t("cancel")
        }}</el-button>
      </span>
    </el-dialog>
    <!-- 关联敏感词库 -->
    <addSensitiveDialog
      v-if="addSensitiveVisible"
      :dialogVisible="addSensitiveVisible"
      @clickConfig="clickConfig"
      :appConfigForm="appConfigForm"
    ></addSensitiveDialog>
  </div>
</template>
  
<script>
import {
  addApplication,
  modelList,
  ttsList,
  defaultApp,
  getAuthChannels,
  applicationPluginDataList,
} from "@/api/app";
import modelSelect from "@/components/ModelSelect.vue";
import knowledgeBaseSet from "./knowledgeBaseSet.vue";
import addKnowledgeBase from "./addKnowledgeBase.vue";
import addFunctionOrTool from "./addFunctionOrTool.vue";
import publishSuccess from "./publishSuccess.vue";
import orderStep from "./orderStep.vue";
import showSetting from "./showSetting.vue";
import publishWay from "./publishWay";
import createApplication from "./createApplication";
import { getKnowledgeInfoList } from "@/api/index";
import addSensitiveDialog from "@/views/intelligentSearch/components/addSensitiveDialog.vue";

export default {
  components: {
    modelSelect,
    knowledgeBaseSet,
    addKnowledgeBase,
    addFunctionOrTool,
    orderStep,
    showSetting,
    publishWay,
    publishSuccess,
    createApplication,
    addSensitiveDialog
  },
  data() {
    return {
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
        sttId: "",
        buttonStyle: "",
        buttonText: "",
        modelId: "",
        modelAnswerFlag: false,
        sensitiveFlag: false,
        networkFlag: false,
        ipFlag: false,
        answerTimeout: "",
        notAnswer: "",
        promptTemplate: "",
        subjectPrompt: "",
        ocrFlag: false,
        videoFlag: false,
        multiDialogueFlag: false,
        multiDialogueNum: 1,
        knowledgeFlag: false,
        rewritingFlag: false,
        recommendation: "",
        recommendationNum: "",
        systemPrompt: "",
        remark: "",
        contentScore: 1.76,
        rangeContentScore: 0.88,
        qaTitleScore: 1.91,
        qaRangeTitleScore: 0.91,
        qaContentScore: 0.88,
        qaRangeContentScore: 0.88,
        filterNum: 3,
        prepareNum: 60,
        publishStatus: "1",
        logo: "",
        previewDoc: "",
        robotIcon: "",
        webIcon: "",
        userIcon: "",
        knowledgeIds: [],
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
        polishFlag: false,
        polishPrompt: "",
        inputPlaceholder: "",
        prologue: "",
        pcAuthChannel: "",
        toHumanFlag: "",
        docLinkType: "",
        backgroundImageUrl: "",
        facadeImageUrl: "",
        subjectFlag: "",
      },
      modleOptions: [],
      uploadBtnLogo: false,
      fileListLogo: [],
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      rules: {
        applicationName: [
          { required: true, message: "请输入名称", trigger: "blur" },
        ],
        introduce: [
          { required: true, message: "请输入描述", trigger: "blur" },
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
      toolVisible: false, // 功能工具添加
      orderStepVisible: false, //执行顺序步骤
      showSettingVisible: false, // 暂时设置
      fabuDialogVisible: false, // 发布
      clOptions: [
        {
          label: "匹配问题",
          value: "qa-question",
        },
        {
          label: "匹配答案",
          value: "qa-content",
        },
        {
          label: "匹配知识库",
          value: "knowledge",
        },
      ],
      h5AuthChannels: [],
      pcAuthChannels: [],
      funcOrTool: [], //功能或工具
      detailedSettings:
        "用结构化的方式，描述智能体的角色设定、工作流程、原则。\n比如：\n# 角色：你是一个天气预报员，可以查询天气信息。\n## 技能\n- 询问用户地理位置、时间天气需求\n- 提供详细的天气预报：包含气温、湿度、天气状况、风速、紫外线和PM2.5值。\n## 原则\n- 只能提供天气信息，不回答其他问题；\n- 所有数据都要从工具中获取，不能自行编造；",
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
      firstApplicationId: "",
      firstId: "",
      rightLoading: false,
	  mcpServerIdList:[]
    };
  },
  props: {
    appConfigForm: Object,
  },
  mounted() {
    this.getModelList(); //模型列表
    this.getTtsId(); //语音列表
    this.knowledgeList(); // 知识库
    this.getH5AuthChannelList();
    this.getPcAuthChannelList();
    this.defaultAppInfoSet(this.appConfigForm);
    this.temporarSave(1, 4)
	this.$EventBus.$on('setaddMcpDataEmit', (val) => {
		console.log('val',val)
	  this.mcpServerIdList = val
	})
  },
  methods: {
    modelChange(val){
      this.appForm.modelId = val
    },
    sendMessage() {
      const iframe = this.$refs.iframe;
      if (iframe) {
        const iframeWindow = iframe.contentWindow;
        // 现在可以安全地使用iframeWindow对象了
        iframeWindow.postMessage({ param1: "测试传入子组件的参数" }, "*");
      }
    },
    closeDialog() {
      this.fabuSuccessVisible = false;
      this.$emit("configCloseDialog", false);
    },
    defaultAppInfoSet(item) {
      this.knowledgeIdArr = item.knowledgeIds || [];
      this.uploadBtnLogo = item.logo ? true : false;
      this.appForm = {
        id: item.id,
        applicationId: item.applicationId,
        templateId: "4e913b5ec70f4eba9f467a713d25da5f",
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
        webIcon: item.webIcon,
        userIcon: item.userIcon,
        identityIcon: item.identityIcon,
        previewDoc: item.previewDoc,
        virtualHumanFlag: item.virtualHumanFlag == "是" ? true : false,
        voiceDialogueFlag: item.voiceDialogueFlag == "是" ? true : false,
        modelAnswerFlag: item.modelAnswerFlag == "是" ? true : false,
        sensitiveFlag: item.sensitiveFlag == "是" ? true : false,
        networkFlag: item.networkFlag == "是" ? true : false,
        ipFlag: item.ipFlag == "是" ? true : false,
        ocrFlag: item.ocrFlag == "是" ? true : false,
        videoFlag: item.videoFlag == "是" ? true : false,
        multiDialogueFlag: item.multiDialogueFlag == "是" ? true : false,
        knowledgeFlag: item.knowledgeFlag == "是" ? true : false,
        sourceShowFlag: item.sourceShowFlag,
        recommendQuestionsShowFlag: item.recommendQuestionsShowFlag,
        rewritingFlag: item.rewritingFlag == "是" ? true : false,
        subjectFlag: item.subjectFlag,
        presetQuestionList:
          item.presetQuestionList && item.presetQuestionList.length > 0
            ? item.presetQuestionList
            : [""],
        knowledgeIds: item.knowledgeIds,
        networkChannel: item.networkChannel,
        polishFlag: item.polishFlag == "是" ? true : false,
        polishPrompt: item.polishPrompt,
        backgroundImageUrl: item.backgroundImageUrl,
        facadeImageUrl: item.facadeImageUrl,
      };

      if (item.processStep && Array.isArray(item.processStep)) {
        this.appForm.processStep = [...item.processStep];
      } else {
        this.appForm.processStep = item.processStep
          ? item.processStep.split(",")
          : [];
      }
      this.defaulFunctiontList();
      console.log("editApp", this.appForm);
    },
    // 查询列表应用插件关联表
    defaulFunctiontList() {
      applicationPluginDataList({
        applicationId: this.appForm.applicationId,
      }).then((res) => {
        if (res.code == "000000") {
          this.funcOrTool = [];
          if (res.data.length > 1) {
            res.data.forEach((element) => {
              if (element.status === "是") {
                this.funcOrTool.push(element.pluginCode);
              }
            });
          }
          this.clearDefault();
        }
      });
    },
    // 显示关联模型
    filterModleName(item) {
      let findItem = this.modleOptions.find((items) => items.modelId == item);
      return findItem?.modelName;
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
        this.addSensitiveVisible = true;
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
    },
    changeStatus(filed, value) {
      console.log("--LL>>", filed, value);
      if ("是" === value) {
        this.appForm[filed] = value;
      } else {
        this.appForm[filed] = "";
      }
    },
    // 弹窗回调带参数
    clickConfigParams(type, data) {
      if (type == "addBaseVisible") {
        this.addBaseVisible = false;
        // 新增知识库
        if (data.length > 0) {
          this.knowledgeIdArr = this.appForm.knowledgeIds = data || [];
        }
      } else if (type == "setBaseVisible") {
        this.setBaseVisible = false;
        // 知识库参数设置
        this.appForm.contentScore = data.contentScore;
        this.appForm.rangeContentScore = data.rangeContentScore;
        this.appForm.qaTitleScore = data.qaTitleScore;
        this.appForm.qaRangeTitleScore = data.qaRangeTitleScore;
        this.appForm.qaContentScore = data.qaContentScore;
        this.appForm.qaRangeContentScore = data.qaRangeContentScore;
        this.appForm.filterNum = data.filterNum;
        this.appForm.prepareNum = data.prepareNum;
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
        this.appForm.webIcon = data.webIcon;
        this.appForm.userIcon = data.userIcon;
        this.appForm.identityIcon = data.identityIcon;
        this.appForm.backgroundImageUrl = data.backgroundImageUrl; // 模版上传背景
        this.appForm.facadeImageUrl = data.facadeImageUrl; // 形象助手
        this.appForm.mobileTemplateId = data.mobileTemplateId;
        this.appForm.templateId = "4e913b5ec70f4eba9f467a713d25da5f";
        this.appForm.greeting = data.greeting;
        this.appForm.inputPlaceholder = data.inputPlaceholder;

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
        this.appForm.applicationName = data.applicationName;
        this.appForm.introduce = data.introduce;
        this.appForm.logo = data.logo;
        this.editApplicationVisible = false;
      }
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
        pageSize: 1000,
        order: "",
        sort: ""
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
    },
    addQues() {
      this.appForm.presetQuestionList.push("");
    },
    deleteQues(index) {
      this.appForm.presetQuestionList.splice(index, 1);
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
    temporarSave(flag, typeflag) {
      this.appForm.publishStatus = "1";
      this.submitAddApp(flag, typeflag);
    },
    submitAddApp(flag, typeflag) {
      this.rightLoading = true;
      if (!this.appForm.templateId && this.appForm.publishStatus == "1") {
        this.$message({
          type: "warning",
          message: "请选择PC模板后再发布",
        });
        return;
      }
      if (!this.appForm.mobileTemplateId && this.appForm.publishStatus == "1") {
        this.$message({
          type: "warning",
          message: "请选择H5模板后再发布",
        });
        return;
      }
      const params = Object.assign(this.appConfigForm, this.appForm);
      if (!params.applicationCode && typeflag == 4) {
        return;
      }
      if (!this.appForm.sourceShowFlag) {
        params.previewDoc = "否";
      }
      // 暂存：2与发布：3
      if (flag == 3 || flag == 2) {
        params.applicationId = params.applicationId
          ? params.applicationId
          : this.firstApplicationId;
        params.id = params.id ? params.id : this.firstId;
      }
      if(flag == 3){
        params.makeType="1"
      }else{
        params.makeType=""
      }
	  //mcp数组集合
		let mcpServerIds = []
		this.mcpServerIdList.forEach((element) => {
			mcpServerIds.push(element.mcpId)
		})
      addApplication({
        ...params,
        type: "search",
		mcpServerIds:mcpServerIds,
        virtualHumanFlag: this.appForm.virtualHumanFlag ? "是" : "否",
        voiceDialogueFlag: this.appForm.voiceDialogueFlag ? "是" : "否",
        modelAnswerFlag: this.appForm.modelAnswerFlag ? "是" : "否",
        sensitiveFlag: this.appForm.sensitiveFlag ? "是" : "否",
        networkFlag: this.appForm.networkFlag ? "是" : "否",
        ipFlag: this.appForm.ipFlag ? "是" : "否",
        ocrFlag: this.appForm.ocrFlag ? "是" : "否",
        videoFlag: this.appForm.videoFlag ? "是" : "否",
        multiDialogueFlag: this.appForm.multiDialogueFlag ? "是" : "否",
        knowledgeFlag: this.appForm.knowledgeFlag ? "是" : "否",
        rewritingFlag: this.appForm.rewritingFlag ? "是" : "否",
        polishFlag: this.appForm.polishFlag ? "是" : "否",
        recommendQuestionsShowFlag: this.appForm.recommendQuestionsShowFlag ? "1" : "0",
        sourceShowFlag: this.appForm.sourceShowFlag ? "1" : "0",
        processStep: this.appForm.processStep
          ? this.appForm.processStep.join(",")
          : "",
      }).then((res) => {
          console.log(res, "res");
          if (res.code == "000000") {
            // this.appForm = JSON.parse(JSON.stringify(res.data));
            if (flag != 1) {
              this.fabuSuccessData = res.data;
              this.fabuSuccessVisible = true;
            }
            if (flag == 2) {
              this.closeDialog();
            }
            if (flag == 1) {
              const iframe = this.$refs.iframe;
              let that = this;
              that.firstApplicationId = res.data?.applicationId;
              that.firstId = res.data?.id;
              if (iframe) {
                this.$refs.iframe.src = "about:blank"; // 作为一个临时的链接，如果是其它正常可访问URL，会浪费一些不必要流量
                const _t = setTimeout(() => {
                  that.$refs.iframe.src = res.data.clientLink;
                  console.log("that.editItem.clientLink", res.data.clientLink);

                  // that.$refs.iframe.src = 'http://localhost:8082/#/knowledgeDetails/zgc'
                  window.addEventListener("message", (event) => {
                    console.log(event.data, "data");
                    if (event.data == "ready")
                      iframe.contentWindow.postMessage({ isIframe: true }, "*");
                  });
                  clearTimeout(_t);
                }, 300);
              }
            }
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        })
        .finally(() => {
          setTimeout(() => {
            this.rightLoading = false;
          }, 3000);
        });
    },
    getTtsId() {
      ttsList({
        pageNo: 1,
        pageSize: 1000,
      }).then((res) => {
        if (res.code == "000000") {
          this.ttsListData = res.data?.records || [];
        } else {
          this.ttsListData = [];
        }
      });
    },
    getModelList() {
      modelList({
        id: "",
        modelId: "",
        modelName: "",
        status: "",
      }).then((res) => {
        if (res.code == "000000") {
          this.modleOptions = res?.data;
        } else {
          this.modleOptions = [];
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
      this.defaultDialogVisible = true;
    },
    defaultDialogClose() {
      this.defaultDialogVisible = false;
    },
    // 自动填充
    filldefaultValues(type) {
      this.defaultDialogVisible = false;
      defaultApp({}).then((res) => {
        if (res.code == "000000") {
          let defaultInfo = res.data;
          if (type == "1") {
            this.appForm.prologue = defaultInfo.prologue;
            this.appForm.tailTalk = defaultInfo.tailTalk;
            this.appForm.failureTalk = defaultInfo.failureTalk;
            this.appForm.answerTimeout = defaultInfo.answerTimeout;
            this.appForm.disclaimer = defaultInfo.disclaimer;
            this.appForm.presetQuestionList =
              defaultInfo.presetQuestionList &&
              defaultInfo.presetQuestionList.length > 0
                ? defaultInfo.presetQuestionList
                : [""];
          }
        }
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
        this.appForm.presetQuestionList = [];
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

      if (!this.funcOrTool.includes("Authentication")) {
        this.appForm.clientAuthChannel = "";
        this.appForm.pcAuthChannel = "";
        this.appForm.toHumanFlag = null;
      }
      if (!this.funcOrTool.includes("interception")) {
        this.appForm.sensitiveFlag = false;
      } else {
        this.appForm.sensitiveFlag = true;
      }

      if (!this.funcOrTool.includes("SearchEngine")) {
        this.appForm.networkChannel = "";
        this.appForm.networkFlag = false;
      } else {
        this.appForm.networkFlag = true;
      }
    },
    sceneSettingConfig() {
      this.sceneSettingVisible = false;
    },
  },
};
</script>

<style lang="scss" scoped>
.largeModel {
  width: 242px;
  border-radius: 4px;
  border: 1px solid #e1e4eb;
  padding: 10px 14px;
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

.abilityInter {
  padding: 0 24px;
}

.demo-ruleForm {
  ::v-deep .el-form-item__label {
    font-family: MiSans, MiSans;
    font-weight: 600;
    font-size: 16px;
    color: #383d47;
    line-height: 36px;
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
  height: calc(100vh - 120px);
  .dialogPower {
    width: 40%;
    background: #ffffff;
    box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.1);
    border-radius: 4px 0 0 4px;
    border: 1px solid #e1e4eb;
    padding: 28px 32px 40px 32px;
    position: relative;

    .left-content {
      height: calc(100vh - 230px);
      overflow-y: auto;
    }
    ::-webkit-scrollbar {
      width: 0;
    }

    ::v-deep .el-textarea__inner {
      font-family: MiSans, MiSans;
    }
  }
  .previewDebugging {
    flex: 1;
    background: #f2f5fa;
    border-radius: 0px 8px 8px 0px;
    border: 1px solid #e1e4eb;
    padding: 28px 32px 28px 32px;
    box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.1);
    position: relative;
  }
  .headBar {
    background: #f2f5fa;
    padding: 16px 0 16px 16px;
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
    width: 100%;
    position: absolute;
    top: 0;
    right: 0;
    img {
      width: 15px;
      height: 15px;
    }
    .leftSlide {
      display: flex;
      justify-content: space-between;
      align-items: center;
      > img {
        margin-right: 16px;
        cursor: pointer;
      }
      .titleIcon {
        p:first-child {
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 20px;
          color: #383d47;
          line-height: 24px;
          text-align: left;
          font-style: normal;
          margin-bottom: 6px;
        }
        p:last-child {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #768094;
          line-height: 18px;
          text-align: left;
          font-style: normal;
        }
      }
    }
    .rightSlide {
      display: flex;
      justify-content: space-between;
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
          margin-right: 5px;
        }
        > span,
        > img {
          vertical-align: middle;
        }
      }
      .btn {
        height: 40px;
        color: #3666ea;
        border: 1px solid #3666ea;
        border-radius: 4px;
        img {
          margin-right: 5px;
        }
        img,
        span {
          vertical-align: middle;
        }
      }
      .btns {
        color: #fff;
        height: 40px;
        background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
        border-radius: 4px;
        img {
          margin-right: 5px;
        }
        img,
        span {
          vertical-align: middle;
        }
      }
    }
  }
}

.marginTop32 {
  margin-top: 32px;
  .widthSpan {
    display: inline-block;
    width: 113px;
    color: #768094 !important;
  }
}
.formTitle {
  font-family: MiSans, MiSans;
  font-weight: 500;
  font-size: 16px;
  color: #383d47;
  line-height: 33px;
  text-align: left;
  font-style: normal;
  text-transform: none;
  margin-bottom: 3px;
  > img {
    width: 16px;
    height: 16px;
  }
  > img,
  span {
    vertical-align: middle;
  }
}

.base-li {
  height: 56px;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #e1e4eb;
  padding: 0 12px;
  margin-bottom: 8px;

  .li-name {
    font-weight: 400;
    font-size: 16px;
    color: #383d47;
    text-align: left;
    font-style: normal;

    > img {
      width: 24px;
      height: 24px;
      color: #a4bffe;
      margin-right: 5px;
    }
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
  background: #f2f5fa;
  border-radius: 4px;
  padding: 16px;

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
  color: #999999;
}
.my-el-input-number[data-unit] .el-input__inner {
  padding-left: 30px;
  padding-right: calc(var(--el-input-number-unit-offset-x) + 12px);
}
.defaultDialo {
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__body {
      padding: 16px 32px;
    }
    .el-dialog__header {
      background: linear-gradient(
        180deg,
        rgba(43, 88, 213, 0.1) 0%,
        rgba(43, 88, 213, 0) 100%
      );
      border-radius: 8px 8px 0px 0px;
    }
    .el-dialog__footer {
      text-align: left;
    }
  }
  ::v-deep .el-textarea__inner {
    font-family: MiSans, MiSans;
  }
  .tipsText {
    font-weight: 400;
    font-size: 16px;
    line-height: 20px;
    color: #828894;
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
  .tabs {
    display: inline-block;
    height: 40px;
    line-height: 40px;
    color: #b4bccc;
    font-size: 16px;
    margin-right: 20px;
    cursor: pointer;

    &.active {
      color: #383d47;
      font-size: 16px;
      font-weight: 500;
      border-bottom: 2px solid #1c50fd;
    }
  }
}
</style>
  