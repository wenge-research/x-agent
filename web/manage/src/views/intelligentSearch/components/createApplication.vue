<template>
  <el-dialog
    :title="$t('createIntelligentSearch')"
    :visible.sync="dialogVisibleApplication"
    width="30%"
    :before-close="cancelTemplate"
    class="applicationDialog"
    append-to-body
  >
    <div style="position: relative">
      <div class="formOuter">
        <div class="uploadImg">
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
              <div style="position: relative; height: 100%">
                <img
                  :src="appForm.logo"
                  style="
                    width: 80px;
                    height: 80px;
                    border-radius: 4px;
                    background: #dcdfe6;
                  "
                />
                <div
                  style="
                    width: 28px;
                    height: 28px;
                    border-radius: 8px 0px 8px 0px;
                    background: #b0b2b8;
                    position: absolute;
                    bottom: 0;
                    right: 0;
                  "
                ></div>
                <img
                  style="
                    height: 14px;
                    position: absolute;
                    bottom: 7px;
                    right: 7px;
                  "
                  src="@/assets/images/edit-line1.svg"
                />
              </div>
            </el-upload>
          </div>
          <div>
            <el-form
              :model="appForm"
              :rules="rules"
              ref="ruleForm"
              class="demo-ruleForm"
            >
              <el-form-item
                :label="$t('nameOne')"
                prop="applicationName"
              >
                <el-input
                  v-model="appForm.applicationName"
                  show-word-limit
                  maxlength="100"
                  style="width: 100%"
                  :placeholder="$t('pleaseEnter')"
                />
              </el-form-item>
              <el-form-item
                :label="$t('describe')"
                prop="introduce"
              >
                <el-input
                  class="inputTextarea"
                  type="textarea"
                  :rows="7"
                  v-model="appForm.introduce"
                  show-word-limit
                  maxlength="1000"
                  style="width: 100%"
                  :placeholder="$t('pleaseEnter')"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="confirmTemplate">{{
        $t("confirm")
      }}</el-button>
      <el-button @click="cancelTemplate">{{ $t("cancel") }}</el-button>
    </span>
  </el-dialog>
</template>

<script>
// 引入 uuid 模块
const { v4: uuidv4 } = require('uuid');
export default {
  data() {
    return {
      appForm: {
        logo: require("@/assets/images/applicationlogo.svg"),
        applicationName: "",
        introduce: "",
      },
      uploadBtnLogo: false,
      fileListLogo: [],
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      rules: {
        applicationName: [
          { required: true, message: `请输入${this.$t("nameOne")}`, trigger: "blur" },
        ],
        introduce: [
          { required: true, message: `请输入${this.$t("describe")}`, trigger: "blur" },
        ],
      },
    };
  },
  props: {
    dialogVisibleApplication: {
      type: Boolean,
      default: false,
    },
    params: Object,
    type: String,
  },
  mounted() {
    if (this.type == "edit") {
      this.appForm.logo = this.params.logo;
      this.appForm.applicationName = this.params.applicationName;
      this.appForm.introduce = this.params.introduce;
    }
  },
  methods: {
    cancelTemplate() {
      if (this.type == "add") {
        this.$emit("cancelApplication", false);
      } else {
        this.$emit("cancelEditApplication", false);
      }
    },
    confirmTemplate() {
      this.$refs.ruleForm.validate(async(valid) => {
        if (valid) {
          if (this.type == "add") {
            this.appForm.applicationCode = await uuidv4()?.replace(/-/g, '')  
            this.$emit("confirmApplication", this.appForm);
          } else {
            this.$emit(
              "confirmEditApplication",
              "editApplicationName",
              this.appForm
            );
          }
        } else {
          return false;
        }
      });
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
.logoAppUpload {
  text-align: center;
  margin-bottom: 20px;
  ::v-deep .el-upload--picture-card {
    border: 0;
    width: 80px;
    height: 80px;
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
</style>
