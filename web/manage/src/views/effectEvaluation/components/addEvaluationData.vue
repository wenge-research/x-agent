<template>
  <div class="app-container">
    <div class="form-box">
      <div class="form-label">
        <div class="label">上传文件</div>
        <el-button type="text" @click="downloadExcel"><i class="el-icon-download"></i>下载模板</el-button>
      </div>
      <el-upload class="upload-demo" :show-file-list="false" drag :on-change="beforeupload" :auto-upload="false" accept=".xlsx,.xls,.csv" action="#" multiple>
        <div class="upload-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path fill-rule="nonzero" fill="#848587"
              d="m12 12.586 4.243 4.242-1.415 1.415L13 16.415V22h-2v-5.587l-1.828 1.83-1.415-1.415L12 12.586ZM12 2a7.001 7.001 0 0 1 6.954 6.194A5.5 5.5 0 0 1 18 18.978V17a6 6 0 0 0-11.996-.225L6 17v1.978a5.5 5.5 0 0 1-.954-10.784A7 7 0 0 1 12 2Z"
              data-follow-fill="#848587" />
          </svg>
        </div>
        <div class="el-upload__text">
          <div class="title">拖拽文件到此处或选择文件</div>
          <div class="info">支持格式：xls、xlsx、csv</div>
        </div>
      </el-upload>
      <div v-if="isRequired" class="el-form-item__error">请上传测评数据集</div>
    </div>

    <div class="file-list">
      <div class="header">
        <div class="title">文件</div>
        <div class="info">操作</div>
      </div>
      <div class="list" v-loading="loading">
        <div class="list-item" v-for="(item, index) in fileList">
          <div class="info">
            <div class="info-icon">
              <img src="@/assets/svg/csv.svg" alt="" />
            </div>
            <div class="info-content">
              <div class="info-name">{{ item.name }}</div>
              <div class="info-size">{{ formatFileSize(item.size) }}</div>
            </div>
          </div>
          <div class="action">
            <el-button type="text" size="small" @click="deleteFile(index)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="btn-wrap">
      <el-button size="small" @click="backPage">上一步</el-button>
      <!-- :disabled="fileList.length == 0" -->
      <el-button type="primary"  size="small" @click="nextPage">下一步</el-button>
    </div>

  </div>
</template>

<script>
import axios from "axios";
// import * as XLSX from 'xlsx';
import { apiImportApplicationDateset, apiDeleteApplicationDateset, downloadEvaluationDataTemp } from '@/api/effectEvaluation'
const { v4: uuidv4 } = require('uuid');
export default {
  name: 'addEvaluationObj',
  props:{
    params: {
      type: Object
    }
  },
  data() {
    return {
      ruleForm: {
        resource: "上传数据集",
      },
      loading: false,
      fileList: [],
      rules: {
        resource: [
          { required: true, message: '请选择', trigger: 'change' }
        ],
      },
      uploadForm: null,
      isRequired: false
    }
  },
  mounted() {
    this.fileList = this.params.fileList ? this.params.fileList : []
  },
  methods: {
    downloadExcel(){
		let _this = this;
		this.loading = true
		axios({
		  method: 'get',
		  url: `${process.env.VUE_APP_BASE_API}/applicationEvaluation/downloadEvaluationDataTemp`,
		  headers: {
		    Authorization: 'Bearer ' + sessionStorage.getItem('manageAccessToken'),
		  },
		  responseType: 'blob',
		  
		})
		    .then((res) => {
				this.loading = false
		      let date = new Date();
		      let fullYear = date.getFullYear();
		      let month = date.getMonth();
		      month = month < 10 ? `0${month}` : `${month}`;
		
		      let day = date.getDate();
		      let hours = date.getHours();
		      hours = hours < 10 ? `0${hours}` : `${hours}`;
		      let minutes = date.getMinutes();
		      let fileName = `${fullYear}${month}${day}_${hours}${minutes}.xlsx`;
		      const url = window.URL.createObjectURL(new Blob([res.data]))
		      const link = document.createElement('a')
		      link.href = url
		      link.setAttribute('download', fileName)
		      document.body.appendChild(link)
		      link.click()
		      
		    })
		    .catch((error) => {
		      console.log('config-res-error:', error)
		      this.loading = false
		    })
		// downloadEvaluationDataTemp().then(res=>{
		//   if(res.code === '000000'){
		    
		//   }
		// })
      // window.location.href = process.env.VUE_APP_API_NEW + '/applicationEvaluation/downloadEvaluationDataTemp'
    },
    formatFileSize(size){
      if (size < 1024) {
        return `${size} Bytes`;
      } else if (size < 1024 * 1024) {
        return `${(size / 1024).toFixed(2)} KB`;
      } else if (size < 1024 * 1024 * 1024) {
        return `${(size / (1024 * 1024)).toFixed(2)} MB`;
      } else if (size < 1024 * 1024 * 1024 * 1024) {
        return `${(size / (1024 * 1024 * 1024)).toFixed(2)} GB`;
      } else {
        return `${(size / (1024 * 1024 * 1024 * 1024)).toFixed(2)} TB`;
      }
    },
    readFile(file) {
      const fileReader = new FileReader()
      fileReader.onload = (evt) => {
        console.log(evt)
        const data = new Uint8Array(evt.target.result);
        const workbook = XLSX.read(data, { type: 'array' });
        // 假设我们读取第一个sheet，你可以通过workbook.SheetNames来获取所有sheet的名称，然后选择需要的sheet读取
        const firstSheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[firstSheetName];
        let JSONDATA = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        const objects = []
        const headers = JSONDATA[0]
        // 遍历行数组（从第二行开始）
        for (let i = 1; i < JSONDATA.length; i++) {
          console.log(JSONDATA[i])
          // 跳过空行
          if (!JSONDATA[i]) {
            continue
          }
          // 将当前行拆分为值数组
          const values = JSONDATA[i]
          // 创建一个空对象
          const obj = {}
          // 遍历标题数组，并将每个标题与当前行的相应值关联
          for (let j = 0; j < headers.length; j++) {
            obj[headers[j]] = values[j] ? values[j]:null
          }
          // 将新对象添加到对象数组中
          objects.push(obj)
        }
        console.log(JSONDATA,objects)
      }
      fileReader.readAsArrayBuffer(file);
    },
    async beforeupload(file) {
      console.log('111',file)
      this.loading = true
      // 读取表格数据
      this.uploadForm = new FormData()
      this.uploadForm.append('file', file.raw);
      let datasetId = await uuidv4()?.replace(/-/g, '')
      this.uploadForm.append('datasetId', datasetId);
      apiImportApplicationDateset(this.uploadForm).then((res)=>{
        this.loading = false
        if(res.code === '000000'){
          file.datasetId = datasetId
          this.fileList = [file]
          this.isRequired = false
        }else{
			this.$message.error(res.msg)
		}
      })
      // this.fileList = [...this.fileList, file]

      // console.log(this.uploadForm)
      return false;
    },
    deleteFile(index) {
      // this.fileList.splice(index, 1)
      apiDeleteApplicationDateset({datasetId: this.fileList[0].datasetId}).then(res=>{
        if(res.code === '000000'){
          this.fileList.splice(index, 1)
        }else{
			this.$message.error(res.msg)
		}
      })
    },
    backPage() {
      this.$emit('update', {
        fileList: this.fileList
      })
      this.$emit('next', 'left')
    },
    nextPage() {
      if(!this.fileList.length){
        this.isRequired = true
        return
      }
      this.$emit('update', {
        fileList: this.fileList
      })
      this.$emit('next', 'right')
    },
    cancelAdd() {
      this.$emit('cancel')
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  background: #fff;
  height: 100%;
  width: 75%;
  margin: 0 auto;
  padding: 24px 12%;
  border-radius: 8px;
  display: flex;
  flex-direction: column;

  .form-box {
    width: 100%;
    margin: 0 auto;
    margin-bottom: 60px;
    position: relative;

    .form-label {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 4px;

      .label {
        color: #1D2129;
        font-size: 16px;
        font-weight: 500;
      }
    }

    ::v-deep .el-form-item__label {
      padding-right: 26px;
    }

    .upload-demo {
      width: 100%;

      ::v-deep .el-upload {
        width: 100%;

        .upload-icon {
          margin-bottom: 4px;

          svg {
            width: 22px;
            height: 20px;
          }
        }

        .el-upload-dragger {
          width: 100%;
          padding-top: 18px;
          height: 101px;
          background-color: #F9FAFC;

          .el-icon-document-delete {
            font-size: 40px;
            margin-bottom: 20px;
          }

          .el-upload__text {
            .title {
              font-weight: 400;
              color: #1D2129;
              font-size: 14px;
              margin-bottom: 4px;
            }

            .info {
              font-size: 12px;
              color: #C9CDD4;
            }
          }
        }
      }
    }
  }

  .file-list {
    width: 100%;
    flex:1;
    // height: calc(100% - 200px);
    margin: 0 auto;
    margin-bottom: 30px;
    overflow: auto;

    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      line-height: 24px;
      border-bottom: 1px solid #E5E6EA;

      .title {
        font-size: 14px;
        color: #86909C;
        margin-bottom: 10px;
        margin-left: 12px;
      }

      .info {
        font-size: 14px;
        color: #86909C;
        margin-right: 12px;
      }
    }

    .list {
      min-height: 100px;
      .list-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        border-bottom: 1px solid #E5E6EA;
        height: 68px;

        .info {
          display: flex;
          align-items: center;

          .info-icon {
            width: 40px;
            height: 40px;
            border-radius: 4px;
            margin-right: 10px;
            font-size: 40px;
            display: flex;
            align-items: center;
            justify-content: center;

            img {
              width: 30px;
              height: 34px;
            }
          }

          .info-content {
            .info-name {
              font-size: 14px;
              color: #1D2129;
              line-height: 20px;
              font-weight: 400;
            }

            .info-size {
              font-size: 12px;
              color: #C9CDD4;
              line-height: 16px;
            }
          }
        }

        .action {
          .el-button {
            padding: 0;
            font-size: 12px;
            margin-right: 16px;
            // color: #303133;
          }
        }
      }
    }
  }

  .btn-wrap {
    display: flex;
    justify-content: flex-end;
    // margin-right: 10%;
  }
}
</style>