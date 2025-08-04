<template>
  <el-dialog
    title=""
    :visible.sync="dialogVisible"
    width="480px"
    :before-close="handleClose"
    class="download-dialog"
    append-to-body
    :close-on-click-modal="false"
        :close-on-press-escape="false"
    :show-close="false"
  >
    <div class="dialog-content">
      <div>
        <img src="@/assets/images/deepseek.png" alt="Download Icon" style="width: 64px; height: 64px;">
      </div>
      <div class="progress-bar">
        <el-progress :percentage="progressPercentage" :show-text="false" :stroke-width="6" class="gradient-progress"></el-progress>
      </div>
      <div class="status">
        <div class="left">{{ $t('downloading') }} DeepSeek-1.5b</div>
        <div class="right">1.84GB / 1.5GB <span>64%</span></div>
      </div>
      <div class="close-button">
        <el-button @click="handleClose" style="width: 160px;">{{ $t('close') }}</el-button>
      </div>
      <div class="hint">
        你可以关闭窗口，部署将在后台继续
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      dialogVisible: true,
      progressPercentage: 64, // 进度百分比
    };
  },
  methods: {
    handleClose() {
      this.dialogVisible = false;
      this.$emit('close');
    },
  },
};
</script>

<style lang="scss" scoped>
.download-dialog {
  ::v-deep .el-dialog {
    border-radius: 8px;
    text-align: center;

    .el-dialog__body {
      padding: 20px 60px;
    }

    .el-dialog__header {
      border-radius: 8px 8px 0 0;

      .el-dialog__title {
        font-size: 16px;
        font-weight: 500;
      }
    }

    .el-dialog__footer {
      text-align: center;
      padding: 10px 20px 20px;
    }
  }

  .dialog-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .icon {
      margin-bottom: 20px;
    }

    .progress-bar {
      width: 100%;
      margin: 20px 0 10px;
    }

    .status {
      display: flex;
      justify-content: space-between;
      width: 100%;
      margin-bottom: 20px;

      .left {
        font-size: 12px;
        color: #36383D;
      }

      .right {
        font-size: 12px;
        color: #828894;
        span {
          color: #36383D;
        }
      }
    }

    .close-button {
      margin: 30px 0 20px 0;
    }

    .hint {
      font-size: 12px;
      color: #828894;
      margin: 0 0 30px;
    }
  }

  .gradient-progress {
    ::v-deep .el-progress-bar__inner {
      background: linear-gradient(to right, #1747E5, #8E65FF);
      opacity: 0.6;
    }
  }
}
</style>