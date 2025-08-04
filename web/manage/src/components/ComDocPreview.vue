<template>
  <el-dialog
      append-to-body
      :title="fileTitle"
      class="preview-dialog"
      :visible.sync="dialogVisible"
      width="900px"
      top:="2vh"
      :before-close="handleClose"
  >
  <!-- <a :href="downloadUrl" class="download" target="_blank" >{{$t('downloadDocument')}}</a> -->
      <div
          class="preview-content"
          :style="{ height: '80vh', overflowY: 'hidden' }"
      >
            <previewDoc v-if="fileUrl && showDoc" :fileUrl="fileUrl" :fileName="fileTitle"></previewDoc>
            <previewPdf v-if="fileUrl && showPdf" :fileUrl="fileUrl" :fileName="fileTitle"></previewPdf>
            <previewTxt v-if="fileUrl && showTxt" :fileUrl="fileUrl" :fileName="fileTitle"></previewTxt>
            <previewPpt v-if="fileUrl && showPpt" :fileUrl="fileUrl" :fileName="fileTitle"></previewPpt>
      </div>
  </el-dialog>
</template>

<script>
import axios from 'axios';
import previewPdf from './previewPdf.vue';
import previewDoc from './previewDoc.vue';
import previewTxt from './previewTxt.vue';
import previewPpt from './previewPpt.vue';
export default {
components: {
  previewPdf, previewDoc, previewTxt, previewPpt
},
data() {
  return {
    dialogVisible: false,
    showDoc: false,
    showPdf: false,
    showTxt: false,
    showPpt: false,
    downloadUrl: '',
    fileType: '',
    fileUrl: '',
    fileTitle: '',
  };
},
methods: {
  openPreview(fileObj) {
    this.dialogVisible = true;
    this.fileTitle = fileObj.fileName;
    this.downloadUrl = fileObj.fileUrl;
    this.fileUrl = fileObj.fileUrl;
    this.fileType = fileObj.fileType;
    this.showDoc = ['doc','docx'].includes(fileObj.fileType);
    this.showPdf = ['pdf'].includes(fileObj.fileType);
    this.showTxt = ['txt'].includes(fileObj.fileType);
    this.showPpt = ['ppt', 'pptx'].includes(fileObj.fileType);
  },
  handleClose(done) {
    this.dialogVisible = false;
    this.showDoc = false;
    this.showPdf = false;
    this.showPpt = false;
    this.showTxt = false;
    done();
  }
}
};
</script>

<style scoped lang="scss">
.preview-content {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  .docxRef{
    position: absolute;
    width: 100%;
    position: absolute;
    left: 0;
    top: 0;
  }
}

.preview-content img {
  max-height: 100%;
  max-width: 100%;
}
.preview-dialog{
  ::v-deep .el-dialog{
    margin-top: 2vh !important;
  }
}
.download {
	position: absolute;
	right: 50px;
	top: 20px;
  color: #409EFF;
  cursor: pointer;
}
</style>
