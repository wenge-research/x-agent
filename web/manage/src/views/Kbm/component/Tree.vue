<template>
  <div class="space-dialog">
    <el-dialog
      :title="$t('operateTopic')"
      :visible.sync="dialogVisible"
      :before-close="handleCancel"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      append-to-body
      width="48%"
      class="my-dialog"
    >
    <el-tree
        :expand-on-click-node="false"
        :data="folders"
        default-expand-all
        ref="tree"
         v-loading="foldersLoading"
      >
        <div class="theme-item" slot-scope="{ node, data }">
          <!-- 编辑 -->
          <div v-if="renthemeVisible && folderDataId == node.data.id && !isAddingChild" class="info" :class="{ 'theme-name-input': renthemeVisible && folderDataId == node.data.id }">
            <el-input
              ref="folderName"
              v-model="folderName"
              :placeholder="$t('enterKnowledgetreeName')"
              clearable
              @blur="handleRenTheme"
            >
            </el-input>
          </div>
          <!-- 新增子节点 -->
          <div v-else-if="data.isAddingChild" class="add-child-input">
            <el-input ref="newChildInput" v-model="newChildName" :placeholder="$t('pleaseEnter')" @blur="handleAddTheme(newChildName)" style="width: 100%"></el-input>
          </div>
          <!-- node节点展示 -->
          <template v-else>
            <span  class="info" :title="node.label">
              <!-- <img v-if="node.expanded" class="folder-opened" :src="require('@/assets/images/folder-open.svg')" alt="">
              <img v-else class="folder" :src="require('@/assets/images/folder.svg')" alt=""> -->
              <!-- <i :class="node.expanded ? 'el-icon-folder-opened' : 'el-icon-folder'"></i> -->
              {{ node.label }}
            </span>
            <div class="options">
              <i class="icon el-icon-plus" @click="addtheme(node)"></i>
              <img v-if="node.label !== '全部'" class="icon" src="@/assets/images/edit-line2.svg" alt="" @click="rentheme(node)"> 
              <i v-if="node.label !== '全部'" class="icon el-icon-delete" @click="remfolder(node)"></i>
              <!-- <el-dropdown>
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
              </el-dropdown> -->
            </div>
          </template>
          
        </div>
      </el-tree>
        <!-- @blur="addTheme('root')" -->
        <el-input
        v-if="addRootVisible"
        ref="folderName"
        v-model="folderName"
        :placeholder="$t('pleaseEnter')"
        clearable
        @blur="handleAddTheme(folderName)"
      >
      </el-input>
      <!-- <div class="tree-btn">
        <el-button type="text" icon="el-icon-plus" @click="addtheme('root')">{{$t('addFirstLevel')}}</el-button>
      </div> -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="$t('deleteTopic')"
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
  </div>
</template>

<script>
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
    dialogVisible: {
      type: Boolean
    },
    knowledgeId: {
      type: String,
      default: "",
    },
    type: {
      type: Number,
      default: 2,
    },
  },
  computed: {
  },
  mounted() {
  },
  data() {
    return {
      folders: [],
      foldersLoading: false,
      renthemeVisible: false,
      folderId: '',
      folderParentId: '',
      folderName: '',
      newChildName: '',
      isAddingChild: false,
      addRootVisible: false,
      folderDataId: '',
      remfolderVisible: false,
      remfolderLoading: false,
    }
  },
  created() {
    this.getFoldersListData()
  },
  methods: {
    async handleRemFolder() {
      this.remfolderLoading = true;
      const res = await deleteFolders({
        foldersIdList: [this.folderId],
        type: this.type,
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
    remfolder({ data }) {
      this.remfolderVisible = true;
      this.folderId = data.foldersId;
    },
    async handleRenTheme() {
      this.renfolderLoading = true;
      const res = await addFolders({
        name: this.folderName,
        foldersId: this.folderId,
        knowledgeId: this.knowledgeId,
        parentId: this.folderParentId,
        id: this.folderDataId,
        type: this.type,
      });
      if (res.code === "000000") {
        this.addRootVisible = false
        this.renthemeVisible = false
        this.isAddingChild = false
        this.folderName = ''
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
    async handleAddTheme(name) {
      name ? this.folderName = name : ''
      this.addthemeLoading = true;
      if (!this.folderName) {
        return
      }
      const res = await addFolders({
        knowledgeId: this.knowledgeId,
        name: this.folderName,
        parentId: this.folderParentId,
        type: this.type,
      });
      if (res.code === "000000") {
        this.addRootVisible = false
        this.folderName = ''
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
      this.addthemeLoading = false;
    },
    rentheme({ data }) {
      this.addRootVisible = false
      this.isAddingChild = false
      this.renthemeVisible = true
      // 调用递归函数重置 isAddingChild 属性
      this.removeNodesWithIsAddingChild(this.folders);
      this.folderId = data.foldersId;
      this.folderParentId = data.parentId;
      this.folderName = data.label;
      this.folderDataId = data.id;
      // this.$set(node.data, 'isAddingChild', false)
      // this.addFirstLevelVisible = false
      this.$nextTick(() => {
        this.$refs.folderName.focus()
      })
    },
    resetIsAddingChild(nodes) {
      nodes.forEach(node => {
        if (node.isAddingChild !== undefined) {
          this.$set(node, 'isAddingChild', false);
        }
        if (node.children && node.children.length > 0) {
          this.resetIsAddingChild(node.children);
        }
      });
    },
    addtheme(node) {
      this.folderName = ''
      if (node === 'root') {
        this.folderParentId = "0"
        // 调用递归函数重置 isAddingChild 属性
        this.isAddingChild = false
        this.removeNodesWithIsAddingChild(this.folders);
        this.renthemeVisible = false
        this.themenName = ''

        this.addRootVisible = true
        
        this.$nextTick(() => {
          this.$refs.folderName.focus()
        })
      } else {
        this.addRootVisible = false
        this.renthemeVisible = false
        // 设置当前节点的 isAddingChild 为 true
        node.data.children ? '' : this.$set(node.data, 'children', []);
        if (node.data.children.findIndex(item => item.isAddingChild) === -1) {
          // 调用递归函数重置 isAddingChild 属性
          this.removeNodesWithIsAddingChild(this.folders);
          node.data.children.unshift({  isAddingChild: true, type: '' })
          this.isAddingChild = true
        }
        // this.$set(node.data.children, 'isAddingChild', true);
        this.folderParentId = node.data.foldersId;
        // 清空新子节点名称
        this.newChildName = '';
        // 确保输入框聚焦
        this.$nextTick(() => {
          this.$refs.newChildInput.focus();
        });
      }
    },
    removeNodesWithIsAddingChild(nodes) {
      for (let i = nodes.length - 1; i >= 0; i--) {
        if (nodes[i].isAddingChild === true) {
          nodes.splice(i, 1);
        } else if (nodes[i].children && nodes[i].children.length > 0) {
          this.removeNodesWithIsAddingChild(nodes[i].children);
        }
      }
    },
    async getFoldersListData() {
      this.foldersLoading = true;
      const { data } = await getFoldersList({
        pageNo: 1,
        pageSize: 9999,
        knowledgeId: this.knowledgeId,
        type: this.type,
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
    handleFolderChange(data, node, ary) {
      this.folderId = data.foldersId;
    },
    handleSubmit() {
      this.$emit('update:dialogVisible', false)
      this.$emit('cancel')
    },
    handleCancel() {
      this.$emit('update:dialogVisible', false)
      this.$emit('cancel')
    },
  }
}
</script>

<style lang="scss" scoped>
  ::v-deep .el-tree {
    height: calc(100% - 150px);
    max-height: 400px;
    overflow-y: auto;
    .el-tree__empty-block {
      display: none;
    }
    .el-tree-node:focus > .el-tree-node__content {
      background: #F0F2F5;
      border-radius: 8px;
    }
    .el-tree-node__content {
      height: 40px;
      margin-bottom: 4px;
      border-radius: 2px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px !important;
      color: #494E57;
      line-height: 20px;
      // padding: 8px 12px;
      &:hover {
        background: #F0F2F5;
        border-radius: 8px;
      }
    }
    .theme-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      flex: 1;
    }
    .info {
      display: flex;
      align-items: center;
      img {
        width: 18px;
        margin-right: 8px;
      }
    }
    .theme-name-input {
      width: 100%;
    }
    .add-child-input {
      width: 100%;
    }
    .options {
      display: flex;
      align-items: center;
      .icon {
        margin-right: 4px;
        width: 16px;
        height: 16px;
        font-size: 16px;
        color: #828894;
        cursor: pointer;  
      }
    }
  }
</style>
