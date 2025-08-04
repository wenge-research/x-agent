<template>
  <el-dialog
    title=""
    :visible.sync="fabuSuccessVisible"
    class="publicWay"
    width="400px"
    :before-close="cancelConfig"
    append-to-body
  >
    <div class="radioOuter flex-center just">
      <img src="@/assets/images/appManagement/fabusuccess.svg" />
      <p class="way">{{ $t("publishSuccess") }}</p>
      <p class="tips" v-if="params.publishStatus == '1'">
        {{ $t("internetAccess") }}
      </p>
      <p class="tips" v-if="params.publishStatus == '2'">
        {{ $t("APIKeyAccess") }}
      </p>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancelConfig">{{ $t("close") }}</el-button>
      <el-button
        type="primary"
        style="background: #1c50fd"
        v-if="params.publishStatus == '1'"
        @click="openWindows"
        >{{$t("visitNow")}}</el-button
      >
      <el-button
        type="primary"
        style="background: #1c50fd"
        v-if="params.publishStatus == '2'"
        @click="showSecretKey"
        >{{$t("viewkey")}}</el-button
      >
    </span>
  </el-dialog>
</template>
        
<script>
export default {
  data() {
    return {};
  },
  props: {
    fabuSuccessVisible: Boolean,
    params: Object,
  },
  mounted() {},
  methods: {
    // 访问
    openWindows() {
      window.open(this.params.clientLink, "_blank");
      this.$emit("clickConfigParams", "fabuSuccessVisible1");
    },
    // 查看秘钥
    showSecretKey() {
      this.$emit("clickConfigParams", "fabuSuccessVisible");
    },
    // 取消
    cancelConfig() {
      this.$emit("clickConfig", false);
    },
  },
};
</script>
        
<style lang="scss" scoped>
.publicWay {
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__header {
      background: linear-gradient(
        180deg,
        rgba(43, 88, 213, 0.1) 0%,
        rgba(43, 88, 213, 0) 100%
      );
      border-radius: 8px 8px 0px 0px;
    }
    .el-dialog__footer {
      text-align: center;
    }
  }
}

.radioOuter {
  img {
    width: 120px;
    height: 120px;
  }
  .way {
    width: 72px;
    height: 24px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #383d47;
    line-height: 24px;
    text-align: left;
    font-style: normal;
  }
  .tips {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #828894;
    line-height: 20px;
    text-align: left;
    font-style: normal;
  }
}
.flex-center {
  display: flex;
  align-items: center;
}

.just {
  flex-direction: column;
  justify-content: center;
}
</style>
        