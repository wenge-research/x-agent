<template>
  <div class="view-left-wrapper" v-loading="foldersLoading">
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
        </div>
      </el-tree>
      <div class="tree-btn" @click="dialogVisible = true">
        <img :src="require('@/assets/images/archive-line.svg')" alt="">
        <span class="classisfy-btn">
          {{ $t('sortMnagement') }}
        </span>
      </div>
      <Tree ref="myTree" v-if="dialogVisible" :dialogVisible.sync="dialogVisible" :knowledgeId="knowledgeId" :type="type" @cancel="handleTreeCancel"></Tree>
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
import Tree from './Tree.vue'
export default {
  props: {
    type: {
      type: Number,
      default: 2,
    },
    knowledgeId: {
      type: String,
      default: "",
    },
  },
  components: {
    Tree
  },
  computed: {
  },
  mounted() {
  },
  data() {
    return {
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },
      foldersLoading: false,
      folders: [],
      filterText: "",
      isAll: true,
      folderId: "",
      dialogVisible: false,
     
    }
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
    handleTreeCancel() {
      this.getFoldersListData();
      this.$emit('cancel')
    },
    async getFoldersListData() {
      this.foldersLoading = true;
      const { data } = await getFoldersList({
        pageNo: 1,
        pageSize: 9999,
        knowledgeId: this.knowledgeId,
        type: this.type
      });
      this.folders = this.formatFolderListData(data.records);
      this.folderId = data.records.find((item) => {
        return item.name == '全部'
      })?.foldersId
      this.$nextTick(() => {
        this.$refs.tree.setCurrentKey(this.folderId);
      })
      this.$emit('change', this.folderId)
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
      this.isAll = false
      this.$emit('change', this.folderId)
    },
    handleAllClick() {
      this.folderId = ""
      this.isAll = true
      this.$nextTick(() => {
        this.$refs.tree.setCurrentKey(null);
      })
      this.$emit('change', '')
    },
  }
}
</script>

<style lang="scss" scoped>
@import "./kbm.scss";

</style>
