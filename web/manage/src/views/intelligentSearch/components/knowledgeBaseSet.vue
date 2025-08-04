<template>
  <el-dialog
    :title="$t('knowledgeBaseParameterSettings')"
    :visible.sync="dialogVisible"
    width="560px"
    class="applicationDialog"
    :before-close="cancelConfig"
    append-to-body
  >
    <div class="setting">
      <div class="flexOuter">
        <span>{{ $t("contentScoreThreshold") }}</span>
        <el-slider
          v-model="appForms.contentScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <el-input-number
          v-model="appForms.contentScore"
          controls-position="right"
          :min="0"
          :max="10"
          :step="0.1"
          size="small"
          style="width: 105px"
        ></el-input-number>
      </div>
      <div class="flexOuter">
        <span>{{ $t("reRankingBodyScoreThreshold") }}</span>
        <el-slider
          v-model="appForms.rangeContentScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <el-input-number
          controls-position="right"
          v-model="appForms.rangeContentScore"
          :min="0"
          :max="10"
          :step="0.1"
          size="small"
          style="width: 105px"
        ></el-input-number>
      </div>
      <div class="flexOuter">
        <span>{{ $t("qaTitleScoreThreshold") }}</span>
        <el-slider
          v-model="appForms.qaTitleScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <el-input-number
          controls-position="right"
          v-model="appForms.qaTitleScore"
          :min="0"
          :max="10"
          :step="0.1"
          size="small"
          style="width: 105px"
        ></el-input-number>
      </div>
      <div class="flexOuter">
        <span>{{ $t("reRankingTitleScoreThreshold") }}</span>
        <el-slider
          v-model="appForms.qaRangeTitleScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <el-input-number
          controls-position="right"
          v-model="appForms.qaRangeTitleScore"
          :min="0"
          :max="10"
          :step="0.1"
          size="small"
          style="width: 105px"
        ></el-input-number>
      </div>
      <div class="flexOuter">
        <span>{{ $t("qaBodyScoreThreshold") }}</span>
        <el-slider
          v-model="appForms.qaContentScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <el-input-number
          controls-position="right"
          v-model="appForms.qaContentScore"
          :min="0"
          :max="10"
          :step="0.1"
          size="small"
          style="width: 105px"
        ></el-input-number>
      </div>
      <div class="flexOuter">
        <span>{{ $t("reRankingBodyAnswerScoreThreshold") }}</span>
        <el-slider
          v-model="appForms.qaRangeContentScore"
          :min="0"
          :max="10"
          :step="0.1"
        ></el-slider>
        <el-input-number
          controls-position="right"
          v-model="appForms.qaRangeContentScore"
          :min="0"
          :max="10"
          :step="0.1"
          size="small"
          style="width: 105px"
        ></el-input-number>
      </div>
      <div class="flexOuter">
        <span>{{ $t("referencedKnowledgeBaseParagraphCount") }}</span>
        <el-slider
          v-model="appForms.filterNum"
          :min="0"
          :max="10"
          :step="1"
        ></el-slider>
        <el-input-number
          controls-position="right"
          v-model="appForms.filterNum"
          :min="0"
          :max="10"
          :step="0.1"
          size="small"
          style="width: 105px"
        ></el-input-number>
      </div>
      <div class="flexOuter">
        <span>{{ $t("knowledgeBaseParagraphPreparationCount") }}</span>
        <el-slider
          v-model="appForms.prepareNum"
          :min="0"
          :max="100"
          :step="1"
        ></el-slider>
        <el-input-number
          controls-position="right"
          v-model="appForms.prepareNum"
          :min="0"
          :max="100"
          :step="0.1"
          size="small"
          style="width: 105px"
        ></el-input-number>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="setConfig">{{
        $t("confirm")
      }}</el-button>
      <el-button @click="cancelConfig">{{ $t("cancel") }}</el-button>
      <el-button
        type="primary"
        icon="el-icon-refresh"
        plain
        @click="useDefaultValue"
        style="
          float: right;
          color: #1c50fd;
          border-color: #1c50fd;
          background: none;
        "
        >{{ $t("useDefaultValue") }}
      </el-button>
    </span>
  </el-dialog>
</template>
    
<script>
export default {
  data() {
    return {
      appForms: {
        contentScore: 1.76,
        rangeContentScore: 0.88,
        qaTitleScore: 1.91,
        qaRangeTitleScore: 0.91,
        qaContentScore: 0.88,
        qaRangeContentScore: 0.88,
        filterNum: 3,
        prepareNum: 60,
      },
    };
  },
  props: {
    dialogVisible: Boolean,
    params: Object,
  },
  mounted() {
    this.openParamsConfig();
  },
  methods: {
    // 回显
    openParamsConfig() {
      this.appForms.contentScore = this.params.contentScore;
      this.appForms.rangeContentScore = this.params.rangeContentScore;
      this.appForms.qaTitleScore = this.params.qaTitleScore;
      this.appForms.qaRangeTitleScore = this.params.qaRangeTitleScore;
      this.appForms.qaContentScore = this.params.qaContentScore;
      this.appForms.qaRangeContentScore = this.params.qaRangeContentScore;
      this.appForms.filterNum = this.params.filterNum;
      this.appForms.prepareNum = this.params.prepareNum;
    },
    // 确认配置
    setConfig() {
      this.$emit("clickConfigParams", "setBaseVisible", this.appForms);
    },
    // 取消
    cancelConfig() {
      this.$emit("clickConfig", false);
    },
    // 使用默认值
    useDefaultValue() {
      this.appForms.contentScore = 1.76;
      this.appForms.rangeContentScore = 0.88;
      this.appForms.qaTitleScore = 1.91;
      this.appForms.qaRangeTitleScore = 0.91;
      this.appForms.qaContentScore = 0.88;
      this.appForms.qaRangeContentScore = 0.88;
      this.appForms.filterNum = 3;
      this.appForms.prepareNum = 60;
    },
  },
};
</script>
    
<style lang="scss" scoped>
.applicationDialog {
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__body {
      padding: 16px 32px;
    }
    .el-dialog__header {
      background: linear-gradient(
        180deg,
        rgba(43, 88, 213, 0.1) 0%,
        rgba(43, 88, 213, 0) 100%
      );
      border-radius: 8px 8px 0px 0px;
    }
    .el-dialog__footer {
      text-align: left;
    }
  }
}
.setting {
  .flexOuter {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    width: 100%;
    margin: 10px 0px;
    ::v-deep .el-slider__runway {
      width: 240px;
      margin-right: 8px;
    }
    span {
      display: inline-block;
      width: 128px;
      position: relative;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      line-height: 20px;
      color: #383d47;
      margin-right: 20px;
    }
  }

  ::v-deep .el-switch .el-switch__label.is-active {
    color: #3666ea;
  }
  ::v-deep .el-switch.is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%) !important;
    border-radius: 12px;
    border-color: transparent;
  }
  ::v-deep .el-slider__runway {
    height: 4px;
  }
  ::v-deep .el-slider__bar {
    height: 4px;
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
    border-radius: 4px;
  }
  ::v-deep .el-slider__button {
    width: 18px;
    height: 18px;
    background: #ffffff;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.12);
    border: 1px solid #f2f5fa;
    margin-top: -4px;
  }
  ::v-deep .el-slider__input {
    width: 110px;
  }
  ::v-deep .el-slider__runway.show-input {
    margin-right: 120px;
  }
}
</style>
    