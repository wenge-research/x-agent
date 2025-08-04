<template>
    <div class="outer">
        <div class="inner">
            <div style="display:flex;justify-content:space-between;margin-bottom:20px;">
                <div style="display:flex;justify-content:flex-start;">
                    <div style="margin-right:24px;">
                        <span style="margin-right:20px;">{{$t('status')}}</span>
                        <el-select style="width:148px;" v-model="status" :placeholder="$t('selectPlaceholder')" clearable>
                            <el-option :label="$t('enable')" value="1"></el-option>
                            <el-option :label="$t('disable')"value="0"></el-option>
                        </el-select>
                    </div>
                    <div style="margin-right:24px;">
                        <span style="margin-right:20px;">{{$t('creationTime')}}</span>
                        <el-date-picker v-model="dateRange" clearable style="width:252px;" type="daterange" :range-separator="$t('to')" :start-placeholder="$t('startDate')" value-format="yyyy-MM-dd"
                            :end-placeholder="$t('endDate')"></el-date-picker>
                    </div>
                    <div style="margin-right:8px;">
                        <el-input v-model="userName" clearable :placeholder="$t('pleaseEnterRoleName')" prefix-icon="el-icon-search" style="width:334px;"></el-input>
                    </div>
                    <el-button type="primary" style="margin-right:8px;height:40px;" @click="search">{{ $t('search') }}</el-button>
                    <el-button @click="resetSearch" style="height:40px;">{{ $t('reset') }}</el-button>
                </div>
                <el-button type="primary" style="float:right;height:40px;" @click="addRoleDialog"><i class="el-icon-circle-plus" style="margin-right:8px;"></i>{{$t('addRole')}}</el-button>
            </div>
            <div style="margin-top:16px;">
                <el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" height="700" class="table">
                    <el-table-column type="index" width="60" :label="$t('sequenceNumber')"></el-table-column>
                    <el-table-column prop="roleName" :label="$t('roleName')"></el-table-column>
                    <el-table-column prop="roleCode" :label="$t('roleCode')"></el-table-column>
                    <el-table-column prop="createTime" :label="$t('creationTime')" width="180"></el-table-column>
                    <el-table-column prop="remark" :label="$t('remarks')" width="180"></el-table-column>
                    <el-table-column :label="$t('userStatus')" width="120">
                        <template slot-scope="scope">
                            <el-switch v-model="scope.row.status" active-value="1" inactive-value="0" @change="changeStatus(scope.row)"></el-switch>
                            <span style="color:#383D47;font-size:16px;margin-left:5px;">{{ scope.row.status==1? $t('available') :$t('disabled') }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column :label="$t('action')" width="180">
                        <template slot-scope="scope">
                            <el-button type="text" size="small" @click="editUser(scope.row)">{{$t('edit')}}</el-button>
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
            <el-dialog :title="row? $t('editRole') : $t('addNewRole')" :visible.sync="dialogVisible" width="720px" class="dialog" :close-on-click-modal="false"
        :close-on-press-escape="false">
                <el-form :model="roleForm" :rules="roles" ref="roleForm" class="roleForm" label-position="top">
                    <div style="display:flex;justify-content:space-between;">
                        <div style="width:calc(50% - 10px);margin-right:20px;">
                            <el-form-item :label="$t('roleName')"prop="roleName">
                                <el-input v-model="roleForm.roleName"></el-input>
                            </el-form-item>
                            <el-form-item :label="$t('roleCode')"prop="roleCode">
                                <el-input v-model="roleForm.roleCode"></el-input>
                            </el-form-item>
                            <el-form-item :label="$t('remarks')" prop="remark">
                                <el-input v-model="roleForm.remark" type="textarea"></el-input>
                            </el-form-item>
                        </div>
                        <div style="width:calc(50% - 10px); width: 272px;height: 464px;border-radius: 4px;border: 1px solid #E1E4EB;">
                            <div style="display:flex;justify-content:space-between;align-items:center;padding:0 16px;height: 44px;background: linear-gradient( 0deg, rgba(28,80,253,0) 0%, rgba(142,101,255,0.1) 100%);border-radius: 4px 4px 0px 0px;">
                                <span style="color:#383D47;font-size:16px;font-weight: 500;">{{$t('roleFunctionList')}}</span>
                                <div style="cursor:pointer;">
                                    <img src="@/assets/images/eraser-line.svg" style="width:18px;margin-right:3px;vertical-align: middle;">
                                    <span style="font-weight: 400;font-size: 16px;color: #1C50FD;vertical-align:middle;" @click="resetChecked">{{$t('clear')}}</span>
                                </div>
                            </div>
                            <div class="checkboxOuter">
                                <el-tree :data="treeData" :props="defaultProps" show-checkbox ref="tree" node-key="menuId"></el-tree>
                            </div>
                        </div>
                    </div>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button type="primary" @click="submitAddUser">{{ $t('confirm') }}</el-button>
                    <el-button @click="cancelSubmit">{{ $t('cancel') }}</el-button>
                </span>
            </el-dialog>
        </div>
    </div>
</template>
<script>
import { addRole, roleList, allmenulist, menulists, deleteRole, updateRoleStatus  } from '@/api/app'
export default {
    data() {
        return {
            row:'',
            treeData: [],
            defaultProps: {
                children: 'children',
                label: 'menuName'
            },
            status:'',
            dateRange:[],
            userName:'',
            currentPage:1,
            total:0,
            pageSize:10,
            roles: {
                roleName: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
                roleCode: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
            },
            roleForm: {
                roleName:'',
                roleCode:'',
                remark:'',
                menuIdList:[],
            },
            tableData: [],
            dialogVisible: false,
        }
    },
    created(){
        this.getUserList();
    },
    methods: {
        getAllMenuList(){
            allmenulist({}).then(res=>{
                if(res.code=='000000'){
                    this.treeData = res.data;
                    if(this.row){
                        this.getMenuList(this.row);
                    }
                }else{
                    this.treeData = [];
                }
            })
        },
        getMenuList(row){
            menulists({
                roleId:row.roleId
            }).then(res=>{
                if(res.code=='000000'){
                    (res.data||[]).forEach(item => {
                        var node = this.$refs.tree.getNode(item);
                        if(node.isLeaf){
                            this.$refs.tree.setChecked(node, true);
                        }
                    });
                }
            })
        },
        resetChecked() {
            this.$refs.tree.setCheckedKeys([]);
        },
        changeStatus(row){
            updateRoleStatus({
                roleId:row.roleId,
                status:row.status,
            }).then(res=>{
                if(res.code=='000000'){
                    this.getUserList();
                    this.$message({
                        type: 'success',
                        message: this.$t("successed") || 'success'
                    });
                }else{
                    this.getUserList();
                    this.$message({
                        type: 'error',
                        message: 'error'
                    })
                }
            })
        },
        deleteData(row){
            this.$confirm(this.$t('thisActionWillDeleteRoleConfirm'), this.$t('tips'), {
                confirmButtonText: this.$t('confirm'),
                cancelButtonText: this.$t('cancel'),
                type: 'warning'
            }).then(() => {
                deleteRole({
                    roleId:[row.roleId]
                }).then(res=>{
                    if(res.code=='000000'){
                        this.getUserList();
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
        addRoleDialog(){
            this.row = '';
            this.getAllMenuList();
            this.roleForm = {
                roleName:'',
                roleCode:'',
                remark:'',
                menuIdList:[],
            },
            this.dialogVisible = true;
        },
        editUser(row){
            this.row = row;
            this.getAllMenuList();
            this.roleForm = {
                roleName:row.roleName,
                roleCode:row.roleCode,
                remark:row.remark,
            },
            this.dialogVisible = true;
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.getUserList();
        },
        handleCurrentChange(val){
            this.currentPage = val;
            this.getUserList();
        },
        resetSearch(){
            this.currentPage = 1;
            this.userName = '',
            this.status = '';
            this.dateRange = [];
            this.getUserList();
        },
        search(){
            this.currentPage = 1;
            this.getUserList();
        },
        getUserList() {
            roleList({
                pageNo:this.currentPage,
                pageSize:this.pageSize,
                roleName:this.userName,
                status:this.status,
                startDate:this.dateRange?this.dateRange[0]:'',
                endDate:this.dateRange?this.dateRange[1]:'',
            }).then(res=>{
                if(res.code=='000000'){
                    this.total = res.data?.totalRow||0;
                    this.tableData = res.data?.records||[];
                }else{
                    this.total = 0;
                    this.tableData = [];
                }
            })
        },
        submitAddUser(){
            this.$refs['roleForm'].validate((valid) => {
                if (valid) {
                    let checkedKeys = this.$refs.tree.getCheckedKeys()?this.$refs.tree.getCheckedKeys():[];
                    let halfCheckedKeys = this.$refs.tree.getHalfCheckedKeys()?this.$refs.tree.getHalfCheckedKeys():[];
                    addRole({
                        id:this.row?this.row.id:'',
                        roleId:this.row?this.row.roleId:'',
                        roleName:this.roleForm.roleName,
                        roleCode:this.roleForm.roleCode,
                        remark:this.roleForm.remark,
                        menuIdList:[...checkedKeys,...halfCheckedKeys],
                    }).then(res=>{
                        if(res.code=='000000'){
                            this.dialogVisible = false;
                            this.getUserList();
                            this.$message({
                                type:'success',
                                message:this.$t('successed')
                            })
                        }else{
                            this.$message({
                                type:'error',
                                message:res.msg
                            })
                        }
                    })
                } else {
                    return false;
                }
            });
        },
        cancelSubmit(){
            this.$refs['roleForm'].resetFields();
            this.dialogVisible = false;
        },
    }
}
</script>
<style lang="scss">
/* 自定义整个滚动条 */
.checkboxOuter::-webkit-scrollbar {
    width: 8px;
    height: 121px;
    background: #fff;
}

/* 自定义滚动条轨道 */
.checkboxOuter::-webkit-scrollbar-track {
    background: #fff; /* 轨道颜色 */
}

/* 自定义滚动条的滑块（thumb） */
.checkboxOuter::-webkit-scrollbar-thumb {
    background: #E1E4EB; /* 滑块颜色 */
    border-radius: 6px;
}
.slectStyle{
    .el-select-dropdown__item.selected{
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
    .inner{
        width: 100%;
        height: 100%;
        background: #fff;
        border-radius: 4px;
        border: 1px solid #E1E4EB;
        padding: 24px;
        ::v-deep .el-input__inner:focus,
        ::v-deep .el-date-editor:focus,
        ::v-deep .el-date-editor.is-active{
            border-color: #1C50FD!important;
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
    .is-checked .el-switch__core{
        background: linear-gradient( 270deg, #8E65FF 0%, #1C50FD 100%);
    }
    .el-switch .el-switch__core{
        border-radius: 12px;
        border: none;
        &:after{
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
        .el-checkbox__input.is-indeterminate .el-checkbox__inner{
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
    .el-select-dropdown__item.selected{
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
::v-deep .dialog{
    .el-dialog__header{
        padding: 20px 32px;
        background: linear-gradient( 180deg, rgba(43,88,213,0.1) 0%, rgba(43,88,213,0) 100%);
        .el-dialog__title{
            color: #383D47;
            font-size: 20px;
            font-weight: 500;
        }
    }
    .el-dialog__body{
        padding: 0 32px;
        .el-input__inner{
            border-radius: 4px;
        }
        .el-input__inner:focus{
            border-color: #1C50FD;
        }
        .is-indeterminate .el-checkbox__inner,
        .is-checked .el-checkbox__inner{
            background-color: #1C50FD;
            border-color: #1C50FD;
        }
        .checkboxOuter{
            height: 410px;
            padding: 0 8px;
            overflow-y: scroll;
        }
        .el-form-item__label{
            color: #383D47;
            font-size: 16px;
            font-weight: 500;
        }
    }
    .el-dialog__footer{
        text-align: left;
        .el-button{
            border-radius: 4px;
        }
        .el-button--primary{
            background: #1C50FD;
            border-color: #1C50FD;
        }
        .el-button--default{
            border-color: #C4C6CC;
            color: #383D47;
            font-size: 16px;
        }
    }
    .roleForm {
        .el-form-item{
            margin-bottom: 20px;
            .el-radio__input.is-checked .el-radio__inner{
                border-color: #1C50FD;
                background: #1C50FD;
            }
            .el-radio__input.is-checked+.el-radio__label{
                color: #1C50FD;
            }
        }
        .el-form-item__label{
            line-height: 20px;
            padding-bottom: 0;
            margin-bottom: 8px;
        }
    }
}
</style>
