<template>
  <div class="outerDialog" v-loading="loading">
    <div>
      <div class="headBar">
        <div class="leftSlide">
          <img src="@/assets/images/arrow-go-back-fill.svg" @click="closeDialog" />
          <div class="titleIcon">
            <p>
              {{ $t("pluginDetails") }}
            </p>
          </div>
        </div>
        <div class="rightSlide">
          <!-- <div>
            <el-switch
              v-model="sensitiveForm.status"
              active-value="1"
              inactive-value="2"
              @change="changeOneStatus()"
            ></el-switch>
            <span style="color: #383d47; font-size: 16px; margin-left: 5px">{{
              sensitiveForm.status == "1" ? $t("settingsEnabled") : $t("settingsDisabled")
            }}</span>
          </div> -->
          <div>
                    <el-switch style="padding-right:10px;"
                        v-model="statusValue"
                        active-color="#1747E5"
                        inactive-color="#CED4E0">
                        </el-switch>
                        <span v-if="statusValue">{{$t("activeStatus")}}</span>
                        <span v-else>{{$t("inactiveStatus")}}</span>
                </div>
          <el-button v-if="!hideEdit" class="btns btnsXian" v-permission="'issue'" @click="submitApp">
            <div class="flex-center just-center">
              <img src="@/assets/images/edit-line.svg" />
              <span>{{ $t("edit") }}</span>
            </div>
          </el-button>
        </div>
      </div>
      <div style="padding: 0 32px;">
        <div class="dialogPower plugin">
            <div class="list-item-top">
                <div style="display: flex;justify-content: space-between;">
                    <img
                        v-if="appConfigForm.icon"
                        class="avatar"
                        :src="appConfigForm.icon"
                        alt=""
                    />
                    <img v-else class="avatar" src="@/assets/images/default-voice.png" alt="" />
                    <div class="text" style="line-height: 40px;font-size:24px;">
                        <!-- <div class="row"> -->
                        <div class="row-name"></div>
                        {{ appConfigForm.componentName }}
                        <!-- </div> -->
                    </div>
                </div>
                <!-- <div><el-button type="text" style="color: #494E57;"> <i class="el-icon-share">{{$t("share")}}</i></el-button></div> -->
            </div>
        </div>
        <div class="tipsTpye flex-center">
            <div class="tips-name" v-if="appConfigForm.category == 1">模型算法</div>
            <div class="tips-name" v-else> APi接入</div>
            <div class="tips">{{ $t("creationTime") }}：{{ appConfigForm.createTime }}</div>
        </div>
        <div class="list-item-center">
            <div class="componentDesc" :title="appConfigForm.description">
            {{ appConfigForm.description }}
            </div>
        </div>

        <div class="elTabs" style="margin-top: 20px">
            <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane :label="$t('applicationInvocation')" name="first"> 
                <div class="plugin-bd-tit">
                {{ $t("inputParameters") }}
                </div>
                <div style="margin-top: 16px; position: relative">
                <el-table
                    ref="multipleTable"
                    :data="tableData"
                    tooltip-effect="dark"
                    style="width: 100%"
                    class="table"
                    max-height="630"
                >
                    <el-table-column
                    prop="name_en"
                    :label="$t('paramName')"
                    ></el-table-column>
                    <el-table-column
                    prop="feild_type"
                    :label="$t('parameterType')"
                    ></el-table-column>
                    <el-table-column prop="isRequired" :label="$t('isRequired')">
                    <template slot-scope="scope">
                        <span v-if="scope.row.isRequired"> 是 </span>
                        <span v-else> 否 </span>
                    </template>
                    </el-table-column>
                    <el-table-column prop="feild_desc" :label="$t('describe')">
                    </el-table-column>
                </el-table>
                </div>
                <div class="plugin-bd-tit">
                {{ $t("outputParameters") }}
                </div>
                <div style="margin-top: 16px">
                <el-table
                    ref="multipleTable"
                    :data="tableDataResult"
                    tooltip-effect="dark"
                    style="width: 100%"
                    class="table"
                    max-height="630"
                >
                    <el-table-column
                    prop="name_en"
                    :label="$t('paramName')"
                    ></el-table-column>
                    <el-table-column
                    prop="feild_type"
                    :label="$t('parameterType')"
                    ></el-table-column>
                    <el-table-column prop="feild_desc" :label="$t('describe')">
                    </el-table-column>
                </el-table>
                </div>
            </el-tab-pane>
            <!-- <el-tab-pane :label="$t('integrationDocument')" name="second"></el-tab-pane>
            <el-tab-pane :label="$t('secretKeyManagement')" name="third">
                <div style="margin-top: 16px">
                <div class="head" style="margin-bottom: 16px">
                    <div>
                    <el-input
                        :placeholder="$t('inputKeyword')"
                        v-model="paramSearch.name"
                        style="width: 334px"
                        clearable
                        prefix-icon="el-icon-search"
                        @blur="searchHandler"
                    >
                    </el-input>
                    <el-button
                        plain
                        style="
                        border: 1px solid #C9CCD1;
                        color: #494E57;
                        margin-left: 16px;
                        border-radius: 4px;
                        position: absolute;
                        right: 0px;
                        "
                        @click="addKey"
                    >
                        <img style="width: 14px;height: 14px;vertical-align: bottom;margin-right: 5px;" src="@/assets/images/add-line.svg" alt="">
                        {{ $t("createNewSecretKey") }}
                    </el-button>
                    </div>
                </div>
                <div style="position: relative">
                    <div style="position: absolute; top: 14px; z-index: 9999; left: 60px">
                    <img
                        v-if="showSecret"
                        style="width: 18px; height: 18px; margin-left: 4px"
                        :src="require('@/assets/images/eye-line.svg')"
                        @click="showSecret = !showSecret"
                    />
                    <img
                        v-else
                        style="width: 18px; height: 18px; margin-left: 4px"
                        :src="require('@/assets/images/eye-off-line.svg')"
                        @click="showSecret = !showSecret"
                    />
                    </div>
                </div>
                <el-table
                    ref="multipleTable"
                    :data="secretAuthUserList"
                    tooltip-effect="dark"
                    style="width: 100%"
                    class="table"
                    max-height="550"
                >
                    <el-table-column
                    prop="secretKey"
                    :label="$t('secretKeyValue')"
                    width="220"
                    >
                    <template slot-scope="scope">
                        <span :class="[showSecret ? '' : 'isNoShowSecret']">{{
                        scope.row.secretKey
                        }}</span>
                    </template>
                    </el-table-column>
                    <el-table-column prop="name" :label="$t('secretKeyLocation')">
                    Header
                    </el-table-column>
                    <el-table-column prop="name" :label="$t('userName')"> </el-table-column>
                    <el-table-column
                    prop="createUser"
                    :label="$t('founder')"
                    ></el-table-column>
                    <el-table-column
                    prop="createTime"
                    :label="$t('creationTime')"
                    ></el-table-column>
                    <el-table-column prop="expireTime" :label="$t('secretKeyExpirationTime')">
                    </el-table-column>
                    <el-table-column prop="remark" :label="$t('describe')"> </el-table-column>
                    <el-table-column prop="enableFlag" :label="$t('status')">
                    <template slot-scope="scope">
                        <el-switch
                        v-model="scope.row.enableFlag"
                        active-value="1"
                        inactive-value="0"
                        @change="changeStatus(scope.row)"
                        ></el-switch>
                        <span style="color: #383d47; font-size: 16px; margin-left: 5px">{{
                        scope.row.enableFlag == "1"
                            ? $t("settingsEnabled")
                            : $t("settingsDisabled")
                        }}</span>
                    </template>
                    </el-table-column>
                    <el-table-column :label="$t('action')" width="120">
                    <template slot-scope="scope">
                        <el-button
                        type="text"
                        size="small"
                        v-if="scope.row.enableFlag == '0'"
                        @click="editMenu(scope.row)"
                        >{{ $t("edit") }}</el-button
                        >
                        <el-button
                        type="text"
                        size="small"
                        v-if="scope.row.enableFlag == '0'"
                        @click="deleteData(scope.row)"
                        >{{ $t("delete") }}</el-button
                        >
                    </template>
                    </el-table-column>
                </el-table>
                </div
            ></el-tab-pane>
            <el-tab-pane :label="$t('statisticalAnalysis')" name="fourth"></el-tab-pane> -->
            </el-tabs>
        </div>
        </div>
    </div>
    <!-- <el-drawer
      append-to-body
      :title="$t('publishApi')"
      :visible.sync="apiDrawer"
      style="width: 100%"
      size="35%"
      class="elDrawerList"
    >
      <modelAlporithmSetting
        v-if="apiDrawer"
        :nodeData="nodeApi"
        @getApiValidate="getApiValidate"
        @updateApi="updateApi"
        @addSecret="addSecret"
      ></modelAlporithmSetting>
    </el-drawer> -->
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
        :appForm="appFormList"
        @closeDialogSecret="closeDialogSecret"
        :addOrUpdate="typeName"
        @isAddSecretClick="isAddSecretClick"
      ></addEditingKey>
    </el-dialog>
  </div>
</template>

<script>
import modelAlporithmSetting from "@/views/systemManage/pluginManage/components/modelAlporithmSetting.vue";
import addEditingKey from "@/views/systemManage/pluginManage/components/addEditingKey.vue";
import {
  getModelPluginApiAuthUserList,
  deleteModelPluginApiAuthUser,
  updateModelPluginApiAuthUser,
} from "@/api/workflow";
export default {
  components: { modelAlporithmSetting, addEditingKey },
  data() {
    return {
    statusValue: false,
      sensitiveForm: {
        status: "1",
      },
      activeName: "first",
      loading: false,
      tableData: [],
      tableDataResult: [],
      paramSearch: {
        name: "",
      },
      secretAuthUserList: [],
      showSecret: false,
      appFormList: {},
      editDialogVisible: false,
      keyName: this.$t("createNewSecretKey"),
      typeName: "add",
    };
  },
  props: {
    appConfigForm: {
        type: Object,
        default: ()=>{}
    },
    hideEdit: {
      type: Boolean,
      default: true
    }
  },
  watch: {
    // 监听 panels 数组的变化
  },

  beforeDestroy() {},
  mounted() {
    // this.getModelList(); //模型列表
    // this.editItem = JSON.parse(JSON.stringify(this.appConfigForm));
    this.editItem = this.appConfigForm;
    console.log("this.appConfigForm",)
    // this.tableData = JSON.parse(this.editItem.descParam);
    // this.tableDataResult = JSON.parse(this.editItem.descResult);
    this.getModelPluginApiAuthUserList();
    // this.getPluginDetail(this.editItem.componentId);
  },
  methods: {
    changeOneStatus() {},
    handleClick() {},
    submitApp() {
      this.$emit("configOpenModelDialog", true);
    },
    closeDialog() {
      this.$emit("configCloseDialog", false);
    },
    editMenu(item) {
      this.appFormList = item;
      console.log(this.appFormList, 7878);
      this.keyName = this.$t("editSecretKey");
      this.typeName = "update";
      this.editDialogVisible = true;
    },
    closeDialogSecret() {
      this.editDialogVisible = false;
    },
    isAddSecretClick() {
      this.editDialogVisible = false;
      this.getModelPluginApiAuthUserList();
    },
    deleteData(item) {
      this.$confirm("请确认是否删除", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(() => {
        deleteModelPluginApiAuthUser([item.id])
          .then((res) => {
            if (res.code == "000000") {
              this.getModelPluginApiAuthUserList();
            }
          })
          .catch((err) => {});
      });
    },
    addKey() {
      this.appFormList.modelPluginApiId = this.editItem.modelPluginApiId;
      this.keyName = this.$t("createNewSecretKey");
      this.typeName = "add";
      this.editDialogVisible = true;
    },
    // 获取秘钥值
    getModelPluginApiAuthUserList() {
      getModelPluginApiAuthUserList({
        name: this.paramSearch.name,
        modelPluginApiId: this.editItem.modelPluginApiId,
      })
        .then((res) => {
          if (res.code == "000000") {
            this.secretAuthUserList = res.data.records;
          }
        })
        .catch((err) => {});
    },
    searchHandler() {
      this.getModelPluginApiAuthUserList();
    },
    changeStatus(row) {
      updateModelPluginApiAuthUser({
        modelPluginApiId: row.modelPluginApiId,
        id: row.id,
        enableFlag: row.enableFlag,
      })
        .then((res) => {})
        .catch((err) => {});
    },
  },
};
</script>

<style lang="scss" scoped>
.flex {
  display: flex;
}
.outerDialog {
//   margin-top: 16px;
  .dialogPower {
    width: 100%;
    position: relative;

    ::-webkit-scrollbar {
      width: 0;
    }

    ::v-deep .el-textarea__inner {
      font-family: MiSans, MiSans;
    }
  }
  .headBar {
    background: #fff;
    padding: 16px 32px;
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
    width: 100%;
    // position: absolute;
    // top: 0;
    // right: 0px;
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
      .titleIcon {
        font-size: 18px;
        p:first-child {
          font-family: MiSans, MiSans;
          font-weight: 500;
        //   font-size: 20px;
          color: #383d47;
          line-height: 28px;
          text-align: left;
          font-style: normal;
        }
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
        color: #1747E5;
        border-color: #1747E5;
        margin-left: 24px;
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
    .el-drawer__body {
      padding: 0px 24px;
    }
  }
  ::v-deep .el-drawer__header {
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
  }
}

.flexDialog {
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__body {
      padding: 20px 32px;
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

::v-deep .el-switch.is-checked .el-switch__core {
  background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
}

.list {
  flex: 1;
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  overflow: auto;
  align-content: flex-start;

  &-item {
    margin-right: 1.3%;
    margin-bottom: 16px;
    width: 24%;
    background: #ffffff;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    display: flex;
    flex-direction: column;
    position: relative;
    &-top {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px 0;
        height: 60px;
      .avatar {
        margin-right: 12px;
        width: 40px;
        height: 40px;
        border: 1px solid #e1e4eb;
        border-radius: 50%;
      }
      .row-name {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 24px;
        color: #383d47;
        line-height: 32px;
      }
    }
  }
}

.tipsTpye {
  margin: 0 16px 14px 0;
  .tips-name {
    padding: 4px 6px;
    background: #ebeef2;
    border-radius: 2px;
    border: 1px solid #B89AF9;
    font-weight: 400;
    font-size: 12px;
    color: #A17FF3;
    margin-right: 16px;
    
  }
  .tips {
    color: #b4bccc;
  }
}
.list-item-center {
  margin: 0 16px;
  .componentDesc {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #383d47;
  }
}

.elTabs {
  ::v-deep .el-tabs {
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
}
.plugin-bd {
  &-tit {
    margin: 24px 0;
    font-size: 16px;
    color: #383d47;
    line-height: 28px;
    position: relative;
    padding: 0 0 0 10px;
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
}

::v-deep .table {
  .is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
  }
  .el-switch .el-switch__core {
    border-radius: 12px;
    border: none;
    &:after {
      top: 2px;
    }
  }
  .has-gutter {
    th {
      font-size: 14px;
      color: #828894;
      background: #f2f5fa;
    }
  }

  tr {
    color: #383d47;
    font-size: 16px;
    font-weight: 400;
    .el-checkbox__input.is-checked .el-checkbox__inner,
    .el-checkbox__input.is-indeterminate .el-checkbox__inner {
      background: #1c50fd;
      border-color: #1c50fd;
    }
  }

  .el-button--text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #1c50fd;
  }
}

.isNoShowSecret {
  text-security: disc;
  -webkit-text-security: disc;
}
</style>
