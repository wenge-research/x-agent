<template>
  <el-drawer
    title="API调用说明"
    :visible.sync="drawer"
    direction="rtl"
    :before-close="handleClose"
    @open="openDrawer"
    append-to-body
    custom-class="apiCallDescriptionDrawer"
    size="1140px"
  >
    <div class="list" v-loading="loading">
      <div class="markdown-container">
        <!-- 核心预览组件 可选 dark/light 主题-->
        <v-md-preview
          :text="mdContent"
          mode="dark"
          @copy-code-success="handleCopySuccess"
        />
      </div>
    </div>
  </el-drawer>
</template>

<script>
// api
import { apiGetApplicationMarkdownApiDesc } from "@/api/app";
export default {
  name: "apiCallDescription",
  props: {
    applicationId: {
      type: String,
    },
    value: {
      type: Boolean,
      default: false,
    },
    // 从父组件接收后端返回的 MD 原始字符串
    rawMd: {
      type: String,
      default: '# Loading...'
    }
  },
  data() {
    return {
      drawer: false,
      mdContent: "",
      loading: false,
    };
  },
  watch: {
    value: {
      handler(n) {
        this.drawer = n;
      },
    },
    rawMd: {
      immediate: true,
      handler(newVal) {
        // 可在此处对原始内容做预处理
        this.mdContent = newVal || '';
      },
    }
  },
  mounted() {},
  methods: {
    openDrawer() {
      this.getAppPublishRecordList();
    },
    handleClose() {
      this.drawer = false;
      this.historyList = [];
      this.$emit("close", "drawerVisibleApiCallDescription");
    },
    async getAppPublishRecordList() {
      this.loading = true;
      try {
        let res = await apiGetApplicationMarkdownApiDesc();
        if (res.code == "000000") {
          this.mdContent = res.data?.content;
        }
      } catch (error) {
        this.loading = false;
      }
      this.loading = false;
    },
    handleCopySuccess() {
      this.$message.success('代码复制成功！');
    }
  },
};
</script>
<style lang="scss">
.apiCallDescriptionDrawer {
  .el-drawer__header {
    padding: 32px 32px 0;
    margin-bottom: 0;
  }
  .el-drawer__header > :first-child {
    height: 32px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 20px;
    color: #36383d;
    line-height: 32px;
  }
  .el-drawer__body {
    padding: 0 0 32px;
  }
}
</style>
<style scoped>
/* 容器样式 */
.markdown-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 覆盖默认样式 */
.v-md-preview {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  padding: 24px;
}

/* 暗黑模式适配 */
.v-md-preview.dark-mode {
  background: #1a1a1a;
  color: #e6e6e6;
}
</style>