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
              >{{ $t("search") }}</el-button
            >
            <el-button @click="resetSearch" style="height: 40px">{{
              $t("reset")
            }}</el-button>
          </div>
          <el-button
            type="primary"
            style="float: right; height: 40px"
            @click="addRoleDialog"
            ><i class="el-icon-circle-plus" style="margin-right: 8px;"></i>{{ $t("newSensitiveLexicon") }}</el-button
          >
        </div>
        <div v-loading="loading">
          <div style="margin-top: 16px">
            <el-table
              ref="multipleTable"
              :data="tableData"
              tooltip-effect="dark"
              style="width: 100%"
              class="table"
              max-height="660"
            >
              <el-table-column
                type="index"
                width="60"
                :label="$t('sequenceNumber')"
              ></el-table-column>
              <el-table-column prop="name" :label="$t('lexiconName')">
                <template slot-scope="scope">
                  <div class="name-ctn">
                    {{ scope.row.name }}
                    <div class="preset" v-if="scope.row.ownerType=='official'&&isAdmin">预置</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                prop="wordCount"
                :label="$t('numberOfWords')"
              ></el-table-column>
              <el-table-column
                prop="applicationCount"
                :label="$t('associatedApplications')"
                ><template slot-scope="scope">
                  <span
                    style="color: #383d47; font-size: 16px; margin-left: 5px"
                    v-if="!scope.row.applicationCount"
                    >0</span
                  >
                  <span
                    style="
                      color: #383d47;
                      font-size: 16px;
                      margin-left: 5px;
                    "
                    v-else
                    @click="applicationRelList(scope.row)"
                    >{{ scope.row.applicationCount }}</span
                  >
                </template></el-table-column
              >
              <el-table-column prop="updateTime" :label="$t('updateTime')">
              </el-table-column>
              <el-table-column prop="createUserName" :label="$t('founder')">
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
              <el-table-column :label="$t('action')" width="60">
                <template slot-scope="scope">
                  <div class="opts-box" @click.stop>
                    <el-dropdown
                      trigger="click"
                      @command="(value) => handleCommand(value, scope.row)"
                      placement="top-end"
                      class="opts-box-dropdown"
                    >
                      <span class="el-dropdown-link">
                        <!-- <i
                          style="transform: rotate(90deg); color: #828894"
                          class="el-icon-more"
                        ></i> -->
                        <iconpark-icon name="more-line" size="18" color="#383838" style="margin-top:2px;"></iconpark-icon>
                      </span>
                      <!-- api接入详情 -->
                      <el-dropdown-menu
                        slot="dropdown"
                        class="opts-box-dropdown-menu"
                      >
                        <el-dropdown-item
                          v-permission="'presetApp'"
                          command="presetApp"
                          v-if="scope.row.ownerType!='official'&&isAdmin"
                          style="display: block;"
                        >
                        <iconpark-icon color="#494E57" name="share-box-line"></iconpark-icon>
                        <span style="color: #494E57">{{ "设为预置" }}</span>
                        </el-dropdown-item>
                        <el-dropdown-item
                          v-permission="'presetApp'"
                          command="presetApp"
                          v-else-if="scope.row.ownerType=='official'&&isAdmin"
                          style="display: block;"
                        >
                        <iconpark-icon color="#F53F3F" name="share-box-line"></iconpark-icon>
                        <span style="color: #F53F3F">{{ "取消预置" }}</span>
                        </el-dropdown-item>
                        <el-dropdown-item
                          command="editView"
                          style="display: block;"
                        >
                        <iconpark-icon color="#494E57" name="edit-box-line"></iconpark-icon>
                        <span style="color: #494E57">{{ $t("edit") }}</span>
                        </el-dropdown-item>
                        <el-dropdown-item
                          v-permission="'deleteApp'"
                          command="deleteApp"
                        >
                        <iconpark-icon color="#494E57" name="delete-bin-4-line"></iconpark-icon>
                        <span style="color: #494E57">{{ $t("delete") }}</span>
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                  <!-- <el-button type="text" size="small" @click.stop="editMenu(scope.row)">{{
                    $t("view")
                  }}</el-button>
                  <el-button type="text" size="small" @click.stop="deleteData(scope.row)">{{
                    $t("delete")
                  }}</el-button> -->
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
                    <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page.sync="pageObj.pageNo"
                    :page-sizes="[12, 24, 36, 48]"
                    :page-size="pageObj.pageSize"
                    background
                    layout="prev, pager, next, sizes"
                    :total="pageObj.total"
                    >
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
    <editSensitiveLibrary
      v-else
      @closeSensitiveDialog="closeSensitiveDialog"
      :sensitiveRow="sensitiveRow"
    ></editSensitiveLibrary>
    <addDialog
      v-if="dialogVisible"
      :dialogVisible="dialogVisible"
      @closeDialog="closeDialog"
      :row="false"
      @submitDialog="submitDialog"
    ></addDialog>
    <associatedApplications
      v-if="dialogAppVisible"
      :dialogVisible="dialogAppVisible"
      :applicationDataList="applicationDataList"
      @closeAppDialog="closeAppDialog"
    >
    </associatedApplications>
  </div>
</template>
<script>
import {
  getInterceptWordHouseList,
  deleteInterceptWordHouse,
  updateStatus,
  applicationRelList,
} from "@/api/toolManager";
import {interceptWordHousePreset} from "@/api/app"
import addDialog from "@/views/toolManager/dictionaryManage/components/addDialog";
import editSensitiveLibrary from "@/views/toolManager/dictionaryManage/components/editSensitiveLibrary";
import associatedApplications from "@/views/toolManager/dictionaryManage/components/associatedApplications";
export default {
  components: {
    addDialog,
    editSensitiveLibrary,
    associatedApplications,
  },
  data() {
    return {
      searchForm: {
        logType: "",
        keyword: "",
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
    };
  },
  computed:{
    isAdmin(){
      return JSON.parse(sessionStorage.getItem("user")).powerType==0
    }
  },
  created() {
    this.getInterceptWordHouseList();
  },
  methods: {
    resetChecked() {
      this.$refs.tree.setCheckedKeys([]);
    },
    handleSizeChange(val) {
      this.pageObj.pageSize = val;
      this.getInterceptWordHouseList();
    },
    handleCurrentChange(val) {
      this.pageObj.pageNo = val;
      this.getInterceptWordHouseList();
    },
    resetSearch() {
      this.pageObj.pageNo = 1;
      this.searchForm.keyword = "";
      this.dateRange = [];
      this.getInterceptWordHouseList();
    },
    search() {
      this.pageObj.pageNo = 1;
      this.getInterceptWordHouseList();
    },
    getInterceptWordHousePresetApi(data){
      interceptWordHousePreset({id:data.id}).then(res=>{
        if(res.code=="000000"){
          this.getInterceptWordHouseList();
        }else{
          this.$message({
            type:"error",
            message:res.msg
          })
        }
      })
    },
    handleCommand(value, item) {
      if (value == "editView") this.editMenu(item);
      if (value == "deleteApp") this.deleteData(item);
      if (value == 'presetApp') this.getInterceptWordHousePresetApi(item);
    },
    handleClick() {},
    async changeStatus(row) {
      let res = await updateStatus({
        id: row.id,
        status: row.status,
      });
      if (res.code == "00000") {
      }
    },
    editMenu(row) {
      this.sensitiveRow = row;
      this.showSensitive = true;
    },
    async deleteData(row) {
      this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => {
        try {
          let res = await deleteInterceptWordHouse([row.id]);
          if (res.code == "000000") {
            this.$message.success(this.$t("deleteSuccess"));
            this.search();
          } else {
            this.$message.warning(res.msg);
          }
        } catch (error) {}
      });
    },
    addRoleDialog() {
      console.log(333);
      this.dialogVisible = true;
    },
    closeDialog() {
      this.dialogVisible = false;
    },
    submitDialog() {
      this.dialogVisible = false;
      this.getInterceptWordHouseList();
    },
    async getInterceptWordHouseList() {
      this.loading = true;
      let params = {
        keyword: this.searchForm.keyword,
        startCreateTime: this.dateRange ? this.dateRange[0] : "",
        endCreateTime: this.dateRange ? this.dateRange[1] : "",
        pageNo: this.pageObj.pageNo,
        pageSize: this.pageObj.pageSize,
        ownerType:"personalGrantTenant"
      };
      const res = await getInterceptWordHouseList(params);
      if (res.code == "000000") {
        this.pageObj.total = res.data?.totalRow || 0;
        this.tableData = res.data?.records || [];
      } else {
        this.pageObj.total = 0;
        this.tableData = [];
      }
      this.loading = false;
    },
    closeSensitiveDialog() {
      this.showSensitive = false;
      this.getInterceptWordHouseList();
    },
    // 获取关联应用个数
    async applicationRelList(row) {
      let res = await applicationRelList({
        interceptWordHouseId: row.id,
        pageNo: 1,
        pageSize: 200,
      });
      if (res.code == "000000") {
        this.applicationDataList = res.data.records;
        this.dialogAppVisible = true;
      }
    },
    closeAppDialog() {
      this.dialogAppVisible = false;
    },
    handleRowClick(row){
      this.editMenu(row)
    }
  },
};
</script>
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>
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
  .name-ctn{
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .preset{
    width: 39px;
    height: 23px;
    background: #EBEEF2;
    border-radius: 4px;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 12px;
    color: #1D2129;
    line-height: 23px;
    text-align: center;
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
