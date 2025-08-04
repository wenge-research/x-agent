<template>
  <el-drawer
    :title="$t('displaySettings')"
    :visible.sync="dialogVisible"
    :direction="direction"
    :before-close="cancelConfig"
    append-to-body
  >
    <div class="drawer-box">
      <div class="flex-center just">
        <div class="flex-center">
          <span class="line"></span>
          <span class="titleName"> {{ $t("selectTemplate") }} </span>
        </div>
      </div>
      <!-- 选择模板 -->
      <div class="select-template">
        <div class="select-template-left">
          <!-- <span class="tips">{{ $t("default") }}</span> -->
          <el-image
            style="height: 100%"
            :src="filterTemplateImg(setForm.templateId)"
            fit="cover"
          ></el-image>
        </div>
        <el-button
          type="text"
          icon="el-icon-refresh"
          style="color: #494E57; font-size: 14px"
          @click="openTemplate"
          >{{ $t("replaceTemplate") }}</el-button
        >
      </div>
      <!-- 背景图 -->
      <div class="background-image">
        <div>
          <div class="background-image-title-box">
            <span class="background-image-title">{{
              $t("backgroundImage")
            }}</span>
            <el-switch
              :active-value="$t('yes')"
              :inactive-value="$t('no')"
              active-color="#1747E5"
              inactive-color="#EAECF0"
            >
            </el-switch>
          </div>
          
          <p class="background-image-tips">{{ $t("uploadImgTips") }}</p>
        </div>
        <el-upload
          :action="actionUrl"
          :data="{ filePath: 'agent_source' }"
          :class="{ hideBox: uploadTemplateImg }"
          :file-list="fileTemplateImgList"
          :limit="1"
          list-type="picture-card"
          :on-remove="handleTemplateImgRemove"
          :on-success="handleTemplateImgSuccess"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
      </div>
      <!-- 欢迎页 -->
      <div class="welcome-page marginTop32">
        <div class="flex-center">
          <span class="line"></span>
          <span class="titleName"> {{ $t("welcomePage") }} </span>
        </div>
        <div class="welcome-page-show">
          <div class="welcome-page-show-inner">
            <el-image
              v-if="assistantImage"
              :src="setForm.facadeImageUrl"
              
              style="height:100%;width: 30%"
              fit="scale-down"
            ></el-image>
            <div class="welcome-page-show-inner-right">
              <div
                v-if="
                  !setForm.identityIcon?.includes('.jpg') &&
                  !setForm.identityIcon?.includes('.png') &&
                  !setForm.identityIcon?.includes('.gif')
                "
                class="identityIcon"
              >
                {{ setForm.identityIcon }}
              </div>
              <el-image
                v-else
                style="height: 100%"
                :src="setForm.identityIcon"
                fit="cover"
              ></el-image>
              <div class="greeting">{{ setForm.greeting }}</div>
            </div>
          </div>
        </div>
        <div class="mt16">
          <div class="welcome-page-selfIntroduction">
            <div class="welcome-page-selfIntroduction-title">
              {{ $t("selfIntroduction") }}
            </div>
            <el-radio-group
              v-removeAriaHidden
              v-model="assistantRadio"
              class="marginLeft20"
              @input="changeRadio"
            >
              <el-radio label="1">{{ $t("words") }}</el-radio>
              <el-radio label="2">{{ $t("picture") }}</el-radio>
            </el-radio-group>
          </div>
          <div style="margin-top: 6px" v-if="assistantRadio == '1'">
            <el-input
              :placeholder="$t('selfIntroductionTips')"
              v-model="setForm.identityIcon"
              clearable
              size="small"
            >
            </el-input>
          </div>
          <div v-else style="margin-top: 6px">
            <el-upload
              :action="actionUrl"
              :data="{ filePath: 'agent_source' }"
              :class="{ hideBox: uploadBtnIdentityIcon }"
              :file-list="fileListIdentityIcon"
              :limit="1"
              class="upload"
              list-type="picture-card"
              :on-remove="handleIdentityIconRemove"
              :on-success="handleIdentityIconSuccess"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
          </div>
        </div>
        <div class="mt16">
          <div class="welcome-page-selfIntroduction-title">
            {{ $t("functionIntroduction") }}
          </div>
          <div style="margin-top: 6px">
            <el-input
              :placeholder="$t('describeFunction')"
              v-model="setForm.greeting"
              type="textarea"
              :rows="4"
              clearable
              show-word-limit
              maxlength="100"
            >
            </el-input>
          </div>
        </div>
        <div class="mt16 flex-center just">
          <div>
            <div class="flex-center">
              <span class="welcome-page-selfIntroduction-title"
                >{{ $t("assistantImage") }}
                <!-- <img src="@/assets/images/question-line.svg" /> -->
              </span>
              <el-switch
                v-model="assistantImage"
                @change="changeAssistant"
                style="margin-left: 8px"
              >
              </el-switch>
            </div>
            <p class="img-tips" v-if="assistantImage">
              {{ $t("uploadImgTips") }}
            </p>
          </div>

          <div class="flex-center" v-if="assistantImage">
            <el-upload
              :action="actionUrl"
              :data="{ filePath: 'agent_source' }"
              :class="{ hideBox: uploadBtnAssistant }"
              :file-list="fileListAssistant"
              :limit="1"
              class="upload"
              list-type="picture-card"
              :on-remove="handleAssistantRemove"
              :on-success="handleAssistantSuccess"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
          </div>
        </div>
      </div>
      <!-- 输入框提示语 -->
      <div class="mt16">
        <span class="welcome-page-selfIntroduction-title">{{
          $t("inputPrompt")
        }}</span>
        <div style="margin-top: 6px">
          <el-input
            :placeholder="$t('inputPrompt')"
            v-model="setForm.inputPlaceholder"
            clearable
            size="small"
          >
          </el-input>
        </div>
      </div>
      <!-- 对话气泡图标 -->
      <div class="dialogue-bubble marginTop32">
        <div class="flex-center just">
          <div class="flex-center">
            <span class="line"></span>
            <span class="titleName" style="margin-bottom: 0px">
              {{ $t("dialogueBubbleIcon") }}
            </span>
          </div>
          <el-switch v-model="dialogueIcon" @change="changeDialogueIcon">
          </el-switch>
        </div>
        <div v-if="dialogueIcon" class="dialogue-bubble-show">
          <div class="dialogue-bubble-show-row">
            <div class="dialogue-bubble-show-row-img">
              <el-image
                style="height: 100%"
                :src="setForm.robotIcon"
                fit="cover"
              ></el-image>
            </div>
            <div class="top-right"></div>
          </div>
          <div class="dialogue-bubble-show-row" style="margin-top: 12px">
            <div class="dialogue-bubble-show-row-img">
              <el-image
                style="height: 100%"
                :src="setForm.userIcon"
                fit="cover"
              ></el-image>
            </div>
            <div class="bottom-right"></div>
          </div>
        </div>
        <div class="dialogue-bubble-upload mt16" v-if="dialogueIcon">
          <div class="flex-center just">
            <div>
              <div class="dialogue-bubble-upload-title">
                {{ $t("assistantIcon") }}
                <!-- <img src="@/assets/images/question-line.svg" /> -->
              </div>
              <p class="dialogue-bubble-upload-tips">
                {{ $t("uploadImgTips") }}
              </p>
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
          <div class="flex-center just mt16">
            <div>
              <div class="dialogue-bubble-upload-title">
                {{ $t("userIcon") }}
                <!-- <img src="@/assets/images/question-line.svg" /> -->
              </div>
              <p class="dialogue-bubble-upload-tips">
                {{ $t("uploadImgTips") }}
              </p>
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
      <!-- 网页设置 -->
      <div class="webpage-settings marginTop32">
        <div class="flex-center">
          <span class="line"></span>
          <span class="titleName"> {{ $t("webPageSettings") }} </span>
        </div>
        <div class="webpage-settings-show">
          <div v-if="setForm.webIcon" class="webpage-settings-show-icon">
            <el-image
              style="height: 100%"
              :src="setForm.webIcon"
              fit="cover"
            ></el-image>
          </div>
        </div>
        <div class="uploadImg mt16">
          <div>
            <span class="webpage-settings-title" style="margin-bottom: 6px"
              >{{ $t("webPageTitle") }}
              <!-- <img src="@/assets/images/question-line.svg" /> -->
            </span>
            <el-input
              style="width: 100%"
              :placeholder="$t('inputPlaceholder')"
              v-model="setForm.webTitle"
              size="small"
            ></el-input>
          </div>
          <div>
            <span class="webpage-settings-title" style="margin-bottom: 6px"
              >{{ $t("applicationCode") }}
              <!-- <img src="@/assets/images/question-line.svg" /> -->
            </span>
            <el-input
              :placeholder="$t('inputPlaceholder')"
              style="width: 100%"
              v-model="setForm.applicationCode"
              @input="validTextEn(setForm.applicationCode)"
              size="small"
            ></el-input>
          </div>
        </div>
        <div class="mt16 flex-center just">
          <div>
            <div class="webpage-settings-title">
              {{ $t("webTagIcon") }}
              <!-- <img src="@/assets/images/question-line.svg" /> -->
            </div>
            <p class="webpage-settings-tips">{{ $t("uploadImgTips") }}</p>
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
      </div>
      <!-- 附加功能 -->
      <div class="additional-features marginTop32">
        <div class="titleName">{{ $t("additionalFeatures") }}</div>
        <div class="mt16 flex-center just">
          <div>
            <div class="webpage-settings-title">
              {{ $t("belongingLogo") }}
              <!-- <img src="@/assets/images/question-line.svg" /> -->
            </div>
            <p class="webpage-settings-tips">{{ $t("uploadImgTips") }}</p>
          </div>
          <el-upload
            :action="actionUrl"
            :data="{ filePath: 'agent_source' }"
            list-type="picture-card"
            :limit="1"
            :class="{ hideBox: uploadBtnAttributionLogo }"
            :file-list="fileListAttributionLogo"
            class="robotImg"
            :on-remove="handleAttributionLogoRemove"
            :on-success="handleAttributionLogoSuccess"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
        </div>
        <div class="additional-features-row mt16">
          <div class="additional-features-row-head flex-center just">
            <div class="additional-features-row-head-title">
              {{ $t("returnToOldVersion") }}
            </div>
            <el-switch
              v-model="isBackOldVersionLink"
              @change="isBackOldVersionLinkChange"
            >
            </el-switch>
          </div>
          <el-input
            v-show="isBackOldVersionLink"
            v-model="setForm.backOldVersionLink"
            :placeholder="$t('enterTheLinkToJumpTo')"
            size="small"
          />
        </div>
        <div class="additional-features-row mt16">
          <div class="additional-features-row-head flex-center just">
            <div class="additional-features-row-head-title">
              {{ $t("bottomInformationBar") }}
            </div>
            <el-switch v-model="isAboutWebsite" @change="isAboutWebsiteChange">
            </el-switch>
          </div>
          <div
            v-show="isAboutWebsite"
            class="edit-content flex-center"
            @click="openEditorDialog"
          >
            <iconpark-icon
              name="edit-line"
              color="#383D47"
              style="margin-right: 6px"
            ></iconpark-icon
            >{{ $t("editContent") }}
          </div>
        </div>
        <div class="additional-features-row mt16">
          <div class="additional-features-row-head flex-center just">
            <div class="additional-features-row-head-title">
              {{ $t("convenientService") }}
            </div>
            <el-switch
              v-model="setForm.menuServiceFlag"
              @change="menuServiceFlagChange"
              :active-value="$t('yes')"
              :inactive-value="$t('no')"
            >
            </el-switch>
          </div>
        </div>
        <div class="additional-features-row mt16">
          <div class="additional-features-row-head flex-center just">
            <div class="additional-features-row-head-title">
              {{ $t("policyList") }}
            </div>
            <el-switch
              v-model="setForm.policyListFlag"
              @change="policyListFlagChange"
              :active-value="$t('yes')"
              :inactive-value="$t('no')"
            >
            </el-switch>
          </div>
        </div>
        <div class="additional-features-row mt16">
          <div class="additional-features-row-head flex-center just">
            <div class="additional-features-row-head-title">
              {{ $t("feedback") }}
            </div>
            <el-switch
              v-model="setForm.feedbackFlag"
              @change="feedbackFlagChange"
              :active-value="$t('yes')"
              :inactive-value="$t('no')"
            >
            </el-switch>
          </div>
        </div>
        <div class="additional-features-row mt16">
          <div class="additional-features-row-head flex-center just">
            <div class="additional-features-row-head-title">
              {{ $t("historicalDialogue") }}
            </div>
            <el-switch
              v-model="setForm.historyFlag"
              @change="historyFlagChange"
              :active-value="$t('yes')"
              :inactive-value="$t('no')"
            >
            </el-switch>
          </div>
        </div>
      </div>
    </div>
    <div class="footer-btn flex-center">
      <el-button plain @click="cancelConfig">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="okHandler">{{
        $t("confirm")
      }}</el-button>
    </div>
    <el-drawer
      :title="$t('selectTemplate')"
      :visible.sync="templateVisible"
      width="960px"
      class="selectTemplate"
      destroy-on-close
      :before-close="cancelTemplate"
      append-to-body
    >
      <div slot="title" class="flex-center select-template-title">
        <div>{{ $t('selectTemplate') }}</div>
        <div class="tabs-box">
          <div
            class="temp-btn flex-center just"
            :class="{ active: templateType == 'PC' }"
            @click="changeTemplate('PC')"
          >
            <div class="temp-btn-content">
              <iconpark-icon name="macbook-line" size="16"></iconpark-icon>
              <span> PC </span>
            </div>
            <i class="el-icon-success" style="color: #4bbe25"></i>
            
          </div>
          <div
            class="temp-btn flex-center just"
            :class="{ active: templateType == 'H5' }"
            @click="changeTemplate('H5')"
            style="margin-left: 10px"
          >
            <div class="temp-btn-content"><iconpark-icon name="smartphone-line" size="16"></iconpark-icon><span> H5 </span></div>
            <i class="el-icon-success" style="color: #4bbe25"></i>
            
          </div>
        </div>
      </div>
      <div class="drawer-box">
        <div class="templateList templateList-pc" v-if="templateType == 'PC'">
          <div
            class="templateList-item"
            v-for="item in templateDataList"
            :key="item.id"
            @click="changeTemplatePC(item)"
          >
            <div
              class="templateItem"
              :class="{ active: templateSign == item.templateId }"
            >
              <span class="tips" v-if="templateSign == item.templateId">{{
                $t("default")
              }}</span>
              <el-image
                :src="item.picturePath"
                fit="cover"
                class="imgs"
              ></el-image>
            </div>
          </div>
        </div>
        <div class="templateList" v-if="templateType == 'H5'">
          <div
            class="templateList-item"
            v-for="item in mobileTemplateDataList"
            :key="item.id"
            @click="changeTemplateH5(item)"
          >
            <div
              class="templateItem"
              :class="{
                active: mobileTemplateSign == item.templateId,
              }"
            >
              <span class="tips" v-if="mobileTemplateSign == item.templateId">{{
                $t("default")
              }}</span>
              <el-image :src="item.picturePath" fit="cover" class="imgsH5">
              </el-image>
            </div>
            <div class="templateName">{{ item.templateName }}</div>
          </div>
        </div>
      </div>
      <div class="footer-btn flex-center">
        <el-button @click="cancelTemplate">{{ $t("cancel") }}</el-button>
        <el-button type="primary" @click="confirmTemplate">{{
          $t("select")
        }}</el-button>
      </div>
    </el-drawer>
    <!-- 底部栏信息 -->
    <wangeditor
      ref="wangeditor"
      v-model="wangeditorVisible"
      @close="closeWangeditorDialog"
      :statement="setForm.aboutWebsite"
      @editStatementValue="editStatementValue"
    ></wangeditor>
  </el-drawer>
</template>
          
<script>
import { templateList } from "@/api/app";
import wangeditor from "./wangeditor";
export default {
  components: { wangeditor },
  data() {
    return {
      direction: "rtl",
      actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
      // actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      setForm: {
        webTitle: "",
        applicationCode: "",
        robotIcon: "",
        webIcon: "",
        attributionLogo: "",
        userIcon: "",
        identityIcon: "",
        mobileTemplateId: "",
        templateId: "",
        facadeImageUrl: "",
        backgroundImageUrl: "",
        inputPlaceholder: "",
        greeting: "", // 功能介绍
        backOldVersionLink: "", // 跳转链接
        aboutWebsite: "", // 关于网站，html富文本
        menuServiceFlag: "否", // 便民服务
        policyListFlag: "否", // 政策列表
        feedbackFlag: "否", // 反馈 (踩与赞)
        historyFlag: "否", // 历史对话
      },
      isBackOldVersionLink: false, // 是否打开跳转链接
      isAboutWebsite: false, // 是否显示关于网站栏目
      wangeditorVisible: false,
      // 模版背景图
      uploadTemplateImg: false,
      fileTemplateImgList: [],

      // 助手形象
      fileListAssistant: [],
      uploadBtnAssistant: false,
      // 自我介绍
      fileListIdentityIcon: [],
      uploadBtnIdentityIcon: false,

      assistantImage: true,
      assistantRadio: "1",
      selfContent: "", // 自我介绍
      dialogueIcon: true, //对话助手开关
      // 助手&用户
      uploadBtnRobot: false,
      uploadBtnUserIcon: false,
      uploadBtnWebIcon: false,
      uploadBtnAttributionLogo: false,
      fileListUserIcon: [],
      fileListRobot: [],
      fileListwebIcon: [],
      fileListAttributionLogo: [],
      // 选择模版
      templateVisible: false,
      templateType: "PC",
      mobileTemplateDataList: [],
      templateDataList: [],
      templateSign: "",
      mobileTemplateSign: "",
    };
  },
  props: {
    dialogVisible: Boolean,
    params: Object,
  },
  mounted() {
    this.setDefaultInfo(this.params);
    this.getTemplateList();
    this.getMobileTemplateList();
  },
  methods: {
    // 返回旧版
    isBackOldVersionLinkChange(v) {
      if (!v) {
        this.setForm.backOldVersionLink = this.params?.backOldVersionLink;
      }
    },
    // 底部信息栏
    isAboutWebsiteChange(v) {
      console.log("isAboutWebsiteChange====", v);
      if (!v) {
        this.setForm.aboutWebsite = this.params?.aboutWebsite;
      }
    },
    // 便民服务
    menuServiceFlagChange(v) {
      console.log("menuServiceFlagChange====", v);
    },
    // 政策列表
    policyListFlagChange(v) {
      console.log("policyListFlagChange====", v);
    },
    // 反馈
    feedbackFlagChange(v) {
      console.log("feedbackFlagChange====", v);
    },
    // 历史对话
    historyFlagChange(v) {
      console.log("historyFlagChange====", v);
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
      this.setForm.aboutWebsite = html;
      this.wangeditorVisible = false;
    },
    setDefaultInfo(item) {
      console.log("setDefaultInfo", item);
      this.uploadTemplateImg = item.backgroundImageUrl ? true : false;
      this.uploadBtnAssistant = item.facadeImageUrl ? true : false;
      this.uploadBtnRobot = item.robotIcon ? true : false;
      this.uploadBtnWebIcon = item.webIcon ? true : false;
      this.uploadBtnAttributionLogo = item.attributionLogo ? true : false;
      this.uploadBtnUserIcon = item.userIcon ? true : false;
      this.uploadBtnIdentityIcon = item.identityIcon ? true : false;
      this.templateSign = item.templateId;
      this.mobileTemplateSign = item.mobileTemplateId;
      this.dialogueIcon = false;
      this.isBackOldVersionLink = item.backOldVersionLink ? true : false;
      this.isAboutWebsite = item.aboutWebsite ? true : false;

      // 模版
      if (item.backgroundImageUrl) {
        this.fileTemplateImgList = [
          {
            name: "backgroundImageUrl",
            url: item.backgroundImageUrl,
          },
        ];
      } else {
        this.fileTemplateImgList = [];
      }
      // 助手形象
      if (item.facadeImageUrl) {
        this.fileListAssistant = [
          {
            name: "facadeImageUrl",
            url: item.facadeImageUrl,
          },
        ];
      } else {
        this.fileListAssistant = [];
      }
      if (item.userIcon) {
        this.fileListUserIcon = [
          {
            name: "useIcon",
            url: item.userIcon,
          },
        ];
      } else {
        this.fileListUserIcon = [];
      }

      if (item.webIcon) {
        this.fileListwebIcon = [
          {
            name: "webIcon",
            url: item.webIcon,
          },
        ];
      } else {
        this.fileListwebIcon = [];
      }

      if (item.attributionLogo) {
        this.fileListAttributionLogo = [
          {
            name: "attributionLogo",
            url: item.attributionLogo,
          },
        ];
      } else {
        this.fileListAttributionLogo = [];
      }

      if (item.robotIcon) {
        this.fileListRobot = [
          {
            name: "robot",
            url: item.robotIcon,
          },
        ];
      } else {
        this.fileListRobot = [];
      }

      if (item.identityIcon) {
        this.fileListIdentityIcon = [
          {
            name: "identityIcon",
            url: item.identityIcon,
          },
        ];
      } else {
        this.fileListIdentityIcon = [];
      }
      if (item.userIcon || item.robotIcon) {
        this.dialogueIcon = true;
      }
      this.setForm = {
        webTitle: item.webTitle,
        applicationCode: item.applicationCode,
        robotIcon: item.robotIcon,
        webIcon: item.webIcon,
        attributionLogo: item.attributionLogo,
        userIcon: item.userIcon,
        identityIcon: item.identityIcon,
        templateIcon: "",
        mobileTemplateId: item.mobileTemplateId,
        templateId: item.templateId,
        facadeImageUrl: item.facadeImageUrl,
        backgroundImageUrl: item.backgroundImageUrl,
        greeting: item.greeting,
        inputPlaceholder: item.inputPlaceholder,
        backOldVersionLink: item.backOldVersionLink,
        aboutWebsite: item.aboutWebsite,
        menuServiceFlag: item.menuServiceFlag,
        policyListFlag: item.policyListFlag,
        feedbackFlag: item.feedbackFlag,
        historyFlag: item.historyFlag,
      };
      this.assistantImage = this.isEmptyAll([
        "identityIcon",
        "facadeImageUrl",
        "greeting",
        "inputPlaceholder",
      ]);
      this.assistantRadio = this.setForm?.identityIcon?.includes("https")
        ? "2"
        : "1";
    },
    filterTemplateImg(item) {
      let findItem = this.templateDataList.find(
        (items) => items.templateId == item
      );
      return findItem?.picturePath;
    },
    // 模版背景图
    handleTemplateImgRemove(file, fileList) {
      this.uploadTemplateImg = false;
      this.setForm.backgroundImageUrl = "";
      this.fileTemplateImgList = [];
    },
    handleTemplateImgSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadTemplateImg = true;
        this.setForm.backgroundImageUrl =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileTemplateImgList = [
          {
            name: "backgroundImageUrl",
            url: this.setForm.backgroundImageUrl,
          },
        ];
      } else {
        this.uploadTemplateImg = false;
        this.setForm.backgroundImageUrl = "";
        this.fileTemplateImgList = [];
      }
    },
    // 助手形象
    handleAssistantRemove(file, fileList) {
      this.uploadBtnAssistant = false;
      this.setForm.facadeImageUrl = "";
      this.fileListAssistant = [];
    },
    handleAssistantSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnAssistant = true;
        this.setForm.facadeImageUrl =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListAssistant = [
          {
            name: "facadeImageUrl",
            url: this.setForm.facadeImageUrl,
          },
        ];
      } else {
        this.uploadBtnAssistant = false;
        this.setForm.facadeImageUrl = "";
        this.fileListAssistant = [];
      }
    },
    // 自我介绍
    handleIdentityIconSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnIdentityIcon = true;
        this.setForm.identityIcon =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListIdentityIcon = [
          {
            name: "identityIcon",
            url: this.setForm.identityIcon,
          },
        ];
      } else {
        this.uploadBtnIdentityIcon = false;
        this.setForm.identityIcon = "";
        this.fileListIdentityIcon = [];
      }
    },
    handleIdentityIconRemove(file, fileList) {
      this.uploadBtnIdentityIcon = false;
      this.setForm.identityIcon = "";
      this.fileListIdentityIcon = [];
    },
    // 机器人
    handleRobotSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnRobot = true;
        this.setForm.robotIcon =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListRobot = [
          {
            name: "robot",
            url: this.setForm.robotIcon,
          },
        ];
      } else {
        this.uploadBtnRobot = false;
        this.setForm.robotIcon = "";
        this.fileListRobot = [];
      }
    },
    handleRobotRemove(file, fileList) {
      this.uploadBtnRobot = false;
      this.setForm.robotIcon = "";
      this.fileListRobot = [];
    },
    // 用户
    handleUserSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnUserIcon = true;
        this.setForm.userIcon =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListUserIcon = [
          {
            name: "userIcon",
            url: this.setForm.userIcon,
          },
        ];
      } else {
        this.uploadBtnUserIcon = false;
        this.setForm.userIcon = "";
        this.fileListUserIcon = [];
      }
    },
    handleUserRemove(file, fileList) {
      this.uploadBtnUserIcon = false;
      this.setForm.userIcon = "";
      this.fileListUserIcon = [];
    },
    // 网页
    handleWebSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnWebIcon = true;
        this.setForm.webIcon =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListwebIcon = [
          {
            name: "webIcon",
            url: this.setForm.webIcon,
          },
        ];
      } else {
        this.uploadBtnWebIcon = false;
        this.setForm.webIcon = "";
        this.fileListwebIcon = [];
      }
    },
    handleWebRemove(file, fileList) {
      this.uploadBtnWebIcon = false;
      this.setForm.webIcon = "";
      this.fileListwebIcon = [];
    },
    // 归属logo
    handleAttributionLogoSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnAttributionLogo = true;
        this.setForm.attributionLogo =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListAttributionLogo = [
          {
            name: "attributionLogo",
            url: this.setForm.attributionLogo,
          },
        ];
      } else {
        this.uploadBtnAttributionLogo = false;
        this.setForm.attributionLogo = "";
        this.fileListAttributionLogo = [];
      }
    },
    handleAttributionLogoRemove(file, fileList) {
      this.uploadBtnAttributionLogo = false;
      this.setForm.attributionLogo = "";
      this.fileListAttributionLogo = [];
    },
    // 应用编码
    validTextEn(value) {
      if (/[a-zA-z]$/.test(value) == false) {
        this.setForm.applicationCode = "";
        this.$message({
          type: "warning",
          message: "请输入纯英文",
        });
      }
    },
    openTemplate() {
      this.templateType = "PC";
      this.templateVisible = true;
    },
    getTemplateList() {
      templateList({ form: "PC" }).then((res) => {
        if (res.code == "000000") {
          this.templateDataList = res.data?.records || [];
        } else {
          this.templateDataList = [];
        }
      });
    },
    getMobileTemplateList() {
      templateList({ form: "H5" }).then((res) => {
        if (res.code == "000000") {
          this.mobileTemplateDataList = res.data?.records || [];
        } else {
          this.mobileTemplateDataList = [];
        }
      });
    },
    changeTemplate(type) {
      this.templateType = type;
    },
    //切换PC模版
    changeTemplatePC(data) {
      this.templateSign = data.templateId;
    },
    // 切换H5模板
    changeTemplateH5(data) {
      this.mobileTemplateSign = data.templateId;
    },
    confirmTemplate() {
      if (!this.templateSign && !this.mobileTemplateSign) {
        this.$message({
          type: "warning",
          message: "请至少选择一个H5模板和PC模板后再确认",
        });
        return;
      }

      if (this.templateSign) {
        this.setForm.templateId = this.templateSign;
        this.setForm.mobileTemplateId = this.mobileTemplateSign;
      } else {
        this.$message({
          type: "warning",
          message: "请至少选择一个PC模板后再确认",
        });
        return;
      }

      if (this.mobileTemplateSign) {
        this.setForm.mobileTemplateId = this.mobileTemplateSign;
      } else {
        this.$message({
          type: "warning",
          message: "请至少选择一个H5模板后再确认",
        });
        return;
      }
      this.templateVisible = false;
    },
    cancelTemplate() {
      this.templateVisible = false;
    },
    clickConfig(model) {
      this.selectTemplateVisible = false;
      if (model.type == "PC") {
        this.setForm.templateId = model.ids;
      } else {
        this.setForm.mobileTemplateId = model.ids;
      }
    },
    // 助手形象
    changeAssistant(val) {
      if (!val) {
        // 注释以下几句代码  解决助手形象关闭打开后右侧上传图片以及上传组件消失问题
        // this.fileListAssistant = [];
        // this.fileListIdentityIcon = [];
        // this.setForm.identityIcon = "";
        // this.setForm.facadeImageUrl = "";
        // this.setForm.greeting = "";
        // this.setForm.inputPlaceholder = "";
        this.assistantRadio = "1";
      }
    },
    // 气泡
    changeDialogueIcon() {
      // this.uploadBtnUserIcon = false;
      // this.setForm.userIcon = "";
      // this.fileListUserIcon = [];
      // this.uploadBtnRobot = false;
      // this.setForm.robotIcon = "";
      // this.fileListRobot = [];
    },
    isEmptyAll(props) {
      for (let key of props) {
        if (
          this.setForm.hasOwnProperty(key) &&
          this.setForm[key] !== null &&
          this.setForm[key] !== "" &&
          this.setForm[key] !== undefined
        ) {
          return true; // 至少一个属性值不为空
        }
      }
      return false; // 所有属性值为空或空字符串
    },
    changeRadio(val) {
      this.uploadBtnIdentityIcon = false;
      this.setForm.identityIcon = "";
      this.fileListIdentityIcon = [];
    },
    okHandler() {
      console.log("设置参数", this.setForm);
      if (!this.assistantImage) {
        this.fileListAssistant = [];
        this.setForm.facadeImageUrl = "";
      }
      if (!this.dialogueIcon) {
        this.uploadBtnUserIcon = false;
        this.setForm.userIcon = "";
        this.fileListUserIcon = [];
        this.uploadBtnRobot = false;
        this.setForm.robotIcon = "";
        this.fileListRobot = [];
      }
      if(!this.isBackOldVersionLink) {
        this.setForm.backOldVersionLink = "";
      }
      if(!this.isAboutWebsite) {
        this.setForm.aboutWebsite = "";
      }
      this.$emit("clickConfigParams", "showSettingVisible", this.setForm);
    },
    cancelConfig() {
      this.$emit("clickConfigParams", "closeDrawer");
    },
  },
};
</script>
          
<style lang="scss" scoped>
::v-deep .el-drawer__open .el-drawer.rtl {
  background: #FFFFFF;
  box-shadow: 0px 1px 4px 4px rgba(0,0,0,0.05);
  border-radius: 4px 0px 0px 4px;
}
::v-deep .el-drawer__header {
  background: #fff;
  font-weight: 500;
  font-size: 20px;
  color: #494E57;
  line-height: 24px;
  padding: 32px 32px 0;
}
.hideBox ::v-deep .el-upload--picture-card {
  display: none;
}

::v-deep .el-switch__core {
  width: 32px !important;
  height: 20px !important;
  border-radius: 12px !important;
  border: none;
  &::after {
    width: 14px;
    height: 14px;
    background-color: #fff;
    margin-top: 2px;
    margin-left: 2px;
  }
}

::v-deep .el-switch.is-checked .el-switch__core {
  width: 32px !important;
  height: 20px !important;
  background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%) !important;
  border-radius: 12px !important;
  border: none;
  &::after {
    width: 14px;
    height: 14px;
    background-color: #fff;
    margin-top: 2px;
  }
}
::v-deep .el-input__inner {
  border-radius: 4px;
}
::v-deep .el-textarea__inner {
  border-radius: 4px;
}
.drawer-box {
  padding: 0px 32px 32px 32px;
  height: calc(100vh - 100px - 88px);
  overflow-y: auto;

  ::v-deep .el-textarea__inner {
    font-family: MiSans, MiSans;
  }
  ::v-deep .el-radio {
    margin-right: 12px;
  }
  ::v-deep .el-radio__inner::after {
    width: 6px;
    height: 6px;
  }

  // 调整样式
  .select-template {
    width: 100%;
    height: 77px;
    background: #f0f4fa;
    border-radius: 12px;
    padding: 8px 18px 8px 8px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    &-left {
      height: 100%;
    }
  }
  .background-image {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 16px;

    &-title {
      font-family: MiSans, MiSans;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
      margin-right: 4px;
    }
    &-tips {
      font-family: MiSans, MiSans;
      font-size: 12px;
      color: #828894;
      line-height: 16px;
      margin-top: 4px;
    }

    ::v-deep .el-upload--picture-card {
      width: 48px;
      height: 48px;
      line-height: 48px;
      border: none;
      background: #f2f4f7;
    }
    ::v-deep .el-upload-list__item {
      width: 48px;
      height: 48px;
      margin: 0;
    }
    .el-icon-plus {
      font-size: 16px;
    }
  }

  .welcome-page {
    &-show {
      width: 100%;
      // height: 136px;
      background: #f0f4fa;
      border-radius: 20px;
      padding: 11px 94px 45px;
      &-inner {
        display: flex;
        align-items: center;
        width: 100%;
        // min-height: 80px;
        // max-height: 110px;
        background: #ffffff;
        border-radius: 12px;
        padding: 4px 10px;
        &-right {
          margin-left: 10px;
          flex: 1;
          .identityIcon {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #383d47;
            line-height: 20px;
          }
          .greeting {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #383d47;
            line-height: 16px;
            margin-top: 4px;
          }
        }
      }
    }
    &-selfIntroduction {
      display: flex;
      justify-content: space-between;
      &-title {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #383d47;
        line-height: 20px;
      }
    }
  }

  .dialogue-bubble {
    &-show {
      width: 100%;
      height: 138px;
      background: #f0f4fa;
      border-radius: 16px;
      padding: 16px 0 14px 16px;
      margin-top: 16px;
      &-row {
        display: flex;
        &-img {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          margin-right: 8px;
          border: 1px solid rgba(255, 255, 255, 0.5);
        }
        .el-image {
          border-radius: 50%;
        }
        .top-right {
          width: 253px;
          height: 32px;
          background: rgba(255, 255, 255, 0.8);
          border-radius: 2px 12px 12px 12px;
        }
        .bottom-right {
          width: 297px;
          height: 64px;
          background: rgba(255, 255, 255, 0.8);
          border-radius: 2px 12px 12px 12px;
        }
      }
    }
    &-upload {
      &-title {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #383d47;
        line-height: 20px;
      }
      &-tips {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #828894;
        line-height: 16px;
        margin-top: 4px;
      }
    }
  }

  .webpage-settings {
    &-show {
      width: 100%;
      height: 120px;
      background: #f0f4fa;
      border-radius: 16px 0px 0px 0px;
      background: url("~@/assets/images/web-set.png");
      background-size: 100% 100%;
      padding-top: 12px;
      padding-left: 46px;
      &-icon {
        width: 32px;
        height: 32px;
      }
    }
    &-title {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #383d47;
      line-height: 20px;
    }
    &-tips {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #828894;
      line-height: 16px;
      margin-top: 4px;
    }
  }

  .additional-features {
    &-row {
      &-head {
        &-title {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #383d47;
          line-height: 32px;
        }
      }
      .edit-content {
        width: 100%;
        height: 32px;
        border-radius: 4px;
        border: 1px solid #d3d9e6;
        justify-content: center;
        cursor: pointer;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #383d47;
      }
    }
  }

  .titleName {
    font-family: MiSans, MiSans;
    font-weight: 600;
    font-size: 16px;
    color: #383d47;
    line-height: 20px;
    margin-bottom: 16px;
  }
  // .line {
  //   display: inline-block;
  //   width: 3px;
  //   height: 18px;
  //   background: #1c50fd;
  //   margin-right: 10px;
  // }
}

.footer-btn {
  width: 100%;
  height: 88px;
  background: #fff;
  padding: 16px 32px;
  justify-content: right;
}

.uploadImg {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  > div {
    flex: 1;
  }
  .logo {
    .el-upload-list__item {
      display: flex;
      align-items: center;
    }
  }
}
.img-title {
  display: inline-block;
  font-weight: 400;
  font-size: 16px;
  color: #383d47;

  > img {
    width: 16px;
    height: 16px;
  }
  > img,
  span {
    vertical-align: middle;
  }
}
.img-tips {
  font-weight: 400;
  font-size: 12px;
  text-align: left;
  color: #828894;
  margin: 10px 0px;
}
.upload {
  ::v-deep .el-upload-list--picture-card .el-upload-list__item-thumbnail {
    height: auto !important;
  }
  ::v-deep .el-upload--picture-card {
    width: 48px;
    height: 48px;
    line-height: 48px;
    border: none;
    background: #f2f4f7;
  }
  ::v-deep .el-upload-list__item {
    width: 48px;
    height: 48px;
    margin: 0;
  }
  .el-icon-plus {
    font-size: 16px;
  }
}
.robotImg {
  margin-left: 32px;
  position: relative;
  ::v-deep .el-upload-list--picture-card .el-upload-list__item-thumbnail {
    height: auto !important;
  }
  ::v-deep .el-upload-list__item {
    width: 48px !important;
    height: 48px !important;
    border-radius: 50%;
    display: flex;
    align-items: center;
    margin: 0;
  }
  ::v-deep .el-upload--picture-card {
    width: 48px;
    height: 48px;
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
  ::v-deep .el-upload-list--picture-card .el-upload-list__item-status-label {
    width: 48px;
    right: -18px;
    top: -5px;
  }
}
.mt16 {
  margin-top: 16px;
}
.marginLeft20 {
  margin-left: 20px;
}
.marginTop20 {
  margin-top: 20px;
}
.marginTop32 {
  margin-top: 32px;
}
.flex-center {
  display: flex;
  align-items: center;
}

.just {
  justify-content: space-between;
}
.templateItem {
  border-radius: 4px;
  margin: 8px;
  position: relative;

  .tips {
    position: absolute;
    top: 5px;
    left: 5px;
    width: 40px;
    height: 24px;
    background: #333333;
    border-radius: 4px;
    font-size: 12px;
    color: #ffffff;
    line-height: 24px;
    text-align: center;
    opacity: 0.7;
    backdrop-filter: blur(1px);
    z-index: 99;
  }
}
.selectTemplate {
  ::v-deep .el-drawer {
    width: 960px !important;
  }
  .el-drawer__header {
    height: auto;
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
    
}
.select-template-title {
  display: flex;
  align-items: center;
  margin-bottom: 0px;
  .tabs-box {
    margin-left: 220px;
    height: 32px;
    background: #F2F4F7;
    border-radius: 2px;
    padding: 2px;
    display: flex;
  }
}

.temp-btn {
  width: 120px;
  height: 28px;
  background: #f2f5fa;
  border-radius: 2px;
  font-weight: 400;
  font-size: 14px;
  color: #494E57;
  line-height: 28px;
  padding: 0 8px;
  box-sizing: border-box;
  cursor: pointer;
  justify-content: space-between;
  .temp-btn-content {
    display: inline-flex;
    align-items: center;
    span {
      margin-left: 4px;
    }
  }

  i {
    display: none;
  }

  &.active {
    background: #FFFFFF;
    box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
    color: #494E57;
    i {
      display: block;
      margin-right: 5px;
    }
  }
}

.templateList {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  &-item {
    margin-bottom: 8px;
  }
  .templateItem {
    background: rgba(28, 80, 253, 0.05);
    border-radius: 4px;
    margin: 8px;
    position: relative;
    cursor: pointer;

    .tips {
      position: absolute;
      top: 5px;
      left: 5px;
      width: 40px;
      height: 24px;
      background: #333333;
      border-radius: 4px;
      font-size: 12px;
      color: #ffffff;
      line-height: 24px;
      text-align: center;
      opacity: 0.7;
      backdrop-filter: blur(1px);
      z-index: 99;
    }

    .imgs {
      width: 259px;
      height: 145px;
      border-radius: 4px;
    }

    &.active {
      border: 1px solid #1c50fd;
    }

    .imgsH5 {
      width: 132px;
      height: 252px;
      border-radius: 4px;
    }
  }
  .templateName {
    text-align: center;
  }
  &.templateList-pc {
    .templateList-item  {
      width: 212px;
      height: 212px;
      
      border-radius: 2px;
      margin-bottom: 12px;
      margin-right: 8px;
      
      .templateItem {
        margin: 0px;
        background: linear-gradient( 180deg, #EAF1FF 0%, #D0DFFD 100%);
        padding: 38px 0px 0px 19px;
        position: relative;
        .imgs {
          width: 192px;
          height: 174px;
          border-radius: 16px 0px 0px 0px;
          border: 4px solid #2E3338;
        }
      }

      &:nth-child(4n) {
        margin-right: 0px;
      }
    }
  }
}
</style>
          