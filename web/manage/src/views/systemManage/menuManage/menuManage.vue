<template>
  <div class="outer">
    <div class="inner">
      <div style="display:flex;justify-content:space-between;margin-bottom:20px;">
        <div style="display:flex;justify-content:flex-start;">
          <div style="margin-right:24px;">
            <span style="margin-right:20px;">{{$t('status')}}</span>
            <el-select style="width:148px;" v-model="status" :placeholder="$t('selectPlaceholder')" clearable>
              <el-option :label="$t('settingsEnabled')" value="1"></el-option>
              <el-option :label="$t('settingsDisabled')" value="0"></el-option>
            </el-select>
          </div>
          <div style="margin-right:24px;">
            <span style="margin-right:20px;">{{$t('settingsType')}}</span>
            <el-select v-model="menuType" :placeholder="$t('selectType')" style="width:148px;" clearable>
              <el-option v-for="item in menuTypeList" :key="item.id" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </div>
          <div style="margin-right:24px;">
            <span style="margin-right:20px;">{{$t('creationTime')}}</span>
            <el-date-picker v-model="dateRange" clearable style="width:252px;" type="daterange" :range-separator="$t('to')" :start-placeholder="$t('startDate')" value-format="yyyy-MM-dd"
                            :end-placeholder="$t('endDate')"></el-date-picker>
          </div>
          <div style="margin-right:8px;">
            <el-input v-model="menuName" clearable :placeholder="$t('inputMenuName')" prefix-icon="el-icon-search" style="width:200px;"></el-input>
          </div>
          <el-button type="primary" style="margin-right:8px;height:40px;" @click="search">{{ $t('search') }}</el-button>
          <el-button @click="resetSearch" style="height:40px;">{{ $t('reset') }}</el-button>
        </div>
        <el-button type="primary" style="float:right;height:40px;" @click="addMenuDialog"><i class="el-icon-circle-plus" style="margin-right:8px;"></i>{{$t('addMenu')}}</el-button>
      </div>
      <div style="margin-top:16px;">
        <el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" height="700" class="table">
          <el-table-column type="index" width="60" :label="$t('sequenceNumber')"></el-table-column>
          <el-table-column prop="menuName" :label="$t('menuName')"></el-table-column>
          <el-table-column prop="menuCode" :label="$t('menuCode')"></el-table-column>
          <el-table-column prop="menuUrl" :label="$t('menuRoute')"></el-table-column>
          <el-table-column :label="$t('settingsType')" width="120" prop="menuType" :formatter="formatters">
<!--            <template slot-scope="scope">-->
<!--              <span style="color:#383D47;font-size:16px;margin-left:5px;">{{ scope.row.menuType===1?'菜单':'功能' }}</span>-->
<!--            </template>-->
          </el-table-column>
          <el-table-column :label="$t('isEnabled')" width="120">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="changeStatus(scope.row)"></el-switch>
              <span style="color:#383D47;font-size:16px;margin-left:5px;">{{ scope.row.status=='1'?$t('settingsEnabled'):$t('settingsDisabled') }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('isHidden')" width="120">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.isHidden" :active-value="0" :inactive-value="1" @change="changeHidden(scope.row)"></el-switch>
              <span style="color:#383D47;font-size:16px;margin-left:5px;">{{ scope.row.isHidden===1?$t('hidden'):$t('display') }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" :label="$t('creationTime')" width="180"></el-table-column>
          <el-table-column :label="$t('action')" width="120">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="editMenu(scope.row)">{{$t('edit')}}</el-button>
              <el-button type="text" size="small" @click="deleteData(scope.row)">{{$t('delete')}}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div style="text-align:right;margin-top:20px;">
        <el-pagination background layout="total, prev, pager, next, sizes, jumper"
                       popper-class="slectStyle"
                       :current-page="currentPage"
                       :total="total"
                       :page-size="pageSize"
                       @current-change="handleCurrentChange"
                       @size-change="handleSizeChange"
                       :page-sizes="[10, 20, 30, 40]"></el-pagination>
      </div>
      <el-dialog :title="row? $t('editMenu') : $t('newMenu')" :visible.sync="dialogVisible" width="600px" class="dialog" :close-on-click-modal="false"
        :close-on-press-escape="false">
        <el-form :model="menuForm" :rules="roles" ref="menuForm" class="menuForm" label-position="top">
          <el-form-item :label="$t('menuName')" prop="menuName">
            <el-input v-model="menuForm.menuName"></el-input>
          </el-form-item>
          <el-form-item :label="$t('menuCode')" prop="menuCode">
            <el-input v-model="menuForm.menuCode"></el-input>
          </el-form-item>
          <el-form-item :label="$t('menuRoute')" prop="menuUrl">
            <el-input v-model="menuForm.menuUrl"></el-input>
          </el-form-item>
          <el-form-item :label="$t('menuIcon')" prop="menuIcon">
            <el-input v-model="menuIconParam.filePath"></el-input>
            <el-upload :action="actionUrl" :data=menuIconParam :file-list="menuIconFile" :limit="1" class="identityUpload" list-type="picture-card" :on-remove="menuIconRemove" :on-success="menuIconSuccess">
              <div>
                <img src="@/assets/images/add-line.svg" style="width:15px;height:15px;vertical-align:middle;">
                <span style="font-size: 16px;color: #B4BCCC;vertical-align:middle;">{{$t('uploadImage')}}</span>
              </div>
            </el-upload>
          </el-form-item>
          <el-form-item :label="$t('activateIcon')" prop="activateIcon">
            <el-upload :action="actionUrl" :data="{ filePath: 'agent_source' }" :file-list="activateIconFile" :limit="1" class="identityUpload" list-type="picture-card" :on-remove="activateIconRemove" :on-success="activateIconSuccess">
              <div>
                <img src="@/assets/images/add-line.svg" style="width:15px;height:15px;vertical-align:middle;">
                <span style="font-size: 16px;color: #B4BCCC;vertical-align:middle;">{{$t('uploadImage')}}</span>
              </div>
            </el-upload>
          </el-form-item>
          <el-form-item :label="$t('parentMenu')" prop="pid">
            <el-tree :data="parentMenuTree" :show-checkbox="true" :check-strictly="true" :props="defaultProps" ref="menuTree" @check="handleNodeClick" :highlight-current="true"  :default-checked-keys="defaultCheckedKeys" node-key="menuId"></el-tree>
          </el-form-item>
          <el-form-item :label="$t('isHidden')" prop="isHidden">
            <el-switch v-model="menuForm.isHidden" :active-value="1" :active-text="$t('hidden')" :inactive-text="$t('display')" :inactive-value="0"></el-switch>
          </el-form-item>
          <el-form-item :label="$t('settingsType')" prop="menuType">
            <el-select v-model="menuForm.menuType" :placeholder="$t('selectType')" style="width: 100%">
              <el-option v-for="item in menuTypeList" :key="item.id" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('clientLabel')" prop="clientType">
            <el-select v-model="menuForm.clientType" :placeholder="$t('pleaseSelectClientLabel')" clearable style="width: 100%">
              <el-option v-for="item in clientTypeList" :key="item.id" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('sortOrder')" prop="orderNo">
            <el-input-number v-model="menuForm.orderNo" :min="0" :max="999" :step="1" style="width: 100%;"></el-input-number>
          </el-form-item>
          <el-form-item :label="$t('menuStatus')" prop="status">
            <el-switch v-model="menuForm.status" :active-value="1" :active-text="$t('enable')" :inactive-text="$t('disable')" :inactive-value="0"></el-switch>
          </el-form-item>
          <el-form-item :label="$t('remarks')" prop="remark">
            <el-input v-model="menuForm.remark" type="textarea"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
                    <el-button type="primary" @click="submitAddUser">{{ $t('confirm') }}</el-button>
                    <el-button @click="cancelSubmit">{{ $t('cancel') }}</el-button>
                </span>
      </el-dialog>
      <el-dialog :title="$t('bindUser')" :visible.sync="bindDialogVisible" width="720px" class="dialog" :close-on-click-modal="false"
        :close-on-press-escape="false">
        <div style="display:flex;justify-content:flex-start;margin-bottom:20px;">
          <div style="margin-right:8px;">
            <el-input v-model="names" clearable :placeholder="$t('inputName')" prefix-icon="el-icon-search" style="width:334px;"></el-input>
          </div>
          <el-button type="primary" style="margin-right:8px;height:40px;" @click="searchs">{{ $t('search') }}</el-button>
        </div>
        <el-table ref="multipleTable" @selection-change="handleSelectionChange" :data="tableDatas" tooltip-effect="dark" style="width: 100%" class="table">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column type="index" width="60" :label="$t('sequenceNumber')"></el-table-column>
          <el-table-column prop="username" :label="$t('userName')"></el-table-column>
          <el-table-column prop="phone" :label="$t('phoneNumber')"></el-table-column>
          <el-table-column prop="deptName" :label="$t('department')"></el-table-column>
          <el-table-column :label="$t('userType')">
            <template slot-scope="scope">
              <span v-if="scope.row.powerType==2">{{$t('regularUser')}}</span>
              <span v-if="scope.row.powerType==1">{{$t('adminUser')}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" :label="$t('creationTime')" width="180"></el-table-column>
        </el-table>
        <!-- <div style="text-align:right;margin-top:20px;">
            <el-pagination background layout="total, prev, pager, next, sizes, jumper"
                popper-class="slectStyle"
                :current-page="currentPages"
                :total="totals"
                :page-size="pageSizes"
                @current-change="handleCurrentChanges"
                @size-change="handleSizeChanges"
                :page-sizes="[5, 10, 15, 20]"></el-pagination>
        </div> -->
        <span slot="footer" class="dialog-footer">
                    <el-button type="primary" @click="bindUserSubmit">{{ $t('confirm') }}</el-button>
                    <el-button @click="cancelBind">{{ $t('cancel') }}</el-button>
                </span>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import {
  addTenant,
  tenantList,
  deleteTenant,
  updateTenantStatus,
  getmanageUser,
  updateTenantUser,
  tenantByUser,
  getMenuList, deptTree, addMenu, deleteMenu, updateMenuStatus, updateHidden, getAllMenuTree
} from '@/api/app'
import {nextTick} from "vue";
export default {
  data() {
    return {
      tableCheckData:[],
      currentPages:1,
      totals:0,
      pageSizes:5,
      bindDialogVisible:false,
      names:'',
      tableDatas:[],
      row:'',
      status:'',
      menuType:'',
      menuName:'',
      dateRange:[],
      userName:'',
      currentPage:1,
      total:0,
      pageSize:10,
      roles: {
        menuName: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
        menuCode: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
        isHidden: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
        menuType: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
        status: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
      },
      menuForm: {
        tenantName:'',
        contacts:'',
        contactsPhone:'',
        contactsEmail:'',
        remark:'',
        userNumLimit:'',
      },
      tableData: [],
      dialogVisible: false,
      actionUrl: `${ process.env.VUE_APP_API_NEW }/wos/file/upload`,
      menuIconFile: [],
      menuIconParam: {
        filePath:'agent_source'
      },
      activateIconFile: [],
      parentMenuTree: {},
      defaultProps: {
        children: 'children',
        label: 'menuName'
      },
      menuTypeList:[
        {
          id:'1',
          label:this.$t('menu'),
          value: 1
        },
          {
            id:'2',
            label:this.$t('function'),
            value: 2
          },
          {
            id:'3',
            label:this.$t('tab'),
            value: 3
          }
      ],
      clientTypeList:[
        {
          id:'1',
          label:'PC',
          value: 'PC'
        },
          {
            id:'2',
            label:'H5',
            value: 'H5'
          }
      ],
      defaultExpandedKeys: [],
      defaultCheckedKeys: [],
    }
  },
  created(){
    this.getMenuList();
    this.getMenuTrees();
  },
  methods: {
    updateHidden,
    handleSelectionChange(val) {
      this.tableCheckData = val;
    },
    searchs(){
      this.currentPages = 1;
      this.getMenuLists();
    },
    bindUser(row){
      this.row = row;
      this.tableCheckData = [];
      this.getMenuLists(1);
      this.names = '';
      this.currentPages = 1;
      this.pageSizes = 5;
      this.bindDialogVisible = true;
    },
    getTenantUser(){
      tenantByUser({
        tenantId:this.row.tenantId
      }).then(res=>{
        if(res.code=='000000'){
          this.tableCheckData = res.data||[];
          let self = this;
          this.$nextTick(()=>{
            let arr = [];
            res.data.forEach(element => {
              let itemIndex = self.tableDatas.findIndex(item=>item.id==element.id);
              arr.push(itemIndex);
            });
            arr.forEach(item=>{
              self.$refs.multipleTable.toggleRowSelection(self.tableDatas[item],true);
            })
          })
        }else{
          this.tableCheckData = [];
        }
      })
    },
    getMenuLists(type) {
      getmanageUser({
        userName:this.names,
      }).then(res=>{
        if(res.code=='000000'){
          // this.totals = res.data?.totalRow||0;
          this.tableDatas = res.data||[];
          if(type==1){
            this.getTenantUser();
          }
        }else{
          // this.totals = 0;
          this.tableDatas = [];
        }
      })
    },
    bindUserSubmit(){
      updateTenantUser({
        tenantId: this.row.tenantId,
        userIdList:this.tableCheckData.map(item=>item.id)
      }).then(res=>{
        if(res.code=='000000'){
          this.$message({
            type: 'success',
            message: this.$t("successed") || 'success'
          })
          this.getMenuList();
          this.bindDialogVisible = false;
        }else{
          this.$message({
            type: 'error',
            message: 'error'
          })
        }
      })
    },
    cancelBind(){
      this.bindDialogVisible = false;

    },
    changeStatus(row){
      updateMenuStatus({
        menuId:row.menuId,
        status:row.status,
      }).then(res=>{
        if(res.code=='000000'){
          this.getMenuList();
          this.$message({
            type: 'success',
            message: this.$t("successed") || 'success'
          });
        }else{
          this.getMenuList();
          this.$message({
            type: 'error',
            message: 'error'
          })
        }
      })
    },
    changeHidden(row){
      updateHidden({
        menuId:row.menuId,
        isHidden:row.isHidden,
      }).then(res=>{
        if(res.code=='000000'){
          this.getMenuList();
          this.$message({
            type: 'success',
            message: this.$t("successed") || 'success'
          });
        }else{
          this.getMenuList();
          this.$message({
            type: 'error',
            message: 'error'
          })
        }
      })
    },
    deleteData(row){
      this.$confirm(this.$t('confirmDeleteMenu'), this.$t('tips'), {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        type: 'warning'
      }).then(() => {
        deleteMenu([row.menuId]).then(res=>{
          if(res.code=='000000'){
            this.getMenuList();
            this.$message({
              type: 'success',
              message: this.$t("successed") || 'success'
            });
          }else{
            this.$message({
              type: 'error',
              message: 'error'
            })
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: this.$t('cancelledOperation')
        });
      });
    },
    addMenuDialog(){
      this.row = '';
      this.menuForm = {
        isHidden: 0,
        status: 1,
      };
      this.dialogVisible = true;
      nextTick(() => {
        this.$refs.menuTree.setCheckedNodes([]);
        this.$refs.menuTree.setCheckedKeys([]);
      });
    },
    editMenu(row){
      this.row = row;
      this.menuForm = JSON.parse(JSON.stringify(row))
      if (row.activateIcon) {
        this.activateIconFile=[{
          url: row.activateIcon,
          name: row.activateIcon.substring(row.activateIcon.lastIndexOf('/')+1)
        }]
      }
      if (row.menuIcon) {
        this.menuIconFile=[{
          url: row.menuIcon,
          name: row.menuIcon.substring(row.menuIcon.lastIndexOf('/')+1)
        }]
      }
      if (row.pid) {
        nextTick(()=>{
          this.$refs.menuTree.setCheckedNodes([]);
          this.defaultExpandedKeys = [row.pid];
          this.defaultCheckedKeys = [row.pid];
        })

      }
      this.dialogVisible = true;
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getMenuList();
    },
    handleCurrentChange(val){
      this.currentPage = val;
      this.getMenuList();
    },
    handleSizeChanges(val) {
      this.pageSizes = val;
      this.getMenuLists();
    },
    handleCurrentChanges(val){
      this.currentPages = val;
      this.getMenuLists();
    },
    resetSearch(){
      this.currentPage = 1;
      this.userName = '',
      this.status = '';
      this.menuName = '';
      this.dateRange = [];
      this.getMenuList();
    },
    search(){
      this.currentPage = 1;
      this.getMenuList();
    },
    getMenuTrees(){
      getAllMenuTree({
        pageNo:1,
        pageSize:9999
      }).then(res=>{
        if(res.code=='000000'){
          this.parentMenuTree = res.data||[];
        }else{
          this.parentMenuTree = [];
        }
      })
    },
    getMenuList() {
      getMenuList({
        pageNo: this.currentPage,
        pageSize: this.pageSize,
        menuType: this.menuType,
        menuName: this.menuName,
        status: this.status,
        startDate: this.dateRange ? this.dateRange[0] : '',
        endDate: this.dateRange ? this.dateRange[1] : '',
      }).then(res => {
        if (res.code == '000000') {
          this.total = res.data?.totalRow || 0;
          this.tableData = res.data?.records || [];
        } else {
          this.total = 0;
          this.tableData = [];
        }
      })
    },
    submitAddUser() {
      this.$refs['menuForm'].validate((valid) => {
        if (valid) {
          addMenu({
            id: this.row ? this.row.id : '',
            tenantId: this.row ? this.row.menuId : '',
            ...this.menuForm,
          }).then(res => {
            if (res.code == '000000') {
              this.dialogVisible = false;
              this.getMenuList();
              this.$message({
                type: 'success',
                message: this.$t('successed')
              })
            } else {
              this.$message({
                type: 'error',
                message: res.msg
              })
            }
          })
        } else {
          return false;
        }
      });
    },
    cancelSubmit() {
        if(!this.row){
            this.$refs['menuForm'].resetFields();
        }
      this.dialogVisible = false;
    },
    menuIconRemove(file, fileList) {
      this.menuForm.menuIcon = '';
      this.menuIconFile = [];
    },
    menuIconSuccess(response, file, fileList) {
      if(response.code='000000'){
        this.menuForm.menuIcon = response.data&&response.data[0]?response.data[0].url:'';
        this.menuIconFile = [{
          name:'identityIcon',
          url:this.menuForm.menuIcon
        }]
      }else{
        this.menuForm.menuIcon = '';
        this.menuIconFile = [];
      }
    },
    activateIconRemove(file, fileList) {
      this.menuForm.activateIcon = '';
      this.activateIconFile = [];
    },
    activateIconSuccess(response, file, fileList) {
      if(response.code='000000'){
        this.menuForm.activateIcon = response.data&&response.data[0]?response.data[0].url:'';
        this.activateIconFile = [{
          name:'identityIcon',
          url:this.menuForm.activateIcon
        }]
      }else{
        this.menuForm.activateIcon = '';
        this.activateIconFile = [];
      }
    },
    handleNodeClick(data){
      if (data) {
        if (data.menuType === 2) {
          this.$message({
            type: 'warning',
            message: this.$t('selectOnlyMenu')
          })
          this.$refs.menuTree.setCheckedNodes([]);
          this.$refs.menuTree.setCheckedKeys([]);
          return
        }
        this.$refs.menuTree.setCheckedNodes([]);
        this.$refs.menuTree.setCheckedKeys([]);
        this.$refs.menuTree.setCheckedNodes([data]);
        this.menuForm.pid=data.menuId
      }
    },
    formatters(row, column){
      if (this.menuTypeList && this.menuTypeList.length) {
        let menuType = this.menuTypeList.filter(item => item.value === row.menuType);
        if (menuType && menuType.length) {
          return menuType[0].label;
        }
      }
    }
  }
}
</script>
<style lang="scss">
.slectStyle {
  .el-select-dropdown__item.selected {
    color: #1C50FD;
  }
}
</style>
<style lang="scss" scoped>
.outer {
  width: 100%;
  height: 100%;
  padding: 32px 24px;
  font-family: MiSans, MiSans;

  .inner {
    width: 100%;
    height: 100%;
    background: #fff;
    border-radius: 4px;
    border: 1px solid #E1E4EB;
    padding: 24px;

    ::v-deep .el-input__inner:focus,
    ::v-deep .el-date-editor:focus,
    ::v-deep .el-date-editor.is-active {
      border-color: #1C50FD !important;
    }
  }
}

.el-button {
  &.el-button-default {
    border-radius: 4px;
    color: #383D47;
    border: 1px solid #C4C6CC;
  }

  &.el-button--primary {
    background-color: #1C50FD;
    color: #fff;
    border-color: #1C50FD;
  }
}

::v-deep .el-input__inner {
  border-radius: 8px !important;
}

::v-deep .el-date-editor .el-range-separator {
  width: 8%;
}

::v-deep .el-checkbox .el-checkbox__inner {
  width: 15px;
  height: 15px;
  border-color: #B4BCCC;
}

::v-deep .el-checkbox__label {
  font-size: 16px;
  color: #383D47;
  font-weight: 400;
}

::v-deep .table {
  &::before{
    height: 0;
  }
  .is-checked .el-switch__core {
    background: linear-gradient(270deg, #8E65FF 0%, #1C50FD 100%);
  }

  .el-switch .el-switch__core {
    border-radius: 12px;
    border: none;

    &:after {
      top: 2px;
    }
  }

  .has-gutter {
    th {
      font-size: 14px;
      color: #828894;
      background: #F2F5FA;
    }
  }

  tr {
    color: #383D47;
    font-size: 16px;
    font-weight: 400;

    .el-checkbox__input.is-checked .el-checkbox__inner,
    .el-checkbox__input.is-indeterminate .el-checkbox__inner {
      background: #1C50FD;
      border-color: #1C50FD;
    }

  }

  .el-button--text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #1C50FD;
  }
}

::v-deep .el-pager li {
  border: 1px solid #DDDFE8;
  color: #272A31;
  background: #fff;

  .el-select-dropdown__item.selected {
    color: #1C50FD;
  }

  &:not(.disabled).active {
    background-color: #fff !important;
    border: 1px solid #1C50FD;
    color: #272A31 !important;
  }
}

::v-deep .btn-prev,
::v-deep .btn-next {
  border: 1px solid #DDDFE8;
  color: #272A31;
  background: #fff;
}

::v-deep .dialog {
  .el-dialog__header {
    padding: 20px 32px;
    background: linear-gradient(180deg, rgba(43, 88, 213, 0.1) 0%, rgba(43, 88, 213, 0) 100%);

    .el-dialog__title {
      color: #383D47;
      font-size: 20px;
      font-weight: 500;
    }
  }

  .el-dialog__body {
    padding: 0 32px;

    .el-input__inner {
      border-radius: 4px;
    }

    .el-input__inner:focus {
      border-color: #1C50FD;
    }

    .el-form-item__label {
      color: #383D47;
      font-size: 16px;
      font-weight: 500;
    }
  }

  .el-dialog__footer {
    text-align: left;

    .el-button {
      border-radius: 4px;
    }

    .el-button--primary {
      background: #1C50FD;
      border-color: #1C50FD;
    }

    .el-button--default {
      border-color: #C4C6CC;
      color: #383D47;
      font-size: 16px;
    }
  }

  .menuForm {
    .el-form-item {
      margin-bottom: 20px;

      .el-radio__input.is-checked .el-radio__inner {
        border-color: #1C50FD;
        background: #1C50FD;
      }

      .el-radio__input.is-checked + .el-radio__label {
        color: #1C50FD;
      }
    }

    .el-form-item__label {
      line-height: 20px;
      padding-bottom: 0;
      margin-bottom: 8px;
    }
  }
}
</style>
