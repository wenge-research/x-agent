<template>
    <el-dialog :title="$t('importFile')" :visible.sync="visible" width="500px"
         :before-close="handCancelFolder" >
        <el-upload action="#" :before-upload="beforeupload" drag accept=".xlsx" :multiple="false"
            :show-file-list="false">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">{{ $t('clickUpload') }}</div>
            <div class="el-upload__condition">
                {{ $t('supportedXlsx') }}
            </div>
        </el-upload>
        <div class="file-item" v-for="item in uploadFile" :key="item.uid">
            {{ item.name }} <i @click="handleRemovefileItem" class="el-icon-close"></i>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button :loading="importLoading" @click="handCancelFolder">{{ $t('cancel') }}</el-button>
            <el-button :loading="importLoading" type="primary" @click="handleImportFolder">{{ $t('confirm') }}</el-button>
        </span>
    </el-dialog>

</template>

<script>
import { apiApplicationInfoImportApp } from "@/api/app";
export default {
    name: "ImportApplication",
    props: {
        visible: {
            type: Boolean
        }
    },
    data() {
        return {
            importLoading: false,
            uploadFile: [],
            uploadForm: new FormData()
        }
    },
    methods: {
        beforeupload(file) {
            this.uploadForm = new FormData()
            this.uploadForm.append('file', file);
            this.uploadFile = [file]
            console.log(this.uploadForm)
            return false;
        },
        handCancelFolder() {
            this.uploadForm = new FormData()
            this.uploadFile = []
            this.$emit('closeImport')
        },
        async handleImportFolder() {
            console.log(this.uploadFile, this.uploadForm)
            if (this.uploadFile.length === 0) {
                this.$message({
                message: this.$t('pleaseSelectUploadFile'),
                type: "warning",
                });
                return false;
            }
            this.importLoading = true;
            const res = await apiApplicationInfoImportApp(this.uploadForm);
            if (res.code === "000000") {
                this.handCancelFolder();
                this.$emit('updateList')
                this.$message({
                    message: res.msg,
                    type: "success",
                });
            } else {
                this.$message({
                    message: res.msg,
                    type: "error",
                });
            }
            this.importLoading = false;
        },
        handleRemovefileItem(){
            this.uploadForm = new FormData()
            this.uploadFile = []
        }

    }
}
</script>

<style lang="scss" scoped>
.el-upload__condition {
    margin-top: 10px;
}

.file-item{
    margin-top: 8px;
    .el-icon-close{
        cursor: pointer;
    }
}
</style>