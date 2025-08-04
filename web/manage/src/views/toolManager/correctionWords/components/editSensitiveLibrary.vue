<template>
  <div class="outerii">
    <div>
      <div>
        <div class="headBar">
          <div class="leftSlide">
            <img
              src="@/assets/images/arrow-go-back-fill.svg"
              @click="closeSensitiveDialog"
            />
            <img style="width: 40px;height: 40px;" src="@/assets/images/sensitiveLibrary.svg" alt="">
            <div class="titleIcon">
              <p>
                {{ sensitiveForm.name || $t("noApplicationName") }}
                <img
                  src="@/assets/images/appManagement/edit-line.svg"
                  class="edit-icon"
                  style="cursor: pointer"
                  @click="openDialog('editName')"
                />
              </p>
              <p>{{ sensitiveForm.remark || $t("noDescription") }}</p>
            </div>
          </div>
          <div class="rightSlide">
            <div>
              <el-switch
                v-model="sensitiveForm.status"
                active-value="1"
                inactive-value="2"
                @change="changeOneStatus()"
              ></el-switch>
              <span style="color: #383d47; font-size: 16px; margin-left: 5px">{{
                sensitiveForm.status == "1"
                  ? $t("settingsEnabled")
                  : $t("settingsDisabled")
              }}</span>
            </div>
           <!-- <el-button class="btn" @click="temporarSave(2)">
              <img src="@/assets/images/save-line1.svg" />
              <span>{{ $t("save") }}</span>
            </el-button> -->
          </div>
        </div>
      </div>
      <div class="inner">
        <div style="display: flex; justify-content: space-between; margin-bottom: 20px">
          <div style="display: flex; justify-content: flex-start">
            <div>
              <span style="margin-right: 20px">{{ $t("classification") }}</span>
              <el-select
                style="width: 148px; margin-right: 16px"
                v-model="searchForm.type"
                :placeholder="$t('selectPlaceholder')"
                clearable
              >
                <el-option
                  v-for="item in verifyStatusColumns"
                  :key="item.id"
                  :label="item.type_name"
                  :value="item.type_name"
                ></el-option>
              </el-select>
            </div>
            <div>
              <span style="margin-right: 20px">{{ $t("status") }}</span>
              <el-select
                style="width: 148px; margin-right: 16px"
                v-model="searchForm.status"
                :placeholder="$t('selectPlaceholder')"
                clearable
              >
                <el-option
                  v-for="item in statusColumns"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                ></el-option>
              </el-select>
            </div>
            <div style="margin-right: 8px">
              <el-input
                v-model="searchForm.keyword"
                clearable
                :placeholder="$t('searchKeywords')"
                prefix-icon="el-icon-search"
                style="width: 334px"
                @keydown.native.enter="search"
              ></el-input>
            </div>
            <el-button
              type="primary"
              style="margin-right: 8px; height: 40px"
              @click="search"
              >{{ $t("searchBtn") }}</el-button
            >
            <el-button @click="resetSearch" style="height: 40px">{{
              $t("reset")
            }}</el-button>
          </div>
          <div style="display: flex; algin-items: center">
            <el-button
              plain
              style="float: right; height: 40px;border-color:#C9CCD1;color:#494E57;"
              class="addbtn"
              @click="addRoleDialog"
              ><i class="el-icon-plus" style="margin-right: 8px;"></i>{{ $t("added") }}</el-button
            >
            <el-button
              type="primary"
              style="float: right; height: 40px"
              @click="dataImportDialogClick"
              ><i class="el-icon-document-add" style="margin-right: 8px;"></i>{{ $t("dataImport") }}</el-button
            >
          </div>
        </div>
        <div class="flex">
          <!-- <el-checkbox style="margin-right:36px;">选择全部</el-checkbox> -->
          <span class="flex" style="margin-right: 21px">
            <span
              style="color: #383d47; font-size: 16px; font-weight: 500; margin-right: 5px"
              >{{ $t("selected") }}</span
            >
            <span style="color: #1747E5; font-weight: 500; font-size: 16px">{{
              tablCheckData.length
            }}</span>
          </span>
          <!-- <span style="margin-right: 21px; cursor: pointer" @click="roleConfig()">
            <img
              src="@/assets/images/settings-4-line1.svg"
              style="width: 15px; vertical-align: middle; margin-right: 4px"
            />
            <span style="color: #3666ea; font-size: 16px; vertical-align: middle">{{
              $t("batchSettings")
            }}</span>
          </span> -->
          <!-- <span style="margin-right: 21px; cursor: pointer" @click="">
            <img
              src="@/assets/images/forbid-line.svg"
              style="width: 15px; vertical-align: middle; margin-right: 4px"
            />
            <span style="color: #3666ea; font-size: 16px; vertical-align: middle">{{
              $t("cancelSelected")
            }}</span>
          </span> -->
          <span class="flex" style="cursor: pointer" >
            <!-- <img
              src="@/assets/images/delete-bin-4-line.svg"
              style="width: 15px; vertical-align: middle; margin-right: 4px"
            /> -->
            <span style="color: #1747E5; font-size: 16px; vertical-align: middle" @click="toggleSelection(tableData)">
                {{$t("clear")}}</span>
            <!-- <span style="color: #1747E5; font-size: 16px; vertical-align: middle" @click="batchSettings()">
                <i class="el-icon-s-operation"></i>
                {{$t("setType")}}</span> -->
            <span style="color: #1747E5; font-size: 16px; vertical-align: middle" @click="deleteData('', 2)">
                <i class="el-icon-delete"></i>
                {{$t("batch")}}{{$t("delete")}}</span>
          </span>
        </div>
        <div v-loading="loading">
          <div style="margin-top: 16px">
            <el-table
              ref="multipleTable"
              :data="tableData"
              tooltip-effect="dark"
              style="width: 100%"
              class="table"
              max-height="630"
              @selection-change="handleSelectionChange"
            >
              <el-table-column type="selection" width="55"></el-table-column>
              <!-- <el-table-column
                type="index"
                width="60"
                :label="$t('sequenceNumber')"
              ></el-table-column> -->
              <!-- <el-table-column
                prop="id"
                :label="$t('dataID')"
                width="90"
              ></el-table-column> -->
              <el-table-column prop="content" :label="$t('keywords')"></el-table-column>
              <el-table-column prop="remark" :label="$t('describe')"></el-table-column>
              <el-table-column prop="type" :label="$t('classification')">
              </el-table-column>
              <el-table-column prop="createUserName" :label="$t('founder')" width="120">
              </el-table-column>
              <el-table-column
                prop="createTime"
                :label="$t('creationTime')"
              ></el-table-column>
              <el-table-column prop="status" :label="$t('enable')" width="120">
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.status"
                    active-value="1"
                    inactive-value="2"
                    @change="changeStatus(scope.row)"
                  ></el-switch>
                  <span style="color: #383d47; font-size: 16px; margin-left: 5px">{{
                    scope.row.status == "1"
                      ? $t("settingsEnabled")
                      : $t("settingsDisabled")
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('action')" width="120">
                <template slot-scope="scope">
                  <el-button type="text" size="small" @click="editMenu(scope.row)">{{
                    $t("edit")
                  }}</el-button>
                  <el-button type="text" size="small" @click="deleteData(scope.row, 1)">{{
                    $t("delete")
                  }}</el-button>
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
      <el-dialog
        :title="$t('batchSettings')"
        :visible.sync="quantityConfig"
        width="30%"
        :before-close="handleClose">
        <div style="margin: 10px 0;">已选<span style="color:#1747E5;">  20  </span>个关键词</div>
        <div class="handlingflex">
          <div class="box"></div>
          <div class="name">
            {{ $t("handlingMethod") }}
          </div>
        </div>
        <div class="handle-list">
          <div class="handle-item" >
            <el-select style="width: 100%;"
              v-model="handlingMethod"
              :placeholder="$t('selectPlaceholder')"
              clearable
            >
              <el-option
                v-for="item in handlingList"
                :key="item.id"
                :label="item.method_name"
                :value="item.method_name"
              ></el-option>
            </el-select>
            <!-- <img
              :src="require('@/assets/images/delete-bin-4-line1.svg')"
              style="width: 17px; height: 17px"
            /> -->
          </div>
          <div
            v-for="(itemData, index) in handledingListData"
            :key="index"
            class="handle-item"
          >
            <el-select
              v-model="itemData.name"
              :placeholder="$t('selectPlaceholder')"
              clearable
              style="width: 140px; margin-right: 8px"
              @change="handleSelect"
            >
              <el-option
                v-for="item in handlingFourList"
                :key="item.name"
                :label="item.name"
                :value="item.name"
                :disabled="item.disabled"
              ></el-option>
            </el-select>
            <el-input
              v-model="itemData.content"
              :placeholder="placrholdername[itemData.name]"
              class="input"
            ></el-input>
            <img
              :src="require('@/assets/images/delete-bin-4-line1.svg')"
              style="width: 17px; height: 17px"
              @click="deleteClick(itemData.name, index)"
            />
          </div>
          <div style="margin-top: 12px" v-if="handledingListData.length < 4">
            <el-button plain class="addbtn" @click="addNewHandleing" style="color:#494E57;border-color:#C9CCD1;">
              <div class="flex flex-just">
                <img
                  src="@/assets/images/add-line2.svg"
                  style="width: 17px; height: 17px; margin-right: 4px"
                />
                <span class="add-name">{{ $t("appendTo") }}</span>
              </div>
            </el-button>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button @click="quantityConfig = false">取 消</el-button>
            <el-button type="primary" @click="quantityConfig = false">确 定</el-button>
        </span>
    </el-dialog>
    </div>
    <!-- 编辑敏感词库 -->
    <addDialog
      v-if="dialogVisible"
      :dialogVisible="dialogVisible"
      @closeDialog="closeSenDialog"
      :sensitiveRow="sensitiveRow"
      @submitDialog="submitDialog"
      :row="true"
    ></addDialog>
    <!-- 数据导入 -->
    <dataImportDialog
      v-if="dialogDataVisible"
      :dialogDataVisible="dialogDataVisible"
      @closeDialog="closeDataDialog"
      :sensitiveRow="sensitiveRow"
    >
    </dataImportDialog>
    <!-- 新增敏感词 -->
    <addSensitiveWordsDraw
      v-if="addQaVisible"
      :addQaVisible="addQaVisible"
      @closeDialog="closeDraw"
      :verifyStatusColumns="verifyStatusColumns"
      @closeWordDialog="closeWordDialog"
      :sensitiveRow="sensitiveRow"
      :row="rowBoolean"
      :sensitiveWordList="sensitiveWordList"
    ></addSensitiveWordsDraw>
  </div>
</template>
<script>
import {
  getInterceptWordList,
  updateStatus,
  getInterceptWordTypeList,
  deleteInterceptWord,
  interceptWordUpdateStatus,
} from "@/api/toolManager";
import addDialog from "@/views/toolManager/dictionaryManage/components/addDialog";
import dataImportDialog from "@/views/toolManager/dictionaryManage/components/dataImportDialog";
import addSensitiveWordsDraw from "@/views/toolManager/dictionaryManage/components/addSensitiveWordsDraw";
export default {
  components: {
    addDialog,
    dataImportDialog,
    addSensitiveWordsDraw,
  },
  props: {
    sensitiveRow: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
        handlingMethod: '',
        handlingList: [],
        handledingListData: [],
        handlingFourList: [],
        placrholdername: {
        "": this.$t("pleaseEnter"),
        限定答案: this.$t("enterALimitedAnswer"),
        添加前缀: this.$t("enterPrefix"),
        添加后缀: this.$t("enterSuffix"),
        替换问题: this.$t("enterTheReplacementContent"),
      },
      searchForm: {
        type: "",
        keyword: "",
        status: "",
      },
      dateRange: [],
      currentPage: 1,
      total: 0,
      pageSize: 10,
      tableData: [],
      loading: false,
      logTypeList: [],
      activeName: "first",
      dialogVisible: false,
      appForm: {
        applicationName: "riririri",
        introduce: "miaossososo",
      },
      statusColumns: [
        {
          text: this.$t("settingsEnabled"),
          value: 1,
        },
        {
          text: this.$t("settingsDisabled"),
          value: 2,
        },
      ],
      verifyStatusColumns: [],
      verifyStatus: "",
      tablCheckData: [],
      dialogDataVisible: false,
      addQaVisible: false,
      senstive: "",
      sensitiveForm: {},
      rowBoolean: false,
      sensitiveWordList: {},
      quantityConfig: false
    };
  },
  watch: {
    sensitiveRow(val) {
      this.sensitiveForm = JSON.parse(JSON.stringify(val));
    },
  },
  created() {
    this.sensitiveForm = JSON.parse(JSON.stringify(this.sensitiveRow));
    this.getInterceptWordTypeList();
    this.getInterceptWordList();
  },
  methods: {
    handleSelect() {
        this.setDisable();
    },
    setDisable() {
      this.handlingFourList.forEach((item) => {
        let isdisable = this.handledingListData.findIndex((val) => val.name == item.name);
        if (isdisable != -1) {
          item.disabled = true;
        } else {
          item.disabled = false;
        }
      });
    },
    deleteClick(name, index) {
      this.handledingListData.splice(index, 1);
      this.handlingFourList.forEach((item) => {
        if (item.name == name) {
          item.disabled = false;
        }
      });
    },
    addNewHandleing() {
      this.handledingListData.push({
        name: "",
      });
    },
    resetChecked() {
      this.$refs.tree.setCheckedKeys([]);
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getInterceptWordList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getInterceptWordList();
    },
    resetSearch() {
      this.currentPage = 1;
      (this.searchForm.keyword = ""),
        (this.searchForm.type = "")((this.searchForm.status = ""));
      this.dateRange = [];
      this.getInterceptWordList();
    },
    search() {
      this.currentPage = 1;
      this.getInterceptWordList();
    },
    async getInterceptWordList() {
      this.loading = true;
      const params = {
        pageNo: this.currentPage,
        pageSize: this.pageSize,
        keyword: this.searchForm.keyword,
        type: this.searchForm.type,
        status: this.searchForm.status,
        startCreateTime: this.dateRange ? this.dateRange[0] : "",
        endCreateTime: this.dateRange ? this.dateRange[1] : "",
        interceptWordHouseId: this.sensitiveForm.id
      };
      try {
        const res = await getInterceptWordList(params);
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
    handleClick() {},
    async changeStatus(row) {
      let res = await interceptWordUpdateStatus({
        id: row.id,
        status: row.status,
      });
      if (res.code == "00000") {
      }
    },
    editMenu(row) {
      this.rowBoolean = true;
      this.sensitiveWordList = row;
      this.addQaVisible = true;
    },
    async deleteData(row, type) {
      if (type == 1) {
        this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
          confirmButtonText: this.$t('confirm'),
          cancelButtonText: this.$t('cancel'),
          confirmButtonClass: "confirm-ok",
          cancelButtonClass: "confirm-cancel",
        }).then(async () => {
          try {
            let res = await deleteInterceptWord([row.id]);
            if (res.code == "000000") {
              this.$message.success("删除成功");
              this.search();
            } else {
              this.$message.warning(res.msg);
            }
          } catch (error) {}
        });
      } else {
        if(this.tablCheckData.length == 0){
          return
        }
        let idList = this.tablCheckData.map((item) => item.id);
        let res = await deleteInterceptWord(idList);
        if (res.code == "000000") {
          this.$message.success("删除成功");
          this.search();
        } else {
          this.$message.warning(res.msg);
        }
      }
    },
    addRoleDialog() {
      this.rowBoolean = false;
      this.addQaVisible = true;
    },
    closeDraw() {
      this.addQaVisible = false;
    },
    closeWordDialog() {
      this.addQaVisible = false;
      this.search();
    },
    temporarSave() {},
    openDialog() {
      this.dialogVisible = true;
    },
    closeSenDialog() {
      this.dialogVisible = false;
    },
    submitDialog(row) {
      this.dialogVisible = false;
      this.sensitiveForm = row;
    },
    batchSettings() {
        this.quantityConfig = true;
    },
    handleClose() {
        this.quantityConfig = false;
    },
    roleConfig(row) {
      this.row = "";
      this.roleName = "";
      this.chooseRole = [];
      if (row) {
        this.rowData = row;
        this.getroleList();
        this.getUserSearchRole();
        this.roleDialogVisible = true;
        this.quantityConfig = false;
      } else {
        //批量配置
        if (this.tablCheckData.length > 0) {
          this.getroleList();
          this.roleDialogVisible = true;
          this.quantityConfig = true;
        } else {
          this.$message({
            type: "warning",
            message: this.$t("pleaseSelectAtLeastOneUser"),
          });
        }
      }
    },
    toggleSelection(rows) {
        if (rows) {
            this.$refs.multipleTable.clearSelection();
        } 
        // else {
        //     rows.forEach(row => {
        //       this.$refs.multipleTable.toggleRowSelection(row);
        //     });
        // }
    },
    deregister(rows) {
    //   if (!row.id && this.tablCheckData.length <= 0) {
    //     this.$message({
    //       type: "warning",
    //       message: this.$t("pleaseSelectUserFirst"),
    //     });
    //     return;
    //   }
    //   this.$confirm(this.$t("thisOperationWillCancelUser"), this.$t("tips"), {
    //     confirmButtonText: this.$t("confirm"),
    //     cancelButtonText: this.$t("cancel"),
    //     type: "warning",
    //   })
    //     .then(() => {})
    //     .catch(() => {
    //       this.$message({
    //         type: "info",
    //         message: this.$t("cancelledOperation"),
    //       });
    //     });
    },
    dataImportDialogClick() {
      this.dialogDataVisible = true;
    },
    closeDataDialog(type) {
      if (type == 2) {
        this.search();
      }
      this.dialogDataVisible = false;
    },
    closeSensitiveDialog() {
      this.$emit("closeSensitiveDialog");
    },
    async changeOneStatus() {
      let res = await updateStatus({
        id: this.sensitiveForm.id,
        status: this.sensitiveForm.status,
      });
      if (res.code != "000000") {
        this.$message.warning("切换失败");
      }
    },
    async getInterceptWordTypeList() {
      let res = await getInterceptWordTypeList();
      if (res.code == "000000") {
        this.verifyStatusColumns = res.data;
      }
    },
    handleSelectionChange(val) {
      this.tablCheckData = val;
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
    color: #1747E5;
  }
}
</style>
<style lang="scss" scoped>
.outerii {
  width: 100%;
  height: 100%;
  font-family: MiSans, MiSans;
  .inner {
    width: 100%;
    height: calc(100% - 50px);
    background: #fff;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    padding: 24px;
    ::v-deep .el-input__inner:focus,
    ::v-deep .el-date-editor:focus,
    ::v-deep .el-date-editor.is-active {
      border-color: #1747E5 !important;
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
    border-radius: 2px;
  &.el-button-default {
    border-radius: 2px;
    color: #383d47;
    border: 1px solid #c4c6cc;
  }

  &.el-button--primary {
    border-radius: 2px;
    background-color: #1747E5;
    color: #fff;
    border-color: #1747E5;
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
    background: linear-gradient(270deg, #8e65ff 0%, #1747E5 100%);
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
// ::v-deep .dialog {
//   .el-dialog__header {
//     padding: 20px 32px;
//     // background: linear-gradient(
//     //   180deg,
//     //   rgba(43, 88, 213, 0.1) 0%,
//     //   rgba(43, 88, 213, 0) 100%
//     // );
//     .el-dialog__title {
//       color: #383d47;
//       font-size: 20px;
//       font-weight: 500;
//     }
//   }
//   .el-dialog__body {
//     padding: 0 32px;
//     .el-input__inner {
//       border-radius: 4px;
//     }
//     .el-input__inner:focus {
//       border-color: #1747E5;
//     }
//     .is-indeterminate .el-checkbox__inner,
//     .is-checked .el-checkbox__inner {
//       background-color: #1747E5;
//       border-color: #1747E5;
//     }
//     .checkboxOuter {
//       height: 410px;
//       padding: 0 8px;
//       overflow-y: scroll;
//     }
//     .el-form-item__label {
//       color: #383d47;
//       font-size: 16px;
//       font-weight: 500;
//     }
//   }
//   .el-dialog__footer {
//     text-align: left;
//     .el-button {
//       border-radius: 4px;
//     }
//     .el-button--primary {
//       background: #1747E5;
//       border-color: #1747E5;
//     }
//     .el-button--default {
//       border-color: #c4c6cc;
//       color: #383d47;
//       font-size: 16px;
//     }
//   }
// }

.headBar {
  padding: 0 0 16px 0;
  display: flex;
  justify-content: space-between;
  width: 100%;
  img {
    width: 17px;
    height: 17px;
  }
  .leftSlide {
    display: flex;
    justify-content: space-between;
    align-items: center;
    > img {
      margin-right: 16px;
      cursor: pointer;
    }
    .titleIcon {
      p:first-child {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        color: #383d47;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        margin-bottom: 6px;
      }
      p:last-child {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #768094;
        line-height: 18px;
        text-align: left;
        font-style: normal;
      }
    }
  }
  .rightSlide {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .preview {
      line-height: 36px;
      margin-right: 28px;
      cursor: pointer;
      > span {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #3666ea;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        text-transform: none;
      }
      > img {
        margin-right: 5px;
      }
      > span,
      > img {
        vertical-align: middle;
      }
    }
    .btn {
      height: 40px;
      color: #fff;
      border: 1px solid #1747E5;
      background: #1747E5;
      border-radius: 4px;
      margin-left: 20px;
      img {
        margin-right: 5px;
      }
      img,
      span {
        vertical-align: middle;
      }
    }
    .btns {
      color: #fff;
      height: 40px;
      background: linear-gradient(270deg, #8e65ff 0%, #1747E5 100%);
      border-radius: 4px;
      img {
        margin-right: 5px;
      }
      img,
      span {
        vertical-align: middle;
      }
    }
  }
}

::v-deep .el-switch.is-checked .el-switch__core {
  background: linear-gradient(270deg, #8e65ff 0%, #1747E5 100%);
}

.addbtn {
  border: 1px solid #1747E5;
  background: #fff;
  color: #1747E5;
}

.flex {
  display: flex;
  align-items: center;
  span {
    padding-right: 20px;
  }
}
.handlingflex {
  display: flex;
  align-items: center;
  .box {
    width: 3px;
    height: 18px;
    background: #1c50fd;
  }
  .name {
    margin-left: 8px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #383d47;
    line-height: 28px;
  }
}
.handle-list {
  margin: 20px 0;
  .handle-item {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    img {
      margin-left: 12px;
    }
    .input {
      flex: 1;
    }
  }
}
</style>
