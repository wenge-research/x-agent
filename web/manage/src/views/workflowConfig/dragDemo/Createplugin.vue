<template>
  <el-dialog
    :title="type == 'edit' ? $t('editplugin') : $t('createPlugin')"
    :visible.sync="dialogVisible"
    width="30%"
    top="5vh"
    :before-close="cancelTemplate"
    class="applicationDialog"
    append-to-body
  >
    <div style="position: relative">
      <div class="formOuter">
        <div class="uploadImg">
          <div>
            <el-form
              :model="appForm"
              :rules="rules"
              ref="ruleForm"
              class="demo-ruleForm"
            >
              <el-form-item
                :label="$t('pluginType')"
                prop="type"
                class="pluginTypeBox"
              >
                <div class="pluginType">
                  <div
                    v-for="(item, index) in pluginTypeList"
                    :key="index"
                    :class="['list', activeIndex == index ? 'activelist' : '']"
                    @click="selectClick(item, index)"
                  >
                    <img :src="item.img" />
                    <div class="label">
                      {{ item.label }}
                    </div>
                  </div>
                </div>
                <!-- <el-select v-model="appForm.type" style="width: 100%">
                               <el-option
                                     v-for="item in pluginTypeList"
                                     :key="item.value"
                                     :label="item.label"
                                     :value="item.value"
                                 ></el-option>
                             </el-select> -->
              </el-form-item>
              <el-form-item :label="$t('pluginName')" prop="name">
                <el-input
                  v-model="appForm.name"
                  show-word-limit
                  maxlength="100"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item :label="$t('pluginDescription')" prop="description">
                <el-input
                  class="inputTextarea"
                  type="textarea"
                  :rows="7"
                  v-model="appForm.description"
                  show-word-limit
                  maxlength="1000"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item :label="$t('type')" prop="labels" class="plugin-type">
                <el-select v-model="appForm.labels" multiple :placeholder="$t('selectPlaceholder')" style="width: 100%">
                  <el-option
                    v-for="item in typeList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.name"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="图标" prop="description">
                <div class="uploadOuter">
                  <el-upload
                    :action="actionUrl"
                    :data="{ filePath: 'agent_source' }"
                    :class="{ hideBox: uploadBtnLogo }"
                    :file-list="fileListLogo"
                    :show-file-list="false"
                    :limit="1"
                    class="logoAppUpload"
                    list-type="picture-card"
                    :on-remove="handleLogoRemove"
                    :on-success="handleLogoSuccess"
                  >
                    <div
                      style="position: relative; height: 100%"
                      @mouseenter="showMask = true"
                      @mouseleave="showMask = false"
                    >
                      <img
                        :src="appForm.icon"
                        style="
                          width: 80px;
                          height: 80px;
                          border-radius: 2px;
                          positon: relative;
                        "
                      />
                      <div
                        v-if="showMask"
                        style="
                          position: absolute;
                          top: 0;
                          left: 0;
                          width: 80px;
                          height: 80px;
                          background: rgba(0, 0, 0, 0.4);
                          backdrop-filter: blur(1px);
                          border-radius: 2px;
                          display: flex;
                          align-items: center;
                          justify-content: center;
                        "
                      >
                        <iconpark-icon
                        name="edit-line"
                        color="#fff"
                        style="margin-right: 15px"
                      ></iconpark-icon>
                        <iconpark-icon
                          name="delete-bin-4-line"
                          color="#fff"
                          @click.stop="deleteImg"
                        ></iconpark-icon>
                      </div>
                      <!-- <img
                      style="
                        height: 14px;
                        position: absolute;
                        bottom: 7px;
                        right: 7px;
                      "
                      src="@/assets/images/edit-line1.svg"
                    /> -->
                    </div>
                  </el-upload>
                  <el-button
                    class="ai-btn"
                    type="primary"
                    :loading="imgLoading"
                    @click="getImageUrl"
                  >
                    <img src="@/assets/images/ai-btn.svg" alt="" />
                    AI生成
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
    <div class="dialog-footer">
      <el-button plain @click="cancelTemplate">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="confirmTemplate">{{
        $t("confirm")
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getAiImage, apiGetLabelTypes } from "@/api/app";

export default {
  data() {
    return {
      appForm: {
        icon: require("@/assets/images/default-plugin.svg"),
        type: 1,
        status: 0,
        name: "",
        description: "",
        labels: []
      },
      uploadBtnLogo: false,
      fileListLogo: [],
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      // actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
      // pluginTypeList: [
      //     { label: "API接入", value: "1" },
      //     { label: "关键词过滤", value: "2" },
      //     { label: "场景调用", value: "3" },
      //     { label: "自定义", value: "4" },
      // ],
      pluginTypeList: [
        {
          label: this.$t("createBasedOnAPI"),
          value: 2,
          img: require("@/assets/images/mxsf.svg"),
        },
        {
          label: this.$t("createThroughCode"),
          value: 6,
          img: require("@/assets/images/apijr.svg"),
        },
        //   {
        //     label: this.$t("keywordFilterLabel"),
        //     value: 3,
        //     img: require("@/assets/images/gjcgl.svg"),
        //   },
        // {
        //   label: this.$t("customLabel"),
        //   value: 4,
        //   img: require("@/assets/images/zdy.svg"),
        // },
      ],
      rules: {
        name: [
          {
            required: true,
            message: "请输入插件名称",
            trigger: "blur",
          },
        ],
        labels: [
          {
            required: true,
            message: "请选择类型",
            trigger: "change",
          }
        ],
        // description: [
        //     {
        //         required: true,
        //         message: "请输入插件描述",
        //         trigger: "blur",
        //     },
        // ],
        type: [
          { required: true, message: "请选择插件类型", trigger: "change" },
        ],
      },
      activeIndex: "",
      showMask: false,
      imgLoading: false,
      typeList: []
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    params: Object,
    type: String,
    modelType: String,
  },
  mounted() {
    this.getLabelTypes();
    if (this.type == "edit") {
      this.appForm.icon = this.params?.icon;
      this.appForm.name = this.params?.name;
      this.appForm.description = this.params?.description;
      this.appForm.labels = this.params?.labels ? this.params.labels.split(',') : [];
      this.appForm.type = this.modelType;
      this.activeIndex = this.modelType;
    }
  },
  methods: {
    // 分类
    async getLabelTypes() {
      let res = await apiGetLabelTypes({ type: 1 });
      if (res.code == "000000") {
        this.typeList = res.data || [];
        this.typeList = this.typeList.filter(item => item.name != '全部')
      }
    },
    getImageUrl() {
      this.$refs.ruleForm.validate(async (valid) => {
        if (valid) {
          this.imgLoading = true;
          getAiImage({
            topic: this.appForm.name,
            description: this.appForm.description,
          })
            .then((res) => {
              if (res.code == "000000") {
                this.appForm.icon =
                  res.data || require("@/assets/images/default-plugin.svg");
              } else {
                this.$message.warning("生成失败");
              }

              this.imgLoading = false;
            })
            .catch(() => {
              this.imgLoading = false;
            });
        } else {
          return false;
        }
      });
    },
    deleteImg() {
      this.handleLogoRemove();
    },
    handleLogoRemove(file, fileList) {
      this.uploadBtnLogo = false;
      this.appForm.icon = require("@/assets/images/default-plugin.svg");
      this.fileListLogo = [];
    },
    handleLogoSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnLogo = true;
        this.appForm.icon =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListLogo = [];
      } else {
        this.uploadBtnLogo = false;
        this.appForm.icon = "";
        this.fileListLogo = [];
      }
    },
    cancelTemplate() {
      this.$emit("cancelCreateplugin", false);
    },
    confirmTemplate() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.$emit("confirmApplication", this.appForm);
        } else {
          return false;
        }
      });
    },
    handleLogoRemove(file, fileList) {
      this.uploadBtnLogo = false;
      this.appForm.icon = require("@/assets/images/default-plugin.svg");
      this.fileListLogo = [];
    },
    handleLogoSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnLogo = true;
        this.appForm.icon =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListLogo = [];
      } else {
        this.uploadBtnLogo = false;
        this.appForm.icon = "";
        this.fileListLogo = [];
      }
    },
    selectClick(item, index) {
      this.appForm.type = item.value;
      this.activeIndex = index;
    },
  },
};
</script>

<style lang="scss" scoped>
.applicationDialog {
  ::v-deep .el-dialog {
    border-radius: 4px;
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
      text-align: left;
    }
  }
}
.inputTextarea {
  ::v-deep .el-textarea__inner {
    font-family: MiSans, MiSans;
  }
}
.plugin-type {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.logoAppUpload {
  text-align: center;
  ::v-deep .el-upload--picture-card {
    border: 0;
    width: 80px;
    height: 80px;
  }
}

.headImgSpan {
  position: relative;
  top: -36px;
  left: 92px;
  color: #1747e5;
  width: 80px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 14px;
  .headImg {
    position: absolute;
    left: 0;
    top: 0;
    width: 80px;
    height: 32px;
    background: linear-gradient(270deg, #8e65ff 0%, #1747e5 100%);
    border-radius: 2px;
    opacity: 0.15;
  }
  img {
    vertical-align: middle;
    width: 11px;
    height: 12px;
    margin-right: 6px;
  }
}

.demo-ruleForm {
  ::v-deep .el-form-item__label {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #383d47;
    line-height: 32px;
  }
  ::v-deep .el-form-item {
    margin-bottom: 16px;
    .el-form-item__content {
      width: 100%;
    }
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

.pluginTypeBox {
  display: flex;
  flex-direction: column;
  ::v-deep .el-form-item__label {
    text-align: left !important;
  }
  .pluginType {
    display: flex;
    justify-content: space-between;
    .list {
      &:nth-child(1) {
        margin-right: 15px;
      }
      //   width: 32%;
      flex: 1;
      //   height: 80px;
      background: #ffffff;
      border-radius: 4px;
      border: 1px solid #d3d9e6;
      text-align: center;
      padding: 12px 0;
      img {
        width: 36px;
        height: 36px;
      }
      .label {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #383d47;
        line-height: 18px;
      }
    }
    .list:hover,
    .activelist {
      background: rgba(28, 80, 253, 0.05);
      border: 1px solid #1c50fd;
    }
  }
}
.dialog-footer {
  text-align: right;
  .el-button {
    border-radius: 2px;
  }
  .el-button--primary {
    background: #1747e5;
    border-color: #1747e5;
  }
  .el-button--default {
    border-color: #c4c6cc;
    color: #383d47;
    font-size: 16px;
  }
}
.uploadOuter {
  width: 100%;
  margin-bottom: 20px;
  display: flex;
  align-items: end;
  :deep(.ai-btn) {
    height: 32px;
    background: linear-gradient(
      270deg,
      rgba(142, 101, 255, 0.15) 0%,
      rgba(23, 71, 229, 0.15) 100%
    );
    border-radius: 2px;
    display: inline-flex;
    align-items: center;
    border: 0px;
    padding: 0px 8px;

    margin-left: 16px;
    font-size: 14px;
    color: #1747e5;
    span {
      display: inline-flex;
      align-items: center;
    }
    img {
      margin-right: 2px;
      width: 16px;
      height: 16px;
      border: none;
    }
  }
}
</style>
