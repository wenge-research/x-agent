<template>
  <div class="setting-dia">
    <div class="function-ctn">
      <div class="setting-dia-title">
        {{ $t("add") }}
      </div>
      <div class="function-list-ctn">
        <div class="function-list" v-for="(item,index) in funtionList" :key="index+'functionList'" v-show="!useLessNameList.includes(item.pluginName)">
          <div class="function-msg">
            <svg class="icon-img" aria-hidden="true">
              <use
                :xlink:href="`#icon-` + getIcon(item.pluginName)"
              ></use>
            </svg>
            <p class="function-title">{{ item.pluginName }}</p>
          </div>
          <div class="function-intro">{{ item.remark }}</div>
          <div class="function-switch">
             <el-switch
                v-model="item.status"
                active-color="#4157FE"
                inactive-color="#CED4E0"
                active-value="是"
                inactive-value="否"
                @change="changeFunc(item)"
              ></el-switch>
          </div>
        </div>
      </div>
    </div>
    <div class="dialogPower" v-if="funcOrTool.length > 0" >
      <div class="left-content" style="height: 100%;">
        <div style="height: 100%;">
          <div class="setting-dia-title">
            {{ $t("function") }}
          </div>
          <div class="dialogPower-list-ctn">
          <!-- <div class="dialog" v-if="funcOrTool.includes('experience')">
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
                  style="color: #828894; font-size: 16px"
                  @click="confirmTips('1')"
                  icon="el-icon-star-off"
                  size="mini"
                  >{{ $t("autoFill") }}</el-button
                >
              </div>
            </div>
            <div class="marginTop12">
              <div class="formTitle">
                <span>{{ $t("openingStatement") }}</span>
              </div>
              <el-input
                :placeholder="$t('inputPlaceholder')"
                v-model="appForm.prologue"
                size="small"
              ></el-input>
            </div>
            <div class="marginTop12">
              <div class="flex-center just">
                <div class="formTitle">
                  <span>{{ $t("addInitialQuestion") }}（{{ appForm.presetQuestionList?.length }}/4）</span>
                </div>
                <el-button
                  v-if="appForm.presetQuestionList?.length < 4"
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
                  size="small"
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
            <div class="marginTop12">
              <div class="formTitle">
                <span>{{ $t("learningGrowthPhrases") }}</span>
                <img src="@/assets/images/question-line.svg" />
              </div>
              <el-input
                :placeholder="$t('inputPlaceholder')"
                v-model="appForm.tailTalk"
                size="small"
              ></el-input>
            </div>
            <div class="marginTop12 flex-center just">
              <div>
                <div class="formTitle">
                  <span>{{ $t("answerFailurePrompt") }}</span>
                  <img src="@/assets/images/question-line.svg" />
                </div>
                <el-input
                  :placeholder="$t('inputPlaceholder')"
                  v-model="appForm.failureTalk"
                  style="width: 204px"
                  size="small"
                ></el-input>
              </div>
              <div>
                <div class="formTitle">
                  <span>{{ $t("responseTimeout") }}</span>
                  <img src="@/assets/images/question-line.svg" />
                </div>
                <el-input-number
                  style="width: 120px"
                  v-model="appForm.answerTimeout"
                  :min="1"
                  :max="100000"
                  :step="1"
                  controls-position="right"
                  :data-unit="$t('second')"
                  class="my-el-input-number"
                  size="small"
                >
                </el-input-number>
              </div>
            </div>
            <div class="marginTop12">
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
          </div> -->
          <div class="dialog experience" v-if="funcOrTool.includes('experience')" style="margin-top: 0;">
            <config-group
              label="对话体验"
              :icon="require('@/assets/images/appManagement/icon1.svg')"
              tip="对话的相关设置，包括开场白、无法回答话术、免责声明等"
              style="border-radius: 8px;"
            >
              <template v-slot:button>
                <div class="flex-center config-group-button">
                  <el-popover
                    placement="right"
                    width="400"
                    popper-class="auto-fill-popover"
                    :visible-arrow="false"
                    v-model="autoFillVisible"
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
                <div class="marginTop10" style="margin-top: 20px;">
                  <div class="formTitle">
                    <span>{{ $t("openingStatement") }}</span>
                  </div>
                  <el-input
                    :placeholder="$t('inputPlaceholder')"
                    v-model="appForm.prologue"
                  ></el-input>
                </div>
                <div class="marginTop10 init-ques" style="margin-top: 18px;">
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
                      color="#494E57"
                    ></iconpark-icon>
                    添加
                  </div>
                </div>
                <div class="marginTop10 flex-center just" style="margin-top: 16px;">
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
                    >
                    </el-input-number>
                  </div>
                </div>
                <div class="marginTop10 flex" style="gap: 16px;margin-top: 16px;">
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
                      @click="openEditorDialog"
                    >
                    编辑内容<iconpark-icon
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
          <!-- <div class="dialog" v-if="funcOrTool.includes('recommendation')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon2.svg')"
                />
                <span class="boxName">{{
                  "问题建议"
                }}</span>
              </div>
            </div>
            <div class="marginTop12 flex just">
              <div style="width: 204px">
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
                    size="small"
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

              <div style="width: 120px">
                <div class="formTitle">
                  <span>{{ $t("recommendedQuestionCount") }}</span>
                </div>
                <el-input
                  :placeholder="$t('inputPlaceholder')"
                  v-model="appForm.recommendationNum"
                  type="number"
                  :step="1"
                  :min="0"
                  :max="20"
                  size="small"
                ></el-input>
              </div>
            </div>
          </div> -->
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
            <div class="marginTop12">
              <div class="formTitle">
                <span class="widthSpan">{{ $t("virtualPersonId") }}</span>
              </div>
              <el-input
                :placeholder="$t('inputPlaceholder')"
                v-model="appForm.virtualHumanId"
                style="width: 100%"
                size="small"
              ></el-input>
            </div>
          </div>
          <div class="dialog" v-if="funcOrTool.includes('voice')">
            <config-group style="border-radius: 8px;" :label="$t('voiceSettings')" :disabled="true"
                            :icon="require('@/assets/images/appManagement/icon4.svg')">
                            <div class="flex" style="gap: 16px;margin-top: 12px;">
                              
                            <div style="flex: 1;">
                              <div>
                                  <el-switch v-model="isSound" active-color="#4157FE" inactive-color="#CED4E0"
                                    style="color: #000000;" @change="changeVoiceStatus"
                                    :active-text="$t('enableVoiceBroadcast')">
                                </el-switch>
                                </div>
                                <div class="flex-center recommendation-box-authentication voice-bot"
                                     ref="popoverBtnRef" style="margin-top: 8px;">
                                    <div class="voice-left">
                                        <img style="width: 20px; height:20px; border-radius: 50%;"
                                            :src="require('@/assets/images/appManagement/icon4.svg')" alt="">
                                        <h1 class="voicev-text">{{ voiceTitle }}</h1>
                                        <iconpark-icon name="volume-up-line" size="16" color="#494E57"
                                            style="margin-left: auto;cursor: pointer"></iconpark-icon>
                                    </div>
                                    <div class="voice-left">
                                        <!-- <h1 class="voicev-text intonation">{{ $t('intonationFast') }}：{{appForm.voiceSpeed}}</h1>
                                        <h1 class="voicev-text intonation">{{ $t('intonation') }}：{{ appForm.pitch }}</h1> -->
                                        <iconpark-icon name="settings-4-line" size="16" color="#494E57" @click="handelVoice"
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
                                <div>
                                  <el-switch v-model="isVoice" active-color="#4157FE" inactive-color="#CED4E0"
                                    style="color: #000000;" @change="changeVoiceStatus"
                                    :active-text="$t('enableVoiceIdentify')">
                                </el-switch>
                                </div>
                                
                                <el-select :placeholder="$t('pleaseSelect')" v-model="appForm.sttId"
                                    style="width: 100%;margin-top: 8px;">
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
          <!-- <div class="dialog" v-if="funcOrTool.includes('answerSource')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon5.svg')"
                />
                <span class="boxName">{{ $t("answerTraceability") }}</span>
              </div>
            </div>
            <div class="marginTop12">
              <div class="formTitle">
                <span class="widthSpan">{{ $t("documentLinkType") }}</span>
              </div>
              <div>
                <el-select
                  :placeholder="$t('pleaseSelect')"
                  v-model="appForm.docLinkType"
                  style="width: 100%"
                  size="small"
                >
                  <el-option
                    :label="$t('originalFileLink')"
                    value="1"
                  ></el-option>
                  <el-option :label="$t('webPageLink')" value="2"></el-option>
                </el-select>
              </div>
              <div class="flex aligns marginTop12">
                <div style="padding-right: 4px">
                  <span>{{ $t("previewFile") }}</span>
                </div>
                <el-switch
                  v-model="appForm.previewDoc"
                  active-color="#4157FE"
                  inactive-color="#CED4E0"
                  active-value="是"
                  inactive-value="否"
                >
                </el-switch>
              </div>
            </div>
          </div> -->
          <div class="dialog" v-if="funcOrTool.includes('answerSource')">
            <config-group
              :disabled="true"
              :label="$t('answerTraceability')"
              :showTip="false"
              :icon="require('@/assets/images/appManagement/icon5.svg')"
              tip="将在回答中附带知识来源文档和生成内容的归属部分"
              style="border-radius: 8px;"
            >
            <template #button>
              <div class="flex-center config-group-button">
                <iconpark-icon name="settings-4-line" size="16" style="margin-left:12px;cursor: pointer;" color="#1d2129" @click="setAnswerSourceVisible=true"></iconpark-icon>
              </div>
            </template>
            </config-group>
          </div>
          <!-- <div class="dialog" v-if="funcOrTool.includes('DisableIP')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon6.svg')"
                />
                <span class="boxName">{{ $t("disableIP") }}</span>
              </div>
            </div>
            <div style="color: #828894; margin: 12px 0px 0px 32px">
              {{ $t("allowRestrictedAccessToIP") }}
            </div>
          </div> -->
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
            <div class="marginTop12 flex-center just">
              <div style="width: 48.5%">
                <div class="formTitle">
                  <span class="widthSpan">{{
                    $t("h5AuthenticationMethod")
                  }}</span>
                </div>
                <el-select
                  :placeholder="$t('pleaseSelect')"
                  v-model="appForm.clientAuthChannel"
                  clearable
                  size="small"
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

              <div style="width: 48.5%">
                <div class="formTitle">
                  <span class="widthSpan">{{
                    $t("pcAuthenticationChannel")
                  }}</span>
                </div>
                <el-select
                  :placeholder="$t('pleaseSelect')"
                  v-model="appForm.pcAuthChannel"
                  clearable
                  size="small"
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

            <div class="marginTop12">
              <el-switch
                  :active-text="$t('needTransferToHuman')"
                  v-model="appForm.toHumanFlag"
                  :active-value="$t('yes')"
                  :inactive-value="$t('no')"
              ></el-switch>
            </div>
          </div>
          <!-- <div class="dialog" v-if="funcOrTool.includes('TouchAnswer')">
            <div class="flex-center just">
              <div class="flex-center">
                <img
                  class="func-iconImg"
                  :src="require('@/assets/images/appManagement/icon10.svg')"
                />
                <span class="boxName">{{ $t("answerPolishing") }}</span>
              </div>
            </div>
            <div class="marginTop12 flex-center">
              <el-input
                :placeholder="detailedSettings"
                v-model="appForm.polishPrompt"
                show-word-limit
                type="textarea"
                :autosize="{ minRows: 6, maxRows: 12 }"
                maxlength="2000"
              ></el-input>
            </div>
          </div> -->
          <!-- 问题建议 -->
			    <div class="dialog" v-if="funcOrTool.includes('recommendation')">
            <config-group
              :disabled="true"
              label="问题建议"
              :icon="require('@/assets/images/appManagement/icon2.svg')"
              tip="在输入问题时提供最多 3 条用户提问建议"
              :showTip="false"
              style="border-radius:8px;"
            >
            <template #button>
              <div class="flex-center config-group-button">
                <iconpark-icon name="settings-4-line" size="16" style="margin-left:12px;cursor: pointer;" color="#1d2129" @click="setQuestionImagineVisible=true"></iconpark-icon>
              </div>
            </template>
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
              style="border-radius:8px;"
            >
            <template #button>
              <div class="flex-center config-group-button">
                <iconpark-icon name="settings-4-line" size="16" style="margin-left:12px;cursor: pointer;" color="#1d2129" @click="setAssociationVisible=true"></iconpark-icon>
              </div>
            </template>
              </config-group>
          </div>
          <!-- 输出结果评价 -->
          <div class="dialog" v-if="funcOrTool.includes('feedback')">
            <config-group
              :disabled="true"
              label="输出结果评价"
              :icon="require('@/assets/images/appManagement/icon12.svg')"
              tip="允许用户对输出结果进行评价反馈"
              style="border-radius: 8px;"
            >
            </config-group>
          </div>
          <!-- 快捷指令 -->
          <div class="dialog" v-if="funcOrTool.includes('quickCommand')">
              <config-group
                style="border-radius: 8px;"
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
          <!-- v-if="funcOrTool.includes('DisableIP')" -->
          <div class="dialog" v-if="funcOrTool.includes('DisableIP')">
            <config-group
              :disabled="true"
              :label="'禁用IP'"
              :icon="require('@/assets/images/appManagement/icon6.svg')"
              tip="应用将检测用户IP地址，多次恶意问答的IP地址会被限制访问"
              style="border-radius: 8px;"
            >
              <!-- <div style="color: #828894; margin: 5px 0px 0px 20px">
                {{ $t("allowRestrictedAccessToIP") }}
              </div> -->
            </config-group>
          </div>
        </div>
        <!-- <div class="marginTop24">
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
                  style="color: #1c50fd; font-size: 14px"
                  @click="openDialog('addSensitive')"
                  size="small"
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
            <div class="marginTop12">
              <div class="formTitle">
                <span class="widthSpan">{{ $t("searchEngine") }}</span>
              </div>
              <el-select
                :placeholder="$t('pleaseSelect')"
                v-model="appForm.networkChannel"
                style="width: 100%"
                size="small"
              >
              <el-option v-for="item in networkChannelList" :label="item.label" :value="item.value" :key="item.id"></el-option>
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
                  style="color: #1c50fd; font-size: 14px"
                  @click="openDialog('sceneSetting')"
                  size="small"
                  >{{ $t("settings") }}</el-button
                >
              </div>
            </div>
          </div>
        </div> -->
          </div>
          
      </div>
      <!-- <div class="bottom-btn">
        <el-button
          type="text"
          icon="el-icon-circle-plus"
          style="color: #1c50fd"
          @click="openDialog('tool')"
          size="small"
          >添加功能</el-button
        >
      </div> -->
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
      :getconfigFuntionListFlag="true"
      :params="appForm"
      @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"
      @changeStatus="changeStatus"
      @addPluginDataEmit="addPluginDataEmit"
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
    <!-- <el-dialog
      title=""
      :visible.sync="defaultDialogVisible"
      width="400px"
      class="defaultDialo autoFillTips"
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
    </el-dialog> -->
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
      class="defaultDialo autoFillTips"
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
      <div class="marginTop12 flex-center">
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
    <!-- 创建快捷指令 -->
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
              @change="handleSpeedChange" :min="0" :max="10"
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
    <!-- 免责声明 -->
    <wangeditor
      ref="wangeditor"
      v-model="wangeditorVisible"
      @close="closeWangeditorDialog"
      :statement="appForm.disclaimer"
      title="免责声明"
      @editStatementValue="editStatementValue"
    ></wangeditor>
  </div>
</template>

<script>
import {
  addApplication,
  modelList,
  ttsList,
  pageQueryVoice,
  defaultApp,
  getQuestionByAI,
  getAuthChannels,
  applicationPluginDataList,
  getDictItemByType,
  applicationPluginList,
  addApplicationPluginData,
} from "@/api/app";
import knowledgeBaseSet from "@/views/appManage/components/knowledgeBaseSet.vue";
import addKnowledgeBase from "@/views/appManage/components/addKnowledgeBase.vue";
import addFunctionOrTool from "@/views/appManage/components/addFunctionOrTool.vue";
import publishSuccess from "@/views/appManage/components/publishSuccess.vue";
import orderStep from "@/views/appManage/components/orderStep.vue";
import showSetting from "@/views/appManage/components/showSetting.vue";
import publishWay from "@/views/appManage/components/publishWay";
import createApplication from "@/views/appManage/components/createApplication";
import { getKnowledgeInfoList } from "@/api/index";
import addSensitiveDialog from "@/views/appManage/components/addSensitiveDialog.vue";
  import ConfigGroup from "@/views/appManage/components/ConfigGroup.vue";
  import wangeditor from "@/views/appManage/components/wangeditor";
// vuex
import { mapMutations } from "vuex";
import { debounce } from 'lodash';

export default {
  components: {
    knowledgeBaseSet,
    addKnowledgeBase,
    addFunctionOrTool,
    orderStep,
    showSetting,
    publishWay,
    publishSuccess,
    createApplication,
    addSensitiveDialog,
    ConfigGroup,
    wangeditor
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
        type:null,
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
        applicationQuickCommandList:[],
        refIndexFlag:"否"
      },
      uploadBtnLogo: false,
      fileListLogo: [],
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      rules: {
        applicationName: [
          {
            required: true,
            message: this.$t("pleaseEnterActivityName"),
            trigger: "blur",
          },
        ],
        introduce: [
          {
            required: true,
            message: this.$t("pleaseSelectActivityArea"),
            trigger: "blur",
          },
        ],
      },
      modelCheckName: [],
      setBaseVisible: false, // 设置知识库参数
      addBaseVisible: false, // 新增知识库
      konwlwdgeList: [], //知识库数据
      knowledgeIdArr: [],
      networkChannelList: [], // 关联敏感词库
      toolVisible: false, // 功能工具添加
      orderStepVisible: false, //执行顺序步骤
      showSettingVisible: false, // 暂时设置
      clOptions: [
        {
          label: this.$t("matchQuestion"),
          value: "qa-question",
        },
        {
          label: this.$t("matchAnswer"),
          value: "qa-content",
        },
        {
          label: this.$t("matchKnowledgeBase"),
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
      selectIds: [],
      pluginList: [],
      iconOptions: [
        {
          name: "对话体验",
          icon: "gongneng-duihuatiyan",
        },
        {
          name: "安全拦截",
          icon: "anquanlanjie",
        },
        {
          name: "问题建议",
          icon: "gongneng-tuijianwenti",
        },
        {
          name: "语音对话",
          icon: "gongneng-yuyinshezhi",
        },
        {
          name: "答案溯源",
          icon: "gongneng-daansuyuan",
        },
        {
          name: "答案润色",
          icon: "daanrunse",
        },
        {
          name: "输出结果评价",
          icon: "gongneng-shuchujieguopingfen",
        },
        {
          name: "快捷指令",
          icon: "gongneng-kuaijiezhiling",
        },
        {
          name: "问题联想",
          icon: "gongneng-wentilianxiang",
        },
        {
          name: "模糊问题引导",
          icon: "gongneng-mohuwentiyindao",
        },
        {
          name:"场景设置",
          icon:"gongneng-changjing"
        },
        {
          name:"检索引擎",
          icon:"gongneng-lianwangsousuo"
        },
        {
          name:"禁用IP",
          icon:"gongneng-jinyongIP"
        },
        {
          name:"问数检索",
          icon:"gongneng-wenshujiansuo"
        }
      ],
      funtionList:[],
      toolList:[],
      listAllData: [],
      getconfigFuntionListFlag:true,
      useLessNameList:["虚拟人","安全拦截","检索引擎","答案润色","场景设置","问数检索","模糊问题引导"],
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
      // 答案溯源
      setAnswerSourceVisible:false,
      // 快捷指令
      dialogVisibleApplicationFlag: false,
		  applicationQuickCommandObj:{
			  commandContent:'',
			  commandName:'',
			  commandType:'command'
		  },
		  applicationQuickCommandList:[],
      // 对话体验
      autoFillVisible: false,
      // 语音设置
      isVoice: false,
      isSound:false,
      visibleVoice:false,
      voiceList: [],
      voiceTitle: '请选择发音',
      voiceTitleId:null,
      selectedIndex:-1,
      curTtsObj:{},
      wangeditorVisible:false
    };
  },
  watch: {
    appForm: {
      handler(n) {
        let obj={...n}
        obj.ttsId=this.isSound?obj.ttsId:null;
        obj.voiceBroadcastConfig=this.isSound? JSON.stringify(this.curTtsObj):null;
        obj.pitch=this.isSound?obj.pitch:null;
        obj.voiceSpeed=this.isSound?obj.voiceSpeed:null;
        obj.streamVoice=this.isSound?obj.streamVoice:null;
        obj.sttId=this.isVoice?obj.sttId:null;
        console.log(obj,"obj");
        
        this.setFuncData(obj);
      },
      deep: true,
    },
  },
  props: {
    appConfigForm: Object,
  },
  mounted() {
    this.getTtsId(); //语音列表
    this.knowledgeList(); // 知识库
    this.getH5AuthChannelList();
    this.getPcAuthChannelList();
    this.initNetworkChannel();
    this.defaultAppInfoSet(this.appConfigForm);
    this.getfuntionList();
  },
  methods: {
    getIcon(name) {
      // console.log(
      //   this.iconOptions.find((item) => {
      //     return item.name === name;
      //   }).icon
      // );
      const obj=this.iconOptions.find((item) => {
        return item.name === name;
      })
      return obj?obj.icon:'gongneng-duihuatiyan';
    },
    ...mapMutations(["setFuncData", "setFuncPluginList"]),
    sendMessage() {
      const iframe = this.$refs.iframe;
      if (iframe) {
        const iframeWindow = iframe.contentWindow;
        // 现在可以安全地使用iframeWindow对象了
        iframeWindow.postMessage({ param1: "测试传入子组件的参数" }, "*");
      }
    },
    initNetworkChannel() {
      this.networkChannelList = [];
      getDictItemByType({param: '互联网搜索引擎'})
          .then((res) => {
            if (res.code === "000000") {
              let datas = res?.data || [];
              if (datas) {
                // this.networkChannelList
                // 将datas数组中的对象转为另外一个对象
                this.networkChannelList = datas.map(item => {
                  return {
                    id: item.itemId,
                    label: item.itemLabel,
                    value: item?.itemName
                  }
                });
              }
            } else {
              this.networkChannelList = [];
            }
          });
    },
    closeDialog() {
      this.$emit("configCloseDialog", false);
    },
    defaultAppInfoSet(item) {
      console.log(item,"item");
      
      this.knowledgeIdArr = item.knowledgeIds || [];
      this.uploadBtnLogo = item.logo ? true : false;
      this.applicationQuickCommandList=item.applicationQuickCommandList || []
      this.voiceBroadcastConfig =JSON.parse(item.voiceBroadcastConfig)
      this.curTtsObj={...this.voiceBroadcastConfig}
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
          console.log(voiceObj,"voiceObj");
          
          this.voiceTitle= voiceObj?voiceObj.fullStyle:"请选择发音"
          this.selectedIndex=this.voiceList.findIndex(ele=>ele.id==this.voiceTitleId)
          console.log(this.selectedIndex,'index');
          
        }
      })
        }
      this.appForm = {
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
        refIndexFlag:item.refIndexFlag ,
        voiceTitleId:this.voiceTitleId,
        virtualHumanFlag: ["是", true, "true"].includes(item.virtualHumanFlag)
          ? true
          : false,
        voiceDialogueFlag: ["是", true, "true"].includes(item.voiceDialogueFlag)
          ? true
          : false,
        modelAnswerFlag: ["是", true, "true"].includes(item.modelAnswerFlag)
          ? true
          : false,
        sensitiveFlag: ["是", true, "true"].includes(item.sensitiveFlag)
          ? true
          : false,
        networkFlag: ["是", true, "true"].includes(item.networkFlag)
          ? true
          : false,
        ipFlag: ["是", true, "true"].includes(item.ipFlag) ? true : false,
        ocrFlag: ["是", true, "true"].includes(item.ocrFlag) ? true : false,
        videoFlag: ["是", true, "true"].includes(item.videoFlag) ? true : false,
        multiDialogueFlag: ["是", true, "true"].includes(item.multiDialogueFlag)
          ? true
          : false,
        knowledgeFlag: ["是", true, "true"].includes(item.knowledgeFlag)
          ? true
          : false,
        sourceShowFlag: item.sourceShowFlag,
        recommendQuestionsShowFlag: item.recommendQuestionsShowFlag,
        rewritingFlag: ["是", true, "true"].includes(item.rewritingFlag)
          ? true
          : false,
        subjectFlag: item.subjectFlag,
        presetQuestionList:
          item.presetQuestionList && item.presetQuestionList.length > 0
            ? item.presetQuestionList
            : [""],
        knowledgeIds: item.knowledgeIds,
        applicationQuickCommandList:item.applicationQuickCommandList || [],
        networkChannel: item.networkChannel,
        polishFlag: ["是", true, "true"].includes(item.polishFlag)
          ? true
          : false,
        polishPrompt: item.polishPrompt,
        backgroundImageUrl: item.backgroundImageUrl,
        facadeImageUrl: item.facadeImageUrl,
        type: item.type,
        pitch:item.pitch,
        voiceSpeed:item.voiceSpeed,
        streamVoice:item.streamVoice
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
          this.pluginList = res.data || [];
          if (res.data.length) {
            res.data.forEach((element) => {
              if (element.status === "是") {
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
      }else if (type == "editName") {
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
    changeFunc(item){
      if(item.status=="是"){
        this.funcOrTool.push(item.pluginCode)
      }else{
        const index=this.funcOrTool.findIndex(ele=>ele==item.pluginCode)
        this.funcOrTool.splice(index,1)
      }
      console.log(this.funtionList);
      this.setFuncPluginList(this.funtionList)
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
        this.appForm.templateId = data.templateId;
        this.appForm.greeting = data.greeting;
        this.appForm.inputPlaceholder = data.inputPlaceholder;

        this.showSettingVisible = false;
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
        // this.changeApplicationStatus(true);
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
    addPluginDataEmit(selectIds) {
      this.selectIds = selectIds;
      this.pluginList = selectIds;
      this.toolVisible = false;
      this.funcOrTool = [];
      selectIds.forEach((element) => {
        if (element.status === "是") {
          this.funcOrTool.push(element.pluginCode);
        }
      });
      this.clearDefault();
      this.setFuncPluginList(selectIds)
      // if (!this.funcOrTool.includes("voice")) {
      //   this.appForm.ttsId = null;
      //   this.appForm.sttId = null;
      //   // this.appForm.voiceDialogueFlag = false;
      // }
    },
     // 查询列表
     getfuntionList() {
      // this.loading = true;
      applicationPluginList({
        applicationId: this.appForm.applicationId,
      }).then((res) => {
        if (res.code == "000000") {
          this.listAllData = res.data;
          this.getconfigFuntionListFlag && this.getconfigFuntionList();
        } 
      })
    },
    // 查询列表应用插件关联表
    getconfigFuntionList() {
      applicationPluginDataList({
        applicationId: this.appForm.applicationId,
      }).then((res) => {
        if (res.code == "000000") {
          this.funtionList = [];
          this.toolList = [];
          if (res.data.length) {
            let arr = res.data;
            this.listAllData.forEach((element) => {
              const matchingObject = arr.find(
                (item) => item.pluginId === element.pluginId
              );
              if (matchingObject) {
                element.status = matchingObject.status;
                element.configExtend=matchingObject.configExtend
              } else {
                element.status = '否';
              }
            });
          }
          this.listAllData.forEach((element) => {
            // element.status = element.status ? true : false;
            if (element.pluginGroup == "插件") {
              this.toolList.push(element);
            } else {
              this.funtionList.push(element);
            }
          });
          this.loading = false;
        }
      });
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
    confirmVagueQuestion(code){
      if(code=="vagueQuestion"){
        this.funtionList.map(item=>{
          if(item.pluginCode=="vagueQuestion"){
            item.configExtend=this.configExtend
          }
        })
        this.setVagueQuestionVisible=false
      }else if(code=="recommendation"){
        this.funtionList.map(item=>{
          if(item.pluginCode=="recommendation"){
            item.configExtend=this.quesImagineForm
          }
        })
        this.setQuestionImagineVisible=false
      }else if(code=="SearchEngine"){
        this.funtionList.map(item=>{
          if(item.pluginCode=="SearchEngine"){
            item.configExtend=this.functionForm.SearchEngine
          }
        })
        this.setSearchEngineVisible=false
      }else if(code == "association"){
        this.funtionList.map(item=>{
          if(item.pluginCode=="association"){
            item.configExtend=this.functionForm.association
          }
        })
        this.setAssociationVisible=false
      }
      console.log(this.funtionList);
      
      this.setFuncPluginList(this.funtionList)
      // this.clearVagueQuestion()
    },
    cancelTemplate(){
      this.dialogVisibleApplicationFlag = false
    },
    confirmTemplate(){
      console.log(this.appForm.applicationQuickCommandList);
      
		  // this.applicationQuickCommandList.push(JSON.parse(JSON.stringify(this.applicationQuickCommandObj)))
      this.appForm.applicationQuickCommandList.push(JSON.parse(JSON.stringify(this.applicationQuickCommandObj)))
      console.log(this.appForm.applicationQuickCommandList);
		  this.dialogVisibleApplicationFlag = false
    },
    deleteApplicationQuickCommandList(index) {
      this.appForm.applicationQuickCommandList.splice(index, 1);
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
      this.curTtsObj={...item}
      this.appForm.ttsId=item.voiceId
      this.appForm.voiceBroadcastConfig=JSON.stringify(item)
      this.appForm.voiceTitleId=item.id
    },
    handleSliderChange(value) {
      this.appForm.voiceSpeed = value;
    },
    handleSpeedChange(value) {
      this.appForm.voiceSpeed = value;
    },
    handlePitchChange(value) {
      this.appForm.pitch = value;
    },
    handlePitchiInputChange(value) {
      this.appForm.pitch = value;
    },
    changeVoiceStatus(){
      let obj={...this.appForm}
        obj.ttsId=this.isSound?obj.ttsId:null;
        obj.voiceBroadcastConfig=this.isSound? JSON.stringify(this.curTtsObj):null;
        obj.pitch=this.isSound?obj.pitch:null;
        obj.voiceSpeed=this.isSound?obj.voiceSpeed:null;
        obj.streamVoice=this.isSound?obj.streamVoice:null;
        obj.sttId=this.isVoice?obj.sttId:null;
        console.log(obj);
        
        this.setFuncData(obj);
    },
    // 打开个人信息收集声明弹框
    openEditorDialog() {
      console.log(66);
      
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
      }
      this.wangeditorVisible = false;
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-switch__core {
  width: 32px !important;
  &::after {
    width: 14px;
    height: 14px;
    top: 2px;
  }
}
.largeModel {
  width: 100%;
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

.setting-dia {
  display: flex;
  // height: 600px;
  overflow: auto;
  position: relative;
  height: calc(100% - 18px);

  .setting-close{
    width: 32px;
    height: 32px;
    position: absolute;
    top: 16px;
    right: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 6666;
    cursor: pointer;
  }

  .function-ctn{
    width: 480px;
    height: 100%;
    border-right: 1px solid #C9CDD4;

    .function-list-ctn{
      width: 100%;
      padding: 0 16px;
      height: calc(100% - 64px);
      box-sizing: border-box;
      overflow-y: auto;
      &::-webkit-scrollbar {
        display: none !important; /* 隐藏滚动条 */
      }


      .function-list{
        width: 100%;
        height: 76px;
        background: #FFFFFF;
        border-radius: 8px;
        border: 1px solid #C9CDD4;
        padding: 12px;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        position: relative;
        margin-bottom: 12px;

        .function-msg{
          height: 24px;
          display: flex;
          gap: 8px;
          
          .icon-img{
            width: 24px;
            height: 24px;
            border-radius: 4px;
            overflow: hidden;
          }

          .function-title{
            height: 24px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 16px;
            color: #1D2129;
            line-height: 24px;
          }
        }

        .function-intro{
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #86909C;
          line-height: 20px;
          width: 100%;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .function-switch{
          width: 24px;
          height: 16px;
          position: absolute;
          top: 12px;
          right: 12px;

          ::v-deep .el-switch__core{
            width: 24px !important;
            height: 16px;

            &::after{
              width: 10px;
              height: 10px;
              // margin-left: -11px;
            }
          }
          ::v-deep .el-switch.is-checked .el-switch__core::after {
              left: 100%;
              margin-left: -11px;
          }
        }
      }

    }
    
  }
  .setting-dia-title{
    width: 100%;
    height: 64px;
    padding: 16px;
    box-sizing: border-box;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #1D2129;
    line-height: 32px;
  }
  .btns {
    position: absolute;
  }
  .dialogPower {
    width: 640px;
    background: #ffffff;
    // padding: 0 32px 40px 32px;
    position: relative;
    // border-left: 1px solid #C9CDD4;
    box-shadow: 1px 1px 4px 0px rgba(21,34,51,0.1);
    border-radius: 0px 12px 12px 0px;

    .dialogPower-list-ctn{
      width: 100%;
      padding: 0 16px;
      box-sizing: border-box;
      height: calc(100% - 64px);
      overflow-y: auto;
    }

    // .left-content {
    //   overflow-y: auto;
    //   height: calc(100% - 64px);
    // }
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
        padding: 10px 20px;
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

.marginTop24 {
  margin-top: 24px;
  .widthSpan {
    display: inline-block;
    color: #768094 !important;
  }
}
.formTitle {
  font-family: MiSans, MiSans;
  font-weight: 500;
  font-size: 14px;
  color: #383d47;
  line-height: 18px;
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
    width: max-content;
    display: inline-block;
    text-align: right;
    padding-right: 4px;
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
  width: calc(100%);
  height: 32px;
  border-radius: 4px;
  border: 1px solid #3666ea;
  text-align: center;
  margin-top: 16px;
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
    // border-radius: 8px;
    // border: 1px solid #E5E6EA;
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

  .experience{
    ::v-deep .el-input__inner{
      background-color: #F7F8FA ;
      border: none !important;
    }

    .init-ques{
      //  ::v-deep .el-input__inner{
      //   border: 1px solid #C9CDD4 !important;
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
        // border: none;
        outline: none;
      }
    }
    ::v-deep .el-input__count-inner{
      background-color: #F7F8FA ;
    }
  }
.marginTop12 {
  margin-top: 12px;
}
.boxName {
  font-family: MiSans, MiSans;
  font-weight: 500;
  font-size: 16px;
  color: #383d47;
  line-height: 20px;
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
    height: 100%;
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
      text-align: right;
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
.autoFillTips {
  ::v-deep .el-dialog {
    height: auto;
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

.add-ques {
  justify-content: center;
  height: 40px;
  border-radius: 4px;
  border: 1px solid #E7E7E7;
  font-size: 14px;
  color: #494E57;
  padding: 0 12px;
  box-sizing: border-box;
  gap: 8px;
  width: 100%;

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
      max-width: 180px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .intonation {
      color: #86909c;
    }
  }
}

.marginTop10{
  margin-top: 10px;
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
</style>
  