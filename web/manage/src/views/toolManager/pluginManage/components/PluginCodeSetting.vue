<template>
  <div class="outerDialog" v-loading="loading">
    <div class="headBar">
      <div class="leftSlide">
        <iconpark-icon
          name="arrow-go-back-fill"
          @click="closeDialog"
          color="#36383D"
          size="18"
          style="cursor: pointer"
        ></iconpark-icon>
        <img v-if="appForm.icon" class="avatar" :src="appForm.icon" alt="" />
        <img
          v-else
          class="avatar"
          src="@/assets/images/default-plugin.svg"
          alt=""
        />
        <div class="titleIcon">
          <div class="txt1">
            {{ appForm.componentName || $t("noApplicationName") }}
            <iconpark-icon
              name="edit-line"
              style="cursor: pointer; margin-left: 10px"
              size="16"
              color="#828894"
              @click="editInfo"
            ></iconpark-icon>
          </div>
          <div class="txt2">
            {{ appForm.description || $t("noDescription") }}
          </div>
        </div>
      </div>
      <div class="rightSlide">
        <div
          style="
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 16px;
          "
        >
          <el-switch
            style="padding-right: 8px"
            v-model="appForm.status"
            :active-value="1"
            :inactive-value="0"
            active-color="#1747E5"
            inactive-color="#CED4E0"
          >
          </el-switch>
          <span v-if="appForm.status">{{ $t("activeStatus") }}</span>
          <span v-else>{{ $t("inactiveStatus") }}</span>
        </div>
        <el-button
          plain
          v-permission="'issue'"
          @click="submitAddApp(false)"
          style="
            width: 92px;
            height: 40px;
            border-radius: 2px;
            font-weight: 500;
            font-size: 16px;
            color: #36383d;
          "
        >
          <iconpark-icon
            name="save-line"
            size="18"
            color="#36383D"
            style="margin-right: 6px; color: #fff"
          ></iconpark-icon>
          <span>{{ $t("save") }}</span>
        </el-button>
        <el-button
          plain
          v-permission="'issue'"
          @click="openFabuDrawer"
          type="primary"
          style="
            width: 140px;
            height: 40px;
            background: #4d77ef;
            border-radius: 2px;
            color: #fff;
            margin-left: 16px;
          "
        >
          <iconpark-icon
            name="send-plane-2-fill"
            size="18"
            color="#fff"
            style="margin-right: 6px"
          ></iconpark-icon>
          <span>{{ $t("saveAndPublish") }}</span>
        </el-button>
      </div>
    </div>
    <codeSetting v-if="appForm.type === 6" @updateDataList="updateDataList" @updateAppForm="updateAppForm" :editItem="editItem" :nodeCode="nodeCode" :nodeStart="nodeStart"></codeSetting>
    <Createplugin
      v-if="dialogVisible"
      :dialogVisible="dialogVisible"
      @confirmApplication="confirmApplication"
      @cancelCreateplugin="cancelCreateplugin"
      :params="paramsData"
      modelType="0"
      type="edit"
    ></Createplugin>
    <!-- 保存并发布 -->
    <el-drawer
      append-to-body
      title="发布至商店"
      :visible.sync="fabuDrawer"
      style="width: 100%"
      size="30%"
      custom-class="fabuDrawer"
    >
      <div v-loading="drawerLoading">
        <div class="fabuDrawer-head">
          <el-form :model="fabuForm">
            <el-form-item label="插件类型">
              <el-select
                v-model="fabuForm.labels"
                multiple
                :placeholder="$t('selectPlaceholder')"
                style="width: 100%"
              >
                <el-option
                  v-for="item in typeList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.name"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="更新说明">
              <el-input
                type="textarea"
                v-model="fabuForm.publishDesc"
                maxlength="500"
                show-word-limit
                :rows="5"
                placeholder="请输入"
              />
            </el-form-item>
          </el-form>
        </div>
        <div v-if="updateRecordList.length" class="label">更新记录</div>
        <ul v-if="updateRecordList.length" class="list">
          <li
            v-for="(item, index) in updateRecordList"
            :key="index"
            class="list-item"
          >
            <div class="drop"></div>
            <div class="line"></div>

            <div class="list-item-time">
              {{ item.createTime }}
              <span v-if="index == 0" class="current">当前</span>
            </div>
            <div class="list-item-content">{{ item.publishDesc }}</div>
          </li>
        </ul>
        <div class="footer">
          <el-button plain @click="fabuDrawer = false">取消</el-button>
          <el-button type="primary" @click="onSubmit">确定</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import ApiSetting from "@/views/toolManager/pluginManage/components/ApiSetting.vue";
import codeSetting from "@/views/toolManager/pluginManage/components/codeSetting.vue";
import {
  pluginQueryDetail,
  runComponent,
  updateComponent,
  apiValidate,
} from "@/api/workflow";
import { apiGetAppPublishRecordList, apiGetLabelTypes } from "@/api/app";
import "vue-json-pretty/lib/styles.css";
import Createplugin from "../Createplugin.vue";

export default {
  components: {  codeSetting, Createplugin },
  data() {
    return {
      // radio: "1",

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
      editIndex: -1,
      formRefs: [],
      outputFormRefs: [],
      rules: {
        label: [
          {
            required: true,
            message: this.$t("enterVariableName"),
            trigger: "blur",
          },
        ],
        desc: [
          {
            required: true,
            message: this.$t("enterDescription"),
            trigger: "blur",
          },
        ],
        type: [
          {
            required: true,
            message: this.$t("selectFieldType"),
            trigger: "change",
          },
        ],
        maxLength: [
          {
            required: true,
            message: this.$t("maxLength"),
            trigger: "change",
          },
        ],
        required: [
          {
            required: true,
            message: this.$t("selectIsRequired"),
            trigger: "change",
          },
        ],
      },
      appForm: {},
      nodeApi: {},
      nodeCode: {},
      nodeStart: {},
      outputText: "",
      selectedValue: [],
      selectedLabel: "",
      outputText: "",
      outputDataByText: "",
      defaultProps: {
        children: "children",
        label: "label",
      },
      dialogVisible: false,
      paramsData: {},
      fabuDrawer: false,
      fabuForm: {
        labels: [],
      },
      typeList: [],
      updateRecordList: [],
      drawerLoading: false,
    };
  },
  props: {
    appConfigForm: Object,
  },
  computed: {
    tenantId() {
      const tenantId = sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))?.tenantId
        : "";
      return tenantId;
    },
  },
  watch: {
    // 监听 panels 数组的变化
  },

  beforeDestroy() {},
  mounted() {
    this.editItem = JSON.parse(JSON.stringify(this.appConfigForm));
    this.getPluginDetail(this.editItem.componentId);
  },
  methods: {
    confirmApplication(params) {
      this.dialogVisible = false;
      this.appForm.componentName = params?.name;
      this.appForm.componentDesc = params?.description;
      this.appForm.icon = params?.icon;
      this.appForm.labels = params?.labels;
    },
    cancelCreateplugin() {
      this.dialogVisible = false;
    },
    editInfo() {
      this.dialogVisible = true;
    },
  
    getLabelByValue(options, values) {
      let labels = [];
      let currentOptions = options;
      for (let val of values) {
        const foundOption = currentOptions.find(
          (option) => option.value === val
        );
        if (foundOption) {
          labels.push(foundOption.label);
          currentOptions = foundOption.children || [];
        }
      }

      return labels.join(" / ");
    },
    getPluginDetail(componentId) {
      this.loading = true;
      pluginQueryDetail({
        componentId: componentId,
      }).then((res) => {
        this.loading = false;
        if (res.data) {
          this.appForm = res.data;
          this.paramsData = {
            name: this.appForm.componentName,
            description: this.appForm.componentDesc,
            icon: this.appForm.icon,
            labels: this.appForm.labels,
          };
          this.nodeCode = res.data
          this.nodeStart = res.data.inputs
        } else {
          this.list = {};
        }
      });
    },


    updateAppForm(data){
      let curData = JSON.parse(data);
      this.nodeCode.settings = curData;
    },
    
 
    scrollToBottom() {
      this.$nextTick(() => {
        const outputElement = document.getElementById("output");
        if (outputElement) {
          outputElement.scrollTop = outputElement.scrollHeight;
        }
      });
    },
    isJsonString(str) {
      try {
        JSON.parse(str);
      } catch (e) {
        return false;
      }
      return true;
    },

    closeDialog() {
      this.$emit("configCloseDialog", false);
    },
    getApiParams(){
      let params = JSON.parse(JSON.stringify(this.appForm));
      let startOutput = JSON.parse(JSON.stringify(params.nodes[0].output));
      let output1 = JSON.parse(JSON.stringify(params.nodes[1].output));
      let output2 = JSON.parse(JSON.stringify(params.nodes[1].output));
      startOutput = startOutput.map((el) => {
        el.type = "string";
        el.value = el.label;
        el.nodeId = params.nodes[1].nodeId;
        el.referenceNodeId = params.nodes[0].nodeId;
        el.valueType = "reference";
        el.direction = 0;
        return el;
      });
      params.nodes[1].input = startOutput;
      output1 = output1.map((el) => {
        el.type = "string";
        el.value = el.label;
        el.nodeId = params.nodes[2].nodeId;
        el.referenceNodeId = params.nodes[1].nodeId;
        el.direction = 0;
        return el;
      });
      params.nodes[2].input = output1;
      output2 = output2.map((el) => {
        el.type = "string";
        el.value = Array.isArray(el.value)
          ? "${" + el.value.join(".") + "}"
          : el.value;
        return el;
      });
      params.nodes[1].output = output2;
      params.tenantId = this.tenantId;
      params.clickPublish = false;
      return params
    },
    updateDataList (data)
    {
      let curData = JSON.parse(data);
      let data0 = curData[0]
      this.nodeStart.output = curData.map((el) => {
       let obj = {}
       if(el.direction){
        obj = el
       } else {
         obj = {
          ...data0,
          label: el.label,
          desc: el.desc,
          paramId: "",
          type: el.type,
          value: el.value,
          required: el.required,
          enabled: el.enabled,
        }
       }
        return obj;
      });
    },
    getCodeParams(){
      let params = JSON.parse(JSON.stringify(this.appForm));
      params.tenantId = this.tenantId;
      params.clickPublish = false;
      return params
    },
    submitAddApp(isFabu) {
      let params = {}
      if (this.appForm.type === 1) {
        params = this.getApiParams()
      } else if (this.appForm.type === 6){
        params = this.getCodeParams()
      }
      if (isFabu) {
        this.drawerLoading = true;
      } else {
        this.loading = true;
      }
      const dataParams = {
        ...params,
        labels: Array.isArray(params.labels)
          ? params.labels.join(",")
          : params.labels,
      };
      if (isFabu) {
        dataParams.publishAppStore = 2;
        dataParams.publishDesc = this.fabuForm.publishDesc;
        dataParams.clickPublish = true;
        dataParams.labels = Array.isArray(this.fabuForm.labels)
          ? this.fabuForm.labels?.join(",")
          : this.fabuForm.labels;
      }
      updateComponent(dataParams)
        .then((res) => {
          if (isFabu) {
            this.drawerLoading = false;
          } else {
            this.loading = false;
          }
          if (res.code == "000000") {
            if (isFabu) {
              this.closeDialog();
              this.$message.success(res.msg);
            }
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        })
        .finally(() => {
          if (isFabu) {
            this.drawerLoading = false;
          } else {
            this.loading = false;
          }
        });
    },
    openFabuDrawer() {
      this.fabuDrawer = true;
      this.getLabelTypes();
      this.getAppPublishRecordList();
    },
    // 分类
    async getLabelTypes() {
      let res = await apiGetLabelTypes({ type: 1 });
      if (res.code == "000000") {
        this.typeList = res.data || [];
        this.typeList = this.typeList.filter((item) => item.name != "全部");
        this.fabuForm.labels = Array.isArray(this.editItem.labels)
          ? this.editItem.labels : this.editItem?.labels ? this.editItem.labels?.split(',')
          : [];
      }
    },
    // 更新记录
    async getAppPublishRecordList() {
      const params = {
        messageSource: 2,
        applicationId: this.editItem.componentId,
      };
      let res = await apiGetAppPublishRecordList(params);
      if (res.code == "000000") {
        this.updateRecordList = res.data?.records || [];
      }
    },
    onSubmit() {
      this.submitAddApp(true);
    },
  },
};
</script>
<style lang="scss">
.fabuDrawer {
  .el-drawer__header {
    height: 72px;
    margin-bottom: 0;
    padding: 32px 32px 8px;
    font-family: MiSans, MiSans;
    font-weight: 600;
    font-size: 20px;
    color: #36383d;
    line-height: 32px;
  }
  .el-drawer__body {
    padding: 0 32px;
    position: relative;
  }
  .label {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #36383d;
    line-height: 32px;
    display: flex;
    align-items: center;
    margin-top: 32px;
    margin-bottom: 20px;
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
  .list {
    height: calc(100vh - 420px);
    overflow-y: auto;
    &-item {
      margin-bottom: 8px;
      padding-left: 20px;
      padding-bottom: 24px;
      position: relative;
      .drop {
        position: absolute;
        top: 6px;
        left: 0;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: #1747e5;
        border: 1px solid #ffffff;
      }
      .line {
        position: absolute;
        top: 6px;
        left: 3px;
        width: 2px;
        height: 90%;
        background: rgba(23, 71, 229, 0.1);
      }
      .current {
        padding: 2px 8px;
        margin-left: 8px;
        background: linear-gradient(
          270deg,
          rgba(142, 101, 255, 0.2) 0%,
          rgba(23, 71, 229, 0.2) 100%
        );
        border-radius: 10px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #36383d;
      }
      &-time {
        height: 20px;
        font-weight: 600;
        font-size: 14px;
        color: #36383d;
        line-height: 20px;
        margin-bottom: 12px;
      }
      &-content {
        width: 468px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 24px;
      }
    }
  }
  .footer {
    position: absolute;
    bottom: 0;
    right: 0;
    height: 88px;
    padding: 16px 32px 32px 0;
  }
}
</style>
<style lang="scss" scoped>
.flex {
  display: flex;
}
.outerDialog {
  display: flex;
  height: calc(100vh - 80px);
  flex-direction: column;
  overflow: hidden;
  .dialogPower {
    width: 100%;
    background: #ffffff;
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
    background: #FFFFFF !important;
    padding: 16px 32px 15px !important;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    margin-bottom: 0px !important;
    width: 100%;
    position: absolute;
    top: 0;
    right: 0;
    border-bottom: 1px solid rgba(0, 0, 0, 0.12);
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
      .avatar {
        width: 40px;
        height: 40px;
        margin: 0 12px 0 14px;
      }
      .titleIcon {
        .txt1 {
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 18px;
          color: #36383d;
          line-height: 24px;
        }
        .txt2 {
          margin-top: 4px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #828894;
          line-height: 20px;
        }
      }
    }
    .rightSlide {
      display: flex;
      justify-content: space-between;
      align-items: center;
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
        background: #1747e5;
        border-radius: 4px;
        img {
          margin-right: 5px;
        }
        img,
        span {
          vertical-align: middle;
        }
      }
      ::v-deep .el-button {
        span {
          display: flex;
          align-items: center;
          height: 100%;
        }
      }
      ::v-deep .el-button--primary.is-plain {
        border-color: rgb(77, 119, 239);
      }
      ::v-deep .el-button--default.is-plain {
        &:hover {
          border-color: #c9ccd1;
        }
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
  height: calc(100vh - 95px);
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
    padding: 24px 0 0 32px;
    ::v-deep .el-tabs {
      .el-tabs__item {
        font-size: 16px;
        color: #383d47;
        padding-right: 6px;
      }
      .el-tabs__item.is-active {
        color: #603eca;
      }
      .el-tabs__active-bar {
        background-color: #603eca;
      }
      .el-tabs__nav-wrap::after {
        height: 1px;
        background: rgba(0, 0, 0, 0.12);
      }
      .el-tabs__content {
        height: calc(100vh - 175px);
        overflow: auto;
      }
      .plugin-bd[data-v-34b0738c] .el-tabs .el-tabs__item.is-active {
        color: #603eca;
      }
      .el-tabs__nav-wrap::after {
        background: none;
      }
    }
    &-tit {
      font-size: 16px;
      color: #383d47;
      line-height: 28px;
      position: relative;
      padding: 0 0 0 10px;
      margin: 20px 0 30px;
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
    &-add {
      border: 1px solid #d5d8de;
      // width: 100%;
      width: 104px;
      height: 40px;
      line-height: 40px;
      text-align: left;
      cursor: pointer;
      padding: 0 6px;
    }
    &-first {
      position: relative;
      .drawer-content {
        padding: 0 20px 0 0;
        overflow-y: scroll;
        max-height: 500px;
      }
    }
    ::v-deep .el-form {
      border-radius: 4px;
      // border: 1px solid #e1e4eb;
      padding: 26px 16px 16px;
      margin: 0 0 16px 0;
      position: relative;
      i.hide {
        position: absolute;
        right: 42px;
        top: 4px;
        cursor: pointer;
        font-size: 20px;
      }
      i.delete {
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
          // font-weight: bold;
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
    &-second {
      .apiUrl {
        background: #f2f4f7;
        width: 100%;
        line-height: 36px;
        border-radius: 2px;
        padding: 12px 16px;
        color: #494e57;
        .title {
          display: flex;
          justify-content: space-between;
        }
        .editHandler {
          cursor: pointer;
        }
        .font {
          color: #3fb816;
          span {
            font-size: 30px;
            vertical-align: middle;
          }
        }
      }
      .operationIcon {
        padding-right: 5px;
        font-size: 20px;
      }
    }
    &-third {
      &-hd {
        margin: 20px 0 30px;
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
        width: 98%;
      }
    }
  }
  &-ft {
    padding: 24px 0 0 0;
    border-left: 1px solid #e1e4eb;
    position: relative;
    ::v-deep .run-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 40px;
      margin-top: 24px;
      border-radius: 2px;
      span {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100%;
      }
    }
    &-tit {
      position: relative;
      padding: 0 32px 0 24px;
      font-size: 20px;
      color: #494e57;
      height: 40px;
      line-height: 40px;
      img {
        position: absolute;
        width: 24px;
        left: 20px;
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
      height: calc(100vh - 166px);
      padding: 0 32px 0 24px;
      .radio-list {
        margin-top: 18px;
        text-align: center;
        background: #f0f1f5;
        padding: 2px;
        height: 38px;
        line-height: 38px;
        span {
          cursor: pointer;
          font-size: 16px;
          color: #828894;
          background: transparent;
          border-radius: 4px;
          display: inline-block;
          width: 50%;
          text-align: center;
          height: 34px;
          position: relative;
          &.active {
            background: #ffffff;
            z-index: 1;
            color: #36383d;
            border-radius: 2px;
            height: 34px;
          }
        }
        &-left {
          // left: 10px;
        }
        &-right {
          // right: 10px;
        }
      }
      .output {
        background: #f7f8fa;
        border-radius: 4px;
        height: calc(100vh - 360px);
        margin-top: 24px;
        padding: 10px 0;
        overflow: auto;
        line-height: 24px;
        .output-text {
          padding: 6px 16px;
          display: block;
        }
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
      margin: 0 0 16px 0;
      position: relative;
      height: calc(100vh - 310px);
      overflow-y: scroll;
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
        padding: 16px 0;
        position: relative;
        &-name {
          font-size: 16px;
          color: #383d47;
          line-height: 20px;
          font-weight: bold;
          display: flex;
          justify-content: space-between;
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
  }
  .el-radio .el-radio__input.is-checked + .el-radio__label {
    color: #494e57;
  }
}
</style>
