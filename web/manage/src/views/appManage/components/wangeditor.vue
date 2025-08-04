<template>
  <!-- 修改个人声明 -->
  <el-dialog
    :title="title ? title :$t('bottomInformationBar')"
    :visible.sync="dialogVisible"
    top="3%"
    width="960px"
    height="800px"
    :modal-append-to-body="false"
    append-to-body
    custom-class="matter-dialog"
    :before-close="closeDialog"
    :close-on-click-modal="false"
        :close-on-press-escape="false"
    @opened="openDialog"
  >
    <div class="dialog-config">
      <!-- <div style="border: 1px solid #ccc">
        <Toolbar
          style="border-bottom: 1px solid #ccc"
          :editor="editor"
          :defaultConfig="toolbarConfig"
          :mode="mode"
        />
        <Editor
          style="height: 500px; overflow-y: hidden"
          v-model="html"
          :defaultConfig="editorConfig"
          :mode="mode"
          @onCreated="initEditor"
        />
      </div> -->
      <div>
        <el-input class="" v-model="html" type="textarea" :rows="26"></el-input>
      </div>
    </div>
    <div class="dialog-footer">
      
      <el-button plain @click="closeDialog" :loading="addLoading">{{
        $t("cancel")
      }}</el-button>
      <el-button type="primary" @click="onSubmit" :loading="addLoading">{{
        $t("confirm")
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";

export default {
  components: { Editor, Toolbar },
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    statement: {
      type: String,
    },
    title: {
      type: String,
    }
  },
  data() {
    return {
      dialogVisible: false,
      addLoading: false,
      // editor配置
      editor: null,
      html: "",
      toolbarConfig: {},
      editorConfig: { placeholder: "请输入" },
      mode: "default", // or 'simple'
    };
  },
  watch: {
    value(n) {
      this.dialogVisible = n;
    },
    statement(n) {
      this.html = n;
    },
  },
  methods: {
    openDialog() {
      console.log("open");
      this.html = this.statement;
    },
    closeDialog() {
      // const editor = this.editor;
      // if (editor == null) return;
      // editor.destroy(); // 组件销毁时，及时销毁编辑器
      this.$emit("close");
    },
    initEditor(editor) {
      this.editor = Object.seal(editor);
    },
    onSubmit() {
      this.dialogVisible = false;
      this.$emit("editStatementValue", this.html);
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .matter-dialog {

  .el-dialog__header {
    padding: 32px 32px 16px;
    .el-dialog__title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #494E57;
      line-height: 32px;
    }
    .el-dialog__headerbtn {
      right: 32px;
      top: 40px;
    }
  }
  .el-dialog__body {
    padding: 0 32px 32px;
  }
}
.dialog-config {
  height: 580px;
  #editor {
    height: 100%;
    ::v-deep .w-e-text-container {
      min-height: 520px !important;
    }
  }
  :deep(.el-textarea) {
    .el-textarea__inner {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #494E57;
      line-height: 22px;
    }
  }
}
.dialog-footer {
  padding-top: 32px;
  text-align: right;
}
</style>