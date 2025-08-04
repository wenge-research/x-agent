<template>
  <el-dialog
    :title="isEdit ? $t('editErrorCorrectingWords') : $t('createErrorCorrectingWords')"
    v-if="dialogVisible"
    :visible.sync="dialogVisible"
    width="600px"
    class="dialog"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    append-to-body
    destroy-on-close
    :before-close="cancelSubmit"
  >
    <el-form
      :model="roleForm"
      :rules="roles"
      ref="roleForm"
      class="roleForm"
      label-position="top"
    >
      <el-form-item :label="$t('keywords')" prop="keyWord">
        <el-input v-model="roleForm.keyWord" :placeholder="$t('pleaseEnter')"></el-input>
      </el-form-item>
      <el-form-item :label="$t('category')" prop="type">
        <el-input v-model="roleForm.type" :placeholder="$t('pleaseEnter')"></el-input>
      </el-form-item>
      <el-form-item :label="$t('correctionWords')" prop="correctWordList">
        <div v-for="(item, index) in roleForm.correctWordList" :key="index">
          <el-input v-model="item.content" :placeholder="$t('pleaseEnter')" style="margin-bottom:10px;"></el-input>
          <i class="el-icon-circle-plus-outline" style="font-size: 20px; margin-right:10px; cursor: pointer;" @click="addRow(index)"></i>
          <i class="el-icon-remove-outline" style="font-size: 20px; cursor: pointer;" @click="deleteRow(index)"></i>
        </div>
      </el-form-item>
    </el-form>
    <div class="dialog-footer">
      <el-button @click="cancelSubmit">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="submitAddUser">{{ $t("confirm") }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addCorrectWord, editCorrectWord } from "@/api/toolManager";
export default {
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    isEdit: {
      type: Boolean,
      default: false,
    },
    sensitiveRow: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      roleForm: {
        correctWordList: [{ content: "" }],
      },
      roles: {
        keyWord: [{ required: true, message: this.$t("cannotBeEmpty"), trigger: "blur" }],
        correctWordList: [{ required: true, message: this.$t("cannotBeEmpty"), trigger: "blur" }],
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
    sensitiveRow(val) {
      this.roleForm = JSON.parse(JSON.stringify(val));
    },
  },
  mounted() {
    this.roleForm = this.sensitiveRow ? JSON.parse(JSON.stringify(this.sensitiveRow)) : {
      keyWord: "",
      type: "",
      correctWordList: [{ content: "" }] // 确保初始化时包含一个空对象
    };
  },
  methods: {
    submitAddUser() {
      this.$refs.roleForm.validate(async (valid) => {
        if (valid) {
          let res;
          if (!this.isEdit) {
            const params = {
              ...this.roleForm,
              tenantId: this.tenantId
            }
            res = await addCorrectWord(params);
          } else {
            res = await editCorrectWord({
              id: this.roleForm.id,
              keyWord: this.roleForm.keyWord,
              type: this.roleForm.type,
              correctWordList: this.roleForm.correctWordList,
            });
          }
          if (res.code == "000000") {
            this.$message({
              message: "保存成功",
              type: "success",
            });
            
            this.$emit("submitDialog", this.roleForm);
          } else {
            this.$message.warning(res.msg);
            this.$emit("closeDialog");
          }
        }
      });
    },
    cancelSubmit() {
      this.$emit("closeDialog");
    },
    addRow(index) {
    this.roleForm.correctWordList.splice(index + 1, 0, { content: "" });
  },
  deleteRow(index) {
    if (this.roleForm.correctWordList.length > 1) {
      this.roleForm.correctWordList.splice(index, 1);
    }
  },
  },
};
</script>

<style lang="scss" scoped>
.el-form-item  {
  display: flex;
}
.el-color-picker__icon, .el-input, .el-textarea {
  width: 200px;
  margin-left: 16px;
  margin-right: 16px;
}
.dialog-footer {
    text-align: right;
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
  }
</style>
