<template>
  <div class="publish-component">
    <div
      class="publish-component-content"
      v-loading.fullscreen.lock="saveLoading"
    >
      <!-- 发布记录 -->
      <div
        v-if="['1', '2'].includes(sourceData.publishStatus)"
        class="publish-component-content-record"
      >
        <div class="publish-component-content-record-head">
          <div class="title">{{ $t("releaseRecord") }}</div>
          <div
            class="history"
            @click="openHistory('drawerVisiblePublishHistory')"
          >
            <iconpark-icon
              name="time-line"
              size="20"
              color="#494E57"
              style="margin-right: 6px"
            ></iconpark-icon
            >{{ $t("releaseHistory") }}
          </div>
          <div class="update" @click="publishHandler">
            <iconpark-icon
              name="send-plane-2-fill"
              size="20"
              color="#fff"
              style="margin-right: 6px"
            ></iconpark-icon
            >{{ $t("update") }}
          </div>
        </div>
        <el-input
          v-model="sourceData.publishDesc"
          type="textarea"
          maxlength="2000"
          show-word-limit
          :rows="5"
          placeholder="输入更新记录，以便后续回顾历史"
        />
      </div>
      <!-- 应用发布 -->
      <div
        v-if="!['1', '2'].includes(sourceData.publishStatus)"
        class="publish-component-content-record"
      >
        <div class="publish-component-content-record-head justify-between">
          <div class="title">{{ $t("applicationRelease") }}</div>
          <div class="update" @click="publishHandler">
            <iconpark-icon
              name="send-plane-2-fill"
              size="20"
              color="#fff"
              style="margin-right: 6px"
            ></iconpark-icon
            >{{ $t("publish") }}
          </div>
        </div>
      </div>
      <!-- 公开发布 -->
      <div
        v-if="!['text-agent'].includes(sourceData.type)"
        class="publish-component-content-open"
      >
        <div class="publish-component-content-open-top">
          <img
            src="@/assets/images/appManagement/open-icon.png"
            alt=""
            class="open-icon"
          />
          <div class="tips">
            <div class="flex items-center">
              <div style="font-weight: 600">{{ $t('publicPublish') }}</div>
              <div
                v-if="['1', '2'].includes(sourceData.publishStatus)"
                class="mark published flex items-center justify-center"
              >
                <iconpark-icon
                  name="checkbox-circle-fill"
                  color="#3FB816"
                  size="14"
                  style="margin-right: 6px"
                ></iconpark-icon
                >已发布
              </div>
              <div
                v-else
                class="mark unpublished flex items-center justify-center"
              >
                <iconpark-icon
                  name="indeterminate-circle-line"
                  color="#828894"
                  size="14"
                  style="margin-right: 6px"
                ></iconpark-icon
                >{{ $t('unPublish') }}
              </div>
            </div>
            <div class="text">用户可通过PC或移动设备浏览器直接访问应用页面</div>
          </div>
          <div
            v-if="!['1', '2'].includes(sourceData.publishStatus)"
            class="fabu"
          >
            请先完成应用发布
          </div>
          <div
            v-if="['1', '2'].includes(sourceData.publishStatus)"
            :class="['http', sourceData?.publicSwitch == '0' ? '':'disabledDiv']"
          >
            <iconpark-icon
              class="iconpark"
              name="file-copy-line"
              size="16"
              color="#494E57"
              @click="cpoyText(sourceData.clientLink)"
            ></iconpark-icon>
            <div class="http-url">{{ sourceData.clientLink }}</div>
          </div>
          <div
            v-if="['1', '2'].includes(sourceData.publishStatus)"
            :class="['experience', sourceData?.publicSwitch == '0' ? '':'disabledDiv']"
            @click="openPreview"
          >
            <iconpark-icon
              name="computer-line"
              size="20"
              color="#1747E5"
              style="margin-right: 6px"
            ></iconpark-icon
            >线上体验
          </div>
          <div class="switch" style="margin-left: 0;">
            {{ sourceData?.publicSwitch == "0" ? $t('activeStatus') : $t('disabled') }}
            <el-switch
              v-model="sourceData.publicSwitch"
              active-color="#1747E5"
              inactive-color="#CED4E0"
              :active-value="0"
              :inactive-value="1"
            >
            </el-switch>
          </div>
        </div>
        <div class="publish-component-content-open-bottom">
          <ul class="t-table">
            <!-- 表头 -->
            <li class="row1 row flex items-center">
              <div class="col1"></div>
              <div class="col2">发布平台</div>
              <div class="col3 flex items-center">{{ $t('status') }}</div>
              <div class="col4 flex items-center">{{ $t('action') }}</div>
            </li>
            <!-- 上架到商店 -->
            <li class="row2 row flex items-center">
              <div class="col1">
                <el-checkbox v-model="publishAppStore" />
              </div>
              <div class="col2 flex items-center">
                <div class="icons">
                  <iconpark-icon
                    name="store-2-line"
                    size="18"
                    color="#494E57"
                    style="margin-top: 10px"
                  ></iconpark-icon>
                </div>
                <div>
                  <div class="desr1">上架到商店</div>
                  <div class="desr2">
                    审核通过后，应用将公开在应用商店可供其他用户体验
                  </div>
                </div>
              </div>
              <div class="col3 flex items-center">
                <div
                  class="col3-result flex items-center justify-center"
                  :class="[
                    sourceData.publishAppStore == '1' ? 'result-success' : '',
                  ]"
                >
                  <iconpark-icon
                    v-if="sourceData.publishAppStore == '1'"
                    name="checkbox-circle-fill"
                    color="#3FB816"
                  ></iconpark-icon>
                  <iconpark-icon
                    v-else
                    name="indeterminate-circle-line"
                    color="#828894"
                  ></iconpark-icon>
                  {{
                    sourceData.publishAppStore == "0"
                      ? "未上架"
                      : sourceData.publishAppStore == "2"
                      ? "待审核"
                      : "已上架"
                  }}
                </div>
              </div>
              <div class="col4 flex items-center">
                {{ $t('classification') }}
                <el-select
                  v-model="sourceData.publishType"
                  style="margin-left: 12px"
                >
                  <el-option
                    v-for="(item, index) in classificationList"
                    :key="index"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </div>
            </li>
            <!-- 微信公众号 -->
            <li class="row2 row flex items-center">
              <div class="col1">
                <el-tooltip
                  content="请先完成授权或配置"
                  :disabled="disabledTooltip"
                  placement="top"
                >
                  <el-checkbox
                    v-model="clientAuthChannelCheckbox"
                    @change="checkBoxChange"
                    :disabled="!clientAuthChannel"
                  />
                </el-tooltip>
              </div>
              <div class="col2 flex items-center">
                <div class="icons">
                  <iconpark-icon
                    name="logo-workweixin-line"
                    size="18"
                    color="#494E57"
                    style="margin-top: 10px"
                  ></iconpark-icon>
                </div>
                <div>
                  <div class="desr1">微信公众号</div>
                  <div class="desr2">接入微信公众号，助理高效私域运营</div>
                </div>
              </div>
              <div class="col3 flex items-center">
                <div
                  class="col3-result flex items-center justify-center"
                  :class="[
                    sourceData.clientAuthChannel ? 'result-success' : '',
                  ]"
                >
                  <iconpark-icon
                    v-if="sourceData.clientAuthChannel"
                    name="checkbox-circle-fill"
                    color="#3FB816"
                  ></iconpark-icon>
                  <iconpark-icon
                    v-else
                    name="indeterminate-circle-line"
                    color="#828894"
                  ></iconpark-icon>
                  {{
                    sourceData.clientAuthChannel
                      ? "已授权"
                      : clientAuthChannel
                      ? "未授权"
                      : "未配置"
                  }}
                </div>
              </div>
              <div class="col4 flex items-center">
                <div
                  class="config-btn flex items-center justify-center"
                  @click="openConfigDialog"
                >
                  <iconpark-icon
                    name="settings-4-line"
                    size="20"
                    color="#494E57"
                    style="margin-right: 2px"
                  ></iconpark-icon>
                  配置
                </div>
              </div>
            </li>
            <!-- 政务网认证 -->
            <li class="row2 row flex items-center">
              <div class="col1">
                <el-checkbox
                  v-model="pcAuthChannelCheckbox"
                  @change="checkBoxChange"
                />
              </div>
              <div class="col2 flex items-center">
                <div class="icons">
                  <iconpark-icon
                    name="id-card-line"
                    size="18"
                    color="#494E57"
                    style="margin-top: 10px"
                  ></iconpark-icon>
                </div>
                <div>
                  <div class="desr1">身份认证</div>
                  <div class="desr2">通过公开发布的链接访问应用时，会进行身份认证</div>
                </div>
              </div>
              <div class="col3 flex items-center">
                <div
                  class="col3-result flex items-center justify-center"
                  :class="[sourceData.pcAuthChannel ? 'result-success' : '']"
                >
                  <iconpark-icon
                    v-if="sourceData.pcAuthChannel"
                    name="checkbox-circle-fill"
                    color="#3FB816"
                  ></iconpark-icon>
                  <iconpark-icon
                    v-else
                    name="indeterminate-circle-line"
                    color="#828894"
                  ></iconpark-icon>
                  {{ sourceData.pcAuthChannel ? $t('activeStatus') : $t('inactiveStatus') }}
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
      <!-- 私有发布 -->
      <div
        class="publish-component-content-private"
        :class="[
          sourceData?.privateSwitch == '0'
            ? ''
            : 'publish-component-content-disabled',
        ]"
      >
        <div class="publish-component-content-open-top">
          <img
            v-if="sourceData?.privateSwitch == '0'"
            src="@/assets/images/appManagement/private-icon.png"
            alt=""
            class="open-icon"
          />
          <img
            v-else
            src="@/assets/images/appManagement/private-disabled.png"
            alt=""
            class="open-icon"
          />
          <div class="tips">
            <div class="flex items-center">
              <div style="font-weight: 600">{{ $t('privatePublish') }}</div>
            </div>
            <div class="text">
              生成一个对应的API key，用户可通过API 密钥访问并使用服务
            </div>
          </div>
          <div class="switch">
            {{ sourceData?.privateSwitch == "0" ? $t('activeStatus') : $t('disabled') }}
            <el-switch
              v-model="sourceData.privateSwitch"
              active-color="#1747E5"
              inactive-color="#CED4E0"
              :active-value="0"
              :inactive-value="1"
            >
            </el-switch>
          </div>
        </div>
        <div class="publish-component-content-private-bottom">
          <div class="describe">
            用于其他应用程序和平台的访问权限。详细说明请查看：<span
              class="describe-txt"
              @click="openHistory('drawerVisibleApiCallDescription')"
              >API调用说明<iconpark-icon
                name="link-m"
                size="14"
                color="#1747E5"
                style="margin-left: 5px"
              ></iconpark-icon
            ></span>
          </div>
          <div class="notes">
            不要与他人共享您的密钥，也不要在浏览器或其他客户端代码中暴露它，以保护您账户的安全。
          </div>
          <div class="set-up">
            <div class="set-up-items">
              <div class="set-up-items-label">API Key</div>
              <div class="set-up-items-input">
                <el-input
                  v-model="sourceData.apiKey"
                  :disabled="!sourceData?.privateSwitch == '0'"
                />
                <iconpark-icon
                  class="iconpark"
                  name="file-copy-line"
                  size="16"
                  color="#494E57"
                  @click="cpoyText(sourceData.apiKey)"
                ></iconpark-icon>
              </div>
            </div>
            <div class="set-up-items">
              <div class="set-up-items-label">{{ $t('secretKeyValue') }}</div>
              <div class="set-up-items-input">
                <iconpark-icon
                  v-if="showNumber"
                  class="eye-icon"
                  name="eye-line"
                  color="#494E57"
                  size="16"
                  @click="showNumber = false"
                ></iconpark-icon>
                <iconpark-icon
                  v-else
                  class="eye-icon"
                  name="eye-off-line"
                  color="#494E57"
                  size="16"
                  @click="showNumber = true"
                ></iconpark-icon>
                <el-input
                  v-model="sourceData.apiSecret"
                  :disabled="!sourceData?.privateSwitch == '0'"
                  class="input-apiSecret"
                  :type="showNumber ? 'text' : 'password'"
                />
                <iconpark-icon
                  class="iconpark"
                  name="file-copy-line"
                  size="16"
                  color="#494E57"
                  @click="cpoyText(sourceData.apiSecret)"
                ></iconpark-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 配置微信公众号 -->
    <el-dialog
      title="配置微信公众号"
      :visible.sync="dialogVisible"
      width="30%"
      :before-close="handleClose"
      append-to-body
      custom-class="wechatDialog"
      @open="openDialog"
    >
      <div v-loading="dialogLoading">
        <div class="dialog-content">
          前往微信公众平台“设置与开发” > “基本配置” >
          “公众号开发信息”，获取配置信息。
        </div>
        <el-form :model="wechatForm" :rules="wechatFormRules" ref="wechatForm">
          <el-form-item label="EncodingAESKey" prop="aesKey">
            <el-input
              v-model="wechatForm.aesKey"
              placeholder="请输入EncodingAESKey"
            />
          </el-form-item>
          <el-form-item label="AppID" prop="appId">
            <el-input v-model="wechatForm.appId" placeholder="请输入AppID" />
          </el-form-item>
          <el-form-item label="App secret" prop="secret">
            <el-input
              v-model="wechatForm.secret"
              placeholder="请输入App secret"
            />
          </el-form-item>
          <el-form-item label="Token" prop="token">
            <el-input v-model="wechatForm.token" placeholder="请输入Token" />
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="onSubmit">{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>
    <!-- 发布历史 -->
    <PublishHistory
      :applicationInfoId="sourceData.id"
      :appVersionNumber="sourceData.appVersionNumber"
      v-model="drawerVisiblePublishHistory"
      @back="closeDialog"
      @close="closeDrawer"
    />
    <!-- api调用说明 -->
    <ApiCallDescription
      :applicationId="sourceData.applicationId"
      v-model="drawerVisibleApiCallDescription"
      @close="closeDrawer"
    />
  </div>
</template>

<script>
// api
import {
  apiAddAuthChannel,
  apiGetAuthChannelByApplicationId,
  addApplicationPluginData,
  addApplication,
} from "@/api/app";
// comoponents
import PublishHistory from "./publishHistory";
import ApiCallDescription from "./apiCallDescription";

export default {
  components: { PublishHistory, ApiCallDescription },
  props: {
    params: {
      type: Object,
      default: () => {},
    },
    appConfigForm: {
      type: Object,
      default: () => {},
    },
    selectIds: {
      type: Array,
      default: () => [],
    },
	mcpServerIdList: {
	  type: Array,
	  default: () => [],
	},
    saveTime: {
      type: String,
    },
  },
  data() {
    return {		
      dialogVisible: false,
      sourceData: {},
      showNumber: true,
      classificationList: [
        "聊天助手",
        "AI翻译",
        "AI搜索",
        "文案写作",
        "行业报告",
        "图片创作",
        "学习助手",
        "合规审查",
      ], // 分类
      isStore: false,
      isStoreResult: true, // 上架到商店状态
      id: "", // 微信配置id
      wechatForm: {
        appId: "",
        secret: "",
        token: "",
        aesKey: "",
      },
      wechatFormRules: {
        appId: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
        secret: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
        token: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
        aesKey: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
      },
      configData: {}, // 配置数据
      dialogLoading: false,
      saveLoading: false,
      clientAuthChannel: "",
      pcAuthChannel: "",
      publishAppStore: false,
      clientAuthChannelCheckbox: false,
      pcAuthChannelCheckbox: false,
      drawerVisiblePublishHistory: false,
      drawerVisibleApiCallDescription: false,
    };
  },
  computed: {
    disabledTooltip() {
      return this.clientAuthChannel ? true : false;
    },
  },
  watch: {
    params: {
      handler(n) {
        console.log("n", n);
        this.sourceData = Object.assign(
          {},
          {
            ...n,
          }
        );
      },
      deep: true,
    },
  },
  mounted() {
    console.log("sourceData2222", this.sourceDatas)
    this.sourceData = Object.assign(
      {},
      {
        ...this.params,
        publicSwitch: this.params.publicSwitch ? 1:0,
        privateSwitch: this.params.privateSwitch ? 1:0,
      }
    );
    if (!this.sourceData.publishType) {
      this.sourceData.publishType = this.classificationList[0];
    }

    this.sourceData.publishDesc = this.sourceData.publishDesc ? this.sourceData.publishDesc:"更新"
    this.publishAppStore = [1, 2].includes(this.sourceData.publishAppStore)
      ? true
      : false;
    this.clientAuthChannelCheckbox = this.sourceData.clientAuthChannel
      ? true
      : false;
    this.pcAuthChannelCheckbox = this.sourceData.pcAuthChannel ? true : false;
    this.clientAuthChannel = this.sourceData.clientAuthChannel;
    this.pcAuthChannel = this.sourceData.pcAuthChannel;
    this.getAuthChannelByApplicationId("h5_we_chat_office");
    this.getAuthChannelByApplicationId("pc_gov");
    console.log("sourceData", this.sourceData);
	
  },
  methods: {
    // 线上体验
    openPreview() {
      if(this.sourceData?.publicSwitch == "1") return
      if (!this.sourceData.clientLink) return;
      window.open(this.sourceData.clientLink, "_blank");
    },
    // 复制功能
    exeCommandCopyText(text) {
      try {
        const t = document.createElement("textarea");
        t.nodeValue = text;
        t.value = text;
        document.body.appendChild(t);
        t.select();
        document.execCommand("copy");
        document.body.removeChild(t);
        return true;
      } catch (e) {
        console.log(e);
        return false;
      }
    },
    cpoyText(content) {
      this.exeCommandCopyText(content);
      this.$message({
        message: this.$t("copySuccessed"),
        type: "success",
      });
    },
    // 打开配置弹窗
    openConfigDialog() {
      this.dialogVisible = true;
    },
    // 关闭弹窗
    handleClose() {
      this.dialogVisible = false;
    },
    // 弹窗打开事件
    async openDialog() {
      this.dialogLoading = true;
      await this.getAuthChannelByApplicationId("h5_we_chat_office");
    },
    onSubmit() {
      this.$refs.wechatForm.validate((valid) => {
        if (valid) {
          this.addAuthChannel();
        }
      });
    },
    // 配置提交
    async addAuthChannel() {
      const params = {
        ...this.wechatForm,
        applicationId: this.sourceData.applicationId,
        redirectUri: this.sourceData.clientLink,
        id: this.id,
      };
      let res = await apiAddAuthChannel(params);
      if (res.code == "000000") {
        this.$message.success("配置成功");
        this.dialogVisible = false;
      }
    },
    // 查询是否配置（微信公众号:h5_we_chat_office/政务网认证:pc_gov）
    async getAuthChannelByApplicationId(channelCode) {
      try {
        const params = {
          applicationId: this.sourceData.applicationId,
          channelCode,
        };
        let res = await apiGetAuthChannelByApplicationId(params);
        if (res.code == "000000") {
          if (res.data && JSON.stringify(res.data) != "{}") {
            for (let key in this.wechatForm) {
              this.wechatForm[key] = res.data[key];
            }
            if (channelCode == "h5_we_chat_office") {
              this.clientAuthChannel = res.data?.authChannelId;
              this.id = res.data?.id;
            } else {
              this.pcAuthChannel = res.data?.authChannelId;
            }
          }
        }
      } catch (error) {
        this.dialogLoading = false;
      }
      this.dialogLoading = false;
    },
    async checkBoxChange() {},
    // 应用发布
    submitAddApp(flag) {
		
      if (!this.sourceData.templateId && this.sourceData.publishStatus == "1") {
        this.$message({
          type: "warning",
          message: "请选择PC模板后再发布",
        });
        return;
      }
      if (
        !this.sourceData.mobileTemplateId &&
        this.sourceData.publishStatus == "1"
      ) {
        this.$message({
          type: "warning",
          message: "请选择H5模板后再发布",
        });
        return;
      }
      const params = Object.assign(this.appConfigForm, this.sourceData);

      if (!this.sourceData.sourceShowFlag) {
        params.previewDoc = "否";
      }
      if (!this.sourceData.networkChannel) {
        params.finalNetworkFlag = "否";
      }
      if (this.selectIds.length) {
        this.addApplicationPluginDataHandler();
      }
      // 暂存：2与发布：3
      if (flag == 3 || flag == 2) {
        params.applicationId = params.applicationId
          ? params.applicationId
          : this.firstApplicationId;
        params.id = params.id ? params.id : this.firstId;
      }
      params.makeType = "1"
      //mcp数组集合
	   console.log(this.mcpServerIdList, 'this.mcpServerIdList')
		let mcpServerIds = []
		this.mcpServerIdList.forEach((element) => {
			mcpServerIds.push(element.mcpId)
		})
		console.log(mcpServerIds, 'this.mcpServerIds')
      this.saveLoading = true;
      console.log(params, '------------发布参数')
      addApplication({
        ...params,
		mcpServerIds:mcpServerIds,
        type: this.sourceData.type ? this.sourceData.type : this.type || "",
        virtualHumanFlag: this.sourceData.virtualHumanFlag ? "是" : "否",
        voiceDialogueFlag: this.sourceData.voiceDialogueFlag ? "是" : "否",
        modelAnswerFlag: this.sourceData.modelAnswerFlag ? "是" : "否",
        streamVoice: this.sourceData.streamVoice,
        sensitiveFlag: this.sourceData.sensitiveFlag ? "是" : "否",
        wenshuFlag: this.sourceData.wenshuFlag ? "是" : "否",
        networkFlag: this.sourceData.networkFlag ? "是" : "否",
        ipFlag: this.sourceData.ipFlag ? "是" : "否",
        ocrFlag: this.sourceData.ocrFlag ? "是" : "否",
        videoFlag: this.sourceData.videoFlag ? "是" : "否",
        multiDialogueFlag: this.sourceData.multiDialogueFlag ? "是" : "否",
        knowledgeFlag: this.sourceData.knowledgeFlag ? "是" : "否",
        rewritingFlag: this.sourceData.rewritingFlag ? "是" : "否",
        polishFlag: this.sourceData.polishFlag ? "是" : "否",
        modelFallbackFlag: this.sourceData.modelFallbackFlag ? "是" : "否",
        recommendQuestionsShowFlag: this.sourceData.recommendQuestionsShowFlag
          ? "1"
          : "0",
        sourceShowFlag: this.sourceData.sourceShowFlag ? "1" : "0",
        interceptWordHouses: this.sourceData.interceptWordHouses
          ? this.sourceData.interceptWordHouses
          : [],
        processStep: this.sourceData.processStep
          ? typeof this.sourceData.processStep === "string"
            ? this.sourceData.processStep
            : this.sourceData.processStep.join(",")
          : "",
        presetQuestionList: this.sourceData?.presetQuestionList?.filter(
          (item) => item
        ),
        clickPublish: true,
        publishAppStore: this.publishAppStore ? 1 : 0,
        clientAuthChannel: this.clientAuthChannelCheckbox
          ? this.clientAuthChannel
          : "",
        pcAuthChannel: this.pcAuthChannelCheckbox ? this.pcAuthChannel : "",
        publishStatus: 1,
      })
        .then((res) => {
          if (res.code == "000000") {
            this.saveLoading = false;
            this.$emit("openComponents", res.data);
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        })
        .finally(() => {
          this.saveLoading = false;
        });
    },
    // 新增应用插件关联表
    addApplicationPluginDataHandler(autoSave) {
      addApplicationPluginData({
        applicationId: this.sourceData.applicationId,
        pluginList: this.selectIds,
      }).then((res) => {
        if (res.code == "000000") {
          if (!autoSave) {
            // this.$message.success(this.$t("successed"));
          }
        }
      });
    },
    // 关闭应用
    closeDialog() {
      this.$emit("closePrantDialog", false);
    },
    publishHandler() {
      if (["workflow", "dialogue"].includes(this.sourceData.type)) {
        const workflowParams = {
          publishDesc: this.sourceData?.publishDesc,
          privateSwitch: this.sourceData?.privateSwitch,
          publicSwitch: this.sourceData?.publicSwitch,
          apiKey: this.sourceData?.apiKey,
          apiSecret: this.sourceData?.apiSecret,
          clickPublish: true,
          publishType: this.sourceData?.publishType,
          publishAppStore: this.publishAppStore ? 1 : 0,
          clientAuthChannel: this.clientAuthChannelCheckbox
            ? this.clientAuthChannel
            : "",
          pcAuthChannel: this.pcAuthChannelCheckbox ? this.pcAuthChannel : "",
        };
        this.saveLoading = true;
        this.$emit("workflowSubmit", workflowParams, true);
      } else {
        this.submitAddApp();
      }
    },
    // 历史记录
    openHistory(drawerName) {
      this[drawerName] = true;
    },
    closeDrawer(drawerName) {
      this[drawerName] = false;
    },
  },
};
</script>
<style lang="scss">
.wechatDialog {
  .el-dialog__header {
    padding: 32px 32px 8px;
    top: 38px;
    right: 32px;
    .el-dialog__close {
      color: #494e57;
    }
  }
  .el-dialog__title {
    font-family: MiSans, MiSans;
    font-weight: 600;
    font-size: 20px;
    color: #494e57;
    line-height: 32px;
  }
  .el-dialog__body {
    padding: 8px 32px;
  }
  .dialog-content {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #494e57;
    line-height: 22px;
    word-break: break-word;
  }
  .el-form-item {
    margin-bottom: 12px;
  }
}
</style>
<style lang="scss" scoped>
.publish-component {
  width: 100%;
  height: 100%;
  background: #fff;
  padding-top: 24px;
  overflow-y: auto;
  &-content {
    width: 75%;
    height: 100%;
    margin: 0 auto;
    // background: red;
    &-record {
      &-head {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        .title {
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 18px;
          color: #494e57;
          line-height: 32px;
          display: flex;
          align-items: center;
          &::before {
            content: "";
            display: inline-block;
            width: 4px;
            height: 18px;
            background: #1747e5;
            border-radius: 0px 2px 2px 0px;
            margin-right: 6px;
          }
        }
        .history {
          margin-left: auto;
          display: flex;
          align-items: center;
          justify-content: center;
          height: 24px;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 16px;
          color: #494e57;
          line-height: 24px;
          cursor: pointer;
        }
        .update {
          margin-left: 32px;
          width: 92px;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #1747e5;
          border-radius: 2px;
          color: #fff;
          cursor: pointer;
        }
      }
    }
    &-open {
      margin-top: 24px;
      padding: 0 4px 4px;
      border-radius: 4px;
      background: linear-gradient(
        270deg,
        rgba(23, 71, 229, 0.1) 0%,
        rgba(51, 125, 244, 0.1) 100%
      );
      &-top {
        height: 104px;
        width: 100%;
        display: flex;
        align-items: center;
        border-radius: 4px 4px 0 0;
        .open-icon {
          width: 80px;
          height: 80px;
          margin-right: 16px;
        }
        .tips {
          color: #494e57;
          font-size: 14px;
          .mark {
            width: 70px;
            height: 24px;
            font-size: 12px;
            background: rgba(130, 136, 148, 0.1);
            border-radius: 2px;
            margin-left: 12px;
          }
          .unpublished {
            color: #828894;
            background: rgba(130, 136, 148, 0.1);
          }
          .published {
            color: #3fb816;
            background: rgba(63, 184, 22, 0.1);
          }
          .text {
            margin-top: 10px;
            font-size: 14px;
            color: #494e57;
          }
        }
        .fabu {
          width: 152px;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-left: auto;
          margin-right: 20px;
          background: #a0bdfc;
          border-radius: 2px;
          color: #fff;
        }
        .http {
          max-width: 512px;
          height: 40px;
          margin-left: auto;
          margin-right: 16px;
          padding: 0 12px;
          background: #ffffff;
          border-radius: 2px;
          border: 1px solid #d5d8de;
          display: flex;
          align-items: center;
          cursor: pointer;
          &-url {
            margin-left: 10px;
            line-height: 40px;
            overflow: hidden; /* 隐藏溢出内容 */
            white-space: nowrap; /* 禁止文本换行 */
            text-overflow: ellipsis; /* 显示省略符号 */
          }
        }
        .experience {
          width: 124px;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 20px;
          background: transparent;
          border-radius: 2px;
          color: #1747e5;
          border: 1px solid #1747e5;
          cursor: pointer;
        }
        .switch {
          display: flex;
          align-items: center;
          margin-left: auto;
          margin-right: 20px;
          font-size: 14px;
          color: #494e57;
          ::v-deep .el-switch__core {
            margin-left: 8px;
            width: 32px !important;
            height: 20px;
          }
          ::v-deep .el-switch__core:after {
            width: 12px;
            height: 12px;
          }
          ::v-deep .el-switch.is-checked .el-switch__core::after {
            margin-left: -14px;
            top: 3px;
          }
          ::v-deep .el-switch__core:after {
            top: 3px;
            margin-left: 1px;
          }
        }
      }
      &-bottom {
        width: 100%;
        height: 335px;
        background: #fff;
        padding: 0 20px;
        .t-table {
          :deep(
              .el-checkbox__input.is-checked .el-checkbox__inner,
              .el-checkbox__input.is-indeterminate .el-checkbox__inner
            ) {
            background-color: #1747e5;
            border-color: #1747e5;
          }
        }
        .row {
          width: 100%;
          .col1 {
            width: 45px;
          }
          .col2 {
            width: 39%;
            .icons {
              width: 40px;
              height: 40px;
              line-height: 40px;
              text-align: center;
              border-radius: 50%;
              background: #f7f8fa;
              margin-right: 16px;
            }
            .desr1 {
              font-family: MiSans, MiSans;
              font-weight: 600;
              font-size: 16px;
              color: #494e57;
              line-height: 24px;
            }
            .desr2 {
              margin-top: 4px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 20px;
            }
          }
          .col3 {
            width: 44%;
            height: 100%;
            &-result {
              width: 78px;
              height: 24px;
              background: rgba(130, 136, 148, 0.1);
              border-radius: 2px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              iconpark-icon {
                margin-right: 6px;
              }
            }
            .result-success {
              background: rgba(63, 184, 22, 0.1);
              color: #3fb816;
            }
          }
          .col4 {
            flex: 1;
            justify-content: flex-end;
            white-space: nowrap;
            .config-btn {
              width: 92px;
              height: 40px;
              background: #ffffff;
              border-radius: 2px;
              border: 1px solid #c9ccd1;
              font-family: MiSans, MiSans;
              font-weight: 600;
              font-size: 16px;
              color: #494e57;
              cursor: pointer;
            }
          }
        }
        .row1 {
          height: 48px;
        }
        .row2 {
          height: 96px;
          border-top: 1px solid rgba(0, 0, 0, 0.12);
        }
      }
    }
    &-private {
      margin-top: 24px;
      margin-bottom: 24px;
      padding: 0 4px 4px;
      border-radius: 4px;
      background: linear-gradient(
        270deg,
        rgba(80, 36, 200, 0.1) 0%,
        rgba(138, 91, 255, 0.1) 100%
      );
      &-top {
        height: 104px;
        width: 100%;
        border-radius: 4px 4px 0 0;
      }
      &-bottom {
        width: 100%;
        height: 168px;
        background: #fff;
        padding: 16px 20px 20px;
        .describe {
          font-size: 14px;
          color: #828894;
          display: flex;
          align-items: center;
          &-txt {
            display: flex;
            align-items: center;
            font-size: 14px;
            color: #1747e5;
            cursor: pointer;
          }
        }
        .notes {
          margin-top: 6px;
          font-size: 14px;
          color: #828894;
        }
        .set-up {
          margin-top: 16px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          &-items {
            width: 49%;
            &-label {
              font-size: 14px;
              color: #494e57;
              margin-bottom: 8px;
            }
            &-input {
              position: relative;
              .eye-icon {
                position: absolute;
                top: 12px;
                left: 12px;
                z-index: 9999;
                cursor: pointer;
              }
              .iconpark {
                position: absolute;
                top: 12px;
                right: 14px;
                z-index: 9999;
                cursor: pointer;
              }
              .input-apiSecret {
                :deep(.el-input__inner) {
                  padding: 0 15px 0 32px;
                }
              }
            }
          }
        }
      }
    }
    &-disabled {
      background: #f7f8fa;
    }
  }
  .disabledDiv{
    pointer-events: none;
    background-color: #F5F7FA;
    border-color: #E4E7ED;
    color: #C0C4CC !important;
    filter: grayscale(100%);
    cursor: not-allowed;
  }
  .flex {
    display: flex;
  }
  .items-center {
    align-items: center;
  }
  .justify-center {
    justify-content: center;
  }
  .justify-between {
    justify-content: space-between;
  }
}

</style>