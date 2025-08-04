<template>
  <div class="outer" style="padding: 24px">
    <p class="outerTitle">{{$t('qaAssistant')}}</p>
    <div class="appOuter">
      <div class="headerContent">
        <el-input
          :placeholder="$t('inputAppName')"
          class="input"
          v-model="applicationName"
          clearable
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="getAppList"
          ></el-button>
        </el-input>
        <el-button type="primary" v-permission="'createApp'" @click="createApp">
          <img src="@/assets/images/add-circle-fill.svg" />
          <span>{{$t('createQaAssistant')}}</span>
        </el-button>
      </div>
      <div class="listOuter">
        <div v-for="item in appList" :key="item.applicationId" class="listItem">
          <span
            class="sign"
            :class="`${
              item.publishStatus == 1 || item.publishStatus == 2
                ? 'successSign'
                : 'failSign'
            }`"
          >
            <img
              src="@/assets/images/checkbox-circle-line.svg"
              v-if="item.publishStatus == 1 || item.publishStatus == 2"
            />
            <img src="@/assets/images/hourglass-line.svg" v-else />
            <span>{{ statusToSrt(item.publishStatus) }}</span>
          </span>
          <div class="listContent">
            <span
              style="
                width: 160px;
                height: 160px;
                background: #dcdfe6;
                border-radius: 8px 0px 0px 8px;
                display: flex;
                align-items: center;
                justify-content: center;
              "
            >
              <img :src="item.logo" class="headImg" />
            </span>
            <div class="textContent">
              <p class="title">{{ item.applicationName }}</p>
              <p class="desc">{{ item.introduce }}</p>
              <div class="date">
                <span v-if="item.periodEnd"
                  >{{$t('validityPeriod')}}{{ item.periodStart }}{{$t('to')}}{{ item.periodEnd }}</span
                >
                <span class="period-box" v-else>
                  <span class="point"></span>
                  <span>{{$t('longTermValid')}}</span>
                </span>
                <div
                  style="display: flex; align-items: center; cursor: pointer"
                  v-permission="'preview'"
                >
                  <div @click="previewApp(item)">
                    <img
                      style="height: 13px; margin-right: 6px"
                      src="@/assets/images/chat-1-line.svg"
                    />
                    <span
                      style="
                        font-size: 16px;
                        color: #1c50fd;
                        margin-right: 12px;
                      "
                      >{{$t('view')}}</span
                    >
                  </div>
                  <div
                    style="
                      width: 28px;
                      height: 28px;
                      background: #e8f0fc;
                      border-radius: 4px;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                    "
                  >
                    <el-dropdown
                      trigger="click"
                      @command="(value) => handleCommand(value, item)"
                    >
                      <span class="el-dropdown-link">
                        <i
                          style="transform: rotate(90deg); color: #1c50fd"
                          class="el-icon-more"
                        ></i>
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item
                          v-permission="'copyApp'"
                          command="copyApp"
                        >
                          <img
                            style="height: 15px; margin-right: 8px"
                            src="@/assets/images/file-copy-line.svg"
                          />
                          <span>{{$t('copy')}}</span>
                        </el-dropdown-item>
                        <el-dropdown-item
                          v-permission="'editeApp'"
                          command="editeApp"
                        >
                          <img
                            style="height: 15px; margin-right: 8px"
                            src="@/assets/images/edit-line.svg"
                          />
                          <span>{{$t('edit')}}</span>
                        </el-dropdown-item>
                        <el-dropdown-item
                          v-permission="'deleteApp'"
                          command="deleteApp"
                        >
                          <img
                            style="height: 15px; margin-right: 8px"
                            src="@/assets/images/delete-bin-4-line.svg"
                          />
                          <span>{{$t('delete')}}</span>
                        </el-dropdown-item>
                        <el-dropdown-item
                          v-permission="'grantApp'"
                          command="grantApp"
                        >
                          <img
                            style="height: 15px; margin-right: 8px"
                            src="@/assets/images/delete-bin-4-line.svg"
                          />
                          <span>{{$t('authorize')}}</span>
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-dialog
      :visible.sync="dialogVisible"
      width="100%"
      fullscreen
      :show-close="false"
      class="flexDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="headBar">
        <div class="leftSlide">
          <img
            src="@/assets/images/arrow-go-back-fill.svg"
            @click="closeDialog"
          />
          <div class="titleIcon">
            <p>{{ appForm.applicationName || $t('noAppName') }}</p>
            <p>{{ appForm.introduce || $t('noDescription') }}</p>
          </div>
        </div>
        <div class="rightSlide">
          <div class="preview" @click="getDefaultParameters">
            <span>
              {{$t('fillDefaultParams')}}
            </span>
          </div>
          <div class="preview" @click="temporarSave(1)">
            <img src="@/assets/images/mac-line.svg" />
            <span>{{$t('viewRenderedEffect')}}</span>
          </div>
          <el-button class="btn" @click="temporarSave(2)">
            <img src="@/assets/images/save-line.svg" />
            <span>{{$t('saveDraft')}}</span>
          </el-button>
          <el-button class="btns" v-permission="'issue'" @click="fabuDialog">
            <img src="@/assets/images/send-plane-fill.svg" />
            <span>{{$t('publish')}}</span>
          </el-button>
        </div>
      </div>
      <div
        style="
          position: relative;
          background: #fff;
          padding: 0 32px 32px;
          display: flex;
          justify-content: flex-start;
          box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.1);
          border-radius: 4px;
        "
      >
        <div style="position: relative">
          <div class="verticalLine"></div>
          <div class="dialogTitle">
            <img src="@/assets/images/contacts-fill.svg" />
            <span>{{$t('basicInfo')}}</span>
          </div>
          <div class="formOuter">
            <div class="uploadImg">
              <div class="uploadOuter">
                <el-upload
                  :action="actionUrl"
                  :data="{ filePath: 'agent_source' }"
                  :class="{ hideBox: uploadBtnLogo }"
                  :file-list="fileListLogo"
                  :limit="1"
                  class="logoUpload"
                  list-type="picture-card"
                  :on-remove="handleLogoRemove"
                  :on-success="handleLogoSuccess"
                >
                  <div>
                    <img
                      src="@/assets/images/add-line.svg"
                      style="width: 15px; height: 15px; vertical-align: middle"
                    />
                    <span
                      style="
                        font-size: 16px;
                        color: #b4bccc;
                        vertical-align: middle;
                      "
                      >{{$t('uploadImage')}}</span
                    >
                  </div>
                </el-upload>
              </div>
              <div>
                <el-input
                  v-model="appForm.applicationName"
                  show-word-limit
                  maxlength="50"
                />
                <el-input
                  type="textarea"
                  :rows="7"
                  v-model="appForm.introduce"
                  show-word-limit
                  maxlength="2000"
                />
              </div>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('identityIcon')}}</span>
                <img src="@/assets/images/question-line.svg" />
              </div>
              <el-upload
                :action="actionUrl"
                :data="{ filePath: 'agent_source' }"
                :class="{ hideBox: uploadBtnIdentityIcon }"
                :file-list="fileListIdentityIcon"
                :limit="1"
                class="identityUpload"
                list-type="picture-card"
                :on-remove="handleIdentityIconRemove"
                :on-success="handleIdentityIconSuccess"
              >
                <div>
                  <img
                    src="@/assets/images/add-line.svg"
                    style="width: 15px; height: 15px; vertical-align: middle"
                  />
                  <span
                    style="
                      font-size: 16px;
                      color: #b4bccc;
                      vertical-align: middle;
                    "
                    >{{$t('uploadImage')}}</span
                  >
                </div>
              </el-upload>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('welcomeMessage')}}</span>
                <img src="@/assets/images/question-line.svg" />
              </div>
              <el-input
                :placeholder="$t('pleaseEnterContent')"
                v-model="appForm.greeting"
              ></el-input>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('inputBoxPrompt')}}</span>
                <img src="@/assets/images/question-line.svg" />
              </div>
              <el-input
                :placeholder="$t('pleaseEnterContent')"
                v-model="appForm.inputPlaceholder"
              ></el-input>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('openingRemarks')}}</span>
                <img src="@/assets/images/question-line.svg" />
              </div>
              <el-input
                :placeholder="$t('pleaseEnterContent')"
                v-model="appForm.prologue"
              ></el-input>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('presetQuestions')}}</span>
                <img src="@/assets/images/question-line.svg" />
              </div>
              <div
                style="margin-bottom: 8px"
                v-for="(item, index) in appForm.presetQuestionList"
                :key="'question' + index"
              >
                <el-input
                  :placeholder="$t('pleaseEnterContent')"
                  style="width: calc(100% - 78px)"
                  v-model="appForm.presetQuestionList[index]"
                ></el-input>
                <i
                  class="el-icon-circle-plus-outline"
                  @click="addQues"
                  style="
                    font-size: 30px;
                    position: relative;
                    top: 6px;
                    color: #b4bccc;
                    cursor: pointer;
                    margin: 0 6px;
                  "
                  v-if="index == 0"
                ></i>
                <i
                  class="el-icon-remove-outline"
                  @click="deleteQues(index)"
                  style="
                    font-size: 30px;
                    position: relative;
                    top: 6px;
                    color: #b4bccc;
                    cursor: pointer;
                    margin-left: 6px;
                  "
                ></i>
              </div>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('appCode')}}</span>
                <span style="color: #f00">*</span>
              </div>
              <el-input
                :placeholder="$t('pleaseEnterContent')"
                v-model="appForm.applicationCode"
                @input="validTextEn(appForm.applicationCode)"
              ></el-input>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('disclaimer')}}</span>
              </div>
              <el-input
                :placeholder="$t('pleaseEnterContent')"
                v-model="appForm.disclaimer"
              ></el-input>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('learningScript')}}</span>
              </div>
              <el-input
                :placeholder="$t('pleaseEnterContent')"
                v-model="appForm.tailTalk"
              ></el-input>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('webPageTitle')}}</span>
              </div>
              <el-input
                :placeholder="$t('pleaseEnterContent')"
                v-model="appForm.webTitle"
              ></el-input>
            </div>
            <div class="marginTop32">
              <div class="formTitle">
                <span>{{$t('otherSettings')}}</span>
              </div>
              <div class="formFlexOuter">
                <div class="formTitle">
                  <span style="color: #768094" class="widthSpan"
                    >{{$t('pleaseEnterContent')}}</span
                  >
                </div>
                <el-input
                  :placeholder="$t('yes')"
                  style="margin-left: 32px"
                  v-model="appForm.failureTalk"
                ></el-input>
              </div>
              <div class="formFlexOuter">
                <div class="formTitle">
                  <span class="widthSpan">{{$t('buttonStyle')}}</span>
                </div>
                <el-input
                  :placeholder="$t('pleaseEnterContent')"
                  style="margin-left: 32px"
                  v-model="appForm.buttonStyle"
                ></el-input>
              </div>
              <div class="formFlexOuter">
                <div class="formTitle">
                  <span class="widthSpan">{{$t('defaultButtonText')}}</span>
                </div>
                <el-input
                  :placeholder="$t('pleaseEnterContent')"
                  style="margin-left: 32px"
                  v-model="appForm.buttonText"
                ></el-input>
              </div>
              <div class="formFlexOuter">
                <div class="formTitle">
                  <span class="widthSpan">{{$t('searchEngine')}}</span>
                </div>
                <el-select
                  :placeholder="$t('pleaseSelect')"
                  v-model="appForm.networkChannel"
                  style="width: 100%; margin-left: 32px"
                >
                  <el-option :label="$t('baidu')" value="baidu"></el-option>
                  <el-option :label="$t('toutiao')" value="toutiao"></el-option>
                  <el-option :label="$t('bing')" value="bing"></el-option>
                  <el-option :label="$t('toutiaoImage')" value="toutiaoImage"></el-option>
                  <el-option :label="$t('quark')" value="quark"></el-option>
                </el-select>
              </div>
              <div class="formFlexOuter">
                <div class="formTitle">
                  <span class="widthSpan">{{$t('sequentialSteps')}}</span>
                </div>
                <el-select
                  :placeholder="$t('pleaseSelect')"
                  v-model="appForm.processStep"
                  multiple
                  style="width: 100%; margin-left: 32px"
                >
                  <el-option value="builtIn" :label="$t('builtInQuestions')"></el-option>
                  <el-option
                    value="interceptSensitive"
                    :label="$t('safetyInterception')"
                  ></el-option>
                  <el-option value="subjectTalk" :label="$t('discussionTopics')"></el-option>
                  <el-option
                    value="findQaTitle"
                    :label="$t('searchQaQuestion')"
                  ></el-option>
                  <el-option
                    value="findQaContent"
                    :label="$t('searchQaAnswer')"
                  ></el-option>
                  <el-option
                    value="finalCollectStrategy"
                    :label="$t('searchKnowledgeBase')"
                  ></el-option>
                  <el-option
                    value="findAnswerByModel"
                    :label="$t('largeModelDivergence')"
                  ></el-option>
                </el-select>
              </div>
              <div class="formFlexOuter">
                <el-switch
                  :active-text="$t('sensitiveWordInterceptionSwitch')"
                  v-model="appForm.sensitiveFlag"
                ></el-switch>
              </div>
              <div class="formFlexOuter">
                <el-switch
                  :active-text="$t('isOnlineSearch')"
                  v-model="appForm.networkFlag"
                ></el-switch>
              </div>
              <div class="formFlexOuter">
                <el-switch
                  :active-text="$t('isIpDisabled')"
                  v-model="appForm.ipFlag"
                ></el-switch>
              </div>
              <div class="formFlexOuter">
                <el-switch
                  :active-text="$t('isPolishingNeeded')"
                  v-model="appForm.polishFlag"
                ></el-switch>
              </div>
              <div class="formFlexOuter" v-if="appForm.polishFlag">
                <div class="formTitle">
                  <span class="widthSpan">{{$t('polishingPrompt')}}</span>
                </div>
                <el-input
                  :placeholder="$t('pleaseEnterContent')"
                  style="margin-left: 32px"
                  v-model="appForm.polishPrompt"
                ></el-input>
              </div>
              <div class="formFlexOuter">
                <el-switch
                  :active-text="$t('enableVirtualPerson')"
                  v-model="appForm.virtualHumanFlag"
                ></el-switch>
              </div>
              <div class="formFlexOuter" v-if="appForm.virtualHumanFlag">
                <div class="formTitle">
                  <span class="widthSpan">{{$t('virtualPersonId')}}</span>
                </div>
                <el-input
                  :placeholder="$t('pleaseEnterContent')"
                  style="margin-left: 32px"
                  v-model="appForm.virtualHumanId"
                ></el-input>
              </div>
              <div class="formFlexOuter">
                <el-switch
                  :active-text="$t('enableVoiceQa')"
                  v-model="appForm.voiceDialogueFlag"
                ></el-switch>
              </div>
              <div class="formFlexOuter" v-if="appForm.voiceDialogueFlag">
                <div class="formTitle">
                  <span class="widthSpan">{{$t('textToSpeech')}}</span>
                </div>
                <el-select
                  :placeholder="$t('pleaseSelect')"
                  v-model="appForm.ttsId"
                  style="width: 100%; margin-left: 32px"
                >
                  <el-option
                    v-for="item in ttsListData"
                    :key="item.id"
                    :value="item.id"
                    :label="item.componentName"
                  ></el-option>
                </el-select>
              </div>
              <div class="formFlexOuter" v-if="appForm.voiceDialogueFlag">
                <div class="formTitle">
                  <span class="widthSpan">{{$t('speechToText')}}</span>
                </div>
                <el-select
                  :placeholder="$t('pleaseSelect')"
                  v-model="appForm.sttId"
                  style="width: 100%; margin-left: 32px"
                >
                  <el-option
                    v-for="item in ttsListData"
                    :key="item.id"
                    :value="item.id"
                    :label="item.componentName"
                  ></el-option>
                </el-select>
              </div>
              <div class="formFlexOuter">
                <div class="formTitle" style="width: 113px">
                  <span style="color: #768094">{{$t('robotIcon')}}</span>
                </div>
                <el-upload
                  :action="actionUrl"
                  :data="{ filePath: 'agent_source' }"
                  list-type="picture-card"
                  :class="{ hideBox: uploadBtnRobot }"
                  :file-list="fileListRobot"
                  :limit="1"
                  class="robotImg"
                  :on-remove="handleRobotRemove"
                  :on-success="handleRobotSuccess"
                >
                  <i class="el-icon-plus"></i>
                </el-upload>
              </div>
              <div class="formFlexOuter">
                <div class="formTitle" style="width: 113px">
                  <span style="color: #768094">{{$t('webPageTagIcon')}}</span>
                </div>
                <el-upload
                  :action="actionUrl"
                  :data="{ filePath: 'agent_source' }"
                  list-type="picture-card"
                  :limit="1"
                  :class="{ hideBox: uploadBtnWebIcon }"
                  :file-list="fileListwebIcon"
                  class="robotImg"
                  :on-remove="handleWebRemove"
                  :on-success="handleWebSuccess"
                >
                  <i class="el-icon-plus"></i>
                </el-upload>
              </div>
              <div class="formFlexOuter">
                <div class="formTitle" style="width: 113px">
                  <span style="color: #768094">{{$t('userIcon')}}</span>
                </div>
                <el-upload
                  :action="actionUrl"
                  :data="{ filePath: 'agent_source' }"
                  list-type="picture-card"
                  :limit="1"
                  :class="{ hideBox: uploadBtnUserIcon }"
                  class="robotImg"
                  :file-list="fileListUserIcon"
                  :on-remove="handleUserRemove"
                  :on-success="handleUserSuccess"
                >
                  <i class="el-icon-plus"></i>
                </el-upload>
              </div>
            </div>
          </div>
        </div>
        <div style="padding: 0 30px 0; position: relative">
          <div class="verticalLine"></div>
          <div class="dialogTitle">
            <img src="@/assets/images/vip-crown-2-fill.svg" />
            <span>{{$t('abilitySettings')}}</span>
          </div>
          <div class="formOuter">
            <el-collapse class="collapseStyle" v-model="collapseActive">
              <el-collapse-item name="first">
                <template slot="title">
                  <div style="display: flex" class="collapseHeader">
                    <div>
                      <img
                        src="@/assets/images/arrow-right-s-fill.svg"
                        class="svgTransform"
                      />
                      <span>{{$t('associateLargeModel')}}</span>
                    </div>
                    <span
                      style="
                        font-size: 16px;
                        position: absolute;
                        right: 0px;
                        color: #3666ea;
                      "
                      >{{$t('howToChooseLargeModel')}}</span
                    >
                  </div>
                </template>
                <div class="formFlexOuter" style="margin-top: 8px">
                  <el-select
                    :placeholder="$t('pleaseSelect')"
                    style="width: 100%"
                    v-model="appForm.modelId"
                    class="assModel"
                  >
                    <el-option
                      v-for="item in modleOptions"
                      :key="item.modelId"
                      :label="item.modelName"
                      :value="item.modelId"
                    ></el-option>
                  </el-select>
                </div>
                <div class="formFlexOuter">
                  <el-switch
                    :active-text="$t('enableModelResponse')"
                    v-model="appForm.modelAnswerFlag"
                  ></el-switch>
                </div>
                <div class="formFlexOuter">
                  <el-switch
                    :active-text="$t('enableImageRecognition')"
                    v-model="appForm.ocrFlag"
                  ></el-switch>
                </div>
                <div class="formFlexOuter">
                  <el-switch
                    :active-text="$t('enableVideoRecognition')"
                    v-model="appForm.videoFlag"
                  ></el-switch>
                </div>
                <div class="formFlexOuter" style="position: relative">
                  <el-switch
                    :active-text="$t('enableMultiRoundResponse')"
                    v-model="appForm.multiDialogueFlag"
                  ></el-switch>
                  <div style="position: absolute; right: 0; top: -9px">
                    <span style="font-size: 16px; margin-right: 3px"
                      >{{$t('contextCarryRounds')}}</span
                    >
                    <el-input-number
                      style="width: 148px"
                      :min="1"
                      :max="10"
                      :label="$t('descriptionText')"
                      v-model="appForm.multiDialogueNum"
                    ></el-input-number>
                  </div>
                </div>
                <div class="formFlexOuter">
                  <el-switch
                    :active-text="$t('enableDocumentQa')"
                    v-model="appForm.knowledgeFlag"
                  ></el-switch>
                </div>
                <div class="formFlexOuter">
                  <el-switch
                    :active-text="$t('enableQuestionRewriting')"
                    v-model="appForm.rewritingFlag"
                  ></el-switch>
                </div>
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span style="color: #768094" class="widthSpan"
                      >{{$t('responseTimeout')}}</span
                    >
                  </div>
                  <el-input
                    :placeholder="$t('pleaseEnterContent')"
                    style="margin-left: 32px"
                    v-model="appForm.answerTimeout"
                    class="heightInput"
                  >
                    <el-button slot="append">{{$t('seconds')}}</el-button>
                  </el-input>
                </div>
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span style="color: #768094" class="widthSpan"
                      >{{$t('pleaseEnterContent')}}</span
                    >
                  </div>
                  <el-input
                    :placeholder="$t('yes')"
                    style="margin-left: 32px"
                    v-model="appForm.notAnswer"
                  ></el-input>
                </div>
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span style="color: #768094" class="widthSpan"
                      >{{$t('promptTemplate')}}</span
                    >
                  </div>
                  <el-input
                    :placeholder="$t('pleaseEnterContent')"
                    style="margin-left: 32px"
                    v-model="appForm.promptTemplate"
                  ></el-input>
                </div>
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span style="color: #768094" class="widthSpan"
                      >{{$t('discussionTopic')}}</span
                    >
                  </div>
                  <el-input
                    :placeholder="$t('pleaseEnterContent')"
                    style="margin-left: 32px"
                    v-model="appForm.subjectPrompt"
                  ></el-input>
                </div>
              </el-collapse-item>
              <el-collapse-item name="second">
                <template slot="title">
                  <div style="display: flex" class="collapseHeader">
                    <div>
                      <img
                        src="@/assets/images/arrow-right-s-fill.svg"
                        class="svgTransform"
                      />
                      <span>{{$t('associateKnowledgeBase')}}</span>
                    </div>
                    <div
                      @click.stop="openParamsConfig"
                      style="position: absolute; right: 0px"
                    >
                      <img
                        src="@/assets/images/equalizer-line.svg"
                        style="width: 15px; height: 15px"
                      />
                      <span style="color: #3666ea">{{$t('parameterSettings')}}</span>
                    </div>
                  </div>
                </template>
                <el-button
                  size="small"
                  style="
                    width: 283px;
                    height: 40px;
                    position: relative;
                    border-radius: 4px;
                  "
                  @click="assKnowledge"
                >
                  <span
                    style="
                      font-size: 20px;
                      position: absolute;
                      left: 70px;
                      top: 8px;
                    "
                    >+</span
                  >
                  <span style="font-size: 16px">{{$t('selectExistingKnowledgeBase')}}</span>
                </el-button>
                <div style="margin-top: 16px">
                  <div
                    v-for="item in appForm.knowledgeIds"
                    style="
                      display: flex;
                      justify-content: space-between;
                      align-items: center;
                      background: rgba(28, 80, 253, 0.05);
                      border-radius: 4px;
                      padding: 8px 15px;
                      margin-top: 5px;
                    "
                  >
                    <div style="display: flex; align-items: center">
                      <img
                        src="@/assets/images/bookmarketedfill.svg"
                        style="width: 15px; height: 15px; margin-right: 10px"
                      />
                      <span>{{ filterKnowledge(item) }}</span>
                    </div>
                    <img
                      src="@/assets/images/closeLine.svg"
                      style="width: 14px; height: 14px; cursor: pointer"
                      @click="deleteKnowledgeId(item)"
                    />
                  </div>
                </div>
              </el-collapse-item>
              <el-collapse-item name="third">
                <template slot="title">
                  <img
                    src="@/assets/images/arrow-right-s-fill.svg"
                    class="svgTransform"
                  />
                  <span
                    style="
                      color: #383d47;
                      font-size: 16px !important;
                      font-weight: 500;
                    "
                    >{{$t('addComponent')}}</span
                  >
                </template>
              </el-collapse-item>
              <el-collapse-item name="fourth">
                <template slot="title">
                  <img
                    src="@/assets/images/arrow-right-s-fill.svg"
                    class="svgTransform"
                  />
                  <span
                    style="
                      color: #383d47;
                      font-size: 16px !important;
                      font-weight: 500;
                    "
                    >{{$t('systemPromptSettings')}}</span
                  >
                </template>
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span class="widthSpan">SystemPrompt</span>
                  </div>
                  <el-input
                    :placeholder="$t('pleaseEnterContent')"
                    v-model="appForm.systemPrompt"
                  ></el-input>
                </div>
              </el-collapse-item>
              <el-collapse-item name="fifth">
                <template slot="title">
                  <img
                    src="@/assets/images/arrow-right-s-fill.svg"
                    class="svgTransform"
                  />
                  <span
                    style="
                      color: #383d47;
                      font-size: 16px !important;
                      font-weight: 500;
                    "
                    >{{$t('recommendationStrategySettings')}}</span
                  >
                </template>
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span class="widthSpan">{{$t('recommendationStrategy')}}</span>
                  </div>
                  <el-select
                    :placeholder="$t('pleaseSelect')"
                    v-model="appForm.recommendation"
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
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span class="widthSpan">{{$t('recommendedQuestionCount')}}</span>
                  </div>
                  <el-input-number
                    :min="1"
                    :max="10"
                    :label="$t('descriptionText2')"
                    v-model="appForm.recommendationNum"
                    style="width: 217px"
                  ></el-input-number>
                </div>
              </el-collapse-item>
              <el-collapse-item name="sixth">
                <template slot="title">
                  <img
                    src="@/assets/images/arrow-right-s-fill.svg"
                    class="svgTransform"
                  />
                  <span
                    style="
                      color: #383d47;
                      font-size: 16px !important;
                      font-weight: 500;
                    "
                    >{{$t('remarks')}}</span
                  >
                </template>
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span class="widthSpan">{{$t('remarks')}}</span>
                  </div>
                  <el-input
                    :placeholder="$t('pleaseEnterContent')"
                    v-model="appForm.remark"
                  ></el-input>
                </div>
              </el-collapse-item>
              <el-collapse-item name="seventh">
                <template slot="title">
                  <img
                    src="@/assets/images/arrow-right-s-fill.svg"
                    class="svgTransform"
                  />
                  <span
                    style="
                      color: #383d47;
                      font-size: 16px !important;
                      font-weight: 500;
                    "
                    >{{$t('authenticationStrategySettings')}}</span
                  >
                </template>
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span class="widthSpan">{{$t('h5AuthenticationMethod')}}</span>
                  </div>
                  <el-select
                    :placeholder="$t('pleaseSelect')"
                    v-model="appForm.clientAuthChannel"
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
                <div class="formFlexOuter">
                  <div class="formTitle">
                    <span class="widthSpan">{{$t('pcAuthenticationChannel')}}</span>
                  </div>
                  <el-select
                    :placeholder="$t('pleaseSelect')"
                    v-model="appForm.pcAuthChannel"
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
                <div class="formFlexOuter">
                  <el-switch
                    :active-text="$t('isManualTransferNeeded')"
                    v-model="appForm.toHumanFlag"
                  ></el-switch>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
        <div
          style="
            padding-left: 30px;
            position: relative;
            width: calc(100% - 1180px);
          "
        >
          <div class="dialogTitle">
            <img src="@/assets/images/contacts-fill2.svg" />
            <span>{{$t('previewAndDebug')}}</span>
          </div>
          <iframe
            ref="iframe"
            style="
              width: 100%;
              height: 100vh;
              margin-top: 24px;
              margin: 30px auto;
            "
            id="myiframe"
          ></iframe>
        </div>
      </div>
    </el-dialog>
    <el-dialog
      :title="$t('knowledgeBaseParameterSettings')"
      :visible.sync="dialogVisibles"
      width="800px"
      class="configDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="flexOuter">
        <span>{{$t('searchContentScoreThreshold')}}</span>
        <el-slider
          show-input
          v-model="appForms.contentScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <span></span>
      </div>
      <div class="flexOuter">
        <span>{{$t('reorderMainTextScoreThreshold')}}</span>
        <el-slider
          show-input
          v-model="appForms.rangeContentScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <span></span>
      </div>
      <div class="flexOuter">
        <span>{{$t('qaSearchTitleScoreThreshold')}}</span>
        <el-slider
          show-input
          v-model="appForms.qaTitleScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <span></span>
      </div>
      <div class="flexOuter">
        <span>{{$t('qaReorderTitleScoreThreshold')}}</span>
        <el-slider
          show-input
          v-model="appForms.qaRangeTitleScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <span></span>
      </div>
      <div class="flexOuter">
        <span>{{$t('qaSearchMainTextScoreThreshold')}}</span>
        <el-slider
          show-input
          v-model="appForms.qaContentScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <span></span>
      </div>
      <div class="flexOuter">
        <span>{{$t('qaReorderMainTextAnswerScoreThreshold')}}</span>
        <el-slider
          show-input
          v-model="appForms.qaRangeContentScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <span></span>
      </div>
      <div class="flexOuter">
        <span>{{$t('quotedKnowledgeBaseParagraphCount')}}</span>
        <el-slider
          show-input
          v-model="appForms.filterNum"
          :min="0"
          :max="10"
          :step="1"
        ></el-slider>
        <span></span>
      </div>
      <div class="flexOuter">
        <span>{{$t('knowledgeBaseParagraphPreparationCount')}}</span>
        <el-slider
          show-input
          v-model="appForms.prepareNum"
          :min="0"
          :max="100"
          :step="1"
        ></el-slider>
        <span></span>
      </div>
      <div class="footer">
        <el-button type="primary" @click="setConfig">{{$t('confirm')}}</el-button>
        <el-button @click="cancelConfig">{{$t('cancel')}}</el-button>
        <el-button type="primary" plain>
          <img
            src="@/assets/images/refresh-line.svg"
            style="width: 15px; height: 15px; margin-right: 5px"
          />
          <span @click="useDefaultValue">{{$t('useDefaultValue')}}</span>
        </el-button>
      </div>
    </el-dialog>
    <el-dialog
      :title="$t('deleteApplication')"
      :visible.sync="deleteDialogVisible"
      width="560px"
      class="deleteDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <p class="desc">{{$t('enterAppNameToDelete')}}</p>
      <el-input
        :placeholder="$t('yes')"
        v-model="deleteItemName"
      ></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmDelete">{{$t('delete')}}</el-button>
        <el-button @click="cancelDelete">{{$t('cancel')}}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="$t('publishMethod')"
      :visible.sync="fabuDialogVisible"
      width="560px"
      class="fabuDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="radioOuter">
        <el-radio v-model="appForm.publishStatus" label="1">{{$t('publicPublish')}}</el-radio>
        <p>
          {{$t('publishAsWebDemo')}}
        </p>
      </div>
      <div class="radioOuter">
        <el-radio v-model="appForm.publishStatus" label="2">{{$t('privatePublish')}}</el-radio>
        <p>{{$t('privatePublishDescription')}}</p>
      </div>
      <div>
        <el-button type="primary" @click="chooseTemplate">{{$t('selectTemplate')}}</el-button>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          @click="submitAddApp(3)"
          :disabled="!appForm.templateId && !appForm.mobileTemplateId"
        >
          <img src="@/assets/images/send-plane-fill.svg" />
          <span>{{$t('publish')}}</span>
        </el-button>
        <el-button @click="fabuDialogVisible = false">{{$t('cancel')}}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="$t('associateKnowledgeBase')"
      :visible.sync="assVisible"
      width="560px"
      class="fabuDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div style="margin-bottom: 6px">
        <el-checkbox-group v-model="knowledgeIdArr">
          <div
            v-for="item in konwlwdgeList"
            :key="item.knowledgeId + item.id"
            style="margin-bottom: 8px"
          >
            <el-checkbox :label="item.knowledgeId">{{
              item.knowledgeName
            }}</el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="setKnowledgeIds">{{$t('confirm')}}</el-button>
        <el-button @click="cancelAss">{{$t('cancel')}}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :visible.sync="previewDialogVisible"
      width="100%"
      fullscreen
      :show-close="false"
      class="flexDialog preViewDialog"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="headBar">
        <div class="leftSlide">
          <img
            src="@/assets/images/arrow-go-back-fill.svg"
            @click="closePriview"
          />
          <div class="titleIcon">
            <p>{{ previewItem.applicationName }}</p>
            <p>{{ previewItem.introduce }}</p>
          </div>
        </div>
        <div class="rightSlide">
          <el-button class="btn" @click="editApp(previewItem)">
            <img src="@/assets/images/edit-line.svg" />
            <span>{{$t('edit')}}</span>
          </el-button>
        </div>
      </div>
      <div
        style="
          padding: 20px 24px;
          background: #fff;
          border-radius: 4px;
          min-height: calc(100vh - 112px);
        "
      >
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane :label="$t('overview')" name="first">
            <div class="tabContentTitle">{{$t('appInfo')}}</div>
            <div class="tabContents">
              <div class="contentInner">
                <div class="title">
                  <span>{{$t('publicPublish')}}</span>
                  <el-switch
                    disabled
                    v-model="previewItem.publishStatus"
                    :active-value="'1'"
                  ></el-switch>
                </div>
                <p class="desc">{{$t('publicAccessLink')}}</p>
                <div class="secondLine">
                  <p class="inp">
                    <span>{{ previewItem.clientLink }}</span>
                    <!-- <img src="@/assets/images/shuaxin.svg"> -->
                  </p>
                  <el-button @click="cpoyText(previewItem.clientLink)">
                    <img src="@/assets/images/copy-line.svg" />
                    <span>{{$t('copyLink')}}</span>
                  </el-button>
                </div>
                <div class="thirdLine">
                  <el-button type="primary" @click="openPreview">
                    <img src="@/assets/images/computer.svg" />
                    <span>{{$t('demo')}}</span>
                  </el-button>
                  <el-button @click="devBuilding">{{$t('embedThirdParty')}}</el-button>
                  <el-button @click="devBuilding">{{$t('accessRestrictions')}}</el-button>
                </div>
              </div>
              <div class="contentInner">
                <div class="title">
                  <span>{{$t('privatePublish2')}}</span>
                  <el-switch
                    v-model="previewItem.publishStatus"
                     :active-value="'2'"
                    disabled
                  ></el-switch>
                </div>
                <p class="desc">{{$t('apiAccessCredential')}}</p>
                <div class="secondLine">
                  <p class="inp">{{ previewItem.apiKey }}</p>
                  <el-button @click="cpoyText()">
                    <img src="@/assets/images/copy-line.svg" />
                    <span>{{$t('copy2')}}</span>
                  </el-button>
                </div>
                <div class="thirdLine">
                  <el-button class="borderBtn" @click="devBuilding">
                    <img src="@/assets/images/pie-chart.svg" />
                    <span>{{$t('usage')}}</span>
                  </el-button>
                  <el-popover placement="right" trigger="hover">
                    <p>{{ previewItem.apiSecret }}</p>
                    <el-button slot="reference">{{$t('viewKey')}}</el-button>
                  </el-popover>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane :label="$t('settings')"name="second"></el-tab-pane>
          <el-tab-pane :label="$t('hitTest')" name="third"></el-tab-pane>
          <el-tab-pane :label="$t('conversationLog')" name="fourth">
            <div style="margin-bottom: 20px">
              <span style="margin-right: 20px">{{$t('questionTime')}}</span>
              <el-date-picker
                v-model="dateRange"
                type="datetimerange"
                format="yyyy-MM-dd HH:mm:ss"
                value-format="yyyy-MM-dd HH:mm:ss"
                align="right"
                unlink-panels
                range-separator="-"
                :start-placeholder="$t('startDate')"
                :end-placeholder="$t('endDate')"
                :picker-options="pickerOptions"
                clearable
                style="width: 400px; margin-right: 16px"
              ></el-date-picker>
              <span style="margin-right: 20px">{{$t('verificationDepartment')}}</span>

              <el-cascader
                popper-class="cascaderStyle"
                :options="treeData"
                :props="{
                  value: 'deptId',
                  label: 'deptName',
                  checkStrictly: true,
                }"
                clearable
                v-model="verifyDeptId"
                :show-all-levels="false"
                style="width: 200px; margin-right: 16px"
              ></el-cascader>

              <span style="margin-right: 20px">{{$t('verificationStatus')}}</span>

              <el-select
                style="width: 148px; margin-right: 16px"
                v-model="verifyStatus"
                :placeholder="$t('pleaseSelect')"
                clearable
                v-if="activeName == 'fourth'"
              >
                <el-option
                  v-for="item in verifyStatusColumns"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                ></el-option>
              </el-select>
              <el-input
                :placeholder="$t('enterQuestionOrAnswerKeyword')"
                style="width: 334px; margin-right: 16px"
                v-model="text"
                clearable
              >
              </el-input>
              <el-button type="primary" @click="searchDialog">{{$t('search')}}</el-button>
              <el-button @click="resetSearch" plain>{{$t('reset')}}</el-button>
              <el-button
                type="warning"
                @click="exportDialog"
                v-if="activeName == 'fourth'"
                plain
                >{{$t('export')}}</el-button
              >
            </div>
            <el-table
              :data="dialogTableData"
              max-height="600px"
              style="width: 100%"
              class="headTable"
            >
              <el-table-column prop="question" :label="$t('question')"></el-table-column>
              <el-table-column prop="answer" :label="$t('answer')" width="100">
                <template slot-scope="scope">
                  <el-popover
                    placement="top-start"
                    :title="scope.row.question"
                    width="400"
                    trigger="click"
                  >
                    <div style="padding: 0 12px 12px">
                      {{ scope.row.answer }}
                    </div>
                    <div
                      slot="reference"
                      style="color: #6295fc; cursor: pointer"
                    >
                    {{$t('details')}}
                    </div>
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column
                prop="userName"
                :label="$t('sourceUser')"
                width="180"
              ></el-table-column>
              <el-table-column
                prop="createTime"
                :label="$t('questionTime2')"
                width="180"
              ></el-table-column>
              <el-table-column
                prop="verifyDeptName"
                :label="$t('verificationDepartment')"
                width="180"
              ></el-table-column>
              <el-table-column prop="verifyStatus" :label="$t('verificationStatus')" width="180">
                <template slot-scope="scope">
                  <div
                    v-if="
                      scope.row.verifyStatus == 0 ||
                      scope.row.verifyStatus == 5 ||
                      scope.row.verifyStatus == 6
                    "
                    class="flex aligns"
                  >
                    <div
                      style="
                        width: 8px;
                        height: 8px;
                        background: #ffb24f;
                        border-radius: 4px;
                        margin-right: 8px;
                      "
                    ></div>
                    <span v-if="scope.row.verifyStatus == 0"> {{$t('pendingVerification')}} </span>
                    <span v-if="scope.row.verifyStatus == 5"> {{$t('pendingReVerification')}} </span>
                    <span v-if="scope.row.verifyStatus == 6"> {{$t('pendingReVerification')}} </span>
                  </div>
                  <div
                    v-if="
                      scope.row.verifyStatus == 1 || scope.row.verifyStatus == 2
                    "
                    class="flex aligns"
                  >
                    <div
                      style="
                        width: 8px;
                        height: 8px;
                        background: #1c50fd;
                        border: 3px solid rgba(28, 80, 253, 0.1);
                        border-radius: 4px;
                        margin-right: 8px;
                      "
                    ></div>
                    <span v-if="scope.row.verifyStatus == 1"> {{$t('verifiedModified')}} </span>
                    <span v-if="scope.row.verifyStatus == 2"> {{$t('verifiedCorrect')}}</span>
                  </div>
                  <div v-if="scope.row.verifyStatus == 3" class="flex aligns">
                    <div
                      style="
                        width: 8px;
                        height: 8px;
                        background: #fc3d30;
                        border-radius: 4px;
                        margin-right: 8px;
                      "
                    ></div>
                    {{$t('maliciousQuestion')}}
                  </div>
                  <div v-if="scope.row.verifyStatus == 4" class="flex aligns">
                    <div
                      style="
                        width: 8px;
                        height: 8px;
                        background: #b4bccc;
                        border-radius: 4px;
                        margin-right: 8px;
                      "
                    ></div>
                    {{$t('noAction')}}
                  </div>
                </template>
              </el-table-column>
              <el-table-column fixed="right" :label="$t('action')" width="100">
                <template slot-scope="scope">
                  <el-button
                    @click="viewClick(scope.row, scope.$index)"
                    type="text"
                    style="color: #6295fc"
                    >{{$t('viewDetails')}}</el-button
                  >
                  <!-- <el-button @click="toSource(scope.row)" type="text" size="small"
                    >溯源</el-button
                  > -->
                </template>
              </el-table-column>
            </el-table>
            <div style="text-align: right; margin-top: 32px">
              <el-pagination
                background
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :page-size="dialogPagesize"
                :current-page="currentPage"
                :page-sizes="[10, 20, 30, 40]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="dialogTotal"
              ></el-pagination>
            </div>
          </el-tab-pane>
          <el-tab-pane :label="$t('answerReview')" name="fifth">
            <div style="margin-bottom: 20px">
              <span style="margin-right: 20px">{{$t('questionTime2')}}</span>
              <el-date-picker
                v-model="dateRange"
                type="datetimerange"
                format="yyyy-MM-dd HH:mm:ss"
                value-format="yyyy-MM-dd HH:mm:ss"
                align="right"
                unlink-panels
                range-separator="-"
                :start-placeholder="$t('startDate')"
                :end-placeholder="$t('endDate')"
                :picker-options="pickerOptions"
                clearable
                style="width: 400px; margin-right: 16px"
              ></el-date-picker>
              <span style="margin-right: 20px">{{$t('verificationDepartment2')}}</span>

              <el-cascader
                popper-class="cascaderStyle"
                :options="treeData"
                :props="{
                  value: 'deptId',
                  label: 'deptName',
                  checkStrictly: true,
                }"
                clearable
                v-model="deptId"
                :show-all-levels="false"
                style="width: 200px; margin-right: 16px"
              ></el-cascader>

              <span style="margin-right: 20px" v-if="activeName == 'fifth'"
                >{{$t('reviewStatus')}}</span
              >
              <el-select
                style="width: 148px; margin-right: 16px"
                v-model="auditStatus"
                :placeholder="$t('pleaseSelect')"
                clearable
              >
                <el-option
                  v-for="item in auditStatusColumns"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                ></el-option>
              </el-select>
              <el-input
                :placeholder="$t('enterKeywordForQuestionOrAnswer')"
                style="width: 334px; margin-right: 16px"
                v-model="text"
                clearable
              >
              </el-input>
              <el-button type="primary" @click="searchDialog">{{$t('search')}}</el-button>
              <el-button @click="resetSearch" plain>{{$t('reset')}}</el-button>
            </div>
            <el-table
              :data="dialogTableDataAnswer"
              max-height="600px"
              style="width: 100%"
              class="headTable"
            >
              <el-table-column prop="question" :label="$t('question')"></el-table-column>
              <el-table-column prop="verifyAnswer" :label="$t('verifiedAnswer')"width="100">
                <template slot-scope="scope">
                  <el-popover
                    placement="top-start"
                    :title="scope.row.question"
                    width="400"
                    trigger="click"
                  >
                    <div style="padding: 0 12px 12px">{{ scope.row.verifyAnswer }}</div>
                    <div slot="reference" style="color: #6295fc; cursor: pointer">
                      {{$t('details')}}
                    </div>
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column
                prop="userName"
                :label="$t('sourceUser')"
                width="180"
              ></el-table-column>
              <el-table-column
                prop="createTime"
                :label="$t('questionTime2')"
                width="180"
              ></el-table-column>
              <el-table-column
                prop="verifyDeptName"
                :label="$t('verificationDepartment')"
                width="180"
              ></el-table-column>
              <el-table-column prop="verifyStatus" :label="$t('verificationStatus')" width="180">
                <template slot-scope="scope">
                  <div
                    v-if="
                      scope.row.verifyStatus == 2
                    "
                    class="flex aligns"
                  >
                  {{$t('correctAnswer')}}                
                  </div>
                  <div
                    v-if="scope.row.verifyStatus == 1"
                    class="flex aligns"
                  >
                  {{$t('incorrectAnswer')}}
                  </div>
                  <div v-if="scope.row.verifyStatus == 3" class="flex aligns">
                    {{$t('maliciousQuestion')}}
                  </div>
                  <div v-if="scope.row.verifyStatus == 4" class="flex aligns">
                    {{$t('noAction')}}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="auditStatus" :label="$t('reviewStatus')" width="180">
                <template slot-scope="scope">
                  <div
                    v-if="
                      scope.row.auditStatus == 0
                    "
                    class="flex aligns"
                  >
                    <div
                      style="
                        width: 8px;
                        height: 8px;
                        background: #ffb24f;
                        border-radius: 4px;
                        margin-right: 8px;
                      "
                    ></div>
                    <span> {{$t('pendingReview')}} </span> 
                  </div>
                  <div
                    v-if="scope.row.auditStatus == 1"
                    class="flex aligns"
                  >
                    <div
                      style="
                        width: 8px;
                        height: 8px;
                        background: #4BBE25;
                        border: 3px solid rgba(28, 80, 253, 0.1);
                        border-radius: 4px;
                        margin-right: 8px;
                      "
                    ></div>
                    <span> {{$t('reviewPassed')}}</span>
                  </div>
                  <div v-if="scope.row.auditStatus == 2" class="flex aligns">
                    <div
                      style="
                        width: 8px;
                        height: 8px;
                        background: #fc3d30;
                        border-radius: 4px;
                        margin-right: 8px;
                      "
                    ></div>
                    {{$t('reviewFailed')}}
                  </div>
                  <div v-if="scope.row.auditStatus == 3" class="flex aligns">
                    <div
                      style="
                        width: 8px;
                        height: 8px;
                        background: #B4BCCC;
                        border-radius: 4px;
                        margin-right: 8px;
                      "
                    ></div>
                    {{$t('noHandling')}}
                  </div>
                </template>
              </el-table-column>
              <el-table-column fixed="right" :label="$t('action')" width="100">
                <template slot-scope="scope">
                  <el-button
                    v-if="scope.row.auditStatus == 0"
                    @click="reviewAnswers(scope.row, scope.$index)"
                    type="text"
                    style="color: #6295fc"
                    >{{$t('review')}}</el-button
                  >
                  <el-button
                    v-else
                    @click="reviewAnswers(scope.row, scope.$index)"
                    type="text"
                    style="color: #6295fc"
                    >{{$t('viewDetails')}}</el-button
                  >
                  <!-- <el-button @click="toSource(scope.row)" type="text" size="small"
                    >溯源</el-button
                  > -->
                </template>
              </el-table-column>
            </el-table>
            <div style="text-align: right; margin-top: 32px">
              <el-pagination
                background
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :page-size="dialogPagesize"
                :current-page="currentPage"
                :page-sizes="[10, 20, 30, 40]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="dialogTotal"
              ></el-pagination>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>
    <el-dialog
      :title="$t('viewTrace')"
      :visible.sync="sourceVisible"
      width="600px"
      class="deleteDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div v-if="sourceTableList.length > 0">
        <div v-for="(item, index) in sourceTableList" :key="item">
          <el-collapse>
            <el-collapse-item :name="index">
              <el-tooltip
                slot="title"
                class="item"
                effect="dark"
                :content="
                  item.route
                    ? (item.knowledgeName ? item.knowledgeName : '暂无知识库') +
                      '-' +
                      item.route.join('|')
                    : ''
                "
                placement="top"
              >
                <div
                  style="
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    font-weight: bold;
                  "
                >
                  {{
                    item.route
                      ? (item.knowledgeName
                          ? item.knowledgeName
                          : '暂无知识库') +
                        '-' +
                        item.route.join('|')
                      : ''
                  }}
                </div>
              </el-tooltip>
              <p
                style="
                  font-size: 16px;
                  color: #646479;
                  margin-bottom: 16px;
                  cursor: pointer;
                "
              >
                {{ item.text }}
              </p>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
      <p style="text-align: center" v-else>{{$t('noData')}}</p>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelSource">{{$t('cancel')}}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="$t('selectTemplate')"
      :visible.sync="templateVisible"
      width="900px"
      class="fabuDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div style="margin-bottom: 16px">
        <el-radio-group v-model="templateType">
          <el-radio label="H5"></el-radio>
          <el-radio label="PC"></el-radio>
        </el-radio-group>
      </div>
      <div style="display: flex; justify-content: space-between">
        <div class="previewItem">
          <el-image
            lazy
            :src="findH5ImagePath()"
            v-if="findH5ImagePath() && templateType == 'H5'"
            style="width: 400px; height: 670px; border-radius: 8px"
          ></el-image>
          <el-image
            lazy
            :src="findPCImagePath()"
            v-if="findPCImagePath() && templateType == 'PC'"
            style="width: 670px; height: 400px; border-radius: 8px"
          ></el-image>
        </div>
        <div class="templateList" v-if="templateType == 'PC'">
          <el-radio-group v-model="templateSign">
            <el-radio
              :label="item.templateId"
              v-for="item in templateDataList"
              :key="item.id"
            >
              <el-image
                lazy
                :src="item.picturePath"
                style="
                  width: 130px;
                  margin-bottom: 16px;
                  border: 2px solid transparent;
                  border-radius: 4px;
                "
              ></el-image>
            </el-radio>
          </el-radio-group>
        </div>
        <div class="templateList" v-if="templateType == 'H5'">
          <el-radio-group v-model="mobileTemplateSign">
            <el-radio
              :label="item.templateId"
              v-for="item in mobileTemplateDataList"
              :key="item.id"
            >
              <el-image
                lazy
                :src="item.picturePath"
                style="
                  width: 70px;
                  margin-bottom: 16px;
                  border: 2px solid transparent;
                  border-radius: 4px;
                "
              ></el-image>
            </el-radio>
          </el-radio-group>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmTemplate">{{$t('select')}}</el-button>
        <el-button @click="cancelTemplate">{{$t('cancel')}}</el-button>
      </span>
    </el-dialog>
    <el-drawer
      :title="$t('questionVerification')"
      :visible.sync="addQaVisible"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
      :close-on-press-escape="false"
      :wrapperClosable="false"
      :before-close="handleClose"
      class="elDrawer"
      size="50%"
    >
      <div slot class="qa-box">
        <div class="userInfo">
          <span>{{ setForm.question }}</span>
        </div>
        <div class="flex aligns just" style="margin-bottom: 12px">
          <div class="flex aligns">
            <div
              :class="['userList', activeIndex == 0 ? 'activeList' : '']"
              @click="clickUser(0)"
            >
            {{$t('originalAnswer')}}
            </div>
            <div
              :class="['userList', activeIndex == 1 ? 'activeList' : '']"
              @click="clickUser(1)"
            >
            {{$t('traceability')}}
            </div>
          </div>
          <div class="flex-center just" style="width: 49.5%">
            <div class="flex-center">
              <div
                style="
                  width: 3px;
                  height: 18px;
                  background: #1c50fd;
                  margin-right: 8px;
                "
              ></div>
              <span
                style="
                  font-family: MiSans, MiSans;
                  font-weight: 500;
                  font-size: 18px;
                  color: #383d47;
                "
                >{{$t('verify')}}</span
              >
            </div>
            <div class="flex-center">
              <!-- 已核实正确情况 -->
              <div
                class="flex-center"
                v-if="
                  setForm.verifyStatus == 0 ||
                  setForm.verifyStatus == 5 ||
                  setForm.verifyStatus == 6
                "
              >
                <el-radio-group v-model="formList.verifyStatus">
                  <el-radio :label="2">{{$t('correct')}}</el-radio>
                  <el-radio :label="1">{{$t('incorrect')}}</el-radio>
                  <el-radio :label="3">{{$t('maliciousQuery')}}</el-radio>
                  <el-radio :label="4">{{$t('noAction')}}</el-radio>
                </el-radio-group>
              </div>
              <div class="flex-center" v-else>
                <img
                  v-if="setForm.verifyStatus != 3"
                  :src="require('@/assets/images/checkbox-circle-fill.svg')"
                />
                <i
                  v-else
                  style="color: #f00; font-size: 22px"
                  class="iconfont el-icon-error"
                ></i>
                <span
                  style="margin: 0 4px 0 8px"
                  v-if="setForm.verifyStatus == 1"
                  >{{$t('verifiedModified')}}</span
                >
                <span
                  style="margin: 0 4px 0 8px"
                  v-if="setForm.verifyStatus == 2"
                  >{{$t('verifiedCorrect')}}</span
                >
                <span
                  style="margin: 0 4px 0 8px"
                  v-if="setForm.verifyStatus == 3"
                  >{{$t('maliciousQuestion')}}</span
                >
                <span
                  style="margin: 0 4px 0 8px"
                  v-if="setForm.verifyStatus == 4"
                  >{{$t('noAction')}}</span
                >
                <el-popover placement="left-start" width="300" trigger="hover">
                  <div style="padding: 0 12px 12px">
                    <div class="flex just">
                      <span class="formKey"> {{$t('verifier')}} </span>
                      <span class="formValue">
                        {{ setForm.verifyUserName }}
                      </span>
                    </div>
                    <div class="flex just">
                      <span class="formKey"> {{$t('department')}}</span>
                      <span class="formValue">
                        {{ setForm.verifyDeptName }}
                      </span>
                    </div>
                    <div class="flex just">
                      <span class="formKey"> {{$t('verificationTime')}}</span>
                      <span class="formValue">
                        {{ setForm.createTime }}
                      </span>
                    </div>
                  </div>
                  <img
                    slot="reference"
                    :src="require('@/assets/images/information-line.svg')"
                  />
                </el-popover>
              </div>
            </div>
          </div>
        </div>
        <div class="content">
          <!-- 溯源 -->
          <div v-if="activeIndex == 1" class="traceability">
            <div v-if="sourceTableList.length > 0">
              <div v-for="(item, index) in sourceTableList" :key="item">
                <el-collapse>
                  <el-collapse-item :name="index">
                    <el-tooltip
                      slot="title"
                      class="item"
                      effect="dark"
                      :content="
                        item.route
                          ? (item.knowledgeName
                              ? item.knowledgeName
                              : '暂无知识库') +
                            '-' +
                            item.route.join('|')
                          : ''
                      "
                      placement="top"
                    >
                      <div
                        style="
                          white-space: nowrap;
                          overflow: hidden;
                          text-overflow: ellipsis;
                          font-weight: bold;
                        "
                      >
                        {{
                          item.route
                            ? (item.knowledgeName
                                ? item.knowledgeName
                                : '暂无知识库') +
                              '-' +
                              item.route.join('|')
                            : ''
                        }}
                      </div>
                    </el-tooltip>
                    <p
                      style="
                        font-size: 16px;
                        color: #646479;
                        margin-bottom: 16px;
                        cursor: pointer;
                      "
                    >
                      {{ item.text }}
                    </p>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
            <p style="text-align: center" v-else>{{$t('noData')}}</p>
          </div>

          <el-input
            v-model="setForm.answer"
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 50 }"
            :placeholder="$t('enter')"
            class="leftText"
            disabled
            v-if="activeIndex == 0"
          ></el-input>
          <el-input
            v-model="formList.verifyAnswer"
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 50 }"
            :placeholder="$t('enter')"
            :class="[
              setForm.verifyStatus == 0 ||
              setForm.verifyStatus == 5 ||
              setForm.verifyStatus == 6
                ? 'verifyStatusAnswer'
                : '',
            ]"
          ></el-input>
        </div>
        <div class="footerDrawer">
          <el-button
            type="primary"
            v-if="
              setForm.verifyStatus == 0 ||
              setForm.verifyStatus == 5 ||
              setForm.verifyStatus == 6
            "
            @click="submitVerification"
            >{{$t('verifyNextAfterSubmit')}}</el-button
          >
          <el-button
            type="primary"
            v-if="
              setForm.verifyStatus == 0 ||
              setForm.verifyStatus == 5 ||
              setForm.verifyStatus == 6
            "
            @click="handleAddQaDialog(0)"
            >{{$t('closeAfterSubmit')}}</el-button
          >
          <el-button @click="handleClose">{{$t('close')}}</el-button>
        </div>
      </div>
    </el-drawer>
    <drawerAnswer v-if="addQaVisibleAnswer" :addQaVisibleAnswer="addQaVisibleAnswer" :setForm="setForm" @handlecloseDraw="handlecloseDraw" @handleAddQaDialogAns="handleAddQaDialogAns"></drawerAnswer>

    <el-dialog :title="$t('authorize')":visible.sync="grantVisible" width="750px" class="deleteDialog" destroy-on-close :close-on-click-modal="false"
        :close-on-press-escape="false">
      <GrantData :data-id="grantData.applicationId" v-if="grantVisible"  data-type="app" @cancelGrant="cancelGrant"></GrantData>
    </el-dialog>
  </div>
</template>
<script>
import {
  appList,
  modelList,
  getAuthChannels,
  addApplication,
  deleteApplication,
  dialogRecord,
  sourceList,
  templateList,
  ttsList,
  copyApp,
  defaultApp,
  verifyRecord,
  deptTree,
  getCheckRecord,
  recordCheckRecord
} from '@/api/app'
import { getKnowledgeInfoList } from '@/api/index'
import drawerAnswer from "./components/drawerAnswer";
import axios from 'axios'
import GrantData from "@/views/appManage/components/GrantData.vue";
import { template } from '@antv/x6/lib/util/string/string';
export default {
  components: {
    drawerAnswer,GrantData
  },
  data() {
    return {
      ttsListData: [],
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      collapseActive: [
        'first',
        'second',
        'third',
        'fourth',
        'fifth',
        'sixth',
        'seventh',
      ],
      templateSign: '',
      mobileTemplateSign: '',
      mobileTemplateDataList: [],
      templateDataList: [],
      templateType: 'H5',
      templateVisible: false,
      sourceTableList: [],
      sourceVisible: false,
      pickerOptions: {
        shortcuts: [
          {
            text: '最近一周',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            },
          },
          {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            },
          },
          {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
              picker.$emit('pick', [start, end])
            },
          },
        ],
      },
      dateRange: [],
      question: '',
      answer: '',
      currentPage: 1,
      dialogTotal: 0,
      dialogPagesize: 10,
      dialogTableData: [],
      uploadBtnLogo: false,
      uploadBtnRobot: false,
      uploadBtnWebIcon: false,
      uploadBtnUserIcon: false,
      uploadBtnIdentityIcon: false,
      appForms: {
        contentScore: 1.76,
        rangeContentScore: 0.88,
        qaTitleScore: 1.91,
        qaRangeTitleScore: 0.91,
        qaContentScore: 0.88,
        qaRangeContentScore: 0.88,
        filterNum: 3,
        prepareNum: 60,
      },
      fileListUserIcon: [],
      fileListwebIcon: [],
      fileListRobot: [],
      fileListLogo: [],
      fileListIdentityIcon: [],
      knowledgeIdArr: [],
      activeName: 'first',
      previewItem: '',
      previewDialogVisible: false,
      deleteItem: '',
      deleteItemName: '',
      konwlwdgeList: [],
      applicationName: '',
      appForm: {
        templateId: '',
        mobileTemplateId: '',
        applicationName: '',
        applicationCode: '',
        introduce: '',
        webTitle: '',
        greeting: '',
        disclaimer: '',
        tailTalk: '',
        failureTalk: '',
        virtualHumanFlag: false,
        virtualHumanId: '',
        voiceDialogueFlag: false,
        ttsId: '',
        sttId: '',
        buttonStyle: '',
        buttonText: '',
        modelId: '',
        modelAnswerFlag: false,
        sensitiveFlag: false,
        networkFlag: false,
        ipFlag: false,
        answerTimeout: '',
        notAnswer: '',
        promptTemplate: '',
        subjectPrompt: '',
        ocrFlag: false,
        videoFlag: false,
        multiDialogueFlag: false,
        multiDialogueNum: '',
        knowledgeFlag: false,
        rewritingFlag: false,
        recommendation: '',
        recommendationNum: '',
        systemPrompt: '',
        remark: '',
        contentScore: 1.76,
        rangeContentScore: 0.88,
        qaTitleScore: 1.91,
        qaRangeTitleScore: 0.91,
        qaContentScore: 0.88,
        qaRangeContentScore: 0.88,
        filterNum: 3,
        prepareNum: 60,
        publishStatus: '1',
        logo: '',
        robotIcon: '',
        webIcon: '',
        userIcon: '',
        knowledgeIds: [],
        presetQuestionList: [''],
        identityIcon: '',
        networkChannel: '',
        processStep: [
          'builtIn',
          'interceptSensitive',
          'subjectTalk',
          'findQaTitle',
          'findQaContent',
          'finalCollectStrategy',
          'findAnswerByModel',
        ],
        polishFlag: false,
        polishPrompt: '',
        inputPlaceholder: '',
        prologue: '',
      },
      appList: [],
      dialogVisibles: false,
      dialogVisible: false,
      deleteDialogVisible: false,
      fabuDialogVisible: false,
      assVisible: false,
      modleOptions: [],
      h5AuthChannels: [],
      pcAuthChannels: [],
      editItem: {},
      clOptions: [
        {
          label: '匹配问题',
          value: 'qa-question',
        },
        {
          label: '匹配答案',
          value: 'qa-content',
        },
        {
          label: '匹配知识库',
          value: 'knowledge',
        },
      ],
      workPosition: '',
      workPositionColumns: [],
      addQaVisible: false,
      setForm: {},
      activeIndex: 0,
      formList: {},
      text: '',
      verifyDeptName: '',
      verifyStatus: '',
      verifyStatusColumns: [
        {
          text: '待核实',
          value: 0,
        },
        {
          text: '已核实错误',
          value: 1,
        },
        {
          text: '已核实正确',
          value: 2,
        },
        {
          text: '恶意问题',
          value: 3,
        },
        {
          text: '不处置',
          value: 4,
        },
        {
          text: '待重新核实',
          value: 5,
        },
      ],
      treeData: [],
      verifyDeptId: '',
      setIndex: '',
      dialogTableDataAnswer: [],
      deptId: "",
      auditStatus: "",
      auditStatusColumns: [
        {
          text: "待审核",
          value: 0,
        },
        {
          text: "审核通过",
          value: 1,
        },
        {
          text: "审核不通过",
          value: 2,
        },
        {
          text: '不处理',
          value: 3,
        },
      ],
      addQaVisibleAnswer: false,
      grantVisible: false,
      grantData:{},
    }
  },
  created() {
    this.getAppList()
    this.getDeptTree()
  },
  methods: {
    sendMessage() {
      const iframe = this.$refs.iframe
      if (iframe) {
        const iframeWindow = iframe.contentWindow
        // 现在可以安全地使用iframeWindow对象了
        iframeWindow.postMessage({ param1: '测试传入子组件的参数' }, '*')
      }
    },
    handleCommand(value, item) {
      if (value == 'copyApp') this.copyAppInfo(item)
      if (value == 'editeApp') this.editApp(item)
      if (value == 'deleteApp') this.openDelete(item)
      if (value == 'grantApp') this.grantApp(item)
    },
    copyAppInfo(item) {
      this.$confirm('此操作复制应用, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          copyApp({ applicationId: item.applicationId }).then((res) => {
            if (res.code == '000000') {
              this.getAppList()
              this.$message({
                type: 'success',
                message: '复制成功',
              })
            } else {
              this.$message({
                type: 'error',
                message: '复制失败',
              })
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消复制',
          })
        })
    },
    openPreview() {
      window.open(this.previewItem.clientLink, '_blank')
    },
    validTextEn(value) {
      if (/[a-zA-z]$/.test(value) == false) {
        this.appForm.applicationCode = ''
        this.$message({
          type: 'warning',
          message: '请输入纯英文',
        })
      }
    },
    deleteKnowledgeId(item) {
      let filterArr = this.knowledgeIdArr.filter((items) => items != item)
      this.knowledgeIdArr = this.appForm.knowledgeIds = filterArr || []
    },
    findPCImagePath() {
      if (this.templateDataList && this.templateDataList.length) {
        let findItem = this.templateDataList.find(
            (item) => this.templateSign == item.templateId
        );
        return findItem?.picturePath
      }
    },
    findH5ImagePath() {
      if (this.mobileTemplateDataList && this.mobileTemplateDataList.length) {
        let findItem = this.mobileTemplateDataList.find(
            (item) => this.mobileTemplateSign == item.templateId
        );
        return findItem?.picturePath
      }
    },
    chooseTemplate() {
      if (!this.editItem) {
        this.templateSign = ''
        this.mobileTemplateSign = ''
      }
      this.templateType = 'H5'
      this.templateDataList = []
      this.mobileTemplateDataList = []
      this.getTemplateList()
      this.getMobileTemplateList()
      this.templateVisible = true
    },
    getTemplateList() {
      templateList({ form: 'PC' }).then((res) => {
        if (res.code == '000000') {
          this.templateDataList = res.data || []
        } else {
          this.templateDataList = []
        }
      })
    },
    getMobileTemplateList() {
      templateList({ form: 'H5' }).then((res) => {
        if (res.code == '000000') {
          this.mobileTemplateDataList = res.data || []
        } else {
          this.mobileTemplateDataList = []
        }
      })
    },
    confirmTemplate() {
      if (!this.templateSign && !this.mobileTemplateSign) {
        this.$message({
          type: 'warning',
          message: '请至少选择一个H5模板和PC模板后再确认',
        })
        return
      }

      if (this.templateSign) {
        this.appForm.templateId = this.templateSign
        this.appForm.mobileTemplateId = this.mobileTemplateSign
      } else {
        this.$message({
          type: 'warning',
          message: '请至少选择一个PC模板后再确认',
        })
        return
      }

      if (this.mobileTemplateSign) {
        this.appForm.mobileTemplateId = this.mobileTemplateSign
      } else {
        this.$message({
          type: 'warning',
          message: '请至少选择一个H5模板后再确认',
        })
        return
      }
      this.templateVisible = false
    },
    cancelTemplate() {
      this.templateVisible = false
    },
    cancelSource() {
      this.sourceVisible = false
    },
    toSource(row) {
      this.sourceTableList = []
      // this.sourceVisible = true
      sourceList({ dialogueId: row.dialogueId }).then((res) => {
        if (res.code == '000000') {
          this.sourceTableList = res.data.sourceAnswerResultList || []
        } else {
          this.sourceTableList = []
        }
      })
    },
    searchDialog() {
      this.currentPage = 1
      if (this.activeName == 'fourth') {
        this.getDialogRecord();
      } else if (this.activeName == 'fifth') {
        this.getDialogRecordAnswer()
      }
    },
    getDialogRecord() {
      let deptId = ''
      if (this.verifyDeptId.length > 0) {
        deptId = this.verifyDeptId[this.verifyDeptId.length - 1]
      }
      dialogRecord({
        timeStart: this.dateRange ? this.dateRange[0] : '',
        timeEnd: this.dateRange ? this.dateRange[1] : '',
        question: this.question,
        answer: this.answer,
        applicationId: this.previewItem.applicationId,
        pageNo: this.currentPage,
        pageSize: this.dialogPagesize,
        verifyDeptId: deptId,
        text: this.text
      }).then((res) => {
        if (res.code == '000000') {
          this.dialogTableData = res.data.list || []
          this.dialogTotal = res.data.total || 0
        } else {
          this.dialogTableData = []
          this.dialogTotal = 0
        }
      })
    },
    devBuilding() {
      this.$message({
        type: 'warning',
        message: '功能建设中...',
      })
    },
    handleSizeChange(val) {
      this.currentPage = 1
      this.dialogPagesize = val
      if (this.activeName == 'fourth') {
        this.getDialogRecord();
      } else if (this.activeName == 'fifth') {
        this.getDialogRecordAnswer()
      }
    },
    handleCurrentChange(val) {
      this.currentPage = val
      if (this.activeName == 'fourth') {
        this.getDialogRecord();
      } else if (this.activeName == 'fifth') {
        this.getDialogRecordAnswer()
      }
    },
    temporarSave(flag) {
      this.appForm.publishStatus = '4'
      this.submitAddApp(flag)
    },
    exeCommandCopyText(text) {
      try {
        const t = document.createElement('textarea')
        t.nodeValue = text
        t.value = text
        document.body.appendChild(t)
        t.select()
        document.execCommand('copy')
        document.body.removeChild(t)
        return true
      } catch (e) {
        console.log(e)
        return false
      }
    },
    cpoyText(content) {
      this.exeCommandCopyText(content)
      this.$message({
        message: '复制成功',
        type: 'success',
      })
    },
    handleClick(tab, event) {
      this.workPosition = ''
      this.dateRange = []
      this.timeStart = ''
      this.timeEnd = ''
      this.text = ''
      this.verifyStatus = ''
      this.verifyDeptId = ''
      this.deptId = "";
      this.auditStatus = "";
      if (tab.name == 'fourth') {
        this.getDialogRecord()
      } else if (tab.name == 'fifth') {
        this.getDialogRecordAnswer()
      }
    },
    previewApp(item) {
      this.activeName = 'first'
      this.previewItem = item
      this.previewDialogVisible = true
    },
    editApp(item) {
      this.editItem = item
      console.log(this.editItem, 'this.editItem')

      this.knowledgeList() //知识库列表
      this.getModelList() //模型列表
      this.getH5AuthChannelList() //微信认证
      this.getPcAuthChannelList() //微信认证
      this.getTtsId() //语音列表
      this.dialogVisible = true
      this.defaultAppInfoSet(item)
    },
    grantApp(item){
      this.grantData = item;
      this.grantVisible = true;
    },
    closePriview() {
      this.previewDialogVisible = false
    },
    submitAddApp(flag) {
      if (!this.appForm.templateId && this.appForm.publishStatus == '1') {
        this.$message({
          type: 'warning',
          message: '请选择PC模板后再发布',
        })
        return
      }
      if (!this.appForm.mobileTemplateId && this.appForm.publishStatus == '1') {
        this.$message({
          type: 'warning',
          message: '请选择H5模板后再发布',
        })
        return
      }
      addApplication({
        ...this.appForm,
        virtualHumanFlag: this.appForm.virtualHumanFlag ? '是' : '否',
        voiceDialogueFlag: this.appForm.voiceDialogueFlag ? '是' : '否',
        modelAnswerFlag: this.appForm.modelAnswerFlag ? '是' : '否',
        sensitiveFlag: this.appForm.sensitiveFlag ? '是' : '否',
        networkFlag: this.appForm.networkFlag ? '是' : '否',
        ipFlag: this.appForm.ipFlag ? '是' : '否',
        ocrFlag: this.appForm.ocrFlag ? '是' : '否',
        videoFlag: this.appForm.videoFlag ? '是' : '否',
        multiDialogueFlag: this.appForm.multiDialogueFlag ? '是' : '否',
        knowledgeFlag: this.appForm.knowledgeFlag ? '是' : '否',
        rewritingFlag: this.appForm.rewritingFlag ? '是' : '否',
        polishFlag: this.appForm.polishFlag ? '是' : '否',
        processStep: this.appForm.processStep
          ? this.appForm.processStep.join(',')
          : '',
      }).then((res) => {
        console.log(res, 'res')

        if (res.code == '000000') {
          this.getAppList()
          if (flag != 1) {
            this.dialogVisible = false
            this.fabuDialogVisible = false
            this.$message({
              type: 'success',
              message: '发布成功',
            })
          }
          if (flag == 1) {
            const iframe = this.$refs.iframe
            let that = this
            if (iframe) {
              this.$refs.iframe.src = 'about:blank' // 作为一个临时的链接，如果是其它正常可访问URL，会浪费一些不必要流量
              const _t = setTimeout(() => {
                that.$refs.iframe.src = res.data.clientLink
                console.log('that.editItem.clientLink', res.data.clientLink)
                window.addEventListener('message', (event) => {
                  console.log(event.data, 'data')
                  if (event.data == 'ready')
                    iframe.contentWindow.postMessage({ isIframe: true }, '*')
                })
                clearTimeout(_t)
              }, 300)
            }
          }
        } else {
          this.$message({
            type: 'error',
            message: res.msg,
          })
        }
      })
    },
    filterKnowledge(item) {
      let findItem = this.konwlwdgeList.find(
        (items) => items.knowledgeId == item
      )
      return findItem?.knowledgeName
    },
    knowledgeList() {
      getKnowledgeInfoList({
        pageNo: 1,
        pageSize: 1000,
        order: '',
        sort: ''
      }).then((res) => {
        if (res.code == '000000') {
          this.konwlwdgeList = res.data?.records
        } else {
          this.konwlwdgeList = []
        }
      })
    },
    assKnowledge() {
      this.knowledgeIdArr = this.appForm.knowledgeIds
      this.knowledgeList()
      this.assVisible = true
    },
    setKnowledgeIds() {
      this.appForm.knowledgeIds = this.knowledgeIdArr
      this.assVisible = false
    },
    cancelAss() {
      this.knowledgeIdArr = []
      this.assVisible = false
    },
    statusToSrt(status) {
      switch (status) {
        case '1':
          return '公开发布'
        case '2':
          return '私有发布'
        case '3':
          return '未发布'
        case '4':
          return '暂存'
        case '5':
          return '停用'
      }
    },
    handleLogoSuccess(response, file, fileList) {
      if ((response.code = '000000')) {
        this.uploadBtnLogo = true
        this.appForm.logo =
          response.data && response.data[0] ? response.data[0].url : ''
        this.fileListLogo = [
          {
            name: 'logo',
            url: this.appForm.logo,
          },
        ]
      } else {
        this.uploadBtnLogo = false
        this.appForm.logo = ''
        this.fileListLogo = []
      }
    },
    handleLogoRemove(file, fileList) {
      this.uploadBtnLogo = false
      this.appForm.logo = ''
      this.fileListLogo = []
    },
    handleRobotSuccess(response, file, fileList) {
      if ((response.code = '000000')) {
        this.uploadBtnRobot = true
        this.appForm.robotIcon =
          response.data && response.data[0] ? response.data[0].url : ''
        this.fileListRobot = [
          {
            name: 'robot',
            url: this.appForm.robotIcon,
          },
        ]
      } else {
        this.uploadBtnRobot = false
        this.appForm.robotIcon = ''
        this.fileListRobot = []
      }
    },
    handleRobotRemove(file, fileList) {
      this.uploadBtnRobot = false
      this.appForm.robotIcon = ''
      this.fileListRobot = []
    },
    handleWebSuccess(response, file, fileList) {
      if ((response.code = '000000')) {
        this.uploadBtnWebIcon = true
        this.appForm.webIcon =
          response.data && response.data[0] ? response.data[0].url : ''
        this.fileListwebIcon = [
          {
            name: 'webIcon',
            url: this.appForm.webIcon,
          },
        ]
      } else {
        this.uploadBtnWebIcon = false
        this.appForm.webIcon = ''
        this.fileListwebIcon = []
      }
    },
    handleWebRemove(file, fileList) {
      this.uploadBtnWebIcon = false
      this.appForm.webIcon = ''
      this.fileListwebIcon = []
    },
    handleUserSuccess(response, file, fileList) {
      if ((response.code = '000000')) {
        this.uploadBtnUserIcon = true
        this.appForm.userIcon =
          response.data && response.data[0] ? response.data[0].url : ''
        this.fileListUserIcon = [
          {
            name: 'userIcon',
            url: this.appForm.userIcon,
          },
        ]
      } else {
        this.uploadBtnUserIcon = false
        this.appForm.userIcon = ''
        this.fileListUserIcon = []
      }
    },
    handleUserRemove(file, fileList) {
      this.uploadBtnUserIcon = false
      this.appForm.userIcon = ''
      this.fileListUserIcon = []
    },
    handleIdentityIconSuccess(response, file, fileList) {
      if ((response.code = '000000')) {
        this.uploadBtnIdentityIcon = true
        this.appForm.identityIcon =
          response.data && response.data[0] ? response.data[0].url : ''
        this.fileListIdentityIcon = [
          {
            name: 'identityIcon',
            url: this.appForm.identityIcon,
          },
        ]
      } else {
        this.uploadBtnIdentityIcon = false
        this.appForm.identityIcon = ''
        this.fileListIdentityIcon = []
      }
    },
    handleIdentityIconRemove(file, fileList) {
      this.uploadBtnIdentityIcon = false
      this.appForm.identityIcon = ''
      this.fileListIdentityIcon = []
    },
    openDelete(item) {
      this.deleteItemName = ''
      this.deleteItem = item
      this.deleteDialogVisible = true
    },
    cancelDelete() {
      this.deleteDialogVisible = false
    },
    confirmDelete() {
      if (this.deleteItem.applicationName == this.deleteItemName) {
        deleteApplication({
          applicationId: this.deleteItem.applicationId,
        }).then((res) => {
          console.log(res, 222)
          if (res.code == '000000') {
            this.$message({
              type: 'success',
              message: '删除成功',
            })
            this.deleteDialogVisible = false
            this.getAppList()
          } else {
            this.$message({
              type: 'error',
              message: '删除失败',
            })
          }
        })
      } else {
        this.$message({
          type: 'warning',
          message: '输入名称与删除应用名称不一致',
        })
        return
      }
    },
    fabuDialog() {
      this.appForm.publishStatus = '1'
      this.fabuDialogVisible = true
    },
    defaultAppInfo() {
      defaultApp({}).then((res) => {
        if (res.code == '000000') {
          this.defaultAppInfoSet(res.data)
        }
      })
    },
    defaultAppInfoSet(item) {
      this.knowledgeIdArr = item.knowledgeIds || []
      this.uploadBtnLogo = item.logo ? true : false
      this.uploadBtnRobot = item.robotIcon ? true : false
      this.uploadBtnWebIcon = item.webIcon ? true : false
      this.uploadBtnUserIcon = item.userIcon ? true : false
      this.uploadBtnIdentityIcon = item.identityIcon ? true : false
      this.templateSign = item.templateId
      this.mobileTemplateSign = item.mobileTemplateId

      if (item.logo) {
        this.fileListLogo = [
          {
            name: 'logo',
            url: item.logo,
          },
        ]
      } else {
        this.fileListLogo = []
      }

      if (item.userIcon) {
        this.fileListUserIcon = [
          {
            name: 'useIcon',
            url: item.userIcon,
          },
        ]
      } else {
        this.fileListUserIcon = []
      }

      if (item.webIcon) {
        this.fileListwebIcon = [
          {
            name: 'webIcon',
            url: item.webIcon,
          },
        ]
      } else {
        this.fileListwebIcon = []
      }

      if (item.robotIcon) {
        this.fileListRobot = [
          {
            name: 'robot',
            url: item.robotIcon,
          },
        ]
      } else {
        this.fileListRobot = []
      }

      if (item.identityIcon) {
        this.fileListIdentityIcon = [
          {
            name: 'identityIcon',
            url: item.identityIcon,
          },
        ]
      } else {
        this.fileListIdentityIcon = []
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
        tailTalk: item.tailTalk,
        failureTalk: item.failureTalk,
        virtualHumanId: item.virtualHumanId,
        clientAuthChannel: item.clientAuthChannel,
        pcAuthChannel: item.pcAuthChannel,
        toHumanFlag: JSON.parse(item.toHumanFlag),
        ttsId: parseInt(item.ttsId),
        sttId: parseInt(item.sttId),
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
        userIcon: item.userIcon,
        identityIcon: item.identityIcon,
        virtualHumanFlag: item.virtualHumanFlag == '是' ? true : false,
        voiceDialogueFlag: item.voiceDialogueFlag == '是' ? true : false,
        modelAnswerFlag: item.modelAnswerFlag == '是' ? true : false,
        sensitiveFlag: item.sensitiveFlag == '是' ? true : false,
        networkFlag: item.networkFlag == '是' ? true : false,
        ipFlag: item.ipFlag == '是' ? true : false,
        ocrFlag: item.ocrFlag == '是' ? true : false,
        videoFlag: item.videoFlag == '是' ? true : false,
        multiDialogueFlag: item.multiDialogueFlag == '是' ? true : false,
        knowledgeFlag: item.knowledgeFlag == '是' ? true : false,
        rewritingFlag: item.rewritingFlag == '是' ? true : false,
        presetQuestionList:
          item.presetQuestionList && item.presetQuestionList.length > 0
            ? item.presetQuestionList
            : [''],
        knowledgeIds: item.knowledgeIds,
        networkChannel: item.networkChannel,
        processStep: item.processStep ? item.processStep.split(',') : [],
        polishFlag: item.polishFlag == '是' ? true : false,
        polishPrompt: item.polishPrompt,
      }
      console.log(this.editItem, 'this.editItem')
    },
    createApp() {
      this.editItem = ''
      this.defaultAppInfo()
      this.knowledgeList()
      this.getTtsId() //语音列表
      this.getModelList() //模型列表
      this.getH5AuthChannelList() //微信认证
      this.getPcAuthChannelList() //微信认证
      this.dialogVisible = true
    },
    getTtsId() {
      ttsList({
        pageNo: 1,
        pageSize: 1000,
      }).then((res) => {
        if (res.code == '000000') {
          this.ttsListData = res.data?.records || []
        } else {
          this.ttsListData = []
        }
      })
    },
    addQues() {
      this.appForm.presetQuestionList.push('')
    },
    deleteQues(index) {
      this.appForm.presetQuestionList.splice(index, 1)
    },
    closeDialog() {
      this.dialogVisible = false
    },
    openParamsConfig() {
      this.appForms.contentScore = this.appForm.contentScore
      this.appForms.rangeContentScore = this.appForm.rangeContentScore
      this.appForms.qaTitleScore = this.appForm.qaTitleScore
      this.appForms.qaRangeTitleScore = this.appForm.qaRangeTitleScore
      this.appForms.qaContentScore = this.appForm.qaContentScore
      this.appForms.qaRangeContentScore = this.appForm.qaRangeContentScore
      this.appForms.filterNum = this.appForm.filterNum
      this.appForms.prepareNum = this.appForm.prepareNum
      this.dialogVisibles = true
    },
    setConfig() {
      this.appForm.contentScore = this.appForms.contentScore
      this.appForm.rangeContentScore = this.appForms.rangeContentScore
      this.appForm.qaTitleScore = this.appForms.qaTitleScore
      this.appForm.qaRangeTitleScore = this.appForms.qaRangeTitleScore
      this.appForm.qaContentScore = this.appForms.qaContentScore
      this.appForm.qaRangeContentScore = this.appForms.qaRangeContentScore
      this.appForm.filterNum = this.appForms.filterNum
      this.appForm.prepareNum = this.appForms.prepareNum
      this.dialogVisibles = false
    },
    cancelConfig() {
      this.dialogVisibles = false
    },
    useDefaultValue() {
      this.appForms.contentScore = 1.76
      this.appForms.rangeContentScore = 0.88
      this.appForms.qaTitleScore = 1.91
      this.appForms.qaRangeTitleScore = 0.91
      this.appForms.qaContentScore = 0.88
      this.appForms.qaRangeContentScore = 0.88
      this.appForms.filterNum = 3
      this.appForms.prepareNum = 60
    },
    getModelList() {
      modelList({
        id: '',
        modelId: '',
        modelName: '',
        status: '',
      }).then((res) => {
        if (res.code == '000000') {
          this.modleOptions = res?.data
        } else {
          this.modleOptions = []
        }
      })
    },
    getH5AuthChannelList() {
      getAuthChannels({
        clientType: 'H5',
      }).then((res) => {
        if (res.code == '000000') {
          this.h5AuthChannels = res?.data
        } else {
          this.h5AuthChannels = []
        }
      })
    },
    getPcAuthChannelList() {
      getAuthChannels({
        clientType: 'PC',
      }).then((res) => {
        if (res.code == '000000') {
          this.pcAuthChannels = res?.data
        } else {
          this.pcAuthChannels = []
        }
      })
    },
    getAppList() {
      appList({
        pageNo: 1,
        pageSize: 100,
        order: '',
        sort: '',
        applicationName: this.applicationName,
      }).then((res) => {
        if (res.code == '000000') {
          this.appList = res.data?.records
        } else {
          this.appList = []
        }
      })
    },
    exportDialog() {
      axios({
        method: 'POST',
        url: `${process.env.VUE_APP_BASE_API}/record/exportRecord`,
        // url: `https://localhost/smart-agent-api-demo/matterGuideInfo/export`,
        headers: {
          Authorization: 'Bearer ' + sessionStorage.getItem('manageAccessToken'),
        },
        responseType: 'blob',
        data: {
          timeStart: this.dateRange ? this.dateRange[0] : '',
          timeEnd: this.dateRange ? this.dateRange[1] : '',
          question: this.question,
          answer: this.answer,
          applicationId: this.previewItem.applicationId,
          pageNo: this.currentPage,
          pageSize: this.dialogPagesize,
        },
      })
        .then((res) => {
          let data = res.data
          console.log(data)
          const url = window.URL.createObjectURL(new Blob([res.data]))
          const link = document.createElement('a')
          link.href = url
          link.setAttribute('download', this.formatDate() + '.xlsx')
          document.body.appendChild(link)
          link.click()
        })
        .catch((error) => {
          console.log('config-res-error:', error)
        })
    },
    formatDate() {
      const date = new Date()
      const year = date.getFullYear()
      let month = date.getMonth() + 1
      let day = date.getDate()
      let hours = date.getHours()
      let minutes = date.getMinutes()

      // 如果月份或日期小于10，则在前面添加0
      month = month < 10 ? '0' + month : month
      day = day < 10 ? '0' + day : day
      hours = hours < 10 ? '0' + hours : hours
      minutes = minutes < 10 ? '0' + minutes : minutes

      return year + month + day + '_' + hours + minutes
    },
    resetSearch() {
      this.workPosition = ''
      this.dateRange = []
      this.timeStart = ''
      this.timeEnd = ''
      this.text = ''
      this.verifyStatus = ''
      this.verifyDeptId = ''
      this.deptId = "";
      this.auditStatus = "";
      if (this.activeName == 'fourth') {
        this.getDialogRecord();
      } else if (this.activeName == 'fifth') {
        this.getDialogRecordAnswer()
      }
    },
    viewClick(row, index) {
      this.setForm = row
      this.setIndex = index
      this.$set(this.formList, 'verifyAnswer', row.verifyAnswer || row.answer)
      this.activeIndex = 0
      this.$set(this.formList, 'verifyStatus', '')
      this.addQaVisible = true
    },
    handleAddQaDialog(type) {
      verifyRecord({
        dialogueId: this.setForm.dialogueId,
        ...this.formList,
      }).then((res) => {
        if (res.code == '000000') {
          this.$message.success('操作成功')
          if (type == 0) {
            this.addQaVisible = false
            setTimeout(()=>{
              this.getDialogRecord()
            }, 1000)
          } else {
            this.setIndex = this.setIndex + 1
            if (this.setIndex > this.dialogPagesize) {
              return this.$message.warning('该数据为该页最后一条')
            }
            this.setForm = this.dialogTableData[this.setIndex]
            this.$set(this.formList, 'verifyStatus', '')
          }
        }
      })
    },
    clickUser(type) {
      this.activeIndex = type
      if (type == 1) {
        this.toSource(this.setForm)
      }
    },
    getDeptTree() {
      deptTree({
        pageNo: 1,
        pageSize: 1000,
      }).then((res) => {
        if (res.code == '000000') {
          this.treeData = res.data || []
        }
      })
    },
    submitVerification() {
      this.handleAddQaDialog(1)
    },
    handleClose() {
      this.addQaVisible = false
      this.getDialogRecord()
    },
    getDialogRecordAnswer() {
      let deptId = '';
      if (this.deptId.length > 0) {
        deptId = this.deptId[this.deptId.length - 1]
      }
      getCheckRecord({
        timeStart: this.dateRange ? this.dateRange[0] : "",
        timeEnd: this.dateRange ? this.dateRange[1] : "",
        text: this.text,
        auditStatus: this.auditStatus,
        // verifyDeptName: this.verifyDeptName,
        applicationId: this.previewItem.applicationId,
        pageNo: this.currentPage,
        pageSize: this.dialogPagesize,
        deptId: deptId,
      }).then((res) => {
        if (res.code == "000000") {
          this.dialogTableDataAnswer = res.data.list || [];
          this.dialogTotal = res.data.total || 0;
        } else {
          this.dialogTableDataAnswer = [];
          this.dialogTotal = 0;
        }
      });
    },
    reviewAnswers(row, index) {
      this.setForm = row;
      this.setIndex = index
      this.addQaVisibleAnswer = true;
    },
    handlecloseDraw() {
      this.addQaVisibleAnswer = false;
      this.getDialogRecordAnswer()
    },
    handleAddQaDialogAns(auditStatus, type) {
      recordCheckRecord({
        applicationId: this.previewItem.applicationId,
        dialogueId: this.setForm.dialogueId,
        auditStatus: auditStatus
      }).then((res) => {
        if (res.code == '000000') {
          this.$message.success('操作成功')
          if (type == 0) {
            this.addQaVisibleAnswer = false
            setTimeout(()=>{
              this.getDialogRecordAnswer()
            }, 1000)
          } else {
            this.setIndex = this.setIndex + 1
            if (this.setIndex > this.dialogPagesize) {
              return this.$message.warning('该数据为该页最后一条')
            }
            this.setForm = this.dialogTableDataAnswer[this.setIndex]
          }
        }
      })
    },
    cancelGrant() {
      this.grantVisible = false;
    },
    getDefaultParameters() {
      this.defaultAppInfo();
    }
  },
}
</script>
<style lang="scss" scoped>
.outer {
  .hideBox ::v-deep .el-upload--picture-card {
    display: none;
  }
  ::v-deep .headTable th {
    background: #f2f5fa;
  }
  ::v-deep .heightInput {
    .el-input-group__append {
      border-radius: 0 4px 4px 0;
    }
    .el-input__inner {
      border-radius: 4px 0 0 4px;
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
    height: 8px;
  }
  ::v-deep .el-slider__bar {
    height: 8px;
    background: rgba(28, 80, 253, 0.3);
  }
  ::v-deep .el-slider__button {
    width: 24px;
    height: 24px;
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
    box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.1);
    border: 1px solid #ffffff;
  }
  ::v-deep .el-radio {
    .el-radio__label {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #383d47;
      line-height: 20px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
    &.is-checked {
      .el-radio__label {
        color: #383d47;
      }
      .el-radio__inner {
        background: #3666ea;
        border-color: #3666ea;
      }
    }
  }
  .outerTitle {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 22px;
    color: #383d47;
    line-height: 28px;
    text-align: left;
    font-style: normal;
    margin-bottom: 16px;
  }
  .appOuter {
    padding: 20px 24px;
    background: #fff;
    min-height: calc(100vh - 100px);
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    .headerContent {
      display: flex;
      justify-content: space-between;
      ::v-deep .input {
        width: 344px;
        .el-input__inner {
          border: 8px;
          border-radius: 4px 0 0 4px;
          border: 1px solid #e1e4eb;
        }
        .el-input-group__append {
          border-radius: 0 4px 4px 0;
        }
      }
      .el-button--primary {
        border: none;
        border-radius: 4px;
        background: #3666ea;

        img {
          width: 15px;
          height: 15px;
          margin-right: 5px;
        }
        span,
        img {
          vertical-align: middle;
        }
      }
    }
    .listOuter {
      display: flex;
      justify-content: flex-start;
      flex-wrap: wrap;
      gap: 16px;
      margin-top: 21px;
      .listItem {
        min-width: 400px;
        width: 32%;
        height: 160px;
        background: #ffffff;
        border-radius: 4px;
        border: 1px solid #e1e4eb;
        // padding: 20px 16px;
        position: relative;

        .sign {
          border-radius: 0px 8px 0px 8px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          line-height: 18px;
          text-align: left;
          font-style: normal;
          padding: 5px 10px;
          position: absolute;
          right: 0;
          top: 0;
          > img {
            width: 15px;
            height: 15px;
            margin-right: 2px;
          }
          > img,
          > span {
            vertical-align: middle;
          }
        }
        .successSign {
          color: #1abc7c;
          background: rgba(26, 188, 124, 0.1);
        }
        .failSign {
          color: #768094;
          background: rgba(118, 128, 148, 0.1);
        }

        .listContent {
          display: flex;
          justify-content: flex-start;

          // align-items: center;
          // gap: 16px;
          // padding: 16px;
          .headImg {
            width: 160px;
            border-radius: 4px 0 0 4px;
            // height:  144px;
          }
          .textContent {
            width: calc(100% - 160px);
            padding: 16px;
            font-family: MiSans, MiSans;
            position: relative;

            .title {
              width: calc(100% - 1px);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              font-weight: 500;
              font-size: 18px;
              color: #383d47;
              line-height: 30px;
              text-align: left;
              font-style: normal;
            }

            .desc {
              width: calc(100% - 1px);
              margin-top: 8px;
              display: -webkit-box;
              -webkit-box-orient: vertical;
              -webkit-line-clamp: 2;
              overflow: hidden;
              text-overflow: ellipsis;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              text-align: left;
              font-style: normal;
              line-height: 1.5;
            }

            .date {
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 24px;
              text-align: left;
              font-style: normal;
              width: calc(100% - 32px);
              position: absolute;
              left: 16px;
              bottom: 16px;
              display: flex;
              align-items: center;
              justify-content: space-between;
              .period-box {
                display: flex;
                align-items: center;
                .point {
                  width: 8px;
                  height: 8px;
                  position: relative;
                  background: rgba(28, 80, 253, 0.1);
                  border-radius: 50%;
                  margin: 4px;
                  &::before {
                    content: '';
                    width: 4px;
                    height: 4px;
                    background: #1c50fd;
                    border-radius: 50%;
                    position: absolute;
                    left: 2px;
                    top: 2px;
                  }
                }
              }
            }
            // >p:last-child{
            //     padding: 1px 4px 4px;
            //     display: inline-block;
            //     height: 24px;
            //     background: #fff;
            //     border-radius: 4px;
            //     >img {
            //         width: 15px;
            //         height: 15px;
            //         border-radius: 4px;
            //         margin-right: 3px;
            //     }
            //     >span{
            //         font-family: MiSans, MiSans;
            //         font-weight: 400;
            //         font-size: 14px;
            //         color: #768094;
            //         line-height: 18px;
            //         text-align: left;
            //         font-style: normal;
            //     }
            //     >span,>img{
            //         vertical-align: middle;
            //     }
            // }
          }
        }

        .fotterOuter {
          display: flex;
          justify-content: space-between;
          margin-top: 28px;
          .footerItem {
            width: 33.3%;
            text-align: center;
            cursor: pointer;
            &:not(:last-child) {
              border-right: 1px solid rgba(0, 0, 0, 0.12);
            }
            img,
            span {
              vertical-align: middle;
            }
            > span {
              color: #3666ea;
            }
            > img {
              width: 15px;
              height: 15px;
              margin-right: 3px;
            }
          }
        }
      }
    }
  }
  ::v-deep .flexDialog {
    background-color: #f2f5fa;
    padding: 0 33px 33px;
    .el-dialog {
      box-shadow: none !important;
      overflow: visible;
    }
    .el-dialog__header {
      display: none;
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
    .dialogTitle {
      margin-top: 30px;
      > img {
        width: 22px;
        height: 22px;
        margin-right: 4px;
      }
      > span {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        color: #383d47;
        line-height: 28px;
        text-align: left;
        font-style: normal;
      }
      > img,
      > span {
        vertical-align: middle;
      }
    }
    .el-dialog__body {
      background: #f2f5fa;
      padding: 77px 0 30px;
      position: relative;
      .verticalLine {
        width: 1px;
        background: rgba(0, 0, 0, 0.12);
        height: calc(100% + 32px);
        position: absolute;
        right: 0;
        top: 0;
      }
      .formOuter {
        width: 576px;
        margin-top: 24px;
        padding-right: 30px;
        .el-textarea {
          .el-textarea__inner {
            border-radius: 4px;
            border: 1px solid #e1e4eb;
          }
        }
        .el-input:not(.el-input-group) {
          .el-input__inner {
            border-radius: 4px;
            border: 1px solid #e1e4eb;
          }
        }
        .marginTop32 {
          margin-top: 32px;
          .el-upload-list__item {
            display: flex;
            align-items: center;
          }
          .identityUpload {
            .el-upload-list--picture-card .el-upload-list__item-thumbnail {
              height: auto !important;
            }
            .el-upload--picture-card {
              width: 104px;
              height: 104px;
              border: none;
              background: #f2f4f7;
              > div {
                height: 0;
                background: pink;
                position: relative;
                top: -19px;
              }
            }
            .el-upload-list__item {
              width: 104px;
              height: 104px;
            }
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
        .uploadImg {
          display: flex;
          justify-content: space-between;
          .logoUpload {
            .el-upload-list__item {
              display: flex;
              align-items: center;
            }
            .el-upload-list--picture-card .el-upload-list__item-thumbnail {
              height: auto !important;
            }
            .el-upload--picture-card {
              width: 104px;
              height: 104px;
              border: none;
              background: #f2f4f7;
              > div {
                height: 0;
                background: pink;
                position: relative;
                top: -19px;
              }
            }
            .el-upload-list__item {
              width: 104px;
              height: 104px;
            }
          }
          .el-textarea {
            margin-top: 16px;
          }
        }
        .formFlexOuter {
          display: flex;
          justify-content: flex-start;
          margin-top: 24px;
          .el-input-number__increase {
            border-radius: 0 4px 4px 0;
          }
          .el-input-number__decrease {
            border-radius: 4px 0 0 4px;
          }
          .assModel {
            .el-input__inner {
              height: 56px;
              font-size: 16px;
              color: #000;
              font-weight: 500;
            }
          }
          .robotImg {
            margin-left: 32px;
            position: relative;
            .el-upload-list--picture-card .el-upload-list__item-thumbnail {
              height: auto !important;
            }
            .el-upload-list__item {
              width: 40px !important;
              height: 40px !important;
              border-radius: 50%;
              display: flex;
              align-items: center;
            }
            .el-upload--picture-card {
              width: 40px;
              height: 40px;
              border-radius: 50%;
              position: relative;
              border: none;
              background: #f2f4f7;
              .el-icon-plus {
                font-size: 14px;
                position: absolute;
                top: calc(50% - 7px);
                right: calc(50% - 7px);
              }
            }
          }
          .widthSpan {
            display: inline-block;
            width: 113px;
            color: #768094 !important;
          }
          .el-switch__label > span {
            font-size: 16px !important;
          }
        }
      }
      .collapseStyle {
        border-top: none;
        .el-collapse-item__arrow {
          display: none;
        }
        .el-collapse-item__header {
          position: relative;
        }
        .el-collapse-item {
          .svgTransform {
            transform: rotate(-90deg);
            transition: all 0.2s;
          }
          &.is-active {
            .svgTransform {
              transform: rotate(0deg);
            }
          }
        }
        .el-collapse-item__wrap {
          padding-left: 10px;
        }
        .collapseHeader {
          > div {
            > span {
              color: #383d47;
              font-size: 16px !important;
              font-weight: 500;
            }
          }
          img,
          span {
            vertical-align: middle;
          }
        }
      }
    }
  }
  ::v-deep .configDialog {
    .el-dialog__title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
    .flexOuter {
      display: flex;
      justify-content: flex-start;
      .el-slider__runway {
        width: 300px;
      }
      > span:first-child {
        display: inline-block;
        width: 350px;
        position: relative;
        top: 11px;
        font-size: 16px;
        color: #383d47;
      }
    }
    .footer {
      margin-top: 30px;
      .el-button--primary {
        border-radius: 4px;
        background: #3666ea;
        border-color: #3666ea;
      }
      .el-button.is-plain {
        border-radius: 4px;
        float: right;
        color: #3666ea;
        background: #fff;
        span,
        img {
          vertical-align: middle;
        }
      }
    }
  }
  ::v-deep .deleteDialog {
    .el-dialog__header {
      background: linear-gradient(
        180deg,
        rgba(43, 88, 213, 0.1) 0%,
        rgba(43, 88, 213, 0) 100%
      );
    }
    .el-dialog__title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
    .desc {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #768094;
      line-height: 20px;
      text-align: left;
      font-style: normal;
      margin-bottom: 8px;
    }
    .el-dialog__footer {
      text-align: left;
      .el-button {
        border-radius: 4px;
      }
      .el-button--primary {
        background: #dc2544;
        color: #fff;
        border-color: transparent;
      }
    }
  }
  ::v-deep .fabuDialog {
    .el-dialog__title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
    .el-dialog__header {
      background: linear-gradient(
        180deg,
        rgba(43, 88, 213, 0.1) 0%,
        rgba(43, 88, 213, 0) 100%
      );
    }
    .el-checkbox.is-checked {
      .el-checkbox__inner {
        background-color: #3666ea;
        border-color: #3666ea;
      }
      .el-checkbox__label {
        display: inline-block;
        color: #3666ea;
      }
    }
    .radioOuter {
      width: 496px;
      height: 104px;
      border-radius: 4px;
      border: 1px solid #e1e4eb;
      margin-bottom: 12px;
      padding: 12px;
      > p {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #768094;
        line-height: 20px;
        text-align: justify;
        font-style: normal;
        margin-top: 6px;
        padding-left: 25px;
      }
    }
    .el-button--primary {
      background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
      color: #fff;
      border-color: transparent;
      height: 40px;
    }
    .templateList {
      width: 190px;
      height: 400px;
      overflow-y: scroll;
      text-align: center;
      .el-radio__input {
        .el-radio__inner {
          display: none;
        }
        &.is-checked + .el-radio__label {
          img {
            border: 2px solid #1c50fd !important;
            border-radius: 4px;
          }
        }
      }
    }
    .previewItem {
      margin: 0 auto;
    }
    .el-radio:last-child {
      margin-right: 30px;
    }
    .el-dialog__footer {
      text-align: left;
      .el-button {
        border-radius: 4px;
      }
      .el-button--primary {
        background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
        color: #fff;
        border-color: transparent;
        height: 40px;
        &.is-disabled {
          opacity: 0.6;
        }
        img {
          width: 15px;
          height: 15px;
          margin-right: 3px;
        }
        span,
        img {
          vertical-align: middle;
        }
      }
    }
  }
  ::v-deep .preViewDialog {
    .el-dialog {
      border-radius: 0 0 8px 8px;
      background: #f2f5fa;
    }
    .el-dialog__body {
      padding: 82px 0 30px;
    }
    .el-tabs {
      .el-tabs__item {
        color: #383d47;
        font-size: 18px;
      }
      .is-active {
        color: #181b49;
      }
      .el-tabs__active-bar {
        background: #3470ff;
      }
      .el-tabs__nav-wrap::after {
        height: 1px;
        background: rgba(0, 0, 0, 0.12);
      }
    }
    .tabContentTitle {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 18px;
      color: #181b49;
      text-align: left;
      font-style: normal;
      border-left: 3px solid #3666ea;
      padding-left: 16px;
      margin: 28px 0 16px;
    }
    .tabContents {
      display: flex;
      justify-content: space-between;
      > .contentInner {
        width: calc(50% - 8px);
        height: 226px;
        padding: 26px;
        border-radius: 4px;
        border: 1px solid #e1e4eb;
        .title {
          display: flex;
          justify-content: space-between;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 18px;
          color: #181b49;
          text-align: left;
          font-style: normal;
          margin-bottom: 24px;
        }
        .desc {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #768094;
          line-height: 18px;
          text-align: left;
          font-style: normal;
          margin-bottom: 8px;
        }
        .secondLine {
          display: flex;
          justify-content: flex-start;
          margin-bottom: 24px;
          .inp {
            margin-right: 30px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            height: 40px;
            width: 500px;
            background: #f2f4f7;
            border-radius: 4px;
            border: 1px solid #e1e4eb;
            line-height: 36px;
            padding: 0 16px;
            position: relative;
            > img {
              width: 16px;
              height: 16px;
              position: absolute;
              right: 10px;
              top: 12px;
              cursor: pointer;
            }
          }
          .el-button {
            font-size: 16px;
            color: #3666ea;
            border-color: #3666ea;
            img {
              width: 15px;
              height: 15px;
              margin-right: 3px;
            }
            img,
            span {
              vertical-align: middle;
            }
          }
        }
        .thirdLine {
          .el-button {
            img {
              width: 15px;
              height: 15px;
              margin-right: 3px;
            }
            img,
            span {
              vertical-align: middle;
            }
          }
          .el-button--primary {
            border: 1px solid #3666ea;
            background: #3666ea;
            color: #fff;
          }
          .borderBtn {
            border: 1px solid #3666ea;
            color: #3666ea;
            margin-right: 16px;
          }
        }
      }
    }
  }
}

.elDrawer {
  ::v-deep .el-drawer__header {
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
    border-radius: 8px 8px 0px 0px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 20px;
    color: #383d47;
    line-height: 24px;
  }
  ::v-deep .el-drawer__body {
    padding: 0 24px 10px;
  }
}

.userInfo {
  margin: 0 0 20px 0;
  span {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
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

.content {
  display: flex;
  ::v-deep .el-textarea.leftText {
    margin-right: 16px;
    .el-textarea__inner {
      border: 1px solid #f2f5fa;
    }
  }
  ::v-deep .el-textarea {
    width: 49.5%;
  }
  .traceability {
    width: 49.5%;
    background: #f2f5fa;
    height: calc(100vh - 256px) !important;
    margin-right: 16px;
    overflow-y: auto;
    padding: 6px 14px;
    ::v-deep .el-collapse-item__header {
      height: 40px;
      line-height: 40px;
      background-color: #f2f5fa;
      font-size: 16px;
      color: #383d47;
    }
    ::v-deep .el-collapse-item__content {
      background-color: #f2f5fa;
      font-size: 14px;
      color: #828894;
    }
  }
  ::v-deep .el-textarea__inner {
    background: #f2f5fa;
    height: calc(100vh - 256px) !important;
    color: #606266;
  }
}

.verifyStatusAnswer {
  ::v-deep .el-textarea__inner {
    background: #fff;
  }
}
.footerDrawer {
  margin-top: 24px;
}

.userList {
  padding: 10px 28px;
  // height: 40px;
  background: #f2f5fa;
  border-radius: 4px;
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 16px;
  color: #828894;
  line-height: 20px;
  margin-right: 8px;
  cursor: pointer;
}

.activeList {
  background: rgba(28, 80, 253, 0.1);
  color: #1c50fd;
}

.formKey {
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 14px;
  color: #828894;
}
.formValue {
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 14px;
  color: #383d47;
}
</style>
