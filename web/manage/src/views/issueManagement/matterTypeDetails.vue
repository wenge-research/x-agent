<template>
  <div class="mattertype-details" v-loading="addLoading">
    <div class="mattertype-details-head">
      <div class="left">
        <iconpark-icon
          class="back-icon"
          name="arrow-go-back-fill"
          size="16"
          color="#828894"
          @click="comeBack"
        ></iconpark-icon>
        <div>
          <div class="left-top">
            {{ addDialogInfo.matterName }}
            <iconpark-icon
              name="edit-line"
              size="18"
              color="#828894"
              @click="updateMatter"
              :title="$t('edit')"
            ></iconpark-icon>
          </div>
          <div class="left-desc">{{ addDialogInfo.matterDesc }}</div>
        </div>
      </div>
      <div class="right">
        <el-switch
          v-model="sourceData.showFlag"
          :active-value="$t('yes')"
          :inactive-value="$t('no')"
        />
        <div>
          {{
            sourceData.showFlag == "是" || "yes" ? $t("enable") : $t("disable")
          }}
        </div>
        <el-button
          type="primary"
          style="width: 72px; border-radius: 4px; margin-left: 24px"
          @click="save"
          >{{ $t("save") }}</el-button
        >
      </div>
    </div>
    <div class="mattertype-details-content">
      <div class="base-info">
        <div class="base-info-title">
          <img src="@/assets/images/base-info-img.png" alt="" />
          <div>{{ $t("basicInformation") }}</div>
        </div>
        <div class="base-info-form">
          <el-form :model="baseInfo" ref="baseInfo">
            <!-- <el-form-item :label="$t('discussionTopics')">
              <el-input
                v-model="baseInfo.subject"
                :placeholder="$t('pleaseEnter')"
                clearable
              />
            </el-form-item> -->
            <el-form-item :label="$t('alias')">
              <el-input
                v-model="baseInfo.aliasName"
                :placeholder="$t('pleaseEnter')"
                clearable
              />
            </el-form-item>
            <el-form-item :label="$t('eventRouting')">
              <el-input
                v-model="baseInfo.matterRoute"
                :placeholder="$t('pleaseEnter')"
                clearable
              />
            </el-form-item>
            <el-form-item :label="$t('openingRemarksOfTheLargeModel')">
              <el-input
                v-model="baseInfo.prompt"
                :placeholder="$t('pleaseEnter')"
                clearable
              />
            </el-form-item>
            <el-form-item :label="$t('systemPromptWords')">
              <el-input
                v-model="baseInfo.extraSystemPrompt"
                :placeholder="$t('pleaseEnter')"
                clearable
              />
            </el-form-item>
            <el-form-item :label="$t('entrancePromptWords')">
              <el-input
                v-model="baseInfo.enterTip"
                :placeholder="$t('pleaseEnter')"
                clearable
              />
            </el-form-item>
            <el-form-item :label="$t('fillInPromptWords')">
              <el-input
                v-model="baseInfo.fillTip"
                :placeholder="$t('pleaseEnter')"
                clearable
              />
            </el-form-item>
            <el-form-item :label="$t('fillInThePrompt')">
              <el-input
                v-model="baseInfo.completeFillTip"
                :placeholder="$t('pleaseEnter')"
                clearable
              />
            </el-form-item>
            <el-form-item
              :label="$t('personalInformationCollectionStatement')"
              class="form-column"
              prop="statement"
            >
              <el-button plain @click="openEditorDialog"
                ><iconpark-icon
                  name="edit-line"
                  size="14"
                  color="#1C50FD"
                ></iconpark-icon
                >{{ $t("editContent") }}</el-button
              >
            </el-form-item>
            <el-form-item
              :label="$t('displayFormat')"
              class="form-column"
              prop="statement"
            >
              <el-select
                style="width: 100%"
                v-model="baseInfo.display"
                :placeholder="$t('selectPlaceholder')"
                clearable
              >
                <el-option
                  v-for="(item, index) in displayFormatList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="collect-form">
        <div class="collect-form-head">
          <div class="collect-form-head-title">
            <iconpark-icon
              name="survey-fill"
              size="22"
              color="#828894"
            ></iconpark-icon>
            <div>{{ $t("collectForms") }}</div>
          </div>
          <el-button
            style="
              width: 118px;
              border-radius: 4px;
              border: 1px solid #1c50fd;
              color: #1c50fd;
            "
            @click="addField"
            ><i class="el-icon-plus"></i> {{ $t("addField") }}</el-button
          >
        </div>
        <div class="fields">
          <div v-for="(groupItem, groupIdx) in newGroupList" :key="groupIdx">
            <div class="fields-group">
              {{ groupItem.title || $t("groupName") }}
            </div>
            <div
              class="fields-item"
              v-for="(item, index) in groupItem.list"
              :key="index"
              :class="[item.id == selectFormId ? 'row-border' : '']"
              @click="formClick(item)"
              draggable="true"
              @dragstart="dragStart(index, groupItem.list)"
              @dragover="dragOver(index, groupItem.list)"
              @drop="drop(index, groupItem.list)"
            >
              <div class="left">
                <iconpark-icon
                  name="draggable"
                  size="22"
                  color="#B4BCCC"
                ></iconpark-icon>
              </div>
              <div class="right">
                <div class="fields-name">
                  {{ item.filedName || $t("filedName") }}
                </div>
                <div
                  style="display: flex; align-items: center; overflow-x: auto"
                >
                  <el-input
                    v-if="item.formType == 'input'"
                    style="width: 100%"
                    v-model="item.value"
                    :placeholder="$t('pleaseEnter')"
                  />
                  <el-input
                    v-else-if="item.formType == 'textarea'"
                    style="width: 100%"
                    :rows="1"
                    v-model="item.value"
                    type="textarea"
                    :placeholder="$t('pleaseEnter')"
                  />
                  <el-select
                    v-else-if="item.formType == 'select'"
                    style="width: 100%; overflow: hidden"
                    v-model="item.value"
                    :placeholder="$t('selectPlaceholder')"
                  >
                    <el-option
                      v-for="(son, sonIdx) in item.optionList"
                      :key="sonIdx"
                      :label="son.lable"
                      :value="son.value"
                    />
                  </el-select>
                  <div
                    v-else-if="item.formType == 'radio'"
                    style="display: flex; height: 40px; align-items: center"
                  >
                    <el-radio
                      v-for="(radioItem, radioIndex) in item.optionList"
                      :key="radioIndex"
                      style="width: 100%"
                      v-model="radioItem.value"
                      >{{ radioItem.lable }}</el-radio
                    >
                  </div>
                  <div
                    v-else-if="item.formType == 'checkbox'"
                    style="display: flex; height: 40px; align-items: center"
                  >
                    <el-checkbox
                      v-for="(checkboxItem, checkboxIndex) in item.optionList"
                      :key="checkboxIndex"
                      style="width: 100%"
                      v-model="checkboxItem.value"
                      >{{ checkboxItem.lable }}</el-checkbox
                    >
                  </div>
                  <el-date-picker
                    v-else-if="item.formType == 'datePicker'"
                    style="width: 100%; overflow: hidden"
                    v-model="item.value"
                    type="date"
                    :placeholder="$t('selectDate')"
                  >
                  </el-date-picker>
                  <el-date-picker
                    v-else-if="item.formType == 'datetime'"
                    style="width: 100%; overflow: hidden"
                    v-model="item.value"
                    type="datetime"
                    :placeholder="$t('selectDateTime')"
                  >
                  </el-date-picker>
                  <el-upload
                    v-else-if="['file', 'image'].includes(item.formType)"
                    class="avatar-uploader"
                    action="https://jsonplaceholder.typicode.com/posts/"
                    :show-file-list="false"
                    :disabled="true"
                  >
                    <i class="el-icon-plus avatar-uploader-icon"></i>
                  </el-upload>
                </div>
                <div class="form-name">
                  {{ formTypeName(item.formType) || $t("input") }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="form-config">
        <div class="form-config-title">
          <div class="txt">{{ $t("formConfiguration") }}</div>
          <div
            v-show="matterGuideFiledList.length"
            class="delete-group"
            @click="deleteGroup"
          >
            {{ $t("delete") }}
          </div>
        </div>
        <ul class="form-config-head">
          <li
            :class="[fieldType == 'fieldInformation' ? 'selected' : '']"
            @click="fieldTypeChange('fieldInformation')"
          >
            {{ $t("fieldInformation") }}
          </li>
          <li
            :class="[fieldType == 'fieldManagement' ? 'selected' : '']"
            @click="fieldTypeChange('fieldManagement')"
          >
            {{ $t("fieldManagement") }}
          </li>
        </ul>
        <div class="form-config-content">
          <!-- 字段信息 -->
          <el-form
            v-show="fieldType == 'fieldInformation'"
            :model="fieldInformationForm"
            :rules="rules"
            ref="overallForm"
            class="configForm"
          >
            <el-form-item :label="$t('formTypes')" prop="formType">
              <el-select
                v-model="fieldInformationForm.formType"
                :placeholder="$t('pleaseSelect')"
                @change="
                  selectChange(fieldInformationForm.formType, 'formType')
                "
              >
                <el-option
                  v-for="(item, index) in formTypeList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('fieldChineseName')" prop="filedName">
              <el-input
                v-model="fieldInformationForm.filedName"
                :placeholder="$t('pleaseEnter')"
                @input="
                  inputChange(fieldInformationForm.filedName, 'filedName')
                "
              />
            </el-form-item>
            <el-form-item :label="$t('filedName')" prop="filedCode">
              <el-input
                v-model="fieldInformationForm.filedCode"
                :placeholder="$t('pleaseEnter')"
                @input="
                  inputChange(fieldInformationForm.filedCode, 'filedCode')
                "
                @blur="filedCodeBlur(fieldInformationForm.filedCode)"
              />
            </el-form-item>
            <el-form-item
              v-if="
                ['input', 'textarea'].includes(fieldInformationForm.formType)
              "
              :label="$t('wordLimit')"
            >
              <el-input-number
                v-model="fieldInformationForm.wordLimit"
                controls-position="right"
                :placeholder="$t('pleaseEnter')"
                @input="
                  inputChange(fieldInformationForm.wordLimit, 'wordLimit')
                "
                :min="1"
                :max="500"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item
              v-if="
                ['select', 'checkbox', 'radio'].includes(
                  fieldInformationForm.formType
                )
              "
              :label="$t('option')"
            >
              <ul>
                <li
                  v-for="(subItem, subIdx) in fieldInformationForm.optionList"
                  :key="subIdx"
                >
                  <el-input
                    v-model.trim="subItem.lable"
                    :placeholder="$t('pleaseEnter')"
                    class="option-input"
                    @blur="optionItemInput(subItem, subItem.lable)"
                  >
                    <i
                      slot="suffix"
                      class="option-clear el-icon-close"
                      @click.stop="deleteOption(subIdx)"
                    ></i>
                  </el-input>
                </li>
              </ul>
              <div class="add-option" @click="addOption">
                <iconpark-icon
                  name="add-line"
                  color="#1C50FD"
                  size="12"
                ></iconpark-icon>
                {{ $t("addOptions") }}
              </div>
            </el-form-item>
            <el-form-item :label="$t('prompt')">
              <el-input
                v-model="fieldInformationForm.placeholder"
                :placeholder="$t('pleaseEnter')"
                @input="
                  inputChange(fieldInformationForm.placeholder, 'placeholder')
                "
              />
            </el-form-item>
            <el-form-item label="">
              <el-checkbox
                v-model="fieldInformationForm.requiredFlag"
                @change="
                  checkboxChange(
                    fieldInformationForm.requiredFlag,
                    'requiredFlag'
                  )
                "
                >{{ $t("required") }}</el-checkbox
              >
            </el-form-item>
            <el-form-item label="">
              <el-input
                v-model="fieldInformationForm.requiredTips"
                :placeholder="$t('promptWhenNotFilledIn')"
                :disabled="!fieldInformationForm.requiredFlag"
                @input="
                  inputChange(fieldInformationForm.requiredTips, 'requiredTips')
                "
              />
            </el-form-item>
            <!-- <el-form-item label="">
                <el-checkbox v-model="fieldInformationForm.requiredFlag">添加填写说明</el-checkbox>
            </el-form-item> -->
            <el-form-item label="">
              <el-input
                v-model="fieldInformationForm.tip"
                :placeholder="$t('pleaseFillInTheExplanatoryText')"
                @input="inputChange(fieldInformationForm.tip, 'tip')"
              />
            </el-form-item>
            <el-form-item
              :label="$t('grouping')"
              class="Grouping"
              prop="filedCodeGroup"
            >
              <el-select
                v-model="fieldInformationForm.filedCodeGroup"
                :placeholder="$t('selectPlaceholder')"
                style="flex: 1"
                @change="
                  selectChange(
                    fieldInformationForm.filedCodeGroup,
                    'filedCodeGroup'
                  )
                "
              >
                <el-option
                  v-for="(item, index) in filedCodeGroupList"
                  :key="index"
                  :label="item.name"
                  :value="item.idx"
                />
              </el-select>
              <el-button
                plain
                style="
                  border-radius: 4px;
                  border: 1px solid #1c50fd;
                  color: #1c50fd;
                  margin-left: 12px;
                "
                @click="openGroupDialog"
                >{{ $t("groupManagement") }}</el-button
              >
            </el-form-item>
          </el-form>
          <!-- 字段管理 -->
          <el-form
            v-show="fieldType == 'fieldManagement'"
            :model="fieldManagementForm"
            ref="fieldManagementForm"
            class="configForm"
          >
            <el-form-item :label="$t('fieldVerificationRuleEncoding')">
              <el-input
                v-model="fieldManagementForm.checkRuleCode"
                :placeholder="$t('pleaseEnter')"
                @input="
                  inputChange(
                    fieldManagementForm.checkRuleCode,
                    'checkRuleCode'
                  )
                "
              />
            </el-form-item>
            <el-form-item
              v-if="
                ['input', 'textarea', 'select', 'radio', 'dataPicker'].includes(
                  fieldInformationForm.formType
                )
              "
              label=""
            >
              <el-checkbox
                v-model="fieldManagementForm.searchFlag"
                @change="
                  checkboxChange(fieldManagementForm.searchFlag, 'searchFlag')
                "
                >{{ $t("allowRetrieval") }}</el-checkbox
              >
            </el-form-item>
            <el-form-item
              v-if="
                ['是', true, 'true'].includes(fieldManagementForm.searchFlag) &&
                ['input', 'textarea', 'select', 'radio', 'dataPicker'].includes(
                  fieldInformationForm.formType
                )
              "
              label=""
            >
              <el-select
                v-model="fieldManagementForm.searchType"
                :placeholder="$t('selectPlaceholder')"
                @change="
                  selectChange(fieldManagementForm.searchType, 'searchType')
                "
              >
                <el-option
                  v-for="(item, index) in searchTypeList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="">
              <el-checkbox
                v-model="fieldManagementForm.uniqueFlag"
                @change="
                  checkboxChange(fieldManagementForm.uniqueFlag, 'uniqueFlag')
                "
                >{{ $t("joinOnly") }}</el-checkbox
              >
            </el-form-item>
            <el-form-item label="">
              <el-checkbox
                v-model="fieldManagementForm.tableShowFlag"
                @change="
                  checkboxChange(
                    fieldManagementForm.tableShowFlag,
                    'tableShowFlag'
                  )
                "
                >{{ $t("displayInTheList") }}</el-checkbox
              >
            </el-form-item>
            <el-form-item label="">
              <el-checkbox
                v-model="fieldManagementForm.exportFlag"
                @change="
                  checkboxChange(fieldManagementForm.exportFlag, 'exportFlag')
                "
                >{{ $t("allowExport") }}</el-checkbox
              >
            </el-form-item>
            <el-form-item label="">
              <el-checkbox
                v-model="fieldManagementForm.formShowFlag"
                @change="
                  checkboxChange(
                    fieldManagementForm.formShowFlag,
                    'formShowFlag'
                  )
                "
                >{{ $t("isTheFormDisplayed") }}</el-checkbox
              >
            </el-form-item>
            <el-form-item :label="$t('statusOfBackendConfiguration')">
              <el-radio-group
                v-model="fieldManagementForm.webFormStatus"
                @change="
                  radioChange(
                    fieldManagementForm.webFormStatus,
                    'webFormStatus'
                  )
                "
              >
                <el-radio label="0">{{ $t("notEditable") }}</el-radio>
                <el-radio label="1">{{ $t("editable") }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item :label="$t('clientStatus')">
              <el-radio-group
                v-model="fieldManagementForm.clientFormStatus"
                @change="
                  radioChange(
                    fieldManagementForm.clientFormStatus,
                    'clientFormStatus'
                  )
                "
              >
                <el-radio label="0">{{ $t("notEditable") }}</el-radio>
                <el-radio label="1">{{ $t("editable") }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
    <!-- 编辑事项 -->
    <create-event
      ref="createEvent"
      v-model="dialogVisible"
      :title="title"
      :form="addDialogInfo"
      :editMatterId="sourceData.id"
      :sourceData="sourceData"
      @close="closeDialog"
      @refreshHandler="refreshHandler"
    ></create-event>
    <!-- 编辑个人信息收集声明 -->
    <wangeditor
      ref="wangeditor"
      v-model="wangeditorVisible"
      @close="closeWangeditorDialog"
      :statement="baseInfo.statement"
      @editStatementValue="editStatementValue"
    ></wangeditor>
    <!-- 分组管理 -->
    <group-dialog
      ref="groupDialog"
      v-model="groupDialogVisible"
      :list="filedCodeGroupList"
      :groupId="sourceData.id"
      :matterId="sourceData.matterId"
      @close="closeGroupDialog"
      @refreshGroupList="refreshGroupList"
    ></group-dialog>
  </div>
</template>

<script>
import {
  apiGetMatterById,
  apiEditMatter,
  apiGetMatterGuideGroupList,
  apiCheckNameCodeExists,
} from "@/api/issueManagement.js";
import createEvent from "./components/create-event";
import wangeditor from "./components/wangeditor";
import groupDialog from "./components/group-dialog";

// 检索类型
const searchTypeList = [
  {
    label: "精确匹配",
    value: "1",
  },
  {
    label: "模糊匹配",
    value: "2",
  },
];
export default {
  components: {
    createEvent,
    wangeditor,
    groupDialog,
  },
  props: {
    matterTypeId: {
      type: [String, Number],
    },
  },
  data() {
    return {
      searchTypeList,
      formTypeList: [
        {
          label: this.$t("textBox"),
          value: "input",
        },
        {
          label: this.$t("textArea"),
          value: "textarea",
        },
        {
          label: this.$t("dropDownList"),
          value: "select",
        },
        {
          label: this.$t("radioButton"),
          value: "radio",
        },
        // {
        //   label: "复选框",
        //   value: "checkbox",
        // },
        {
          label: this.$t("date"),
          value: "datePicker",
        },
        // {
        //   label: "日期时间",
        //   value: "datetime",
        // },
        {
          label: this.$t("file"),
          value: "file",
        },
        {
          label: this.$t("image"),
          value: "image",
        },
        {
          label: this.$t("fixedFormWorkExperience"),
          value: "addFormGzjl",
        },
        {
          label: this.$t("fixedFormSkills"),
          value: "addFormZyjn",
        },
        {
          label: this.$t("fixedFormLanguageSkills"),
          value: "addFormWytc",
        },
        {
          label: this.$t("fixedFormComputerCertification"),
          value: "addFormJsjdjzs",
        },
      ], // 表单类型
      filedCodeGroupList: [], // 分组列表
      value: "是",
      fieldType: "fieldInformation", // fieldInformation: 字段信息 fieldManagement: 字段管理
      sourceData: {}, // 事项详情
      dialogVisible: false,
      wangeditorVisible: false,
      groupDialogVisible: false,
      title: "",
      selectFormId: "",
      addDialogInfo: {
        matterName: "", // 事项名称
        matterDesc: "", // 描述
        matterType: "", // 事项类型
        applicationId: "", // 绑定应用
        subjectFlag: "", // 是否讨论话题
        processing: "", // 处理方式
      }, // 新增所需字段
      baseInfo: {
        subject: "", // 讨论主题
        aliasName: "", // 别名
        matterRoute: "", // 事项路由
        prompt: "", // 大模型事项提示词
        extraSystemPrompt: "", // 系统提示词
        enterTip: "", // 入口提示词
        fillTip: "", // 填写提示词
        completeFillTip: "", // 填完后的提示语
        display: "", // 展示形式
        statement: "", // 声明
      }, // 基础信息
      displayFormatList: [
        {
          label: this.$t("sidePullProcess"),
          value: "侧拉流程",
        },
        {
          label: this.$t("popUpForm"),
          value: "弹框表单",
        },
        {
          label: this.$t("embeddedCard"),
          value: "内嵌卡片",
        },
        {
          label: this.$t("embeddedButtonUploadImage"),
          value: "内嵌按钮-上传图片",
        },
        {
          label: this.$t("embeddedButtonJumpToExternalNetwork"),
          value: "内嵌按钮-跳转外网",
        },
      ],
      matterGuideFiledList: [],
      fieldInformationForm: {
        formType: "",
        filedName: "",
        filedCode: "",
        wordLimit: "",
        placeholder: "",
        requiredFlag: "",
        requiredTips: "",
        tip: "",
        filedCodeGroup: "",
        optionList: [],
      }, // 字段信息表单
      fieldManagementForm: {
        checkRuleCode: "", // 字段校验规则
        searchFlag: "", // 允许检索
        searchType: "", // 精确/模糊匹配
        uniqueFlag: "", // 加入唯一
        tableShowFlag: "", // 在列表显示
        exportFlag: "", // 允许导出
        formShowFlag: "", // 表单是否显示
        webFormStatus: "", // 后台配置状态
        clientFormStatus: "", // 客户端配置状态
      }, // 字段管理表单
      rules: {
        formType: [
          {
            required: true,
            message: this.$t("formTypeCannotBeEmpty"),
            trigger: "change",
          },
        ],
        filedName: [
          {
            required: true,
            message: this.$t("fieldChineseNameCannotBeEmpty"),
            trigger: "blur",
          },
        ],
        filedCode: [
          {
            required: true,
            message: this.$t("fieldNameCannotBeEmpty"),
            trigger: "blur",
          },
        ],
        filedCodeGroup: [
          {
            required: true,
            message: this.$t("groupCannotBeEmpty"),
            trigger: "change",
          },
        ],
      },
      key: "",
      addLoading: false,
      draggedItem: "",
      newGroupList: [],
    };
  },
  computed: {
    groupList() {
      const groupedArray = this.matterGuideFiledList?.reduce((acc, curr) => {
        const title = this.filedCodeGroupList?.find(
          (i) => i.idx == curr.filedCodeGroup
        )?.name;
        const foundItem = acc.find((item) => item.title === title);

        if (foundItem) {
          foundItem.list.push(curr);
        } else {
          acc.push({ title: title, list: [curr] });
        }
        return acc;
      }, []);

      return groupedArray;
    },
    tenantId() {
        const tenantId = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.tenantId : "";
        return tenantId;
    }
  },
  watch: {
    groupList: {
      handler(n) {
        this.newGroupList = Object.assign([], [...n]);
      },
    },
  },
  async mounted() {
    await this.getMatterById();
    await this.getMatterGuideGroupList();
  },
  methods: {
    dragStart(index, data) {
      this.draggedItem = index;
    },
    dragOver(index, data) {
      event.preventDefault();
    },
    drop(index, data) {
      const draggedItem = data[this.draggedItem];
      data.splice(this.draggedItem, 1);
      data.splice(index, 0, draggedItem);
    },
    //
    formTypeName(type) {
      const name = this.formTypeList.find((i) => i.value == type)?.label;
      return name || "";
    },
    // 关闭新建事项弹窗
    closeDialog() {
      this.dialogVisible = false;
    },
    // 保存并关闭新建事项弹窗
    refreshHandler(id) {
      this.dialogVisible = false;
      this.getMatterById(id);
    },
    // 关闭新建事项弹窗
    closeWangeditorDialog() {
      this.wangeditorVisible = false;
    },
    // 打开个人信息收集声明弹框
    openEditorDialog() {
      this.wangeditorVisible = true;
    },
    // 关闭个人信息收集声明弹窗
    closeWangeditorDialog() {
      this.wangeditorVisible = false;
    },
    // 打开分组管理弹窗
    openGroupDialog() {
      this.groupDialogVisible = true;
    },
    // 关闭分组管理弹窗
    closeGroupDialog() {
      this.groupDialogVisible = false;
    },
    // 保存分组刷新分组list
    refreshGroupList() {
      this.groupDialogVisible = false;
      this.getMatterGuideGroupList();
    },
    //   表单配置 - 字段信息/字段管理切换
    fieldTypeChange(type) {
      this.fieldType = type;
    },
    // 返回列表
    comeBack() {
      this.$emit("comeBack");
    },
    // 详情
    async getMatterById(id) {
      this.addLoading = true;
      const res = await apiGetMatterById(id || this.matterTypeId);
      if (res.code == "000000") {
        this.sourceData = res.data || {};
        for (let key in this.baseInfo) {
          this.baseInfo[key] = this.sourceData[key];
        }
        for (let key in this.addDialogInfo) {
          this.addDialogInfo[key] = this.sourceData[key];
        }
        // 收集表单分组
        this.matterGuideFiledList = this.sourceData?.matterGuideFiledList || [];
        if (this.matterGuideFiledList?.length) {
          this.selectFormId = this.matterGuideFiledList[0].id;
        }
        if (this.matterGuideFiledList?.length) {
          for (let key in this.fieldInformationForm) {
            this.fieldInformationForm[key] = this.matterGuideFiledList[0][key];
            if (
              [
                "requiredFlag",
                "uniqueFlag",
                "tableShowFlag",
                "exportFlag",
                "formShowFlag",
                "searchFlag",
              ].includes(key)
            ) {
              this.fieldInformationForm[key] = ["是", true, "true"].includes(
                this.matterGuideFiledList[0][key]
              )
                ? true
                : false;
              if (
                [
                  "requiredFlag",
                  "uniqueFlag",
                  "tableShowFlag",
                  "exportFlag",
                  "formShowFlag",
                  "searchFlag",
                ].includes(key)
              ) {
                this.fieldInformationForm[key] = ["是", true, "true"].includes(
                  this.matterGuideFiledList[0][key]
                )
                  ? true
                  : false;
              }
            }
          }
          for (let keys in this.fieldManagementForm) {
            this.fieldManagementForm[keys] = this.matterGuideFiledList[0][keys];
            if (
              [
                "requiredFlag",
                "uniqueFlag",
                "tableShowFlag",
                "exportFlag",
                "formShowFlag",
                "searchFlag",
              ].includes(keys)
            ) {
              console.log("keys", keys);
              this.fieldManagementForm[keys] =
                this.matterGuideFiledList[0][keys] == "是" ? true : false;
            }
          }
        }
      }
      this.addLoading = false;
    },
    // 修改新增基本信息
    updateMatter() {
      this.dialogVisible = true;
      this.title = this.$t("editItem");
    },
    // 修改个人声明的值
    editStatementValue(html) {
      this.baseInfo.statement = html;
      this.wangeditorVisible = false;
    },
    // 分组列表
    async getMatterGuideGroupList() {
      const res = await apiGetMatterGuideGroupList({
        matterId: this.sourceData.matterId,
      });
      if (res.code == "000000") {
        this.filedCodeGroupList = res.data || [];
      }
    },
    // 收集表单点击
    formClick(data) {
      console.log("newId", data);
      this.fieldType = "fieldInformation";
      this.key = data?.id ? "id" : "newId";
      this.selectFormId = data[this.key];

      const sourceData = this.matterGuideFiledList.find(
        (item) => item[this.key] == data[this.key]
      );
      console.log("sourceData", sourceData);
      console.log("data", data);
      for (let key in this.fieldInformationForm) {
        this.fieldInformationForm[key] = sourceData[key];
        if (
          [
            "requiredFlag",
            "uniqueFlag",
            "tableShowFlag",
            "exportFlag",
            "formShowFlag",
            "searchFlag",
          ].includes(key)
        ) {
          this.fieldInformationForm[key] = ["是", true, "true"].includes(
            sourceData[key]
          )
            ? true
            : false;
        }
      }
      for (let keys in this.fieldManagementForm) {
        this.fieldManagementForm[keys] = sourceData[keys];
        if (
          [
            "requiredFlag",
            "uniqueFlag",
            "tableShowFlag",
            "exportFlag",
            "formShowFlag",
            "searchFlag",
          ].includes(keys)
        ) {
          this.fieldManagementForm[keys] = ["是", true, "true"].includes(
            sourceData[keys]
          )
            ? true
            : false;
        }
      }
      console.log("fieldInformationForm", this.fieldInformationForm);
    },
    // 收集表单 - 添加
    addField() {
      if (this.matterGuideFiledList?.length) {
        this.$refs.overallForm.validate((valid) => {
          if (valid) {
            this.addFieldHandler();
          }
        });
      } else {
        this.addFieldHandler();
      }
    },
    addFieldHandler() {
      this.fieldType = "fieldInformation";
      this.fieldInformationForm = {
        formType: "input",
        filedName: "",
        filedCode: "",
        wordLimit: "",
        placeholder: "",
        requiredFlag: "",
        requiredTips: "",
        tip: "",
        filedCodeGroup: "",
        optionList: [],
      };
      this.fieldManagementForm = {
        checkRuleCode: "", // 字段校验规则
        searchFlag: "", // 允许检索
        searchType: "", // 精确/模糊匹配
        uniqueFlag: "", // 加入唯一
        tableShowFlag: "", // 在列表显示
        exportFlag: "", // 允许导出
        formShowFlag: "", // 允许导出
        webFormStatus: "0", // 后台配置状态
        clientFormStatus: "0", // 客户端配置状态
      };
      const sourceData = Object.assign(
        {},
        {
          newId: new Date().getTime(),
          id: new Date().getTime(),
          ...this.fieldInformationForm,
          ...this.fieldManagementForm,
        }
      );
      this.matterGuideFiledList.unshift(sourceData);
      //   console.log("matterGuideFiledList----------", this.matterGuideFiledList)
      this.selectFormId = sourceData.newId;
      //   console.log("selectFormId----------", this.selectFormId)
      //   console.log("sourceData----------", sourceData)
    },
    // 表单配置 - 下拉框变动
    selectChange(v, fields) {
      console.log("v------", v);
      console.log("fields------", fields);
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData[fields] = v;
      if (v == "select") {
        sourceData.optionList = [];
      }
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
    },
    filedCodeBlur(v) {
      if (!v) return;
      this.checkNameCodeExists(v);
    },
    // 表单配置 - input框变动
    inputChange(v, fields) {
      console.log("v------", v);
      console.log("fields------", fields);
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData[fields] = v;
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
    },
    // 表单配置 - checkbox变动
    checkboxChange(v, fields) {
      console.log("v------", v);
      console.log("fields------", fields);
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData[fields] = v;
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
    },
    // 表单配置 - radio变动
    radioChange(v, fields) {
      console.log("v------", v);
      console.log("fields------", fields);
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData[fields] = v;
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
    },
    // 添加选项
    addOption() {
      let obj = {
        lable: "",
        value: "",
        idx: new Date().getTime(),
      };
      this.fieldInformationForm.optionList.push(obj);
    },
    // 删除分组
    deleteGroup() {
      console.log("this.gourp", this.selectFormId);
      this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
        confirmButtonText: this.$t("confirm"),
        cancelButtonText: this.$t("cancel"),
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(() => {
        this.matterGuideFiledList = this.matterGuideFiledList.filter(
          (i) => i.id != this.selectFormId
        );
        if (this.matterGuideFiledList?.length) {
          this.selectFormId = this.matterGuideFiledList[0].id;
          for (let key in this.fieldInformationForm) {
            this.fieldInformationForm[key] = this.matterGuideFiledList[0][key];
            if (
              [
                "requiredFlag",
                "uniqueFlag",
                "tableShowFlag",
                "exportFlag",
                "formShowFlag",
                "searchFlag",
              ].includes(key)
            ) {
              this.fieldInformationForm[key] = ["是", true, "true"].includes(
                this.matterGuideFiledList[0][key]
              )
                ? true
                : false;
            }
          }
          for (let keys in this.fieldManagementForm) {
            this.fieldManagementForm[keys] = this.matterGuideFiledList[0][keys];
            if (
              [
                "requiredFlag",
                "uniqueFlag",
                "tableShowFlag",
                "exportFlag",
                "formShowFlag",
                "searchFlag",
              ].includes(keys)
            ) {
              this.fieldManagementForm[keys] = ["是", true, "true"].includes(
                this.matterGuideFiledList[0][keys]
              )
                ? true
                : false;
            }
          }
        } else {
          this.selectFormId = "";
          for (let key in this.fieldInformationForm) {
            this.fieldInformationForm[key] = "";
          }
          for (let keys in this.fieldManagementForm) {
            this.fieldManagementForm[keys] = "";
          }
        }
      });
    },
    optionItemInput(data, lable) {
      data.value = lable;
      console.log("data-----------", data);
      console.log(
        "this.fieldInformationForm.optionList",
        this.fieldInformationForm.optionList
      );
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData.optionList = this.fieldInformationForm.optionList;
      console.log("sourceData---3333", sourceData);
      //   console.log("sourceIdx", sourceIdx);
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
      //   console.log("matterGuideFiledList", this.matterGuideFiledList);
    },
    // 删除选项
    deleteOption(idx) {
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData.optionList = sourceData.optionList.filter(
        (ele, index) => index != idx
      );
      this.fieldInformationForm.optionList = sourceData.optionList;
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      console.log("sourceData----删除选项", sourceData);
      console.log("sourceIdx", sourceIdx);
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
    },
    // 字段名判重
    async checkNameCodeExists(filedCode) {
      const params = {
        matterId: this.sourceData.matterId,
        filedCode,
      };
      const res = await apiCheckNameCodeExists(params);
      if (res.code != "000000") {
        this.$message.warning(this.$t("duplicateFieldNamesPleaseModify"));
      }
    },
    // 保存
    async save() {
      // const matterGuideFiledList = this.matterGuideFiledList.filter((item) => {
      //   if ("newId" in item) {
      //     delete item.id;
      //     delete item.newId;
      //   }
      //   return true;
      // });
      this.matterGuideFiledList = this.matterGuideFiledList.filter((item) => {
        if ("newId" in item) {
          delete item.id;
          delete item.newId;
        }
        return true;
      });
      this.matterGuideFiledList.forEach((ele) => {
        if (["select", "radio", "checkbox"].includes(ele.formType)) {
          ele.optionList = ele.optionList?.filter((i) => i.lable);
          ele.optionList?.forEach((k) => {
            k.value = k.lable;
          });
        }
        for (let key in ele) {
          if (
            [
              "requiredFlag",
              "uniqueFlag",
              "tableShowFlag",
              "exportFlag",
              "formShowFlag",
              "searchFlag",
            ].includes(key)
          ) {
            ele[key] = ["是", "true", true].includes(ele[key]) ? "是" : "否";
          }
        }
      });
      console.log("newGroupList", this.newGroupList);
      console.log("matterGuideFiledList", this.matterGuideFiledList);
      const matterGuideFiledList = this.newGroupList?.flatMap(
        (obj) => obj.list
      );
      matterGuideFiledList.forEach((item, index) => {
        item.sorted = index + 1;
      });

      const params = {
        ...this.baseInfo,
        id: this.sourceData.id,
        showFlag: this.sourceData.showFlag,
        matterGuideFiledList,
        tenantId: this.tenantId
      };
      this.addLoading = true;
      const res = await apiEditMatter(params);
      if (res.code == "000000") {
        console.log("res", res);
        this.$message.success(this.$t("success"));
        this.$emit("refreshList");
      } else {
        this.$message.warning(res.msg);
      }
      this.addLoading = false;
    },
  },
};
</script>

<style lang="scss" scoped>
.mattertype-details {
  width: 100%;
  height: 100%;
  &-head {
    height: 80px;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .left {
      display: flex;
      align-items: center;
      .back-icon {
        width: 20px;
        margin-right: 16px;
      }
      iconpark-icon {
        cursor: pointer;
      }
      &-top {
        display: flex;
        align-items: center;
        height: 26px;
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 20px;
        color: #383d47;
        line-height: 26px;
        iconpark-icon {
          margin-left: 12px;
        }
      }
      &-desc {
        margin-top: 4px;
        height: 18px;
        font-family: MiSans, MiSans;
        font-size: 14px;
        color: #828894;
        line-height: 18px;
      }
    }
    .right {
      display: flex;
      align-items: center;
    }
  }
  &-content {
    width: 100%;
    height: calc(100% - 80px);
    display: flex;
    background: #fff;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    .base-info {
      width: 520px;
      height: 100%;
      border-right: 1px solid rgba(0, 0, 0, 0.12);
      padding: 24px 32px;

      &-title {
        display: flex;
        align-items: center;
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 20px;
        color: #383d47;
        img {
          width: 24px;
          height: 24px;
          margin-right: 8px;
        }
      }
      &-form {
        margin-top: 24px;
        height: calc(100% - 24px - 24px);
        width: 100%;
        overflow: auto;
        &::-webkit-scrollbar {
          display: none;
        }
        .form-column {
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          ::v-deep .el-form-item__content {
            width: 100%;
          }
          ::v-deep .el-button {
            width: 100%;
            display: flex;
            align-items: center;
            border-radius: 4px;
            border: 1px solid #1c50fd;
            font-size: 16px;
            color: #1c50fd;
            span {
              display: flex;
              align-items: center;
              justify-content: center;
              width: 100%;
              iconpark-icon {
                margin-right: 6px;
              }
            }
          }
        }
      }
    }
    .collect-form {
      width: 520px;
      height: 100%;
      border-right: 1px solid rgba(0, 0, 0, 0.12);
      padding: 24px 32px;
      &-head {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 16px;
        &-title {
          display: flex;
          align-items: center;
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 20px;
          color: #383d47;
          iconpark-icon {
            margin-right: 8px;
          }
        }
      }
      .fields {
        width: 100%;
        height: calc(100% - 56px);
        overflow: auto;
        &::-webkit-scrollbar {
          display: none;
        }
        &-group {
          width: 100%;
          height: 40px;
          margin-bottom: 8px;
          display: flex;
          align-items: center;
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 18px;
          color: #383d47;
          &::before {
            content: "";
            display: inline-block;
            width: 3px;
            height: 18px;
            background: #1c50fd;
            margin-right: 8px;
          }
        }
        &-item {
          width: 100%;
          height: 112px;
          display: flex;
          background: #ffffff;
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          margin-bottom: 12px;
          cursor: pointer;
          .left {
            width: 32px;
            height: 100%;
            border-right: 1px solid #e1e4eb;
            background: #f2f5fa;
            border-radius: 7px 0px 0px 7px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
          .right {
            flex: 1;
            height: 100%;
            position: relative;
            padding: 20px 24px;
            overflow: hidden;
            .fields-name {
              margin-bottom: 8px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              line-height: 24px;
              font-size: 16px;
              color: #383d47;
            }
            .form-name {
              position: absolute;
              right: 0;
              top: 0;
              padding: 5px 8px;
              background: rgba(130, 136, 148, 0.1);
              border-radius: 0px 8px 0px 8px;
              font-family: MiSans, MiSans;
              font-size: 14px;
              color: #828894;
            }
          }
        }
        .row-border {
          border: 1px solid #1c50fd;
        }
      }
      ::v-deep .el-radio {
        width: auto !important;
      }
      ::v-deep .el-checkbox {
        width: auto !important;
      }
    }
    .form-config {
      flex: 1;
      height: 100%;
      padding: 24px 32px;
      .add-option {
        z-index: 99;
        max-width: 108px;
        display: flex;
        align-items: center;
        margin-bottom: 16px;
        height: 28px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #1c50fd;
        line-height: 28px;
        cursor: pointer;
        iconpark-icon {
          margin-right: 4px;
        }
      }
      .option-input {
        position: relative;
        margin-bottom: 8px;
      }
      .option-clear {
        position: absolute;
        right: 14px;
        top: 14px;
        cursor: pointer;
        color: #828894;
      }
      .Grouping {
        ::v-deep .el-form-item__content {
          display: flex;
        }
      }
      &-title {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 28px;
        .txt {
          display: flex;
          align-items: center;
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 18px;
          color: #383d47;
          line-height: 28px;
          &::before {
            content: "";
            display: block;
            width: 3px;
            height: 18px;
            background: #1c50fd;
            margin-right: 8px;
          }
        }

        .delete-group {
          font-weight: 600;
          font-size: 16px;
          color: #d82225;
          cursor: pointer;
        }
      }
      &-head {
        margin-top: 20px;
        display: flex;
        align-items: center;
        width: 100%;
        height: 40px;
        background: #f2f5fa;
        border-radius: 4px;
        text-align: center;
        li {
          flex: 1;
          height: 100%;
          line-height: 40px;
          font-family: MiSans, MiSans;
          font-size: 16px;
          color: #828894;
          cursor: pointer;
        }
        .selected {
          background: #d1e0fe;
          border-radius: 4px;
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 16px;
          color: #1c50fd;
        }
      }
      &-content {
        margin-top: 16px;
        width: 100%;
        height: calc(100% - 88px);
        overflow: auto;
        &::-webkit-scrollbar {
          display: none;
        }
        .configForm {
          .el-form-item {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            ::v-deep .el-form-item__content {
              width: 100%;
            }
            ::v-deep .el-select {
              width: 100%;
            }
          }
        }
      }
      ::v-deep .el-input-number .el-input__inner {
        text-align: left;
      }
      ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner,
      .el-checkbox__input.is-indeterminate .el-checkbox__inner {
        background: #1c50fd;
        border-color: #1c50fd;
      }
      ::v-deep .el-checkbox__label {
        color: #1c50fd;
      }
      ::v-deep .el-radio__inner::after {
        width: 6px;
        height: 6px;
      }
    }
  }
  ::v-deep .el-switch__core {
    width: 32px !important;
    height: 20px;
    border-radius: 12px;
    background: #ced4e0;
    border: none;
    margin-right: 4px;
    &::after {
      width: 14px;
      height: 14px;
      top: 3px !important;
    }
  }
  ::v-deep .el-switch.is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
    &::after {
      width: 14px;
      height: 14px;
      top: 3px !important;
    }
  }
  ::v-deep .el-form-item__label {
    height: 24px;
    font-family: MiSans, MiSans;
    font-size: 16px;
    color: #383d47;
    line-height: 24px;
    margin-bottom: 8px;
  }
}
</style>