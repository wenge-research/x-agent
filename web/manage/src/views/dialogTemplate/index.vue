<template>
  <div class="outer">
    <div class="inner">
      <div style="display:flex;justify-content:space-between;margin-bottom:20px;">
        <div style="display:flex;justify-content:flex-start;">
          <div style="margin-right:24px;">
            <span style="margin-right:20px;">{{ $t('templateForm') }}</span>
            <el-select style="width:148px;" v-model="form" :placeholder="$t('selectPlaceholder')" clearable>
              <el-option v-for="item in formOption" :key="item.id" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </div>
          <div style="margin-right: 8px">
            <el-input
              v-model="dialogTemplateName"
              clearable
              :placeholder="$t('searchTemplateName')"
              prefix-icon="el-icon-search"
              style="width: 334px"
              @keydown.native.enter="search"
              @input="search"
            ></el-input>
          </div>

          <el-button type="primary" style="margin-right:8px;height:40px;" @click="search">{{ $t('search') }}</el-button>
          <el-button @click="resetSearch" style="height:40px;">{{ $t('reset') }}</el-button>
        </div>
        <el-button type="primary" style="float:right;height:40px;" @click="addRoleDialog"><i class="el-icon-circle-plus" style="margin-right:8px;"></i>{{
          $t('addChatTemplate') }}</el-button>
      </div>
      <div style="margin-top:16px;">
        <el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" class="table">
          <el-table-column type="index" width="60" :label="$t('sequenceNumber')"></el-table-column>
          <el-table-column prop="templateName" :label="$t('templateName')"></el-table-column>
          <el-table-column prop="templateRoute" :label="$t('templateRoute')"></el-table-column>
          <el-table-column prop="picturePath" :label="$t('templateImagePath')" width="220">
            <template slot-scope="scope">
              <div>
                <el-image :src="scope.row.picturePath" style="height: 100px;"></el-image>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="form" :label="$t('chatTemplateForm')" width="180"></el-table-column>
          <el-table-column prop="form" :label="$t('isEnabled')" width="180">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.statusBoolean" active-color="#0044FF" inactive-color="#DDDDDD"
                @change="changeStatus(scope.row)"></el-switch>
            </template>
          </el-table-column>
          <el-table-column :label="$t('action')" width="180">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="editTemplate(scope.row)">{{ $t('edit') }}</el-button>
              <el-button type="text" size="small" @click="deleteData(scope.row)">{{ $t('delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div style="text-align:right;margin-top:20px;">
        <el-pagination background layout="total, prev, pager, next, sizes, jumper" popper-class="slectStyle"
          :current-page="currentPage" :total="total" :page-size="pageSize" @current-change="handleCurrentChange"
          @size-change="handleSizeChange" :page-sizes="[5, 10]"></el-pagination>
      </div>
      <el-dialog :title="row ? $t('editChatTemplate') : $t('addNewChatTemplate')" :visible.sync="dialogVisible"
        width="600px" class="dialog" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form :model="dialogTemplateFrom" :rules="roles" ref="dialogTemplateFrom" class="dialogTemplateFrom"
          label-position="top">
          <div>
            <el-form-item :label="$t('templateName')" prop="templateName">
              <el-input v-model="dialogTemplateFrom.templateName"></el-input>
            </el-form-item>
            <el-form-item :label="$t('templateRoute')" prop="templateRoute">
              <el-input v-model="dialogTemplateFrom.templateRoute"></el-input>
            </el-form-item>
            <el-form-item :label="$t('templateImage')" prop="picturePath">
              <!--              <el-input v-model="dialogTemplateFrom.picturePath" type="textarea"></el-input>-->
              <el-upload :action="actionUrl" :data="{ filePath: 'agent_source' }" :file-list="fileList" :limit="1"
                class="identityUpload" list-type="picture-card" :on-remove="onRemove" :on-success="onSuccess">
                <div>
                  <img src="@/assets/images/add-line.svg" style="width:15px;height:15px;vertical-align:middle;">
                  <span style="font-size: 16px;color: #B4BCCC;vertical-align:middle;">{{ $t('uploadImage') }}</span>
                </div>
              </el-upload>
            </el-form-item>
            <el-form-item :label="$t('templateForm')" prop="form">
              <el-select style="width:100%;" v-model="dialogTemplateFrom.form" :placeholder="$t('selectPlaceholder')"
                clearable>
                <el-option v-for="item in formOption" :key="item.id" :label="item.label"
                  :value="item.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('intelligenceType')" prop="intelligenceType">
              <el-select style="width:100%;" v-model="dialogTemplateFrom.intelligenceType" :placeholder="$t('selectPlaceholder')"
                clearable>
                <el-option v-for="item in intelligenceTypeOption" :key="item.id" :label="item.label"
                  :value="item.value"></el-option>
              </el-select>
            </el-form-item>
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
import {
  addRole,
  roleList,
  allmenulist,
  menulists,
  deleteRole,
  updateRoleStatus,
  getDialogTemplateListApi, addDialogTemplateApi, deleteDialogTemplateApi, updateTemplateStatus
} from '@/api/app'
export default {
  data() {
    return {
      row: '',
      treeData: [],
      defaultProps: {
        children: 'children',
        label: 'menuName'
      },
      form: '',
      dateRange: [],
      templateName: '',
      dialogTemplateName: '',
      userName: '',
      currentPage: 1,
      total: 0,
      pageSize: 5,
      roles: {
        templateName: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
        templateRoute: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
        picturePath: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
        form: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
        intelligenceType: [{ required: true, message: this.$t('cannotBeEmpty'), trigger: 'blur' }],
      },
      dialogTemplateFrom: {},
      tableData: [],
      dialogVisible: false,
      // actionUrl: `${ process.env.VUE_APP_BASE_API }/wos/file/upload`,
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      fileList: [],
      formOption: [
        {
          label: 'PC',
          value: 'PC',
          id: '1',
        },
        {
          label: 'H5',
          value: 'H5',
          id: '2',
        }
      ],
      intelligenceTypeOption: [
        {
          label: '智能问答',
          value: 'smart_qa',
        },
        {
          label: '智能报告',
          value: 'smart_report',
        },
        {
          label: '智能搜索',
          value: 'smart_search',
        },
        {
          label: '智能翻译',
          value: 'smart_translate',
        },
        {
          label: '智能审查',
          value: 'smart_review',
        }
      ]
    }
  },
  created() {
    this.search();
  },
  methods: {
    //切换模板状态 （0：未启用  1：启用）
    async changeStatus(row) {
      const parms = {
        templateId: row.templateId,
        status: row.statusBoolean ? '1' : '0'
      }
      try {
        const res = await updateTemplateStatus(parms)
        if (res.code == '000000') {
          this.getDialogTemplateList();
          this.$message({
            type: 'success',
            message: res.msg
          });
        } else {
          this.$message({
            type: 'error',
            message: res.msg
          })
        }
      } catch (error) {
        console.log('error' + error)
      }
    },
    getAllMenuList() {
      allmenulist({}).then(res => {
        if (res.code == '000000') {
          this.treeData = res.data;
          if (this.row) {
            this.getMenuList(this.row);
          }
        } else {
          this.treeData = [];
        }
      })
    },
    getMenuList(row) {
      menulists({
        roleId: row.roleId
      }).then(res => {
        if (res.code == '000000') {
          (res.data || []).forEach(item => {
            var node = this.$refs.tree.getNode(item);
            if (node.isLeaf) {
              this.$refs.tree.setChecked(node, true);
            }
          });
        }
      })
    },
    resetChecked() {
      this.$refs.tree.setCheckedKeys([]);
    },
    // changeStatus(row) {
    //   console.log('row',row)
    //   updateRoleStatus({
    //     templateId: row.templateId,
    //     status: row.status,
    //   }).then(res => {
    //     if (res.code == '000000') {
    //       this.getDialogTemplateList();
    //       this.$message({
    //         type: 'success',
    //         message: 'success'
    //       });
    //     } else {
    //       this.getDialogTemplateList();
    //       this.$message({
    //         type: 'error',
    //         message: 'error'
    //       })
    //     }
    //   })
    // },
    deleteData(row) {
      this.$confirm(this.$t('confirmDeleteChatTemplate'), this.$t('tips'), {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        type: 'warning'
      }).then(() => {
        deleteDialogTemplateApi([row.templateId]).then(res => {
          if (res.code == '000000') {
            this.getDialogTemplateList();
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
      this.dialogTemplateFrom = {}
      this.dialogVisible = true;
      this.fileList = []
    },
    editTemplate(row) {
      this.row = row;
      this.dialogTemplateFrom = { ...row }
      this.dialogVisible = true;
      if (this.dialogTemplateFrom.picturePath) {
        this.fileList = [{
          url: this.dialogTemplateFrom.picturePath,
          name: this.dialogTemplateFrom.picturePath.substring(this.dialogTemplateFrom.picturePath.lastIndexOf('/') + 1)
        }]
      }
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getDialogTemplateList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getDialogTemplateList();
    },
    resetSearch() {
      this.currentPage = 1;
      this.form = '',
      this.dialogTemplateName = '',
      this.getDialogTemplateList();
    },
    search() {
      this.currentPage = 1;
      this.getDialogTemplateList();
    },
    getDialogTemplateList() {
      getDialogTemplateListApi({
        pageNo: this.currentPage,
        pageSize: this.pageSize,
        form: this.form,
        dialogTemplateName: this.dialogTemplateName
      }).then(res => {
        if (res.code == '000000') {
          this.total = res.data?.totalRow || 0;
          const tableData = res.data?.records || [];
          this.tableData = tableData.map(item => ({
            ...item,
            statusBoolean: item.status === 1 // 1 → true, 0 → false
          }))
        } else {
          this.total = 0;
          this.tableData = [];
        }
      })
    },
    submitAddUser() {
      this.$refs['dialogTemplateFrom'].validate((valid) => {
        if (valid) {
          addDialogTemplateApi(this.dialogTemplateFrom).then(res => {
            if (res.code == '000000') {
              this.dialogVisible = false;
              this.getDialogTemplateList();
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
      this.$refs['dialogTemplateFrom'].resetFields();
      this.dialogVisible = false;
    },
    onRemove: function menuIconRemove(file, fileList) {
      this.dialogTemplateFrom.picturePath = '';
      this.fileList = [];
    },
    onSuccess: function menuIconSuccess(response, file, fileList) {
      if (response.code = '000000') {
        this.dialogTemplateFrom.picturePath = response.data && response.data[0] ? response.data[0].url : '';
        this.fileList = [{
          name: 'picturePath',
          url: this.dialogTemplateFrom.picturePath
        }];
      } else {
        this.dialogTemplateFrom.picturePath = '';
        this.fileList = [];
      }
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
  background: #fff;
  /* 轨道颜色 */
}

/* 自定义滚动条的滑块（thumb） */
.checkboxOuter::-webkit-scrollbar-thumb {
  background: #E1E4EB;
  /* 滑块颜色 */
  border-radius: 6px;
}

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
  padding: 32px 24px 0;
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

    .is-indeterminate .el-checkbox__inner,
    .is-checked .el-checkbox__inner {
      background-color: #1C50FD;
      border-color: #1C50FD;
    }

    .checkboxOuter {
      height: 410px;
      padding: 0 8px;
      overflow-y: scroll;
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

  .dialogTemplateFrom {
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
