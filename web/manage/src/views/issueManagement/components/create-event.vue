<template>
  <!-- 创建事项 -->
  <el-dialog
    :title="title"
    :visible.sync="dialogVisible"
    top="3%"
    width="560px"
    height="684px"
    :modal-append-to-body="false"
    append-to-body
    custom-class="matter-dialog"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    @open="openDialog"
  >
    <div class="dialog-config">
      <el-form ref="matterForm" :model="matterForm" :rules="rules">
        <el-form-item prop="matterName" :label="$t('matterName')">
          <el-input
            v-model="matterForm.matterName"
            :placeholder="$t('inputName')"
            maxlength="200"
            show-word-limit
            clearable
          />
        </el-form-item>
        <el-form-item prop="matterDesc" :label="$t('eventDescription')">
          <el-input
            v-model="matterForm.matterDesc"
            :placeholder="$t('pleaseEnter')"
            type="textarea"
            rows="5"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item
          class="select-form"
          prop="matterType"
          :label="$t('matterType')"
        >
          <el-select v-model="matterForm.matterType" clearable>
            <el-option
              v-for="(item, index) in matterTypeList"
              :key="index"
              :label="item.name"
              :value="item.idx"
            />
          </el-select>
        </el-form-item>
        <!-- <el-form-item
          class="select-form"
          prop="applicationId"
          :label="$t('bindingApplications')"
        >
          <el-select v-model="matterForm.applicationId" clearable>
            <el-option
              v-for="(item, index) in applicationIdList"
              :key="index"
              :label="item.applicationName"
              :value="item.applicationId"
            />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item
          class="select-form checkbox-form"
          prop="subjectFlag"
          :label="$t('doYouWantToDiscussTheTopic')"
        >
          <el-checkbox v-model="matterForm.subjectFlag"></el-checkbox>
        </el-form-item> -->
      </el-form>
    </div>
    <div class="dialog-footer">
      <el-button type="primary" @click="onSubmit" :loading="addLoading"
        >{{ $t('confirm') }}</el-button
      >
      <el-button plain @click="closeDialog" :loading="addLoading"
        >{{ $t('cancel') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import {
  apiGetMatterGuideTypeList,
  apiAddMatter,
  apiEditMatter,
} from "@/api/issueManagement.js";
import { getApplicationInfoList } from "@/api/index.js";
import { getInterceptWordHandlingMethodList } from "@/api/toolManager";

export default {
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
    },
    form: {
      type: Object,
      default: () => {},
    },
    sourceData: {
      type: Object,
      default: () => {},
    },
    editMatterId: {
      type: [String, Number],
    },
  },
  data() {
    return {
      dialogVisible: false,
      addLoading: false,
      matterTypeList: [],
      applicationIdList: [],
      matterForm: {
        matterName: "", // 事项名称
        matterDesc: "", // 描述
        matterType: "", // 事项类型
        // applicationId: "", // 绑定应用
        subjectFlag: "", // 是否讨论话题
      },
      rules: {
        matterName: [
          {
            required: true,
            message: "请输入事项名称",
            trigger: "blur",
          },
        ],
        matterType: [
          {
            required: true,
            message: "请选择事项类型",
            trigger: "change",
          },
        ],
        // applicationId: [
        //   {
        //     required: true,
        //     message: "请选择绑定应用",
        //     trigger: "change",
        //   },
        // ],
      },
      handledingListData: [],
      handlingList: [],
      handlingForm: {},
      handlingFourList: [
        {
          name: this.$t("limitedAnswer"),
        },
        {
          name: this.$t("addPrefix"),
        },
        {
          name: this.$t("addSuffix"),
        },
        {
          name: this.$t("replacementIssues"),
        },
      ],
      handleWordList: {
        answer: this.$t("limitedAnswer"),
        preQuestion: this.$t("addPrefix"),
        extendQuestion: this.$t("addSuffix"),
        replaceQuestion: this.$t("replacementIssues"),
      },
      placrholdername: {
        "": this.$t("pleaseEnter"),
        限定答案: this.$t("enterALimitedAnswer"),
        添加前缀: this.$t("enterPrefix"),
        添加后缀: this.$t("enterSuffix"),
        替换问题: this.$t("enterTheReplacementContent"),
      },
      processList: {
        way: ""
      },
    };
  },
  computed: {
    tenantId() {
        const tenantId = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.tenantId : "";
        return tenantId;
    }
  },
  watch: {
    value(n) {
      this.dialogVisible = n;
      if (this.title.includes("编辑")) {
        this.matterForm = Object.assign(
          {},
          {
            ...this.form,
            subjectFlag: this.form.subjectFlag == 1 ? true : false
          }
        );
        if (this.form.processing) {
          let process = JSON.parse(this.form.processing);
          this.processList.way = process && process.way;
          this.handledingListData = [];
          for (var key in process) {
            if (key != "way") {
              this.handledingListData.push({
                name: this.handleWordList[key],
                content: process[key],
              });
            }
          }
        }
      }
    },
  },
  mounted() {
    this.handlingMethodList();
  },
  methods: {
    openDialog() {
      this.getMatterGuideTypeList();
      this.getApplicationInfoList();
    },
    closeDialog() {
      this.$refs.matterForm.resetFields();
      this.$emit("close");
    },
    // 事项类型数据源
    async getMatterGuideTypeList() {
      const res = await apiGetMatterGuideTypeList({});
      if (res.code == "000000") {
        this.matterTypeList = res.data || [];
      }
    },
    // 绑定应用数据源
    async getApplicationInfoList() {
      const res = await getApplicationInfoList({ pageNo: 1, pageSize: 100 });
      if (res.code == "000000") {
        this.applicationIdList = res.data?.records || [];
      }
    },
    onSubmit() {
      this.$refs.matterForm.validate((valid) => {
        if (valid) {
          if (this.title.includes("编辑")) {
            this.editMatter();
          } else {
            this.addMatter();
          }
        }
      });
    },
    // 新增
    async addMatter() {
      this.addLoading = true;
      this.handledingListData.forEach((item) => {
        if (item.name == this.$t("limitedAnswer")) {
          this.processList.answer = item.content;
        }
        if (item.name == this.$t("addPrefix")) {
          this.processList.preQuestion = item.content;
        }
        if (item.name == this.$t("addSuffix")) {
          this.processList.extendQuestion = item.content;
        }
        if (item.name == this.$t("replacementIssues")) {
          this.processList.replaceQuestion = item.content;
        }
      });
      const params = {
        ...this.matterForm,
        subjectFlag: this.matterForm.subjectFlag ? 1 : 0,
        processing: this.matterForm.subjectFlag ? JSON.stringify(this.processList) : "",
        tenantId: this.tenantId
      };
      try {
        const res = await apiAddMatter(params);
        if (res.code == "000000") {
          this.$message.success(this.$t("success"));
          const id = res.data?.id;
          this.$refs.matterForm?.resetFields();
          this.$emit("refreshHandler", id);
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.addLoading = false;
      }
      this.addLoading = false;
    },
    // 编辑
    async editMatter() {
      this.addLoading = true;
      this.handledingListData.forEach((item) => {
        if (item.name == this.$t("limitedAnswer")) {
          this.processList.answer = item.content;
        }
        if (item.name == this.$t("addPrefix")) {
          this.processList.preQuestion = item.content;
        }
        if (item.name == this.$t("addSuffix")) {
          this.processList.extendQuestion = item.content;
        }
        if (item.name == this.$t("replacementIssues")) {
          this.processList.replaceQuestion = item.content;
        }
      });
      const params = {
        id: this.editMatterId,
        ...this.matterForm,
        subjectFlag: this.matterForm.subjectFlag ? 1 : 0,
        processing: this.matterForm.subjectFlag ? JSON.stringify(this.processList) : "",
        tenantId: this.tenantId
      };
      try {
        const res = await apiEditMatter(params);
        if (res.code == "000000") {
          this.$message.success(this.$t("success"));
          const id = res.data?.id;
          this.$refs.matterForm?.resetFields();
          this.$emit("refreshHandler", id);
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.addLoading = false;
      }
      this.addLoading = false;
    },

    // 从详情打开弹窗
    openDialogEdit(data) {
      this.openDialog();
      this.matterForm = Object.assign(
        {},
        {
          ...data,
        }
      );
    },
    async handlingMethodList() {
      let res = await getInterceptWordHandlingMethodList();
      if (res.code == "000000") {
        this.handlingList = res.data || [];
      }
    },
    addNewHandleing() {
      this.handledingListData.push({
        name: "",
      });
    },
    handleSelect() {
      this.setDisable();
    },
    setDisable() {
      this.handlingFourList.forEach((item) => {
        let isdisable = this.handledingListData.findIndex((val) => val.name == item.name);
        if (isdisable != -1) {
          item.disabled = true;
        } else {
          item.disabled = false;
        }
      });
    },
    deleteClick(name, index) {
      this.handledingListData.splice(index, 1);
      this.handlingFourList.forEach((item) => {
        if (item.name == name) {
          item.disabled = false;
        }
      });
    },
    checkboxChange(val) {
      if (this.title.includes("创建") && val) {
        this.processList.way = '事项'
      }
      if (!val) {
        this.handledingListData = []
        this.processList = {
          way: ''
        }
      }
    }
  },
};
</script>

<style lang="scss" scoped>
.checkbox-form {
  display: flex;
  flex-direction: row !important;
  margin-bottom: 10px;
  ::v-deep .el-form-item__label {
    width: 136px;
    white-space: nowrap;
  }
  ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner, .el-checkbox__input.is-indeterminate .el-checkbox__inner {
    border-color: #1C50FD;
    background: #1C50FD;
  }
}

.flex {
  display: flex;
  align-items: center;
  .box {
    width: 3px;
    height: 18px;
    background: #1c50fd;
  }
  .name {
    margin-left: 8px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #383d47;
    line-height: 28px;
  }
}

.handle-list {
  margin: 10px 0 20px;
  .handle-item {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    img {
      margin-left: 12px;
    }
    .input {
      flex: 1;
    }
  }
}
.flex-just {
  justify-content: center;
}
.addbtn {
  width: 100%;
}
</style>