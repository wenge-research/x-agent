<template>
  <div>
    <el-dialog
      :title="$t('createKnowledgeBase')"
      :visible.sync="createKbmVisible"
      width="560px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="my-dialog"
      append-to-body
      destroy-on-close
      :before-close="cancelDialog"
    >
      <div class="create-box">
        <el-form :model="setForm" ref="setForm" :rules="rules">
          <el-form-item :label="$t('knowledgeBaseName')" prop="knowledgeName">
            <el-input
              v-model="setForm.knowledgeName"
              :placeholder="$t('enterKnowledgeBaseName')"
              maxlength="50"
              show-word-limit
            ></el-input>
          </el-form-item>
          <el-form-item :label="$t('knowledgeBaseDescription')">
            <el-input
              size="small"
              type="textarea"
              :autosize="{ minRows: 6, maxRows: 4 }"
              :placeholder="$t('enterKnowledgeBaseDescription')"
              v-model="setForm.introduce"
              maxlength="2000"
              show-word-limit
            >
            </el-input>
          </el-form-item>
          <!-- <el-form-item
            :label="$t('vectorModel')"
            prop="denseVectorId"
            class="form-col"
          >
            <el-select v-model="setForm.denseVectorId" style="width: 100%" :disabled="setForm.id != ''">
              <el-option
                v-for="item in denseVectorIdList"
                :key="item.vectorId"
                :label="item.vectorName"
                :value="item.vectorId"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            :label="$t('documentParsingStrategy')"
            prop="documentAnalysisServer"
            class="form-col"
          >
            <el-select v-model="setForm.documentAnalysisServer" style="width: 100%">
              <el-option
                v-for="item in documentAnalysisServerList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item> -->
        </el-form>
        <!-- <el-button
          type="primary"
          size="small"
          icon="el-icon-circle-plus"
          @click="addTagVisible = true"
          >{{ $t("addTag") }}</el-button
        > -->
        <!-- <div class="labels" v-if="selectedTag.length">
          <div class="label" v-for="(item, index) in selectedTag" :key="index">
            <span> {{ item }}</span>
          </div>
        </div> -->
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          :loading="createKbmLoading"
          @click="cancelDialog"
          >{{ $t("cancel") }}</el-button
        >
        <el-button
          :loading="createKbmLoading"
          type="primary"
          @click="handleKbmSubmit"
          >{{ $t("confirm") }}</el-button
        >
      </span>
    </el-dialog>

  </div>
</template>
<script>
import {
  addKnowledgeInfo,
  apiGetDenseVectorList,
} from "@/api/index.js";
import { mapActions } from 'vuex';
export default {
  props: {
    createKbmVisible: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      setForm: {
        knowledgeName: "", // 知识库名称
        introduce: "", // 知识库描述
        id: "", // 数据id
        knowledgeId: "",
        denseVectorId: "",
        documentAnalysisServer: "yayiAnalysis", // 文档解析策略
      },
      createKbmLoading: false,
      denseVectorIdList: [],
      documentAnalysisServerList: [
        {
          label: this.$t('yayiIntelligentAnalysis'),
          value: 'yayiAnalysis'
        },
        {
          label: this.$t('alibabaCloudPolicyAnalysis'),
          value: 'policy-aliyun'
        },
        {
          label: this.$t('localDeploymentAnalysis'),
          value: 'local-depoly'
        },
      ],
      rules: {
        knowledgeName: [
          {
            required: true,
            message: this.$t('enterKnowledgeBaseName'),
            trigger: "blur",
          },
        ],
        denseVectorId: [
          {
            required: true,
            message: this.$t('pleaseChooseAVectorModel'),
            trigger: "change",
          },
        ],
        documentAnalysisServer: [
          {
            required: true,
            message: this.$t('pleaseSelectADocumentParsingStrategy'),
            trigger: "change",
          },
        ],
      },
    }
  },
  mounted() {
    this.getDenseVectorList();
  },
  methods: {
    ...mapActions(['fetchKnowledgeList']),
    async getDenseVectorList() {
      const res = await apiGetDenseVectorList({pageNo: 1, pageSize: 999});
      if (res.code == "000000") {
        this.denseVectorIdList = res.data?.records || [];
        this.setForm.denseVectorId = res.data?.records.find(item => item.enableFlag === 1)?.vectorId || ''
      }
    },
    handleKbmSubmit() {
      this.$refs.setForm.validate(async (valid) => {
        if (valid) {
          const extraParams = {};
          if (this.cuttingMode === this.$t("customSplitStrategy")) {
            extraParams.cuttingMode = String(this.subsection);
            extraParams.segmentLength = this.segmentLength;
            extraParams.cleanFlag = this.cleanFlag;
          }
          extraParams.tagName = String(this.selectedTag);
          const params = {
            ...this.setForm,
            ...extraParams,
          };
          await this.initKnowlegeBase(params);
        }
      });
    },
    // 创建/编辑知识库
    async initKnowlegeBase(params) {
      this.createKbmLoading = true;
      const res = await addKnowledgeInfo(params);
      if (res.code === "000000") {
        this.$emit("submitDialog", params);
        this.$EventBus.$emit("updateKnowledgeAllList");
        this.fetchKnowledgeList();
        // this.knowledgeId = res.data.knowledgeId
        this.$message({
          message: res.msg,
          type: "success",
        });
      } else {
        this.$message({
          message: res.msg,
          type: "error",
        });
      }
      this.createKbmLoading = false;
    },
    cancelDialog() {
      this.$emit("cancelDialog");
    }
  }

}
</script>
<style lang="scss" scoped>
@import "../../Kbm/component/Kbm.scss";
.create-box {
  .labels {
    margin-top: 10px;
    .label {
      display: inline-block;
      padding: 6px 8px;
      background: #f2f5fa;
      border-radius: 5px;
      font-weight: 400;
      font-size: 14px;
      color: #768094;
      margin-right: 5px;
      margin-bottom: 5px;
    }
  }
}

</style>