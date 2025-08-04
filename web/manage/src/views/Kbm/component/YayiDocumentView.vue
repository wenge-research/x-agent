<template>
  <div class="document-view">
    <div class="left" v-loading="foldersLoading">
      <el-input
        size="small"
        :placeholder="$t('inputKeywordFilter')"
        v-model="filterText"
      >
      </el-input>
      <div class="option">
        <span>{{$t('all')}}</span>
        <el-button
          icon="el-icon-circle-plus"
          type="text"
          @click="addfolder('root')"
          >{{$t('add')}}</el-button
        >
      </div>
      <div class="tree">
        <el-tree
          :expand-on-click-node="false"
          :data="folders"
          :filter-node-method="filterNode"
          default-expand-all
          ref="tree"
          @node-click="handleFolderChange"
        >
          <div class="folders-item" slot-scope="{ node, data }">
            <span class="info" :title="node.label">
              <!-- <i :class="node.expanded ? 'el-icon-folder-opened' : 'el-icon-folder'"></i> -->
              {{ node.label }}
            </span>
            <span class="option">
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
            </span>
          </div>
        </el-tree>
      </div>
    </div>
    <!-- <div class="solid" @mousedown="onMouseDown"></div> -->
    <div class="right" :style="{ width: rightWidth + 'px' }">
      <div class="form-box">
        <el-form :inline="true">
          <el-form-item>
            <el-input
              v-model="fileName"
              size="small"
              :placeholder="$t('pleaseEnterFileName')"
            >
              <i slot="suffix" class="el-input__icon el-icon-search" />
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="small"
              @click="getFileListData(folderId)"
              >{{$t('search')}}</el-button
            >
          </el-form-item>
        </el-form>
      </div>
      <div class="option-box">
        <div>
<!--          <el-popconfirm title="请确认是否删2除" @onConfirm="deleteInBatches">-->
<!--            <el-button-->
<!--              :disabled="multipleSelection.length === 0"-->
<!--              slot="reference"-->
<!--              type="danger"-->
<!--              icon="el-icon-delete"-->
<!--              size="small"-->
<!--              >批量删除</el-button-->
<!--            >-->
<!--          </el-popconfirm>-->
        </div>
        <div>
          <!-- <el-button type="primary" :disabled="multipleSelection.length === 0" icon="el-icon-setting" size="small"
            @click="timeInBatches">批量设定失效时间</el-button> -->
          <el-button type="primary" icon="el-icon-s-order" size="small"
            >{{$t('operationLog')}}</el-button
          >
        </div>
      </div>
      <div class="table-box">
        <el-table
          v-loading="tableLoading"
          border
          stripe
          style="width: 100%"
          :data="tableData"
          height="100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column
            prop="fileName"
            :label="$t('fileName')"
            width="200"
          ></el-table-column>
          <el-table-column
            prop="wordCount"
            :label="$t('characterCount')"
            width=""
          ></el-table-column>
          <el-table-column
            prop="paragraphsNum"
            :label="$t('segmentCount')"
            width=""
          ></el-table-column>
          <el-table-column
            prop="createTime"
            :label="$t('uploadTime')"
            width="200"
          ></el-table-column>
          <el-table-column prop="status" :label="$t('fileStatus')" width="100">
            <template slot-scope="scope">
              <div class="file-type">
                <span
                  :style="{ backgroundColor: FILE_TYP[scope.row.status].color }"
                ></span>
                {{ FILE_TYP[scope.row.status].label }}
                <img
                  v-if="scope.row.status === 6"
                  src="@/assets/images/alert-fill.svg"
                  style="width: 15px; height: 15px; vertical-align: middle"
                  @click="changePassword(scope.row)"
                />
              </div>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('operation')" width="230">
            <template slot-scope="scope">
              <div class="btns">
                <!-- <el-button type="text" @click="editDocument(scope.row)"
                  >{{$t('editLink')}}</el-button
                > -->
                <el-button type="text" @click="viewDocument(scope.row)"
                  >{{$t('viewSlice')}}</el-button
                >
                <el-button type="text" @click="remFileData(scope.row.fileId)"
                  >{{$t('delete')}}</el-button
                >
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-size="pageSize"
          :current-page="pageNo"
          :page-sizes="[20, 50, 100, 150, 200]"
          layout="total, sizes, prev, pager, next, jumper"
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
    >
      <el-input
        v-model="folderName"
        size="small"
        :placeholder="$t('pleaseEnterDirectoryName')"
      />
      <span slot="footer" class="dialog-footer">
        <el-button :loading="addfolderLoading" @click="addfolderVisible = false"
          >{{$t('cancel')}}</el-button
        >
        <el-button
          :loading="addfolderLoading"
          type="primary"
          @click="handleAddFolder"
          >{{$t('confirm')}}</el-button
        >
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
    >
      <span>{{$t('confirmDelete')}}</span>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="remfolderLoading" @click="remfolderVisible = false"
          >{{$t('cancel')}}</el-button
        >
        <el-button
          :loading="remfolderLoading"
          type="primary"
          @click="handleRemFolder"
          >{{$t('confirm')}}</el-button
        >
      </span>
    </el-dialog>

    <el-dialog
      :title="$t('ameFolder')"
      :visible.sync="renfolderVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
    >
      <el-input
        v-model="folderName"
        size="small"
        :placeholder="$t('pleaseEnterDirectoryName')"
      />
      <span slot="footer" class="dialog-footer">
        <el-button :loading="renfolderLoading" @click="renfolderVisible = false"
          >{{$t('cancel')}}</el-button
        >
        <el-button
          :loading="renfolderLoading"
          type="primary"
          @click="handleRenFolder"
          >{{$t('confirm')}}</el-button
        >
      </span>
    </el-dialog>

    <el-dialog
      :title="$t('importFile')"
      :visible.sync="importVisible"
      width="500px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
    >
      <el-upload
        action="#"
        :before-upload="beforeupload"
        drag
        multiple
        :show-file-list="false"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">{{ $t('clickUpload') }}</div>
      </el-upload>
      <div class="file-con">
        <div class="file-item" v-for="item in improtFilesList" :key="item.uid">
          <span class="name">{{ item.name }} </span>
          <el-input
            style="width: 260px"
            v-model="item.webLink"
            :placeholder="$t('pleaseEnterFileLink')"
            size="small"
          ></el-input>
          <i @click="handleRemovefileItem(item.uid)" class="el-icon-close"></i>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="importLoading" @click="handCancelFolder"
          >{{$t('cancel')}}</el-button
        >
        <el-button
          :loading="importLoading"
          type="primary"
          @click="handleImportFolder"
          >{{$t('confirm')}}</el-button
        >
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
    >
      <span>{{$t('confirmDelete')}}</span>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="remLoading" @click="remVisible = false"
          >{{$t('cancel')}}</el-button
        >
        <el-button
          :loading="remLoading"
          type="primary"
          @click="handleRemQaDialog"
          >{{$t('confirm')}}</el-button
        >
      </span>
    </el-dialog>

    <el-drawer
      :visible.sync="documentVisible"
      size="100%"
      :with-header="false"
      append-to-body
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :wrapperClosable="false"
      :close-on-press-escape="false"
    >
      <DocumentInfo
        @close="handleClose"
        :knowledgeId="knowledgeId"
        :rowData="fileData"
      />
    </el-drawer>

    <el-dialog
      :title="$t('validityPeriodSetting')"
      :visible.sync="timeVisible"
      width="400px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
    >
      <div>{{$t('afterExpiryNotSearched')}}</div>
      <div style="margin-bottom: 10px"></div>
      <el-date-picker
        v-model="validTime"
        type="datetime"
        :placeholder="$t('selectDateTime')"
      ></el-date-picker>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="timeLoading" @click="timeVisible = false"
          >{{$t('cancel')}}</el-button
        >
        <el-button :loading="timeLoading" type="primary">{{$t('confirm')}}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t('editLink')"
      :visible.sync="editWebLinkVisible"
      width="400px"
      :modal-append-to-body="false"
      :show-close="false"
      :close-on-press-escape="false"
    >
      <el-input v-model="editWebLink" size="small"  />
      <span slot="footer" class="dialog-footer">
        <el-button @click="editWebLinkVisible = false">{{$t('cancel')}}</el-button>
        <el-button
          :loading="editWebLinkLoading"
          type="primary"
          @click="handleEditWebLink"
          >{{$t('confirm')}}</el-button
        >
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
    >
      <el-input v-model="password" size="small" :placeholder="$t('pleaseEnterFilePassword')" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="passwordVisible = false">{{$t('cancel')}}</el-button>
        <el-button
          :loading="passwordLoading"
          type="primary"
          @click="handlePassword"
          >{{$t('confirm')}}</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import DocumentInfo from "@/views/Kbm/component/DocumentInfo.vue";
import { FILE_TYP } from "@/views/Kbm/content/index";
import {
  getFoldersList,
  addFolders,
  deleteFolders,
  getFileList,
  addFile,
  deleteFile,
  updatePassword,
  updateFileWebLink,
} from "@/api/index.js";
export default {
  props: {
    knowledgeId: {
      type: String,
      default: "",
    },
  },
  components: {
    DocumentInfo,
  },
  data() {
    return {
      leftWidth: "",
      rightWidth: "",
      FILE_TYP,
      filterText: "",
      tableLoading: false,
      pageNo: 1,
      pageSize: 50,
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
    };
  },

  created() {
    this.getFileListData("");
    this.getFoldersListData();
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
  },
  methods: {
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
        type: 7,
      });
      this.folders = this.formatFolderListData(data.records);
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
        type: 7,
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
    importfolder({ data }) {
      this.folderId = data.foldersId;
      this.importVisible = true;
    },
    beforeupload(file) {
      this.uploadForm.append("files", file);
      this.uploadFile.push(file);
      let obj = {
        name: file.name,
        webLink: "",
        uid: file.uid,
      };
      this.improtFilesList.push(obj);
      return false;
    },
    handCancelFolder() {
      this.importVisible = false;
      this.uploadFile = [];
      this.uploadForm = new FormData();
      this.improtFilesList = [];
      this.params = new FormData();
    },
    async handleImportFolder() {
      this.params = new FormData();
      if (this.uploadFile.length === 0) {
        this.$message({
          message: this.$t('pleaseSelectUploadFile'),
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
      this.params.append("type", 4);
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
        fileTypes: [4],
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
.document-view {
  width: 100%;
  height: 100%;
  overflow: hidden;

  .solid {
    width: 5px;
    height: 100%;
    background: #b4b0bb;
    margin-right: 10px;
    cursor: ew-resize;
  }

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

    .option-box {
      display: flex;
      margin: 10px 0;
      justify-content: space-between;
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
          width: 5px;
          height: 5px;
          border-radius: 50%;
          margin-right: 10px;
        }
      }

      .btns {
        display: flex;
        justify-content: center;
      }
    }
  }
}

.file-con {
  max-height: 300px;
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
</style>

<style scoped>
/deep/ .el-drawer__body {
  overflow: hidden;
}

/deep/ .el-upload-dragger {
  width: 460px;
}
</style>