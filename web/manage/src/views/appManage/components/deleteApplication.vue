<template>
  <el-dialog
    :title="$t('deleteApp')"
    :visible.sync="deleteDialogVisible"
    width="560px"
    class="deleteDialog"
    :before-close="cancelDelete"
    append-to-body
  >
    <p class="desc">{{ $t("deletionWarning") }}</p>
    <el-input
      :placeholder="$t('enterApplicationNameToDelete')"
      v-model="deleteItemName"
    ></el-input>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancelDelete">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="confirmDelete">确定删除</el-button>
    </span>
  </el-dialog>
</template>
  
<script>
import { deleteApplication } from "@/api/app";
export default {
  data() {
    return {
      deleteItemName: "",
    };
  },
  props: {
    deleteDialogVisible: {
      type: Boolean,
      default: false,
    },
    params: Object,
  },
  mounted() {},
  methods: {
    confirmDelete() {
      if (this.params.applicationName == this.deleteItemName) {
        deleteApplication({
          applicationId: this.params.applicationId,
        }).then((res) => {
          console.log(res, 222);
          if (res.code == "000000") {
            this.$message({
              type: "success",
              message: "删除成功",
            });
            this.$emit("configCancelDelete", false);
          } else {
            this.$message({
              type: "error",
              message: "删除失败",
            });
          }
        });
      } else {
        this.$message({
          type: "warning",
          message: "输入名称与删除应用名称不一致",
        });
        return;
      }
    },
    cancelDelete() {
      this.$emit("configCancelDelete", false);
    },
  },
};
</script>
  
<style lang="scss" scoped>
.deleteDialog {
  ::v-deep .el-dialog__header {
    background: #fff !important;
  }
  ::v-deep .el-dialog__title {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 20px;
    color: #383d47;
    line-height: 24px;
    text-align: left;
    font-style: normal;
    text-transform: none;
  }
  ::v-deep .el-dialog__headerbtn {
    top: 36px;
  }
  .desc {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #768094;
    line-height: 20px;
    text-align: left;
    font-style: normal;
    margin-bottom: 8px;
  }
  ::v-deep .el-dialog__footer {
    text-align: right !important;
    .el-button {
      border-radius: 4px;
    }
    .el-button--primary {
      background: #1747E5;
      color: #fff;
      border-color: transparent;
    }
  }
}
</style>
  