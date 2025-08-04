<template>
  <div class="ppt-preview">
    <div v-loading="loading" class="ppt-container">
      <VueOfficePptx 
        :src="fileUrl" 
        :options="options"
        @rendered="onRendered"
        @error="onError"
        style="width: 100%; height: 100%;"
      />
    </div>
  </div>
</template>

<script>
import VueOfficePptx from '@vue-office/pptx'
// 移除不存在的CSS导入

export default {
  name: 'previewPpt',
  components: {
    VueOfficePptx
  },
  props: {
    fileUrl: {
      type: String,
      required: true
    },
    fileName: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      loading: true,
      options: {
        delay: 100, // 延迟渲染，单位毫秒
        lazy: false // 是否启用懒加载
      }
    }
  },
  methods: {
    onRendered() {
      this.loading = false
      console.log('PPT 渲染完成')
    },
    onError(error) {
      this.loading = false
      console.error('PPT 渲染失败:', error)
      this.$emit('error', error)
    }
  }
}
</script>

<style scoped>
.ppt-preview {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.ppt-container {
  width: 100%;
  height: 100%;
  overflow: auto;
}

/* 添加一些基本样式来确保组件正常显示 */
:deep(.vue-office-pptx) {
  width: 100%;
  height: 100%;
}

:deep(.vue-office-pptx .ppt-page) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin: 10px auto;
}
</style>