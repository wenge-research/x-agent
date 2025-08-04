<template>
  <div class="app-container">
    <el-form :model="ruleForm" label-position="top" :hide-required-asterisk="true" :rules="rules" ref="ruleForm"
      class="form-box">
      <el-form-item prop="modelId">
        <template v-slot:label>
          <div class="label-box">
            请选择裁判模型
            <span class="red">*</span>
          </div>
        </template>
        <div class="select-wrap" :class="{ 'select-error-wrap': isErrorSelect }">
          <modelSelect v-if="!loading" :id="ruleForm.modelId" @change="modelChange"></modelSelect>
          <span v-if="isErrorSelect" class="error-info">请选择模型</span>
        </div>

      </el-form-item>
      <el-form-item class="textarea-wrap" label="裁判规则提示测" prop="cueWord">
        <template v-slot:label>
          <div class="label-box action-box">
            裁判规则提示词
            <div class="info-action">
              <!-- <el-button style="color: #1747E5;" size="small" type="text">
                <img style="width: 16px;height: 16px;" src="@/assets/svg/AI.svg" alt="">
                自动优化
              </el-button> -->
              <el-button style="color: #1D2129;" size="small" type="text" @click="openVocabularyDrawer">
                <svg style="width: 14px;height: 15px;" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                  <path fill-rule="nonzero" fill="#1D2129"
                    d="M5 8v12h14V8H5Zm0-2h14V4H5v2Zm15 16H4a1 1 0 0 1-1-1V3a1 1 0 0 1 1-1h16a1 1 0 0 1 1 1v18a1 1 0 0 1-1 1ZM7 10h4v4H7v-4Zm0 6h10v2H7v-2Zm6-5h4v2h-4v-2Z"
                    data-follow-fill="#848587" />
                </svg>
                提示词库
              </el-button>
              <el-button style="color: #1747E5;" size="small" :disabled="!(ruleForm.cueWord && ruleForm.modelId)"
                @click="shouRunDialog">
                <svg style="width: 15px;height: 14px;" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                  <path fill-rule="nonzero" fill="#1747E5"
                    d="M3 3h18a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Zm1 2v14h16V5H4Zm8 10h6v2h-6v-2Zm-3.333-3L5.838 9.172l1.415-1.415L11.495 12l-4.242 4.243-1.415-1.415L8.667 12Z"
                    data-follow-fill="#848587" />
                </svg>
                试运行
              </el-button>
            </div>
          </div>
        </template>
        <!-- <div class="info-content">
            {{ ruleForm.info }}
          </div> -->
        <div class="info-content">
          <el-input type="textarea" placeholder="请输入内容" maxlength="2000" show-word-limit resize="none"
            v-model="ruleForm.cueWord">
          </el-input>
        </div>


      </el-form-item>
    </el-form>




    <div class="btn-wrap">
      <el-button size="small" @click="nextPage">上一步</el-button>
      <el-button size="small" type="primary" @click="submit">提交</el-button>
    </div>


    <el-dialog title="试运行" :visible.sync="runVisible" width="70%" :before-close="handleClose">
      <div class="dialog-info">将从上传的评测集中随机选择3个样本测试</div>
      <el-table v-loading="tableLoading" :data="tableData" class="my-table">
        <el-table-column prop="input" label="input">
          <template slot-scope="scope">
            <el-popover placement="right-start" trigger="hover">
              <div class="dialog-popover-content">
                {{ scope.row.input }}
              </div>
              <span slot="reference" type="text">{{ scope.row.input }}</span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="referenceOutput" label="referemce_output">
          <template slot-scope="scope">
            <el-popover placement="right-start" trigger="hover">
              <div class="dialog-popover-content">
                {{ scope.row.referenceOutput }}
              </div>
              <span slot="reference" type="text">{{ scope.row.referenceOutput }}</span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="output" label="实际输出">
          <template slot-scope="scope">
            <el-popover placement="right-start" trigger="hover">
              <div class="dialog-popover-content">
                {{ scope.row.output }}
              </div>
              <span slot="reference" type="text">{{ scope.row.output }}</span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分">
          <template slot-scope="scope">
            <!-- <div>{{ scope.row.evaluationRule }}</div> -->
            <i v-if="runLoading" class="el-icon-loading" style="color: rgba(157, 164, 179, 1);"></i>
            <div v-else>
              <span v-if="scope.row.score" >{{ scope.row.score }}</span>
              <span v-else style="color: rgba(157, 164, 179, 1);">待测评</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="scoreReason" label="评分理由">
          <template slot-scope="scope">
            <i v-if="runLoading" class="el-icon-loading" style="color: rgba(157, 164, 179, 1);"></i>
            <div style="width: 100%;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;" v-else>
              <el-popover v-if="scope.row.scoreReason" placement="right-start" trigger="hover">
                <div class="dialog-popover-content">
                  {{ scope.row.scoreReason }}
                </div>
                <span slot="reference" type="text">{{ scope.row.scoreReason }}</span>
              </el-popover>
              <span v-else style="color: rgba(157, 164, 179, 1);">待测评</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="runVisible = false">取 消</el-button>
        <el-button type="primary" @click="runDate" :loading="runLoading"><i v-if="!runLoading"
            class="el-icon-caret-right" style="margin-right: 3px;"></i>运行</el-button>
      </span>
    </el-dialog>

    <!-- 提示词库 -->
    <vocabularyDrawer ref="vocabularyDrawer" @insertVocabulary="insertVocabularyFn" />
  </div>
</template>

<script>
import { mapActions } from "vuex";
import { apiRunApplicationModelId, apiSelectApplicationDateset } from "@/api/effectEvaluation";
import modelSelect from "@/components/ModelSelect.vue";
import vocabularyDrawer from './vocabularyDrawer.vue';
export default {
  name: 'addEvaluationRule',
  components: {
    modelSelect,
    vocabularyDrawer
  },
  props: {
    params: {
      type: Object
    }
  },
  data() {
    return {
      loading: false,
      isErrorSelect: false,
      ruleForm: {
        modelId: null,
        cueWord: null
      },
      runLoading: false,
      tableLoading: false,
      runVisible: false,
      tableData: [],
      rules: {
        modelId: [
          { required: true, message: '请选择裁判模型', trigger: 'change' }
        ],
        cueWord: [
          { required: true, message: '请输入裁判规则提示词', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.fetchModleOptions();
    this.ruleForm = {
      modelId: this.params.modelId ? this.params.modelId : null,
      cueWord: this.params.cueWord ? this.params.cueWord : null,
    }
  },
  methods: {
    ...mapActions(['fetchModleOptions']),
    runDate() {
      this.runLoading = true
      let params = {
        cueWord: this.ruleForm.cueWord,
        llmInfoId: this.ruleForm.modelId,
        datasetId: this.params.fileList[0].datasetId,
        applicationCode: this.params.applicationCode,
        applicationId: this.params.applicationId,
        applicationDataset: this.tableData
      }
      apiRunApplicationModelId(params).then(res => {
        this.runLoading = false
        if (res.code == '000000') {
          this.tableData = res.data
        }
      })
    },
    insertVocabularyFn(val) {
      // 判断插入到哪里
      this.ruleForm.cueWord = val
      console.log(val)
    },
    openVocabularyDrawer() {
      this.$refs.vocabularyDrawer.openDarwer()
    },
    // 显示关联模型
    filterModleName(item) {
      let findItem = this.$store.state?.workflow?.modleOptions.find((items) => items.modelId == item);
      return findItem?.modelName;
    },
    modelChange(val) {
      console.log(val)
      if (val) {
        this.isErrorSelect = false
      }
      this.ruleForm.modelId = val
      this.ruleForm.modelName = this.filterModleName(val)
    },
    handleClose() {
      this.tableData = []
      this.runLoading = false
      this.runVisible = false
    },
    shouRunDialog() {
      this.runVisible = true
      apiSelectApplicationDateset({ datasetId: this.params.fileList[0].datasetId }).then(res => {
        if (res.code == '000000') {
          if (res.data && res.data.length > 3) {
            this.tableData = res.data.slice(0, 3);
          } else {
            this.tableData = res.data || []
          }
        }
      })
    },
    submit() {
      this.$refs['ruleForm'].validate((val) => {
        if (val) {
          console.log(this.ruleForm)
          this.$emit('update', this.ruleForm)
          this.$emit('submit')
        }
      })
    },
    nextPage() {
      this.$emit('update', this.ruleForm)
      this.$emit('next', 'center')
    },
    cancelAdd() {
      this.$emit('cancel')
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  background: #fff;
  width: 80%;
  height: 100%;
  margin: 0 auto;
  padding: 24px 12%;
  border-radius: 8px;
  display: flex;
  flex-direction: column;

  ::v-deep .el-dialog {
    border-radius: 8px;

    .el-dialog__header {
      .el-dialog__headerbtn .el-dialog__close {
        color: #1D2129;
        font-size: 14px;
      }
    }

    .el-dialog__title {
      color: #1D2129;
      font-weight: 500;
    }

    .el-dialog__body {
      padding-top: 20px;
    }
  }

  .dialog-info {
    color: #1D2129;
    margin-bottom: 12px;
  }

  .form-box {
    // width: 75%;
    // height: 100%;
    width: 100%;
    flex: 1;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    // margin-bottom: 60px;

    ::v-deep .el-form-item__label {
      padding: 0;
      display: block;

      .label-box {
        color: #1D2129;
        font-weight: 500;
        line-height: 20px;
        margin-bottom: 8px;

        .red {
          color: #FF0000;
        }
      }

      .action-box {
        display: flex;
        width: 100%;
        justify-content: space-between;
        align-items: center;

        .info-action {
          display: flex;
          align-items: center;
        }

        .el-button--small {
          padding: 0;

          span {
            display: flex;
            align-items: center;

            img {
              margin-right: 4px;
            }

            svg {
              margin-right: 4px;
            }
          }
        }

        .el-button--default {
          padding: 6px 14px;
          background: #fff;
          border-color: #1747E5;
        }
      }
    }

    .textarea-wrap {
      flex: 1;
      display: flex;
      flex-direction: column;

      ::v-deep .el-form-item__content {
        flex: 1;
      }
    }


    .info-content {
      height: 100%;

      // white-space: pre-line;
      // width: 100%;
      // height: 360px;
      // overflow-x: auto;
      // line-height: 16px;
      // padding: 10px 0;
      // background-color: rgba(249,250,251);
      // margin-bottom: 20px;
      ::v-deep .el-textarea {
        height: 100%;

        textarea {
          height: 100%;
        }
      }
    }

    .example-wrap {
      // line-height: 16px;
      background: rgba(252, 252, 255);

      .title {
        font-size: 14px;
        // font-weight: 500;
        color: rgba(137, 142, 155);
      }

      .example-content {
        width: 100%;
        overflow: auto;

        .list {
          display: flex;
          align-items: center;
          width: 100%;
          overflow: auto;
          flex-wrap: nowrap;
          padding-bottom: 10px;

          .list-item {
            width: 24%;
            height: 120px;
            flex: 0 0 auto;
            border: 1px solid rgba(239, 240, 246);
            border-radius: 8px;
            box-sizing: border-box;
            padding: 12px;
            margin-right: 1%;
            background: #fff;

            .list-title {
              font-size: 14px;
              font-weight: 500;
              line-height: 20px;
              color: rgba(93, 92, 104);
              margin-bottom: 4px;
            }

            .list-content {
              font-size: 12px;
              color: rgba(93, 92, 104);
              line-height: 20px;
              overflow: hidden;
              display: -webkit-box;
              /* 使用WebKit兼容性写法 */
              -webkit-box-orient: vertical;
              /* 垂直排列盒子 */
              -webkit-line-clamp: 3;
            }
          }
        }
      }

    }
  }

  .btn-wrap {
    display: flex;
    justify-content: flex-end;
    // margin-right: 10%;
  }
}

::v-deep .my-table.el-table {
  .el-table__header {
    .el-table__cell {
      padding: 3px 0;
      border-right: 1px solid #fff;
      background: #F2F4F7;

      .cell {
        padding: 3px 12px;
        background: #F2F4F7;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 14px;
        color: #828894;
        line-height: 20px;
      }
    }
  }

  .el-button--text:not(.is-disabled) {
    // color: #1747E5;
  }

  // 斑马格
  .el-table__body {
    tr.el-table__row--striped td.el-table__cell {
      background: #F7F8FA;
    }
  }

  .select-wrap {
    position: relative;

    .error-info {
      position: absolute;
      bottom: -16px;
      left: 2px;
      color: rgb(245, 108, 108);
    }
  }

  .select-error-wrap {
    ::v-deep .model-setting-btn {
      border-color: rgb(245, 108, 108);
    }
  }
}
.el-popover__reference{
  // white-space: nowrap;
  // overflow: hidden;
  // text-overflow: ellipsis;
}
.dialog-popover-content{
  padding: 12px;
  width: 260px;
  font-size: 12px;
}
::v-deep .el-table .cell{
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

</style>