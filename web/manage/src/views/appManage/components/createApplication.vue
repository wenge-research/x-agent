<template>
  <el-dialog
    :title="type == 'edit' ? '编辑应用' : $t('createApplication')"
    :visible.sync="dialogVisibleApplication"
    width="480px"
    :before-close="cancelTemplate"
    class="applicationDialog"
    append-to-body
    :close-on-click-modal="false"
        :close-on-press-escape="false"
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
                label="项目类型"
                prop="type"
                v-if="false"
              >
                <div class="application-type-box">
                  <el-select class="app-type-select" placeholder="请选择项目类型" style="width: 100%" v-model="appForm.type">
                    <el-option v-for="item in applicationTypeLists" :key="item.value" :value="item.value" :label="item.label"></el-option>
                  </el-select>
                  <!-- <div v-for="item in typeOptions" :key="item.id" class="application-type" :class="{ active: appForm.applicationType === item.id }" @click="handleTypeChange(item.id)">
                    <div class="type-item">
                      <img :src="item.icon" alt="">
                      <span class="type-name">{{ item.name }}</span>
                    </div>
                  </div> -->
                </div>
              </el-form-item>
              <el-form-item
                :label="$t('applicationName')"
                prop="applicationName"
              >
                <el-input
                  v-model.trim="appForm.applicationName"
                  show-word-limit
                  maxlength="100"
                  style="width: 100%"
                  placeholder="请输入应用名称"
                />
              </el-form-item>
              <el-form-item
                :label="$t('applicationDescription')"
                prop="introduce"
              >
                <el-input
                  class="inputTextarea"
                  type="textarea"
                  :rows="7"
                  v-model="appForm.introduce"
                  show-word-limit
                  maxlength="200"
                  style="width: 100%"
                  placeholder="请输入应用描述"
                />
              </el-form-item>
              <el-form-item label="图标">
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
                      @mouseenter="imgMouseenter"
                      @mouseleave="imgMouseleave"
                      v-if="appForm.facadeImageUrl"
                    >
                      <img
                        v-if="appForm.facadeImageUrl"
                        :src="appForm.facadeImageUrl"
                        style="
                          width: 80px;
                          height: 80px;
                          border-radius: 4px;
                          background: #dcdfe6;
                        "
                      />
                      <!--  -->
                      <div
                        v-show="showDeleteIcon"
                        class="opts-btn"
                      >
                      <iconpark-icon name="edit-line" size="18" color="#FFFFFF"></iconpark-icon>
                      <iconpark-icon  name="delete-bin-4-line" class="delete" size="18" color="#FFFFFF" @click.stop="deleteLogo"></iconpark-icon>
                      
                      </div>
                      
                    </div>
                    <div v-else style="height: 100%; display: flex; align-items: center; justify-content: center; background: #f2f4f7">
                      <iconpark-icon name="add-line" size="24" color="#8c939d"></iconpark-icon>
                    </div>
                  </el-upload>
                  <el-button class="ai-btn" type="primary" :loading="imgLoading" @click="getImageUrl">
                    <img src="@/assets/images/ai-btn.svg" alt="">
                    AI生成
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancelTemplate">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="confirmTemplate" style="background: #1747E5;">{{
        $t("confirm")
      }}</el-button>
    </span>
  </el-dialog>
</template>

<script>
// 引入 uuid 模块
const { v4: uuidv4 } = require('uuid');
import { applicationTypes } from '@/utils/constants';
import { getAiImage } from '@/api/app';
export default {
  data() {
    return {
      appForm: {
        facadeImageUrl: this.getRandomHeadImgDefaultBgColor(),
        applicationName: "",
        introduce: "",
        type: applicationTypes[0].value
      },
      uploadBtnLogo: false,
      fileListLogo: [],
      actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
      rules: {
        applicationName: [
          { required: true, message: "请输入应用名称", trigger: "blur" },
        ],
        // introduce: [
        //   { required: true, message: "请输入应用描述", trigger: "blur" },
        // ],
        type: [
          { required: true, message: "请选择项目类型", trigger: "change" },
        ]
      },
      showDeleteIcon: false,
      typeOptions: [
        { name: '智能体', id: 1, icon: require('@/assets/images/icon-zhinengti.svg') },
        { name: '应用', id: 2, icon: require('@/assets/images/icon-application.svg') },
      ],
      applicationTypeLists: applicationTypes,
      imgLoading: false
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
  async mounted() {
    if (this.type == "edit") {
      this.appForm.facadeImageUrl = this.params.facadeImageUrl;
      this.appForm.applicationName = this.params.applicationName;
      this.appForm.introduce = this.params.introduce;
      this.appForm.type = this.params.type;
    }
  },
  methods: {
    imgMouseenter() {
      console.log("imgMouseenter");
      if (this.appForm.facadeImageUrl) {
        this.showDeleteIcon = true;
      }
    },
    imgMouseleave() {
      console.log("imgMouseleave");
      this.showDeleteIcon = false;
    },
    deleteLogo() {
      this.appForm.facadeImageUrl = this.getRandomHeadImgDefaultBgColor();
      return;
    },
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
      this.appForm.facadeImageUrl = "";
      this.fileListLogo = [];
    },
    handleLogoSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnLogo = true;
        this.appForm.facadeImageUrl =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListLogo = [];
      } else {
        this.uploadBtnLogo = false;
        this.appForm.facadeImageUrl = "";
        this.fileListLogo = [];
      }
    },
    handleTypeChange(id) {
      this.appForm.applicationType = id;
    },
    getRandomHeadImgDefaultBgColor() {
      const imgList = [
          require('@/assets/images/appManagement/default-logo-1.svg'),
          require('@/assets/images/appManagement/default-logo-2.svg'),
          require('@/assets/images/appManagement/default-logo-3.svg'),
          require('@/assets/images/appManagement/default-logo-4.svg'),
          require('@/assets/images/appManagement/default-logo-5.svg'),
          require('@/assets/images/appManagement/default-logo-6.svg'),
          require('@/assets/images/appManagement/default-logo-7.svg'),
      ]
      const randomIndex = Math.floor(Math.random() * imgList.length);
      return imgList[randomIndex];
    },
    getImageUrl() {
      this.$refs.ruleForm.validate(async(valid) => {
        if (valid) {
          this.imgLoading = true;
          getAiImage({
            topic: this.appForm.applicationName,
            description: this.appForm.introduce
          }).then((res) => {
            if (res.code == "000000") {
              this.appForm.facadeImageUrl = res.data || this.getRandomHeadImgDefaultBgColor();
            }else {
              this.$message.warning('生成失败')
            }
            
            this.imgLoading = false;
          }).catch(() => {

            this.imgLoading = false;
          });
        } else {
          return false;
        }
      });
      
      
    }
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
      background: #FFFFFF;
      border-radius: 8px 8px 0px 0px;
      padding: 32px 32px 16px 32px;
      font-weight: 500;
      font-size: 20px;
      color: #494E57;
      line-height: 24px;
      .el-dialog__title {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        color: #494E57;
        line-height: 32px;
      }
      .el-dialog__headerbtn {
        right: 32px;
        top: 36px;
      }
    }
    .el-dialog__footer {
      text-align: right;
    }
  }
  .opts-btn {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    background: rgba(0, 0, 0, .4);
    backdrop-filter: blur(1px);
    position: absolute;
    bottom: 0;
    right: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
  }

  .delete {
    // position: absolute;
    // top: 8px;
    // right: 8px;
  }
}
.inputTextarea {
  ::v-deep .el-textarea__inner {
    font-family: MiSans, MiSans;
  }
}
.logoAppUpload {
  text-align: center;
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
.application-type-box {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 16px;
  .application-type {
    width: 202px;
    height: 80px;
    display: flex;
    justify-content: center;
    background: #FFFFFF;
    border-radius: 2px;
    border: 1px solid #D5D8DE;
    align-items: center;
    cursor: pointer;
    .type-item {
      display: inline-flex;
      flex-direction: column;
      text-align: center;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 18px;
      img {
        width: 20px;
        height: 20px;
        margin: 0px auto 8px;
      }
    }
    &.active {
      background: rgba(28,80,253,0.05);
      border: 1px solid #1747E5;
    }
  }
}
.uploadOuter {
  width: 100%;
  display: flex;
  align-items: end;
  :deep(.ai-btn) {
    height: 32px;
    background: linear-gradient( 270deg, rgba(142, 101, 255, .15) 0%, rgba(23, 71, 229, .15) 100%);
    border-radius: 2px;
    display: inline-flex;
    align-items: center;
    border: 0px;
    padding: 0px 8px;
    
    margin-left: 16px;
    font-size: 14px;
    color: #1747E5;
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
