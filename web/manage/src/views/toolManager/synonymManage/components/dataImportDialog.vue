<template>
  <el-dialog
    :title="$t('dataImport')"
    :visible.sync="dialogDataVisible"
    width="600px"
    class="dialog"
	@close="cancelSubmit"
    :close-on-click-modal="false"
        :close-on-press-escape="false"
  >
    <div style="margin: 10px 0">
      <el-upload
        action="#"
        drag
        accept=".xlsx"
        :before-upload="beforeupload"
        :show-file-list="false"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          <div class="top">
            {{ $t("promptToDragAndDropFilesHereOr")
            }}<em style="color: #1c50fd">{{ $t("selectFile") }}</em>
          </div>
          <div class="bottom">
            {{ $t("uploadTextMore") }}
          </div>
          <div class="bottom">
            {{ $t("uploadText") }}
          </div>
        </div>
      </el-upload>
      <div class="file-item" v-for="item in uploadFile" :key="item.uid">
        {{ item.name }} <i @click="handleRemovefileItem" class="el-icon-close"></i>
      </div>
    </div>

    <div class="dialog-footer">
      <el-button
        type="text"
        icon="el-icon-download"
        @click="handleDownloadKnowledgeDataTemp"
        :loading="downLoading"
        >{{ $t("downloadTemplate") }}</el-button
      >
      <div>
        <el-button :loading="exportLoading" @click="cancelSubmit" class="cancelBtn">{{ $t("cancel") }}</el-button>
        <el-button type="primary" @click="submitAddUser">{{ $t("confirm") }}</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { downloadSynonymWordDataTemp, importSynonymWordData } from "@/api/toolManager";
export default {
  props: {
    dialogDataVisible: {
      type: Boolean,
      default: false,
    },
    row: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      uploadForm: new FormData(),
      uploadFile: [],
      downLoading: false,
      exportLoading: false
    };
  },
  methods: {
    async submitAddUser() {
      this.exportLoading = false;
      let res = await importSynonymWordData(this.uploadForm)
      if (res.code == "000000") {
        this.exportLoading = true;
        this.$message.success("导入成功")
        this.$emit("closeDataDialog", 2);
      } else {
        this.exportLoading = true;
        this.$message.warning(res.msg)
      }
    },
    cancelSubmit() {
      this.$emit("closeDataDialog", 1);
    },
    beforeupload(file) {
      this.uploadForm = new FormData();
      this.uploadForm.append("file", file);
      this.uploadFile = [file];
      return false;
    },
    handleRemovefileItem() {
      this.uploadForm = new FormData();
      this.uploadFile = [];
    },
    async handleDownloadKnowledgeDataTemp() {
      let res = await downloadSynonymWordDataTemp();
      let data = res;
      console.log(data);
      const url = window.URL.createObjectURL(new Blob([res]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "模版" + this.formatDate() + ".xlsx");
      document.body.appendChild(link);
      link.click();
    },
    formatDate() {
      const date = new Date()
      const year = date.getFullYear()
      let month = date.getMonth() + 1
      let day = date.getDate()
      let hours = date.getHours()
      let minutes = date.getMinutes()

      // 如果月份或日期小于10，则在前面添加0
      month = month < 10 ? '0' + month : month
      day = day < 10 ? '0' + day : day
      hours = hours < 10 ? '0' + hours : hours
      minutes = minutes < 10 ? '0' + minutes : minutes

      return year + month + day + '_' + hours + minutes
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-upload {
  width: 100%;
}
::v-deep .dialog .el-dialog__header {
    background: none;
}
::v-deep .el-upload-dragger {
  width: 100%;
  background: #f9fafc;
  background: #f9fafc;
  border-radius: 4px;
  border: 1px dashed rgba(0, 0, 0, 0.12);
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  .el-icon-upload {
    margin: 0 18px 0 0;
  }
  .el-upload__text {
    text-align: left;
    .top {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #383d47;
      line-height: 20px;
    }
    .bottom {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #b4bccc;
      line-height: 20px;
    }
  }
}
.flex {
  display: flex;
  align-items: center;
}
.dialog-footer {
    display: flex;
    justify-content: space-between;
    padding-bottom: 20px;
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
    .el-button--text {
        color: #1747E5;
    }
  }

.file-item {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  margin-top: 12px;
  align-items: center;

  span {
    width: 60%;
  }

  i {
    margin-left: 5px;
    cursor: pointer;
    font-size: 20px;
  }
}

.cancelBtn.el-button:hover {
  background: #fff;
}
</style>
