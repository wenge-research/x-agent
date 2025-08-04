<template>
  <div class="config-manage">
    <div class="inner">
      <div
        style="
          display: flex;
          justify-content: space-between;
          margin-bottom: 20px;
        "
      >
        <div style="display: flex; align-items: center; width: 100%">
          <div style="margin-right: 8px">
            <el-input
              v-model="searchForm.keyword"
              clearable
              :placeholder="$t('pleaseEnterKeyword')"
              prefix-icon="el-icon-search"
              style="width: 334px"
              @keydown.native.enter="search"
            ></el-input>
          </div>
          <el-button
            type="primary"
            style="margin-right: 8px; height: 40px"
            @click="search"
            >{{ $t("search") }}</el-button
          >
          <!-- <el-button @click="resetSearch" style="height: 40px">{{
            $t("reset")
          }}</el-button> -->
          <el-button type="primary" style="margin-left: auto" @click="addConfig"
            ><i class="el-icon-plus" style="margin-right: 8px;"></i> {{$t('addConfiguration')}}</el-button
          >
        </div>
      </div>
      <div v-loading="loading">
        <div style="margin-top: 16px">
          <el-table
            size="small"
            ref="multipleTable"
            :data="tableData"
            tooltip-effect="dark"
            style="width: 100%"
            class="table"
            max-height="590"
          >
            <el-table-column
              type="index"
              width="60"
              :label="$t('sequenceNumber')"
            ></el-table-column>
            <el-table-column prop="keyInfo" label="KEY"></el-table-column>
            <el-table-column prop="valueInfo" label="VALUE"></el-table-column>
            <el-table-column prop="remark" :label="$t('description')"></el-table-column>
            <el-table-column :label="$t('operation')" width="120">
              <template slot-scope="scope">
                <el-button type="text" @click="editConfig(scope.row)"
                  >{{$t('edit')}}</el-button
                >
                <el-button type="text" @click="deleteHandler(scope.row)"
                  >{{$t('delete')}}</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div style="text-align: right; margin-top: 20px">
          <el-pagination
            background
            layout="total, prev, pager, next, sizes, jumper"
            popper-class="slectStyle"
            :current-page="currentPage"
            :total="total"
            :page-size="pageSize"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
            :page-sizes="[10, 20, 30, 40]"
          ></el-pagination>
        </div>
      </div>
    </div>
    <!-- 添加配置 -->
    <el-dialog
      :title="`${dialogTitle}配置`"
      :visible.sync="dialogVisible"
      width="560px"
      height="472px"
      :modal-append-to-body="false"
      append-to-body
      custom-class="config-manage-dialog weight1"
      :before-close="closeDialog"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="dialog-config">
        <el-form ref="configForm" :model="configForm" :rules="rules">
          <el-form-item prop="keyInfo" label="KEY">
            <el-input
              v-model="configForm.keyInfo"
              :placeholder="$t('pleaseEnter')"
              clearable
            />
          </el-form-item>
          <el-form-item prop="valueInfo" label="VALUE">
            <el-input
              v-model="configForm.valueInfo"
              :placeholder="$t('pleaseEnter')"
              clearable
              type="textarea"
              :rows="4"
            />
          </el-form-item>
          <el-form-item prop="remark" :label="$t('description')">
            <el-input
              v-model="configForm.remark"
              :placeholder="$t('pleaseEnter')"
              type="textarea"
              maxlength="2000"
              :rows="4"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <div class="dialog-footer">
        
        <el-button plain @click="closeDialog" :loading="addLoading"
          >{{ $t('cancel') }}</el-button
        >
        <el-button type="primary" @click="onSubmit" :loading="addLoading"
          >{{ $t('confirm') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  apiGetApplicationConfigList,
  apiAddApplicationConfig,
  apiUpdateApplicationConfig,
  apiDeleteApplicationConfig,
} from "@/api/configManage";
export default {
  name: "ConfigManage",
  props: {
    data: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      searchForm: {
        keyword: "",
      },
      currentPage: 1,
      total: 0,
      pageSize: 10,
      tableData: [],
      loading: false,
      addLoading: false,
      dialogTitle: "添加",
      dialogVisible: false,
      configForm: {
        keyInfo: "",
        valueInfo: "",
        remark: "",
      },
      rules: {
        keyInfo: [
          {
            required: true,
            message: "请输入KEY",
            trigger: "blur",
          },
        ],
        valueInfo: [
          {
            required: true,
            message: "请输入VALUE",
            trigger: "blur",
          },
        ],
        remark: [
          {
            required: true,
            message: "请输入描述",
            trigger: "blur",
          },
        ],
      },
    };
  },
  created() {
    this.getApplicationConfigList();
  },
  methods: {
    handleSizeChange(val) {
      this.pageSize = val;
      this.getApplicationConfigList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getApplicationConfigList();
    },
    resetSearch() {
      this.currentPage = 1;
      (this.searchForm.keyword = ""), (this.searchForm.logType = "");
      this.getApplicationConfigList();
    },
    search() {
      this.currentPage = 1;
      this.getApplicationConfigList();
    },
    async getApplicationConfigList() {
      this.loading = true;
      const params = {
        pageNo: this.currentPage,
        pageSize: this.pageSize,
        keyword: this.searchForm.keyword,
        applicationId: this.data?.applicationId,
      };
      try {
        const res = await apiGetApplicationConfigList(params);
        if (res.code == "000000") {
          this.total = res.data?.totalRow || 0;
          this.tableData = res.data?.records || [];
        } else {
          this.total = 0;
          this.tableData = [];
        }
      } catch (error) {
        this.loading = false;
      }
      this.loading = false;
    },
    addConfig() {
      this.configForm.keyInfo = "";
      this.configForm.valueInfo = "";
      this.configForm.remark = "";
      this.configForm.id = "";
      this.dialogVisible = true;
      this.dialogTitle = "添加";
    },
    editConfig(data) {
      this.dialogVisible = true;
      this.dialogTitle = "修改";
      this.configForm.keyInfo = data.keyInfo;
      this.configForm.valueInfo = data.valueInfo;
      this.configForm.remark = data.remark;
      this.configForm.id = data.id;
    },
    closeDialog() {
      for (let key in this.configForm) {
        this.configForm[key] = "";
      }
      this.$refs['configForm'].resetFields();
      this.dialogVisible = false;
    },
    onSubmit() {
      this.$refs.configForm.validate((valid) => {
        if (valid) {
          if (this.dialogTitle == "添加") {
            this.addApplicationConfig();
          } else {
            this.updateApplicationConfig();
          }
        }
      });
    },
    // 新增
    async addApplicationConfig() {
      this.addLoading = true;
      const params = {
        applicationId: this.data?.applicationId,
        ...this.configForm,
      };
      try {
        const res = await apiAddApplicationConfig(params);
        if (res.code == "000000") {
          this.$message.success(`${this.dialogTitle}成功`);
          this.closeDialog();
          this.search();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.addLoading = false;
      }
      this.addLoading = false;
    },
    // 修改
    async updateApplicationConfig() {
      this.addLoading = true;
      const params = {
        applicationId: this.data?.applicationId,
        ...this.configForm,
      };
      try {
        const res = await apiUpdateApplicationConfig(params);
        if (res.code == "000000") {
          this.$message.success(`${this.dialogTitle}成功`);
          this.closeDialog();
          this.search();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.addLoading = false;
      }
      this.addLoading = false;
    },
    deleteHandler(data) {
      this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => {
        const params = {
          applicationId: this.data?.applicationId,
          delIds: [data.id],
        };
        try {
          const res = await apiDeleteApplicationConfig(params);
          if (res.code == "000000") {
            this.$message.success("删除成功");
            this.search();
          } else {
            this.$message.warning(res.msg);
          }
        } catch (error) {
          this.addLoading = false;
        }
      });
    },
  },
};
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
  background: #e1e4eb; /* 滑块颜色 */
  border-radius: 6px;
}
.slectStyle {
  .el-select-dropdown__item.selected {
    color:  #1747E5;
  }
}
.config-manage-dialog {
  background: #fff !important;
  .el-dialog__header {
    padding: 32px 32px 16px;
    background: #fff !important;
    .el-dialog__title {
      height: 24px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
    }
    .el-dialog__headerbtn {
      top: 36px;
    }
  }
  .el-dialog__body {
    padding: 0 32px 32px !important;
    background: #fff !important;
    .el-input__inner {
      border-radius: 4px;
    }
    .el-input__inner:focus {
      border-color:  #1747E5;
    }
    .checkboxOuter {
      height: 410px;
      padding: 0 8px;
      overflow-y: scroll;
    }
    .el-form-item__label {
      color: #383d47;
      font-size: 16px;
      font-weight: 500;
    }
  }
  .dialog-footer {
    text-align: right;
    .el-button {
      border-radius: 4px;
    }
    .el-button--primary {
      background:  #1747E5;
      border-color:  #1747E5;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
}
.confirm-ok {
  background:  #1747E5 !important;
  border-color: transparent !important;
}
.confirm-cancel {
  &:hover {
    background: none !important;
    color:  #1747E5 !important;
    border-color:  #1747E5 !important;
  }
}
</style>
<style lang="scss" scoped>
.config-manage {
  width: 100%;
  height: 100%;
  font-family: MiSans, MiSans;
  .inner {
    width: 100%;
    height: calc(100% - 70px);
    background: #fff;
    border-radius: 4px;
    ::v-deep .el-input__inner:focus,
    ::v-deep .el-date-editor:focus,
    ::v-deep .el-date-editor.is-active {
      border-color:  #1747E5 !important;
    }
    ::v-deep .el-button {
      border-radius: 4px;
    }
  }
}
.el-button {
  &.el-button-default {
    border-radius: 4px;
    color: #383d47;
    border: 1px solid #c4c6cc;
  }

  &.el-button--primary {
    background-color:  #1747E5;
    color: #fff;
    border-color:  #1747E5;
  }
}

::v-deep .el-input__inner {
  border-radius: 8px !important;
}
::v-deep .el-textarea__inner {
  font-family: MiSans, MiSans;
  font-size: 14px;
  color: #606266;
  line-height: 20px;
}

::v-deep .el-date-editor .el-range-separator {
  width: 8%;
}

::v-deep .table {
  .is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%,  #1747E5 100%);
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
      background: #f2f5fa;
    }
  }

  tr {
    color: #383d47;
    font-size: 14px;
    font-weight: 400;
  }

  .el-button--text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color:  #1747E5;
  }
}

::v-deep .el-pager li {
  border: 1px solid #dddfe8;
  color: #272a31;
  background: #fff;
  .el-select-dropdown__item.selected {
    color:  #1747E5;
  }

  &:not(.disabled).active {
    background-color: #fff !important;
    border: 1px solid  #1747E5;
    color: #272a31 !important;
  }
}

::v-deep .btn-prev,
::v-deep .btn-next {
  border: 1px solid #dddfe8;
  color: #272a31;
  background: #fff;
}
</style>
