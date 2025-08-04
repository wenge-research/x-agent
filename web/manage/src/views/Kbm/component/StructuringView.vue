<template>
  <div class="structuring-view document-view">
    <LeftTree @change="handleNodeClick" :type="5" :knowledgeId="knowledgeId" :isAdminOrUser="isAdminOrUser"></LeftTree>
    <div class="view-right-wrapper">
      <div class="right-header">
        <div class="form-box">
          <el-form :inline="true">
            <el-form-item>
              <el-input v-model="desc" :placeholder="$t('pleaseEnterDocumentName')" class="my-input" clearable @input="handleSearch">
                <i slot="suffix" class="el-input__icon el-icon-search" />
              </el-input>
            </el-form-item>

            <!-- <el-form-item>
              <el-button type="primary" size="small" @click="getUnionListData">{{$t('search')}}</el-button>
            </el-form-item> -->
          </el-form>
        </div>
        <!-- <div class="option-box">
          <div>
            <el-button :disabled="multipleSelection.length === 0" slot="reference" type="danger" icon="el-icon-delete"
              size="small" @click="batchesRemTask">{{$t('batch')}}{{$t('delete')}}</el-button>
          </div>
          <div>
            <el-button type="primary" :disabled="multipleSelection.length === 0" icon="el-icon-setting" size="small"
                @click="timeInBatches">{{ $t('batchSetExpirationTime') }}</el-button>
          </div>
        </div> -->
        <div class="btns-box">
          <el-button v-if="!isAll" type="primary" @click="createTask" :disabled="!isAdminOrUser"><i class="el-icon-plus" style="margin-right: 8px;"></i>{{$t('newStructuringTask')}}</el-button>
        </div>
      </div>
      <div class="table-btns">
        <div class="selected-num">{{ $t('selected') }}<span class="num">{{multipleSelection.length}}</span></div>
        <el-checkbox v-model="isSelectedAll" @change="handleSelectedAll">{{$t('all')}}</el-checkbox>
        <el-button type="text" :disabled="multipleSelection.length === 0" @click="handleClearSelection">{{$t('clearSelected')}}</el-button>
        <el-button type="text" :disabled="multipleSelection.length === 0" icon="el-icon-delete" v-if="isAdminOrUser"
               @click="batchesRemTask" style="margin-left: 0px;">{{$t('batch')}}{{$t('delete')}}</el-button>
      </div>
      <div class="table-box">
        <el-table ref="table" v-loading="tableLoading" class="my-table" :data="tableData" height="100%"
          @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="desc" :label="$t('fileDBName')" min-width="200">
            <template slot-scope="scope">
              <div class="file-wrapper">
                <img :src="require(`@/assets/images/${scope.row.type}.svg`)" alt="">
                <div class="fileName">{{ scope.row.desc }} </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="type" :label="$t('dataSourceType')" width="120">
            <template slot-scope="scope">
              <span>{{ scope.row.type == 'excel' ? $t('excelDoc') : $t('databaseConnection') }}</span>
            </template>
          </el-table-column>
          <!-- <el-table-column prop="" label="有效时间" width=""></el-table-column> -->
          <el-table-column prop="createTime" :label="$t('uploadTime')" width="200"></el-table-column>
          <el-table-column prop="enableFlag" :label="$t('enabledStatus')" width="120">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.enableFlag" :active-value="Number(1)" :inactive-value="Number(0)"
                active-color="#1747E5" inactive-color="#EAECF0" @change="handleEnableFlagChange(scope.row)"
                :class="[scope.row.enableFlag == 1 ? 'switch-on' : 'switch-off']" :width="24">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column prop="parserFlag" :label="$t('structuringStatus')" width="120">
            <template slot-scope="scope">
              <div class="file-type">
                <span :style="{ backgroundColor: TASK_TYP[scope.row.parserFlag].color }"></span>
                {{ TASK_TYP[scope.row.parserFlag].label }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="validDate" :label="$t('validDuration')" width="200">
            <template slot-scope="scope">
              <div>{{ scope.row.validDate == '2099-12-31 00:00:00' ? '永久有效' : scope.row.validDate }}</div>
            </template>
          </el-table-column>
          <!-- <el-table-column prop="synchStatus" :label="$t('synchronizationStatus')" width="120">
            <template slot-scope="scope">
              <div class="file-type" :class="[['0', '4'].includes(scope.row.synchStatus) ? 'clickable' : '']" @click="updateSynchStatusUnionData(scope.row)">
                <span :style="{ backgroundColor: TASK_TYP[scope.row.parserFlag].color }"></span>
                {{ synchronizationStatusOperate(scope.row.synchStatus) }}
              </div>
            </template>
          </el-table-column> -->
          <el-table-column fixed="right" :label="$t('action')" width="160">
            <template slot-scope="scope">
              <div class="btns">
                <el-button type="text" @click="viewTask(scope.row)">{{$t('viewResult')}}</el-button>
                <!-- <el-button type="text">管理</el-button> -->
                <el-button type="text" v-if="scope.row.type === 'datasource'" @click="detailsTask(scope.row)">{{$t('detailsText')}}</el-button>
                <el-button type="text" @click="remTask(scope.row)" :disabled="!isAdminOrUser">{{$t('delete')}}</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <div class="total-num">
          {{ $t('totalNum', { total: total }) }}
        </div>
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="pageSize" background
          :current-page="pageNo" :page-sizes="[20, 50, 100, 150, 200]" layout="prev, pager, next, sizes, jumper"
          :total="total">
        </el-pagination>
      </div>
    </div>
    <el-dialog class="my-dialog" :title="$t('newStructuringTask')" :visible.sync="createVisible" width="560px" :modal-append-to-body="false"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
      <div class="task-dialog">
        <div class="options">
          <div class="option" :class="{ 'option-active': item.id === taskActive }"
            v-for="item in TASK_TYPE_OPTIONS" :key="item.id" @click="handleTaskChange(item.id)">
            <img :src="item.icon" alt="">
            {{ item.label }}
          </div>
        </div>
        <div class="xlsx-con" v-if="[1].includes(taskActive)">
          <el-upload action="#" drag accept=".xlsx, .xls" :before-upload="beforeupload" :show-file-list="false">
            <img class="icon-upload" src="@/assets/images/upload-cloud-2-fill.svg" alt="">
            <div class="el-upload__text">{{$t('dragXlsxFileHere')}}，{{$t('or')}}{{ $t('clickUpload') }}</div>
            <div class="el-upload__condition">
              {{$t('supportedXlsx')}}
            </div>
          </el-upload>
          <div class="file-item" v-for="item in uploadFile" :key="item.uid">
            {{ item.name }} <i @click="handleRemovefileItem" class="el-icon-close"></i>
          </div>
        </div>
        <div class="sql-con" v-if="[2].includes(taskActive)">
          <el-form :model="sqlForm" ref="sqlForm" :rules="rules">
            <el-form-item :label="$t('fileLinkName')" prop="desc">
              <el-input v-model="sqlForm.desc" :placeholder="$t('pleaseEnterFileLinkName')"></el-input>
            </el-form-item>
			<el-form-item :label="$t('databaseConnectionType')" prop="desc">
			  <el-select :placeholder="$t('pleaseEnterFileLinkName')" v-model="sqlForm.dsType"
			    style="width: 100% ">
			    <el-option v-for="item in networkChannelList" :label="item.label" :value="item.value"
			      :key="item.value"></el-option>
			  </el-select>
			</el-form-item>
			
            <div class="inline-form-item">
              <el-form-item :label="$t('hostnameOrIpAddress')" prop="jdbcUrl">
                <el-input v-model="sqlForm.jdbcUrl" :placeholder="$t('pleaseEnterHostnameOrIpAddress')"></el-input>
              </el-form-item>
              <el-form-item label="端口" prop="port">
                <el-input v-model="sqlForm.port" placeholder="请输入端口"></el-input>
              </el-form-item>
            </div>
            <!-- <el-col :span="15">
              <el-form-item label="主机名/IP地址" prop="jdbcUrl">
                <el-input v-model="sqlForm.jdbcUrl" placeholder="请输入主机名/IP地址"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="1">
              <span style="opacity: 0;">-</span>
            </el-col>
            <el-col :span="8">
              <el-form-item label="端口">
                <el-input :disabled="true" v-model="sqlForm.port" placeholder="请输入端口"></el-input>
              </el-form-item>
            </el-col> -->
            <el-form-item :label="$t('userName')" prop="jdbcName">
              <el-input v-model="sqlForm.jdbcName" :placeholder="$t('pleaseEnterUsername')"></el-input>
            </el-form-item>
            <el-form-item :label="$t('password')" prop="jdbcPassword">
              <el-input v-model="sqlForm.jdbcPassword" :placeholder="$t('pleaseEnterPassword')" show-password></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div class="title">{{$t('validityPeriodSetting')}}</div>
        <div class="message">{{$t('dataAfterExpiry')}}</div>
        <el-date-picker v-model="time" type="datetime" :placeholder="$t('defaultPermanentValidity')" value-format="yyyy-MM-dd HH:mm:ss">
        </el-date-picker>
      </div>
      <div slot="footer" class="dialog-footer my-dialog-footer">
        <el-button :loading="createLoading" @click="createVisible = false">{{ $t('cancel') }}</el-button>
        <el-button :loading="createLoading" type="primary" @click="handleCreateTask">{{$t('confirm')}}</el-button>
      </div>
    </el-dialog>
    <el-dialog  :visible.sync="progressVisiable" width="width" :modal="false" :show-close="false"
    title="文件上传进度">
       <el-progress :percentage="progress"></el-progress>
    </el-dialog>
    <el-dialog class="my-dialog details-dialog" :title="$t('detailsText')" :visible.sync="detailsVisible" width="550px" :modal-append-to-body="false"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
      <div v-loading="detailLoading">
        <div class="item" v-for="item in DETAILS_FIELD_MAP" :key="item.id">
          <div class="title">{{ item.label }}</div>
          <div v-if="item.field != 'jdbcPassword'" class="con">{{ detail[item.field] }}</div>
          <template v-else>
            <div v-if="detail[item.field]" class="con">
              <template v-if="!isShowPassword">
                <span class="password-circle" v-for="it in detail[item.field].length" :key="it"></span>
                <img src="@/assets/images/eye-line-close.svg" alt="" @click="isShowPassword = true">
            </template>
            <template v-else>
              <span>{{ detail[item.field] }}</span>
              <img src="@/assets/images/eye-line.svg" alt="" @click="isShowPassword = false">
            </template>
            </div>
            
          </template>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailsVisible = false">{{$t('close')}}</el-button>
      </span>
    </el-dialog>

    <el-dialog :title="$t('deleteDataSource')" :visible.sync="remTaskVisible" width="400px" :modal-append-to-body="false"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
      <span>{{$t('confirmDelete')}}</span>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="remTaskLoading" @click="remTaskVisible = false">{{ $t('cancel') }}</el-button>
        <el-button :loading="remTaskLoading" type="primary" @click="handleRemTask">{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>

    <!-- 切片信息 -->
    <el-drawer :visible.sync="infoVisible" size="90%" :with-header="false" append-to-body :modal-append-to-body="false"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" :wrapperClosable="false">
      <StructuringInfo :row="row" @close="handleClose" :knowledgeId="knowledgeId" />
    </el-drawer>
  </div>
</template>

<script>
import StructuringInfo from '@/views/Kbm/component/StructuringInfo.vue'
import { getUnionList, addDataSourceParserInfo, addExcelParserInfo, delDataSourceParserInfo, getDataSourceDetail, excelEnable, dataSourceEnable, apiUpdateSynchStatusUnionData } from '@/api/index.js'
import { TASK_TYPE_OPTIONS, DETAILS_FIELD_MAP, TASK_TYP } from '@/views/Kbm/content/index'
import LeftTree from './LeftTree.vue'
import { debounce } from 'lodash';
export default {
  props: {
    knowledgeId: {
      type: String,
      default: ''
    },
    isAdminOrUser:{
      type:Boolean,
      default:true
    }
  },
  components: {
    StructuringInfo, LeftTree
  },
  data() {
    return {
		networkChannelList:[
			{label:'mysql',value:'1'},
			{label:'达梦',value:'2'},
			{label:'崖山',value:'3'},
			],
      ws:null,
      isRestart:false,
      remTaskVisible: false,
      remTaskLoading: false,
      TASK_TYPE_OPTIONS,
      DETAILS_FIELD_MAP,
      TASK_TYP,
      infoVisible: false,
      createLoading: false,
      detailsVisible: false,
      time: '',
      createVisible: false,
      tableLoading: false,
      tableData: [],
      desc: '',
      multipleSelection: [],
      taskActive: 1,
      uploadForm: new FormData(),
      uploadFile: [],
      sqlForm: {
        desc: '', // 名称
        jdbcUrl: '', // ip地址
        jdbcName: '', // 用户名
        jdbcPassword: '', // 密码
        port: ''// 端口
      },
      rules: {
        desc: [{ required: true, message: this.$t('pleaseEnterFileLinkName'), trigger: 'blur' }],
        jdbcUrl: [{ required: true, message: this.$t('pleaseEnterHostnameOrIpAddress'), trigger: 'blur' }],
        jdbcName: [{ required: true, message: this.$t('pleaseEnterUsername'), trigger: 'blur' }],
        port: [{ required: true, message: this.$t('pleaseEnterPort'), trigger: 'blur' }],
        jdbcPassword: [{ required: true, message: this.$t('pleaseEnterPassword'), trigger: 'blur' }],
      },
      row: {},
      detail: {},
      detailLoading: false,
      pageNo: 1,
      pageSize: 20,
      total: 0,
      task: {},
      strutsDelItems: [],
      isAll: true,
      isSelectedAll: false,
      isShowPassword: false,
      foldersId: '',
      progress:0,
      progressVisiable:false,
    }
  },
  mounted(){
    const userId = JSON.parse(sessionStorage.getItem('user')).accountName
    this.ws = new WebSocket(`wss://localhost${process.env.VUE_APP_BASE_API}/model-agent-ws/${userId}`)
    this.ws.onmessage = (event)=>{
        console.log(JSON.parse(event.data),'event.data.nums')
      this.progress = JSON.parse(event.data).nums
        console.log('收到消息')
        if(this.isRestart == true){
          this.ws.close()
        }
      if(this.progress == 100 && JSON.parse(event.data).msg == '任务完成'){
        // this.isRestart = true
        setTimeout(()=>{
          this.progressVisiable = false
          setTimeout(()=>{
          this.progress = 0
        },500)
        },1000)
        
        this.$message({
            message: res.msg,
			showClose: true,
            type: 'success'
          });
      }
       }
       this.ws.onclose=()=>{
        if(this.isRestart == false){
          this.restartWebSocket()
        }else{
          this.isRestart = false
          console.log('关闭')
        }
         }
         this.ws.onerror=(error)=>{
        console.log(error,'报错了')
         }
  },
  beforeDestroy() {
    this.isRestart = true
    this.ws.close()
  },
  created() {
  },
  methods: {
    handleSelectedAll() {
      this.$refs.table.toggleAllSelection();
    },
    handleClearSelection() {
      this.$refs.table.clearSelection();
    },
    handleSearch: debounce(function() {
      this.getUnionListData()
    }, 500),
    handleNodeClick(node) {
      if (!node) {
        this.isAll = true
        this.foldersId = ''
      } else {
        this.isAll = false
        this.foldersId = node
      }
      this.getUnionListData()
    },
    synchronizationStatusOperate(status) {
      let val = ""
      switch (status) {
        case "0":
          val = this.$t('notSynchronized')
          break;
        case "2":
          val = this.$t('synchronizing')
          break;
        case "3":
          val = this.$t('synchronizationCompleted')
          break;
        case "4":
          val = this.$t('synchronizationException')
          break;
      
        default:
          break;
      }
      return val;
    },
    async handleEnableFlagChange(row) {
      let res
      try {
        if (row.type === 'excel') {
          res = await excelEnable({
            businessId: row.businessId,
            enableFlag: Number(row.enableFlag)
          })
        } else {
          res = await dataSourceEnable({
            businessId: row.businessId,
            enableFlag: Number(row.enableFlag)
          })
        }
        if (res.code === '000000') {
          this.getUnionListData()
          this.$message({
            message: res.msg,
			showClose: true,
            type: 'success'
          });
        } else {
          this.$message({
            message: res.msg,
			showClose: true,
            type: 'warning'
          });
        }
      } catch (error) {
        this.$message({
          message: error,
		  showClose: true,
          type: 'warning'
        });
      }
    },
    async handleRemTask() {
      this.remTaskLoading = true
      try {
        const res = await delDataSourceParserInfo({
          strutsDelItems: this.strutsDelItems
        })
        if (res.code === '000000') {
          this.remTaskVisible = false
          this.getUnionListData()
          this.$message({
            message: res.msg,
			showClose: true,
            type: 'success'
          });
        } else {
          this.$message({
            message: res.msg,
			showClose: true,
            type: 'warning'
          });
        }
        this.remTaskLoading = false
      } catch (error) {
        this.$message({
          message: error,
		  showClose: true,
          type: 'warning'
        });
        this.remTaskLoading = false
      }
    },
    batchesRemTask() {
      this.strutsDelItems = this.multipleSelection.map(n => {
        return {
          businessId: n.businessId,
          type: n.type === 'datasource' ? 1 : 2
        }
      })
      this.remTaskVisible = true
    },
    remTask(row) {
      this.strutsDelItems = [
        {
          businessId: row.businessId,
          type: row.type === 'datasource' ? 1 : 2
        }
      ]
      this.remTaskVisible = true
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
      this.isSelectedAll = val.length === this.pageSize ||  val.length === this.tableData.length
    },
    handleClose() {
      this.infoVisible = false
    },
    handleCurrentChange(n) {
      this.pageNo = n
      this.getUnionListData()
    },
    handleSizeChange(n) {
      this.pageSize = n
      this.getUnionListData()
    },
    detailsTask(row) {
      this.detailsVisible = true
      this.getDataSourceDetailData(row.businessId)
    },
    viewTask(row) {
      this.infoVisible = true
      this.row = row
    },
    createTask() {
      this.init()
      this.createVisible = true
    },
    beforeupload(file) {
      this.uploadForm = new FormData()
      this.uploadForm.append('file', file);
      this.uploadFile = [file]
      return false;
    },
    handleRemovefileItem() {
      this.uploadForm = new FormData();
      this.uploadFile = []
    },
    handleTaskChange(id) {
      this.taskActive = id
      this.init()
    },
    async getUnionListData() {
      this.tableLoading = true
      let params = {
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        desc: this.desc,
        knowledgeId: this.knowledgeId,
      }
      if(this.foldersId)params.folderId = this.foldersId
      const { data } = await getUnionList(params)
      this.tableData = data.records
      this.total = data.total
      this.tableLoading = false
    },
    // 同步
    async updateSynchStatusUnionData(data) {
      if(['2', '3'].includes(data.synchStatus)) return;
      this.tableLoading = true
      const params = {
        businessId: data.businessId,
        knowledgeId: this.knowledgeId,
      }
      const res = await apiUpdateSynchStatusUnionData(params)
      if(res.code == '000000') {
        this.$message.success(res.data)
        this.getUnionListData()
      }
      this.tableLoading = false
    },
    handleCreateTask() {
      if (this.taskActive === 1) {
        this.fileManipulation()
      } else {
        this.databaseManipulation()
      }
    },
    // 数据库操作
    databaseManipulation() {
      this.$refs['sqlForm'].validate(async (valid) => {
        if (valid) {
          this.createLoading = true
          const res = await addDataSourceParserInfo({
            ...this.sqlForm,
            knowledgeId: this.knowledgeId,
            foldersId: this.foldersId,
            enableFlag: 1,
            validDate: this.time
          })
          if (res.code === '000000') {
            this.createVisible = false
            this.init()
            this.getUnionListData()
            this.$message({
              message: res.msg,
			  showClose: true,
              type: 'success'
            });
          } else {
            this.$message({
              message: res.msg,
			  showClose: true,
              type: 'warning'
            });
          }
          this.createLoading = false
        }
      });
    },
    restartWebSocket(){
      console.log('重启~~~')
      const userId = JSON.parse(sessionStorage.getItem('user')).accountName
      this.ws = new WebSocket(`wss://localhost${process.env.VUE_APP_BASE_API}/model-agent-ws/${userId}`)
       this.ws.onopen = ()=>{
        console.log('创建成功')
       }
       this.ws.onmessage = (event)=>{
        console.log(JSON.parse(event.data),'event.data.nums')
      this.progress = JSON.parse(event.data).nums
        console.log('收到消息')
        if(this.isRestart == true){
          this.ws.close()
        }
      if(this.progress == 100 && JSON.parse(event.data).msg == '任务完成'){
        // this.isRestart = true
        this.progressVisiable = false
       
        this.$message({
            message: res.msg,
			showClose: true,
            type: 'success'
          });
      }
       }
       this.ws.onclose=()=>{
        if(this.isRestart == false){
          this.restartWebSocket()
        }else{
          this.isRestart = false
          console.log('关闭')
        }
         }
         this.ws.onerror=(error)=>{
        console.log(error,'报错了')
         }
    },
    // 文件操作
    async fileManipulation() {
      if (this.uploadFile.length === 0) {
        this.$message({
          message: this.$t('pleaseSelectUploadFile'),
		  showClose: true,
          type: 'warning'
        });
        return false
      }
      this.createLoading = true
      this.uploadForm.append('knowledgeId', this.knowledgeId)
      this.uploadForm.append('foldersId', this.foldersId)
      this.uploadForm.append('enableFlag', 1)
      this.uploadForm.append('validDate', this.time)
      try {
        
        const res = await addExcelParserInfo(this.uploadForm)
        if (res.code === '000000') {
          this.createVisible = false
        this.progressVisiable=true
          this.init()
          this.getUnionListData()
        } else {
          this.$message({
            message: res.msg,
			showClose: true,
            type: 'warning'
          });
        }
        this.createLoading = false
      } catch (error) {
        this.$message({
          message: error,
		  showClose: true,
          type: 'warning'
        });
        this.createLoading = false
      }
    },
    init() {
      this.sqlForm.desc = ''
      this.sqlForm.jdbcName = ''
      this.sqlForm.jdbcPassword = ''
      this.sqlForm.jdbcUrl = ''
      this.sqlForm.port = ''
      this.uploadFile = []
      this.time = ''
      this.uploadForm = new FormData();
    },
    async getDataSourceDetailData(id) {
      this.detailLoading = true
      const res = await getDataSourceDetail({
        businessId: id
      })
      this.detail = res.data
      this.detailLoading = false
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./kbm.scss";

.structuring-view {
  width: 100%;
  height: 100%;
  display: flex;
  // flex-direction: column;
  overflow: auto;


  &::-webkit-scrollbar {
    width: 5px;
    height: 7px;
  }

  &::-webkit-scrollbar-thumb {
    border-radius: 5px;
    box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
    background: #535353;
  }

  &::-webkit-scrollbar-track {
    border-radius: 5px;
    background: #fff;
    cursor: pointer;
  }

  .form-box {
    .el-form-item {
      margin-bottom: 0px;
    }
  }

  .option-box {
    display: flex;
    margin: 10px 0;
    justify-content: space-between;
  }

  .table-box {
    flex-grow: 1;
    .my-table {
      .file-wrapper {
        display: flex;
        align-items: center;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #494E57;
        line-height: 20px;
        img {
          width: 24px;
          height: 24px;
          margin-right: 8px;
        }
      }
      .el-switch {
        height: 16px;
        ::v-deep .el-switch__core {
          height: 16px;
          &:after {
            top: 2px;
            width: 10px;
            height: 10px;
          }
        }
        &.switch-on {
          ::v-deep .el-switch__core {
            &:after {
              margin-left: -10px;
            }
          }
        }
      }
    }
    .file-type {
      display: flex;
      align-items: center;

      span {
        display: inline-block;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        margin-right: 8px;
      }
    }
    .clickable {
      cursor: pointer;
      color: #1c50fd;
    }
  }
}

.task-dialog {
  .title {
    font-weight: 500;
    font-size: 16px;
    color: #383D47;
    line-height: 24px;
    margin-top: 20px;
  }

  .message {
    font-weight: 400;
    font-size: 14px;
    color: #768094;
    line-height: 18px;
    margin-top: 4px;
    margin-bottom: 8px;
  }

  .options {
    display: flex;
    justify-content: space-between;
    background: #F7F8FA;
    .option {
      width: 246px;
      height: 36px;
      background: #F7F8FA;
      border-radius: 2px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 16px;
      color: #494E57;
      line-height: 24px;
      cursor: pointer;

      img {
        width: 24px;
        height: 24px;
        margin-right: 4px;
      }
    }
    .option-active {
      background: #fff;
      box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
      color: #828894;
    }
  }

  .xlsx-con, .sql-con {
    margin-top: 16px;

  }
  .sql-con {
    .inline-form-item {
      display: flex;
      align-items: center;
      .el-form-item:first-child {
        width: 65%;
        margin-right: 16px;
      }
    }
  }
  ::v-deep .el-upload-dragger {
    height: 120px;
    background: #F9FAFC;
    border-radius: 2px;
    border: 1px dotted rgba(0,0,0,0.12);
    .icon-upload {
      width: 22px;
      height: 20px;
      margin: 16px 0 8px;
    }
    .el-icon-upload {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 20px;
      color: #494E57;
      line-height: 20px;
    }
    .el-upload__text {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
    }
    .el-upload__condition {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #B4BCCC;
      line-height: 16px;
    }
  } 
}

.details-dialog {
  ::v-deep .el-dialog {
    .el-dialog__body {
      padding-top: 0;
      padding-bottom: 8px;
    }
  }
  
  .item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;

    .title {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #828894;
      line-height: 20px;
    }

    .con {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
      .password-circle {
        display: inline-block;
        width: 10px;
        height: 10px;
        margin-right: 4px;
        border-radius: 50%;
        background: #494E57;
        
      }
      img {
        width: 16px;
        height: 16px;
        vertical-align: sub;
        cursor: pointer;
      }
    }
  }

  .item:last-child {
    margin-bottom: 0;
  }
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

.el-form-item {
  margin-bottom: 10px;
}
</style>