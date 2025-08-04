<template>
  <div :class="['kbm-create', isAppManage && widthSide == 240 ? 'kbm-create-fill' : '', isAppManage && widthSide != 240 ? 'kbm-create-min' : '']">
    <div class="header">
      <div class="header-left">
        <img v-if="!isAppManage" class="back-icon" src="@/assets/images/arrow-go-back-fill.svg" @click="hanldeCallBack">
        <img v-if="isAppManage" class="back-icon" src="@/assets/images/arrow-go-back-fill.svg" @click="clickBack">
        <div class="name-box">
          <img :src="require('@/assets/images/kbm.svg')" alt="">
          <div class="name-content">
            <div class="name">
              <div class="name-text">{{ rowData.knowledgeName || $t('createKnowledgeBase') }}</div>
              <img src="@/assets/images/edit-line2.svg" alt="" @click="setKnowledge(rowData)"> 
            </div>
            <div class="desc">{{ rowData.introduce || '' }}</div>
          </div>
        </div>
      </div>
      <el-tabs v-model="activeConfig" type="card">
        <el-tab-pane v-for="item in OPERATE_OPTIONS" v-if="permissions(item.code)" :label="item.label" :key="item.id" :name="item.id">
        </el-tab-pane>
      </el-tabs>
      <div class="header-right">
        <el-switch
          v-if="!isAppManage"
          v-model="rowData.status"
          :active-value="$t('yes')"
          :inactive-value="$t('no')"
          active-color="#1747E5"
          inactive-color="#EAECF0"
          :width="24"
          :class="[rowData.status == $t('yes') ? 'switch-on' : 'switch-off']"
          @change="handleStatusChange(rowData)"
        >
        </el-switch>
        <span v-if="!isAppManage" class="status-text">{{ rowData.status == $t('yes') ? $t('activeStatus') : $t('inactiveStatus') }}</span>
        <el-button v-if="isAppManage && showBtn" type="primary" :icon="isAddedApp?'el-icon-remove':'el-icon-plus'" @click="handleAddToApp">{{ isAddedApp ? '从应用中移除' : '添加到应用'}}</el-button>
      </div>
      <!-- <div class="title">{{ rowData.knowledgeName || $t('createKnowledgeBase') }}</div> -->
    </div>
    <div class="main">
      <!-- <div class="splits">
        <div class="mode" v-for="item in SPLIT_OPTIONS" :key="item.id">
          <el-radio v-model="cuttingMode" :label="item.label">
            <span class="label">{{ item.label }}</span>
          </el-radio>
          <div class="message">{{ item.message }}</div>
          <template v-if="item.id === 2 && cuttingMode === '自定义切分策略'">
            <div class="item">
              <div class="hint">分段标识</div>
              <el-select v-model="subsection" multiple filterable allow-create default-first-option placeholder="请选择分段标识">
                <el-option v-for="item in MARKING_OPTIONS" :key="item.id" :label="item.label" :value="item.con">
                </el-option>
              </el-select>
            </div>
            <div class="item">
              <div class="hint">分段长度</div>
              <el-slider v-model="segmentLength" show-input>
              </el-slider>
            </div>
            <div class="item">
              <div class="hint">自动清洗 <span>去除重复多余符号、空行、制表符</span></div>
              <el-switch v-model="cleanFlag" active-value="是" inactive-value="否" active-color="#13ce66"
                inactive-color="#ff4949">
              </el-switch>
            </div>
          </template>
        </div>
      </div> -->

      


      <div class="con">
        <QAView :knowledgeId="knowledgeId" v-if="['1'].includes(activeConfig)" />
        <DocumentView :knowledgeId="knowledgeId" v-if="['2'].includes(activeConfig)" />
        <UrlView :knowledgeId="knowledgeId" v-if="['3'].includes(activeConfig)" />
        <StructuringView :knowledgeId="knowledgeId" v-if="['5'].includes(activeConfig)" />
        <MultiMedia :knowledgeId="knowledgeId" v-if="['6'].includes(activeConfig)" />
        <YayiDocumentView :knowledgeId="knowledgeId" v-if="['7'].includes(activeConfig)" />
      </div>
    </div>
    <el-dialog
      :title="$t('editKnowledgeBase')"
      :visible.sync="createKbmVisible"
      width="560px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="my-dialog"
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
              maxlength="200"
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
        <div class="labels" v-if="selectedTag.length">
          
          <div class="label" v-for="(item, index) in selectedTag" :key="index">
            <span> {{ item }}</span>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          :loading="createKbmLoading"
          @click="createKbmVisible = false"
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
import { OPERATE_OPTIONS, } from '@/views/Kbm/content/index'
import QAView from '@/views/Kbm/component/QAView.vue'
import DocumentView from '@/views/Kbm/component/DocumentView.vue'
import UrlView from '@/views/Kbm/component/UrlView.vue'
import StructuringView from '@/views/Kbm/component/StructuringView.vue'
import MultiMedia from '@/views/Kbm/component/MultiMedia.vue'
import YayiDocumentView from "@/views/Kbm/component/YayiDocumentView.vue";
import {
  updateStatus,
  apiGetDenseVectorList,
  addKnowledgeInfo,
  getKnowledgeInfoList,

} from "@/api/index.js";

export default {
  props: {
    rowData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    type: {
      type: String,
      default: ''
    },
    isAppManage: {
      type: Boolean,
      default: true
    },
    showBtn: {
      type: Boolean,
      default: true
    }
  },
  components: {
    YayiDocumentView,
    QAView,
    DocumentView,
    UrlView,
    StructuringView,
    MultiMedia
  },
  data() {
    return {
      OPERATE_OPTIONS, // 操作类型
      knowledgeId: '', // 知识库id
      activeConfig: '1',
      createKbmVisible: false,
      createKbmLoading: false,
      setForm: {
        knowledgeName: "", // 知识库名称
        introduce: "", // 知识库描述
        id: "", // 数据id
        knowledgeId: "",
        denseVectorId: "",
        documentAnalysisServer: "yayiAnalysis", // 文档解析策略
      },
      segmentLength: 21, // 长度
      selectedTag: [], // 已选标签
      cuttingMode: this.$t("smartSplitting"), // 切分方式 
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
  computed: {
    widthSide() {
      console.log(33333,6666)
      return this.$store.state.tab.widthSide
    },
    isAddedApp() {
      return this.$store.state.tab.isAddedToApp
    }
  },
  watch: {
    // isCollapse(val) {
    //   console.log(3444, val)
    //   this.widthSide = val
    // }
  },
  created() {
    this.knowledgeName = this.rowData.knowledgeName
    this.knowledgeId = this.rowData.knowledgeId
    this.getDenseVectorList()
  },
  methods: {
    handleAddToApp(){
      const isAdded = this.$store.state.tab.isAddedToApp
      console.log(isAdded,'isAdded')
      if(isAdded) {
        this.$EventBus.$emit("deleteKnowledgeToApp", this.rowData);
        this.$store.commit('setIsAddedToApp', false);
        this.$message({
          message: '移除成功',
          type: "success",
        });
      }else {
        this.$EventBus.$emit("addKnowledgeToApp", this.rowData);
        this.$store.commit('setIsAddedToApp', true);
        this.$message({
          message: '添加成功',
          type: "success",
        });
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
      const res = await addKnowledgeInfo(params).finally(() => {
        this.createKbmLoading = false;
      });
      if (res.code === "000000") {
        this.createKbmVisible = false;
        this.getKnowledgeInfoListData();
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
    async getKnowledgeInfoListData() {
      const { data } = await getKnowledgeInfoList({
        pageNo: 1,
        pageSize: 10,
        knowledgeName: '',
        applicationId: '',
        knowledgeId: this.knowledgeId,
        order: "create_time",
        sort: "desc"
      })
      let rowData = {}
      rowData = data?.records[0] || {}
      console.log(data)
      this.$emit("update:rowData", rowData)
    },
    // 向量模型
    async getDenseVectorList() {
      const res = await apiGetDenseVectorList({pageNo: 1, pageSize: 999});
      if (res.code == "000000") {
        this.denseVectorIdList = res.data?.records || [];
      }
    },
    // 编辑知识库
    setKnowledge(row) {
      this.createKbmVisible = true;
      this.setForm.knowledgeName = row.knowledgeName;
      this.setForm.introduce = row.introduce;
      this.setForm.id = row.id;
      this.setForm.knowledgeId = row.knowledgeId;
      this.setForm.denseVectorId = row.denseVectorId;
      this.setForm.documentAnalysisServer = row.documentAnalysisServer;
      this.segmentLength = row.segmentLength;
      if (row.tagName !== "" && row.tagName !== null) {
        this.selectedTag = row.tagName.split(",").filter(item => item!== 'undefined');
      }
      if (row.cuttingMode !== "" && row.cuttingMode !== null) {
        this.cuttingMode = this.$t("customSplitStrategy");
        this.subsection = row.cuttingMode.split(",");
      } else {
        this.cuttingMode = this.$t("smartSplitting");
      }
    },
    async handleStatusChange(row) {
      await updateStatus({
        knowledgeId: row.knowledgeId,
        status: row.status,
      });
    },
    hanldeCallBack() {
      this.$emit('change-view', 'kbmList', null);
    },
    permissions(code) {
    const permissionButton = this.getUserPermissions();
    const item = permissionButton.find(n => n.menuCode === code);
    return !!item;
  },
  getUserPermissions(){
    const permission = JSON.parse(sessionStorage.getItem('permission'))
    const permissionButton = [];
    permission.forEach(item => {
      if (item.children && Array.isArray(item.children)) {
        item.children.forEach(child => {
          permissionButton.push({
            isHidden: child.isHidden,
            menuCode: child.menuCode
          });
        });
      }
    });
    return permissionButton
  },
  clickBack() {
    this.$emit('clickBack', false)
  }
  }
}

</script>

<style lang="scss" scoped>
@import "./Kbm.scss";

.kbm-create {
  width: 100%;
  height: 100%;
  padding-top: 0;
  position: absolute;
  z-index: 2;
  display: flex;
  flex-direction: column;
  background: #FFFFFF;

  .header {
    width: 100%;
    height: 80px;
    padding: 16px 32px;
    border-bottom: 1px solid rgba(0,0,0,0.12);
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-shrink: 0;
    .header-left {
      max-width: 30%;
      display: flex;
      align-items: center;
      flex-shrink: 0;
      .back-icon {
        width: 20px;
        margin-right: 8px;
        cursor: pointer;
      }

      .title {
        font-size: 18px;
        font-weight: bold;
        margin-left: 10px;
      }
      .name-box {
        display: flex;
        align-items: center;
        img {
          width: 40px;
          height: 40px;
          margin-right: 12px;
          border-radius: 2px;
        }
        .name-content {
          .name {
            margin-bottom: 4px;
            display: flex;
            align-items: center;
            justify-content: flex-start;
            .name-text {
              width: 96%;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 18px;
              color: #494E57;
              line-height: 24px;
              display: -webkit-box;
              -webkit-box-orient: vertical;
              -webkit-line-clamp: 1;
              overflow: hidden;
              text-overflow: ellipsis;
            }
            img {
              width: 14px;
              margin-left: 10px;
              cursor: pointer;
            }
          }
          .desc {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #B4BCCC;
            line-height: 20px;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 1;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
      }
    }
    
    ::v-deep .el-tabs {
      .el-tabs__header {
        margin-bottom: 0;
        border: none;
        .el-tabs__nav {
          border: 0px;
          height: 40px;
          background: #F7F8FA;
          border-radius: 4px;
          padding: 2px;
        }
        .el-tabs__item {
          // min-width: 96px;
          height: 36px;
          line-height: 36px;
          border-radius: 2px;
          padding: 0 24px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 16px;
          color: #828894;
          border: none;
          
          &.is-active {
            background: #FFFFFF;
            box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
            font-weight: 500;
            color: #494E57;
          }
          &:hover {
            background: rgba(188,193,204, 0.2);
          }
        }
      }
    }
    .header-right {
      .status-text {
        margin-left: 10px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #494E57;
        line-height: 20px;
      }
    }
  }

  .main {
    box-sizing: border-box;
    flex-grow: 1;
    width: 100%;
    height: 100%;
    // padding: 10px 20px;
    display: flex;
    border-radius: 4px;
    // border: 1px solid #E1E4EB;
    background: #FFFFFF;
    overflow: hidden;
    flex-direction: column;

    .options {
      width: 100%;
      display: flex;
      margin-bottom: 10px;

      .option {
        cursor: pointer;
        height: 40px;
        border-radius: 4px;
        border: 1px solid #E1E4EB;
        margin-right: 14px;
        font-weight: bold;
        font-size: 14px;
        white-space: nowrap;
        box-sizing: border-box;
        color: #768094;
        line-height: 40px;
        text-align: center;
        padding: 0 20px;
      }

      .option.active {
        background: rgba(28, 80, 253, 0.1);
        border: 1px solid #1C50FD;
      }

      .option:last-child {
        margin-right: 0;
      }
    }

    .con {
      flex-grow: 1;
      height: 100%;
      overflow: hidden;
    }

    // .splits {
    //   .mode {
    //     width: 100%;
    //     border-radius: 4px;
    //     border: 1px solid #E1E4EB;
    //     margin-bottom: 12px;
    //     padding: 12px 20px;
    //     box-sizing: border-box;

    //     .radio {
    //       display: flex;
    //       align-items: center;
    //     }

    //     .label {
    //       font-weight: bold;
    //       font-size: 16px;
    //       color: #383D47;
    //       line-height: 24px;
    //     }

    //     .message {
    //       font-weight: 400;
    //       font-size: 14px;
    //       color: #768094;
    //       line-height: 20px;
    //       margin-top: 5px;
    //       margin-bottom: 12px;
    //     }

    //     .item {
    //       // display: flex;

    //       .hint {
    //         font-size: 16px;
    //         color: #768094;
    //         line-height: 24px;
    //         font-weight: 700;
    //         margin: 5px 0;

    //         span {
    //           font-weight: 400;
    //           font-size: 14px;
    //           color: #768094;
    //           line-height: 20px;
    //         }
    //       }
    //     }

    //     .el-select {
    //       width: 100%;
    //     }
    //   }
    // }
  }
}
.iconfont {
  font-size: 16px;
  color: #848587;
  cursor: pointer;
}

.kbm-create-fill {
  width: 100%;
  ::v-deep .el-dialog {
    .el-dialog__footer {
      text-align: right !important;
    }
  }
}
.kbm-create-min {
  width: 100%;
  ::v-deep .el-dialog {
    .el-dialog__footer {
      text-align: right !important;
    }
  }
}

</style>
