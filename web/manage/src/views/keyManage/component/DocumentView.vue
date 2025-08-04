<template>
  <div class="document-view">
    <div class="left view-left-wrapper" v-loading="foldersLoading">
      <div class="left-search">
        <el-input
          v-model="filterText"
          size="small"
          :placeholder="$t('enterKnowledgetreeName')"
          clearable
        >
          <i slot="prefix" class="el-input__icon el-icon-search" />
        </el-input>
      </div>
      <!-- <div class="all-text" :class="{'current-node': isAll}" @click="handleAllClick">
        {{ $t('allText') }}
      </div> -->
      <!-- <div class="option">
        <span>{{$t('all')}}</span>
        <el-button
          icon="el-icon-circle-plus"
          type="text"
          @click="addfolder('root')"
          >{{$t('add')}}</el-button
        >
      </div> -->
      <el-tree
        :expand-on-click-node="false"
        :data="folders"
        :filter-node-method="filterNode"
        default-expand-all
        ref="tree"
        node-key="foldersId"
        @node-click="handleFolderChange"
      >
        <div class="folders-item" slot-scope="{ node, data }">
          <span class="info" :title="node.label">
            <img v-if="node.expanded" class="folder-opened" :src="require('@/assets/images/folder-open.svg')" alt="">
            <img v-else class="folder" :src="require('@/assets/images/folder.svg')" alt="">
            <!-- <i :class="node.expanded ? 'el-icon-folder-opened' : 'el-icon-folder'"></i> -->
            {{ node.label }}
          </span>
          <!-- <span class="option">
            <el-dropdown>
              <i class="el-icon-arrow-down el-icon-more-outline"></i>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <el-button
                    type="text"
                    icon="el-icon-circle-plus"
                    @click="addfolder(node)"
                    >{{$t('addSubordinate')}}</el-button
                  >
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button
                    type="text"
                    icon="el-icon-edit"
                    @click="renfolder(node)"
                    >{{$t('edit')}}</el-button
                  >
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button
                    type="text"
                    icon="el-icon-delete"
                    @click="remfolder(node)"
                    >{{$t('delete')}}</el-button
                  >
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button
                    type="text"
                    icon="el-icon-upload"
                    @click="importfolder(node)"
                    >{{$t('uploadFile')}}</el-button
                  >
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </span> -->
        </div>
      </el-tree>
      <div class="tree-btn" @click="dialogVisible = true">
        <img :src="require('@/assets/images/archive-line.svg')" alt="">
        <span class="classisfy-btn">
          {{ $t('sortMnagement') }}
        </span>
      </div>
    </div>
    <!-- <div class="solid" @mousedown="onMouseDown"></div> -->
    <div class="view-right-wrapper" :style="{ width: rightWidth + 'px' }">
      <div class="right-header">
        <div class="form-box">
          <el-form :inline="true">
            <el-form-item>
              <el-input
                v-model="fileName"
                :placeholder="$t('enterFileName')"
                class="my-input"
                 clearable
                 @input="handleSearch(folderId)"
              >
                <i slot="suffix" class="el-input__icon el-icon-search" />
              </el-input>
            </el-form-item>
            <el-form-item>
              <div class="icon-refresh" @click="getFileListData(folderId)">
                <svg width="16" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" d="M5.463 4.433A9.961 9.961 0 0 1 12 2c5.523 0 10 4.477 10 10 0 2.136-.67 4.116-1.81 5.74L17 12h3A8 8 0 0 0 6.46 6.228l-.997-1.795Zm13.074 15.134A9.961 9.961 0 0 1 12 22C6.477 22 2 17.523 2 12c0-2.136.67-4.116 1.81-5.74L7 12H4a8 8 0 0 0 13.54 5.772l.997 1.795Z" data-follow-fill="#848587"/></svg>
              </div>
            </el-form-item>

            <!-- <el-form-item>
              <el-button
                type="primary"
                size="small"
                @click="getFileListData(folderId)"
                >{{$t('search')}}</el-button
              >
            </el-form-item> -->
          </el-form>
        </div>
        <div class="btns-box">
          <!-- <el-button class="icon-export" type="text" @click="handleOperationLog">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" fill="#1747E5" d="m16 2 5 5v13.992A1 1 0 0 1 20.007 22H3.993A1 1 0 0 1 3 21.008V2.992C3 2.444 3.447 2 3.998 2H16Zm-1 2H5v16h14V8h-4V4Zm-2 5v4h3v2h-5V9h2Z" data-follow-fill="#848587"/></svg>
            {{$t('operationLog')}}</el-button
          > -->
          <el-button class="icon-import" type="primary" @click="importfolder">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" d="m12 12.586 4.243 4.242-1.415 1.415L13 16.415V22h-2v-5.587l-1.828 1.83-1.415-1.415L12 12.586ZM12 2a7.001 7.001 0 0 1 6.954 6.194A5.5 5.5 0 0 1 18 18.978v-2.014a3.5 3.5 0 1 0-1.111-6.91 5 5 0 1 0-9.777 0 3.5 3.5 0 0 0-1.292 6.88l.18.03v2.014a5.5 5.5 0 0 1-.954-10.784A7 7 0 0 1 12 2Z" data-follow-fill="#848587"/></svg>
            {{$t('importFileData')}}</el-button
          >
        </div>
      </div>
      <!-- <div class="option-box">
        <div>
          <el-popconfirm :title="$t('confirmDelete')" @confirm="deleteInBatches">
            <el-button
              :disabled="multipleSelection.length === 0"
              slot="reference"
              type="danger"
              icon="el-icon-delete"
              size="small"
              >{{$t('batch')}}{{$t('delete')}}</el-button
            >
          </el-popconfirm>
        </div>
        <div>
          <el-button type="primary" :disabled="multipleSelection.length === 0" icon="el-icon-setting" size="small"
            @click="timeInBatches">{{ $t('batchSetExpirationTime') }}</el-button>
          <el-button type="primary" icon="el-icon-s-order" size="small"
            >{{$t('operationLog')}}</el-button
          >
        </div>
      </div> -->
      <div class="table-btns">
        <div class="selected-num">{{ $t('selected') }}<span class="num">{{multipleSelection.length}}</span></div>
        <el-checkbox v-model="isSelectedAll" @change="handleSelectedAll">{{$t('all')}}</el-checkbox>
        <el-button type="text" :disabled="multipleSelection.length === 0" @click="handleClearSelection">{{$t('clearSelected')}}</el-button>
        <el-popconfirm :title="$t('confirmDelete')" @confirm="deleteInBatches" popper-class="deleteIn-batches-popconfirm">
          <el-button
            :disabled="multipleSelection.length === 0"
            slot="reference"
            type="text"
            icon="el-icon-delete"
            >{{$t('batch')}}{{$t('delete')}}</el-button
          >
        </el-popconfirm>
      </div>
      <div class="table-box">
        <el-table
          ref="table"
          v-loading="tableLoading"
          class="my-table" 
          style="width: 100%"
          :data="tableData"
          height="100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column
            prop="fileName"
            :label="$t('fileName')"
            min-width="200"
          >
          <template slot-scope="scope">
            <div class="file-wrapper">
              <img :src="fileWrapperImg(scope.row.fileType)" alt="">
              <div class="fileName">{{ scope.row.fileName }} </div>
            </div>
          </template>
          </el-table-column>
          <el-table-column
            prop="wordCount"
            :label="$t('characterCount')"
            width="100"
          ></el-table-column>
          <el-table-column
            prop="paragraphsNum"
            :label="$t('segmentCount')"
            width="100"
          ></el-table-column>
          <el-table-column
            prop="createTime"
            :label="$t('uploadTime')"
            width="200"
          ></el-table-column>
          <el-table-column prop="status" :label="$t('fileStatus')" width="120">
            <template slot="header" slot-scope="scope">
              <div style="display: flex;align-items: center;">
                <div>{{ $t('fileStatus') }}</div>
                <el-tooltip class="item" effect="dark" :content="$t('noticeTip')" placement="top">
                  <img width="14" style="margin-left: 4px;cursor: pointer;" src="@/assets/images/question-line.svg" alt="">
                </el-tooltip>
              </div>
            </template>
            <template slot-scope="scope">
              <div class="file-type">
                <span
                  :style="{ backgroundColor: FILE_TYP[scope.row.status].color, display: 'inline-block', width: '8px', height: '8px', borderRadius: '50%' }"
                ></span>
                {{ FILE_TYP[scope.row.status].label }}
                <!-- <img
                  v-if="scope.row.status === 3"
                  src="@/assets/images/alert-fill.svg"
                  style="width: 15px; height: 15px; vertical-align: middle;margin-left: 8px;cursor: pointer;"
                  @click="changePassword(scope.row)"
                /> -->
                <img
                  v-if="scope.row.status === 3"
                  src="@/assets/images/reset-left-line.svg"
                  :class="[iconLoading && currentId == scope.row.id ? 'iconLoading' : '']"
                  style="width: 15px; height: 15px; vertical-align: middle;margin-left: 8px;cursor: pointer;"
                  @click="handleResetAnalyze(scope.row)"
                />
              </div>
            </template>
          </el-table-column>
          <!-- <el-table-column prop="status" :label="$t('enabledStatus')" width="110">
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
              <span class="switch-status-text">{{ scope.row.status == $t('yes') ? $t('active') : $t('inactive') }}</span>
            </template>
          </el-table-column> -->
          <el-table-column fixed="right" :label="$t('action')" width="140">
            <template slot-scope="scope">
              <div class="btns">
                <!-- <el-button type="text" @click="editDocument(scope.row)"
                  >{{$t('edit')}}</el-button
                > -->
                <el-button type="text" @click="viewDocument(scope.row)" v-if="scope.row.status === 2"
                  >{{$t('view')}}</el-button
                >
                <!-- <el-button
                  type="text"
                  @click="preview(scope.row)"
                  >{{$t('preview')}}</el-button
                > -->
                <el-button type="text" @click="remFileData(scope.row.fileId)"
                  >{{$t('delete')}}</el-button
                >
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <!-- <div class="tips">{{$t('noticeTip')}}</div> -->
        <div class="total-num">
          {{ $t('totalNum', { total: total }) }}
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
      :title="$t('addFolder')"
      :visible.sync="addfolderVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <el-input
        v-model="folderName"
        size="small"
        :placeholder="$t('enterDirectoryName')"
      />
      <span slot="footer" class="dialog-footer">
        <el-button :loading="addfolderLoading" @click="addfolderVisible = false"
          >{{ $t('cancel') }}</el-button>
        <el-button
          :loading="addfolderLoading"
          type="primary"
          @click="handleAddFolder"
          >{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t('deleteFolder')"
      :visible.sync="remfolderVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <span>{{$t('confirmDeleteFolder')}}</span>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="remfolderLoading" @click="remfolderVisible = false"
          >{{ $t('cancel') }}</el-button>
        <el-button
          :loading="remfolderLoading"
          type="primary"
          @click="handleRemFolder"
          >{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t('nameFolder')"
      :visible.sync="renfolderVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <el-input
        v-model="folderName"
        size="small"
        :placeholder="$t('enterDirectoryName')"
      />
      <span slot="footer" class="dialog-footer">
        <el-button :loading="renfolderLoading" @click="renfolderVisible = false"
          >{{ $t('cancel') }}</el-button>
        <el-button
          :loading="renfolderLoading"
          type="primary"
          @click="handleRenFolder"
          >{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t('importFileData')"
      :visible.sync="importVisible"
      width="720px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      class="my-dialog"
      append-to-body
    >
    <!-- <el-upload action="#" drag accept=".xlsx" :before-upload="beforeupload" :show-file-list="false">
      <img class="icon-upload" src="@/assets/images/upload-cloud-2-fill.svg" alt="">
      <div class="el-upload__text">{{$t('dragXlsxFileHere')}}，{{$t('or')}}{{$t('clickUpload')}}</div>
      <div class="el-upload__condition">{{$t('supportedXlsx')}}</div>
    </el-upload> -->
      <el-upload
        action="#"
        :before-upload="beforeupload"
        drag
        multiple
        :show-file-list="false"
        accept=".docx, .doc, .pdf, .txt, .pptx"
      >
        <img class="icon-upload" src="@/assets/images/upload-cloud-2-fill.svg" alt="">
        <div class="el-upload__text">{{$t('dragFileHere')}}{{$t('or')}}{{$t('clickUpload')}}</div>
        <div class="el-upload__condition">
          <div>{{$t('supportedFormatsOne')}}</div>
          <div>{{$t('supportedFormatsTwo')}}{{ 10-improtFilesList.length }}</div>
        </div>
      </el-upload>
      <el-table
        style="width: 100%"
        :data="improtFilesList"
        height="360px"
        class="import-table"
      >
        <el-table-column
          prop="name"
          :label="$t('file')"
        >
          <template slot-scope="scope">
            <div class="file-wrapper">
              <img :src="fileWrapperImg(scope.row.type)" alt="">
              <div class="file-name">
                <div class="name">{{ scope.row.name }}</div>
                <div class="size">{{ formatFileSize(scope.row.size) }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <!-- <el-table-column
          prop="webLink"
          :label="$t('addFileLink')"
        >
        <template slot-scope="scope">
          <div class="file-wrapper">
            <el-input
              style="width: 260px"
              v-model="scope.row.webLink"
              :placeholder="$t('enterFileLink')"
              size="small"
            ></el-input>
          </div>
        </template>
        </el-table-column> -->
        <el-table-column fixed="right" :label="$t('action')" width="60">
          <template slot-scope="scope">
            <div class="btns">
              <el-button type="text" @click="handleRemovefileItem(scope.row.uid)"
                >{{$t('delete')}}</el-button
              >
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- <div class="file-con">
        
        <div class="file-item" v-for="item in improtFilesList" :key="item.uid">
          <span class="name">{{ item.name }} </span>
          <el-input
            style="width: 260px"
            v-model="item.webLink"
            :placeholder="$t('enterFileLink')"
            size="small"
          ></el-input>
          <i @click="handleRemovefileItem(item.uid)" class="el-icon-close"></i>
        </div>
      </div> -->
      <span slot="footer" class="dialog-footer">
        <el-button :loading="importLoading" @click="handCancelFolder"
          >{{ $t('cancel') }}</el-button>
        <el-button
          :loading="importLoading"
          type="primary"
          @click="handleImportFolder"
          >{{ $t('confirmImport') }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t('tips')"
      :visible.sync="remVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <span>{{$t('confirmDeleteFolder')}}</span>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="remLoading" @click="remVisible = false"
          >{{ $t('cancel') }}</el-button>
        <el-button
          :loading="remLoading"
          type="primary"
          @click="handleRemQaDialog"
          >{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>

    <el-drawer
      :visible.sync="documentVisible"
      size="100%"
      :with-header="false"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :wrapperClosable="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <DocumentInfo
        @close="handleClose"
        :knowledgeId="knowledgeId"
        :rowData="fileData"
      />
    </el-drawer>

    <el-dialog
      :title="$t('validitySetting')"
      :visible.sync="timeVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <div>{{$t('expiryNotice')}}</div>
      <div style="margin-bottom: 10px"></div>
      <el-date-picker
        v-model="validTime"
        type="datetime"
        :placeholder="$t('selectDateTime')"
      ></el-date-picker>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="timeLoading" @click="timeVisible = false"
          >{{ $t('cancel') }}</el-button>
        <el-button :loading="timeLoading" type="primary">{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t('editLink')"
      :visible.sync="editWebLinkVisible"
      width="400px"
      :modal-append-to-body="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <el-input v-model="editWebLink" size="small"  />
      <span slot="footer" class="dialog-footer">
        <el-button @click="editWebLinkVisible = false">{{ $t('cancel') }}</el-button>
        <el-button
          :loading="editWebLinkLoading"
          type="primary"
          @click="handleEditWebLink"
          >{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="$t('fileEncrypted')"
      :visible.sync="passwordVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <el-input v-model="password" size="small" :placeholder="$t('enterFilePassword')" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="passwordVisible = false">{{ $t('cancel') }}</el-button>
        <el-button
          :loading="passwordLoading"
          type="primary"
          @click="handlePassword"
          >{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>
    
    <ComDocPreview ref="ComDocPreview"></ComDocPreview>
    <Tree ref="myTree" v-if="dialogVisible" :dialogVisible.sync="dialogVisible" :knowledgeId="knowledgeId" @cancel="handleTreeCancel"></Tree>
    <OperationLog ref="OperationLog" v-if="operationLogVisible" :visible.sync="operationLogVisible" :knowledgeId="knowledgeId"></OperationLog>
  </div>
</template>

<script>
import DocumentInfo from "@/views/Kbm/component/DocumentInfo.vue";
import { FILE_TYP } from "@/views/Kbm/content/index";
import ComDocPreview from '@/components/ComDocPreview'
import Tree from './Tree.vue'
import OperationLog from './OperationLog.vue'

import {
  getFoldersList,
  addFolders,
  deleteFolders,
  getFileList,
  addFile,
  deleteFile,
  updatePassword,
  updateFileWebLink,
  apiDocumentAnalysisById
} from "@/api/index.js";
import { debounce } from 'lodash';
export default {
  props: {
    knowledgeId: {
      type: String,
      default: "",
    },
  },
  components: {
    DocumentInfo, ComDocPreview, Tree, OperationLog
  },
  data() {
    return {
      leftWidth: "",
      rightWidth: "",
      FILE_TYP,
      filterText: "",
      tableLoading: false,
      pageNo: 1,
      pageSize: 20,
      total: 0,
      tableData: [],
      importData: {},
      validTime: "",
      timeVisible: false,
      remVisible: false,
      remLoading: false,
      timeLoading: false,
      renfolderVisible: false,
      renfolderLoading: false,
      remfolderVisible: false,
      remfolderLoading: false,
      addfolderVisible: false,
      addfolderLoading: false,
      editWebLinkVisible: false,
      editId: '',
      editWebLink: "",
      documentVisible: false,
      importVisible: false,
      importLoading: false,
      folderName: "",
      folderParentId: "",
      folderId: "",
      tableLoading: false,
      folders: [],
      multipleSelection: [],
      isSelectedAll: false,
      foldersLoading: false,
      fileName: "", // 文件名称
      urlId: "",
      uploadForm: new FormData(),
      uploadFile: [],
      fileId: "",
      fileData: {},
      isResizing: false,
      containerWidth: 600,
      passwordVisible: false,
      editWebLinkLoading: false,
      passwordLoading: false,
      password: "",
      filePassData: {},
      folderDataId: null, // 节点id
      improtFilesList: [],
      params: new FormData(),
      dialogVisible: false,
      isAll: false,
      operationLogVisible: false,
      iconLoading: false,
      currentId: ''
    };
  },

  created() {
    this.getFoldersListData();
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
  },
  methods: {
    formatFileSize(size) {
      let sizeInKB = Number(size.slice(0, -2))
      if (sizeInKB < 1) {
        return `${Math.round(sizeInKB * 1024)} B`;
      } else if (sizeInKB < 1024) {
        return `${sizeInKB.toFixed(1)} kB`;
      } else if (sizeInKB < 1024 * 1024) {
        return `${(sizeInKB / 1024).toFixed(1)} MB`;
      } else if (sizeInKB < 1024 * 1024 * 1024) {
        return `${(sizeInKB / (1024 * 1024)).toFixed(1)} GB`;
      } else {
        return `${(sizeInKB / (1024 * 1024 * 1024)).toFixed(1)} TB`;
      }
    },
    // 重试
    async retryHandler(id) {
      this.iconLoading = true;
      try {
        const res = await apiDocumentAnalysisById({
        id,
      });
      if (res.code === "000000") {
        this.getFoldersListData()
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
      } catch (error) {
        this.iconLoading = false;
      }
      this.iconLoading = false;
    },
    handleResetAnalyze(row) {
      this.retryHandler(row.id)
      this.currentId = row.id;
    },

    fileWrapperImg(type){
      let typeStr = '';
      if(type == 'docx' || type == 'doc'){
        typeStr = 'docx'
      }else {
        typeStr = type
      }
      return require(`@/assets/images/${typeStr}.svg`)
    },

    handleTreeCancel() {
      this.isAll = true
      this.getFileListData("");
      this.getFoldersListData();
    },
    handleOperationLog() {
      // this.operationLogVisible = true
    },
    async handleStatusChange(row) {
      // await addKnowledgeData({
      //   ...row,
      //   knowledgeId: this.knowledgeId,
      //   status: row.status,
      // })
      // setTimeout(() => {
      //   this.getKnowledgeDataListData();
      // }, 1000)
    },
    handleSearch: debounce(function(folderId) {
      this.getFileListData(folderId)
    }, 500),
    handleSelectedAll() {
      this.$refs.table.toggleAllSelection();
    },
    handleClearSelection() {
      this.$refs.table.clearSelection();
    },
    handleAllClick() {
      this.folderId = ""
      this.isAll = true
      this.$nextTick(() => {
        this.$refs.tree.setCurrentKey(null);
      })
      this.getFileListData(this.folderId);
    },
    onMouseDown() {
      this.isResizing = true;
      document.addEventListener("mousemove", this.onMouseMove);
      document.addEventListener("mouseup", this.onMouseUp);
    },
    onMouseMove(event) {
      if (this.isResizing) {
        const containerRect = this.$el.getBoundingClientRect();
        const mouseX = event.clientX - containerRect.left;
        this.leftWidth = mouseX;
        this.rightWidth = this.containerWidth - mouseX;
      }
    },
    onMouseUp() {
      this.isResizing = false;
      document.removeEventListener("mousemove", this.onMouseMove);
      document.removeEventListener("mouseup", this.onMouseUp);
    },
    handleFolderChange(data, node, ary) {
      this.folderId = data.foldersId;
      this.isAll = false
      this.getFileListData(data.foldersId);
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    handleClose() {
      this.documentVisible = false;
    },
    hanldeCallBack() {
      this.$emit("change-view", "kbmList", null);
    },
    async getFoldersListData() {
      this.foldersLoading = true;
      const { data } = await getFoldersList({
        pageNo: 1,
        pageSize: 9999,
        knowledgeId: this.knowledgeId,
        type: 2,
      });
      this.folders = this.formatFolderListData(data.records);
      this.folderId = data.records.find((item) => {
        return item.name == '全部'
      })?.foldersId
      this.$nextTick(() => {
        this.$refs.tree.setCurrentKey(this.folderId);
        this.getFileListData(this.folderId);
      })
      this.foldersLoading = false;
    },
    formatFolderListData(items) {
      const folderMap = {};
      items.forEach((item) => {
        folderMap[item.foldersId] = {
          label: item.name,
          parentId: item.parentId,
          foldersId: item.foldersId,
          id: item.id,
        };
        const hasChildren = items.some(
          (subItem) => subItem.parentId === item.foldersId
        );
        if (hasChildren) {
          folderMap[item.foldersId].children = [];
        }
      });
      items.forEach((item) => {
        if (item.parentId !== "0" && folderMap[item.parentId]) {
          folderMap[item.parentId].children.push(folderMap[item.foldersId]);
        }
      });
      const tree = Object.values(folderMap).filter(
        (folder) => folder.parentId === "0"
      );

      return tree;
    },
    addfolder(node) {
      this.folderName = "";
      if (node === "root") {
        this.folderParentId = "0";
      } else {
        this.folderParentId = node.data.foldersId;
      }
      this.addfolderVisible = true;
    },
    async handleAddFolder() {
      this.addfolderLoading = true;
      const res = await addFolders({
        knowledgeId: this.knowledgeId,
        name: this.folderName,
        parentId: this.folderParentId,
        type: 2,
      });
      if (res.code === "000000") {
        this.addfolderVisible = false;
        this.getFoldersListData();
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
      this.addfolderLoading = false;
      console.log(res);
    },
    remfolder({ data }) {
      this.remfolderVisible = true;
      this.folderId = data.foldersId;
    },
    async handleRemFolder() {
      this.remfolderLoading = true;
      const res = await deleteFolders({
        foldersIdList: [this.folderId],
        type: 2,
      });
      if (res.code === "000000") {
        this.remfolderVisible = false;
        this.getFoldersListData(this.folderId);
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
      this.remfolderLoading = false;
    },
    renfolder({ data }) {
      this.renfolderVisible = true;
      this.folderId = data.foldersId;
      this.folderParentId = data.parentId;
      this.folderName = data.label;
      this.folderDataId = data.id;
    },
    async handleRenFolder() {
      this.renfolderLoading = true;
      const res = await addFolders({
        name: this.folderName,
        foldersId: this.folderId,
        knowledgeId: this.knowledgeId,
        parentId: this.folderParentId,
        id: this.folderDataId,
        type: 2,
      });
      if (res.code === "000000") {
        this.renfolderVisible = false;
        this.getFoldersListData(this.folderId);
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
      this.renfolderLoading = false;
    },
    importfolder() {
      // this.folderId = data.foldersId;
      this.importVisible = true;
    },
    beforeupload(file) {

      const allowedTypes = ['docx', 'doc', 'pdf', 'txt'];
      const fileType = file.name.split('.').pop().toLowerCase();

      if (!allowedTypes.includes(fileType)) {
          this.$message({
              message: this.$t('supportedFormatsOne'),
              type: "warning",
          });
          return false;
      }

      if(this.improtFilesList.length >= 10){
        this.$message({
          message: this.$t('supportedFormatsMaximumUploads'),
          type: "warning",
        });
        return false;
      }

      let type = file?.name.split('.')
      type = type[type.length - 1]
      console.log('file', file)
      let size = file?.size / 1024
      this.uploadForm.append("files", file);
      this.uploadFile.push(file);
      let obj = {
        name: file.name,
        webLink: "",
        uid: file.uid,
        type: type.toLowerCase(),
        size: size.toFixed(2) + 'kb'
      };
      this.improtFilesList.push(obj);
      console.log(this.improtFilesList,'this.improtFilesList')
      return false;
    },
    handCancelFolder() {
      this.importVisible = false;
      this.uploadFile = [];
      this.uploadForm = new FormData();
      this.improtFilesList = [];
      this.params = new FormData();
      this.$refs.tree.setCurrentKey(this.folderId)
    },
    async handleEditFolder() {
      console.log(8989, this.improtFilesList)
    },
    async handleImportFolder() {
      this.params = new FormData();
      if (this.uploadFile.length === 0) {
        this.$message({
          message: this.$t('selectUploadFile'),
          type: "warning",
        });
        return false;
      }
      this.importLoading = true;
      this.improtFilesList.forEach((item, index) => {
        this.params.append(`files[${index}].webLink`, item.webLink);
        this.params.append(`files[${index}].file`, this.uploadFile[index]);
      });
      this.params.append("foldersId", this.folderId);
      this.params.append("type", 0);
      const res = await addFile(this.params);
      if (res.code === "000000") {
        this.handCancelFolder();
        this.getFileListData(this.folderId);
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
      this.importLoading = false;
    },
    handleRemovefileItem(uid) {
      const newFiles = new FormData();
      for (let [key, value] of this.uploadForm.entries()) {
        console.log(key, value);
        if (value.uid !== uid) {
          newFiles.append(key, value);
        }
      }
      this.uploadForm = newFiles;
      this.uploadFile = this.uploadFile.filter((item) => item.uid !== uid);
      this.improtFilesList = this.improtFilesList.filter(
        (item) => item.uid !== uid
      );
    },
    async getFileListData(value) {
      this.tableLoading = true;
      const { data } = await getFileList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        folderId: value,
        knowledgeId: this.knowledgeId,
        fileName: this.fileName,
        fileTypes: [0],
      });
      this.tableData = data.records;
      this.total = data.totalRow;
      this.tableLoading = false;
    },
    handleCurrentChange(n) {
      this.pageNo = n;
      this.getFileListData(this.folderId);
    },
    handleSizeChange(n) {
      this.pageSize = n;
      this.getFileListData(this.folderId);
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
      this.isSelectedAll = val.length === this.pageSize ||  val.length === this.tableData.length
    },
    async deleteInBatches() {
      const res = await deleteFile({
        idList: this.multipleSelection.map((n) => n.fileId),
        type: 2,
      });
      if (res.code === "000000") {
        this.getFileListData(this.folderId);
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
    },
    remFileData(id) {
      this.fileId = id;
      this.remVisible = true;
    },
    async handleRemQaDialog() {
      this.remLoading = true;
      const res = await deleteFile({
        idList: [this.fileId],
        type: 2,
      });
      if (res.code === "000000") {
        this.remVisible = false;
        this.getFileListData(this.folderId);
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
    editDocument(row) {
      this.editWebLinkVisible = true;
      this.editId = row.id;
      this.editWebLink = row.webLink;
    },
    preview(row) {
      this.$refs.ComDocPreview.openPreview(row);
    },
    viewDocument(row) {
      this.fileData = row;
      this.documentVisible = true;
    },
    timeInBatches() {
      this.timeVisible = true;
    },
    changePassword(row) {
      this.filePassData = row;
      this.passwordVisible = true;
    },
    async handlePassword() {
      this.passwordLoading = true;
      const res = await updatePassword({
        ...this.filePassData,
        password: this.password,
      });
      if (res.code === "000000") {
        this.$message({
          message: res.msg,
          type: "success",
        });
        this.passwordVisible = false;
        this.passwordLoading = false;
      } else {
        this.$message({
          message: res.msg,
          type: "error",
        });
        this.passwordLoading = false;
      }
    },
    async handleEditWebLink() {
      this.editWebLinkLoading = true;
      const res = await updateFileWebLink({
        id: this.editId,
        webLink: this.editWebLink,
      });
      if (res.code === "000000") {
        this.$message({
          message: res.msg,
          type: "success",
        });
        this.editWebLinkVisible = false;
        this.editWebLinkLoading = false;
        this.getFileListData(this.folderId);
      } else {
        this.$message({
          message: res.msg,
          type: "error",
        });
        this.editWebLinkLoading = false;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./kbm.scss";
.left {
    width: 288px;
    float: left;
    height: 100%;
    flex-shrink: 0;
    border-right: 2px solid #f6f6f6;
    margin-right: 10px;
    padding: 10px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;

    .option {
      margin: 10px 0;
      font-weight: bold;
      font-size: 16px;
      color: #383d47;
      line-height: 28px;
      position: absolute;
      right: 0;
      z-index: 100;
      top: -16px;
    }

    .tree {
      flex-grow: 1;
      overflow: scroll;
      padding-right: 10px;

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
    }

    .folders-item {
      width: calc(100% - 24px);
      position: relative;
      .info {
        font-size: 15px;
        width: calc(100% - 20px);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: block;
        i {
          margin-right: 5px;
        }
      }
    }
  }
.document-view {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  .solid {
    width: 5px;
    height: 100%;
    background: #b4b0bb;
    margin-right: 10px;
    cursor: ew-resize;
  }
}

.file-con {
  max-height: 600px;
  overflow-y: scroll;
  margin-top: 12px;

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
}

.file-item {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  margin-top: 12px;
  align-items: center;

  span {
    width: 60%;
  }

  i {
    margin-left: 5px;
    cursor: pointer;
    font-size: 20px;
  }
}
.icon-import {
  svg {
    width: 16px;
    vertical-align: text-top;
    fill: #fff;
  }
  &:hover {
    svg {
      // fill: #1747E5;
    }
  }
}
::v-deep .el-upload-dragger {
  height: 120px;
  background: #F9FAFC;
  border-radius: 2px;
  border: 1px dotted rgba(0,0,0,0.12);
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
  .file-type {
    display: flex;
    align-items: center;
    span {
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
      animation: spin 2s linear infinite; /* 2s 为旋转一圈的时间，infinite 表示无限循环 */
    }
  }
}
.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
  position: relative;
  .tips{
    position: absolute;
    color: red;
    bottom: 8px;
    left: 0;
  }
}
.file-wrapper {
  display: flex;
  align-items: center;
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 14px;
  color: #494E57;
  line-height: 20px;
  .file-name {
    .name {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      -webkit-line-clamp: 1;
    }
    .size {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #BCC1CC;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      -webkit-line-clamp: 1;
    }
  }
  img {
    width: 24px;
    height: 24px;
    margin-right: 8px;
  }
  
}
.import-table {
  margin-bottom: 28px;
  ::v-deep .el-table__body-wrapper {
    height: auto !important;
    max-height: 100%;
  }
}
</style>

<style scoped>
/deep/ .el-drawer__body {
  overflow: hidden;
}

/deep/ .el-upload-dragger {
  width: 460px;
}
</style>