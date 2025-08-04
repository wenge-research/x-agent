<template>
  <el-dialog
    :title="'知识库高级设置'"
    :visible.sync="dialogVisible"
    width="560px"
    class="applicationDialog"
    :before-close="cancelConfig"
    append-to-body
  >
    <div class="setting">
      <div class="flexOuter">
        <div class="flexTitle">
          <span>重排模型</span>
          <div class="icon-ctn">
            <el-tooltip popper-class="workflow-tooltip" content="对初步检索出的候选内容进行二次排序，提升传递给大模型的信息质量。" placement="top" effect="light">
              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
            </el-tooltip>
          </div>
          
        </div>
        <div class="flexContent">
          <el-select v-model="appForms.rearrangeModel" style="width: 100%;margin: 10px 0;" size="small" placeholder="请选择">
            <el-option label="雅意" value="yayi"></el-option>
            <el-option label="火山引擎" value="volcengine"></el-option>
          </el-select>
        </div>
        <div class="flexTitle">
          <span>{{ $t("contentScoreThreshold") }}</span>
          <div class="icon-ctn">
            <el-tooltip popper-class="workflow-tooltip" content="对检索出的候选内容根据设定的匹配度召回并提供给大模型，低于设定值的段落将不被召回。" placement="top" effect="light">
              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
            </el-tooltip>
          </div>
        </div>
        <div class="flexContent">
          <el-slider
            v-model="appForms.contentScore"
            :min="0"
            :max="10"
            :step="0.1"
          ></el-slider>
          <el-input-number
            class="score-input"
            v-model="appForms.contentScore"
            controls-position="right"
            :min="0"
            :max="10"
            :step="0.1"
            size="small"
            style="width: 80px"
          ></el-input-number>
          <span class="score-reset-btn" @click="setDefaultValue('contentScore', 1.49)">
            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
          </span>
          
        </div>
      </div>
      <div class="flexOuter">
        <div class="flexTitle">
          <span>{{ $t("reRankingBodyScoreThreshold") }}</span>
          <div class="icon-ctn">
            <el-tooltip popper-class="workflow-tooltip" content="对检索出的候选内容进行重新排序时，根据设定的匹配度召回并提供给大模型，低于设定值的段落将不被召回。" placement="top" effect="light">
              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
            </el-tooltip>
          </div>
        </div>
        <div class="flexContent">
          <el-slider
            v-model="appForms.rangeContentScore"
            :min="0"
            :max="10"
            :step="0.1"
          ></el-slider>
          <el-input-number
            class="score-input"
            controls-position="right"
            v-model="appForms.rangeContentScore"
            :min="0"
            :max="10"
            :step="0.1"
            size="small"
            style="width: 80px"
          ></el-input-number>
          <span class="score-reset-btn" @click="setDefaultValue('rangeContentScore', 1.49)">
            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
          </span>
        </div>
      </div>
      <div class="flexOuter">
        <div class="flexTitle">
          <span>{{ $t("referencedKnowledgeBaseParagraphCount") }}</span>
          <div class="icon-ctn">
            <el-tooltip popper-class="workflow-tooltip" content="从知识库中召回并提供给大模型的段落数量上限。" placement="top" effect="light">
              <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
            </el-tooltip>
          </div>
        </div>
        <div class="flexContent">
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
            class="score-input"
            style="width: 80px"
          ></el-input-number>
          <span class="score-reset-btn" @click="setDefaultValue('filterNum', 10)">
            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
          </span>
        </div>
      </div>
      
      <div v-if="appForms.powerType==0">
        <div class="flexOuter">
        <div class="flexTitle">
          <span>{{ $t("qaTitleScoreThreshold") }}</span>
        </div>
        <div class="flexContent">
          <el-slider
            v-model="appForms.qaTitleScore"
            :min="0"
            :max="10"
            :step="0.1"
          ></el-slider>
          <el-input-number
            class="score-input"
            controls-position="right"
            v-model="appForms.qaTitleScore"
            :min="0"
            :max="10"
            :step="0.1"
            size="small"
            style="width: 80px"
          ></el-input-number>
          <span class="score-reset-btn" @click="setDefaultValue('qaTitleScore', 1.76)">
            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
          </span>
        </div>
      </div>
      <div class="flexOuter">
        <div class="flexTitle">
          <span>{{ $t("reRankingTitleScoreThreshold") }}</span>
        </div>
        <div class="flexContent">
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
            class="score-input"
            style="width: 80px"
          ></el-input-number>
          <span class="score-reset-btn" @click="setDefaultValue('qaRangeTitleScore', 0.91)">
            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
          </span>
        </div>
      </div>
      <div class="flexOuter">
        <div class="flexTitle">
          <span>{{ $t("qaBodyScoreThreshold") }}</span>
        </div>
        <div class="flexContent">
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
            class="score-input"
            style="width: 80px"
          ></el-input-number>
          <span class="score-reset-btn" @click="setDefaultValue('qaContentScore', 1.49)">
            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
          </span>
        </div>
      </div>
      <div class="flexOuter">
        <div class="flexTitle">
          <span>{{ $t("reRankingBodyAnswerScoreThreshold") }}</span>
        </div>
        <div class="flexContent">
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
            class="score-input"
            style="width: 80px"
          ></el-input-number>
          <span class="score-reset-btn" @click="setDefaultValue('qaRangeContentScore', 1.49)">
            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
          </span>
        </div>
      </div>
      <div class="flexOuter">
        <div class="flexTitle">
          <span>{{ $t("knowledgeBaseParagraphPreparationCount") }}</span>
        </div>
        <div class="flexContent" v-if="appForms.rearrangeModel=='yayi'">
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
            class="score-input"
            style="width: 80px"
          ></el-input-number>
          <span class="score-reset-btn" @click="setDefaultValue('prepareNum', 60)">
            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
          </span>
        </div>
        <div class="flexContent" v-else>
          <el-slider
            v-model="appForms.volcenginePrepareNum"
            :min="0"
            :max="100"
            :step="1"
          ></el-slider>
          <el-input-number
            controls-position="right"
            v-model="appForms.volcenginePrepareNum"
            :min="0"
            :max="100"
            :step="0.1"
            size="small"
            class="score-input"
            style="width: 80px"
          ></el-input-number>
          <span class="score-reset-btn" @click="setDefaultValue('volcenginePrepareNum', 60)">
            <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
          </span>
        </div>
      </div>
      </div>
      
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button
        type="primary"
        icon="el-icon-refresh"
        plain
        @click="useDefaultValue"
        style="
          float: left;
          color: #1c50fd;
          border-color: #1c50fd;
          background: none;
        "
        >{{ $t("useDefaultValue") }}
      </el-button>
      <el-button type="primary" @click="setConfig" style="float: right;">{{
        $t("confirm")
      }}</el-button>
      <el-button @click="cancelConfig" style="float: right;">{{ $t("cancel") }}</el-button>
      
    </span>
  </el-dialog>
</template>
    
<script>
export default {
  data() {
    return {
      appForms: {
        rearrangeModel: 'yayi',
        contentScore: 1.49,
        rangeContentScore: 1.49,
        qaTitleScore: 1.76,
        qaRangeTitleScore: 0.91,
        qaContentScore: 1.49,
        qaRangeContentScore: 1.49,
        filterNum: 10,
        prepareNum: 60,
        volcenginePrepareNum:60
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
      this.appForms.rearrangeModel = this.params.rearrangeModel || 'yayi';
      this.appForms.rangeContentScore = this.params.rangeContentScore;
      this.appForms.qaTitleScore = this.params.qaTitleScore;
      this.appForms.qaRangeTitleScore = this.params.qaRangeTitleScore;
      this.appForms.qaContentScore = this.params.qaContentScore;
      this.appForms.qaRangeContentScore = this.params.qaRangeContentScore;
      this.appForms.filterNum = this.params.filterNum;
      this.appForms.prepareNum = this.params.prepareNum;
      this.appForms.volcenginePrepareNum=this.params.volcenginePrepareNum;
      this.appForms.powerType= JSON.parse(sessionStorage.getItem("user")).powerType
      console.log(this.appForms);
      
      
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
      this.appForms.rearrangeModel = 'yayi';
      this.appForms.contentScore = 1.49;
      this.appForms.rangeContentScore = 1.49;
      this.appForms.qaTitleScore = 1.76;
      this.appForms.qaRangeTitleScore = 0.91;
      this.appForms.qaContentScore = 1.49;
      this.appForms.qaRangeContentScore = 1.49;
      this.appForms.filterNum = 10;
      this.appForms.prepareNum = 60;
      this.appForms.volcenginePrepareNum = 60;
    },
    setDefaultValue(key, value) {
      this.appForms[key] = value;
    }
  },
};
</script>
    
<style lang="scss" scoped>
.applicationDialog {
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
      padding: 10px 32px 20px;
      text-align: left;
      overflow: hidden;
    }
  }
}
.setting {
  .flexOuter {
    display: flex;
    // justify-content: flex-start;
    // align-items: center;
    flex-direction: column;
    width: 100%;
    margin: 10px 0px;
    .flexContent {
      display: flex;
      justify-content: flex-start;
      align-items: center;
    }

    .flexTitle{
      display: flex;
      align-items: center;
      gap: 5px;

      span{
        display: inline-block;
        height: 20px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #1D2129;
        line-height: 20px;
      }

      .icon-ctn{
        width: 20px;
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
    ::v-deep .el-slider__runway {
      width: 368px;
      margin-right: 8px;
    }
    span {
      display: inline-block;
      position: relative;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
    }
    :deep(.score-input) {
      width: 80px;
      margin-right: 8px;
      .el-input-number__decrease, .el-input-number__increase {
        width: 20px;
      }
      .el-input {
        .el-input__inner {
          padding-left: 8px;
          padding-right: 26px;
        }
      }
    }
    .score-reset-btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 32px;
      height: 32px;
      border-radius: 2px;
      margin-right: 0px;
      cursor: pointer;
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
    