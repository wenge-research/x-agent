
<template>
  <div>
      <vue-office-docx :src="fileUrl" style="height: 100vh;" @rendered="renderedHandler" @error="errorHandler" />
  </div>
</template>

<script>
// 引入VueOfficeDocx组件
import VueOfficeDocx from '@vue-office/docx'
// 引入相关样式
import '@vue-office/docx/lib/index.css'

export default {
  name: "VueOfficeDocxDemo",
  components: {
      VueOfficeDocx
  },
  props: {
    // 文件URL
    fileUrl: {
      type: String,
      required: true
    },
  },
  data() {
      return {
      }
  },
  methods: {
      renderedHandler() {
          console.log("渲染完成")
      },
      errorHandler() {
          console.log("渲染失败")
      }
  }
};
</script>
