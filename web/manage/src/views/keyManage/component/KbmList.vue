<template>
  <div class="kbm-list outer-wrapper">
    <div class="header">
      <div class="title">
        {{ $t("secretKeyManagement") }}
      </div>
    </div>
    <div class="main">
      <div class="form-box">
        <div class="form">
          <el-input v-model="knowledgeName" size="small" :placeholder="$t('enterServiceName')" clearable
            @input="handleSearch">
            <i slot="suffix" class="el-input__icon el-icon-search" />
          </el-input>
        </div>
        <div class="option" v-permission="'crateKnowledge'">
          <el-button type="primary" @click="createKnowledge"><i class="el-icon-circle-plus" style="margin-right: 8px;"></i>新增密钥</el-button>
        </div>
      </div>
      <div class="table-box">
        <el-table width="100%" v-loading="tableLoading" :data="tableData" height="90%"
          :cell-class-name="setCellClassName" @cell-click="handleTableCellClick">
          <el-table-column prop="orgName" :label="$t('Unit/DepartmentName')" fit>
          </el-table-column>
          <el-table-column prop="name" :label="$t('personResponsible')" fit>
          </el-table-column>
          <el-table-column prop="mobile" :label="$t('contactInformation')" fit>
          </el-table-column>
          <el-table-column prop="expireTime" :label="$t('expirationDate')" fit sortable>
          </el-table-column>
          <el-table-column prop="createUser" :label="$t('founder')" fit>
          </el-table-column>
          <el-table-column prop="createTime" :label="$t('creationTime')" fit sortable>
          </el-table-column>
          <el-table-column prop="enableFlag" :label="$t('isEnabled')" fit>
            <template slot-scope="scope">
              <el-switch v-model="scope.row.enableFlag" active-value='1' inactive-value='0' active-color="#1747E5"
                inactive-color="#EAECF0" :width="24" :class="[scope.row.enableFlag == 1 ? 'switch-on' : 'switch-off']"
                @change="handleStatusChange(scope.row)">
              </el-switch>
              <span class="status-text">{{ scope.row.enableFlag == 1 ? $t('activeStatus') : $t('inactiveStatus')
              }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="qps" :label="$t('concurrentLimit')" width="180" sortable>
          </el-table-column>
          <el-table-column :label="$t('action')" width="200">
            <template slot-scope="scope">
              <div class="btns">
                <el-button type="text" @click="keyCopy(scope.row.secretKey)" ref="copyButton">复制密钥</el-button>
                <el-button type="text" @click="editKey(scope.row)">编辑</el-button>
                <el-button type="text" @click="delKey(scope.row)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <div class="total-num">
          {{ $t('totalData', { total: total, page: totalPage }) }}
        </div>
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="pageSize"
          :current-page="pageNo" :page-sizes="[20, 50, 100, 150, 200]" layout="prev, pager, next, sizes, jumper"
          background :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog :title="$t('tips')" :visible.sync="remVisible" width="400px" :modal-append-to-body="false"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false">
      <span>{{ $t("confirmDelete") }}【{{ remKnowledgeName }}】</span>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="remLoading" @click="remVisible = false">{{
          $t("cancel")
        }}</el-button>
        <el-button :loading="remLoading" type="primary" @click="handleRemKnowledage">{{ $t("confirm") }}</el-button>
      </span>
    </el-dialog>

    <el-dialog :title="isEdit ? '编辑密钥' : '新增密钥'" :visible.sync="createKbmVisible" width="560px"
      :modal-append-to-body="false" :close-on-click-modal="false" :close-on-press-escape="false" class="my-dialog">
      <div class="create-box">
        <el-form :model="setForm" ref="setForm" :rules="rules" width="100%">
          <el-form-item label="单位名称" prop="orgName">
            <el-input v-model="setForm.orgName" placeholder="单位名称" show-word-limit></el-input>
          </el-form-item>
          <el-form-item label="责任人" prop="name">
            <el-input v-model="setForm.name" placeholder="责任人" show-word-limit></el-input>
          </el-form-item>
          <el-form-item label="联系方式" prop="mobile">
            <el-input v-model="setForm.mobile" placeholder="联系方式" show-word-limit></el-input>
          </el-form-item>
          <el-form-item label="到期时间" prop="expireTime">
            <el-date-picker :placeholder="$t('enterKnowledgeBaseName')" v-model="setForm.expireTime" type="datetime"
              format="yyyy-MM-dd HH:mm:ss" value-format="yyyy-MM-dd HH:mm:ss" placeholder="YYYY-MM-DD">
            </el-date-picker>
          </el-form-item>
          <el-row>
            <el-col :span="8">
              <el-form-item label="并发限制" prop="qps">
                <el-input-number v-model="setForm.qps" controls-position="right" @change="handleChange"
                  :min="0"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div class="labels" v-if="selectedTag.length">
          <div class="label" v-for="(item, index) in selectedTag" :key="index">
            <span> {{ item }}</span>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="createKbmLoading" @click="cancelKeyInfo">{{ $t("cancel") }}</el-button>
        <el-button :loading="createKbmLoading" type="primary" @click="handleKbmSubmit">{{ $t("confirm") }}</el-button>
      </span>
    </el-dialog>

    <!-- 密钥信息弹窗 -->
    <el-dialog width="400px" :visible.sync="endKbmVisible" :modal-append-to-body="false" class="deleteDialog my-dialog"
      destroy-on-close :close-on-click-modal="false">
      <div class="keyInfo-box">
        <div class="img">
          <img src="../../../assets/images/success-pic.png" alt="">
        </div>
        <div class="text">密钥已生成</div>
        <div class="key">
          {{ currentSecretKey }}
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="createKbmLoading" @click="endKbmVisible = false">关闭</el-button>
        <el-button :loading="createKbmLoading" type="primary" @click="keyCopy(currentSecretKey)">复制密钥</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import ClipboardJS from 'clipboard';
import {
  updateModelPluginApiAuthUserList,
  getModelPluginApiAuthUserList,
  deleteModelPluginApiAuthUser,
  deleteKnowledgeInfo,
  addModelPluginApiAuthUser,
  getApplicationInfoList,
  apiGetDenseVectorList,
} from "@/api/index.js";
import { TAG_MAP } from "../content/index";
import GrantData from "@/views/appManage/components/GrantData.vue";
import { debounce } from 'lodash';
export default {
  components: {
    GrantData,
  },
  data() {
    return {
      TAG_MAP, // 可选标签
      tableData: [],
      tableLoading: true,
      tag: "",
      // newKnowledgeName: '',
      remKnowledgeName: "",
      knowledgeName: "", // 知识库名称
      remVisible: false,
      // anewVisible: false,
      knowledgeId: "",
      remLoading: false,
      // anewLoading: false,
      pageNo: 1,
      pageSize: 20,
      total: 0,
      totalPage: 0,
      // knowledgeRow: {},
      applicationId: "",
      applicationData: [],
      createKbmVisible: false,
      endKbmVisible: false,  //密钥已生产弹窗
      createKbmLoading: false,
      selectedTag: [], // 已选标签
      tagName: "", // 标签查询
      addTagVisible: false, // 标签视图
      setForm: {
        orgName: "", // 单位名称
        name: "", // 责任人
        mobile: "", // 联系电话
        expireTime: "", //过期时间
        qps: null, //并发限制
        secretKey: '',  //密钥
      },
      isEdit: false,  //是否编辑
      keyId: null,   //选中密钥id
      startDenseVectorId: "",
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
        orgName: [
          {
            required: true,
            message: '请填写单位名称',
            trigger: "blur",
          },
        ],
        name: [
          {
            required: true,
            message: '请填写责任人',
            trigger: "change",
          },
        ],
        mobile: [
          {
            required: true,
            message: '请填联系电话',
            trigger: "change",
          },
        ],
        expireTime: [
          {
            required: true,
            message: '请选择到期时间',
            trigger: "change",
          },
        ],
      },
      cleanFlag: this.$t("no"), // 是否清洗
      segmentLength: 21, // 长度
      subsection: [], // 分段标识
      cuttingMode: this.$t("smartSplitting"), // 切分方式 
      grantData: {},
      grantVisible: false,
      clipboardInstance: null, // 用于保存 Clipboard 实例
      currentSecretKey: '' // 用于存储当前要复制的密钥
    };
  },
  created() {
    this.getModelPluginApiAuthUserListData();
    this.getApplicationInfoListData();
    this.getDenseVectorList();
  },
  methods: {
    setCellClassName({ row, column, rowIndex, columnIndex }) {
      let isClick = ['knowledgeName', 'associatedApp', '', 'createTime', 'updateTime'].includes(column?.property)
      if (isClick) {
        return 'is-cell-link'
      }
      return ''
    },
    handleTableCellClick(row, column, cell, event) {
      let isClick = ['knowledgeName', 'associatedApp', '', 'createTime', 'updateTime'].includes(column?.property)
      if (isClick) {
        this.viewKnowledge(row)
      }
    },
    handleSearch: debounce(function () {
      this.getModelPluginApiAuthUserListData()
    }, 500),
    // 向量模型
    async getDenseVectorList() {
      const res = await apiGetDenseVectorList({ pageNo: 1, pageSize: 999 });
      if (res.code == "000000") {
        this.denseVectorIdList = res.data?.records || [];
        this.startDenseVectorId = res.data?.records.find(item => item.enableFlag === 1)?.vectorId || ''
      }
    },
    //并发数量加减
    handleChange(value) {
      console.log(value);
    },
    // 添加密钥
    async handleKbmSubmit() {
      this.createKbmLoading = true;
      let id = null;
      //走编辑的时候复制id传参
      if (this.isEdit) {
        id = this.keyId
      }
      const params = {
        ...this.setForm,
        id,
        type: 'api'
      };
      const res = await addModelPluginApiAuthUser(params).finally(() => {
        this.createKbmLoading = false;
      });
      if (res.code === "000000") {
        this.createKbmVisible = false;
        if(!this.isEdit) {
          this.endKbmVisible = true;
          this.currentSecretKey = res.data.secretKey;
        } else {
          this.currentSecretKey = res.secretKey;
        }
        this.$message({
          message: res.msg,
          type: "success",
        });
        this.getModelPluginApiAuthUserListData();
      } else {
        this.$message({
          message: res.msg,
          type: "error",
        });
      }
      this.createKbmLoading = false;
      this.isEdit = false;
      this.keyId = null;
      this.setForm = {
        orgName: "", // 单位名称
        name: "", // 责任人
        mobile: "", // 联系电话
        expireTime: "", //过期时间
        qps: null, //并发限制
        secretKey: '',  //密钥
      }
    },

    //编辑密钥
    editKey(row) {
      console.log(row)
      const data = { ...row };
      this.setForm.orgName = data.orgName;
      this.setForm.name = data.name;
      this.setForm.mobile = data.mobile;
      this.setForm.expireTime = data.expireTime;
      this.setForm.qps = data.qps;
      this.setForm.secretKey = data.secretKey;
      this.keyId = data.id;
      this.isEdit = true;
      this.createKbmVisible = true;
    },

    //复制密钥
    keyCopy(row) {
      const button = this.$refs.copyButton.$el
      // 创建 Clipboard 实例
      const clipboard = new ClipboardJS(button, {
        text: () => row // 直接使用当前的密钥
      })
      clipboard.on('success', () => {
        this.$message.success('密钥复制成功!')
        clipboard.destroy() // 复制成功后立即销毁实例
      })
      clipboard.on('error', (err) => {
        console.error('复制失败:', err)
        this.$message.error('密钥复制失败，请手动复制')
        clipboard.destroy()
      })
      button.click()
    },

    //删除密钥
    async delKey(row) {
      const params = [row.id]
      const res = await deleteModelPluginApiAuthUser(params).finally(() => {
        this.createKbmLoading = false;
      });
      if (res.code === "000000") {
        this.$message({
          message: res.msg,
          type: "success",
        });
        this.getModelPluginApiAuthUserListData();
      } else {
        this.$message({
          message: res.msg,
          type: "error",
        });
      }
    },
    //关闭信息弹窗
    cancelKeyInfo() {
      this.createKbmVisible = false;
      this.setForm = {
        orgName: "", // 单位名称
        name: "", // 责任人
        mobile: "", // 联系电话
        expireTime: "", //过期时间
        qps: null, //并发限制
        secretKey: '',  //密钥
      };
      this.isEdit = false;
      this.keyId = null;
    },
    createKnowledge() {
      this.isEdit = false;
      this.keyId = null;
      this.createKbmVisible = true;
      this.setForm.knowledgeName = "";
      this.setForm.introduce = "";
      this.setForm.denseVectorId = this.startDenseVectorId;
      this.setForm.documentAnalysisServer = "yayiAnalysis";
      this.setForm.id = "";
      this.setForm.knowledgeId = "";
      this.selectedTag = [];
      this.setForm = {}
    },
    viewKnowledge(row) {
      this.$emit("change-view", "kbmCreate", {
        type: "viewKnowledge",
        data: row,
      });
    },

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
        this.selectedTag = row.tagName.split(",");
      }
      if (row.cuttingMode !== "" && row.cuttingMode !== null) {
        this.cuttingMode = this.$t("customSplitStrategy");
        this.subsection = row.cuttingMode.split(",");
      } else {
        this.cuttingMode = this.$t("smartSplitting");
      }
    },

    handleClickTag(item) {
      this.tag = item;
    },
    handleTagCancel() {
      this.tagId = "";
      this.selectedTag = [];
      this.addTagVisible = false;
    },
    handleAddTag(item) {
      this.selectedTag.push(item);
    },
    handelRemTag(index) {
      this.selectedTag.splice(index, 1);
    },
    async handleRemKnowledage() {
      this.remLoading = true;
      const res = await deleteKnowledgeInfo([this.knowledgeId]);
      if (res.code === "000000") {
        this.remVisible = false;
        this.getModelPluginApiAuthUserListData();
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
      this.remLoading = false;
    },
    async getModelPluginApiAuthUserListData() {
      this.tableLoading = true;
      const { data } = await getModelPluginApiAuthUserList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        name: this.knowledgeName,
        applicationId: this.applicationId,
        order: "create_time",
        sort: "desc",
        type: 'api'
      });
      this.total = data.totalRow;
      this.totalPage = data.totalPage;
      this.tableData = data.records;
      this.tableLoading = false;
    },
    async handleStatusChange(row) {
      row.deleteFlag === '1' ? '0' : '1'
      const params = {
        id: row.id,
        enableFlag: row.enableFlag
      }
      await updateModelPluginApiAuthUserList(params)
      this.getModelPluginApiAuthUserListData();
    },
    handleCurrentChange(n) {
      this.pageNo = n;
      this.getModelPluginApiAuthUserListData();
    },
    handleSizeChange(n) {
      this.pageSize = n;
      this.getModelPluginApiAuthUserListData();
    },
    async getApplicationInfoListData() {
      const { data } = await getApplicationInfoList({
        pageNo: 1,
        pageSize: 999,
      });
      this.applicationData = data.records;
    },
    cancelGrant() {
      this.grantVisible = false;
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./Kbm.scss";

.tag-box {
  height: 240px;
  display: flex;

  .title {
    font-weight: blod;
    font-size: 14px;
    color: #768094;
    line-height: 18px;
    margin-bottom: 10px;
  }

  .waiting-box {
    flex: 1;
    border-right: 1px solid #e4e8ee;
    padding: 0 15px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    height: 100%;

    .labels {
      width: 100%;
      margin-top: 10px;
      flex-grow: 1;
      overflow-y: scroll;

      &::-webkit-scrollbar {
        width: 5px;
        height: 7px;
      }

      &::-webkit-scrollbar-thumb {
        border-radius: 5px;
        box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        background: #535353;
      }

      &::-webkit-scrollbar-track {
        border-radius: 5px;
        background: #fff;
        cursor: pointer;
      }

      .label {
        height: 28px;
        font-weight: 400;
        font-size: 14px;
        color: #636d91;
        line-height: 28px;
        display: flex;
        cursor: pointer;
        justify-content: space-between;
        align-items: center;
        padding: 0 12px 0px 8px;
        border-radius: 5px;
        margin-bottom: 5px;
      }

      .label:last-child {
        margin-bottom: 0;
      }

      .label:hover {
        background: #f2f7fc;
      }

      .label.active {
        background: #f2f7fc;
      }
    }
  }

  .selected-box {
    height: 100%;
    flex: 1;
    padding: 0 15px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;

    .labels {
      width: 100%;
      flex: 1;
      overflow-y: scroll;

      .label {
        display: inline-block;
        padding: 6px 8px;
        background: #f2f5fa;
        border-radius: 5px;
        margin: 5px;

        span {
          margin-right: 5px;
          font-weight: 400;
          font-size: 14px;
          color: #768094;
        }

        .el-button {
          padding: 0;
          color: #768094;
        }
      }
    }
  }
}



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

.kbm-list {
  width: 100%;
  height: 100%;
  position: absolute;
  // z-index: 1;
  display: flex;
  flex-direction: column;

  .header {
    .title {
      margin-bottom: 24px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 24px;
      color: #494E57;
      line-height: 40px;
      text-align: left;
      font-style: normal;
    }
  }

  .main {
    box-sizing: border-box;
    flex-grow: 1;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    background: #ffffff;
    overflow: hidden;

    .form-box {
      width: 100%;
      display: flex;
      justify-content: space-between;

      .form {
        display: flex;

        .el-input {
          width: 334px;

          ::v-deep .el-input__inner {
            height: 40px;
            line-height: 40px;
          }
        }

        ::v-deep .el-input__inner::-webkit-input-placeholder {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #B4BCCC;
          line-height: 20px;
        }

        ::v-deep .el-input__inner::-moz-placeholder {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #B4BCCC;
          line-height: 20px;
        }

        ::v-deep .el-input__inner:-ms-input-placeholder {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #B4BCCC;
          line-height: 20px;
        }

        ::v-deep .el-input__inner::placeholder {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #B4BCCC;
          line-height: 20px;
        }
      }
    }

    .table-box {
      margin-top: 24px;
      flex-grow: 1;
      overflow: hidden;

      ::v-deep .el-table .is-cell-link {
        cursor: pointer;
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
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #494E57;
            line-height: 20px;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 1;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .desc {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #B4BCCC;
            line-height: 16px;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 1;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
      }

      .btns {
        display: flex;
      }

      .el-button {
        padding: 0 5px;
      }

      .el-switch {
        height: 16px;

        ::v-deep .el-switch__core {
          height: 16px;

          &:after {
            top: 2px;
            width: 10px;
            height: 10px;
          }
        }

        &.switch-on {
          ::v-deep .el-switch__core {
            &:after {
              margin-left: -10px;
            }
          }
        }
      }

      .status-text {
        margin-left: 4px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #494E57;
        line-height: 20px;
      }
    }

  }
}

.keyInfo-box {
  display: flex;
  flex-direction: column;
  align-items: center;

  .img {}

  .text {
    font-size: 18px;
    color: #1D2129;
    margin-top: 18px;
  }

  .key {
    padding: 15px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 309px;
    // height: 56px;
    background-color: #f0f0f0;
    border-radius: 4px;
    margin-top: 20px;
    margin-bottom: 10px;
    font-weight: 400;
    font-size: 18px;
    color: #1D2129;
  }
}

.form-col {
  display: flex;
  flex-direction: column;
  align-items: flex-start;

  ::v-deep .el-form-item__content {
    width: 100%;
  }
}

.my-dialog {
  ::v-deep .el-dialog .el-dialog__body {
    padding: 16px 32px;
  }
}

::v-deep .el-input-number.is-controls-right .el-input__inner {
  display: block;
}
</style>