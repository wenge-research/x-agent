<template>
  <div class="document-view">
    <div class="left view-left-wrapper" v-loading="foldersLoading">
      <div class="left-search">
        <el-input
          size="small"
          :placeholder="$t('enterKnowledgetreeName')"
          v-model="filterText"
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
    <div class="view-right-wrapper">
      <div class="right-header">
        <div class="form-box">
          <el-form :inline="true">
            <el-form-item>
              <el-input
                v-model="fileName"
                size="small"
                :placeholder="$t('enterFileName')"
                clearable
                @input="handleSearch(folderId)"
              >
                <i slot="suffix" class="el-input__icon el-icon-search" />
              </el-input>
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
          <!-- <el-button
            type="primary"
            :disabled="multipleSelection.length === 0"
            icon="el-icon-setting"
            @click="timeInBatches"
            >{{ $t('batchSetExpirationTime') }}</el-button
          > -->
          <el-button class="icon-import" type="primary" icon="el-icon-plus" @click="importfolder">
            <!-- <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" d="m12 12.586 4.243 4.242-1.415 1.415L13 16.415V22h-2v-5.587l-1.828 1.83-1.415-1.415L12 12.586ZM12 2a7.001 7.001 0 0 1 6.954 6.194A5.5 5.5 0 0 1 18 18.978v-2.014a3.5 3.5 0 1 0-1.111-6.91 5 5 0 1 0-9.777 0 3.5 3.5 0 0 0-1.292 6.88l.18.03v2.014a5.5 5.5 0 0 1-.954-10.784A7 7 0 0 1 12 2Z" data-follow-fill="#848587"/></svg> -->
            {{$t('importFileData')}}</el-button
          >
        </div>
      </div>
      <!-- <div class="option-box">
        <span class="length-text"
          >{{$t('selected')}}<i>{{ multipleSelection.length }}</i></span
        >
        <div class="popconfirm">
          <el-popconfirm :title="$t('confirmDelete')"@confirm="deleteInBatches">
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
          <el-button
            type="primary"
            :disabled="multipleSelection.length === 0"
            icon="el-icon-setting"
            size="small"
            @click="timeInBatches"
            >{{ $t('batchSetExpirationTime') }}</el-button
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
          :data="tableData"
          style="width: 100%"
          height="100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column :label="$t('fileName')" width="">
            <template slot-scope="scope">
              <img
                v-if="scope.row.type == 2"
                src="@/assets/images/icon-picture.svg"
                style="width: 20px; height: 20px; vertical-align: middle"
              />
              <img
                v-else-if="scope.row.type == 3"
                src="@/assets/images/icon-video.svg"
                style="width: 20px; height: 20px; vertical-align: middle"
              />
              <img
                v-else
                src="@/assets/images/icon-mp3.svg"
                style="width: 20px; height: 20px; vertical-align: middle"
              />
              <span style="margin-left: 4px">{{ scope.row.fileName }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="wordCount"
            :label="$t('characterCount')"
            width="80"
          ></el-table-column>
          <!-- <el-table-column :label="$t('validDuration')" width="160">
            <template slot-scope="scope">
              <i
                class="el-icon-setting"
                style="cursor: pointer"
                @click="timeInBatches(scope.row)"
              ></i>
              <span style="margin-left: 10px">{{ scope.row.periodEnd }}</span>
            </template>
          </el-table-column> -->
          <el-table-column
            prop="createTime"
            :label="$t('uploadTime')"
            width="200"
          ></el-table-column>
          <!-- <el-table-column label="启用状态" width="120">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.type"> </el-switch>
            </template>
          </el-table-column> -->
          <el-table-column prop="status" :label="$t('status')" width="120">
            <!-- <template slot="header" slot-scope="scope">
              <div style="display: flex;align-items: center;">
                <div>{{ $t('status') }}</div>
                <el-tooltip class="item" effect="dark" :content="$t('noticeTip')" placement="top">
                  <img width="14" style="margin-left: 4px" src="@/assets/images/question-line.svg" alt="">
                </el-tooltip>
              </div>
            </template> -->
            <template slot-scope="scope">
              <div class="file-type" style="display: flex;align-items: center;">
                <span
                  :style="{ 
                    backgroundColor: FILE_TYP[scope.row.status].color, 
                    display: 'inline-block',
                    width: '8px',
                    height: '8px',
                    borderRadius: '50%',
                    marginRight: '8px', }"
                ></span>
                {{ FILE_TYP[scope.row.status].label }}
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
          <!-- <el-table-column
            prop="errorMsg"
            :label="$t('failureReason')"
            width="160"
          ></el-table-column>
          <el-table-column
            prop="errorNum"
            :label="$t('retryCount')"
            width="120"
          ></el-table-column> -->
          <el-table-column fixed="right" :label="$t('action')" width="200">
            <template slot-scope="scope">
              <div class="btns">
                <el-button
                  type="text"
                  :disabled="scope.row.status !== 2"
                  @click="viewDocument(scope.row)"
                  >{{$t('viewSlice')}}</el-button
                >
                <el-button
                  type="text"
                  @click="preview(scope.row)"
                  >{{$t('preview')}}</el-button
                >
                <!-- <el-button type="text">预览</el-button> -->
                <el-button type="text" @click="remFileData(scope.row.fileId)"
                  >{{$t('delete')}}</el-button
                >
                <el-button :disabled="![1,2,3,5].includes(scope.row.status)" type="text" @click="retryFileData(scope.row.fileId)"
                  >{{$t('retry')}}</el-button
                >
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
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
      v-if="importVisible"
      class="my-dialog"
      :title="$t('importFiles')"
      :visible.sync="importVisible"
      width="500px"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <div class="upload-box">
        <div
          class="file-type"
          @click="changeUploadFileType(1)"
          :class="{ active: fileMdiaType === 1 }"
        >
          <img src="@/assets/images/icon-mp3.svg" />
          <span>{{$t('uploadAudio')}}</span>
        </div>
        <div
          class="file-type"
          @click="changeUploadFileType(3)"
          :class="{ active: fileMdiaType === 3 }"
        >
          <img src="@/assets/images/icon-video.svg" />
          <span>{{$t('uploadVideo')}}</span>
        </div>
        <div
          class="file-type"
          @click="changeUploadFileType(2)"
          :class="{ active: fileMdiaType === 2 }"
        >
          <img src="@/assets/images/icon-picture.svg" />
          <span>{{$t('uploadImage')}}</span>
        </div>
      </div>
      <el-upload
        action="#"
        :before-upload="beforeupload"
        drag
        multiple
        :show-file-list="false"
      >
        <img class="icon-upload" src="@/assets/images/upload-cloud-2-fill.svg" alt="">
        <div class="el-upload__text">{{$t('dragFileHere')}}，{{$t('or')}}{{$t('selectFile')}}</div>
        <div class="el-upload__condition" v-if="fileMdiaType == 1">
          {{$t('supportedAudioFormats')}}
        </div>
        <div class="el-upload__condition" v-else-if="fileMdiaType == 3">
          {{$t('supportedVideoFormat')}}
        </div>
        <div class="el-upload__condition" v-else>
          {{$t('supportedImageFormats')}}      
        </div>
      </el-upload>
      <!-- <div style="margin-top: 16px">
        <input
          type="file"
          id="folderInput"
          webkitdirectory
          directory
          multiple
          @change="handleFiles"
        />
      </div> -->
      <!-- <h3 class="file-list">{{$t('fileList')}}</h3> -->
      <div class="file-con">
        <div
          class="file-item"
          v-for="(item, index) in uploadFile"
          :key="item.uid"
        >
          <div class="file-left">
            <img v-if="fileMdiaType == 1" src="@/assets/images/icon-mp3.svg" />
            <img v-if="fileMdiaType == 3" src="@/assets/images/icon-video.svg" />
            <img v-if="fileMdiaType == 2" src="@/assets/images/icon-picture.svg" />
            <div class="file-name">
              <div class="name">{{ item.name }} </div>
              <div class="size">{{ (item.size / 1024).toFixed(2) }}kb </div>
            </div>
          </div>
          <span class="file-delete-icon">
            <i
              @click="handleRemovefileItem(item, index)"
              class="el-icon-close"
            ></i>
          </span>
        </div>
      </div>
      <div slot="footer" class="dialog-footer my-dialog-footer">
        <el-button :loading="importLoading" @click="handCancelFolder"
          >{{ $t('cancel') }}</el-button>
        <el-button
          :loading="importLoading"
          type="primary"
          @click="handleImportFolder"
          >{{$t('confirmImport')}}</el-button
        >
      </div>
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
      size="90%"
      :with-header="false"
      append-to-body
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :wrapperClosable="false"
      :close-on-press-escape="false"
    >
      <MultiMediaInfo
        @close="handleClose"
        :knowledgeId="knowledgeId"
        :rowData="fileData"
      />
    </el-drawer>

    <el-dialog
      class="my-dialog"
      :title="$t('validDurationSetting')"
      :visible.sync="timeVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <div>{{$t('knowledgeExpiration')}}</div>
      <div style="margin-bottom: 10px"></div>
      <el-date-picker
        v-model="validTime"
        type="datetime"
        :placeholder="$t('selectDateTime')"
      ></el-date-picker>
      <div slot="footer" class="dialog-footer my-dialog-footer">
        <el-button :loading="timeLoading" @click="timeVisible = false"
          >{{ $t('cancel') }}</el-button>
        <el-button :loading="timeLoading" type="primary">{{ $t('confirm') }}</el-button>
      </div>
    </el-dialog>
    <Tree ref="myTree" v-if="dialogVisible" :dialogVisible.sync="dialogVisible" :knowledgeId="knowledgeId" :type="6" @cancel="getFoldersListData"></Tree>
    <ComFilePreview ref="ComFilePreview"></ComFilePreview>
  </div>
</template>

<script>
import MultiMediaInfo from "@/views/Kbm/component/MultiMediaInfo.vue";
import { FILE_TYP } from "@/views/Kbm/content/index";
import ComFilePreview from '@/components/ComFilePreview'
import Tree from './Tree.vue'

import {
  getFoldersList,
  addFolders,
  deleteFolders,
  getFileList,
  addFile,
  deleteFile,
  singleMediumAnalysis,
  addImage,
  addAudio,
  addVideo,
  apiSingleMediumAnalysis
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
    MultiMediaInfo, ComFilePreview, Tree
  },
  data() {
    return {
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
      documentVisible: false,
      importVisible: false,
      importLoading: false,
      folderName: "",
      folderParentId: "",
      folderId: "",
      tableLoading: false,
      folders: [],
      multipleSelection: [],
      foldersLoading: false,
      fileName: "", // 文件名称
      urlId: "",
      uploadForm: new FormData(),
      uploadFile: [],
      fileId: "",
      fileData: {},
      fileMdiaType: 1, //上传多媒体文件类型
      folderDataId: null,
      files: "", //文件夹
      isAll: true,
      operationLogVisible: false,
      dialogVisible: false,
      isSelectedAll: false,
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
    // 重试
    async retryHandler(fileId) {
      this.iconLoading = true;
      try {
        const res = await apiSingleMediumAnalysis({
        fileId,
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
      this.retryHandler(row.fileId)
      this.currentId = row.id;
    },
    handleSelectedAll() {
      this.$refs.table.toggleAllSelection();
    },
    handleClearSelection() {
      this.$refs.table.clearSelection();
    },
    handleSearch: debounce(function(folderId) {
      this.getFileListData(folderId)
    }, 500),
    handleAllClick() {
      this.folderId = ""
      this.isAll = true
      this.$nextTick(() => {
        this.$refs.tree.setCurrentKey(null);
      })
      this.getFileListData('');
    },
    handleFiles(event) {
      this.files = event.target.files; // 获取文件列表
      for (let i = 0; i < this.files.length; i++) {
        this.uploadForm.append("files", this.files[i]);
        this.uploadFile.push(this.files[i]);
      }
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
        pageSize: 200,
        knowledgeId: this.knowledgeId,
        type: 6,
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
        type: 6,
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
    },
    remfolder({ data }) {
      this.remfolderVisible = true;
      this.folderId = data.foldersId;
    },
    async handleRemFolder() {
      this.remfolderLoading = true;
      const res = await deleteFolders({
        foldersIdList: [this.folderId],
        type: 6,
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
        type: 6,
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
    changeUploadFileType(type) {
      if (this.uploadFile.length > 0) {
        this.$message.warning(this.$t('uploadSameFileType'));
      } else {
        this.fileMdiaType = type;
      }
    },
    importfolder({ data }) {
      // this.folderId = data.foldersId;
      this.importVisible = true;
      this.fileMdiaType = 1;
    },
    beforeupload(file) {
      const fileType = file.type;
      const allowedTypes =
        this.fileMdiaType == 1
          ? ["audio/mp3", "audio/mpeg", "audio/wav"]
          :  this.fileMdiaType == 3
          ? ["video/mp4"]
          : ["image/jpeg", "image/png", "image/jpg"]; // 允许上传的文件类型
      const maxSize = 500 * 1024 * 1024; //文件大小
      if (!allowedTypes.includes(fileType)) {
        let msg =
          this.fileMdiaType == 1
            ? this.$t('supportedAudioFormatsOnly')
            : this.fileMdiaType == 3
            ? this.$t('supportedVideoFormatOnly')
            : this.$t('supportedImageFormatsOnly');
        this.$message.warning(msg);
        return false; // 阻止文件上传
      } else if (file.size > maxSize) {
        this.$message.warning(
          this.$t('fileSizeExceeds') + maxSize / (1024 * 1024) + "MB" + this.$t('limit')  
                       );
        return false; // 阻止文件上传
      } else {
        this.uploadForm.append("files", file);
        this.uploadFile.push(file);
        return false;
      }
    },
    handCancelFolder() {
      this.importVisible = false;
      this.uploadFile = [];
      this.uploadForm = new FormData();
      // this.clearInput();
    },
    async handleImportFolder() {
      if (this.uploadFile.length === 0) {
        this.$message({
          message: this.$t('selectUploadFile'),
          type: "warning",
        });
        return false;
      }
      this.importLoading = true;
      this.uploadForm.append("foldersId", this.folderId);
      let api = this.fileMdiaType == 1 ? addAudio : this.fileMdiaType == 3 ? addVideo : addImage;
      const res = await api(this.uploadForm);
      if (res.code === "000000") {
        this.handCancelFolder();
        this.getFileListData(this.folderId);
        // this.clearInput();
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
    clearInput() {
      document.getElementById("folderInput").value = "";
    },
    handleRemovefileItem(data) {
      const newFiles = new FormData();
      for (let [key, value] of this.uploadForm.entries()) {
        if (
          value.uid !== data.uid ||
          value.lastModified !== data.lastModified
        ) {
          newFiles.append(key, value);
        }
      }
      this.uploadForm = newFiles;
      this.uploadFile = this.uploadFile.filter(
        (item) =>
          item.uid !== data.uid || item.lastModified !== data.lastModified
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
        fileTypes: [1, 2, 3],
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
        type: 6,
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
    async retryFileData(id) {
      this.remLoading = true;
      const res = await singleMediumAnalysis({
        fileId: id,
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
    async handleRemQaDialog() {
      this.remLoading = true;
      const res = await deleteFile({
        idList: [this.fileId],
        type: 6,
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
    viewDocument(row) {
      this.fileData = row;
      this.documentVisible = true;
    },
    preview(row) {
      this.$refs.ComFilePreview.openPreview(row.fileUrl, row.fileType,'');
    },
    timeInBatches() {
      this.timeVisible = true;
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./kbm.scss";

.document-view {
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.left {
    float: left;
    width: 288px;
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

  .right {
    display: flex;
    float: left;
    width: calc(100% - 298px);
    flex-direction: column;
    height: 100%;
    .form-box {
      .el-form-item {
        margin-bottom: 0px;
      }
    }
    .float-right {
      float: right;
    }

    .option-box {
      display: flex;
      flex-direction: row;
      align-items: center;
      margin: 10px;

      .popconfirm {
        margin: 0 20px;
      }

      .length-text {
        font-size: 16px;
        color: #828894;
        i {
          color: #409eff;
          padding: 0px 5px;
        }
      }
    }

    .table-box {
      flex-grow: 1;
      /* Vue 2 */
      ::v-deep .el-table__body-wrapper {
        overflow-x: auto;
      }
      .file-type {
        display: flex;
        align-items: center;
        span {
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          margin-right: 8px;
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

      .btns {
        display: flex;
        justify-content: center;
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
  }

.file-con {
  max-height: 300px;
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
}

.file-item {
  height: 56px;
  padding: 8px 16px 8px 12px;
  background: #FFFFFF;
  border-radius: 2px;
  border: 1px solid #D5D8DE;
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  margin-top: 16px;
  align-items: center;
  &:first-child {
    margin-top: 12px;
  }
  .file-left {
    display: flex;
    align-items: center;
    img {
      width: 20px;
      margin-right: 12px;
    }
    .name {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
    }
    .size {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #828894;
      line-height: 16px;
    }
  }
  .file-delete-icon {
    display: inline-block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    border-radius: 50%;
    background: #BCC1CC;
    .el-icon-close {
      color: #fff;
      font-size: 12px;
    }
  }
  span {
    width: 90%;
  }

  i {
    cursor: pointer;
    font-size: 20px;
  }
}
.upload-box {
  display: flex;
  margin-bottom: 16px;
  cursor: pointer;
  .file-type {
    width: 164px;
    height: 36px;
    flex: 1;
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #828894;
    line-height: 24px;
    background: #F7F8FA;
    border-radius: 2px;

    img {
      height: 20px;
      width: 20px;
      margin-right: 6px;
    }

    &.active {
      background: #FFFFFF;
      color: #494E57;
      box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
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
.file-list {
  margin: 20px 0px;
  color: #383d47;
  font-size: 18px;
  font-weight: 500;
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