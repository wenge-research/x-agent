<!--
 * @Author: git config user.appName
 * @Date: 2024-12-23 10:49:53
 * @LastEditors: git config user.appName
 * @LastEditTime: 2025-01-06 15:08:13
 * @FilePath: \llm-agent-web\src\components\ComFilePreview.vue
 * @Description: 
 * 
 * Copyright (c) 2025 by ${git_name_email}, All Rights Reserved. 
-->
<template>
  <el-dialog append-to-body :visible.sync="dialogVisible" :width="fileType === 'mp3' ? '400px' : '900px'" @close="handleClose">
    <div class="preview-content" :style="{ maxHeight: '80vh', overflowY: 'auto' }">
      <div v-if="['jpg', 'jpeg', 'png'].includes(fileType)">
        <img :src="fileUrl" alt="Image Preview" :style="{maxWidth: '100%', width: imageWidth, height: imageHeight }">
      </div>
      <video controls v-else-if="fileType === 'mp4'" style="max-width: 100%; height: auto;">
        <source :src="fileUrl" :type="fileMimeType">
      </video>
      <audio controls v-else-if="fileType === 'mp3' || fileType === 'wav'" >
        <source :src="fileUrl" :type="fileMimeType">
      </audio>
      <audio controls v-else-if="fileType === 'wav'" style="width: 100%;">
        <source :src="fileUrl" :type="fileMimeType">
      </audio>
      <p v-else>Unsupported file type</p>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: {
    imageWidth: {
      type: String,
      default: ''
    },
    imageHeight: {
      type: String,
      default: 'auto'
    }
  },
  data() {
    return {
      dialogVisible: false,
      fileUrl: '',
      fileType: '',
      fileMimeType: '',
      fileContent: ''
    };
  },
  methods: {
    openPreview(fileUrl, fileType, fileContent) {
      this.fileUrl = fileUrl;
      this.fileType = fileType;
      console.log(this.fileUrl,this.fileType,'文件的类型和地址')
      this.fileMimeType = this.getFileMimeType(fileType);
      this.dialogVisible = true;
    },
    getFileMimeType(type) {
      switch (type) {
        case 'mp3':
          return 'audio/mp3';
        case 'mp4':
          return 'video/mp4';
        default:
          return '';
      }
    },
    handleClose() {
      this.fileUrl = '';
      this.fileType = '';
      this.fileMimeType = '';
      this.dialogVisible = false;
    }
  },
  mounted() {
  }
};
</script>

<style scoped>
.tinymce-editor {
  width: 100%;
  height: 500px;
}
</style>