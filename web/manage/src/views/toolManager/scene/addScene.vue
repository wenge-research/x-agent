<template>
  <el-dialog
    :title="dialogType == 'add' ? $t('addScene') : $t('editScene')"
    :visible.sync="dialogVisibleApplication"
    width="30%"
    :before-close="close"
    class="addScene-dialog"
    append-to-body
    :close-on-click-modal="false"
        :close-on-press-escape="false"
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
                <div v-if="!appForm.icon" class="upload-img">
                  <iconpark-icon
                    name="edit-line"
                    color="#fff"
                    size="18"
                  ></iconpark-icon>
                </div>
                <img
                  v-if="appForm.icon"
                  :src="appForm.icon"
                  style="width: 80px; height: 80px; border-radius: 8px"
                />
                <img
                  v-else
                  src="@/assets/images/scene-icon.png"
                  style="width: 80px; height: 80px"
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
              <el-form-item :label="$t('sceneName')" prop="sceneName">
                <el-input
                  v-model="appForm.sceneName"
                  show-word-limit
                  maxlength="100"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item :label="$t('sceneDescription')" prop="matterDesc">
                <el-input
                  class="inputTextarea"
                  type="textarea"
                  :rows="5"
                  v-model="appForm.matterDesc"
                  show-word-limit
                  maxlength="1000"
                  style="width: 100%"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
    <div class="dialog-footer">
      <el-button @click="close">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="onSubmit">{{
        $t("confirm")
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { apiAddSceneManagement } from "@/api/scene";
export default {
  data() {
    return {
      appForm: {
        icon: "",
        sceneName: "",
        matterDesc: "",
      },
      uploadBtnLogo: false,
      fileListLogo: [],
      // actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`, // 测试
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      rules: {
        sceneName: [
          {
            required: true,
            message: this.$t("pleaseEnterTheSceneName"),
            trigger: "blur",
          },
        ],
        matterDesc: [
          {
            required: true,
            message: this.$t("pleaseEnterASceneDescription"),
            trigger: "blur",
          },
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
    dialogType: String,
  },
  computed: {
    tenantId() {
        const tenantId = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.tenantId : "";
        return tenantId;
    }
  },
  mounted() {
    if (this.dialogType == "edit") {
      this.appForm.icon = this.params.icon;
      this.appForm.sceneName = this.params.sceneName;
      this.appForm.matterDesc = this.params.matterDesc;
    }
  },
  methods: {
    close() {
      this.$emit('closeDialog')
    },
    async addSceneManagement() {
      let params = {
        ...this.appForm,
        tenantId: this.tenantId
      };
      const res = await apiAddSceneManagement(params);
      if (res.code == "000000") {
        const data = res.data || {};
        this.$emit("addSceneManagement", data);
      } else {
        this.$message.warning(res.msg);
      }
    },
    onSubmit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          if (this.dialogType == "add") {
            this.addSceneManagement();
          } else {
            this.$emit("updateSceneManagement", this.appForm);
          }
        } else {
          return false;
        }
      });
    },
    handleLogoRemove(file, fileList) {
      this.uploadBtnLogo = false;
      // this.appForm.icon = require("@/assets/images/applicationlogo.svg");
      this.appForm.icon = "";
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
  },
};
</script>

<style lang="scss" scoped>
.addScene-dialog {
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
    .dialog-footer {
    text-align: right;
    height: 42px;
    line-height: 42px;
    .el-button {
      border-radius: 2px;
    }
    .el-button--primary {
      background: #1747E5;
      border-color: #1747E5;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
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
  .upload-img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.2);
    backdrop-filter: blur(1px);
    position: absolute;
    top: 0;
    left: 0;
    display: flex;
    align-items: center;
    justify-content: center;
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
