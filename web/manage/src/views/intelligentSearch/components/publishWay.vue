<template>
  <el-dialog
    :title="$t('publishMethod')"
    :visible.sync="fabuDialogVisible"
    width="560px"
    class="publicWay"
    destroy-on-close
    :before-close="cancelConfig"
    append-to-body
  >
    <div class="flex-center">
      <div
        class="radioOuter flex-center just"
        :class="{ active: publishStatus == '1' }"
        @click="handlePublish('1')"
        style="margin-right: 16px"
      >
        <img src="@/assets/images/public.svg" />
        <p class="way">{{ $t("publicPublish") }}</p>
        <p class="tips">{{ $t("publicPublishTip") }}</p>
      </div>
      <div
        class="radioOuter flex-center just"
        :class="{ active: publishStatus == '2' }"
        @click="handlePublish('2')"
      >
        <img src="@/assets/images/private.svg" />
        <p class="way">{{ $t("privatePublish") }}</p>
        <p class="tips">
          {{ $t("privatePublishTip") }}
        </p>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <div class="flex-center">
        <div class="pulishBtn flex-center" @click="setConfig">
          <img src="@/assets/images/send-plane-fill.svg" />
          <span>{{$t('confirmPublish')}}</span>
        </div>
        <span class="canlseBtn" @click="cancelConfig"> {{$t('cancel')}} </span>
      </div>
    </span>
  </el-dialog>
</template>
      
<script>
export default {
  data() {
    return {
      publishStatus: "1",
    };
  },
  props: {
    fabuDialogVisible: Boolean,
    params: Object,
  },
  mounted() {
    this.publishStatus = this.params.publishStatus;
  },
  methods: {
    handlePublish(type) {
      this.publishStatus = type;
    },
    // 确认配置
    setConfig() {
      this.$emit("clickConfigParams", "fabuDialogVisible", this.publishStatus);
    },
    // 取消
    cancelConfig() {
      this.$emit("confirmPublishWay");
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
  }
}

.radioOuter {
  width: 240px;
  height: 200px;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #e1e4eb;
  cursor: pointer;
  padding: 20px;

  img {
    width: 80px;
    height: 80px;
  }
  .way {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 16px;
    color: #383d47;
    line-height: 22px;
    text-align: left;
    font-style: normal;
    margin: 4px 0px;
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
  &.active {
    background: rgba(28, 80, 253, 0.05);
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
.pulishBtn {
  width: 126px;
  height: 40px;
  background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
  border-radius: 4px;
  color: #ffffff;
  line-height: 40px;
  justify-content: center;
  cursor: pointer;
  img {
    width: 18px;
    height: 18px;
    margin-right: 8px;
    transform: rotate(50deg);
  }
}
.canlseBtn {
  display: inline-block;
  width: 72px;
  height: 40px;
  border-radius: 4px;
  border: 1px solid #c4c6cc;
  line-height: 40px;
  text-align: center;
  margin-left: 20px;
  cursor: pointer;
}
</style>
      