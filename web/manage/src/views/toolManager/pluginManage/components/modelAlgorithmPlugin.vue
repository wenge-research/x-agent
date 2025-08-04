<template>
  <div class="outerDialog">
    <div class="headBar" v-show="headBarShow" >
      <div class="leftSlide">
        <img
          src="@/assets/images/arrow-go-back-fill.svg"
          @click="closeDialog"
        />
        <img v-if="appForm.icon" :src="appForm.icon" class="mx-icon" />
        <div v-else class="mx-img">
          <img src="@/assets/images/mxsf.svg" />
        </div>

        <div class="titleIcon">
          <p>
            {{ appForm.name || $t("noApplicationName") }}
            <img
              src="@/assets/images/edit-line2.svg"
              style="cursor: pointer"
              @click="editInfo"
            />
          </p>
          <p>{{ appForm.description || $t("noDescription") }}</p>
        </div>
      </div>
      <div class="rightSlide">
        <el-button
          class="btns btnsXian"
          v-permission="'issue'"
          @click="submitApp"
        >
          <div class="flex-center just-center">
            <img src="@/assets/images/send-plane-4-fill.svg" />
            <span>{{ $t("saveAndPublish") }}</span>
          </div>
        </el-button>

        <el-button
          class="btns"
          type="primary"
          v-permission="'issue'"
          @click="submitAddApp(2)"
        >
          <div class="flex-center just-center">
            <img src="@/assets/images/save-line1.svg" />
            <span>{{ $t("save") }}</span>
          </div>
        </el-button>
      </div>
    </div>
    <div class="dialogPower plugin">
      <el-row>
        <el-col :span="12">
          <div class="flex-center just orchestration">
            <div class="flex-center">
              <!-- <img :src="require('@/assets/images/chat-thread-fill.svg')" /> -->
              <span
                style="font-size: 22px; margin-left: 10px;color:color: #494E57;"
                >{{ $t("orchestration") }}</span
              >
            </div>
            <div class="largeModel">
              <el-select
                :placeholder="$t('selectPlaceholder')"
                style="width: 100%"
                v-model="appForm.modelId"
                class="assModel"
              >
                <div
                  slot="prefix"
                  class="selectPrice el-input__icon"
                  v-if="appForm.modelId"
                >
                  <img
                    style="width: 18px; height: 18px; margin: 11px 8px 11px 4px"
                    :src="chatGptIdList[appForm.modelId]"
                  />
                </div>
                <el-option
                  v-for="item in modleOptions"
                  :key="item.modelId"
                  :label="item.modelName"
                  :value="item.modelId"
                >
                  <div style="display: flex; align-items: center; height: 34px">
                    <img
                      style="width: 18px; height: 18px; margin-right: 8px"
                      :src="chatGptIdList[item.modelId]"
                    />
                    <span class="modelName">{{ item.modelName }}</span>
                  </div>
                </el-option>
              </el-select>
            </div>
          </div>
          <div style="height: calc(100vh - 190px); overflow-y: auto">
            <div class="promptWord">
              <div class="word">
                {{ $t("promptWord") }}
              </div>
              <el-input
                :placeholder="$t('pleaseInput')"
                v-model="appForm.modelInstruction"
                show-word-limit
                type="textarea"
                :autosize="{ minRows: 8, maxRows: 20 }"
                maxlength="2000"
              ></el-input>
            </div>
            <!-- 变量 -->
            <div class="plugin-bd-first">
              <div class="plugin-bd-tit">
                {{ $t("variable") }}
                <!-- <span @click="addPanel"><em>+</em>{{ $t("addParam") }}</span> -->
              </div>
              <div class="drawer-content">
                <div
                  v-for="(panel, index) in variableList"
                  :key="index"
                  :name="index.toString()"
                  style="cursor: pointer"
                >
                  <div class="params-list">
                    <div class="params-list-item" @click="variableClick(panel)">
                      <div class="params-list-item-name require">
                        {{ panel.name_en }}
                        <span style="color: red" v-if="panel.isRequired"
                          >*</span
                        >
                        <span class="params-list-item-type">{{
                          panel.feild_type
                        }}</span>
                      </div>
                      <div class="params-list-item-desc">
                        {{ panel.feild_desc }}
                      </div>
                      <div class="params-list-item-tool">
                        <img
                          :src="require('@/assets/images/edit-line2.svg')"
                          @click.stop="editIndex = index"
                        />
                        <img
                          :src="
                            require('@/assets/images/delete-bin-4-line1.svg')
                          "
                          @click="removePanel(index)"
                        />
                      </div>
                    </div>
                    <div class="save-form" v-if="editIndex === index">
                      <el-form
                        :model="panel"
                        :ref="'formRefs' + index"
                        :rules="rules"
                        label-width="80px"
                        @submit.prevent
                        class="variable-form"
                      >
                        <el-form-item
                          :label="$t('variableName')"
                          prop="name_en"
                        >
                          <el-input
                            size="medium"
                            :placeholder="$t('pleaseInput')"
                            v-model="panel.name_en"
                          ></el-input>
                        </el-form-item>
                        <el-form-item
                          :label="$t('fieldType')"
                          prop="feild_type"
                        >
                          <el-select
                            size="medium"
                            v-model="panel.feild_type"
                            style="width: 100%"
                          >
                            <el-option
                              label="string"
                              value="string"
                            ></el-option>
                            <el-option
                              label="integer"
                              value="integer"
                            ></el-option>
                          </el-select>
                        </el-form-item>
                        <el-form-item
                          :label="$t('description')"
                          prop="feild_desc"
                        >
                          <el-input
                            size="medium"
                            v-model="panel.feild_desc"
                            type="textarea"
                            :rows="2"
                            :placeholder="$t('pleaseInput')"
                          ></el-input>
                        </el-form-item>
                        <el-form-item
                          :label="$t('maxLength')"
                          prop="max_length"
                        >
                          <el-input-number
                            v-model="panel.max_length"
                            controls-position="right"
                            @change="handleChange"
                            :min="1"
                            :max="10000"
                            style="width: 100%"
                          ></el-input-number>
                        </el-form-item>
                        <el-form-item
                          :label="$t('isRequired')"
                          prop="isRequired"
                        >
                          <el-radio-group
                            size="medium"
                            v-model="panel.isRequired"
                          >
                            <el-radio :label="true">{{ $t("yes") }}</el-radio>
                            <el-radio :label="false">{{ $t("no") }}</el-radio>
                          </el-radio-group>
                        </el-form-item>
                      </el-form>
                      <div class="save-img">
                        <img
                          src="@/assets/images/check-line.svg"
                          @click="addParameterPanel(panel, 1, index)"
                        />
                        <img
                          src="@/assets/images/close-line1.svg"
                          @click="closeParameterPanel(1)"
                        />
                      </div>
                    </div>
                  </div>
                </div>
                <div class="save-form" v-if="parameterPanel">
                  <el-form
                    :model="panelList"
                    ref="formRefsList"
                    :rules="rules"
                    label-width="80px"
                    @submit.prevent
                    class="variable-form"
                  >
                    <el-form-item :label="$t('variableName')" prop="name_en">
                      <el-input
                        size="medium"
                        v-model="panelList.name_en"
                        :placeholder="$t('pleaseInput')"
                      ></el-input>
                    </el-form-item>
                    <el-form-item :label="$t('fieldType')" prop="feild_type">
                      <el-select
                        size="medium"
                        v-model="panelList.feild_type"
                        style="width: 100%"
                      >
                        <el-option label="string" value="string"></el-option>
                        <el-option label="integer" value="integer"></el-option>
                      </el-select>
                    </el-form-item>
                    <el-form-item :label="$t('description')" prop="feild_desc">
                      <el-input
                        size="medium"
                        v-model="panelList.feild_desc"
                        type="textarea"
                        :rows="2"
                        :placeholder="$t('pleaseInput')"
                      ></el-input>
                    </el-form-item>
                    <el-form-item :label="$t('maxLength')" prop="max_length">
                      <el-input-number
                        v-model="panelList.max_length"
                        controls-position="right"
                        @change="handleChange"
                        :min="1"
                        :max="10000"
                        style="width: 100%"
                      ></el-input-number>
                    </el-form-item>
                    <el-form-item :label="$t('isRequired')" prop="isRequired">
                      <el-radio-group
                        size="medium"
                        v-model="panelList.isRequired"
                      >
                        <el-radio :label="true">{{ $t("yes") }}</el-radio>
                        <el-radio :label="false">{{ $t("no") }}</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-form>
                  <div class="save-img">
                    <img
                      src="@/assets/images/check-line.svg"
                      @click="addParameterPanel(panelList, 2)"
                    />
                    <img
                      src="@/assets/images/close-line1.svg"
                      @click="closeParameterPanel(2)"
                    />
                  </div>
                </div>
              </div>
              <div style="margin-top: 10px">
                <el-button @click="addPanel" icon="el-icon-plus">
                  {{ $t("addParam") }}
                </el-button>
              </div>
            </div>
            <!-- 输出 -->
            <div class="plugin-bd-first" v-if="descResultList.length">
              <div class="plugin-bd-tit">
                {{ $t("output") }}
              </div>
              <div class="drawer-content">
                <div
                  v-for="(panel, index) in descResultList"
                  :key="index"
                  :name="index.toString()"
                >
                  <div class="params-list">
                    <div class="params-list-item">
                      <div class="params-list-item-name require">
                        {{ panel.name_en }}
                        <!-- <span style="color: red" v-if="panel.required">*</span> -->
                        <span class="params-list-item-type">{{
                          panel.feild_type
                        }}</span>
                      </div>
                      <div class="params-list-item-desc">
                        {{ panel.desc }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="plugin-ft">
            <div class="plugin-ft-tit flex-center">
              <div class="flex-center">
                <!-- <img src="@/assets/images/icon-contacts-fill.png" /> -->
                <span class="pluginDebug">{{ $t("pluginDebug") }}</span>
              </div>

              <span class="clear" @click="clearAllParams"
                ><i class="el-icon-takeaway-box"></i>
                {{ $t("clearContent") }}</span
              >
            </div>
            <div class="plugin-ft-cont">
              <div class="radio-list">
                <span
                  class="radio-list-left"
                  :class="{ active: runType === '1' }"
                  @click="runType = '1'"
                  >{{ $t("input") }}</span
                >
                <span
                  class="radio-list-right"
                  :class="{ active: runType === '2' }"
                  @click="runType = '2'"
                  >{{ $t("output") }}</span
                >
              </div>
              <div class="typeIn" v-if="runType === '1'">
                <el-form
                  :model="ruleForm"
                  :rules="rulesTypeIn"
                  ref="ruleForm"
                  label-width="0"
                  class="demo-ruleForm params-list"
                >
                  <el-form-item
                    label=""
                    v-for="(panel, index) in variableListOver"
                    :key="index"
                  >
                    <div class="params-list-item flex-align-just">
                      <div class="params-list-item-name">
                        {{ panel.name_en }}
                        <span style="color: red" v-if="panel.isRequired"
                          >*</span
                        >
                        <span
                          class="params-list-item-type"
                          :class="{
                            required: panel.isRequired,
                          }"
                          >{{ panel.feild_type }}</span
                        >
                      </div>
                      <!-- <div>
                        <el-radio-group size="medium" v-model="panelListFile">
                          <el-radio :label="true">{{ $t("file") }}</el-radio>
                          <el-radio :label="false">{{ $t("address") }}</el-radio>
                        </el-radio-group>
                      </div> -->
                    </div>
                    <!-- v-if="panelListFile" -->
                    <el-input
                      v-model="panel.value"
                      type="textarea"
                      rows="2"
                      :placeholder="$t('pleaseInput')"
                    ></el-input>
                    <!-- <div class="uploadOuter" v-else>
                      <el-upload
                        :action="actionUrl"
                        :file-list="fileListLogo"
                        :show-file-list="false"
                        :limit="1"
                        :on-remove="handleLogoRemove"
                        :on-success="handleLogoSuccess"
                      >
                        <i class="el-icon-upload"></i>
                        <div class="el-upload__text">
                          {{ $t("dragFileHere") }}，{{ $t("or")
                          }}<em>{{ $t("clickUpload") }}</em>
                        </div>
                        <div class="el-upload__tip" slot="tip">
                          支持.xlsx、.json、.jsonl、.png、.jpg、.jpeg、.pdf、.wav、.docx、.csv、.txt格式，仅支持单个文件且不超过20MB
                        </div>
                      </el-upload>
                    </div> -->
                  </el-form-item>
                </el-form>
                <!-- <el-button
                  type="primary"
                  class="run-btn"
                  @click="runApi"
                  :loading="runApiLoading"
                  icon="el-icon-video-play"
                >
                  <span style="font-size: 16px">{{ $t("startRun") }}</span>
                </el-button> -->
                <el-button
                  class="run-btn"
                  @click="runApi"
                  :loading="runApiLoading"
                  ><span
                    ><img
                      style="
                        width: 14px;
                        height: 14px;
                        vertical-align: bottom;
                        margin-right: 5px;
                      "
                      src="@/assets/images/send-plane-3-fill.svg"
                      alt="" /></span
                  >{{ $t("startRun") }}</el-button
                >
              </div>
              <div class="typeIn" v-if="runType === '2'">
                <div class="output">
                  <vue-json-pretty :data="outputJsonData" />
                </div>
                <!-- <div class="output" v-else>
                  <div class="output-text">
                    {{ outputDataByText }}
                  </div>
                </div> -->
                <!-- <el-button
                  type="primary"
                  class="run-btn"
                  @click="runApi"
                  :loading="runApiLoading"
                  icon="el-icon-video-play"
                >
                  <span style="font-size: 16px">{{ $t("startRun") }}</span>
                </el-button> -->
                <el-button
                  class="run-btn"
                  @click="runApi"
                  :loading="runApiLoading"
                  ><span
                    ><img
                      style="
                        width: 14px;
                        height: 14px;
                        vertical-align: bottom;
                        margin-right: 5px;
                      "
                      src="@/assets/images/send-plane-3-fill.svg"
                      alt="" /></span
                  >{{ $t("startRun") }}</el-button
                >
              </div>
            </div>
          </div></el-col
        >
      </el-row>
    </div>
    <el-drawer
      append-to-body
      :title="$t('publishApi')"
      :visible.sync="apiDrawer"
      style="width: 100%"
      size="35%"
      class="elDrawerList"
    >
      <modelAlporithmSetting
        v-if="apiDrawer"
        @closeDialogApi="closeDialogApi"
        @addSecret="addSecret"
        :appForm="appForm"
        @saveAndPublishClick="saveAndPublishClick"
        :secretAuthUserList="secretAuthUserList"
      ></modelAlporithmSetting>
    </el-drawer>
    <el-dialog
      :visible.sync="editDialogVisible"
      width="30%"
      class="flexDialog"
      :close-on-click-modal="true"
      append-to-body
      :title="keyName"
      top="8vh"
    >
      <addEditingKey
        :appForm="appForm"
        @isAddSecretClick="isAddSecretClick"
        addOrUpdate="add"
        @closeDialogSecret="closeDialogSecret"
        v-if="editDialogVisible"
      ></addEditingKey>
    </el-dialog>
    <Createplugin
      v-if="dialogVisible"
      :dialogVisible="dialogVisible"
      @confirmApplication="confirmApplication"
      @cancelCreateplugin="cancelCreateplugin"
      :params="appForm"
      modelType="1"
      type="edit"
    ></Createplugin>
  </div>
</template>

<script>
import modelAlporithmSetting from "@/views/toolManager/pluginManage/components/modelAlporithmSetting.vue";
import addEditingKey from "@/views/toolManager/pluginManage/components/addEditingKey.vue";
import { getModelPluginApiById, runComponent } from "@/api/workflow";
import axios from "axios";
import {
  updateComponent,
  apiValidate,
  updateModelPluginApi,
  getModelPluginApiAuthUserList,
} from "@/api/workflow";
import {
  addApplication,
  modelList,
  ttsList,
  defaultApp,
  getAuthChannels,
  applicationPluginDataList,
} from "@/api/app";
import VueJsonPretty from "vue-json-pretty";
import "vue-json-pretty/lib/styles.css";
import Createplugin from "../Createplugin.vue";

export default {
  components: {
    modelAlporithmSetting,
    VueJsonPretty,
    addEditingKey,
    Createplugin,
  },
  data() {
    return {
      apiDrawer: false,
      loading: false,
      runApiLoading: false,
      outputJsonData: {},
      component: {},
      editItem: {},
      runType: "1",
      ruleForm: {
        name: "",
      },
      fileListLogo: [],
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      rulesTypeIn: {
        rawQuery: [
          {
            required: true,
            message: this.$t("enterRawQuery"),
            trigger: "blur",
          },
        ],
      },
      activeName: "first",
      editIndex: "",
      editIndexTwo: "",
      formRefs: [],
      outputFormRefs: [],
      rules: {
        name_en: [
          {
            required: true,
            message: this.$t("enterVariableName"),
            trigger: "blur",
          },
        ],
        feild_desc: [
          {
            required: true,
            message: this.$t("enterDescription"),
            trigger: "blur",
          },
        ],
        feild_type: [
          {
            required: true,
            message: this.$t("selectFieldType"),
            trigger: "change",
          },
        ],
        isRequired: [
          {
            required: true,
            message: this.$t("pleaseSelect"),
            trigger: "change",
          },
        ],
        max_length: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
      },
      appForm: {},
      nodeApi: {},
      nodeStart: {},
      nodeEnd: {},
      outputText: "",
      outputDataByText: "",
      defaultProps: {
        children: "children",
        label: "label",
      },
      chatGptIdList: {
        f570229417e36672814ff51a695447eb5: require("@/assets/images/zpqy.png"),
        f570229417ef4672814ff51a695447eb5: require("@/assets/images/wxyy.png"),
        f570229417ef4672814ff51a65447eb5: require("@/assets/images/deepseek.png"),
        f570229417ef4d72814ff51a65447eb5: require("@/assets/images/kimi.png"),
        f570229417ef4d79814ff51a65447eb5: require("@/assets/images/yayi.png"),
        e4cc6a8d45f74e6099cae1f56a0fe5e9: require("@/assets/images/doubao.png"),
      },
      modleOptions: [],
      panelList: {},
      panelListTwo: {},
      parameterPanel: false,
      parameterPanelTwo: false,
      panelListFile: "",
      editDialogVisible: false,
      keyName: this.$t("createNewSecretKey"),
      variableList: [],
      descResultList: [],
      secretAuthUserList: [],
      variableListOver: [],
      dialogVisible: false,
    };
  },
  props: {
    appConfigForm: Object,
    headBarShow: {
      type: Boolean,
      default: true
    }
  },
  watch: {
    // 监听 panels 数组的变化
  },

  beforeDestroy() {},
  mounted() {
    this.getModelList(); //模型列表
    this.editItem = JSON.parse(JSON.stringify(this.appConfigForm));
    this.getPluginDetail(this.editItem.id);
    this.getModelPluginApiAuthUserList();
  },
  methods: {
    confirmApplication(params) {
      this.dialogVisible = false;
      for (let key in params) {
        this.appForm[key] = params[key];
      }
    },
    cancelCreateplugin() {
      this.dialogVisible = false;
    },
    editInfo() {
      this.dialogVisible = true;
    },
    getPluginDetail(id) {
      this.loading = true;
      getModelPluginApiById({
        id: id,
      }).then((res) => {
        this.loading = false;
        if (res.data) {
          this.appForm = res.data;
          this.variableList = JSON.parse(res.data?.descParam) || [];
          this.variableListOver = JSON.parse(JSON.stringify(this.variableList));
          this.descResultList = JSON.parse(res.data?.descResult) || [];
        } else {
          this.list = {};
        }
      });
    },
    removeDollarBraces(str) {
      // 正则表达式匹配 ${...} 格式的字符串
      const regex = /\$\{([^}]*)\}/g;
      // 使用 replace 方法替换匹配的部分
      return str.replace(regex, "$1");
    },
    updateApi(settings, inputs, closeDrawerFlag) {
      let manageAccessToken = sessionStorage.getItem("manageAccessToken");
      this.nodeApi.settings = settings;
      // this.nodeApi.input = inputs.map((el) => {
      //   return {
      //     label: el.name,
      //     variable: el.name,
      //     type: "string",
      //     desc: "",
      //     nodeId: this.nodeApi?.nodeId,
      //     referenceNodeId: this.nodeStart?.nodeId,
      //     referenceNodeName: "开始",
      //     valueType: /\$\{[^}]*\}/.test(el.value) ? "reference" : "string",
      //     value: this.removeDollarBraces(el.value),
      //     maxLength: 20,
      //     required: true,
      //     direction: 0,
      //   };
      // });
      if (closeDrawerFlag) {
        this.apiDrawer = false;
      }
    },
    closeDialogApi() {
      this.apiDrawer = false;
    },
    clearAllParams() {
      this.variableListOver.map((el) => (el.value = null));
      this.runType = "1";
    },
    handleLogoRemove(file, fileList) {
      this.uploadBtnLogo = false;
      this.appForm.logo = require("@/assets/images/applicationlogo.svg");
      this.fileListLogo = [];
    },
    handleLogoSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnLogo = true;
        this.appForm.logo =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListLogo = [];
      } else {
        this.uploadBtnLogo = false;
        this.appForm.logo = "";
        this.fileListLogo = [];
      }
    },
    copyText() {
      let text = this.$refs.copyLink.innerHTML;
      this.exeCommandCopyText(text);
      this.$message({
        message: this.$t("copySuccessed"),
        type: "success",
      });
    },
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
    addPanel() {
      this.parameterPanel = true;
      this.panelList = {};
    },
    addParameterPanel(item, type, index) {
      let form = "formRefs" + index;
      let list = type == 1 ? this.$refs[form][0] : this.$refs.formRefsList;
      list.validate((valid) => {
        if (valid) {
          const newPanel = {
            name_en: item.name_en,
            feild_type: item.feild_type,
            feild_desc: item.feild_desc,
            max_length: item.max_length,
            isRequired: item.isRequired,
          };
          if (this.variableList && this.variableList.length > 0) {
            if (type == 2) {
              this.variableList.push(newPanel);
              this.variableListOver = JSON.parse(
                JSON.stringify(this.variableList)
              );
            } else {
              this.variableListOver = JSON.parse(
                JSON.stringify(this.variableList)
              );
            }
          } else {
            this.variableList = [newPanel];
            this.variableListOver = JSON.parse(
              JSON.stringify(this.variableList)
            );
          }
          console.log(this.variableListOver, 7777);
          this.$nextTick(() => {
            this.$forceUpdate();
          });
          type == 1 ? (this.editIndex = "") : (this.parameterPanel = false);
        }
      });
    },
    closeParameterPanel(type) {
      type == 1 ? (this.editIndex = "") : (this.parameterPanel = false);
    },
    closeParameterPanelTwo(type) {
      type == 1 ? (this.editIndexTwo = "") : (this.parameterPanelTwo = false);
    },
    removePanel(index) {
      this.variableList.splice(index, 1);
      this.formRefs.splice(index, 1);
      this.$nextTick(() => {
        this.$forceUpdate();
      });
    },
    replaceTemplate(template, data) {
      return template.replace(/\$\{(\w+)\}/g, (match, key) => {
        return data[key] || match;
      });
    },
    handleClick(tab, event) {
      console.log(tab, event);
    },
    closeDialog() {
      this.$emit("configCloseDialog", false);
    },
    saveAndPublishClick(list) {
      console.log(this.variableList, 99);

      let params = JSON.parse(JSON.stringify(this.appForm));
      if (!params.modelId) {
        this.$message.warning("请选择模型");
        return;
      }
      updateModelPluginApi({
        modelId: params.modelId,
        modelInstruction: params.modelInstruction,
        authMethod: list.authMethod,
        category: list.category,
        descParam: JSON.stringify(this.variableList),
        descResult: params.descResult,
        pathApi: list.pathApi,
        docFileUrl: list.docFileUrl,
        enableFlag: 1,
        modelPluginApiId: params.modelPluginApiId,
        id: params.id,
      }).then((res) => {
        if (res.code == "000000") {
          this.closeDialog();
        } else {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }
      });
    },
    submitAddApp() {
      let params = JSON.parse(JSON.stringify(this.appForm));
      if (!params.modelId) {
        this.$message.warning("请选择模型");
        return;
      }
      updateModelPluginApi({
        modelId: params.modelId,
        modelInstruction: params.modelInstruction,
        authMethod: params.authMethod,
        category: params.category,
        descParam: JSON.stringify(this.variableList),
        descResult: params.descResult,
        pathApi: params.pathApi,
        docFileUrl: params.docFileUrl,
        enableFlag: 0,
        modelPluginApiId: params.modelPluginApiId,
        id: params.id,
        description: params.description,
        name: params.name,
        icon: params.icon,
      }).then((res) => {
        if (res.code == "000000") {
          this.closeDialog();
        } else {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }
      });
    },
    submitApp() {
      this.apiDrawer = true;
    },
    // 显示关联模型
    filterModleName(item) {
      let findItem = this.modleOptions.find((items) => items.modelId == item);
      return findItem?.modelName;
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
    handleChange() {},
    addSecret() {
      this.editDialogVisible = true;
    },
    isAddSecretClick() {
      this.editDialogVisible = false;
      this.getModelPluginApiAuthUserList();
    },
    // 获取秘钥值
    getModelPluginApiAuthUserList() {
      getModelPluginApiAuthUserList({
        modelPluginApiId: this.editItem.modelPluginApiId,
      })
        .then((res) => {
          if (res.code == "000000") {
            this.secretAuthUserList = res.data?.records;
            this.secretAuthUserList.forEach((item) => {
              item.showSecret = false;
            });
          }
        })
        .catch((err) => {});
    },
    variableClick(item) {
      this.appForm.modelInstruction = this.appForm.modelInstruction
        ? `${this.appForm.modelInstruction}{${item.name_en}}`
        : `{${item.name_en}}`;
    },
    // runApi() {
    //   let params = {};
    //   this.runApiLoading = true;
    //   this.nodeStart?.output.forEach((el) => {
    //     params[el.label] = el.value;
    //   });
    //   runComponent({
    //     componentId: this.editItem.componentId,
    //     inputs: params,
    //     clientId: Math.floor(Math.random() * 10000000000),
    //   }).then((res) => {
    //     this.runApiLoading = false;
    //     if (res.data) {
    //       this.outputJsonData = res;
    //       this.runType = "2";
    //       this.outputDataByText = this.replaceTemplate(
    //         this.nodeEnd?.settings?.responseTemplate,
    //         res.data
    //       );
    //     } else {
    //     }
    //   });
    // },
    runApi() {
      let params = {};
      this.runApiLoading = true;
      this.variableListOver.forEach((el) => {
        params[el.name_en] = el.value || "";
      });
      axios({
        method: "POST",
        url: `${process.env.VUE_APP_BASE_API}${this.editItem.pathApi}`,
        // url: `https://localhost/smart-agent-api-demo/matterGuideInfo/export`,
        headers: {
          Authorization:
            "Bearer " + sessionStorage.getItem("manageAccessToken"),
        },
        data: params,
      })
        .then((res) => {
          this.runApiLoading = false;
          if (res.data) {
            this.outputJsonData = res.data;
            this.runType = "2";
            //   this.outputDataByText = this.replaceTemplate(
            //     this.nodeEnd?.settings?.responseTemplate,
            //     res.data
            //   );
          } else {
            this.runApiLoading = false;
          }
        })
        .catch((error) => {
          console.log(error);
          this.runApiLoading = false;
          this.$message.error("系统异常");
        });
    },
    closeDialogSecret() {
      this.editDialogVisible = false;
    },
  },
};
</script>

<style lang="scss" scoped>
.flex {
  display: flex;
}
.outerDialog {
  display: flex;
  height: calc(100vh - 110px);
  flex-direction: column;
  .dialogPower {
    width: 100%;
    background: #ffffff;
    // border-radius: 4px 0 0 4px;
    border-top: 1px solid #e1e4eb;
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
    background: #fff;
    padding: 16px 0 16px 16px;
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
    width: 100%;
    // position: absolute;
    // top: 0;
    // right: 20px;
    padding-left: 40px;
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
      .mx-icon {
        margin: 0 12px 0 4px;
        width: 44px;
        height: 44px;
        border-radius: 50%;
      }
      .mx-img {
        margin: 0 12px 0 4px;
        width: 44px;
        height: 44px;
        background: #f0f4fa;
        border-radius: 4px;
        text-align: center;
        padding: 6px 0;
        img {
          width: 32px;
          height: 32px;
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
      .el-button.el-button--primary {
            background: #1747E5;
            border-color: #1747E5;
        }
      .btns {
        color: #fff;
        height: 40px;
        border-radius: 2px;
        img {
          margin-right: 5px;
        }
        img,
        span {
          vertical-align: middle;
        }
      }
      .btnsXian {
        color: #494e57;
        border-color: #c9ccd1;
      }
    }
  }
}

.el-row {
  margin-bottom: 20px;
  &:last-child {
    margin-bottom: 0;
  }
}
.el-col {
  border-radius: 4px;
}
.bg-purple-dark {
  background: #99a9bf;
}
.bg-purple {
  background: #d3dce6;
}
.bg-purple-light {
  background: #e5e9f2;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}
.plugin {
  position: relative;
  color: #383d47;
  &-hd {
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
    border-radius: 8px 0px 0px 8px;
    padding: 20px;
    height: calc(100vh - 112px);
    position: relative;
    .eidt-api {
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translate(-50%);
      width: 80%;
      border-color: #1c50fd;
      color: #1c50fd;
      border-radius: 4px;
    }
    &-tit {
      font-size: 16px;
      line-height: 20px;
      position: relative;
      i {
        position: absolute;
        right: 0;
        cursor: pointer;
      }
    }
    &-link {
      font-size: 14px;
      margin: 20px 0 40px;
    }
    &-info {
      position: relative;
      li {
        margin: 16px 0;
      }
      &-left {
        font-size: 14px;
        color: #828894;
        line-height: 18px;
      }
      &-right {
        position: absolute;
        right: 0;
        &.green {
          color: #30be14;
        }
        &.status {
          &::before {
            content: "";
            width: 8px;
            height: 8px;
            border-radius: 100%;
            background: #30be14;
            position: absolute;
            left: -16px;
            top: 3px;
          }
        }
      }
    }
  }
  &-bd {
    padding: 8px 0 0 20px;
    ::v-deep .el-tabs {
      .el-tabs__item {
        font-size: 16px;
        color: #383d47;
        padding-right: 6px;
      }
      .el-tabs__item.is-active {
        color: #383d47;
      }
      .el-tabs__active-bar {
        background-color: #1c50fd;
      }
      .el-tabs__nav-wrap::after {
        height: 1px;
        background: rgba(0, 0, 0, 0.12);
      }
      .el-tabs__content {
        height: calc(100vh - 175px);
        overflow: auto;
      }
    }
    &-tit {
      font-size: 16px;
      color: #383d47;
      line-height: 28px;
      position: relative;
      padding: 0 0 10px 10px;
      span {
        color: #1c50fd;
        font-size: 14px;
        margin: 0 0 0 36px;
        cursor: pointer;
        position: relative;
        em {
          font-size: 22px;
          position: absolute;
          left: -16px;
          top: -5px;
        }
      }
      &::before {
        content: "";
        width: 3px;
        height: 18px;
        background: #1c50fd;
        position: absolute;
        left: -0;
        top: 4px;
      }
    }
    &-first {
      position: relative;
      padding: 20px;
      .drawer-content {
        overflow-x: hidden;
      }
    }
    ::v-deep .el-form {
      border-radius: 4px;
      border: 1px solid #e1e4eb;
      padding: 26px 16px 16px;
      margin: 0 0 16px 0;
      position: relative;
      i.hide {
        position: absolute;
        right: 12px;
        top: 4px;
        cursor: pointer;
        font-size: 20px;
      }
    }
    .params-list {
      margin: 10px 0 0 0;

      &-item {
        border-radius: 4px;
        border: 1px solid #e1e4eb;
        padding: 16px;
        margin: 0 0 16px 0;
        position: relative;
        &-name {
          font-size: 16px;
          color: #383d47;
          line-height: 20px;
          font-weight: bold;
        }
        &-type {
          background: #f2f5fa;
          border-radius: 4px;
          font-size: 12px;
          color: #828894;
          position: relative;
          padding: 4px 12px;
          left: 10px;
          &.require {
            &::after {
              content: "*";
              position: absolute;
              left: -12px;
              top: 3px;
              color: red;
              font-size: 20px;
            }
          }
        }
        &-desc {
          font-size: 14px;
          color: #828894;
          margin: 10px 0 0 0;
        }
        &-tool {
          font-size: 20px;
          position: absolute;
          right: 16px;
          top: 16px;
          color: #828894;
          i {
            margin: 0 0 0 10px;
            cursor: pointer;
          }
        }
      }
    }
    &-second,
    &-third {
      .params-list {
        overflow: hidden;
        &-item {
          width: calc(50% - 20px);
          float: left;
          margin-right: 20px;
          background: #ffffff;
          box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          padding: 16px 16px 4px;
        }
        ul {
          li {
            margin: 16px 0;
            position: relative;
            span {
              font-size: 14px;
              color: #828894;
            }
            i {
              position: absolute;
              right: 0;
              border-radius: 10px;
              border: 1px solid #6c49db;
              color: #6c49db;
              font-size: 12px;
              padding: 3px 10px;
            }
            em {
              position: absolute;
              right: 0;
              font-size: 14px;
              color: #383d47;
            }
          }
        }
      }
    }
    &-response {
      background: #ffffff;
      border-radius: 4px;
      border: 1px solid #e1e4eb;
      margin: 10px 20px 0 0;
    }
    &-third {
      &-hd {
        margin: 10px 0;
        span {
          font-size: 14px;
          color: #828894;
          margin: 0 30px 0 0;
        }
      }
      .el-form {
        margin: 0 20px 16px 0;
        float: left;
        padding: 20px 20px 0 0;
        width: calc(50% - 20px);
      }
    }
  }
  &-ft {
    padding: 0;
    border-left: 1px solid #e1e4eb;
    position: relative;
    .run-btn {
      position: absolute;
      bottom: 20px;
      left: 50%;
      transform: translate(-50%);
      width: calc(100% - 64px);
      border-radius: 4px;
    }
    &-tit {
      position: relative;
      padding: 30px 20px 20px;
      font-size: 16px;
      color: #383d47;
      //   border-bottom: 1px solid #e1e4eb;
      img {
        width: 24px;
        height: 24px;
      }
      .pluginDebug {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 22px;
        color: #383d47;
        margin-left: 10px;
      }
      .clear {
        position: absolute;
        right: 20px;
        font-size: 16px;
        color: #828894;
        cursor: pointer;
      }
    }
    &-cont {
      height: calc(100vh - 270px);
      .radio-list {
        text-align: center;
        margin: 16px 24px;
        span {
          cursor: pointer;
          font-size: 16px;
          color: #828894;
          background: #f2f5fa;
          border-radius: 4px;
          display: inline-block;
          width: 50%;
          text-align: center;
          height: 40px;
          line-height: 40px;
          position: relative;
          &.active {
            background: #ffffff;
            box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
            border-radius: 2px;
            z-index: 1;
            color: #494e57;
          }
        }
        &-left {
          left: 10px;
        }
        &-right {
          right: 10px;
        }
      }
      .output {
        background: #f8f8f8;
        border-radius: 4px;
        height: calc(100vh - 320px);
        margin: 10px 32px;
        overflow: auto;
        padding: 0 10px 0 0;
        ::v-deep .vjs-key {
          color: #005cc5;
          width: fit-content;
          text-align: right;
          min-width: 72px;
        }
        ::v-deep .vjs-value-string {
          color: #666;
        }
        ::v-deep .vjs-tree-node {
          background: #f8f8f8 !important;
        }
        .output-text {
          padding: 6px 16px;
          display: block;
        }
      }
      .tips {
        position: absolute;
        text-align: center;
        left: 50%;
        top: 35%;
        transform: translateX(-50%);
        line-height: 40px;
        font-size: 16px;
        color: #383d47;
        img {
          display: block;
          margin: 0 auto;
        }
      }
    }

    ::v-deep .el-form {
      padding: 0 32px 16px;
      margin: 0 0 16px 0;
      position: relative;
      height: calc(100vh - 260px);
      overflow: auto;
    }
    .uploadOuter {
      background: #f9fafc;
      border-radius: 4px;
      border: 1px solid rgba(0, 0, 0, 0.12);
      position: relative;
      padding: 20px 20px 20px 110px;
      text-align: left;
      line-height: 24px;
      ::v-deep .el-upload {
        text-align: left;
      }
      .el-icon-upload {
        position: absolute;
        font-size: 40px;
        left: 50px;
        transform: translate(0, -70%);
        top: 50%;
        color: #b4bccc;
      }
      .el-upload__text {
        text-align: left;
        font-size: 16px;
        em {
          color: #3666ea;
        }
      }
      .el-upload__tip {
        font-size: 14px;
        color: #b4bccc;
        margin: 0;
      }
    }
    .params-list {
      &-item {
        height: 40px;
        background: #fff;
        border: none;
        padding: 0;
        position: relative;
        &-name {
          font-size: 16px;
          color: #383d47;
          line-height: 20px;
          font-weight: bold;
        }
        &-type {
          background: #f2f5fa;
          border-radius: 4px;
          font-size: 12px;
          color: #828894;
          position: relative;
          padding: 4px 6px;
          left: 10px;
          &.require {
            &::after {
              content: "*";
              position: absolute;
              left: -12px;
              top: 3px;
              color: red;
              font-size: 20px;
            }
          }
        }
        &-desc {
          font-size: 14px;
          color: #828894;
          margin: 10px 0 0 0;
        }
        &-tool {
          font-size: 20px;
          position: absolute;
          right: 16px;
          top: 16px;
          color: #828894;
          i {
            margin: 0 0 0 10px;
            cursor: pointer;
          }
        }
      }
    }
  }
}

.flex-center {
  display: flex;
  align-items: center;
}

.just {
  justify-content: space-between;
}

.orchestration {
  margin: 20px;
}
.largeModel {
}

.modelName {
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 16px;
  color: #494c4f;
  line-height: 20px;
}

.abilityInter {
  padding: 0 24px;
}

.promptWord {
  margin: 0 20px;
  background: #f0f5ff;
  border-radius: 11px;
  border: 1px solid #1c50fd;
  ::v-deep .el-textarea__inner {
    border: 1px solid #fff;
    border-radius: 4px 4px 10px 10px;
  }
  .word {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 14px;
    color: #383d47;
    line-height: 20px;
    margin: 10px 0 7px 12px;
  }
}

.params-list {
  margin-bottom: 12px;
  &-item {
    height: 40px;
    border: 1px solid #d5d8de;
    border-radius: 4px;
    display: flex;
    align-items: center;
    padding: 10px;
    position: relative;
    &-name {
      font-size: 16px;
      color: #383d47;
      line-height: 20px;
      font-weight: bold;
    }
    &-type {
      background: #ebeef2;
      border-radius: 4px;
      font-size: 12px;
      color: #828894;
      padding: 2px 4px;
      margin: 0 54px 0 6px;
    }
    &-desc {
      font-size: 14px;
      color: #828894;
      flex: 1;
    }
    &-tool {
      img {
        width: 16px;
        margin: 0 0 0 10px;
        cursor: pointer;
      }
    }
  }
}
.variable-form {
  flex: 1;
}

.save-form {
  display: flex;
  background: #f7f9fc;
  border-radius: 4px;
  margin-top: 12px;
  padding: 16px 10px;
  align-items: flex-start;
  .save-img {
    width: 60px;
    display: flex;
    margin-top: 6px;
    img {
      width: 24px;
      margin-left: 6px;
      cursor: pointer;
    }
  }
}

.flex-align-just {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.just-center {
  justify-content: center;
}

.elDrawerList {
  ::v-deep .el-drawer {
    border-radius: 8px 0px 0px 8px;
    display: block;
    .el-drawer__body {
      padding: 0px 24px;
      height: calc(100vh - 160px) !important;
      overflow-y: auto;
      overflow-x: hidden;
    }
  }
  ::v-deep .el-drawer__header {
    // background: linear-gradient(
    //   180deg,
    //   rgba(43, 88, 213, 0.1) 0%,
    //   rgba(43, 88, 213, 0) 100%
    // );
  }
}

.flexDialog {
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__body {
      padding: 10px 32px 20px;
    }
  }
  ::v-deep .el-dialog__header {
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
  }
}
</style>
