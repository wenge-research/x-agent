<template>
  <!-- 修改个人声明 -->
  <el-dialog
    :title="$t('personalInformationCollectionStatement')"
    :visible.sync="dialogVisible"
    top="3%"
    width="960px"
    height="800px"
    :modal-append-to-body="false"
    append-to-body
    custom-class="matter-dialog"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    @opened="openDialog"
  >
    <div class="dialog-config">
      <div style="border: 1px solid #ccc">
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
      </div>
    </div>
    <div class="dialog-footer">
      <el-button type="primary" @click="onSubmit" :loading="addLoading"
        >{{$t('confirm')}}</el-button
      >
      <el-button plain @click="closeDialog" :loading="addLoading"
        >{{$t('cancel')}}</el-button
      >
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
    }
  },
  methods: {
    openDialog() {
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
      this.$emit("editStatementValue", this.html)
    },
  },
};
</script>

<style lang="scss" scoped>
.dialog-config {
  height: 631px;
  #editor {
    height: 100%;
    ::v-deep .w-e-text-container {
      min-height: 520px !important;
    }
  }
}
</style>