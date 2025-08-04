<template>
  <div class="matterTypePage">
    <template v-if="type == 'list'">
      <div class="matterTypePage-head">{{ $t("eventManagement") }}</div>

      <div class="matterTypePage-content">
        <div class="matter-type">
          <div class="matter-type-search">
            <el-input
              v-model="searchForm.keyword"
              :placeholder="`${$t('pleaseEnterKeyword')}`"
              prefix-icon="el-icon-search"
              clearable
              style="width: 334px; margin-right: 8px"
              @keydown.native.enter="searchHandler"
            />
            <!-- <div class="form-label">{{ $t("bindingApplications") }}</div>
            <el-select v-model="searchForm.applicationId" clearable filterable>
              <el-option
                v-for="item in applicationIdList"
                :key="item.applicationId"
                :label="item.applicationName"
                :value="item.applicationId"
              >
              </el-option>
            </el-select> -->
            <div style="margin-right: 10px"></div>
            <el-button type="primary" @click="searchHandler">{{
              $t("search")
            }}</el-button>
            <el-button
              type="primary"
              style="margin-left: auto"
              @click="addMatter"
            >
              <div style="display: flex; align-items: center">
                <iconpark-icon
                  name="add-circle-fill"
                  color="#fff"
                  size="15"
                  style="margin-right: 8px"
                ></iconpark-icon>
                {{ $t("createEvent") }}
              </div>
            </el-button>
          </div>
          <div class="matter-type-content">
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
                  <el-table-column
                    prop="matterName"
                    :label="$t('matterName')"
                  ></el-table-column>
                  <el-table-column
                    prop="matterDesc"
                    :label="$t('remarks')"
                  ></el-table-column>
                  <el-table-column
                    prop="matterTypeName"
                    :label="$t('matterType')"
                    width="180"
                  >
                  </el-table-column>
                  <el-table-column
                    prop="createTime"
                    :label="$t('creationTime')"
                    width="180"
                  >
                  </el-table-column>
                  <el-table-column
                    prop="createUserName"
                    :label="$t('founder')"
                    width="120"
                  ></el-table-column>
                  <el-table-column
                    prop="applicationName"
                    :label="$t('associatedApplications')"
                    width="180"
                  ></el-table-column>
                  <el-table-column
                    prop="showFlag"
                    :label="$t('enable')"
                    width="90"
                  >
                    <template slot-scope="scoped">
                      <el-switch
                        v-model="scoped.row.isShowFlag"
                        @change="updateShowFlag(scoped.row)"
                      >
                      </el-switch>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('action')" width="140">
                    <template slot-scope="scoped">
                      <el-button type="text" @click="editMatter(scoped.row)">{{
                        $t("edit")
                      }}</el-button>
                      <el-button
                        type="text"
                        @click="deleteHandler(scoped.row)"
                        >{{ $t("delete") }}</el-button
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
                  :current-page="pageData.pageNo"
                  :total="pageData.total"
                  :page-size="pageData.pageSize"
                  @current-change="handleCurrentChange"
                  @size-change="handleSizeChange"
                  :page-sizes="[10, 20, 30, 40]"
                ></el-pagination>
              </div>
            </div>
          </div>
          <!-- 创建事项 -->
          <create-event
            ref="createEvent"
            v-model="dialogVisible"
            :title="title"
            @close="closeDialog"
            @refreshHandler="refreshHandler"
          ></create-event>
        </div>
      </div>
    </template>
    <!-- 事项类型编辑 -->
    <MatterTypeDetails
      v-else
      ref="MatterTypeDetails"
      :matterTypeId="matterTypeId"
      @comeBack="comeBackList"
      @refreshList="refreshList"
    />
  </div>
</template>

<script>
import {
  apiGetMatterList,
  apiUpdateShowFlag,
  apiDeleteMatter,
} from "@/api/issueManagement.js";
import { getApplicationInfoList } from "@/api/index.js";
import createEvent from "./components/create-event";
import MatterTypeDetails from "./matterTypeDetails";

export default {
  name: "MatterType",
  components: {
    createEvent,
    MatterTypeDetails,
  },
  props: {
    form: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      searchForm: {
        keyword: "",
        applicationId: "",
      },
      applicationIdList: [],
      tableData: [],
      pageData: {
        pageNo: 1,
        pageSize: 10,
        total: 0,
      },
      loading: false,

      dialogVisible: false,
      title: "",
      type: "list", // list-展示列表 detail-展示详情
      matterTypeId: "",
    };
  },
  mounted() {
    this.getApplicationInfoList();
    this.getMatterList();
  },
  methods: {
    // 应用列表
    async getApplicationInfoList() {
      const { data } = await getApplicationInfoList({
        pageNo: 1,
        pageSize: 100,
      });
      this.applicationIdList = data?.records || [];
    },
    searchHandler() {
      this.pageData.pageNo = 1;
      this.getMatterList();
    },
    handleCurrentChange(page) {
      this.pageData.pageNo = page;
      this.getMatterList();
    },
    handleSizeChange(size) {
      this.pageData.pageSize = size;
      this.getMatterList();
    },
    // 查询列表
    async getMatterList() {
      let params = {
        ...this.searchForm,
        ...this.pageData,
      };
      delete params.total;
      this.loading = true;
      const res = await apiGetMatterList(params);
      if (res.code == "000000") {
        this.tableData = res.data?.records || [];
        this.tableData.forEach((item) => {
          item.isShowFlag = item.showFlag == "是" ? true : false;
        });
        this.pageData.total = res.data?.totalRow || 0;
      }
      this.loading = false;
    },
    // 更新启用状态
    async updateShowFlag(row) {
      row.showFlag = row.isShowFlag ? "是" : "否";
      let params = {
        id: row.id,
        showFlag: row.showFlag, //是/否
      };
      const res = await apiUpdateShowFlag(params);
      if (res.code == "000000") {
        this.$message.success(
          `${
            row.showFlag == "是"
              ? this.$t("settingsEnabled")
              : this.$t("disabled")
          }`
        );
        this.getMatterList();
      }
    },
    // 新建事项
    addMatter() {
      this.dialogVisible = true;
      this.title = "创建事项";
    },
    // 关闭新建事项弹窗
    closeDialog() {
      this.dialogVisible = false;
    },
    // 保存并关闭新建事项弹窗
    refreshHandler(id) {
      this.dialogVisible = false;
      this.getMatterList();
      this.$emit("comeToDetailPage", id);
    },
    // 删除
    deleteHandler(data) {
      this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
        confirmButtonText: this.$t("confirm"),
        cancelButtonText: this.$t("cancel"),
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => {
        const params = {
          delMatterIds: [data.matterId],
        };
        try {
          const res = await apiDeleteMatter(params);
          if (res.code == "000000") {
            this.$message.success(this.$t("deleteSuccess"));
            this.searchHandler();
          } else {
            this.$message.warning(res.msg);
          }
        } catch (error) {}
      });
    },
    // 编辑
    editMatter(data) {
      // this.$emit("comeToDetailPage", data.id);
      this.matterTypeId = data.id;
      this.type = "detail";
    },
    // 事项类型详情保存成功 - 刷新列表
    refreshList() {
      this.type = "list";
      this.getApplicationInfoList();
      this.getMatterList();
    },
    // 事项类型详情返回
    comeBackList() {
      this.type = "list";
      this.getApplicationInfoList();
      this.getMatterList();
    },
  },
};
</script>
<style lang="scss">
.matter-dialog {
  background: #fff !important;
  .el-dialog__header {
    padding: 32px 32px 16px;
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
    //   display: block !important;
    .el-dialog__title {
      height: 24px;
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
    }
  }
  .el-dialog__body {
    padding: 0 32px 32px !important;
    background: #fff !important;
    .el-input__inner {
      border-radius: 4px;
    }
    .el-input__inner:focus {
      border-color: #1c50fd;
    }

    .select-form {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      .el-form-item__content {
        width: 100%;
        .el-select {
          width: 100%;
        }
      }
    }

    .checkboxOuter {
      height: 410px;
      padding: 0 8px;
      overflow-y: scroll;
    }
    .el-form-item__label {
      color: #383d47;
      font-size: 16px;
      font-weight: 600;
    }
  }
  .dialog-footer {
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
  .el-textarea__inner {
    font-family: MiSans, MiSans !important;
    font-weight: 400;
    font-size: 14px !important;
    color: #606266;
  }
}
.confirm-ok {
  background: #1c50fd !important;
  border-color: transparent !important;
}
.confirm-cancel {
  &:hover {
    background: none !important;
    color: #1c50fd !important;
    border-color: #1c50fd !important;
  }
}
</style>
<style lang="scss" scoped>
.matterTypePage {
  width: 100%;
  height: 100%;
  padding: 32px;
  background: #fff;
  border-radius: 16px;

  &-head {
    height: 40px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #383d47;
    line-height: 40px;
    margin-bottom: 24px;
  }
  &-content {
    height: calc(100% - 60px);
    width: 100%;
  }
}
.matter-type {
  width: 100%;
  height: 100%;
  &-search {
    display: flex;
    align-items: center;
    .form-label {
      margin-right: 20px;
      height: 40px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #828894;
      line-height: 40px;
    }
  }
  ::v-deep .el-button {
    border-radius: 4px;
  }
  ::v-deep .el-switch__core {
    width: 32px !important;
    height: 20px;
    border-radius: 12px;
    background: #ced4e0;
    border: none;
    &::after {
      width: 14px;
      height: 14px;
      top: 3px !important;
    }
  }
  ::v-deep .el-switch.is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
    &::after {
      width: 14px;
      height: 14px;
      top: 3px !important;
    }
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
}
</style>