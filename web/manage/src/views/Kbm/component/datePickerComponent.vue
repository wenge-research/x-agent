<template>
  <el-dialog
    :title="$t('updateValidityPeriod')"
    :visible.sync="dialogVisible"
    width="20%"
    :before-close="handleClose"
    :modal-append-to-body="false"
    :close-on-click-modal="false"
    :show-close="false"
    :close-on-press-escape="false"
    @opened="openDialog"
  >
    <div>
      <div style="margin-bottom: 8px">{{ $t("effectiveDate") }}</div>
      <el-date-picker
        v-model="dialogForm[startKey]"
        type="datetime"
        value-format="yyyy-MM-dd HH:mm:ss"
        :placeholder="$t('selectDateTime')"
        style="margin-bottom: 12px"
      >
      </el-date-picker>
      <div style="margin-bottom: 8px">{{ $t("expiringDate") }}</div>
      <el-date-picker
        v-model="dialogForm[endKey]"
        type="datetime"
        value-format="yyyy-MM-dd HH:mm:ss"
        :placeholder="$t('selectDateTime')"
      >
      </el-date-picker>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose" :loading="loading">{{
        $t("cancel")
      }}</el-button>
      <el-button type="primary" @click="onSubmit" :loading="loading">{{
        $t("confirm")
      }}</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updatePassword, apiUpdateUrlParserInfo } from "@/api/index.js";
export default {
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    startKey: {
      type: String,
      default: "",
    },
    endKey: {
      type: String,
      default: "",
    },
    dialogType: {
      type: String,
      default: "",
    },
    sourceData: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      dialogVisible: false,
      dialogForm: {
        effectiveStartTime: "",
        effectiveEndTime: "",
        periodStart: "", // 文档/url - 生效时间
        periodEnd: "", // 文档/url - 失效时间
      },
      loading: false,
    };
  },
  watch: {
    value(n) {
      this.dialogVisible = n;
    },
  },
  methods: {
    openDialog() {
      this.dialogForm[this.startKey] = this.sourceData[this.startKey]
      this.dialogForm[this.endKey] = this.sourceData[this.endKey]
    },
    // 文档
    async addKnowledgeData() {
      this.loading = true;
      try {
        const params = {
          id: this.sourceData?.id,
          fileId: this.sourceData?.fileId,
          periodStart: this.dialogForm?.periodStart,
          periodEnd: this.dialogForm?.periodEnd,
        };
        const res = await updatePassword(params);
        console.log("res====", res);
        if (res.code == "000000") {
          // this.$message.success(this.$t('successed'))
          this.handleClose();
          this.$emit("refreshList");
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.loading = false;
      }
      this.loading = false;
    },
    // url
    async updateUrlParserInfo() {
      this.loading = true;
      try {
        const params = {
          id: this.sourceData?.id,
          urlId: this.sourceData?.urlId,
          periodStart: this.dialogForm?.periodStart,
          periodEnd: this.dialogForm?.periodEnd,
        };
        const res = await apiUpdateUrlParserInfo(params);
        console.log("res====", res);
        if (res.code == "000000") {
          // this.$message.success(this.$t('successed'))
          this.handleClose();
          this.$emit("refreshList");
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.loading = false;
      }
      this.loading = false;
    },
    onSubmit() {
      switch (this.dialogType) {
        // 文档
        case "document":
          this.addKnowledgeData();
          break;
        // url
        case "url":
          this.updateUrlParserInfo();
          break;

        default:
          break;
      }
    },
    handleClose() {
      this.dialogVisible = false;
      this.$emit("closeDialog", false);
    },
  },
};
</script>

<style>
</style>