<template>
  <el-dialog
    :title="`删除` + deleteName"
    :visible.sync="deleteDialogVisible"
    width="560px"
    class="deleteDialog"
    :before-close="cancelDelete"
    append-to-body
  >
    <p class="desc">{{ $t("deletionWarning") }}</p>
    <el-input
      :placeholder="`请输入要删除的` + deleteName + `名称`"
      v-model="deleteItemName"
    ></el-input>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="confirmDelete">{{
        $t("delete")
      }}</el-button>
      <el-button @click="cancelDelete">{{ $t("cancel") }}</el-button>
    </span>
  </el-dialog>
</template>
  
<script>
import { deleteComponent } from "@/api/workflow";
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
    deleteName: {
      type: String,
      default: "工作流",  
    }
  },
  mounted() {},
  methods: {
    confirmDelete() {
      if (this.params.componentName == this.deleteItemName) {
        deleteComponent({
          componentId: this.params.componentId,
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
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
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
    text-align: left;
    .el-button {
      border-radius: 4px;
    }
    .el-button--primary {
      background: #dc2544;
      color: #fff;
      border-color: transparent;
    }
  }
}
</style>
  