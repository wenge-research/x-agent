<template>
  <div>
  <el-drawer
    title=""
    :visible.sync="dialogVisible"

    :modal-append-to-body="true"
    :close-on-click-modal="true"
    :append-to-body="true"
    :wrapper-closable="false"
    size="1140px"
    :show-close="false"
    :withHeader="false"
    class="knowledgeDrawer"
  >
  <div class="knowledge-content">
	  <div class="content-left" style="width: 204px;background-color: #f6f8fa;">
	    <div class="content-left-header">选择数据表</div>
		<div style="margin-bottom: 24px">
		  <el-button
		    type="primary"
		    icon="el-icon-circle-plus"
		    style="width: 100%"
		    @click="addKnowledge"
		    >新建数据表</el-button
		  >
		</div>
	    <div style="margin-bottom: 24px">
	       <div class="list-box list-box-left">
	         <div :class="liTitle=='应用数据表'?'flex-center box-li-on':'flex-center'" style="margin-bottom: 24px;padding: 10px 10px;cursor: pointer;">
				 <div class="li-name flex-center" @click="liTitle='应用数据表'" >
				   <img src="@/assets/images/appManagement/sjb.svg"  style="width:20px;margin-right: 10px;"/>
				 
				   <div class="li-title" >应用数据表
            <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'于当前工作流内创建，且使用范围仅限该工作流的数据表'" placement="top" :effect="'light'">
              <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 4px;position: relative;top: 2px;"></iconpark-icon>
            </el-tooltip>
           </div>           
				 </div>
			 </div>
	         <div  :class="liTitle=='知识库数据表'?'flex-center box-li-on':'flex-center'" style="padding: 10px 10px;cursor: pointer;">
				 <div class="li-name flex-center" >
				   <img src="@/assets/images/appManagement/sjb.svg"  style="width:20px;margin-right: 10px;"/>
				 </div>
				 <div class="li-content" @click="liTitle='知识库数据表'">
				   <div class="li-title">知识库数据表</div>           
				 </div>
	         </div>
	       </div>
	    </div>
	    
	  </div>
    <div v-if="liTitle=='知识库数据表'" style="flex: 1;padding: 28px 0 0 0;box-sizing: border-box;">
      <div style="padding: 0 34px;">
        <el-input
          placeholder="搜索知识库表名"
          prefix-icon="el-icon-search"
          v-model="searchKnowKeyWord"
          style="width:276px;"
          @input="knowledgeList"
        >
        </el-input>
      </div>
      <div style="display: flex;">
        <div class="content-left" v-if="liTitle=='知识库数据表' && konwlwdgeList.length>0">
          <el-row style="margin-bottom: 24px" v-loading="loading">
            <div class="list-box list-box-left" style="height: calc(100vh - 100px);">
              <ul>
                <li
                  v-for="(item,index) in konwlwdgeList"
                  :key="index" @click="addGetUnionListData(item,index)"
                >
          <div class="base-li-left flex-center just">
                  <div class="li-name flex-center" >
                    <img src="@/assets/images/appManagement/zsk.svg" />
                  </div>
                  <div class="li-content">
                    <div class="li-title">{{ item.knowledgeName }}</div>
                      
                  </div>
            <iconpark-icon name="close-line" size="24" :name="tableDataIndex==index?'arrow-up-s-line':'arrow-down-s-line'"></iconpark-icon>
          </div>
          <ul v-if="tableData.length>0&&tableDataIndex==index">
          <li
            class=""
            v-for="(ite,inx) in tableData"
            :key="ite.id" @click.stop="addGetSqlListData(ite,inx)"
            :class="sqlDataIndex==inx?'base-li-left-ren flex-center just on':'base-li-left-ren flex-center just'"
          >
            <div class="li-name flex-center" >
              <img :src="require(`@/assets/images/${ite.type}.svg`)" alt="">
            </div>
            <div class="li-content">
            <div class="li-title">{{ ite.description }}</div>
            
            </div>
            
          </li>
          </ul>
              <div v-if="tableData.length==0&&tableDataIndex==index" style="margin:30px auto ;text-align: center;">暂无数据</div>
                </li>
              </ul>
              
            </div>
          </el-row>
          
        </div>
        <div class="content-right" v-if="liTitle=='知识库数据表'">
          <div class="content-right-header">
            <div class="flex-center">
              <div class="tabs">
                <div v-for="(tab, index) in tabsList" :key="index" class="tab-item" :class="{'active': tab.value === activeTab}" @click.stop="handleTabClick(tab.value)">{{ tab.name }}</div>
              </div>
            </div>
            <iconpark-icon name="close-line" size="24" @click.stop="cancelAss"></iconpark-icon>
            
          </div>
          <div class="content-right-body" v-loading="sqlLoading">
            <div class="list-box">
              <ul v-if="!showAdd">
                <li
                  class="base-li flex-center just"
                  v-for="(item,index) in sqlList"
                  :key="item.index"
                >
                  <div class="li-name flex-center" >
                    <img src="@/assets/images/sql.png">
                  </div>
                  <div class="li-content">
                    <div class="li-title">{{ item }}</div>
                  </div>
                  <el-button
                    class="delete-btn"
                    type="danger"
                    v-if="sqlIndex==index"
                    icon="el-icon-remove-outline"
                    @click="detItem(index)"
                    size="small"
                    >{{ $t("remove") }}</el-button
                  >
                  <el-button
                    v-else
                    class="add-btn"
                    type="primary"
                    icon="el-icon-circle-plus-outline"
                    size="small"
                    @click="addItem(item,index)"
                    >添加</el-button
                  >
                </li>
              </ul>
          <div v-if="sqlList.length==0" class="no-data">
            <img class="no-data-img" src="@/assets/images/no-data.png" alt="" />
            <div class="no-data-text">{{ $t("noData") }}</div>
          </div>
            </div>
            
          </div>
        </div>
      </div>
    </div>
    
    
    <div class="content-right" v-else>
      <div class="content-right-header">
        <!-- <div class="flex-center">
          <div class="tabs">
            <div v-for="(tab, index) in tabsList" :key="index" class="tab-item" :class="{'active': tab.value === activeTab}" @click.stop="handleTabClick(tab.value)">{{ tab.name }}</div>
          </div>
        </div> -->
		<div>
		  <el-input
		    placeholder="搜索数据表名"
		    prefix-icon="el-icon-search"
		    v-model="searchKeyWord"
		    style="width: 100%"
		    @input="getComponentNodeTableInfoListSql"
		  >
		  </el-input>
		</div>
        <iconpark-icon name="close-line" size="24" @click.stop="cancelAss"></iconpark-icon>
        
      </div>
      <div class="content-right-body" v-loading="sqlLoading">
        <div class="list-box">
          <ul>
            <li
              class="base-li flex-center just"
              v-for="(item,index) in tableInfoList"
              :key="item.index"
			  @click="addSqlDetails(item)"
            >
              
			  <div class="flex-center just">
				  <div class="li-name flex-center" >
				     <img :src="item.icon" v-if="item.icon!=''&&item.icon!='null'&&item.icon!=null">
					  <img src="@/assets/images/sql.png" v-else>
				  </div>
				  <div>
					  <div class="li-content">
						<div class="li-title">{{ item.tableName }}</div>
					  </div>
					  <div class="li-content">
						<div class="li-introduce">{{ item.description }}</div>
					  </div>
				   </div>
			  </div>
              <el-button
                class="delete-btn"
                type="danger"
                v-if="sqlInfoIndex==index"
                icon="el-icon-remove-outline"
                @click.stop="detItem1(index)"
                size="small"
                >{{ $t("remove") }}</el-button
              >
              <el-button
                v-else
                class="add-btn"
                type="primary"
                icon="el-icon-circle-plus-outline"
                size="small"
                @click.stop="addItem1(item.tableName,index)"
                >添加</el-button
              >
            </li>
          </ul>
    		 <div v-if="tableInfoList.length==0" class="no-data">
    			<img class="no-data-img" src="@/assets/images/no-data.png" alt="" />
    			<div class="no-data-text">{{ $t("noData") }}</div>
    		</div>
        </div>
        
      </div>
    </div>
  </div>
   
  </el-drawer>
  <el-dialog
      title="新建数据表"
      :visible.sync="dialogVisibleApplication"
      width="560px"
      :before-close="cancelTemplate"
      class="applicationDialog"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
  >
      <div style="position: relative">
          <div class="formOuter">
              <div class="uploadImg">
                 
                  <div>
                      <el-form
                          :model="appForm"
                          :rules="rules"
                          ref="ruleForm"
                          class="demo-ruleForm"
                      >
                         
                          <el-form-item
                              label="数据表名"
                              prop="tableName"
                          >
                              <el-input
                                  v-model="appForm.tableName"
                                  show-word-limit
                                  maxlength="100"
                                  style="width: 100%"
                              />
                          </el-form-item>
                          <el-form-item
                              label="描述"
                              prop="componentDesc"
                          >
                              <el-input
                                  class="inputTextarea"
                                  type="textarea"
                                  :rows="4"
                                  v-model="appForm.description"
                                  show-word-limit
                                  maxlength="1000"
                                  style="width: 100%"
                              />
                          </el-form-item>
                          
                          <el-form-item label="图标">
                              <div class="uploadOuter">
                                  <el-upload
                                      :action="actionUrl"
                                      :data="{ filePath: 'agent_source' }"
                                      :class="{ hideBox: uploadBtnLogo }"
                                      :file-list="fileListLogo"
                                      :show-file-list="false"
                                      :limit="1"
                                      class="logoAppUpload"
                                      list-type="picture-card"
                                      :on-remove="handleLogoRemove"
                                      :on-success="handleLogoSuccess"
                                  >
                                      <div @mouseenter="imgMouseenter" @mouseleave="imgMouseleave" style="position: relative; height: 100%" v-if="appForm.icon">
                                          <img
                                              :src="appForm.icon"
                                              style="
                                                  width: 80px;
                                                  height: 80px;
                                                  border-radius: 4px;
                                              "
                                          />
                                          <div
                                              v-show="showDeleteIcon"
                                              class="opts-btn"
                                          >
                                              <iconpark-icon name="edit-line" size="18" color="#FFFFFF"></iconpark-icon>
                                              <iconpark-icon  name="delete-bin-4-line" class="delete" size="18" color="#FFFFFF" @click.stop="deleteLogo"></iconpark-icon>
                                          
                                          </div>
                                      </div>
                                      <div v-else style="height: 100%; display: flex; align-items: center; justify-content: center; background: #f2f4f7">
                                          <iconpark-icon name="add-line" size="24" color="#8c939d"></iconpark-icon>
                                      </div>
                                  </el-upload>
                                  <el-button class="ai-btn" type="primary" :loading="imgLoading" @click="getImageUrl">
                                      <img src="@/assets/images/ai-btn.svg" alt="">
                                      AI生成
                                  </el-button>
                              </div>
                          </el-form-item>
                      </el-form>
                  </div>
              </div>
          </div>
      </div>
      <span slot="footer" class="dialog-footer">
          <el-button @click="cancelTemplate">{{ $t("cancel") }}</el-button>
          <el-button type="primary" @click="confirmTemplate">{{
              $t("confirm")
          }}</el-button>
      </span>
  </el-dialog>
  <!-- 切片信息 -->
  <el-drawer :visible.sync="sqlDetailsDialogVisible" size="100%" :with-header="false" append-to-body :modal-append-to-body="false"
    :close-on-click-modal="false" :show-close="true" :close-on-press-escape="false" :wrapperClosable="false">
    <sqlDetails     
        @closeDialog="closeAddSensitiveLibraryDialog"
    	  :sqlDetails="sqlDetailsObj"
      ></sqlDetails>
  </el-drawer>
 
  </div>
</template>
  
<script>
import { getKnowledgeInfoList,getUnionList,getTableList } from "@/api/index";
import { addComponentNodeTableInfo,getComponentNodeTableInfoList } from "@/api/sql";
import sqlDetails from "@/views/workflowConfig/dragDemo/components/sqlDetails";
import { getAiImage } from '@/api/app';
export default {
  components: {
    sqlDetails
  },
  data() {
    return {
	 dialogVisibleApplication:false,
	 sqlDetailsDialogVisible:false,
	 sqlDetailsObj:{},
	 appForm: {
	     tableName: '',
		 description: '',
	     icon: require("@/assets/images/default-icon-workflow.svg"), // 默认图像的URL
	     
	 },
	 rules: {
	     tableName: [
	         {
	             required: true,
	             message: '请输入数据表名',
	             trigger: "blur",
	         },
	     ]
	 },
	 searchKeyWord:'',
   searchKnowKeyWord:"",
	 uploadBtnLogo: false,
	 fileListLogo: [],
	 actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
      pageNo: 1,
      pageSize: 10,
      total: 0,
	  liTitle:'',
	  tableInfoList: [], //知识库 分页
      konwlwdgeList: [], //知识库 分页
      sqlLoading: false,
      knowledgeIdArr: [],
	  tableData: [],
	  sqlList:[],
	  tableDataIndex:null,
	  sqlDataIndex:0,
	  sqlIndex:null,
	  sqlInfoIndex:null,
      showAdd: false,
      konwlwdgeAllList: [], //知识库
	  knowledgeId:'',
	  businessId:'',
	  tableName:'',
      tabsList: [
        {
          name: "全部",
          value: "",
        },
        // {
        //   name: "我的",
        //   value: "user",
        // },
      ],
      activeTab: '',
      sortFeild: "update_time",
      showDeleteIcon: false,
      imgLoading: false,
      addSensitiveDialogVisible: false,
      knowledgeIdArrOld: []
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    configData: Array,
    appConfigForm: Object,
  },
  mounted() {
	this.knowledgeList();
    this.getComponentNodeTableInfoListSql()
	this.knowledgeIdArr = this.configData || [];
    this.knowledgeIdArrOld = this.configData ? JSON.parse(JSON.stringify(this.configData)) : [];
	this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
	if (this.knowledgeIdArrOld.length>0) {
		if(this.knowledgeIdArrOld[0].knowledgeId){
			this.liTitle='知识库数据表'
		}else{
			this.liTitle='应用数据表'
		}
	    
	} 
  },
  methods: {
	  closeAddSensitiveLibraryDialog(){
		  this.sqlDetailsDialogVisible = false
	  },
	  addSqlDetails(item){
		  console.log('1221')
		  this.sqlDetailsObj = item
		  this.sqlDetailsDialogVisible = true
	  },
	  addKnowledge(){
		  this.dialogVisibleApplication = true
		  this.liTitle='应用数据表'
	  },
	  addGetUnionListData(item,index){
		  this.tableDataIndex = index
		  this.knowledgeId = item.knowledgeId
		  this.getUnionListData()
	  },
	  addGetSqlListData(item,index){
		  this.sqlDataIndex = index
		  this.businessId = item.businessId
		  this.getSqlListData()
	  },
	  async getSqlListData() {
	    this.sqlLoading = true
	    const res = await getTableList({
	      businessId: this.businessId,
	    })
	    this.sqlList = res.data
	    this.tableName = res.data[0]
	    this.sqlLoading = false
		if (this.knowledgeIdArrOld.length>0) {
			this.tableName = this.knowledgeIdArrOld[0].tableName
		    this.sqlIndex = this.sqlList.findIndex((items) => items == this.knowledgeIdArrOld[0].tableName);
		}
	  },
	  knowledgeList() {
	    this.loading = true;	    
	    getKnowledgeInfoList({
	      pageNo: this.pageNo,
	      pageSize: 999999,	     
	      order: this.sortFeild,
	      sort: 'desc',	      
	      status: '是',
        knowledgeName:this.searchKnowKeyWord
	    }).then((res) => {
	      if (res.code == "000000") {
	        this.konwlwdgeList = res.data?.records.filter(item => item.dataSourceParserInfoNum && item.dataSourceParserInfoNum > 0);
	        this.total = res.data.totalRow;
	        this.loading = false;
			if (this.knowledgeIdArrOld.length>0) {
				if(this.knowledgeIdArrOld[0].knowledgeId){
					let konwlwdgeListId = this.konwlwdgeList.map((item) => item.knowledgeId);
					this.tableDataIndex = konwlwdgeListId.findIndex((items) => items == this.knowledgeIdArrOld[0].knowledgeId);
					this.knowledgeId = this.knowledgeIdArrOld[0].knowledgeId
					this.getUnionListData()
				}
			    
			} 
	      } else {
	        this.konwlwdgeList = [];
	      }	      
	    });
	  },
    // 显示关联知识库
    filterKnowledge(item) {
      if (this.knowledgeIdArr) {
        return this.knowledgeIdArr.includes(item);
      } else {
        return false;
      }
    },   
    //获取sql
	async getUnionListData() {
	  this.tableLoading = true
	  let params = {
	    pageNo: this.pageNo,
	    pageSize: 999,
	    desc: this.desc,
	    knowledgeId: this.knowledgeId,
	  }
	  const { data } = await getUnionList(params)
	  this.tableData = data.records
	  
	  if (this.knowledgeIdArrOld.length>0) {
	      let businessIdListId = this.tableData.map((item) => item.businessId);
	      this.sqlDataIndex = businessIdListId.findIndex((items) => items == this.knowledgeIdArrOld[0].businessId);
	  	  this.businessId = this.knowledgeIdArrOld[0].businessId
	  	  this.getSqlListData()
	  }else{
		  this.businessId = this.tableData[0].businessId
		  this.getSqlListData()
	  }
	  
	},
	
    // 添加知识库
    addItem(data,index) {
	  this.sqlIndex = index
	  this.tableName = data
	  this.knowledgeIdArr = []
	  let obj = {
		  knowledgeId:this.knowledgeId,
		  businessId:this.businessId,
		  tableName:this.tableName
	  }
      this.knowledgeIdArr.push(obj);
      this.setKnowledgeIds();
    },
	addItem1(data,index) {
	  this.sqlInfoIndex = index
	  this.tableName = data
	  this.knowledgeIdArr = []
	  let obj = {
		  knowledgeId:'',
		  businessId:'',
		  tableName:this.tableName
	  }
      this.knowledgeIdArr.push(obj);
      this.setKnowledgeIds();
    },
    // 移除知识库
    detItem(data,index) { 
	   this.sqlInfoIndex = null
      this.knowledgeIdArr.splice(index, 1);
      this.setKnowledgeIds();
    },
    detItem1(data,index) { 
	 this.sqlInfoIndex = null
      this.knowledgeIdArr.splice(index, 1);
      this.setKnowledgeIds();
    },
    // 确认添加知识库
    setKnowledgeIds() {
      console.log('this.knowledgeIdArr',this.knowledgeIdArr)
      this.$emit("clickConfigParams", "addSensitive", this.knowledgeIdArr);
    },
    // 取消添加
    cancelAss() {
      
      this.$emit("clickConfig", false);
    },
    handleTabClick(tab) {
      this.activeTab = tab;
      this.pageNo = 1;
      this.knowledgeList();
    },
    imgMouseenter() {
        console.log("imgMouseenter");
        if (this.appForm.icon) {
            this.showDeleteIcon = true;
        }
    },
    imgMouseleave() {
        console.log("imgMouseleave");
        this.showDeleteIcon = false;
    },
    deleteLogo() {
        this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
        return;
    },
    cancelTemplate() {
      this.dialogVisibleApplication = false
    },
    confirmTemplate() {
        this.$refs.ruleForm.validate((valid) => {
            if (valid) {
				console.log('111')
				// if (!/^[a-zA-Z_]+$/.test(this.appForm.tableName)) {
				//         // 如果不符合条件，可以清除非法字符或者提醒用户				      
				//         // 或者显示错误信息
				// 		this.$message.warning('数据表名输入只能包含英文字符或下划线！')
				//        return
				//     }
				if (this.appForm.tableName=='') {
				        // 如果不符合条件，可以清除非法字符或者提醒用户				      
				        // 或者显示错误信息
						this.$message.warning('请输入数据表！')
				       return
				    }
                addComponentNodeTableInfo(this.appForm).then((res) => {
                  if (res.code == "000000") {
                    this.dialogVisibleApplication = false
					this.appForm.tableName =  ''
					this.appForm.description =  ''
					this.getComponentNodeTableInfoListSql()
                  } else {
                        this.$message.warning(res.msg)
                  }	      
                });
            } else {
                return false;
            }
        });
    },
	getComponentNodeTableInfoListSql() {	     
	  getComponentNodeTableInfoList({
	    pageNo: this.pageNo,
	    pageSize: 999999,
		tableName: this.searchKeyWord,
	  }).then((res) => {
	    if (res.code == "000000") {
	      this.tableInfoList = res.data?.records;
		  let sqlInfoList = this.tableInfoList.map((item) => item.tableName);
		  if(this.knowledgeIdArrOld.length>0){
			   this.sqlInfoIndex = sqlInfoList.findIndex((items) => items == this.knowledgeIdArrOld[0].tableName);
		  }
		 
	      
	    } else {
	      this.tableInfoList = [];
	    }	      
	  });
	},
    handleLogoRemove(file, fileList) {
        this.uploadBtnLogo = false;
        this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
        this.fileListLogo = [];
    },
    handleLogoSuccess(response, file, fileList) {
        if ((response.code = "000000")) {
            this.uploadBtnLogo = true;
            this.appForm.icon =
                response.data && response.data[0]
                    ? response.data[0].url
                    : "";
            this.fileListLogo = [];
        } else {
            this.uploadBtnLogo = false;
            this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
            this.fileListLogo = [];
        }
    },
    handleTypeChange(value) {       
		if(this.appForm.icon.includes("default-workflow-")) {
			this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
		}       
    },
    getRandomHeadImgDefaultBgColor() {
        const colors = ['#2BCBCB', '#2AA3CB', '#2B7BCB', '#5B90F9', '#5B69F9', '#9C5BF9', '#C45BF9'];
        const workflowImgList = [
            require('@/assets/images/default-workflow-1.svg'),
            require('@/assets/images/default-workflow-2.svg'),
            require('@/assets/images/default-workflow-3.svg'),
            require('@/assets/images/default-workflow-4.svg'),
            require('@/assets/images/default-workflow-5.svg'),
            require('@/assets/images/default-workflow-6.svg'),
            require('@/assets/images/default-workflow-7.svg'),
        ];
        const dialogueImgList = [
            require('@/assets/images/default-dialogue-1.svg'),
            require('@/assets/images/default-dialogue-2.svg'),
            require('@/assets/images/default-dialogue-3.svg'),
            require('@/assets/images/default-dialogue-4.svg'),
            require('@/assets/images/default-dialogue-5.svg'),
            require('@/assets/images/default-dialogue-6.svg'),
            require('@/assets/images/default-dialogue-7.svg'),
        ];
        const imgList = this.appForm.type == 2 ? workflowImgList : dialogueImgList;
        const randomIndex = Math.floor(Math.random() * imgList.length);
        return imgList[randomIndex];
    },
    getImageUrl() {
        this.$refs.ruleForm.validate(async(valid) => {
            if (valid) {
                this.imgLoading = true;
                getAiImage({
                    topic: this.appForm.tableName,
                    description: this.appForm.description
                }).then((res) => {
                    if (res.code == "000000") {
                        this.appForm.icon = res.data || this.getRandomHeadImgDefaultBgColor();
                    }else {
                        this.$message.warning('生成失败')
                    }
                    
                    this.imgLoading = false;
                }).catch(() => {
    
                    this.imgLoading = false;
                });
            } else {
                return false;
            }
        });
    }
  },
};
</script>
  
<style lang="scss" scoped>
.knowledgeDrawer {
  ::v-deep .el-drawer {
    background: #FFFFFF;
    box-shadow: 0px 1px 4px 4px rgba(0,0,0,0.05);
    border-radius: 4px 0px 0px 4px;
    .el-drawe__body {
      padding: 0px;
    }
  }
  .knowledge-content {
    height: 100%;
    width: 100%;
    display: flex;
    overflow: hidden;
    .content-left {
      width: 344px;
      padding: 32px 24px;
      height: 100%;
      box-sizing: border-box;
      font-family: MiSans, MiSans;
      .content-left-header {
        font-weight: 500;
        font-size: 20px;
        color: #494E57;
        line-height: 32px;
        margin-bottom: 24px;
      }
    }
    .content-right {
      flex: 1;
      height: 100%;
      padding: 28px 0px 24px;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      .content-right-header {
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 32px;
        font-family: MiSans, MiSans;
        margin-bottom: 16px;
        cursor: pointer;
        .tabs {
          display: flex;
          font-weight: 400;
          font-size: 18px;
          color: #828894;
          line-height: 28px;
          .tab-item {
            padding: 6px 0px;
            margin-right: 16px;
            cursor: pointer;
            &.active {
              font-weight: 500;
              font-size: 18px;
              color: #603ECA;
              position: relative;
              &:after {
                content: "";
                display: block;
                width: 20px;
                height: 3px;
                background: #603ECA;
                border-radius: 2px;
                position: absolute;
                margin-left: -10px;
                left: 50%;
                bottom: 0px;
              }
            }
          }
        }
      }
      .content-right-body { 
        flex: 1;
        overflow: hidden;
        display: flex;
        flex-direction: column;
        .list-box {
          flex: 1;
          overflow-y: auto;
          padding: 0px 32px;
        }
      }
    }
  }
}
.list-box-left{
	overflow-y: auto;
	height: 90vh;
}
.base-li {
  background: #ffffff;
  border-radius: 2px;
  border: 1px solid #D5D8DE;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;

  .li-name {
    font-weight: 400;
    font-size: 14px;
    color: #383d47;
    text-align: left;
    font-style: normal;
    cursor: pointer;

    > img {
      width: 36px;
      height: 36px;
      border-radius: 2px;
      margin-right: 16px;
    }
  }
  .li-content {
    flex: 1;
    overflow: hidden;
    cursor: pointer;
    .li-title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-bottom: 4px;
    }
    .li-introduce {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #828894;
      line-height: 16px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-bottom: 8px;
	  max-width: 700px;
    }
    .li-content-bottom {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #828894;
      line-height: 20px;
      > span {
        margin-right: 12px;
      }
      .li-num {
        display: inline-block;
        line-height: 20px;
        padding: 0 4px;
        background: #EBEEF2;
        border-radius: 2px;
        color: #494E57;
      }
    }
  }
  &:hover {
    background: #F2F4F7;
  }
}
.box-li-on{
	background: #ecefec;
}
.base-li-left-ren{
	background: #ffffff;
	  border-radius: 2px;
	  padding: 10px 10px 10px 40px;
	  cursor: pointer;
	
	  .li-name {
	    font-weight: 400;
	    font-size: 14px;
	    color: #383d47;
	    text-align: left;
	    font-style: normal;
	    cursor: pointer;
	
	    > img {
	      width: 26px;
	      height: 26px;
	      border-radius: 2px;
	      margin-right: 16px;
	    }
	  }
	  .li-content {
	    flex: 1;
	    overflow: hidden;
	    cursor: pointer;
	    .li-title {
	      font-family: MiSans, MiSans;
	      font-weight: 500;
	      font-size: 14px;
	      color: #494E57;
	      line-height: 20px;
	      overflow: hidden;
	      text-overflow: ellipsis;
	      white-space: nowrap;
	      margin-bottom: 4px;
	    }
	    
	    
	  }
	  &:hover {
	    background: #F2F4F7;
	  }
	  
	}
.on {
	background: #F2F4F7;
  }
.base-li-left {
  background: #ffffff;
  border-radius: 2px;
  padding: 10px;
  margin-bottom: 12px;
  cursor: pointer;

  .li-name {
    font-weight: 400;
    font-size: 14px;
    color: #383d47;
    text-align: left;
    font-style: normal;
    cursor: pointer;

    > img {
      width: 36px;
      height: 36px;
      border-radius: 2px;
      margin-right: 16px;
    }
  }
  .li-content {
    flex: 1;
    overflow: hidden;
    cursor: pointer;
    .li-title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-bottom: 4px;
    }
    
    
  }
  &:hover {
    background: #F2F4F7;
  }
}
.flex-center {
  display: flex;
  align-items: center;
}

.just {
  justify-content: space-between;
}

::-webkit-scrollbar {
  display: none;
}
.no-data {
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
}
}
.applicationDialog {
    ::v-deep .el-dialog {
        border-radius: 4px;
        .el-dialog__body {
            padding: 16px 32px;
        }
        .el-dialog__header {
            background: #fff;
            border-radius: 8px 8px 0px 0px;
            .el-dialog__headerbtn {
                top: 36px;
                right: 32px;
            }
        }
        .el-dialog__footer {
            text-align: right;
            padding: 10px 32px 20px;
        }
    }
}
.inputTextarea {
    ::v-deep .el-textarea__inner {
        font-family: MiSans, MiSans;
    }
}
.logoAppUpload {
    text-align: center;
    ::v-deep .el-upload--picture-card {
        border: 0;
        width: 80px;
        height: 80px;
    }
}

.demo-ruleForm {
    ::v-deep .el-form-item__label {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #383d47;
        line-height: 32px;
    }
}
.flex {
    display: flex;
}
.flex-center {
    display: flex;
    align-items: center;
}

.aligns {
    align-items: center;
}
.just {
    justify-content: space-between;
}
.uploadOuter {
  width: 100%;
  display: flex;
  align-items: end;
  :deep(.ai-btn) {
    height: 32px;
    background: linear-gradient( 270deg, rgba(142, 101, 255, .15) 0%, rgba(23, 71, 229, .15) 100%);
    border-radius: 2px;
    display: inline-flex;
    align-items: center;
    border: 0px;
    padding: 0px 8px;
    
    margin-left: 16px;
    font-size: 14px;
    color: #1747E5;
    span {
      display: inline-flex;
      align-items: center;
    }
    img {
      margin-right: 2px;
      width: 16px;
      height: 16px;
      border: none;
    }
  }
}
.type-radio-group {
    display: flex;
    gap: 16px;
    .type-radio {
        flex: 1;
        height: 96px;
        background: #FFFFFF;
        border-radius: 2px;
        border: 1px solid #D5D8DE;
        padding: 12px;
        cursor: pointer;
        .title {
            display: flex;
            align-items: center;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 16px;
            color: #383D47;
            line-height: 24px;
            margin-bottom: 12px;
            img {
                width: 24px;
                height: 24px;
                margin-right: 8px;
            }
        }
        .desc {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #828894;
            line-height: 18px;
        }
        &.active {
            background: rgba(28,80,253,0.05);
            border: 1px solid #1747E5;
        }
        
    }
}
.opts-btn {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    background: rgba(0, 0, 0, .4);
    backdrop-filter: blur(1px);
    position: absolute;
    bottom: 0;
    right: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
}

</style>
  