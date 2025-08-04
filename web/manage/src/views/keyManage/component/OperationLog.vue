<template>
  <div class="log-drawer">
    <el-drawer class="my-drawer" :title="$t('operationLog')" :visible.sync="visible" :modal-append-to-body="false" :close-on-click-modal="false"
      :show-close="false" :close-on-press-escape="false" :wrapperClosable="false" size="29.2%">
      <div slot class="operation-box">
        <div class="operation-content">
          <div class="log-item">
            <div class="log-title"></div>
            <div class="log-file">
              <div class="file-operation"></div>
              <div class="file-type"></div>
              <div class="file-name"></div>
            </div>
          </div>
        </div>
        <div class="footer">
          <el-button @click="handleCancel">{{ $t('close') }}</el-button>
          <!-- <el-button type="primary" @click="handleSubmit">{{ $t('confirm') }}</el-button> -->
        </div>
      </div>
    </el-drawer>
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
    visible: {
      type: Boolean
    },
    knowledgeId: {
      type: String,
      default: "",
    },
  },
  computed: {
  },
  mounted() {
  },
  data() {
    return {
    }
  },
  created() {
  },
  methods: {
    handleSubmit() {},
    handleCancel() {
      this.$emit('update:visible', false)
    },

  }
}
</script>

<style lang="scss" scoped>
@import "./kbm.scss";
.operation-content {
  padding: 12px 16px 12px 12px;
}
</style>
