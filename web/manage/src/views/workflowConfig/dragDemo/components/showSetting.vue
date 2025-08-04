<template>
  <div>
    <div class="drawer-box">
      <div class="flex-center just">
        <div class="flex-center" style="margin: 16px 0 10px">
          <span class="titleName"> {{ $t("selectTemplate") }} </span>
        </div>
      </div>
      <div class="bg flex-center just">
        <div class="templateItem">
          <!-- <span class="tips">{{ $t("default") }}</span> -->
          <el-image
            style="width: 107px; height: 60px"
            :src="filterTemplateImg(setForm.templateId)"
            fit="cover"
          ></el-image>
        </div>
        <el-button
          type="text"
          icon="el-icon-refresh"
          style="color: #1c50fd; font-size: 14px"
          @click="openTemplate"
          size="mini"
          >{{ $t("replaceTemplate") }}</el-button
        >
      </div>
      <div class="uploadImg backgroundImage bg">
        <div class="img-box flex-center just">
          <div>
            <span class="img-title">{{ $t("backgroundImage") }}</span>
            <p class="img-tips">{{ $t("uploadImgTips") }}</p>
          </div>
          <el-upload
            :action="actionUrl"
            :data="{ filePath: 'agent_source' }"
            :class="{ hideBox: uploadTemplateImg }"
            :file-list="fileTemplateImgList"
            :limit="1"
            class="logo upload"
            list-type="picture-card"
            :on-remove="handleTemplateImgRemove"
            :on-success="handleTemplateImgSuccess"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
        </div>
      </div>
      <div class="mt24">
        <div class="flex-center">
          <span class="titleName"> {{ $t("welcomePage") }} </span>
        </div>
        <div class="mt16 bg">
          <div class="flex-center just">
            <span class="img-title"
              >{{ $t("assistantImage") }}
              <img src="@/assets/images/question-line.svg" />
            </span>
            <el-switch v-model="assistantImage" @change="changeAssistant">
            </el-switch>
          </div>
          <p class="img-tips" v-if="assistantImage">
            {{ $t("uploadImgTips") }}
          </p>
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
        <div class="mt16" v-if="assistantImage">
          <div class="flex-center">
            <span class="img-title">{{ $t("selfIntroduction") }} </span>
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
          <div style="margin-top: 8px" v-if="assistantRadio == '1'">
            <el-input
              :placeholder="$t('selfIntroductionTips')"
              v-model="setForm.identityIcon"
              clearable
              size="small"
            >
            </el-input>
          </div>
          <div v-else style="margin-top: 10px">
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
        <div class="mt16" v-if="assistantImage">
          <span class="img-title">{{ $t("functionIntroduction") }} </span>
          <div style="margin-top: 10px">
            <el-input
              :placeholder="$t('describeFunction')"
              v-model="setForm.greeting"
              type="textarea"
              :rows="3"
              clearable
              show-word-limit
              maxlength="1000"
            >
            </el-input>
          </div>
        </div>
        <div class="mt16" v-if="assistantImage">
          <span class="img-title">{{ $t("inputPrompt") }} </span>
          <div style="margin-top: 8px">
            <el-input
              :placeholder="$t('inputPrompt')"
              v-model="setForm.inputPlaceholder"
              clearable
              size="small"
            >
            </el-input>
          </div>
        </div>
      </div>
      <div class="mt24">
        <div class="flex-center w-full just">
          <div class="flex-center">
            <span class="titleName"> {{ $t("dialogueBubbleIcon") }} </span>
          </div>
          <el-switch v-model="dialogueIcon" @change="changeDialogueIcon">
          </el-switch>
        </div>
        <div class="dialogue-icon mt14">
          <div class="zhushou">
            <img
              v-if="setForm.robotIcon"
              class="default-img"
              :src="setForm.robotIcon"
              alt=""
            />
            <img
              v-else
              class="default-img"
              src="@/assets/images/zhushou-default.png"
              alt=""
            />
            <div class="zhushou-right"></div>
          </div>
          <div class="yonghu">
            <img
              v-if="setForm.userIcon"
              class="default-img"
              :src="setForm.userIcon"
              alt=""
            />
            <img
              v-else
              class="default-img"
              src="@/assets/images/yonghu-default.png"
              alt=""
            />
            <div class="yonghu-right"></div>
          </div>
        </div>
        <div class="mt16" v-if="dialogueIcon">
          <div>
            <span class="img-title"
              >{{ $t("assistantIcon") }}
              <img src="@/assets/images/question-line.svg" />
            </span>
            <p class="img-tips">{{ $t("uploadImgTips") }}</p>
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
          <div class="mt16">
            <span class="img-title"
              >{{ $t("userIcon") }}
              <img src="@/assets/images/question-line.svg" />
            </span>
            <p class="img-tips">{{ $t("uploadImgTips") }}</p>
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
      <div class="mt24">
        <div class="flex-center">
          <span class="titleName"> {{ $t("webPageSettings") }} </span>
        </div>
        <div class="uploadImg mt16">
          <div>
            <span class="img-title" style="margin-bottom: 10px"
              >{{ $t("webPageTitle") }}
              <img src="@/assets/images/question-line.svg" />
            </span>
            <el-input
              style="width: 180px"
              :placeholder="$t('inputPlaceholder')"
              v-model="setForm.webTitle"
              size="small"
            ></el-input>
          </div>
          <div>
            <span class="img-title" style="margin-bottom: 10px"
              >{{ $t("applicationCode") }}
              <img src="@/assets/images/question-line.svg" />
            </span>
            <el-input
              :placeholder="$t('inputPlaceholder')"
              style="width: 180px"
              v-model="setForm.applicationCode"
              @input="validTextEn(setForm.applicationCode)"
              size="small"
            ></el-input>
          </div>
        </div>
        <div class="mt16">
          <span class="img-title"
            >{{ $t("webTagIcon") }}
            <img src="@/assets/images/question-line.svg" />
          </span>
          <p class="img-tips">{{ $t("uploadImgTips") }}</p>
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
    </div>
    <el-dialog
      :title="$t('selectTemplate')"
      :visible.sync="templateVisible"
      width="900px"
      class="selectTemplate"
      destroy-on-close
      :before-close="cancelTemplate"
      append-to-body
      top="2%"
    >
      <div style="margin-bottom: 16px" class="flex-center">
        <div
          class="temp-btn flex-center"
          :class="{ active: templateType == 'PC' }"
          @click="changeTemplate('PC')"
        >
          <i class="el-icon-success" style="color: #4bbe25"></i>
          <span> PC </span>
        </div>
        <div
          class="temp-btn flex-center"
          :class="{ active: templateType == 'H5' }"
          @click="changeTemplate('H5')"
          style="margin-left: 10px"
        >
          <i class="el-icon-success" style="color: #4bbe25"></i>
          <span> H5 </span>
        </div>
      </div>
      <div>
        <div class="templateList" v-if="templateType == 'PC'">
          <div
            class="templateItem"
            v-for="item in templateDataList"
            :key="item.id"
            :class="{ active: templateSign == item.templateId }"
            @click="changeTemplatePC(item)"
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
        <div class="templateList" v-if="templateType == 'H5'">
          <div
            class="templateItem"
            v-for="item in mobileTemplateDataList"
            :key="item.id"
            :class="{
              active: mobileTemplateSign == item.templateId,
            }"
            @click="changeTemplateH5(item)"
          >
            <span class="tips" v-if="mobileTemplateSign == item.templateId">{{
              $t("default")
            }}</span>
            <el-image :src="item.picturePath" fit="cover" class="imgsH5">
            </el-image>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmTemplate">{{
          $t("select")
        }}</el-button>
        <el-button @click="cancelTemplate">{{ $t("cancel") }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
          
<script>
import { templateList } from "@/api/app";
// vuex
import { mapMutations } from "vuex";

export default {
  components: {},
  props: {
    params: Object,
  },
  data() {
    return {
      direction: "rtl",
      actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
      setForm: {
        webTitle: "",
        applicationCode: "",
        robotIcon: "",
        webIcon: "",
        userIcon: "",
        identityIcon: "",
        mobileTemplateId: "",
        templateId: "",
        facadeImageUrl: "",
        backgroundImageUrl: "",
        inputPlaceholder: "",
        greeting: "", // 功能介绍
      },
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
      fileListUserIcon: [],
      fileListRobot: [],
      fileListwebIcon: [],
      // 选择模版
      templateVisible: false,
      templateType: "PC",
      mobileTemplateDataList: [],
      templateDataList: [],
      templateSign: "",
      mobileTemplateSign: "",
    };
  },
  watch: {
    setForm: {
      handler(n) {
        this.setShowDrawerData(n);
      },
      deep: true,
    },
  },
  mounted() {
    this.setDefaultInfo(this.params);
    this.getTemplateList();
    this.getMobileTemplateList();
  },
  methods: {
    ...mapMutations(["setShowDrawerData"]),
    setDefaultInfo(item) {
      this.uploadTemplateImg = item.backgroundImageUrl ? true : false;
      this.uploadBtnAssistant = item.facadeImageUrl ? true : false;
      this.uploadBtnRobot = item.robotIcon ? true : false;
      this.uploadBtnWebIcon = item.webIcon ? true : false;
      this.uploadBtnUserIcon = item.userIcon ? true : false;
      this.uploadBtnIdentityIcon = item.identityIcon ? true : false;
      this.templateSign = item.templateId;
      this.mobileTemplateSign = item.mobileTemplateId;
      this.dialogueIcon = false;

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
        userIcon: item.userIcon,
        identityIcon: item.identityIcon,
        templateIcon: "",
        mobileTemplateId: item.mobileTemplateId,
        templateId: item.templateId,
        facadeImageUrl: item.facadeImageUrl,
        backgroundImageUrl: item.backgroundImageUrl,
        greeting: item.greeting,
        inputPlaceholder: item.inputPlaceholder,
      };
      this.assistantImage = this.isEmptyAll([
        "identityIcon",
        "facadeImageUrl",
        "greeting",
        "inputPlaceholder",
      ]);
      this.assistantRadio = this.setForm.identityIcon.includes("https")
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
        this.fileListAssistant = [];
        this.fileListIdentityIcon = [];
        this.setForm.identityIcon = "";
        this.setForm.facadeImageUrl = "";
        this.setForm.greeting = "";
        this.setForm.inputPlaceholder = "";
        this.assistantRadio = "1";
      }
    },
    // 气泡
    changeDialogueIcon() {
      this.uploadBtnUserIcon = false;
      this.setForm.userIcon = "";
      this.fileListUserIcon = [];
      this.uploadBtnRobot = false;
      this.setForm.robotIcon = "";
      this.fileListRobot = [];
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
    cancelConfig() {
      console.log("设置参数", this.setForm);
      this.$emit("clickConfigParams", "showSettingVisible", this.setForm);
    },
  },
};
</script>
          
<style lang="scss" scoped>
::v-deep .el-drawer__header {
  background: linear-gradient(
    180deg,
    rgba(43, 88, 213, 0.1) 0%,
    rgba(43, 88, 213, 0) 100%
  );
  color: #383d47;
  font-weight: 500;
  font-size: 20px;
  padding: 32px 32px 0;
}
.hideBox ::v-deep .el-upload--picture-card {
  display: none;
}

::v-deep .el-switch.is-checked .el-switch__core {
  background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
  border-radius: 12px;
}
::v-deep .el-switch .el-switch__core::after {
  background-color: #fff;
}
::v-deep .el-input__inner {
  border-radius: 4px;
}
::v-deep .el-textarea__inner {
  border-radius: 4px;
}
::v-deep .el-radio .el-radio__label {
  font-size: 14px !important;
  padding-left: 4px !important;
}
::v-deep .el-radio__inner::after {
  width: 6px !important;
  height: 6px !important;
}
::v-deep .el-switch__core {
  width: 32px !important;
  &::after {
    width: 14px;
    height: 14px;
    top: 2px;
  }
}
.drawer-box {
  width: 100%;
  height: calc(100% - 50px);
  overflow-y: auto;
  overflow-x: hidden;

  ::v-deep .el-textarea__inner {
    font-family: MiSans, MiSans;
  }

  .titleName {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 16px;
    color: #383d47;
    line-height: 20px;
  }
}

.uploadImg {
  display: flex;
  justify-content: space-between;
  .logo {
    .el-upload-list__item {
      display: flex;
      align-items: center;
    }
  }
}
.backgroundImage {
  margin-top: 12px;
  .img-box {
    width: 100%;
  }
}
.dialogue-icon {
  background: #f0f4fa;
  border-radius: 16px;
  padding: 14px 16px 16px;
  .zhushou,
  .yonghu {
    display: flex;
  }
  .yonghu {
    margin-top: 12px;
  }
  .zhushou-right {
    width: 253px;
    height: 32px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 2px 12px 12px 12px;
  }
  .yonghu-right {
    width: 298px;
    height: 64px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 2px 12px 12px 12px;
  }
  .default-img {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    margin-right: 8px;
  }
}
.img-title {
  display: inline-block;
  font-weight: 400;
  font-size: 14px;
  line-height: 20px;
  color: #383d47;
  display: flex;
  align-items: center;

  > img {
    width: 14px;
    height: 14px;
    margin-left: 6px;
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
  margin: 4px 0 12px;
}
.upload {
  ::v-deep .el-upload-list--picture-card .el-upload-list__item-thumbnail {
    height: auto !important;
  }
  ::v-deep .el-upload--picture-card {
    width: 48px;
    height: 48px;
    border: none;
    background: rgba(255, 255, 255, 1);
    position: relative;

    i {
      font-size: 24px;
      position: absolute;
      top: 12px;
      left: 50%;
      margin-left: -12px;
    }
  }
  ::v-deep .el-upload-list__item {
    width: 48px;
    height: 48px;
    margin: 0;
    border: none;
  }
}
.robotImg {
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
  }
  ::v-deep .el-upload--picture-card {
    width: 120px;
    height: 32px;
    border-radius: 4px;
    border: 1px solid #c4c6cc;
    background: rgba(255, 255, 255, 1);
    position: relative;
    // display: flex;
    // align-items: center;
    // justify-content: center;
    font-size: 14px;
    color: #828894;
    .el-icon-plus {
      font-size: 14px;
      margin-right: 6px;
      position: absolute;
      top: 8px;
      left: 50%;
      margin-left: -7px;
    }
  }
  ::v-deep .el-upload-list--picture-card .el-upload-list__item-status-label {
    width: 64px;
    right: -18px;
    top: -5px;
  }
}
.w-full {
  width: 100%;
}
.mt24 {
  margin-top: 24px;
}
.mt16 {
  margin-top: 16px;
}
.mt14 {
  margin-top: 14px;
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

.bg {
  background: #f0f4fa;
  border-radius: 12px;
  padding: 8px;
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
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__header {
      background: linear-gradient(
        180deg,
        rgba(43, 88, 213, 0.1) 0%,
        rgba(43, 88, 213, 0) 100%
      );
      border-radius: 8px 8px 0px 0px;
    }
  }
}

.temp-btn {
  width: 88px;
  height: 40px;
  background: #f2f5fa;
  border-radius: 4px;
  color: #828894;
  font-size: 16px;
  line-height: 40px;
  cursor: pointer;
  justify-content: center;

  i {
    display: none;
  }

  &.active {
    color: #383d47;
    background: rgba(28, 80, 253, 0.1);

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
  max-height: 600px;
  overflow-y: auto;
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
}
</style>
          