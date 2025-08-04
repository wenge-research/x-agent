<template>
    <div class="outer">
        <div class="inner">
            <div style="display:flex;justify-content:space-between;margin-bottom:20px;">
                <div style="display:flex;justify-content:flex-start;">
                    <div style="margin-right:24px;">
                        <span style="margin-right:20px;">{{ $t('status') }}</span>
                        <el-select style="width:148px;" v-model="status" :placeholder="$t('selectPlaceholder')"
                            clearable>
                            <el-option :label="$t('settingsEnabled')" value="1"></el-option>
                            <el-option :label="$t('settingsDisabled')" value="0"></el-option>
                        </el-select>
                    </div>
                    <div style="margin-right:24px;">
                        <span style="margin-right:20px;">{{ $t('creationTime') }}</span>
                        <el-date-picker v-model="dateRange" clearable style="width:252px;" type="daterange"
                            :range-separator="$t('to')" :start-placeholder="$t('startDate')" value-format="yyyy-MM-dd"
                            :end-placeholder="$t('endDate')"></el-date-picker>
                    </div>
                    <div style="margin-right:8px;">
                        <el-input v-model="userName" clearable :placeholder="$t('inputTenantName')"
                            prefix-icon="el-icon-search" style="width:334px;"></el-input>
                    </div>
                    <el-button type="primary" style="margin-right:8px;height:40px;" @click="search">{{ $t('search')
                        }}</el-button>
                    <el-button @click="resetSearch" style="height:40px;">{{ $t('reset') }}</el-button>
                </div>
                <el-button type="primary" style="float:right;height:40px;"
                    @click="addRoleDialog"><i class="el-icon-circle-plus" style="margin-right:8px;"></i>{{ $t('addTenant') }}</el-button>
            </div>
            <div style="margin-top:16px;">
                <el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" height="700" class="table">
                    <el-table-column type="index" width="60" :label="$t('sequenceNumber')"></el-table-column>
                    <el-table-column prop="tenantName" :label="$t('tenantName')"></el-table-column>
                    <el-table-column prop="contacts" :label="$t('contactPerson')"></el-table-column>
                    <el-table-column prop="contactsPhone" :label="$t('contactPhone')"></el-table-column>
                    <el-table-column prop="contactsEmail" :label="$t('contactEmail')"></el-table-column>
                    <el-table-column prop="createTime" :label="$t('creationTime')" width="180"></el-table-column>
                    <el-table-column :label="$t('isEnabled')" width="120">
                        <template slot-scope="scope">
                            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0"
                                @change="changeStatus(scope.row)"></el-switch>
                            <span style="color:#383D47;font-size:16px;margin-left:5px;">{{ scope.row.status == '1' ?
                                $t('enabled') : $t('disabled') }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column :label="$t('action')" width="200">
                        <template slot-scope="scope">
                            <el-button type="text" size="small" @click="editUser(scope.row)">{{ $t('edit') }}</el-button>
                            <el-button type="text" size="small"
                                @click="bindUser(scope.row)">{{ $t('bindUser') }}</el-button>
                            <el-button type="text" size="small"
                                @click="deleteData(scope.row)">{{ $t('delete') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <div style="text-align:right;margin-top:20px;">
                <el-pagination background layout="total, prev, pager, next, sizes, jumper" popper-class="slectStyle"
                    :current-page="currentPage" :total="total" :page-size="pageSize"
                    @current-change="handleCurrentChange" @size-change="handleSizeChange"
                    :page-sizes="[10, 20, 30, 40]"></el-pagination>
            </div>
            <el-dialog :title="row ? $t('editTenant') : $t('newTenant')" :visible.sync="dialogVisible" width="720px"
                class="dialog" :close-on-click-modal="false" :close-on-press-escape="false">
                <el-form :model="roleForm" :rules="roles" ref="roleForm" class="roleForm" label-position="top">
                    <div style="margin:22px 0;">
                        <img src="@/assets/images/contacts-fills.svg"
                            style="margin-right:5px;width:20px;vertical-align:middle;">
                        <span
                            style="vertical-align:middle;width: 80px;font-family: MiSans, MiSans;font-weight: 500;font-size: 20px;color: #383D47;">{{ $t('basicInfo') }}</span>
                    </div>
                    <el-form-item :label="$t('tenantName')" prop="tenantName">
                        <el-input v-model="roleForm.tenantName"></el-input>
                    </el-form-item>
                    <el-form-item :label="$t('contactPerson')" prop="contacts">
                        <el-input v-model="roleForm.contacts"></el-input>
                    </el-form-item>
                    <el-form-item :label="$t('contactPhone')" prop="contactsPhone">
                        <el-input v-model="roleForm.contactsPhone"></el-input>
                    </el-form-item>
                    <el-form-item :label="$t('contactEmail')" prop="contactsEmail">
                        <el-input v-model="roleForm.contactsEmail"></el-input>
                    </el-form-item>
                    <el-form-item :label="$t('remarks')" prop="remark">
                        <el-input v-model="roleForm.remark" type="textarea"></el-input>
                    </el-form-item>
                    <div style="margin:22px 0;">
                        <img src="@/assets/images/chat-voice-fill.svg"
                            style="margin-right:5px;width:20px;vertical-align:middle;">
                        <span
                            style="vertical-align:middle;width: 80px;font-family: MiSans, MiSans;font-weight: 500;font-size: 20px;color: #383D47;">{{ $t('resourceQuota') }}</span>
                    </div>
                    <el-form-item :label="$t('numberOfUsers')" prop="userNumLimit">
                        <el-input v-model="roleForm.userNumLimit"></el-input>
                    </el-form-item>
                    <el-form-item :label="$t('userDefaultPw')" prop="defaultPassword">
                        <el-input type="password" v-model="roleForm.defaultPassword" show-password clearable></el-input>
                    </el-form-item>
                    <el-form-item :label="$t('imageLogo')" prop="logoUrl">
                        <el-upload :action="actionUrl" :data="{ filePath: 'agent_source' }"
                            :class="{ hideBox: uploadBtnLogo }" :file-list="fileListLogo" :limit="1" class="logoUpload"
                            list-type="picture-card" :on-remove="handleLogoRemove" :on-success="handleLogoSuccess">
                            <div>
                                <img src="@/assets/images/add-line.svg"
                                    style="width: 15px; height: 15px; vertical-align: middle" />
                                <span
                                    style="font-size: 16px; color: #b4bccc; vertical-align: middle">{{ $t('uploadImage') }}</span>
                            </div>
                        </el-upload>
                    </el-form-item>
                    <el-form-item label="favicon图片" prop="favicon" style="margin-top:20px;">
                        <el-upload :action="actionUrl" :data="{ filePath: 'agent_source' }"
                            :class="{ hideBox: uploadfavLogo }" :file-list="favListLogo" :limit="1" class="logoUpload"
                            list-type="picture-card" :on-remove="handleFavRemove" :on-success="handleFavSuccess">
                            <div>
                                <img src="@/assets/images/add-line.svg"
                                    style="width: 15px; height: 15px; vertical-align: middle" />
                                <span
                                    style="font-size: 16px; color: #b4bccc; vertical-align: middle">{{ $t('uploadImage') }}</span>
                            </div>
                        </el-upload>
                    </el-form-item>
                    <el-form-item label="页面title" prop="title">
                        <el-input v-model="roleForm.title"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button type="primary" @click="submitAddUser">{{ $t('confirm') }}</el-button>
                    <el-button @click="cancelSubmit">{{ $t('cancel') }}</el-button>
                </span>
            </el-dialog>
            <el-dialog
              title="绑定用户"
              :visible.sync="bindDialogVisible"
              top="10vh"
              width="720px"
              class="grantDialog"
              destroy-on-close
              :close-on-click-modal="false"
              :close-on-press-escape="false"
            >
                <GrantData
                :tenantId="row.tenantId"
                v-if="bindDialogVisible"
                @cancelGrant="cancelGrant"
              ></GrantData>
            </el-dialog>
            
        </div>
    </div>
</template>
<script>
import { addTenant, tenantList, deleteTenant, updateTenantStatus, getmanageUser, updateTenantUser, tenantByUser, findTenant } from '@/api/app'
import GrantData from "./GrantData.vue";

export default {
    components: {
      GrantData
    },
    data() {
        return {
            tableCheckData: [],
            currentPages: 1,
            totals: 0,
            pageSizes: 5,
            bindDialogVisible: false,
            names: '',
            tableDatas: [],
            row: '',
            status: '',
            dateRange: [],
            userName: '',
            currentPage: 1,
            total: 0,
            pageSize: 10,

            roles: {
                tenantName: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
                contacts: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
                contactsPhone: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
                contactsEmail: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
                userNumLimit: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
                defaultPassword: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
                logoUrl: [{ required: true, validator: this.checkLogoUrl, trigger: 'blur' }],
            },
            roleForm: {
                tenantName: '',
                contacts: '',
                contactsPhone: '',
                contactsEmail: '',
                remark: '',
                userNumLimit: 20,
                defaultPassword: 'User#%2024',
                title: '',
                favicon: '',
            },
            tableData: [],
            dialogVisible: false,
            actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
            fileListLogo: [],
            favListLogo: [],
            uploadBtnLogo: false,
            uploadfavLogo: false,
        }
    },
    created() {
        this.getUserList();
    },
    methods: {
        cancelGrant() {
          this.bindDialogVisible = false;
        },
        checkLogoUrl(rule, value, callback) {
            if (!this.roleForm.logoUrl) {
                return callback(new Error(this.$t('cannotBeEmpty')));
            } else {
                callback();
            }
        },
        handleSelectionChange(val) {
            this.tableCheckData = val;
        },
        searchs() {
            this.currentPages = 1;
            this.getUserLists();
        },
        bindUser(row) {
            this.row = row;
            this.bindDialogVisible = true;
        },
        getTenantUser() {
            tenantByUser({
                tenantId: this.row.tenantId
            }).then(res => {
                if (res.code == '000000') {
                    this.tableCheckData = res.data || [];
                    let self = this;
                    this.$nextTick(() => {
                        let arr = [];
                        res.data.forEach(element => {
                            let itemIndex = self.tableDatas.findIndex(item => item.id == element.id);
                            arr.push(itemIndex);
                        });
                        arr.forEach(item => {
                            self.$refs.multipleTable.toggleRowSelection(self.tableDatas[item], true);
                        })
                    })
                } else {
                    this.tableCheckData = [];
                }
            })
        },
        getUserLists(type) {
            getmanageUser({
                userName: this.names,
            }).then(res => {
                if (res.code == '000000') {
                    // this.totals = res.data?.totalRow||0;
                    this.tableDatas = res.data || [];
                    if (type == 1) {
                        this.getTenantUser();
                    }
                } else {
                    // this.totals = 0;
                    this.tableDatas = [];
                }
            })
        },
        bindUserSubmit() {
            updateTenantUser({
                tenantId: this.row.tenantId,
                userIdList: this.tableCheckData.map(item => item.id)
            }).then(res => {
                if (res.code == '000000') {
                    this.$message({
                        type: 'success',
                        message: this.$t("successed") || 'success'
                    })
                    this.getUserList();
                    this.bindDialogVisible = false;
                } else {
                    this.$message({
                        type: 'error',
                        message: 'error'
                    })
                }
            })
        },
        cancelBind() {
            this.bindDialogVisible = false;

        },
        changeStatus(row) {
            updateTenantStatus({
                tenantId: row.tenantId,
                status: row.status,
            }).then(res => {
                if (res.code == '000000') {
                    this.getUserList();
                    this.$message({
                        type: 'success',
                        message: this.$t("successed") || 'success'
                    });
                } else {
                    this.getUserList();
                    this.$message({
                        type: 'error',
                        message: 'error'
                    })
                }
            })
        },
        deleteData(row) {
            this.$confirm(this.$t('confirmDeleteTenant'), this.$t('tips'), {
                confirmButtonText: this.$t('confirm'),
                cancelButtonText: this.$t('cancel'),
                type: 'warning'
            }).then(() => {
                deleteTenant({
                    tenantId: [row.tenantId]
                }).then(res => {
                    if (res.code == '000000') {
                        this.getUserList();
                        this.$message({
                            type: 'success',
                            message: this.$t("successed") || 'success'
                        });
                    } else {
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
        addRoleDialog() {
            this.row = '';
            this.roleForm = {
                tenantName: '',
                contacts: '',
                contactsPhone: '',
                contactsEmail: '',
                remark: '',
                userNumLimit: 20,
                logoUrl: '',
                favicon: '',
                defaultPassword: 'User#%2024',
            },
                this.fileListLogo = []
            this.favListLogo = []
            this.dialogVisible = true;
        },
        editUser(row) {
            console.log(row)
            // findTenant({tenantId:row.tenantId}).then(res=>{
            //     console.log(res)
            //     this.roleForm = res.data
            // })
            this.row = row;
            this.roleForm = {
                tenantName: row.tenantName,
                contacts: row.contacts,
                contactsPhone: row.contactsPhone,
                contactsEmail: row.contactsEmail,
                remark: row.remark,
                userNumLimit: row.userNumLimit,
                defaultPassword: row.defaultPassword,
                logoUrl: row.logoUrl,
                favicon: row.favicon,
                title: row.title,
            }
            if (row.logoUrl) {
                this.fileListLogo = [
                    {
                        name: "logo",
                        url: row.logoUrl,
                    },
                ];
            } else {
                this.fileListLogo = []
            }
            if (row.favicon) {
                this.favListLogo = [
                    {
                        name: 'fav',
                        url: row.favicon
                    }
                ]
            } else {
                this.favListLogo = []
            }
            this.dialogVisible = true;
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.getUserList();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.getUserList();
        },
        handleSizeChanges(val) {
            this.pageSizes = val;
            this.getUserLists();
        },
        handleCurrentChanges(val) {
            this.currentPages = val;
            this.getUserLists();
        },
        resetSearch() {
            this.currentPage = 1;
            this.userName = '',
                this.status = '';
            this.dateRange = [];
            this.getUserList();
        },
        search() {
            this.currentPage = 1;
            this.getUserList();
        },
        getUserList() {
            tenantList({
                pageNo: this.currentPage,
                pageSize: this.pageSize,
                tenantName: this.userName,
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
            this.$refs['roleForm'].validate((valid) => {
                if (valid) {
                    addTenant({
                        id: this.row ? this.row.id : '',
                        tenantId: this.row ? this.row.tenantId : '',
                        ...this.roleForm,
                    }).then(res => {
                        if (res.code == '000000') {
                            console.log(this.roleForm)
                            this.dialogVisible = false;
                            this.getUserList();
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
            this.$refs['roleForm'].resetFields();
            this.dialogVisible = false;
        },
        handleLogoSuccess(response, file, fileList) {
            if ((response.code = "000000")) {
                this.uploadBtnLogo = true;
                this.roleForm.logoUrl = response.data && response.data[0] ? response.data[0].url : "";
                this.fileListLogo = [
                    {
                        name: "logo",
                        url: this.roleForm.logoUrl,
                    },
                ];

            } else {
                this.uploadBtnLogo = false;
                this.roleForm.logoUrl = "";
                this.fileListLogo = [];
            }
        },
        handleLogoRemove(file, fileList) {
            this.uploadBtnLogo = false;
            this.roleForm.logoUrl = "";
            this.fileListLogo = [];
        },
        handleFavSuccess(response, file, fileList) {
            if (response.code == "000000") {
                this.uploadfavLogo = true
                this.roleForm.favicon = response.data && response.data[0] ? response.data[0].url : "";
                this.favListLogo = [
                    {
                        name: "favLogo",
                        url: this.roleForm.favicon,
                    },
                ];
                console.log(this.roleForm.favicon, this.favListLogo)
            } else {
                this.uploadfavLogo = false;
                this.roleForm.favicon = ''
                this.favListLogo = []
            }
        },
        handleFavRemove(file, fileList) {
            this.uploadfavLogo = false;
            this.roleForm.favicon = ''
            this.favListLogo = []
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
        height: 600px;
        overflow-y: auto;

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

    .roleForm {
        .el-form-item {
            margin-bottom: 20px;

            .el-radio__input.is-checked .el-radio__inner {
                border-color: #1C50FD;
                background: #1C50FD;
            }

            .el-radio__input.is-checked+.el-radio__label {
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
