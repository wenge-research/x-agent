<template>
  <div class="page-wrap" v-loading="loading">
      <div class="page-header">
        <el-row class="header-wrap" type="flex" justify="space-between" align="middle">
          <el-col style="
              font-size: 18px;
              display: flex;
              align-items: center;
              font-weight: 550;
            " :span="6">
            <img style="
                cursor: pointer;
                width: 18px;
                height: 18px;
                margin-right: 10px;
              " src="@/assets/images/arrow-go-back-fill.svg" @click="closePage" />
           <div class="list-item-top-icon">
           	<img src="@/assets/images/sjk-icon1.png" alt="" v-if="appForm.type==1" />
           	<img src="@/assets/images/sjk-icon2.png" alt="" v-else />
           </div>			
            <span>{{appForm.name}}</span>
          </el-col>
          
        </el-row>
      </div>
      <div class="line"></div>
      <div class="content">
		  <div class="content-left" v-if="isFlagLeft">
		  		<div class="content-left-num">数据表{{tableLeftData.length}}</div>
				<div style="margin-bottom: 20px;" v-if="appForm.type==1">
					<el-button plain
						style="width: 100%; background-color: #1747E5; color:#fff; border-radius: 2px;"
						@click="addShowStatistical">
						上传数据表
					</el-button>
				</div>
				<!-- <div>
				  <el-input
				    placeholder="搜索数据表名"
				    prefix-icon="el-icon-search"
				    v-model="searchKeyWord"
				    style="width: 100%"
				    @input="getComponentNodeTableInfoListSql"
				  >
				  </el-input>
				</div> -->
				<div class="content-left-list">
					<div :class="index==tableIndex?'content-left-list-item content-left-list-item-on':'content-left-list-item'" v-for="(item,index) in tableLeftData" @click="addTable(item,index)" :key="index">
						<div class="li-name flex-center" >
						   <img src="@/assets/images/sql.png">
						</div>
						<div class="li-content" style="width:86%; display: flex;justify-content: space-between;">
						  <div class="li-title">{{item.configName}}</div>
						  <el-dropdown v-if="appForm.type==1" trigger="click" @command="(value) => handleCommand(value,item)"
						  	placement="top-end" class="opts-box-dropdown" style="transform: rotate(90deg);"
						  >
						  	<span class="el-dropdown-link">
						  		<iconpark-icon name="more-line" size="18"
						  			color="#383838"></iconpark-icon>
						  	</span>
						  	<!-- api接入详情 -->
						  	<el-dropdown-menu slot="dropdown" class="opts-box-dropdown-menu">													
						  		<el-dropdown-item command="editeApp">
						  			<iconpark-icon color="#494E57"
						  				name="edit-box-line"></iconpark-icon>
						  			<span style="color: #494E57">{{ $t("edit") }}</span>
						  		</el-dropdown-item>
						  		<el-dropdown-item v-permission="'deleteApp'" command="deleteApp">
						  			<iconpark-icon color="#494E57"
						  				name="delete-bin-4-line"></iconpark-icon>
						  			<span style="color: #494E57">{{ $t("delete") }}</span>
						  		</el-dropdown-item>
						  	</el-dropdown-menu>
						  </el-dropdown>
						</div>
						
					</div>
					
				</div>
				
				<div v-if="tableLeftData.length==0" class="no-data">
					<img class="no-data-img" src="@/assets/images/no-data.png" alt="" />
					<div class="no-data-text">{{ $t("noData") }}</div>
				</div>
		  </div>
		  <div class="arrow-left" :style="isFlagLeft?'left:19.5%;':'left:0.5%;'" @click="addFlagLeft">
		  	<img  src="@/assets/images/dataSj/xl-left.png" v-if="isFlagLeft">
		  	<img  src="@/assets/images/dataSj/xl-right.png" v-else>
		  	<!-- <i :class="isFlag?'el-icon-arrow-up el-icon--right':'el-icon-arrow-down el-icon--right'" @click="addFlag"></i> -->
		  </div>
		  <div class="content-right" :style="isFlagLeft?'margin-left:0;width:79%;':'margin-left:1.5%;width:100%;'">
			  <div v-if="tableLeftData.length>0" style="height: 100%;">
		  		<div class="content-right-header">
					<div class="content-right-header-top">
						<div class="content-left-list-item">
							<div class="li-name flex-center" >
							   <img src="@/assets/images/sql.png">
							</div>
							<div class="li-content">
							  <div class="li-title">{{tabItem.configName}}
								<i :class="isFlag?'el-icon-arrow-up el-icon--right':'el-icon-arrow-down el-icon--right'" @click="addFlag"></i>
							  </div>
							</div>
						</div>
						<div class="xgpz" @click="addEdit(tabItem)"><span style="margin-right: 4px;">
						  		<iconpark-icon name="list-settings-line" size="18"
						  			color="#1747E5"></iconpark-icon>
						  	</span>修改配置</div>
					</div>
					<div class="content-desc" v-if="isFlag">{{tabItem.describe!='null'&&tabItem.describe!=null?tabItem.describe:''}}</div>
				</div> 
				<div class="content-table">
					<el-table :data="tableData" border stripe style="width: 100%" height="100%" :header-cell-style="{ background: '#F2F3F5' }">
						<el-table-column :prop="item" :label="item" v-for="(item,index) in tableHeads" :key="index">
							
						</el-table-column>
						
						
					</el-table>
				</div>
				<div v-if="tableData.length" class="pagination">
					<div class="total">
						{{ $t("total") }}{{ pageObj.total }}{{ $t("items") }}
					</div>
					<el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
						:current-page.sync="pageObj.pageNo" :page-sizes="[12, 24, 36, 48]"
						:page-size="pageObj.pageSize" background layout="prev, pager, next, sizes"
						:total="pageObj.total">
					</el-pagination>
				</div>
		      </div>
			  <div v-if="tableLeftData.length==0" class="no-data">
			  	<img class="no-data-img" src="@/assets/images/no-data.png" alt="" />
			  	<div class="no-data-text">{{ $t("noData") }}请先上传数据表</div>
			  </div>
		  </div>
	  </div>
      <el-dialog class="my-dialog" title="上传数据表" :visible.sync="importVisible" width="720px"
        :modal-append-to-body="false" :close-on-click-modal="false" :close-on-press-escape="false" append-to-body>
        
        <el-upload action="#" drag multiple accept=".xlsx, .xls, .csv, .json" :class="isDrag ? 'el-upload-dragger' : 'drag'"
          :before-upload="beforeupload" :show-file-list="false" @dragenter.native="handleDragEnter"
          @dragleave.native="handleDragLeave" @drop.native="handleDrag">
          <img class="icon-upload" src="@/assets/images/upload-cloud-2-fill.svg" fill="#1747e5;"  alt="" />
          <div class="el-upload__text" v-show="!isDrag">
             拖拽Excel文件到此处或<span style="color:#1747e5;">选择文件</span>
          </div>
          <div class="el-upload__condition" v-show="!isDrag">
             文件大小不超过50MB，不超过10万行,支持.xlsx/.xls/.csv/.json四种格式文件
          </div>
		 <!-- <div class="el-upload__condition" v-show="!isDrag">
		    支持.xlsx/.xls/.csv/.json四种格式文件
		  </div> -->
          <div v-show="isDrag" class="draged">松开文件上传</div>
        </el-upload>
        <div class="file-num">文件数：{{uploadFile.length}}</div>
        <el-table style="width: 100%" :data="uploadFile" height="360px" class="import-table">
          <el-table-column prop="name" :label="$t('file')">
            <template slot-scope="scope">
              <div class="file-wrapper">
                <img :src="require(`@/assets/images/excel.svg`)" alt="" />
                <div class="file-name">
                  <div class="name">{{ scope.row.name }}</div>
                  <!-- <div class="size">{{ formatFileSize(scope.row.size) }}</div> -->
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('action')" width="60">
            <template slot-scope="scope">
              <div class="btns">
                <el-button type="text" @click="handleRemovefileItem(scope.row.uid)">{{
                  $t("delete")
                }}</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <!-- <div class="file-item" v-for="item in uploadFile" :key="item.uid">
          {{ item.name }} <i @click="handleRemovefileItem" class="el-icon-close"></i>
        </div> -->
        <div style="margin-bottom: 12px"></div>
        <span slot="footer" class="dialog-footer">
          <el-button :loading="importLoading" @click="handleCancelImport">{{
            $t("cancel")
          }}</el-button>
          <el-button :loading="importLoading" type="primary" @click="handleImport">{{
            $t("confirmImport")
          }}</el-button>
        </span>
      </el-dialog>
	  <el-dialog title="删除数据" :visible.sync="deleteDialogVisible" width="560px" class="deleteDialog"
	  	:before-close="cancelDelete" append-to-body>
	  	<p class="desc">{{ $t("deletionWarning") }}</p>
	  	<span slot="footer" class="dialog-footer">
	  		<el-button @click="cancelDelete">{{ $t("cancel") }}</el-button>
	  		<el-button type="primary" @click="confirmDelete">确定删除</el-button>
	  	</span>
	  </el-dialog>
	  <el-dialog class="my-dialog" title="修改数据配置" :visible.sync="createVisible" width="960px" :modal-append-to-body="false"
	    :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
	    <div class="task-dialog">
	      
	      <div class="xlsx-con">
	        <el-form :model="editeForm" ref="sqlForm">
	          		        
	          <el-form-item label="数据表名称" prop="configName">
	            <el-input v-model="editeForm.configName" :disabled="appForm.type==2?true:false" placeholder="请输入数据集名称"></el-input>
	          </el-form-item>
	          <el-form-item label="数据库描述" prop="describe">
	            <el-input type="textarea" v-model="editeForm.describe" placeholder="请准确描述表格的应用场景,大模型将根据此描述识别并调用该表"></el-input>
	          </el-form-item>
	        </el-form>
	       
	      </div>
	      <div class="file-num">字段描述</div>
	      <el-table :data="tableEditData" style="width: 100%;margin-top: 20px;" :header-cell-style="{ background: '#F2F3F5' }">
	      	<el-table-column prop="titleName" label="列名">
	      		<template slot-scope="scope">
	      		  <div class="file-wrapper">
	      		      <el-input							       
	      		        v-model="scope.row.titleName"
	      		        style="width: 100%" :disabled="true"
	      		      >
	      		      </el-input>
	      		  </div>
	      		</template>
	      	</el-table-column>
	      	<el-table-column prop="comment" label="数据示例">
	      		<template slot-scope="scope">
	      		  <div class="file-wrapper">
	      		      <el-input							       
	      		        v-model="scope.row.example"
	      		        style="width: 100%"
	      		      >
	      		      </el-input>
	      		  </div>
	      		</template>
	      	</el-table-column>
			<el-table-column prop="comment" label="描述">
	      		<template slot-scope="scope">
	      		  <div class="file-wrapper">
	      		      <el-input							       
	      		        v-model="scope.row.fieldDesc"
	      		        style="width: 100%"
	      		      >
	      		      </el-input>
	      		  </div>
	      		</template>
	      	</el-table-column>
			
	      	<el-table-column prop="fieIdType" label="数据类型">
	      		<template slot-scope="scope">
	      		  <div class="file-wrapper">							    
	      			  <el-select size="medium" v-model="scope.row.fieldType" :disabled="true">
	      			    <el-option
	      			      v-for="(item, index) in panelTypeList"
	      			      :key="index"
	      			      :label="item.label"
	      			      :value="item.value"
	      			    ></el-option>
	      			  </el-select>
	      		  </div>
	      		</template>
	      	</el-table-column>	      		      	     	
	      </el-table>
	    </div>
	    <div slot="footer" class="dialog-footer my-dialog-footer">
	      <el-button  @click="createVisible = false">{{ $t('cancel') }}</el-button>
	      <el-button  type="primary" @click="handleCreateTask">{{$t('confirm')}}</el-button>
	    </div>
	  </el-dialog>
  </div>
</template>
<script>
// 引入 uuid 模块
import { getAiImage } from "@/api/app";
import { debounce } from "lodash";
import { formatFileSize } from "@/utils/tool";
import addOrEditMcp from "./components/addOrEditMcp.vue";
import {
  getCollectBaseConfig,
  getCollectDataList,
  addCollectBaseConfig,
  deleteCollectBaseConfig,
  editCollectConfig,
  editCollectData
} from "@/api/dataSj";
// import VueJsonEditor from 'vue-json-editor'
export default {
  components: {
    addOrEditMcp
  },

  data() {
    return {
	  isFlag:true,
	  isFlagLeft:true,
	  loading: false,
      appForm: {
        
      },
	  editeForm: {
        configName:'',
		describe:'',
      },
	  panelTypeList:[
	  	{
	  		label:'varchar(255)',
	  		value:'varchar(255)',
	  	},
	  	{
	  		label:'int',
	  		value:'int',
	  	},
	  	{
	  		label:'‌decimal(12,4)',
	  		value:'‌decimal(12,4)',
	  	},
	  	{
	  		label:'‌datetime',
	  		value:'‌datetime',
	  	},
	  	{
	  		label:'‌text',
	  		value:'‌text',
	  	},
	  ],
	  tableLeftData: [],
	  importVisible:false,
	  isDrag: false,
	  createVisible: false,
	  deleteDialogVisible: false,
	  searchKeyWord:'',
	  tableData: [],
	  tableEditData: [],
	  uploadForm: new FormData(),
	  uploadFile: [],
	  importLoading: false,
	  filename:[],
	  configId:'',
	  deleteId:'',
	  tableHeads:[],
	  tableIndex:0,
	  tabItem:{},
	  pageObj: {
	  	pageNo: 1,
	  	pageSize: 12,
	  	total: 0,
	  },
    }  
  },
  props: {
    dialogVisibleApplication: {
      type: Boolean,
      default: false,
    },
    params: Object,
    type: String,
  },
  
  mounted() {   
    this.appForm = this.params; 
	this.getDetails()
	// this.getCollectDataLists()
  },
  
  methods: {
	addFlag(){
		if(this.isFlag){
			this.isFlag = false
		}else{
			this.isFlag = true
		} 
	},
	addFlagLeft(){
		if(this.isFlagLeft){
			this.isFlagLeft = false
		}else{
			this.isFlagLeft = true
		} 
	},
    closePage(){
	   this.$emit("close", false);
    },
    addShowStatistical(){
		this.importVisible = true
	},
	getComponentNodeTableInfoListSql(){
		
	},
	addTable(item,index){
		this.tabItem = item
		this.tableIndex = index
		this.configId = item.configId
		 this.getCollectDataLists()
	},
    getDetails() {
		var obj = {
			collectId:this.appForm.collectId
		}
      getCollectBaseConfig(obj).then((data) => {
        if (data.code == "000000") {
          this.tableLeftData = data.data;
		  
		  if(data.data.length>0){
			  this.configId = data.data[0].configId
			  this.tabItem = data.data[0]
			  this.getCollectDataLists()
		  }
        }
      });
    },
	handleCurrentChange(page) {
		this.pageObj.pageNo = page;
		this.getCollectDataLists();
	},
	handleSizeChange(size) {
		this.pageObj.pageSize = size;
		this.getCollectDataLists();
	},
	getCollectDataLists() {
		var obj = {
			configId:this.configId,
			pageSize: this.pageObj.pageSize,
			pageNo: this.pageObj.pageNo
		}
	   getCollectDataList(obj).then((data) => {
	    if (data.code == "000000") {
	      this.tableData = data.data.records;
		  this.pageObj.total = data.data.totalRow
		  this.tableHeads = Object.keys(this.tableData[0]);
		  console.log(this.tableHeads,'this.tableHeads ')
		  // this.tableHeads = Object.keys(this.tableData[0]);
		  console.log(this.tableHeads.shift(),'this.tableHeads11 ')
		  // this.tableHeads = this.tableHeads.shift()
		  var obj = {}
		  this.tableEditData = []
		  this.tableHeads.forEach((item) => {
			   obj = {
				  fieldName :'',
				  fieldType : '',
				  fieldDesc :'',
				  titleName :item,
				  example : '',
			  }
			  this.tableEditData.push(obj)
		  })
		  console.log('this.tableHeads',this.tableHeads)
	    }
	  });
	},
	formatFileSize,
	handleDragLeave(e) {
	  const rect = e.currentTarget.getBoundingClientRect();
	
	  const isInside =
	    e.clientX >= rect.left &&
	    e.clientX <= rect.right &&
	    e.clientY >= rect.top &&
	    e.clientY <= rect.bottom;
	
	  if (!isInside) {
	    this.isDrag = false;
	  }
	},
	handleDragEnter(e) {
	  e.preventDefault();
	  this.isDrag = true;
	},
	handleDrag() {
	
	  this.isDrag = false
	},
	handleRemovefileItem(uid) {
		this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
	 	 this.uploadFile=this.uploadFile.filter(item=>item.uid!=uid);
		//  this.uploadForm.set("file",this.uploadFile)
		this.uploadForm=new FormData()
        }).catch(() => { });
	},
	beforeupload(file) {
	  this.isDrag = false;
	  let size = file?.size / 1024 / 1024;
	  console.log("size", size);
	  if (size > 15) {
	    this.$message.error("上传文件大小不能超过 15MB!");
	    return false; // 阻止上传
	  }
	  // this.uploadForm = new FormData();
	  // this.filename = []
	  console.log("12345", file);
	  // this.filename = file.name
	  // this.uploadForm.set("file", file);
	  // this.uploadFile = [file];
	  // uploadFile
	  this.uploadForm.append("files", file);
	  this.filename.push(file.name)
	  this.uploadFile.push(file)
	  // this.uploadForm.set("file", this.uploadFile);
	  return false;
	},
    handleCancelImport() {
      this.importVisible = false;
      this.uploadFile = [];
      this.uploadForm = new FormData();
    },
	async handleImport() {
	  if (this.uploadFile.length === 0) {
	    this.$message({
	      message: '请先上传数据表',
	      type: "warning",
	    });
	    return false;
	  }
	  this.importLoading = true
	  
	  this.uploadForm.set('collectId', this.appForm.collectId)
	  this.uploadForm.set('type', 'file')
	  this.uploadForm.set('describe', this.appForm.describe)
	  this.uploadForm.set('configNames', this.filename.join(','))
	  
	  const res = await addCollectBaseConfig(this.uploadForm)
		this.importLoading = false
	  console.log(res, '导入的情况')
	  if (res.code === '000000') {
	    this.importVisible = false;
	     this.uploadForm = new FormData();
			this.uploadFile = [];
		this.filename = []
	    this.getDetails()	
	  } else {
	    this.$message({
	      message: res.msg,
	      type: "error",
	    });
		this.importLoading = false
	  }
	  
	},
	handleCommand(value, item) {
		if (value == 'editeApp') {
			console.log('点击编辑',item)
			console.log('点击编辑fieldConfig',JSON.parse(item.fieldConfig))
			this.tabItem = item
			this.createVisible = true
			this.deleteId = item.configId
			this.editeForm.configName= item.configName
			this.editeForm.describe= item.describe
	        this.tableEditData = JSON.parse(item.fieldConfig)
		} else if (value == 'deleteApp') {
			this.deleteId = item.configId
			this.deleteDialogVisible = true
		}
	},
	addEdit(item){
		this.createVisible = true
		this.deleteId = item.configId
		this.editeForm.configName= item.configName
		this.editeForm.describe= item.describe
		this.tableEditData = JSON.parse(item.fieldConfig)
	},
	//删除弹窗关闭
	cancelDelete() {
		this.deleteDialogVisible = false
	},
	//确定删除
	confirmDelete() {
		deleteCollectBaseConfig(
			[this.deleteId]
		).then((data) => {
			if (data.code == "000000") {
				this.$message.success(data.msg);
				this.deleteDialogVisible = false
				setTimeout(() => {
					this.getDetails()
				}, 1000)
	
			}
		})
	},
	async handleCreateTask(){
		if (this.editeForm.configName =='' ) {
		  this.$message({
		    message: '请先输入数据表名称',
		    type: "warning",
		  });
		  return false;
		}
		if (this.editeForm.describe =='' ) {
		  this.$message({
		    message: '请先输入数据表描述',
		    type: "warning",
		  });
		  return false;
		}
		this.editeForm.configId = this.deleteId
		// this.tableEditData.forEach((item)=>{
		// 	item.example = item.fieldName
		// })
		this.editeForm.fields = this.tableEditData
		
		const res = await editCollectConfig(this.editeForm)		
		if (res.code === '000000') {
		   this.createVisible = false;		  		 
		   this.getDetails()	
		} else {
		  this.$message({
		    message: res.msg,
		    type: "error",
		  });
				
		}
	}
  },
};
</script>
<style lang="scss" scoped>
::v-deep .main {
  background-color: #f6f6f6 !important;
}

::v-deep .CodeMirror pre.CodeMirror-line,
.CodeMirror pre.CodeMirror-line-like {
  padding: 6px 4px !important;
}


.el-radio {
  display: flex;
  flex-direction: row-reverse;
  /* 反转子元素的排列顺序 */
  justify-content: flex-end;
  margin-left: 0 !important;
}

::v-deep .el-radio__label {
  width: 46px;
  font-size: 18px;
  margin-right: 70px !important;
}

.page-wrap {
  width: 100%;
  min-height: 100vh;
  background-color: #f6f6f6;

  .el-icon-back {
    cursor: pointer;
  }

  .page-header {
    height: 72px;
    width: 100%;
    background: #f4fbfd;

    .header-wrap {
      height: 72px;
      padding: 0 28px;

      .open-service {
        &:hover {
          background: #1036c4 !important;
        }

        &:focus {
          background: #1036c4 !important;
        }
      }
    }

    .demo-form-inline {
      align-items: center;

      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .line {
    width: 100%;
    height: 1px;
    background: #e7e7e7;
  }
}
.list-item-top-icon{
	img{
		width: 40px;
		height: 40px;
		margin-right: 10px;
	}
}
.content-table{
	padding: 20px;
	height: calc(100% - 141px);
}
.content{
	width: 100%;
	height: calc(100vh - 80px);
	background-color: #fff;
	border-radius: 6px;
	display: flex;
	position: relative;
	.content-left{
		width: 20%;
		// height: 100%;
		border-right: 1px solid #eee;
		padding: 20px;
		
		.content-left-num{
			font-size: 18px;
			margin-bottom: 20px;
		}
		.content-left-list{
			margin-top: 20px;
			
		}
		
		.content-left-list-item{
			display: flex;
			align-items: center;
			margin-bottom: 10px;
			padding: 5px;
			cursor: pointer;
			img{
				width: 30px;
				margin-right: 5px;
			}
		}
		.content-left-list-item:hover{
			background-color: #f2f4f5;
		}
		.content-left-list-item-on{
			background-color: #f2f4f5;
		}
		
	}
	.arrow-left{
		position: absolute;
		left: 19.5%;
		top: 30px;
		width: 20px;
		height: 50px;
		background-color: #fff;
		border: 1px solid #e1e1e1;
		border-radius: 10px;
		display: flex;
		justify-content: center;
		align-items: center;
		cursor: pointer;
		img{
			width: 14px;
			margin: auto;
		}
	}
	.content-right{
		height: 100%;
		width: 79%;
	}
	.content-right-header{
		
		background: #f8fbfe;
		padding: 20px;
		.content-right-header-top{
			display: flex;
			align-items: center;
			justify-content: space-between;
		}
		
		.content-left-list-item{
			display: flex;
			align-items: center;
			img{
				width: 30px;
				margin-right: 5px;
			}
		}
		.li-title{
			font-weight: 550;
		}
	}
	.content-desc{
		margin-top: 20px;
	}
	.xgpz{
		color: #1747E5;
		cursor: pointer;
		display: flex;
		align-items: center;
	}
}
.no-data {
	width: 100%;
display: flex;
flex-direction: column;
justify-content: center;
align-items: center;
&-img {
  width: 240px;
  height: 164px;
}
&-text {
  margin-top: 16px;
  font-size: 14px;
  color: #909399;
  text-align: center;
}
}
.file-num{
	margin-top:20px ;
	font-size: 16px;
	font-weight: 550;
	color: #000;
	position: relative;
	padding-left: 12px;
}
.file-num::before{
	content: '';
	display: inline-block;
	width: 3px;
	height: 20px;
	background-color: #1036c4;
	position: absolute;
	top: 50%;
	transform: translateY(-50%);
	left: 0;
}
.my-dialog{
	width: 100%;
}
::v-deep .el-upload{
	width: 100%;
}
::v-deep .el-upload-dragger {
  width: 100%;
  position: relative;
  height: 112px;
  background: #f9fafc;
  border-radius: 2px;
  border: 1px dotted rgba(0, 0, 0, 0.12);

  .el-upload-dragger {
    width: 100%;
    position: relative;
    height: 112px;
    border-radius: 2px;
    background-color: #f0f8ff;
    /* 浅蓝色 */
    border: 1px dotted #409eff;
    /* 蓝色边框 */
  }

  .icon-upload {
    width: 22px;
    height: 20px;
    margin: 16px 0 8px;
  }

  .el-icon-upload {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 20px;
    color: #494e57;
    line-height: 20px;
  }

  .el-upload__text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #494e57;
    line-height: 20px;
  }

  .el-upload__condition {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 12px;
    color: #b4bccc;
    line-height: 16px;
  }

  .draged {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #494e57;
    line-height: 20px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
}

.file {
  margin: 10px 0;
  display: flex;
  justify-content: space-between;

  .name {
    font-weight: bold;
  }
}
.el-icon-arrow-down{
		margin-left: 5px;
		cursor: pointer;
	}
.file-item {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  font-weight: bold;
  margin-top: 8px;

  i {
    cursor: pointer;
  }
}
.file-wrapper {
  display: flex;
  align-items: center;
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 14px;
  color: #494e57;
  line-height: 20px;

  .file-name {
    .name {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494e57;
      line-height: 20px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      -webkit-line-clamp: 1;
    }

    .size {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #bcc1cc;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      -webkit-line-clamp: 1;
    }
  }

  img {
    width: 24px;
    height: 24px;
    margin-right: 20px;
  }
}

.import-table {
  margin-bottom: 28px;

  ::v-deep .el-table__body-wrapper {
    height: auto !important;
    max-height: 100%;
  }
}
.pagination {
	// width: 100%;
  // position: fixed;
  // bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 32px;
  padding-left: 20px;
  margin-top: 0 !important;
  .total {
    // width: 58px;
    height: 24px;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #272a31;
    line-height: 24px;
    text-align: left;
    font-style: normal;
    text-transform: none;
  }
  .el-pagination {
    padding: 2px 0px 2px 5px;
}
  .el-pagination.is-background .btn-next,
  .el-pagination.is-background .btn-prev,
  .el-pagination.is-background .el-pager li {
    background-color: transparent !important;
    border-radius: 4px !important;
    border: 1px solid #dddfe8;
  }
  .el-pagination.is-background .el-pager li:not(.disabled).active {
    background: transparent;
    border: 1px solid #3666ea;
    font-size: 16px;
    color: #3666ea;
  }
  .el-pagination.is-background .el-pager li {
    background: transparent !important;
    border-radius: 4px;
    border: 1px solid #dddfe8;
  }
  .el-pagination .el-select .el-input .el-input__inner {
    font-size: 16px;
    color: #272a31;
  }
  .el-pagination__sizes{
    margin-right: 0;
  }
  .el-pagination .el-select .el-input {
    margin: 0 0 0 5px;
  }
}

::v-deep .el-dialog__header {
	background: #fff !important;
}

::v-deep .el-dialog__title {
	font-family: MiSans, MiSans;
	font-weight: 550;
	font-size: 20px;
	color: #333;
	line-height: 24px;
	text-align: left;
	font-style: normal;
	text-transform: none;
}
::v-deep  .el-form-item__label{
	font-family: MiSans, MiSans;
	font-weight: 550;
	font-size: 16px;
	color: #333;
	line-height: 24px;
	text-align: left;
	font-style: normal;
	text-transform: none;
	margin-bottom: 10px;
}
::v-deep .el-dialog__headerbtn {
	top: 36px;
}

.desc {
	font-family: MiSans, MiSans;
	font-weight: 400;
	font-size: 16px;
	color: #768094;
	line-height: 20px;
	text-align: left;
	font-style: normal;
	margin-bottom: 8px;
}

::v-deep .el-dialog__footer {
	text-align: right !important;

	.el-button {
		border-radius: 4px;
	}

	.el-button--primary {
		background: #1747E5;
		color: #fff;
		border-color: transparent;
	}
}
	
	::v-deep .el-dialog__body{
		padding: 10px 20px;
	}
	::v-deep .el-table thead{
		font-size: 16px;
		font-weight: 550;
		color: #000;
	}
	// ::v-deep .el-table td.el-table__cell{
	// 	border: 1px solid #EBEEF5;
	// }
</style>
