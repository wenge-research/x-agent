<template>
  <div class="url-view document-view">
    <LeftTree @change="handleNodeClick" :type="3" :knowledgeId="knowledgeId" @cancel="handleTreeCancel" :isAdminOrUser="isAdminOrUser"></LeftTree>
    <div class="view-right-wrapper">
      <div class="right-header">
        <div class="form-box">
          <el-form :inline="true">
            <el-form-item>
              <el-input v-model="title" :placeholder="$t('pleaseEnterApiName')" class="my-input" clearable
                @input="handleSearch">
                <i slot="suffix" class="el-input__icon el-icon-search" />

              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div class="btns-box">
          <el-button type="primary" @click="addUrlDialog" :disabled="!isAdminOrUser"><i class="el-icon-plus" style="margin-right: 8px;"></i>{{ $t('addApi') }}</el-button>
        </div>
      </div>
      <div class="table-btns">
        <div class="selected-num">{{ $t('selected') }}<span class="num">{{ multipleSelection.length }}</span></div>
        <el-checkbox v-model="isSelectedAll" @change="handleSelectedAll">{{ $t('all') }}</el-checkbox>
        <el-button type="text" :disabled="multipleSelection.length === 0" @click="handleClearSelection">{{
          $t('clearSelected') }}</el-button>
        <el-button @confirm="executeInBatches" :disabled="multipleSelection.length === 0" slot="reference" type="text"
          icon="el-icon-position">{{ $t('executeSelected') }}</el-button>
        <el-popconfirm :title="$t('confirmDelete')" @confirm="deleteInBatches"
          popper-class="deleteIn-batches-popconfirm" v-if="isAdminOrUser">
          <el-button :disabled="multipleSelection.length === 0" slot="reference" type="text" icon="el-icon-delete">{{
            $t('batch') }}{{ $t('delete') }}</el-button>
        </el-popconfirm>
      </div>
      <div class="table-box">
        <el-table ref="table" v-loading="tableLoading" class="my-table" :data="tableData" height="100%"
          @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="pageUrl" :label="$t('apiName')" min-width="120">
            <template slot-scope="scope">
              <div class="file-wrapper">
                <img :src="require(`@/assets/images/url.svg`)" alt="">
                <div class="fileName">{{ scope.row.name }} </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="requestType" label="请求方式" width="88">
            <template #default="{ row }">
              {{ row.requestType === 0 ? 'GET' : 'POST' }}
            </template>
          </el-table-column>
          <el-table-column prop="apiAddress" label="接口地址" min-width="180"></el-table-column>
          <el-table-column prop="storageType" label="存储类型" width="88">
            <template #default="{ row }">
              {{ row.storageType === 0 ? '全量更新' : '增量更新' }}
            </template>
          </el-table-column>
          <el-table-column prop="scheduledTaskDesc" label="定时任务" min-width="150"></el-table-column>
          <el-table-column prop="updateTime" :label="$t('prevUpdateTime')" width="200"></el-table-column>
          <el-table-column prop="scheduledTaskLastState" label="上次执行结果" width="110">
            <template #default="{ row }">
              {{ row.scheduledTaskLastState === 0 ? '失败' : '成功' }}
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('action')" min-width="120">
            <template slot-scope="scope">
              <div class="btns">
                <el-button type="text" @click="executiveRequest(scope.row.id)">执行请求</el-button>
                <el-button type="text" @click="viewSlice(scope.row.knowledgeApiId, scope.row.id)">查看</el-button>
                <el-button type="text" @click="remUrlData(scope.row.id)" :disabled="!isAdminOrUser">{{ $t('delete') }}</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <div class="total-num">
          {{ $t('totalNum', { total: total }) }}
        </div>
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" background
          :page-size="pageSize" :current-page="pageNo" :page-sizes="[20, 50, 100, 150, 200]"
          layout="prev, pager, next, sizes, jumper" :total="total">
        </el-pagination>
      </div>
    </div>

    <!-- 切片信息 -->
    <el-drawer :visible.sync="urlVisible" size="100%" :with-header="false" append-to-body :modal-append-to-body="false"
      :close-on-click-modal="false" :show-close="true" :close-on-press-escape="false" :wrapperClosable="false">
      <apiDetails @close="handleClose" :urlId="urlId" :knowledgeId="id" />
    </el-drawer>


    <el-dialog class="my-dialog" :title="$t('importExcelFile')" :visible.sync="importVisible" width="500px"
      :modal-append-to-body="false" :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false"
      append-to-body>
      <div class="import-header">
        <el-button type="text" icon="el-icon-download" @click="handleDownloadKnowledgeDataTemp"
          :loading="downLoading">{{
            $t('downloadTemplate') }}</el-button>
      </div>
      <div style="margin-bottom: 18px;"></div>
      <el-upload action="#" drag accept=".xlsx" :before-upload="beforeupload" :show-file-list="false">
        <img class="icon-upload" src="@/assets/images/upload-cloud-2-fill.svg" alt="">
        <div class="el-upload__text">{{ $t('dragXlsxFileHere') }}，{{ $t('or') }}{{ $t('clickUpload') }}</div>
      </el-upload>
      <div class="file-item" v-for="item in uploadFile" :key="item.uid">
        {{ item.name }} <i @click="handleRemovefileItem" class="el-icon-close"></i>
      </div>
      <div style="margin-bottom: 12px;"></div>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="importLoading" @click="handleCancelImport">{{ $t('cancel') }}</el-button>
        <el-button :loading="importLoading" type="primary" @click="handleImport">{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>
    <!-- <el-dialog class="my-dialog" el-dialog :title="$t('addUrl')" :visible.sync="addApiVisible" width="800px" :modal-append-to-body="false"
        :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
      </el-dialog> -->
    <!-- 新增接口抽屉 -->
    <el-drawer class="my-dialog" title="新增接口" size="50%" :visible.sync="addApiVisible" append-to-body
      :modal-append-to-body="false" :close-on-click-modal="false" :close-on-press-escape="false" ref="addApiDrawer"
      :wrapperClosable="false" style="height: 100vh"> <!-- 设置固定高度 -->
      <div style="height: 100%; overflow-y: auto"> <!-- 内容容器设置滚动 -->
        <addApi :knowledgeId="knowledgeId"  @close-drawer="handleCloseDrawer" />
      </div>
    </el-drawer>


    <el-dialog :title="$t('deleteUrl')" :visible.sync="remUrlVisible" width="400px" :modal-append-to-body="false"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
      <span>{{ $t('confirmDelete') }}</span>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="remUrlLoading" @click="remUrlVisible = false">{{ $t('cancel') }}</el-button>
        <el-button :loading="remUrlLoading" type="primary" @click="handleRemUrl">{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { FREQUENCY_OPTIONS, URL_TYP } from '@/views/Kbm/content/index'
import { getUrlList, addKnowledgeUrlData, deleteUrlParserInfo, importUrlParserInfoData, apiUrlParserById } from '@/api/index.js'
import apiDetails from '@/views/Kbm/component/apiDetails.vue'
import { FILE_TYP } from "@/views/Kbm/content/index";
import LeftTree from './LeftTree.vue'
import { debounce } from 'lodash';
import { formatFileSize } from '@/utils/tool';
import addApi from './addApi.vue';
import { getKnowledgeApi, delKnowledgeApi, runApiKnowledgeApi } from './../../../api/Kbm'
export default {
  components: {
    apiDetails, LeftTree, addApi
  },
  props: {
    knowledgeId: {
      type: String,
      default: ''
    },
    isAdminOrUser:{
      type:Boolean,
      default:true
    }
  },
  data() {
    return {
      FILE_TYP,
      remUrlLoading: false,
      remUrlVisible: false,
      addApiVisible: false,
      multipleSelection: [],
      FREQUENCY_OPTIONS,
      URL_TYP,
      tableData: [],
      title: '',
      urlId: '',
      id: '',
      tableLoading: true,
      analysisLoading: false,
      urlVisible: false,
      urlParams: [
        {
          link: '',
          autoUpdate: 2
        }
      ],
      pageNo: 1,
      pageSize: 50,
      total: 0,
      downLoading: false,
      importVisible: false,
      importLoading: false,
      themeName: '',
      typeId: '',
      pid: '',
      uploadForm: new FormData(),
      uploadFile: [],

      isAll: true,
      isSelectedAll: false,
      foldersId: '',
      iconLoading: false,
      currentId: ''
    }
  },
  created() {
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
  },
  methods: {
    handleCloseDrawer() {
      this.addApiVisible = false;
      this.getUrlListData();
    },
    formatFileSize,
    // 重试
    async retryHandler(id) {
      this.iconLoading = true;
      try {
        const res = await apiUrlParserById({
          id,
        });
        if (res.code === "000000") {
          this.getUrlListData()
          this.$message({
            message: res.msg,
            showClose: true,
            type: "success",
          });
        } else {
          this.$message({
            message: res.msg,
            showClose: true,
            type: "error",
          });
        }
      } catch (error) {
        this.iconLoading = false;
      }
      this.iconLoading = false;
    },

    handleResetAnalyze(row) {
      this.retryHandler(row.id)
      this.currentId = row.id;
    },
    handleTreeCancel() {
      this.isAll = true
      this.getUrlListData()

    },
    handleSelectedAll() {
      this.$refs.table.toggleAllSelection();
    },
    handleClearSelection() {
      this.$refs.table.clearSelection();
    },
    handleNodeClick(node) {
      if (!node) {
        this.isAll = true
        this.foldersId = ''
      } else {
        this.isAll = false
        this.foldersId = node
      }
      this.getUrlListData()
    },
    handleSearch: debounce(function () {
      this.getUrlListData()
    }, 500),
    //执行所选
    executeInBatches() {
      console.log('执行所选')
    },
    async deleteInBatches() {
      console.log('this.multipleSelection',this.multipleSelection)
      const res = await delKnowledgeApi({
        ids: this.multipleSelection.map(n => n.id)
      })
      if (res.code === '000000') {
        this.getUrlListData()
        this.$message({
          message: res.msg,
          showClose: true,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          showClose: true,
          type: 'error'
        });
      }
    },
    remUrlData(id) {
      this.remUrlVisible = true
      this.urlId = id
    },
    async handleRemUrl() {
      this.remUrlLoading = true
      const res = await delKnowledgeApi({
        ids: [this.urlId]
      })
      if (res.code === "000000") {
        this.remUrlVisible = false
        this.getUrlListData()
        this.$message({
          message: res.msg,
          showClose: true,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          showClose: true,
          type: 'error'
        });
      }
      this.remUrlLoading = false
    },
    //执行请求
    async executiveRequest(id) {
       try {
        const res = await runApiKnowledgeApi({id})
        if(res.code === '000000') {
          this.$message.success("执行成功");
        }
       } catch (error) {
        console.log('error', error)
       }
    },
    // 下载模版
    async handleDownloadKnowledgeDataTemp() {
      window.location.href = process.env.VUE_APP_API_NEW + '/urlParserInfo/downloadKnowledgeUrlTemp' // 生产
    },
    handleRemovefileItem() {
      this.uploadForm = new FormData();
      this.uploadFile = []
    },
    beforeupload(file) {
      this.uploadForm = new FormData()
      this.uploadForm.append('file', file);
      this.uploadFile = [file]
      return false;
    },
    handleCancelImport() {
      this.importVisible = false
      this.uploadFile = []
      this.uploadForm = new FormData();
    },
    async handleImport() {
      if (this.uploadFile.length === 0) {
        this.$message({
          message: this.$t('pleaseSelectUploadFile'),
          showClose: true,
          type: 'warning'
        });
        return false
      }
      this.importLoading = true
      this.uploadForm.append('knowledgeId', this.knowledgeId)
      this.uploadForm.append('foldersId', this.foldersId)
      const res = await importUrlParserInfoData(this.uploadForm)
      if (res.code === '000000') {
        this.handleCancelImport()
        this.tableLoading = true
        setTimeout(() => {
          this.getUrlListData()
        }, 1500)
        this.$message({
          message: res.msg,
          showClose: true,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          showClose: true,
          type: 'error'
        });
      }
      this.importLoading = false
    },
    addUrlDialog() {
      // this.urlParams = [
      //   {
      //     link: '',
      //     autoUpdate: 2
      //   }
      // ]
      this.addApiVisible = true
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
      this.isSelectedAll = val.length === this.pageSize || val.length === this.tableData.length
    },
    handleClose() {
      this.urlVisible = false
    },
    handleAddLink() {
      this.urlParams.push({
        link: '',
        autoUpdate: 2
      })
    },
    handleRemLink(index) {
      if (this.urlParams.length === 1) {
        return
      }
      this.urlParams.splice(index, 1)
    },
    async getUrlListData() {
      this.tableLoading = true
      const { data } = await getKnowledgeApi({
        pageSize: this.pageSize,
        pageNo: this.pageNo,
        knowledgeId: this.knowledgeId,
        name: this.title,
        foldersId: this.foldersId
      })
      this.tableData = data.records
      this.total = data.totalRow
      this.tableLoading = false
    },

    // async getKnowledgeApiList() { 
    //   this.tableLoading = true
    //   const { data } = await getKnowledgeApi({
    //     pageSize: this.pageSize,
    //     pageNo: this.pageNo,
    //     knowledgeId: this.knowledgeId,
    //     title: this.title,
    //     foldersId: this.foldersId
    //   })
    // },
    viewSlice(id, knowledgeId) {
      this.urlId = id
      this.id = knowledgeId
      this.urlVisible = true
    },
    handleCurrentChange(n) {
      this.pageNo = n
      this.getUrlListData()
    },
    handleSizeChange(n) {
      this.pageSize = n
      this.getUrlListData()
    }
  }
}
</script>
<style lang="scss" scoped>
@import "./kbm.scss";

.url-view {
  width: 100%;
  height: 100%;
  display: flex;
  overflow: auto;
  position: relative;

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

  .form-box {
    .el-form-item {
      margin-bottom: 0px;
    }
  }

  .option-box {
    display: flex;
    margin: 10px 0;
    justify-content: space-between;
  }

  .table-box {
    flex-grow: 1;

    .my-table {
      .file-wrapper {
        display: flex;
        align-items: center;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #494E57;
        line-height: 20px;

        img {
          width: 24px;
          height: 24px;
          margin-right: 8px;
        }
      }
    }

    .file-type {
      display: flex;
      align-items: center;

      span {
        display: inline-block;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        margin-right: 6px;
      }

      /* 定义旋转动画 */
      @keyframes spin {
        from {
          transform: rotate(0deg);
        }

        to {
          transform: rotate(360deg);
        }
      }

      /* 应用动画到图片 */
      .iconLoading {
        animation: spin 2s linear infinite;
        /* 2s 为旋转一圈的时间，infinite 表示无限循环 */
      }
    }

    .btns {
      display: flex;
      // justify-content: center;
    }
  }

  .pagination {
    display: flex;
    // justify-content: flex-end;
    align-items: center;
    margin-top: 24px;
    position: relative;

    .tips {
      position: absolute;
      color: red;
      bottom: 8px;
      left: 0;
    }
  }
}

.upload-box {
  .title {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #828894;
    line-height: 20px;
  }

  .con {

    // max-height: 240px;
    .item {
      display: flex;
      align-items: center;
      margin-top: 8px;

      .el-icon-delete {
        font-size: 20px;
        color: #494E57;
        cursor: pointer;
      }
    }
  }

  .url-add-btns {
    margin-top: 12px;
  }

  .hint {
    margin-top: 18px;

    p {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #828894;
      line-height: 18px;
      margin-top: 5px;
    }
  }
}

.import-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

::v-deep .el-upload-dragger {
  height: 112px;
  background: #F9FAFC;
  border-radius: 2px;
  border: 1px dotted rgba(0, 0, 0, 0.12);

  .icon-upload {
    width: 22px;
    height: 20px;
    margin: 16px 0 8px;
  }

  .el-icon-upload {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 20px;
    color: #494E57;
    line-height: 20px;
  }

  .el-upload__text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #494E57;
    line-height: 20px;
  }

  .el-upload__condition {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 12px;
    color: #B4BCCC;
    line-height: 16px;
  }
}

.file {
  margin: 10px 0;
  display: flex;
  justify-content: space-between;

  .name {
    font-weight: bold;
  }
}

.file-item {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  font-weight: bold;
  margin-top: 8px;

  i {
    cursor: pointer;
  }
}
</style>
<style scoped>
/deep/ .el-drawer__body {
  overflow: hidden !important;
}

::v-deep .el-drawer__header {
  margin-bottom: 20px;
  font-size: 20px;
  color: #1D2129;
}
</style>