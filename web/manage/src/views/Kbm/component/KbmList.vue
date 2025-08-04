<template>
  <div class="kbm-list outer-wrapper">
    <div class="header">
      <div class="title">
        {{ $t("kbm") }}
      </div>
    </div>
    <div class="main">
      <div class="form-box">
        <div class="form">
          <el-select
            clearable
            v-model="applicationId"
            filterable
            :placeholder="$t('selectApplication')"
            style="width: 200px; margin-right: 16px"
            @change="getKnowledgeInfoListData"
          >
            <el-option
              v-for="item in applicationData"
              :key="item.applicationId"
              :label="item.applicationName"
              :value="item.applicationId"
            >
            </el-option>
          </el-select>
          <el-input
            v-model="knowledgeName"
            size="small"
            :placeholder="$t('enterKnowledgeBaseName')"
            clearable
            @input="handleSearch"
          >
            <i slot="suffix" class="el-input__icon el-icon-search" />
          </el-input>
          <!-- <div style="margin-left: 10px"></div>
          <el-select
            clearable
            v-model="applicationId"
            size="small"
            filterable
            :placeholder="$t('selectApplication')"
          >
            <el-option
              v-for="item in applicationData"
              :key="item.applicationId"
              :label="item.applicationName"
              :value="item.applicationId"
            >
            </el-option>
          </el-select>
          <div style="margin-left: 10px"></div>
          <el-button
            type="primary"
            size="small"
            @click="getKnowledgeInfoListData"
            >{{ $t("search") }}</el-button
          > -->
        </div>
        <div class="flex">
          <el-button
            style="margin-right: 10px"
            @click="openStatistical"
            ><i class="el-icon-pie-chart" style="margin-right: 8px;"></i>{{ $t("statisticalAnalysis") }}</el-button
          >
          <div class="option" v-permission="'crateKnowledge'">
            <el-button
              type="primary"
              @click="createKnowledge"
              ><i class="el-icon-circle-plus" style="margin-right: 8px;"></i>{{ $t("createKnowledgeBase") }}</el-button
            >
          </div>
        </div>
      </div>
      <div class="table-box">
        <el-table
          v-loading="tableLoading"
          :data="tableData"
          height="100%"
          class="my-table"
          :cell-class-name="setCellClassName"
          @cell-click="handleTableCellClick"
        >
          <el-table-column
            prop="knowledgeName"
            :label="$t('knowledgeBaseName')"
            min-width="120"
          >
            <template slot-scope="scope">
              <div class="name-box">
                <img :src="require('@/assets/images/kbm.svg')" alt="" />
                <div class="name-content">
                  <div class="name">
                    {{ scope.row.knowledgeName }}
                    <div class="preset" v-if="scope.row.ownerType=='official'&&isAdmin">预置</div>
                    <div class="preset" v-if="!isAdmin&&scope.row.granted">授权</div>
                  </div>
                  <div class="desc" :title="scope.row.introduce">
                    {{ scope.row.introduce }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <!-- <el-table-column
            prop="denseVectorName"
            :label="$t('vectorModel')"
          >
          </el-table-column> -->
          <el-table-column
            prop="associatedApp"
            :label="$t('relationApplication')"
            width="120"
          >
          </el-table-column>
          <!-- <el-table-column
            prop="knowledgeId"
            :label="$t('knowledgeBaseId')"
          >
          </el-table-column> -->

          <el-table-column prop="denseVectorName" :label="$t('vectorModel')">
          </el-table-column>
          <el-table-column prop="createUser" :label="$t('founder')" width="120">
          </el-table-column>
          <el-table-column
            prop="createTime"
            :label="$t('creationTime')"
            width="180"
            sortable
          >
          </el-table-column>
          <el-table-column
            prop="updateTime"
            :label="$t('lastUpdateTime')"
            width="180"
            sortable
          >
          </el-table-column>
          <el-table-column prop="status" :label="$t('isEnabled')" width="120">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="$t('yes')"
                :inactive-value="$t('no')"
                active-color="#1747E5"
                inactive-color="#EAECF0"
                :width="24"
                :class="[scope.row.status == $t('yes') ? 'switch-on' : 'switch-off']"
                @change="handleStatusChange(scope.row)"
              >
              </el-switch>
              <span class="status-text">{{
                scope.row.status == $t("yes") ? $t("activeStatus") : $t("inactiveStatus")
              }}</span>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('action')" width="60">
            <template slot-scope="scope">
              <div class="opts-box" v-if="isAdminOrUser(scope.row)">
                <el-dropdown
                  trigger="click"
                  @command="(value) => handleCommand(value, scope.row)"
                  placement="top-end"
                  class="opts-box-dropdown"
                >
                  <span class="el-dropdown-link">
                    <!-- <i
                      style="transform: rotate(90deg); color: #828894"
                      class="el-icon-more"
                    ></i> -->
                    <iconpark-icon name="more-line" size="18" color="#383838" style="margin-top:2px;"></iconpark-icon>
                  </span>
                  <!-- api接入详情 -->
                  <el-dropdown-menu
                    slot="dropdown"
                    class="opts-box-dropdown-menu"
                  >
                    <el-dropdown-item
                      v-permission="'presetApp'"
                      command="presetApp"
                      v-if="scope.row.ownerType!='official'&&isAdmin"
                      style="display: block;"
                    >
                    <iconpark-icon color="#494E57" name="share-box-line"></iconpark-icon>
                    <span style="color: #494E57">{{ "设为预置" }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-permission="'presetApp'"
                      command="presetApp"
                      v-else-if="scope.row.ownerType=='official'&&isAdmin"
                      style="display: block;"
                    >
                    <iconpark-icon color="#F53F3F" name="share-box-line"></iconpark-icon>
                    <span style="color: #F53F3F">{{ "取消预置" }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      command="editView"
                      style="display: block;"
                    >
                    <iconpark-icon color="#494E57" name="edit-box-line"></iconpark-icon>
                    <span style="color: #494E57">{{ $t("edit") }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-permission="'deleteApp'"
                      command="deleteApp"
                    >
                    <iconpark-icon color="#494E57" name="delete-bin-4-line"></iconpark-icon>
                    <span style="color: #494E57">{{ $t("delete") }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-permission="'grantApp'"
                      command="grantApp"
                    >
                      <iconpark-icon color="#494E57" name="user-add-line"></iconpark-icon>
                      <span style="color: #494E57">{{ $t("authorization") }}</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
              
              <!-- <div class="btns">
                <el-button
                  type="text"
                  @click="setKnowledge(scope.row)"
                  v-permission="'editKnowledge'"
                  >{{ $t("edit") }}</el-button
                >
                <el-button
                  type="text"
                  @click="viewKnowledge(scope.row)"
                  v-permission="'searchKnowledge'"
                  >{{ $t("view") }}</el-button
                >
                <el-button
                  type="text"
                  @click="remKnowledage(scope.row)"
                  v-permission="'deleteKnowledge'"
                  >{{ $t("delete") }}</el-button
                >
                <el-button
                  type="text"
                  @click="grantKnowledge(scope.row)"
                  v-permission="'grantKnowledge'"
                  >{{ $t("authorization") }}</el-button
                >
              </div> -->
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <div class="total-num">
          {{ $t("totalData", { total: total, page: totalPage }) }}
        </div>
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-size="pageSize"
          :current-page="pageNo"
          :page-sizes="[20, 50, 100, 150, 200]"
          layout="prev, pager, next, sizes, jumper"
          background
          :total="total"
        >
        </el-pagination>
      </div>
    </div>

    <el-dialog
      :title="$t('tips')"
      :visible.sync="remVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
    >
      <span>{{ $t("confirmDelete") }}【{{ remKnowledgeName }}】</span>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="remLoading" @click="remVisible = false">{{
          $t("cancel")
        }}</el-button>
        <el-button :loading="remLoading" type="primary" @click="handleRemKnowledage">{{
          $t("confirm")
        }}</el-button>
      </span>
    </el-dialog>

    <!-- <el-dialog title="请输入知识库名称" :visible.sync="anewVisible" width="30%" :modal-append-to-body="false"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false">
      <el-input v-model="newKnowledgeName" size="small" placeholder="请输入知识库名称"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="anewLoading" @click="anewVisible = false">{{ $t('cancel') }}</el-button>
        <el-button :loading="anewLoading" type="primary" @click="handleAnewKnowledge">{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog> -->

    <el-dialog
      :title="$t('createKnowledgeBase')"
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
        <el-button :loading="createKbmLoading" @click="createKbmVisible = false">{{
          $t("cancel")
        }}</el-button>
        <el-button :loading="createKbmLoading" type="primary" @click="handleKbmSubmit">{{
          $t("confirm")
        }}</el-button>
      </span>
    </el-dialog>

    <!-- 添加标签 -->
    <el-dialog
      :title="$t('knowledgeBaseTags')"
      width="600px"
      :visible.sync="addTagVisible"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
    >
      <div class="tag-box">
        <div class="waiting-box">
          <div class="title">{{ $t("selectOrAddCustomTag") }}</div>
          <el-input v-model="tagName" size="small" :placeholder="$t('enterTagName')">
            <i slot="suffix" class="el-input__icon el-icon-search" />
          </el-input>
          <div class="labels">
            <div
              class="label"
              v-for="(item, index) in TAG_MAP"
              :key="index"
              :class="{ active: item === tag || selectedTag.includes(item) }"
              @click="handleClickTag(item)"
            >
              <span> {{ item }}</span>
              <el-button
                type="text"
                @click="handleAddTag(item)"
                v-if="item === tag && !selectedTag.includes(item)"
                >{{ $t("add") }}</el-button
              >
            </div>
          </div>
        </div>
        <div class="selected-box">
          <div class="title">{{ $t("existingTags") }}</div>
          <div class="labels">
            <div class="label" v-for="(item, index) in selectedTag" :key="index">
              <span> {{ item }}</span>
              <el-button
                type="text"
                icon="el-icon-close"
                @click="handelRemTag(index)"
              ></el-button>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="handleTagCancel">{{ $t("cancel") }}</el-button>
        <el-button type="primary" @click="addTagVisible = false">{{
          $t("confirm")
        }}</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="$t('authorization')"
      width="600px"
      top="10vh"
      :visible.sync="grantVisible"
      :modal-append-to-body="false"
      class="deleteDialog my-dialog"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <GrantData
        :data-id="grantData.knowledgeId"
        v-if="grantVisible"
        data-type="knowledge"
        @cancelGrant="cancelGrant"
        :queryCurrentTenantUserFlag="true"
      ></GrantData>
    </el-dialog>
  </div>
</template>

<script>
import {
  getKnowledgeInfoList,
  updateStatus,
  deleteKnowledgeInfo,
  addKnowledgeInfo,
  getApplicationInfoList,
  apiGetDenseVectorList,
} from "@/api/index.js";
import {knowledgeInfoPreset} from "@/api/app"
import { TAG_MAP } from "../content/index";
import GrantData from "@/views/appManage/components/GrantData.vue";
import { debounce } from "lodash";
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
      createKbmLoading: false,
      selectedTag: [], // 已选标签
      tagName: "", // 标签查询
      addTagVisible: false, // 标签视图
      setForm: {
        knowledgeName: "", // 知识库名称
        introduce: "", // 知识库描述
        id: "", // 数据id
        knowledgeId: "",
        denseVectorId: "",
        documentAnalysisServer: "yayiAnalysis", // 文档解析策略
      },
      startDenseVectorId: "",
      denseVectorIdList: [],
      documentAnalysisServerList: [
        {
          label: this.$t("yayiIntelligentAnalysis"),
          value: "yayiAnalysis",
        },
        {
          label: this.$t("alibabaCloudPolicyAnalysis"),
          value: "policy-aliyun",
        },
        {
          label: this.$t("localDeploymentAnalysis"),
          value: "local-depoly",
        },
      ],
      rules: {
        knowledgeName: [
          {
            required: true,
            message: this.$t("enterKnowledgeBaseName"),
            trigger: "blur",
          },
        ],
        denseVectorId: [
          {
            required: true,
            message: this.$t("pleaseChooseAVectorModel"),
            trigger: "change",
          },
        ],
        documentAnalysisServer: [
          {
            required: true,
            message: this.$t("pleaseSelectADocumentParsingStrategy"),
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
    };
  },
  computed:{
    isAdmin(){
      return JSON.parse(sessionStorage.getItem('user')).powerType==0
    },
    isAdminOrUser(){
      return (data)=>{
        let obj=JSON.parse(sessionStorage.getItem("user"))
        return this.isAdmin || obj.accountName == data.createUser
      }
    }
  },
  created() {
    this.getKnowledgeInfoListData();
    this.getApplicationInfoListData();
    this.getDenseVectorList();
  },
  methods: {
    openStatistical() {
      this.$emit("showStatisticalPage", true);
    },
    setCellClassName({ row, column, rowIndex, columnIndex }) {
      let isClick = [
        "knowledgeName",
        "associatedApp",
        "",
        "createTime",
        "updateTime",
      ].includes(column?.property);
      if (isClick) {
        return "is-cell-link";
      }
      return "";
    },
    handleTableCellClick(row, column, cell, event) {
      let isClick = [
        "knowledgeName",
        "associatedApp",
        "",
        "createTime",
        "updateTime",
      ].includes(column?.property);
      if (isClick) {
        this.viewKnowledge(row);
      }
    },
    handleSearch: debounce(function () {
      this.getKnowledgeInfoListData();
    }, 500),
    // 向量模型
    async getDenseVectorList() {
      const res = await apiGetDenseVectorList({ pageNo: 1, pageSize: 999 });
      if (res.code == "000000") {
        this.denseVectorIdList = res.data?.records || [];
        this.startDenseVectorId =
          res.data?.records.find((item) => item.enableFlag === 1)
            ?.vectorId || "";
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
            status: "是",
          };
          await this.initKnowlegeBase(params);
        }
      });
    },
    getKnowledgeInfoPresetApi(data){
      knowledgeInfoPreset({id:data.id}).then(res=>{
        if(res.code=="000000"){
          this.getKnowledgeInfoListData()
        }else{
          this.$message({
            type:"error",
            message:res.msg
          })
        }
      })
    },
    handleCommand(value, item) {
      if (value == "editView") this.viewKnowledge(item);
      if (value == "deleteApp") this.remKnowledage(item);
      if (value == "grantApp") this.grantKnowledge(item);
      if (value == 'presetApp') this.getKnowledgeInfoPresetApi(item);
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
        this.viewKnowledge(res?.data);
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
    createKnowledge() {
      this.createKbmVisible = true;
      this.setForm.knowledgeName = "";
      this.setForm.introduce = "";
      this.setForm.denseVectorId = this.startDenseVectorId;
      this.setForm.documentAnalysisServer = "yayiAnalysis";
      this.setForm.id = "";
      this.setForm.knowledgeId = "";
      this.selectedTag = [];
    },
    viewKnowledge(row) {
      this.$emit("change-view", "kbmCreate", {
        type: "viewKnowledge",
        data: row,
      });
    },
    grantKnowledge(row) {
      this.grantData = row;
      this.grantVisible = true;
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
    remKnowledage(row) {
      this.knowledgeId = row.knowledgeId;
      this.remKnowledgeName = row.knowledgeName;
      this.remVisible = true;
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
        this.getKnowledgeInfoListData();
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
    async getKnowledgeInfoListData() {
      this.tableLoading = true;
      const userInfo = sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))
        : null;
      const accountName = userInfo ? userInfo.accountName : "";
      const { data } = await getKnowledgeInfoList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        knowledgeName: this.knowledgeName,
        applicationId: this.applicationId,
        order: "create_time",
        sort: "desc",
        createUser: "",
        ownerType:"personalGrantTenant"
      });
      this.total = data.totalRow;
      this.totalPage = data.totalPage;
      this.tableData = data.records;
      this.tableLoading = false;
    },
    async handleStatusChange(row) {
      await updateStatus({
        knowledgeId: row.knowledgeId,
        status: row.status,
      });
      this.getKnowledgeInfoListData();
    },
    handleCurrentChange(n) {
      this.pageNo = n;
      this.getKnowledgeInfoListData();
    },
    handleSizeChange(n) {
      this.pageNo=1
      this.pageSize = n;
      this.getKnowledgeInfoListData();
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
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>
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
      color: #494e57;
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
          color: #b4bccc;
          line-height: 20px;
        }
        ::v-deep .el-input__inner::-moz-placeholder {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #b4bccc;
          line-height: 20px;
        }
        ::v-deep .el-input__inner:-ms-input-placeholder {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #b4bccc;
          line-height: 20px;
        }
        ::v-deep .el-input__inner::placeholder {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #b4bccc;
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
            color: #494e57;
            line-height: 20px;
            // display: -webkit-box;
            // -webkit-box-orient: vertical;
            // -webkit-line-clamp: 1;
            // overflow: hidden;
            // text-overflow: ellipsis;
            display: flex;
            align-items: center;
            gap: 8px;

            .preset{
              width: 39px;
              height: 23px;
              background: #EBEEF2;
              border-radius: 4px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 12px;
              color: #1D2129;
              line-height: 23px;
              text-align: center;
            }
          }
          .desc {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #b4bccc;
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
        color: #494e57;
        line-height: 20px;
      }
    }
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
</style>
