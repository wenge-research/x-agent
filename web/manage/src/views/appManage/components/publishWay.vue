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
        <p class="tips">用户可通过PC或移动设备浏览器直接访问网页demo</p>
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
        
        <span class="canlseBtn" @click="cancelConfig"> {{$t('cancel')}}</span>
        <div class="pulishBtn flex-center" @click="setConfig">
          <img src="@/assets/images/send-plane-fill.svg" />
          <span>{{$t('confirmPublish')}}</span>
        </div>
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
    .el-dialog__body {
      padding: 0px 32px 16px;
    }
    .el-dialog__header {
      background-color: #ffffff;
      padding: 32px 32px 16px;
      font-weight: 500;
      font-size: 20px;
      color: #494E57;
      line-height: 24px;
      .el-dialog__headerbtn {
        top: 36px;
        right: 32px;
      }
    }
    .el-dialog__footer {
      text-align: right;
      .dialog-footer {
        .flex-center {
          justify-content: flex-end;
          gap: 16px;
        }
      }
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
  padding: 16px 8px;

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
    text-align: center;
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
  gap: 0px !important;
  width: 126px;
  height: 40px;
  background: #1747E5;
  border-radius: 2px;
  color: #ffffff;
  line-height: 40px;
  justify-content: center !important;
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
      