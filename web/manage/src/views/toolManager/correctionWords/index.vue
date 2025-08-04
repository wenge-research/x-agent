<template>
  <div class="outer">
    <!-- <div class="title">{{ $t("sensitive") }}</div> -->
    <div v-if="!showSensitive">
      <!-- <el-tabs v-model="activeName" @tab-click="handleClick" class="elTabs">
          <el-tab-pane :label="$t('sensitive')" name="first"></el-tab-pane>
          <el-tab-pane :label="$t('correctionWords')" name="second"></el-tab-pane>
          <el-tab-pane :label="$t('synonym')" name="third"></el-tab-pane>
        </el-tabs> -->
      <div class="inner">
        <div style="display: flex; justify-content: space-between; margin-bottom: 20px">
          <div style="display: flex; justify-content: flex-start">
            <div style="margin-right: 24px">
              <span style="margin-right: 20px">{{ $t("updateTime") }}</span>
              <el-date-picker v-model="dateRange" clearable style="width: 252px" type="daterange"
                :range-separator="$t('to')" :start-placeholder="$t('startDate')" value-format="yyyy-MM-dd HH:mm:ss"
                :end-placeholder="$t('endDate')" :default-time="['00:00:00', '23:59:59']"></el-date-picker>
            </div>
            <div style="margin-right: 8px">
              <el-input v-model="searchForm.keyword" clearable :placeholder="$t('searchKeywords')"
                prefix-icon="el-icon-search" style="width: 334px" @keydown.native.enter="search"></el-input>
            </div>
            <div style="margin-right: 8px">
              <el-select v-model="searchForm.type" placeholder="请选择所属分类" clearable>
                <el-option
                  v-for="item in typeList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </div>
            <el-button type="primary" style="margin-right: 8px; height: 40px" @click="search">{{ $t("search")
              }}</el-button>
            <el-button @click="resetSearch" style="height: 40px">{{
              $t("reset")
            }}</el-button>
          </div>
          <div style="float: right; height: 40px">
            <el-button type="primary"
            @click="addRoleDialog"><i class="el-icon-circle-plus" style="margin-right: 8px;"></i>{{ $t("createErrorCorrectingWords") }}</el-button>
            <el-button @click="handleImport">{{ $t("batch") }}{{ $t("import") }}</el-button>
          </div>
        </div>
        <div v-loading="loading">
          <div style="margin-top: 16px">
            <el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" class="table"
              max-height="660">
              <el-table-column type="index" width="60" :label="$t('sequenceNumber')"></el-table-column>
              <el-table-column prop="keyWord" :label="$t('keywords')"></el-table-column>
              <el-table-column prop="type" :label="$t('category')"></el-table-column>
              <el-table-column prop="wordCount" :label="$t('numberOfCorrects')"></el-table-column>
              <el-table-column prop="updateTime" :label="$t('updateTime')">
              </el-table-column>
              <el-table-column prop="createUserName" :label="$t('founder')">
              </el-table-column>
              <el-table-column prop="createTime" :label="$t('creationTime')"></el-table-column>
              <!-- <el-table-column prop="status" :label="$t('enable')" width="120">
                <template slot-scope="scope">
                  <el-switch v-model="scope.row.status" active-value="1" inactive-value="2"
                    @change="changeStatus(scope.row)"></el-switch>
                  <span style="color: #383d47; font-size: 16px; margin-left: 5px">{{
                    scope.row.status == "1"
                      ? $t("settingsEnabled")
                      : $t("settingsDisabled")
                  }}</span>
                </template>
              </el-table-column> -->
              <el-table-column :label="$t('action')" width="120">
                <template slot-scope="scope">
                  <el-button type="text" size="small" @click="editMenu(scope.row)">{{
                    $t("view")
                  }}</el-button>
                  <el-button type="text" size="small" @click="deleteData(scope.row)">{{
                    $t("delete")
                  }}</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <!-- <div style="text-align: right; margin-top: 20px">
              <el-pagination
                background
                layout="total, prev, pager, next, sizes, jumper"
                popper-class="slectStyle"
                :current-page="pageObj.pageNo"
                :total="total"
                :page-size="pageSize"
                @current-change="handleCurrentChange"
                @size-change="handleSizeChange"
                :page-sizes="[10, 20, 30, 40]"
              ></el-pagination>
            </div> -->
          <div v-if="tableData.length" class="pagination">
            <div class="total">
              {{ $t("total") }}{{ pageObj.total }}{{ $t("items") }}
            </div>
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
              :current-page.sync="pageObj.pageNo" :page-sizes="[12, 24, 36, 48]" :page-size="pageObj.pageSize"
              background layout="prev, pager, next, sizes" :total="pageObj.total">
            </el-pagination>
          </div>
          <div v-else class="no-data">
            <img src="@/assets/images/no-data.png" alt="" />
            <div class="txt1">{{ $t("noData") }}</div>
          </div>
        </div>
      </div>
    </div>
    <!-- 编辑敏感词库 -->
    <editSensitiveLibrary v-else @closeSensitiveDialog="closeSensitiveDialog" :sensitiveRow="sensitiveRow">
    </editSensitiveLibrary>
    <addDialog v-if="dialogVisible" :dialogVisible="dialogVisible" @closeDialog="closeDialog" :isEdit="isEdit"
    :sensitiveRow="sensitiveRow" @submitDialog="submitDialog"></addDialog>
    <associatedApplications v-if="dialogAppVisible" :dialogVisible="dialogAppVisible"
      :applicationDataList="applicationDataList" @closeAppDialog="closeAppDialog">
    </associatedApplications>
    <!-- 数据导入 -->
    <dataImportDialog
      v-if="dialogDataVisible"
      :dialogDataVisible="dialogDataVisible"
      @closeDataDialog="closeDataDialog"
    >
    </dataImportDialog>
  </div>
</template>
<script>
import {
  getCorrectWordList,
  deleteCorrectWord,
  getCorrectWordKeyList,
} from "@/api/toolManager";
import addDialog from "@/views/toolManager/correctionWords/components/addDialog";
import editSensitiveLibrary from "@/views/toolManager/correctionWords/components/editSensitiveLibrary";
import associatedApplications from "@/views/toolManager/correctionWords/components/associatedApplications";
import dataImportDialog from "@/views/toolManager/correctionWords/components/dataImportDialog";
export default {
  components: {
    addDialog,
    editSensitiveLibrary,
    associatedApplications,
    dataImportDialog
  },
  data() {
    return {
      searchForm: {
        logType: "",
        keyword: "",
        type: "" 
      },
      dateRange: [],
      pageObj: {
        pageNo: 1,
        pageSize: 12,
        total: 0,
      },
      tableData: [],
      loading: false,
      logTypeList: [],
      activeName: "first",
      dialogVisible: false,
      showSensitive: false,
      tablCheckData: [],
      dialogAppVisible: false,
      applicationDataList: [],
      typeList: [],
      isEdit: false,
      dialogDataVisible: false,
    };
  },
  created() {
    this.getSynonymWordList();
  },
  mounted() {
    this.getSynonymWordKeyList()
  },
  methods: {
    resetChecked() {
      this.$refs.tree.setCheckedKeys([]);
    },
    handleSizeChange(val) {
      this.pageObj.pageSize = val;
      this.getSynonymWordList();
    },
    handleCurrentChange(val) {
      this.pageObj.pageNo = val;
      this.getSynonymWordList();
    },
    resetSearch() {
      this.pageObj.pageNo = 1;
      this.searchForm.keyword = "";
      this.dateRange = [];
      this.getSynonymWordList();
    },
    search() {
      this.pageObj.pageNo = 1;
      this.getSynonymWordList();
    },
    handleClick() { },
    editMenu(row) {
      this.sensitiveRow = row;
      this.isEdit = true; 
      this.dialogVisible = true;
    },
    async deleteData(row) {
      this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => {
        try {
          let res = await deleteCorrectWord([row.id]);
          if (res.code == "000000") {
            this.$message.success(this.$t("deleteSuccess"));
            this.search();
          } else {
            this.$message.warning(res.msg);
          }
        } catch (error) { }
      });
    },
    addRoleDialog() {
      this.isEdit = false;
      this.sensitiveRow = {
      keyWord: "",
      type: "",
      correctWordList: [{ content: "" }]
    };
      this.dialogVisible = true;
    },
    closeDialog() {
      this.dialogVisible = false;
    },
    submitDialog() {
      this.dialogVisible = false;
      this.getSynonymWordList();
    },
    async getSynonymWordList() {
      this.loading = true;
      let params = {
        keyword: this.searchForm.keyword,
        type: this.searchForm.type,
        startCreateTime: this.dateRange ? this.dateRange[0] : "",
        endCreateTime: this.dateRange ? this.dateRange[1] : "",
        pageNo: this.pageObj.pageNo,
        pageSize: this.pageObj.pageSize,
      };
      const res = await getCorrectWordList(params);
      if (res.code == "000000") {
        this.pageObj.total = res.data?.totalRow || 0;
        this.tableData = res.data?.records || [];
      } else {
        this.pageObj.total = 0;
        this.tableData = [];
      }
      this.loading = false;
    },
    getSynonymWordKeyList() {
      getCorrectWordKeyList().then((res) => {
        if (res.code == "000000") {
          this.typeList = res.data.map((item) => {
            return {
              value: item,
              label: item,
            };
          });
        }
      });
    },
    closeSensitiveDialog() {
      this.showSensitive = false;
      this.getSynonymWordList();
    },
    closeAppDialog() {
      this.dialogAppVisible = false;
    },
    handleImport() {
      this.dialogDataVisible = true;
    },
    closeDataDialog() {
      this.dialogDataVisible = false;
      this.getSynonymWordList();
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
  background: #fff;
  /* 轨道颜色 */
}

/* 自定义滚动条的滑块（thumb） */
.checkboxOuter::-webkit-scrollbar-thumb {
  background: #e1e4eb;
  /* 滑块颜色 */
  border-radius: 6px;
}

.slectStyle {
  .el-select-dropdown__item.selected {
    color: #1747E5;
  }
}
</style>
<style lang="scss" scoped>
.outer {
  width: 100%;
  height: 100%;
  padding: 24px 0;
  font-family: MiSans, MiSans;

  .inner {
    width: 100%;
    height: calc(100% - 50px);
    background: #fff;
    border-radius: 4px;

    // padding: 24px;
    ::v-deep .el-input__inner:focus,
    ::v-deep .el-date-editor:focus,
    ::v-deep .el-date-editor.is-active {
      border-color: #1747E5 !important;
    }
  }

  .title {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #383d47;
    // line-height: 40px;
    text-align: left;
    font-style: normal;
    margin-bottom: 24px;
  }
}

.el-button {
  &.el-button--default {
    border-radius: 2px !important;
    color: #383d47;
    border: 1px solid #c4c6cc;
  }

  &.el-button--primary {
    background-color: #1747E5;
    color: #fff;
    border-color: #1747E5;
    border-radius: 2px
  }
}

::v-deep .el-input__inner {
  border-radius: 2px !important;
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
    background: #1747E5;
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
      background: #1747E5;
      border-color: #1747E5;
    }
  }

  .el-button--text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #1747E5;
  }
}

::v-deep .el-pager li {
  border: 1px solid #dddfe8;
  color: #272a31;
  background: #fff;

  .el-select-dropdown__item.selected {
    color: #1747E5;
  }

  &:not(.disabled).active {
    background-color: #fff !important;
    border: 1px solid #1747E5;
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

    // background: linear-gradient(
    //   180deg,
    //   rgba(43, 88, 213, 0.1) 0%,
    //   rgba(43, 88, 213, 0) 100%
    // );
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
      border-color: #1747E5;
    }

    .is-indeterminate .el-checkbox__inner,
    .is-checked .el-checkbox__inner {
      background-color: #1747E5;
      border-color: #1747E5;
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
      background: #1747E5;
      border-color: #1747E5;
    }

    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
}

.elTabs {
  ::v-deep .el-tabs__header {
    margin-bottom: 5px;
  }

  ::v-deep .el-tabs__item {
    padding: 0 8px;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 22px;
    color: #828894;
    line-height: 28px;
  }

  ::v-deep .el-tabs__active-bar {
    height: 0;
  }

  ::v-deep .is-active {
    color: #383d47;
  }

  ::v-deep .el-tabs__nav-wrap::after {
    height: 0;
  }
}
</style>