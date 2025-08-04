<template>
  <!-- 图片预览弹窗 -->
  <el-dialog
    v-model="dialogVisible"
    :show-close="false"
    :close-on-click-modal="true"
    :fullscreen="false"
    custom-class="mobile-preview-dialog"
    width="90%"
    top="10vh"
    @close="closePreview"
  >
    <div class="image-container">
      <img 
        :src="currentImageUrl" 
        alt="预览图片"
        class="preview-image"
        @click.stop
      />
    </div>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';

const dialogVisible = ref(false);
const currentImageUrl = ref('');

/**
 * 打开图片预览
 * @param {string} imageUrl - 要预览的图片URL
 */
const openPreview = (imageUrl) => {
  if (!imageUrl) {
    console.warn('没有传入有效的图片URL');
    return;
  }
  currentImageUrl.value = imageUrl;
  dialogVisible.value = true;
};

/**
 * 关闭图片预览
 */
const closePreview = () => {
  dialogVisible.value = false;
  currentImageUrl.value = '';
};

// 暴露方法供父组件调用
defineExpose({
  openPreview,
  closePreview
});
</script>

<style scoped>
.image-container {
  width: 100%;
  height: 100%;
  padding: 10px;
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  align-items: center;
}

.preview-image {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
  border-radius: 4px;
}
</style>

<style>
/* 全局覆盖el-dialog样式 */
.mobile-preview-dialog {
  width: 90% !important;
  max-width: none !important;
  margin: 5% auto !important;
  border-radius: 8px;
  background: transparent;
  box-shadow: none;
}

.mobile-preview-dialog .el-dialog {
  background: #fff !important;
}
.mobile-preview-dialog .el-dialog__header {
  padding: 0;
}

.mobile-preview-dialog .el-dialog__body {
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: transparent;
}
</style>