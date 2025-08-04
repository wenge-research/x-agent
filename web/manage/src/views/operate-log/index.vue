<template>
  <div class="outer">
    <div class="title">{{$t('operationLog')}}</div>
    <div class="inner">
      <div
        style="
          display: flex;
          justify-content: space-between;
          margin-bottom: 20px;
        "
      >
        <div style="display: flex; justify-content: flex-start">
          <div style="margin-right: 24px">
            <span style="margin-right: 20px">{{ $t("operationType") }}</span>
            <el-select
              style="width: 148px"
              v-model="searchForm.logType"
              :placeholder="$t('selectPlaceholder')"
              clearable
            >
              <el-option
                v-for="(item, index) in logTypeList"
                :key="index"
                :label="item.value"
                :value="item.key"
              ></el-option>
            </el-select>
          </div>
          <div style="margin-right: 24px">
            <span style="margin-right: 20px">{{ $t("operationTime") }}</span>
            <el-date-picker
              v-model="dateRange"
              clearable
              style="width: 252px"
              type="daterange"
              :range-separator="$t('to')"
              :start-placeholder="$t('startDate')"
              value-format="yyyy-MM-dd HH:mm:ss"
              :end-placeholder="$t('endDate')"
              :default-time="['00:00:00', '23:59:59']"
            ></el-date-picker>
          </div>
          <div style="margin-right: 8px">
            <el-input
              v-model="searchForm.keyword"
              clearable
              :placeholder="$t('searchUsernameOrObjectName')"
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
          <el-button @click="resetSearch" style="height: 40px">{{
            $t("reset")
          }}</el-button>
        </div>
      </div>
      <div v-loading="loading">
        <div style="margin-top: 16px">
          <el-table
            ref="multipleTable"
            :data="tableData"
            tooltip-effect="dark"
            style="width: 100%;"
            class="table"
            max-height="630"
          >
            <el-table-column
              type="index"
              width="60"
              :label="$t('sequenceNumber')"
            ></el-table-column>
            <el-table-column prop="ip" :label="$t('ipAddress')"></el-table-column>
            <el-table-column
              prop="operatorTagUserName"
              :label="$t('username')"
            ></el-table-column>
            <el-table-column
              prop="operatorTagUserId"
              :label="$t('userUniqueID')"
              width="100"
            ></el-table-column>
            <el-table-column prop="logTypeName" :label="$t('operationType')" width="90">
            </el-table-column>
            <el-table-column prop="objectType" :label="$t('objectType')" width="90">
            </el-table-column>
            <el-table-column prop="objectName" :label="$t('objectName')"></el-table-column>
            <el-table-column
              prop="objectUuid"
              :label="$t('objectUniqueID')"
            ></el-table-column>
            <el-table-column
              prop="accessTime"
              :label="$t('operationTime')"
              width="180"
            ></el-table-column>
            <el-table-column
              prop="belongModuleName"
              :label="$t('moduleBelongsTo')"
            ></el-table-column>
            <el-table-column
              prop="description"
              :label="$t('operationDescription')"
            ></el-table-column>
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
  </div>
</template>
<script>
import { apiUmsOperationList, apiGetLogTypeList } from "@/api/app";
export default {
  data() {
    return {
      searchForm: {
        logType: "",
        keyword: "",
      },
      dateRange: [],
      currentPage: 1,
      total: 0,
      pageSize: 10,
      tableData: [],
      loading: false,
      logTypeList: [],
    };
  },
  created() {
    this.getLogTypeList();
    this.getUserList();
  },
  methods: {
    resetChecked() {
      this.$refs.tree.setCheckedKeys([]);
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getUserList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getUserList();
    },
    resetSearch() {
      this.currentPage = 1;
      (this.searchForm.keyword = ""), (this.searchForm.logType = "");
      this.dateRange = [];
      this.getUserList();
    },
    search() {
      this.currentPage = 1;
      this.getUserList();
    },
    // 操作类型
    async getLogTypeList() {
      const res = await apiGetLogTypeList();
      if (res.code == "000000") {
        this.logTypeList = res.data || [];
      }
    },
    async getUserList() {
      this.loading = true;
      const params = {
        pageNo: this.currentPage,
        pageSize: this.pageSize,
        keyword: this.searchForm.keyword,
        logType: this.searchForm.logType,
        startAccessTime: this.dateRange ? this.dateRange[0] : "",
        endAccessTime: this.dateRange ? this.dateRange[1] : "",
      };
      try {
        const res = await apiUmsOperationList(params);
        if (res.code == "000000") {
          this.total = res.data?.total || 0;
          this.tableData = res.data?.list || [];
        } else {
          this.total = 0;
          this.tableData = [];
        }
      } catch (error) {
        this.loading = false;
      }
      this.loading = false;
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
    color: #1c50fd;
  }
}
</style>
<style lang="scss" scoped>
.outer {
  width: 100%;
  height: 100%;
  padding: 9px 24px 24px;
  font-family: MiSans, MiSans;
  .inner {
    width: 100%;
    height: calc(100% - 70px);
    background: #fff;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    padding: 24px;
    ::v-deep .el-input__inner:focus,
    ::v-deep .el-date-editor:focus,
    ::v-deep .el-date-editor.is-active {
      border-color: #1c50fd !important;
    }
  }
  .title {
    height: 70px;
    line-height: 40px;
    padding: 16px 0 8px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 22px;
    color: #383d47;
  }
}
.el-button {
  &.el-button-default {
    border-radius: 4px;
    color: #383d47;
    border: 1px solid #c4c6cc;
  }

  &.el-button--primary {
    background-color: #1c50fd;
    color: #fff;
    border-color: #1c50fd;
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
  border-color: #b4bccc;
}

::v-deep .el-checkbox__label {
  font-size: 16px;
  color: #383d47;
  font-weight: 400;
}

::v-deep .table {
  .is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
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
    font-size: 16px;
    font-weight: 400;
    .el-checkbox__input.is-checked .el-checkbox__inner,
    .el-checkbox__input.is-indeterminate .el-checkbox__inner {
      background: #1c50fd;
      border-color: #1c50fd;
    }
  }

  .el-button--text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #1c50fd;
  }
}

::v-deep .el-pager li {
  border: 1px solid #dddfe8;
  color: #272a31;
  background: #fff;
  .el-select-dropdown__item.selected {
    color: #1c50fd;
  }

  &:not(.disabled).active {
    background-color: #fff !important;
    border: 1px solid #1c50fd;
    color: #272a31 !important;
  }
}

::v-deep .btn-prev,
::v-deep .btn-next {
  border: 1px solid #dddfe8;
  color: #272a31;
  background: #fff;
}
::v-deep .dialog {
  .el-dialog__header {
    padding: 20px 32px;
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
    .el-dialog__title {
      color: #383d47;
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
      border-color: #1c50fd;
    }
    .is-indeterminate .el-checkbox__inner,
    .is-checked .el-checkbox__inner {
      background-color: #1c50fd;
      border-color: #1c50fd;
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
  .el-dialog__footer {
    text-align: left;
    .el-button {
      border-radius: 4px;
    }
    .el-button--primary {
      background: #1c50fd;
      border-color: #1c50fd;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
}
</style>
