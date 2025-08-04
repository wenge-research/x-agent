<template>
  <el-dialog
    :title="row ? $t('editSensitiveLexicon') : $t('newSensitiveLexicon')"
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
      <el-form-item :label="$t('lexiconName')" prop="name">
        <el-input v-model="roleForm.name" :placeholder="$t('pleaseEnter')"></el-input>
      </el-form-item>
      <el-form-item :label="$t('describe')" prop="remark">
        <el-input
          v-model="roleForm.remark"
          type="textarea"
          :placeholder="$t('pleaseEnter')"
          :autosize="{ minRows: 4, maxRows: 6 }"
        ></el-input>
      </el-form-item>
    </el-form>
    <div class="dialog-footer">
      <el-button @click="cancelSubmit">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="submitAddUser">{{ $t("confirm") }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addInterceptWordHouse, updateInterceptWordHouse } from "@/api/toolManager";
export default {
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    row: {
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
      roleForm: {},
      roles: {
        name: [{ required: true, message: this.$t("cannotBeEmpty"), trigger: "blur" }],
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
      console.log(val, 8888);
      this.roleForm = JSON.parse(JSON.stringify(val));
    },
  },
  mounted() {
    console.log(this.sensitiveRow, 9999);
    
    this.roleForm = this.sensitiveRow ? JSON.parse(JSON.stringify(this.sensitiveRow)) : {};
  },
  methods: {
    submitAddUser() {
      this.$refs.roleForm.validate(async (valid) => {
        if (valid) {
          let res;
          if (!this.row) {
            const params = {
              ...this.roleForm,
              tenantId: this.tenantId
            }
            res = await addInterceptWordHouse(params);
          } else {
            res = await updateInterceptWordHouse({
              id: this.roleForm.id,
              name: this.roleForm.name,
              remark: this.roleForm.remark,
              tenantId: this.tenantId
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
  },
};
</script>

<style lang="scss" scoped>
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
